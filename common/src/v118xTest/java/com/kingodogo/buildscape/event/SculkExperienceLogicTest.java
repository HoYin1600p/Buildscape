package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SculkExperienceLogicTest {
    @Test
    void decorativeSculkBlocksIncludingThePillarGiveOneExperience() {
        for (String path : new String[]{"sculk", "sculk_slab", "sculk_stairs", "sculk_wall", "sculk_vertical_slab",
                "sculk_pillar"}) {
            assertEquals(1, SculkExperienceLogic.forBlock(path), path);
        }
    }

    @Test
    void functionalBlocksFollowVanillaAndVeinsGiveNone() {
        assertEquals(0, SculkExperienceLogic.forBlock("sculk_vein"));
        assertEquals(5, SculkExperienceLogic.forBlock("sculk_catalyst"));
        assertEquals(5, SculkExperienceLogic.forBlock("sculk_sensor"));
        assertEquals(5, SculkExperienceLogic.forBlock("sculk_shrieker"));
        assertEquals(-1, SculkExperienceLogic.forBlock("stone"));
        assertEquals(-1, SculkExperienceLogic.forBlock(null));
    }

    @Test
    void silkTouchRemovesSculkExperienceOnlyAndLeavesOtherBlocksAlone() {
        assertEquals(0, SculkExperienceLogic.withTool(5, true));
        assertEquals(5, SculkExperienceLogic.withTool(5, false));
        assertEquals(0, SculkExperienceLogic.withTool(0, true));
        // -1 keeps the loader's own experience, for example ore drops mined with Silk Touch.
        assertEquals(-1, SculkExperienceLogic.withTool(-1, true));
    }
}
