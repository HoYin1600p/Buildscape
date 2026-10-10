package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashSet;

public class UpdateConfigPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "update_config");

    private final SyncConfigPacket data;

    public UpdateConfigPacket(PillarParticleConfig config) {
        this.data = new SyncConfigPacket(config);
    }

    public UpdateConfigPacket(SyncConfigPacket data) {
        this.data = data;
    }

    public UpdateConfigPacket(FriendlyByteBuf buf) {
        this.data = new SyncConfigPacket(buf);
    }

    public static UpdateConfigPacket decode(FriendlyByteBuf buf) {
        return new UpdateConfigPacket(buf);
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
        data.write(buf);
    }

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (!Services.PLATFORM.hasPermission(serverPlayer, PillarParticleConfig.CONFIG_PERMISSION_LEVEL)) {
            Services.PLATFORM.sendActionBarMessage(serverPlayer,
                    Services.PLATFORM.translatable("buildscape.config.server_config_only"));
            return;
        }

        PillarParticleConfig serverConfig = PillarParticleConfig.getServerConfig();
        String oldPattern = serverConfig.pattern;

        serverConfig.particle_speed = data.particle_speed;
        serverConfig.particle_spread = data.particle_spread;
        serverConfig.particle_lifetime = data.particle_lifetime;
        serverConfig.particle_density = data.particle_density;
        serverConfig.use_pattern = data.use_pattern;
        serverConfig.pattern = data.pattern;
        serverConfig.pattern_speed = data.pattern_speed;
        serverConfig.pattern_spread = data.pattern_spread;
        serverConfig.pattern_intensity = data.pattern_intensity;
        serverConfig.max_particle_color = data.max_particle_color;
        if (data.particle_color != null) {
            serverConfig.particle_color = new ArrayList<>(data.particle_color);
        }
        if (data.items != null) {
            serverConfig.items = new HashSet<>(data.items);
        }

        serverConfig.savePropertiesToDisk();
        serverConfig.saveItemsToDisk();

        PacketFactory.sendToAll(new SyncConfigPacket(serverConfig));

        PillarIdManager manager = PillarIdManager.get();
        if (manager.hasLoaded()) {
            boolean updatedAny = false;
            boolean isPatternChanged = !data.pattern.equals(oldPattern);

            for (PillarIdManager.PillarData pData : manager.getAllData()) {
                boolean hasPatternOverride = pData.pattern != null && !pData.pattern.equals("default");
                boolean isCustomized = pData.hasColors() || hasPatternOverride;

                if (isCustomized) {
                    if (isPatternChanged && (pData.pattern == null || pData.pattern.equals("default"))) {
                        pData.pattern = oldPattern;
                        updatedAny = true;
                    }
                } else {
                    if (pData.pattern != null || pData.patternSpeed != null || pData.patternSpread != null) {
                        pData.pattern = null;
                        pData.patternSpeed = null;
                        pData.patternSpread = null;
                        pData.patternIntensity = null;
                        updatedAny = true;
                    }
                }
            }

            if (updatedAny) {
                manager.saveImmediate();
            }

            net.minecraft.server.MinecraftServer server = Services.PLATFORM.getServer(serverPlayer);
            if (server != null) {
                for (ServerLevel level : server.getAllLevels()) {
                    if (level == null) continue;
                    String dimensionKey = PillarIdManager.getDimensionKey(level);

                    for (PillarIdManager.PillarData pData : manager.getAllData()) {
                        if (pData == null || !pData.dimension.equals(dimensionKey)) continue;

                        try {
                            BlockPos pos = pData.getBlockPos();
                            if (!level.hasChunkAt(pos)) continue;
                            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                        } catch (Exception e) {
                            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to resend block update for pillar after config update", e);
                        }
                    }
                }
            }
        }
    }
}
