package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client-to-server request for the full pillar list. The server limits each player to one request per five seconds,
 * loads the registry first if it is not loaded yet, and answers with {@link SyncPillarIdsPacket} chunks.
 */
public class RequestPillarIdsPacket {

    private static final String LAST_REQUEST_TIME = "BuildScapePillarSyncRequestTime";
    private static final long REQUEST_COOLDOWN_MS = 5_000L;

    public RequestPillarIdsPacket() {}

    public RequestPillarIdsPacket(FriendlyByteBuf buf) {}

    public static RequestPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new RequestPillarIdsPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {}

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

            try {
                SyncPillarIdsPacket.sendToPlayer(player, manager.getAllPillarDataForSync());
            } catch (RuntimeException e) {
                BuildScape.LOGGER.error("Unable to send pillar data to {}", player.getGameProfile().getName(), e);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
