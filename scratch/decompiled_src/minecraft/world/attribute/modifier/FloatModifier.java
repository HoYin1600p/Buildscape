package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface FloatModifier extends AttributeModifier {
   FloatModifier ALPHA_BLEND = new FloatModifier() {
      public Float apply(final Float subject, final FloatWithAlpha argument) {
         return Mth.lerp(argument.alpha(), subject, argument.value());
      }

      public Codec argumentCodec(final EnvironmentAttribute type) {
         return FloatWithAlpha.CODEC;
      }

      public LerpFunction argumentKeyframeLerp(final EnvironmentAttribute type) {
         return (alpha, from, to) -> new FloatWithAlpha(Mth.lerp(alpha, from.value(), to.value()), Mth.lerp(alpha, from.alpha(), to.alpha()));
      }
   };
   FloatModifier ADD = Float::sum;
   FloatModifier SUBTRACT = (FloatModifier.Simple)(a, b) -> a - b;
   FloatModifier MULTIPLY = (FloatModifier.Simple)(a, b) -> a * b;
   FloatModifier MINIMUM = Math::min;
   FloatModifier MAXIMUM = Math::max;

   @FunctionalInterface
   public interface Simple extends FloatModifier {
      default Codec argumentCodec(final EnvironmentAttribute type) {
         return Codec.FLOAT;
      }

      default LerpFunction argumentKeyframeLerp(final EnvironmentAttribute type) {
         return LerpFunction.ofFloat();
      }
   }
}
