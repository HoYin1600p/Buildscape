package com.kingodogo.buildscape.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Fabric reserves an array slot first; its tab constructor uses length minus one. */
@Mixin(targets = "com.kingodogo.buildscape.adapter.v118x.CreativeTabs118x", remap = false)
public abstract class CreativeTabAllocationMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target =
            "Lcom/kingodogo/buildscape/adapter/v118x/CreativeTabs118x$1;<init>(ILjava/lang/String;)V"), index = 0)
    private static int buildscape$reservedTabIndex(int index) {
        return index - 1;
    }
}
