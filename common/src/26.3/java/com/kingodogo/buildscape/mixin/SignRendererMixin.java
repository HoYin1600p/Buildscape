package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v263.client.ClientSignFrames;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Minecraft 26.3 renders signs through extractRenderState and submit only; the old
 * render(SignBlockEntity, float, PoseStack, MultiBufferSource, int, int) entry point no longer
 * exists, so the sign frame is extracted and submitted through those two hooks.
 */
@Mixin(targets = "net.minecraft.client.renderer.blockentity.AbstractSignRenderer")
public class SignRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("RETURN"))
    private void buildscape$extractFrame(SignBlockEntity entity, SignRenderState state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking, CallbackInfo ci) {
        ClientSignFrames.extract(entity, state, partialTick);
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("TAIL"))
    private void buildscape$submitFrame(SignRenderState state, PoseStack pose, SubmitNodeCollector collector,
            CameraRenderState camera, CallbackInfo ci) {
        ClientSignFrames.submit(state, pose, collector, camera);
    }
}
