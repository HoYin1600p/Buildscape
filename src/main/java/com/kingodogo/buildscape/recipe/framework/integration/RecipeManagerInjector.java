package com.kingodogo.buildscape.recipe.framework.integration;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeManagerInjector {

    private static Field recipesField;
    private static Field byNameField;

    static {
        try {
            for (Field f : RecipeManager.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    if (recipesField == null) {
                        recipesField = f;
                    } else if (byNameField == null) {
                        byNameField = f;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("BDRE Injector: Failed to locate RecipeManager map fields", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void inject(RecipeManager recipeManager, List<Recipe<?>> newRecipes) {
        if (recipeManager == null || newRecipes == null || newRecipes.isEmpty()) {
            return;
        }

        if (recipesField == null || byNameField == null) {
            BuildScape.LOGGER.error("BDRE Injector: RecipeManager map fields are unavailable.");
            return;
        }

        try {
            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipesMap =
                    (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>) recipesField.get(recipeManager);
            Map<ResourceLocation, Recipe<?>> byNameMap =
                    (Map<ResourceLocation, Recipe<?>>) byNameField.get(recipeManager);

            if (recipesMap == null || byNameMap == null) {
                BuildScape.LOGGER.error("BDRE Injector: RecipeManager internal maps are null.");
                return;
            }

            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> mutableRecipesMap = new HashMap<>(recipesMap);
            Map<ResourceLocation, Recipe<?>> mutableByNameMap = new HashMap<>(byNameMap);

            int injectedCount = 0;
            for (Recipe<?> recipe : newRecipes) {
                RecipeType<?> type = recipe.getType();
                ResourceLocation id = recipe.getId();

                Map<ResourceLocation, Recipe<?>> typeMap = mutableRecipesMap.computeIfAbsent(type, t -> new HashMap<>());
                if (!(typeMap instanceof HashMap)) {
                    typeMap = new HashMap<>(typeMap);
                    mutableRecipesMap.put(type, typeMap);
                }

                typeMap.put(id, recipe);
                mutableByNameMap.put(id, recipe);
                injectedCount++;
            }

            recipesField.set(recipeManager, mutableRecipesMap);
            byNameField.set(recipeManager, mutableByNameMap);

            BuildScape.LOGGER.debug("BDRE Injector: Injected {} custom recipes into RecipeManager.", injectedCount);

        } catch (Exception e) {
            BuildScape.LOGGER.error("BDRE Injector: Critical failure injecting recipes into RecipeManager", e);
        }
    }
}
