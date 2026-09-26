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

        NonNullList<Ingredient> trimmed = NonNullList.withSize(bounds.width() * bounds.height(), Ingredient.EMPTY);
        for (int row = 0; row < bounds.height(); row++) {
            for (int col = 0; col < bounds.width(); col++) {
                trimmed.set(row * bounds.width() + col, ingredients.get(bounds.sourceIndex(row, col, width)));
            }
        }
        return new Trimmed(bounds.width(), bounds.height(), trimmed);
    }
}
