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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.TickEvent;

public final class ForgeGameplayEvents {
    private ForgeGameplayEvents() {}

    public static void register() {
        AdvancementEvents.configure(new LoaderAdvancementAccess());
        var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(LoaderWandererTrades::register);
        bus.addListener((net.minecraftforge.event.entity.player.AdvancementEvent event) -> {
            if (event.getPlayer() instanceof ServerPlayer player) {
                var id = event.getAdvancement().getId();
                AdvancementEvents.onAdvancementEarned(player,
                        new com.kingodogo.buildscape.util.CommonId(id.getNamespace(), id.getPath()));
            }
        });
        bus.addListener((RegisterCommandsEvent event) -> BuildscapeCommands.register(event.getDispatcher()));
        bus.addListener(ForgeGameplayEvents::rightClickBlock);
        bus.addListener(ForgeGameplayEvents::rightClickItem);
        bus.addListener(ForgeGameplayEvents::entityInteract);
        bus.addListener((PlayerInteractEvent.LeftClickBlock event) -> {
            InteractionResult result = ModCommonEvents.onLeftClickBlock(event.getPlayer(), event.getWorld(), event.getPos());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        });
        bus.addListener((BlockEvent.EntityPlaceEvent event) -> {
            if (event.getWorld() instanceof Level level) {
                ModCommonEvents.onBlockPlaced(level, event.getPos(), event.getPlacedBlock(),
                        event.getEntity() instanceof Player player ? player : null);
            }
        });
        bus.addListener((LivingDeathEvent event) -> livingDeath(event));
        bus.addListener(LoaderStewRecipes::crafted);
        bus.addListener((LivingEntityUseItemEvent.Finish event) -> ModCommonEvents.onItemUseFinish(event.getEntityLiving(), event.getItem()));
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getPlayer() instanceof ServerPlayer player) ModCommonEvents.onPlayerJoin(player);
        });
        bus.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getPlayer() instanceof ServerPlayer player) ModCommonEvents.onPlayerLeave(player);
        });
        bus.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> {
            if (event.getPlayer() instanceof ServerPlayer player) ModCommonEvents.onPlayerChangedDimension(player);
        });
        bus.addListener((PlayerWakeUpEvent event) -> StrawBedHandler.onPlayerWakeUp(event.getPlayer()));
        bus.addListener((PlayerSetSpawnEvent event) -> {
            if (StrawBedHandler.shouldCancelSpawn(event.getPlayer(), event.getNewSpawn())) event.setCanceled(true);
        });
        bus.addListener((EntityMobGriefingEvent event) -> {
            if (ModCommonEvents.isMobGriefingDisabled(event.getEntity(), Services.PLATFORM.getEntityLevel(event.getEntity()))) {
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            }
        });
        bus.addListener(ForgeGameplayEvents::anvilUpdate);
        bus.addListener((EntityJoinWorldEvent event) -> {
            if (event.getEntity() instanceof net.minecraft.world.entity.item.ItemEntity item
                    && FrostRoseDropHandler.shouldCancelItemSpawn(item)) {
                event.setCanceled(true);
                return;
            }
            ItemFrameParticleHandler.onEntityJoin(event.getEntity(), event.getWorld());
        });
        bus.addListener((EntityLeaveWorldEvent event) -> ItemFrameParticleHandler.onEntityLeave(event.getEntity(), event.getWorld()));
        bus.addListener((WorldEvent.Unload event) -> {
            if (event.getWorld() instanceof ServerLevel level) {
                ModCommonEvents.onLevelUnload(level);
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
                    && event.getWorld().isEmptyBlock(event.getPos().above())) {
                if (!event.getWorld().isClientSide()) {
                    event.getWorld().setBlock(event.getPos().above(),
                            Services.PLATFORM.getBlock(new com.kingodogo.buildscape.util.CommonId(
                                    "buildscape", "white_haze_bush")).defaultBlockState(), 3);
                }
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
            }
        });
    }

    private static void livingDeath(LivingDeathEvent event) {
        ModCommonEvents.onLivingDeath(event.getEntityLiving(), event.getSource());
        var entity = event.getEntityLiving();
        if (entity.level.isClientSide) return;
        var origin = entity.blockPosition();
        var catalyst = Services.PLATFORM.getBlock(new com.kingodogo.buildscape.util.CommonId("buildscape", "sculk_catalyst"));
        for (var pos : net.minecraft.core.BlockPos.betweenClosed(origin.offset(-8, -8, -8), origin.offset(8, 8, 8))) {
            if (entity.level.getBlockState(pos).is(catalyst)) {
                com.kingodogo.buildscape.block.SculkCatalystHandler.onMobKilledNearCatalyst(entity.level, pos, origin, entity);
                break;
            }
        }
    }

    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = ModCommonEvents.onRightClickBlock(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos(), event.getFace());
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getPlayer().isSpectator()) return;
        var held = event.getPlayer().getItemInHand(event.getHand());
        boolean confetti = held.getItem() instanceof com.kingodogo.buildscape.item.ConfettiItem;
        if (confetti && event.getPlayer().getCooldowns().isOnCooldown(held.getItem())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }
        var cooldownStack = confetti ? held.copy() : null;
        InteractionResult result = ModCommonEvents.onRightClickItem(event.getPlayer(), event.getWorld(), event.getHand());
        if (confetti && result == InteractionResult.SUCCESS && !event.getWorld().isClientSide()) {
            event.getPlayer().getCooldowns().addCooldown(cooldownStack.getItem(), 10);
        }
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void entityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = ModCommonEvents.onEntityInteract(event.getPlayer(), event.getWorld(), event.getHand(), event.getTarget());
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
