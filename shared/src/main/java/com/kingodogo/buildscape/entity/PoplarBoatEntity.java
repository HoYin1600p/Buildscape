package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
public interface PoplarBoatEntity {
    static Entity create(Level level, double x, double y, double z) {
        return Services.PLATFORM.createBoatEntity(level, x, y, z);
    }

    default Entity asEntity() {
        return (Entity) this;
    }
}
