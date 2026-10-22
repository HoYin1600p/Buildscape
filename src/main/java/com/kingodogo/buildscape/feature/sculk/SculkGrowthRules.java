package com.kingodogo.buildscape.feature.sculk;

import java.util.Random;
import java.util.function.BooleanSupplier;

final class SculkGrowthRules {
    static final int GROWTH_COST = 10;
    static final int NO_GROWTH_RADIUS = 4;
    static final int CHARGE_DECAY_RATE = 10;
    static final int ADDITIONAL_DECAY_RATE = 5;

    private SculkGrowthRules() {}

    static boolean insideNoGrowthRadius(double distanceSquared) {
        return distanceSquared < NO_GROWTH_RADIUS * NO_GROWTH_RADIUS;
    }

    static boolean hasGrowthCapacity(int nearby) {
        return nearby <= 2;
    }

    static int chargeOnDefaultCell(int charge, int decayDelay) {
        return decayDelay > 0 ? charge : 0;
    }

    static int nextDecayDelay(boolean sculkBehaviour, int decayDelay) {
        return sculkBehaviour ? 1 : Math.max(0, decayDelay - 1);
    }

    static int decayPenalty(int charge, double distanceSquared) {
        float distance = (float) Math.sqrt(distanceSquared) - NO_GROWTH_RADIUS;
        float factor = Math.min(1.0F, distance * distance / ((24 - NO_GROWTH_RADIUS) * (24 - NO_GROWTH_RADIUS)));
        return Math.max(1, (int) (charge * factor * 0.5F));
    }

    static ChargeUse useCharge(int charge, double distanceSquared, boolean growthSpace, Random random) {
        return useCharge(charge, distanceSquared, () -> growthSpace, random);
    }

    static ChargeUse useCharge(int charge, double distanceSquared, BooleanSupplier growthSpace, Random random) {
        if (charge == 0 || random.nextInt(CHARGE_DECAY_RATE) != 0) {
            return new ChargeUse(charge, false);
        }
        boolean inside = insideNoGrowthRadius(distanceSquared);
        if (!inside && growthSpace.getAsBoolean()) {
            boolean grow = random.nextInt(GROWTH_COST) < charge;
            return new ChargeUse(Math.max(0, charge - GROWTH_COST), grow);
        }
        int penalty = random.nextInt(ADDITIONAL_DECAY_RATE) == 0
                ? (inside ? 1 : decayPenalty(charge, distanceSquared))
                : 0;
        return new ChargeUse(charge - penalty, false);
    }

    record ChargeUse(int remainingCharge, boolean grow) {
    }
}
