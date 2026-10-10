package com.kingodogo.buildscape.client.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ParticleColorSlotsTest {
    private static int checks;

    private ParticleColorSlotsTest() {
    }

    public static void main(String[] args) {
        List<String> sixColors = new ArrayList<>(Arrays.asList(
                "#111111", "#222222", "#333333", "#444444", "#555555", "#666666"));
        check("#666666".equals(ParticleColorSlots.colorAt(sixColors, 5)), "existing color remains intact");
        check(ParticleColorSlots.DEFAULT_COLOR.equals(ParticleColorSlots.colorAt(sixColors, 6)),
                "seventh swatch has a safe default");
        check(sixColors.size() == 6, "display lookup does not mutate config");
        check(ParticleColorSlots.DEFAULT_COLOR.equals(ParticleColorSlots.colorAt(sixColors, -1)),
                "negative index has a safe default");
        check(ParticleColorSlots.DEFAULT_COLOR.equals(ParticleColorSlots.colorAt(null, 0)),
                "missing palette has a safe default");

        ParticleColorSlots.ensureCapacity(sixColors, 7);
        check(sixColors.size() == 7, "enabling seventh color creates a stored slot");
        check(ParticleColorSlots.DEFAULT_COLOR.equals(sixColors.get(6)), "new stored slot is white");
        check("#666666".equals(sixColors.get(5)), "materializing a slot preserves earlier colors");
        ParticleColorSlots.ensureCapacity(sixColors, 7);
        check(sixColors.size() == 7, "materializing a slot is idempotent");

        System.out.println("Particle color slot tests passed: " + checks + " checks");
    }

    private static void check(boolean result, String description) {
        if (!result) {
            throw new AssertionError(description);
        }
        checks++;
    }
}
