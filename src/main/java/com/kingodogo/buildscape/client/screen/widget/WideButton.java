package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;

public class WideButton extends Button {
    
    public WideButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int bgColor = this.isHovered ? 0xFFE0E0E0 : 0xFFC0C0C0;
        if (!this.active) {
            bgColor = 0xFF808080;
        }

        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, bgColor);

        int borderColor = 0xFF000000;
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + 1, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + 1, this.y + this.height, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, borderColor);

        int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        drawCenteredString(poseStack, 
            mc.font, 
            this.getMessage(), 
            this.x + this.width / 2, 
            this.y + (this.height - 8) / 2, 
            textColor
        );
    }
}

