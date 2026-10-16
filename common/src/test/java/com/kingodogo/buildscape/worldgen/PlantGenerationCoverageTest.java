package com.kingodogo.buildscape.worldgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlantGenerationCoverageTest {
    @ParameterizedTest(name = "{0} retains the 4.0.4 plant set")
    @ValueSource(strings = {"fabric/1.21.1", "forge/1.21.1", "neoforge/1.21.1",
            "fabric/26.2", "forge/26.2", "neoforge/26.2", "fabric/26.3", "forge/26.3", "neoforge/26.3"})
    void eachModernTargetShipsTheCompleteBiomeFeatureTable(String target) throws Exception {
        Path main = Path.of(System.getProperty("buildscape.mainResources", "common/src/main/resources")).toAbsolutePath();
        Path common = main.getParent().getParent().getParent();
        Path repo = common.getParent();
        Path resources = target.endsWith("1.21.1") ? common.resolve("src/v121x/resources") : main;
        Path overlay = target.endsWith("26.3") ? common.resolve("src/v263/resources") : resources;
        String loader = target.substring(0, target.indexOf('/'));
        JsonObject reference;
        try (var stream = getClass().getResourceAsStream("/worldgen/plant-biomes-4.0.4.json")) {
            assertNotNull(stream);
            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                reference = JsonParser.parseReader(reader).getAsJsonObject();
            }
        }
        assertEquals(reference.size(), BiomePlacements.ENTRIES.size());
        for (var entry : BiomePlacements.ENTRIES) {
            String name = entry.biomeTag().substring(entry.biomeTag().lastIndexOf('/') + 1);
            JsonObject expected = reference.getAsJsonObject(name);
            assertNotNull(expected, name);
            assertEquals(expected.getAsJsonObject("features").keySet(), new HashSet<>(entry.features()), target + ": " + name);
            JsonObject tag = read(resources.resolve("data/minecraft/tags/worldgen/biome/buildscape/" + name + ".json"));
            assertFalse(tag.get("replace").getAsBoolean());
            assertEquals(strings(expected.getAsJsonArray("biomes")), strings(tag.getAsJsonArray("values")), target);
            if (!loader.equals("fabric")) {
                String path = "data/buildscape/" + loader + "/biome_modifier/" + name + ".json";
                JsonObject modifier = read(effective(resources, overlay, path));
                assertEquals(loader + ":add_features", modifier.get("type").getAsString(), target);
                assertEquals("#" + entry.biomeTag(), modifier.get("biomes").getAsString(), target);
                assertEquals("vegetal_decoration", modifier.get("step").getAsString(), target);
                assertEquals(entry.features().stream().map(feature -> "buildscape:" + feature).toList(),
                        modifier.getAsJsonArray("features").asList().stream().map(JsonElement::getAsString).toList(), target);
                assertEquals(entry.features().size(), modifier.getAsJsonArray("features").size());
            }
            for (String feature : entry.features()) {
                JsonObject placed = read(effective(resources, overlay, "data/buildscape/worldgen/placed_feature/" + feature + ".json"));
                assertTrue(placed.has("feature"), target + ": " + feature);
                int rarityFilters = 0;
                for (var value : placed.getAsJsonArray("placement")) {
                    JsonObject placement = value.getAsJsonObject();
                    if (placement.get("type").getAsString().equals("minecraft:rarity_filter")) {
                        assertEquals(expected.getAsJsonObject("features").get(feature).getAsInt(), placement.get("chance").getAsInt(), target + ": " + feature);
                        rarityFilters++;
                    }
                }
                assertEquals(1, rarityFilters, target + ": " + feature);
            }
        }
        if (loader.equals("fabric")) {
            String hook = Files.readString(repo.resolve(target + "/src/main/java/com/kingodogo/buildscape/worldgen/ModBiomeModifications.java"));
            for (String required : List.of("BiomePlacements.ENTRIES", "BiomeSelectors.tag(", "entry.biomeTag()",
                    "entry.features()", "VEGETAL_DECORATION", "Registries.PLACED_FEATURE"))
                assertTrue(hook.contains(required), target + ": " + required);
        } else {
            Path modifiers = resources.resolve("data/buildscape/" + loader + "/biome_modifier");
            try (var files = Files.list(modifiers)) {
                assertEquals(reference.size(), files.filter(path -> path.toString().endsWith(".json")).count(), target);
            }
        }
    }

    private static Path effective(Path resources, Path overlay, String path) {
        return Files.isRegularFile(overlay.resolve(path)) ? overlay.resolve(path) : resources.resolve(path);
    }

    private static Set<String> strings(JsonArray values) {
        Set<String> result = new HashSet<>();
        values.forEach(value -> result.add(value.getAsString()));
        return result;
    }

    private static JsonObject read(Path path) throws Exception {
        assertTrue(Files.isRegularFile(path), "Missing worldgen resource: " + path);
        try (var reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
}
