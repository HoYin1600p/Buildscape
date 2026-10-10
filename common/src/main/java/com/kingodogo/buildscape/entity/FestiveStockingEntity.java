package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
public interface FestiveStockingEntity {
    static Entity create(Level level, BlockPos pos, Direction direction, String color) {
        return Services.PLATFORM.createFestiveStockingEntity(level, pos, direction, color);
    }

    default Entity asEntity() {
        return (Entity) this;
    }

    String getColorVariant();
    void setColorVariant(String color);
    ItemStack getItem();
    void setItem(ItemStack stack);
    Direction getDirection();
}
