package com.kingodogo.buildscape.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateAllPillarIdsPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "update_all_pillar_ids");
    private static final Gson GSON = new GsonBuilder().create();

    private final List<PillarIdManager.PillarData> pillarDataList;

    public UpdateAllPillarIdsPacket(Map<String, PillarIdManager.PillarData> dataMap) {
        this.pillarDataList = new ArrayList<>(dataMap.values());
    }

    public UpdateAllPillarIdsPacket(List<PillarIdManager.PillarData> pillarDataList) {
        this.pillarDataList = new ArrayList<>(pillarDataList);
    }

    public UpdateAllPillarIdsPacket(FriendlyByteBuf buf) {
        int count = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_PILLARS, "pillar");
        this.pillarDataList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String json = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_JSON_LENGTH, "pillar data");
            PillarIdManager.PillarData data = GSON.fromJson(json, PillarIdManager.PillarData.class);
            if (data == null || data.id == null) {
                throw new IllegalArgumentException("pillar data is missing its id");
            }
            this.pillarDataList.add(data);
        }
    }

    public static UpdateAllPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new UpdateAllPillarIdsPacket(buf);
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
        NetworkPacketLimits.checkCount(pillarDataList.size(), NetworkPacketLimits.MAX_PILLARS, "pillar");
        buf.writeInt(pillarDataList.size());
        for (PillarIdManager.PillarData data : pillarDataList) {
            String json = GSON.toJson(data);
            NetworkPacketLimits.writeUtf(buf, json, NetworkPacketLimits.MAX_PILLAR_JSON_LENGTH, "pillar data");
        }
    }

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !com.kingodogo.buildscape.platform.Services.PLATFORM.hasPermission(serverPlayer, 2)) return;

        PillarIdManager manager = PillarIdManager.get();
        boolean changed = false;

        for (PillarIdManager.PillarData incoming : pillarDataList) {
            if (incoming == null || incoming.id == null) continue;
            PillarIdManager.PillarData existing = manager.getPillarData(incoming.id);
            if (existing != null) {
                existing.pattern = incoming.pattern;
                existing.patternSpeed = incoming.patternSpeed;
                existing.patternSpread = incoming.patternSpread;
                existing.patternIntensity = incoming.patternIntensity;
                existing.usePattern = incoming.usePattern;
                existing.maxParticleColor = incoming.maxParticleColor;
                if (incoming.dyeColors != null) {
                    existing.dyeColors = new ArrayList<>(incoming.dyeColors);
                }
                existing.modifiedTime = System.currentTimeMillis();
                changed = true;
            }
        }

        if (changed) {
            manager.saveImmediate();
            manager.syncAllLoadedPillars(com.kingodogo.buildscape.platform.Services.PLATFORM.getServer(serverPlayer));
        }
    }
}
