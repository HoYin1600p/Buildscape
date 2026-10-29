package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.mixin.LivingEntityExperienceV118xAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Vanilla 1.19 catalyst charging for 1.18.2. The death's experience is read when the mob dies, so
 * catalysts still charge in packs whose mods handle experience drops themselves; the drop is then zeroed.
 */
public final class SculkCatalystHandler {
    private static final boolean DEBUG = Boolean.getBoolean("buildscape.debug.sculk");
    private static final SculkDeathRules.ConsumptionGuard<LivingEntity> CONSUMPTION = new SculkDeathRules.ConsumptionGuard<>();
    private static final ThreadLocal<DeathCharge> ACTIVE_DEATH = new ThreadLocal<>();

    private SculkCatalystHandler() {}

    /** Loaders call this from their death hook once the death can no longer be cancelled. */
    public static void onDeath(LivingEntity entity, DamageSource source) {
        if (!(entity.level instanceof ServerLevel level) || entity instanceof Player) {
            return;
        }
        debug("death seen entity={} at {} in {}", entity.getUUID(), entity.blockPosition(),
                level.dimension().location());
        if (CONSUMPTION.isConsumed(entity)) {
            return;
        }

        LivingEntityExperienceV118xAccessor experience = (LivingEntityExperienceV118xAccessor) entity;
        Player player = experience.buildscape$getLastHurtByPlayer();
        if (player == null && source != null && source.getEntity() instanceof Player killer) {
            player = killer;
        }
        if (player == null || !experience.buildscape$callShouldDropExperience()
                || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            debug("death ineligible entity={} playerCredit={}", entity.getUUID(), player != null);
            return;
        }
        int reward = experience.buildscape$callGetExperienceReward(player);
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
                death.catalyst.absorbExperience(new BlockPos(entity.getX(), entity.getY() + 0.5, entity.getZ()),
                        reward);
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

    /** Experience the entity may still drop: none once a catalyst has taken it. */
    public static int experienceToDrop(LivingEntity entity, int experience) {
        if (entity.level.isClientSide) {
            return experience;
        }
        return CONSUMPTION.experienceToDrop(entity, experience);
    }

    public static boolean isConsumed(LivingEntity entity) {
        return !entity.level.isClientSide && CONSUMPTION.isConsumed(entity);
    }

    static boolean offerDeath(SculkCatalystBlockEntity catalyst, Entity entity, BlockPos deathPos) {
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
        return true;
    }

    static void debug(String message, Object... arguments) {
        if (DEBUG) {
            BuildscapeCommon.LOGGER.debug("[Sculk debug] " + message, arguments);
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
