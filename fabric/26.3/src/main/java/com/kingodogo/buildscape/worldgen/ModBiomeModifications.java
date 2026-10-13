package com.kingodogo.buildscape.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class ModBiomeModifications {
    private ModBiomeModifications() {}

    public static void register() {
        for (BiomePlacements.Entry entry : BiomePlacements.ENTRIES) {
            var biomes = BiomeSelectors.tag(TagKey.create(Registries.BIOME, Identifier.parse(entry.biomeTag())));
            for (String feature : entry.features()) {
                BiomeModifications.addFeature(biomes, GenerationStep.Decoration.VEGETAL_DECORATION,
                        ResourceKey.create(Registries.PLACED_FEATURE,
                                Identifier.fromNamespaceAndPath("buildscape", feature)));
            }
        }
    }
}
