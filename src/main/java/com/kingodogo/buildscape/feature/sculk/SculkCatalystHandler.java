package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.mixin.accessor.LivingEntityExperienceAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;

public final class SculkCatalystHandler {
    private static final boolean DEBUG = Boolean.getBoolean("buildscape.debug.sculk");
    private static final SculkDeathRules.ConsumptionGuard<LivingEntity> CONSUMPTION = new SculkDeathRules.ConsumptionGuard<>();
    private static final ThreadLocal<DeathCharge> ACTIVE_DEATH = new ThreadLocal<>();

    private SculkCatalystHandler() {}

    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity.level instanceof ServerLevel level) || event.isCanceled() || entity instanceof Player) {
            return;
        }
        debug("death seen entity={} at {} in {}", entity.getUUID(), entity.blockPosition(),
                level.dimension().location());
        if (CONSUMPTION.isConsumed(entity)) {
            return;
        }

        LivingEntityExperienceAccessor experience = (LivingEntityExperienceAccessor) entity;
        Player player = experience.getLastHurtByPlayer();
        if (player == null && event.getSource().getEntity() instanceof Player killer) {
            player = killer;
        }
        if (player == null || !experience.callShouldDropExperience()
                || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            debug("death ineligible entity={} playerCredit={}", entity.getUUID(), player != null);
            return;
        }
        int reward = experience.callGetExperienceReward(player);
        debug("XP read entity={} reward={}", entity.getUUID(), reward);
        if (reward <= 0) {
            return;
        }

        DeathCharge previous = ACTIVE_DEATH.get();
        DeathCharge death = new DeathCharge(entity);
        ACTIVE_DEATH.set(death);
        try {
            entity.gameEvent(GameEvent.ENTITY_KILLED);
            BlockPos deathPos = entity.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(deathPos.offset(-8, -8, -8), deathPos.offset(8, 8, 8))) {
                if (!SculkDeathRules.isInRange(pos.distSqr(deathPos)) || !level.hasChunkAt(pos)) {
                    continue;
                }
                BlockState state = level.getBlockState(pos);
                if (!(state.getBlock() instanceof SculkCatalystBlock)) {
                    continue;
                }
                SculkCatalystBlockEntity catalyst = SculkCatalystBlock.ensureBlockEntity(level, pos.immutable(), state);
                if (catalyst != null) {
                    offerDeath(catalyst, entity, deathPos);
                }
            }
            if (death.catalyst == null) {
                debug("no catalyst in range entity={}", entity.getUUID());
            } else if (!death.catalyst.isRemoved() && CONSUMPTION.tryConsume(entity)) {
                debug("catalyst found entity={} catalyst={}", entity.getUUID(), death.catalyst.getBlockPos());
                death.catalyst.absorbExperience(deathPos, reward);
                debug("charge entity={} catalyst={} XP={}", entity.getUUID(), death.catalyst.getBlockPos(), reward);
            }
        } finally {
            if (previous == null) {
                ACTIVE_DEATH.remove();
            } else {
                ACTIVE_DEATH.set(previous);
            }
        }
    }

    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.level.isClientSide || event.isCanceled() || !CONSUMPTION.isConsumed(entity)) {
            return;
        }
        int previous = event.getDroppedExperience();
        event.setDroppedExperience(CONSUMPTION.experienceToDrop(entity, previous));
        debug("drop zeroed entity={} previousXP={}", entity.getUUID(), previous);
    }

    public static boolean offerDeath(SculkCatalystBlockEntity catalyst, Entity entity, BlockPos deathPos) {
        DeathCharge death = ACTIVE_DEATH.get();
        if (death == null || death.entity != entity || catalyst.isRemoved() || catalyst.getLevel() != entity.level) {
            return false;
        }
        double distance = catalyst.getBlockPos().distSqr(deathPos);
        if (!SculkDeathRules.isInRange(distance)) {
            return false;
        }
        if (SculkDeathRules.preferCatalyst(distance, death.distance,
                death.catalyst == null ? -1 : catalyst.getBlockPos().compareTo(death.catalyst.getBlockPos()))) {
            death.catalyst = catalyst;
            death.distance = distance;
        }
        debug("catalyst offered entity={} catalyst={} distanceSquared={}", entity.getUUID(), catalyst.getBlockPos(),
                distance);
        return true;
    }

    static void debug(String message, Object... arguments) {
        if (DEBUG) {
            BuildScape.LOGGER.debug("[Sculk debug] " + message, arguments);
        }
    }

    private static final class DeathCharge {
        private final LivingEntity entity;
        private SculkCatalystBlockEntity catalyst;
        private double distance = Double.MAX_VALUE;

        private DeathCharge(LivingEntity entity) {
            this.entity = entity;
        }
    }
}
