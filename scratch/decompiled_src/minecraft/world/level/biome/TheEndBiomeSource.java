package net.minecraft.world.level.biome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public class TheEndBiomeSource extends BiomeSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryOps.retrieveElement(Biomes.THE_END), RegistryOps.retrieveElement(Biomes.END_HIGHLANDS), RegistryOps.retrieveElement(Biomes.END_MIDLANDS), RegistryOps.retrieveElement(Biomes.SMALL_END_ISLANDS), RegistryOps.retrieveElement(Biomes.END_BARRENS)).apply(i, i.stable(TheEndBiomeSource::new)));
   private final Holder end;
   private final Holder highlands;
   private final Holder midlands;
   private final Holder islands;
   private final Holder barrens;

   public static TheEndBiomeSource create(final HolderGetter biomes) {
      return new TheEndBiomeSource(biomes.getOrThrow(Biomes.THE_END), biomes.getOrThrow(Biomes.END_HIGHLANDS), biomes.getOrThrow(Biomes.END_MIDLANDS), biomes.getOrThrow(Biomes.SMALL_END_ISLANDS), biomes.getOrThrow(Biomes.END_BARRENS));
   }

   private TheEndBiomeSource(final Holder end, final Holder highlands, final Holder midlands, final Holder islands, final Holder barrens) {
      this.end = end;
      this.highlands = highlands;
      this.midlands = midlands;
      this.islands = islands;
      this.barrens = barrens;
   }

   protected Stream collectPossibleBiomes() {
      return Stream.of(this.end, this.highlands, this.midlands, this.islands, this.barrens);
   }

   protected MapCodec codec() {
      return CODEC;
   }

   public BiomeResolver createResolver(final Climate.Sampler sampler) {
      return (quartX, quartY, quartZ) -> this.getNoiseBiome(quartX, quartY, quartZ, sampler);
   }

   private Holder getNoiseBiome(final int quartX, final int quartY, final int quartZ, final Climate.Sampler sampler) {
      int blockX = QuartPos.toBlock(quartX);
      int blockY = QuartPos.toBlock(quartY);
      int blockZ = QuartPos.toBlock(quartZ);
      int chunkX = SectionPos.blockToSectionCoord(blockX);
      int chunkZ = SectionPos.blockToSectionCoord(blockZ);
      if ((long)chunkX * (long)chunkX + (long)chunkZ * (long)chunkZ <= 4096L) {
         return this.end;
      } else {
         int weirdBlockX = (SectionPos.blockToSectionCoord(blockX) * 2 + 1) * 8;
         int weirdBlockZ = (SectionPos.blockToSectionCoord(blockZ) * 2 + 1) * 8;
         double heightValue = (double)sampler.erosion().compute(new DensityFunction.SinglePointContext(weirdBlockX, blockY, weirdBlockZ));
         if (heightValue > 0.25D) {
            return this.highlands;
         } else if (heightValue >= -0.0625D) {
            return this.midlands;
         } else {
            return heightValue < -0.21875D ? this.islands : this.barrens;
         }
      }
   }
}
