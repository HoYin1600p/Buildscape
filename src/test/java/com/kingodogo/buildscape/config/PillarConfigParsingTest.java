package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.google.gson.Gson;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class PillarConfigParsingTest extends BootstrappedGameTest {

    @Test
    public void existingItemListsRemainExactAndSupportComments() {
        Set<String> items = PillarParticleConfig.parseItems(
                "{\n// user list\n\"items\": [\"minecraft:stick\", \"#forge:gems\"]}");
        assertEquals(Set.of("minecraft:stick", "#forge:gems"), items);
        assertTrue(PillarParticleConfig.parseItems("{\"items\": []}").isEmpty());
    }

    @Test
    public void invalidItemDocumentsAreRejected() {
        for (String content : new String[]{"", "null", "{}", "{\"items\": null}",
                "{\"items\": {}}", "{\"items\": ["}) {
            assertThrows(RuntimeException.class, () -> PillarParticleConfig.parseItems(content));
        }
    }

    @Test
    public void itemArraysSkipTrailingCommasAndNonStrings() {
        Set<String> expected = Set.of("minecraft:stick", "#forge:gems");
        assertEquals(expected, PillarParticleConfig.parseItems(
                "{\"items\": [\"minecraft:stick\", \"#forge:gems\",]}"));
        assertEquals(expected, PillarParticleConfig.parseItems(
                "{\"items\": [\"minecraft:stick\", 7, null, false, {}, [], \"#forge:gems\"]}"));
    }

    @Test
    public void itemStringsSurviveJsonEscaping() {
        Set<String> items = Set.of("example:quote\"slash\\");
        String json = new Gson().toJson(Map.of("items", items));
        assertEquals(items, PillarParticleConfig.parseItems(json));
    }

    @Test
    public void malformedPresetsAndLastAppliedDoNotHideGoodPresets() {
        Gson gson = new Gson();
        Map<String, PresetsConfig.Preset> mixed = PresetsConfig.parsePresets(gson.fromJson(
                "{\"good\": {\"name\": \"Good\", \"items\": [\"minecraft:stick\", 7, null,]},"
                        + " \"bad\": {\"items\": null}, \"_lastApplied\": 42}",
                com.google.gson.JsonObject.class));
        assertEquals(Set.of("good"), mixed.keySet());
        assertEquals("Good", mixed.get("good").name);
        assertEquals(Set.of("minecraft:stick"), mixed.get("good").items);
        Map<String, PresetsConfig.Preset> parsed = PresetsConfig.parsePresets(gson.fromJson(
                "{\"custom\": {\"name\": \"Custom\", \"items\": []}, \"_lastApplied\": \"custom\"}",
                com.google.gson.JsonObject.class));
        assertTrue(parsed.get("custom").items.isEmpty());
        assertEquals("Custom", parsed.get("custom").name);
    }

    @Test
    public void unreadableFileBackupIsNeverReplaced() throws Exception {
        Path directory = Files.createTempDirectory("pillar-config-test");
        Path file = directory.resolve("pillar-items.json");
        Path backup = directory.resolve("pillar-items.json.bak");
        try {
            Files.writeString(file, "first broken contents");
            PillarParticleConfig.backupUnreadableFile(file.toFile());
            Files.writeString(file, "second broken contents");
            PillarParticleConfig.backupUnreadableFile(file.toFile());
            assertEquals("first broken contents", Files.readString(backup));
            assertEquals("second broken contents", Files.readString(file));
        } finally {
            Files.deleteIfExists(backup);
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }
}
