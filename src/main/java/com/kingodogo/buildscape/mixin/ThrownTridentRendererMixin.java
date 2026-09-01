package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTridentRenderer.class)
public class ThrownTridentRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void buildscape$pushTridentStack(ThrownTrident thrownTrident, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        FestiveGlintHandler.push(((ThrownTridentAccessor) thrownTrident).getTridentItem());
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    private void buildscape$popTridentStack(ThrownTrident thrownTrident, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }
}
