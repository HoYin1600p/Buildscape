package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public record InclusiveRange(Comparable minInclusive, Comparable maxInclusive) {
   public static final Codec INT = codec(Codec.INT);

   public InclusiveRange {
      if (minInclusive.compareTo(maxInclusive) > 0) {
         throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
      }
   }

   public InclusiveRange(final Comparable value) {
      this(value, value);
   }

   public static Codec codec(final Codec elementCodec) {
      return ExtraCodecs.intervalCodec(elementCodec, "min_inclusive", "max_inclusive", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive);
   }

   public static Codec codec(final Codec elementCodec, final Comparable minAllowedInclusive, final Comparable maxAllowedInclusive) {
      return codec(elementCodec).validate((value) -> {
         if (value.minInclusive().compareTo(minAllowedInclusive) < 0) {
            return DataResult.error(() -> "Range limit too low, expected at least " + String.valueOf(minAllowedInclusive) + " [" + String.valueOf(value.minInclusive()) + "-" + String.valueOf(value.maxInclusive()) + "]");
         } else {
            return value.maxInclusive().compareTo(maxAllowedInclusive) > 0 ? DataResult.error(() -> "Range limit too high, expected at most " + String.valueOf(maxAllowedInclusive) + " [" + String.valueOf(value.minInclusive()) + "-" + String.valueOf(value.maxInclusive()) + "]") : DataResult.success(value);
         }
      });
   }

   public static DataResult create(final Comparable minInclusive, final Comparable maxInclusive) {
      return minInclusive.compareTo(maxInclusive) <= 0 ? DataResult.success(new InclusiveRange(minInclusive, maxInclusive)) : DataResult.error(() -> "min_inclusive must be less than or equal to max_inclusive");
   }

   public InclusiveRange map(final Function mapper) {
      return new InclusiveRange((Comparable)mapper.apply(this.minInclusive), (Comparable)mapper.apply(this.maxInclusive));
   }

   public boolean isValueInRange(final Comparable value) {
      return value.compareTo(this.minInclusive) >= 0 && value.compareTo(this.maxInclusive) <= 0;
   }

   public boolean contains(final InclusiveRange subRange) {
      return subRange.minInclusive().compareTo(this.minInclusive) >= 0 && subRange.maxInclusive.compareTo(this.maxInclusive) <= 0;
   }

   public String toString() {
      return "[" + String.valueOf(this.minInclusive) + ", " + String.valueOf(this.maxInclusive) + "]";
   }
}
