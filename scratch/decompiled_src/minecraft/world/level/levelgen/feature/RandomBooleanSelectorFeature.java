package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record RandomBooleanSelectorFeature(Holder featureTrue, Holder featureFalse) implements Feature {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(PlacedFeature.CODEC.fieldOf("feature_true").forGetter(RandomBooleanSelectorFeature::featureTrue), PlacedFeature.CODEC.fieldOf("feature_false").forGetter(RandomBooleanSelectorFeature::featureFalse)).apply(i, RandomBooleanSelectorFeature::new));

   public MapCodec codec() {
      return CODEC;
   }

   public Stream getSubFeatures() {
      return Stream.concat(((PlacedFeature)this.featureTrue.value()).getFeatures(), ((PlacedFeature)this.featureFalse.value()).getFeatures());
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      boolean result = random.nextBoolean();
      return ((PlacedFeature)(result ? this.featureTrue : this.featureFalse).value()).place(level, chunkGenerator, random, origin);
   }
}
