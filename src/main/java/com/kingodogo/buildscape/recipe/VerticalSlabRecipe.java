package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.block.ModVerticalSlabs;
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
        int slabCount = 0;
        ItemStack slabStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem) stack.getItem()).getBlock();

                    // Check if it's a slab that has a vertical version
                    if (block instanceof SlabBlock && ModVerticalSlabs.VERTICAL_SLABS.containsKey(block)) {
                        if (slabStack.isEmpty()) {
                            slabStack = stack;
                        } else if (stack.getItem() != slabStack.getItem()) {
                            return false; // Different slabs mixed
                        }
                        slabCount++;
                    } else {
                        return false; // Not a compatible slab
                    }
                } else {
                    return false; // Not a block
                }
            }
        }

        // Need exactly 3 slabs in a vertical line? 
        // Or specific shape?
        // Standard vanilla vertical slab recipes (from mods like quark) often use 3 slabs in a vertical column.
        // Let's implement full check logic in matches or assume it's shapeless? 
        // CustomRecipe usually implies special logic.
        // If we want a shapeless recipe of 1 slab -> 1 vertical slab (maybe?), or 3->3.
        // Let's assume the user wants: 3 normal slabs vertically = 3 vertical slabs.

        // Wait, CustomRecipe is usually for complex NBT stuff. 
        // If it's just a shape, we should use a ShapedRecipe json.
        // BUT, since we have many slabs, we want a dynamic recipe that works for ALL of them.

        // Let's implement: 3 same slabs in a vertical column = 3 vertical slabs.
        if (slabCount != 3) return false;

        // Check positioning: 0, 3, 6 (left col), or 1, 4, 7 (mid), or 2, 5, 8 (right)
        // We need to find the column.

        // Simplification: ANY 3 vertical slabs.
        // Actually, let's just use shapeless: 1 slab -> 1 vertical slab? No, that conflicts with placement.
        // Usually it's 3 vertical.

        int width = container.getWidth();
        int height = container.getHeight();

        for (int x = 0; x < width; x++) {
            // Check if column x has 3 items
            boolean colMatch = true;
            ItemStack first = ItemStack.EMPTY;
            for (int y = 0; y < height; y++) {
                // 3x3 grid indices: x + y * width
                // But CraftingContainer access is flat index?
                // No, container.getItem(x + y * width).

                // Wait, CraftingContainer structure depends on menu.
                // Let's just scan generic.

                // For 3x3:
                // If we find a column of 3, return true.
            }
        }

        // Actually simplest implementation for "Vertical Slab" dynamic recipe:
        // Input: 2 identical slabs in a vertical strip.
        // Output: 2 vertical slabs.

        // Scan all 3 columns
        for (int col = 0; col < 3; col++) {
            ItemStack i1 = container.getItem(col);
            ItemStack i2 = container.getItem(col + 3);
            ItemStack i3 = container.getItem(col + 2 * 3);

            // Check top 2
            if (!i1.isEmpty() && !i2.isEmpty() && i3.isEmpty()) {
                if (i1.getItem() == i2.getItem() && i1.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SlabBlock) {
                    Block block = bi.getBlock();
                    if (ModVerticalSlabs.VERTICAL_SLABS.containsKey(block) && countTotalItems(container) == 2)
                        return true;
                }
            }
            // Check bottom 2
            if (i1.isEmpty() && !i2.isEmpty() && !i3.isEmpty()) {
                if (i2.getItem() == i3.getItem() && i2.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SlabBlock) {
                    Block block = bi.getBlock();
                    if (ModVerticalSlabs.VERTICAL_SLABS.containsKey(block) && countTotalItems(container) == 2)
                        return true;
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
            if (!stack.isEmpty()) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                Block vertical = ModVerticalSlabs.VERTICAL_SLABS.get(block);
                return new ItemStack(vertical, 2);
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
        return ModRecipeSerializers.VERTICAL_SLAB_RECIPE.get();
    }
}
