package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class PlayerDimensionMixin {
    @Unique private Level buildscape$previousLevel;

    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;",
            at = @At("HEAD"))
    private void buildscape$beforeDimension(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        buildscape$previousLevel = ((ServerPlayer) (Object) this).level();
    }

    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;",
            at = @At("RETURN"))
    private void buildscape$dimension(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer player = cir.getReturnValue();
        if (player != null && player.level() != buildscape$previousLevel) ModCommonEvents.onPlayerChangedDimension(player);
        buildscape$previousLevel = null;
    }
}
