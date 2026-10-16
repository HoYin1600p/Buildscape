package com.kingodogo.buildscape.adapter.v118x;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.worldgen.BiomePlacements;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlantPlacementsTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void both1182LoadersUseEveryArchivePlantInTheOriginalBiomes() throws Exception {
        JsonObject reference;
        try (var reader = Files.newBufferedReader(commonRoot().resolve("src/test/resources/worldgen/plant-biomes-4.0.4.json"))) {
            reference = JsonParser.parseReader(reader).getAsJsonObject();
        }
        assertEquals(reference.size(), BiomePlacements.ENTRIES.size());
        for (var entry : BiomePlacements.ENTRIES) {
            String group = entry.biomeTag().substring(entry.biomeTag().lastIndexOf('/') + 1);
            JsonObject expected = reference.getAsJsonObject(group);
            assertEquals(expected.getAsJsonObject("features").keySet(), new java.util.HashSet<>(entry.features()));
            assertEquals(java.util.stream.StreamSupport.stream(expected.getAsJsonArray("biomes").spliterator(), false)
                            .map(value -> value.getAsString()).toList(),
                    PlantPlacements.biomes(entry));
            for (String feature : entry.features())
                assertEquals(expected.getAsJsonObject("features").get(feature).getAsInt(), PlantPlacements.rarity(feature), feature);
        }
        Path repo = commonRoot().getParent();
        for (String loader : List.of("forge", "fabric")) {
            String hook = Files.readString(repo.resolve(loader + "/1.18.2/src/main/java/com/kingodogo/buildscape/worldgen/ModBiomeModifications.java"));
            assertTrue(hook.contains("BiomePlacements.ENTRIES"), loader);
            assertTrue(hook.contains("PlantPlacements.biomes(entry)"), loader);
            assertTrue(hook.contains("PlantPlacements.create(feature)"), loader);
            assertTrue(hook.contains("VEGETAL_DECORATION"), loader);
        }
        String events = Files.readString(repo.resolve("forge/1.18.2/src/main/java/com/kingodogo/buildscape/event/ForgeGameplayEvents.java"));
        assertTrue(events.contains("bus.addListener(com.kingodogo.buildscape.worldgen.ModBiomeModifications::onBiomeLoading)"));
        String fabric = Files.readString(repo.resolve("fabric/1.18.2/src/main/java/com/kingodogo/buildscape/worldgen/ModBiomeModifications.java"));
        assertTrue(fabric.contains("Registry.register(BuiltinRegistries.PLACED_FEATURE"));
        assertFalse(fabric.contains("BiomeSelectors.tag("), "1.18.2 must not depend on absent biome tags");
    }

    @Test
    void placementOrderAndFiltersMatch404() {
        var configured = new ConfiguredFeature<>(Feature.NO_OP, NoneFeatureConfiguration.INSTANCE);
        for (var entry : BiomePlacements.ENTRIES) for (String feature : entry.features()) {
            // Vanilla substitutes let this test inspect filters without registering mod blocks.
            var placed = PlantPlacements.create(feature, configured, Blocks.DIRT, Blocks.GRASS);
            var modifiers = placed.placement();
            assertEquals(PlantPlacements.rarity(feature), json(modifiers.get(0)).get("chance").getAsInt());
            assertEquals("minecraft:in_square", json(modifiers.get(1)).get("type").getAsString());
            assertEquals("minecraft:biome", json(modifiers.get(modifiers.size() - 1)).get("type").getAsString());
            if (feature.equals("all_colored_spore_blossom")) {
                assertEquals(3, modifiers.size());
                assertSame(Feature.RANDOM_PATCH, placed.feature().value().feature());
                var patch = (RandomPatchConfiguration) placed.feature().value().config();
                assertEquals(10, patch.tries());
                assertEquals(7, patch.xzSpread());
                assertEquals(2, patch.ySpread());
                var inner = patch.feature().value();
                assertSame(configured, inner.feature().value());
                assertEquals(2, inner.placement().size());
                assertSame(WorldGenFactory.MossBlockCeilingPlacement.INSTANCE, inner.placement().get(0));
                assertEquals(List.of("minecraft:air"), blockIds(json(inner.placement().get(1)).getAsJsonObject("predicate")));
            } else {
                boolean propagule = feature.startsWith("mangrove_propagule_patch_");
                boolean swamp = propagule || feature.equals("clover_swamp");
                assertSame(configured, placed.feature().value());
                assertEquals(swamp ? 7 : 6, modifiers.size());
                assertEquals(propagule ? "OCEAN_FLOOR_WG" : "WORLD_SURFACE_WG", json(modifiers.get(2)).get("heightmap").getAsString());
                assertEquals(List.of("minecraft:air"), blockIds(json(modifiers.get(3)).getAsJsonObject("predicate")));
                JsonObject ground = json(modifiers.get(4)).getAsJsonObject("predicate");
                assertEquals(JsonParser.parseString("[0,-1,0]"), ground.get("offset"));
                assertEquals(swamp ? List.of("minecraft:grass_block", "minecraft:dirt") : List.of("minecraft:grass_block"), blockIds(ground));
                if (swamp) {
                    JsonObject survival = json(modifiers.get(5)).getAsJsonObject("predicate");
                    assertEquals("minecraft:would_survive", survival.get("type").getAsString());
                    assertEquals("minecraft:grass", survival.getAsJsonObject("state").get("Name").getAsString());
                }
            }
        }
    }

    private static JsonObject json(PlacementModifier modifier) {
        return PlacementModifier.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get()), modifier)
                .getOrThrow(false, message -> { throw new AssertionError(message); }).getAsJsonObject();
    }

    private static List<String> blockIds(JsonObject predicate) {
        // The registry-aware holder-set codec encodes a singleton as a string.
        var blocks = predicate.get("blocks");
        return blocks.isJsonArray()
                ? java.util.stream.StreamSupport.stream(blocks.getAsJsonArray().spliterator(), false)
                        .map(value -> value.getAsString()).toList()
                : List.of(blocks.getAsString());
    }

    private static Path commonRoot() {
        return Path.of(System.getProperty("buildscape.v118xMixinConfig", "src/v118x/resources/buildscape.mixins.json"))
                .toAbsolutePath().getParent().getParent().getParent().getParent();
    }
}
