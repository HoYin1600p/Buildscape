package com.kingodogo.buildscape.client.particle;

import com.kingodogo.buildscape.particle.SmokeColorRegistry;
import org.junit.Test;

import java.util.Locale;

public final class SmokeColorRegistryTest {

    @Test
    public void queuedColorsSurviveLocaleChanges() {
        Locale original = Locale.getDefault();
        try {
            SmokeColorRegistry.clearColorCache();
            Locale.setDefault(Locale.US);
            SmokeColorRegistry.registerColorForPosition(123.125, 64.5, -456.25, "#123456");
            Locale.setDefault(Locale.GERMANY);
            check("#123456".equals(SmokeColorRegistry.consumeColor(123.125, 64.5, -456.25)),
                    "queued color survives a locale change");
            check(SmokeColorRegistry.consumeColor(123.125, 64.5, -456.25) == null,
                    "a queued color is consumed only once");

            SmokeColorRegistry.registerColorForPosition(-29999999.1234, -64.125, 29999999.1234, "#112233");
            SmokeColorRegistry.registerColorForPosition(-29999999.1232, -64.125, 29999999.1234, "#445566");
            check("#112233".equals(SmokeColorRegistry.consumeColor(-29999999.1234, -64.125, 29999999.1234)),
                    "large negative coordinates preserve color identity");
            check("#445566".equals(SmokeColorRegistry.consumeColor(-29999999.1232, -64.125, 29999999.1234)),
                    "nearby positions retain separate colors");

            SmokeColorRegistry.registerColorForPosition(1.0, 2.0, 3.0, "#FFFFFF");
            SmokeColorRegistry.clearColorCache();
            check(SmokeColorRegistry.consumeColor(1.0, 2.0, 3.0) == null,
                    "world cleanup discards queued colors");
        } finally {
            Locale.setDefault(original);
            SmokeColorRegistry.clearColorCache();
        }
    }

    private static void check(boolean result, String description) {
        if (!result) throw new AssertionError(description);
    }
}
