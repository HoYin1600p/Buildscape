package net.minecraft.data.worldgen.placement;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.PileFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VillagePlacements {
   public static final ResourceKey PILE_HAY_VILLAGE = PlacementUtils.createKey("pile_hay");
   public static final ResourceKey PILE_MELON_VILLAGE = PlacementUtils.createKey("pile_melon");
   public static final ResourceKey PILE_SNOW_VILLAGE = PlacementUtils.createKey("pile_snow");
   public static final ResourceKey PILE_ICE_VILLAGE = PlacementUtils.createKey("pile_ice");
   public static final ResourceKey PILE_PUMPKIN_VILLAGE = PlacementUtils.createKey("pile_pumpkin");
   public static final ResourceKey OAK_VILLAGE = PlacementUtils.createKey("oak");
   public static final ResourceKey ACACIA_VILLAGE = PlacementUtils.createKey("acacia");
   public static final ResourceKey SPRUCE_VILLAGE = PlacementUtils.createKey("spruce");
   public static final ResourceKey PINE_VILLAGE = PlacementUtils.createKey("pine");
   public static final ResourceKey PATCH_CACTUS_VILLAGE = PlacementUtils.createKey("patch_cactus");
   public static final ResourceKey FLOWER_PLAIN_VILLAGE = PlacementUtils.createKey("flower_plain");
   public static final ResourceKey PATCH_TAIGA_GRASS_VILLAGE = PlacementUtils.createKey("patch_taiga_grass");
   public static final ResourceKey PATCH_BERRY_BUSH_VILLAGE = PlacementUtils.createKey("patch_berry_bush");

   public static void bootstrap(final BootstrapContext context) {
      HolderGetter configuredFeatures = context.lookup(Registries.FEATURE);
      Holder pileHay = configuredFeatures.getOrThrow(PileFeatures.PILE_HAY);
      Holder pileMelon = configuredFeatures.getOrThrow(PileFeatures.PILE_MELON);
      Holder pileSnow = configuredFeatures.getOrThrow(PileFeatures.PILE_SNOW);
      Holder pileIce = configuredFeatures.getOrThrow(PileFeatures.PILE_ICE);
      Holder pilePumpkin = configuredFeatures.getOrThrow(PileFeatures.PILE_PUMPKIN);
      Holder oak = configuredFeatures.getOrThrow(TreeFeatures.OAK);
      Holder acacia = configuredFeatures.getOrThrow(TreeFeatures.ACACIA);
      Holder spruce = configuredFeatures.getOrThrow(TreeFeatures.SPRUCE);
      Holder pine = configuredFeatures.getOrThrow(TreeFeatures.PINE);
      Holder cactus = configuredFeatures.getOrThrow(VegetationFeatures.CACTUS);
      Holder flowerPlain = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_PLAIN);
      Holder taigaGrass = configuredFeatures.getOrThrow(VegetationFeatures.TAIGA_GRASS);
      Holder berryBush = configuredFeatures.getOrThrow(VegetationFeatures.BERRY_BUSH);
      PlacementUtils.register(context, PILE_HAY_VILLAGE, pileHay);
      PlacementUtils.register(context, PILE_MELON_VILLAGE, pileMelon);
      PlacementUtils.register(context, PILE_SNOW_VILLAGE, pileSnow);
      PlacementUtils.register(context, PILE_ICE_VILLAGE, pileIce);
      PlacementUtils.register(context, PILE_PUMPKIN_VILLAGE, pilePumpkin);
      PlacementUtils.register(context, OAK_VILLAGE, oak, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
      PlacementUtils.register(context, ACACIA_VILLAGE, acacia, PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
      PlacementUtils.register(context, SPRUCE_VILLAGE, spruce, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(context, PINE_VILLAGE, pine, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(context, PATCH_CACTUS_VILLAGE, cactus, CountPlacement.of(10), OffsetPlacement.ofTriangle(7, 3), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(Blocks.CACTUS))));
      PlacementUtils.register(context, FLOWER_PLAIN_VILLAGE, flowerPlain, CountPlacement.of(64), OffsetPlacement.ofTriangle(6, 2), BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE));
      PlacementUtils.register(context, PATCH_TAIGA_GRASS_VILLAGE, taigaGrass, CountPlacement.of(32), OffsetPlacement.ofTriangle(7, 3), BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE));
      PlacementUtils.register(context, PATCH_BERRY_BUSH_VILLAGE, berryBush, CountPlacement.of(96), OffsetPlacement.ofTriangle(7, 3), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(Direction.DOWN, Blocks.GRASS_BLOCK))));
   }
}
