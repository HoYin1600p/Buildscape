package com.kingodogo.buildscape.recipe.framework;

import com.kingodogo.buildscape.recipe.framework.cache.BinaryRecipeCache;
import com.kingodogo.buildscape.recipe.framework.compiler.FamilyExpander;
import com.kingodogo.buildscape.recipe.framework.compiler.TemplateEngine;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;
import com.kingodogo.buildscape.recipe.framework.util.RecipePackFormatter;
import com.kingodogo.buildscape.recipe.framework.validation.RecipeValidator;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipeCacheFixesTest {
    @Test
    void sourceHashesNormalizeWindowsLineEndings() {
        String json = "{\n  \"recipes\": []\n}\n";
        String[] categories = {"crafting", "stonecutting"};
        String lf = BinaryRecipeCache.computeSourceHash(categories,
                Map.of("crafting", json.getBytes(StandardCharsets.UTF_8)));
        assertEquals(lf, BinaryRecipeCache.computeSourceHash(categories,
                Map.of("crafting", json.replace("\n", "\r\n").getBytes(StandardCharsets.UTF_8))));
        assertNotEquals(lf, BinaryRecipeCache.computeSourceHash(categories,
                Map.of("crafting", "{}".getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    void formatterRejectsDuplicateKeysAtEveryDepth() {
        assertThrows(IllegalArgumentException.class,
                () -> RecipePackFormatter.format("{\"recipes\":[],\"recipes\":[]}"));
        assertThrows(IllegalArgumentException.class,
                () -> RecipePackFormatter.format("{\"recipes\":[{\"id\":\"a\",\"id\":\"b\"}]}"));
        assertDoesNotThrow(() -> RecipePackFormatter.format("{\"recipes\":[]}"));
    }

    @Test
    void invalidRecordsDoNotPoisonFollowingValidationAndTagsRemainValid() {
        var validator = new RecipeValidator(id -> id.toString().equals("minecraft:stone"));
        assertFalse(validator.validateCompiled(recipe("shaped", List.of(""), "minecraft:stone")));
        assertFalse(validator.validateCompiled(recipe("shaped", List.of("{}"), "minecraft:stone")));
        assertFalse(validator.validateCompiled(recipe("shaped", List.of("not JSON"), "minecraft:stone")));
        assertFalse(validator.validateCompiled(recipe("shaped", List.of("minecraft:stone"), "minecraft:air")));
        assertTrue(validator.validateCompiled(recipe("shaped", List.of("#c:unbound_tag"), "minecraft:stone")));
        assertTrue(validator.validateCompiled(recipe("shaped", List.of("minecraft:stone"), "minecraft:stone")));
        assertTrue(validator.validateCompiled(recipe("confetti_configure", List.of(), "minecraft:air")));
        assertFalse(validator.checkDuplicate("buildscape:test"));
        assertTrue(validator.checkDuplicate("buildscape:test"));
    }

    @Test
    void cacheConsumesRejectedRecordAndKeepsFollowingSpecialRecipe() throws Exception {
        var bytes = new ByteArrayOutputStream();
        try (var out = new DataOutputStream(bytes)) {
            out.writeInt(0x4B59524F);
            out.writeInt(5);
            out.writeUTF("test-hash");
            String[] pool = {"buildscape:invalid", "", "not an identifier", "minecraft:stone", "buildscape:special"};
            out.writeInt(pool.length);
            for (String value : pool) out.writeUTF(value);
            out.writeInt(2);
            // Invalid stonecutting result, followed by a special recipe with no result.
            for (int value : new int[]{0, 1, 2, 1, 1}) out.writeInt(value);
            out.writeByte(3);
            out.writeInt(3);
            for (int value : new int[]{4, 1, 1, 1, 1}) out.writeInt(value);
            out.writeByte(11);
        }
        var recipes = BinaryRecipeCache.loadCacheFromStream(new ByteArrayInputStream(bytes.toByteArray()), "test-hash");
        assertEquals(1, recipes.size());
        assertEquals("buildscape:special", recipes.getFirst().id());
        assertTrue(BinaryRecipeCache.loadCacheFromStream(new ByteArrayInputStream(bytes.toByteArray()), "different-hash").isEmpty());
    }

    @Test
    void defaultFamiliesDoNotInventGateOrStonecutterBlocks() {
        var expander = new FamilyExpander(new TemplateEngine());
        for (String base : List.of("BS:ashpen_black_planks", "BS:poplar_planks", "BS:stone")) {
            String type = base.endsWith("_planks") ? "wood" : "stone";
            var recipes = expander.expandFamily(new RecipeIR.FamilySpec(type, base, "", List.of(), List.of(), false));
            assertEquals(base.contains("ashpen") ? 6 : 8, recipes.size());
            assertTrue(recipes.stream().noneMatch(recipe -> recipe.result().item().endsWith("_stonecutter")
                    || (recipe.result().item().endsWith("_gate") && !recipe.result().item().endsWith("_fence_gate"))));
        }
    }

    private static RecipeIR.CompiledRecipe recipe(String type, List<String> ingredients, String result) {
        return new RecipeIR.CompiledRecipe("buildscape:test", type, "", 1, 1, ingredients,
                "minecraft:stone", "", result, 1, "", 0f, 0, 0);
    }
}
