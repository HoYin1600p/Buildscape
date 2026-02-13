package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Sent from client to server to request a fresh sync of pillar IDs.
 * This ensures the GUI always has the latest data when opened.
 */
public class RequestPillarIdsPacket {

    public RequestPillarIdsPacket() {
    }

    public RequestPillarIdsPacket(FriendlyByteBuf buf) {
        // No data needed
    }

    public static RequestPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new RequestPillarIdsPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        // No data needed
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }

            // Get pillar data from server and send it to the requesting client
            PillarIdManager manager = PillarIdManager.get();

            // Ensure manager is loaded
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
        });
        ctx.get().setPacketHandled(true);
    }
}
