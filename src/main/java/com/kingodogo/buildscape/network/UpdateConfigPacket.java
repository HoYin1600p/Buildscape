package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
public class UpdateConfigPacket implements CommonPacket {
    public static final CommonId ID = new CommonId("buildscape", "update_config_v2");

    private static final int CHUNK_BYTES = 24_000;
    private static final int MAX_UPDATE_BYTES = 2 * 1024 * 1024;
    private static final int MAX_CHUNKS = (MAX_UPDATE_BYTES + CHUNK_BYTES - 1) / CHUNK_BYTES;

    private final UUID transferId;
    private final int chunkIndex;
    private final int chunkCount;
    private final byte[] payload;

    public UUID getTransferId() {
        return transferId;
    }
    public int getChunkIndex() {
        return chunkIndex;
    }
    public int getChunkCount() {
        return chunkCount;
    }
    public byte[] getPayload() {
        return payload;
    }

    private UpdateConfigPacket(UUID transferId, int chunkIndex, int chunkCount, byte[] payload) {
        this.transferId = transferId;
        this.chunkIndex = chunkIndex;
        this.chunkCount = chunkCount;
        this.payload = payload;
    }

    public static List<UpdateConfigPacket> split(PillarParticleConfig config) {
        return split(config, ConfigSetting.uploadable());
    }

    /**
     * Encodes the chosen setting groups of {@code config} and cuts them into ordered chunks.
     * Throws {@link IllegalArgumentException} when the upload exceeds 2 MiB.
     */
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
        Player player = Services.PLATFORM.getClientPlayer();
        boolean levelTwo = player != null && Services.PLATFORM.hasPermission(player, 2);
        boolean levelFour = player != null && Services.PLATFORM.hasPermission(player, 4);
        for (UpdateConfigPacket packet : splitForClient(config,
                PillarParticleConfig.getClientPermissionLevel(), levelTwo, levelFour)) {
            PacketFactory.sendToServer(packet);
        }
    }
    public static List<UpdateConfigPacket> splitForClient(PillarParticleConfig config, int syncedLevel,
            boolean levelTwo,
            boolean levelFour) {
        int level = PillarParticleConfig.resolveClientPermissionLevel(syncedLevel, levelTwo, levelFour);
        Set<ConfigSetting> settings = ConfigSetting.allowed(config.edit_permission_mode, level);
        settings.removeIf(setting -> setting.itemsFile ? !config.canEditItems() : !config.canEditProperties());
        return split(config, settings);
    }

    public UpdateConfigPacket(FriendlyByteBuf buf) {
        this.transferId = buf.readUUID();
        this.chunkIndex = NetworkPacketLimits.readBoundedInt(buf, 0, MAX_CHUNKS - 1, "config chunk index");
        this.chunkCount = NetworkPacketLimits.readBoundedInt(buf, 1, MAX_CHUNKS, "config chunk count");
        this.payload = buf.readByteArray(CHUNK_BYTES);
        if (chunkIndex >= chunkCount || payload.length == 0
                || (chunkIndex < chunkCount - 1 && payload.length != CHUNK_BYTES)) {
            throw new IllegalArgumentException("Invalid config chunk");
        }
    }

    public static UpdateConfigPacket decode(FriendlyByteBuf buf) {
        return new UpdateConfigPacket(buf);
    }

    public void write(FriendlyByteBuf buf) {
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

    /**
     * Copies the allowed groups from {@code data} into the server config and trims {@code data.uploadSettings} to the groups
     * that were accepted. Returns a refusal message for the last refused group, or null when everything was accepted.
     */
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
        if (!accepted.isEmpty()) {
            boolean applied = serverConfig.applyUpload(data, accepted);
            if (!applied && refusal == null) {
                refusal = "BuildScape: Config update refused because your configuration is out of date. Please resync.";
            }
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

    @Override
    public CommonId getId() { return ID; }

    @Override
    public PacketDirection getDirection() { return PacketDirection.CLIENT_TO_SERVER; }

    private static void refuse(ServerPlayer player, String reason, PillarParticleConfig config) {
        Services.PLATFORM.sendActionBarMessage(player, ComponentHelper.literal(reason));
        PacketFactory.sendToPlayer(player, new EditRefusedPacket(null));
        PacketFactory.sendToPlayer(player, new SyncConfigPacket(config, player));
    }

    @Override
    public void handle(Player sender) {
        if (!(sender instanceof ServerPlayer serverPlayer)) return;
        PillarParticleConfig serverConfig = PillarParticleConfig.getServerConfig();
        int permissionLevel = Services.PLATFORM.hasPermission(serverPlayer, 4) ? 4
                : Services.PLATFORM.hasPermission(serverPlayer, 2) ? 2 : 0;
        ConfigAssembly assembly = pendingAssembly(ServerPendingHolder.PENDING, serverPlayer, this,
                serverConfig.edit_permission_mode, permissionLevel);
        if (assembly == null) {
            refuse(serverPlayer, "BuildScape: This setting is locked by the server edit policy.", serverConfig);
            return;
        }
        SyncConfigPacket data;
        try {
            data = assembly.accept(this);
        } catch (RuntimeException e) {
            ServerPendingHolder.PENDING.remove(serverPlayer);
            BuildscapeCommon.LOGGER.warn("Rejected config update from {}", serverPlayer.getUUID(), e);
            refuse(serverPlayer, "BuildScape: Invalid config update refused.", serverConfig);
            return;
        }
        if (data == null) return;
        ServerPendingHolder.PENDING.remove(serverPlayer);
        String oldPattern = serverConfig.pattern;
        String refusal = applyValues(serverConfig, data, permissionLevel);
        if (refusal != null) refuse(serverPlayer, refusal, serverConfig);
        if (data.uploadSettings.isEmpty()) {
            if (refusal == null) PacketFactory.sendToPlayer(serverPlayer, new SyncConfigPacket(serverConfig, serverPlayer));
            return;
        }
        boolean propertiesSaved = data.uploadSettings.stream().noneMatch(setting -> !setting.itemsFile)
                || serverConfig.savePropertiesToDisk();
        boolean itemsSaved = !data.uploadSettings.contains(ConfigSetting.ITEMS) || serverConfig.saveItemsToDisk();
        if (!propertiesSaved || !itemsSaved) {
            Services.PLATFORM.sendActionBarMessage(serverPlayer,
                    ComponentHelper.literal("BuildScape: Changes are active but could not be saved: "
                            + serverConfig.getSaveFailureReason()));
        }
        PacketFactory.sendToAll(new SyncConfigPacket(serverConfig));
        if (!data.uploadSettings.contains(ConfigSetting.PARTICLES)
                && !data.uploadSettings.contains(ConfigSetting.COLORS)) return;
        PillarIdManager manager = PillarIdManager.get();
        if (manager.hasLoaded()) {
            boolean updatedAny = false;
            boolean isPatternChanged = !serverConfig.pattern.equals(oldPattern);

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
