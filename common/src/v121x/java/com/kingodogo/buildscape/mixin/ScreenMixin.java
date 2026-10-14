package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** The tooltip text of every hovered stack is built here, so this is where the stack is captured. */
@Mixin(value = Screen.class, priority = 900)
public abstract class ScreenMixin {
    @Inject(method = "getTooltipFromItem", at = @At("RETURN"), cancellable = true)
    private static void buildscape$tooltipText(Minecraft minecraft, ItemStack stack, CallbackInfoReturnable<List<Component>> cir) {
        cir.setReturnValue(MixinFactory.get().prepareCustomTooltipText(stack, cir.getReturnValue()));
    }
}
