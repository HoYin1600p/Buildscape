package com.kingodogo.buildscape.platform.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;

/** Loader access to client sprite internals, isolated from the server service provider. */
public final class NeoForgeClientAccess {
    private NeoForgeClientAccess() {}

    public static NativeImage spriteImage(SpriteContents contents) {
        return contents.originalImage;
    }
}
