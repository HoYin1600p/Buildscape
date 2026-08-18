package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;

public class ShufflingList implements Iterable {
   protected final List entries;
   private final RandomSource random = RandomSource.create();

   public ShufflingList() {
      this.entries = Lists.newArrayList();
   }

   private ShufflingList(final List entries) {
      this.entries = Lists.newArrayList(entries);
   }

   public static Codec codec(final Codec elementCodec) {
      return ShufflingList.WeightedEntry.codec(elementCodec).listOf().xmap(ShufflingList::new, (l) -> l.entries);
   }

   public ShufflingList add(final Object data, final int weight) {
      this.entries.add(new ShufflingList.WeightedEntry(data, weight));
      return this;
   }

   public ShufflingList shuffle() {
      this.entries.forEach((k) -> k.setRandom(this.random.nextFloat()));
      this.entries.sort(Comparator.comparingDouble(ShufflingList.WeightedEntry::getRandWeight));
      return this;
   }

   public Stream stream() {
      return this.entries.stream().map(ShufflingList.WeightedEntry::getData);
   }

   public Iterator iterator() {
      return Iterators.transform(this.entries.iterator(), ShufflingList.WeightedEntry::getData);
   }

   public String toString() {
      return "ShufflingList[" + String.valueOf(this.entries) + "]";
   }

   public static class WeightedEntry {
      private final Object data;
      private final int weight;
      private double randWeight;

      private WeightedEntry(final Object data, final int weight) {
         this.weight = weight;
         this.data = data;
      }

      private double getRandWeight() {
         return this.randWeight;
      }

      private void setRandom(final float random) {
         this.randWeight = -Math.pow((double)random, (double)(1.0F / (float)this.weight));
      }

      public Object getData() {
         return this.data;
      }

      public int getWeight() {
         return this.weight;
      }

      public String toString() {
         return this.weight + ":" + String.valueOf(this.data);
      }

      public static Codec codec(final Codec elementCodec) {
         return new Codec() {
            public DataResult decode(final DynamicOps ops, final Object input) {
               Dynamic map = new Dynamic(ops, input);
               return map.get("data").flatMap(elementCodec::parse).map((data) -> new ShufflingList.WeightedEntry(data, map.get("weight").asInt(1))).map((r) -> Pair.of(r, ops.empty()));
            }

            public DataResult encode(final ShufflingList.WeightedEntry input, final DynamicOps ops, final Object prefix) {
               return ops.mapBuilder().add("weight", ops.createInt(input.weight)).add("data", elementCodec.encodeStart(ops, input.data)).build(prefix);
            }
         };
      }
   }
}
