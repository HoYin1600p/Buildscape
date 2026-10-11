package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
public interface ICommonNeighborAware {
    void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block neighborBlock, BlockPos fromPos, boolean isMoving);
}
