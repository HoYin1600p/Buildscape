package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.StrawBedHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerSpawnMixin {
    // Forge's PlayerSetSpawnEvent covers all spawn changes, including calls outside sleeping.
    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void buildscape$cancelStrawSpawn(ResourceKey<Level> dimension, BlockPos pos, float angle,
                                           boolean forced, boolean sendMessage, CallbackInfo ci) {
        if (StrawBedHandler.shouldCancelSpawn((ServerPlayer) (Object) this, pos)) ci.cancel();
    }
}
