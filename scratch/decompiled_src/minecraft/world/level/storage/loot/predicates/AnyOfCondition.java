package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Util;

public class AnyOfCondition extends CompositeLootItemCondition {
   public static final MapCodec MAP_CODEC = createCodec(AnyOfCondition::new);

   private AnyOfCondition(final HolderSet terms) {
      super(terms, combine(terms));
   }

   private static Predicate combine(final HolderSet terms) {
      return !terms.isBound() ? (context) -> {
         for(Holder entry : terms) {
            if (((LootItemCondition)entry.value()).test(context)) {
               return true;
            }
         }

         return false;
      } : Util.anyOf(holdersToLazyPredicates(terms));
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public static AnyOfCondition.Builder anyOf(final LootItemCondition.Builder... terms) {
      return new AnyOfCondition.Builder(terms);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(final LootItemCondition.Builder... terms) {
         super(terms);
      }

      public AnyOfCondition.Builder or(final LootItemCondition.Builder term) {
         this.addTerm(term);
         return this;
      }

      public AnyOfCondition.Builder or(final Holder term) {
         this.addTerm(term);
         return this;
      }

      protected LootItemCondition create(final HolderSet terms) {
         return new AnyOfCondition(terms);
      }
   }
}
