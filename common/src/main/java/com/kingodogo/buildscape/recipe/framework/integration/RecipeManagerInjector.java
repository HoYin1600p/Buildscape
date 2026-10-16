package com.kingodogo.buildscape.recipe.framework.integration;

import com.kingodogo.buildscape.platform.Services;
import com.google.common.collect.ImmutableMap;
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

    public record Indexes<T, K, R>(Map<T, Map<K, R>> byType, Map<K, R> byName) {
    }

    /** Copy vanilla indexes, replacing same-ID recipes in both indexes. */
    public static <T, K, R> Indexes<T, K, R> mergeIndexesGeneric(
            Map<T, ? extends Map<K, ? extends R>> existingByType,
            Map<K, ? extends R> existingByName,
            Collection<? extends R> newRecipes,
            Function<? super R, ? extends T> type,
            Function<? super R, ? extends K> id) {
        Map<T, Map<K, R>> byType = new LinkedHashMap<>();
        if (existingByType != null) {
            existingByType.forEach((recipeType, recipes) -> byType.put(recipeType, new LinkedHashMap<>(recipes)));
        }
        Map<K, R> byName = new LinkedHashMap<>();
        if (existingByName != null) byName.putAll(existingByName);
        if (newRecipes != null) {
            for (R recipe : newRecipes) {
                if (recipe == null) continue;
                K recipeId = id.apply(recipe);
                T recipeType = type.apply(recipe);
                R previous = byName.put(recipeId, recipe);
                if (previous != null && !java.util.Objects.equals(type.apply(previous), recipeType)) {
                    Map<K, R> previousType = byType.get(type.apply(previous));
                    if (previousType != null) previousType.remove(recipeId);
                }
                byType.computeIfAbsent(recipeType, ignored -> new LinkedHashMap<>()).put(recipeId, recipe);
            }
        }
        Map<T, Map<K, R>> immutableByType = new LinkedHashMap<>();
        byType.forEach((recipeType, recipes) -> immutableByType.put(recipeType, ImmutableMap.copyOf(recipes)));
        return new Indexes<>(ImmutableMap.copyOf(immutableByType), ImmutableMap.copyOf(byName));
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
        } catch (RuntimeException | LinkageError exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error(
                    "BDRE Injector: Critical failure injecting recipes into RecipeManager", exception);
        }
    }
}
