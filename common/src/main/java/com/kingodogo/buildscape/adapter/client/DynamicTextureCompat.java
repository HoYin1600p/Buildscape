package com.kingodogo.buildscape.adapter.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ExternalTextureProvider;
import net.minecraft.client.renderer.texture.TextureResources;
import net.minecraft.resources.Identifier;

public final class DynamicTextureCompat implements AutoCloseable {
    private final String name;
    private final NativeImage image;
    private Identifier registeredId;
    private TextureResources resources;

    public DynamicTextureCompat(String name, int width, int height, boolean clear) {
        this.name = name;
        this.image = new NativeImage(width, height, clear);
    }

    public DynamicTextureCompat(String name, NativeImage image) {
        this.name = name;
        this.image = image;
    }

    public NativeImage getPixels() {
        return image;
    }

    public void attachId(Identifier id) {
        this.registeredId = id;
    }

    public void upload() {
        if (registeredId == null) {
            registeredId = Identifier.fromNamespaceAndPath("buildscape", name);
        }
        var tm = Minecraft.getInstance().getTextureManager();
        if (resources != null) {
            tm.release(registeredId);
        }
        resources = TextureResources.from2dImage(() -> name, image);
        tm.registerProvider(registeredId, new ExternalTextureProvider(resources));
    }

    @Override
    public void close() {
        if (registeredId != null) {
            Minecraft.getInstance().getTextureManager().release(registeredId);
        }
        image.close();
    }
}
