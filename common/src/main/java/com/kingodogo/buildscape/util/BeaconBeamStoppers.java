package com.kingodogo.buildscape.util;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.level.block.state.BlockState;

public final class BeaconBeamStoppers {
    private static final CommonId TAG = new CommonId("buildscape", "beacon_beam_stoppers");

    private BeaconBeamStoppers() {}

    public static boolean isStopper(BlockState state) {
        CommonId id = Services.PLATFORM.getBlockId(state.getBlock());
        if (id == null || (id.getNamespace().equals("buildscape") && id.getPath().equals("tinted_glass_jar"))) {
            return false;
        }
        return id.getPath().contains("tinted_glass") || Services.PLATFORM.isBlockInTag(state.getBlock(), TAG);
    }
}
