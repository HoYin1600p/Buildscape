package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
public interface ICommonRemoval {
    void onBlockRemoved(Level level, BlockPos pos, BlockState state);
}
