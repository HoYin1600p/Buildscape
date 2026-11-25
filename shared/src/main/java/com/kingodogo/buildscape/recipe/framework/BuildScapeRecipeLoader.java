package com.kingodogo.buildscape.recipe.framework;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.cache.BinaryRecipeCache;
import com.kingodogo.buildscape.recipe.framework.compiler.BuildScapeRecipeCompiler;
import com.kingodogo.buildscape.recipe.framework.compiler.StonecuttingCycleValidator;
import com.kingodogo.buildscape.recipe.framework.integration.RecipeManagerInjector;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import com.kingodogo.buildscape.recipe.framework.util.IngredientCache;
import com.kingodogo.buildscape.recipe.framework.util.RecipePackFormatter;
import com.kingodogo.buildscape.recipe.framework.validation.RecipeValidator;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.RecipeManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.WeakReference;

public class BuildScapeRecipeLoader {

    public static final BuildScapeRecipeLoader INSTANCE = new BuildScapeRecipeLoader();

    private static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    private volatile RecipeManager currentRecipeManager;
    private volatile List<RecipeIR.CompiledRecipe> preparedRecipes = List.of();
    private final RecipeManager boundRecipeManager;
    private final WeakReference<ResourceManager> resourceScope;
    private volatile WeakReference<RecipeManager> deferredRecipeManager = new WeakReference<>(null);
    private boolean recipesReady;
    private boolean recipesApplied;
    private static final Map<ResourceManager, PendingReload> RESOURCE_RELOADS = new WeakHashMap<>();

    private static final class PendingReload {
        WeakReference<RecipeManager> manager = new WeakReference<>(null);
        // Keep prepared recipes alive until the late manager callback; the resource key remains weak.
        BuildScapeRecipeLoader loader;
    }

    public BuildScapeRecipeLoader() { this.boundRecipeManager = null; this.resourceScope = null; }

    private BuildScapeRecipeLoader(RecipeManager recipeManager) {
        this.boundRecipeManager = Objects.requireNonNull(recipeManager);
        this.resourceScope = null;
    }

    private BuildScapeRecipeLoader(ResourceManager resources) {
        this.boundRecipeManager = null;
        this.resourceScope = new WeakReference<>(Objects.requireNonNull(resources));
    }

    public static BuildScapeRecipeLoader forReload(RecipeManager recipeManager) {
        return new BuildScapeRecipeLoader(recipeManager);
    }

    public static BuildScapeRecipeLoader forResourceReload(ResourceManager resources) {
        BuildScapeRecipeLoader loader = new BuildScapeRecipeLoader(resources);
        RecipeManager manager;
        synchronized (RESOURCE_RELOADS) {
            PendingReload pending = RESOURCE_RELOADS.computeIfAbsent(resources, ignored -> new PendingReload());
            if (pending.loader != null) throw new IllegalStateException("Resource reload already registered");
            pending.loader = loader;
            manager = pending.manager.get();
        }
        if (manager != null) loader.bindDeferredManager(manager);
        return loader;
    }

    public static void bindResourceManager(ResourceManager resources, RecipeManager manager) {
        Objects.requireNonNull(resources);
        Objects.requireNonNull(manager);
        BuildScapeRecipeLoader loader;
        synchronized (RESOURCE_RELOADS) {
            PendingReload pending = RESOURCE_RELOADS.computeIfAbsent(resources, ignored -> new PendingReload());
            pending.manager = new WeakReference<>(manager);
            loader = pending.loader;
        }
        if (loader != null) loader.bindDeferredManager(manager);
    }

    private void bindDeferredManager(RecipeManager manager) {
        synchronized (this) {
            RecipeManager existing = deferredRecipeManager.get();
            if (existing != null && existing != manager)
                throw new IllegalStateException("Resource reload manager is already bound");
            deferredRecipeManager = new WeakReference<>(manager);
        }
        finishDeferredReload();
    }

    private void finishDeferredReload() {
        List<RecipeIR.CompiledRecipe> ready;
        RecipeManager manager;
        synchronized (this) {
            manager = deferredRecipeManager.get();
            if (!recipesReady || recipesApplied || manager == null) return;
            recipesApplied = true;
            ready = preparedRecipes;
        }
        RecipeManagerInjector.inject(manager, ready);
        ResourceManager resources = resourceScope.get();
        if (resources != null) {
            synchronized (RESOURCE_RELOADS) {
                PendingReload pending = RESOURCE_RELOADS.get(resources);
                if (pending != null && pending.loader == this) RESOURCE_RELOADS.remove(resources);
            }
        }
    }

    public void setCurrentRecipeManager(RecipeManager recipeManager) {
        if (boundRecipeManager != null || resourceScope != null) throw new IllegalStateException("Reload manager is already bound");
        this.currentRecipeManager = recipeManager;
        List<RecipeIR.CompiledRecipe> ready = preparedRecipes;
        if (recipeManager != null && !ready.isEmpty()) {
            RecipeManagerInjector.inject(recipeManager, ready);
        }
    }

    public RecipeManager getCurrentRecipeManager() {
        return boundRecipeManager != null ? boundRecipeManager
                : resourceScope != null ? deferredRecipeManager.get() : currentRecipeManager;
    }

    public List<RecipeIR.CompiledRecipe> prepareRecipes(ResourceManager resourceManager) {
        IngredientCache.clear();

        long startTime = System.currentTimeMillis();

        Map<String, byte[]> rawCategoryData = new LinkedHashMap<>();

        for (String category : CATEGORIES) {
            CommonId location = CommonId.of(BuildscapeCommon.MOD_ID, "recipes_pack/" + category + ".json");
            try {
                byte[] bytes = Services.PLATFORM.readResourceBytes(resourceManager, location);
                if (bytes != null) {
                    rawCategoryData.put(category, bytes);
                }
            } catch (Exception e) {
                BuildscapeCommon.LOGGER.error("BDRE Loader: Error reading category file [{}]", location, e);
            }
        }

        String contentHash = BinaryRecipeCache.computeSourceHash(CATEGORIES, rawCategoryData);
        Set<String> runtimeCategories = findRuntimeCategories(rawCategoryData);

        CommonId bundledLocation = CommonId.of(BuildscapeCommon.MOD_ID, "recipes_pack/recipes.bscb");
        try {
            byte[] bundledBytes = Services.PLATFORM.readResourceBytes(resourceManager, bundledLocation);
            if (bundledBytes != null) {
                try (InputStream stream = new ByteArrayInputStream(bundledBytes)) {
                    List<RecipeIR.CompiledRecipe> bundled = BinaryRecipeCache.loadCacheFromStream(stream, contentHash);
                    if (!bundled.isEmpty()) {
                        List<RecipeIR.CompiledRecipe> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, bundled, true);
                        BuildscapeCommon.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", recipes.size(), System.currentTimeMillis() - startTime);
                        return recipes;
                    }
                }
            }
        } catch (Exception e) {
            BuildscapeCommon.LOGGER.warn("BDRE Loader: Exception reading bundled binary cache resource", e);
        }

        if (BinaryRecipeCache.isCacheValid(contentHash)) {
            BuildscapeCommon.LOGGER.debug("BDRE Loader: Cache HIT! Fast-loading recipes from local binary cache...");
            List<RecipeIR.CompiledRecipe> cached = BinaryRecipeCache.loadCache(contentHash);
            if (!cached.isEmpty()) {
                List<RecipeIR.CompiledRecipe> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, cached, false);
                BuildscapeCommon.LOGGER.info("BDRE Loader: Loaded {} recipes from binary cache in {} ms.", recipes.size(), System.currentTimeMillis() - startTime);
                return recipes;
            }
        }

        BuildscapeCommon.LOGGER.debug("BDRE Loader: Cache MISS/Invalid. Parallel streaming and compiling category source files...");
        Map<String, List<RecipeIR.CompiledRecipe>> compiledByCategory = new ConcurrentHashMap<>();

        rawCategoryData.entrySet().parallelStream().forEach(entry -> {
            String category = entry.getKey();
            byte[] bytes = entry.getValue();

            try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
                RecipePackFormatter.validateJson(new String(bytes, StandardCharsets.UTF_8));
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack, false);
                compiledByCategory.put(category, result.recipes());
                compiler.clear();
            } catch (Exception e) {
                BuildscapeCommon.LOGGER.error("BDRE Loader: Failure parsing category [{}]", category, e);
            }
        });

        List<RecipeIR.CompiledRecipe> orderedRecipes = new ArrayList<>();
        Set<RecipeIR.CompiledRecipe> cacheableCandidates = new HashSet<>();
        for (String category : CATEGORIES) {
            List<RecipeIR.CompiledRecipe> categoryRecipes = compiledByCategory.getOrDefault(category, List.of());
            orderedRecipes.addAll(categoryRecipes);
            if (!runtimeCategories.contains(category)) cacheableCandidates.addAll(categoryRecipes);
        }

        List<RecipeIR.CompiledRecipe> recipes = mergeRuntimeRecipes(List.of(), orderedRecipes, true);
        List<RecipeIR.CompiledRecipe> cacheableSourceRecipes = recipes.stream().filter(cacheableCandidates::contains).toList();
        if (!cacheableSourceRecipes.isEmpty()) {
            BinaryRecipeCache.saveCache(contentHash, cacheableSourceRecipes);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        BuildscapeCommon.LOGGER.info("BDRE Loader: Successfully compiled {} recipes across {} categories in {} ms.", recipes.size(), rawCategoryData.size(), elapsed);

        return recipes;
    }

    private List<RecipeIR.CompiledRecipe> appendRuntimeRecipes(
            Map<String, byte[]> rawCategoryData,
            Set<String> runtimeCategories,
            List<RecipeIR.CompiledRecipe> cachedRecipes,
            boolean trustedBundledCache) {
        List<RecipeIR.CompiledRecipe> runtimeRecipes = new ArrayList<>();
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
                RecipePackFormatter.validateJson(new String(categoryData, StandardCharsets.UTF_8));
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack, false);
                runtimeRecipes.addAll(result.recipes());
                compiler.clear();
            } catch (Exception e) {
                BuildscapeCommon.LOGGER.error("BDRE Loader: Failure parsing runtime-only category [{}]", category, e);
            }
        }
        return mergeRuntimeRecipes(cachedRecipes, runtimeRecipes, !trustedBundledCache || !runtimeRecipes.isEmpty());
    }

    private static List<RecipeIR.CompiledRecipe> mergeRuntimeRecipes(List<RecipeIR.CompiledRecipe> base,
                                                                    List<RecipeIR.CompiledRecipe> runtime,
                                                                    boolean validateBase) {
        Map<String, RecipeIR.CompiledRecipe> recipesById = new LinkedHashMap<>();
        List<RecipeIR.CompiledRecipe> validBase = RecipeValidator.filterCompiled(base);
        if (validateBase) validBase = filterStonecuttingCycles(validBase);
        StonecuttingCycleValidator cycles = new StonecuttingCycleValidator();
        for (RecipeIR.CompiledRecipe recipe : validBase) {
            addUnique(recipesById, recipe);
            if (!runtime.isEmpty()) addStonecuttingRecipe(cycles, recipe);
        }
        RecipeValidator validator = new RecipeValidator();
        for (RecipeIR.CompiledRecipe recipe : runtime) {
            if (recipe == null || !validator.validateCompiled(recipe)) continue;
            RecipeIR.CompiledRecipe previous = recipesById.get(recipe.id());
            if (previous != null) cycles.removeRecipe(previous.id());
            try {
                addStonecuttingRecipe(cycles, recipe);
            } catch (IllegalArgumentException exception) {
                if (previous != null) addStonecuttingRecipe(cycles, previous);
                BuildscapeCommon.LOGGER.warn("BDRE Loader: Rejected recipe [{}]: {}", recipe.id(), exception.getMessage());
                continue;
            }
            recipesById.put(recipe.id(), recipe);
            if (previous != null) BuildscapeCommon.LOGGER.debug("BDRE Loader: Recipe [{}] overrides an earlier recipe", recipe.id());
        }
        return new ArrayList<>(recipesById.values());
    }

    private static List<RecipeIR.CompiledRecipe> filterStonecuttingCycles(List<RecipeIR.CompiledRecipe> recipes) {
        StonecuttingCycleValidator cycles = new StonecuttingCycleValidator();
        List<RecipeIR.CompiledRecipe> accepted = new ArrayList<>(recipes.size());
        for (RecipeIR.CompiledRecipe recipe : recipes) {
            try {
                addStonecuttingRecipe(cycles, recipe);
            } catch (IllegalArgumentException exception) {
                BuildscapeCommon.LOGGER.warn("BDRE Loader: Rejected recipe [{}]: {}", recipe.id(), exception.getMessage());
                continue;
            }
            accepted.add(recipe);
        }
        return accepted;
    }

    private static void addStonecuttingRecipe(StonecuttingCycleValidator cycles, RecipeIR.CompiledRecipe recipe) {
        if (!"stonecutting".equals(recipe.type())) return;
        List<String> inputs = new ArrayList<>();
        collectStonecuttingInputs(recipe.input(), inputs);
        cycles.addRecipeChecked(recipe.id(), inputs, recipe.resultItem(), recipe.resultCount());
    }

    private static void collectStonecuttingInputs(String input, List<String> result) {
        if (input == null || input.isEmpty()) return;
        input = input.trim();
        if (input.startsWith("{") || input.startsWith("[")) {
            collectStonecuttingInputs(com.google.gson.JsonParser.parseString(input), result);
        } else {
            result.add(input);
        }
    }

    private static void collectStonecuttingInputs(com.google.gson.JsonElement input, List<String> result) {
        // Tags remain separate graph nodes because their members depend on the active datapack.
        if (input.isJsonArray()) {
            for (com.google.gson.JsonElement alternative : input.getAsJsonArray()) collectStonecuttingInputs(alternative, result);
        } else if (input.isJsonObject()) {
            var object = input.getAsJsonObject();
            if (object.has("item")) result.add(object.get("item").getAsString());
            if (object.has("tag")) result.add("#" + object.get("tag").getAsString());
        } else if (input.isJsonPrimitive() && input.getAsJsonPrimitive().isString()) {
            result.add(input.getAsString());
        }
    }

    private static boolean addUnique(Map<String, RecipeIR.CompiledRecipe> recipes, RecipeIR.CompiledRecipe recipe) {
        if (recipes.putIfAbsent(recipe.id(), recipe) != null) {
            BuildscapeCommon.LOGGER.warn("BDRE: Duplicate recipe ID {} (keeping the first recipe)", recipe.id());
            return false;
        }
        return true;
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

    public void applyRecipes(List<RecipeIR.CompiledRecipe> recipes) {
        List<RecipeIR.CompiledRecipe> ready = recipes == null ? List.of() : List.copyOf(RecipeValidator.filterCompiled(recipes));
        if (resourceScope != null) {
            synchronized (this) {
                preparedRecipes = ready;
                recipesReady = true;
            }
            finishDeferredReload();
            return;
        }
        if (boundRecipeManager == null) preparedRecipes = ready;
        RecipeManager recipeManager = getCurrentRecipeManager();
        if (recipeManager != null && !ready.isEmpty()) {
            RecipeManagerInjector.inject(recipeManager, ready);
        }
    }
}
