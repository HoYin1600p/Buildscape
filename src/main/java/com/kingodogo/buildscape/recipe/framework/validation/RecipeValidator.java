package com.kingodogo.buildscape.recipe.framework.validation;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.recipe.framework.compiler.AliasResolver;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates parsed recipe specifications before building Minecraft Recipe objects.
 * Prevents registration of recipes with unregistered items, invalid shapes, or duplicate IDs.
 */
public class RecipeValidator {

    private final Set<ResourceLocation> registeredIds = new HashSet<>();

    public boolean validate(RecipeIR.RecipeSpec spec, AliasResolver aliasResolver) {
        if (spec == null) {
            return false;
        }

        // Validate Result Item
        if (spec.result() == null || spec.result().item() == null || spec.result().item().isEmpty()) {
            BuildScape.LOGGER.debug("BDRE Validator: Recipe {} skipped due to missing result item.", spec.id());
            return false;
        }

        String resultItemStr = aliasResolver.resolveString(spec.result().item());
        ResourceLocation resultLoc = ResourceLocation.tryParse(resultItemStr);
        if (resultLoc == null || !ForgeRegistries.ITEMS.containsKey(resultLoc)) {
            BuildScape.LOGGER.debug("BDRE Validator: Item {} not found in registry for recipe {}.", resultItemStr, spec.id());
            return false;
        }

        // Validate Ingredients
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

    public boolean checkDuplicate(ResourceLocation recipeId) {
        if (recipeId == null) return true;
        if (registeredIds.contains(recipeId)) {
            BuildScape.LOGGER.debug("BDRE Validator: Duplicate recipe ID detected: {}", recipeId);
            return true;
        }
        registeredIds.add(recipeId);
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
            return ResourceLocation.tryParse(tagId) != null;
        }
        ResourceLocation loc = ResourceLocation.tryParse(resolved);
        return loc != null && ForgeRegistries.ITEMS.containsKey(loc);
    }

    public void clear() {
        registeredIds.clear();
    }
}
