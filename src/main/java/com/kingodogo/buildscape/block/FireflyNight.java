package com.kingodogo.buildscape.block;

import net.minecraft.world.level.Level;

/**
 * Decides on the client whether it is night for firefly bushes.
 *
 * <p>{@code Level.isDay()} reads the cached sky darkness, which in 1.18.2 the client sets only when the client
 * world is created, so it never changes after joining. This repeats the vanilla server formula
 * ({@code Level.updateSkyBrightness}) from the day time, rain and thunder, which the client does keep current.
 */
final class FireflyNight {
    private static final int DAY_DARKNESS_LIMIT = 4;

    private FireflyNight() {}

    static boolean isNight(Level level) {
        if (level.dimensionType().hasFixedTime()) {
            return true;
        }
        return isNight(level.getTimeOfDay(1.0F), level.getRainLevel(1.0F), level.getThunderLevel(1.0F));
    }

    /** Same result as vanilla {@code !Level.isDay()} on a server, from a time of day in [0, 1) plus rain and thunder in [0, 1]. */
    static boolean isNight(float timeOfDay, float rainLevel, float thunderLevel) {
        double brightness = 1.0D - (Math.cos(timeOfDay * (Math.PI * 2.0D)) * 2.0D + 0.2D);
        brightness = Math.min(1.0D, Math.max(0.0D, brightness));
        brightness = 1.0D - brightness;
        brightness *= 1.0D - rainLevel * 5.0D / 16.0D;
        brightness *= 1.0D - thunderLevel * 5.0D / 16.0D;
        brightness = 1.0D - brightness;
        return (int) (brightness * 11.0D) >= DAY_DARKNESS_LIMIT;
    }
}
