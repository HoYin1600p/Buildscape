package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
public interface ICommonShapeUpdate {
    BlockState onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelReader level, BlockPos pos, BlockPos neighborPos);
}
