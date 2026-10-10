package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import com.kingodogo.buildscape.item.FestiveStockingItem;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public final class CustomFireworkRecipeLogic {
    private CustomFireworkRecipeLogic() {}

    public static boolean matches(int size, IntFunction<ItemStack> itemAt) {
        int gunpowder = 0, shapeItems = 0, dyes = 0;
        boolean fixed = false;
        for (int i = 0; i < size; i++) {
            ItemStack stack = itemAt.apply(i);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.GUNPOWDER)) gunpowder++;
            else if (isFixedShape(stack)) { shapeItems++; fixed = true; }
            else if (isDyeableShape(stack)) shapeItems++;
            else if (stack.getItem() instanceof DyeItem) dyes++;
            else return false;
        }
        return !(fixed && dyes > 0) && gunpowder == 1 && shapeItems == 1;
    }

    public static ItemStack assemble(int size, IntFunction<ItemStack> itemAt) {
        byte shape = -1;
        boolean fixed = false;
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ItemStack stack = itemAt.apply(i);
            if (stack.is(Items.CAKE)) { shape = CustomFireworkShapeRegistry.CAKE_ID; fixed = true; }
            else if (stack.is(Items.GOLD_INGOT)) { shape = CustomFireworkShapeRegistry.CROWN_ID; fixed = true; }
            else if (stack.is(Items.SUNFLOWER)) { shape = CustomFireworkShapeRegistry.TROPHY_ID; fixed = true; }
            else if (stack.is(Items.SPRUCE_SAPLING)) { shape = CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID; fixed = true; }
            else if (isStocking(stack)) shape = CustomFireworkShapeRegistry.PRESENTS_ID;
            else if (stack.is(Items.SUGAR_CANE) || stack.is(Items.SUGAR)) shape = CustomFireworkShapeRegistry.CANDY_CANE_ID;
            else if (stack.is(ModItems.FROST_ROSE.get().asItem())) { shape = CustomFireworkShapeRegistry.SNOWFLAKE_ID; fixed = true; }
            else if (stack.getItem() instanceof DyeItem) colors.add(Services.PLATFORM.getDyeFireworkColor(stack));
        }
        if (shape < 0 || fixed && !colors.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = new ItemStack(Items.FIREWORK_STAR);
        Services.PLATFORM.configureFireworkStar(result, shape, resolvedColors(shape, colors), false, false);
        return result;
    }

    private static boolean isFixedShape(ItemStack stack) {
        return stack.is(Items.CAKE) || stack.is(Items.GOLD_INGOT) || stack.is(Items.SUNFLOWER)
                || stack.is(Items.SPRUCE_SAPLING) || stack.is(ModItems.FROST_ROSE.get().asItem());
    }

    private static boolean isDyeableShape(ItemStack stack) {
        return isStocking(stack) || stack.is(Items.SUGAR_CANE) || stack.is(Items.SUGAR);
    }

    private static boolean isStocking(ItemStack stack) {
        if (!(stack.getItem() instanceof FestiveStockingItem)) return false;
        CompoundTag root = Services.PLATFORM.getCustomData(stack, false);
        if (root == null || !root.contains("StoredItem")) return true;
        CompoundTag stored = Services.PLATFORM.getTagCompound(root, "StoredItem");
        return stored == null || stored.isEmpty();
    }

    private static int[] resolvedColors(byte shape, List<Integer> colors) {
        if (!colors.isEmpty()) return colors.stream().mapToInt(Integer::intValue).toArray();
        if (shape == CustomFireworkShapeRegistry.CAKE_ID) return new int[]{0xFFFDD0, 0x8B4513, 0xFF2D55};
        if (shape == CustomFireworkShapeRegistry.CROWN_ID) return new int[]{0xFFD700, 0xFFFF77, 0xFF0044};
        if (shape == CustomFireworkShapeRegistry.TROPHY_ID) return new int[]{0xFFD700, 0xFFFF88, 0x00FFFF};
        if (shape == CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID) return new int[]{0x227733, 0xFF3030, 0xFFD700};
        if (shape == CustomFireworkShapeRegistry.PRESENTS_ID) return new int[]{0xFF2233, 0xFFD700};
        if (shape == CustomFireworkShapeRegistry.CANDY_CANE_ID) return new int[]{0xFF0033};
        if (shape == CustomFireworkShapeRegistry.SNOWFLAKE_ID) return new int[]{0xFFFFFF, 0xE0F7FF, 0x5AC8FF};
        return new int[0];
    }
}
