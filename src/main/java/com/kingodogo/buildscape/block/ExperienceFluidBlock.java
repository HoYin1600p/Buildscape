package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.Random;

public class ExperienceFluidBlock extends LiquidBlock {
    public ExperienceFluidBlock(java.util.function.Supplier<? extends FlowingFluid> fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(30) == 0) {
            double x = (double) pos.getX() + random.nextDouble();
            double y = (double) pos.getY() + 1.0D;
            double z = (double) pos.getZ() + random.nextDouble();
            level.addParticle(ModParticles.XP_PARTICLE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
