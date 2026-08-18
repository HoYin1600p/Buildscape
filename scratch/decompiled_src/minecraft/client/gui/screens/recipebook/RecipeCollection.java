package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class RecipeCollection {
   public static final RecipeCollection EMPTY = new RecipeCollection(List.of());
   private final List entries;
   private final Set craftable = new HashSet();
   private final Set selected = new HashSet();

   public RecipeCollection(final List recipes) {
      this.entries = recipes;
   }

   public void selectRecipes(final StackedItemContents stackedContents, final Predicate selector) {
      for(RecipeDisplayEntry entry : this.entries) {
         boolean isSelected = selector.test(entry.display());
         if (isSelected) {
            this.selected.add(entry.id());
         } else {
            this.selected.remove(entry.id());
         }

         if (isSelected && entry.canCraft(stackedContents)) {
            this.craftable.add(entry.id());
         } else {
            this.craftable.remove(entry.id());
         }
      }

   }

   public boolean isCraftable(final RecipeDisplayId recipe) {
      return this.craftable.contains(recipe);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasAnySelected() {
      return !this.selected.isEmpty();
   }

   public List getRecipes() {
      return this.entries;
   }

   public List getSelectedRecipes(final RecipeCollection.CraftableStatus selector) {
      Predicate var10000;
      switch (selector.ordinal()) {
         case 0:
            var10000 = this.selected::contains;
            break;
         case 1:
            var10000 = this.craftable::contains;
            break;
         case 2:
            var10000 = (recipe) -> this.selected.contains(recipe) && !this.craftable.contains(recipe);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Predicate predicate = var10000;
      List result = new ArrayList();

      for(RecipeDisplayEntry entries : this.entries) {
         if (predicate.test(entries.id())) {
            result.add(entries);
         }
      }

      return result;
   }

   public static enum CraftableStatus {
      ANY,
      CRAFTABLE,
      NOT_CRAFTABLE;

      // $FF: synthetic method
      private static RecipeCollection.CraftableStatus[] $values() {
         return new RecipeCollection.CraftableStatus[]{ANY, CRAFTABLE, NOT_CRAFTABLE};
      }
   }
}
