package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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

        boolean isItem = entity instanceof ItemEntity;
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
        if (level == null || !level.isClientSide() || flowState == null || !flowState.hasWater()) {
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

        double waterTopY = flowState.isSource() ? 0.75D : (0.70D - flowState.getDistance() * 0.08D);

        for (Direction dir : flowDirs) {
            int particleCount = 1 + random.nextInt(2);
            for (int i = 0; i < particleCount; i++) {
                spawnDirectionalBubble(level, pos, random, dir, waterTopY);
            }
        }

        if (random.nextInt(60) == 0) {
            Services.PLATFORM.playWaterAmbient(level, pos);
        }
    }

    public static void spawnFlowParticles(Level level, BlockPos pos, PipeFlowState flowState) {
        spawnFlowParticles(level, pos, ThreadLocalRandom.current(), flowState);
    }

    private static void spawnDirectionalBubble(Level level, BlockPos pos, Random random, Direction dir, double waterTopY) {
        double csX = 0.25D + random.nextDouble() * 0.50D;
        double csZ = 0.25D + random.nextDouble() * 0.50D;
        double csY = Math.max(0.13D, waterTopY - 0.02D);
        double speed = 0.08D + random.nextDouble() * 0.04D;
        double jitter = (random.nextDouble() - 0.5D) * 0.008D;

        double px = pos.getX() + csX;
        double py = pos.getY() + csY;
        double pz = pos.getZ() + csZ;
        double vx = dir.getStepX() * speed;
        double vy = dir.getStepY() * speed;
        double vz = dir.getStepZ() * speed;

        if (dir.getAxis().isHorizontal()) {
            vx += jitter;
            vz += jitter;
        }

        level.addParticle(ParticleTypes.BUBBLE, px, py, pz, vx, vy, vz);
    }

    public static void spawnBubbleParticles(Level level, BlockPos pos, Random random, BubbleColumnState bubbleState) {
        if (level == null || !level.isClientSide() || bubbleState == null || bubbleState == BubbleColumnState.NONE) {
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
        } else if (bubbleState == BubbleColumnState.DOWN) {
            for (int i = 0; i < 2; i++) {
                double px = minX + random.nextDouble() * 0.5D;
                double py = pos.getY() + 0.15D + random.nextDouble() * 0.35D;
                double pz = minZ + random.nextDouble() * 0.5D;
                level.addParticle(ParticleTypes.CURRENT_DOWN, px, py, pz, 0.0D, -0.02D, 0.0D);
            }
        }
    }
}
