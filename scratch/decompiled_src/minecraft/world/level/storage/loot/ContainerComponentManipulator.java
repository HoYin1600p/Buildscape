package net.minecraft.world.level.storage.loot;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ContainerComponent;
import net.minecraft.world.item.slot.SlotCollection;
import net.minecraft.world.item.slot.SlotSelector;

public record ContainerComponentManipulator(DataComponentType type, ContainerComponent empty) {
   public void setContents(final ItemStack itemStack, final ContainerComponent defaultValue, final Stream newContents) {
      ContainerComponent currentValue = (T)((ContainerComponent)itemStack.getOrDefault(this.type(), defaultValue));
      ContainerComponent newValue = (T)currentValue.copyWithContents(newContents);
      itemStack.set(this.type(), newValue);
   }

   public void setContents(final ItemStack itemStack, final Stream newContents) {
      this.setContents(itemStack, this.empty(), newContents);
   }

   public void modifyItems(final ItemStack itemStack, final UnaryOperator modifier) {
      ContainerComponent contents = (T)((ContainerComponent)itemStack.get(this.type()));
      if (contents != null) {
         UnaryOperator nonEmptyModifier = (currentItemStack) -> {
            if (currentItemStack.isEmpty()) {
               return currentItemStack;
            } else {
               ItemStack newItemStack = (ItemStack)modifier.apply(currentItemStack);
               newItemStack.limitSize(newItemStack.getMaxStackSize());
               return newItemStack;
            }
         };
         this.setContents(itemStack, contents.itemCopies().map(nonEmptyModifier));
      }

   }

   public SlotCollection getSlots(final ItemStack itemStack) {
      return new ContainerComponentManipulator.ComponentSlotCollection(itemStack, this);
   }

   private static record ComponentSlotCollection(ItemStack itemStack, ContainerComponentManipulator component) implements SlotCollection {
      public Stream itemCopies() {
         ContainerComponent contents = (T)((ContainerComponent)this.itemStack.get(this.component.type()));
         return contents != null ? contents.itemCopies() : Stream.empty();
      }

      public int size() {
         ContainerComponent contents = (T)((ContainerComponent)this.itemStack.get(this.component.type()));
         return contents != null ? contents.size() : 0;
      }

      public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
         ContainerComponent currentValue = (T)((ContainerComponent)this.itemStack.getOrDefault(this.component.type(), this.component.empty()));
         ContainerComponent.Mutable mutable = currentValue.asMutable();
         int slotsReplaced = mutable.replaceSlotItems(items, slotSelector);
         this.itemStack.set(this.component.type(), (ContainerComponent)mutable.toImmutable());
         return slotsReplaced;
      }

      public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
         ContainerComponent currentValue = (T)((ContainerComponent)this.itemStack.getOrDefault(this.component.type(), this.component.empty()));
         ContainerComponent.Mutable mutable = currentValue.asMutable();
         mutable.modifySlots(consumer, slotSelector);
         this.itemStack.set(this.component.type(), (ContainerComponent)mutable.toImmutable());
      }
   }
}
