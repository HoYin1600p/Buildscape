package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.client.particle.FireworkParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkParticles.Starter.class)
public abstract class FireworkStarterMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void buildscape$handleCustomFireworkShapes(CallbackInfo ci) {
        MixinFactory.handleFireworkStarterTick(this);
    }
}
