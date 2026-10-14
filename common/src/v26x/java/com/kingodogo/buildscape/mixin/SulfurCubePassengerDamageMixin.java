package com.kingodogo.buildscape.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class SulfurCubePassengerDamageMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void buildscape$noDamageWhileRidingSulfurCube(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (self.getVehicle() instanceof SulfurCube vehicle) {
            if (source.is(DamageTypeTags.IS_FALL)
                    || source.is(DamageTypes.FLY_INTO_WALL)
                    || source.getEntity() == vehicle
                    || source.getDirectEntity() == vehicle) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void buildscape$noFallDamageWhileRidingSulfurCube(double fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (self.getVehicle() instanceof SulfurCube) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void buildscape$resetFallDistanceWhileRidingSulfurCube(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (self.getVehicle() instanceof SulfurCube) {
            self.resetFallDistance();
        }
    }
}
