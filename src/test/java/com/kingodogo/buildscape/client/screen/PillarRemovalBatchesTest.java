package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.network.RemovePillarPacket;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PillarRemovalBatchesTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void emptyRemovalSendsNoBatches() {
        assertBatches(0, 0, 0);
    }

    @Test
    public void singleRemovalUsesOneBatch() {
        assertBatches(1, 1, 1);
    }

    @Test
    public void fullPacketUsesOneBatch() {
        assertBatches(256, 1, 256);
    }

    @Test
    public void firstIdBeyondPacketLimitStartsAnotherBatch() {
        assertBatches(257, 2, 1);
    }

    @Test
    public void twentyThousandIdsUseSeventyNineBatches() {
        assertBatches(20_000, 79, 32);
    }

    private static void assertBatches(int count, int expectedBatches, int lastBatchSize) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(Integer.toString(i));
        }
        List<List<String>> batches = BuildScapeConfigScreen.removalBatches(ids);
        assertEquals(expectedBatches, batches.size());
        List<String> flattened = new ArrayList<>();
        for (int i = 0; i < batches.size(); i++) {
            List<String> batch = batches.get(i);
            assertEquals(i == batches.size() - 1 ? lastBatchSize : RemovePillarPacket.MAX_IDS, batch.size());
            assertTrue(batch.size() <= RemovePillarPacket.MAX_IDS);
            flattened.addAll(batch);
        }
        assertEquals(ids, flattened);
        assertEquals(count, ids.size());
    }
}
