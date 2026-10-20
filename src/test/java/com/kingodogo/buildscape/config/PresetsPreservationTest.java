package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PresetsPreservationTest extends BootstrappedGameTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void savingAfterPartialParsePreservesMalformedPresetAndValidPreset() throws Exception {
        assertPreserved("{\"name\":\"Broken\",\"items\":{\"keep\":42},\"extra\":[null,true],\"nullable\":null}");
    }

    @Test
    public void savingPreservesNonStringItemsInOtherwiseValidPreset() throws Exception {
        assertPreserved("{\"name\":\"Mixed\",\"items\":[\"minecraft:stone\",42,null,{\"keep\":true}]}");
    }

    @Test
    public void unreadableFileRefusesEditsWithReasonAndKeepsLastGoodPresets() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-presets.json");
        Files.writeString(file, "{\"default\":{\"name\":\"Default\",\"items\":[\"minecraft:diamond\"]},"
                + "\"damaged\":{\"name\":\"Mixed\",\"items\":[\"minecraft:stone\",42]}}");
        PresetsConfig config = new PresetsConfig(directory);
        config.load();
        Files.writeString(file, "{unreadable");
        config.load();
        assertFalse(config.deletePreset("damaged"));
        assertEquals("unreadable", config.getLastEditFailure());
        assertFalse(config.savePreset("damaged", "Replacement", java.util.Set.of("minecraft:dirt")));
        assertEquals("unreadable", config.getLastEditFailure());
        assertTrue(config.getPresetKeys().contains("damaged"));
        assertEquals(java.util.Set.of("minecraft:stone"), config.getPreset("damaged").items);
        assertEquals("{unreadable", Files.readString(file));
    }

    private void assertPreserved(String malformed) throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-presets.json");
        String original = "{\"default\":{\"name\":\"Default\",\"items\":[\"minecraft:diamond\"]},"
                + "\"damaged\":" + malformed + ",\"_lastApplied\":\"default\"}";
        Files.writeString(file, original);
        Constructor<PresetsConfig> constructor = PresetsConfig.class.getDeclaredConstructor(Path.class);
        constructor.setAccessible(true);
        PresetsConfig config = constructor.newInstance(directory);
        config.load();
        config.save();
        JsonObject expected = JsonParser.parseString(original).getAsJsonObject();
        JsonObject actual = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        assertEquals(expected.get("damaged"), actual.get("damaged"));
        assertEquals(expected.get("default"), actual.get("default"));
        assertEquals("default", actual.get("_lastApplied").getAsString());
        assertTrue(config.getPreset("default").items.contains("minecraft:diamond"));
        assertEquals(original, Files.readString(directory.resolve("pillar-presets.json.bak")));
        assertTrue(config.getPresetKeys().contains("damaged"));
        assertNotNull(config.getPreset("damaged"));
        assertTrue(config.deletePreset("damaged"));
        config.save();
        config.load();
        assertFalse(config.getPresetKeys().contains("damaged"));
        assertNull(config.getPreset("damaged"));
        JsonObject deleted = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        assertFalse(deleted.has("damaged"));
        assertEquals(expected.get("default"), deleted.get("default"));
        assertEquals(java.util.Set.of("minecraft:diamond"), config.getPreset("default").items);
        Files.writeString(file, original);
        config.load();
        assertTrue(config.savePreset("damaged", "Replacement", java.util.Set.of("minecraft:dirt")));
        config.load();
        config.save();
        JsonObject replaced = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        assertEquals("Replacement", replaced.getAsJsonObject("damaged").get("name").getAsString());
        assertEquals(java.util.Set.of("minecraft:dirt"), config.getPreset("damaged").items);
        assertEquals(expected.get("default"), replaced.get("default"));
        assertFalse(replaced.getAsJsonObject("damaged").has("extra"));
    }
}
