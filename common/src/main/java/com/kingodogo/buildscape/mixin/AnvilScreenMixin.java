package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {

    @Dynamic
    @Inject(method = "renderLabels(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", at = @At("TAIL"), require = 0)
    private void buildscape$renderZeroCostLabel(PoseStack poseStack, int mouseX, int mouseY, CallbackInfo ci) {
        MixinFactory.renderAnvilZeroCostLabel(this, poseStack);
    }
}
