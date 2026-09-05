package com.kingodogo.buildscape.recipe.framework.integration;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RecipeManagerInjector {

    private RecipeManagerInjector() {
    }

    public static void inject(RecipeManager recipeManager, List<Recipe<?>> newRecipes) {
        if (recipeManager == null || newRecipes == null || newRecipes.isEmpty()) {
            return;
        }

        try {
            Map<ResourceLocation, Recipe<?>> merged = mergeById(recipeManager.getRecipes(), newRecipes);
            recipeManager.replaceRecipes(merged.values());
            BuildScape.LOGGER.debug("BDRE Injector: Injected {} custom recipes into RecipeManager.", newRecipes.size());
        } catch (RuntimeException e) {
            BuildScape.LOGGER.error("BDRE Injector: Critical failure injecting recipes into RecipeManager", e);
        }
    }

    static Map<ResourceLocation, Recipe<?>> mergeById(
            Collection<Recipe<?>> existingRecipes,
            Collection<Recipe<?>> newRecipes) {
        Map<ResourceLocation, Recipe<?>> merged = new LinkedHashMap<>();
        if (existingRecipes != null) {
            for (Recipe<?> recipe : existingRecipes) {
                if (recipe != null) {
                    merged.put(recipe.getId(), recipe);
                }
            }
        }
        if (newRecipes != null) {
            for (Recipe<?> recipe : newRecipes) {
                if (recipe != null) {
                    merged.put(recipe.getId(), recipe);
                }
            }
        }
        return merged;
    }
}
