package com.kingodogo.buildscape.recipe.framework.cache;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.recipe.HazeBushDyeRecipe;

import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HazeBushDyeRecipeTest extends BootstrappedGameTest {

    private HazeBushDyeRecipe recipe() {
        // Vanilla stand-ins keep these unit tests independent of mod registry startup.
        return new HazeBushDyeRecipe(new ResourceLocation("buildscape", "test_haze_dye"), "",
                new ItemStack(Items.ORANGE_TULIP), NonNullList.of(Ingredient.EMPTY,
                        Ingredient.of(Items.WHITE_TULIP), Ingredient.of(Items.ORANGE_DYE)));
    }

    private CraftingContainer grid(ItemStack bush) {
        AbstractContainerMenu menu = new AbstractContainerMenu(null, 0) {
            @Override
            public boolean stillValid(net.minecraft.world.entity.player.Player player) {
                return true;
            }

            @Override
            public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int slot) {
                return ItemStack.EMPTY;
            }
        };
        CraftingContainer grid = new CraftingContainer(menu, 2, 2);
        // Reverse slot order to check the recipe ingredient, not crafting slot order.
        grid.setItem(0, new ItemStack(Items.ORANGE_DYE));
        grid.setItem(3, bush);
        return grid;
    }

    @Test
    public void drainedStateIsCopiedWithoutAliasingAndFullBushStaysUntagged() {
        HazeBushDyeRecipe recipe = recipe();
        ItemStack bush = new ItemStack(Items.WHITE_TULIP);
        CompoundTag state = new CompoundTag();
        state.putString("has_haze", "false");
        bush.addTagElement("BlockStateTag", state);
        bush.getOrCreateTag().putBoolean("HasHaze", false);
        bush.getOrCreateTag().putString("unrelated", "leave behind");
        ItemStack dyed = recipe.assemble(grid(bush));
        assertEquals(Items.ORANGE_TULIP, dyed.getItem());
        assertEquals("false", dyed.getTagElement("BlockStateTag").getString("has_haze"));
        assertFalse(dyed.getTag().getBoolean("HasHaze"));
        assertTrue(dyed.getTag().contains("HasHaze"));
        assertFalse(dyed.getTag().contains("unrelated"));
        dyed.getTagElement("BlockStateTag").putString("has_haze", "true");
        assertEquals("false", state.getString("has_haze"));
        assertFalse(recipe.getResultItem().hasTag());
        assertFalse(recipe.assemble(grid(new ItemStack(Items.WHITE_TULIP))).hasTag());
    }

    @Test
    public void eitherStateTagSurvivesIndependently() {
        for (String nbt : List.of("{BlockStateTag:{has_haze:\"false\"}}", "{HasHaze:0b}")) {
            ItemStack bush = new ItemStack(Items.WHITE_TULIP);
            try {
                bush.setTag(net.minecraft.nbt.TagParser.parseTag(nbt));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
            assertEquals(bush.getTag(), recipe().assemble(grid(bush)).getTag());
        }
    }

    @Test
    public void cacheAndNetworkRetainRecipeBehavior() throws Exception {
        HazeBushDyeRecipe recipe = recipe();
        byte[] cache = BinaryRecipeCache.serializeCache("test", List.of(recipe));
        try (var in = new java.io.DataInputStream(new ByteArrayInputStream(cache))) {
            in.readInt(); // magic
            in.readInt(); // version
            assertEquals("test", in.readUTF());
            String[] pool = new String[in.readInt()];
            for (int i = 0; i < pool.length; i++) {
                pool[i] = in.readUTF();
            }
            assertEquals(1, in.readInt());
            in.mark(cache.length);
            in.readInt(); // id
            in.readInt(); // group
            int resultIndex = in.readInt();
            in.readInt(); // count
            in.readInt(); // result NBT
            assertEquals(13, in.readByte());
            in.reset();
            // Match the existing cache regression tests: pre-resolve items outside Forge startup.
            var items = new net.minecraft.world.item.Item[pool.length];
            items[resultIndex] = Items.ORANGE_TULIP;
            Ingredient[] ingredients = new Ingredient[pool.length];
            for (int i = 0; i < pool.length; i++) {
                if (pool[i].contains("minecraft:white_tulip")) {
                    ingredients[i] = Ingredient.of(Items.WHITE_TULIP);
                }
                if (pool[i].contains("minecraft:orange_dye")) {
                    ingredients[i] = Ingredient.of(Items.ORANGE_DYE);
                }
            }
            assertTrue(BinaryRecipeCache.readRecipe(in, pool, ingredients, items,
                    new ResourceLocation[pool.length]) instanceof HazeBushDyeRecipe);
        }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            HazeBushDyeRecipe.SERIALIZER.toNetwork(buffer, recipe);
            HazeBushDyeRecipe decoded = HazeBushDyeRecipe.SERIALIZER.fromNetwork(recipe.getId(), buffer);
            assertEquals(recipe.getIngredients().size(), decoded.getIngredients().size());
            assertEquals(recipe.getResultItem().getItem(), decoded.getResultItem().getItem());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void allFifteenColorsUseStatePreservingRecipeWithBushFirst() throws Exception {
        var pack = JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/data/buildscape/recipes_pack/crafting.json"))).getAsJsonObject();
        int count = 0;
        for (var entry : pack.getAsJsonArray("crafting_shapeless")) {
            if (!entry.isJsonObject()) {
                continue;
            }
            var json = entry.getAsJsonObject();
            if (!json.has("type") || !"buildscape:haze_bush_dye".equals(json.get("type").getAsString())) {
                continue;
            }
            var ingredients = json.getAsJsonArray("ingredients");
            assertEquals("BS:white_haze_bush", ingredients.get(0).getAsString());
            String color = ingredients.get(1).getAsString().replace("MC:", "").replace("_dye", "");
            assertEquals("BS:" + color + "_haze_bush", json.getAsJsonObject("result").get("item").getAsString());
            assertEquals(1, json.getAsJsonObject("result").get("count").getAsInt());
            count++;
        }
        assertEquals(15, count);
    }
}
