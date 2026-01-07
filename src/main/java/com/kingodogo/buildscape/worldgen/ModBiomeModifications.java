package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ModBiomeModifications {

    private static BlockPredicate matchesBlocksBelow(List<Block> blocks) {
        return new BlockPredicate() {
            @Override
            public boolean test(WorldGenLevel level, BlockPos pos) {
                BlockPos belowPos = pos.below();
                BlockState belowState = level.getBlockState(belowPos);
                return blocks.contains(belowState.getBlock());
            }

            @Override
            public net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType<
                    ?
                    > type() {
                return null;
            }
        };
    }

    private static net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate mossBlockAboveAir() {
        return new net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate() {
            @Override
            public boolean test(
                    net.minecraft.world.level.WorldGenLevel level,
                    net.minecraft.core.BlockPos pos
            ) {
                if (!level.getBlockState(pos).isAir()) {
                    return false;
                }
                BlockPos abovePos = pos.above();
                return level
                        .getBlockState(abovePos)
                        .is(net.minecraft.world.level.block.Blocks.MOSS_BLOCK);
            }

            @Override
            public net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType<
                    ?
                    > type() {
                return null;
            }
        };
    }

    private static void addFeatureToBiome(
            BiomeLoadingEvent event,
            ConfiguredFeature<?, ?> feature,
            int rarity
    ) {
        if (feature != null) {
            PlacedFeature placed = new PlacedFeature(
                    Holder.direct(feature),
                    List.of(
                            RarityFilter.onAverageOnceEvery(rarity),
                            InSquarePlacement.spread(),
                            HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                            BlockPredicateFilter.forPredicate(
                                    net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                            List.of(net.minecraft.world.level.block.Blocks.AIR)
                                    )
                            ),
                            BlockPredicateFilter.forPredicate(
                                    matchesBlocksBelow(
                                            List.of(net.minecraft.world.level.block.Blocks.GRASS_BLOCK)
                                    )
                            ),
                            BiomeFilter.biome()
                    )
            );
            event
                    .getGeneration()
                    .addFeature(
                            GenerationStep.Decoration.VEGETAL_DECORATION,
                            Holder.direct(placed)
                    );
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
            int rarity
    ) {
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
            int rarity
    ) {
        if (feature != null) {
            PlacedFeature placed = new PlacedFeature(
                    Holder.direct(feature),
                    List.of(
                            RarityFilter.onAverageOnceEvery(rarity),
                            InSquarePlacement.spread(),
                            HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
                            BlockPredicateFilter.forPredicate(
                                    net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                            List.of(net.minecraft.world.level.block.Blocks.AIR)
                                    )
                            ),
                            BlockPredicateFilter.forPredicate(
                                    matchesBlocksBelow(
                                            new java.util.ArrayList<net.minecraft.world.level.block.Block>() {
                                                {
                                                    add(net.minecraft.world.level.block.Blocks.GRASS_BLOCK);
                                                    add(com.kingodogo.buildscape.block.ModBlocks.MUD.get());
                                                }
                                            }
                                    )
                            ),
                            BlockPredicateFilter.forPredicate(
                                    net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.wouldSurvive(
                                            com.kingodogo.buildscape.block.ModBlocks.MANGROVE_PROPAGULE.get()
                                                    .defaultBlockState(),
                                            net.minecraft.core.BlockPos.ZERO
                                    )
                            ),
                            BiomeFilter.biome()
                    )
            );
            event
                    .getGeneration()
                    .addFeature(
                            GenerationStep.Decoration.VEGETAL_DECORATION,
                            Holder.direct(placed)
                    );
        }
    }

    private static void addCloverStatesToBiome(
            BiomeLoadingEvent event,
            int rarity
    ) {
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
        if (event.getName().equals(Biomes.FLOWER_FOREST.location())) {
            addFeatureToBiome(event, ModConfiguredFeatures.RED_MONETS.get(), 4);
            addFeatureToBiome(event, ModConfiguredFeatures.BLUE_MONETS.get(), 4);
            addFeatureToBiome(event, ModConfiguredFeatures.PURPLE_MONETS.get(), 4);
            addFeatureToBiome(
                    event,
                    ModConfiguredFeatures.LIGHT_BLUE_MONETS.get(),
                    4
            );
            addFeatureToBiome(event, ModConfiguredFeatures.PINK_MONETS.get(), 4);
            addFeatureToBiome(event, ModConfiguredFeatures.YELLOW_MONETS.get(), 4);
        }

        if (
                event.getName().equals(Biomes.BIRCH_FOREST.location()) ||
                        event.getName().equals(Biomes.OLD_GROWTH_BIRCH_FOREST.location())
        ) {
            addFeatureToBiome(event, ModConfiguredFeatures.ALL_PETALS.get(), 11);
            addFeatureToBiome(event, ModConfiguredFeatures.CLOVER.get(), 32);
        }

        if (event.getName().equals(Biomes.LUSH_CAVES.location())) {
            if (ModConfiguredFeatures.ALL_COLORED_SPORE_BLOSSOM.isPresent()) {
                ConfiguredFeature<?, ?> sporeBlossomFeature =
                        ModConfiguredFeatures.ALL_COLORED_SPORE_BLOSSOM.get();

                PlacedFeature innerPlaced = new PlacedFeature(
                        Holder.direct(sporeBlossomFeature),
                        List.of(
                                com.kingodogo.buildscape.worldgen.MossBlockCeilingPlacement.INSTANCE,
                                BlockPredicateFilter.forPredicate(
                                        net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                                List.of(net.minecraft.world.level.block.Blocks.AIR)
                                        )
                                )
                        )
                );

                ConfiguredFeature<?, ?> patchFeature = new ConfiguredFeature<>(
                        net.minecraft.world.level.levelgen.feature.Feature.RANDOM_PATCH,
                        new net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration(
                                10,
                                7,
                                2,
                                Holder.direct(innerPlaced)
                        )
                );

                PlacedFeature sporeBlossomPlaced = new PlacedFeature(
                        Holder.direct(patchFeature),
                        List.of(
                                RarityFilter.onAverageOnceEvery(5),
                                InSquarePlacement.spread(),
                                BiomeFilter.biome()
                        )
                );

                event
                        .getGeneration()
                        .addFeature(
                                GenerationStep.Decoration.VEGETAL_DECORATION,
                                Holder.direct(sporeBlossomPlaced)
                        );
            }
        }

        if (event.getName().equals(Biomes.SWAMP.location())) {
            ConfiguredFeature<?, ?> cloverFeature =
                    ModConfiguredFeatures.CLOVER.get();
            if (cloverFeature != null) {
                PlacedFeature cloverPlaced = new PlacedFeature(
                        Holder.direct(cloverFeature),
                        List.of(
                                RarityFilter.onAverageOnceEvery(16),
                                InSquarePlacement.spread(),
                                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                                BlockPredicateFilter.forPredicate(
                                        net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                                List.of(net.minecraft.world.level.block.Blocks.AIR)
                                        )
                                ),
                                BlockPredicateFilter.forPredicate(
                                        matchesBlocksBelow(
                                                new java.util.ArrayList<
                                                        net.minecraft.world.level.block.Block
                                                        >() {
                                                    {
                                                        add(net.minecraft.world.level.block.Blocks.GRASS_BLOCK);
                                                        add(com.kingodogo.buildscape.block.ModBlocks.MUD.get());
                                                    }
                                                }
                                        )
                                ),
                                BlockPredicateFilter.forPredicate(
                                        net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.wouldSurvive(
                                                com.kingodogo.buildscape.block.ModBlocks.CLOVER.get()
                                                        .defaultBlockState(),
                                                net.minecraft.core.BlockPos.ZERO
                                        )
                                ),
                                BiomeFilter.biome()
                        )
                );
                event
                        .getGeneration()
                        .addFeature(
                                GenerationStep.Decoration.VEGETAL_DECORATION,
                                Holder.direct(cloverPlaced)
                        );
            }

            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_1.get(),
                    24
            );
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_2.get(),
                    24
            );
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_3.get(),
                    24
            );

            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_4.get(),
                    200
            );
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_5.get(),
                    267
            );
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_6.get(),
                    533
            );
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_7.get(),
                    800
            );
            addPropagulePatchToBiome(
                    event,
                    ModConfiguredFeatures.MANGROVE_PROPAGULE_PATCH_8.get(),
                    1600
            );
        }
    }
}
