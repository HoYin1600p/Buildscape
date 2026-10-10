package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.FestiveGlintHelper;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemMixin {

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void buildscape$waterBottleMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.is(Items.POTION)) {
            if (Services.PLATFORM.isWaterPotion(self)) {
                cir.setReturnValue(ModGameRules.isWaterBottleStackingEnabled() ? 16 : 1);
            } else {
                cir.setReturnValue(1);
            }
        } else if (self.is(Items.CAKE)) {
            cir.setReturnValue(ModGameRules.isCakeStackingEnabled() ? 64 : 1);
        }
    }

    @Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true)
    private void buildscape$festiveHasFoil(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (FestiveGlintHelper.hasFestiveGlint(self)) {
            cir.setReturnValue(true);
        }
    }
}
