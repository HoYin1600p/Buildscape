package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.GoldenDandelionGrowth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin {
    @Redirect(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AgeableMob;setAge(I)V"
        )
    )
    private void buildscape$preserveFrozenAge(AgeableMob mob, int age) {
        if (GoldenDandelionGrowth.shouldAdvanceNaturally(GoldenDandelionGrowth.isFrozen(mob))) {
            mob.setAge(age);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void buildscape$migrateLegacyFrozenGrowth(CompoundTag entityData, CallbackInfo ci) {
        AgeableMob mob = (AgeableMob) (Object) this;
        if (GoldenDandelionGrowth.consumeLegacyFlag(entityData) && mob.isBaby()) {
            GoldenDandelionGrowth.setFrozen(mob, true);
        }
    }
}
