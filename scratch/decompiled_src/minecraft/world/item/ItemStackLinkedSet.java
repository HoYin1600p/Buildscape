package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class ItemStackLinkedSet {
   private static final Hash.Strategy TYPE_AND_TAG = new Hash.Strategy() {
      public int hashCode(final @Nullable ItemStack item) {
         return ItemStack.hashItemAndComponents(item);
      }

      public boolean equals(final @Nullable ItemStack a, final @Nullable ItemStack b) {
         return a == b || a != null && b != null && a.isEmpty() == b.isEmpty() && ItemStack.isSameItemSameComponents(a, b);
      }
   };

   public static Set createTypeAndComponentsSet() {
      return new ObjectLinkedOpenCustomHashSet(TYPE_AND_TAG);
   }
}
