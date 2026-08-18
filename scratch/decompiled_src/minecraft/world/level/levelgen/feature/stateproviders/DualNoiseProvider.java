package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class DualNoiseProvider extends NoiseProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(InclusiveRange.codec(Codec.INT, 1, 64).fieldOf("variety").forGetter((p) -> p.variety), NormalNoise.DIRECT_CODEC.fieldOf("slow_noise").forGetter((p) -> p.slowNoiseParameters), ExtraCodecs.POSITIVE_FLOAT.fieldOf("slow_scale").forGetter((p) -> p.slowScale)).and(noiseProviderCodec(i)).apply(i, DualNoiseProvider::new));
   private final InclusiveRange variety;
   private final NormalNoise slowNoiseParameters;
   private final float slowScale;
   private final Noise slowNoise;

   public DualNoiseProvider(final InclusiveRange variety, final NormalNoise slowNoiseParameters, final float slowScale, final long seed, final NormalNoise parameters, final float scale, final List states) {
      super(seed, parameters, scale, states);
      this.variety = variety;
      this.slowNoiseParameters = slowNoiseParameters;
      this.slowScale = slowScale;
      this.slowNoise = slowNoiseParameters.create(new WorldgenRandom(new LegacyRandomSource(seed)));
   }

   public MapCodec codec() {
      return CODEC;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      double varietyNoise = (double)this.getSlowNoiseValue(pos);
      int localVariety = (int)Mth.clampedMap(varietyNoise, -1.0D, 1.0D, (double)((Integer)this.variety.minInclusive()).intValue(), (double)(this.variety.maxInclusive() + 1));
      List possibleStates = Lists.newArrayListWithCapacity(localVariety);

      for(int i = 0; i < localVariety; ++i) {
         possibleStates.add(this.getRandomState(this.states, this.getSlowNoiseValue(pos.offset(i * '픑', 0, i * '薺'))));
      }

      return this.getRandomState(possibleStates, pos, (double)this.scale);
   }

   protected float getSlowNoiseValue(final BlockPos pos) {
      return this.slowNoise.get((double)((float)pos.getX() * this.slowScale), (double)((float)pos.getY() * this.slowScale), (double)((float)pos.getZ() * this.slowScale));
   }
}
