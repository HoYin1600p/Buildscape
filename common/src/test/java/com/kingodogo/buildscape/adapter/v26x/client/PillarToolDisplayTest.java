package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.client.renderer.PillarItemDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PillarToolDisplayTest {
    @BeforeAll static void bootstrap() { TestBootstrap.initialize(); }

    @Test void referenceKeywordsRequireTheRightDisplayType() {
        assertEquals(PillarItemDisplay.Mode.ITEM, PillarItemDisplay.parse(" \u00a7aItEm ", false));
        assertEquals(PillarItemDisplay.Mode.DEFAULT, PillarItemDisplay.parse("item sword", true));
        assertEquals(PillarItemDisplay.Mode.FIXED, PillarItemDisplay.parse("My FIXED sword", true));
        assertEquals(PillarItemDisplay.Mode.DEFAULT, PillarItemDisplay.parse("fixed", false));
        assertEquals(PillarItemDisplay.Mode.DEFAULT, PillarItemDisplay.parse(null, true));
        // These are not tool keywords in the frozen reference: spin is a mob state.
        assertEquals(PillarItemDisplay.Mode.DEFAULT, PillarItemDisplay.parse("spin", true));
        assertEquals(PillarItemDisplay.Mode.DEFAULT, PillarItemDisplay.parse("stuck", true));
    }

    @Test void removedToolSubclassesHaveEquivalent26xClassification() {
        for (var item : new net.minecraft.world.item.Item[] {
                Items.DIAMOND_SWORD, Items.TRIDENT, Items.DIAMOND_SHOVEL}) {
            var stack = new ItemStack(item);
            assertTrue(PillarToolDisplay.swordLike(stack));
            assertFalse(PillarToolDisplay.axeLike(stack));
        }
        for (var item : new net.minecraft.world.item.Item[] {
                Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE, Items.DIAMOND_HOE}) {
            var stack = new ItemStack(item);
            assertTrue(PillarToolDisplay.axeLike(stack));
            assertFalse(PillarToolDisplay.swordLike(stack));
        }
        for (var item : new net.minecraft.world.item.Item[] {
                Items.STONE, Items.DIAMOND_HELMET, Items.ELYTRA, Items.BOW}) {
            var stack = new ItemStack(item);
            assertFalse(PillarToolDisplay.swordLike(stack));
            assertFalse(PillarToolDisplay.axeLike(stack));
        }
        assertFalse(PillarToolDisplay.swordLike(ItemStack.EMPTY));
        assertFalse(PillarToolDisplay.axeLike(null));
    }

    @Test void fixedToolsUseReferenceAnglesScaleAndOffsets() {
        var sword = PillarItemDisplay.fixedTool(true, 0.6, 0.6);
        assertEquals(-0.5, sword.translateY(), 1e-9);
        assertEquals(135, sword.rotateZ());
        assertEquals(0.8F, sword.scale());
        var axe = PillarItemDisplay.fixedTool(false, 0.6, 0.6);
        assertEquals(-0.55, axe.translateY(), 1e-9);
        assertEquals(190, axe.rotateZ());
        assertEquals(0.8F, axe.scale());
    }

    @Test void oversizedAndEmptyModelsKeepReferenceLengthCorrection() {
        assertEquals(-0.416, PillarItemDisplay.fixedTool(true, 0, 0).translateY(), 1e-7);
        assertEquals(-0.466, PillarItemDisplay.fixedTool(false, 0, 0).translateY(), 1e-7);
        assertEquals(0.144, PillarItemDisplay.fixedTool(true, 1.2, 1.6).translateY(), 1e-7);
        assertEquals(0.094, PillarItemDisplay.fixedTool(false, 1.2, 1.6).translateY(), 1e-7);
    }
}
