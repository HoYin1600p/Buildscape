package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
public interface ColoredItemFrameEntity {
    static Entity create(Level level, BlockPos pos, Direction direction, String color) {
        return Services.PLATFORM.createColoredItemFrameEntity(level, pos, direction, color);
    }

    default Entity asEntity() {
        return (Entity) this;
    }

    default int getId() {
        return asEntity().getId();
    }

    default double getX() {
        return asEntity().getX();
    }

    default double getY() {
        return asEntity().getY();
    }

    default double getZ() {
        return asEntity().getZ();
    }

    default Vec3 position() {
        return asEntity().position();
    }

    default BlockPos blockPosition() {
        return asEntity().blockPosition();
    }

    default Level getEntityLevel() {
        return Services.PLATFORM.getEntityLevel(asEntity());
    }

    Direction getDirection();
    String getColorVariant();
    void setColorVariant(String color);
    String getParticlePattern();
    void setParticlePattern(String pattern);
    String getParticleColorsRaw();
    void setParticleColorsRaw(String colors);
    ItemStack getItem();
    void setItem(ItemStack stack);
    int getRotation();
    void setRotation(int rotation);
}
