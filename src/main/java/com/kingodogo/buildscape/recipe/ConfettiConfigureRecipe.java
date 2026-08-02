package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ConfettiConfigureRecipe extends CustomRecipe {
    public ConfettiConfigureRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int confettiCount = 0;
        int gunpowderCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItems.CONFETTI_ITEM.get())) {
                    confettiCount++;
                } else if (stack.is(Items.GUNPOWDER)) {
                    gunpowderCount++;
                } else {
                    return false;
                }
            }
        }

        return confettiCount == 1 && gunpowderCount >= 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack confettiStack = ItemStack.EMPTY;
        int gunpowderCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItems.CONFETTI_ITEM.get())) {
                    confettiStack = stack;
                } else if (stack.is(Items.GUNPOWDER)) {
                    gunpowderCount++;
                }
            }
        }

        if (confettiStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int currentLevel = 1;
        if (confettiStack.hasTag() && confettiStack.getTag().contains("BurstLevel")) {
            currentLevel = confettiStack.getTag().getInt("BurstLevel");
        }

        int nextLevel = Math.min(5, currentLevel + gunpowderCount);
        ItemStack result = confettiStack.copy();
        result.setCount(1);
        result.getOrCreateTag().putInt("BurstLevel", nextLevel);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CONFETTI_CONFIGURE_RECIPE.get();
    }
}
