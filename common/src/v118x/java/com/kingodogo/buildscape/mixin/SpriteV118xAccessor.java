package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlasSprite.class)
public interface SpriteV118xAccessor {
    @Accessor("mainImage") NativeImage[] buildscape$getImages();
}
