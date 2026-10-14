package com.kingodogo.buildscape.event;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
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
        AdvancementEvents.configure(new LoaderAdvancementAccess());
        LoaderWandererTrades.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                com.kingodogo.buildscape.command.BuildscapeCommands.register(dispatcher));
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isSpectator()) return InteractionResult.PASS;
            // Forge asks the item whether sneaking still uses the block (the wrench on pipes); vanilla and Fabric skip the block when sneaking with an item.
            var held = player.getItemInHand(hand);
            if (player.isSecondaryUseActive() && held.getItem() instanceof com.kingodogo.buildscape.item.WrenchItem wrench
                    && wrench.doesSneakBypassUse(held, level, hit.getBlockPos(), player)) {
                var used = level.getBlockState(hit.getBlockPos()).use(level, player, hand, hit);
                if (used.consumesAction()) return used;
            }
            return ModCommonEvents.onRightClickBlock(player, level, hand, hit.getBlockPos(), hit.getDirection());
        });
        UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (player.isSpectator()) return InteractionResult.PASS;
            var golden = GoldenDandelionHandler.onEntityInteract(player, level, hand, entity);
            return golden != InteractionResult.PASS ? golden : ModCommonEvents.onEntityInteract(player, level, hand, entity);
        });
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> player.isSpectator() ? InteractionResult.PASS
                : ModCommonEvents.onLeftClickBlock(player, level, pos));
        UseItemCallback.EVENT.register((player, level, hand) -> {
            var held = player.getItemInHand(hand);
            if (player.isSpectator()) return InteractionResultHolder.pass(held);
            boolean confetti = held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem;
            var item = held.getItem();
            if (confetti && player.getCooldowns().isOnCooldown(item)) return InteractionResultHolder.fail(held);
            var result = ModCommonEvents.onRightClickItem(player, level, hand);
            if (confetti && result == InteractionResult.SUCCESS && !level.isClientSide) player.getCooldowns().addCooldown(item, 10);
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
        ServerWorldEvents.UNLOAD.register((server, level) -> {
            ModCommonEvents.onLevelUnload(level);
            com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onWorldUnload(level);
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler::onWorldTick);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD.register(
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler::onChunkLoad);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onChunkUnload(level, chunk.getPos()));
    }
    public static void livingDeath(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
        ModCommonEvents.onLivingDeath(entity, source);
        if (entity.level.isClientSide) return;
        var origin = entity.blockPosition();
        var catalyst = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlock(
                new com.kingodogo.buildscape.util.CommonId("buildscape", "sculk_catalyst"));
        for (var pos : net.minecraft.core.BlockPos.betweenClosed(origin.offset(-8, -8, -8), origin.offset(8, 8, 8))) {
            if (entity.level.getBlockState(pos).is(catalyst)) {
                com.kingodogo.buildscape.block.SculkCatalystHandler.onMobKilledNearCatalyst(entity.level, pos, origin, entity);
                break;
            }
        }
    }
}
