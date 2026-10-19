package com.kingodogo.buildscape.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ConfigAssembly {
    private static final int MAX_UPDATE_BYTES = 2 * 1024 * 1024;
    private UUID transferId;
    private int chunkCount;
    private int nextIndex;
    private long started;
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    public long getStarted() {
        return started;
    }

    public SyncConfigPacket accept(UpdateConfigPacket packet) {
        long now = System.currentTimeMillis();
        if (nextIndex == 0) {
            transferId = packet.getTransferId();
            chunkCount = packet.getChunkCount();
            started = now;
        }
        if (!transferId.equals(packet.getTransferId()) || chunkCount != packet.getChunkCount()
                || nextIndex != packet.getChunkIndex() || now - started > 30_000L
                || bytes.size() + packet.getPayload().length > MAX_UPDATE_BYTES) {
            throw new IllegalArgumentException("Incomplete or oversized config update");
        }
        bytes.write(packet.getPayload(), 0, packet.getPayload().length);
        nextIndex++;
        if (nextIndex != chunkCount) {
            return null;
        }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes.toByteArray()));
        try {
            SyncConfigPacket data = readUpload(buffer);
            if (buffer.isReadable()) {
                throw new IllegalArgumentException("Trailing config update data");
            }
            return data;
        } finally {
            buffer.release();
        }
    }

    static void writeUpload(FriendlyByteBuf buffer, SyncConfigPacket data, java.util.Set<ConfigSetting> settings) {
        buffer.writeInt(ConfigSetting.mask(settings));
        if (settings.contains(ConfigSetting.ITEMS)) {
            NetworkPacketLimits.checkCount(data.items.size(), NetworkPacketLimits.MAX_CONFIG_ITEMS, "configured item");
            buffer.writeInt(data.items.size());
            for (String item : data.items) {
                NetworkPacketLimits.writeUtf(buffer, item, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "configured item");
            }
        }
        if (settings.contains(ConfigSetting.COLORS)) {
            NetworkPacketLimits.checkCount(data.particle_color.size(), NetworkPacketLimits.MAX_DYE_COLORS, "particle color");
            buffer.writeInt(data.particle_color.size());
            for (String color : data.particle_color) {
                NetworkPacketLimits.writeUtf(buffer, color, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "particle color");
            }
            buffer.writeInt(data.max_particle_color);
        }
        if (settings.contains(ConfigSetting.PARTICLES)) {
            buffer.writeDouble(data.particle_speed);
            buffer.writeDouble(data.particle_spread);
            buffer.writeInt(data.particle_lifetime);
            buffer.writeInt(data.particle_density);
            buffer.writeBoolean(data.use_pattern);
            NetworkPacketLimits.writeUtf(buffer, data.pattern, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern");
            buffer.writeDouble(data.pattern_speed);
            buffer.writeDouble(data.pattern_spread);
            buffer.writeDouble(data.pattern_intensity);
        }
        if (settings.contains(ConfigSetting.POLICY)) {
            NetworkPacketLimits.writeUtf(buffer, data.edit_permission_mode.name(), 16, "edit permission mode");
        }
    }

    private static SyncConfigPacket readUpload(FriendlyByteBuf buffer) {
        SyncConfigPacket data = new SyncConfigPacket();
        data.uploadSettings = ConfigSetting.fromMask(buffer.readInt());
        if (data.uploadSettings.contains(ConfigSetting.ITEMS)) {
            int count = NetworkPacketLimits.readCount(buffer, NetworkPacketLimits.MAX_CONFIG_ITEMS, "configured item");
            data.items = new java.util.HashSet<>();
            for (int i = 0; i < count; i++) {
                data.items.add(NetworkPacketLimits.readUtf(buffer, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "configured item"));
            }
        }
        if (data.uploadSettings.contains(ConfigSetting.COLORS)) {
            int count = NetworkPacketLimits.readCount(buffer, NetworkPacketLimits.MAX_DYE_COLORS, "particle color");
            data.particle_color = new java.util.ArrayList<>();
            for (int i = 0; i < count; i++) {
                data.particle_color.add(NetworkPacketLimits.readUtf(buffer, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "particle color"));
            }
            data.max_particle_color = NetworkPacketLimits.readBoundedInt(buffer, 0, NetworkPacketLimits.MAX_DYE_COLORS, "maximum particle colors");
        }
        if (data.uploadSettings.contains(ConfigSetting.PARTICLES)) {
            data.particle_speed = NetworkPacketLimits.readBoundedDouble(buffer, NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPEED, "particle speed");
            data.particle_spread = NetworkPacketLimits.readBoundedDouble(buffer, NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPREAD, "particle spread");
            data.particle_lifetime = NetworkPacketLimits.readBoundedInt(buffer, 1, NetworkPacketLimits.MAX_PARTICLE_LIFETIME, "particle lifetime");
            data.particle_density = NetworkPacketLimits.readBoundedInt(buffer, 0, NetworkPacketLimits.MAX_PARTICLE_DENSITY, "particle density");
            data.use_pattern = buffer.readBoolean();
            data.pattern = NetworkPacketLimits.readUtf(buffer, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pattern");
            data.pattern_speed = NetworkPacketLimits.readBoundedDouble(buffer, NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPEED, "pattern speed");
            data.pattern_spread = NetworkPacketLimits.readBoundedDouble(buffer, NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_SPREAD, "pattern spread");
            data.pattern_intensity = NetworkPacketLimits.readBoundedDouble(buffer, NetworkPacketLimits.MIN_PARTICLE_RATE, NetworkPacketLimits.MAX_PARTICLE_INTENSITY, "pattern intensity");
        }
        if (data.uploadSettings.contains(ConfigSetting.POLICY)) {
            data.edit_permission_mode = PillarParticleConfig.parseEditPermissionMode(NetworkPacketLimits.readUtf(buffer, 16, "edit permission mode"));
        }
        return data;
    }

}
