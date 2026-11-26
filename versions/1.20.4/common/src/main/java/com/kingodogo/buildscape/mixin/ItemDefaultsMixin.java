package com.kingodogo.buildscape.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemDefaultsMixin {
    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void buildscape$defaultMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        Item self = (Item) (Object) this;
        if (self == Items.POTION) cir.setReturnValue(16);
        else if (self == Items.CAKE) cir.setReturnValue(64);
    }
}
