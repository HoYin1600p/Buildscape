package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.network.ResetPillarsPacket;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Guards the building blocks of Reset Selected batching: {@code removalBatches} plus {@code ResetPillarsPacket} size
 * limits. The click path in the config screen is covered by the in-game check for large resets.
 */
public class ResetPillarsBatchingTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void twentyThousandResetIdsEncodeInSmallPacketsWithoutLosingIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 20_000; i++) {
            ids.add("S-P" + i);
        }

        List<List<String>> batches = BuildScapeConfigScreen.removalBatches(ids);
        assertEquals(79, batches.size());
        List<String> encodedIds = new ArrayList<>();
        for (int i = 0; i < batches.size(); i++) {
            List<String> batch = batches.get(i);
            assertEquals(i == batches.size() - 1 ? 32 : 256, batch.size());
            assertTrue("Reset packets must contain at most 256 ids", batch.size() <= 256);

            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                new ResetPillarsPacket(batch).encode(buffer);
                assertTrue("Reset payload must stay below the serverbound limit",
                        buffer.readableBytes() < 32_767);
                int count = buffer.readInt();
                assertEquals(batch.size(), count);
                assertTrue("Encoded reset packets must contain at most 256 ids", count <= 256);
                for (int j = 0; j < count; j++) {
                    String id = buffer.readUtf(512);
                    assertEquals(batch.get(j), id);
                    encodedIds.add(id);
                }
                assertEquals(0, buffer.readableBytes());
            } finally {
                buffer.release();
            }
        }
        assertEquals(ids, encodedIds);
    }
}
