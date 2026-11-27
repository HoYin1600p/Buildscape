package com.kingodogo.buildscape.biome;

import com.kingodogo.buildscape.util.CommonId;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BiomeTintStoreTest {
    @Test
    void columnsAreChunkedCorrectlyAcrossNegativeCoordinates() {
        var store = new BiomeTintStore();
        var biome = CommonId.of("minecraft", "plains");
        assertTrue(store.putColumn(-1, -17, biome));
        assertFalse(store.putColumn(-1, -17, biome));
        long chunk = BiomeTintStore.chunkKey(-1, -2);
        assertEquals(new net.minecraft.world.level.ChunkPos(-1, -2).pack(), chunk);
        assertTrue(store.hasChunk(chunk));
        assertEquals(biome, store.getChunk(chunk).get(255));
        assertFalse(store.hasChunk(BiomeTintStore.chunkKey(0, -2)));
    }

    @Test
    void restorationAndSnapshotsDoNotShareMutableMaps() {
        var store = new BiomeTintStore();
        var biome = CommonId.of("minecraft", "swamp");
        var columns = new HashMap<>(Map.of(42, biome));
        store.restoreChunk(7L, columns);
        columns.clear();
        assertEquals(biome, store.getChunk(7L).get(42));
        assertThrows(UnsupportedOperationException.class, () -> store.snapshot().get(7L).clear());
        assertThrows(IllegalArgumentException.class, () -> store.restoreChunk(7L, Map.of(256, biome)));
        store.restoreChunk(7L, Map.of());
        assertFalse(store.hasChunk(7L));
    }
}
