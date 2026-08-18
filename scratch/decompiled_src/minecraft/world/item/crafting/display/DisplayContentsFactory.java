package net.minecraft.world.item.crafting.display;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface DisplayContentsFactory {
   public interface ForRemainders extends DisplayContentsFactory {
      Object addRemainder(Object entry, List remainders);
   }

   public interface ForStacks extends DisplayContentsFactory {
      default Object forStack(final Holder item) {
         return this.forStack(new ItemStack(item));
      }

      default Object forStack(final Item item) {
         return this.forStack(new ItemStack(item));
      }

      Object forStack(ItemStack stack);
   }
}
