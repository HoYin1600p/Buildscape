package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.recipe.ConfettiConfigureRecipe;
import com.kingodogo.buildscape.recipe.ShapedDurabilityRecipe;
import com.kingodogo.buildscape.recipe.ShapelessDurabilityRecipe;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.util.IngredientCache;
import com.kingodogo.buildscape.recipe.framework.validation.RecipeValidator;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class BuildScapeRecipeCompiler {

    private final AliasResolver aliasResolver = new AliasResolver();
    private final TemplateEngine templateEngine = new TemplateEngine();
    private final FamilyExpander familyExpander = new FamilyExpander(templateEngine);
    private final RecipeValidator validator = new RecipeValidator();
    private final RecipeIdAllocator recipeIds = new RecipeIdAllocator();
    private final StonecuttingCycleValidator cycles = new StonecuttingCycleValidator();

    public static record CompileResult(
            List<Recipe<?>> recipes,
            int totalProcessed,
            int totalGeneratedFromFamilies
    ) {}

    public CompileResult compileCategory(RecipeIR.CategoryPack pack) {
        List<Recipe<?>> compiledRecipes = new ArrayList<>();

        if (pack == null) {
            return new CompileResult(compiledRecipes, 0, 0);
        }

        cycles.addCategory(pack);
        cycles.validate();

        aliasResolver.registerAliases(pack.aliases());

        templateEngine.registerTemplates(pack.templates());

        int familyCount = 0;
        if (pack.families() != null) {
            for (RecipeIR.FamilySpec family : pack.families()) {
                try {
                    List<RecipeIR.RecipeSpec> familyRecipes = familyExpander.expandFamily(family);
                    familyCount += familyRecipes.size();
                    for (RecipeIR.RecipeSpec spec : familyRecipes) {
                        Recipe<?> recipe = compileSingleRecipe(pack.category(), spec);
                        if (recipe != null) {
                            compiledRecipes.add(recipe);
                        }
                    }
                } catch (Exception e) {
                    BuildScape.LOGGER.warn("BDRE Compiler: Exception expanding family base [{}]", family.base(), e);
                }
            }
        }

        int directCount = 0;
        if (pack.recipes() != null) {
            for (RecipeIR.RecipeSpec spec : pack.recipes()) {
                directCount++;
                try {
                    Recipe<?> recipe = compileSingleRecipe(pack.category(), spec);
                    if (recipe != null) {
                        compiledRecipes.add(recipe);
                    }
                } catch (Exception e) {
                    BuildScape.LOGGER.warn("BDRE Compiler: Exception compiling single recipe [{}]", spec.id(), e);
                }
            }
        }

        BuildScape.LOGGER.debug(
                "BDRE Compiled Category [{}] - Total: {} (Direct: {}, Family Generated: {})",
                pack.category(), compiledRecipes.size(), directCount, familyCount
        );

        return new CompileResult(compiledRecipes, directCount + familyCount, familyCount);
    }

    public Recipe<?> compileSingleRecipe(String category, RecipeIR.RecipeSpec spec) {
        if ("confetti_configure".equalsIgnoreCase(spec.type()) || "buildscape:confetti_configure".equalsIgnoreCase(spec.type())) {
            ResourceLocation confettiId = new ResourceLocation(BuildScape.MODID, "autogen/special/confetti_configure");
            return new ConfettiConfigureRecipe(confettiId);
        }
        if ("clear_shulker_filters".equalsIgnoreCase(spec.type()) || "buildscape:clear_shulker_filters".equalsIgnoreCase(spec.type())) {
            ResourceLocation shulkerId = new ResourceLocation(BuildScape.MODID, "autogen/special/clear_shulker_filters");
            return new com.kingodogo.buildscape.recipe.ClearShulkerFiltersRecipe(shulkerId);
        }

        if (spec.rawJson() != null && ("forge:conditional".equalsIgnoreCase(spec.type()) || spec.rawJson().contains("\"conditions\"") || spec.rawJson().contains("\"botanypots:crop\""))) {
            try {
                com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(spec.rawJson()).getAsJsonObject();
                ResourceLocation recipeId = spec.id() != null
                        ? new ResourceLocation(spec.id().contains(":") ? spec.id() : BuildScape.MODID + ":" + spec.id())
                        : new ResourceLocation(BuildScape.MODID, "autogen/" + category + "/" + Math.abs(spec.rawJson().hashCode()));

                if (!conditionsMet(json, recipeId)) {
                    return null;
                }

                if (json.has("recipes") && json.get("recipes").isJsonArray()) {
                    com.google.gson.JsonArray recipesArr = json.getAsJsonArray("recipes");
                    for (int i = 0; i < recipesArr.size(); i++) {
                        com.google.gson.JsonObject entry = recipesArr.get(i).getAsJsonObject();
                        if (!conditionsMet(entry, recipeId)) {
                            continue;
                        }
                        if (entry.has("recipe")) {
                            com.google.gson.JsonObject innerRecipeJson = entry.getAsJsonObject("recipe");
                            ResourceLocation innerId = new ResourceLocation(recipeId.getNamespace(), recipeId.getPath() + (recipesArr.size() > 1 ? "_" + i : ""));
                            try {
                                Recipe<?> compiled = RecipeManager.fromJson(innerId, innerRecipeJson);
                                if (compiled != null) {
                                    return compiled;
                                }
                            } catch (Throwable ignored) {}
                        }
                    }
                    return null;
                }
            } catch (Throwable t) {
                return null;
            }
        }

        try {
            ShapedRecipeSymbols.validate(spec);
        } catch (IllegalArgumentException e) {
            BuildScape.LOGGER.warn("BDRE Compiler: Skipping recipe {}: {}", spec.id(), e.getMessage());
            return null;
        }
        ResourceLocation recipeId = new ResourceLocation(recipeIds.allocate(category, spec, aliasResolver));
        if (!validator.validate(spec, aliasResolver)) {
            return null;
        }

        if (validator.checkDuplicate(recipeId)) {
            return null;
        }

        Item resultItem = resolveItem(spec.result().item());
        if (resultItem == null) {
            return null;
        }
        ItemStack resultStack = new ItemStack(resultItem, spec.result().count());
        if (spec.result().nbt() != null && !spec.result().nbt().isBlank()) {
            try {
                resultStack.setTag(net.minecraft.nbt.TagParser.parseTag(spec.result().nbt()));
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
                BuildScape.LOGGER.warn("BDRE Compiler: Invalid result NBT for recipe {}", spec.id(), e);
                return null;
            }
        }
        String group = spec.group() != null ? spec.group() : "";

        String recipeType = spec.type() != null ? spec.type().toLowerCase(Locale.ROOT) : category.toLowerCase(Locale.ROOT);

        switch (recipeType) {
            case "shaped" -> {
                return compileShaped(recipeId, group, spec, resultStack);
            }
            case "shapeless" -> {
                return compileShapeless(recipeId, group, spec, resultStack);
            }
            case "shaped_durability", "buildscape:shaped_durability" -> {
                ShapedRecipe base = compileShaped(recipeId, group, spec, resultStack);
                if (base == null) return null;
                return new ShapedDurabilityRecipe(base.getId(), base.getGroup(), base.getWidth(), base.getHeight(), base.getIngredients(), base.getResultItem(), 1);
            }
            case "shapeless_durability", "buildscape:shapeless_durability" -> {
                ShapelessRecipe base = compileShapeless(recipeId, group, spec, resultStack);
                if (base == null) return null;
                return new ShapelessDurabilityRecipe(base.getId(), base.getGroup(), base.getResultItem(), base.getIngredients(), 1);
            }
            case "buildscape:haze_bush_dye" -> {
                ShapelessRecipe base = compileShapeless(recipeId, group, spec, resultStack);
                if (base == null) return null;
                return new com.kingodogo.buildscape.recipe.HazeBushDyeRecipe(
                        base.getId(), base.getGroup(), base.getResultItem(), base.getIngredients());
            }
            case "stonecutting" -> {
                Ingredient input = resolveIngredient(spec.input());
                return new StonecutterRecipe(recipeId, group, input, resultStack);
            }
            case "smelting" -> {
                Ingredient input = resolveIngredient(spec.input());
                return new SmeltingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "blasting" -> {
                Ingredient input = resolveIngredient(spec.input());
                return new BlastingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "smoking" -> {
                Ingredient input = resolveIngredient(spec.input());
                return new SmokingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "campfire", "campfire_cooking" -> {
                Ingredient input = resolveIngredient(spec.input());
                return new CampfireCookingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "smithing" -> {
                Ingredient base = resolveIngredient(spec.input());
                Ingredient addition = spec.ingredients() != null && !spec.ingredients().isEmpty()
                    ? resolveIngredient(spec.ingredients().get(0)) : Ingredient.EMPTY;
                return new UpgradeRecipe(recipeId, base, addition, resultStack);
            }
            default -> {
                BuildScape.LOGGER.warn("BDRE Compiler: Unknown recipe type '{}' for id {}", recipeType, recipeId);
                return null;
            }
        }
    }

    /**
     * Evaluates the Forge recipe conditions of a JSON object. An object without conditions is met; an
     * unknown or malformed condition counts as not met.
     */
    private static boolean conditionsMet(com.google.gson.JsonObject json, ResourceLocation recipeId) {
        if (json == null || !json.has("conditions")) {
            return true;
        }
        try {
            return net.minecraftforge.common.crafting.CraftingHelper.processConditions(json, "conditions");
        } catch (Throwable t) {
            BuildScape.LOGGER.warn("BDRE Compiler: Could not evaluate conditions of recipe {}; skipping it.", recipeId, t);
            return false;
        }
    }

    private ShapedRecipe compileShaped(ResourceLocation id, String group, RecipeIR.RecipeSpec spec, ItemStack result) {
        List<String> patternList = spec.pattern();
        if (patternList == null || patternList.isEmpty()) return null;
        int height = patternList.size();
        int width = 0;
        for (String line : patternList) {
            width = Math.max(width, line.length());
        }

        Map<Character, Ingredient> keyMap = new HashMap<>();
        if (spec.keys() != null) {
            for (Map.Entry<String, String> entry : spec.keys().entrySet()) {
                if (entry.getKey().length() > 0) {
                    char c = entry.getKey().charAt(0);
                    Ingredient ing = resolveIngredient(entry.getValue());
                    keyMap.put(c, ing);
                }
            }
        }

        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
        for (int row = 0; row < height; row++) {
            String line = patternList.get(row);
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                if (c != ' ') {
                    Ingredient ing = keyMap.get(c);
                    if (ing == null) {
                        BuildScape.LOGGER.warn("BDRE Compiler: Skipping recipe {}: undefined symbol '{}'", id, c);
                        return null;
                    }
                    ingredients.set(row * width + col, ing);
                }
            }
        }

        com.kingodogo.buildscape.recipe.framework.util.ShapedPatternTrimmer.Trimmed trimmed = com.kingodogo.buildscape.recipe.framework.util.ShapedPatternTrimmer.trim(width, height, ingredients);
        return new ShapedRecipe(id, group, trimmed.width(), trimmed.height(), trimmed.ingredients(), result);
    }

    private ShapelessRecipe compileShapeless(ResourceLocation id, String group, RecipeIR.RecipeSpec spec, ItemStack result) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        if (spec.ingredients() != null) {
            for (String ingStr : spec.ingredients()) {
                Ingredient ing = resolveIngredient(ingStr);
                if (!ing.isEmpty()) {
                    ingredients.add(ing);
                }
            }
        }
        return new ShapelessRecipe(id, group, result, ingredients);
    }

    public Ingredient resolveIngredient(String rawSpec) {
        if (rawSpec == null || rawSpec.isEmpty()) {
            return Ingredient.EMPTY;
        }

        if (rawSpec.startsWith("[") && rawSpec.endsWith("]")) {
            String inner = rawSpec.substring(1, rawSpec.length() - 1);
            String[] parts = inner.split(",");
            List<Ingredient> ingredients = new ArrayList<>();
            for (String part : parts) {
                Ingredient ing = resolveIngredient(part.trim());
                if (!ing.isEmpty()) {
                    ingredients.add(ing);
                }
            }
            return ingredients.isEmpty() ? Ingredient.EMPTY : Ingredient.merge(ingredients);
        }

        String resolved = aliasResolver.resolveString(rawSpec);

        if (resolved.startsWith("#")) {
            String tagId = resolved.substring(1);
            TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(tagId));
            return IngredientCache.get(tagKey);
        }

        ResourceLocation loc = new ResourceLocation(resolved);
        Item item = ForgeRegistries.ITEMS.getValue(loc);
        if (item != null && ForgeRegistries.ITEMS.containsKey(loc)) {
            return IngredientCache.get(item);
        }

        return Ingredient.EMPTY;
    }

    public Item resolveItem(String rawSpec) {
        if (rawSpec == null || rawSpec.isEmpty()) {
            return null;
        }
        String resolved = aliasResolver.resolveString(rawSpec);
        ResourceLocation loc = new ResourceLocation(resolved);
        return ForgeRegistries.ITEMS.getValue(loc);
    }

    public void clear() {
        validator.clear();
        recipeIds.clear();
        cycles.clear();
    }
}
