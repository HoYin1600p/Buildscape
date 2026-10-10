package com.kingodogo.buildscape.recipe.framework.util;

public final class RecipePackFormatterTest {
    private static int checks;

    private RecipePackFormatterTest() {
    }

    public static void main(String[] args) {
        String input = "{\"_comment\":\"test\",\"crafting_shaped\":[[\"BS:item\",[\"#\"],[[\"#\",\"MC:stone\"]],1]]}";
        String formatted = RecipePackFormatter.format(input);
        check(formatted != null && !formatted.isEmpty(), "formatted output is non-empty");
        check(formatted.contains("crafting_shaped"), "contains recipe category key");
        check(formatted.contains("[\"BS:item\",[\"#\"],[[\"#\",\"MC:stone\"]],1]"), "formats recipe array elements into compact single lines");
        check(formatted.contains("\"_comment\": \"test\""), "preserves top level keys");

        System.out.println("Recipe pack formatter tests passed: " + checks + " checks");
    }

    private static void check(boolean result, String description) {
        if (!result) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
