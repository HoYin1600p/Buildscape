package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLayoutPortTest {
    @Test
    void screenUsesAvailableCanvasAndReservesSpaceForTheCrest() {
        var layout = GuiScaleHelper.calculateLayout(1280, 720, 0,
                BuildScapeConfigScreen.SidePanelType.PRESETS);
        assertEquals(GuiScaleHelper.SCREEN_PADDING, layout.frameX);
        assertEquals(GuiScaleHelper.CREST_OVERHANG_TOP + 4, layout.frameY);
        assertEquals(1280 - GuiScaleHelper.SCREEN_PADDING, layout.frameRight);
        assertEquals(720 - GuiScaleHelper.SCREEN_PADDING, layout.frameY + layout.frameH);
    }

    @Test
    void openingSidePanelKeepsBothPanelsInsideCanvas() {
        var closed = GuiScaleHelper.calculateLayout(960, 480, 0,
                BuildScapeConfigScreen.SidePanelType.PRESETS);
        var open = GuiScaleHelper.calculateLayout(960, 480, 1,
                BuildScapeConfigScreen.SidePanelType.PRESETS);
        assertEquals(closed.frameX, open.frameX);
        assertEquals(GuiScaleHelper.SIDE_PANEL_TARGET_W - 2, closed.frameW - open.frameW);
        assertEquals(960 - GuiScaleHelper.SCREEN_PADDING, open.sidePanelX + open.sidePanelW);
    }
}
