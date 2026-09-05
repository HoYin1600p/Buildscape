package com.kingodogo.buildscape.recipe.framework;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.recipe.framework.cache.BinaryRecipeCache;
import com.kingodogo.buildscape.recipe.framework.compiler.BuildScapeRecipeCompiler;
import com.kingodogo.buildscape.recipe.framework.integration.RecipeManagerInjector;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import com.kingodogo.buildscape.recipe.framework.util.IngredientCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuildScapeRecipeLoader implements PreparableReloadListener {

    public static final BuildScapeRecipeLoader INSTANCE = new BuildScapeRecipeLoader();

    private static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    private RecipeManager currentRecipeManager;
    private volatile List<Recipe<?>> loadedRecipes = List.of();

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
        INSTANCE.currentRecipeManager = event.getServerResources().getRecipeManager();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(net.minecraftforge.client.event.RecipesUpdatedEvent event) {
        if (!INSTANCE.loadedRecipes.isEmpty()) {
            RecipeManagerInjector.inject(event.getRecipeManager(), INSTANCE.loadedRecipes);
            BuildScape.LOGGER.debug("BDRE: Re-injected {} recipes into Client RecipeManager on RecipesUpdatedEvent.", INSTANCE.loadedRecipes.size());
        }
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture.supplyAsync(() -> prepareRecipes(resourceManager, preparationsProfiler), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(recipes -> applyRecipes(recipes, reloadProfiler), gameExecutor);
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
                    Resource resource = resourceManager.getResource(location);
                    byte[] bytes = resource.getInputStream().readAllBytes();
                    rawCategoryData.put(category, bytes);
                }
            } catch (Exception e) {
                BuildScape.LOGGER.error("BDRE Loader: Error reading category file [{}]", location, e);
            }
        }

        String contentHash = BinaryRecipeCache.computeSourceHash(CATEGORIES, rawCategoryData);
        Set<String> runtimeCategories = findRuntimeCategories(rawCategoryData);

        ResourceLocation bundledLocation = new ResourceLocation(BuildScape.MODID, "recipes_pack/recipes.bscb");
        if (resourceManager.hasResource(bundledLocation)) {
            try {
                Resource res = resourceManager.getResource(bundledLocation);
                List<Recipe<?>> bundled = BinaryRecipeCache.loadCacheFromStream(res.getInputStream(), contentHash);
                if (!bundled.isEmpty()) {
                    List<Recipe<?>> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, bundled);
                    profiler.pop();
                    BuildScape.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", recipes.size(), System.currentTimeMillis() - startTime);
                    return recipes;
                }
            } catch (Exception e) {
                BuildScape.LOGGER.warn("BDRE Loader: Exception reading bundled binary cache resource", e);
            }
        }

        if (BinaryRecipeCache.isCacheValid(contentHash)) {
            BuildScape.LOGGER.debug("BDRE Loader: Cache HIT! Fast-loading recipes from local binary cache...");
            List<Recipe<?>> cached = BinaryRecipeCache.loadCache(contentHash);
            if (!cached.isEmpty()) {
                List<Recipe<?>> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, cached);
                profiler.pop();
                BuildScape.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", recipes.size(), System.currentTimeMillis() - startTime);
                return recipes;
            }
        }

        BuildScape.LOGGER.debug("BDRE Loader: Cache MISS/Invalid. Parallel streaming and compiling category source files...");
        Map<String, List<Recipe<?>>> compiledByCategory = new java.util.concurrent.ConcurrentHashMap<>();

        rawCategoryData.entrySet().parallelStream().forEach(entry -> {
            String category = entry.getKey();
            byte[] bytes = entry.getValue();

            try (Reader reader = new InputStreamReader(new java.io.ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack);
                compiledByCategory.put(category, result.recipes());
                compiler.clear();
            } catch (Exception e) {
                BuildScape.LOGGER.error("BDRE Loader: Failure parsing category [{}]", category, e);
            }
        });

        List<Recipe<?>> recipes = new ArrayList<>();
        List<Recipe<?>> cacheableSourceRecipes = new ArrayList<>();
        for (String category : CATEGORIES) {
            List<Recipe<?>> categoryRecipes = compiledByCategory.getOrDefault(category, List.of());
            recipes.addAll(categoryRecipes);
            if (!runtimeCategories.contains(category)) {
                cacheableSourceRecipes.addAll(categoryRecipes);
            }
        }

        if (!cacheableSourceRecipes.isEmpty()) {
            BinaryRecipeCache.saveCache(contentHash, cacheableSourceRecipes);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        BuildScape.LOGGER.info("BDRE Loader: Successfully compiled {} recipes across {} categories in {} ms.", recipes.size(), rawCategoryData.size(), elapsed);

        profiler.pop();
        return recipes;
    }

    private List<Recipe<?>> appendRuntimeRecipes(
            Map<String, byte[]> rawCategoryData,
            Set<String> runtimeCategories,
            List<Recipe<?>> cachedRecipes) {
        Map<ResourceLocation, Recipe<?>> recipesById = new LinkedHashMap<>();
        for (Recipe<?> recipe : cachedRecipes) {
            recipesById.put(recipe.getId(), recipe);
        }

        int added = 0;
        for (String category : CATEGORIES) {
            if (!runtimeCategories.contains(category)) {
                continue;
            }
            byte[] categoryData = rawCategoryData.get(category);
            if (categoryData == null) {
                continue;
            }
            try (Reader reader = new InputStreamReader(
                    new java.io.ByteArrayInputStream(categoryData), StandardCharsets.UTF_8)) {
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack);
                for (Recipe<?> recipe : result.recipes()) {
                    recipesById.put(recipe.getId(), recipe);
                    added++;
                }
                compiler.clear();
            } catch (Exception e) {
                BuildScape.LOGGER.error("BDRE Loader: Failure parsing runtime-only category [{}]", category, e);
            }
        }
        if (added > 0) {
            BuildScape.LOGGER.info("BDRE Loader: Added {} runtime-only recipes after cache load.", added);
        }
        return new ArrayList<>(recipesById.values());
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

    private void applyRecipes(List<Recipe<?>> recipes, ProfilerFiller profiler) {
        profiler.push("BDRE_ApplyRecipes");
        loadedRecipes = List.copyOf(recipes);
        if (currentRecipeManager != null && !loadedRecipes.isEmpty()) {
            RecipeManagerInjector.inject(currentRecipeManager, loadedRecipes);
        }
        profiler.pop();
    }
}
