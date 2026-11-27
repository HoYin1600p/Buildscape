package com.kingodogo.buildscape.recipe.framework.compiler;

import com.google.gson.JsonParser;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModLoadedConditionsTest {
    @ParameterizedTest
    @ValueSource(strings = {"forge:mod_loaded", "neoforge:mod_loaded", "fabric:all_mods_loaded"})
    void testsPresenceBeforeCompiling(String type) {
        String json = "{\"conditions\":[{\"type\":\"" + type + "\",\"modid\":\"example\"}]}";
        var recipe = recipe("confetti_configure", json);
        assertNotNull(new BuildScapeRecipeCompiler(Set.of("example")::contains).compileSingleRecipe("special", recipe));
        assertNull(new BuildScapeRecipeCompiler(Set.<String>of()::contains).compileSingleRecipe("special", recipe));
    }

    @Test
    void fabricChecksEveryValueAndUsesItsConditionKey() {
        var json = JsonParser.parseString("[{\"condition\":\"fabric:all_mods_loaded\",\"values\":[\"one\",\"two\"]}]");
        assertTrue(BuildScapeRecipeCompiler.conditionsMet(json, Set.of("one", "two")::contains));
        assertFalse(BuildScapeRecipeCompiler.conditionsMet(json, Set.of("one")::contains));
    }

    @Test
    void selectsTheFirstSatisfiedConditionalRecipe() {
        var compiler = new BuildScapeRecipeCompiler(Set.of("present")::contains);
        String json = """
                {"recipes":[
                  {"conditions":[{"type":"neoforge:mod_loaded","modid":"missing"}],
                   "recipe":{"type":"confetti_configure"}},
                  {"conditions":[{"type":"forge:mod_loaded","modid":"present"}],
                   "recipe":{"type":"clear_shulker_filters"}}
                ]}
                """;
        assertEquals("clear_shulker_filters", compiler.compileSingleRecipe("special", recipe("forge:conditional", json)).type());
        assertNull(new BuildScapeRecipeCompiler(Set.<String>of()::contains)
                .compileSingleRecipe("special", recipe("forge:conditional", json)));
    }

    @Test
    void recognizesTopLevelLoaderConditions() {
        var compiler = new BuildScapeRecipeCompiler(Set.<String>of()::contains);
        assertNull(compiler.compileSingleRecipe("special", recipe("confetti_configure",
                "{\"neoforge:conditions\":[{\"type\":\"neoforge:mod_loaded\",\"modid\":\"missing\"}]}")));
        assertNull(compiler.compileSingleRecipe("special", recipe("confetti_configure",
                "{\"fabric:load_conditions\":[{\"condition\":\"fabric:all_mods_loaded\",\"values\":[\"missing\"]}]}")));
    }

    @Test
    void missingIdsAndUnavailablePlatformFailClosed() {
        assertFalse(BuildScapeRecipeCompiler.conditionsMet(
                JsonParser.parseString("[{\"type\":\"neoforge:mod_loaded\"}]"), id -> true));
        assertFalse(BuildScapeRecipeCompiler.conditionsMet(
                JsonParser.parseString("[{\"type\":\"forge:mod_loaded\",\"modid\":\"example\"}]"),
                id -> { throw new IllegalStateException("Platform unavailable"); }));
    }

    private static RecipeIR.RecipeSpec recipe(String type, String json) {
        return new RecipeIR.RecipeSpec("conditional_test", type, "", List.of(), Map.of(), List.of(),
                null, null, 0, 0, json);
    }
}
