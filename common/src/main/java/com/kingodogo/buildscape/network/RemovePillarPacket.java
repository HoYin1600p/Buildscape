package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class RemovePillarPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "remove_pillar");

    private final List<String> pillarIds;

    public RemovePillarPacket(List<String> pillarIds) {
        this.pillarIds = new ArrayList<>(pillarIds);
    }

    public RemovePillarPacket(FriendlyByteBuf buf) {
        int size = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_PILLARS, "pillar id");
        this.pillarIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.pillarIds.add(NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id"));
        }
    }

    public static RemovePillarPacket decode(FriendlyByteBuf buf) {
        return new RemovePillarPacket(buf);
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
    public void write(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(pillarIds.size(), NetworkPacketLimits.MAX_PILLARS, "pillar id");
        buf.writeInt(pillarIds.size());
        for (String id : pillarIds) {
            NetworkPacketLimits.writeUtf(buf, id, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
        }
    }

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !com.kingodogo.buildscape.platform.Services.PLATFORM.hasPermission(serverPlayer, 2)) return;

        PillarIdManager manager = PillarIdManager.get();
        boolean changed = manager.removePillars(pillarIds);
        if (changed) {
            manager.syncAllLoadedPillars(com.kingodogo.buildscape.platform.Services.PLATFORM.getServer(serverPlayer));
        }
    }
}
