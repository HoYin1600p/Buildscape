package com.kingodogo.buildscape.recipe.framework.integration;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

public final class RecipeManagerInjector {

    private RecipeManagerInjector() {
    }

    public static void inject(RecipeManager recipeManager, List<RecipeIR.CompiledRecipe> newRecipes) {
        if (recipeManager == null || newRecipes == null || newRecipes.isEmpty()) {
            return;
        }
        Services.PLATFORM.injectRecipes(recipeManager, newRecipes);
    }
}
