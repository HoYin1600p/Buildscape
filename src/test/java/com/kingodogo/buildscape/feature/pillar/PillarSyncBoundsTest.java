package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.network.SyncPillarIdsPacket;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PillarSyncBoundsTest {
    @Test
    public void excessiveChunkCountIsRejectedBeforeAllocation() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buffer.writeLong(1L);
            buffer.writeInt(Integer.MAX_VALUE);
            assertThrows(DecoderException.class, () -> SyncPillarIdsPacket.decode(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    public void duplicateChunksDoNotConsumeBudgetAndOversizedChunksAreRejected() throws Exception {
        Class<?> type = Class.forName(SyncPillarIdsPacket.class.getName() + "$SyncAccumulator");
        Constructor<?> constructor = type.getDeclaredConstructor(int.class, long.class);
        constructor.setAccessible(true);
        Object accumulator = constructor.newInstance(2, 0L);
        Method add = type.getDeclaredMethod("add", int.class, List.class, int.class);
        add.setAccessible(true);
        assertEquals(true, add.invoke(accumulator, 0, List.of(), 16 * 1024 * 1024));
        assertEquals(true, add.invoke(accumulator, 0, List.of(), 16 * 1024 * 1024));
        assertEquals(false, add.invoke(accumulator, 1, List.of(), 1));
    }
}
