package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ConfigCategoryButton extends Button {
    private boolean active = false;
    private float customTextScale = 1.0f; // Default scale
    
    public ConfigCategoryButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTextScale(float scale) {
        this.customTextScale = scale;
    }
    
    @Override
    public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        // Draw background with depth
        int bgColor = active ? 0xFF1E5B3D : (hovered ? 0xFF3D3D3D : 0xFF2D2D2D);
        int borderColor = active ? 0xFF50FF8A : (hovered ? 0xFF808080 : 0xFF404040);

        // Border
        fill(poseStack, x, y, x + width, y + height, borderColor);
        // Inner background
        fill(poseStack, x + 1, y + 1, x + width - 1, y + height - 1, bgColor);

        // Glossy effect if active
        if (active) {
            fill(poseStack, x + 1, y + 1, x + width - 1, y + 2, 0x40FFFFFF);
        }

        float textScale = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getStandardTextScale();

        String buttonText = getMessage().getString();
        int textColor = active ? 0xFFFFFF : (hovered ? 0xFFFFFF : 0xCCCCCC);

        int borderPadding = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(6);
        int availableWidth = width - borderPadding * 2;

        // Use the custom scale set by the parent screen
        float finalScale = this.customTextScale;
        int textWidth = mc.font.width(buttonText);

        // Only truncate if it STILL doesn't fit (shouldn't happen with correct parent logic)
        if (textWidth * finalScale > availableWidth) {
            int truncatedWidth = (int) ((availableWidth) / finalScale) - mc.font.width("...");
            if (truncatedWidth > 0) {
                String truncated = mc.font.plainSubstrByWidth(buttonText, truncatedWidth);
                buttonText = truncated + "...";
            }
        }

        poseStack.pushPose();
        // Vertically center based on the scaled height
        float scaledHeight = 8 * finalScale;
        poseStack.translate(x + borderPadding, y + (height - scaledHeight) / 2.0f, 0);
        poseStack.scale(finalScale, finalScale, 1.0f);

        mc.font.drawShadow(poseStack, buttonText, 0, 0, textColor);
        poseStack.popPose();
    }
}

