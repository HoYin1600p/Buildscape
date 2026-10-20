package com.kingodogo.buildscape.feature.geyser;

import java.util.function.IntPredicate;

final class GeyserLiftRules {
    private GeyserLiftRules() {}

    static int unobstructedTop(int sulfurY, IntPredicate passableAtY) {
        for (int y = sulfurY + 1; y < sulfurY + 24; y++) {
            if (!passableAtY.test(y)) {
                return y - 1;
            }
        }
        return sulfurY + 23;
    }
}
