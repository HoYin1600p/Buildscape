package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.client.MuffBlockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public abstract class SoundMuffMixin {
    @Dynamic
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$mute(SoundInstance sound, CallbackInfo ci) {
        if (sound != null && MuffBlockManager.shouldMute(Minecraft.getInstance().level,
                sound.getSource(), sound.getX(), sound.getY(), sound.getZ())) {
            ci.cancel();
        }
    }
}
