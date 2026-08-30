package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.item.BuildersPouchItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ClearShulkerFiltersRecipe extends CustomRecipe {
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    private static final String FILTERS_TAG = "GhostFilters";

    public ClearShulkerFiltersRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack filteredContainer = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (!filteredContainer.isEmpty() || !isSupportedContainer(stack)) return false;
            filteredContainer = stack;
        }
        return !filteredContainer.isEmpty() && hasFilters(filteredContainer);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && isSupportedContainer(stack) && hasFilters(stack)) {
                ItemStack result = stack.copy();
                result.setCount(1);
                clearFilters(result);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CLEAR_SHULKER_FILTERS_RECIPE.get();
    }

    private static boolean isShulker(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    private static boolean isSupportedContainer(ItemStack stack) {
        return isShulker(stack) || stack.getItem() instanceof BuildersPouchItem;
    }

    private static boolean hasFilters(ItemStack stack) {
        if (stack.getItem() instanceof BuildersPouchItem) return BuildersPouchItem.hasFilters(stack);
        CompoundTag blockEntityTag = stack.getTagElement(BLOCK_ENTITY_TAG);
        if (blockEntityTag == null) return false;

        if (blockEntityTag.contains(FILTERS_TAG, Tag.TAG_LIST)) {
            ListTag filters = blockEntityTag.getList(FILTERS_TAG, Tag.TAG_STRING);
            for (int i = 0; i < filters.size(); i++) {
                if (!filters.getString(i).isEmpty()) return true;
            }
        }

        if (!blockEntityTag.contains("Items", Tag.TAG_LIST)) return false;
        ListTag items = blockEntityTag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag item = items.getCompound(i);
            if (item.contains("tag", Tag.TAG_COMPOUND)
                    && item.getCompound("tag").getBoolean("ghost")) {
                return true;
            }
        }
        return false;
    }

    private static void clearFilters(ItemStack stack) {
        if (stack.getItem() instanceof BuildersPouchItem) {
            BuildersPouchItem.clearFilters(stack);
            return;
        }
        CompoundTag blockEntityTag = stack.getTagElement(BLOCK_ENTITY_TAG);
        if (blockEntityTag == null) return;

        blockEntityTag.remove(FILTERS_TAG);
        if (blockEntityTag.contains("Items", Tag.TAG_LIST)) {
            ListTag items = blockEntityTag.getList("Items", Tag.TAG_COMPOUND);
            for (int i = items.size() - 1; i >= 0; i--) {
                CompoundTag item = items.getCompound(i);
                if (item.contains("tag", Tag.TAG_COMPOUND)
                        && item.getCompound("tag").getBoolean("ghost")) {
                    items.remove(i);
                }
            }
            if (items.isEmpty()) {
                blockEntityTag.remove("Items");
            } else {
                blockEntityTag.put("Items", items);
            }
        }
        if (blockEntityTag.isEmpty()) {
            stack.removeTagKey(BLOCK_ENTITY_TAG);
        }
    }
}
