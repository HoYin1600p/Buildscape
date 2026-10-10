package com.kingodogo.buildscape.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoomHandlerTest {
    @BeforeEach void reset() { ZoomHandler.reset(); }
    @AfterEach void clear() { ZoomHandler.reset(); }

    @Test void toggleAndResetRestoreDefaultState() {
        assertFalse(ZoomHandler.isZooming());
        ZoomHandler.toggleZoom();
        assertTrue(ZoomHandler.isZooming());
        ZoomHandler.toggleZoom();
        assertFalse(ZoomHandler.isZooming());
        ZoomHandler.toggleZoom();
        ZoomHandler.getSmoothedRotation(60, 30);
        ZoomHandler.reset();
        assertFalse(ZoomHandler.isZooming());
        assertEquals(1.0f, ZoomHandler.getZoomLevel());
        ZoomHandler.toggleZoom();
        assertArrayEquals(new float[]{-20, -10}, ZoomHandler.getSmoothedRotation(-20, -10));
    }

    @Test void cinematicRotationCrossesYawBoundaryByShortestPath() {
        ZoomHandler.toggleZoom();
        ZoomHandler.getSmoothedRotation(179, 0);
        float[] rotation = ZoomHandler.getSmoothedRotation(-179, 30);
        assertEquals(179.3f, rotation[0], 0.001f);
        assertEquals(4.5f, rotation[1], 0.001f);
    }

    @Test void disablingZoomStopsCameraSmoothing() {
        ZoomHandler.toggleZoom();
        ZoomHandler.getSmoothedRotation(0, 0);
        ZoomHandler.toggleZoom();
        assertArrayEquals(new float[]{90, 40}, ZoomHandler.getSmoothedRotation(90, 40));
    }

    @Test void scrollIsIgnoredWhenInactiveAndClampedWhenActive() {
        ZoomHandler.handleScroll(100);
        settle();
        assertEquals(1.0f, ZoomHandler.getZoomLevel());
        ZoomHandler.toggleZoom();
        settle();
        assertEquals(0.25f, ZoomHandler.getZoomLevel());
        ZoomHandler.handleScroll(100);
        settle();
        assertEquals(0.1f, ZoomHandler.getZoomLevel());
        ZoomHandler.handleScroll(-100);
        settle();
        assertEquals(1.0f, ZoomHandler.getZoomLevel());
        ZoomHandler.toggleZoom();
        settle();
        assertEquals(1.0f, ZoomHandler.getZoomLevel());
    }

    private static void settle() {
        for (int tick = 0; tick < 100; tick++) ZoomHandler.advanceZoom();
    }
}
