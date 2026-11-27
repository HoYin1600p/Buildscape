package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersMixin {

    @Dynamic
    @Inject(method = "<init>(I)V", at = @At("TAIL"), require = 0)
    private void buildscape$addFestiveGlintBuffers(CallbackInfo ci) {
        MixinFactory.registerFixedRenderBuffers(this);
    }
}
