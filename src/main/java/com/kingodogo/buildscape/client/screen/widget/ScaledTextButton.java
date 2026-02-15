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
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        // Draw background with depth
        int bgColor = hovered ? 0xFF3D3D3D : 0xFF2D2D2D;
        int borderColor = hovered ? 0xFF808080 : 0xFF404040;

        // Border
        fill(poseStack, x, y, x + width, y + height, borderColor);
        // Inner background
        fill(poseStack, x + 1, y + 1, x + width - 1, y + height - 1, bgColor);

        double guiScale = mc.getWindow().getGuiScale();
        float baseTextScale = 1.0f;
        if (guiScale >= 3.0) {
            baseTextScale = 0.75f;
        } else if (guiScale >= 2.5) {
            baseTextScale = 0.85f;
        }

        String buttonText = getMessage().getString();
        int textColor = hovered ? 0xFFFFFF : 0xCCCCCC;

        int borderPadding = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(6);
        int availableWidth = width - borderPadding * 2;

        float finalScale = baseTextScale;
        int textWidth = mc.font.width(buttonText);

        // Use fixed scale, no dynamic shrinking
        // Handle specific truncation for Ko-fi/Edit GUI if text doesn't fit
        String displayText = buttonText;
        if (textWidth * finalScale > availableWidth) {
            if (buttonText.toLowerCase().contains("ko-fi")) {
                displayText = "Ko-fi";
                // If "Ko-fi" still doesn't fit, it will fall through to truncation
                if (mc.font.width(displayText) * finalScale > availableWidth) {
                    int maxCharsWidth = (int) (availableWidth / finalScale) - mc.font.width("...");
                    if (maxCharsWidth > 0) displayText = mc.font.plainSubstrByWidth(displayText, maxCharsWidth) + "...";
                }
            } else if (buttonText.contains("Edit GUI")) {
                displayText = "Edit GUI";
                if (mc.font.width(displayText) * finalScale > availableWidth) {
                    int maxCharsWidth = (int) (availableWidth / finalScale) - mc.font.width("...");
                    if (maxCharsWidth > 0) displayText = mc.font.plainSubstrByWidth(displayText, maxCharsWidth) + "...";
                }
            } else {
                int maxCharsWidth = (int) (availableWidth / finalScale) - mc.font.width("...");
                if (maxCharsWidth > 0) {
                    displayText = mc.font.plainSubstrByWidth(buttonText, maxCharsWidth) + "...";
                }
            }
        }

        poseStack.pushPose();
        float scaledHeight = 8 * finalScale;
        // Shift left by borderPadding and vertically center
        poseStack.translate(this.x + borderPadding, this.y + (this.height - scaledHeight) / 2.0, 0);
        poseStack.scale(finalScale, finalScale, 1.0f);

        mc.font.drawShadow(poseStack, displayText, 0, 0, textColor);
        poseStack.popPose();
    }
}
