package com.kingodogo.buildscape.recipe.framework;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.recipe.framework.compiler.AliasResolver;
import com.kingodogo.buildscape.recipe.framework.compiler.BuildScapeRecipeCompiler;
import com.kingodogo.buildscape.recipe.framework.compiler.StonecuttingCycleValidator;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.junit.Test;

import java.lang.reflect.Method;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RuntimeRecipeCycleTest extends BootstrappedGameTest {

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
    public void allIngredientAlternativesParticipateAndRuntimeIdsOverrideCachedRecipes() {
        Recipe<?> cached = cut("cached", Items.STONE, Items.GRANITE, 4);
        Recipe<?> returnRecipe = new StonecutterRecipe(new ResourceLocation("test", "alternatives"), "",
                Ingredient.of(Items.DIORITE, Items.GRANITE), new ItemStack(Items.STONE));
        Recipe<?> collision = cut("cached", Items.GRANITE, Items.DIORITE, 2);
        assertEquals(List.of(cached), BuildScapeRecipeLoader.combineRuntimeRecipes(
                List.of(cached), List.of(returnRecipe)));
        assertEquals(List.of(collision), BuildScapeRecipeLoader.combineRuntimeRecipes(
                List.of(cached), List.of(collision)));
    }

    @Test
    public void cyclicOverrideRestoresTheCachedRecipeAndItsEdges() {
        Recipe<?> cached = cut("cached", Items.STONE, Items.GRANITE, 1);
        Recipe<?> invalid = cut("cached", Items.STONE, Items.STONE, 2);
        Recipe<?> safe = cut("safe", Items.GRANITE, Items.DIORITE, 2);
        assertEquals(List.of(cached, safe), BuildScapeRecipeLoader.combineRuntimeRecipes(
                List.of(cached), List.of(invalid, safe)));
    }

    @Test
    public void cacheMissFiltersEveryCategoryBeforeSelectingCacheContents() throws Exception {
        Recipe<?> runtime = cut("runtime", Items.DIORITE, Items.ANDESITE, 1);
        String cyclic = "{\"stonecutting\":[" + spec("gain", "stone", "granite", 4) + ","
                + spec("return", "granite", "stone", 1) + "]}";
        List<Recipe<?>> cacheable = new BuildScapeRecipeCompiler().compileCategory(
                StreamingRecipeParser.parseCategory("stonecutting", new StringReader(cyclic)), false).recipes();
        var compiled = BuildScapeRecipeLoader.validateCompiledRecipes(
                Map.of("special", List.of(runtime), "stonecutting", cacheable), Set.of("special"));
        assertEquals(List.of(cacheable.get(0), runtime), compiled.recipes());
        assertEquals(List.of(cacheable.get(0)), compiled.cacheableRecipes());
        assertFalse(compiled.cacheableRecipes().contains(cacheable.get(1)));
    }

    @Test
    public void cacheMissUsesCategoryOrderEvenWhenTheRejectedRecipeWouldHaveBeenCacheable() {
        Recipe<?> runtime = cut("runtime_gain", Items.STONE, Items.GRANITE, 4);
        Recipe<?> closing = cut("cacheable_return", Items.GRANITE, Items.STONE, 1);
        var compiled = BuildScapeRecipeLoader.validateCompiledRecipes(
                Map.of("stonecutting", List.of(closing), "crafting", List.of(runtime)), Set.of("crafting"));
        assertEquals(List.of(runtime), compiled.recipes());
        assertTrue(compiled.cacheableRecipes().isEmpty());
    }

    @Test
    public void hundredsOfSafeRuntimeRecipesUseOneBatchValidation() {
        List<Recipe<?>> runtime = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            runtime.add(cut("safe_" + i, Items.GRANITE, Items.DIORITE, 2));
        }
        AtomicInteger validations = new AtomicInteger();
        AtomicInteger staleReports = new AtomicInteger();
        assertEquals(runtime, BuildScapeRecipeLoader.combineRuntimeRecipes(List.of(), runtime, cycles -> {
            validations.incrementAndGet();
            cycles.validate();
        }, message -> staleReports.incrementAndGet()));
        assertEquals(1, validations.get());
        Recipe<?> cached = cut("cached", Items.STONE, Items.GRANITE, 1);
        validations.set(0);
        assertEquals(401, BuildScapeRecipeLoader.combineRuntimeRecipes(List.of(cached), runtime, cycles -> {
            validations.incrementAndGet();
            cycles.validate();
        }, message -> staleReports.incrementAndGet()).size());
        assertEquals(2, validations.get());
        assertEquals(0, staleReports.get());
    }

    @Test
    public void staleCacheCycleIsReportedOnceAndDoesNotRejectUnrelatedRuntimeRecipes() {
        Recipe<?> gain = cut("gain", Items.STONE, Items.GRANITE, 4);
        Recipe<?> closing = cut("return", Items.GRANITE, Items.STONE, 1);
        Recipe<?> safe = cut("safe", Items.GRANITE, Items.DIORITE, 2);
        AtomicInteger reports = new AtomicInteger();
        assertEquals(List.of(gain, safe), BuildScapeRecipeLoader.combineRuntimeRecipes(
                List.of(gain, closing), List.of(safe), StonecuttingCycleValidator::validate,
                message -> reports.incrementAndGet()));
        assertEquals(1, reports.get());
    }

    @Test
    public void runtimeInputNodesMatchGeneratorItemsTagsAliasesAndArrays() throws Exception {
        AliasResolver aliases = new AliasResolver();
        aliases.registerAliases(Map.of("ROCK", "minecraft:stone", "ROCK_TAG", "forge:stone"));
        List<String> generatorInputs = List.of("minecraft:stone", "MC:stone", "ROCK",
                "#forge:stone", "#F:stone", "#ROCK_TAG", "[MC:stone, #F:stone, ROCK]");
        List<String> ingredientJson = List.of("{\"item\":\"minecraft:stone\"}",
                "{\"item\":\"minecraft:stone\"}", "{\"item\":\"minecraft:stone\"}",
                "{\"tag\":\"forge:stone\"}", "{\"tag\":\"forge:stone\"}",
                "{\"tag\":\"forge:stone\"}",
                "[{\"item\":\"minecraft:stone\"},{\"tag\":\"forge:stone\"},{\"item\":\"minecraft:stone\"}]");
        Method add = StonecuttingCycleValidator.class.getDeclaredMethod(
                "addInput", String.class, String.class, int.class, String.class, AliasResolver.class);
        add.setAccessible(true);
        var outgoing = StonecuttingCycleValidator.class.getDeclaredField("outgoing");
        outgoing.setAccessible(true);
        for (int i = 0; i < generatorInputs.size(); i++) {
            StonecuttingCycleValidator cycles = new StonecuttingCycleValidator();
            add.invoke(cycles, generatorInputs.get(i), "minecraft:granite", 1, "test:parity", aliases);
            var generated = (Map<?, ?>) outgoing.get(cycles);
            assertEquals(generatorInputs.get(i), generated.keySet(),
                    new java.util.LinkedHashSet<>(StonecuttingCycleValidator.inputNodes(
                            JsonParser.parseString(ingredientJson.get(i)))));
        }
    }

    @Test
    public void incrementalChecksAgreeWithFullValidationOnSeededGraphs() {
        Random random = new Random(846203L);
        for (int graph = 0; graph < 600; graph++) {
            StonecuttingCycleValidator full = new StonecuttingCycleValidator();
            StonecuttingCycleValidator incremental = new StonecuttingCycleValidator();
            for (StonecuttingCycleValidator cycles : List.of(full, incremental)) {
                cycles.addRecipe("test:unit_forward", List.of("test:0"), "test:1", 1);
                cycles.addRecipe("test:unit_return", List.of("test:1"), "test:0", 1);
                cycles.addRecipe("test:safe_gain", List.of("test:1"), "test:2", 2);
            }
            for (int candidate = 0; candidate < 24; candidate++) {
                String id = "test:candidate_" + candidate;
                List<String> inputs = new ArrayList<>();
                for (int alternative = 0, n = 1 + random.nextInt(3); alternative < n; alternative++) {
                    inputs.add("test:" + random.nextInt(8));
                }
                String output = "test:" + random.nextInt(8);
                int count = new int[]{0, 1, 1, 2, Integer.MAX_VALUE}[random.nextInt(5)];
                full.addRecipe(id, inputs, output, count);
                boolean rejected = false;
                try {
                    full.validate();
                } catch (IllegalArgumentException e) {
                    rejected = true;
                    full.removeRecipe(id);
                }
                boolean incrementalRejected = false;
                try {
                    incremental.addRecipeChecked(id, inputs, output, count);
                } catch (IllegalArgumentException e) {
                    incrementalRejected = true;
                    assertTrue(e.getMessage().contains(id));
                }
                assertEquals("graph " + graph + ", candidate " + candidate, rejected, incrementalRejected);
                incremental.validate();
            }
        }
    }

    @Test
    public void thousandsOfRecipesWithOneOffenderUseCheapFallbacks() {
        List<Recipe<?>> candidates = new ArrayList<>();
        for (int i = 0; i < 4000; i++) {
            candidates.add(cut("safe_" + i, Items.STONE, Items.GRANITE, 2));
        }
        List<Recipe<?>> expected = List.copyOf(candidates);
        candidates.add(2000, cut("offender", Items.GRANITE, Items.STONE, 1));
        for (boolean staleCache : List.of(false, true)) {
            AtomicInteger validations = new AtomicInteger();
            AtomicInteger reports = new AtomicInteger();
            long start = System.nanoTime();
            List<Recipe<?>> accepted = BuildScapeRecipeLoader.combineRuntimeRecipes(
                    staleCache ? candidates : List.of(), staleCache ? List.of() : candidates, cycles -> {
                        validations.incrementAndGet();
                        cycles.validate();
                    }, message -> reports.incrementAndGet());
            long elapsed = System.nanoTime() - start;
            assertEquals(expected, accepted);
            assertEquals(1, validations.get());
            assertEquals(staleCache ? 1 : 0, reports.get());
            System.out.println("4001 stonecutting recipes, stale cache=" + staleCache + ": "
                    + elapsed / 1_000_000.0 + " ms");
            assertTrue("Fallback took " + elapsed / 1_000_000.0 + " ms", elapsed < 10_000_000_000L);
        }
    }

    @Test
    public void customIngredientSerializationFailureFallsBackToItemsForCycleChecks() {
        AtomicInteger attempts = new AtomicInteger();
        Ingredient custom = new Ingredient(java.util.stream.Stream.empty()) {
            @Override
            public JsonElement toJson() {
                attempts.incrementAndGet();
                throw new IllegalStateException("Ingredient serializer is unavailable");
            }

            @Override
            public ItemStack[] getItems() {
                return new ItemStack[]{new ItemStack(Items.GRANITE)};
            }
        };
        Recipe<?> gain = cut("gain", Items.STONE, Items.GRANITE, 4);
        Recipe<?> closing = new StonecutterRecipe(new ResourceLocation("test", "custom_return"), "",
                custom, new ItemStack(Items.STONE));
        Recipe<?> safe = new StonecutterRecipe(new ResourceLocation("test", "custom_safe"), "",
                custom, new ItemStack(Items.DIORITE));
        assertEquals(List.of(gain), BuildScapeRecipeLoader.combineRuntimeRecipes(List.of(gain), List.of(closing)));
        assertEquals(List.of(gain, safe), BuildScapeRecipeLoader.combineRuntimeRecipes(List.of(gain), List.of(safe)));
        assertTrue(attempts.get() > 0);
    }

    @Test
    public void noRuntimeCategoriesReturnTheCachedListDirectly() throws Exception {
        List<Recipe<?>> cached = List.of(cut("cached", Items.STONE, Items.GRANITE, 4));
        assertSame(cached, append(Map.of(), Set.of(), cached));
    }

    @Test
    public void bundledCacheWithoutRuntimeRecipesSkipsSerializationAndValidation() throws Exception {
        Ingredient input = new Ingredient(java.util.stream.Stream.empty()) {
            @Override
            public JsonElement toJson() {
                throw new AssertionError("Trusted bundled cache must not rebuild the graph");
            }
        };
        List<Recipe<?>> bundled = List.of(new StonecutterRecipe(new ResourceLocation("test", "bundled"), "",
                input, new ItemStack(Items.GRANITE, 2)));
        assertSame(bundled, append(Map.of(), Set.of(), bundled, true));
        assertSame(bundled, BuildScapeRecipeLoader.combineRuntimeRecipes(bundled, List.of(), true,
                cycles -> fail("Trusted bundled cache must not be validated"),
                message -> fail("Trusted bundled cache must not be filtered")));
    }

    @Test
    public void localCacheValidatesOnceAndBundledCacheWithRuntimeRecipesStillValidates() {
        List<Recipe<?>> cached = List.of(cut("cached", Items.STONE, Items.GRANITE, 2));
        AtomicInteger validations = new AtomicInteger();
        java.util.function.Consumer<StonecuttingCycleValidator> validate = cycles -> {
            validations.incrementAndGet();
            cycles.validate();
        };
        assertSame(cached, BuildScapeRecipeLoader.combineRuntimeRecipes(cached, List.of(), false, validate,
                message -> fail(message)));
        assertEquals(1, validations.get());
        validations.set(0);
        Recipe<?> runtime = cut("runtime", Items.GRANITE, Items.DIORITE, 1);
        assertEquals(List.of(cached.get(0), runtime), BuildScapeRecipeLoader.combineRuntimeRecipes(
                cached, List.of(runtime), true, validate, message -> fail(message)));
        assertEquals(2, validations.get());
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

    private static List<Recipe<?>> append(Map<String, String> sources, Set<String> categories,
            List<Recipe<?>> cached) throws Exception {
        return append(sources, categories, cached, false);
    }

    @SuppressWarnings("unchecked")
    private static List<Recipe<?>> append(Map<String, String> sources, Set<String> categories,
            List<Recipe<?>> cached, boolean trustedBundledCache) throws Exception {
        Map<String, byte[]> bytes = new java.util.LinkedHashMap<>();
        sources.forEach((category, source) -> bytes.put(category, source.getBytes(StandardCharsets.UTF_8)));
        Method append = BuildScapeRecipeLoader.class.getDeclaredMethod(
                "appendRuntimeRecipes", Map.class, Set.class, List.class, boolean.class);
        append.setAccessible(true);
        return (List<Recipe<?>>) append.invoke(BuildScapeRecipeLoader.INSTANCE, bytes, categories, cached,
                trustedBundledCache);
    }
}
