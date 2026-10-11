package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.client.renderer.CopperChestRenderer;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Consumer;

public final class CopperChestClientEvents {
    private CopperChestClientEvents() {
    }

    // TODO S7b: forward the atlas id and sprite consumer from Forge's texture stitch event.
    public static void onTextureStitch(ResourceLocation atlas, Consumer<ResourceLocation> addSprite) {
        CopperChestRenderer.registerTextures(atlas, addSprite);
    }
}
