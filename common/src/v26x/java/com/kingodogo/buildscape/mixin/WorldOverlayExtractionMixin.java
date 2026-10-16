package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v26x.client.ClientWorldHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.extract.LevelExtractor")
public abstract class WorldOverlayExtractionMixin {
    @Shadow @Final private LevelRenderState levelRenderState;

    @Dynamic
    @Inject(method = "extract", at = @At("TAIL"), require = 0)
    private void buildscape$extractOverlays(DeltaTracker delta, Camera camera, float partialTick, CallbackInfo callback) {
        ClientWorldHooks.extractWorldOverlays(levelRenderState, camera);
    }
}
