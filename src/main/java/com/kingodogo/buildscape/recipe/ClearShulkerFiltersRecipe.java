package com.kingodogo.buildscape.recipe;

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
        ItemStack filteredShulker = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (!filteredShulker.isEmpty() || !isShulker(stack)) return false;
            filteredShulker = stack;
        }
        return !filteredShulker.isEmpty() && hasFilters(filteredShulker);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && isShulker(stack) && hasFilters(stack)) {
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

    private static boolean hasFilters(ItemStack stack) {
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
        CompoundTag blockEntityTag = stack.getTagElement(BLOCK_ENTITY_TAG);
        if (blockEntityTag == null) return;

        blockEntityTag.remove(FILTERS_TAG);
        if (!blockEntityTag.contains("Items", Tag.TAG_LIST)) return;

        ListTag items = blockEntityTag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = items.size() - 1; i >= 0; i--) {
            CompoundTag item = items.getCompound(i);
            if (item.contains("tag", Tag.TAG_COMPOUND)
                    && item.getCompound("tag").getBoolean("ghost")) {
                items.remove(i);
            }
        }
        blockEntityTag.put("Items", items);
    }
}
