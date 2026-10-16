package com.kingodogo.buildscape.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.KeyMapping;

public class ModKeyBinds {

    public static final String CATEGORY = "key.categories.buildscape";

    public static final KeyMapping CINEMATIC_ZOOM = Services.PLATFORM.createKeyMapping(
            "key.buildscape.cinematic_zoom", InputConstants.KEY_C, CATEGORY);

    public static void register(java.util.function.Consumer<KeyMapping> registrar) {
        registrar.accept(CINEMATIC_ZOOM);
    }

    public static void tick() {
        while (CINEMATIC_ZOOM.consumeClick()) {
            if (!Services.PLATFORM.isScreenOpen()) ZoomHandler.toggleZoom();
        }
    }
}
