package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.recipe.framework.compiler.FamilyExpander;
import com.kingodogo.buildscape.recipe.framework.compiler.TemplateEngine;
import com.kingodogo.buildscape.recipe.framework.parser.StreamingRecipeParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VanillaWoodFamiliesTest {
    @Test
    void keptVerticalSlabsUseVanillaPlanksAndBuildscapeOutputs() throws Exception {
        var stream = getClass().getResourceAsStream("/data/buildscape/recipes_pack/crafting.json");
        assertNotNull(stream);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            var pack = StreamingRecipeParser.parseCategory("crafting", reader);
            var templates = new TemplateEngine();
            templates.registerTemplates(pack.templates());
            var expander = new FamilyExpander(templates);
            for (String wood : List.of("mangrove", "cherry", "pale_oak")) {
                var families = pack.families().stream()
                        .filter(family -> ("MC:" + wood + "_planks").equals(family.base()))
                        .toList();
                assertEquals(1, families.size(), wood);
                var recipes = expander.expandFamily(families.getFirst());
                assertEquals(1, recipes.size(), wood);
                var recipe = recipes.getFirst();
                assertEquals("BS:" + wood + "_vertical_slab", recipe.result().item());
                assertEquals(6, recipe.result().count());
                assertEquals(Map.of("X", "MC:" + wood + "_planks"), recipe.keys());
                assertEquals(List.of("X", "X", "X"), recipe.pattern());
            }
        }
    }
}
