package com.kingodogo.buildscape.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class ColoredItemFrameEntity extends ItemFrame {

    public ColoredItemFrameEntity(EntityType<? extends ItemFrame> type, Level level) {
        super(type, level);
    }

    public ColoredItemFrameEntity(EntityType<? extends ItemFrame> type, Level level, BlockPos pos, Direction direction) {
        super(type, level, pos, direction);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
