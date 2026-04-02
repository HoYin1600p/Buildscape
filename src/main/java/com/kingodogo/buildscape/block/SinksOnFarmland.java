package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface SinksOnFarmland {
    BooleanProperty ON_FARMLAND = BooleanProperty.create("on_farmland");

    default boolean shouldSink(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }
}
