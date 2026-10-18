package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.GoldenDandelionGrowth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin {
    @Inject(method = "ageUp(IZ)V", at = @At("HEAD"), cancellable = true)
    private void buildscape$preventFrozenFeedingGrowth(int age, boolean forced, CallbackInfo ci) {
        AgeableMob mob = (AgeableMob) (Object) this;
        if (age > 0 && mob.isBaby() && GoldenDandelionGrowth.isFrozen(mob)) {
            ci.cancel();
        }
    }

    @Inject(method = "setAge", at = @At("HEAD"), cancellable = true)
    private void buildscape$preserveFrozenAge(int age, CallbackInfo ci) {
        AgeableMob mob = (AgeableMob) (Object) this;
        if (GoldenDandelionGrowth.isFrozen(mob) && mob.getAge() < 0 && age > mob.getAge()) {
            ci.cancel();
        }
    }

    @Inject(method = "setAge", at = @At("RETURN"))
    private void buildscape$clearAdultFrozenGrowth(int age, CallbackInfo ci) {
        AgeableMob mob = (AgeableMob) (Object) this;
        if (!mob.isBaby()) {
            GoldenDandelionGrowth.setFrozen(mob, false);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void buildscape$migrateLegacyFrozenGrowth(CompoundTag entityData, CallbackInfo ci) {
        AgeableMob mob = (AgeableMob) (Object) this;
        if (GoldenDandelionGrowth.consumeLegacyFlag(entityData) && mob.isBaby()) {
            GoldenDandelionGrowth.setFrozen(mob, true);
        }
        if (!mob.isBaby()) {
            GoldenDandelionGrowth.setFrozen(mob, false);
        }
    }
}
