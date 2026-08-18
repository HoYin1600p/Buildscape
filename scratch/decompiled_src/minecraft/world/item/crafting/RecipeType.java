package net.minecraft.world.item.crafting;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public interface RecipeType {
   RecipeType CRAFTING = register("crafting");
   RecipeType SMELTING = register("smelting");
   RecipeType BLASTING = register("blasting");
   RecipeType SMOKING = register("smoking");
   RecipeType CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType STONECUTTING = register("stonecutting");
   RecipeType SMITHING = register("smithing");
   RecipeType BREWING = register("brewing");

   static RecipeType register(final String name) {
      return (RecipeType)Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.withDefaultNamespace(name), new RecipeType() {
         public String toString() {
            return name;
         }
      });
   }
}
