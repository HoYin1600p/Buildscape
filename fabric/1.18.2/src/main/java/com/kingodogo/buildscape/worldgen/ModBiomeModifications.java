package com.kingodogo.buildscape.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import com.kingodogo.buildscape.adapter.v118x.PlantPlacements;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class ModBiomeModifications {
    private ModBiomeModifications() {}

    public static void register() {
        for (BiomePlacements.Entry entry : BiomePlacements.ENTRIES) {
            var biomes = BiomeSelectors.includeByKey(PlantPlacements.biomes(entry).stream()
                    .map(id -> ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(id))).toList());
            for (String feature : entry.features()) {
                com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() ->
                        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation("buildscape", feature),
                                PlantPlacements.create(feature)));
                BiomeModifications.addFeature(biomes, GenerationStep.Decoration.VEGETAL_DECORATION,
                        ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY,
                                new ResourceLocation("buildscape", feature)));
            }
        }
    }
}

