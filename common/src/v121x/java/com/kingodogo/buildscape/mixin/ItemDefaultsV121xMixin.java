package com.kingodogo.buildscape.mixin;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemDefaultsV121xMixin {
    // Keep new stacks and the defaults used to encode component patches consistent.
    @Inject(method = "components", at = @At("RETURN"), cancellable = true)
    private void buildscape$stackableDefaults(CallbackInfoReturnable<DataComponentMap> cir) {
        Item self = (Item) (Object) this;
        if (self == Items.POTION || self == Items.CAKE) {
            cir.setReturnValue(DataComponentMap.builder().addAll(cir.getReturnValue())
                    .set(DataComponents.MAX_STACK_SIZE, self == Items.POTION ? 16 : 64).build());
        }
    }

    @Inject(method = "getDefaultMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void buildscape$defaultMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        Item self = (Item) (Object) this;
        if (self == Items.POTION) cir.setReturnValue(16);
        else if (self == Items.CAKE) cir.setReturnValue(64);
    }
}
