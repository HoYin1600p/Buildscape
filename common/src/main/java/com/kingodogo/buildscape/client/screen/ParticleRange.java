package com.kingodogo.buildscape.client.screen;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * The ranges the config screen's particle counters step through, with the text that goes with them. A stored
 * value outside a range is kept and shown exactly; only an explicit edit of that one setting moves it in,
 * after a first click that says so ({@link Migration}).
 */
enum ParticleRange {
    PATTERN_SPEED("Pattern Speed", 0.01D, 0.50D, 0.01D, 2, ""),
    PATTERN_SPREAD("Pattern Spread", 0.01D, 0.50D, 0.01D, 2, ""),
    PATTERN_INTENSITY("Pattern Intensity", 0.1D, 5.0D, 0.1D, 1, "x"),
    PARTICLE_SPEED("Particle Speed", 0.01D, 0.50D, 0.01D, 2, ""),
    PARTICLE_SPREAD("Particle Spread", 0.01D, 0.50D, 0.01D, 2, ""),
    PARTICLE_LIFETIME("Particle Lifetime", 5D, 120D, 5D, 0, " ticks"),
    PARTICLE_DENSITY("Particle Density", 1D, 16D, 1D, 0, "x");

    private static final double EPSILON = 1.0E-9D;
    /** How long the first click of a migration stays armed for the second one. */
    static final long MIGRATION_WINDOW_MS = 6000L;

    final String label;
    final double min;
    final double max;
    final double step;
    final int decimals;
    private final String suffix;

    ParticleRange(String label, double min, double max, double step, int decimals, String suffix) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.step = step;
        this.decimals = decimals;
        this.suffix = suffix;
    }

    boolean isOutOfRange(double value) {
        return Double.isNaN(value) || value < min - EPSILON || value > max + EPSILON;
    }

    /** The in-range value an explicit edit of an out-of-range value lands on: the nearest bound. */
    double nearestInRange(double value) {
        if (Double.isNaN(value) || value < min) {
            return min;
        }
        return Math.min(max, value);
    }

    /** The value as the counter shows it: the setting's usual precision, more digits only when it stores more. */
    String formatValue(double value) {
        return plain(value) + suffix;
    }

    String formatBound(double bound) {
        return String.format(Locale.ROOT, "%." + decimals + "f", bound);
    }

    private String plain(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return String.valueOf(value);
        }
        BigDecimal exact = BigDecimal.valueOf(value);
        if (exact.stripTrailingZeros().scale() > decimals) {
            BigDecimal shown = exact.setScale(Math.min(6, exact.stripTrailingZeros().scale()), RoundingMode.HALF_UP);
            return shown.stripTrailingZeros().toPlainString();
        }
        return exact.setScale(decimals, RoundingMode.HALF_UP).toPlainString();
    }

    /** Hover text for the counter. */
    String tooltip(double value) {
        String range = "Range: " + formatBound(min) + " - " + formatBound(max);
        return isOutOfRange(value)
                ? range + " (stored value " + plain(value) + " is outside it)"
                : range;
    }

    /** Notice shown by the first click on an out-of-range value. */
    String warning(double value) {
        return label + " " + plain(value) + " is " + (value > max ? "above the new limit" : "below the new minimum")
                + "; the next click sets it to " + formatBound(nearestInRange(value));
    }

    String migrated(double value) {
        return label + " set to " + formatBound(nearestInRange(value));
    }

    /**
     * Two-click migration of one out-of-range setting. The first click only warns; a second click on the same
     * setting within {@link #MIGRATION_WINDOW_MS} commits the in-range value. Another setting's click starts over.
     */
    static final class Migration {
        enum Step {
            EDIT,
            WARN,
            COMMIT
        }

        private ParticleRange armed;
        private String armedScope;
        private long armedUntil;

        /** What a click on this setting does now; {@code scope} tells apart the same setting on another pillar. */
        Step click(ParticleRange range, String scope, double value, long now) {
            if (!range.isOutOfRange(value)) {
                armed = null;
                return Step.EDIT;
            }
            if (armed == range && java.util.Objects.equals(armedScope, scope) && now <= armedUntil) {
                armed = null;
                return Step.COMMIT;
            }
            armed = range;
            armedScope = scope;
            armedUntil = now + MIGRATION_WINDOW_MS;
            return Step.WARN;
        }

        void reset() {
            armed = null;
        }
    }
}
