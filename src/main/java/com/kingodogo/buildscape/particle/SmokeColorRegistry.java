package com.kingodogo.buildscape.particle;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores pending smoke colors keyed by spawn position.
 * Not client-only so SmokeVentBlock can reference it without dist-cleaner issues.
 */
public class SmokeColorRegistry {

    private static final Map<String, ColorEntry> POSITION_COLOR_MAP = new ConcurrentHashMap<>();

    public static void registerColorForPosition(double x, double y, double z, String colorCode) {
        String posKey = makePositionKey(x, y, z);
        POSITION_COLOR_MAP.put(posKey, new ColorEntry(colorCode));
    }

    public static String consumeColor(double x, double y, double z) {
        String posKey = makePositionKey(x, y, z);
        ColorEntry entry = POSITION_COLOR_MAP.remove(posKey);
        cleanupOldEntries();
        return entry != null ? entry.color : null;
    }

    public static void clearColorCache() {
        POSITION_COLOR_MAP.clear();
    }

    private static String makePositionKey(double x, double y, double z) {
        return String.format("%.4f,%.4f,%.4f", x, y, z);
    }

    private static void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, ColorEntry>> iterator = POSITION_COLOR_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ColorEntry> entry = iterator.next();
            if (now - entry.getValue().timestamp > 1000) {
                iterator.remove();
            }
        }
    }

    private static class ColorEntry {
        final String color;
        final long timestamp;

        ColorEntry(String color) {
            this.color = color;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
