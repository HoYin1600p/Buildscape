package com.kingodogo.buildscape.kyro;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.config.PresetsConfig;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class PresetsPreservationTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void savingAfterPartialParsePreservesMalformedPresetAndValidPreset() throws Exception {
        assertPreserved("{\"name\":\"Broken\",\"items\":{\"keep\":42},\"extra\":[null,true],\"nullable\":null}");
    }

    @Test
    public void savingPreservesNonStringItemsInOtherwiseValidPreset() throws Exception {
        assertPreserved("{\"name\":\"Mixed\",\"items\":[\"minecraft:stone\",42,null,{\"keep\":true}]}");
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
        assertFalse(config.savePreset("damaged", "Replacement", java.util.Set.of("minecraft:dirt")));
        config.save();
        assertEquals(actual, JsonParser.parseString(Files.readString(file)));
    }
}
