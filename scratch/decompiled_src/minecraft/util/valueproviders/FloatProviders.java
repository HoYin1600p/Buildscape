package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FloatProviders {
   private static final Codec CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.FLOAT, BuiltInRegistries.FLOAT_PROVIDER_TYPE.byNameCodec().dispatch(FloatProvider::codec, (t) -> t));
   public static final Codec CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> (FloatProvider)either.map(ConstantFloat::of, (f) -> f), (f) -> {
      Either var10000;
      if (f instanceof ConstantFloat constantFloat) {
         var10000 = Either.left(constantFloat.value());
      } else {
         var10000 = Either.right(f);
      }

      return var10000;
   });

   public static Codec codec(final float minValue, final float maxValue) {
      return CODEC.validate((value) -> {
         if (value.min() < minValue) {
            return DataResult.error(() -> "Value provider too low: " + minValue + " [" + value.min() + "-" + value.max() + "]");
         } else {
            return value.max() > maxValue ? DataResult.error(() -> "Value provider too high: " + maxValue + " [" + value.min() + "-" + value.max() + "]") : DataResult.success(value);
         }
      });
   }

   public static Codec codec(final float minValue) {
      return CODEC.validate((value) -> value.min() < minValue ? DataResult.error(() -> "Value provider too low: " + minValue + " [" + value.min() + "-" + value.max() + "]") : DataResult.success(value));
   }

   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "constant", ConstantFloat.MAP_CODEC);
      Registry.register(registry, "uniform", UniformFloat.MAP_CODEC);
      Registry.register(registry, "clamped_normal", ClampedNormalFloat.MAP_CODEC);
      return (MapCodec)Registry.register(registry, "trapezoid", TrapezoidFloat.MAP_CODEC);
   }
}
