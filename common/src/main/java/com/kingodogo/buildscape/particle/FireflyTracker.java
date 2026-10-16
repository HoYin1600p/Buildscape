package com.kingodogo.buildscape.particle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public final class FireflyTracker {
    public static final int MAX_IN_RANGE = 500;
    public static final double RANGE_SQUARED = 100.0D;
    private static final Map<Object, Entry> ACTIVE = new ConcurrentHashMap<>();

    private FireflyTracker() {}

    public static boolean canSpawn(Object level, double x, double y, double z) {
        int count = 0;
        for (Entry entry : ACTIVE.values()) {
            if (entry.level == level) {
                double dx = entry.x - x, dy = entry.y - y, dz = entry.z - z;
                if (dx * dx + dy * dy + dz * dz <= RANGE_SQUARED && ++count >= MAX_IN_RANGE) return false;
            }
        }
        return true;
    }

    public static void register(Object token, Object level, double x, double y, double z) {
        ACTIVE.put(token, new Entry(level, x, y, z));
    }

    public static void update(Object token, double x, double y, double z) {
        Entry entry = ACTIVE.get(token);
        if (entry != null) { entry.x = x; entry.y = y; entry.z = z; }
    }

    public static void remove(Object token) { ACTIVE.remove(token); }

    private static final class Entry {
        private final Object level;
        private volatile double x, y, z;
        private Entry(Object level, double x, double y, double z) {
            this.level = level; this.x = x; this.y = y; this.z = z;
        }
    }
}
