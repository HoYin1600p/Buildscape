package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class AquaticPlacements {
   public static final ResourceKey SEAGRASS_WARM = PlacementUtils.createKey("seagrass_warm");
   public static final ResourceKey SEAGRASS_NORMAL = PlacementUtils.createKey("seagrass_normal");
   public static final ResourceKey SEAGRASS_COLD = PlacementUtils.createKey("seagrass_cold");
   public static final ResourceKey SEAGRASS_RIVER = PlacementUtils.createKey("seagrass_river");
   public static final ResourceKey SEAGRASS_SWAMP = PlacementUtils.createKey("seagrass_swamp");
   public static final ResourceKey SEAGRASS_DEEP_WARM = PlacementUtils.createKey("seagrass_deep_warm");
   public static final ResourceKey SEAGRASS_DEEP = PlacementUtils.createKey("seagrass_deep");
   public static final ResourceKey SEAGRASS_DEEP_COLD = PlacementUtils.createKey("seagrass_deep_cold");
   public static final ResourceKey SEA_PICKLE = PlacementUtils.createKey("sea_pickle");
   public static final ResourceKey KELP_COLD = PlacementUtils.createKey("kelp_cold");
   public static final ResourceKey KELP_WARM = PlacementUtils.createKey("kelp_warm");
   public static final ResourceKey WARM_OCEAN_VEGETATION = PlacementUtils.createKey("warm_ocean_vegetation");

   private static List seagrassPlacement(final int count) {
      return List.of(InSquarePlacement.spread(), CountPlacement.of(count), OffsetPlacement.ofTriangle(7, 0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.WATER)), BiomeFilter.biome());
   }

   public static void bootstrap(final BootstrapContext context) {
      HolderGetter configuredFeatures = context.lookup(Registries.FEATURE);
      Holder.Reference seagrassShort = configuredFeatures.getOrThrow(AquaticFeatures.SEAGRASS_SHORT);
      Holder.Reference seagrassSlightlyLessShort = configuredFeatures.getOrThrow(AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT);
      Holder.Reference seagrassMid = configuredFeatures.getOrThrow(AquaticFeatures.SEAGRASS_MID);
      Holder.Reference seagrassTall = configuredFeatures.getOrThrow(AquaticFeatures.SEAGRASS_TALL);
      Holder.Reference seaPickle = configuredFeatures.getOrThrow(AquaticFeatures.SEA_PICKLE);
      Holder.Reference kelp = configuredFeatures.getOrThrow(AquaticFeatures.KELP);
      Holder.Reference warmOceanVegetation = configuredFeatures.getOrThrow(AquaticFeatures.WARM_OCEAN_VEGETATION);
      PlacementUtils.register(context, SEAGRASS_WARM, seagrassShort, seagrassPlacement(80));
      PlacementUtils.register(context, SEAGRASS_NORMAL, seagrassShort, seagrassPlacement(48));
      PlacementUtils.register(context, SEAGRASS_COLD, seagrassShort, seagrassPlacement(32));
      PlacementUtils.register(context, SEAGRASS_RIVER, seagrassSlightlyLessShort, seagrassPlacement(48));
      PlacementUtils.register(context, SEAGRASS_SWAMP, seagrassMid, seagrassPlacement(64));
      PlacementUtils.register(context, SEAGRASS_DEEP_WARM, seagrassTall, seagrassPlacement(80));
      PlacementUtils.register(context, SEAGRASS_DEEP, seagrassTall, seagrassPlacement(48));
      PlacementUtils.register(context, SEAGRASS_DEEP_COLD, seagrassTall, seagrassPlacement(40));
      PlacementUtils.register(context, SEA_PICKLE, seaPickle, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), CountPlacement.of(20), OffsetPlacement.ofTriangle(7, 0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.WATER)), BiomeFilter.biome());
      PlacementUtils.register(context, KELP_COLD, kelp, NoiseBasedCountPlacement.of(120, 80.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.WATER), BlockPredicate.matchesBlocks(Direction.UP, Blocks.WATER), BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.CANNOT_SUPPORT_KELP)))), BiomeFilter.biome());
      PlacementUtils.register(context, KELP_WARM, kelp, NoiseBasedCountPlacement.of(80, 80.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.WATER), BlockPredicate.matchesBlocks(Direction.UP, Blocks.WATER), BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.CANNOT_SUPPORT_KELP)))), BiomeFilter.biome());
      PlacementUtils.register(context, WARM_OCEAN_VEGETATION, warmOceanVegetation, NoiseBasedCountPlacement.of(20, 400.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
   }
}
