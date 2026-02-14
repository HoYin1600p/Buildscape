package com.kingodogo.buildscape.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

public class VerticalSlabRecipe extends CustomRecipe {
    public VerticalSlabRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack slabStack = ItemStack.EMPTY;
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock) {
                    slabStack = stack;
                    count++;
                } else {
                    return false; // Found non-slab item
                }
            }
        }

        return count == 1 && !slabStack.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack slabStack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                slabStack = stack;
                break;
            }
        }

        if (slabStack.isEmpty() || !(slabStack.getItem() instanceof BlockItem blockItem)) {
            return ItemStack.EMPTY;
        }

        Block slabBlock = blockItem.getBlock();
        Block verticalSlab = com.kingodogo.buildscape.block.ModVerticalSlabs.VERTICAL_SLABS.get(slabBlock);

        if (verticalSlab != null) {
            return new ItemStack(verticalSlab);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.VERTICAL_SLAB_RECIPE.get();
    }
}
