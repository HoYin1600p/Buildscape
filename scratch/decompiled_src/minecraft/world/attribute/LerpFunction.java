package net.minecraft.world.attribute;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public interface LerpFunction {
   LerpFunction CONSTANT = ofStep(1.0F);

   static LerpFunction ofFloat() {
      return Mth::lerp;
   }

   static LerpFunction ofInteger() {
      return Mth::lerpInt;
   }

   static LerpFunction ofDegrees(final float maxDelta) {
      return (alpha, from, to) -> {
         float delta = Mth.wrapDegrees(to - from);
         return Math.abs(delta) >= maxDelta ? to : from + alpha * delta;
      };
   }

   static LerpFunction ofConstant() {
      return CONSTANT;
   }

   static LerpFunction ofStep(final float threshold) {
      return (alpha, from, to) -> alpha >= threshold ? to : from;
   }

   static LerpFunction ofColor() {
      return ARGB::srgbLerp;
   }

   static LerpFunction ofListCrossFade(final LerpFunction.AlphaScaler scaler) {
      return (alpha, from, to) -> {
         if (alpha == 0.0F) {
            return from;
         } else if (alpha == 1.0F) {
            return to;
         } else {
            ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(from.size() + to.size());

            for(Object element : from) {
               builder.add(scaler.apply(element, 1.0F - alpha));
            }

            for(Object element : to) {
               builder.add(scaler.apply(element, alpha));
            }

            return builder.build();
         }
      };
   }

   Object apply(float alpha, Object from, Object to);

   @FunctionalInterface
   public interface AlphaScaler {
      Object apply(Object item, float alpha);
   }
}
