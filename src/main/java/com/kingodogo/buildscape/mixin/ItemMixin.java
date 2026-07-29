package com.kingodogo.buildscape.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void buildscape$waterBottleMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        Item self = (Item) (Object) this;
        if (self == Items.POTION || self == Items.SPLASH_POTION || self == Items.LINGERING_POTION) {
            cir.setReturnValue(16);
        }
    }

    @Inject(method = "getItemStackLimit", at = @At("HEAD"), cancellable = true, remap = false)
    private void buildscape$waterBottleItemStackLimit(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) {
            cir.setReturnValue(16);
        }
    }
}
