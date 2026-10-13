package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.StrawBedHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forge and NeoForge fire PlayerSetSpawnEvent from setRespawnPosition, so every way of setting a spawn (bed,
 * /spawnpoint, respawn anchor) can be refused for a straw bed. Fabric's ALLOW_SETTING_SPAWN only runs on the sleep
 * path, so the command and other callers bypassed the straw bed rule; cancel at the common method instead.
 */
@Mixin(ServerPlayer.class)
public abstract class StrawBedSpawnMixin {
    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void buildscape$keepSpawnForStrawBed(ServerPlayer.RespawnConfig config, boolean showMessage, CallbackInfo ci) {
        if (config != null && StrawBedHandler.shouldCancelSpawn((ServerPlayer) (Object) this, config.respawnData().pos())) {
            ci.cancel();
        }
    }
}
