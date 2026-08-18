package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;

public class RecipeDisplays {
   public static RecipeDisplay.Type bootstrap(final Registry registry) {
      Registry.register(registry, "crafting_shapeless", ShapelessCraftingRecipeDisplay.TYPE);
      Registry.register(registry, "crafting_shaped", ShapedCraftingRecipeDisplay.TYPE);
      Registry.register(registry, "furnace", FurnaceRecipeDisplay.TYPE);
      Registry.register(registry, "stonecutter", StonecutterRecipeDisplay.TYPE);
      return (RecipeDisplay.Type)Registry.register(registry, "smithing", SmithingRecipeDisplay.TYPE);
   }
}
