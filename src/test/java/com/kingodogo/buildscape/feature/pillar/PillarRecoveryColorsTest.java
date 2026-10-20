package com.kingodogo.buildscape.feature.pillar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class PillarRecoveryColorsTest {

    @Test
    public void missingOrDifferentSizedManagerColorsCount_asChanged() {
        assertTrue(PillarRecovery.colorsChanged(null, List.of("#FFFFFF")));
        assertTrue(PillarRecovery.colorsChanged(List.of(), List.of("#FFFFFF")));
        assertTrue(PillarRecovery.colorsChanged(List.of("#FFFFFF"), List.of("#FFFFFF", "#000000")));
    }

    @Test
    public void sameColorsInSameOrder_areUnchanged() {
        assertFalse(PillarRecovery.colorsChanged(List.of("#FFFFFF", "#000000"), List.of("#FFFFFF", "#000000")));
    }

    @Test
    public void differentOrNullColor_countsAsChanged() {
        assertTrue(PillarRecovery.colorsChanged(List.of("#FFFFFF", "#000000"), List.of("#000000", "#FFFFFF")));
        assertTrue(PillarRecovery.colorsChanged(List.of("#FFFFFF"), Arrays.asList((String) null)));
    }
}
