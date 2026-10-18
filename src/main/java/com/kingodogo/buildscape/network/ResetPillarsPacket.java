package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
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
            if (serverPlayer == null || !PillarParticleConfig.canPlayerEdit(serverPlayer,
                    PillarParticleConfig.getServerConfig().edit_permission_mode, PillarParticleConfig.EditScope.LIMITED)) {
                return;
            }

            PillarIdManager manager = PillarIdManager.get();
            boolean changed = false;

            for (String id : packet.pillarIds) {
                if (id == null) continue;
                PillarIdManager.PillarData existing = manager.getPillarData(id);
                if (existing != null) {
                    PillarIdManager.clearPillarOverrides(existing);
                    changed = true;
                }
            }

            if (changed) {
                manager.saveImmediate();
                manager.syncAllLoadedPillars(serverPlayer.getServer());
                SyncPillarIdsPacket.sendToAll(manager.getAllPillarDataForSync());
            }
        });
        context.setPacketHandled(true);
    }
}
