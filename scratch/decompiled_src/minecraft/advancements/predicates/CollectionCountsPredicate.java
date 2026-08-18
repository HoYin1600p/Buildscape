package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionCountsPredicate extends Predicate {
   List unpack();

   static Codec codec(final Codec elementCodec) {
      return CollectionCountsPredicate.Entry.codec(elementCodec).listOf().xmap(CollectionCountsPredicate::of, CollectionCountsPredicate::unpack);
   }

   @SafeVarargs
   static CollectionCountsPredicate of(final CollectionCountsPredicate.Entry... predicates) {
      return of(List.of(predicates));
   }

   static CollectionCountsPredicate of(final List predicates) {
      Object var10000;
      switch (predicates.size()) {
         case 0:
            var10000 = new CollectionCountsPredicate.Zero();
            break;
         case 1:
            var10000 = new CollectionCountsPredicate.Single((CollectionCountsPredicate.Entry)predicates.getFirst());
            break;
         default:
            var10000 = new CollectionCountsPredicate.Multiple(predicates);
      }

      return (CollectionCountsPredicate)var10000;
   }

   public static record Entry(Predicate test, MinMaxBounds.Ints count) {
      public static Codec codec(final Codec elementCodec) {
         return RecordCodecBuilder.create((i) -> i.group(elementCodec.fieldOf("test").forGetter(CollectionCountsPredicate.Entry::test), MinMaxBounds.Ints.CODEC.fieldOf("count").forGetter(CollectionCountsPredicate.Entry::count)).apply(i, CollectionCountsPredicate.Entry::new));
      }

      public boolean test(final Iterable values) {
         int count = 0;

         for(Object value : values) {
            if (this.test.test(value)) {
               ++count;
            }
         }

         return this.count.matches(count);
      }
   }

   public static record Multiple(List entries) implements CollectionCountsPredicate {
      public boolean test(final Iterable values) {
         for(CollectionCountsPredicate.Entry entry : this.entries) {
            if (!entry.test(values)) {
               return false;
            }
         }

         return true;
      }

      public List unpack() {
         return this.entries;
      }
   }

   public static record Single(CollectionCountsPredicate.Entry entry) implements CollectionCountsPredicate {
      public boolean test(final Iterable values) {
         return this.entry.test(values);
      }

      public List unpack() {
         return List.of(this.entry);
      }
   }

   public static class Zero implements CollectionCountsPredicate {
      public boolean test(final Iterable values) {
         return true;
      }

      public List unpack() {
         return List.of();
      }
   }
}
