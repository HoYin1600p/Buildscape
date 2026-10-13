package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Minecraft 26.3 builds a tab's items in {@code buildContents} (the 1.18-era {@code fillItemList}
 * no longer exists), so the vertical-slab arrangement hooks only that method.
 */
@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Dynamic
    @Inject(method = "buildContents", at = @At("TAIL"))
    private void buildscape$arrangeContents(@Coerce Object parameters, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        NonNullList<ItemStack> ordered = NonNullList.create();
        ordered.addAll(tab.getDisplayItems());
        MixinFactory.arrangeCreativeTabs(tab, ordered);
        tab.getDisplayItems().clear();
        tab.getDisplayItems().addAll(ordered);
    }
}
