package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ResetPillarsPacket {

    private final List<String> pillarIds;

    public ResetPillarsPacket(Collection<String> pillarIds) {
        this.pillarIds = pillarIds != null ? new ArrayList<>(pillarIds) : new ArrayList<>();
    }

    public ResetPillarsPacket(FriendlyByteBuf buf) {
        int count = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_PILLARS, "pillar id");
        this.pillarIds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.pillarIds.add(NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id"));
        }
    }

    public static ResetPillarsPacket decode(FriendlyByteBuf buf) {
        return new ResetPillarsPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(pillarIds.size(), NetworkPacketLimits.MAX_PILLARS, "pillar id");
        buf.writeInt(pillarIds.size());
        for (String id : pillarIds) {
            NetworkPacketLimits.writeUtf(buf, id, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
        }
    }

    public static void handle(ResetPillarsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null || !EditGuard.require(serverPlayer, ConfigSetting.PILLAR_EDIT)) {
                return;
            }

            PillarIdManager manager = PillarIdManager.get();
            if (manager.resetPillars(packet.pillarIds)) {
                manager.saveImmediate();
                manager.syncAllLoadedPillars(serverPlayer.getServer());
                SyncPillarIdsPacket.sendToAll(manager.getAllPillarDataForSync());
            }
        });
        context.setPacketHandled(true);
    }
}
