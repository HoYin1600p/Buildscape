package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.client.renderer.CopperChestRenderer;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Consumer;

public final class CopperChestClientEvents {
    private CopperChestClientEvents() {
    }

    // The physical client loader forwards Forge's atlas and sprite consumer here.
    public static void onTextureStitch(ResourceLocation atlas, Consumer<ResourceLocation> addSprite) {
        CopperChestRenderer.registerTextures(atlas, addSprite);
    }
}
