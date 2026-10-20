package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class PresetSlotsAndItemOrderTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void fourCustomPresetsByDefaultThenTheLimitMessageCase() throws Exception {
        assertEquals(4, BuildscapeClientConfig.DEFAULT_CUSTOM_PRESETS);
        Path root = temporary.newFolder().toPath();
        assertEquals(4, new BuildscapeClientConfig(root).getMaxCustomPresets());
        PresetsConfig presets = new PresetsConfig(temporary.newFolder().toPath());
        presets.setCustomSlots(4);
        for (int i = 1; i <= 4; i++) {
            assertTrue("slot " + i, presets.savePreset("preset_" + i, "P" + i, Set.of("minecraft:stone")));
        }
        assertFalse(presets.savePreset("preset_5", "P5", Set.of("minecraft:stone")));
        assertEquals("limit", presets.getLastEditFailure());
        assertEquals(4, presets.getCustomPresetCount());
        // Overwriting an existing preset is not a new slot.
        assertTrue(presets.savePreset("preset_2", "Renamed", Set.of("minecraft:dirt")));
        // Deleting one frees a slot.
        assertTrue(presets.deletePreset("preset_3"));
        assertTrue(presets.savePreset("preset_5", "P5", Set.of("minecraft:stone")));
    }

    @Test
    public void slotCountComesFromBuildscapeCfgWithSaneBounds() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path file = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "HideBuildscapeConfig = false\nMaxCustomPresets = 9\n");
        assertEquals(9, new BuildscapeClientConfig(root).getMaxCustomPresets());
        Files.writeString(file, "MaxCustomPresets = 0\n");
        assertEquals(1, new BuildscapeClientConfig(root).getMaxCustomPresets());
        Files.writeString(file, "MaxCustomPresets = 500\n");
        assertEquals(32, new BuildscapeClientConfig(root).getMaxCustomPresets());
        Files.writeString(file, "MaxCustomPresets = many\n");
        assertEquals(4, new BuildscapeClientConfig(root).getMaxCustomPresets());
    }

    @Test
    public void missingPresetKeysAreAppendedAndTheLastPresetIsRemembered() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path file = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        String original = "# mine\nUITheme = light\n";
        Files.writeString(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        String text = Files.readString(file);
        assertTrue(text.startsWith(original));
        assertEquals("4", ConfigLayoutIO.readEntries(text).get("MaxCustomPresets"));
        assertEquals("default", config.getLastPreset());
        config.setLastPreset("preset_2");
        assertEquals("preset_2", new BuildscapeClientConfig(root).getLastPreset());
        assertEquals("light", ConfigLayoutIO.readEntries(Files.readString(file)).get("UITheme"));
        config.setLastPreset(null);
        assertEquals("default", new BuildscapeClientConfig(root).getLastPreset());
    }

    @Test
    public void savedItemsKeepTheirOrderAndNewEntriesGoLast() {
        List<String> existing = List.of("minecraft:zebra", "minecraft:apple", "minecraft:stone", "minecraft:apple");
        // Remove stone, add two new ones: existing order and the repeated entry stay, additions follow.
        List<String> saved = PillarParticleConfig.orderedItemsForSave(existing,
                Set.of("minecraft:zebra", "minecraft:apple", "minecraft:dirt", "#forge:gems"));
        assertEquals(List.of("minecraft:zebra", "minecraft:apple", "minecraft:apple", "#forge:gems", "minecraft:dirt"), saved);
        // Nothing changed: the list comes back exactly as it was.
        assertEquals(existing, PillarParticleConfig.orderedItemsForSave(existing, Set.copyOf(existing)));
        // An unreadable token is kept where it was even though the screen never lists it.
        List<String> withInvalid = List.of("minecraft:zebra", "Not Valid!", "minecraft:apple");
        assertEquals(withInvalid, PillarParticleConfig.orderedItemsForSave(withInvalid,
                Set.of("minecraft:zebra", "minecraft:apple")));
    }

    @Test
    public void savingToDiskKeepsTheFileOrderAndAppendsAdditions() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-items.json");
        Files.writeString(file, "{\n  \"items\": [\n    \"minecraft:zebra\",\n    \"minecraft:apple\",\n    \"minecraft:stone\"\n  ]\n}\n");
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        config.items.remove("minecraft:stone");
        config.items.add("minecraft:dirt");
        assertTrue(config.saveItemsToDisk(false));
        com.google.gson.JsonArray saved = ConfigLayoutIO.readProperties(ConfigLayoutIO.readText(file)).getAsJsonArray("items");
        int zebra = indexOf(saved, "minecraft:zebra");
        int apple = indexOf(saved, "minecraft:apple");
        int dirt = indexOf(saved, "minecraft:dirt");
        assertTrue(zebra >= 0 && apple > zebra);
        assertTrue("addition follows the existing entries", dirt > apple);
        assertEquals(-1, indexOf(saved, "minecraft:stone"));
    }

    private static int indexOf(com.google.gson.JsonArray array, String value) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getAsString().equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
