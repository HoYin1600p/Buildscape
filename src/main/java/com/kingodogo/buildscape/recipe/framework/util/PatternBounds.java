package com.kingodogo.buildscape.recipe.framework.util;

import java.util.function.IntPredicate;

public record PatternBounds(int minRow, int minCol, int width, int height) {

    public static PatternBounds of(int width, int height, IntPredicate filledAt) {
        int minRow = height;
        int maxRow = -1;
        int minCol = width;
        int maxCol = -1;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (filledAt.test(row * width + col)) {
                    minRow = Math.min(minRow, row);
                    maxRow = Math.max(maxRow, row);
                    minCol = Math.min(minCol, col);
                    maxCol = Math.max(maxCol, col);
                }
            }
        }
        if (maxRow < 0) {
            return new PatternBounds(0, 0, width, height);
        }
        return new PatternBounds(minRow, minCol, maxCol - minCol + 1, maxRow - minRow + 1);
    }

    public boolean isFull(int width, int height) {
        return minRow == 0 && minCol == 0 && this.width == width && this.height == height;
    }

    public int sourceIndex(int row, int col, int sourceWidth) {
        return (row + minRow) * sourceWidth + col + minCol;
    }
}
