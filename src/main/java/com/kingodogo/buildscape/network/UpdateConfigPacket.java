package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

public class UpdateConfigPacket {

    private final SyncConfigPacket data;

    public UpdateConfigPacket(PillarParticleConfig config) {
        this.data = new SyncConfigPacket(config);
    }

    public UpdateConfigPacket(FriendlyByteBuf buf) {
        this.data = new SyncConfigPacket(buf);
    }

    public static UpdateConfigPacket decode(FriendlyByteBuf buf) {
        return new UpdateConfigPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        data.encode(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (!player.hasPermissions(2)) {
                return;
            }

            PillarParticleConfig serverConfig = PillarParticleConfig.get();
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

            serverConfig.saveProperties();
            serverConfig.saveItems();

            ModMessages.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    new SyncConfigPacket(serverConfig)
            );

            com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager.get();
            if (manager.hasLoaded()) {
                boolean updatedAny = false;
                boolean isPatternChanged = !data.pattern.equals(oldPattern);

                for (com.kingodogo.buildscape.config.PillarIdManager.PillarData pData : manager.getAllData()) {
                    boolean hasPatternOverride = pData.pattern != null && !pData.pattern.equals("default");
                    boolean isCustomized = pData.hasColors() || hasPatternOverride;

                    if (isCustomized) {
                        if (isPatternChanged && (pData.pattern == null || pData.pattern.equals("default"))) {
                            pData.pattern = oldPattern;
                            updatedAny = true;
                        }
                    } else {
                        if (pData.pattern != null || pData.pattern_speed != null || pData.pattern_spread != null) {
                            pData.pattern = null;
                            pData.pattern_speed = null;
                            pData.pattern_spread = null;
                            pData.pattern_intensity = null;
                            updatedAny = true;
                        }
                    }
                }

                if (updatedAny) {
                    manager.saveImmediate();
                }

                for (net.minecraft.server.level.ServerLevel level : player.getServer().getAllLevels()) {
                    if (level == null) continue;
                    String dimensionKey = com.kingodogo.buildscape.config.PillarIdManager.getDimensionKey(level);

                    for (com.kingodogo.buildscape.config.PillarIdManager.PillarData pData : manager.getAllData()) {
                        if (pData == null || !pData.dimension.equals(dimensionKey)) continue;

                        try {
                            net.minecraft.core.BlockPos pos = pData.getBlockPos();
                            if (!level.hasChunkAt(pos)) continue;

                            net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
                            if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
                                pillarBE.syncFromData(pData);
                                level.sendBlockUpdated(pos, pillarBE.getBlockState(), pillarBE.getBlockState(), 3);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
