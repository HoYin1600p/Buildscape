package com.kingodogo.buildscape.platform.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;

/** Loader access to client sprite internals, isolated from the server service provider. */
@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
public final class NeoForgeClientAccess {
    private NeoForgeClientAccess() {}

    public static NativeImage spriteImage(SpriteContents contents) {
        try {
            var f = SpriteContents.class.getDeclaredField("originalImage");
            f.setAccessible(true);
            return (NativeImage) f.get(contents);
        } catch (Throwable t) {
            throw new IllegalStateException("Cannot access SpriteContents.originalImage", t);
        }
    }
}
