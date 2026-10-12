package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.client.ZoomHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Dynamic
    @Inject(method = "getFov(Lnet/minecraft/client/Camera;FZ)D", at = @At("RETURN"), cancellable = true, require = 0)
    private void buildscape$zoomFov(Camera camera, float partialTick, boolean useSetting,
            CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(cir.getReturnValue() * ZoomHandler.getZoomLevel());
    }
}
