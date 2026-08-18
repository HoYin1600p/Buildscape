package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record NoOpFeature() implements Feature {
   public static final MapCodec CODEC = MapCodec.unit(NoOpFeature::new);

   public MapCodec codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      return true;
   }
}
