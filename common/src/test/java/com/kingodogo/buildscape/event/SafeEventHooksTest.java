package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SafeEventHooksTest {
    @Test void failedPlacementHookDoesNotEscapeOrDisableLaterPlacements() {
        assertDoesNotThrow(() -> SafeEventHooks.run("test placement", () -> {
            throw new NullPointerException("missing custom stat key");
        }));
        int nextPlacement = SafeEventHooks.call("test placement", () -> 7, 0);
        assertEquals(7, nextPlacement);
    }

    @Test void failedInteractionReturnsTheVanillaFallbackEveryTime() {
        for (int i = 0; i < 2; i++) {
            assertEquals("pass", SafeEventHooks.call("test interaction", () -> {
                throw new IllegalArgumentException("missing block property");
            }, "pass"));
        }
    }
}
