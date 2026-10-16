package com.kingodogo.buildscape.client.tooltip;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BuildersPouchTooltipData implements TooltipComponent {
    public static final CommonId TEXTURE = new CommonId(BuildscapeCommon.MOD_ID, "textures/gui/shulker_box_tooltip.png");

    public static final int COLOR_GOLD = 0xFBC02D;

    public static float[] hexToRgb(int hex) {
        float r = ((hex >> 16) & 0xFF) / 255.0f;
        float g = ((hex >> 8) & 0xFF) / 255.0f;
        float b = (hex & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }

    private final NonNullList<ItemStack> filterStacks;
    private final NonNullList<ItemStack> realStacks;

    public BuildersPouchTooltipData(NonNullList<ItemStack> filterStacks, NonNullList<ItemStack> realStacks) {
        this.filterStacks = filterStacks;
        this.realStacks = realStacks;
    }

    public NonNullList<ItemStack> getFilterStacks() {
        return filterStacks;
    }

    public NonNullList<ItemStack> getRealStacks() {
        return realStacks;
    }

    public int getHeight() {
        return 1 * 18 + 14;
    }

    public int getWidth() {
        return 9 * 18 + 14;
    }

    public int getWidth(net.minecraft.client.gui.Font font) {
        return getWidth();
    }
}
