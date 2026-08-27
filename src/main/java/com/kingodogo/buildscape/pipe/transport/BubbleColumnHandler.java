package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.Set;

/**
 * Handles bubble column logic, detection, entity physics, and visual particle/sound effects
 * contained inside Hollow Steel Pipes.
 */
public class BubbleColumnHandler {

    /**
     * Inspects the block directly below the given position to detect a Soul Sand or Magma Block base.
     */
    public static BubbleColumnState detectBubbleColumnBase(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return BubbleColumnState.NONE;
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (belowState.is(Blocks.SOUL_SAND)) {
            return BubbleColumnState.UP;
        }
        if (belowState.is(Blocks.MAGMA_BLOCK)) {
            return BubbleColumnState.DOWN;
        }
        return BubbleColumnState.NONE;
    }

    /**
     * Checks if a blockstate is a valid bubble-column activating base block.
     */
    public static boolean isBubbleColumnBase(BlockState state) {
        if (state == null) return false;
        return state.is(Blocks.SOUL_SAND) || state.is(Blocks.MAGMA_BLOCK);
    }

    /**
     * Applies fluid current physics and bubble column effects to entities inside the pipe's water stream.
     * Items and entities are pushed along the flow direction, and bubble columns propel entities vertically.
     */
    public static void handleEntityInside(Level level, BlockPos pos, BlockState state, Entity entity, PipeFlowState flowState) {
        if (entity == null || flowState == null || !flowState.hasWater()) {
            return;
        }

        Vec3 delta = entity.getDeltaMovement();
        BubbleColumnState bubble = flowState.getBubbleColumn();

        // 1. Calculate aggregate horizontal & vertical flow vector
        double flowX = 0.0D;
        double flowY = 0.0D;
        double flowZ = 0.0D;
        Set<Direction> flowDirs = flowState.getFlowDirections();
        if (!flowDirs.isEmpty()) {
            for (Direction dir : flowDirs) {
                flowX += dir.getStepX();
                flowY += dir.getStepY();
                flowZ += dir.getStepZ();
            }
            double count = flowDirs.size();
            flowX /= count;
            flowY /= count;
            flowZ /= count;
        }

        // 2. Vertical bubble column physics (highest priority)
        if (bubble == BubbleColumnState.UP) {
            double newY = Math.min(delta.y + 0.1D, 0.7D);
            entity.setDeltaMovement(delta.x + flowX * 0.03D, newY, delta.z + flowZ * 0.03D);
            entity.resetFallDistance();

            if (entity instanceof LivingEntity living) {
                living.setAirSupply(living.getMaxAirSupply());
            }
            return;
        } else if (bubble == BubbleColumnState.DOWN) {
            double newY = Math.max(delta.y - 0.08D, -0.7D);
            entity.setDeltaMovement(delta.x + flowX * 0.03D, newY, delta.z + flowZ * 0.03D);
            return;
        }

        // 3. Fluid stream pushing physics for items, mobs, and players
        boolean isItem = entity instanceof net.minecraft.world.entity.item.ItemEntity;
        double pushStrength = isItem ? 0.07D : 0.03D;

        double newX = delta.x * (isItem ? 0.85D : 0.95D) + flowX * pushStrength;
        double newZ = delta.z * (isItem ? 0.85D : 0.95D) + flowZ * pushStrength;
        double newY = delta.y;

        if (flowY < 0) {
            // Downward waterfall / downward pipe channel
            newY = Math.max(delta.y - 0.06D, -0.4D);
        } else if (isItem) {
            // Items float with water buoyancy inside horizontal pipes
            newY = Math.max(delta.y * 0.8D, -0.03D);
        }

        entity.setDeltaMovement(newX, newY, newZ);
        entity.resetFallDistance();
    }

    /**
     * Spawns contained directional flow bubble particles and bubble column effects
     * strictly inside the 1x1 internal cavity of the pipe.
     *
     * Particle behavior:
     *   - Each bubble originates near the UPSTREAM (incoming water) face of this pipe.
     *   - It travels toward the DOWNSTREAM (outgoing water) face.
     *   - When this is an open endpoint (isOpenEndpoint=true), extra particles are emitted
     *     slightly OUTSIDE the pipe exit face to visually show water leaving the pipe.
     */
    public static void spawnFlowParticles(Level level, BlockPos pos, Random random, PipeFlowState flowState) {
        if (level == null || !level.isClientSide || flowState == null || !flowState.hasWater()) {
            return;
        }

        // 1. If bubble column is active, spawn dense vertical elevator particles
        if (flowState.getBubbleColumn().isActive()) {
            spawnBubbleParticles(level, pos, random, flowState.getBubbleColumn());
        }

        // 2. Spawn directional flow bubbles indicating the physical movement of water
        Set<Direction> flowDirs = flowState.getFlowDirections();
        if (flowDirs.isEmpty()) {
            // Standing contained water without flow: occasional gentle ambient bubble
            if (random.nextInt(4) == 0) {
                double px = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
                double py = pos.getY() + 0.3D + random.nextDouble() * 0.4D;
                double pz = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;
                level.addParticle(ParticleTypes.UNDERWATER, px, py, pz, 0.0D, 0.0D, 0.0D);
            }
            return;
        }

        // For each active flow direction:
        //   - Spawn 1-2 bubbles that travel along the flow direction (from entry face to exit face).
        //   - If this is an open endpoint in this direction, also spray bubbles slightly outside.
        boolean isEndpoint = flowState.isOpenEndpoint();

        for (Direction dir : flowDirs) {
            int particleCount = 1 + random.nextInt(2); // 1–2 particles per direction per frame
            for (int i = 0; i < particleCount; i++) {
                spawnDirectionalBubble(level, pos, random, dir);
            }

            // Open endpoint spray: shoot extra particles outside the pipe exit face
            // so water visually "comes out" of the pipe end.
            if (isEndpoint) {
                // Check if this direction is actually the exit (not connected to another pipe)
                // We spawn 1-3 extra particles slightly outside the face
                if (random.nextInt(3) == 0) {
                    spawnExitSpray(level, pos, random, dir);
                }
            }
        }

        // Occasional subtle flowing water sound effect
        if (random.nextInt(60) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SoundEvents.WATER_AMBIENT,
                    SoundSource.BLOCKS,
                    0.1F, 1.2F + (random.nextFloat() - random.nextFloat()) * 0.2F,
                    false
            );
        }
    }

    /**
     * Spawns a single bubble particle that travels from the UPSTREAM entry face of this pipe
     * toward the DOWNSTREAM exit face.
     *
     * The particle originates near the ENTRY face (where water comes IN) so it visually
     * travels across the full pipe interior in the flow direction.
     *
     * Entry face = the face OPPOSITE to the flow direction (water enters from behind the flow).
     * Exit face  = the face IN the flow direction (water exits toward the next pipe or the world).
     */
    private static void spawnDirectionalBubble(Level level, BlockPos pos, Random random, Direction dir) {
        double px, py, pz;
        double vx = 0.0D, vy = 0.0D, vz = 0.0D;
        double speed = 0.1D + random.nextDouble() * 0.06D;    // slightly faster = more visible
        double jitter = (random.nextDouble() - 0.5D) * 0.012D;

        // Cross-section spread within the 1x1 interior (0.25 to 0.75 in non-flow axes)
        double cs1 = 0.25D + random.nextDouble() * 0.50D;
        double cs2 = 0.25D + random.nextDouble() * 0.50D;

        // Particle starts near the UPSTREAM (entry) face of this pipe
        // so it visually crosses the full pipe before exiting.
        // Entry = OPPOSITE face of the flow direction.
        switch (dir) {
            case EAST -> {
                // Flow: +X   |  Entry: West face (x ~ 0.25)
                px = pos.getX() + 0.20D + random.nextDouble() * 0.10D;
                py = pos.getY() + cs1;
                pz = pos.getZ() + cs2;
                vx = speed;
                vy = jitter;
                vz = jitter;
            }
            case WEST -> {
                // Flow: -X   |  Entry: East face (x ~ 0.75)
                px = pos.getX() + 0.80D - random.nextDouble() * 0.10D;
                py = pos.getY() + cs1;
                pz = pos.getZ() + cs2;
                vx = -speed;
                vy = jitter;
                vz = jitter;
            }
            case SOUTH -> {
                // Flow: +Z   |  Entry: North face (z ~ 0.25)
                px = pos.getX() + cs1;
                py = pos.getY() + cs2;
                pz = pos.getZ() + 0.20D + random.nextDouble() * 0.10D;
                vx = jitter;
                vy = jitter;
                vz = speed;
            }
            case NORTH -> {
                // Flow: -Z   |  Entry: South face (z ~ 0.75)
                px = pos.getX() + cs1;
                py = pos.getY() + cs2;
                pz = pos.getZ() + 0.80D - random.nextDouble() * 0.10D;
                vx = jitter;
                vy = jitter;
                vz = -speed;
            }
            case DOWN -> {
                // Flow: -Y   |  Entry: Top face (y ~ 0.75)
                px = pos.getX() + cs1;
                py = pos.getY() + 0.75D - random.nextDouble() * 0.10D;
                pz = pos.getZ() + cs2;
                vx = jitter;
                vy = -speed;
                vz = jitter;
            }
            case UP -> {
                // Flow: +Y   |  Entry: Bottom face (y ~ 0.25)
                px = pos.getX() + cs1;
                py = pos.getY() + 0.20D + random.nextDouble() * 0.10D;
                pz = pos.getZ() + cs2;
                vx = jitter;
                vy = speed;
                vz = jitter;
            }
            default -> { return; }
        }

        level.addParticle(ParticleTypes.BUBBLE, px, py, pz, vx, vy, vz);
    }

    /**
     * Spawns a small spray of bubble and splash particles OUTSIDE the pipe exit face.
     * This gives the visual appearance of water leaving the pipe at the open endpoint.
     *
     * Particles are spawned 0.1–0.4 blocks outside the exit face, with velocity
     * outward and slight spreading.
     */
    private static void spawnExitSpray(Level level, BlockPos pos, Random random, Direction dir) {
        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.5D;
        double cz = pos.getZ() + 0.5D;

        // Move to the exit face center (0.5 + 0.5 outward in dir)
        cx += dir.getStepX() * 0.5D;
        cy += dir.getStepY() * 0.5D;
        cz += dir.getStepZ() * 0.5D;

        // Spread within the pipe interior cross-section (±0.2)
        double spread = 0.2D;

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            // Place particle just outside the pipe face (0.05–0.30 blocks out)
            double outDist = 0.05D + random.nextDouble() * 0.25D;
            double px = cx + dir.getStepX() * outDist + (random.nextDouble() - 0.5D) * spread;
            double py = cy + dir.getStepY() * outDist + (random.nextDouble() - 0.5D) * spread;
            double pz = cz + dir.getStepZ() * outDist + (random.nextDouble() - 0.5D) * spread;

            // Velocity: fast in flow direction, slight spread
            double speed = 0.08D + random.nextDouble() * 0.08D;
            double vx = dir.getStepX() * speed + (random.nextDouble() - 0.5D) * 0.04D;
            double vy = dir.getStepY() * speed + (random.nextDouble() - 0.5D) * 0.04D - 0.01D; // slight gravity droop
            double vz = dir.getStepZ() * speed + (random.nextDouble() - 0.5D) * 0.04D;

            level.addParticle(ParticleTypes.BUBBLE, px, py, pz, vx, vy, vz);

            // Occasional SPLASH at the very exit
            if (random.nextInt(4) == 0) {
                level.addParticle(ParticleTypes.SPLASH,
                        cx + dir.getStepX() * 0.08D,
                        cy + dir.getStepY() * 0.08D,
                        cz + dir.getStepZ() * 0.08D,
                        dir.getStepX() * 0.05D,
                        0.08D,
                        dir.getStepZ() * 0.05D);
            }
        }

        // Occasional bubble-pop at the pipe lip
        if (random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.BUBBLE_POP,
                    cx, cy, cz,
                    dir.getStepX() * 0.04D, 0.02D, dir.getStepZ() * 0.04D);
        }
    }

    /**
     * Spawns contained bubble column particles and ambient sounds within the 1x1 pipe cavity.
     */
    public static void spawnBubbleParticles(Level level, BlockPos pos, Random random, BubbleColumnState bubbleState) {
        if (level == null || !level.isClientSide || bubbleState == null || bubbleState == BubbleColumnState.NONE) {
            return;
        }

        double minX = pos.getX() + 0.25D;
        double minZ = pos.getZ() + 0.25D;

        if (bubbleState == BubbleColumnState.UP) {
            for (int i = 0; i < 2; i++) {
                double px = minX + random.nextDouble() * 0.5D;
                double py = pos.getY() + random.nextDouble();
                double pz = minZ + random.nextDouble() * 0.5D;
                level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, px, py, pz, 0.0D, 0.04D, 0.0D);
            }

            if (random.nextInt(6) == 0) {
                double px = minX + random.nextDouble() * 0.5D;
                double py = pos.getY() + 0.8D + random.nextDouble() * 0.2D;
                double pz = minZ + random.nextDouble() * 0.5D;
                level.addParticle(ParticleTypes.BUBBLE_POP, px, py, pz, 0.0D, 0.02D, 0.0D);
            }

            if (random.nextInt(50) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F,
                        false
                );
            }
        } else if (bubbleState == BubbleColumnState.DOWN) {
            for (int i = 0; i < 2; i++) {
                double px = minX + random.nextDouble() * 0.5D;
                double py = pos.getY() + random.nextDouble();
                double pz = minZ + random.nextDouble() * 0.5D;
                level.addParticle(ParticleTypes.CURRENT_DOWN, px, py, pz, 0.0D, -0.04D, 0.0D);
            }

            if (random.nextInt(50) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F,
                        false
                );
            }
        }
    }
}
