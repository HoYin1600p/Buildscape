package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;

public final class ItemContainerContents implements ContainerComponent, TooltipProvider {
   private static final int NO_SLOT = -1;
   public static final int MAX_SIZE = 256;
   public static final ItemContainerContents EMPTY = new ItemContainerContents(List.of());
   public static final Codec CODEC = ItemContainerContents.Slot.CODEC.sizeLimitedListOf(256).xmap(ItemContainerContents::fromSlots, ItemContainerContents::asSlots);
   public static final StreamCodec STREAM_CODEC = ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs::optional).apply(ByteBufCodecs.list(256)).map(ItemContainerContents::new, (c) -> c.items);
   private final List items;
   private final int hashCode;

   private ItemContainerContents(final List items) {
      if (items.size() > 256) {
         throw new IllegalArgumentException("Got " + items.size() + " items, but maximum is 256");
      } else {
         this.items = items;
         this.hashCode = items.hashCode();
      }
   }

   private static List emptyContents(final int size) {
      return new ArrayList(Collections.nCopies(size, Optional.empty()));
   }

   private static ItemContainerContents fromSlots(final List slots) {
      OptionalInt maxSlotIndex = slots.stream().mapToInt(ItemContainerContents.Slot::index).max();
      if (maxSlotIndex.isEmpty()) {
         return EMPTY;
      } else {
         List items = emptyContents(maxSlotIndex.getAsInt() + 1);

         for(ItemContainerContents.Slot slot : slots) {
            items.set(slot.index(), Optional.of(slot.item()));
         }

         return new ItemContainerContents(items);
      }
   }

   public static ItemContainerContents fromItems(final List itemStacks) {
      int lastNonEmptySlot = findLastNonEmptySlot(itemStacks);
      if (lastNonEmptySlot == -1) {
         return EMPTY;
      } else {
         List items = emptyContents(lastNonEmptySlot + 1);

         for(int i = 0; i <= lastNonEmptySlot; ++i) {
            ItemStack sourceStack = (ItemStack)itemStacks.get(i);
            if (!sourceStack.isEmpty()) {
               items.set(i, Optional.of(ItemStackTemplate.fromNonEmptyStack(sourceStack)));
            }
         }

         return new ItemContainerContents(items);
      }
   }

   private static int findLastNonEmptySlot(final List itemStacks) {
      for(int i = itemStacks.size() - 1; i >= 0; --i) {
         if (!((ItemStack)itemStacks.get(i)).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   private List asSlots() {
      List slots = new ArrayList();

      for(int i = 0; i < this.items.size(); ++i) {
         Optional item = (Optional)this.items.get(i);
         if (item.isPresent()) {
            slots.add(new ItemContainerContents.Slot(i, (ItemStackTemplate)item.get()));
         }
      }

      return slots;
   }

   private ItemStack createStackFromSlot(final int slot) {
      if (slot < this.items.size()) {
         Optional slotContents = (Optional)this.items.get(slot);
         if (slotContents.isPresent()) {
            return ((ItemStackTemplate)slotContents.get()).create();
         }
      }

      return ItemStack.EMPTY;
   }

   public void copyInto(final NonNullList destination) {
      for(int i = 0; i < destination.size(); ++i) {
         destination.set(i, this.createStackFromSlot(i));
      }

   }

   public ItemStack copyOne() {
      return this.createStackFromSlot(0);
   }

   public Stream itemCopies() {
      return this.items.stream().map((i) -> (ItemStack)i.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY));
   }

   private Stream nonEmptyItemsStream() {
      return this.items.stream().flatMap(Optional::stream);
   }

   public Stream nonEmptyItemCopyStream() {
      return this.nonEmptyItemsStream().map(ItemStackTemplate::create);
   }

   public Iterable nonEmptyItems() {
      return () -> this.nonEmptyItemsStream().iterator();
   }

   public int size() {
      return this.items.size();
   }

   public ItemContainerContents copyWithContents(final Stream newContents) {
      return fromItems(newContents.toList());
   }

   public ItemContainerContents.Mutable asMutable() {
      List itemsList = new ArrayList(this.items.size());

      for(Optional item : this.items) {
         itemsList.add((ItemStack)item.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY));
      }

      return new ItemContainerContents.Mutable(itemsList);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj instanceof ItemContainerContents) {
            ItemContainerContents contents = (ItemContainerContents)obj;
            if (this.items.equals(contents.items)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer consumer, final TooltipFlag flag, final DataComponentGetter components) {
      int lineCount = 0;
      int itemCount = 0;

      for(Optional item : this.items) {
         if (!item.isEmpty()) {
            ++itemCount;
            if (lineCount <= 4) {
               ++lineCount;
               ItemStack itemStack = ((ItemStackTemplate)item.get()).create();
               consumer.accept(Component.translatable("item.container.item_count", itemStack.getCount(), itemStack.getHoverName()));
            }
         }
      }

      if (itemCount - lineCount > 0) {
         consumer.accept(Component.translatable("item.container.more_items", itemCount - lineCount).withStyle(ChatFormatting.ITALIC));
      }

   }

   public static class Mutable extends SimpleMutableContainer {
      private Mutable(final List items) {
         super(items);
      }

      public boolean canInsertNewSlots() {
         return this.items.size() < 256;
      }

      public ItemContainerContents toImmutable() {
         return ItemContainerContents.fromItems(this.items);
      }
   }

   private static record Slot(int index, ItemStackTemplate item) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.intRange(0, 255).fieldOf("slot").forGetter(ItemContainerContents.Slot::index), ItemStackTemplate.CODEC.fieldOf("item").forGetter(ItemContainerContents.Slot::item)).apply(i, ItemContainerContents.Slot::new));
   }
}
