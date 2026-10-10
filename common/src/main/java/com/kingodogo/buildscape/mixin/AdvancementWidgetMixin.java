package com.kingodogo.buildscape.mixin;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {

    @Dynamic
    @Redirect(
            method = "draw",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;getIcon()Lnet/minecraft/world/item/ItemStack;"),
            require = 0
    )
    private ItemStack cycleIconIfApplicable(DisplayInfo displayInfo) {
        return MixinFactory.cycleAdvancementIcon(this, displayInfo);
    }
}
