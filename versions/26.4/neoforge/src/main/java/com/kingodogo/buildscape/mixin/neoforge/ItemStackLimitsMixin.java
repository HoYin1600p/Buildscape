package com.kingodogo.buildscape.mixin.neoforge;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Covers NeoForge's override of the ItemInstance default stack limit. */
@Mixin(ItemStack.class)
public abstract class ItemStackLimitsMixin {
    @Inject(method = "getMaxStackSize()I", at = @At("HEAD"), cancellable = true)
    private void buildscape$stackLimits(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.is(Items.POTION)) {
            cir.setReturnValue(Services.PLATFORM.isWaterPotion(self)
                    && ModGameRules.isWaterBottleStackingEnabled() ? 16 : 1);
        } else if (self.is(Items.CAKE)) {
            cir.setReturnValue(ModGameRules.isCakeStackingEnabled() ? 64 : 1);
        }
    }
}
