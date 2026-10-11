package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.platform.Services;
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
public class ItemMixin {

    @Dynamic("Forge 1.18.2 cake stacking; other VersionClusters retain their existing stack limits")
    @Inject(method = {"getMaxStackSize()I", "m_41741_()I"}, at = @At("HEAD"),
            cancellable = true, require = 0, remap = false)
    private void buildscape$legacyCakeMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        if (!Services.isVersionCluster("v118x")) return;
        ItemStack self = (ItemStack) (Object) this;
        if (self.is(Items.POTION)) {
            cir.setReturnValue(Services.PLATFORM.isWaterPotion(self)
                    && ModGameRules.isWaterBottleStackingEnabled() ? 16 : 1);
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
