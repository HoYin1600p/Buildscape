package com.kingodogo.buildscape.client.biome;

import com.kingodogo.buildscape.biome.BiomeTintStore;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BiomeTintCacheTest {
    @Test
    void blendsPaintedAndOriginalColumnsAcrossChunkBorders() {
        var cache = new BiomeTintCache<String>();
        Object world = new Object();
        var columns = new HashMap<>(Map.of(BiomeTintStore.columnIndex(-1, 0), "painted"));
        assertTrue(cache.replace(world, BiomeTintStore.chunkKey(-1, 0), columns));
        columns.clear();
        assertEquals("painted", cache.get(world, -1, 0));
        assertEquals(0x1c0000, cache.blendedColor(world, 0, 0, 1,
                (x, z) -> "original", (biome, x, z) -> biome.equals("painted") ? 0xff0000 : 0));
        assertNull(cache.blendedColor(world, 20, 20, 1, (x, z) -> "original", (biome, x, z) -> 0));
    }

    @Test
    void changingWorldOrUnloadingChunkCannotLeakOldTints() {
        var cache = new BiomeTintCache<String>();
        Object first = new Object(), second = new Object();
        long chunk = BiomeTintStore.chunkKey(0, 0);
        assertTrue(cache.replace(first, chunk, Map.of(0, "first")));
        assertNull(cache.get(second, 0, 0));
        assertTrue(cache.replace(second, chunk, Map.of(0, "second")));
        assertFalse(cache.remove(first, chunk));
        assertEquals("second", cache.get(second, 0, 0));
        assertTrue(cache.remove(second, chunk));
        assertNull(cache.get(second, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> cache.replace(second, chunk, Map.of(256, "bad")));
    }
}
