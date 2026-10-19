package com.kingodogo.buildscape.item;

import net.minecraft.nbt.CompoundTag;

/** The data change behind the biome brush's sneak + left-click, kept free of items and players. */
public final class BiomeBrushClear {
    private BiomeBrushClear() {
    }

    /**
     * Removes the captured biome and, when {@code clearSelection} is set, both corners and their
     * dimension keys. A null tag (a brush that never stored anything) is left alone.
     */
    public static void apply(CompoundTag tag, boolean clearSelection) {
        if (tag == null) return;
        tag.remove(BiomeBrushItem.KEY_BIOME);
        if (clearSelection) {
            tag.remove(BiomeBrushItem.KEY_POS1);
            tag.remove(BiomeBrushItem.KEY_POS2);
            tag.remove(BiomeBrushItem.KEY_POS1_DIMENSION);
            tag.remove(BiomeBrushItem.KEY_POS2_DIMENSION);
        }
    }

    public static String messageKey(boolean clearSelection) {
        return clearSelection
                ? "message.buildscape.biome_brush.selection_cleared"
                : "message.buildscape.biome_brush.cleared";
    }

    public static String tooltipKey(boolean clearSelection) {
        return clearSelection
                ? "tooltip.buildscape.biome_brush.desc4_selection"
                : "tooltip.buildscape.biome_brush.desc4_biome";
    }
}
