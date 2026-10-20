package com.kingodogo.buildscape.feature.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.Set;

public class BubbleColumnHandler {

    public static BubbleColumnState detectBubbleColumnBase(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) {
            return BubbleColumnState.NONE;
        }
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
        if (state == null) {
            return false;
        }
        return state.is(Blocks.SOUL_SAND) || state.is(Blocks.MAGMA_BLOCK);
    }

    public static void handleEntityInside(Level level, BlockPos pos, BlockState state, Entity entity,
            PipeFlowState flowState) {
        if (entity == null || flowState == null || !flowState.hasWater()) {
            return;
        }

        Vec3 delta = entity.getDeltaMovement();
        BubbleColumnState bubble = flowState.getBubbleColumn();

        double flowX = 0.0;
        double flowY = 0.0;
        double flowZ = 0.0;
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
            double newY = Math.min(delta.y + 0.1, 0.7);
            entity.setDeltaMovement(delta.x + flowX * 0.03, newY, delta.z + flowZ * 0.03);
            entity.resetFallDistance();

            if (entity instanceof LivingEntity living) {
                living.setAirSupply(living.getMaxAirSupply());
            }
            return;
        } else if (bubble == BubbleColumnState.DOWN) {
            double newY = Math.max(delta.y - 0.08, -0.7);
            entity.setDeltaMovement(delta.x + flowX * 0.03, newY, delta.z + flowZ * 0.03);
            return;
        }

        boolean isItem = entity instanceof ItemEntity;
        double pushStrength = isItem ? 0.07 : 0.03;

        double newX = delta.x * (isItem ? 0.85 : 0.95) + flowX * pushStrength;
        double newZ = delta.z * (isItem ? 0.85 : 0.95) + flowZ * pushStrength;
        double newY = delta.y;

        if (flowY < 0) {
            newY = Math.max(delta.y - 0.06, -0.4);
        } else if (isItem) {
            newY = Math.max(delta.y * 0.8, -0.03);
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
                double px = pos.getX() + 0.3 + random.nextDouble() * 0.4;
                double py = pos.getY() + 0.2 + random.nextDouble() * 0.3;
                double pz = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
                level.addParticle(ParticleTypes.UNDERWATER, px, py, pz, 0.0, 0.0, 0.0);
            }
            return;
        }

        double waterTopY;
        if (flowState.isSource()) {
            waterTopY = 0.75;
        } else {
            waterTopY = switch (flowState.getDistance()) {
                case 1 -> 0.70;
                case 2 -> 0.61;
                case 3 -> 0.52;
                case 4 -> 0.43;
                case 5 -> 0.34;
                case 6 -> 0.25;
                case 7 -> 0.16;
                default -> 0.13;
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
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.WATER_AMBIENT,
                    SoundSource.BLOCKS,
                    0.1F, 1.2F + (random.nextFloat() - random.nextFloat()) * 0.2F,
                    false);
        }
    }

    private static void spawnDirectionalBubble(Level level, BlockPos pos, Random random, Direction dir,
            double waterTopY) {
        double px, py, pz;
        double vx = 0.0, vy = 0.0, vz = 0.0;
        double speed = 0.08 + random.nextDouble() * 0.04;
        double jitter = (random.nextDouble() - 0.5) * 0.008;

        double csX = 0.25 + random.nextDouble() * 0.50;
        double csZ = 0.25 + random.nextDouble() * 0.50;
        double csY = Math.max(0.13, waterTopY - 0.02);

        switch (dir) {
            case EAST -> {
                px = pos.getX() + 0.20 + random.nextDouble() * 0.10;
                py = pos.getY() + csY;
                pz = pos.getZ() + csZ;
                vx = speed;
                vy = 0.0;
                vz = jitter;
            }
            case WEST -> {
                px = pos.getX() + 0.80 - random.nextDouble() * 0.10;
                py = pos.getY() + csY;
                pz = pos.getZ() + csZ;
                vx = -speed;
                vy = 0.0;
                vz = jitter;
            }
            case SOUTH -> {
                px = pos.getX() + csX;
                py = pos.getY() + csY;
                pz = pos.getZ() + 0.20 + random.nextDouble() * 0.10;
                vx = jitter;
                vy = 0.0;
                vz = speed;
            }
            case NORTH -> {
                px = pos.getX() + csX;
                py = pos.getY() + csY;
                pz = pos.getZ() + 0.80 - random.nextDouble() * 0.10;
                vx = jitter;
                vy = 0.0;
                vz = -speed;
            }
            case DOWN -> {
                px = pos.getX() + csX;
                py = pos.getY() + 0.75 - random.nextDouble() * 0.10;
                pz = pos.getZ() + csZ;
                vx = jitter;
                vy = -speed;
                vz = jitter;
            }
            case UP -> {
                px = pos.getX() + csX;
                py = pos.getY() + 0.20 + random.nextDouble() * 0.10;
                pz = pos.getZ() + csZ;
                vx = jitter;
                vy = speed;
                vz = jitter;
            }
            default -> {
                return;
            }
        }

        level.addParticle(ParticleTypes.BUBBLE, px, py, pz, vx, vy, vz);
        if (random.nextInt(5) == 0 && dir != Direction.DOWN && dir != Direction.UP) {
            level.addParticle(ParticleTypes.BUBBLE_POP, px, py + 0.01, pz, vx * 0.3, 0.005, vz * 0.3);
        }
    }

    private static void spawnExitSpray(Level level, BlockPos pos, Random random, Direction dir, double waterTopY) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + Math.max(0.13, waterTopY - 0.02);
        double cz = pos.getZ() + 0.5;

        cx += dir.getStepX() * 0.5;
        cz += dir.getStepZ() * 0.5;

        double spread = 0.18;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double outDist = 0.05 + random.nextDouble() * 0.20;
            double px = cx + dir.getStepX() * outDist + (random.nextDouble() - 0.5) * spread;
            double py = cy + (random.nextDouble() - 0.5) * 0.02;
            double pz = cz + dir.getStepZ() * outDist + (random.nextDouble() - 0.5) * spread;

            double speed = 0.06 + random.nextDouble() * 0.04;
            double vx = dir.getStepX() * speed + (random.nextDouble() - 0.5) * 0.02;
            double vy = 0.0;
            double vz = dir.getStepZ() * speed + (random.nextDouble() - 0.5) * 0.02;

            level.addParticle(ParticleTypes.BUBBLE, px, py, pz, vx, vy, vz);
        }
    }

    public static void spawnBubbleParticles(Level level, BlockPos pos, Random random, BubbleColumnState bubbleState) {
        if (level == null || !level.isClientSide || bubbleState == null || bubbleState == BubbleColumnState.NONE) {
            return;
        }

        double minX = pos.getX() + 0.25;
        double minZ = pos.getZ() + 0.25;

        if (bubbleState == BubbleColumnState.UP) {
            for (int i = 0; i < 2; i++) {
                double px = minX + random.nextDouble() * 0.5;
                double py = pos.getY() + 0.15 + random.nextDouble() * 0.35;
                double pz = minZ + random.nextDouble() * 0.5;
                level.addParticle(ParticleTypes.BUBBLE, px, py, pz, 0.0, 0.01, 0.0);
            }

            if (random.nextInt(6) == 0) {
                double px = minX + random.nextDouble() * 0.5;
                double py = pos.getY() + 0.40 + random.nextDouble() * 0.1;
                double pz = minZ + random.nextDouble() * 0.5;
                level.addParticle(ParticleTypes.BUBBLE_POP, px, py, pz, 0.0, 0.01, 0.0);
            }

            if (random.nextInt(50) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F,
                        false);
            }
        } else if (bubbleState == BubbleColumnState.DOWN) {
            for (int i = 0; i < 2; i++) {
                double px = minX + random.nextDouble() * 0.5;
                double py = pos.getY() + 0.15 + random.nextDouble() * 0.35;
                double pz = minZ + random.nextDouble() * 0.5;
                level.addParticle(ParticleTypes.CURRENT_DOWN, px, py, pz, 0.0, -0.02, 0.0);
            }

            if (random.nextInt(50) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F,
                        false);
            }
        }
    }
}
