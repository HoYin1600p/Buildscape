package com.kingodogo.buildscape.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.config.PillarIdManager;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class UpdateAllPillarIdsPacket {

    private static final Gson GSON = new GsonBuilder().create();
    private final List<PillarIdManager.PillarData> pillarDataList;

    public UpdateAllPillarIdsPacket(Map<String, PillarIdManager.PillarData> dataMap) {
        this.pillarDataList = new ArrayList<>(dataMap.values());
    }

    public UpdateAllPillarIdsPacket(FriendlyByteBuf buf) {
        int count = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_PILLARS, "pillar");
        this.pillarDataList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String json = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_JSON_LENGTH, "pillar data");
            PillarIdManager.PillarData data = GSON.fromJson(json, PillarIdManager.PillarData.class);
            if (data == null || data.id == null) {
                throw new DecoderException("pillar data is missing its id");
            }
            this.pillarDataList.add(data);
        }
    }

    public static UpdateAllPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new UpdateAllPillarIdsPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(pillarDataList.size(), NetworkPacketLimits.MAX_PILLARS, "pillar");
        buf.writeInt(pillarDataList.size());
        for (PillarIdManager.PillarData data : pillarDataList) {
            String json = GSON.toJson(data);
            NetworkPacketLimits.writeUtf(buf, json,
                    NetworkPacketLimits.MAX_PILLAR_JSON_LENGTH, "pillar data");
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (!player.hasPermissions(2)) {
                return;
            }

            PillarIdManager manager = PillarIdManager.get();
            Map<String, PillarIdManager.PillarData> newMap = new LinkedHashMap<>();
            for (PillarIdManager.PillarData data : pillarDataList) {
                if (data.id != null) {
                    newMap.put(data.id, data);
                }
            }

            manager.replaceAllPillarData(newMap);
            manager.saveImmediate();

            ModMessages.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                    new SyncPillarIdsPacket(new ArrayList<>(newMap.values()))
            );

            manager.syncAllLoadedPillars(player.getServer());
        });
        ctx.get().setPacketHandled(true);
    }
}
