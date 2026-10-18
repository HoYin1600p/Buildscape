package com.kingodogo.buildscape.kyro;

import com.kingodogo.buildscape.network.EditPillarRowsPacket;
import com.kingodogo.buildscape.network.EditPillarRowsResultPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PillarEditPacketTest {
    @Test
    public void maximumBatchFitsPayloadAndKeepsCorrelationId() {
        List<EditPillarRowsPacket.Row> rows = new ArrayList<>();
        for (int i = 0; i < EditPillarRowsPacket.MAX_ROWS; i++) {
            rows.add(new EditPillarRowsPacket.Row("A".repeat(32), "B".repeat(32),
                    "test:" + "a".repeat(251), Integer.MIN_VALUE, 64, Integer.MAX_VALUE));
        }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new EditPillarRowsPacket(123L, rows).encode(buffer);
            assertTrue(buffer.readableBytes() < 32_767);
            EditPillarRowsPacket decoded = EditPillarRowsPacket.decode(buffer);
            assertEquals(0, buffer.readableBytes());
            decoded.encode(buffer);
            assertEquals(123L, buffer.readLong());
            assertEquals(EditPillarRowsPacket.MAX_ROWS, buffer.readInt());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void resultRoundTripsRowIdsReasonAndBatchId() throws Exception {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new EditPillarRowsResultPacket(456L, List.of("S-P1", "S-P2"), "position_conflict").encode(buffer);
            EditPillarRowsResultPacket decoded = EditPillarRowsResultPacket.decode(buffer);
            assertEquals(0, buffer.readableBytes());
            for (String name : List.of("batchId", "oldIds", "reason")) {
                Field field = EditPillarRowsResultPacket.class.getDeclaredField(name);
                field.setAccessible(true);
                Object expected = name.equals("batchId") ? 456L : name.equals("oldIds")
                        ? List.of("S-P1", "S-P2") : "position_conflict";
                assertEquals(expected, field.get(decoded));
            }
        } finally {
            buffer.release();
        }
    }
}
