package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.client.ClientEvents;
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
}
