package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;

public interface BoundedFloatFunction {
   BoundedFloatFunction IDENTITY = new BoundedFloatFunction() {
      public float apply(final Float value) {
         return value;
      }

      public Interval range() {
         return Interval.INFINITE;
      }
   };

   float apply(final Object c);

   Interval range();

   static BoundedFloatFunction constant(final float value) {
      final Interval range = Interval.ofExact(value);
      return new BoundedFloatFunction() {
         public float apply(final Object c) {
            return value;
         }

         public Interval range() {
            return range;
         }
      };
   }

   default BoundedFloatFunction comap(final Function function) {
      final BoundedFloatFunction outer = this;
      return new BoundedFloatFunction(this) {
         {
            Objects.requireNonNull(this$0);
         }

         public float apply(final Object c2) {
            return outer.apply(function.apply(c2));
         }

         public Interval range() {
            return outer.range();
         }
      };
   }
}
