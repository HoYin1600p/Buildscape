package com.kingodogo.buildscape.network;

import com.google.gson.Gson;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.core.io.Json;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Client-to-server bulk update of pillar records, sent as one JSON string per pillar. Needs the bulk pillar-edit
 * permission. The server checks id, pattern and numeric fields of each entry and stops at the first
 * out-of-range value.
 */
public class UpdateAllPillarIdsPacket {

    private static final Gson GSON = Json.PLAIN;
    private final List<PillarIdManager.PillarData> pillarDataList;

    public UpdateAllPillarIdsPacket(Map<String, PillarIdManager.PillarData> dataMap) {
        this.pillarDataList = dataMap != null ? new ArrayList<>(dataMap.values()) : new ArrayList<>();
    }

    public UpdateAllPillarIdsPacket(List<PillarIdManager.PillarData> pillarDataList) {
        this.pillarDataList = pillarDataList != null ? new ArrayList<>(pillarDataList) : new ArrayList<>();
    }

    public UpdateAllPillarIdsPacket(Collection<PillarIdManager.PillarData> pillarDataCollection) {
        this.pillarDataList = pillarDataCollection != null ? new ArrayList<>(pillarDataCollection) : new ArrayList<>();
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

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(pillarDataList.size(), NetworkPacketLimits.MAX_PILLARS, "pillar");
        buf.writeInt(pillarDataList.size());
        for (PillarIdManager.PillarData data : pillarDataList) {
            String json = GSON.toJson(data);
            NetworkPacketLimits.writeUtf(buf, json, NetworkPacketLimits.MAX_PILLAR_JSON_LENGTH, "pillar data");
        }
    }

    public static void handle(UpdateAllPillarIdsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null || !EditGuard.require(serverPlayer, ConfigSetting.PILLAR_BULK)) {
                return;
            }

            PillarIdManager manager = PillarIdManager.get();
            boolean changed = false;

            for (PillarIdManager.PillarData incoming : packet.pillarDataList) {
                if (incoming == null || incoming.id == null) {
                    continue;
                }
                if (incoming.id.length() > NetworkPacketLimits.MAX_PILLAR_ID_LENGTH) {
                    return;
                }
                if (incoming.pattern != null && incoming.pattern.length() > NetworkPacketLimits.MAX_PATTERN_LENGTH) {
                    return;
                }
                if (incoming.pattern_speed != null
                        && (!Double.isFinite(incoming.pattern_speed) || incoming.pattern_speed < 0)) {
                    return;
                }
                if (incoming.pattern_spread != null
                        && (!Double.isFinite(incoming.pattern_spread) || incoming.pattern_spread < 0)) {
                    return;
                }
                if (incoming.pattern_intensity != null
                        && (!Double.isFinite(incoming.pattern_intensity) || incoming.pattern_intensity < 0)) {
                    return;
                }
                if (incoming.max_particle_color != null && (incoming.max_particle_color < 0
                        || incoming.max_particle_color > NetworkPacketLimits.MAX_DYE_COLORS)) {
                    return;
                }
                if (incoming.dyeColors != null) {
                    if (incoming.dyeColors.size() > NetworkPacketLimits.MAX_DYE_COLORS) {
                        return;
                    }
                    for (String color : incoming.dyeColors) {
                        if (color == null || color.length() > NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH) {
                            return;
                        }
                    }
                }
            }

            for (PillarIdManager.PillarData incoming : packet.pillarDataList) {
                if (incoming == null || incoming.id == null) {
                    continue;
                }
                PillarIdManager.PillarData existing = manager.getPillarData(incoming.id);
                if (existing != null) {
                    existing.pattern = incoming.pattern;
                    existing.pattern_speed = incoming.pattern_speed != null
                            ? NetworkPacketLimits.clamp(incoming.pattern_speed, NetworkPacketLimits.MIN_PARTICLE_RATE,
                                    NetworkPacketLimits.MAX_PARTICLE_SPEED)
                            : null;
                    existing.pattern_spread = incoming.pattern_spread != null
                            ? NetworkPacketLimits.clamp(incoming.pattern_spread, NetworkPacketLimits.MIN_PARTICLE_RATE,
                                    NetworkPacketLimits.MAX_PARTICLE_SPREAD)
                            : null;
                    existing.pattern_intensity = incoming.pattern_intensity != null
                            ? NetworkPacketLimits.clamp(incoming.pattern_intensity,
                                    NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_INTENSITY)
                            : null;
                    existing.use_pattern = incoming.use_pattern;
                    existing.max_particle_color = incoming.max_particle_color != null
                            ? Math.max(0, Math.min(NetworkPacketLimits.MAX_DYE_COLORS, incoming.max_particle_color))
                            : null;
                    if (incoming.dyeColors != null) {
                        existing.dyeColors = new ArrayList<>(incoming.dyeColors);
                    }
                    existing.modifiedTime = System.currentTimeMillis();
                    manager.applyFrameEdit(existing);
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
