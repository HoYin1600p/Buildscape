package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdvancementCriterionTest {
    @Test
    void acceptsValidPathsAndSkipsInvalidDatapackCriteria() {
        assertTrue(AdvancementEvents.isAdvancementPath("put_it_on_display"));
        assertTrue(AdvancementEvents.isAdvancementPath("nested/advancement"));
        assertFalse(AdvancementEvents.isAdvancementPath(null));
        assertFalse(AdvancementEvents.isAdvancementPath(""));
        assertFalse(AdvancementEvents.isAdvancementPath("invalid criterion"));
        assertFalse(AdvancementEvents.isAdvancementPath("INVALID"));
        assertFalse(AdvancementEvents.isAdvancementPath("other:advancement"));
    }
}
