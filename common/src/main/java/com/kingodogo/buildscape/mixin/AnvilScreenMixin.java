package com.kingodogo.buildscape.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {
    @Inject(method = "extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V", at = @At("TAIL"))
    private void buildscape$renderZeroCostLabel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        AnvilScreen screen = (AnvilScreen) (Object) this;
        if (screen.getMenu().getCost() != 0 || !screen.getMenu().getSlot(2).hasItem()) return;
        var font = Minecraft.getInstance().font;
        Component label = Component.translatable("container.repair.cost", 0);
        int x = 166 - font.width(label);
        graphics.fill(x - 2, 67, 168, 79, 0x4F000000);
        graphics.text(font, label, x, 69, 0xFF80FF20);
    }
}
