package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildScapeConfigFilteringTest {
    @Test
    void searchMatchesIdentifiersNamesAndNamespacesCaseInsensitively() {
        assertTrue(BuildScapeConfigScreen.matchesConfigSearch("minecraft:stone", "Stone", " STONE "));
        assertTrue(BuildScapeConfigScreen.matchesConfigSearch("examplemod:polished_tile", "Polished Tile", "exampleMOD"));
        assertTrue(BuildScapeConfigScreen.matchesConfigSearch("forge:storage_blocks/iron", "", "storage_blocks"));
        assertTrue(BuildScapeConfigScreen.matchesConfigSearch("c:ingots/copper", null, ""));
        assertFalse(BuildScapeConfigScreen.matchesConfigSearch("minecraft:dirt", "Dirt", "stone"));
    }

    @Test
    void namespaceDiscoveryIsSortedUniqueAndIgnoresMalformedIdentifiers() {
        assertEquals(
                Arrays.asList("c", "examplemod", "forge", "minecraft"),
                BuildScapeConfigScreen.discoverNamespaces(Arrays.asList(
                        "minecraft:stone", "forge:ores/iron", "c:ingots/copper",
                        "examplemod:block", "forge:storage_blocks", "", "invalid", null)));
        assertEquals(Collections.emptyList(), BuildScapeConfigScreen.discoverNamespaces(Collections.emptyList()));
    }

    @Test
    void searchLayoutKeepsControlsPositiveAndInsideAvailableWidth() {
        assertEquals(Arrays.asList(52, 52, 4), BuildScapeConfigScreen.calculateSearchControlWidths(108, 52, 4));
        assertEquals(Arrays.asList(164, 72, 4), BuildScapeConfigScreen.calculateSearchControlWidths(240, 72, 4));
        assertEquals(Arrays.asList(404, 92, 4), BuildScapeConfigScreen.calculateSearchControlWidths(500, 92, 4));
    }
}
