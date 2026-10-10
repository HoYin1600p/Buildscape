package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class RequestPillarIdsPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "request_pillar_ids");

    private static final String LAST_REQUEST_TIME = "BuildScapePillarSyncRequestTime";
    private static final long REQUEST_COOLDOWN_MS = 5_000L;

    public RequestPillarIdsPacket() {}

    public RequestPillarIdsPacket(FriendlyByteBuf buf) {}

    public static RequestPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new RequestPillarIdsPacket(buf);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENT_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {}

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        long now = System.currentTimeMillis();
        CompoundTag persistentData = Services.PLATFORM.getEntityData(serverPlayer);
        long lastRequest = Services.PLATFORM.getTagLong(persistentData, LAST_REQUEST_TIME, 0L);
        if (lastRequest > 0L && now - lastRequest < REQUEST_COOLDOWN_MS) {
            return;
        }
        persistentData.putLong(LAST_REQUEST_TIME, now);

        PillarIdManager manager = PillarIdManager.get();
        if (!manager.hasLoaded()) {
            manager.load(Services.PLATFORM.getServer(serverPlayer));
            return;
        }

        try {
            SyncPillarIdsPacket.sendToPlayer(serverPlayer, manager.getAllPillarDataForSync());
        } catch (RuntimeException e) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to sync pillar IDs to requesting player", e);
        }
    }
}
