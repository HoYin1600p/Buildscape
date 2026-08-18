package net.minecraft.world.item.slot;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemProvider;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

public interface SlotCollection {
   SlotCollection EMPTY = new SlotCollection() {
      public Stream itemCopies() {
         return Stream.empty();
      }

      public int size() {
         return 0;
      }

      public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
         return 0;
      }

      public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
      }
   };

   Stream itemCopies();

   int size();

   int replaceSlotItems(ItemProvider items, SlotSelector slotSelector);

   default int replaceSlotItems(final ItemProvider items) {
      return this.replaceSlotItems(items, SlotSelector.ANY_SLOT);
   }

   void modifySlots(Consumer consumer, SlotSelector slotSelector);

   default void modifySlots(final Consumer consumer) {
      this.modifySlots(consumer, SlotSelector.ANY_SLOT);
   }

   default SlotCollection filter(final Predicate predicate) {
      return new SlotCollection.Filtered(this, predicate);
   }

   default SlotCollection flatMap(final Function mapper) {
      return new SlotCollection.FlatMapped(this, mapper);
   }

   default SlotCollection limit(final int limit) {
      return new SlotCollection.Limited(this, limit);
   }

   static SlotCollection of(final SlotAccess slotAccess) {
      return new SlotCollection() {
         public Stream itemCopies() {
            return Stream.of(slotAccess.get().copy());
         }

         public int size() {
            return 1;
         }

         public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
            if (items.hasNext() && slotSelector.trySelectSlot(slotAccess.get())) {
               return slotAccess.set(items.next()) ? 1 : 0;
            } else {
               return 0;
            }
         }

         public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
            if (slotSelector.trySelectSlot(slotAccess.get())) {
               consumer.accept(slotAccess);
            }

         }
      };
   }

   static SlotCollection of(final Collection slots) {
      SlotCollection var10000;
      switch (slots.size()) {
         case 0:
            var10000 = EMPTY;
            break;
         case 1:
            var10000 = of((SlotAccess)slots.iterator().next());
            break;
         default:
            var10000 = new SlotCollection() {
               public Stream itemCopies() {
                  return slots.stream().map(SlotAccess::get).map(ItemStack::copy);
               }

               public int size() {
                  return slots.size();
               }

               public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
                  Iterator iterator = slots.iterator();
                  int successCount = 0;

                  while(iterator.hasNext() && items.hasNext()) {
                     SlotAccess slot = (SlotAccess)iterator.next();
                     if (slotSelector.trySelectSlot(slot.get()) && slot.set(items.next())) {
                        ++successCount;
                     }
                  }

                  return successCount;
               }

               public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
                  for(SlotAccess slot : slots) {
                     if (slotSelector.trySelectSlot(slot.get())) {
                        consumer.accept(slot);
                     }
                  }

               }
            };
      }

      return var10000;
   }

   static SlotCollection concat(final SlotCollection first, final SlotCollection second) {
      return new SlotCollection() {
         public Stream itemCopies() {
            return Stream.concat(first.itemCopies(), second.itemCopies());
         }

         public int size() {
            return first.size() + second.size();
         }

         public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
            int firstCount = first.replaceSlotItems(items, slotSelector);
            int secondCount = second.replaceSlotItems(items, slotSelector);
            return firstCount + secondCount;
         }

         public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
            first.modifySlots(consumer, slotSelector);
            second.modifySlots(consumer, slotSelector);
         }
      };
   }

   static SlotCollection concat(final List terms) {
      SlotCollection var10000;
      switch (terms.size()) {
         case 0:
            var10000 = EMPTY;
            break;
         case 1:
            var10000 = (SlotCollection)terms.getFirst();
            break;
         case 2:
            var10000 = concat((SlotCollection)terms.get(0), (SlotCollection)terms.get(1));
            break;
         default:
            var10000 = new SlotCollection() {
               public Stream itemCopies() {
                  return terms.stream().flatMap(SlotCollection::itemCopies);
               }

               public int size() {
                  return terms.stream().mapToInt(SlotCollection::size).sum();
               }

               public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
                  int successCount = 0;

                  for(SlotCollection slots : terms) {
                     successCount += slots.replaceSlotItems(items, slotSelector);
                  }

                  return successCount;
               }

               public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
                  for(SlotCollection slots : terms) {
                     slots.modifySlots(consumer, slotSelector);
                  }

               }
            };
      }

      return var10000;
   }

   public static record Filtered(SlotCollection slots, Predicate filter) implements SlotCollection {
      public Stream itemCopies() {
         return this.slots.itemCopies().filter(this.filter);
      }

      public int size() {
         return (int)this.itemCopies().count();
      }

      public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
         return this.slots.replaceSlotItems(items, slotSelector.filter(this.filter));
      }

      public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
         this.slots.modifySlots(consumer, slotSelector.filter(this.filter));
      }

      public SlotCollection filter(final Predicate predicate) {
         Objects.requireNonNull(predicate);
         return new SlotCollection.Filtered(this.slots, (t) -> this.filter.test(t) && predicate.test(t));
      }
   }

   public static record FlatMapped(SlotCollection slots, Function mapper) implements SlotCollection {
      public Stream itemCopies() {
         return this.slots.itemCopies().map(this.mapper).flatMap(SlotCollection::itemCopies);
      }

      public int size() {
         return this.slots.itemCopies().map(this.mapper).mapToInt(SlotCollection::size).sum();
      }

      public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
         MutableInt successCount = new MutableInt();
         this.slots.modifySlots((slot) -> {
            SlotCollection mapped = (SlotCollection)this.mapper.apply(slot.get());
            successCount.add(mapped.replaceSlotItems(items, slotSelector));
         });
         return successCount.intValue();
      }

      public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
         this.slots.modifySlots((slot) -> {
            SlotCollection mapped = (SlotCollection)this.mapper.apply(slot.get());
            mapped.modifySlots(consumer, slotSelector);
         });
      }
   }

   public static record Limited(SlotCollection slots, int limit) implements SlotCollection {
      public Stream itemCopies() {
         return this.slots.itemCopies().limit((long)this.limit);
      }

      public int size() {
         return Math.min(this.slots.size(), this.limit);
      }

      public int replaceSlotItems(final ItemProvider items, final SlotSelector slotSelector) {
         return this.slots.replaceSlotItems(items, slotSelector.limit(this.limit));
      }

      public void modifySlots(final Consumer consumer, final SlotSelector slotSelector) {
         this.slots.modifySlots(consumer, slotSelector.limit(this.limit));
      }

      public SlotCollection limit(final int limit) {
         return new SlotCollection.Limited(this.slots, Math.min(this.limit, limit));
      }
   }
}
