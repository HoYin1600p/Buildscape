package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.item.ItemStack;

import java.util.Random;
public final class DurabilityRecipeLogic {
    private DurabilityRecipeLogic() {}

    public static ItemStack getRemainingItem(ItemStack stack, int damageAmount) {
        if (stack == null || stack.isEmpty()) return ItemStack.EMPTY;

        if ((stack.isDamageableItem() || stack.getMaxDamage() > 0)
                && !Services.PLATFORM.isArmorItem(stack)) {
            ItemStack damagedStack = stack.copy();
            int unbreakingLevel = Services.PLATFORM.getUnbreakingLevel(damagedStack);
            if (unbreakingLevel > 0
                    && new Random().nextFloat() >= 1.0f / (unbreakingLevel + 1.0f)) {
                return damagedStack;
            }

            int newDamage = damagedStack.getDamageValue() + damageAmount;
            damagedStack.setDamageValue(newDamage);
            return newDamage < damagedStack.getMaxDamage() ? damagedStack : ItemStack.EMPTY;
        }

        return Services.PLATFORM.getCraftingRemainingItem(stack);
    }
}
