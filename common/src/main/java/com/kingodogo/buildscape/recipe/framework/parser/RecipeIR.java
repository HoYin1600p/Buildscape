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

    public record CompiledRecipe(
            String id,
            String type,
            String group,
            int width,
            int height,
            List<String> ingredients,
            String input,
            String addition,
            String resultItem,
            int resultCount,
            String resultNbt,
            float experience,
            int cookingTime,
            int damageAmount
    ) {
        public CompiledRecipe(String id, String type, String group, int width, int height,
                              List<String> ingredients, String input, String addition,
                              String resultItem, int resultCount, String resultNbt,
                              float experience, int cookingTime, int damageAmount) {
            this.id = id != null ? id : "";
            this.type = type != null ? type : "";
            this.group = group != null ? group : "";
            this.width = width;
            this.height = height;
            this.ingredients = ingredients != null ? List.copyOf(ingredients) : List.of();
            this.input = input != null ? input : "";
            this.addition = addition != null ? addition : "";
            this.resultItem = resultItem != null ? resultItem : "minecraft:air";
            this.resultCount = resultCount > 0 ? resultCount : 1;
            this.resultNbt = resultNbt != null ? resultNbt : "";
            this.experience = experience;
            this.cookingTime = cookingTime;
            this.damageAmount = damageAmount;
        }
    }
}

