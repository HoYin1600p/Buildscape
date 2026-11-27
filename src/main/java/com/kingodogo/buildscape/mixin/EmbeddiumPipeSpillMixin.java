package com.kingodogo.buildscape.mixin;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer", remap = false)
public class EmbeddiumPipeSpillMixin {

    @Dynamic
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void buildscape$renderSpill(CallbackInfoReturnable<Boolean> cir) {
    }
}
