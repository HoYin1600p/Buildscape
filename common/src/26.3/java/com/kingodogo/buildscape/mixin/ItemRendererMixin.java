package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Minecraft 26.3 picks glint render types per quad material (MaterialInfo.itemGlintRenderType and
 * itemGlintSpecialRenderType) inside ItemFeatureRenderer.prepareMainSubmit, and RenderTypes.glint() and
 * glintTranslucent() are gone. The submitted stack is pushed around prepareSubmit and both glint picks
 * are routed through FestiveSubmission.currentGlint.
 */
@Mixin(ItemFeatureRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "prepareSubmit(Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;)V", at = @At("HEAD"))
    private void buildscape$pushSubmittedStack(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        FestiveGlintHandler.push(FestiveSubmission.stackFor(submit));
    }

    @Inject(method = "prepareSubmit(Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;)V", at = @At("RETURN"))
    private void buildscape$popSubmittedStack(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }

    @Redirect(method = "prepareMainSubmit(Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;itemGlintRenderType()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType buildscape$festiveGlint(BakedQuad.MaterialInfo material) {
        return FestiveSubmission.currentGlint(material.itemGlintRenderType());
    }

    @Redirect(method = "prepareMainSubmit(Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;itemGlintSpecialRenderType()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType buildscape$festiveSpecialGlint(BakedQuad.MaterialInfo material) {
        return FestiveSubmission.currentGlint(material.itemGlintSpecialRenderType());
    }
}
