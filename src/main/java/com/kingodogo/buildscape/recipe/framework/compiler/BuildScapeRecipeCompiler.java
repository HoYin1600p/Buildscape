package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.recipe.ConfettiConfigureRecipe;
import com.kingodogo.buildscape.recipe.ShapedDurabilityRecipe;
import com.kingodogo.buildscape.recipe.ShapelessDurabilityRecipe;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.validation.RecipeValidator;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

public class BuildScapeRecipeCompiler {

    private final AliasResolver aliasResolver = new AliasResolver();
    private final TemplateEngine templateEngine = new TemplateEngine();
    private final FamilyExpander familyExpander = new FamilyExpander(templateEngine);
    private final RecipeValidator validator = new RecipeValidator();

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

                if (json.has("recipes") && json.get("recipes").isJsonArray()) {
                    com.google.gson.JsonArray recipesArr = json.getAsJsonArray("recipes");
                    for (int i = 0; i < recipesArr.size(); i++) {
                        com.google.gson.JsonObject entry = recipesArr.get(i).getAsJsonObject();
                        if (entry.has("conditions")) {
                            boolean conditionsMet = true;
                            com.google.gson.JsonArray conds = entry.getAsJsonArray("conditions");
                            for (int c = 0; c < conds.size(); c++) {
                                com.google.gson.JsonObject cond = conds.get(c).getAsJsonObject();
                                String condType = cond.has("type") ? cond.get("type").getAsString() : "";
                                if ("forge:mod_loaded".equals(condType)) {
                                    String modid = cond.has("modid") ? cond.get("modid").getAsString() : "";
                                    try {
                                        if (!net.minecraftforge.fml.ModList.get().isLoaded(modid)) {
                                            conditionsMet = false;
                                            break;
                                        }
                                    } catch (Throwable ignored) {
                                        conditionsMet = false;
                                        break;
                                    }
                                }
                            }
                            if (!conditionsMet) {
                                continue;
                            }
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

        if (!validator.validate(spec, aliasResolver)) {
            return null;
        }

        String rawId = spec.id() != null ? spec.id() : generateRecipeId(spec);
        String cleanPath = sanitizePath("autogen/" + category + "/" + rawId);

        ResourceLocation recipeId = ResourceLocation.tryParse(BuildScape.MODID + ":" + cleanPath);
        if (recipeId == null) {
            BuildScape.LOGGER.warn("BDRE Compiler: Invalid recipe resource location path: {}", cleanPath);
            return null;
        }

        if (validator.checkDuplicate(recipeId)) {
            return null;
        }

        Item resultItem = aliasResolver.resolveItem(spec.result().item());
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
            case "stonecutting" -> {
                Ingredient input = aliasResolver.resolveIngredient(spec.input());
                return new StonecutterRecipe(recipeId, group, input, resultStack);
            }
            case "smelting" -> {
                Ingredient input = aliasResolver.resolveIngredient(spec.input());
                return new SmeltingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "blasting" -> {
                Ingredient input = aliasResolver.resolveIngredient(spec.input());
                return new BlastingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "smoking" -> {
                Ingredient input = aliasResolver.resolveIngredient(spec.input());
                return new SmokingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "campfire", "campfire_cooking" -> {
                Ingredient input = aliasResolver.resolveIngredient(spec.input());
                return new CampfireCookingRecipe(recipeId, group, input, resultStack, spec.experience(), spec.cookingTime());
            }
            case "smithing" -> {
                Ingredient base = aliasResolver.resolveIngredient(spec.input());
                Ingredient addition = spec.ingredients() != null && !spec.ingredients().isEmpty()
                        ? aliasResolver.resolveIngredient(spec.ingredients().get(0)) : Ingredient.EMPTY;
                return new UpgradeRecipe(recipeId, base, addition, resultStack);
            }
            default -> {
                BuildScape.LOGGER.warn("BDRE Compiler: Unknown recipe type '{}' for id {}", recipeType, recipeId);
                return null;
            }
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
                    Ingredient ing = aliasResolver.resolveIngredient(entry.getValue());
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
                    Ingredient ing = keyMap.getOrDefault(c, Ingredient.EMPTY);
                    ingredients.set(row * width + col, ing);
                }
            }
        }

        return new ShapedRecipe(id, group, width, height, ingredients, result);
    }

    private ShapelessRecipe compileShapeless(ResourceLocation id, String group, RecipeIR.RecipeSpec spec, ItemStack result) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        if (spec.ingredients() != null) {
            for (String ingStr : spec.ingredients()) {
                Ingredient ing = aliasResolver.resolveIngredient(ingStr);
                if (!ing.isEmpty()) {
                    ingredients.add(ing);
                }
            }
        }
        return new ShapelessRecipe(id, group, result, ingredients);
    }

    private String generateRecipeId(RecipeIR.RecipeSpec spec) {
        String res = spec.result() != null ? spec.result().item() : "unknown";
        return sanitizePath(res) + "_" + Math.abs(spec.hashCode());
    }

    private String sanitizePath(String str) {
        if (str == null) return "unknown";
        String resolved = aliasResolver.resolveString(str);
        return resolved.toLowerCase(Locale.ROOT)
                .replace(":", "_")
                .replaceAll("[^a-z0-9/._-]", "_");
    }

    public void clear() {
        validator.clear();
    }
}
