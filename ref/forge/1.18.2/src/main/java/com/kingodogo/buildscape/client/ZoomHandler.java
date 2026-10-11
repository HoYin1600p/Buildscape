package com.kingodogo.buildscape.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ZoomHandler {

    private static boolean isZooming = false;
    private static float currentZoomLevel = 1.0f;
    private static float targetZoomLevel = 1.0f;

    private static final float BASE_ZOOM = 0.25f;
    private static final float MIN_ZOOM = 0.1f;
    private static final float MAX_ZOOM = 1.0f;
    private static final float SCROLL_SENSITIVITY = 0.05f;
    private static final float ZOOM_SPEED = 0.15f;

    private static final float CINEMATIC_SMOOTHING = 0.15f;

    private static float smoothedYaw = 0.0f;
    private static float smoothedPitch = 0.0f;
    private static boolean hasInitialRotation = false;

    public static void toggleZoom() {
        isZooming = !isZooming;
        if (!isZooming) {
            targetZoomLevel = MAX_ZOOM;
            hasInitialRotation = false;
        } else {
            targetZoomLevel = BASE_ZOOM;
        }
    }

    public static boolean isZooming() {
        return isZooming;
    }

    public static void handleScroll(double scrollDelta) {
        if (!isZooming) return;

        float zoomChange = (float) (-scrollDelta * SCROLL_SENSITIVITY);
        targetZoomLevel = Mth.clamp(
                targetZoomLevel + zoomChange,
                MIN_ZOOM,
                MAX_ZOOM
        );
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        float zoomDiff = targetZoomLevel - currentZoomLevel;
        currentZoomLevel += zoomDiff * ZOOM_SPEED;

        if (Math.abs(zoomDiff) < 0.001f) {
            currentZoomLevel = targetZoomLevel;
        }
    }

    public static float getZoomLevel() {
        return currentZoomLevel;
    }

    public static float[] getSmoothedRotation(
            float currentYaw,
            float currentPitch
    ) {
        if (!isZooming || !hasInitialRotation) {
            smoothedYaw = currentYaw;
            smoothedPitch = currentPitch;
            hasInitialRotation = true;
            return new float[]{currentYaw, currentPitch};
        }

        float yawDiff = currentYaw - smoothedYaw;
        float pitchDiff = currentPitch - smoothedPitch;

        while (yawDiff > 180.0f) yawDiff -= 360.0f;
        while (yawDiff < -180.0f) yawDiff += 360.0f;

        smoothedYaw += yawDiff * CINEMATIC_SMOOTHING;
        smoothedPitch += pitchDiff * CINEMATIC_SMOOTHING;

        while (smoothedYaw > 180.0f) smoothedYaw -= 360.0f;
        while (smoothedYaw < -180.0f) smoothedYaw += 360.0f;

        smoothedPitch = Mth.clamp(smoothedPitch, -90.0f, 90.0f);

        return new float[]{smoothedYaw, smoothedPitch};
    }

    public static void reset() {
        isZooming = false;
        currentZoomLevel = MAX_ZOOM;
        targetZoomLevel = MAX_ZOOM;
        hasInitialRotation = false;
    }
}
