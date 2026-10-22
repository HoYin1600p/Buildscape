package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.recipe.framework.compiler.AliasResolver;
import com.kingodogo.buildscape.recipe.framework.compiler.FamilyExpander;
import com.kingodogo.buildscape.recipe.framework.compiler.RecipeCacheGenerator;
import com.kingodogo.buildscape.recipe.framework.compiler.RecipeIdAllocator;
import com.kingodogo.buildscape.recipe.framework.compiler.ShapedRecipeSymbols;
import com.kingodogo.buildscape.recipe.framework.compiler.TemplateEngine;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import org.junit.Test;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RecipeCompilationTest {
    @Test
    public void bundledPackContainsShapedPoplarBoat() throws Exception {
        Path directory = Path.of("src/main/resources/data/buildscape/recipes_pack");
        List<RecipeIR.RecipeSpec> boats = parse(Files.readString(directory.resolve("crafting.json"))).stream()
                .filter(spec -> spec.result() != null && "BS:poplar_boat".equals(spec.result().item()))
                .toList();
        assertEquals(1, boats.size());
        RecipeIR.RecipeSpec boat = boats.get(0);
        assertEquals("shaped", boat.type());
        assertEquals(List.of("# #", "###"), boat.pattern());
        assertEquals(Map.of("#", "BS:poplar_planks"), boat.keys());
        assertEquals(1, boat.result().count());
        assertArrayEquals(RecipeCacheGenerator.generateCache(directory),
                Files.readAllBytes(directory.resolve("recipes.bscb")));
    }

    @Test
    public void outputIdsRemainStableWhenRecipeContentsChange() throws Exception {
        RecipeIR.RecipeSpec one = parse("{\"stonecutting\":[[\"BS:copper_chest\",\"MC:stone\",1]]}").get(0);
        RecipeIR.RecipeSpec two = parse("{\"stonecutting\":[[\"BS:copper_chest\",\"MC:copper_block\",2]]}").get(0);
        AliasResolver aliases = new AliasResolver();
        assertEquals("buildscape:autogen/crafting/copper_chest",
                new RecipeIdAllocator().allocate("crafting", one, aliases));
        assertEquals("buildscape:autogen/crafting/copper_chest",
                new RecipeIdAllocator().allocate("crafting", two, aliases));
        RecipeIdAllocator ids = new RecipeIdAllocator();
        String first = ids.allocate("crafting", one, aliases);
        String collision = ids.allocate("crafting", two, aliases);
        assertTrue(collision.startsWith(first + "_"));
        assertEquals(first, ids.allocate("crafting", one, aliases));
        assertEquals(collision, ids.allocate("crafting", two, aliases));
    }

    @Test
    public void nonunitReversibleConversionsKeepOnlyTheForwardRecipe() throws Exception {
        List<RecipeIR.RecipeSpec> recipes = parse("{\"stonecutting_reversible\":[[\"MC:stone_slab\",\"MC:stone\",2]]}");
        assertEquals(1, recipes.size());
        assertEquals(2, recipes.get(0).result().count());
        assertEquals(2, parse("{\"stonecutting_reversible\":[[\"MC:stone_bricks\",\"MC:stone\",1]]}").size());

        List<RecipeIR.RecipeSpec> family = new FamilyExpander(new TemplateEngine()).expandFamily(
                new RecipeIR.FamilySpec("stone", "MC:stone", "MC:stone_", List.of("slab"), List.of(), true));
        assertEquals(2, family.size());
        assertTrue(family.stream().noneMatch(spec -> "MC:stone".equals(spec.result().item())));
    }

    @Test
    public void undefinedShapedSymbolsAreRejected() {
        RecipeIR.RecipeSpec bad = new RecipeIR.RecipeSpec(null, "shaped", "", List.of("AB"),
                Map.of("A", "MC:stone"), List.of(), null,
                new RecipeIR.ResultSpec("MC:stone", 1, null), 0, 0);
        assertThrows(IllegalArgumentException.class, () -> ShapedRecipeSymbols.validate(bad));
        RecipeIR.RecipeSpec valid = new RecipeIR.RecipeSpec(null, "shaped", "", List.of("A "),
                Map.of("A", "MC:stone"), List.of(), null,
                new RecipeIR.ResultSpec("MC:stone", 1, null), 0, 0);
        ShapedRecipeSymbols.validate(valid);
    }

    private static List<RecipeIR.RecipeSpec> parse(String source) throws Exception {
        return StreamingRecipeParser.parseCategory("crafting", new StringReader(source)).recipes();
    }
}
