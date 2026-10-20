package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.PillarDisplayEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class PillarOutlineRendererMixin {
    @Shadow private PostChain entityEffect;
    @Shadow protected abstract boolean shouldShowEntityOutlines();
    @Unique private boolean buildscape$outlineProcessed;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void buildscape$beginOutlines(CallbackInfo callback) {
        buildscape$outlineProcessed = false;
        PillarDisplayEffects.beginFrame(shouldShowEntityOutlines());
    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"))
    private void buildscape$processOutlines(PostChain chain, float partialTick) {
        if (chain == entityEffect) {
            buildscape$outlineProcessed = true;
        }
        chain.process(partialTick);
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void buildscape$finishOutlines(PoseStack poseStack, float partialTick, long finishNanoTime,
            boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture,
            Matrix4f projectionMatrix, CallbackInfo callback) {
        if (PillarDisplayEffects.hasOutline() && !buildscape$outlineProcessed && shouldShowEntityOutlines()) {
            entityEffect.process(partialTick);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
    }
}
