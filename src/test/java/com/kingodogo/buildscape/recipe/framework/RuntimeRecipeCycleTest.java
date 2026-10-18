package com.kingodogo.buildscape.recipe.framework;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class RuntimeRecipeCycleTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void runtimeCategoriesRejectOnlyTheRecipeClosingAGainCycle() throws Exception {
        List<Recipe<?>> recipes = append(Map.of(
                "crafting", category("gain", "stone", "granite", 4),
                "special", category("return", "granite", "stone", 1)),
                Set.of("crafting", "special"), List.of());
        assertEquals(1, recipes.size());
        assertTrue(recipes.get(0).getId().toString().endsWith("/gain"));
    }

    @Test
    public void runtimeRecipeCannotCloseACycleAgainstCachedRecipes() throws Exception {
        Recipe<?> cached = cut("cached_gain", Items.STONE, Items.GRANITE, 4);
        List<Recipe<?>> recipes = append(Map.of("special", category("return", "granite", "stone", 1)),
                Set.of("special"), List.of(cached));
        assertEquals(List.of(cached), recipes);
        assertSame(cached, recipes.get(0));
    }

    @Test
    public void cyclesWithinOneRuntimeCategoryDoNotDiscardItsOtherRecipes() throws Exception {
        String source = "{\"stonecutting\":["
                + spec("gain", "stone", "granite", 4) + ","
                + spec("return", "granite", "stone", 1) + ","
                + spec("safe", "granite", "diorite", 2) + "]}";
        List<Recipe<?>> recipes = append(Map.of("special", source), Set.of("special"), List.of());
        assertEquals(2, recipes.size());
        assertTrue(recipes.stream().anyMatch(recipe -> recipe.getId().toString().endsWith("/safe")));
        assertFalse(recipes.stream().anyMatch(recipe -> recipe.getId().toString().endsWith("/return")));
    }

    @Test
    public void nonCyclicRuntimeRecipesAndUnitReversalsStillLoad() throws Exception {
        Recipe<?> cached = cut("cached", Items.STONE, Items.GRANITE, 1);
        List<Recipe<?>> recipes = append(Map.of(
                "crafting", category("return", "granite", "stone", 1),
                "special", category("safe_gain", "granite", "diorite", 4)),
                Set.of("crafting", "special"), List.of(cached));
        assertEquals(3, recipes.size());
        assertSame(cached, recipes.get(0));
    }

    @Test
    public void rejectedRecipeEdgesAreRemovedBeforeCheckingLaterRecipes() {
        Recipe<?> cached = cut("cached", Items.STONE, Items.GRANITE, 4);
        Recipe<?> rejected = cut("return", Items.GRANITE, Items.STONE, 1);
        Recipe<?> safe = cut("safe", Items.DIORITE, Items.STONE, 2);
        assertEquals(List.of(cached, safe),
                BuildScapeRecipeLoader.combineRuntimeRecipes(List.of(cached), List.of(rejected, safe)));
    }

    @Test
    public void allIngredientAlternativesParticipateAndCachedIdsArePreserved() {
        Recipe<?> cached = cut("cached", Items.STONE, Items.GRANITE, 4);
        Recipe<?> returnRecipe = new StonecutterRecipe(new ResourceLocation("test", "alternatives"), "",
                Ingredient.of(Items.DIORITE, Items.GRANITE), new ItemStack(Items.STONE));
        Recipe<?> collision = cut("cached", Items.GRANITE, Items.DIORITE, 2);
        assertEquals(List.of(cached), BuildScapeRecipeLoader.combineRuntimeRecipes(
                List.of(cached), List.of(returnRecipe, collision)));
    }

    @Test
    public void noRuntimeCategoriesReturnTheCachedListDirectly() throws Exception {
        List<Recipe<?>> cached = List.of(cut("cached", Items.STONE, Items.GRANITE, 4));
        assertSame(cached, append(Map.of(), Set.of(), cached));
    }

    private static Recipe<?> cut(String id, Item input, Item output, int count) {
        return new StonecutterRecipe(new ResourceLocation("test", id), "",
                Ingredient.of(input), new ItemStack(output, count));
    }

    private static String category(String id, String input, String output, int count) {
        return "{\"stonecutting\":[" + spec(id, input, output, count) + "]}";
    }

    private static String spec(String id, String input, String output, int count) {
        return "{\"id\":\"" + id + "\",\"input\":\"minecraft:" + input
                + "\",\"result\":{\"item\":\"minecraft:" + output + "\",\"count\":" + count + "}}";
    }

    @SuppressWarnings("unchecked")
    private static List<Recipe<?>> append(Map<String, String> sources, Set<String> categories,
                                           List<Recipe<?>> cached) throws Exception {
        Map<String, byte[]> bytes = new java.util.LinkedHashMap<>();
        sources.forEach((category, source) -> bytes.put(category, source.getBytes(StandardCharsets.UTF_8)));
        Method append = BuildScapeRecipeLoader.class.getDeclaredMethod(
                "appendRuntimeRecipes", Map.class, Set.class, List.class);
        append.setAccessible(true);
        return (List<Recipe<?>>) append.invoke(BuildScapeRecipeLoader.INSTANCE, bytes, categories, cached);
    }
}
