package com.kingodogo.buildscape.feature.biome;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SyncBiomeTintPacketTest {
    private static final ResourceLocation DIMENSION = new ResourceLocation("minecraft", "overworld");
    private static final ResourceLocation BIOME = new ResourceLocation("minecraft", "swamp");

    @Test
    public void fullChunkAndEmptyReplacementRoundTrip() {
        Map<Integer, ResourceLocation> columns = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            columns.put(i, BIOME);
        }
        roundTrip(columns);
        roundTrip(Map.of());
    }

    private void roundTrip(Map<Integer, ResourceLocation> columns) {
        SyncBiomeTintPacket original = new SyncBiomeTintPacket(DIMENSION, new ChunkPos(-2, 3), columns);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            SyncBiomeTintPacket.encode(original, buffer);
            SyncBiomeTintPacket decoded = SyncBiomeTintPacket.decode(buffer);
            assertEquals(original.dimension, decoded.dimension);
            assertEquals(original.chunk, decoded.chunk);
            assertEquals(original.columns, decoded.columns);
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void invalidCountsAndDuplicateColumnsAreRejected() {
        for (int count : new int[] {-1, 257, 2}) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                buffer.writeResourceLocation(DIMENSION);
                buffer.writeChunkPos(new ChunkPos(0, 0));
                buffer.writeVarInt(count);
                if (count == 2) {
                    for (int i = 0; i < 2; i++) {
                        buffer.writeByte(0);
                        buffer.writeResourceLocation(BIOME);
                    }
                }
                assertThrows(IllegalArgumentException.class, () -> SyncBiomeTintPacket.decode(buffer));
            } finally {
                buffer.release();
            }
        }
        assertThrows(IllegalArgumentException.class,
                () -> new SyncBiomeTintPacket(DIMENSION, new ChunkPos(0, 0), Map.of(256, BIOME)));
    }
}
