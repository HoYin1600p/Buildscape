package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.event.HollowLogCrawlHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class HollowLogPlayerPoseMixin {
    // Player updates its pose after LivingEntity.tick, overwriting the shared tick hook.
    @Dynamic
    @Inject(method = "updatePlayerPose", at = @At("TAIL"), require = 0)
    private void buildscape$keepHollowCrawlPose(CallbackInfo ci) {
        HollowLogCrawlHandler.tickPlayer((Player) (Object) this);
    }
}
