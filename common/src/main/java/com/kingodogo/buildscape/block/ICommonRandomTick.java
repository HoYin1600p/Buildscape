package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
public interface ICommonRandomTick {
    void onRandomTick(BlockState state, ServerLevel level, BlockPos pos);
}
