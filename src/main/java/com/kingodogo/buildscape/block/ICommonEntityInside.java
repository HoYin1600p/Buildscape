package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
public interface ICommonEntityInside {
    void onEntityInside(Level level, BlockPos pos, BlockState state, Entity entity);
}
