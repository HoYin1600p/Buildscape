package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public class CheckerboardColumnBiomeSource extends BiomeSource implements BiomeResolver {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter((s) -> s.allowedBiomes), Codec.intRange(0, 62).optionalFieldOf("scale", 2).forGetter((s) -> s.size)).apply(i, CheckerboardColumnBiomeSource::new));
   private final HolderSet allowedBiomes;
   private final int bitShift;
   private final int size;

   public CheckerboardColumnBiomeSource(final HolderSet allowedBiomes, final int size) {
      this.allowedBiomes = allowedBiomes;
      this.bitShift = size + 2;
      this.size = size;
   }

   protected Stream collectPossibleBiomes() {
      return this.allowedBiomes.stream();
   }

   public BiomeResolver createResolver(final Climate.Sampler sampler) {
      return this;
   }

   protected MapCodec codec() {
      return CODEC;
   }

   public Holder getNoiseBiome(final int quartX, final int quartY, final int quartZ) {
      return this.allowedBiomes.get(Math.floorMod((quartX >> this.bitShift) + (quartZ >> this.bitShift), this.allowedBiomes.size()));
   }
}
