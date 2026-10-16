package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.ZoomHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseInputMixin {
    @Inject(method = "onPress(JIII)V", at = @At("TAIL"))
    private void buildscape$mouseInput(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (window == Minecraft.getInstance().getWindow().getWindow() && button == 1 && action == 1) {
            ClientEvents.onRightClick();
        }
    }

    @Inject(method = "onScroll(JDD)V", at = @At("HEAD"), cancellable = true)
    private void buildscape$zoomScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (window != mc.getWindow().getWindow() || mc.screen != null || !ZoomHandler.isZooming()) return;
        double delta = mc.options.discreteMouseScroll ? Math.signum(vertical) : vertical;
        ZoomHandler.handleScroll(delta * mc.options.mouseWheelSensitivity);
        ci.cancel();
    }
}
