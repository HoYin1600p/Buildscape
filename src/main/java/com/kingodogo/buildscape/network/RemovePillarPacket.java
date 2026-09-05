package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RemovePillarPacket {

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

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(pillarIds.size(), NetworkPacketLimits.MAX_PILLARS, "pillar id");
        buf.writeInt(pillarIds.size());
        for (String id : pillarIds) {
            NetworkPacketLimits.writeUtf(buf, id, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!player.hasPermissions(2)) return;

            PillarIdManager manager = PillarIdManager.get();
            boolean changed = false;
            for (String id : pillarIds) {
                PillarIdManager.PillarData data = manager.getPillarData(id);
                if (data != null) {
                    manager.removePillar(id);
                    changed = true;
                }
            }

            if (changed) {
                manager.saveImmediate();

                ModMessages.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                        new SyncPillarIdsPacket(manager.getAllPillarDataForSync())
                );

                manager.syncAllLoadedPillars(player.getServer());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
