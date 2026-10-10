package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.blockentity.AbstractSignRenderer")
public class SignRendererMixin {

    @Dynamic
    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("RETURN"), require = 0)
    private void buildscape$extractFrame(SignBlockEntity entity, @Coerce Object state, float partialTick,
            @Coerce Object camera, @Coerce Object breaking, CallbackInfo ci) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientSignFrames.extract(entity, state, partialTick);
    }

    @Dynamic
    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("TAIL"), require = 0)
    private void buildscape$submitFrame(@Coerce Object state, PoseStack pose, @Coerce Object collector,
            @Coerce Object camera, CallbackInfo ci) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientSignFrames.submit(state, pose, collector, camera);
    }

    @Dynamic
    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/SignBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("TAIL"),
            require = 0
    )
    private void buildscape$renderSignFrame(
            SignBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            @Coerce Object bufferSource,
            int combinedLight,
            int combinedOverlay,
            CallbackInfo ci
    ) {
        MixinFactory.renderSignFrame(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}

