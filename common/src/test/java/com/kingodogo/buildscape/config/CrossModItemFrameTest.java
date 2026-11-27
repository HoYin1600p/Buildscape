package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.util.CommonId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrossModItemFrameTest {
    @Test
    void frameRegistrationPreservesTheOwningModsEntityType() {
        assertEquals("supplementaries:gold_item_frame",
                PillarIdManager.resolveItemFrameType(new CommonId("supplementaries", "gold_item_frame")));
        assertEquals("minecraft:item_frame", PillarIdManager.resolveItemFrameType(null));
    }
}
