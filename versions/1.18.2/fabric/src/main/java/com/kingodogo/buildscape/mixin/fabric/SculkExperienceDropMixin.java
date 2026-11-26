package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fabric has no experience drop event: a death a sculk catalyst absorbed drops no orbs. */
@Mixin(LivingEntity.class)
public abstract class SculkExperienceDropMixin {
    @Inject(method = "dropExperience", at = @At("HEAD"), cancellable = true)
    private void buildscape$sculkConsumedExperience(CallbackInfo ci) {
        if (com.kingodogo.buildscape.block.SculkCatalystHandler.isConsumed((LivingEntity) (Object) this)) {
            ci.cancel();
        }
    }
}
