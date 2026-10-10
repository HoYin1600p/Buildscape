package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.command.BuildscapeCommands;
import com.kingodogo.buildscape.command.FireworkTestCommand;
import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    private boolean buildscape$hasStarted = false;

    @Inject(method = "tickChildren", at = @At("TAIL"))
    private void buildscape$onServerTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        if (!buildscape$hasStarted) {
            buildscape$hasStarted = true;
            ModCommonEvents.onServerStarted(self);
            try {
                if (self.getCommands() != null && self.getCommands().getDispatcher() != null) {
                    BuildscapeCommands.register(self.getCommands().getDispatcher());
                    FireworkTestCommand.register(self.getCommands().getDispatcher());
                }
            } catch (Exception e) {
                System.err.println("[Buildscape] Failed to register commands on server start: " + e.getMessage());
            }
        }
        ModCommonEvents.onServerTick(self);
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void buildscape$onServerStop(CallbackInfo ci) {
        ModCommonEvents.onServerStopping();
    }
}
