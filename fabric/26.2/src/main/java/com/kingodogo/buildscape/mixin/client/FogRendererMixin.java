package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidFog;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    // NeoForge applies the same values through its fog events.
    @Inject(method = "setupFog", at = @At("RETURN"))
    private void buildscape$experienceFluidFog(Camera camera, int renderDistance, DeltaTracker deltaTracker,
                                               float darkenWorldAmount, ClientLevel level,
                                               CallbackInfoReturnable<FogData> cir) {
        ExperienceFluidFog.apply(camera, level, cir.getReturnValue());
    }
}
