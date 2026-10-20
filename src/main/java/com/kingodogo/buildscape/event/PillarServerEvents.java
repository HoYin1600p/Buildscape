package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.homemaker.SyncHomemakerCooldownPacket;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.SyncGameRulesPacket;
import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PillarServerEvents {

    private static final int PILLAR_SAVE_INTERVAL = 600;
    private static final int PILLAR_BACKUP_INTERVAL = 6000;
    private static final int RECOVERY_DELAY_TICKS = 600;
    private static boolean serverFullyInitialized = false;
    private static int pillarSaveTickCounter = 0;
    private static int pillarBackupTickCounter = 0;
    private static int recoveryDelayTicks = 0;
    private static boolean recoveryAttempted = false;

    public static boolean isServerFullyInitialized() {
        return serverFullyInitialized;
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        serverFullyInitialized = false;
        pillarSaveTickCounter = 0;
        pillarBackupTickCounter = 0;
        recoveryDelayTicks = 0;
        recoveryAttempted = false;

        PillarIdManager.resetWorldCache();
    }

    @SubscribeEvent
    public void onServerStarted(
            ServerStartedEvent event) {
        PillarIdManager.get().load();
    }

    @SubscribeEvent
    public void onServerStopped(
            ServerStoppedEvent event) {

        try {
            MinecraftServer server = event.getServer();
            if (server != null) {
                PillarIdManager manager = PillarIdManager
                        .get();
                if (manager != null && manager.hasLoaded()) {
                    manager.savePeriodic(true);
                }
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error saving pillar data on server stop: {}", e.getMessage(), e);
        }

        serverFullyInitialized = false;
        pillarSaveTickCounter = 0;
        pillarBackupTickCounter = 0;
        recoveryDelayTicks = 0;
        recoveryAttempted = false;

        PillarIdManager.fullReset();
    }

    @SubscribeEvent
    public void onPlayerJoin(
            PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            PillarIdManager manager = PillarIdManager
                    .get();
            if (!manager.hasLoaded() && !manager.isLoadInProgress()) {
                manager.load();
            }

            PillarParticleConfig serverConfig = PillarParticleConfig
                    .getServerConfig();
            SyncConfigPacket configPacket = new SyncConfigPacket(
                    serverConfig);
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    configPacket);

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null && server.isRunning()) {
                ModMessages.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new SyncGameRulesPacket(
                                serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY),
                                serverPlayer.getLevel().getGameRules()
                                        .getBoolean(ModGameRules.DISABLE_ENDERMAN_GRIEFING),
                                serverPlayer.getLevel().getGameRules()
                                        .getBoolean(ModGameRules.DISABLE_CREEPER_GRIEFING),
                                serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.DISABLE_GHAST_GRIEFING),
                                serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.IS_CAKE_STACK),
                                serverPlayer.getLevel().getGameRules().getBoolean(ModGameRules.IS_WATER_BOTTLE_STACK)));

                schedulePillarIdSync(server, serverPlayer, manager, 0);

                long cooldown = serverPlayer.getPersistentData()
                        .contains(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME)
                                ? serverPlayer.getPersistentData()
                                        .getLong(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME)
                                : 0L;
                ModMessages.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new SyncHomemakerCooldownPacket(cooldown));
            }

        }
    }

    private void schedulePillarIdSync(MinecraftServer server,
            ServerPlayer player,
            PillarIdManager manager,
            int attempt) {
        final int MAX_ATTEMPTS = 10;
        final int RETRY_DELAY_MS = 500;

        if (attempt >= MAX_ATTEMPTS) {
            server.execute(() -> {
                if (player.hasDisconnected()) {
                    return;
                }
                sendPillarIdsToPlayer(player, manager);
            });
            return;
        }

        if (manager.hasLoaded()) {
            server.execute(() -> {
                if (player.hasDisconnected()) {
                    return;
                }
                sendPillarIdsToPlayer(player, manager);
            });
        } else {
            BuildScape.getAsyncPool().submit(() -> {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ignored) {
                    // interrupted while waiting; retry the sync now
                }
                schedulePillarIdSync(server, player, manager, attempt + 1);
            });
        }
    }

    private void sendPillarIdsToPlayer(ServerPlayer player,
            PillarIdManager manager) {
        try {
            SyncPillarIdsPacket.sendToPlayer(
                    player, manager.getAllPillarDataForSync());
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error sending pillar IDs to player: {}", e.getMessage(), e);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(
            PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            long cooldown = serverPlayer.getPersistentData().contains(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME)
                    ? serverPlayer.getPersistentData().getLong(NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME)
                    : 0L;
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncHomemakerCooldownPacket(cooldown));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(
            WorldEvent.Unload event) {
        if (event.getWorld() instanceof ServerLevel) {

            try {
                MinecraftServer server = ServerLifecycleHooks
                        .getCurrentServer();
                if (server != null) {
                    PillarIdManager manager = PillarIdManager
                            .get();
                    if (manager != null && manager.hasLoaded()) {
                        manager.savePeriodic(true);
                    }
                }
            } catch (Exception e) {
                BuildScape.LOGGER.error("Error syncing/saving colors before world unload", e);
            }

        }
    }

    @SubscribeEvent
    public void onServerTick(
            TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = ServerLifecycleHooks
                .getCurrentServer();
        if (server == null || !server.isRunning()) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get();
        if (!serverFullyInitialized) {
            if (manager.hasLoaded()) {
                serverFullyInitialized = true;
            } else {
                if (!manager.isLoadInProgress()) {
                    manager.load();
                }
                return;
            }
        }

        pillarSaveTickCounter++;
        pillarBackupTickCounter++;
        if (pillarBackupTickCounter >= PILLAR_BACKUP_INTERVAL) {
            pillarBackupTickCounter = 0;
            pillarSaveTickCounter = 0;
            manager.savePeriodic(true);
        } else if (pillarSaveTickCounter >= PILLAR_SAVE_INTERVAL) {
            pillarSaveTickCounter = 0;
            manager.savePeriodic(false);
        }
        if (pillarSaveTickCounter % 20 == 0) {
            manager.flushRequestedSave();
        }

        if (server.getPlayerList().getPlayerCount() == 0) {
            return;
        }

        PillarIdManager.checkAndRunScheduledRecovery();

        recoveryDelayTicks++;
        if (recoveryDelayTicks >= RECOVERY_DELAY_TICKS
                && !recoveryAttempted
                && serverFullyInitialized) {
            recoveryAttempted = true;
            if (manager.needsWorldRecovery()) {
                manager.scheduleRecoveryFromWorld(server, false);
            }
        }

    }
}
