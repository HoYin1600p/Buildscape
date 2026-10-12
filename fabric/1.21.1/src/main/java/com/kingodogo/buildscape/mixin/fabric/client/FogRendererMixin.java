package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.adapter.v121x.ExperienceFluids;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;
    private static boolean buildscape$isExperience(Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null && ExperienceFluids.still().isSame(
                mc.level.getFluidState(camera.getBlockPosition()).getType());
    }
    @Dynamic
    @Inject(method = "setupColor", at = @At("TAIL"), require = 0)
    private static void buildscape$fluidColor(Camera camera, float partialTick, ClientLevel level,
            int renderDistance, float bossFog, CallbackInfo ci) {
        if (!buildscape$isExperience(camera)) return;
        fogRed = 0.3F;
        fogGreen = 0.9F;
        fogBlue = 0.1F;
        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
    }
    @Dynamic
    @Inject(method = "setupFog", at = @At("TAIL"), require = 0)
    private static void buildscape$fluidDistance(Camera camera, FogRenderer.FogMode mode,
            float distance, boolean thickFog, float partialTick, CallbackInfo ci) {
        if (!buildscape$isExperience(camera)) return;
        RenderSystem.setShaderFogStart(0.5F);
        RenderSystem.setShaderFogEnd(12.0F);
    }
}
