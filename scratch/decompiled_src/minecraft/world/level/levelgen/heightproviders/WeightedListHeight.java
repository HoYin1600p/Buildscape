package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class WeightedListHeight extends HeightProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WeightedList.nonEmptyCodec(HeightProvider.CODEC).fieldOf("distribution").forGetter((c) -> c.distribution)).apply(i, WeightedListHeight::new));
   private final WeightedList distribution;

   public WeightedListHeight(final WeightedList distribution) {
      this.distribution = distribution;
   }

   public int sample(final RandomSource random, final WorldGenerationContext heightAccessor) {
      return ((HeightProvider)this.distribution.getRandomOrThrow(random)).sample(random, heightAccessor);
   }

   public HeightProviderType getType() {
      return HeightProviderType.WEIGHTED_LIST;
   }
}
