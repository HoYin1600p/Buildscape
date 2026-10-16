package com.kingodogo.buildscape.adapter.v118x;

import com.kingodogo.buildscape.worldgen.BiomePlacements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.ArrayList;
import java.util.List;

/** Code-built placements from 4.0.4, shared by the two 1.18.2 loader hooks. */
public final class PlantPlacements {
    private PlantPlacements() {}

    // Match the original biome names, independently of datapack biome tags.
    public static List<String> biomes(BiomePlacements.Entry entry) {
        return switch (entry.biomeTag()) {
            case "minecraft:buildscape/flower_forests" -> List.of("minecraft:flower_forest");
            case "minecraft:buildscape/birch_forests" -> List.of("minecraft:birch_forest", "minecraft:old_growth_birch_forest");
            case "minecraft:buildscape/lush_caves" -> List.of("minecraft:lush_caves");
            case "minecraft:buildscape/swamps" -> List.of("minecraft:swamp");
            default -> throw new IllegalArgumentException("Unknown plant biome group: " + entry.biomeTag());
        };
    }

    public static int rarity(String feature) {
        return switch (feature) {
            case "red_monets", "blue_monets", "purple_monets", "light_blue_monets", "pink_monets", "yellow_monets" -> 4;
            case "all_petals", "wildflowers", "leaf_litter" -> 11;
            case "clover" -> 32;
            case "clover_swamp" -> 16;
            case "all_colored_spore_blossom", "mangrove_propagule_patch_1", "mangrove_propagule_patch_2", "mangrove_propagule_patch_3" -> 5;
            case "mangrove_propagule_patch_4" -> 40;
            case "mangrove_propagule_patch_5" -> 53;
            case "mangrove_propagule_patch_6" -> 107;
            case "mangrove_propagule_patch_7" -> 160;
            case "mangrove_propagule_patch_8" -> 320;
            default -> throw new IllegalArgumentException("Unknown plant placement: " + feature);
        };
    }

    public static PlacedFeature create(String feature) {
        String configured = feature.equals("clover_swamp") ? "clover" : feature;
        ConfiguredFeature<?, ?> value = BuiltinRegistries.CONFIGURED_FEATURE
                .getOptional(new ResourceLocation("buildscape", configured)).orElseThrow(() ->
                        new IllegalStateException("Missing configured plant feature: " + configured));
        boolean propagule = feature.startsWith("mangrove_propagule_patch_");
        boolean swamp = propagule || feature.equals("clover_swamp");
        Block mud = swamp ? block("mud") : Blocks.GRASS_BLOCK;
        Block survival = swamp ? block(propagule ? "mangrove_propagule" : "clover") : Blocks.AIR;
        return create(feature, value, mud, survival);
    }

    private static Block block(String name) {
        return Registry.BLOCK.getOptional(new ResourceLocation("buildscape", name)).orElseThrow(() ->
                new IllegalStateException("Missing plant placement block: " + name));
    }

    // Separate the registered inputs so placement semantics can be checked without loader bootstrap.
    static PlacedFeature create(String feature, ConfiguredFeature<?, ?> configured, Block mud, Block survival) {
        if (feature.equals("all_colored_spore_blossom")) {
            PlacedFeature inner = new PlacedFeature(Holder.direct(configured), List.of(
                    WorldGenFactory.MossBlockCeilingPlacement.INSTANCE,
                    BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(List.of(Blocks.AIR)))));
            ConfiguredFeature<?, ?> patch = new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                    new RandomPatchConfiguration(10, 7, 2, Holder.direct(inner)));
            return new PlacedFeature(Holder.direct(patch), List.of(
                    RarityFilter.onAverageOnceEvery(rarity(feature)), InSquarePlacement.spread(), BiomeFilter.biome()));
        }

        boolean propagule = feature.startsWith("mangrove_propagule_patch_");
        boolean swamp = propagule || feature.equals("clover_swamp");
        List<PlacementModifier> modifiers = new ArrayList<>();
        modifiers.add(RarityFilter.onAverageOnceEvery(rarity(feature)));
        modifiers.add(InSquarePlacement.spread());
        modifiers.add(HeightmapPlacement.onHeightmap(propagule
                ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG));
        modifiers.add(BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(List.of(Blocks.AIR))));
        modifiers.add(BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(
                swamp ? List.of(Blocks.GRASS_BLOCK, mud) : List.of(Blocks.GRASS_BLOCK), new Vec3i(0, -1, 0))));
        if (swamp) modifiers.add(BlockPredicateFilter.forPredicate(
                BlockPredicate.wouldSurvive(survival.defaultBlockState(), BlockPos.ZERO)));
        modifiers.add(BiomeFilter.biome());
        return new PlacedFeature(Holder.direct(configured), List.copyOf(modifiers));
    }
}
