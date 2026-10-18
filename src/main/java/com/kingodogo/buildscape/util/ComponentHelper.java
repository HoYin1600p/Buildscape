package com.kingodogo.buildscape.util;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public final class ComponentHelper {

    private ComponentHelper() {}

    public static MutableComponent literal(String text) {
        return new TextComponent(text != null ? text : "");
    }

    public static MutableComponent translatable(String key) {
        return new TranslatableComponent(key);
    }

    public static MutableComponent translatable(String key, Object... args) {
        return new TranslatableComponent(key, args);
    }

    public static MutableComponent empty() {
        return literal("");
    }
}
