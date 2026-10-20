package com.kingodogo.buildscape.recipe.framework;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.recipe.framework.cache.BinaryRecipeCache;
import com.kingodogo.buildscape.recipe.framework.compiler.BuildScapeRecipeCompiler;
import com.kingodogo.buildscape.recipe.framework.compiler.StonecuttingCycleValidator;
import com.kingodogo.buildscape.recipe.framework.integration.RecipeManagerInjector;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import com.kingodogo.buildscape.recipe.framework.util.IngredientCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Reload listener that supplies most of the mod's crafting, stonecutting and similar recipes from the compact pack
 * files {@code data/buildscape/recipes_pack/<category>.json}, instead of thousands of single JSON files.
 * On each reload it tries, in order, the bundled binary cache {@code recipes_pack/recipes.bscb}, then the local cache
 * in {@code <game dir>/buildscape/cache}, and compiles the JSON sources (in parallel by category) only if both miss.
 * Categories with conditional recipes are always compiled at load time, because conditions depend on the loaded mods. Recipes listed in
 * {@code buildscape/disabled_recipes.json} files are skipped, and the result is injected into the reload's
 * {@code RecipeManager}.
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuildScapeRecipeLoader implements PreparableReloadListener {

    /** Manager-less instance for code that only needs the loader's parsing helpers; never registered. */
    public static final BuildScapeRecipeLoader INSTANCE = new BuildScapeRecipeLoader(null);

    private static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    private static final String DISABLED_RECIPES_PATH = "buildscape/disabled_recipes.json";

    /** The manager of the reload this listener belongs to; each reload gets its own listener and manager. */
    private final RecipeManager recipeManager;

    private BuildScapeRecipeLoader(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    static BuildScapeRecipeLoader forReload(RecipeManager recipeManager) {
        return new BuildScapeRecipeLoader(recipeManager);
    }

    RecipeManager recipeManager() {
        return recipeManager;
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        // One listener per reload, bound to that reload's RecipeManager. A shared listener would inject into
        // whichever manager registered last, leaving the manager that finishes last without the recipes when
        // two reloads overlap.
        event.addListener(forReload(event.getServerResources().getRecipeManager()));
    }

    /**
     * Reads and compiles recipes on the background executor, then applies them on the game thread once the reload barrier
     * allows it.
     */
    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> new Prepared(
                prepareRecipes(resourceManager, preparationsProfiler),
                loadDisabledIds(resourceManager)), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(prepared -> applyRecipes(prepared.recipes(), prepared.disabledIds(), reloadProfiler),
                        gameExecutor);
    }

    private record Prepared(List<Recipe<?>> recipes, Set<ResourceLocation> disabledIds) {
    }

    /**
     * Reads every {@code data/<namespace>/buildscape/disabled_recipes.json} of the form
     * {@code {"recipes": ["ns:id", ...]}} and merges them. Missing files mean nothing is disabled;
     * unreadable ones are logged and skipped.
     */
    private Set<ResourceLocation> loadDisabledIds(ResourceManager resourceManager) {
        Set<ResourceLocation> disabled = new HashSet<>();
        try {
            for (String namespace : resourceManager.getNamespaces()) {
                ResourceLocation location = new ResourceLocation(namespace, DISABLED_RECIPES_PATH);
                if (!resourceManager.hasResource(location)) {
                    continue;
                }
                try {
                    for (Resource resource : resourceManager.getResources(location)) {
                        try (resource;
                                Reader reader = new InputStreamReader(
                                        resource.getInputStream(), StandardCharsets.UTF_8)) {
                            readDisabledIds(JsonParser.parseReader(reader), disabled, location);
                        } catch (Exception e) {
                            // one unreadable list must not block the other lists
                            BuildScape.LOGGER.warn("BDRE Loader: Could not read disabled recipe list [{}]", location,
                                    e);
                        }
                    }
                } catch (Exception e) {
                    // one unreadable location must not block the other locations
                    BuildScape.LOGGER.warn("BDRE Loader: Could not read disabled recipe list [{}]", location, e);
                }
            }
        } catch (Exception e) {
            // the resource manager lookup failed; no recipes are disabled
            BuildScape.LOGGER.warn("BDRE Loader: Could not look up disabled recipe lists", e);
        }
        return Set.copyOf(disabled);
    }

    private static void readDisabledIds(JsonElement root, Set<ResourceLocation> disabled, ResourceLocation source) {
        if (root == null || !root.isJsonObject() || !root.getAsJsonObject().has("recipes")
                || !root.getAsJsonObject().get("recipes").isJsonArray()) {
            BuildScape.LOGGER.warn("BDRE Loader: Disabled recipe list [{}] has no \"recipes\" array.", source);
            return;
        }
        for (JsonElement element : root.getAsJsonObject().getAsJsonArray("recipes")) {
            ResourceLocation id = element.isJsonPrimitive() ? ResourceLocation.tryParse(element.getAsString()) : null;
            if (id == null) {
                BuildScape.LOGGER.warn("BDRE Loader: Ignoring invalid recipe id {} in [{}].", element, source);
            } else {
                disabled.add(id);
            }
        }
    }

    private List<Recipe<?>> prepareRecipes(ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.push("BDRE_PrepareRecipes");
        IngredientCache.clear();

        long startTime = System.currentTimeMillis();

        Map<String, byte[]> rawCategoryData = new LinkedHashMap<>();

        for (String category : CATEGORIES) {
            ResourceLocation location = new ResourceLocation(BuildScape.MODID, "recipes_pack/" + category + ".json");
            try {
                if (resourceManager.hasResource(location)) {
                    try (Resource resource = resourceManager.getResource(location);
                            InputStream stream = resource.getInputStream()) {
                        rawCategoryData.put(category, stream.readAllBytes());
                    }
                }
            } catch (Exception e) {
                // an unreadable category file is skipped
                BuildScape.LOGGER.error("BDRE Loader: Error reading category file [{}]", location, e);
            }
        }

        String contentHash = BinaryRecipeCache.computeSourceHash(CATEGORIES, rawCategoryData);
        Set<String> runtimeCategories = findRuntimeCategories(rawCategoryData);

        ResourceLocation bundledLocation = new ResourceLocation(BuildScape.MODID, "recipes_pack/recipes.bscb");
        if (resourceManager.hasResource(bundledLocation)) {
            try (Resource res = resourceManager.getResource(bundledLocation);
                    InputStream stream = res.getInputStream()) {
                List<Recipe<?>> bundled = BinaryRecipeCache.loadCacheFromStream(stream, contentHash);
                if (!bundled.isEmpty()) {
                    List<Recipe<?>> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, bundled, true);
                    profiler.pop();
                    BuildScape.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", recipes.size(),
                            System.currentTimeMillis() - startTime);
                    return recipes;
                }
            } catch (Exception e) {
                // the bundled cache is unreadable; fall back to compiling the sources
                BuildScape.LOGGER.warn("BDRE Loader: Exception reading bundled binary cache resource", e);
            }
        }

        if (BinaryRecipeCache.isCacheValid(contentHash)) {
            BuildScape.LOGGER.debug("BDRE Loader: Cache HIT! Fast-loading recipes from local binary cache...");
            List<Recipe<?>> cached = BinaryRecipeCache.loadCache(contentHash);
            if (!cached.isEmpty()) {
                List<Recipe<?>> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, cached, false);
                profiler.pop();
                BuildScape.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", recipes.size(),
                        System.currentTimeMillis() - startTime);
                return recipes;
            }
        }

        BuildScape.LOGGER
                .debug("BDRE Loader: Cache MISS/Invalid. Parallel streaming and compiling category source files...");
        Map<String, List<Recipe<?>>> compiledByCategory = new ConcurrentHashMap<>();

        rawCategoryData.entrySet().parallelStream().forEach(entry -> {
            String category = entry.getKey();
            byte[] bytes = entry.getValue();

            try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack, false);
                compiledByCategory.put(category, result.recipes());
                compiler.clear();
            } catch (Exception e) {
                // a category that fails to parse or compile is skipped; the others still load
                BuildScape.LOGGER.error("BDRE Loader: Failure parsing category [{}]", category, e);
            }
        });

        CompiledRecipes compiled = validateCompiledRecipes(compiledByCategory, runtimeCategories);
        List<Recipe<?>> recipes = compiled.recipes();
        List<Recipe<?>> cacheableSourceRecipes = compiled.cacheableRecipes();

        if (!cacheableSourceRecipes.isEmpty()) {
            BinaryRecipeCache.saveCache(contentHash, cacheableSourceRecipes);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        BuildScape.LOGGER.info("BDRE Loader: Successfully compiled {} recipes across {} categories in {} ms.",
                recipes.size(), rawCategoryData.size(), elapsed);

        profiler.pop();
        return recipes;
    }

    record CompiledRecipes(List<Recipe<?>> recipes, List<Recipe<?>> cacheableRecipes) {
    }

    static CompiledRecipes validateCompiledRecipes(Map<String, List<Recipe<?>>> compiledByCategory,
            Set<String> runtimeCategories) {
        List<Recipe<?>> ordered = new ArrayList<>();
        Set<Recipe<?>> cacheable = new HashSet<>();
        for (String category : CATEGORIES) {
            List<Recipe<?>> recipes = compiledByCategory.getOrDefault(category, List.of());
            ordered.addAll(recipes);
            if (!runtimeCategories.contains(category)) {
                cacheable.addAll(recipes);
            }
        }
        List<Recipe<?>> accepted = combineRuntimeRecipes(List.of(), ordered);
        return new CompiledRecipes(accepted, accepted.stream().filter(cacheable::contains).toList());
    }

    private List<Recipe<?>> appendRuntimeRecipes(
            Map<String, byte[]> rawCategoryData,
            Set<String> runtimeCategories,
            List<Recipe<?>> cachedRecipes,
            boolean trustedBundledCache) {
        if (runtimeCategories.isEmpty()) {
            return combineRuntimeRecipes(cachedRecipes, List.of(), trustedBundledCache);
        }
        List<Recipe<?>> runtimeRecipes = new ArrayList<>();
        for (String category : CATEGORIES) {
            if (!runtimeCategories.contains(category)) {
                continue;
            }
            byte[] categoryData = rawCategoryData.get(category);
            if (categoryData == null) {
                continue;
            }
            try (Reader reader = new InputStreamReader(
                    new ByteArrayInputStream(categoryData), StandardCharsets.UTF_8)) {
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack, false);
                runtimeRecipes.addAll(result.recipes());
                compiler.clear();
            } catch (Exception e) {
                // a category that fails to parse or compile is skipped; the others still load
                BuildScape.LOGGER.error("BDRE Loader: Failure parsing runtime-only category [{}]", category, e);
            }
        }
        List<Recipe<?>> combined = combineRuntimeRecipes(cachedRecipes, runtimeRecipes);
        int added = combined.size() - cachedRecipes.size();
        if (added > 0) {
            BuildScape.LOGGER.info("BDRE Loader: Added {} runtime-only recipes after cache load.", added);
        }
        return combined;
    }

    static List<Recipe<?>> combineRuntimeRecipes(List<Recipe<?>> cachedRecipes, List<Recipe<?>> runtimeRecipes) {
        return combineRuntimeRecipes(cachedRecipes, runtimeRecipes, false);
    }

    static List<Recipe<?>> combineRuntimeRecipes(List<Recipe<?>> cachedRecipes, List<Recipe<?>> runtimeRecipes,
            boolean trustedBundledCache) {
        return combineRuntimeRecipes(cachedRecipes, runtimeRecipes, trustedBundledCache,
                StonecuttingCycleValidator::validate,
                message -> BuildScape.LOGGER
                        .warn("BDRE Loader: Cached recipes contain a gain cycle; filtering cache: {}", message));
    }

    static List<Recipe<?>> combineRuntimeRecipes(List<Recipe<?>> cachedRecipes, List<Recipe<?>> runtimeRecipes,
            Consumer<StonecuttingCycleValidator> validate,
            Consumer<String> reportStaleCache) {
        return combineRuntimeRecipes(cachedRecipes, runtimeRecipes, false, validate, reportStaleCache);
    }

    static List<Recipe<?>> combineRuntimeRecipes(List<Recipe<?>> cachedRecipes, List<Recipe<?>> runtimeRecipes,
            boolean trustedBundledCache, Consumer<StonecuttingCycleValidator> validate,
            Consumer<String> reportStaleCache) {
        if (trustedBundledCache && runtimeRecipes.isEmpty()) {
            return cachedRecipes;
        }
        Map<ResourceLocation, Recipe<?>> recipesById = new LinkedHashMap<>();
        StonecuttingCycleValidator cycles = new StonecuttingCycleValidator();
        for (Recipe<?> recipe : cachedRecipes) {
            recipesById.put(recipe.getId(), recipe);
        }
        rebuildCycles(cycles, recipesById.values());
        if (!recipesById.isEmpty()) {
            try {
                validate.accept(cycles);
            } catch (IllegalArgumentException e) {
                reportStaleCache.accept(e.getMessage());
                List<Recipe<?>> base = new ArrayList<>(recipesById.values());
                recipesById.clear();
                cycles.clear();
                acceptIndividually(recipesById, cycles, base);
            }
        }
        if (runtimeRecipes.isEmpty()) {
            return new ArrayList<>(recipesById.values()).equals(cachedRecipes)
                    ? cachedRecipes
                    : new ArrayList<>(recipesById.values());
        }
        Map<ResourceLocation, Recipe<?>> additions = new LinkedHashMap<>();
        for (Recipe<?> recipe : runtimeRecipes) {
            additions.put(recipe.getId(), recipe);
        }
        Map<ResourceLocation, Recipe<?>> combined = new LinkedHashMap<>(recipesById);
        for (Recipe<?> recipe : additions.values()) {
            if (combined.put(recipe.getId(), recipe) != null) {
                BuildScape.LOGGER.debug("BDRE Loader: Runtime recipe [{}] overrides cached recipe.", recipe.getId());
            }
        }
        rebuildCycles(cycles, combined.values());
        try {
            validate.accept(cycles);
            return new ArrayList<>(combined.values());
        } catch (IllegalArgumentException e) {
            rebuildCycles(cycles, recipesById.values());
            acceptIndividually(recipesById, cycles, additions.values());
        }
        return new ArrayList<>(recipesById.values());
    }

    private static void rebuildCycles(StonecuttingCycleValidator cycles, Collection<Recipe<?>> recipes) {
        cycles.clear();
        recipes.forEach(recipe -> addCycleRecipe(cycles, recipe));
    }

    private static void acceptIndividually(Map<ResourceLocation, Recipe<?>> accepted,
            StonecuttingCycleValidator cycles, Collection<Recipe<?>> candidates) {
        for (Recipe<?> recipe : candidates) {
            Recipe<?> previous = accepted.get(recipe.getId());
            cycles.removeRecipe(recipe.getId().toString());
            try {
                addCycleRecipe(cycles, recipe, true);
                accepted.put(recipe.getId(), recipe);
            } catch (IllegalArgumentException e) {
                cycles.removeRecipe(recipe.getId().toString());
                if (previous != null) {
                    addCycleRecipe(cycles, previous);
                }
                BuildScape.LOGGER.error("BDRE Loader: Rejected recipe [{}]: {}", recipe.getId(), e.getMessage(), e);
            }
        }
    }

    private static void addCycleRecipe(StonecuttingCycleValidator cycles, Recipe<?> recipe) {
        addCycleRecipe(cycles, recipe, false);
    }

    private static void addCycleRecipe(StonecuttingCycleValidator cycles, Recipe<?> recipe, boolean checked) {
        if (!(recipe instanceof StonecutterRecipe)) {
            return;
        }
        ItemStack result = recipe.getResultItem();
        if (result.isEmpty()) {
            return;
        }
        List<String> inputs = new ArrayList<>();
        try {
            for (Ingredient ingredient : recipe.getIngredients()) {
                inputs.addAll(StonecuttingCycleValidator.inputNodes(ingredient.toJson()));
            }
        } catch (RuntimeException e) {
            BuildScape.LOGGER.debug(
                    "BDRE Loader: Could not serialize ingredients for recipe [{}]; using expanded items.",
                    recipe.getId(), e);
            inputs.clear();
            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack input : ingredient.getItems()) {
                    inputs.add(ForgeRegistries.ITEMS.getKey(input.getItem()).toString());
                }
            }
        }
        String output = ForgeRegistries.ITEMS.getKey(result.getItem()).toString();
        if (checked) {
            cycles.addRecipeChecked(recipe.getId().toString(), inputs, output, result.getCount());
        } else {
            cycles.addRecipe(recipe.getId().toString(), inputs, output, result.getCount());
        }
    }

    private Set<String> findRuntimeCategories(Map<String, byte[]> rawCategoryData) {
        Set<String> runtimeCategories = new HashSet<>();
        for (Map.Entry<String, byte[]> entry : rawCategoryData.entrySet()) {
            String source = new String(entry.getValue(), StandardCharsets.UTF_8);
            if (source.contains("\"forge:conditional\"")
                    || source.contains("\"conditions\"")
                    || source.contains("\"botanypots:crop\"")) {
                runtimeCategories.add(entry.getKey());
            }
        }
        return runtimeCategories;
    }

    private void applyRecipes(List<Recipe<?>> recipes, Set<ResourceLocation> disabledIds, ProfilerFiller profiler) {
        profiler.push("BDRE_ApplyRecipes");
        if (recipeManager != null && !recipes.isEmpty()) {
            RecipeManagerInjector.inject(recipeManager, List.copyOf(recipes), disabledIds);
        }
        profiler.pop();
    }
}
