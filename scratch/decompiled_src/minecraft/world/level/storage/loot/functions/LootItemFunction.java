package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemFunction extends LootContextUser, BiFunction {
   MapCodec codec();

   static Consumer decorate(final Optional maybeFunction, final Consumer output, final LootContext context) {
      if (maybeFunction.isPresent()) {
         Holder function = (Holder)maybeFunction.get();
         return (drop) -> output.accept((ItemStack)((LootItemFunction)function.value()).apply(drop, context));
      } else {
         return output;
      }
   }

   public interface Builder {
      LootItemFunction build();
   }
}
