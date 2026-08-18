package net.minecraft.world.level.storage.loot.predicates;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public interface ConditionUserBuilder {
   default ConditionUserBuilder when(final LootItemCondition.Builder builder) {
      return this.when(Holder.direct(builder.build()));
   }

   ConditionUserBuilder when(Holder condition);

   default ConditionUserBuilder when(final Iterable collection, final Function conditionProvider) {
      ConditionUserBuilder result = (T)this.unwrap();

      for(Object value : collection) {
         result = (T)result.when((LootItemCondition.Builder)conditionProvider.apply(value));
      }

      return result;
   }

   ConditionUserBuilder unwrap();

   static Optional buildCondition(final List conditions) {
      if (conditions.isEmpty()) {
         return Optional.empty();
      } else {
         return conditions.size() == 1 ? Optional.of((Holder)conditions.getFirst()) : Optional.of(Holder.direct(AllOfCondition.allOf(HolderSet.direct(conditions))));
      }
   }
}
