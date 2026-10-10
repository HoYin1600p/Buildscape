package com.kingodogo.buildscape.adapter.v26x.fluid;

import com.kingodogo.buildscape.fluid.ExperienceFluidProperties;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

/** Loader-neutral 26.2 equivalent of the reference ForgeFlowingFluid. */
public abstract class ExperienceFluid extends FlowingFluid {
    @Override public Fluid getSource() { return ExperienceFluids.still(); }
    @Override public Fluid getFlowing() { return ExperienceFluids.flowing(); }
    @Override public Item getBucket() { return Services.PLATFORM.getItem(new CommonId("buildscape", "experience_bucket")); }
    @Override public boolean isSame(Fluid other) { return other == getSource() || other == getFlowing(); }
    @Override protected boolean canConvertToSource(ServerLevel level) { return ExperienceFluidProperties.CONVERTS_TO_SOURCE; }
    @Override public int getSlopeFindDistance(LevelReader level) { return ExperienceFluidProperties.SLOPE_FIND_DISTANCE; }
    @Override public int getDropOff(LevelReader level) { return ExperienceFluidProperties.DROP_OFF; }
    @Override public int getTickDelay(LevelReader level) { return ExperienceFluidProperties.TICK_DELAY; }
    @Override public float getExplosionResistance() { return ExperienceFluidProperties.EXPLOSION_RESISTANCE; }
    @Override public Optional<SoundEvent> getPickupSound() { return Optional.of(SoundEvents.BUCKET_FILL); }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        Block.dropResources(state, level, pos, state.hasBlockEntity() ? level.getBlockEntity(pos) : null);
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return Services.PLATFORM.getBlock(new CommonId("buildscape", "experience_liquid"))
                .defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid other, Direction direction) {
        // The reference forbids experience/lava mixing, in either direction.
        return direction == Direction.DOWN && !isSame(other) && !Fluids.LAVA.isSame(other);
    }

    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluid) {
        if (!Fluids.LAVA.isSame(level.getFluidState(pos).getType())) {
            super.spreadTo(level, pos, state, direction, fluid);
        }
    }

    public static class Source extends ExperienceFluid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }

    public static class Flowing extends ExperienceFluid {
        public Flowing() { registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7)); }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }
}
