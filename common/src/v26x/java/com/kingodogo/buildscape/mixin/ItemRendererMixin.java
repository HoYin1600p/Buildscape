package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFeatureRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "prepareFoilSubmit", at = @At("HEAD"))
    private void buildscape$pushSubmittedStack(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        FestiveGlintHandler.push(FestiveSubmission.stackFor(submit));
    }

    @Inject(method = "prepareFoilSubmit", at = @At("RETURN"))
    private void buildscape$popSubmittedStack(ItemFeatureRenderer.Submit submit, CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }

    @Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType buildscape$festiveGlint() {
        return FestiveSubmission.currentGlint(RenderTypes.glint());
    }

    @Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;glintTranslucent()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType buildscape$festiveTranslucentGlint() {
        return FestiveSubmission.currentGlint(RenderTypes.glintTranslucent());
    }
}
