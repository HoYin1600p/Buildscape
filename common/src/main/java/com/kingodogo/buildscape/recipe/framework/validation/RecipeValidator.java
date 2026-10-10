package com.kingodogo.buildscape.recipe.framework.validation;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.compiler.AliasResolver;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Set;

public class RecipeValidator {

    private final Set<CommonId> registeredIds = new HashSet<>();

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
        try {
            Item item = Services.PLATFORM.getItem(loc);
            return item != null && item != Items.AIR;
        } catch (Throwable ignored) {
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
        if (namespace.isBlank() || path.isBlank() || namespace.indexOf(' ') >= 0 || path.indexOf(' ') >= 0) return null;
        return new CommonId(namespace, path);
    }
}
