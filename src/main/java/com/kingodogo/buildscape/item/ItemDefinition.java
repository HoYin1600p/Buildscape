package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.BuildscapeCommon;

public class ItemDefinition {
    private final String id;
    private final CommonItemProperties properties;

    public ItemDefinition(String id) {
        this(id, CommonItemProperties.of());
    }

    public ItemDefinition(String id, CommonItemProperties properties) {
        this.id = id;
        this.properties = properties != null ? properties : CommonItemProperties.of();
    }

    public String getId() {
        return id;
    }

    public CommonItemProperties getProperties() {
        return properties;
    }

    public String getNamespacedId() {
        return BuildscapeCommon.MOD_ID + ":" + id;
    }

    public net.minecraft.world.item.Item asItem() {
        return com.kingodogo.buildscape.platform.Services.PLATFORM.getItem(new com.kingodogo.buildscape.util.CommonId("buildscape", id));
    }

    public net.minecraft.world.item.ItemStack createStack(int count) {
        net.minecraft.world.item.Item item = asItem();
        return item != null ? new net.minecraft.world.item.ItemStack(item, count) : net.minecraft.world.item.ItemStack.EMPTY;
    }

    @Override
    public String toString() {
        return getNamespacedId() + " [" + properties.getItemType() + "]";
    }
}
