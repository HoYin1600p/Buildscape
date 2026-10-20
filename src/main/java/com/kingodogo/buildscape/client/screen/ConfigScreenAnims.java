package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.feature.pillar.PillarIdManager;

/**
 * Modular animation curves, timing definitions, speed configurations,
 * and stateful transition models for BuildScape screens.
 */
public final class ConfigScreenAnims {

    private ConfigScreenAnims() {}

    // Speed setting presets
    public static final String[] ANIM_SPEEDS = {"Smooth", "Snappy", "Instant"};
    private static int animSpeedIndex = 0;

    public static int getAnimSpeedIndex() {
        return animSpeedIndex;
    }

    public static void setAnimSpeedIndex(int index) {
        animSpeedIndex = Math.max(0, Math.min(ANIM_SPEEDS.length - 1, index));
    }

    public static void cycleAnimSpeed(int step) {
        animSpeedIndex = Math.floorMod(animSpeedIndex + step, ANIM_SPEEDS.length);
    }

    public static String getAnimSpeedLabel() {
        return ANIM_SPEEDS[animSpeedIndex];
    }

    public static float getAnimSpeedMultiplier() {
        return switch (animSpeedIndex) {
            case 1 -> 0.60f; // Snappy
            case 2 -> 0.001f; // Instant
            default -> 1.0f; // Smooth
        };
    }

    // Standard durations scaled by client speed setting
    public static int getOpenAnimMs() { return Math.max(1, (int) (300 * getAnimSpeedMultiplier())); }
    public static int getCloseAnimMs() { return Math.max(1, (int) (220 * getAnimSpeedMultiplier())); }
    public static int getNavAnimMs() { return Math.max(1, (int) (260 * getAnimSpeedMultiplier())); }
    public static int getNavIndicatorAnimMs() { return Math.max(1, (int) (260 * getAnimSpeedMultiplier())); }
    public static int getTabTransitionAnimMs() { return Math.max(1, (int) (240 * getAnimSpeedMultiplier())); }
    public static int getSidePanelAnimMs() { return Math.max(1, (int) (280 * getAnimSpeedMultiplier())); }
    public static int getPillarRemovalAnimMs() { return Math.max(1, (int) (260 * getAnimSpeedMultiplier())); }
    public static int getToggleAnimMs() { return Math.max(1, (int) (180 * getAnimSpeedMultiplier())); }
    public static int getThemeSwitchAnimMs() { return Math.max(1, (int) (220 * getAnimSpeedMultiplier())); }

    // Easing curves
    public static float easeOutSine(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        return (float) Math.sin(clamped * (Math.PI / 2.0));
    }

    public static float easeInSine(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        return 1.0f - (float) Math.cos(clamped * (Math.PI / 2.0));
    }

    public static float easeOutCubic(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        float f = 1.0f - clamped;
        return 1.0f - f * f * f;
    }

    public static float easeInCubic(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        return clamped * clamped * clamped;
    }

    public static float calculateItemContentTransitionAlpha(long now, long startedAt, int duration) {
        float progress = Math.min(1.0F, Math.max(0.0F, (now - startedAt) / (float) duration));
        return 0.45F + 0.55F * easeOutCubic(progress);
    }

    // Pillar table animations
    public static float calculatePillarRowShift(float startOffset, long startTime, int durationMs, long now) {
        if (durationMs <= 0 || now >= startTime + durationMs) return 0.0f;
        if (now <= startTime) return startOffset;
        float progress = (now - startTime) / (float) durationMs;
        return startOffset * (1.0f - easeOutCubic(progress));
    }

    public static int calculatePillarRemovalSlideX(int colX, int slideDistance, long startTime, int durationMs, long now) {
        if (durationMs <= 0 || now <= startTime) return colX;
        float progress = Math.min(1.0f, Math.max(0.0f, (now - startTime) / (float) durationMs));
        return colX + (int) (slideDistance * easeOutSine(progress));
    }

    public static float calculatePillarRemovalAlpha(float baseAlpha, long startTime, int durationMs, long now) {
        if (durationMs <= 0 || now >= startTime + durationMs) return 0.0f;
        if (now <= startTime) return baseAlpha;
        float progress = Math.min(1.0f, Math.max(0.0f, (now - startTime) / (float) durationMs));
        return baseAlpha * (1.0f - progress);
    }

    public static class PillarRowShiftAnim {
        public final float startOffset;
        public final long startTime;
        public final int durationMs;

        public PillarRowShiftAnim(float startOffset, long startTime, int durationMs) {
            this.startOffset = startOffset;
            this.startTime = startTime;
            this.durationMs = durationMs;
        }

        public float getCurrentOffset(long now) {
            return calculatePillarRowShift(startOffset, startTime, durationMs, now);
        }

        public boolean isFinished(long now) {
            return now >= startTime + durationMs;
        }
    }

    public static class PillarRemovalEntry {
        public final PillarIdManager.PillarData data;
        public final float contentY;
        public final long startTime;
        public final int durationMs;

        public PillarRemovalEntry(PillarIdManager.PillarData data, float contentY, long startTime, int durationMs) {
            this.data = data;
            this.contentY = contentY;
            this.startTime = startTime;
            this.durationMs = durationMs;
        }

        public float getProgress(long now) {
            if (durationMs <= 0) return 1.0f;
            float progress = (now - startTime) / (float) durationMs;
            return Math.min(1.0f, Math.max(0.0f, progress));
        }

        public int getSlideX(int colX, int slideDistance, long now) {
            return calculatePillarRemovalSlideX(colX, slideDistance, startTime, durationMs, now);
        }

        public float getRemovalAlpha(float baseAlpha, long now) {
            return calculatePillarRemovalAlpha(baseAlpha, startTime, durationMs, now);
        }

        public boolean isFinished(long now) {
            return now >= startTime + durationMs;
        }
    }
}
