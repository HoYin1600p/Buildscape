package net.minecraft.util.random;

import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

public class WeightedRandom {
   private WeightedRandom() {
   }

   public static int getTotalWeight(final List items, final ToIntFunction weightGetter) {
      long totalWeight = 0L;

      for(Object item : items) {
         totalWeight += (long)weightGetter.applyAsInt(item);
      }

      if (totalWeight > 2147483647L) {
         throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
      } else {
         return (int)totalWeight;
      }
   }

   public static Optional getRandomItem(final RandomSource random, final List items, final int totalWeight, final ToIntFunction weightGetter) {
      if (totalWeight < 0) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Negative total weight in getRandomItem"));
      } else if (totalWeight == 0) {
         return Optional.empty();
      } else {
         int selection = random.nextInt(totalWeight);
         return getWeightedItem(items, selection, weightGetter);
      }
   }

   public static Optional getWeightedItem(final List items, int index, final ToIntFunction weightGetter) {
      for(Object item : items) {
         index -= weightGetter.applyAsInt(item);
         if (index < 0) {
            return Optional.of(item);
         }
      }

      return Optional.empty();
   }

   public static Optional getRandomItem(final RandomSource random, final List items, final ToIntFunction weightGetter) {
      return getRandomItem(random, items, getTotalWeight(items, weightGetter), weightGetter);
   }
}
