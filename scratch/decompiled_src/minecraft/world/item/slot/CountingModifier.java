package net.minecraft.world.item.slot;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public class CountingModifier implements Consumer {
   private final UnaryOperator modifier;
   private int updatedCount;

   public CountingModifier(final UnaryOperator modifier) {
      this.modifier = modifier;
   }

   public void accept(final SlotAccess slot) {
      boolean success = slot.set((ItemStack)this.modifier.apply(slot.get().copy()));
      if (success) {
         ++this.updatedCount;
      }

   }

   public int updatedCount() {
      return this.updatedCount;
   }
}
