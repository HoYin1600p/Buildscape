package com.kingodogo.buildscape.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class BeaconBeamStoppers {
    private BeaconBeamStoppers() {}

    public static boolean isStopper(BlockState state) {
        ResourceLocation id = Registry.BLOCK.getKey(state.getBlock());
        return isStopper(id.toString(), state.is(Tags.BEACON_BEAM_STOPPERS));
    }

    public static boolean isStopper(String id, boolean tagged) {
        if ("buildscape:tinted_glass_jar".equals(id)) {
            return false;
        }
        return tagged || id.substring(id.indexOf(':') + 1).contains("tinted_glass");
    }

    private static final class Tags {
        private static final TagKey<Block> BEACON_BEAM_STOPPERS = TagKey.create(
                Registry.BLOCK_REGISTRY, new ResourceLocation("buildscape", "beacon_beam_stoppers"));
    }
}
