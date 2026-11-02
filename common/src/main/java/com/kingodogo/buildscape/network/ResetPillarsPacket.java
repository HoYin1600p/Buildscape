package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ResetPillarsPacket implements CommonPacket {
    public static final CommonId ID = new CommonId("buildscape", "reset_pillars");
    private final List<String> ids;

    public ResetPillarsPacket(Collection<String> ids) {
        this.ids = new ArrayList<>(ids);
    }

    public static ResetPillarsPacket decode(FriendlyByteBuf buf) {
        int count = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_PILLARS, "pillar id");
        List<String> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id"));
        }
        return new ResetPillarsPacket(ids);
    }

    @Override public CommonId getId() { return ID; }
    @Override public PacketDirection getDirection() { return PacketDirection.CLIENT_TO_SERVER; }

    @Override public void write(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(ids.size(), NetworkPacketLimits.MAX_PILLARS, "pillar id");
        buf.writeInt(ids.size());
        for (String id : ids) NetworkPacketLimits.writeUtf(buf, id, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
    }

    @Override public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !EditGuard.requirePillarEdit(serverPlayer)) return;
        PillarIdManager manager = PillarIdManager.get();
        if (manager.resetPillars(ids)) {
            manager.saveImmediate();
            manager.syncAllLoadedPillars(Services.PLATFORM.getServer(serverPlayer));
            SyncPillarIdsPacket.sendToAll(manager.getAllPillarDataForSync());
        }
    }
}
