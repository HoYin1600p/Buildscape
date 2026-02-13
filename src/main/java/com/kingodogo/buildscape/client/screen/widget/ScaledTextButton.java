package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ScaledTextButton extends Button {
    
    public ScaledTextButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }
    
    @Override
    public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        float textScale = 1.0f;
        if (guiScale >= 3.0) {
            textScale = 0.75f;
        } else if (guiScale >= 2.5) {
            textScale = 0.85f;
        }

        String buttonText = getMessage().getString();

        int availableWidth = (int)(width / textScale);
        int textWidth = mc.font.width(buttonText);

        String displayText = buttonText;
        if (textWidth > availableWidth) {
            if (buttonText.contains("Ko-fi") || buttonText.contains("ko-fi")) {
                displayText = "Ko-fi";
            } else if (buttonText.contains("Edit GUI")) {
                displayText = "Edit GUI";
            } else {
                String truncated = mc.font.plainSubstrByWidth(buttonText, availableWidth - mc.font.width("..."));
                displayText = truncated + "...";
            }
        }

        int bgColor = this.isHovered ? 0xFFE0E0E0 : 0xFFC0C0C0;
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, bgColor);

        int borderColor = 0xFF000000;
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + 1, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + 1, this.y + this.height, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, borderColor);

        int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
        poseStack.pushPose();
        poseStack.translate(this.x + this.width / 2.0, this.y + (this.height - 8) / 2.0, 0);
        poseStack.scale(textScale, textScale, 1.0f);
        drawCenteredString(poseStack, 
            mc.font, 
            new net.minecraft.network.chat.TextComponent(displayText), 
            0, 
            0, 
            textColor
        );
        poseStack.popPose();
    }
}

