package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DurabilityRecipeOverflowTest extends BootstrappedGameTest {
    @Test
    public void shapedRecipeBreaksOnOverflowAndAddsNormalDamageExactly() {
        checkDamage(true);
    }

    @Test
    public void shapelessRecipeBreaksOnOverflowAndAddsNormalDamageExactly() {
        checkDamage(false);
    }

    private static void checkDamage(boolean shaped) {
        ItemStack tool = new ItemStack(Items.IRON_PICKAXE);
        tool.setDamageValue(17);
        tool.getOrCreateTag().putString("custom", "preserved");
        CraftingContainer grid = grid(tool);
        ItemStack broken = recipe(shaped, Integer.MAX_VALUE).getRemainingItems(grid).get(0);
        assertTrue("An overflowing damage amount must consume the broken tool", broken.isEmpty());
        assertTrue("The remainder must never expose negative damage", broken.getDamageValue() >= 0);
        assertEquals(17, tool.getDamageValue());

        ItemStack remaining = recipe(shaped, 3).getRemainingItems(grid).get(0);
        assertFalse(remaining.isEmpty());
        assertEquals(Items.IRON_PICKAXE, remaining.getItem());
        assertEquals(20, remaining.getDamageValue());
        assertEquals(1, remaining.getCount());
        assertEquals("preserved", remaining.getTag().getString("custom"));
        assertEquals(17, tool.getDamageValue());
    }

    private static CraftingRecipe recipe(boolean shaped, int damage) {
        ResourceLocation id = new ResourceLocation("buildscape", "test_durability_overflow");
        NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.IRON_PICKAXE));
        ItemStack result = new ItemStack(Items.STICK);
        return shaped
                ? new ShapedDurabilityRecipe(id, "", 1, 1, ingredients, result, damage)
                : new ShapelessDurabilityRecipe(id, "", result, ingredients, damage);
    }

    private static CraftingContainer grid(ItemStack tool) {
        AbstractContainerMenu menu = new AbstractContainerMenu(null, 0) {
            @Override
            public boolean stillValid(Player player) {
                return true;
            }

            @Override
            public ItemStack quickMoveStack(Player player, int slot) {
                return ItemStack.EMPTY;
            }
        };
        CraftingContainer grid = new CraftingContainer(menu, 1, 1);
        grid.setItem(0, tool);
        return grid;
    }

}
