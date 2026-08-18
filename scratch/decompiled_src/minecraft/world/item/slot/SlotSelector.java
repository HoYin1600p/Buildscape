package net.minecraft.world.item.slot;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

public sealed interface SlotSelector {
   SlotSelector ANY_SLOT = new SlotSelector.FilteringSelector((Predicate)null);
   SlotSelector EMPTY_SLOTS = selecting(ItemStack::isEmpty);
   SlotSelector NON_EMPTY_SLOTS = selecting((s) -> !s.isEmpty());

   boolean trySelectSlot(ItemStack itemInSlot);

   SlotSelector filter(Predicate predicate);

   SlotSelector limit(int limit);

   static SlotSelector selecting(final Predicate predicate) {
      return new SlotSelector.FilteringSelector(predicate);
   }

   static SlotSelector tracking(final SlotSelector slotSelector, final MutableInt selectedCount) {
      return new SlotSelector.TrackingSelector(slotSelector, Integer.MAX_VALUE, selectedCount);
   }

   static SlotSelector tracking(final MutableInt selectedCount) {
      return tracking(ANY_SLOT, selectedCount);
   }

   public static record FilteringSelector(@Nullable Predicate filter) implements SlotSelector {
      public boolean trySelectSlot(final ItemStack itemInSlot) {
         return this.filter == null || this.filter.test(itemInSlot);
      }

      public SlotSelector filter(final Predicate predicate) {
         return this.filter == null ? new SlotSelector.FilteringSelector(predicate) : new SlotSelector.FilteringSelector((t) -> this.filter.test(t) && predicate.test(t));
      }

      public SlotSelector limit(final int limit) {
         return new SlotSelector.TrackingSelector(this, limit, new MutableInt());
      }
   }

   public static record TrackingSelector(SlotSelector slotSelector, int limit, MutableInt selectedCount) implements SlotSelector {
      public boolean trySelectSlot(final ItemStack itemInSlot) {
         if (this.selectedCount.intValue() < this.limit && this.slotSelector.trySelectSlot(itemInSlot)) {
            this.selectedCount.increment();
            return true;
         } else {
            return false;
         }
      }

      public SlotSelector filter(final Predicate predicate) {
         return new SlotSelector.TrackingSelector(this.slotSelector.filter(predicate), this.limit, this.selectedCount);
      }

      public SlotSelector limit(final int limit) {
         return limit < this.limit ? new SlotSelector.TrackingSelector(this.slotSelector, limit, this.selectedCount) : this;
      }
   }
}
