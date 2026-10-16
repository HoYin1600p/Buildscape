package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.client.ZoomHandler;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraInputMixin {
    @Dynamic
    @ModifyArgs(method = "setup", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0), require = 0)
    private void buildscape$cinematicRotation(Args args) {
        float[] rotation = ZoomHandler.getSmoothedRotation(args.<Float>get(0), args.<Float>get(1));
        args.set(0, rotation[0]);
        args.set(1, rotation[1]);
    }
}
