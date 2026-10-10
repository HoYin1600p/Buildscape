package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.adapter.v26x.client.FramebufferScissor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FramebufferScissorTest {
    @Test void clipsUseGuiScaleAndTopLeftOrigin() {
        assertEquals(new FramebufferScissor(10, 20, 110, 70),
                FramebufferScissor.toGui(20, 940, 200, 100, 1080, 2));
        assertEquals(new FramebufferScissor(10, 20, 110, 70),
                FramebufferScissor.toGui(30, 870, 300, 150, 1080, 3));
    }

    @Test void fractionalEdgesRoundOutwards() {
        assertEquals(new FramebufferScissor(0, 1, 3, 4),
                FramebufferScissor.toGui(1, 10, 4, 4, 17, 2));
    }

    @Test void collapsedPanelsProduceEmptyClips() {
        assertEquals(new FramebufferScissor(10, 20, 10, 20),
                FramebufferScissor.toGui(20, 1040, -10, -20, 1080, 2));
        assertEquals(new FramebufferScissor(0, 3, 0, 3),
                FramebufferScissor.toGui(1, 10, 0, 0, 17, 2));
    }
}
