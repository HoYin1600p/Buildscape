package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public class ByIdMap {
   private static IntFunction createMap(final ToIntFunction idGetter, final Object[] values) {
      if (values.length == 0) {
         throw new IllegalArgumentException("Empty value list");
      } else {
         Int2ObjectMap result = new Int2ObjectOpenHashMap();

         for(Object value : values) {
            int id = idGetter.applyAsInt(value);
            Object previous = (T)result.put(id, value);
            if (previous != null) {
               throw new IllegalArgumentException("Duplicate entry on id " + id + ": current=" + String.valueOf(value) + ", previous=" + String.valueOf(previous));
            }
         }

         return result;
      }
   }

   public static IntFunction sparse(final ToIntFunction idGetter, final Object[] values, final Object _default) {
      IntFunction idToObject = createMap(idGetter, values);
      return (id) -> Objects.requireNonNullElse(idToObject.apply(id), _default);
   }

   private static Object[] createSortedArray(final ToIntFunction idGetter, final Object[] values) {
      int length = values.length;
      if (length == 0) {
         throw new IllegalArgumentException("Empty value list");
      } else {
         Object[] result = (T[])((Object[])values.clone());
         Arrays.fill(result, (Object)null);

         for(Object value : values) {
            int id = idGetter.applyAsInt(value);
            if (id < 0 || id >= length) {
               throw new IllegalArgumentException("Values are not continous, found index " + id + " for value " + String.valueOf(value));
            }

            Object previous = result[id];
            if (previous != null) {
               throw new IllegalArgumentException("Duplicate entry on id " + id + ": current=" + String.valueOf(value) + ", previous=" + String.valueOf(previous));
            }

            result[id] = value;
         }

         for(int i = 0; i < length; ++i) {
            if (result[i] == null) {
               throw new IllegalArgumentException("Missing value at index: " + i);
            }
         }

         return result;
      }
   }

   public static IntFunction continuous(final ToIntFunction idGetter, final Object[] values, final ByIdMap.OutOfBoundsStrategy strategy) {
      Object[] sortedValues = (T[])createSortedArray(idGetter, values);
      int length = sortedValues.length;
      IntFunction var10000;
      switch (strategy.ordinal()) {
         case 0:
            Object zeroValue = sortedValues[0];
            var10000 = (id) -> id >= 0 && id < length ? sortedValues[id] : zeroValue;
            break;
         case 1:
            var10000 = (id) -> sortedValues[Mth.positiveModulo(id, length)];
            break;
         case 2:
            var10000 = (id) -> sortedValues[Mth.clamp(id, 0, length - 1)];
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static enum OutOfBoundsStrategy {
      ZERO,
      WRAP,
      CLAMP;

      // $FF: synthetic method
      private static ByIdMap.OutOfBoundsStrategy[] $values() {
         return new ByIdMap.OutOfBoundsStrategy[]{ZERO, WRAP, CLAMP};
      }
   }
}
