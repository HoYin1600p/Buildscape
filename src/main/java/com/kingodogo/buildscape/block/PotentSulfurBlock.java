package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

public class PotentSulfurBlock extends Block {

    public PotentSulfurBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        boolean isUnderwater = aboveState.getFluidState().is(net.minecraft.tags.FluidTags.WATER);

        if (isUnderwater) {
            // Spawn bubbles rising underwater
            for (int i = 0; i < 2; i++) {
                double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
                level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0.0, 0.1, 0.0);
            }

            // Find the top of the water column (surface of the water block)
            BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            int maxSearch = 32;
            while (maxSearch > 0 && level.getFluidState(checkPos).is(net.minecraft.tags.FluidTags.WATER)) {
                checkPos.move(net.minecraft.core.Direction.UP);
                maxSearch--;
            }

            // checkPos is now the block directly above the top water block
            if (level.getBlockState(checkPos).isAir()) {
                if (random.nextFloat() < 0.60F) { // 40% reduction
                    double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
                    double y = checkPos.getY() + 0.05; // spawn slightly above the water surface
                    double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
                    level.addParticle(com.kingodogo.buildscape.particle.ModParticles.NOXIOUS_GAS.get(), x, y, z, (random.nextDouble() - 0.5) * 0.02, 0.03, (random.nextDouble() - 0.5) * 0.02);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        applyNauseaEffect(level, pos);
        level.scheduleTick(pos, this, 20); // check again in 20 ticks (1 second)
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 20);
        }
    }

    private static void applyNauseaEffect(Level level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        boolean isUnderwater = level.getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER);
        if (!isUnderwater) {
            return; // Only apply nausea when placed underwater
        }

        // Trace up to find the top of the water column
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        int maxSearch = 32;
        while (maxSearch > 0 && level.getFluidState(checkPos).is(net.minecraft.tags.FluidTags.WATER)) {
            checkPos.move(net.minecraft.core.Direction.UP);
            maxSearch--;
        }

        // checkPos is now the first block directly above the water surface.
        // We only apply nausea in the gas column above the water (up to 4 air blocks above)
        int gasStartY = checkPos.getY();
        int gasEndY = gasStartY - 1;

        for (int i = 0; i < 4; i++) {
            BlockPos airCheck = new BlockPos(pos.getX(), gasStartY + i, pos.getZ());
            if (level.getBlockState(airCheck).isAir()) {
                gasEndY = airCheck.getY();
            } else {
                break;
            }
        }

        if (gasEndY >= gasStartY) {
            AABB area = new AABB(
                pos.getX(), gasStartY, pos.getZ(),
                pos.getX() + 1, gasEndY + 1, pos.getZ() + 1
            );
            
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0)); // Nausea for 8 seconds
            }
        }
    }
}
