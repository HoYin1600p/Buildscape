package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInstance.class)
public interface ItemInstanceMixin {
    @Inject(method = "getMaxStackSize()I", at = @At("HEAD"), cancellable = true)
    private void buildscape$waterBottleMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        if (!((Object) this instanceof ItemStack self)) return;
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

}
