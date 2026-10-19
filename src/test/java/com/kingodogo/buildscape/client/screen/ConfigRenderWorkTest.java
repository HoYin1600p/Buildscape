package com.kingodogo.buildscape.client.screen;

import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class ConfigRenderWorkTest {
    @Test
    public void visibleRowsCoverPartialRowsAndClampToList() {
        assertArrayEquals(new int[]{0, 3}, ConfigRenderWork.visibleRows(0, 50, 24, 100));
        assertArrayEquals(new int[]{1, 4}, ConfigRenderWork.visibleRows(25, 50, 24, 100));
        assertArrayEquals(new int[]{98, 100}, ConfigRenderWork.visibleRows(2352, 50, 24, 100));
        assertArrayEquals(new int[]{0, 0}, ConfigRenderWork.visibleRows(0, 50, 24, 0));
    }

    @Test
    public void blankAndIdMatchingSearchesNeverResolveDisplayNames() {
        AtomicInteger calls = new AtomicInteger();
        java.util.function.Supplier<String> name = () -> { calls.incrementAndGet(); return ConfigRenderWork.normalize("Diamond Pickaxe"); };
        assertTrue(ConfigRenderWork.matches("minecraft:diamond_pickaxe", "", name));
        assertTrue(ConfigRenderWork.matches("minecraft:diamond_pickaxe", "diamond", name));
        assertEquals(0, calls.get());
        assertTrue(ConfigRenderWork.matches("mod:tool", "diamond pickaxe", name));
        assertEquals(1, calls.get());
    }

    @Test
    public void cacheIsBoundedAndInvalidationRecomputesValues() {
        ConfigRenderWork.Cache<String, String> cache = new ConfigRenderWork.Cache<>(2);
        assertEquals("old", cache.get("a", key -> "old"));
        assertEquals("old", cache.get("a", key -> "new"));
        cache.get("b", key -> key);
        cache.get("c", key -> key);
        assertEquals("new", cache.get("a", key -> "new"));
        cache.clear();
        assertEquals("reloaded", cache.get("a", key -> "reloaded"));
    }
}
