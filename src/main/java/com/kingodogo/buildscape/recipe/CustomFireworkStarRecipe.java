package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import com.kingodogo.buildscape.item.FestiveStockingItem;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CustomFireworkStarRecipe extends CustomRecipe {

    public CustomFireworkStarRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int gunpowderCount = 0;
        int shapeItemCount = 0;
        int dyeCount = 0;
        boolean isFixedPalette = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(Items.GUNPOWDER)) {
                    gunpowderCount++;
                } else if (isFixedPaletteShapeItem(stack)) {
                    shapeItemCount++;
                    isFixedPalette = true;
                } else if (isDyeableShapeItem(stack)) {
                    shapeItemCount++;
                } else if (stack.getItem() instanceof DyeItem) {
                    dyeCount++;
                } else {
                    return false;
                }
            }
        }

        if (isFixedPalette && dyeCount > 0) {
            return false;
        }

        return gunpowderCount == 1 && shapeItemCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        byte shapeId = -1;
        List<Integer> colors = new ArrayList<>();
        boolean isFixedPalette = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(Items.CAKE)) {
                    shapeId = CustomFireworkShapeRegistry.CAKE_ID;
                    isFixedPalette = true;
                } else if (stack.is(Items.GOLD_INGOT)) {
                    shapeId = CustomFireworkShapeRegistry.CROWN_ID;
                    isFixedPalette = true;
                } else if (stack.is(Items.SUNFLOWER)) {
                    shapeId = CustomFireworkShapeRegistry.TROPHY_ID;
                    isFixedPalette = true;
                } else if (stack.is(Items.SPRUCE_SAPLING)) {
                    shapeId = CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID;
                    isFixedPalette = true;
                } else if (isStockingItem(stack)) {
                    shapeId = CustomFireworkShapeRegistry.PRESENTS_ID;
                } else if (stack.is(Items.SUGAR_CANE) || stack.is(Items.SUGAR)) {
                    shapeId = CustomFireworkShapeRegistry.CANDY_CANE_ID;
                } else if (stack.is(ModItems.FROST_ROSE.get()) || stack.is(ModBlocks.FROST_ROSE.get().asItem())) {
                    shapeId = CustomFireworkShapeRegistry.SNOWFLAKE_ID;
                    isFixedPalette = true;
                } else if (stack.getItem() instanceof DyeItem dye) {
                    colors.add(dye.getDyeColor().getFireworkColor());
                }
            }
        }

        if (shapeId == -1 || (isFixedPalette && !colors.isEmpty())) {
            return ItemStack.EMPTY;
        }

        if (colors.isEmpty()) {
            if (shapeId == CustomFireworkShapeRegistry.CAKE_ID) {
                colors.add(0xFFFDD0);
                colors.add(0x8B4513);
                colors.add(0xFF2D55);
            } else if (shapeId == CustomFireworkShapeRegistry.CROWN_ID) {
                colors.add(0xFFD700);
                colors.add(0xFFFF77);
                colors.add(0xFF0044);
            } else if (shapeId == CustomFireworkShapeRegistry.TROPHY_ID) {
                colors.add(0xFFD700);
                colors.add(0xFFFF88);
                colors.add(0x00FFFF);
            } else if (shapeId == CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID) {
                colors.add(0x227733);
                colors.add(0xFF3030);
                colors.add(0xFFD700);
            } else if (shapeId == CustomFireworkShapeRegistry.PRESENTS_ID) {
                colors.add(0xFF2233);
                colors.add(0xFFD700);
            } else if (shapeId == CustomFireworkShapeRegistry.CANDY_CANE_ID) {
                colors.add(0xFF0033);
            } else if (shapeId == CustomFireworkShapeRegistry.SNOWFLAKE_ID) {
                colors.add(0xFFFFFF);
                colors.add(0xE0F7FF);
                colors.add(0x5AC8FF);
            }
        }

        ItemStack result = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag explosionTag = new CompoundTag();
        explosionTag.putByte("Type", shapeId);
        explosionTag.putIntArray("Colors", colors.stream().mapToInt(Integer::intValue).toArray());
        result.getOrCreateTag().put("Explosion", explosionTag);

        return result;
    }

    private static boolean isFixedPaletteShapeItem(ItemStack stack) {
        return stack.is(Items.CAKE) || stack.is(Items.GOLD_INGOT) || stack.is(Items.SUNFLOWER)
                || stack.is(Items.SPRUCE_SAPLING)
                || stack.is(ModItems.FROST_ROSE.get()) || stack.is(ModBlocks.FROST_ROSE.get().asItem());
    }

    private static boolean isDyeableShapeItem(ItemStack stack) {
        return isStockingItem(stack) || stack.is(Items.SUGAR_CANE) || stack.is(Items.SUGAR);
    }

    private static boolean isShapeItem(ItemStack stack) {
        return isFixedPaletteShapeItem(stack) || isDyeableShapeItem(stack);
    }

    private static boolean isStockingItem(ItemStack stack) {
        return stack.getItem() instanceof FestiveStockingItem || stack.getItem().getDescriptionId().contains("stocking");
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CUSTOM_FIREWORK_STAR_RECIPE.get();
    }
}
