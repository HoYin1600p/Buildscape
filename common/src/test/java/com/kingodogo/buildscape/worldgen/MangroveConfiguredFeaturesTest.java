package com.kingodogo.buildscape.worldgen;

import com.google.gson.*;
import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.WorldGenFactory;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MangroveConfiguredFeaturesTest {
    @BeforeAll
    static void bootstrap() {
        TestBootstrap.initialize();
        WorldGenFactory.register();
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
        assertEquals("buildscape:mangrove_log", config.getAsJsonObject("trunk_provider").getAsJsonObject("state").get("Name").getAsString());
        assertEquals("buildscape:mangrove_leaves", config.getAsJsonObject("foliage_provider").getAsJsonObject("state").get("Name").getAsString());
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
        String fullPath = "/data/buildscape/worldgen/" + path + ".json";
        try (var stream = MangroveConfiguredFeaturesTest.class.getResourceAsStream(fullPath)) {
            assertNotNull(stream, fullPath);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
