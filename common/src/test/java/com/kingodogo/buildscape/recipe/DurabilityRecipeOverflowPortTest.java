package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.TestBootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DurabilityRecipeOverflowPortTest {
    @BeforeAll
    static void bootstrap() { TestBootstrap.initialize(); }

    @Test
    void overflowingCostBreaksToolWithoutChangingCraftingInput() {
        ItemStack tool = new ItemStack(Items.IRON_PICKAXE);
        tool.setDamageValue(17);
        assertTrue(DurabilityRecipeLogic.getRemainingItem(tool, Integer.MAX_VALUE).isEmpty());
        assertEquals(17, tool.getDamageValue());
        assertEquals(1, tool.getCount());
    }

    @Test
    void ordinaryCostReturnsDamagedCopyAndBreaksAtTheLimit() {
        ItemStack tool = new ItemStack(Items.IRON_PICKAXE);
        tool.setDamageValue(17);
        ItemStack remaining = DurabilityRecipeLogic.getRemainingItem(tool, 3);
        assertFalse(remaining.isEmpty());
        assertEquals(20, remaining.getDamageValue());
        assertEquals(tool.getItem(), remaining.getItem());
        assertEquals(17, tool.getDamageValue());
        assertTrue(DurabilityRecipeLogic.getRemainingItem(tool, tool.getMaxDamage() - 17).isEmpty());
    }
}
