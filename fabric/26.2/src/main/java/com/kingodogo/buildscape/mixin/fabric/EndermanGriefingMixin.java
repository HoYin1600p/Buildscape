package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net.minecraft.world.entity.monster.EnderMan$EndermanTakeBlockGoal",
        "net.minecraft.world.entity.monster.EnderMan$EndermanLeaveBlockGoal"})
public abstract class EndermanGriefingMixin {
    @Shadow(remap = false) @Final private EnderMan enderman;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void buildscape$griefing(CallbackInfoReturnable<Boolean> cir) {
        if (ModCommonEvents.isMobGriefingDisabled(enderman, enderman.level())) cir.setReturnValue(false);
    }
}
