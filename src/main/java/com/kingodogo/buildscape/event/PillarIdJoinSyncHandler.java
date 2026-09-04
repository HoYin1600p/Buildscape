package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PillarIdJoinSyncHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        PillarIdManager manager = PillarIdManager.get();

        if (!manager.hasLoaded()) {
            manager.load();
        }

        net.minecraft.server.MinecraftServer server = player.getServer();
        if (server != null && server.isRunning()) {
            manager.syncColorsFromNBTToManager(server);
        }

        List<PillarIdManager.PillarData> pillarDataList = manager.getAllPillarDataForSync();

        SyncPillarIdsPacket syncPacket = new SyncPillarIdsPacket(pillarDataList);
        ModMessages.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                syncPacket
        );

        net.minecraft.world.level.GameRules rules = player.getLevel().getGameRules();
        ModMessages.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new com.kingodogo.buildscape.network.SyncGameRulesPacket(
                        rules.getBoolean(com.kingodogo.buildscape.world.ModGameRules.FAST_LEAF_DECAY),
                        rules.getBoolean(com.kingodogo.buildscape.world.ModGameRules.DISABLE_ENDERMAN_GRIEFING),
                        rules.getBoolean(com.kingodogo.buildscape.world.ModGameRules.DISABLE_CREEPER_GRIEFING),
                        rules.getBoolean(com.kingodogo.buildscape.world.ModGameRules.DISABLE_GHAST_GRIEFING)
                )
        );

        long cooldown = 0;
        if (player.getPersistentData().contains("WanderingHomemakerCooldownRealTime")) {
            cooldown = player.getPersistentData().getLong("WanderingHomemakerCooldownRealTime");
        }
        ModMessages.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new com.kingodogo.buildscape.network.SyncHomemakerCooldownPacket(cooldown)
        );
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            long cooldown = 0;
            if (player.getPersistentData().contains("WanderingHomemakerCooldownRealTime")) {
                cooldown = player.getPersistentData().getLong("WanderingHomemakerCooldownRealTime");
            }
            ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    new com.kingodogo.buildscape.network.SyncHomemakerCooldownPacket(cooldown)
            );
        }
    }
}
