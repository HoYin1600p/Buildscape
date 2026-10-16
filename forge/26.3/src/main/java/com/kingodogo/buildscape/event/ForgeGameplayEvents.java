package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.command.BuildscapeCommands;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;

public final class ForgeGameplayEvents {
    private ForgeGameplayEvents() {}

    public static void register() {
        AdvancementEvents.configure(new LoaderAdvancementAccess());


        net.minecraftforge.event.server.ServerStartingEvent.BUS.addListener((net.minecraftforge.event.server.ServerStartingEvent event) -> {
            ((com.kingodogo.buildscape.adapter.v263.PlatformAdapterBase) Services.PLATFORM)
                    .registerDeferredServerData(event.getServer());
            LoaderWandererTrades.register(event.getServer());
        });
        net.minecraftforge.event.OnDatapackSyncEvent.BUS.addListener((net.minecraftforge.event.OnDatapackSyncEvent event) -> {
            // A null player means a data reload rather than a player joining.
            if (event.getPlayer() == null) {
                ((com.kingodogo.buildscape.adapter.v263.PlatformAdapterBase) Services.PLATFORM)
                        .registerDeferredServerData(event.getPlayerList().getServer());
            }
            LoaderWandererTrades.register(event.getPlayerList().getServer());
        });
        net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent.BUS.addListener((net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                var id = event.getAdvancement().id();
                AdvancementEvents.onAdvancementEarned(player,
                        new com.kingodogo.buildscape.util.CommonId(id.getNamespace(), id.getPath()));
            }
        });
        RegisterCommandsEvent.BUS.addListener((RegisterCommandsEvent event) -> BuildscapeCommands.register(event.getDispatcher()));
        PlayerInteractEvent.RightClickBlock.BUS.addListener(ForgeGameplayEvents::rightClickBlock);
        PlayerInteractEvent.RightClickItem.BUS.addListener(ForgeGameplayEvents::rightClickItem);
        PlayerInteractEvent.EntityInteractSpecific.BUS.addListener(ForgeGameplayEvents::entityInteract);
        PlayerInteractEvent.LeftClickBlock.BUS.addListener((PlayerInteractEvent.LeftClickBlock event) -> {
            InteractionResult result = ModCommonEvents.onLeftClickBlock(event.getEntity(), event.getLevel(), event.getPos());
            return result != InteractionResult.PASS;
        });
        BlockEvent.EntityPlaceEvent.BUS.addListener((BlockEvent.EntityPlaceEvent event) -> {
            if (event.getLevel() instanceof Level level) {
                ModCommonEvents.onBlockPlaced(level, event.getPos(), event.getPlacedBlock(),
                        event.getEntity() instanceof Player player ? player : null);
            }
        });
        LivingDeathEvent.BUS.addListener((LivingDeathEvent event) -> ModCommonEvents.onLivingDeath(event.getEntity(), event.getSource()));
        PlayerEvent.ItemCraftedEvent.BUS.addListener((PlayerEvent.ItemCraftedEvent event) ->
                ModCommonEvents.onItemCrafted(event.getEntity(), event.getCrafting(), event.getContainer()));
        LivingEntityUseItemEvent.Finish.BUS.addListener((LivingEntityUseItemEvent.Finish event) -> ModCommonEvents.onItemUseFinish(event.getEntity(), event.getItem()));
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerJoin(player);
        });
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerLeave(player);
        });
        PlayerEvent.PlayerChangedDimensionEvent.BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerChangedDimension(player);
        });
        PlayerWakeUpEvent.BUS.addListener((PlayerWakeUpEvent event) -> StrawBedHandler.onPlayerWakeUp(event.getEntity()));
        PlayerSetSpawnEvent.BUS.addListener((PlayerSetSpawnEvent event) -> {
            return StrawBedHandler.shouldCancelSpawn(event.getEntity(),
                    event.getConfig() == null ? null : event.getConfig().respawnData().pos());
        });
        EntityMobGriefingEvent.BUS.addListener((EntityMobGriefingEvent event) -> {
            if (ModCommonEvents.isMobGriefingDisabled(event.getEntity(), Services.PLATFORM.getEntityLevel(event.getEntity()))) {
                event.setResult(net.minecraftforge.common.util.Result.DENY);
            }
        });
        AnvilUpdateEvent.BUS.addListener(ForgeGameplayEvents::anvilUpdate);
        EntityJoinLevelEvent.BUS.addListener((EntityJoinLevelEvent event) -> {
            if (event.getEntity() instanceof net.minecraft.world.entity.item.ItemEntity item
                    && FrostRoseDropHandler.shouldCancelItemSpawn(item)) {
                return true;
            }
            ItemFrameParticleHandler.onEntityJoin(event.getEntity(), event.getLevel());
            return false;
        });
        EntityLeaveLevelEvent.BUS.addListener((EntityLeaveLevelEvent event) -> ItemFrameParticleHandler.onEntityLeave(event.getEntity(), event.getLevel()));
        LevelEvent.Unload.BUS.addListener((LevelEvent.Unload event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                ModCommonEvents.onLevelUnload(level);
            }
        });
        ServerStartedEvent.BUS.addListener((ServerStartedEvent event) -> ModCommonEvents.onServerStarted(event.getServer()));
        net.minecraftforge.event.server.ServerStoppingEvent.BUS.addListener((net.minecraftforge.event.server.ServerStoppingEvent event) -> ModCommonEvents.onServerStopping());
        ServerTickEvent.Post.BUS.addListener((ServerTickEvent.Post event) -> {
            ModCommonEvents.onServerTick(event.server());
            com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(event.server().overworld());
        });
    }

    private static boolean rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = ModCommonEvents.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            return true;
        }
        return false;
    }

    private static boolean rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().isSpectator()) return false;
        var held = event.getEntity().getItemInHand(event.getHand());
        boolean confetti = held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem;
        if (confetti && event.getEntity().getCooldowns().isOnCooldown(held)) {
            event.setCancellationResult(InteractionResult.FAIL);
            return true;
        }
        var cooldownStack = confetti ? held.copy() : null;
        InteractionResult result = ModCommonEvents.onRightClickItem(event.getEntity(), event.getLevel(), event.getHand());
        if (confetti && result == InteractionResult.SUCCESS && !event.getLevel().isClientSide()) {
            event.getEntity().getCooldowns().addCooldown(cooldownStack, 10);
        }
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            return true;
        }
        return false;
    }

    private static boolean entityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        InteractionResult result = ModCommonEvents.onEntityInteract(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            return true;
        }
        return false;
    }

    private static void anvilUpdate(AnvilUpdateEvent event) {
        var result = FestiveGlintAnvilHandler.processAnvil(event.getLeft(), event.getRight(), event.getName());
        if (result != null) {
            event.setOutput(result.output);
            event.setCost(result.cost);
            event.setMaterialCost(result.materialCost);
        }
    }
}

