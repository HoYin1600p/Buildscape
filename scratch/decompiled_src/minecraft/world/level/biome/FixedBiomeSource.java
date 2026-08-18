package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import org.jspecify.annotations.Nullable;

public class FixedBiomeSource extends BiomeSource implements BiomeResolver {
   public static final MapCodec CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, (s) -> s.biome).stable();
   private final Holder biome;

   public FixedBiomeSource(final Holder biome) {
      this.biome = biome;
   }

   protected Stream collectPossibleBiomes() {
      return Stream.of(this.biome);
   }

   protected MapCodec codec() {
      return CODEC;
   }

   public BiomeResolver createResolver(final Climate.Sampler sampler) {
      return this;
   }

   public Holder getNoiseBiome(final int quartX, final int quartY, final int quartZ) {
      return this.biome;
   }

   public @Nullable Pair findBiomeHorizontal(final int originX, final int originY, final int originZ, final int r, final int skipStep, final Predicate allowed, final RandomSource random, final boolean findClosest, final Climate.Sampler sampler) {
      if (allowed.test(this.biome)) {
         return findClosest ? Pair.of(new BlockPos(originX, originY, originZ), this.biome) : Pair.of(new BlockPos(originX - r + random.nextInt(r * 2 + 1), originY, originZ - r + random.nextInt(r * 2 + 1)), this.biome);
      } else {
         return null;
      }
   }

   public @Nullable Pair findClosestBiome3d(final BlockPos origin, final int searchRadius, final int sampleResolutionHorizontal, final int sampleResolutionVertical, final Predicate allowed, final Climate.Sampler sampler, final LevelReader level) {
      return allowed.test(this.biome) ? Pair.of(origin.atY(Mth.clamp(origin.getY(), level.getMinY() + 1, level.getMaxY() + 1)), this.biome) : null;
   }

   public Set getBiomesWithin(final int x, final int y, final int z, final int radius) {
      return Sets.newHashSet(Set.of(this.biome));
   }
}
