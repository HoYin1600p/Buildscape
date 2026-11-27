package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    @Shadow @Final private Advancement advancement;
    @Shadow @Final private DisplayInfo display;

    @Dynamic
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;getIcon()Lnet/minecraft/world/item/ItemStack;"), require = 0)
    private ItemStack buildscape$cycleIcon(DisplayInfo displayInfo) {
        return MixinFactory.cycleAdvancementIcon(advancement, displayInfo);
    }
}