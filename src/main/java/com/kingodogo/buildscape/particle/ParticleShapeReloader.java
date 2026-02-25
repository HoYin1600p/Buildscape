package com.kingodogo.buildscape.particle;

/**
 * ParticleShapeReloader - Hot-reload system for particle shapes
 * Allows reloading wing shapes without requiring a restart
 */
public class ParticleShapeReloader {

    private static long lastReloadCheck = 0;
    private static final long RELOAD_CHECK_INTERVAL = 1000;  // Check every 1 second
    private static volatile boolean shapeChangeRequested = false;
    private static String requestedShapeId = null;

    /**
     * Request a shape reload on the next tick (thread-safe)
     */
    public static void requestShapeReload(String shapeId) {
        requestedShapeId = shapeId;
        shapeChangeRequested = true;
    }

    /**
     * Reload all wing shapes (hot-reload)
     */
    public static void reloadAllShapes() {
        ParticleShapeLibrary.clearCache();
        shapeChangeRequested = false;
    }

    /**
     * Check if a specific shape needs reloading
     */
    public static boolean isShapeReloadNeeded(String shapeId) {
        return shapeChangeRequested && (requestedShapeId == null || requestedShapeId.equals(shapeId));
    }

    /**
     * Tick check - call periodically to check if reload is needed
     */
    public static void tick() {
        long currentTime = System.currentTimeMillis();

        // Check periodically for shape changes
        if (currentTime - lastReloadCheck > RELOAD_CHECK_INTERVAL) {
            lastReloadCheck = currentTime;

            if (shapeChangeRequested) {
                reloadAllShapes();
            }
        }
    }
}
