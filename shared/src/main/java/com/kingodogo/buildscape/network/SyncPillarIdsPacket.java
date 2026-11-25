package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SyncPillarIdsPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "sync_pillar_ids");

    private static final int PILLARS_PER_CHUNK = 64;
    private static final int MAX_SYNC_PILLARS = 65_536;
    private static final int MAX_CHUNKS = MAX_SYNC_PILLARS;
    private static final int MAX_CHUNK_BYTES = 28_000;
    private static final int MAX_BUFFERED_BYTES = 16 * 1024 * 1024;
    private static final long ACCUMULATOR_TIMEOUT_MS = 30_000L;
    private static final AtomicLong NEXT_SYNC_ID = new AtomicLong();
    private static final Map<Long, SyncAccumulator> CLIENT_ACCUMULATORS = new HashMap<>();
    private static long latestClientSyncId = Long.MIN_VALUE;
    private static boolean clientSyncDiscarded;

    public static void clearClientState() {
        synchronized (CLIENT_ACCUMULATORS) {
            CLIENT_ACCUMULATORS.clear();
            latestClientSyncId = Long.MIN_VALUE;
            clientSyncDiscarded = false;
        }
    }

    private final long syncId;
    private final int chunkIndex;
    private final int chunkCount;
    private final List<PillarIdManager.PillarData> pillarDataList;
    private final int encodedSize;

    public SyncPillarIdsPacket(long syncId, int chunkIndex, int chunkCount,
                               Collection<PillarIdManager.PillarData> pillarDataList) {
        this.syncId = syncId;
        this.chunkIndex = chunkIndex;
        this.chunkCount = chunkCount;
        this.pillarDataList = new ArrayList<>(pillarDataList);
        this.encodedSize = 0;
    }

    public SyncPillarIdsPacket(FriendlyByteBuf buf) {
        int start = buf.readerIndex();
        this.syncId = buf.readLong();
        this.chunkCount = NetworkPacketLimits.readBoundedInt(buf, 1, MAX_CHUNKS, "pillar sync chunk count");
        this.chunkIndex = NetworkPacketLimits.readBoundedInt(buf, 0, chunkCount - 1, "pillar sync chunk index");
        int count = NetworkPacketLimits.readCount(buf, PILLARS_PER_CHUNK, "pillar sync chunk");
        this.pillarDataList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.pillarDataList.add(readPillarData(buf));
        }
        this.encodedSize = buf.readerIndex() - start;
    }

    public static SyncPillarIdsPacket decode(FriendlyByteBuf buf) {
        return new SyncPillarIdsPacket(buf);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.SERVER_TO_CLIENT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(chunkCount, MAX_CHUNKS, "pillar sync chunk");
        if (chunkCount < 1 || chunkIndex < 0 || chunkIndex >= chunkCount) {
            throw new IllegalArgumentException("pillar synchronization chunk bounds are invalid: "
                    + chunkIndex + "/" + chunkCount);
        }

        buf.writeLong(syncId);
        buf.writeInt(chunkCount);
        buf.writeInt(chunkIndex);

        NetworkPacketLimits.checkCount(pillarDataList.size(), PILLARS_PER_CHUNK, "pillar sync chunk");
        buf.writeInt(pillarDataList.size());
        for (PillarIdManager.PillarData data : pillarDataList) {
            writePillarData(buf, data);
        }
    }

    public static void sendToPlayer(ServerPlayer player, List<PillarIdManager.PillarData> data) {
        for (SyncPillarIdsPacket packet : split(data)) {
            PacketFactory.sendToPlayer(player, packet);
        }
    }

    public static void sendToAll(List<PillarIdManager.PillarData> data) {
        for (SyncPillarIdsPacket packet : split(data)) {
            PacketFactory.sendToAll(packet);
        }
    }

    public static List<SyncPillarIdsPacket> split(List<PillarIdManager.PillarData> data) {
        List<List<PillarIdManager.PillarData>> chunks = new ArrayList<>();
        List<PillarIdManager.PillarData> current = new ArrayList<>();
        int currentBytes = Long.BYTES + 3 * Integer.BYTES;
        int entries = 0;
        int skipped = 0;
        if (data != null) {
            for (PillarIdManager.PillarData entry : data) {
                int size = encodedEntrySize(entry);
                if (size < 0 || size + Long.BYTES + 3 * Integer.BYTES > MAX_CHUNK_BYTES) {
                    skipped++;
                    continue;
                }
                if (!current.isEmpty() && (current.size() == PILLARS_PER_CHUNK || currentBytes + size > MAX_CHUNK_BYTES)) {
                    chunks.add(current);
                    current = new ArrayList<>();
                    currentBytes = Long.BYTES + 3 * Integer.BYTES;
                }
                current.add(entry);
                currentBytes += size;
                entries++;
                if (entries > MAX_SYNC_PILLARS) {
                    com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Pillar sync exceeds the {} entry limit", MAX_SYNC_PILLARS);
                    return List.of();
                }
            }
        }
        if (skipped > 0) com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Skipped {} invalid pillar sync entries", skipped);
        if (!current.isEmpty() || chunks.isEmpty()) chunks.add(current);
        int chunkCount = chunks.size();
        long syncId = NEXT_SYNC_ID.incrementAndGet();
        List<SyncPillarIdsPacket> packets = new ArrayList<>(chunkCount);
        for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
            packets.add(new SyncPillarIdsPacket(syncId, chunkIndex, chunkCount, chunks.get(chunkIndex)));
        }
        return packets;
    }

    private static int encodedEntrySize(PillarIdManager.PillarData data) {
        var buffer = io.netty.buffer.Unpooled.buffer();
        try {
            writePillarData(new FriendlyByteBuf(buffer), data);
            return buffer.readableBytes();
        } catch (RuntimeException exception) {
            return -1;
        } finally {
            buffer.release();
        }
    }

    @Override
    public void handle(Player player) {
        acceptClientChunk(this);
    }

    private static void acceptClientChunk(SyncPillarIdsPacket packet) {
        List<PillarIdManager.PillarData> complete = null;
        long now = System.currentTimeMillis();
        synchronized (CLIENT_ACCUMULATORS) {
            expireClientState(now);
            if (packet.syncId < latestClientSyncId) return;
            if (packet.syncId > latestClientSyncId) {
                CLIENT_ACCUMULATORS.clear();
                latestClientSyncId = packet.syncId;
                clientSyncDiscarded = false;
            }
            if (clientSyncDiscarded) return;
            SyncAccumulator existing = CLIENT_ACCUMULATORS.get(packet.syncId);
            if (existing != null && existing.chunkCount != packet.chunkCount) {
                CLIENT_ACCUMULATORS.clear();
                clientSyncDiscarded = true;
                return;
            }
            SyncAccumulator accumulator = CLIENT_ACCUMULATORS.computeIfAbsent(packet.syncId,
                    id -> new SyncAccumulator(packet.chunkCount, now));
            if (!accumulator.add(packet.chunkIndex, packet.pillarDataList, packet.encodedSize)) {
                CLIENT_ACCUMULATORS.clear();
                clientSyncDiscarded = true;
                return;
            }
            if (accumulator.isComplete()) {
                complete = accumulator.combine();
                CLIENT_ACCUMULATORS.remove(packet.syncId);
                clientSyncDiscarded = true;
            }
        }
        if (complete == null) {
            return;
        }

        PillarIdManager.getClient().replaceFromServerSync(complete);
        try {
            com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.onPillarsSynced();
        } catch (Throwable ignored) {}
    }

    public static void expireClientState() {
        synchronized (CLIENT_ACCUMULATORS) {
            expireClientState(System.currentTimeMillis());
        }
    }

    private static void expireClientState(long now) {
        if (CLIENT_ACCUMULATORS.entrySet().removeIf(entry -> now - entry.getValue().createdAt > ACCUMULATOR_TIMEOUT_MS)) {
            clientSyncDiscarded = true;
        }
    }

    private static PillarIdManager.PillarData readPillarData(FriendlyByteBuf buf) {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
        data.dimension = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar dimension");
        data.x = buf.readInt();
        data.y = buf.readInt();
        data.z = buf.readInt();
        data.createdTime = buf.readLong();
        data.modifiedTime = buf.readLong();

        int colorCount = NetworkPacketLimits.readCount(buf, NetworkPacketLimits.MAX_DYE_COLORS, "pillar color");
        data.dyeColors = new ArrayList<>(colorCount);
        for (int i = 0; i < colorCount; i++) {
            data.dyeColors.add(NetworkPacketLimits.readUtf(
                    buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar color"));
        }

        data.use_pattern = readNullableBoolean(buf);
        data.pattern = readNullableString(buf, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pillar pattern");
        data.pattern_speed = readNullableDouble(buf, "pillar pattern speed");
        data.pattern_spread = readNullableDouble(buf, "pillar pattern spread");
        data.pattern_intensity = readNullableDouble(buf, "pillar pattern intensity");
        data.max_particle_color = readNullableInt(buf);
        data.displayedItem = readNullableString(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "displayed item");
        data.pillarType = readNullableString(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar type");
        data.facing = readNullableString(buf, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pillar facing");
        data.itemYaw = readNullableFloat(buf);
        return data;
    }

    private static void writePillarData(FriendlyByteBuf buf, PillarIdManager.PillarData data) {
        if (data == null || data.id == null || data.dimension == null) {
            throw new IllegalArgumentException("pillar data is incomplete");
        }
        NetworkPacketLimits.writeUtf(buf, data.id, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
        NetworkPacketLimits.writeUtf(buf, data.dimension, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar dimension");
        buf.writeInt(data.x);
        buf.writeInt(data.y);
        buf.writeInt(data.z);
        buf.writeLong(data.createdTime);
        buf.writeLong(data.modifiedTime);

        List<String> colors = data.dyeColors != null ? data.dyeColors : java.util.Collections.emptyList();
        NetworkPacketLimits.checkCount(colors.size(), NetworkPacketLimits.MAX_DYE_COLORS, "pillar color");
        buf.writeInt(colors.size());
        for (String color : colors) {
            NetworkPacketLimits.writeUtf(buf, color, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar color");
        }

        writeNullableBoolean(buf, data.use_pattern);
        writeNullableString(buf, data.pattern, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pillar pattern");
        writeNullableDouble(buf, data.pattern_speed);
        writeNullableDouble(buf, data.pattern_spread);
        writeNullableDouble(buf, data.pattern_intensity);
        writeNullableInt(buf, data.max_particle_color);
        writeNullableString(buf, data.displayedItem, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "displayed item");
        writeNullableString(buf, data.pillarType, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar type");
        writeNullableString(buf, data.facing, NetworkPacketLimits.MAX_PATTERN_LENGTH, "pillar facing");
        writeNullableFloat(buf, data.itemYaw);
    }

    private static String readNullableString(FriendlyByteBuf buf, int maximum, String field) {
        return buf.readBoolean() ? NetworkPacketLimits.readUtf(buf, maximum, field) : null;
    }

    private static void writeNullableString(FriendlyByteBuf buf, String value, int maximum, String field) {
        buf.writeBoolean(value != null);
        if (value != null) {
            NetworkPacketLimits.writeUtf(buf, value, maximum, field);
        }
    }

    private static Boolean readNullableBoolean(FriendlyByteBuf buf) {
        return buf.readBoolean() ? buf.readBoolean() : null;
    }

    private static void writeNullableBoolean(FriendlyByteBuf buf, Boolean value) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeBoolean(value);
        }
    }

    private static Double readNullableDouble(FriendlyByteBuf buf, String field) {
        return buf.readBoolean() ? NetworkPacketLimits.readFiniteDouble(buf, field) : null;
    }

    private static void writeNullableDouble(FriendlyByteBuf buf, Double value) {
        if (value != null && !Double.isFinite(value)) {
            throw new IllegalArgumentException("pillar numeric value must be finite");
        }
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeDouble(value);
        }
    }

    private static Integer readNullableInt(FriendlyByteBuf buf) {
        return buf.readBoolean() ? buf.readInt() : null;
    }

    private static void writeNullableInt(FriendlyByteBuf buf, Integer value) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeInt(value);
        }
    }

    private static Float readNullableFloat(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return null;
        }
        float value = buf.readFloat();
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException("pillar item yaw must be finite");
        }
        return value;
    }

    private static void writeNullableFloat(FriendlyByteBuf buf, Float value) {
        if (value != null && !Float.isFinite(value)) {
            throw new IllegalArgumentException("pillar item yaw must be finite");
        }
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeFloat(value);
        }
    }

    private static final class SyncAccumulator {
        private final int chunkCount;
        private final long createdAt;
        private final List<List<PillarIdManager.PillarData>> chunks;
        private int received;
        private int bufferedEntries;
        private int bufferedBytes;

        private SyncAccumulator(int chunkCount, long createdAt) {
            this.chunkCount = chunkCount;
            this.createdAt = createdAt;
            this.chunks = new ArrayList<>(java.util.Collections.nCopies(chunkCount, null));
        }

        private boolean add(int index, List<PillarIdManager.PillarData> data, int bytes) {
            if (chunks.get(index) != null) return true;
            if (bufferedEntries + data.size() > MAX_SYNC_PILLARS
                    || bufferedBytes + bytes > MAX_BUFFERED_BYTES) return false;
            received++;
            bufferedEntries += data.size();
            bufferedBytes += bytes;
            chunks.set(index, new ArrayList<>(data));
            return true;
        }

        private boolean isComplete() {
            return received == chunkCount;
        }

        private List<PillarIdManager.PillarData> combine() {
            List<PillarIdManager.PillarData> combined = new ArrayList<>();
            for (List<PillarIdManager.PillarData> chunk : chunks) {
                combined.addAll(chunk);
            }
            if (combined.size() > MAX_SYNC_PILLARS) {
                throw new IllegalArgumentException("pillar synchronization exceeds the maximum entry count");
            }
            return combined;
        }
    }
}
