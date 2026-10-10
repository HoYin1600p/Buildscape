package com.kingodogo.buildscape.particle;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public final class SmokeColorRegistry {
    private static final Map<String, ColorEntry> POSITION_COLOR_MAP = new ConcurrentHashMap<>();

    private SmokeColorRegistry() {
    }

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

    public static float[] consumeRgb(double x, double y, double z) {
        String colorCode = consumeColor(x, y, z);
        if (colorCode == null || colorCode.length() != 7 || colorCode.charAt(0) != '#') {
            return new float[]{0.9F, 0.9F, 0.9F};
        }
        try {
            return new float[]{
                    Integer.parseInt(colorCode.substring(1, 3), 16) / 255.0F,
                    Integer.parseInt(colorCode.substring(3, 5), 16) / 255.0F,
                    Integer.parseInt(colorCode.substring(5, 7), 16) / 255.0F
            };
        } catch (NumberFormatException ignored) {
            return new float[]{0.9F, 0.9F, 0.9F};
        }
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

    private static final class ColorEntry {
        private final String color;
        private final long timestamp;

        private ColorEntry(String color) {
            this.color = color;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
