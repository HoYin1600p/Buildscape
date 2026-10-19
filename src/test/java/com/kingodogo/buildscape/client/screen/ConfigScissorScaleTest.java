package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigScissorScaleTest {

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void unscaledLayoutUsesTheWindowGuiScale() {
        assertArrayEquals(new int[] {40, 1440 - 220, 200, 100},
                BuildScapeConfigScreen.scissorRect(20, 60, 100, 50, 2.0, 1440));
    }

    @Test
    public void shrunkLayoutScalesTheClipRectangleToo() {
        // GUI scale 3 on 2560x1440: canvas 853x480 is narrower than 960, so the screen draws at 853/960.
        float uiScale = GuiScaleHelper.calculateUiScale(853, 480, 3.0);
        assertEquals(853f / 960f, uiScale, 1e-6f);
        double scale = 3.0 * uiScale;
        int[] rect = BuildScapeConfigScreen.scissorRect(300, 100, 200, 120, scale, 1440);
        // The drawn panel spans effective x 300..500, i.e. GUI x 300*uiScale.., framebuffer x *3.
        assertEquals(Math.round(300 * scale), rect[0]);
        assertEquals(Math.round(200 * scale), rect[2]);
        assertEquals(Math.round(1440 - 220 * scale), rect[1]);
        assertEquals(Math.round(120 * scale), rect[3]);
    }

    @Test
    public void shrinkIsActiveOnlyBelowTheRequiredCanvas() {
        assertTrue(GuiScaleHelper.isShrunk(GuiScaleHelper.calculateUiScale(853, 480, 3.0)));
        assertTrue(GuiScaleHelper.isShrunk(GuiScaleHelper.calculateUiScale(640, 360, 3.0)));
        assertFalse(GuiScaleHelper.isShrunk(GuiScaleHelper.calculateUiScale(1280, 720, 2.0)));
        assertFalse(GuiScaleHelper.isShrunk(1.0f));
    }
}
