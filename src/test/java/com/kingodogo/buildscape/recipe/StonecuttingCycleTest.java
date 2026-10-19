package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.recipe.framework.compiler.RecipeCacheGenerator;
import com.kingodogo.buildscape.recipe.framework.compiler.StonecuttingCycleValidator;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class StonecuttingCycleTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void realPackCompilesWithoutStonecuttingGainCycles() throws Exception {
        assertTrue(RecipeCacheGenerator.generateCache(
                Path.of("src/main/resources/data/buildscape/recipes_pack")).length > 0);
    }

    @Test
    public void compilerRejectsThreeStepGainCycleAcrossCategories() throws Exception {
        Path source = temporary.newFolder().toPath();
        Files.writeString(source.resolve("crafting.json"), "{\"stonecutting\":["
                + "{\"id\":\"first\",\"input\":\"MC:stone\",\"result\":{\"item\":\"MC:granite\",\"count\":4}}]}");
        Files.writeString(source.resolve("stonecutting.json"), "{\"stonecutting\":["
                + "{\"id\":\"second\",\"input\":\"MC:granite\",\"result\":\"MC:diorite\"},"
                + "{\"id\":\"third\",\"input\":\"MC:diorite\",\"result\":\"MC:stone\"}]}");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> RecipeCacheGenerator.generateCache(source));
        assertTrue(error.getMessage().contains("net item gain"));
        for (String id : List.of("first", "second", "third")) {
            assertTrue(error.getMessage(), error.getMessage().contains("/" + id + " ("));
        }
    }

    @Test
    public void compilerChecksCategoriesExcludedFromTheBinaryCache() throws Exception {
        Path source = temporary.newFolder().toPath();
        Files.writeString(source.resolve("special.json"), "{\"conditions\":[],"
                + "\"stonecutting\":[[\"MC:granite\",\"MC:stone\",2],[\"MC:stone\",\"MC:granite\",1]]}");
        assertThrows(IllegalArgumentException.class, () -> RecipeCacheGenerator.generateCache(source));
    }

    @Test
    public void unitReversalsAndAcyclicGainsAreAllowed() throws Exception {
        StonecuttingCycleValidator validator = new StonecuttingCycleValidator();
        validator.addCategory(parse("{\"stonecutting_reversible\":[[\"MC:granite\",\"MC:stone\",1]],"
                + "\"stonecutting\":[[\"MC:stone_slab\",\"MC:granite\",2]]}"));
        validator.validate();
    }

    @Test
    public void multiplyingSelfLoopIsRejectedWithoutOverflow() throws Exception {
        StonecuttingCycleValidator validator = new StonecuttingCycleValidator();
        validator.addCategory(parse("{\"stonecutting\":[[\"MC:stone\",\"minecraft:stone\",2147483647]]}"));
        assertThrows(IllegalArgumentException.class, validator::validate);
    }

    @Test
    public void familyExpansionAndIngredientAlternativesParticipateInCycles() throws Exception {
        StonecuttingCycleValidator validator = new StonecuttingCycleValidator();
        validator.addCategory(parse("{\"stone_families\":[{\"base\":\"MC:stone\",\"prefix\":\"MC:stone_\","
                + "\"generate\":[\"slab\"]}],\"stonecutting\":[[\"MC:stone\",\"[MC:granite,MC:stone_slab]\",1]]}"));
        assertThrows(IllegalArgumentException.class, validator::validate);
    }

    @Test
    public void realPackKeepsUnitReversalsAndCutsBitGratesOneToOne() throws Exception {
        RecipeIR.CategoryPack pack;
        try (var reader = Files.newBufferedReader(Path.of("src/main/resources/data/buildscape/recipes_pack/stonecutting.json"))) {
            pack = StreamingRecipeParser.parseCategory("stonecutting", reader);
        }
        assertTrue(hasConversion(pack, "BS:bit_copper_grate", "BS:bit_exposed_copper_grate"));
        assertTrue(hasConversion(pack, "BS:bit_exposed_copper_grate", "BS:bit_copper_grate"));
        assertTrue(hasConversion(pack, "BS:bit_cut_copper_slab", "BS:bit_cut_copper_vertical_slab"));
        assertTrue(hasConversion(pack, "BS:bit_cut_copper_vertical_slab", "BS:bit_cut_copper_slab"));
        for (String grate : List.of("BS:bit_copper_grate", "BS:bit_exposed_copper_grate",
                "BS:bit_weathered_copper_grate", "BS:bit_oxidized_copper_grate")) {
            assertTrue(grate, hasConversion(pack, "MC:copper_block", grate));
        }
        assertTrue(hasConversion(pack, "BS:bit_copper_grate", "MC:copper_block"));
    }

    @Test
    public void clearRemovesPreviousGraphEdges() throws Exception {
        StonecuttingCycleValidator validator = new StonecuttingCycleValidator();
        validator.addCategory(parse("{\"stonecutting\":[[\"MC:granite\",\"MC:stone\",2]]}"));
        validator.clear();
        validator.addCategory(parse("{\"stonecutting\":[[\"MC:stone\",\"MC:granite\",1]]}"));
        validator.validate();
    }

    private static boolean hasConversion(RecipeIR.CategoryPack pack, String input, String output) {
        return pack.recipes().stream().anyMatch(spec -> input.equals(spec.input())
                && output.equals(spec.result().item()) && spec.result().count() == 1);
    }

    private static RecipeIR.CategoryPack parse(String source) throws Exception {
        return StreamingRecipeParser.parseCategory("stonecutting", new StringReader(source));
    }
}
