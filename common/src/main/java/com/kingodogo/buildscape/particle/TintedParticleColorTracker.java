package com.kingodogo.buildscape.particle;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class TintedParticleColorTracker {

    private static final Map<String, ColorEntry> POSITION_COLOR_MAP = new ConcurrentHashMap<>();

    private static class ColorEntry {
        final String color;
        final long timestamp;

        ColorEntry(String color) {
            this.color = color;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static void registerColorForPosition(double x, double y, double z, String colorCode) {
        cleanupOldEntries();
        String posKey = makePositionKey(x, y, z);
        POSITION_COLOR_MAP.put(posKey, new ColorEntry(colorCode));
    }

    public static String getColorForPosition(double x, double y, double z) {
        String posKey = makePositionKey(x, y, z);
        ColorEntry entry = POSITION_COLOR_MAP.remove(posKey);
        return entry != null ? entry.color : null;
    }

    public static float[] consumeRgb(double x, double y, double z, float defaultRed, float defaultGreen, float defaultBlue) {
        String colorCode = getColorForPosition(x, y, z);
        if (colorCode == null || colorCode.length() != 7 || colorCode.charAt(0) != '#') {
            return new float[]{defaultRed, defaultGreen, defaultBlue};
        }
        try {
            return new float[]{Integer.parseInt(colorCode.substring(1, 3), 16) / 255.0F,
                    Integer.parseInt(colorCode.substring(3, 5), 16) / 255.0F,
                    Integer.parseInt(colorCode.substring(5, 7), 16) / 255.0F};
        } catch (NumberFormatException ignored) {
            return new float[]{defaultRed, defaultGreen, defaultBlue};
        }
    }

    private static String makePositionKey(double x, double y, double z) {
        return String.format(java.util.Locale.ROOT, "%.4f,%.4f,%.4f", x, y, z);
    }

    private static void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, ColorEntry>> iterator = POSITION_COLOR_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ColorEntry> entry = iterator.next();
            if (now - entry.getValue().timestamp > 2000L) {
                iterator.remove();
            }
        }
    }

    public static void clear() {
        POSITION_COLOR_MAP.clear();
    }
}
