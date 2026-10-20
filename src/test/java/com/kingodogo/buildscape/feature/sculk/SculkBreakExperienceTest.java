package com.kingodogo.buildscape.feature.sculk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SculkBreakExperienceTest {
    @Test
    public void decorativeSculkBlocksGiveOneExperienceIncludingPillar() {
        for (String path : new String[]{"sculk", "sculk_slab", "sculk_stairs", "sculk_wall", "sculk_vertical_slab",
                "sculk_pillar"}) {
            assertEquals(path, 1, SculkBreakExperience.forBlock(path));
        }
    }

    @Test
    public void functionalBlocksFollowVanilla() {
        assertEquals(0, SculkBreakExperience.forBlock("sculk_vein"));
        assertEquals(5, SculkBreakExperience.forBlock("sculk_catalyst"));
        assertEquals(5, SculkBreakExperience.forBlock("sculk_sensor"));
        assertEquals(5, SculkBreakExperience.forBlock("sculk_shrieker"));
    }

    @Test
    public void otherBlocksAreNotHandled() {
        assertEquals(-1, SculkBreakExperience.forBlock("stone"));
        assertEquals(-1, SculkBreakExperience.forBlock(null));
    }

    @Test
    public void silkTouchRemovesExperience() {
        assertEquals(0, SculkBreakExperience.withTool(5, 1));
        assertEquals(5, SculkBreakExperience.withTool(5, 0));
    }
}
