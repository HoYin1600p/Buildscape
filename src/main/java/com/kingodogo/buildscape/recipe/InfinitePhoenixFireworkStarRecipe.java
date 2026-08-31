package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class InfinitePhoenixFireworkStarRecipe extends CustomRecipe {

    public InfinitePhoenixFireworkStarRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int gunpowderCount = 0;
        int paperCount = 0;
        int starCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(Items.GUNPOWDER)) {
                    gunpowderCount++;
                } else if (stack.is(Items.PAPER)) {
                    paperCount++;
                } else if (stack.is(ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get())) {
                    starCount++;
                } else {
                    return false;
                }
            }
        }

        return gunpowderCount >= 1 && gunpowderCount <= 3 && paperCount == 1 && starCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        int flight = 1;
        ItemStack starStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(Items.GUNPOWDER)) {
                    flight++;
                } else if (stack.is(ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get())) {
                    starStack = stack;
                }
            }
        }

        flight = Math.min(flight - 1, 3);

        ItemStack result = new ItemStack(Items.FIREWORK_ROCKET, 3);
        CompoundTag fireworksTag = result.getOrCreateTagElement("Fireworks");
        ListTag explosionsList = new ListTag();

        CompoundTag explosionTag = new CompoundTag();
        explosionTag.putByte("Type", CustomFireworkShapeRegistry.PHOENIX_ID);

        int[] colors = new int[]{0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00};
        if (!starStack.isEmpty() && starStack.hasTag()) {
            CompoundTag starExp = starStack.getTagElement("Explosion");
            if (starExp != null && starExp.contains("Colors")) {
                colors = starExp.getIntArray("Colors");
            }
        }
        explosionTag.putIntArray("Colors", colors);
        explosionTag.putBoolean("Flicker", true);
        explosionTag.putBoolean("Trail", true);

        explosionsList.add(explosionTag);
        fireworksTag.put("Explosions", explosionsList);
        fireworksTag.putByte("Flight", (byte) flight);

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.is(ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get())) {
                remaining.set(i, stack.copy());
            }
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.INFINITE_PHOENIX_FIREWORK_STAR_RECIPE.get();
    }
}
