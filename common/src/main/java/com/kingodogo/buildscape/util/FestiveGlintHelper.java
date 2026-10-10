package com.kingodogo.buildscape.util;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class FestiveGlintHelper {

    public static final String TAG_FESTIVE_GLINT = "FestiveGlint";
    public static final String TAG_BUILDCAPE_GLINT = "BuildscapeGlint";
    public static final String TAG_LEGACY_GLINT = "festive_glint";

    private FestiveGlintHelper() {}

    public static boolean hasFestiveGlint(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
        if (tag == null) {
            return false;
        }
        return Services.PLATFORM.getTagBoolean(tag, TAG_FESTIVE_GLINT, false)
                || "festive".equalsIgnoreCase(Services.PLATFORM.getTagString(tag, TAG_BUILDCAPE_GLINT, ""))
                || Services.PLATFORM.getTagBoolean(tag, TAG_LEGACY_GLINT, false);
    }

    public static void applyFestiveGlint(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Services.PLATFORM.updateCustomData(stack, tag -> {
            tag.putBoolean(TAG_FESTIVE_GLINT, true);
            tag.putString(TAG_BUILDCAPE_GLINT, "festive");
        });
    }

    public static boolean isEnchantedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (Services.PLATFORM.isEnchantedItem(stack)) {
            return true;
        }
        return hasFestiveGlint(stack);
    }
}
