package com.kingodogo.buildscape.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

public class PoplarBoatEntity extends Boat {

    public PoplarBoatEntity(
            EntityType<? extends Boat> entityType,
            Level level
    ) {
        super(entityType, level);
        this.setType(Boat.Type.OAK);
    }

    public PoplarBoatEntity(Level level, double x, double y, double z) {
        this(
                com.kingodogo.buildscape.entity.ModEntities.POPLAR_BOAT.get(),
                level
        );
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setType(Boat.Type.OAK);
    }

    @Override
    public net.minecraft.world.item.Item getDropItem() {
        return com.kingodogo.buildscape.item.ModItems.POPLAR_BOAT.get();
    }
}
