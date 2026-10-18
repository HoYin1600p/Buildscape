package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class UpdateConfigPacket {

    private static final int CHUNK_BYTES = 24_000;
    private static final int MAX_UPDATE_BYTES = 2 * 1024 * 1024;
    private static final int MAX_CHUNKS = (MAX_UPDATE_BYTES + CHUNK_BYTES - 1) / CHUNK_BYTES;

    private final UUID transferId;
    private final int chunkIndex;
    private final int chunkCount;
    private final byte[] payload;

    public UUID getTransferId() { return transferId; }
    public int getChunkIndex() { return chunkIndex; }
    public int getChunkCount() { return chunkCount; }
    public byte[] getPayload() { return payload; }

    private UpdateConfigPacket(UUID transferId, int chunkIndex, int chunkCount, byte[] payload) {
        this.transferId = transferId;
        this.chunkIndex = chunkIndex;
        this.chunkCount = chunkCount;
        this.payload = payload;
    }

    public static List<UpdateConfigPacket> split(PillarParticleConfig config) {
        SyncConfigPacket data = new SyncConfigPacket(config);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            data.encode(buffer);
            if (buffer.readableBytes() > MAX_UPDATE_BYTES) {
                throw new IllegalArgumentException("Config update exceeds maximum size");
            }
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            int count = (bytes.length + CHUNK_BYTES - 1) / CHUNK_BYTES;
            UUID id = UUID.randomUUID();
            List<UpdateConfigPacket> packets = new ArrayList<>();
            for (int index = 0; index < count; index++) {
                int offset = index * CHUNK_BYTES;
                packets.add(new UpdateConfigPacket(id, index, count,
                        Arrays.copyOfRange(bytes, offset, Math.min(bytes.length, offset + CHUNK_BYTES))));
            }
            return packets;
        } finally {
            buffer.release();
        }
    }

    public static void send(PillarParticleConfig config) {
        for (UpdateConfigPacket packet : split(config)) {
            ModMessages.INSTANCE.sendToServer(packet);
        }
    }

    public UpdateConfigPacket(FriendlyByteBuf buf) {
        this.transferId = buf.readUUID();
        this.chunkIndex = NetworkPacketLimits.readBoundedInt(buf, 0, MAX_CHUNKS - 1, "config chunk index");
        this.chunkCount = NetworkPacketLimits.readBoundedInt(buf, 1, MAX_CHUNKS, "config chunk count");
        this.payload = buf.readByteArray(CHUNK_BYTES);
        if (chunkIndex >= chunkCount || payload.length == 0
                || (chunkIndex < chunkCount - 1 && payload.length != CHUNK_BYTES)) {
            throw new DecoderException("Invalid config chunk");
        }
    }

    public static UpdateConfigPacket decode(FriendlyByteBuf buf) {
        return new UpdateConfigPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(transferId);
        buf.writeInt(chunkIndex);
        buf.writeInt(chunkCount);
        buf.writeByteArray(payload);
    }

    public static String applyValues(PillarParticleConfig serverConfig, SyncConfigPacket data) {
        if (!serverConfig.canApplyUpdate()) {
            return "BuildScape: Changes refused because the server config is read-only. Check the server log.";
        }
        if (data.items != null) {
            for (String item : data.items) {
                if (!PillarParticleConfig.isValidItemId(item) && !serverConfig.items.contains(item)) {
                    return "BuildScape: Invalid item or tag refused: " + item;
                }
            }
        }
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
            serverConfig.editItems(data.items);
        }
        return null;
    }

    public static final class Assembly extends ConfigAssembly {
    }

    private static final class ServerPendingHolder {
        private static final Map<ServerPlayer, ConfigAssembly> PENDING = new WeakHashMap<>();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            PillarParticleConfig serverConfig = PillarParticleConfig.getServerConfig();
            if (!PillarParticleConfig.canPlayerEdit(player, serverConfig.edit_permission_mode, PillarParticleConfig.EditScope.GENERAL)) {
                player.displayClientMessage(new net.minecraft.network.chat.TranslatableComponent(
                        "buildscape.config.server_config_only").withStyle(net.minecraft.ChatFormatting.RED), true);
                return;
            }

            ServerPendingHolder.PENDING.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getStarted() > 30_000L);
            if (chunkIndex == 0) {
                ServerPendingHolder.PENDING.put(player, new ConfigAssembly());
            }
            ConfigAssembly assembly = ServerPendingHolder.PENDING.get(player);
            if (assembly == null) {
                return;
            }
            SyncConfigPacket data;
            try {
                data = assembly.accept(this);
            } catch (Exception e) {
                ServerPendingHolder.PENDING.remove(player);
                com.kingodogo.buildscape.BuildScape.LOGGER.warn("Rejected config update from " + player.getUUID(), e);
                player.displayClientMessage(new net.minecraft.network.chat.TextComponent(
                        "BuildScape: Config update refused because it contains invalid or incomplete data.")
                        .withStyle(net.minecraft.ChatFormatting.RED), false);
                return;
            }
            if (data == null) {
                return;
            }
            ServerPendingHolder.PENDING.remove(player);

            String oldPattern = serverConfig.pattern;

            String refusal = applyValues(serverConfig, data);
            if (refusal != null) {
                player.displayClientMessage(new net.minecraft.network.chat.TextComponent(refusal)
                        .withStyle(net.minecraft.ChatFormatting.RED), false);
                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncConfigPacket(serverConfig));
                return;
            }

            if (PillarParticleConfig.canPlayerEdit(player, serverConfig.edit_permission_mode, PillarParticleConfig.EditScope.POLICY)
                    && data.edit_permission_mode != null) {
                serverConfig.edit_permission_mode = data.edit_permission_mode;
            }

            boolean propertiesSaved = serverConfig.savePropertiesToDisk();
            boolean itemsSaved = serverConfig.saveItemsToDisk();
            if (!propertiesSaved || !itemsSaved) {
                String files = !propertiesSaved && !itemsSaved ? "pillar-properties.json and pillar-items.json"
                        : !propertiesSaved ? "pillar-properties.json" : "pillar-items.json";
                player.displayClientMessage(new net.minecraft.network.chat.TextComponent(
                        "BuildScape: Changes to " + files + " are active but will not survive a restart: "
                                + serverConfig.getSaveFailureReason() + ".")
                        .withStyle(net.minecraft.ChatFormatting.YELLOW), false);
            }

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
                    // Persist the explicit config apply together with the updated registry settings.
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
