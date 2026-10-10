package com.kingodogo.buildscape.cosmetic.sign;

import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum SignFrameType {
    NONE("", () -> null, null),
    STRINGLIGHT("stringlight", () -> com.kingodogo.buildscape.platform.Services.PLATFORM.getItem(new CommonId("buildscape", "stringlight_frame")), new CommonId("buildscape", "block/stringlight_frame"));

    private final String id;
    private final Supplier<Item> itemSupplier;
    @Nullable
    private final CommonId modelLocation;

    private static final Map<String, SignFrameType> BY_ID = new HashMap<>();

    static {
        for (SignFrameType type : values()) {
            if (!type.id.isEmpty()) {
                BY_ID.put(type.id, type);
            }
        }
    }

    SignFrameType(String id, Supplier<Item> itemSupplier, @Nullable CommonId modelLocation) {
        this.id = id;
        this.itemSupplier = itemSupplier;
        this.modelLocation = modelLocation;
    }

    public String getId() {
        return this.id;
    }

    @Nullable
    public Item getItem() {
        return this.itemSupplier.get();
    }

    @Nullable
    public CommonId getModelLocation() {
        return this.modelLocation;
    }

    public static SignFrameType fromId(@Nullable String id) {
        if (id == null || id.isEmpty()) {
            return NONE;
        }
        return BY_ID.getOrDefault(id, NONE);
    }
}
