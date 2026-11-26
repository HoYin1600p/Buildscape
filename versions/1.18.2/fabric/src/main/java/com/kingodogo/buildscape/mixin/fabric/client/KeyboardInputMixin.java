package com.kingodogo.buildscape.mixin.fabric.client;

import com.kingodogo.buildscape.client.ClientEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardInputMixin {
    @Inject(method = "keyPress(JIIII)V", at = @At("TAIL"))
    private void buildscape$keyInput(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (window == Minecraft.getInstance().getWindow().getWindow()) ClientEvents.onKeyInput(key, action);
    }
}
