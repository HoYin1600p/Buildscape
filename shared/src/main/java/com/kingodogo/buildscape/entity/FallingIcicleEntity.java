package com.kingodogo.buildscape.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
public interface FallingIcicleEntity {
    BlockState getBlockState();
    BlockPos getStartPos();
}
