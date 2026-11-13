package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.util.CommonId;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BiomeTintPayloadTest {
    @Test
    void roundTripsSnapshotsAndEmptyClears() {
        for (Map<Integer, CommonId> columns : java.util.List.of(
                Map.of(0, new CommonId("minecraft", "plains"), 255, new CommonId("minecraft", "swamp")),
                Map.<Integer, CommonId>of())) {
            var original = new BiomeTintPayload(new CommonId("minecraft", "overworld"), -3, 7, columns);
            var buffer = new FriendlyByteBuf(Unpooled.buffer());
            original.write(buffer);
            assertEquals(original, BiomeTintPayload.read(buffer));
            buffer.release();
        }
    }

    @Test
    void rejectsMalformedAndDuplicateColumns() {
        assertThrows(IllegalArgumentException.class, () -> new BiomeTintPayload(
                new CommonId("minecraft", "overworld"), 0, 0, Map.of(256, new CommonId("minecraft", "plains"))));
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeUtf("minecraft:overworld");
        buffer.writeInt(0).writeInt(0).writeInt(2);
        buffer.writeByte(1).writeUtf("minecraft:plains");
        buffer.writeByte(1).writeUtf("minecraft:swamp");
        assertThrows(IllegalArgumentException.class, () -> BiomeTintPayload.read(buffer));
        buffer.release();
    }
}
