package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;

public interface FunctionUserBuilder {
   default FunctionUserBuilder apply(final LootItemFunction.Builder builder) {
      return this.apply(Holder.direct(builder.build()));
   }

   FunctionUserBuilder apply(Holder function);

   default FunctionUserBuilder apply(final Iterable collection, final Function functionProvider) {
      FunctionUserBuilder result = (T)this.unwrap();

      for(Object value : collection) {
         result = (T)result.apply((LootItemFunction.Builder)functionProvider.apply(value));
      }

      return result;
   }

   default FunctionUserBuilder apply(final Object[] collection, final Function functionProvider) {
      return this.apply(Arrays.asList(collection), functionProvider);
   }

   FunctionUserBuilder unwrap();

   static Optional buildFunction(final List conditions) {
      if (conditions.isEmpty()) {
         return Optional.empty();
      } else {
         return conditions.size() == 1 ? Optional.of((Holder)conditions.getFirst()) : Optional.of(Holder.direct(SequenceFunction.of(conditions)));
      }
   }
}
