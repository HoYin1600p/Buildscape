package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.mixinsupport.FestiveSubmission;
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

    @Inject(method = "submit", at = @At("HEAD"))
    private void buildscape$pushStack(CallbackInfo ci) {
        FestiveGlintHandler.push(FestiveSubmission.stackFor((ItemStackRenderState) (Object) this));
    }

    @Inject(method = "submit", at = @At("RETURN"))
    private void buildscape$popStack(CallbackInfo ci) {
        FestiveGlintHandler.pop();
    }
}
