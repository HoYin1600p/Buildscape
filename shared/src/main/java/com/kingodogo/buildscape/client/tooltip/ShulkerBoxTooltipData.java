package com.kingodogo.buildscape.client.tooltip;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class ShulkerBoxTooltipData implements TooltipComponent {
    public static final CommonId TEXTURE = new CommonId(BuildscapeCommon.MOD_ID, "textures/gui/shulker_box_tooltip.png");

    public static final int COLOR_UNCOLORED = 0x975DA8;

    public static final Map<DyeColor, Integer> DYE_COLORS = createDyeColors();

    private static Map<DyeColor, Integer> createDyeColors() {
        Map<DyeColor, Integer> map = new EnumMap<>(DyeColor.class);
        map.put(DyeColor.WHITE,      0xD7D7D7);
        map.put(DyeColor.ORANGE,     0xD7601F);
        map.put(DyeColor.MAGENTA,    0xAB329F);
        map.put(DyeColor.LIGHT_BLUE, 0x2999C6);
        map.put(DyeColor.YELLOW,     0xE1A119);
        map.put(DyeColor.LIME,       0x54A411);
        map.put(DyeColor.PINK,       0xF1759A);
        map.put(DyeColor.GRAY,       0x34383B);
        map.put(DyeColor.LIGHT_GRAY, 0x69695F);
        map.put(DyeColor.CYAN,       0x12707E);
        map.put(DyeColor.PURPLE,     0x5E1F99);
        map.put(DyeColor.BLUE,       0x282B88);
        map.put(DyeColor.BROWN,      0x5D3A21);
        map.put(DyeColor.GREEN,      0x4C6514);
        map.put(DyeColor.RED,        0x891E1C);
        map.put(DyeColor.BLACK,      0x1E1E23);
        return map;
    }

    public static int getHexColor(@Nullable DyeColor dye) {
        if (dye == null) return COLOR_UNCOLORED;
        return DYE_COLORS.getOrDefault(dye, COLOR_UNCOLORED);
    }

    public static float[] getTint(@Nullable DyeColor dye) {
        return hexToRgb(getHexColor(dye));
    }

    public static float[] hexToRgb(int hex) {
        float r = ((hex >> 16) & 0xFF) / 255.0f;
        float g = ((hex >> 8) & 0xFF) / 255.0f;
        float b = (hex & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }

    private final NonNullList<ItemStack> filterStacks;
    private final NonNullList<ItemStack> realStacks;
    @Nullable
    private final DyeColor color;

    public ShulkerBoxTooltipData(NonNullList<ItemStack> filterStacks, NonNullList<ItemStack> realStacks, @Nullable DyeColor color) {
        this.filterStacks = filterStacks;
        this.realStacks = realStacks;
        this.color = color;
    }

    public NonNullList<ItemStack> getFilterStacks() {
        return filterStacks;
    }

    public NonNullList<ItemStack> getRealStacks() {
        return realStacks;
    }

    @Nullable
    public DyeColor getColor() {
        return color;
    }

    public int getHeight() {
        return 3 * 18 + 14;
    }

    public int getWidth() {
        return 9 * 18 + 14;
    }

    public int getWidth(net.minecraft.client.gui.Font font) {
        return getWidth();
    }
}
