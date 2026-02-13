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
        int size = buf.readInt();
        this.pillarIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.pillarIds.add(buf.readUtf());
        }
    }

    public static RemovePillarPacket decode(FriendlyByteBuf buf) {
        return new RemovePillarPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(pillarIds.size());
        for (String id : pillarIds) {
            buf.writeUtf(id);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            PillarIdManager manager = PillarIdManager.get();
            boolean changed = false;
            for (String id : pillarIds) {
                // We don't check for OP here as usually players managing the GUI should be able to remove them.
                // If it's a global server-wide manager, maybe security is needed, but the user didn't specify.
                // However, PillarIdManager is usually global.

                PillarIdManager.PillarData data = manager.getPillarData(id);
                if (data != null) {
                    manager.removePillar(id);
                    changed = true;
                }
            }

            if (changed) {
                // IMPORTANT: Save immediately to file so it's persistent
                manager.saveImmediate();

                // Broadcast the updated list to all players so their GUIs sync
                ModMessages.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                        new SyncPillarIdsPacket(manager.getAllPillarDataForSync())
                );

                // Force sync pillars in the world so they stop using the removed IDs
                manager.syncAllLoadedPillars(player.getServer());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
