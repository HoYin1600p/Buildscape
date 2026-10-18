package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
            if (player == null) return;
            if (!com.kingodogo.buildscape.config.PillarParticleConfig.canPlayerEdit(player,
                    com.kingodogo.buildscape.config.PillarParticleConfig.getServerConfig().edit_permission_mode,
                    com.kingodogo.buildscape.config.PillarParticleConfig.EditScope.LIMITED)) return;

            PillarIdManager manager = PillarIdManager.get();
            boolean changed = manager.removePillars(pillarIds);

            if (changed) {
                manager.syncAllLoadedPillars(player.getServer());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
