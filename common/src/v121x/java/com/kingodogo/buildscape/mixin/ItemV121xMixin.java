package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.component.DataComponents;
import com.kingodogo.buildscape.util.FestiveGlintHelper;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemV121xMixin {

    @Dynamic("1.21.1 component stack limits with cake and water-potion gamerules")
    @Inject(method = "getMaxStackSize()I", at = @At("HEAD"),
            cancellable = true)
    private void buildscape$ruleMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.is(Items.POTION)) {
            cir.setReturnValue(Services.PLATFORM.isWaterPotion(self)
                    && ModGameRules.isWaterBottleStackingEnabled()
                    ? Math.min(self.getOrDefault(DataComponents.MAX_STACK_SIZE, 16), 16) : 1);
        } else if (self.is(Items.CAKE)) {
            cir.setReturnValue(ModGameRules.isCakeStackingEnabled()
                    ? Math.min(self.getOrDefault(DataComponents.MAX_STACK_SIZE, 64), 64) : 1);
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
