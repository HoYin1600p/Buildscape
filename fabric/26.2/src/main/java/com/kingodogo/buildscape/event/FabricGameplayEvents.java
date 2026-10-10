package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.command.BuildscapeCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public final class FabricGameplayEvents {
    private FabricGameplayEvents() {}

    public static void register() {
        AdvancementEvents.configure(new LoaderAdvancementAccess());
        ServerLifecycleEvents.SERVER_STARTING.register(LoaderWandererTrades::register);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> {
            if (success) LoaderWandererTrades.register(server);
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) ->
                BuildscapeCommands.register(dispatcher));
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> player.isSpectator()
                ? InteractionResult.PASS : ModCommonEvents.onRightClickBlock(player, level, hand, hit.getBlockPos(), hit.getDirection()));
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                int experience = SculkExperienceLogic.experience(player, state, player.getMainHandItem());
                if (experience > 0) net.minecraft.world.entity.ExperienceOrb.award(serverLevel,
                        net.minecraft.world.phys.Vec3.atCenterOf(pos), experience);
            }
        });
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (player.isSpectator()) return InteractionResult.PASS;
            var held = player.getItemInHand(hand);
            boolean confetti = held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem;
            if (confetti && player.getCooldowns().isOnCooldown(held)) return InteractionResult.FAIL;
            var cooldownStack = confetti ? held.copy() : null;
            var result = ModCommonEvents.onRightClickItem(player, level, hand);
            if (confetti && result == InteractionResult.SUCCESS && !level.isClientSide()) {
                player.getCooldowns().addCooldown(cooldownStack, 10);
            }
            return result;
        });
        UseEntityCallback.EVENT.register((player, level, hand, target, hit) -> player.isSpectator()
                ? InteractionResult.PASS : ModCommonEvents.onEntityInteract(player, level, hand, target));
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!player.isSpectator()) ModCommonEvents.onLeftClickBlock(player, level, pos);
            return InteractionResult.PASS;
        });
        ServerLivingEntityEvents.AFTER_DEATH.register(ModCommonEvents::onLivingDeath);
        ServerPlayerEvents.JOIN.register(ModCommonEvents::onPlayerJoin);
        ServerPlayerEvents.LEAVE.register(ModCommonEvents::onPlayerLeave);
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, pos) -> !StrawBedHandler.shouldCancelSpawn(player, pos));
        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> {
            if (entity instanceof Player player) StrawBedHandler.onPlayerWakeUp(player, pos);
        });
        ServerEntityEvents.ENTITY_LOAD.register(ItemFrameParticleHandler::onEntityJoin);
        ServerEntityEvents.ENTITY_UNLOAD.register(ItemFrameParticleHandler::onEntityLeave);
        ServerLevelEvents.UNLOAD.register((server, level) ->
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onWorldUnload(level));
        ServerLifecycleEvents.SERVER_STARTED.register(ModCommonEvents::onServerStarted);
        ServerTickEvents.END_SERVER_TICK.register(ModCommonEvents::onServerTick);
    }
}
