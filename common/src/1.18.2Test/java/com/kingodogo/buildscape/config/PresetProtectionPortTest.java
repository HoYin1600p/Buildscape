package com.kingodogo.buildscape.config;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PresetProtectionPortTest {
    @TempDir Path directory;

    @Test
    void malformedPresetsSurviveEditingOtherPresets() throws Exception {
        Path file = directory.resolve("pillar-presets.json");
        String original = "{\"damaged\":{\"name\":7,\"items\":null},"
                + "\"valid\":{\"name\":\"Caf\u00e9\",\"items\":[\"minecraft:stick\"]}}";
        Files.writeString(file, original);
        PresetsConfig config = new PresetsConfig(directory);
        config.load(false);
        config.setCustomSlots(8);
        assertTrue(config.savePreset("new", "New", Set.of("minecraft:diamond")));

        var saved = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        assertEquals(JsonParser.parseString(original).getAsJsonObject().get("damaged"), saved.get("damaged"));
        assertEquals("Caf\u00e9", saved.getAsJsonObject("valid").get("name").getAsString());
        assertEquals(original, Files.readString(directory.resolve("pillar-presets.json.bak")));
    }

    @Test
    void fullyUnreadableFileRefusesEditsWithoutLosingOriginal() throws Exception {
        Path file = directory.resolve("pillar-presets.json");
        Files.writeString(file, "{ broken");
        PresetsConfig config = new PresetsConfig(directory);
        config.load(false);
        assertFalse(config.savePreset("new", "New", Set.of("minecraft:diamond")));
        assertEquals("{ broken", Files.readString(file));
        assertFalse(config.getPresetKeys().contains("new"));
    }

    @Test
    void configurableSlotsKeepExistingPresetsVisibleAndBoundNewOnes() throws Exception {
        PresetsConfig config = new PresetsConfig(directory);
        config.load(false);
        config.setCustomSlots(6);
        for (int i = 0; i < 6; i++) {
            assertTrue(config.savePreset("custom_" + i, "Custom " + i, Set.of("minecraft:stick")));
        }
        config.setCustomSlots(2);
        assertEquals(6, config.getCustomPresetCount());
        assertFalse(config.savePreset("extra", "Extra", Set.of("minecraft:diamond")));
        assertTrue(config.savePreset("custom_0", "Edited", Set.of("minecraft:diamond")));
        assertEquals(6, config.getCustomPresetCount());
    }
}
