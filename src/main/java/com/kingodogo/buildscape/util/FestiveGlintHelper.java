package com.kingodogo.buildscape.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class FestiveGlintHelper {

    public static final String TAG_FESTIVE_GLINT = "FestiveGlint";
    public static final String TAG_BUILDCAPE_GLINT = "BuildscapeGlint";
    public static final String TAG_LEGACY_GLINT = "festive_glint";

    private FestiveGlintHelper() {
    }

    public static boolean hasFestiveGlint(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        return tag.getBoolean(TAG_FESTIVE_GLINT)
                || "festive".equalsIgnoreCase(tag.getString(TAG_BUILDCAPE_GLINT))
                || tag.getBoolean(TAG_LEGACY_GLINT);
    }

    public static void applyFestiveGlint(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(TAG_FESTIVE_GLINT, true);
        tag.putString(TAG_BUILDCAPE_GLINT, "festive");
    }

    public static boolean isEnchantedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.isEnchanted()) {
            return true;
        }
        if (!EnchantmentHelper.getEnchantments(stack).isEmpty()) {
            return true;
        }
        if (stack.getItem() instanceof EnchantedBookItem && !EnchantedBookItem.getEnchantments(stack).isEmpty()) {
            return true;
        }
        if (stack.hasFoil()) {
            return true;
        }
        return hasFestiveGlint(stack);
    }
}
