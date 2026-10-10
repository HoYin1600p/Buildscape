package com.kingodogo.buildscape.recipe.framework.util;

public final class PatternBoundsTest {
    private static int checks;

    private PatternBoundsTest() {
    }

    public static void main(String[] args) {
        PatternBounds wall = bounds("   ", "BBB", "BBB");
        check(wall.minRow() == 1 && wall.minCol() == 0, "leading blank row is trimmed");
        check(wall.width() == 3 && wall.height() == 2, "wall pattern shrinks to 3x2");

        PatternBounds pillar = bounds(" R ", " R ", " R ");
        check(pillar.minRow() == 0 && pillar.minCol() == 1, "blank side columns are trimmed");
        check(pillar.width() == 1 && pillar.height() == 3, "pillar pattern shrinks to 1x3");
        check(pillar.sourceIndex(2, 0, 3) == 7, "trimmed cell maps back to its source index");

        PatternBounds corner = bounds("A  ", "   ", "  B");
        check(corner.width() == 3 && corner.height() == 3, "interior blanks are kept");
        check(corner.isFull(3, 3), "pattern touching every edge is already trimmed");

        PatternBounds single = bounds("   ", " X ", "   ");
        check(single.minRow() == 1 && single.minCol() == 1, "single cell keeps its offset");
        check(single.width() == 1 && single.height() == 1, "single cell shrinks to 1x1");

        PatternBounds empty = bounds("  ", "  ");
        check(empty.isFull(2, 2), "empty pattern is left unchanged");

        System.out.println("Pattern bounds tests passed: " + checks + " checks");
    }

    private static PatternBounds bounds(String... rows) {
        int width = rows[0].length();
        String joined = String.join("", rows);
        return PatternBounds.of(width, rows.length, index -> joined.charAt(index) != ' ');
    }

    private static void check(boolean result, String description) {
        if (!result) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
