package com.kingodogo.buildscape.item;

import net.minecraft.nbt.CompoundTag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class BiomeBrushClearTest {
    private static CompoundTag fullTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString(BiomeBrushItem.KEY_BIOME, "minecraft:plains");
        tag.put(BiomeBrushItem.KEY_POS1, new CompoundTag());
        tag.put(BiomeBrushItem.KEY_POS2, new CompoundTag());
        tag.putString(BiomeBrushItem.KEY_POS1_DIMENSION, "minecraft:overworld");
        tag.putString(BiomeBrushItem.KEY_POS2_DIMENSION, "minecraft:overworld");
        tag.putInt("Damage", 5);
        return tag;
    }

    @Test
    public void biomeOnlyKeepsBothCornersAndTheirDimensions() {
        CompoundTag tag = fullTag();
        BiomeBrushClear.apply(tag, false);
        assertFalse(tag.contains(BiomeBrushItem.KEY_BIOME));
        assertTrue(tag.contains(BiomeBrushItem.KEY_POS1));
        assertTrue(tag.contains(BiomeBrushItem.KEY_POS2));
        assertTrue(tag.contains(BiomeBrushItem.KEY_POS1_DIMENSION));
        assertTrue(tag.contains(BiomeBrushItem.KEY_POS2_DIMENSION));
        assertEquals(5, tag.getInt("Damage"));
    }

    @Test
    public void selectionModeClearsBiomeCornersAndDimensionsOnly() {
        CompoundTag tag = fullTag();
        BiomeBrushClear.apply(tag, true);
        assertFalse(tag.contains(BiomeBrushItem.KEY_BIOME));
        assertFalse(tag.contains(BiomeBrushItem.KEY_POS1));
        assertFalse(tag.contains(BiomeBrushItem.KEY_POS2));
        assertFalse(tag.contains(BiomeBrushItem.KEY_POS1_DIMENSION));
        assertFalse(tag.contains(BiomeBrushItem.KEY_POS2_DIMENSION));
        assertEquals(5, tag.getInt("Damage"));
    }

    @Test
    public void missingTagAndEmptyTagAreSafe() {
        BiomeBrushClear.apply(null, true);
        BiomeBrushClear.apply(null, false);
        CompoundTag empty = new CompoundTag();
        BiomeBrushClear.apply(empty, true);
        assertTrue(empty.isEmpty());
    }

    @Test
    public void textFollowsTheSetting() {
        assertNotEquals(BiomeBrushClear.messageKey(true), BiomeBrushClear.messageKey(false));
        assertNotEquals(BiomeBrushClear.tooltipKey(true), BiomeBrushClear.tooltipKey(false));
        assertEquals("message.buildscape.biome_brush.cleared", BiomeBrushClear.messageKey(false));
        assertEquals("message.buildscape.biome_brush.selection_cleared", BiomeBrushClear.messageKey(true));
    }
}
