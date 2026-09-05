package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class SyncPillarIdsPacket {

    private static final int PILLARS_PER_CHUNK = 64;
    private static final int MAX_CHUNKS = (NetworkPacketLimits.MAX_PILLARS + PILLARS_PER_CHUNK - 1)
            / PILLARS_PER_CHUNK;
    private static final long ACCUMULATOR_TIMEOUT_MS = 30_000L;
    private static final AtomicLong NEXT_SYNC_ID = new AtomicLong();
    private static final Map<Long, SyncAccumulator> CLIENT_ACCUMULATORS = new HashMap<>();

    private final long syncId;
    private final int chunkIndex;
    private final int chunkCount;
    private final List<PillarIdManager.PillarData> pillarDataList;

    private SyncPillarIdsPacket(long syncId, int chunkIndex, int chunkCount,
                                Collection<PillarIdManager.PillarData> pillarDataList) {
        this.syncId = syncId;
        this.chunkIndex = chunkIndex;
        this.chunkCount = chunkCount;
        this.pillarDataList = new ArrayList<>(pillarDataList);
    }

    public SyncPillarIdsPacket(FriendlyByteBuf buf) {
        this.syncId = buf.readLong();
        this.chunkCount = NetworkPacketLimits.readBoundedInt(buf, 1, MAX_CHUNKS, "pillar sync chunk count");
        this.chunkIndex = NetworkPacketLimits.readBoundedInt(buf, 0, chunkCount - 1, "pillar sync chunk index");
        int count = NetworkPacketLimits.readCount(buf, PILLARS_PER_CHUNK, "pillar sync chunk");
        this.pillarDataList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.pillarDataList.add(readPillarData(buf));
        }
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

    public static void sendToAll(List<PillarIdManager.PillarData> data) {
        for (SyncPillarIdsPacket packet : createChunks(data)) {
            ModMessages.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
        }
    }

    public static void sendToPlayer(ServerPlayer player, List<PillarIdManager.PillarData> data) {
        if (player == null || player.hasDisconnected()) {
            return;
        }
        for (SyncPillarIdsPacket packet : createChunks(data)) {
            ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    public static void clearClientState() {
        synchronized (CLIENT_ACCUMULATORS) {
            CLIENT_ACCUMULATORS.clear();
        }
    }

    private static List<SyncPillarIdsPacket> createChunks(List<PillarIdManager.PillarData> data) {
        List<PillarIdManager.PillarData> snapshot = data != null ? data : java.util.Collections.emptyList();
        NetworkPacketLimits.checkCount(snapshot.size(), NetworkPacketLimits.MAX_PILLARS, "pillar");
        int chunkCount = Math.max(1, (snapshot.size() + PILLARS_PER_CHUNK - 1) / PILLARS_PER_CHUNK);
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
            CLIENT_ACCUMULATORS.entrySet().removeIf(entry ->
                    now - entry.getValue().createdAt > ACCUMULATOR_TIMEOUT_MS);
            SyncAccumulator accumulator = CLIENT_ACCUMULATORS.compute(packet.syncId, (id, existing) ->
                    existing != null && existing.chunkCount == packet.chunkCount
                            ? existing
                            : new SyncAccumulator(packet.chunkCount, now));
            accumulator.add(packet.chunkIndex, packet.pillarDataList);
            if (accumulator.isComplete()) {
                complete = accumulator.combine();
                CLIENT_ACCUMULATORS.remove(packet.syncId);
            }
        }
        if (complete == null) {
            return;
        }

        PillarIdManager.getClient().replaceFromServerSync(complete);
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.screen instanceof com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen configScreen) {
            configScreen.refreshCurrentTab();
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
            throw new EncoderException("pillar data is incomplete");
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
        private final List<List<PillarIdManager.PillarData>> chunks;
        private int received;

        private SyncAccumulator(int chunkCount, long createdAt) {
            this.chunkCount = chunkCount;
            this.createdAt = createdAt;
            this.chunks = new ArrayList<>(java.util.Collections.nCopies(chunkCount, null));
        }

        private void add(int index, List<PillarIdManager.PillarData> data) {
            if (chunks.get(index) == null) {
                received++;
            }
            chunks.set(index, new ArrayList<>(data));
        }

        private boolean isComplete() {
            return received == chunkCount;
        }

        private List<PillarIdManager.PillarData> combine() {
            List<PillarIdManager.PillarData> combined = new ArrayList<>();
            for (List<PillarIdManager.PillarData> chunk : chunks) {
                combined.addAll(chunk);
            }
            if (combined.size() > NetworkPacketLimits.MAX_PILLARS) {
                throw new DecoderException("pillar synchronization exceeds the maximum entry count");
            }
            return combined;
        }
    }
}
