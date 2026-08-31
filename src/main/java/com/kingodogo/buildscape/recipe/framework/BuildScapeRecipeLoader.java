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
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Primary Datapack Reload Listener for the BuildScape Dynamic Compact Recipe Engine (BDRE).
 * Hooks into AddReloadListenerEvent to stream, compile, cache, and inject 10,000+ custom recipes.
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuildScapeRecipeLoader implements PreparableReloadListener {

    public static final BuildScapeRecipeLoader INSTANCE = new BuildScapeRecipeLoader();

    private static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    private RecipeManager currentRecipeManager;
    private final List<Recipe<?>> loadedRecipes = new ArrayList<>();

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

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (INSTANCE.currentRecipeManager != null && !INSTANCE.loadedRecipes.isEmpty()) {
            RecipeManagerInjector.inject(INSTANCE.currentRecipeManager, INSTANCE.loadedRecipes);
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
        loadedRecipes.clear();
        IngredientCache.clear();

        long startTime = System.currentTimeMillis();

        // Step 1: Collect resources and compute hash
        Map<String, byte[]> rawCategoryData = new LinkedHashMap<>();
        ByteArrayOutputStream hashBuffer = new ByteArrayOutputStream();

        for (String category : CATEGORIES) {
            ResourceLocation location = new ResourceLocation(BuildScape.MODID, "recipes_pack/" + category + ".json");
            try {
                if (resourceManager.hasResource(location)) {
                    Resource resource = resourceManager.getResource(location);
                    byte[] bytes = resource.getInputStream().readAllBytes();
                    rawCategoryData.put(category, bytes);
                    hashBuffer.write(bytes);
                }
            } catch (Exception e) {
                BuildScape.LOGGER.error("BDRE Loader: Error reading category file [{}]", location, e);
            }
        }

        String contentHash = BinaryRecipeCache.computeHash(hashBuffer.toByteArray());

        // Step 2A: Check Bundled JAR Binary Cache
        ResourceLocation bundledLocation = new ResourceLocation(BuildScape.MODID, "recipes_pack/recipes.bscb");
        if (resourceManager.hasResource(bundledLocation)) {
            try {
                Resource res = resourceManager.getResource(bundledLocation);
                List<Recipe<?>> bundled = BinaryRecipeCache.loadCacheFromStream(res.getInputStream(), contentHash);
                if (!bundled.isEmpty()) {
                    loadedRecipes.addAll(bundled);
                    appendNonCacheableSpecialRecipes(rawCategoryData, loadedRecipes);
                    profiler.pop();
                    BuildScape.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", loadedRecipes.size(), System.currentTimeMillis() - startTime);
                    return loadedRecipes;
                }
            } catch (Exception e) {
                BuildScape.LOGGER.warn("BDRE Loader: Exception reading bundled binary cache resource", e);
            }
        }

        // Step 2B: Check Local Disk Binary Cache
        if (BinaryRecipeCache.isCacheValid(contentHash)) {
            BuildScape.LOGGER.debug("BDRE Loader: Cache HIT! Fast-loading recipes from local binary cache...");
            List<Recipe<?>> cached = BinaryRecipeCache.loadCache(contentHash);
            if (!cached.isEmpty()) {
                loadedRecipes.addAll(cached);
                appendNonCacheableSpecialRecipes(rawCategoryData, loadedRecipes);
                profiler.pop();
                BuildScape.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", loadedRecipes.size(), System.currentTimeMillis() - startTime);
                return loadedRecipes;
            }
        }

        // Step 3: Stream and Compile (Parallelized across CPU cores)
        BuildScape.LOGGER.debug("BDRE Loader: Cache MISS/Invalid. Parallel streaming and compiling category source files...");
        List<Recipe<?>> synchronizedLoadedRecipes = Collections.synchronizedList(loadedRecipes);

        rawCategoryData.entrySet().parallelStream().forEach(entry -> {
            String category = entry.getKey();
            byte[] bytes = entry.getValue();

            try (Reader reader = new InputStreamReader(new java.io.ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack);
                synchronizedLoadedRecipes.addAll(result.recipes());
                compiler.clear();
            } catch (Exception e) {
                BuildScape.LOGGER.error("BDRE Loader: Failure parsing category [{}]", category, e);
            }
        });

        // Step 4: Save Binary Cache
        if (!loadedRecipes.isEmpty()) {
            BinaryRecipeCache.saveCache(contentHash, loadedRecipes);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        BuildScape.LOGGER.info("BDRE Loader: Successfully compiled {} recipes across {} categories in {} ms.", loadedRecipes.size(), rawCategoryData.size(), elapsed);

        profiler.pop();
        return loadedRecipes;
    }

    private void appendNonCacheableSpecialRecipes(Map<String, byte[]> rawCategoryData, List<Recipe<?>> recipes) {
        byte[] specialData = rawCategoryData.get("special");
        if (specialData == null) return;

        try (Reader reader = new InputStreamReader(new java.io.ByteArrayInputStream(specialData), StandardCharsets.UTF_8)) {
            BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
            RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory("special", reader);
            BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack);

            Set<ResourceLocation> loadedIds = new HashSet<>();
            for (Recipe<?> recipe : recipes) {
                loadedIds.add(recipe.getId());
            }

            int added = 0;
            for (Recipe<?> recipe : result.recipes()) {
                if (!BinaryRecipeCache.isCacheable(recipe) && loadedIds.add(recipe.getId())) {
                    recipes.add(recipe);
                    added++;
                }
            }
            compiler.clear();

            if (added > 0) {
                BuildScape.LOGGER.info("BDRE Loader: Added {} runtime-only special recipes after cache load.", added);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("BDRE Loader: Failure parsing runtime-only special recipes", e);
        }
    }

    private void applyRecipes(List<Recipe<?>> recipes, ProfilerFiller profiler) {
        profiler.push("BDRE_ApplyRecipes");
        if (currentRecipeManager != null && !recipes.isEmpty()) {
            RecipeManagerInjector.inject(currentRecipeManager, recipes);
        }
        profiler.pop();
    }
}
