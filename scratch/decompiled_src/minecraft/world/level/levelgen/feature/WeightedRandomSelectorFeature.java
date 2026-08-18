package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record WeightedRandomSelectorFeature(WeightedList features) implements Feature {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WeightedList.codec(PlacedFeature.CODEC).fieldOf("features").forGetter(WeightedRandomSelectorFeature::features)).apply(i, WeightedRandomSelectorFeature::new));

   public MapCodec codec() {
      return CODEC;
   }

   public Stream getSubFeatures() {
      return this.features.unwrap().stream().flatMap((weighted) -> ((PlacedFeature)((Holder)weighted.value()).value()).getFeatures());
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      Optional featureToPlace = this.features.getRandom(random);
      return featureToPlace.map((placedFeatureHolder) -> ((PlacedFeature)placedFeatureHolder.value()).place(level, chunkGenerator, random, origin)).orElse(false);
   }
}
