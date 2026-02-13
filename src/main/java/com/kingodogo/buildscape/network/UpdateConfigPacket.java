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

            // Only OP can change global config (level 2)
            if (!player.hasPermissions(2)) {
                return;
            }

            PillarParticleConfig serverConfig = PillarParticleConfig.get();
            String oldPattern = serverConfig.pattern; // Capture old pattern for transition logic

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

            // Notify all players about the new config
            ModMessages.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    new SyncConfigPacket(serverConfig)
            );

            // Transition logic: Identify which pillars follow the new global config and which stick to their current state.
            // As requested, customized pillars (dyed OR pattern-overridden) should NOT be affected by the global pattern button.
            com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager.get();
            if (manager.hasLoaded()) {
                boolean updatedAny = false;
                boolean isPatternChanged = !data.pattern.equals(oldPattern);

                for (com.kingodogo.buildscape.config.PillarIdManager.PillarData pData : manager.getAllData()) {
                    // Robust check for modification: has colors or has hardcoded pattern settings
                    boolean hasPatternOverride = pData.pattern != null && !pData.pattern.equals("default");
                    boolean isCustomized = pData.hasColors() || hasPatternOverride;

                    if (isCustomized) {
                        // If it's customized in any way but currently following the global pattern (no hard override yet),
                        // we MUST lock its effective pattern to the OLD one before the global change takes over.
                        if (isPatternChanged && (pData.pattern == null || pData.pattern.equals("default"))) {
                            pData.pattern = oldPattern; // Lock to the pattern it was using BEFORE this global change
                            updatedAny = true;
                        }
                    } else {
                        // Unmodified pillar: Clear its overrides to ensure it follows the new global config precisely.
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

                // Sync ALL relevant pillars to ensure they pick up their updated settings (either new global or newly locked override)
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
                                // Force block update to ensure client recognizes the pattern lock immediately
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
