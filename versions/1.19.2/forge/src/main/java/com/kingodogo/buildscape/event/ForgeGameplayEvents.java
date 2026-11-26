package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.command.BuildscapeCommands;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.TickEvent;

public final class ForgeGameplayEvents {
    private ForgeGameplayEvents() {}

    public static void register() {
        AdvancementEvents.configure(new LoaderAdvancementAccess());
        var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(LoaderWandererTrades::register);
        bus.addListener((net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                var id = event.getAdvancement().getId();
                AdvancementEvents.onAdvancementEarned(player,
                        new com.kingodogo.buildscape.util.CommonId(id.getNamespace(), id.getPath()));
            }
        });
        bus.addListener((RegisterCommandsEvent event) -> BuildscapeCommands.register(event.getDispatcher()));
        bus.addListener(ForgeGameplayEvents::rightClickBlock);
        bus.addListener(ForgeGameplayEvents::rightClickItem);
        bus.addListener(ForgeGameplayEvents::entityInteract);
        bus.addListener(ForgeGameplayEvents::entityInteractSpecific);
        bus.addListener((PlayerInteractEvent.LeftClickBlock event) -> {
            InteractionResult result = ModCommonEvents.onLeftClickBlock(event.getEntity(), event.getLevel(), event.getPos());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        });
        bus.addListener((BlockEvent.EntityPlaceEvent event) -> {
            if (event.getLevel() instanceof Level level) {
                ModCommonEvents.onBlockPlaced(level, event.getPos(), event.getPlacedBlock(),
                        event.getEntity() instanceof Player player ? player : null);
            }
        });
        bus.addListener((LivingDeathEvent event) -> livingDeath(event));
        // Lowest priority: catalysts only charge from deaths no other mod cancelled.
        bus.addListener(EventPriority.LOWEST, (LivingDeathEvent event) -> {
            if (!event.isCanceled()) {
                com.kingodogo.buildscape.block.SculkCatalystHandler.onDeath(event.getEntity(), event.getSource());
            }
        });
        bus.addListener(EventPriority.LOWEST, (LivingExperienceDropEvent event) -> {
            if (!event.isCanceled()) {
                event.setDroppedExperience(com.kingodogo.buildscape.block.SculkCatalystHandler.experienceToDrop(
                        event.getEntity(), event.getDroppedExperience()));
            }
        });
        bus.addListener(EventPriority.HIGHEST, (PlayerEvent.BreakSpeed event) -> event.setNewSpeed(
                com.kingodogo.buildscape.block.LeafMiningSpeed.adjust(event.getEntity(), event.getState(), event.getNewSpeed())));
        bus.addListener(LoaderStewRecipes::crafted);
        bus.addListener((LivingEntityUseItemEvent.Finish event) -> ModCommonEvents.onItemUseFinish(event.getEntity(), event.getItem()));
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerJoin(player);
        });
        bus.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerLeave(player);
        });
        bus.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ModCommonEvents.onPlayerChangedDimension(player);
        });
        bus.addListener((PlayerWakeUpEvent event) -> StrawBedHandler.onPlayerWakeUp(event.getEntity()));
        bus.addListener((PlayerSetSpawnEvent event) -> {
            if (StrawBedHandler.shouldCancelSpawn(event.getEntity(), event.getNewSpawn())) event.setCanceled(true);
        });
        bus.addListener((EntityMobGriefingEvent event) -> {
            if (ModCommonEvents.isMobGriefingDisabled(event.getEntity(), Services.PLATFORM.getEntityLevel(event.getEntity()))) {
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            }
        });
        bus.addListener(ForgeGameplayEvents::anvilUpdate);
        bus.addListener((EntityJoinLevelEvent event) -> {
            if (event.getEntity() instanceof net.minecraft.world.entity.item.ItemEntity item
                    && FrostRoseDropHandler.shouldCancelItemSpawn(item)) {
                event.setCanceled(true);
                return;
            }
            ItemFrameParticleHandler.onEntityJoin(event.getEntity(), event.getLevel());
        });
        bus.addListener((EntityLeaveLevelEvent event) -> ItemFrameParticleHandler.onEntityLeave(event.getEntity(), event.getLevel()));
        bus.addListener((LevelEvent.Unload event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                ModCommonEvents.onLevelUnload(level);
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onWorldUnload(level);
            }
        });
        bus.addListener((TickEvent.LevelTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel level) {
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onWorldTick(level);
            }
        });
        bus.addListener((net.minecraftforge.event.level.ChunkEvent.Load event) -> {
            if (event.getLevel() instanceof ServerLevel level
                    && event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk chunk) {
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onChunkLoad(level, chunk);
            }
        });
        bus.addListener((net.minecraftforge.event.level.ChunkEvent.Unload event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                com.kingodogo.buildscape.block.EyeblossomTransitionHandler.onChunkUnload(level, event.getChunk().getPos());
            }
        });
        bus.addListener((ServerStartedEvent event) -> ModCommonEvents.onServerStarted(event.getServer()));
        bus.addListener((net.minecraftforge.event.server.ServerStoppingEvent event) -> ModCommonEvents.onServerStopping());
        bus.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase != TickEvent.Phase.END) return;
            var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                ModCommonEvents.onServerTick(server);
                com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(server.overworld());
            }
        });
        bus.addListener((BlockEvent.BreakEvent event) -> {
            int experience = SculkExperienceLogic.experience(event.getPlayer(), event.getState(),
                    event.getPlayer().getMainHandItem());
            if (experience >= 0) event.setExpToDrop(experience);
        });
        bus.addListener((net.minecraftforge.event.entity.player.BonemealEvent event) -> {
            if (event.getBlock().is(net.minecraft.world.level.block.Blocks.PODZOL)
                    && event.getLevel().isEmptyBlock(event.getPos().above())) {
                if (!event.getLevel().isClientSide()) {
                    event.getLevel().setBlock(event.getPos().above(),
                            Services.PLATFORM.getBlock(new com.kingodogo.buildscape.util.CommonId(
                                    "buildscape", "white_haze_bush")).defaultBlockState(), 3);
                }
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
            }
        });
    }

    private static void livingDeath(LivingDeathEvent event) {
        ModCommonEvents.onLivingDeath(event.getEntity(), event.getSource());
    }

    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = ModCommonEvents.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().isSpectator()) return;
        var held = event.getEntity().getItemInHand(event.getHand());
        boolean confetti = held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem;
        if (confetti && event.getEntity().getCooldowns().isOnCooldown(held.getItem())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }
        var cooldownStack = confetti ? held.copy() : null;
        InteractionResult result = ModCommonEvents.onRightClickItem(event.getEntity(), event.getLevel(), event.getHand());
        if (confetti && result == InteractionResult.SUCCESS && !event.getLevel().isClientSide()) {
            event.getEntity().getCooldowns().addCooldown(cooldownStack.getItem(), 10);
        }
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void entityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = GoldenDandelionHandler.onEntityInteract(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
        if (result == InteractionResult.PASS) result = ModCommonEvents.onEntityInteract(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void entityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
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
            event.setCost(result.cost);
            event.setMaterialCost(result.materialCost);
        }
    }
}
