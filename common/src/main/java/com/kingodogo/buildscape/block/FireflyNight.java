package com.kingodogo.buildscape.block;

import net.minecraft.world.level.Level;

/** Computes client night state from values that stay current in Minecraft 1.18.2. */
final class FireflyNight {
    private static final int DAY_DARKNESS_LIMIT = 4;

    private FireflyNight() {
    }

    static boolean isNight(Level level) {
        if (level.dimensionType().hasFixedTime()) {
            return true;
        }
        double brightness = 1.0 - (Math.cos(level.getTimeOfDay(1.0F) * (Math.PI * 2.0)) * 2.0 + 0.2);
        brightness = Math.min(1.0, Math.max(0.0, brightness));
        brightness = 1.0 - brightness;
        brightness *= 1.0 - level.getRainLevel(1.0F) * 5.0 / 16.0;
        brightness *= 1.0 - level.getThunderLevel(1.0F) * 5.0 / 16.0;
        return (int) ((1.0 - brightness) * 11.0) >= DAY_DARKNESS_LIMIT;
    }
}
