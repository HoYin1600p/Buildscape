package com.kingodogo.buildscape.recipe.framework;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.cache.BinaryRecipeCache;
import com.kingodogo.buildscape.recipe.framework.compiler.BuildScapeRecipeCompiler;
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

public class BuildScapeRecipeLoader {

    public static final BuildScapeRecipeLoader INSTANCE = new BuildScapeRecipeLoader();

    private static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    private volatile RecipeManager currentRecipeManager;
    private volatile List<RecipeIR.CompiledRecipe> preparedRecipes = List.of();

    public void setCurrentRecipeManager(RecipeManager recipeManager) {
        this.currentRecipeManager = recipeManager;
        List<RecipeIR.CompiledRecipe> ready = preparedRecipes;
        if (recipeManager != null && !ready.isEmpty()) {
            RecipeManagerInjector.inject(recipeManager, ready);
        }
    }

    public RecipeManager getCurrentRecipeManager() {
        return this.currentRecipeManager;
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
                        List<RecipeIR.CompiledRecipe> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, bundled);
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
                List<RecipeIR.CompiledRecipe> recipes = appendRuntimeRecipes(rawCategoryData, runtimeCategories, cached);
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
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack);
                compiledByCategory.put(category, result.recipes());
                compiler.clear();
            } catch (Exception e) {
                BuildscapeCommon.LOGGER.error("BDRE Loader: Failure parsing category [{}]", category, e);
            }
        });

        List<RecipeIR.CompiledRecipe> recipes = new ArrayList<>();
        List<RecipeIR.CompiledRecipe> cacheableSourceRecipes = new ArrayList<>();
        for (String category : CATEGORIES) {
            List<RecipeIR.CompiledRecipe> categoryRecipes = compiledByCategory.getOrDefault(category, List.of());
            recipes.addAll(categoryRecipes);
            if (!runtimeCategories.contains(category)) {
                cacheableSourceRecipes.addAll(categoryRecipes);
            }
        }

        recipes = RecipeValidator.filterCompiled(recipes);
        cacheableSourceRecipes = RecipeValidator.filterCompiled(cacheableSourceRecipes);
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
            List<RecipeIR.CompiledRecipe> cachedRecipes) {
        Map<String, RecipeIR.CompiledRecipe> recipesById = new LinkedHashMap<>();
        for (RecipeIR.CompiledRecipe recipe : cachedRecipes) {
            addUnique(recipesById, recipe);
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
                    new ByteArrayInputStream(categoryData), StandardCharsets.UTF_8)) {
                RecipePackFormatter.validateJson(new String(categoryData, StandardCharsets.UTF_8));
                BuildScapeRecipeCompiler compiler = new BuildScapeRecipeCompiler();
                RecipeIR.CategoryPack categoryPack = StreamingRecipeParser.parseCategory(category, reader);
                BuildScapeRecipeCompiler.CompileResult result = compiler.compileCategory(categoryPack);
                for (RecipeIR.CompiledRecipe recipe : result.recipes()) {
                    if (addUnique(recipesById, recipe)) added++;
                }
                compiler.clear();
            } catch (Exception e) {
                BuildscapeCommon.LOGGER.error("BDRE Loader: Failure parsing runtime-only category [{}]", category, e);
            }
        }
        if (added > 0) {
            BuildscapeCommon.LOGGER.info("BDRE Loader: Added {} runtime-only recipes after cache load.", added);
        }
        return RecipeValidator.filterCompiled(new ArrayList<>(recipesById.values()));
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
        preparedRecipes = recipes == null ? List.of() : List.copyOf(RecipeValidator.filterCompiled(recipes));
        RecipeManager recipeManager = currentRecipeManager;
        if (recipeManager != null && !preparedRecipes.isEmpty()) {
            RecipeManagerInjector.inject(recipeManager, preparedRecipes);
        }
    }
}
