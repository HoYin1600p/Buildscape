package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public abstract class ItemStackRenderStateMixin {
    @Inject(method = "clear", at = @At("HEAD"))
    private void buildscape$clearStack(CallbackInfo ci) {
        FestiveSubmission.forget((ItemStackRenderState) (Object) this);
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V", at = @At("HEAD"))
    private void buildscape$pushStack(PoseStack poses, SubmitNodeCollector collector, int light, int overlay,
            int outlineColor, CallbackInfo ci) {
        FestiveGlintHandler.push(FestiveSubmission.stackFor((ItemStackRenderState) (Object) this));
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V", at = @At("RETURN"))
    private void buildscape$popStack(PoseStack poses, SubmitNodeCollector collector, int light, int overlay,
            int outlineColor, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }
}
