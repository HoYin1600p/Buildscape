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

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Dynamic
    @Inject(method = "fillItemList", at = @At("TAIL"), require = 0)
    private void buildscape$arrangeVerticalSlabs(NonNullList<ItemStack> items, CallbackInfo ci) {
        MixinFactory.arrangeCreativeTabs((CreativeModeTab) (Object) this, items);
    }

    @Dynamic
    @Inject(method = "buildContents", at = @At("TAIL"), require = 0)
    private void buildscape$arrangeContents(@Coerce Object parameters, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        NonNullList<ItemStack> ordered = NonNullList.create();
        ordered.addAll(tab.getDisplayItems());
        MixinFactory.arrangeCreativeTabs(tab, ordered);
        tab.getDisplayItems().clear();
        tab.getDisplayItems().addAll(ordered);
    }
}
