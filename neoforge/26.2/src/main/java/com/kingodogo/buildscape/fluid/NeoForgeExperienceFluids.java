package com.kingodogo.buildscape.fluid;

import com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluid;
import com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public final class NeoForgeExperienceFluids {
    private static FluidType type;

    private NeoForgeExperienceFluids() {}

    public static FluidType type() {
        if (type == null) {
            type = new FluidType(FluidType.Properties.create()
                    .descriptionId("block.buildscape.experience_liquid")
                    .lightLevel(ExperienceFluidProperties.LIGHT_LEVEL)
                    .density(ExperienceFluidProperties.DENSITY)
                    .viscosity(ExperienceFluidProperties.VISCOSITY)
                    .canConvertToSource(ExperienceFluidProperties.CONVERTS_TO_SOURCE)
                    .isWaterLike(true).canSwim(true).canPushEntity(true).canExtinguish(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
                @Override public BlockState getBlockForFluidState(net.minecraft.world.level.BlockAndLightGetter level,
                        net.minecraft.core.BlockPos pos, FluidState state) {
                    return state.createLegacyBlock();
                }
            };
        }
        return type;
    }

    public static ExperienceFluids.Pair createPair() {
        return new ExperienceFluids.Pair(new Source(), new Flowing());
    }

    private static final class Source extends ExperienceFluid.Source {
        @Override public FluidType getFluidType() { return type(); }
    }

    private static final class Flowing extends ExperienceFluid.Flowing {
        @Override public FluidType getFluidType() { return type(); }
    }
}
