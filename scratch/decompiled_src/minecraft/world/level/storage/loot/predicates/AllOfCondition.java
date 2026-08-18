package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Util;

public class AllOfCondition extends CompositeLootItemCondition {
   public static final MapCodec MAP_CODEC = createCodec(AllOfCondition::new);

   private AllOfCondition(final HolderSet terms) {
      super(terms, combine(terms));
   }

   private static Predicate combine(final HolderSet terms) {
      return !terms.isBound() ? (context) -> {
         for(Holder entry : terms) {
            if (!((LootItemCondition)entry.value()).test(context)) {
               return false;
            }
         }

         return true;
      } : Util.allOf(holdersToLazyPredicates(terms));
   }

   public static AllOfCondition allOf(final HolderSet terms) {
      return new AllOfCondition(terms);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public static AllOfCondition.Builder allOf(final LootItemCondition.Builder... terms) {
      return new AllOfCondition.Builder(terms);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(final LootItemCondition.Builder... terms) {
         super(terms);
      }

      public AllOfCondition.Builder and(final LootItemCondition.Builder term) {
         this.addTerm(term);
         return this;
      }

      protected LootItemCondition create(final HolderSet terms) {
         return new AllOfCondition(terms);
      }
   }
}
