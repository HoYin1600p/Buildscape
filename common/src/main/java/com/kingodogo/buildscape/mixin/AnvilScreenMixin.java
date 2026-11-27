package com.kingodogo.buildscape.mixin;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {
    @Dynamic
    @Inject(require = 0, method = "renderLabels(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", at = @At("TAIL"))
    private void buildscape$renderZeroCostLabel(PoseStack graphics, int mouseX, int mouseY, CallbackInfo ci) {
        com.kingodogo.buildscape.mixinsupport.MixinFactory.renderAnvilZeroCostLabel(this, graphics);
    }
}
