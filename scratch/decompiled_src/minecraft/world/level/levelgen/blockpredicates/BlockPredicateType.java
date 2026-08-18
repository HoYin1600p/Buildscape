package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public interface BlockPredicateType {
   BlockPredicateType MATCHING_BLOCKS = register("matching_blocks", MatchingBlocksPredicate.CODEC);
   BlockPredicateType MATCHING_BLOCK_TAG = register("matching_block_tag", MatchingBlockTagPredicate.CODEC);
   BlockPredicateType MATCHING_FLUIDS = register("matching_fluids", MatchingFluidsPredicate.CODEC);
   BlockPredicateType MATCHING_BIOMES = register("matching_biomes", MatchingBiomesPredicate.CODEC);
   BlockPredicateType HAS_STURDY_FACE = register("has_sturdy_face", HasSturdyFacePredicate.CODEC);
   BlockPredicateType SOLID = register("solid", SolidPredicate.CODEC);
   BlockPredicateType REPLACEABLE = register("replaceable", ReplaceablePredicate.CODEC);
   BlockPredicateType WOULD_SURVIVE = register("would_survive", WouldSurvivePredicate.CODEC);
   BlockPredicateType INSIDE_WORLD_BOUNDS = register("inside_world_bounds", InsideWorldBoundsPredicate.CODEC);
   BlockPredicateType ANY_OF = register("any_of", AnyOfPredicate.CODEC);
   BlockPredicateType ALL_OF = register("all_of", AllOfPredicate.CODEC);
   BlockPredicateType NOT = register("not", NotPredicate.CODEC);
   BlockPredicateType TRUE = register("true", TrueBlockPredicate.CODEC);
   BlockPredicateType UNOBSTRUCTED = register("unobstructed", UnobstructedPredicate.CODEC);
   BlockPredicateType HEIGHT_RANGE = register("height_range", HeightRangePredicate.CODEC);
   BlockPredicateType VOLUME_MATCH = register("volume_match", VolumeMatchPredicate.CODEC);

   MapCodec codec();

   private static BlockPredicateType register(final String id, final MapCodec codec) {
      return (BlockPredicateType)Registry.register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, id, (BlockPredicateType)() -> codec);
   }
}
