package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Button that scales and truncates text based on GUI scale to fit within the button.
 * For high GUI scales (3-4), text is scaled down and truncated if needed.
 */
public class ScaledTextButton extends Button {
    
    public ScaledTextButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }
    
    @Override
    public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Calculate text scale based on GUI scale (shrink text for high GUI scales)
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        float textScale = 1.0f;
        if (guiScale >= 3.0) {
            textScale = 0.75f; // Scale down for GUI scale 3-4
        } else if (guiScale >= 2.5) {
            textScale = 0.85f; // Scale down slightly for GUI scale 2.5-3
        }
        
        // Get button text and check if it needs truncation
        String buttonText = getMessage().getString();
        
        // Calculate available width with scaling
        int availableWidth = (int)(width / textScale);
        int textWidth = mc.font.width(buttonText);
        
        // Truncate text if needed (for high GUI scales)
        String displayText = buttonText;
        if (textWidth > availableWidth) {
            // For "Support on Ko-fi", show "Ko-fi" when truncated
            if (buttonText.contains("Ko-fi") || buttonText.contains("ko-fi")) {
                displayText = "Ko-fi";
            } else if (buttonText.contains("Edit GUI")) {
                displayText = "Edit GUI";
            } else {
                // Generic truncation
                String truncated = mc.font.plainSubstrByWidth(buttonText, availableWidth - mc.font.width("..."));
                displayText = truncated + "...";
            }
        }
        
        // Draw button background only (don't call super.renderButton to avoid duplicate text)
        // Render background manually - standard button appearance
        int bgColor = this.isHovered ? 0xFFE0E0E0 : 0xFFC0C0C0;
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, bgColor);
        
        // Draw button border
        int borderColor = 0xFF000000;
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + 1, borderColor); // Top
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, borderColor); // Bottom
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x, this.y, this.x + 1, this.y + this.height, borderColor); // Left
        net.minecraft.client.gui.GuiComponent.fill(poseStack, this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, borderColor); // Right
        
        // Draw text with scaling (only render once)
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

