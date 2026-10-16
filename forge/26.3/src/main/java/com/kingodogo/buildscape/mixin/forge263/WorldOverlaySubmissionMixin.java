package com.kingodogo.buildscape.mixin.forge263;

import com.kingodogo.buildscape.adapter.v263.client.ClientWorldHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Submit the shared extracted overlays before the feature frame is prepared. */
@Mixin(LevelRenderer.class)
public abstract class WorldOverlaySubmissionMixin {
    @Dynamic
    @Inject(method = "submitFeatures", at = @At("TAIL"), require = 0)
    private void buildscape$submitOverlays(LevelRenderState state, SubmitNodeCollector collector,
            boolean renderOutline, CallbackInfo callback) {
        ClientWorldHooks.collectWorldOverlays(new PoseStack(), collector, state);
    }
}
