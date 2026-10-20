package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModBiomeModifications {

    private static BlockPredicate matchesBlocksBelow(List<Block> blocks) {
        return BlockPredicate.matchesBlocks(blocks, new Vec3i(0, -1, 0));
    }

    private static BlockPredicate mossBlockAboveAir() {
        return BlockPredicate.matchesBlocks(List.of(Blocks.MOSS_BLOCK), new Vec3i(0, 1, 0));
    }

    private static void addFeatureToBiome(
            BiomeLoadingEvent event,
            ConfiguredFeature<?, ?> feature,
            int rarity) {
        if (feature != null) {
            PlacedFeature placed = new PlacedFeature(
                    Holder.direct(feature),
                    List.of(
                            RarityFilter.onAverageOnceEvery(rarity),
                            InSquarePlacement.spread(),
                            HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                            BlockPredicateFilter.forPredicate(
                                    BlockPredicate.matchesBlocks(
                                            List.of(Blocks.AIR))),
                            BlockPredicateFilter.forPredicate(
                                    matchesBlocksBelow(
                                            List.of(Blocks.GRASS_BLOCK))),
                            BiomeFilter.biome()));
            event
                    .getGeneration()
                    .addFeature(
                            GenerationStep.Decoration.VEGETAL_DECORATION,
                            Holder.direct(placed));
        }
    }

    private static void addPetalColorToBiome(
            BiomeLoadingEvent event,
            ConfiguredFeature<?, ?> state1N,
            ConfiguredFeature<?, ?> state1S,
            ConfiguredFeature<?, ?> state1E,
            ConfiguredFeature<?, ?> state1W,
            ConfiguredFeature<?, ?> state2N,
            ConfiguredFeature<?, ?> state2S,
            ConfiguredFeature<?, ?> state2E,
            ConfiguredFeature<?, ?> state2W,
            ConfiguredFeature<?, ?> state3N,
            ConfiguredFeature<?, ?> state3S,
            ConfiguredFeature<?, ?> state3E,
            ConfiguredFeature<?, ?> state3W,
            ConfiguredFeature<?, ?> state4N,
            ConfiguredFeature<?, ?> state4S,
            ConfiguredFeature<?, ?> state4E,
            ConfiguredFeature<?, ?> state4W,
            int rarity) {
        addFeatureToBiome(event, state1N, rarity);
        addFeatureToBiome(event, state1S, rarity);
        addFeatureToBiome(event, state1E, rarity);
        addFeatureToBiome(event, state1W, rarity);
        addFeatureToBiome(event, state2N, rarity);
        addFeatureToBiome(event, state2S, rarity);
        addFeatureToBiome(event, state2E, rarity);
        addFeatureToBiome(event, state2W, rarity);
        addFeatureToBiome(event, state3N, rarity);
        addFeatureToBiome(event, state3S, rarity);
        addFeatureToBiome(event, state3E, rarity);
        addFeatureToBiome(event, state3W, rarity);
        addFeatureToBiome(event, state4N, rarity);
        addFeatureToBiome(event, state4S, rarity);
        addFeatureToBiome(event, state4E, rarity);
        addFeatureToBiome(event, state4W, rarity);
    }

    private static void addPropagulePatchToBiome(
            BiomeLoadingEvent event,
            ConfiguredFeature<?, ?> feature,
            int rarity) {
        if (feature != null) {
            PlacedFeature placed = new PlacedFeature(
                    Holder.direct(feature),
                    List.of(
                            RarityFilter.onAverageOnceEvery(rarity),
                            InSquarePlacement.spread(),
                            HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                            BlockPredicateFilter.forPredicate(
                                    BlockPredicate.matchesBlocks(
                                            List.of(Blocks.AIR))),
                            BlockPredicateFilter.forPredicate(
                                    matchesBlocksBelow(
                                            new ArrayList<Block>() {
                                                {
                                                    add(Blocks.GRASS_BLOCK);
                                                    add(ModBlocks.MUD.get());
                                                }
                                            })),
                            BlockPredicateFilter.forPredicate(
                                    BlockPredicate.wouldSurvive(
                                            ModBlocks.MANGROVE_PROPAGULE.get()
                                                    .defaultBlockState(),
                                            BlockPos.ZERO)),
                            BiomeFilter.biome()));
            event
                    .getGeneration()
                    .addFeature(
                            GenerationStep.Decoration.VEGETAL_DECORATION,
                            Holder.direct(placed));
        }
    }

    private static void addCloverStatesToBiome(
            BiomeLoadingEvent event,
            int rarity) {
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_1_N.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_1_S.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_1_E.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_1_W.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_2_N.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_2_S.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_2_E.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_2_W.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_3_N.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_3_S.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_3_E.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_3_W.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_4_N.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_4_S.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_4_E.get(), rarity);
        addFeatureToBiome(event, ModConfiguredFeatures.CLOVER_4_W.get(), rarity);
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if (event.getName() == null) {
            return;
        }
        if (event.getName().equals(Biomes.FLOWER_FOREST.location())) {
            addFeatureToBiome(event, ModConfiguredFeatures.RED_MONETS.get(), 4);
            addFeatureToBiome(event, ModConfiguredFeatures.BLUE_MONETS.get(), 4);
            addFeatureToBiome(event, ModConfiguredFeatures.PURPLE_MONETS.get(), 4);
            addFeatureToBiome(
                    event,
                    ModConfiguredFeatures.LIGHT_BLUE_MONETS.get(),
                    4);
            addFeatureToBiome(event, ModConfiguredFeatures.PINK_MONETS.get(), 4);
            addFeatureToBiome(event, ModConfiguredFeatures.YELLOW_MONETS.get(), 4);
        }

        if (event.getName().equals(Biomes.BIRCH_FOREST.location()) ||
                event.getName().equals(Biomes.OLD_GROWTH_BIRCH_FOREST.location())) {
            addFeatureToBiome(event, ModConfiguredFeatures.ALL_PETALS.get(), 11);
            addFeatureToBiome(event, ModConfiguredFeatures.CLOVER.get(), 32);
            addFeatureToBiome(event, ModConfiguredFeatures.WILDFLOWERS.get(), 11);
            addFeatureToBiome(event, ModConfiguredFeatures.LEAF_LITTER.get(), 11);
        }

        if (event.getName().equals(Biomes.LUSH_CAVES.location())) {
            if (ModConfiguredFeatures.ALL_COLORED_SPORE_BLOSSOM.isPresent()) {
                ConfiguredFeature<?, ?> sporeBlossomFeature = ModConfiguredFeatures.ALL_COLORED_SPORE_BLOSSOM.get();

                PlacedFeature innerPlaced = new PlacedFeature(
                        Holder.direct(sporeBlossomFeature),
                        List.of(
                                MossBlockCeilingPlacement.INSTANCE,
                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.matchesBlocks(
                                                List.of(Blocks.AIR)))));

                ConfiguredFeature<?, ?> patchFeature = new ConfiguredFeature<>(
                        Feature.RANDOM_PATCH,
                        new RandomPatchConfiguration(
                                10,
                                7,
                                2,
                                Holder.direct(innerPlaced)));

                PlacedFeature sporeBlossomPlaced = new PlacedFeature(
                        Holder.direct(patchFeature),
                        List.of(
                                RarityFilter.onAverageOnceEvery(5),
                                InSquarePlacement.spread(),
                                BiomeFilter.biome()));

                event
                        .getGeneration()
                        .addFeature(
                                GenerationStep.Decoration.VEGETAL_DECORATION,
                                Holder.direct(sporeBlossomPlaced));
            }
        }

        if (event.getName().equals(Biomes.SWAMP.location())) {
            ConfiguredFeature<?, ?> cloverFeature = ModConfiguredFeatures.CLOVER.get();
            if (cloverFeature != null) {
                PlacedFeature cloverPlaced = new PlacedFeature(
                        Holder.direct(cloverFeature),
                        List.of(
                                RarityFilter.onAverageOnceEvery(16),
                                InSquarePlacement.spread(),
                                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.matchesBlocks(
                                                List.of(Blocks.AIR))),
                                BlockPredicateFilter.forPredicate(
                                        matchesBlocksBelow(
                                                new ArrayList<Block>() {
                                                    {
                                                        add(Blocks.GRASS_BLOCK);
                                                        add(ModBlocks.MUD.get());
                                                    }
                                                })),
                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.wouldSurvive(
                                                ModBlocks.CLOVER.get()
                                                        .defaultBlockState(),
                                                BlockPos.ZERO)),
                                BiomeFilter.biome()));
                event
                        .getGeneration()
                        .addFeature(
                                GenerationStep.Decoration.VEGETAL_DECORATION,
                                Holder.direct(cloverPlaced));
            }

            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_1.get(),
                    5);
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_2.get(),
                    5);
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_3.get(),
                    5);

            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_4.get(),
                    40);
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_5.get(),
                    53);
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_6.get(),
                    107);
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_7.get(),
                    160);
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_8.get(),
                    320);
        }
    }
}
