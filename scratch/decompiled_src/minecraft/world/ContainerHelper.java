package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ContainerHelper {
   public static final String TAG_ITEMS = "Items";

   public static ItemStack removeItem(final List itemStacks, final int slot, final int count) {
      return slot >= 0 && slot < itemStacks.size() && !((ItemStack)itemStacks.get(slot)).isEmpty() && count > 0 ? ((ItemStack)itemStacks.get(slot)).split(count) : ItemStack.EMPTY;
   }

   public static ItemStack takeItem(final List itemStacks, final int slot) {
      return slot >= 0 && slot < itemStacks.size() ? (ItemStack)itemStacks.set(slot, ItemStack.EMPTY) : ItemStack.EMPTY;
   }

   public static void saveAllItems(final ValueOutput output, final NonNullList itemStacks) {
      saveAllItems(output, itemStacks, true);
   }

   public static void saveAllItems(final ValueOutput output, final NonNullList itemStacks, final boolean alsoWhenEmpty) {
      ValueOutput.TypedOutputList itemsOutput = output.list("Items", ItemStackWithSlot.CODEC);

      for(int i = 0; i < itemStacks.size(); ++i) {
         ItemStack itemStack = (ItemStack)itemStacks.get(i);
         if (!itemStack.isEmpty()) {
            itemsOutput.add(new ItemStackWithSlot(i, itemStack));
         }
      }

      if (itemsOutput.isEmpty() && !alsoWhenEmpty) {
         output.discard("Items");
      }

   }

   public static void loadAllItems(final ValueInput input, final NonNullList itemStacks) {
      for(ItemStackWithSlot item : input.listOrEmpty("Items", ItemStackWithSlot.CODEC)) {
         if (item.isValidInContainer(itemStacks.size())) {
            itemStacks.set(item.slot(), item.stack());
         }
      }

   }

   public static int clearOrCountMatchingItems(final Container container, final Predicate predicate, final int amountToRemove, final boolean countingOnly) {
      int count = 0;

      for(int i = 0; i < container.getContainerSize(); ++i) {
         ItemStack itemStack = container.getItem(i);
         int amountRemoved = clearOrCountMatchingItems(itemStack, predicate, amountToRemove - count, countingOnly);
         if (amountRemoved > 0 && !countingOnly && itemStack.isEmpty()) {
            container.setItem(i, ItemStack.EMPTY);
         }

         count += amountRemoved;
      }

      return count;
   }

   public static int clearOrCountMatchingItems(final ItemStack itemStack, final Predicate predicate, final int amountToRemove, final boolean countingOnly) {
      if (!itemStack.isEmpty() && predicate.test(itemStack)) {
         if (countingOnly) {
            return itemStack.getCount();
         } else {
            int amountRemoved = amountToRemove < 0 ? itemStack.getCount() : Math.min(amountToRemove, itemStack.getCount());
            itemStack.shrink(amountRemoved);
            return amountRemoved;
         }
      } else {
         return 0;
      }
   }
}
