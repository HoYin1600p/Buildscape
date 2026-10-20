package com.kingodogo.buildscape.client.screen;

import org.junit.Test;

import static org.junit.Assert.*;

public class PillarRemovalAnimationTest {

    @Test
    public void rowShiftStartsAtFullOffsetAndEasesToZero() {
        long start = 1000L;
        int duration = 260;
        float startOffset = 26.0f; // 1 row step

        // At start (t = 0)
        assertEquals(26.0f, BuildScapeConfigScreen.calculatePillarRowShift(startOffset, start, duration, start), 0.001f);
        assertEquals(26.0f, BuildScapeConfigScreen.calculatePillarRowShift(startOffset, start, duration, start - 100), 0.001f);

        // At half time (t = 130ms): easeOutCubic(0.5) = 1 - 0.5^3 = 0.875
        // remaining offset factor = 1 - 0.875 = 0.125
        float midShift = BuildScapeConfigScreen.calculatePillarRowShift(startOffset, start, duration, start + 130);
        assertEquals(26.0f * 0.125f, midShift, 0.01f);

        // At completion (t = 260ms)
        assertEquals(0.0f, BuildScapeConfigScreen.calculatePillarRowShift(startOffset, start, duration, start + 260), 0.001f);

        // Past completion
        assertEquals(0.0f, BuildScapeConfigScreen.calculatePillarRowShift(startOffset, start, duration, start + 500), 0.001f);
    }

    @Test
    public void zeroOrNegativeDurationRowShiftCompletesImmediately() {
        long start = 1000L;
        assertEquals(0.0f, BuildScapeConfigScreen.calculatePillarRowShift(26.0f, start, 0, start), 0.001f);
        assertEquals(0.0f, BuildScapeConfigScreen.calculatePillarRowShift(26.0f, start, -50, start), 0.001f);
    }

    @Test
    public void removalSlideMovesRightFromColXToTargetDistance() {
        long start = 1000L;
        int duration = 260;
        int colX = 50;
        int slideDistance = 300;

        // At start
        assertEquals(colX, BuildScapeConfigScreen.calculatePillarRemovalSlideX(colX, slideDistance, start, duration, start));
        assertEquals(colX, BuildScapeConfigScreen.calculatePillarRemovalSlideX(colX, slideDistance, start, duration, start - 50));

        // At half time (t = 130ms): easeOutSine(0.5) = sin(pi/4) ~= 0.7071
        int midX = BuildScapeConfigScreen.calculatePillarRemovalSlideX(colX, slideDistance, start, duration, start + 130);
        int expectedMidX = colX + (int) (slideDistance * (float) Math.sin(0.5f * Math.PI / 2.0));
        assertEquals(expectedMidX, midX);
        assertTrue(midX > colX);
        assertTrue(midX < colX + slideDistance);

        // At completion (t = 260ms)
        assertEquals(colX + slideDistance, BuildScapeConfigScreen.calculatePillarRemovalSlideX(colX, slideDistance, start, duration, start + 260));

        // Past completion
        assertEquals(colX + slideDistance, BuildScapeConfigScreen.calculatePillarRemovalSlideX(colX, slideDistance, start, duration, start + 400));
    }

    @Test
    public void removalAlphaFadesLinearlyToZero() {
        long start = 1000L;
        int duration = 260;
        float baseAlpha = 1.0f;

        // At start
        assertEquals(1.0f, BuildScapeConfigScreen.calculatePillarRemovalAlpha(baseAlpha, start, duration, start), 0.001f);

        // At half time
        assertEquals(0.5f, BuildScapeConfigScreen.calculatePillarRemovalAlpha(baseAlpha, start, duration, start + 130), 0.001f);

        // At completion
        assertEquals(0.0f, BuildScapeConfigScreen.calculatePillarRemovalAlpha(baseAlpha, start, duration, start + 260), 0.001f);

        // Past completion
        assertEquals(0.0f, BuildScapeConfigScreen.calculatePillarRemovalAlpha(baseAlpha, start, duration, start + 500), 0.001f);
    }

    @Test
    public void multiRowRemovalPositionsLeftoversSeamlessly() {
        // Initial list has 5 items at indices 0, 1, 2, 3, 4
        int rowStep = 26; // rowH 24 + rowGap 2
        float[] initialContentY = new float[]{0, 26, 52, 78, 104};

        // Items at index 1 and 3 are removed
        // Surviving items are index 0, 2, 4
        // Target indices are 0 -> 0, 2 -> 1, 4 -> 2
        int[] survivingOldIndices = new int[]{0, 2, 4};
        float[] newTargets = new float[]{0 * rowStep, 1 * rowStep, 2 * rowStep}; // 0, 26, 52

        // Differences (start offsets) for surviving items:
        // Item 0: 0 - 0 = 0
        // Item 2: 52 - 26 = +26 (1 item removed above it)
        // Item 4: 104 - 52 = +52 (2 items removed above it)
        long start = 2000L;
        int duration = 260;

        float offset0 = initialContentY[0] - newTargets[0];
        float offset2 = initialContentY[2] - newTargets[1];
        float offset4 = initialContentY[4] - newTargets[2];

        assertEquals(0.0f, offset0, 0.001f);
        assertEquals(26.0f, offset2, 0.001f);
        assertEquals(52.0f, offset4, 0.001f);

        // At t = 0 (immediately when click occurs)
        float visualY0_start = newTargets[0] + BuildScapeConfigScreen.calculatePillarRowShift(offset0, start, duration, start);
        float visualY2_start = newTargets[1] + BuildScapeConfigScreen.calculatePillarRowShift(offset2, start, duration, start);
        float visualY4_start = newTargets[2] + BuildScapeConfigScreen.calculatePillarRowShift(offset4, start, duration, start);

        // Matches initial visual positions exactly (no visual jump!)
        assertEquals(initialContentY[0], visualY0_start, 0.001f);
        assertEquals(initialContentY[2], visualY2_start, 0.001f);
        assertEquals(initialContentY[4], visualY4_start, 0.001f);

        // At completion (t = 260ms)
        float visualY0_end = newTargets[0] + BuildScapeConfigScreen.calculatePillarRowShift(offset0, start, duration, start + 260);
        float visualY2_end = newTargets[1] + BuildScapeConfigScreen.calculatePillarRowShift(offset2, start, duration, start + 260);
        float visualY4_end = newTargets[2] + BuildScapeConfigScreen.calculatePillarRowShift(offset4, start, duration, start + 260);

        // Reached exact target rows seamlessly
        assertEquals(newTargets[0], visualY0_end, 0.001f);
        assertEquals(newTargets[1], visualY2_end, 0.001f);
        assertEquals(newTargets[2], visualY4_end, 0.001f);
    }
}
