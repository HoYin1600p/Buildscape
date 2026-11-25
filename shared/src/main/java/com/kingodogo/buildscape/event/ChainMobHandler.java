package com.kingodogo.buildscape.event;

import net.minecraft.world.entity.LivingEntity;

public final class ChainMobHandler {
    private ChainMobHandler() {}

    public static void onLivingUpdate(LivingEntity entity) {
        ChainMobLogic.onLivingUpdate(entity);
    }
}
