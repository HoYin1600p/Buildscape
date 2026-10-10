package com.kingodogo.buildscape.block.behavior;

import java.util.HashMap;
import java.util.Map;
public final class SafeDestroySpeed {

    private static final Map<String, Float> SPEED_OVERRIDES = new HashMap<>();

    static {
        SPEED_OVERRIDES.put("azalea_leaves", 0.2f);
        SPEED_OVERRIDES.put("flowering_azalea_leaves", 0.2f);
        SPEED_OVERRIDES.put("redstone_lamp", 0.3f);
        SPEED_OVERRIDES.put("hay_block", 0.5f);
        SPEED_OVERRIDES.put("carved_pumpkin", 1.0f);
        SPEED_OVERRIDES.put("target", 1.0f);
        SPEED_OVERRIDES.put("purpur_pillar", 1.5f);
    }

    private SafeDestroySpeed() {}
    public static float getSafeDestroySpeed(String blockId) {
        if (blockId == null) {
            return 2.0f;
        }
        String clean = blockId.toLowerCase().replace("minecraft:", "");
        return SPEED_OVERRIDES.getOrDefault(clean, 2.0f);
    }
}
