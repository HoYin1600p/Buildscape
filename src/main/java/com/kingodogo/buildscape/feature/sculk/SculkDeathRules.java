package com.kingodogo.buildscape.feature.sculk;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class SculkDeathRules {
    private SculkDeathRules() {}

    public static boolean isInRange(double distanceSquared) {
        return distanceSquared >= 0.0 && distanceSquared <= 64.0;
    }

    public static boolean preferCatalyst(double distanceSquared, double nearestDistanceSquared, int positionOrder) {
        return isInRange(distanceSquared) && (distanceSquared < nearestDistanceSquared
                || distanceSquared == nearestDistanceSquared && positionOrder < 0);
    }

    public static final class ConsumptionGuard<T> {
        private final Set<T> consumed = Collections.newSetFromMap(new WeakHashMap<>());

        public boolean isConsumed(T entity) {
            return consumed.contains(entity);
        }

        public boolean tryConsume(T entity) {
            return consumed.add(entity);
        }

        public int experienceToDrop(T entity, int experience) {
            return isConsumed(entity) ? 0 : experience;
        }
    }
}
