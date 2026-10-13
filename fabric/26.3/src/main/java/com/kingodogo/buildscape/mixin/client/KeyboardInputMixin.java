package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.client.ClientEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardInputMixin {
    @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("TAIL"))
    private void buildscape$keyInput(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (window == Minecraft.getInstance().getWindow().handle()) {
            ClientEvents.onKeyInput(event.key(), action);
        }
    }
}
