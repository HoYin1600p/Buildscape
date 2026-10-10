package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;
public class ExperienceFluidBlock extends LiquidBlock {
    private final Supplier<? extends FlowingFluid> fluidSupplier;

    public ExperienceFluidBlock(Supplier<? extends FlowingFluid> fluidSupplier, BlockBehaviour.Properties properties) {
        super(Fluids.WATER, properties);
        this.fluidSupplier = fluidSupplier;
    }

    public FlowingFluid getFlowingFluid() {
        return fluidSupplier != null ? fluidSupplier.get() : null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        FlowingFluid f = getFlowingFluid();
        if (f != null && f != Fluids.EMPTY && f != Fluids.WATER) {
            int level = state.getValue(LEVEL);
            if (level == 0) return f.getSource(false);
            if (level < 8) return f.getFlowing(8 - level, false);
            return f.getFlowing(8, true);
        }
        return super.getFluidState(state);
    }

    public static void spawnXpParticle(Level level, BlockPos pos, double offsetX, double offsetZ) {
        double x = (double) pos.getX() + offsetX;
        double y = (double) pos.getY() + 1.0D;
        double z = (double) pos.getZ() + offsetZ;
        level.addParticle(com.kingodogo.buildscape.particle.ModParticles.XP_PARTICLE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
