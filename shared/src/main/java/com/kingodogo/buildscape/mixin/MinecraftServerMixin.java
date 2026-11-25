package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void buildscape$onServerStop(CallbackInfo ci) {
        ModCommonEvents.onServerStopping();
    }
}
