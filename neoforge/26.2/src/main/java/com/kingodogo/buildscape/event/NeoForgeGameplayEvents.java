package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.command.BuildscapeCommands;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class NeoForgeGameplayEvents {
    private NeoForgeGameplayEvents() {}

    public static void register() {
        var bus = NeoForge.EVENT_BUS;
        bus.addListener((RegisterCommandsEvent event) -> BuildscapeCommands.register(event.getDispatcher()));
        bus.addListener(NeoForgeGameplayEvents::rightClickBlock);
        bus.addListener(NeoForgeGameplayEvents::rightClickItem);
        bus.addListener(NeoForgeGameplayEvents::entityInteract);
        bus.addListener((PlayerInteractEvent.LeftClickBlock event) ->
                ModCommonEvents.onLeftClickBlock(event.getEntity(), event.getLevel(), event.getPos()));
        bus.addListener((BlockEvent.EntityPlaceEvent event) -> {
            if (event.getLevel() instanceof Level level) {
                ModCommonEvents.onBlockPlaced(level, event.getPos(), event.getPlacedBlock(),
                        event.getEntity() instanceof Player player ? player : null);
            }
        });
        bus.addListener((LivingDeathEvent event) -> ModCommonEvents.onLivingDeath(event.getEntity(), event.getSource()));
        bus.addListener((PlayerEvent.ItemCraftedEvent event) ->
                ModCommonEvents.onItemCrafted(event.getEntity(), event.getCrafting(), event.getInventory()));
        bus.addListener((LivingEntityUseItemEvent.Finish event) -> ModCommonEvents.onItemUseFinish(event.getEntity(), event.getItem()));
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerJoin(player);
        });
        bus.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerLeave(player);
        });
        bus.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerJoin(player);
        });
        bus.addListener((PlayerWakeUpEvent event) -> StrawBedHandler.onPlayerWakeUp(event.getEntity()));
        bus.addListener((PlayerSetSpawnEvent event) -> {
            if (StrawBedHandler.shouldCancelSpawn(event.getEntity(), event.getNewSpawn())) event.setCanceled(true);
        });
        bus.addListener((EntityMobGriefingEvent event) -> {
            if (ModCommonEvents.isMobGriefingDisabled(event.getEntity(), Services.PLATFORM.getEntityLevel(event.getEntity()))) {
                event.setCanGrief(false);
            }
        });
        bus.addListener(NeoForgeGameplayEvents::anvilUpdate);
        bus.addListener((EntityJoinLevelEvent event) -> ItemFrameParticleHandler.onEntityJoin(event.getEntity(), event.getLevel()));
        bus.addListener((EntityLeaveLevelEvent event) -> ItemFrameParticleHandler.onEntityLeave(event.getEntity(), event.getLevel()));
        bus.addListener((LevelEvent.Unload event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onWorldUnload(level);
            }
        });
        bus.addListener((ServerStartedEvent event) -> ModCommonEvents.onServerStarted(event.getServer()));
        bus.addListener((ServerTickEvent.Post event) -> ModCommonEvents.onServerTick(event.getServer()));
    }

    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = ModCommonEvents.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getPos());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = ModCommonEvents.onRightClickItem(event.getEntity(), event.getLevel(), event.getHand());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void entityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = ModCommonEvents.onEntityInteract(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void anvilUpdate(AnvilUpdateEvent event) {
        var result = FestiveGlintAnvilHandler.processAnvil(event.getLeft(), event.getRight(), event.getName());
        if (result != null) {
            event.setOutput(result.output);
            event.setXpCost(result.cost);
            event.setMaterialCost(result.materialCost);
        }
    }
}
