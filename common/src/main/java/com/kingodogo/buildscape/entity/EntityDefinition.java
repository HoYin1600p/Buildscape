package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.BuildscapeCommon;

public class EntityDefinition {
    private final String id;

    public EntityDefinition(String id) {
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
