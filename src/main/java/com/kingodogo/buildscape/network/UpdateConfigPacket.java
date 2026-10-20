package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.feature.pillar.PillarBlockEntity;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        return split(config, ConfigSetting.uploadable());
    }

    public static List<UpdateConfigPacket> split(PillarParticleConfig config, Set<ConfigSetting> settings) {
        SyncConfigPacket data = new SyncConfigPacket(config);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            ConfigAssembly.writeUpload(buffer, data, settings);
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
        for (UpdateConfigPacket packet : ClientUpload.split(config)) {
            ModMessages.INSTANCE.sendToServer(packet);
        }
    }

    static List<UpdateConfigPacket> splitForClient(PillarParticleConfig config, int syncedLevel, boolean levelTwo, boolean levelFour) {
        int level = PillarParticleConfig.resolveClientPermissionLevel(syncedLevel, levelTwo, levelFour);
        Set<ConfigSetting> settings = ConfigSetting.allowed(config.edit_permission_mode, level);
        settings.removeIf(setting -> setting.itemsFile ? !config.canEditItems() : !config.canEditProperties());
        return split(config, settings);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientUpload {
        private static List<UpdateConfigPacket> split(PillarParticleConfig config) {
            LocalPlayer player = Minecraft.getInstance().player;
            return splitForClient(config, -1, player != null && player.hasPermissions(2),
                    player != null && player.hasPermissions(PillarParticleConfig.CONFIG_PERMISSION_LEVEL));
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
        return applyValues(serverConfig, data, PillarParticleConfig.CONFIG_PERMISSION_LEVEL);
    }

    public static String applyValues(PillarParticleConfig serverConfig, SyncConfigPacket data, int permissionLevel) {
        String refusal = null;
        Set<ConfigSetting> accepted = EnumSet.noneOf(ConfigSetting.class);
        // Resolve every rule against the policy before this upload, including policy changes.
        for (ConfigSetting setting : data.uploadSettings) {
            if (!setting.canEdit(serverConfig.edit_permission_mode, permissionLevel)) {
                refusal = "BuildScape: This setting is locked by the server edit policy.";
            } else if (!serverConfig.canApplyUpdate(setting.itemsFile, !setting.itemsFile)) {
                refusal = "BuildScape: Changes refused because the server config is read-only. Check the server log.";
            } else {
                accepted.add(setting);
            }
        }
        if (accepted.contains(ConfigSetting.ITEMS) && data.items != null) {
            for (String item : data.items) {
                if (!PillarParticleConfig.isValidItemId(item) && !serverConfig.items.contains(item)) {
                    refusal = "BuildScape: Invalid item or tag refused: " + item;
                    accepted.remove(ConfigSetting.ITEMS);
                    break;
                }
            }
        }
        if (accepted.contains(ConfigSetting.PARTICLES)) {
            serverConfig.particle_speed = data.particle_speed;
            serverConfig.particle_spread = data.particle_spread;
            serverConfig.particle_lifetime = data.particle_lifetime;
            serverConfig.particle_density = data.particle_density;
            serverConfig.use_pattern = data.use_pattern;
            serverConfig.pattern = data.pattern;
            serverConfig.pattern_speed = data.pattern_speed;
            serverConfig.pattern_spread = data.pattern_spread;
            serverConfig.pattern_intensity = data.pattern_intensity;
        }
        if (accepted.contains(ConfigSetting.COLORS)) {
            serverConfig.max_particle_color = data.max_particle_color;
            if (data.particle_color != null) {
                serverConfig.particle_color = new ArrayList<>(data.particle_color);
            }
        }
        if (accepted.contains(ConfigSetting.ITEMS) && data.items != null) {
            serverConfig.editItems(data.items);
        }
        if (accepted.contains(ConfigSetting.POLICY) && data.edit_permission_mode != null) {
            serverConfig.edit_permission_mode = data.edit_permission_mode;
        }
        data.uploadSettings = accepted;
        return refusal;
    }

    public static final class Assembly extends ConfigAssembly {
    }

    private static final class ServerPendingHolder {
        private static final Map<ServerPlayer, ConfigAssembly> PENDING = new WeakHashMap<>();
    }

    static <K> ConfigAssembly pendingAssembly(Map<K, ConfigAssembly> pending, K sender, UpdateConfigPacket packet,
                                            PillarParticleConfig.EditPermissionMode mode, int permissionLevel) {
        if (ConfigSetting.allowed(mode, permissionLevel).isEmpty()) {
            pending.remove(sender);
            return null;
        }
        pending.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getStarted() > 30_000L);
        if (packet.chunkIndex == 0) {
            pending.put(sender, new ConfigAssembly());
        }
        return pending.get(sender);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }

            PillarParticleConfig serverConfig = PillarParticleConfig.getServerConfig();
            int permissionLevel = player.hasPermissions(PillarParticleConfig.CONFIG_PERMISSION_LEVEL)
                    ? PillarParticleConfig.CONFIG_PERMISSION_LEVEL : player.hasPermissions(2) ? 2 : 0;
            ConfigAssembly assembly = pendingAssembly(ServerPendingHolder.PENDING, player, this,
                    serverConfig.edit_permission_mode, permissionLevel);
            if (assembly == null) {
                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncConfigPacket(serverConfig));
                EditGuard.refuse(player, EditRefusedPacket.Reason.LOCKED, null);
                return;
            }
            SyncConfigPacket data;
            try {
                data = assembly.accept(this);
            } catch (Exception e) {
                ServerPendingHolder.PENDING.remove(player);
                BuildScape.LOGGER.warn("Rejected config update from {}", player.getUUID(), e);
                player.displayClientMessage(new TextComponent(
                        "BuildScape: Config update refused because it contains invalid or incomplete data.")
                        .withStyle(ChatFormatting.RED), false);
                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncConfigPacket(serverConfig));
                EditGuard.refuse(player, EditRefusedPacket.Reason.INVALID, null);
                return;
            }
            if (data == null) {
                return;
            }
            ServerPendingHolder.PENDING.remove(player);

            String oldPattern = serverConfig.pattern;

            String refusal = applyValues(serverConfig, data, permissionLevel);
            if (refusal != null) {
                player.displayClientMessage(new TextComponent(refusal)
                        .withStyle(ChatFormatting.RED), false);
                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncConfigPacket(serverConfig));
                EditGuard.refuse(player, EditGuard.reasonFor(refusal), null);
            }
            if (data.uploadSettings.isEmpty()) {
                if (refusal == null) {
                    ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncConfigPacket(serverConfig));
                }
                return;
            }

            boolean propertiesSaved = data.uploadSettings.stream().noneMatch(setting -> !setting.itemsFile)
                    || serverConfig.savePropertiesToDisk();
            boolean itemsSaved = !data.uploadSettings.contains(ConfigSetting.ITEMS) || serverConfig.saveItemsToDisk();
            if (!propertiesSaved || !itemsSaved) {
                String files = !propertiesSaved && !itemsSaved ? "pillar-properties.json and pillar-items.json"
                        : !propertiesSaved ? "pillar-properties.json" : "pillar-items.json";
                player.displayClientMessage(new TextComponent(
                        "BuildScape: Changes to " + files + " are active but will not survive a restart: "
                                + serverConfig.getSaveFailureReason() + ".")
                        .withStyle(ChatFormatting.YELLOW), false);
            }

            ModMessages.INSTANCE.send(
                    PacketDistributor.ALL.noArg(),
                    new SyncConfigPacket(serverConfig)
            );

            if (!data.uploadSettings.contains(ConfigSetting.PARTICLES) && !data.uploadSettings.contains(ConfigSetting.COLORS)) {
                return;
            }

            PillarIdManager manager = PillarIdManager.get();
            if (manager.hasLoaded()) {
                boolean updatedAny = false;
                boolean isPatternChanged = !serverConfig.pattern.equals(oldPattern);

                for (PillarIdManager.PillarData pData : manager.getAllData()) {
                    boolean hasPatternOverride = pData.pattern != null && !pData.pattern.equals(ParticlePatterns.DEFAULT);
                    boolean isCustomized = pData.hasColors() || hasPatternOverride;

                    if (isCustomized) {
                        if (isPatternChanged && (pData.pattern == null || pData.pattern.equals(ParticlePatterns.DEFAULT))) {
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

                for (ServerLevel level : player.getServer().getAllLevels()) {
                    if (level == null) {
                        continue;
                    }
                    String dimensionKey = PillarIdManager.getDimensionKey(level);

                    for (PillarIdManager.PillarData pData : manager.getAllData()) {
                        if (pData == null || !pData.dimension.equals(dimensionKey)) {
                            continue;
                        }

                        try {
                            BlockPos pos = pData.getBlockPos();
                            if (!level.hasChunkAt(pos)) {
                                continue;
                            }

                            BlockEntity be = level.getBlockEntity(pos);
                            if (be instanceof PillarBlockEntity pillarBE) {
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
