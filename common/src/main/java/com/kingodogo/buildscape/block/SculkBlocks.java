package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.level.block.Block;

/** Registry lookups for the restored sculk family. */
final class SculkBlocks {
    private SculkBlocks() {}

    static Block sculk() {
        return get("sculk");
    }

    static Block vein() {
        return get("sculk_vein");
    }

    static Block catalyst() {
        return get("sculk_catalyst");
    }

    static Block sensor() {
        return get("sculk_sensor");
    }

    static Block shrieker() {
        return get("sculk_shrieker");
    }

    private static Block get(String path) {
        return Services.PLATFORM.getBlock(new CommonId("buildscape", path));
    }
}
