package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;

import java.util.Locale;

public final class ShapedRecipeSymbols {
    private ShapedRecipeSymbols() {}

    public static void validate(RecipeIR.RecipeSpec spec) {
        String type = spec.type() == null ? "" : spec.type().toLowerCase(Locale.ROOT);
        if (!type.equals("shaped") && !type.equals("shaped_durability")
                && !type.equals("buildscape:shaped_durability")) {
            return;
        }
        if (spec.pattern() == null || spec.pattern().isEmpty()) {
            throw new IllegalArgumentException("Shaped recipe has no pattern");
        }
        for (String row : spec.pattern()) {
            for (int i = 0; i < row.length(); i++) {
                char symbol = row.charAt(i);
                if (symbol != ' ' && (spec.keys() == null || !spec.keys().containsKey(String.valueOf(symbol)))) {
                    throw new IllegalArgumentException("Pattern references undefined symbol '" + symbol + "'");
                }
            }
        }
    }
}
