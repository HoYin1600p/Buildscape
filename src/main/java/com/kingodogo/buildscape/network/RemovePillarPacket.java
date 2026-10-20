package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Client-to-server request to delete a list of pillar records (at most 256 ids) from the server's pillar registry.
 * Needs the pillar-edit permission; on success the server re-syncs pillar data to every player.
 */
public class RemovePillarPacket {

    public static final int MAX_IDS = 256;

    private final List<String> pillarIds;

    public RemovePillarPacket(List<String> pillarIds) {
        NetworkPacketLimits.checkCount(pillarIds.size(), MAX_IDS, "pillar id");
        this.pillarIds = new ArrayList<>(pillarIds);
    }

    public RemovePillarPacket(String pillarId) {
        this(pillarId != null ? List.of(pillarId) : List.of());
    }

    public RemovePillarPacket(FriendlyByteBuf buf) {
        int size = NetworkPacketLimits.readCount(buf, MAX_IDS, "pillar id");
        this.pillarIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.pillarIds.add(NetworkPacketLimits.readUtf(buf, 32, "pillar id"));
        }
    }

    public static RemovePillarPacket decode(FriendlyByteBuf buf) {
        return new RemovePillarPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(pillarIds.size(), MAX_IDS, "pillar id");
        buf.writeInt(pillarIds.size());
        for (String id : pillarIds) {
            NetworkPacketLimits.writeUtf(buf, id, 32, "pillar id");
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            if (!EditGuard.require(player, ConfigSetting.PILLAR_EDIT)) {
                return;
            }

            PillarIdManager manager = PillarIdManager.get();
            boolean changed = manager.removePillars(pillarIds);

            if (changed) {
                manager.syncAllLoadedPillars(player.getServer());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
