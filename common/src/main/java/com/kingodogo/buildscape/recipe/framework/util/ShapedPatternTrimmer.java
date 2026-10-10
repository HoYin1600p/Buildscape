package com.kingodogo.buildscape.recipe.framework.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public final class ShapedPatternTrimmer {

    public record Trimmed(int width, int height, NonNullList<Ingredient> ingredients) {
    }

    private ShapedPatternTrimmer() {
    }

    public static Trimmed trim(int width, int height, NonNullList<Ingredient> ingredients) {
        PatternBounds bounds = PatternBounds.of(width, height, index -> !ingredients.get(index).isEmpty());
        if (bounds.isFull(width, height)) {
            return new Trimmed(width, height, ingredients);
        }

        NonNullList<Ingredient> trimmed = NonNullList.withSize(bounds.width() * bounds.height(), Ingredient.of());
        for (int row = 0; row < bounds.height(); row++) {
            for (int col = 0; col < bounds.width(); col++) {
                trimmed.set(row * bounds.width() + col, ingredients.get(bounds.sourceIndex(row, col, width)));
            }
        }
        return new Trimmed(bounds.width(), bounds.height(), trimmed);
    }

    public record TrimmedStrings(int width, int height, java.util.List<String> ingredients) {
    }

    public static TrimmedStrings trimStrings(int width, int height, java.util.List<String> ingredients) {
        PatternBounds bounds = PatternBounds.of(width, height, index -> {
            String s = ingredients.get(index);
            return s != null && !s.isEmpty() && !"{}".equals(s) && !"minecraft:air".equals(s);
        });
        if (bounds.isFull(width, height)) {
            return new TrimmedStrings(width, height, ingredients);
        }

        java.util.List<String> trimmed = new java.util.ArrayList<>(bounds.width() * bounds.height());
        for (int row = 0; row < bounds.height(); row++) {
            for (int col = 0; col < bounds.width(); col++) {
                trimmed.add(ingredients.get(bounds.sourceIndex(row, col, width)));
            }
        }
        return new TrimmedStrings(bounds.width(), bounds.height(), trimmed);
    }
}
