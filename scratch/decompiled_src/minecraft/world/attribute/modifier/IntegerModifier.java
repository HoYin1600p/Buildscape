package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface IntegerModifier extends AttributeModifier {
   IntegerModifier ADD = Integer::sum;
   IntegerModifier SUBTRACT = (a, b) -> a - b;
   IntegerModifier MULTIPLY = (a, b) -> a * b;
   IntegerModifier MINIMUM = Math::min;
   IntegerModifier MAXIMUM = Math::max;

   Integer apply(Integer integer, Object argument);

   @FunctionalInterface
   public interface Simple extends IntegerModifier {
      default Codec argumentCodec(final EnvironmentAttribute type) {
         return Codec.INT;
      }

      default LerpFunction argumentKeyframeLerp(final EnvironmentAttribute type) {
         return LerpFunction.ofInteger();
      }
   }
}
