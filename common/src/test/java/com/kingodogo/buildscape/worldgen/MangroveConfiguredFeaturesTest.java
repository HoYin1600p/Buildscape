package com.kingodogo.buildscape.worldgen;

import com.google.gson.*;
import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.WorldGenFactory;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MangroveConfiguredFeaturesTest {
    @BeforeAll
    static void bootstrap() {
        TestBootstrap.initialize();
    }

    @Test
    void allWorldgenDataUsesRegisteredTypesAndResolvesFeatureReferences() throws Exception {
        Map<String, JsonObject> configured = resources("worldgen/configured_feature");
        Map<String, JsonObject> placed = resources("worldgen/placed_feature");
        Map<String, JsonObject> modifiers = resources("neoforge/biome_modifier");
        var vanilla = VanillaRegistries.createLookup();
        Set<String> configuredIds = new HashSet<>(configured.keySet());
        vanilla.lookupOrThrow(Registries.CONFIGURED_FEATURE).listElementIds()
                .forEach(key -> configuredIds.add(key.identifier().toString()));
        Set<String> placedIds = new HashSet<>(placed.keySet());
        vanilla.lookupOrThrow(Registries.PLACED_FEATURE).listElementIds()
                .forEach(key -> placedIds.add(key.identifier().toString()));
        Set<String> registeredTypes = registeredTypes();
        configured.forEach((id, json) -> {
            assertRegisteredTypes(json, registeredTypes);
            assertTrue(BuiltInRegistries.FEATURE.keySet().stream()
                    .anyMatch(type -> type.toString().equals(json.get("type").getAsString())), id);
            assertFeatureReferences(json.get("config"), placedIds, configuredIds);
        });
        placed.forEach((id, json) -> {
            assertRegisteredTypes(json, registeredTypes);
            assertFeatureReferences(json, configuredIds, configuredIds);
            assertTrue(json.getAsJsonArray("placement").size() > 0, id);
        });
        modifiers.forEach((id, json) -> {
            // Verified against NeoForge 26.2's AddFeaturesBiomeModifier codec.
            assertEquals("neoforge:add_features", json.get("type").getAsString(), id);
            assertEquals("vegetal_decoration", json.get("step").getAsString(), id);
            for (JsonElement feature : json.getAsJsonArray("features"))
                assertTrue(placedIds.contains(feature.getAsString()), id + ": " + feature);
        });
        assertEquals(BiomePlacements.ENTRIES.size(), modifiers.size());
        for (BiomePlacements.Entry entry : BiomePlacements.ENTRIES) {
            String name = entry.biomeTag().substring(entry.biomeTag().lastIndexOf('/') + 1);
            JsonObject modifier = modifiers.get("buildscape:" + name);
            assertNotNull(modifier, name);
            assertEquals("#" + entry.biomeTag(), modifier.get("biomes").getAsString());
            assertEquals(entry.features().stream().map(feature -> "buildscape:" + feature).toList(),
                    modifier.getAsJsonArray("features").asList().stream().map(JsonElement::getAsString).toList());
            JsonObject tag = dataResource("/data/minecraft/tags/worldgen/biome/buildscape/" + name + ".json");
            assertFalse(tag.get("replace").getAsBoolean());
            for (JsonElement biome : tag.getAsJsonArray("values"))
                assertTrue(vanilla.lookupOrThrow(Registries.BIOME).listElementIds()
                        .anyMatch(key -> key.identifier().toString().equals(biome.getAsString())), biome.toString());
        }
        assertEquals(Set.of("minecraft:flower_forest"), biomeValues("flower_forests"));
        assertEquals(Set.of("minecraft:birch_forest", "minecraft:old_growth_birch_forest"), biomeValues("birch_forests"));
        assertEquals(Set.of("minecraft:lush_caves"), biomeValues("lush_caves"));
        assertEquals(Set.of("minecraft:swamp"), biomeValues("swamps"));
        // Catch missing additions, accidental patch-count changes and references to
        // removed 1.18 feature types (notably random_patch).
        for (String color : Set.of("red", "blue", "purple", "light_blue", "pink", "yellow"))
            assertPatch(placed.get("buildscape:" + color + "_monets"), 4, 64);
        assertPatch(placed.get("buildscape:all_petals"), 11, 192);
        assertPatch(placed.get("buildscape:clover"), 32, 64);
        assertPatch(placed.get("buildscape:wildflowers"), 11, 64);
        assertPatch(placed.get("buildscape:leaf_litter"), 11, 64);
        assertPatch(placed.get("buildscape:clover_swamp"), 16, 64);
        int[] rarities = {5, 5, 5, 40, 53, 107, 160, 320};
        for (int n = 1; n <= 8; n++)
            assertPatch(placed.get("buildscape:mangrove_propagule_patch_" + n), rarities[n - 1], n);
        assertPatch(placed.get("buildscape:all_colored_spore_blossom"), 5, 10);
    }

    @ParameterizedTest
    @ValueSource(strings = {"poplar", "cherry", "pale_oak"})
    void saplingsUseReferenceSingleTrunkTrees(String name) throws Exception {
        JsonObject config = resource("configured_feature/" + name).getAsJsonObject("config");
        JsonObject trunk = config.getAsJsonObject("trunk_placer");
        assertEquals("minecraft:straight_trunk_placer", trunk.get("type").getAsString());
        assertEquals(name.equals("pale_oak") ? 5 : 4, trunk.get("base_height").getAsInt());
        assertEquals(2, trunk.get("height_rand_a").getAsInt());
        assertEquals(name.equals("pale_oak") ? 1 : 0, trunk.get("height_rand_b").getAsInt());
        String materialNamespace = name.equals("poplar") ? "buildscape:" : "minecraft:";
        assertEquals(materialNamespace + name + "_log", config.getAsJsonObject("trunk_provider")
                .getAsJsonObject("state").get("Name").getAsString());
        JsonObject foliage = config.getAsJsonObject("foliage_placer");
        assertEquals("minecraft:blob_foliage_placer", foliage.get("type").getAsString());
        assertEquals(2, foliage.get("radius").getAsInt());
        assertEquals(3, foliage.get("height").getAsInt());
        if (name.equals("poplar")) {
            Set<String> leaves = new HashSet<>();
            for (JsonElement entry : config.getAsJsonObject("foliage_provider").getAsJsonArray("entries")) {
                leaves.add(entry.getAsJsonObject().getAsJsonObject("data").get("Name").getAsString());
                assertEquals(1, entry.getAsJsonObject().get("weight").getAsInt());
            }
            assertEquals(Set.of("buildscape:red_poplar_leaves", "buildscape:orange_poplar_leaves",
                    "buildscape:yellow_poplar_leaves"), leaves);
        } else {
            assertEquals(materialNamespace + name + "_leaves", config.getAsJsonObject("foliage_provider")
                    .getAsJsonObject("state").get("Name").getAsString());
        }
        assertEquals(name.equals("pale_oak") ? 1 : 0, config.getAsJsonArray("decorators").size());
        if (name.equals("pale_oak")) assertEquals("buildscape:creaking_heart",
                config.getAsJsonArray("decorators").get(0).getAsJsonObject().get("type").getAsString());
        var grower = WorldGenFactory.saplingGrower(name + "_sapling");
        var tree = grower.getClass().getDeclaredField("tree");
        var mega = grower.getClass().getDeclaredField("megaTree");
        tree.setAccessible(true);
        mega.setAccessible(true);
        assertEquals("buildscape:" + name, ((net.minecraft.resources.ResourceKey<?>)
                ((java.util.Optional<?>) tree.get(grower)).orElseThrow()).identifier().toString());
        assertTrue(((java.util.Optional<?>) mega.get(grower)).isEmpty());
    }

    private static Set<String> biomeValues(String name) throws Exception {
        Set<String> values = new HashSet<>();
        dataResource("/data/minecraft/tags/worldgen/biome/buildscape/" + name + ".json")
                .getAsJsonArray("values").forEach(value -> values.add(value.getAsString()));
        return values;
    }

    private static void assertPatch(JsonObject json, int rarity, int tries) {
        assertNotNull(json);
        boolean foundRarity = false, foundCount = false, foundOffset = false;
        for (JsonElement element : json.getAsJsonArray("placement")) {
            JsonObject modifier = element.getAsJsonObject();
            switch (modifier.get("type").getAsString()) {
                case "minecraft:rarity_filter" -> {
                    assertEquals(rarity, modifier.get("chance").getAsInt()); foundRarity = true;
                }
                case "minecraft:count" -> {
                    assertEquals(tries, modifier.get("count").getAsInt()); foundCount = true;
                }
                case "minecraft:random_offset" -> {
                    assertEquals(7, modifier.getAsJsonObject("xz_spread").get("max").getAsInt());
                    foundOffset = true;
                }
            }
        }
        assertTrue(foundRarity && foundCount && foundOffset);
    }

    private static void assertFeatureReferences(JsonElement json, Set<String> directIds, Set<String> configuredIds) {
        if (json.isJsonObject()) {
            for (var entry : json.getAsJsonObject().entrySet()) {
                JsonElement value = entry.getValue();
                if ((entry.getKey().equals("feature") || entry.getKey().equals("default"))
                        && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                    assertTrue(directIds.contains(value.getAsString()), "Unresolved feature: " + value);
                } else if (entry.getKey().equals("feature") && value.isJsonObject()) {
                    assertFeatureReferences(value, configuredIds, configuredIds);
                } else assertFeatureReferences(value, directIds, configuredIds);
            }
        } else if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(value -> assertFeatureReferences(value, directIds, configuredIds));
        }
    }

    private static Map<String, JsonObject> resources(String directory) throws Exception {
        var location = MangroveConfiguredFeaturesTest.class.getResource("/data/buildscape/" + directory);
        assertNotNull(location, directory);
        Path root = Path.of(location.toURI());
        Map<String, JsonObject> result = new HashMap<>();
        try (var paths = Files.walk(root)) {
            for (Path path : paths.filter(file -> file.toString().endsWith(".json")).toList()) {
                String name = root.relativize(path).toString().replace('\\', '/').replaceFirst("\\.json$", "");
                try (var reader = Files.newBufferedReader(path)) {
                    result.put("buildscape:" + name, JsonParser.parseReader(reader).getAsJsonObject());
                }
            }
        }
        assertFalse(result.isEmpty(), directory);
        return result;
    }

    private static Set<String> registeredTypes() {
        Set<String> types = new HashSet<>();
        BuiltInRegistries.REGISTRY.forEach(registry -> registry.keySet().forEach(id -> types.add(id.toString())));
        types.addAll(Set.of("buildscape:random_state", "buildscape:moss_block_ceiling_placement",
                "buildscape:creaking_heart", "buildscape:mangrove_leave_vine", "buildscape:mangrove_moss_carpet",
                "buildscape:mangrove_propagule", "buildscape:mangrove_root",
                "buildscape:mangrove_random_spread", "buildscape:mangrove_upwards_branching"));
        return types;
    }

    @ParameterizedTest
    @ValueSource(strings = {"mangrove", "tall_mangrove"})
    void configuredFeatureResolvesPlacedReferenceAndUsesRegisteredTypes(String name) throws Exception {
        JsonObject feature = resource("configured_feature/" + name);
        JsonObject placed = resource("placed_feature/" + name + "_checked");
        assertEquals("buildscape:" + name, placed.get("feature").getAsString());
        assertEquals("minecraft:tree", feature.get("type").getAsString());
        Set<String> registeredTypes = new HashSet<>();
        BuiltInRegistries.REGISTRY.forEach(registry -> registry.keySet().forEach(id -> registeredTypes.add(id.toString())));
        // Loader startup registers these WorldGenFactory types. This codec fixture
        // runs against frozen vanilla registries without loading a platform provider.
        registeredTypes.addAll(Set.of("buildscape:random_state", "buildscape:moss_block_ceiling_placement",
                "buildscape:creaking_heart", "buildscape:mangrove_leave_vine", "buildscape:mangrove_moss_carpet",
                "buildscape:mangrove_propagule", "buildscape:mangrove_root",
                "buildscape:mangrove_random_spread", "buildscape:mangrove_upwards_branching"));
        assertRegisteredTypes(feature, registeredTypes);
        JsonObject config = feature.getAsJsonObject("config");
        assertTrue(WorldGenFactory.MangroveUpwardsBranchingTrunkPlacer.CODEC.codec()
                .parse(JsonOps.INSTANCE, config.get("trunk_placer")).isSuccess());
        assertTrue(WorldGenFactory.MangroveRandomSpreadFoliagePlacer.CODEC.codec()
                .parse(JsonOps.INSTANCE, config.get("foliage_placer")).isSuccess());
        assertTrue(WorldGenFactory.MangroveRootDecorator.CODEC.codec()
                .parse(JsonOps.INSTANCE, config.getAsJsonArray("decorators").get(0)).isSuccess());
        assertTrue(WorldGenFactory.MangrovePropaguleDecorator.CODEC.codec()
                .parse(JsonOps.INSTANCE, config.getAsJsonArray("decorators").get(3)).isSuccess());
        assertEquals(name.equals("tall_mangrove") ? 4 : 2,
                config.getAsJsonObject("trunk_placer").get("base_height").getAsInt());
        assertEquals("minecraft:mangrove_log", config.getAsJsonObject("trunk_provider").getAsJsonObject("state").get("Name").getAsString());
        assertEquals("minecraft:mangrove_leaves", config.getAsJsonObject("foliage_provider").getAsJsonObject("state").get("Name").getAsString());
    }

    private static void assertRegisteredTypes(JsonElement json, Set<String> registeredTypes) {
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            if (object.has("type")) assertTrue(registeredTypes.contains(object.get("type").getAsString()), object.toString());
            object.entrySet().forEach(entry -> assertRegisteredTypes(entry.getValue(), registeredTypes));
        } else if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> assertRegisteredTypes(element, registeredTypes));
        }
    }

    private static JsonObject resource(String path) throws Exception {
        return dataResource("/data/buildscape/worldgen/" + path + ".json");
    }

    private static JsonObject dataResource(String fullPath) throws Exception {
        try (var stream = MangroveConfiguredFeaturesTest.class.getResourceAsStream(fullPath)) {
            assertNotNull(stream, fullPath);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
