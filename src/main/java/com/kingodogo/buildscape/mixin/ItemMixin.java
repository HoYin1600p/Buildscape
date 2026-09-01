package com.kingodogo.buildscape.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemMixin {

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void buildscape$waterBottleMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        // Only water bottles should be stackable to 16
        if (self.is(Items.POTION)) {
            if (PotionUtils.getPotion(self) == Potions.WATER) {
                cir.setReturnValue(16);
            } else {
                cir.setReturnValue(1);
            }
        }
    }

    @Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true)
    private void buildscape$festiveHasFoil(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (com.kingodogo.buildscape.util.FestiveGlintHelper.hasFestiveGlint(self)) {
            cir.setReturnValue(true);
        }
    }
}
