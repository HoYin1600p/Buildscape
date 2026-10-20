package com.kingodogo.buildscape.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class BeaconBeamStoppersTest {

    @Test
    public void matchesTagOrRegistryPathWithAnExplicitJarException() {
        assertTrue(BeaconBeamStoppers.isStopper("minecraft:tinted_glass", false));
        for (String shape : new String[]{"slab", "stairs", "vertical_slab", "ornament"}) {
            assertTrue(shape, BeaconBeamStoppers.isStopper("buildscape:tinted_glass_" + shape, false));
        }
        assertTrue(BeaconBeamStoppers.isStopper("buildscape:big_tinted_glass_ornament", false));
        assertTrue(BeaconBeamStoppers.isStopper("other:polished_tinted_glass_panel", false));
        assertTrue(BeaconBeamStoppers.isStopper("other:beam_cap", true));
        assertTrue(BeaconBeamStoppers.isStopper("other:tinted_glass_jar", false));
        assertTrue(BeaconBeamStoppers.isStopper("minecraft:bedrock", true));
        assertFalse(BeaconBeamStoppers.isStopper("buildscape:tinted_glass_jar", false));
        assertFalse(BeaconBeamStoppers.isStopper("buildscape:tinted_glass_jar", true));
        assertFalse(BeaconBeamStoppers.isStopper("tinted_glass:clear_panel", false));
        assertFalse(BeaconBeamStoppers.isStopper("minecraft:stone", false));
        assertFalse(BeaconBeamStoppers.isStopper("minecraft:glass", false));
        assertFalse(BeaconBeamStoppers.isStopper("buildscape:tinted_cardboard", false));
    }
}
