package com.kingodogo.buildscape.util;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.network.chat.MutableComponent;
public final class ComponentHelper {

    private ComponentHelper() {}

    public static MutableComponent literal(String text) {
        return Services.PLATFORM.literal(text);
    }

    public static MutableComponent translatable(String key) {
        return Services.PLATFORM.translatable(key);
    }

    public static MutableComponent translatable(String key, Object... args) {
        return Services.PLATFORM.translatable(key, args);
    }

    public static MutableComponent empty() {
        return literal("");
    }
}
