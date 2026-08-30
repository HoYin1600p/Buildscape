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

// Bubble column logic and entity physics inside Hollow Steel Pipes
public class BubbleColumnHandler {

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

    public static boolean isBubbleColumnBase(BlockState state) {
        if (state == null) return false;
        return state.is(Blocks.SOUL_SAND) || state.is(Blocks.MAGMA_BLOCK);
    }

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

    // Spawns flow particles and bubble column effects inside pipe cavity
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
                double py = pos.getY() + 0.2D + random.nextDouble() * 0.3D;
                double pz = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;
                level.addParticle(ParticleTypes.UNDERWATER, px, py, pz, 0.0D, 0.0D, 0.0D);
            }
            return;
        }

        double waterTopY;
        if (flowState.isSource()) {
            waterTopY = 0.75D;
        } else {
            waterTopY = switch (flowState.getDistance()) {
                case 1 -> 0.70D;
                case 2 -> 0.61D;
                case 3 -> 0.52D;
                case 4 -> 0.43D;
                case 5 -> 0.34D;
                case 6 -> 0.25D;
                case 7 -> 0.16D;
                default -> 0.13D;
            };
        }

        // For each active flow direction:
        //   - Spawn 1-2 bubbles that travel along the flow direction (from entry face to exit face).
        //   - If this is an open endpoint in this direction, also spray bubbles slightly outside.
        boolean isEndpoint = flowState.isOpenEndpoint();

        for (Direction dir : flowDirs) {
            int particleCount = 1 + random.nextInt(2); // 1–2 particles per direction per frame
            for (int i = 0; i < particleCount; i++) {
                spawnDirectionalBubble(level, pos, random, dir, waterTopY);
            }

            // Open endpoint spray: shoot extra particles outside the pipe exit face
            // so water visually "comes out" of the pipe end.
            if (isEndpoint) {
                // Check if this direction is actually the exit (not connected to another pipe)
                // We spawn 1-3 extra particles slightly outside the face
                if (random.nextInt(3) == 0) {
                    spawnExitSpray(level, pos, random, dir, waterTopY);
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
     * Spawns a single bubble particle that floats directly on the WATER SURFACE
     * and travels from the UPSTREAM entry face of this pipe toward the DOWNSTREAM exit face.
     */
    private static void spawnDirectionalBubble(Level level, BlockPos pos, Random random, Direction dir, double waterTopY) {
        double px, py, pz;
        double vx = 0.0D, vy = 0.0D, vz = 0.0D;
        double speed = 0.08D + random.nextDouble() * 0.04D;
        double jitter = (random.nextDouble() - 0.5D) * 0.008D;

        double csX = 0.25D + random.nextDouble() * 0.50D;
        double csZ = 0.25D + random.nextDouble() * 0.50D;
        double csY = Math.max(0.13D, waterTopY - 0.02D); // Floats directly on the water surface!

        switch (dir) {
            case EAST -> {
                // Flow: +X
                px = pos.getX() + 0.20D + random.nextDouble() * 0.10D;
                py = pos.getY() + csY;
                pz = pos.getZ() + csZ;
                vx = speed;
                vy = 0.0D;
                vz = jitter;
            }
            case WEST -> {
                // Flow: -X
                px = pos.getX() + 0.80D - random.nextDouble() * 0.10D;
                py = pos.getY() + csY;
                pz = pos.getZ() + csZ;
                vx = -speed;
                vy = 0.0D;
                vz = jitter;
            }
            case SOUTH -> {
                // Flow: +Z
                px = pos.getX() + csX;
                py = pos.getY() + csY;
                pz = pos.getZ() + 0.20D + random.nextDouble() * 0.10D;
                vx = jitter;
                vy = 0.0D;
                vz = speed;
            }
            case NORTH -> {
                // Flow: -Z
                px = pos.getX() + csX;
                py = pos.getY() + csY;
                pz = pos.getZ() + 0.80D - random.nextDouble() * 0.10D;
                vx = jitter;
                vy = 0.0D;
                vz = -speed;
            }
            case DOWN -> {
                // Flow: -Y (Vertical Drop)
                px = pos.getX() + csX;
                py = pos.getY() + 0.75D - random.nextDouble() * 0.10D;
                pz = pos.getZ() + csZ;
                vx = jitter;
                vy = -speed;
                vz = jitter;
            }
            case UP -> {
                // Flow: +Y (Bubble Elevator)
                px = pos.getX() + csX;
                py = pos.getY() + 0.20D + random.nextDouble() * 0.10D;
                pz = pos.getZ() + csZ;
                vx = jitter;
                vy = speed;
                vz = jitter;
            }
            default -> { return; }
        }

        level.addParticle(ParticleTypes.BUBBLE, px, py, pz, vx, vy, vz);
        if (random.nextInt(5) == 0 && dir != Direction.DOWN && dir != Direction.UP) {
            level.addParticle(ParticleTypes.BUBBLE_POP, px, py + 0.01D, pz, vx * 0.3D, 0.005D, vz * 0.3D);
        }
    }

    /**
     * Spawns a small spray of bubble and splash particles OUTSIDE the pipe exit face.
     * This gives the visual appearance of water leaving the pipe at the open endpoint.
     */
    private static void spawnExitSpray(Level level, BlockPos pos, Random random, Direction dir, double waterTopY) {
        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + Math.max(0.13D, waterTopY - 0.02D);
        double cz = pos.getZ() + 0.5D;

        // Move to the exit face center
        cx += dir.getStepX() * 0.5D;
        cz += dir.getStepZ() * 0.5D;

        double spread = 0.18D;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double outDist = 0.05D + random.nextDouble() * 0.20D;
            double px = cx + dir.getStepX() * outDist + (random.nextDouble() - 0.5D) * spread;
            double py = cy + (random.nextDouble() - 0.5D) * 0.02D;
            double pz = cz + dir.getStepZ() * outDist + (random.nextDouble() - 0.5D) * spread;

            double speed = 0.06D + random.nextDouble() * 0.04D;
            double vx = dir.getStepX() * speed + (random.nextDouble() - 0.5D) * 0.02D;
            double vy = 0.0D;
            double vz = dir.getStepZ() * speed + (random.nextDouble() - 0.5D) * 0.02D;

            level.addParticle(ParticleTypes.BUBBLE, px, py, pz, vx, vy, vz);
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
                double py = pos.getY() + 0.15D + random.nextDouble() * 0.35D;
                double pz = minZ + random.nextDouble() * 0.5D;
                level.addParticle(ParticleTypes.BUBBLE, px, py, pz, 0.0D, 0.01D, 0.0D);
            }

            if (random.nextInt(6) == 0) {
                double px = minX + random.nextDouble() * 0.5D;
                double py = pos.getY() + 0.40D + random.nextDouble() * 0.1D;
                double pz = minZ + random.nextDouble() * 0.5D;
                level.addParticle(ParticleTypes.BUBBLE_POP, px, py, pz, 0.0D, 0.01D, 0.0D);
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
                double py = pos.getY() + 0.15D + random.nextDouble() * 0.35D;
                double pz = minZ + random.nextDouble() * 0.5D;
                level.addParticle(ParticleTypes.CURRENT_DOWN, px, py, pz, 0.0D, -0.02D, 0.0D);
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
