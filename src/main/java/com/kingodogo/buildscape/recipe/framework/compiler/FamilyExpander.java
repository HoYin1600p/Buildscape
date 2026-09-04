package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FamilyExpander {

    private final TemplateEngine templateEngine;

    public FamilyExpander(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public List<RecipeIR.RecipeSpec> expandFamily(RecipeIR.FamilySpec family) {
        List<RecipeIR.RecipeSpec> generated = new ArrayList<>();
        String baseItem = family.base();
        if (baseItem == null || baseItem.isEmpty()) {
            return generated;
        }

        String familyType = family.type();
        String prefix = family.prefix();
        List<String> targets = family.generate();
        List<String> excludes = family.exclude() != null ? family.exclude() : List.of();
        boolean reversible = family.reversible();

        if (prefix == null || prefix.isEmpty()) {
            if (baseItem.endsWith("_planks")) {
                prefix = baseItem.substring(0, baseItem.length() - "planks".length());
                if ("auto".equals(familyType) || familyType.isEmpty()) familyType = "wood";
            } else {
                prefix = baseItem + "_";
                if ("auto".equals(familyType) || familyType.isEmpty()) familyType = "stone";
            }
        }

        if (targets == null || targets.isEmpty()) {
            if ("wood".equalsIgnoreCase(familyType)) {
                targets = List.of("stairs", "slab", "fence", "fence_gate", "gate", "door", "trapdoor", "button", "pressure_plate", "stonecutter");
            } else {
                targets = List.of("stairs", "slab", "wall", "chiseled", "stonecutter");
            }
        }

        for (String target : targets) {
            if (excludes.contains(target)) {
                continue;
            }

            String recipeId = sanitizeId(prefix + target + "_from_" + baseItem);
            String outputItem = prefix + target;

            switch (target) {
                case "stairs" -> addTemplateOrFallback(generated, recipeId, "stairs", baseItem, outputItem, 4, "shaped",
                        List.of("X  ", "XX ", "XXX"), baseItem);
                case "slab" -> addTemplateOrFallback(generated, recipeId, "slab", baseItem, outputItem, 6, "shaped",
                        List.of("XXX"), baseItem);
                case "wall" -> addTemplateOrFallback(generated, recipeId, "wall", baseItem, outputItem, 6, "shaped",
                        List.of("XXX", "XXX"), baseItem);
                case "fence" -> addCustomShaped(generated, recipeId, List.of("W#W", "W#W"), baseItem, "minecraft:stick", outputItem, 3);
                case "gate", "fence_gate" -> addCustomShaped(generated, recipeId, List.of("#W#", "#W#"), baseItem, "minecraft:stick", outputItem, 1);
                case "door" -> addTemplateOrFallback(generated, recipeId, "door", baseItem, outputItem, 3, "shaped",
                        List.of("XX", "XX", "XX"), baseItem);
                case "trapdoor" -> addTemplateOrFallback(generated, recipeId, "trapdoor", baseItem, outputItem, 2, "shaped",
                        List.of("XXX", "XXX"), baseItem);
                case "button" -> addTemplateOrFallback(generated, recipeId, "button", baseItem, outputItem, 1, "shapeless",
                        List.of(), baseItem);
                case "pressure_plate" -> addTemplateOrFallback(generated, recipeId, "pressure_plate", baseItem, outputItem, 1, "shaped",
                        List.of("XX"), baseItem);
                case "chiseled" -> addTemplateOrFallback(generated, recipeId, "chiseled", baseItem, outputItem, 1, "shaped",
                        List.of("XX", "XX"), baseItem);
                case "stonecutter" -> addStonecutterRecipe(generated, recipeId, baseItem, outputItem, 1);
                default -> {
                    RecipeIR.RecipeSpec spec = templateEngine.expand(recipeId, target, baseItem, outputItem, 1);
                    if (spec != null) {
                        generated.add(spec);
                    }
                }
            }

            if ("stone".equalsIgnoreCase(familyType) && !"stonecutter".equals(target)) {
                String scId = sanitizeId(prefix + target + "_stonecutting");
                int scCount = "slab".equals(target) ? 2 : 1;
                addStonecutterRecipe(generated, scId, baseItem, outputItem, scCount);
                if (reversible) {
                    String reverseScId = sanitizeId(baseItem + "_from_" + prefix + target + "_stonecutting");
                    addStonecutterRecipe(generated, reverseScId, outputItem, baseItem, 1);
                }
            }
        }

        return generated;
    }

    private void addTemplateOrFallback(
            List<RecipeIR.RecipeSpec> generated,
            String recipeId,
            String templateName,
            String baseItem,
            String outputItem,
            int defaultCount,
            String defaultType,
            List<String> defaultPattern,
            String inputItem
    ) {
        RecipeIR.RecipeSpec spec = templateEngine.expand(recipeId, templateName, baseItem, outputItem, defaultCount);
        if (spec != null) {
            generated.add(spec);
        } else {
            java.util.Map<String, String> keys = java.util.Map.of("X", inputItem);
            generated.add(new RecipeIR.RecipeSpec(
                    recipeId, defaultType, "", defaultPattern, keys, List.of(inputItem), baseItem,
                    new RecipeIR.ResultSpec(outputItem, defaultCount, null), 200, 0.1f
            ));
        }
    }

    private void addCustomShaped(
            List<RecipeIR.RecipeSpec> generated,
            String recipeId,
            List<String> pattern,
            String mainItem,
            String stickItem,
            String outputItem,
            int count
    ) {
        java.util.Map<String, String> keys = java.util.Map.of("W", mainItem, "#", stickItem);
        generated.add(new RecipeIR.RecipeSpec(
                recipeId, "shaped", "", pattern, keys, List.of(), mainItem,
                new RecipeIR.ResultSpec(outputItem, count, null), 200, 0.1f
        ));
    }

    private void addStonecutterRecipe(
            List<RecipeIR.RecipeSpec> generated,
            String recipeId,
            String baseItem,
            String outputItem,
            int count
    ) {
        generated.add(new RecipeIR.RecipeSpec(
                recipeId, "stonecutting", "", List.of(), java.util.Map.of(), List.of(), baseItem,
                new RecipeIR.ResultSpec(outputItem, count, null), 0, 0.0f
        ));
    }

    private String sanitizeId(String raw) {
        if (raw == null) return "unknown";
        return raw.toLowerCase(Locale.ROOT)
                .replace(":", "_")
                .replaceAll("[^a-z0-9_.-]", "_");
    }
}
