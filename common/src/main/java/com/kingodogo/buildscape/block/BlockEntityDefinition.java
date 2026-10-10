package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildscapeCommon;

public class BlockEntityDefinition {
    private final String id;

    public BlockEntityDefinition(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNamespacedId() {
        return BuildscapeCommon.MOD_ID + ":" + id;
    }

    @Override
    public String toString() {
        return getNamespacedId();
    }
}
