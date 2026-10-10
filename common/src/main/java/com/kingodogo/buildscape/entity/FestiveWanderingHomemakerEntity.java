package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
public interface FestiveWanderingHomemakerEntity {
    static Entity create(Level level) {
        return Services.PLATFORM.createWanderingHomemakerEntity(level, true);
    }

    default Entity asEntity() {
        return (Entity) this;
    }
}
