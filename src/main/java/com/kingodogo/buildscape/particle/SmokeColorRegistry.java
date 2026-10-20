package com.kingodogo.buildscape.particle;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmokeColorRegistry {

    private static final Map<ParticlePositionKey, ColorEntry> POSITION_COLOR_MAP = new ConcurrentHashMap<>();

    public static void registerColorForPosition(double x, double y, double z, String colorCode) {
        ParticlePositionKey posKey = ParticlePositionKey.of(x, y, z);
        POSITION_COLOR_MAP.put(posKey, new ColorEntry(colorCode));
    }

    public static String consumeColor(double x, double y, double z) {
        ParticlePositionKey posKey = ParticlePositionKey.of(x, y, z);
        ColorEntry entry = POSITION_COLOR_MAP.remove(posKey);
        return entry != null ? entry.color : null;
    }

    public static void clearColorCache() {
        POSITION_COLOR_MAP.clear();
    }

    public static void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<ParticlePositionKey, ColorEntry>> iterator = POSITION_COLOR_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ParticlePositionKey, ColorEntry> entry = iterator.next();
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
