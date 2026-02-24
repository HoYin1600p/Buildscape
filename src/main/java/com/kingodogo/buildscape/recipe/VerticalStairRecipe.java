package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.block.ModVerticalStairs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;

public class VerticalStairRecipe extends CustomRecipe {

    public VerticalStairRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int width = container.getWidth();
        int height = container.getHeight();

        if (height < 3) return false;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y <= height - 3; y++) {
                ItemStack top = container.getItem(x + y * width);
                ItemStack mid = container.getItem(x + (y + 1) * width);
                ItemStack bot = container.getItem(x + (y + 2) * width);

                if (!top.isEmpty() && !mid.isEmpty() && !bot.isEmpty()) {
                    if (top.getItem() instanceof BlockItem topBi &&
                            mid.getItem() instanceof BlockItem midBi &&
                            bot.getItem() instanceof BlockItem botBi) {

                        Block topBlock = topBi.getBlock();
                        Block midBlock = midBi.getBlock();
                        Block botBlock = botBi.getBlock();

                        if (topBlock instanceof StairBlock &&
                                topBlock == midBlock &&
                                midBlock == botBlock &&
                                ModVerticalStairs.VERTICAL_STAIRS.containsKey(topBlock)) {

                            if (countTotalItems(container) == 3) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private int countTotalItems(CraftingContainer container) {
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!container.getItem(i).isEmpty()) count++;
        }
        return count;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (ModVerticalStairs.VERTICAL_STAIRS.containsKey(block)) {
                    Block vertical = ModVerticalStairs.VERTICAL_STAIRS.get(block);
                    return new ItemStack(vertical, 3);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 1 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.VERTICAL_STAIR_RECIPE.get();
    }
}

