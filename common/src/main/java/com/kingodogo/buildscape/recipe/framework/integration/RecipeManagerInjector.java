package com.kingodogo.buildscape.recipe.framework.integration;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class RecipeManagerInjector {

    private RecipeManagerInjector() {
    }

    /** Native recipe IDs differ between clusters; adapters supply their ID accessor. */
    public static <K, R> Map<K, R> mergeById(Collection<? extends R> existingRecipes,
            Collection<? extends R> newRecipes, Function<? super R, ? extends K> id) {
        Map<K, R> merged = new LinkedHashMap<>();
        if (existingRecipes != null) {
            for (R recipe : existingRecipes) {
                if (recipe != null) merged.put(id.apply(recipe), recipe);
            }
        }
        if (newRecipes != null) {
            for (R recipe : newRecipes) {
                if (recipe != null) merged.put(id.apply(recipe), recipe);
            }
        }
        return merged;
    }

    public static void inject(RecipeManager recipeManager, List<RecipeIR.CompiledRecipe> newRecipes) {
        if (recipeManager == null || newRecipes == null || newRecipes.isEmpty()) {
            return;
        }
        try {
            Services.PLATFORM.injectRecipes(recipeManager, newRecipes);
        } catch (RuntimeException exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error(
                    "BDRE Injector: Critical failure injecting recipes into RecipeManager", exception);
        }
    }
}
