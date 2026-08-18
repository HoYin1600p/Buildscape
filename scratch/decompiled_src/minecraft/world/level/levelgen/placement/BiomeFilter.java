package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public class BiomeFilter implements PlacementFilter {
   private static final BiomeFilter INSTANCE = new BiomeFilter();
   public static final MapCodec CODEC = MapCodec.unit(() -> INSTANCE);

   private BiomeFilter() {
   }

   public static BiomeFilter biome() {
      return INSTANCE;
   }

   public boolean shouldPlace(final PlacementContext context, final RandomSource random, final BlockPos origin) {
      PlacedFeature feature = (PlacedFeature)context.topFeature().orElseThrow(() -> new IllegalStateException("Tried to biome check an unregistered feature, or a feature that should not restrict the biome"));
      Holder biome = context.getLevel().getBiome(origin);
      return context.generator().getBiomeGenerationSettings(biome).hasFeature(feature);
   }

   public MapCodec codec() {
      return CODEC;
   }
}
