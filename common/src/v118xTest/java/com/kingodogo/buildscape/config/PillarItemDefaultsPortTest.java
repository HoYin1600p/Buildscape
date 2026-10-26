package com.kingodogo.buildscape.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PillarItemDefaultsPortTest {
    @TempDir Path directory;

    @Test
    void explicitVersionPreservesRemovedDefaults() throws Exception {
        Path file = directory.resolve("pillar-items.json");
        String original = "{\"items\":[\"minecraft:stick\"],\"defaults_version\":405}";
        Files.writeString(file, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        assertEquals(Set.of("minecraft:stick"), config.items);
        assertEquals(original, Files.readString(file));
    }

    @Test
    void reloadRetainsLastGoodItemsWhenFileBecomesMalformed() throws Exception {
        Path file = directory.resolve("pillar-items.json");
        Files.writeString(file, "{\"items\":[\"minecraft:stick\"],\"defaults_version\":405}");
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        Files.writeString(file, "{ broken");
        config.loadItemsInternal(false);
        assertEquals(Set.of("minecraft:stick"), config.items);
        assertEquals("{ broken", Files.readString(file));
    }

    @Test
    void nonStringEntriesProtectOriginalDocument() throws Exception {
        Path file = directory.resolve("pillar-items.json");
        String original = "{\"items\":[\"minecraft:stick\",7],\"custom\":42}";
        Files.writeString(file, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        assertFalse(config.saveItemsToDisk(false));
        assertEquals(original, Files.readString(file));
    }

    @Test
    void manualRemovalsStayRemovedAfterAnUnversionedFullDefaultListWasLoaded() throws Exception {
        Path file = directory.resolve("pillar-items.json");
        var original = new com.google.gson.JsonObject();
        original.add("items", ConfigPaths.loadItemsTemplate("vanilla_pillar_items").get("items"));
        Files.writeString(file, original.toString());
        new PillarParticleConfig(directory).loadItemsInternal(false);

        Set<String> pre405 = PillarParticleConfig.parseItems(
                ConfigPaths.loadItemsTemplate("legacy_pillar_items_pre405").toString());
        Set<String> introduced405 = new java.util.HashSet<>(PillarParticleConfig.parseItems(
                ConfigPaths.loadItemsTemplate("legacy_pillar_items").toString()));
        introduced405.removeAll(pre405);
        var edited = ConfigLayoutIO.readProperties(Files.readString(file));
        var entries = edited.getAsJsonArray("items").iterator();
        while (entries.hasNext()) {
            if (introduced405.contains(entries.next().getAsString())) entries.remove();
        }
        Files.writeString(file, edited.toString());

        PillarParticleConfig reloaded = new PillarParticleConfig(directory);
        reloaded.loadItemsInternal(false);
        assertTrue(java.util.Collections.disjoint(introduced405, reloaded.items));
    }
}
