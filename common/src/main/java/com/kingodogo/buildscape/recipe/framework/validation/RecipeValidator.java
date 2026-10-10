package com.kingodogo.buildscape.recipe.framework.validation;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.compiler.AliasResolver;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Predicate;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.BuildscapeCommon;

public class RecipeValidator {

    private final Set<CommonId> registeredIds = new HashSet<>();
    private final Predicate<CommonId> itemExists;

    public RecipeValidator() {
        this(RecipeValidator::registryHasItem);
    }

    public RecipeValidator(Predicate<CommonId> itemExists) {
        this.itemExists = itemExists;
    }

    /** Validates cached IR without binding tags or constructing native recipes. */
    public boolean validateCompiled(RecipeIR.CompiledRecipe recipe) {
        String reason = invalidReason(recipe);
        if (reason == null) return true;
        BuildscapeCommon.LOGGER.warn("BDRE: Dropped recipe {}: {}", recipe.id(), reason);
        return false;
    }

    private String invalidReason(RecipeIR.CompiledRecipe recipe) {
        if (parseId(recipe.id()) == null) return "invalid recipe id";
        String type = recipe.type().toLowerCase(Locale.ROOT).replaceFirst("^buildscape:", "");
        if (type.equals("confetti_configure") || type.equals("clear_shulker_filters")) return null;
        CommonId result = parseId(recipe.resultItem());
        if (result == null || !hasItem(result)) return "result resolves to air: " + recipe.resultItem();
        boolean shaped = type.equals("shaped") || type.equals("shaped_durability");
        if (shaped || type.equals("shapeless") || type.equals("shapeless_durability")) {
            if (shaped && (recipe.width() < 1 || recipe.width() > 3 || recipe.height() < 1
                    || recipe.height() > 3 || recipe.ingredients().size() != recipe.width() * recipe.height())) {
                return "invalid shaped dimensions";
            }
            boolean occupied = false;
            for (String ingredient : recipe.ingredients()) {
                if (shaped && ingredient.isEmpty()) continue;
                if (!validCompiledIngredient(ingredient)) return "invalid ingredient: " + ingredient;
                occupied = true;
            }
            return occupied ? null : "all recipe slots are empty";
        }
        if (!validCompiledIngredient(recipe.input())) return "invalid input: " + recipe.input();
        if (type.equals("smithing") && !validCompiledIngredient(recipe.addition())) return "invalid smithing addition";
        return null;
    }

    private boolean validCompiledIngredient(String value) {
        if (value == null || value.isBlank()) return false;
        if (value.startsWith("[")) {
            if (!value.endsWith("]")) return false;
            String inner = value.substring(1, value.length() - 1).trim();
            if (inner.isEmpty()) return false;
            if (inner.startsWith("\"") || inner.startsWith("{")) {
                try {
                    JsonElement json = JsonParser.parseString(value);
                    boolean valid = false;
                    for (JsonElement entry : json.getAsJsonArray()) {
                        if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()
                                || !validCompiledIngredient(entry.getAsString())) return false;
                        valid = true;
                    }
                    return valid;
                } catch (RuntimeException exception) {
                    return false;
                }
            }
            // Compact alternative lists are not JSON. At least one alternative
            // must exist; tags remain valid before the reload binds their contents.
            AliasResolver aliases = new AliasResolver();
            for (String alternative : inner.split(",")) {
                if (validCompiledIngredient(aliases.resolveString(alternative.trim()))) return true;
            }
            return false;
        }
        if (value.startsWith("{") || value.startsWith("\"")) return false;
        boolean tag = value.startsWith("#");
        CommonId id = parseId(tag ? value.substring(1) : value);
        return id != null && (tag || hasItem(id));
    }

    public static List<RecipeIR.CompiledRecipe> filterCompiled(List<RecipeIR.CompiledRecipe> recipes) {
        RecipeValidator validator = new RecipeValidator();
        List<RecipeIR.CompiledRecipe> accepted = new ArrayList<>();
        for (RecipeIR.CompiledRecipe recipe : recipes) {
            if (recipe != null && validator.validateCompiled(recipe) && !validator.checkDuplicate(recipe.id())) {
                accepted.add(recipe);
            }
        }
        return accepted;
    }

    public boolean validate(RecipeIR.RecipeSpec spec, AliasResolver aliasResolver) {
        if (spec == null) {
            return false;
        }

        if (spec.result() == null || spec.result().item() == null || spec.result().item().isEmpty()) {
            return false;
        }

        String resultItemStr = aliasResolver.resolveString(spec.result().item());
        CommonId resultLoc = parseId(resultItemStr);
        if (resultLoc == null || !hasItem(resultLoc)) {
            return false;
        }

        if ("shapeless".equalsIgnoreCase(spec.type())) {
            if (spec.ingredients() == null || spec.ingredients().isEmpty()) {
                return false;
            }
            for (String ing : spec.ingredients()) {
                if (!validateIngredientSpec(ing, aliasResolver)) {
                    return false;
                }
            }
        } else if ("shaped".equalsIgnoreCase(spec.type())) {
            if (spec.pattern() == null || spec.pattern().isEmpty() || spec.keys() == null || spec.keys().isEmpty()) {
                return false;
            }
            for (String ing : spec.keys().values()) {
                if (!validateIngredientSpec(ing, aliasResolver)) {
                    return false;
                }
            }
        } else if (spec.input() != null) {
            if (!validateIngredientSpec(spec.input(), aliasResolver)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkDuplicate(Object recipeId) {
        CommonId commonId = recipeId instanceof CommonId id ? id : parseId(String.valueOf(recipeId));
        if (commonId == null) return true;
        if (registeredIds.contains(commonId)) {
            BuildscapeCommon.LOGGER.warn("BDRE: Duplicate recipe ID {} (keeping the first recipe)", commonId);
            return true;
        }
        registeredIds.add(commonId);
        return false;
    }

    private boolean validateIngredientSpec(String rawSpec, AliasResolver aliasResolver) {
        if (rawSpec == null || rawSpec.isEmpty()) {
            return false;
        }

        if (rawSpec.startsWith("[") && rawSpec.endsWith("]")) {
            String alternatives = rawSpec.substring(1, rawSpec.length() - 1);
            for (String alternative : alternatives.split(",")) {
                if (validateIngredientSpec(alternative.trim(), aliasResolver)) {
                    return true;
                }
            }
            return false;
        }

        String resolved = aliasResolver.resolveString(rawSpec);
        if (resolved.startsWith("#")) {
            String tagId = resolved.substring(1);
            return parseId(tagId) != null;
        }
        CommonId loc = parseId(resolved);
        return loc != null && hasItem(loc);
    }

    private boolean hasItem(CommonId loc) {
        return itemExists.test(loc);
    }

    private static boolean registryHasItem(CommonId loc) {
        try {
            Item item = Services.PLATFORM.getItem(loc);
            return item != null && item != Items.AIR;
        } catch (Throwable ignored) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to resolve recipe result item", ignored);
            return false;
        }
    }

    public void clear() {
        registeredIds.clear();
    }

    private static CommonId parseId(String value) {
        if (value == null || value.isBlank()) return null;
        String[] parts = value.split(":", 2);
        String namespace = parts.length == 2 ? parts[0] : "minecraft";
        String path = parts.length == 2 ? parts[1] : parts[0];
        if (!namespace.matches("[a-z0-9_.-]+") || !path.matches("[a-z0-9/._-]+")) return null;
        return new CommonId(namespace, path);
    }
}
