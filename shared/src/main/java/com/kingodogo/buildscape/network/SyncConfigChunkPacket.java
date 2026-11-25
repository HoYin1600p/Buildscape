package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.util.CommonId;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SyncConfigChunkPacket implements CommonPacket {
    public static final CommonId ID = new CommonId("buildscape", "sync_config_part_v2");
    private static final int CHUNK_BYTES = 24_000;
    private static final int MAX_SYNC_BYTES = NetworkPacketLimits.MAX_CONFIG_ITEMS
            * (NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH + 5)
            + NetworkPacketLimits.MAX_DYE_COLORS * (NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH + 5) + 4096;
    private static final int MAX_CHUNKS = (MAX_SYNC_BYTES + CHUNK_BYTES - 1) / CHUNK_BYTES;
    private static Assembly pending;

    private final UUID transferId;
    private final int index;
    private final int count;
    private final byte[] payload;

    private SyncConfigChunkPacket(UUID transferId, int index, int count, byte[] payload) {
        this.transferId = transferId;
        this.index = index;
        this.count = count;
        this.payload = payload;
    }

    public SyncConfigChunkPacket(FriendlyByteBuf buffer) {
        transferId = buffer.readUUID();
        index = NetworkPacketLimits.readBoundedInt(buffer, 0, MAX_CHUNKS - 1, "config sync index");
        count = NetworkPacketLimits.readBoundedInt(buffer, 1, MAX_CHUNKS, "config sync count");
        payload = buffer.readByteArray(CHUNK_BYTES);
        if (index >= count || payload.length == 0 || index < count - 1 && payload.length != CHUNK_BYTES) {
            throw new IllegalArgumentException("Invalid config synchronization chunk");
        }
    }

    public static SyncConfigChunkPacket decode(FriendlyByteBuf buffer) {
        return new SyncConfigChunkPacket(buffer);
    }

    public static List<SyncConfigChunkPacket> split(SyncConfigPacket config) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            config.write(buffer);
            if (buffer.readableBytes() > MAX_SYNC_BYTES) {
                throw new IllegalArgumentException("Config synchronization exceeds maximum size");
            }
            int count = (buffer.readableBytes() + CHUNK_BYTES - 1) / CHUNK_BYTES;
            UUID transferId = UUID.randomUUID();
            List<SyncConfigChunkPacket> parts = new ArrayList<>(count);
            for (int index = 0; index < count; index++) {
                byte[] bytes = new byte[Math.min(CHUNK_BYTES, buffer.readableBytes())];
                buffer.readBytes(bytes);
                parts.add(new SyncConfigChunkPacket(transferId, index, count, bytes));
            }
            return parts;
        } finally {
            buffer.release();
        }
    }

    @Override
    public CommonId getId() { return ID; }

    @Override
    public PacketDirection getDirection() { return PacketDirection.SERVER_TO_CLIENT; }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(transferId);
        buffer.writeInt(index);
        buffer.writeInt(count);
        buffer.writeByteArray(payload);
    }

    public static synchronized void clearPending() {
        pending = null;
    }

    private static synchronized SyncConfigPacket receive(SyncConfigChunkPacket packet) {
        if (packet.index == 0) pending = new Assembly();
        if (pending == null) throw new IllegalArgumentException("Missing config synchronization start");
        SyncConfigPacket complete = pending.accept(packet, System.currentTimeMillis());
        if (complete != null) pending = null;
        return complete;
    }

    @Override
    public void handle(Player player) {
        try {
            SyncConfigPacket complete = receive(this);
            if (complete != null) complete.handle(player);
        } catch (RuntimeException e) {
            clearPending();
            BuildscapeCommon.LOGGER.warn("Rejected incomplete or invalid config synchronization", e);
        }
    }

    static final class Assembly {
        private UUID transferId;
        private int count;
        private int next;
        private long started;
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        SyncConfigPacket accept(SyncConfigChunkPacket packet, long now) {
            if (transferId == null) {
                if (packet.index != 0) throw new IllegalArgumentException("Missing config synchronization start");
                transferId = packet.transferId;
                count = packet.count;
                started = now;
            }
            if (!transferId.equals(packet.transferId) || count != packet.count || next != packet.index
                    || now - started > 30_000L || bytes.size() + packet.payload.length > MAX_SYNC_BYTES) {
                throw new IllegalArgumentException("Incomplete or oversized config synchronization");
            }
            bytes.write(packet.payload, 0, packet.payload.length);
            if (++next != count) return null;
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes.toByteArray()));
            try {
                SyncConfigPacket config = new SyncConfigPacket(buffer);
                if (buffer.isReadable()) throw new IllegalArgumentException("Trailing config synchronization data");
                return config;
            } finally {
                buffer.release();
            }
        }
    }
}
