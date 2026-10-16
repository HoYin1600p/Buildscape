package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;

/** Water physics with distinct registry identities and the experience block/bucket. */
public final class ExperienceFluids {
    private ExperienceFluids() {}
    private static java.util.function.Supplier<? extends WaterFluid> sourceFactory = Source::new;
    private static java.util.function.Supplier<? extends WaterFluid> flowingFactory = Flowing::new;
    private static boolean constructed;
    public static void installFactories(java.util.function.Supplier<? extends WaterFluid> source,
            java.util.function.Supplier<? extends WaterFluid> flowing) {
        if (constructed) throw new IllegalStateException("Experience fluids have already been constructed");
        sourceFactory = java.util.Objects.requireNonNull(source);
        flowingFactory = java.util.Objects.requireNonNull(flowing);
    }
    private static final class Values {
        private static final WaterFluid STILL = sourceFactory.get();
        private static final WaterFluid FLOWING = flowingFactory.get();
        static { constructed = true; }
    }
    public static WaterFluid still() { return Values.STILL; }
    public static WaterFluid flowing() { return Values.FLOWING; }

    private static BlockState blockState(FluidState state) {
        return Services.PLATFORM.getBlock(new CommonId("buildscape", "experience_liquid"))
                .defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    private static int getLegacyLevel(FluidState state) {
        return state.isSource() ? 0 : 8 - Math.min(state.getAmount(), 8) + (state.getValue(net.minecraft.world.level.material.FlowingFluid.FALLING) ? 8 : 0);
    }

    public static class Source extends WaterFluid.Source {
        @Override protected boolean canConvertToSource(net.minecraft.world.level.Level level) { return false; }
        @Override public Fluid getSource() { return still(); }
        @Override public Fluid getFlowing() { return flowing(); }
        @Override public boolean isSame(Fluid fluid) { return fluid == still() || fluid == flowing(); }
        @Override public Item getBucket() { return Services.PLATFORM.getItem(new CommonId("buildscape", "experience_bucket")); }
        @Override public BlockState createLegacyBlock(FluidState state) { return blockState(state); }
    }
    public static class Flowing extends WaterFluid.Flowing {
        @Override protected boolean canConvertToSource(net.minecraft.world.level.Level level) { return false; }
        @Override public Fluid getSource() { return still(); }
        @Override public Fluid getFlowing() { return flowing(); }
        @Override public boolean isSame(Fluid fluid) { return fluid == still() || fluid == flowing(); }
        @Override public Item getBucket() { return Services.PLATFORM.getItem(new CommonId("buildscape", "experience_bucket")); }
        @Override public BlockState createLegacyBlock(FluidState state) { return blockState(state); }
    }
}
