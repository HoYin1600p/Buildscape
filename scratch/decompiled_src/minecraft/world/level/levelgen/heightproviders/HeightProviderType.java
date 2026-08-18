package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public interface HeightProviderType {
   HeightProviderType CONSTANT = register("constant", ConstantHeight.CODEC);
   HeightProviderType UNIFORM = register("uniform", UniformHeight.CODEC);
   HeightProviderType BIASED_TO_BOTTOM = register("biased_to_bottom", BiasedToBottomHeight.CODEC);
   HeightProviderType VERY_BIASED_TO_BOTTOM = register("very_biased_to_bottom", VeryBiasedToBottomHeight.CODEC);
   HeightProviderType TRAPEZOID = register("trapezoid", TrapezoidHeight.CODEC);
   HeightProviderType WEIGHTED_LIST = register("weighted_list", WeightedListHeight.CODEC);

   MapCodec codec();

   private static HeightProviderType register(final String id, final MapCodec codec) {
      return (HeightProviderType)Registry.register(BuiltInRegistries.HEIGHT_PROVIDER_TYPE, id, (HeightProviderType)() -> codec);
   }
}
