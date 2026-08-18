package net.minecraft.client.multiplayer;

import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class ClientRecipeContainer implements RecipeAccess {
   private final Map itemSets;
   private final SelectableRecipe.SingleInputSet stonecutterRecipes;

   public ClientRecipeContainer(final Map itemSets, final SelectableRecipe.SingleInputSet stonecutterRecipes) {
      this.itemSets = itemSets;
      this.stonecutterRecipes = stonecutterRecipes;
   }

   public RecipePropertySet propertySet(final ResourceKey id) {
      return (RecipePropertySet)this.itemSets.getOrDefault(id, RecipePropertySet.EMPTY);
   }

   public SelectableRecipe.SingleInputSet stonecutterRecipes() {
      return this.stonecutterRecipes;
   }
}
