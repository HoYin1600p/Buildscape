package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.ZoomHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseInputMixin {
    @Inject(method = "onButton(JLnet/minecraft/client/input/MouseButtonInfo;I)V", at = @At("TAIL"))
    private void buildscape$mouseInput(long window, MouseButtonInfo button, int action, CallbackInfo ci) {
        if (window == Minecraft.getInstance().getWindow().handle() && button.button() == 1 && action == 1) {
            ClientEvents.onRightClick();
        }
    }

    // Match NeoForge's scroll event after vanilla scales and accumulates the
    // wheel movement, before either hotbar or spectator controls consume it.
    @Inject(method = "onScroll(JDD)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), cancellable = true)
    private void buildscape$zoomScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ClientPlatformHooks.isScreenOpen() || !ZoomHandler.isZooming()) return;
        Minecraft mc = Minecraft.getInstance();
        double delta = mc.options.discreteMouseScroll().get() ? Math.signum(vertical) : vertical;
        ZoomHandler.handleScroll(delta * mc.options.mouseWheelSensitivity().get());
        ci.cancel();
    }
}
