package net.minecraft.world.attribute;

import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

public interface AttributeRange {
   AttributeRange UNIT_FLOAT = ofFloat(0.0F, 1.0F);
   AttributeRange UNIT_FLOAT_EPSILON = ofFloat(0.0F, 0.9999999F);
   AttributeRange NON_NEGATIVE_FLOAT = ofFloat(0.0F, Float.POSITIVE_INFINITY);

   static AttributeRange any() {
      return new AttributeRange() {
         public DataResult validate(final Object value) {
            return DataResult.success(value);
         }

         public Object sanitize(final Object value) {
            return value;
         }
      };
   }

   static AttributeRange ofFloat(final float minValue, final float maxValue) {
      return new AttributeRange() {
         public DataResult validate(final Float value) {
            return value >= minValue && value <= maxValue ? DataResult.success(value) : DataResult.error(() -> value + " is not in range [" + minValue + "; " + maxValue + "]");
         }

         public Float sanitize(final Float value) {
            return value >= minValue && value <= maxValue ? value : Mth.clamp(value, minValue, maxValue);
         }
      };
   }

   DataResult validate(Object value);

   Object sanitize(Object value);
}
