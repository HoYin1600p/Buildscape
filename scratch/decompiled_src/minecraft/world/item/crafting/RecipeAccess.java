package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceKey;

public interface RecipeAccess {
   RecipePropertySet propertySet(ResourceKey id);

   SelectableRecipe.SingleInputSet stonecutterRecipes();
}
