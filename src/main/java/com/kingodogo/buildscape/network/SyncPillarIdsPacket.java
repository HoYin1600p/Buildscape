package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.network.ClientPacketHandlers;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Server-to-client transfer of the pillar registry, cut into chunks of 64 pillars that share a sync id.
 * The client collects the chunks per sync id, ignores older syncs, discards a sync that is inconsistent, too large or
 * older than 30 seconds, and replaces its pillar list only once every chunk of the newest sync has arrived.
 */
public class SyncPillarIdsPacket {

    private static final int PILLARS_PER_CHUNK = 64;
    private static final int MAX_BUFFERED_ENTRIES = 65_536;
    private static final int MAX_CHUNKS = MAX_BUFFERED_ENTRIES / PILLARS_PER_CHUNK;
    private static final int MAX_BUFFERED_BYTES = 16 * 1024 * 1024;
    private static final long ACCUMULATOR_TIMEOUT_MS = 30_000L;
    private static final AtomicLong NEXT_SYNC_ID = new AtomicLong();
    private static final Map<Long, SyncAccumulator> CLIENT_ACCUMULATORS = new HashMap<>();
    private static long latestClientSyncId = Long.MIN_VALUE;
    private static boolean clientSyncDiscarded;

    private final long syncId;
    private final int chunkIndex;
    private final int chunkCount;
    private final List<PillarIdManager.PillarData> pillarDataList;
    private final int encodedSize;

    private SyncPillarIdsPacket(long syncId, int chunkIndex, int chunkCount,
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

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(chunkCount, MAX_CHUNKS, "pillar sync chunk");
        if (chunkCount < 1 || chunkIndex < 0 || chunkIndex >= chunkCount) {
            throw new EncoderException("invalid pillar sync chunk metadata");
        }
        NetworkPacketLimits.checkCount(pillarDataList.size(), PILLARS_PER_CHUNK, "pillar sync chunk");
        buf.writeLong(syncId);
        buf.writeInt(chunkCount);
        buf.writeInt(chunkIndex);
        buf.writeInt(pillarDataList.size());
        for (PillarIdManager.PillarData data : pillarDataList) {
            writePillarData(buf, data);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> acceptClientChunk(this)));
        ctx.get().setPacketHandled(true);
    }

    /**
     * Chunks the list and sends it to every connected player. Sends nothing when the list is over the 65,536 entry limit.
     */
    public static void sendToAll(List<PillarIdManager.PillarData> data) {
        for (SyncPillarIdsPacket packet : createChunks(data)) {
            ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
        }
    }

    /**
     * Chunks the list and sends it to one player, skipping a disconnected one.
     */
    public static void sendToPlayer(ServerPlayer player, List<PillarIdManager.PillarData> data) {
        if (player == null || player.hasDisconnected()) {
            return;
        }
        for (SyncPillarIdsPacket packet : createChunks(data)) {
            ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    /**
     * Client side: forgets partly received syncs, for example when leaving a world.
     */
    public static void clearClientState() {
        synchronized (CLIENT_ACCUMULATORS) {
            CLIENT_ACCUMULATORS.clear();
            latestClientSyncId = Long.MIN_VALUE;
            clientSyncDiscarded = false;
        }
    }

    private static List<SyncPillarIdsPacket> createChunks(List<PillarIdManager.PillarData> data) {
        List<PillarIdManager.PillarData> snapshot = new ArrayList<>();
        int skipped = 0;
        if (data != null) {
            for (PillarIdManager.PillarData entry : data) {
                if (canEncode(entry)) {
                    snapshot.add(entry);
                } else {
                    skipped++;
                }
            }
        }
        if (skipped > 0) {
            BuildScape.LOGGER.warn("Skipped {} invalid pillar sync entries", skipped);
        }
        if (snapshot.size() > MAX_BUFFERED_ENTRIES) {
            BuildScape.LOGGER.warn("Pillar sync exceeds the {} entry limit", MAX_BUFFERED_ENTRIES);
            return List.of();
        }
        int chunkCount = (int) Math.max(1L, (snapshot.size() + (long) PILLARS_PER_CHUNK - 1) / PILLARS_PER_CHUNK);
        long syncId = NEXT_SYNC_ID.incrementAndGet();
        List<SyncPillarIdsPacket> packets = new ArrayList<>(chunkCount);
        for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
            int from = chunkIndex * PILLARS_PER_CHUNK;
            int to = Math.min(snapshot.size(), from + PILLARS_PER_CHUNK);
            packets.add(new SyncPillarIdsPacket(syncId, chunkIndex, chunkCount, snapshot.subList(from, to)));
        }
        return packets;
    }

    private static void acceptClientChunk(SyncPillarIdsPacket packet) {
        List<PillarIdManager.PillarData> complete = null;
        long now = System.currentTimeMillis();
        synchronized (CLIENT_ACCUMULATORS) {
            expireClientState();
            if (packet.syncId < latestClientSyncId) {
                return;
            }
            if (packet.syncId > latestClientSyncId) {
                CLIENT_ACCUMULATORS.clear();
                latestClientSyncId = packet.syncId;
                clientSyncDiscarded = false;
            }
            if (clientSyncDiscarded) {
                return;
            }
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

        ClientPacketHandlers.replacePillarsFromServer(complete);
    }

    private static void expireClientState() {
        long now = System.currentTimeMillis();
        synchronized (CLIENT_ACCUMULATORS) {
            if (CLIENT_ACCUMULATORS.entrySet()
                    .removeIf(entry -> now - entry.getValue().createdAt > ACCUMULATOR_TIMEOUT_MS)) {
                clientSyncDiscarded = true;
            }
        }
    }

    @Mod.EventBusSubscriber(modid = BuildScape.MODID, value = Dist.CLIENT)
    public static final class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                expireClientState();
            }
        }
    }

    private static PillarIdManager.PillarData readPillarData(FriendlyByteBuf buf) {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
        data.dimension = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH,
                "pillar dimension");
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

    public static boolean canEncode(PillarIdManager.PillarData data) {
        ByteBuf scratch = Unpooled.buffer();
        try {
            writePillarData(new FriendlyByteBuf(scratch), data);
            return true;
        } catch (RuntimeException e) {
            return false;
        } finally {
            scratch.release();
        }
    }

    private static void writePillarData(FriendlyByteBuf buf, PillarIdManager.PillarData data) {
        if (data == null || data.id == null || data.dimension == null) {
            throw new EncoderException("pillar data is incomplete");
        }
        NetworkPacketLimits.writeUtf(buf, data.id, NetworkPacketLimits.MAX_PILLAR_ID_LENGTH, "pillar id");
        NetworkPacketLimits.writeUtf(buf, data.dimension, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH,
                "pillar dimension");
        buf.writeInt(data.x);
        buf.writeInt(data.y);
        buf.writeInt(data.z);
        buf.writeLong(data.createdTime);
        buf.writeLong(data.modifiedTime);

        List<String> colors = data.dyeColors != null ? data.dyeColors : Collections.emptyList();
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
            throw new EncoderException("pillar numeric value must be finite");
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
            throw new DecoderException("pillar item yaw must be finite");
        }
        return value;
    }

    private static void writeNullableFloat(FriendlyByteBuf buf, Float value) {
        if (value != null && !Float.isFinite(value)) {
            throw new EncoderException("pillar item yaw must be finite");
        }
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeFloat(value);
        }
    }

    private static final class SyncAccumulator {
        private final int chunkCount;
        private final long createdAt;
        private final Map<Integer, List<PillarIdManager.PillarData>> chunks;
        private int received;
        private int bufferedEntries;
        private int bufferedBytes;

        private SyncAccumulator(int chunkCount, long createdAt) {
            this.chunkCount = chunkCount;
            this.createdAt = createdAt;
            this.chunks = new HashMap<>();
        }

        private boolean add(int index, List<PillarIdManager.PillarData> data, int bytes) {
            if (chunks.containsKey(index)) {
                return true;
            }
            if (bufferedEntries + data.size() > MAX_BUFFERED_ENTRIES
                    || bufferedBytes + bytes > MAX_BUFFERED_BYTES) {
                return false;
            }
            received++;
            bufferedEntries += data.size();
            bufferedBytes += bytes;
            chunks.put(index, new ArrayList<>(data));
            return true;
        }

        private void add(int index, List<PillarIdManager.PillarData> data) {
            if (!add(index, data, 0)) {
                throw new DecoderException("pillar sync exceeds buffer limits");
            }
        }

        private boolean isComplete() {
            return received == chunkCount;
        }

        private List<PillarIdManager.PillarData> combine() {
            List<PillarIdManager.PillarData> combined = new ArrayList<>();
            for (int i = 0; i < chunkCount; i++) {
                combined.addAll(chunks.get(i));
            }
            return combined;
        }
    }
}
