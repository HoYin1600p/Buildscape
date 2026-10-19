package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.network.EditPillarRowsPacket;
import com.kingodogo.buildscape.network.EditPillarRowsResultPacket;
import com.kingodogo.buildscape.network.RemovePillarPacket;
import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PillarRegistryPacketTest {

    @Test
    public void maximumEditBatchFitsServerboundPayloadAndRoundTrips() {
        List<EditPillarRowsPacket.Row> rows = new ArrayList<>();
        for (int i = 0; i < EditPillarRowsPacket.MAX_ROWS; i++) {
            rows.add(new EditPillarRowsPacket.Row("A".repeat(32), "B".repeat(32),
                    "test:" + "a".repeat(251), Integer.MIN_VALUE, 64, Integer.MAX_VALUE));
        }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        FriendlyByteBuf encoded = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new EditPillarRowsPacket(123L, rows).encode(buffer);
            assertTrue(buffer.readableBytes() < 32_767);
            EditPillarRowsPacket.decode(buffer).encode(encoded);
            assertEquals(0, buffer.readableBytes());
            assertEquals(123L, encoded.readLong());
            assertEquals(EditPillarRowsPacket.MAX_ROWS, encoded.readInt());
            assertEquals("A".repeat(32), encoded.readUtf(32));
            assertEquals("B".repeat(32), encoded.readUtf(32));
            assertEquals(rows.get(0).dimension, encoded.readUtf(256));
            assertEquals(Integer.MIN_VALUE, encoded.readInt());
            assertEquals(64, encoded.readInt());
            assertEquals(Integer.MAX_VALUE, encoded.readInt());
        } finally {
            buffer.release();
            encoded.release();
        }
    }

    @Test
    public void resultRoundTripsConflictingRowReasonAndBatchId() throws Exception {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        String reason = "position_conflict:" + "A".repeat(32) + ":" + "B".repeat(32);
        try {
            new EditPillarRowsResultPacket(456L, List.of("S-P1", "S-P2"), reason).encode(buffer);
            EditPillarRowsResultPacket decoded = EditPillarRowsResultPacket.decode(buffer);
            assertEquals(0, buffer.readableBytes());
            for (String name : List.of("batchId", "oldIds", "reason")) {
                Field field = EditPillarRowsResultPacket.class.getDeclaredField(name);
                field.setAccessible(true);
                Object expected = name.equals("batchId") ? 456L : name.equals("oldIds")
                        ? List.of("S-P1", "S-P2") : reason;
                assertEquals(expected, field.get(decoded));
            }
        } finally {
            buffer.release();
        }
    }

    @Test(expected = EncoderException.class)
    public void oversizedEditBatchIsRejected() {
        new EditPillarRowsPacket(Collections.nCopies(EditPillarRowsPacket.MAX_ROWS + 1,
                new EditPillarRowsPacket.Row("S-P1", "S-P1", "minecraft:overworld", 0, 64, 0)));
    }

    @Test(expected = DecoderException.class)
    public void oversizedEditCountIsRejectedBeforeAllocation() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buffer.writeLong(0L);
            buffer.writeInt(EditPillarRowsPacket.MAX_ROWS + 1);
            EditPillarRowsPacket.decode(buffer);
        } finally {
            buffer.release();
        }
    }

    @Test
    public void removalBatchFitsServerboundPayload() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new RemovePillarPacket(Collections.nCopies(RemovePillarPacket.MAX_IDS, "A".repeat(32))).encode(buffer);
            assertTrue(buffer.readableBytes() < 32_767);
            RemovePillarPacket.decode(buffer);
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void editsPreserveLiveFieldsAndLeaveUnrelatedRows() throws Exception {
        Constructor<PillarIdManager> constructor = PillarIdManager.class.getDeclaredConstructor(boolean.class);
        constructor.setAccessible(true);
        PillarIdManager manager = constructor.newInstance(true);
        PillarIdManager.PillarData live = data("S-P1");
        live.pattern = "spiral";
        live.use_pattern = true;
        live.pattern_speed = 1.5;
        live.pattern_spread = 2.0;
        live.pattern_intensity = 3.0;
        live.max_particle_color = 5;
        live.displayedItem = "minecraft:diamond";
        live.pillarType = "minecraft:item_frame";
        live.facing = "north";
        live.itemYaw = 45.0f;
        live.createdTime = 123L;
        live.dyeColors.add("#123456");
        manager.addPillarDataFromSync(live);
        PillarIdManager.PillarData unrelated = data("S-P2");
        unrelated.x = 100;
        manager.addPillarDataFromSync(unrelated);
        assertEquals("", manager.editPillarRows(List.of(
                new EditPillarRowsPacket.Row("S-P1", "M-P1", "minecraft:the_nether", 1, 2, 3))));
        assertNull(manager.getPillarData("S-P1"));
        live = manager.getPillarData("M-P1");
        assertNotNull(live);
        assertEquals(100, manager.getPillarData("S-P2").x);
        assertEquals(2, manager.getPillarCount());
        assertEquals("minecraft:the_nether", live.dimension);
        assertEquals(1, live.x);
        assertEquals(2, live.y);
        assertEquals(3, live.z);
        assertEquals("spiral", live.pattern);
        assertEquals(Boolean.TRUE, live.use_pattern);
        assertEquals(Double.valueOf(1.5), live.pattern_speed);
        assertEquals(Double.valueOf(2.0), live.pattern_spread);
        assertEquals(Double.valueOf(3.0), live.pattern_intensity);
        assertEquals(Integer.valueOf(5), live.max_particle_color);
        assertEquals("minecraft:diamond", live.displayedItem);
        assertEquals("minecraft:item_frame", live.pillarType);
        assertEquals("north", live.facing);
        assertEquals(Float.valueOf(45.0f), live.itemYaw);
        assertEquals(123L, live.createdTime);
        assertEquals(List.of("#123456"), live.dyeColors);
        assertEquals("M-P1", manager.getIdForPosition("minecraft:the_nether:1:2:3:north"));
    }

    @Test
    public void syncBeyondOldRegistryLimitChunksAndCombines() throws Exception {
        List<PillarIdManager.PillarData> entries = new ArrayList<>();
        for (int i = 0; i < 16_385; i++) entries.add(data("S-P" + i));
        Method create = SyncPillarIdsPacket.class.getDeclaredMethod("createChunks", List.class);
        create.setAccessible(true);
        List<?> packets = (List<?>) create.invoke(null, entries);
        assertEquals(257, packets.size());
        Class<?> accumulatorType = Class.forName(SyncPillarIdsPacket.class.getName() + "$SyncAccumulator");
        Constructor<?> constructor = accumulatorType.getDeclaredConstructor(int.class, long.class);
        constructor.setAccessible(true);
        Object accumulator = constructor.newInstance(packets.size(), 0L);
        Method add = accumulatorType.getDeclaredMethod("add", int.class, List.class);
        Method combine = accumulatorType.getDeclaredMethod("combine");
        add.setAccessible(true);
        combine.setAccessible(true);
        int offset = 0;
        for (int i = 0; i < packets.size(); i++) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                ((SyncPillarIdsPacket) packets.get(i)).encode(buffer);
                SyncPillarIdsPacket.decode(buffer);
                assertEquals(0, buffer.readableBytes());
            } finally {
                buffer.release();
            }
            int end = Math.min(entries.size(), offset + 64);
            add.invoke(accumulator, i, entries.subList(offset, end));
            offset = end;
        }
        assertEquals(entries, combine.invoke(accumulator));
    }

    private static PillarIdManager.PillarData data(String id) {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = id;
        data.dimension = "minecraft:overworld";
        return data;
    }
}
