package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class IntProviders {
   private static final Codec CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, BuiltInRegistries.INT_PROVIDER_TYPE.byNameCodec().dispatch(IntProvider::codec, (t) -> t));
   public static final Codec CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> (IntProvider)either.map(ConstantInt::of, (f) -> f), (f) -> {
      Either var10000;
      if (f instanceof ConstantInt constantInt) {
         var10000 = Either.left(constantInt.value());
      } else {
         var10000 = Either.right(f);
      }

      return var10000;
   });
   public static final Codec NON_NEGATIVE_CODEC = codec(0, Integer.MAX_VALUE);
   public static final Codec POSITIVE_CODEC = codec(1, Integer.MAX_VALUE);

   public static Codec codec(final int minValue, final int maxValue) {
      return validateCodec(minValue, maxValue, CODEC);
   }

   public static Codec validateCodec(final int minValue, final int maxValue, final Codec codec) {
      return codec.validate((value) -> validate(minValue, maxValue, value));
   }

   private static DataResult validate(final int minValue, final int maxValue, final IntProvider value) {
      if (value.minInclusive() < minValue) {
         return DataResult.error(() -> "Value provider too low: " + minValue + " [" + value.minInclusive() + "-" + value.maxInclusive() + "]");
      } else {
         return value.maxInclusive() > maxValue ? DataResult.error(() -> "Value provider too high: " + maxValue + " [" + value.minInclusive() + "-" + value.maxInclusive() + "]") : DataResult.success(value);
      }
   }

   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "constant", ConstantInt.MAP_CODEC);
      Registry.register(registry, "uniform", UniformInt.MAP_CODEC);
      Registry.register(registry, "biased_to_bottom", BiasedToBottomInt.MAP_CODEC);
      Registry.register(registry, "very_biased_to_bottom", VeryBiasedToBottomInt.MAP_CODEC);
      Registry.register(registry, "clamped", ClampedInt.MAP_CODEC);
      Registry.register(registry, "weighted_list", WeightedListInt.MAP_CODEC);
      Registry.register(registry, "clamped_normal", ClampedNormalInt.MAP_CODEC);
      return (MapCodec)Registry.register(registry, "trapezoid", TrapezoidInt.MAP_CODEC);
   }
}
