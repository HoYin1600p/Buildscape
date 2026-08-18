package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FeatureSizeType {
   public static final FeatureSizeType TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", TwoLayersFeatureSize.CODEC);
   public static final FeatureSizeType THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", ThreeLayersFeatureSize.CODEC);
   private final MapCodec codec;

   private static FeatureSizeType register(final String name, final MapCodec codec) {
      return (FeatureSizeType)Registry.register(BuiltInRegistries.FEATURE_SIZE_TYPE, name, new FeatureSizeType(codec));
   }

   private FeatureSizeType(final MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
