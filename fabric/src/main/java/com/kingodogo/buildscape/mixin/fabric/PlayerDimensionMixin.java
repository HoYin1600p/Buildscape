package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class PlayerDimensionMixin {
    @Inject(method = "changeDimension", at = @At("RETURN"))
    private void buildscape$dimension(DimensionTransition transition, CallbackInfoReturnable<Entity> cir) {
        if (cir.getReturnValue() != null) com.kingodogo.buildscape.event.ModCommonEvents.onPlayerChangedDimension((ServerPlayer) (Object) this);
    }
}
