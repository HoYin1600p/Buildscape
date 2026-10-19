package com.kingodogo.buildscape.client.screen.widget;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorPickerGeometryTest {
    @Test
    public void endpointsReachBothRgbAndHsbExtremesAtEveryScale() {
        for (int size : new int[]{2, 3, 40, 80, 100}) {
            assertEquals(0.0f, ColorPickerGeometry.fraction(20, 20, size), 0.0f);
            float maximum = ColorPickerGeometry.fraction(20 + size - 1, 20, size);
            assertEquals(1.0f, maximum, 0.0f);
            assertEquals(255, (int) (maximum * 255));
            assertEquals(360.0f, maximum * 360.0f, 0.0f);
        }
    }

    @Test
    public void draggingOutsideTheSliderClampsToItsEndpoints() {
        assertEquals(0.0f, ColorPickerGeometry.fraction(-100, 20, 80), 0.0f);
        assertEquals(1.0f, ColorPickerGeometry.fraction(1000, 20, 80), 0.0f);
        assertEquals(0.5f, ColorPickerGeometry.fraction(59.5, 20, 80), 0.0f);
    }

    @Test
    public void collapsedDimensionsRemainFinite() {
        for (int size : new int[]{-1, 0, 1}) {
            assertEquals(0.0f, ColorPickerGeometry.fraction(1000, 20, size), 0.0f);
        }
    }
}
