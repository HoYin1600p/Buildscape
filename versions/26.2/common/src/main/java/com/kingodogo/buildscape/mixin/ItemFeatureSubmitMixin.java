package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFeatureRenderer.Submit.class)
public abstract class ItemFeatureSubmitMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void buildscape$rememberSubmittedStack(CallbackInfo ci) {
        FestiveSubmission.remember((ItemFeatureRenderer.Submit) (Object) this, FestiveGlintHandler.getCurrent());
    }
}
