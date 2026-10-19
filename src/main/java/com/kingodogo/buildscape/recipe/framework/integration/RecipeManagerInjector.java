package com.kingodogo.buildscape.recipe.framework.integration;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.mixin.RecipeManagerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class RecipeManagerInjector {

    private RecipeManagerInjector() {
    }

    public static void inject(RecipeManager recipeManager, List<Recipe<?>> newRecipes) {
        inject(recipeManager, newRecipes, Set.of());
    }

    public static void inject(RecipeManager recipeManager, List<Recipe<?>> newRecipes,
                              Set<ResourceLocation> disabledIds) {
        if (recipeManager == null || newRecipes == null || newRecipes.isEmpty()) {
            return;
        }

        try {
            RecipeManagerAccessor accessor = (RecipeManagerAccessor) recipeManager;
            RecipeIndexes indexes = mergeIndexes(accessor.buildscape$getRecipes(),
                    accessor.buildscape$getByName(), newRecipes, disabledIds);
            accessor.buildscape$setRecipes(indexes.byType());
            accessor.buildscape$setByName(indexes.byName());
            BuildScape.LOGGER.debug("BDRE Injector: Injected {} custom recipes into RecipeManager.", newRecipes.size());
        } catch (RuntimeException | LinkageError e) {
            BuildScape.LOGGER.error("BDRE Injector: Critical failure injecting recipes into RecipeManager", e);
        }
    }

    public record RecipeIndexes(
            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> byType,
            Map<ResourceLocation, Recipe<?>> byName) {
    }

    public static RecipeIndexes mergeIndexes(
            Map<RecipeType<?>, ? extends Map<ResourceLocation, ? extends Recipe<?>>> existingByType,
            Map<ResourceLocation, ? extends Recipe<?>> existingByName,
            Collection<? extends Recipe<?>> newRecipes,
            Set<ResourceLocation> disabledIds) {
        Indexes<RecipeType<?>, ResourceLocation, Recipe<?>> indexes = mergeIndexesGeneric(
                existingByType, existingByName, newRecipes, Recipe::getType, Recipe::getId, disabledIds);
        return new RecipeIndexes(indexes.byType(), indexes.byName());
    }

    public record Indexes<T, K, R>(Map<T, Map<K, R>> byType, Map<K, R> byName) {
    }

    public static <T, K, R> Indexes<T, K, R> mergeIndexesGeneric(
            Map<T, ? extends Map<K, ? extends R>> existingByType,
            Map<K, ? extends R> existingByName,
            Collection<? extends R> newRecipes,
            Function<? super R, ? extends T> type,
            Function<? super R, ? extends K> id) {
        return mergeIndexesGeneric(existingByType, existingByName, newRecipes, type, id, Set.of());
    }

    /**
     * Merges new recipes into the existing indexes. A new recipe is skipped when its id already exists
     * (a datapack or script override wins) or when its id is in {@code disabledIds}.
     */
    public static <T, K, R> Indexes<T, K, R> mergeIndexesGeneric(
            Map<T, ? extends Map<K, ? extends R>> existingByType,
            Map<K, ? extends R> existingByName,
            Collection<? extends R> newRecipes,
            Function<? super R, ? extends T> type,
            Function<? super R, ? extends K> id,
            Set<? extends K> disabledIds) {
        Map<T, Map<K, R>> byType = new HashMap<>();
        if (existingByType != null) {
            existingByType.forEach((recipeType, recipes) -> byType.put(recipeType, new LinkedHashMap<>(recipes)));
        }

        Map<K, R> byName = new LinkedHashMap<>();
        if (existingByName != null) {
            byName.putAll(existingByName);
        }

        if (newRecipes != null) {
            for (R recipe : newRecipes) {
                if (recipe == null) continue;
                K recipeId = id.apply(recipe);
                if (byName.containsKey(recipeId) || (disabledIds != null && disabledIds.contains(recipeId))) {
                    continue;
                }
                byType.computeIfAbsent(type.apply(recipe), ignored -> new LinkedHashMap<>())
                        .put(recipeId, recipe);
                byName.put(recipeId, recipe);
            }
        }

        // Keep insertion order like vanilla's ImmutableMap: recipe lookup returns the first match, and
        // Map.copyOf iterates in a per-JVM random order, so overlapping recipes would win differently per launch.
        Map<T, Map<K, R>> immutableByType = new LinkedHashMap<>();
        byType.forEach((recipeType, recipes) -> immutableByType.put(recipeType, Collections.unmodifiableMap(recipes)));
        return new Indexes<>(Collections.unmodifiableMap(immutableByType), Collections.unmodifiableMap(byName));
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
                    merged.putIfAbsent(recipe.getId(), recipe);
                }
            }
        }
        return merged;
    }
}
