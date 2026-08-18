package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.world.level.Level;

public interface SmithingRecipe extends Recipe {
   default RecipeType getType() {
      return RecipeType.SMITHING;
   }

   RecipeSerializer getSerializer();

   default boolean matches(final SmithingRecipeInput input, final Level level) {
      return Ingredient.testOptionalIngredient(this.templateIngredient(), input.template()) && this.baseIngredient().test(input.base()) && Ingredient.testOptionalIngredient(this.additionIngredient(), input.addition());
   }

   Optional templateIngredient();

   Ingredient baseIngredient();

   Optional additionIngredient();

   default RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.SMITHING;
   }
}
