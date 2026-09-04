package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class RequestPillarIdsPacket {

    public RequestPillarIdsPacket() {
    }

    public RequestPillarIdsPacket(FriendlyByteBuf buf) {
    }

    public static RequestPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new RequestPillarIdsPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
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
        });
        ctx.get().setPacketHandled(true);
    }
}
