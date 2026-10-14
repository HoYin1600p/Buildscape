package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cake and potion stack limits follow the Buildscape game rules, as the shared ItemMixin does on Forge 1.18.2.
 * That mixin names the method with remap = false (SRG), so on Fabric it never applies; this one is remapped.
 */
@Mixin(ItemStack.class)
public class ItemStackMaxSizeMixin {
    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void buildscape$gameRuleMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.is(Items.POTION)) {
            cir.setReturnValue(Services.PLATFORM.isWaterPotion(self)
                    && ModGameRules.isWaterBottleStackingEnabled() ? 16 : 1);
        } else if (self.is(Items.CAKE)) {
            cir.setReturnValue(ModGameRules.isCakeStackingEnabled() ? 64 : 1);
        }
    }
}
