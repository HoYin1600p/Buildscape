package com.kingodogo.buildscape.client;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlowLightTintTest {
    @Test
    void preservesReleaseSelectionForNegativeHashesIncludingMinimumInteger() {
        var colors = List.of("#110000", "#220000", "#330000");
        assertEquals(0xff220000, GlowLightTint.color(-1, colors));
        assertEquals(0xff330000, GlowLightTint.color(Integer.MIN_VALUE, colors));
        assertEquals(-1, GlowLightTint.color(0, List.of("not a color")));
        assertEquals(-1, GlowLightTint.color(0, List.of()));
    }
}
