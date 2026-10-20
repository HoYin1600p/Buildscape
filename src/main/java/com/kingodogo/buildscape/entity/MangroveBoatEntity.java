package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.registry.ModEntities;
import com.kingodogo.buildscape.registry.items.WoodItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class MangroveBoatEntity extends Boat {

    public MangroveBoatEntity(
            EntityType<? extends Boat> entityType,
            Level level) {
        super(entityType, level);
        this.setType(Boat.Type.OAK);
    }

    public MangroveBoatEntity(Level level, double x, double y, double z) {
        this(
                ModEntities.MANGROVE_BOAT.get(),
                level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setType(Boat.Type.OAK);
    }

    @Override
    public Item getDropItem() {
        return WoodItems.MANGROVE_BOAT.get();
    }
}
