package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.client.ZoomHandler;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraInputMixin {
    // NeoForge posts ComputeFov here for both the level and the hand FOV.
    @Inject(method = "modifyFovBasedOnDeathOrFluid(FF)F", at = @At("RETURN"), cancellable = true)
    private void buildscape$zoomFov(float partialTick, float fov, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() * ZoomHandler.getZoomLevel());
    }

    // Ordinal 1 is the normal entity rotation. Ordinal 0 is the minecart
    // branch, and later calls mirror third-person views or align sleeping players.
    @ModifyArgs(method = "alignWithEntity(F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1))
    private void buildscape$cinematicRotation(Args args) {
        float[] rotation = ZoomHandler.getSmoothedRotation(args.<Float>get(0), args.<Float>get(1));
        args.set(0, rotation[0]);
        args.set(1, rotation[1]);
    }
}
