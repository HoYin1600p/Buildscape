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

        boolean isItem = entity instanceof net.minecraft.world.entity.item.ItemEntity;
        double pushStrength = isItem ? 0.07D : 0.03D;

        double newX = delta.x * (isItem ? 0.85D : 0.95D) + flowX * pushStrength;
        double newZ = delta.z * (isItem ? 0.85D : 0.95D) + flowZ * pushStrength;
        double newY = delta.y;

        if (flowY < 0) {
            newY = Math.max(delta.y - 0.06D, -0.4D);
        } else if (isItem) {
            newY = Math.max(delta.y * 0.8D, -0.03D);
        }

        entity.setDeltaMovement(newX, newY, newZ);
        entity.resetFallDistance();
    }

    public static void spawnFlowParticles(Level level, BlockPos pos, Random random, PipeFlowState flowState) {
        if (level == null || !level.isClientSide || flowState == null || !flowState.hasWater()) {
            return;
        }

        if (flowState.getBubbleColumn().isActive()) {
            spawnBubbleParticles(level, pos, random, flowState.getBubbleColumn());
        }

        Set<Direction> flowDirs = flowState.getFlowDirections();
        if (flowDirs.isEmpty()) {
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

        boolean isEndpoint = flowState.isOpenEndpoint();

        for (Direction dir : flowDirs) {
            int particleCount = 1 + random.nextInt(2);
            for (int i = 0; i < particleCount; i++) {
                spawnDirectionalBubble(level, pos, random, dir, waterTopY);
            }

            if (isEndpoint) {
                if (random.nextInt(3) == 0) {
                    spawnExitSpray(level, pos, random, dir, waterTopY);
                }
            }
        }

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

    private static void spawnDirectionalBubble(Level level, BlockPos pos, Random random, Direction dir, double waterTopY) {
        double px, py, pz;
        double vx = 0.0D, vy = 0.0D, vz = 0.0D;
        double speed = 0.08D + random.nextDouble() * 0.04D;
        double jitter = (random.nextDouble() - 0.5D) * 0.008D;

        double csX = 0.25D + random.nextDouble() * 0.50D;
        double csZ = 0.25D + random.nextDouble() * 0.50D;
        double csY = Math.max(0.13D, waterTopY - 0.02D);

        switch (dir) {
            case EAST -> {
                px = pos.getX() + 0.20D + random.nextDouble() * 0.10D;
                py = pos.getY() + csY;
                pz = pos.getZ() + csZ;
                vx = speed;
                vy = 0.0D;
                vz = jitter;
            }
            case WEST -> {
                px = pos.getX() + 0.80D - random.nextDouble() * 0.10D;
                py = pos.getY() + csY;
                pz = pos.getZ() + csZ;
                vx = -speed;
                vy = 0.0D;
                vz = jitter;
            }
            case SOUTH -> {
                px = pos.getX() + csX;
                py = pos.getY() + csY;
                pz = pos.getZ() + 0.20D + random.nextDouble() * 0.10D;
                vx = jitter;
                vy = 0.0D;
                vz = speed;
            }
            case NORTH -> {
                px = pos.getX() + csX;
                py = pos.getY() + csY;
                pz = pos.getZ() + 0.80D - random.nextDouble() * 0.10D;
                vx = jitter;
                vy = 0.0D;
                vz = -speed;
            }
            case DOWN -> {
                px = pos.getX() + csX;
                py = pos.getY() + 0.75D - random.nextDouble() * 0.10D;
                pz = pos.getZ() + csZ;
                vx = jitter;
                vy = -speed;
                vz = jitter;
            }
            case UP -> {
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

    private static void spawnExitSpray(Level level, BlockPos pos, Random random, Direction dir, double waterTopY) {
        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + Math.max(0.13D, waterTopY - 0.02D);
        double cz = pos.getZ() + 0.5D;

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
