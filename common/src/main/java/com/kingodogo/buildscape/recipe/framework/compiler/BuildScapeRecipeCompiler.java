package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.util.ShapedPatternTrimmer;
import com.kingodogo.buildscape.recipe.framework.validation.RecipeValidator;
import com.kingodogo.buildscape.util.CommonId;

import java.util.*;

public class BuildScapeRecipeCompiler {

    private final AliasResolver aliasResolver = new AliasResolver();
    private final TemplateEngine templateEngine = new TemplateEngine();
    private final FamilyExpander familyExpander = new FamilyExpander(templateEngine);
    private final RecipeValidator validator = new RecipeValidator();

    public static record CompileResult(
            List<RecipeIR.CompiledRecipe> recipes,
            int totalProcessed,
            int totalGeneratedFromFamilies
    ) {}

    public CompileResult compileCategory(RecipeIR.CategoryPack pack) {
        List<RecipeIR.CompiledRecipe> compiledRecipes = new ArrayList<>();

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
                        RecipeIR.CompiledRecipe recipe = compileSingleRecipe(pack.category(), spec);
                        if (recipe != null) {
                            compiledRecipes.add(recipe);
                        }
                    }
                } catch (Exception e) {
                    BuildscapeCommon.LOGGER.warn("BDRE Compiler: Exception expanding family base [{}]", family.base(), e);
                }
            }
        }

        int directCount = 0;
        if (pack.recipes() != null) {
            for (RecipeIR.RecipeSpec spec : pack.recipes()) {
                directCount++;
                try {
                    RecipeIR.CompiledRecipe recipe = compileSingleRecipe(pack.category(), spec);
                    if (recipe != null) {
                        compiledRecipes.add(recipe);
                    }
                } catch (Exception e) {
                    BuildscapeCommon.LOGGER.warn("BDRE Compiler: Exception compiling single recipe [{}]", spec.id(), e);
                }
            }
        }

        BuildscapeCommon.LOGGER.debug(
                "BDRE Compiled Category [{}] - Total: {} (Direct: {}, Family Generated: {})",
                pack.category(), compiledRecipes.size(), directCount, familyCount
        );

        return new CompileResult(compiledRecipes, directCount + familyCount, familyCount);
    }

    public RecipeIR.CompiledRecipe compileSingleRecipe(String category, RecipeIR.RecipeSpec spec) {
        if ("confetti_configure".equalsIgnoreCase(spec.type()) || "buildscape:confetti_configure".equalsIgnoreCase(spec.type())) {
            return new RecipeIR.CompiledRecipe(
                    BuildscapeCommon.MOD_ID + ":autogen/special/confetti_configure",
                    "confetti_configure", "", 0, 0, List.of(), "", "", "minecraft:air", 1, "", 0f, 0, 0
            );
        }
        if ("clear_shulker_filters".equalsIgnoreCase(spec.type()) || "buildscape:clear_shulker_filters".equalsIgnoreCase(spec.type())) {
            return new RecipeIR.CompiledRecipe(
                    BuildscapeCommon.MOD_ID + ":autogen/special/clear_shulker_filters",
                    "clear_shulker_filters", "", 0, 0, List.of(), "", "", "minecraft:air", 1, "", 0f, 0, 0
            );
        }

        if (spec.rawJson() != null && ("forge:conditional".equalsIgnoreCase(spec.type()) || spec.rawJson().contains("\"conditions\"") || spec.rawJson().contains("\"botanypots:crop\""))) {
            try {
                com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(spec.rawJson()).getAsJsonObject();
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
                                if ("forge:mod_loaded".equals(condType) || "fabric:all_mods_loaded".equals(condType)) {
                                    String modid = cond.has("modid") ? cond.get("modid").getAsString() : "";
                                    try {
                                        if (!Services.PLATFORM.isModLoaded(modid)) {
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
                    }
                }
            } catch (Throwable ignored) {}
            return null;
        }

        if (!validator.validate(spec, aliasResolver)) {
            return null;
        }

        String rawId = spec.id() != null ? spec.id() : generateRecipeId(spec);
        String cleanPath = sanitizePath("autogen/" + category + "/" + rawId);

        CommonId recipeId = CommonId.tryParse(BuildscapeCommon.MOD_ID + ":" + cleanPath);
        if (recipeId == null) {
            BuildscapeCommon.LOGGER.warn("BDRE Compiler: Invalid recipe resource location path: {}", cleanPath);
            return null;
        }

        if (validator.checkDuplicate(recipeId)) {
            return null;
        }

        String resultItem = aliasResolver.resolveString(spec.result() != null ? spec.result().item() : "minecraft:air");
        int resultCount = spec.result() != null ? spec.result().count() : 1;
        String resultNbt = (spec.result() != null && spec.result().nbt() != null) ? spec.result().nbt() : "";
        String group = spec.group() != null ? spec.group() : "";
        String recipeType = spec.type() != null ? spec.type().toLowerCase(Locale.ROOT) : category.toLowerCase(Locale.ROOT);
        if (recipeType.startsWith("buildscape:")) recipeType = recipeType.substring("buildscape:".length());

        switch (recipeType) {
            case "shaped" -> {
                return compileShaped(recipeId.toString(), group, spec, resultItem, resultCount, resultNbt, false);
            }
            case "shapeless" -> {
                return compileShapeless(recipeId.toString(), group, spec, resultItem, resultCount, resultNbt, false);
            }
            case "shaped_durability" -> {
                return compileShaped(recipeId.toString(), group, spec, resultItem, resultCount, resultNbt, true);
            }
            case "shapeless_durability" -> {
                return compileShapeless(recipeId.toString(), group, spec, resultItem, resultCount, resultNbt, true);
            }
            case "stonecutting" -> {
                String input = aliasResolver.resolveString(spec.input());
                return new RecipeIR.CompiledRecipe(recipeId.toString(), "stonecutting", group, 0, 0, List.of(), input, "", resultItem, resultCount, resultNbt, 0f, 0, 0);
            }
            case "smelting" -> {
                String input = aliasResolver.resolveString(spec.input());
                return new RecipeIR.CompiledRecipe(recipeId.toString(), "smelting", group, 0, 0, List.of(), input, "", resultItem, resultCount, resultNbt, spec.experience(), spec.cookingTime(), 0);
            }
            case "blasting" -> {
                String input = aliasResolver.resolveString(spec.input());
                return new RecipeIR.CompiledRecipe(recipeId.toString(), "blasting", group, 0, 0, List.of(), input, "", resultItem, resultCount, resultNbt, spec.experience(), spec.cookingTime(), 0);
            }
            case "smoking" -> {
                String input = aliasResolver.resolveString(spec.input());
                return new RecipeIR.CompiledRecipe(recipeId.toString(), "smoking", group, 0, 0, List.of(), input, "", resultItem, resultCount, resultNbt, spec.experience(), spec.cookingTime(), 0);
            }
            case "campfire", "campfire_cooking" -> {
                String input = aliasResolver.resolveString(spec.input());
                return new RecipeIR.CompiledRecipe(recipeId.toString(), "campfire", group, 0, 0, List.of(), input, "", resultItem, resultCount, resultNbt, spec.experience(), spec.cookingTime(), 0);
            }
            case "smithing" -> {
                String input = aliasResolver.resolveString(spec.input());
                String addition = (spec.ingredients() != null && !spec.ingredients().isEmpty())
                        ? aliasResolver.resolveString(spec.ingredients().get(0)) : "";
                return new RecipeIR.CompiledRecipe(recipeId.toString(), "smithing", group, 0, 0, List.of(), input, addition, resultItem, resultCount, resultNbt, 0f, 0, 0);
            }
            default -> {
                BuildscapeCommon.LOGGER.warn("BDRE Compiler: Unknown recipe type '{}' for id {}", recipeType, recipeId);
                return null;
            }
        }
    }

    private RecipeIR.CompiledRecipe compileShaped(String id, String group, RecipeIR.RecipeSpec spec, String resultItem, int resultCount, String resultNbt, boolean durability) {
        List<String> patternList = spec.pattern();
        if (patternList == null || patternList.isEmpty()) return null;
        int height = patternList.size();
        int width = 0;
        for (String line : patternList) {
            width = Math.max(width, line.length());
        }

        Map<Character, String> keyMap = new HashMap<>();
        if (spec.keys() != null) {
            for (Map.Entry<String, String> entry : spec.keys().entrySet()) {
                if (entry.getKey().length() > 0) {
                    char c = entry.getKey().charAt(0);
                    String ingStr = aliasResolver.resolveString(entry.getValue());
                    keyMap.put(c, ingStr);
                }
            }
        }

        List<String> ingredients = new ArrayList<>(width * height);
        for (int i = 0; i < width * height; i++) {
            ingredients.add("");
        }

        for (int row = 0; row < height; row++) {
            String line = patternList.get(row);
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                if (c != ' ') {
                    String ing = keyMap.getOrDefault(c, "");
                    ingredients.set(row * width + col, ing);
                }
            }
        }

        ShapedPatternTrimmer.TrimmedStrings trimmed = ShapedPatternTrimmer.trimStrings(width, height, ingredients);
        return new RecipeIR.CompiledRecipe(
                id,
                durability ? "shaped_durability" : "shaped",
                group,
                trimmed.width(),
                trimmed.height(),
                trimmed.ingredients(),
                "",
                "",
                resultItem,
                resultCount,
                resultNbt,
                0f,
                0,
                durability ? 1 : 0
        );
    }

    private RecipeIR.CompiledRecipe compileShapeless(String id, String group, RecipeIR.RecipeSpec spec, String resultItem, int resultCount, String resultNbt, boolean durability) {
        List<String> ingredients = new ArrayList<>();
        if (spec.ingredients() != null) {
            for (String ingStr : spec.ingredients()) {
                String ing = aliasResolver.resolveString(ingStr);
                if (!ing.isEmpty()) {
                    ingredients.add(ing);
                }
            }
        }
        return new RecipeIR.CompiledRecipe(
                id,
                durability ? "shapeless_durability" : "shapeless",
                group,
                0,
                0,
                ingredients,
                "",
                "",
                resultItem,
                resultCount,
                resultNbt,
                0f,
                0,
                durability ? 1 : 0
        );
    }

    private String generateRecipeId(RecipeIR.RecipeSpec spec) {
        String res = (spec.result() != null && spec.result().item() != null) ? spec.result().item() : "unknown";
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
