package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;

import java.util.*;

public class TemplateEngine {

    private final Map<String, RecipeIR.TemplateSpec> templates = new HashMap<>();

    public void registerTemplates(Map<String, RecipeIR.TemplateSpec> newTemplates) {
        if (newTemplates != null) {
            templates.putAll(newTemplates);
        }
    }

    public RecipeIR.TemplateSpec getTemplate(String name) {
        return templates.get(name);
    }

    public RecipeIR.RecipeSpec expand(
            String recipeId,
            String templateName,
            String inputItem,
            String outputItem,
            int outputCount
    ) {
        RecipeIR.TemplateSpec template = templates.get(templateName);
        if (template == null) {
            return null;
        }

        Map<String, String> resolvedKeys = new HashMap<>();
        if (template.keys() != null) {
            for (Map.Entry<String, String> entry : template.keys().entrySet()) {
                String keyChar = entry.getKey();
                String valPattern = entry.getValue();
                String replacedVal = valPattern.replace("${input}", inputItem).replace("${output}", outputItem);
                resolvedKeys.put(keyChar, replacedVal);
            }
        }

        List<String> resolvedIngredients = new ArrayList<>();
        if (template.ingredients() != null) {
            for (String ing : template.ingredients()) {
                resolvedIngredients.add(ing.replace("${input}", inputItem).replace("${output}", outputItem));
            }
        }

        int finalCount = outputCount > 0 ? outputCount : (template.result() != null ? template.result().count() : 1);
        RecipeIR.ResultSpec result = new RecipeIR.ResultSpec(outputItem, finalCount, null);

        return new RecipeIR.RecipeSpec(
                recipeId,
                template.type(),
                "",
                template.pattern() != null ? new ArrayList<>(template.pattern()) : Collections.emptyList(),
                resolvedKeys,
                resolvedIngredients,
                inputItem,
                result,
                template.cookingTime(),
                template.experience()
        );
    }
}
