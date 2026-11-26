package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.client.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseInputMixin {
    @Dynamic
    @Inject(method = "onPress(JIII)V", at = @At("TAIL"), require = 0)
    private void buildscape$mouseInput(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (window == Minecraft.getInstance().getWindow().getWindow() && button == 1 && action == 1) {
            ClientEvents.onRightClick();
        }
    }
}
