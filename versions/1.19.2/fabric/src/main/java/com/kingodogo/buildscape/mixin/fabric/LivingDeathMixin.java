package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingDeathMixin {
    @org.spongepowered.asm.mixin.Shadow protected boolean dead;
    @Inject(method = "die", at = @At("HEAD"))
    private void buildscape$death(DamageSource source, CallbackInfo ci) {
        var entity = (LivingEntity)(Object)this;
        if (!entity.isRemoved() && !dead) com.kingodogo.buildscape.event.FabricGameplayEvents.livingDeath(entity, source);
    }
}
