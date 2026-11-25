package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface CushionEntity {
    DyeColor getColor();
    BlockState getRenderState();
    BlockPos getSupportPos();

    static Entity create(Level level, BlockPos supportPos, double localX, double localZ, double supportHeight, DyeColor color) {
        return Services.PLATFORM.createCushionEntity(level, supportPos, localX, localZ, supportHeight, color);
    }
}
