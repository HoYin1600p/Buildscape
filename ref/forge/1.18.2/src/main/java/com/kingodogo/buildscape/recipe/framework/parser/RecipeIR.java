package com.kingodogo.buildscape.recipe.framework.parser;

import java.util.List;
import java.util.Map;

public class RecipeIR {

    public record CategoryPack(
            String category,
            Map<String, String> aliases,
            Map<String, TemplateSpec> templates,
            List<FamilySpec> families,
            List<RecipeSpec> recipes
    ) {}

    public record TemplateSpec(
            String type,
            List<String> pattern,
            Map<String, String> keys,
            List<String> ingredients,
            ResultSpec result,
            int cookingTime,
            float experience
    ) {}

    public record FamilySpec(
            String type,
            String base,
            String prefix,
            List<String> generate,
            List<String> exclude,
            boolean reversible
    ) {}

    public record RecipeSpec(
            String id,
            String type,
            String group,
            List<String> pattern,
            Map<String, String> keys,
            List<String> ingredients,
            String input,
            ResultSpec result,
            int cookingTime,
            float experience,
            String rawJson
    ) {
        public RecipeSpec(String id, String type, String group, List<String> pattern, Map<String, String> keys, List<String> ingredients, String input, ResultSpec result, int cookingTime, float experience) {
            this(id, type, group, pattern, keys, ingredients, input, result, cookingTime, experience, null);
        }
    }

    public record ResultSpec(
            String item,
            int count,
            String nbt
    ) {}
}
