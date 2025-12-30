package com.kingodogo.buildscape.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

public class ModKeyBinds {

    public static final String CATEGORY = "key.categories.buildscape";

    public static final KeyMapping CINEMATIC_ZOOM = new KeyMapping(
            "key.buildscape.cinematic_zoom",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_C,
            CATEGORY
    );

    public static void register() {
    }
}
