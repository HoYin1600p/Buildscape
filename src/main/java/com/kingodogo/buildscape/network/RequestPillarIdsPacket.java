package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestPillarIdsPacket {

    private static final String LAST_REQUEST_TIME = "BuildScapePillarSyncRequestTime";
    private static final long REQUEST_COOLDOWN_MS = 5_000L;

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

            long now = System.currentTimeMillis();
            long lastRequest = player.getPersistentData().getLong(LAST_REQUEST_TIME);
            if (lastRequest > 0L && now - lastRequest < REQUEST_COOLDOWN_MS) {
                return;
            }
            player.getPersistentData().putLong(LAST_REQUEST_TIME, now);

            PillarIdManager manager = PillarIdManager.get();

            if (!manager.hasLoaded()) {
                manager.load();
                return;
            }

            SyncPillarIdsPacket.sendToPlayer(player, manager.getAllPillarDataForSync());
        });
        ctx.get().setPacketHandled(true);
    }
}
