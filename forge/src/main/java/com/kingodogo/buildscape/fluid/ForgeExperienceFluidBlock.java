package com.kingodogo.buildscape.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import java.util.Random;
import java.util.function.Supplier;

/** Forge's supplier constructor defers fluid lookup until after fluid registration. */
public final class ForgeExperienceFluidBlock extends LiquidBlock {
    public ForgeExperienceFluidBlock(Supplier<? extends FlowingFluid> fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(30) == 0) {
            com.kingodogo.buildscape.block.ExperienceFluidBlock.spawnXpParticle(level, pos, random.nextDouble(), random.nextDouble());
        }
    }
}
