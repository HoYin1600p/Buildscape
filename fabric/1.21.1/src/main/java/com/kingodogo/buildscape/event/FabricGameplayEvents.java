package com.kingodogo.buildscape.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;

public final class FabricGameplayEvents {
    private FabricGameplayEvents() {}
    public static void register() {
        net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AFTER_DEATH.register(ModCommonEvents::onLivingDeath);
        AdvancementEvents.configure(new LoaderAdvancementAccess());
        LoaderWandererTrades.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) ->
                com.kingodogo.buildscape.command.BuildscapeCommands.register(dispatcher));
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> player.isSpectator() ? InteractionResult.PASS
                : ModCommonEvents.onRightClickBlock(player, level, hand, hit.getBlockPos(), hit.getDirection()));
        UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> player.isSpectator() ? InteractionResult.PASS
                : ModCommonEvents.onEntityInteract(player, level, hand, entity));
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> player.isSpectator() ? InteractionResult.PASS
                : ModCommonEvents.onLeftClickBlock(player, level, pos));
        UseItemCallback.EVENT.register((player, level, hand) -> {
            var held = player.getItemInHand(hand);
            if (player.isSpectator()) return InteractionResultHolder.pass(held);
            boolean confetti = held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem;
            var item = held.getItem();
            if (confetti && player.getCooldowns().isOnCooldown(item)) return InteractionResultHolder.fail(held);
            var result = ModCommonEvents.onRightClickItem(player, level, hand);
            if (confetti && result == InteractionResult.SUCCESS && !level.isClientSide()) player.getCooldowns().addCooldown(item, 10);
            return new InteractionResultHolder<>(result, player.getItemInHand(hand));
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ModCommonEvents.onPlayerJoin(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ModCommonEvents.onPlayerLeave(handler.player));
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, pos) -> !StrawBedHandler.shouldCancelSpawn(player, pos));
        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> {
            if (entity instanceof Player player) StrawBedHandler.onPlayerWakeUp(player, pos);
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity item && FrostRoseDropHandler.shouldCancelItemSpawn(item)) {
                item.discard();
                return;
            }
            ItemFrameParticleHandler.onEntityJoin(entity, level);
        });
        ServerEntityEvents.ENTITY_UNLOAD.register(ItemFrameParticleHandler::onEntityLeave);
        ServerWorldEvents.UNLOAD.register((server, level) -> ModCommonEvents.onLevelUnload(level));
    }
}