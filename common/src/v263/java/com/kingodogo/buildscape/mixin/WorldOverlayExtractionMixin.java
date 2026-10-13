package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v263.client.ClientWorldHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.extract.LevelExtractor")
public abstract class WorldOverlayExtractionMixin {
    @Shadow @Final private LevelRenderState levelRenderState;

    @Inject(method = "extract(Lnet/minecraft/client/DeltaTracker;Lnet/minecraft/client/Camera;F)V", at = @At("TAIL"))
    private void buildscape$extractOverlays(DeltaTracker delta, Camera camera, float partialTick, CallbackInfo callback) {
        ClientWorldHooks.extractWorldOverlays(levelRenderState, camera);
    }
}
