package com.kingodogo.buildscape.adapter;

import net.minecraft.resources.Identifier;

public final class AtlasIds {
    private AtlasIds() {}

    /** AtlasManager accepts an atlas id; render types still use the atlas texture path. */
    public static Identifier blocks() {
        return Identifier.fromNamespaceAndPath("minecraft", "blocks");
    }
}
