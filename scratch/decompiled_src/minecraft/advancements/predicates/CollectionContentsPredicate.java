package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionContentsPredicate extends Predicate {
   List unpack();

   static Codec codec(final Codec elementCodec) {
      return elementCodec.listOf().xmap(CollectionContentsPredicate::of, CollectionContentsPredicate::unpack);
   }

   @SafeVarargs
   static CollectionContentsPredicate of(final Predicate... predicates) {
      return of(List.of(predicates));
   }

   static CollectionContentsPredicate of(final List predicates) {
      Object var10000;
      switch (predicates.size()) {
         case 0:
            var10000 = new CollectionContentsPredicate.Zero();
            break;
         case 1:
            var10000 = new CollectionContentsPredicate.Single((Predicate)predicates.getFirst());
            break;
         default:
            var10000 = new CollectionContentsPredicate.Multiple(predicates);
      }

      return (CollectionContentsPredicate)var10000;
   }

   public static record Multiple(List tests) implements CollectionContentsPredicate {
      public boolean test(final Iterable values) {
         List testsToMatch = new ArrayList(this.tests);

         for(Object value : values) {
            testsToMatch.removeIf((p) -> p.test(value));
            if (testsToMatch.isEmpty()) {
               return true;
            }
         }

         return false;
      }

      public List unpack() {
         return this.tests;
      }
   }

   public static record Single(Predicate test) implements CollectionContentsPredicate {
      public boolean test(final Iterable values) {
         for(Object value : values) {
            if (this.test.test(value)) {
               return true;
            }
         }

         return false;
      }

      public List unpack() {
         return List.of(this.test);
      }
   }

   public static class Zero implements CollectionContentsPredicate {
      public boolean test(final Iterable values) {
         return true;
      }

      public List unpack() {
         return List.of();
      }
   }
}
