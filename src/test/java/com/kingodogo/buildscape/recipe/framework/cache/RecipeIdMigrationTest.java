package com.kingodogo.buildscape.recipe.framework.cache;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.core.RecipeIdMigration;
import com.kingodogo.buildscape.recipe.framework.compiler.RecipeCacheGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RecipeIdMigrationTest extends BootstrappedGameTest {

    @Test
    public void everyMigrationTargetExistsInBundledCache() throws Exception {
        Set<ResourceLocation> recipeIds = readBundledRecipeIds();
        Set<ResourceLocation> oldIds = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                resource("recipe_id_migration_405.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] ids = line.split("\\s+");
                assertEquals("Expected old and new recipe ids", 2, ids.length);
                ResourceLocation oldId = new ResourceLocation(ids[0]);
                ResourceLocation newId = new ResourceLocation(ids[1]);
                assertTrue("Duplicate old recipe id: " + oldId, oldIds.add(oldId));
                assertEquals(newId, RecipeIdMigration.translate(oldId));
                assertTrue("Missing migration target: " + oldId + " -> " + newId, recipeIds.contains(newId));
            }
        }
        assertEquals(6_438, oldIds.size());
    }

    @Test
    public void translatesOldIdsAndPreservesUnmappedIds() {
        assertEquals(new ResourceLocation("buildscape:autogen/blasting/steel_block"),
                RecipeIdMigration.translate(new ResourceLocation(
                        "buildscape:buildscape_autogen/blasting/buildscape_steel_block_392972376")));
        for (String id : new String[]{"buildscape:autogen/blasting/steel_block",
                "buildscape:buildscape_autogen/missing", "buildscape:missing",
                "minecraft:buildscape_autogen/missing", "other:missing"}) {
            ResourceLocation original = new ResourceLocation(id);
            assertSame(original, RecipeIdMigration.translate(original));
        }
    }

    private static Set<ResourceLocation> readBundledRecipeIds() throws Exception {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(resource("recipes.bscb")))) {
            assertEquals(RecipeCacheGenerator.MAGIC_HEADER, in.readInt());
            assertEquals(RecipeCacheGenerator.CACHE_VERSION, in.readInt());
            assertFalse(in.readUTF().isEmpty());
            String[] pool = new String[in.readInt()];
            Ingredient[] ingredients = new Ingredient[pool.length];
            Item[] items = new Item[pool.length];
            ResourceLocation[] locations = new ResourceLocation[pool.length];
            for (int i = 0; i < pool.length; i++) {
                pool[i] = in.readUTF();
                ingredients[i] = pool[i].isEmpty() ? Ingredient.EMPTY : Ingredient.of(Items.STONE);
                items[i] = Items.STONE;
            }
            int count = in.readInt();
            assertTrue(count > 0);
            Set<ResourceLocation> ids = new HashSet<>();
            for (int i = 0; i < count; i++) {
                Recipe<?> recipe = BinaryRecipeCache.readRecipe(in, pool, ingredients, items, locations);
                assertNotNull("Bundled recipe record " + i + " was rejected", recipe);
                assertTrue("Duplicate bundled recipe id: " + recipe.getId(), ids.add(recipe.getId()));
            }
            assertEquals(-1, in.read());
            return ids;
        }
    }

    private static InputStream resource(String name) {
        InputStream stream = RecipeIdMigrationTest.class.getResourceAsStream(
                "/data/buildscape/recipes_pack/" + name);
        assertNotNull("Missing bundled resource: " + name, stream);
        return stream;
    }
}
