package net.minecraft.world.level.biome;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class BiomeSources {
   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "fixed", FixedBiomeSource.CODEC);
      Registry.register(registry, "multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(registry, "checkerboard", CheckerboardColumnBiomeSource.CODEC);
      return (MapCodec)Registry.register(registry, "the_end", TheEndBiomeSource.CODEC);
   }
}
