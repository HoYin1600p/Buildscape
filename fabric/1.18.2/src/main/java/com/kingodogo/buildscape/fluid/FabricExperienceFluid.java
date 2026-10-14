package com.kingodogo.buildscape.fluid;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.Item;

public abstract class FabricExperienceFluid extends WaterFluid {
    public static final FlowingFluid STILL = new Source();
    public static final FlowingFluid FLOWING = new Flowing();
    public static void register() {
        Services.PLATFORM.register(Registry.FLUID, new CommonId("buildscape", "experience_still"), STILL);
        Services.PLATFORM.register(Registry.FLUID, new CommonId("buildscape", "experience_flowing"), FLOWING);
    }
    @Override public Fluid getSource() { return STILL; }
    @Override public Fluid getFlowing() { return FLOWING; }
    @Override public boolean isSame(Fluid fluid) { return fluid == STILL || fluid == FLOWING; }
    @Override public Item getBucket() { return Services.PLATFORM.getItem(new CommonId("buildscape", "experience_bucket")); }
    @Override public BlockState createLegacyBlock(FluidState state) {
        return Services.PLATFORM.getBlock(new CommonId("buildscape", "experience_liquid")).defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }
    @Override protected boolean canConvertToSource() { return false; }
    public static final class Source extends FabricExperienceFluid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }
    public static final class Flowing extends FabricExperienceFluid {
        @Override protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }
}
