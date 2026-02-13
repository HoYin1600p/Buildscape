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

/**
 * Handles syncing of pillar IDs to the client when they join the server.
 * This ensures the client side has the latest data immediately.
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PillarIdJoinSyncHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        // On join, send all pillar data from server to the joining client
        PillarIdManager manager = PillarIdManager.get();

        // Ensure manager is loaded on server side
        if (!manager.hasLoaded()) {
            manager.load();
        }

        // Sync colors from NBT before sending to ensure freshest data
        net.minecraft.server.MinecraftServer server = player.getServer();
        if (server != null && server.isRunning()) {
            manager.syncColorsFromNBTToManager(server);
        }

        // Get all pillar data and send to client
        List<PillarIdManager.PillarData> pillarDataList = manager.getAllPillarDataForSync();

        SyncPillarIdsPacket syncPacket = new SyncPillarIdsPacket(pillarDataList);
        ModMessages.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                syncPacket
        );
    }
}
