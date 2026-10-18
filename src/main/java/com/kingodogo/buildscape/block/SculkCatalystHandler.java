package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;

public final class SculkCatalystHandler {
    private static final ThreadLocal<DeathCharge> ACTIVE_DEATH = new ThreadLocal<>();

    private SculkCatalystHandler() {
    }

    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntityLiving().level.isClientSide || event.isCanceled()
                || event.getEntityLiving() instanceof Player || event.getDroppedExperience() <= 0) return;

        DeathCharge previous = ACTIVE_DEATH.get();
        DeathCharge death = new DeathCharge(event);
        ACTIVE_DEATH.set(death);
        try {
            event.getEntityLiving().gameEvent(GameEvent.ENTITY_KILLED);
            if (death.catalyst != null && !death.catalyst.isRemoved()) {
                death.catalyst.absorbExperience(event.getEntityLiving().blockPosition(), event.getDroppedExperience());
                event.setDroppedExperience(0);
            }
        } finally {
            if (previous == null) ACTIVE_DEATH.remove();
            else ACTIVE_DEATH.set(previous);
        }
    }

    public static boolean offerDeath(SculkCatalystBlockEntity catalyst, Entity entity, BlockPos deathPos) {
        DeathCharge death = ACTIVE_DEATH.get();
        if (death == null || death.event.getEntityLiving() != entity) return false;
        double distance = catalyst.getBlockPos().distSqr(deathPos);
        if (distance > 64.0D) return false;
        if (death.catalyst == null || distance < death.distance
                || distance == death.distance && catalyst.getBlockPos().compareTo(death.catalyst.getBlockPos()) < 0) {
            death.catalyst = catalyst;
            death.distance = distance;
        }
        return true;
    }

    private static final class DeathCharge {
        private final LivingExperienceDropEvent event;
        private SculkCatalystBlockEntity catalyst;
        private double distance = Double.MAX_VALUE;

        private DeathCharge(LivingExperienceDropEvent event) {
            this.event = event;
        }
    }
}
