package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.entity.ThrownTridentRenderer")
public class ThrownTridentRendererMixin {

    @Dynamic
    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), require = 0)
    private void buildscape$pushTridentStack(@Coerce Object thrownTrident, float entityYaw, float partialTicks, PoseStack poseStack, @Coerce Object bufferSource, int packedLight, CallbackInfo ci) {
        net.minecraft.world.item.ItemStack stack = MixinFactory.getThrownTridentItem(thrownTrident);
        if (stack != null && !stack.isEmpty()) {
            FestiveGlintHandler.push(stack);
        }
    }

    @Dynamic
    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"), require = 0)
    private void buildscape$popTridentStack(@Coerce Object thrownTrident, float entityYaw, float partialTicks, PoseStack poseStack, @Coerce Object bufferSource, int packedLight, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }
}

