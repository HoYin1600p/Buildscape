package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

    public AnvilScreenMixin(AnvilMenu menu, Inventory playerInventory, Component title, ResourceLocation menuResource) {
        super(menu, playerInventory, title, menuResource);
    }

    @Inject(method = "renderLabels", at = @At("TAIL"))
    private void buildscape$renderZeroCostLabel(PoseStack poseStack, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.menu.getCost() == 0 && this.menu.getSlot(2).hasItem()) {
            Component component = new TranslatableComponent("container.repair.cost", 0);
            int k = this.imageWidth - 8 - this.font.width(component) - 2;
            fill(poseStack, k - 2, 67, this.imageWidth - 8, 79, 0x4F000000);
            this.font.drawShadow(poseStack, component, (float) k, 69.0F, 0x80FF20);
        }
    }
}
