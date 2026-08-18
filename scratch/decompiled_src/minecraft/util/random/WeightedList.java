package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public final class WeightedList {
   private static final int FLAT_THRESHOLD = 64;
   private final int totalWeight;
   private final List items;
   private final WeightedList.@Nullable Selector selector;

   private WeightedList(final List items) {
      this.items = List.copyOf(items);
      this.totalWeight = WeightedRandom.getTotalWeight(items, Weighted::weight);
      if (this.totalWeight == 0) {
         this.selector = null;
      } else if (this.totalWeight < 64) {
         this.selector = new WeightedList.Flat(this.items, this.totalWeight);
      } else {
         this.selector = new WeightedList.Compact(this.items);
      }

   }

   public static WeightedList of() {
      return new WeightedList(List.of());
   }

   public static WeightedList of(final Object value) {
      return new WeightedList(List.of(new Weighted(value, 1)));
   }

   public static WeightedList of(final Object... items) {
      WeightedList.Builder builder = builder();

      for(Object item : items) {
         builder.add(item);
      }

      return builder.build();
   }

   @SafeVarargs
   public static WeightedList of(final Weighted... items) {
      return new WeightedList(List.of(items));
   }

   public static WeightedList of(final List items) {
      return new WeightedList(items);
   }

   public static WeightedList.Builder builder() {
      return new WeightedList.Builder();
   }

   public boolean isEmpty() {
      return this.selector == null;
   }

   public WeightedList map(final Function mapper) {
      return new WeightedList(Lists.transform(this.items, (e) -> e.map(mapper)));
   }

   public Optional getRandom(final RandomSource random) {
      if (this.selector == null) {
         return Optional.empty();
      } else {
         int selection = random.nextInt(this.totalWeight);
         return Optional.of(this.selector.get(selection));
      }
   }

   public Object getRandomOrThrow(final RandomSource random) {
      if (this.selector == null) {
         throw new IllegalStateException("Weighted list has no elements");
      } else {
         int selection = random.nextInt(this.totalWeight);
         return this.selector.get(selection);
      }
   }

   public List unwrap() {
      return this.items;
   }

   private static Codec entryToListCodec(final Codec weightedElementCodec) {
      return weightedElementCodec.listOf().xmap(WeightedList::of, WeightedList::unwrap);
   }

   public static Codec codec(final Codec elementCodec) {
      return entryToListCodec(Weighted.codec(elementCodec));
   }

   public static Codec codec(final MapCodec elementCodec) {
      return entryToListCodec(Weighted.codec(elementCodec));
   }

   private static Codec entryToNonEmptyListCodec(final Codec weightedElementCodec) {
      return entryToListCodec(weightedElementCodec).validate((list) -> list.isEmpty() ? DataResult.error(() -> "Weighted list must contain at least one entry with non-zero weight") : DataResult.success(list));
   }

   public static Codec nonEmptyCodec(final Codec elementCodec) {
      return entryToNonEmptyListCodec(Weighted.codec(elementCodec));
   }

   public static Codec nonEmptyCodec(final MapCodec elementCodec) {
      return entryToNonEmptyListCodec(Weighted.codec(elementCodec));
   }

   public static StreamCodec streamCodec(final StreamCodec elementCodec) {
      return Weighted.streamCodec(elementCodec).apply(ByteBufCodecs.list()).map(WeightedList::of, WeightedList::unwrap);
   }

   public boolean contains(final Object value) {
      for(Weighted item : this.items) {
         if (item.value().equals(value)) {
            return true;
         }
      }

      return false;
   }

   public boolean equals(final @Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof WeightedList)) {
         return false;
      } else {
         WeightedList list = (WeightedList)obj;
         return this.totalWeight == list.totalWeight && Objects.equals(this.items, list.items);
      }
   }

   public int hashCode() {
      int result = this.totalWeight;
      return 31 * result + this.items.hashCode();
   }

   public static class Builder {
      private final ImmutableList.Builder result = ImmutableList.builder();

      public WeightedList.Builder add(final Object item) {
         return this.add(item, 1);
      }

      public WeightedList.Builder add(final Object item, final int weight) {
         this.result.add(new Weighted(item, weight));
         return this;
      }

      public WeightedList.Builder addAll(final WeightedList other) {
         this.result.addAll(other.unwrap());
         return this;
      }

      public WeightedList build() {
         return new WeightedList(this.result.build());
      }
   }

   private static class Compact implements WeightedList.Selector {
      private final Weighted[] entries;

      private Compact(final List entries) {
         this.entries = (Weighted[])entries.toArray((x$0) -> new Weighted[x$0]);
      }

      public Object get(int selection) {
         for(Weighted entry : this.entries) {
            selection -= entry.weight();
            if (selection < 0) {
               return entry.value();
            }
         }

         throw new IllegalStateException(selection + " exceeded total weight");
      }
   }

   private static class Flat implements WeightedList.Selector {
      private final Object[] entries;

      private Flat(final List entries, final int totalWeight) {
         this.entries = new Object[totalWeight];
         int i = 0;

         for(Weighted entry : entries) {
            int weight = entry.weight();
            Arrays.fill(this.entries, i, i + weight, entry.value());
            i += weight;
         }

      }

      public Object get(final int selection) {
         return this.entries[selection];
      }
   }

   private interface Selector {
      Object get(int selection);
   }
}
