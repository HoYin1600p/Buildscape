package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Inject(method = "buildContents", at = @At("TAIL"))
    private void buildscape$arrangeContents(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        NonNullList<ItemStack> ordered = NonNullList.create();
        ordered.addAll(tab.getDisplayItems());
        MixinFactory.arrangeCreativeTabs(tab, ordered);
        tab.getDisplayItems().clear();
        tab.getDisplayItems().addAll(ordered);
    }
}
