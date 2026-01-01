package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ConfigCategoryButton extends Button {
    private boolean active = false;
    
    public ConfigCategoryButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Transparent background - no background rendering
        
        // Calculate text scale based on GUI scale (shrink text for high GUI scales)
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        float textScale = 1.0f;
        if (guiScale >= 3.0) {
            textScale = 0.75f; // Scale down for GUI scale 3-4
        } else if (guiScale >= 2.5) {
            textScale = 0.85f; // Scale down slightly for GUI scale 2.5-3
        }
        
        // Get button text and check if it needs truncation to fit sidebar width
        String buttonText = getMessage().getString();
        int textColor = active ? 0xFFFFFF : 0xCCCCCC;
        
        // Calculate available width with scaling (fit within button width which is 90% of sidebar)
        int availableWidth = (int)(width / textScale);
        int textWidth = mc.font.width(buttonText);
        
        // Truncate text if needed to fit within sidebar percentage width
        if (textWidth > availableWidth) {
            String truncated = mc.font.plainSubstrByWidth(buttonText, availableWidth - mc.font.width("..."));
            buttonText = truncated + "...";
        }
        
        // Draw text with scaling, centered within button (which is already sized to sidebar percentage)
        poseStack.pushPose();
        poseStack.translate(x + width / 2.0, y + (height - 8) / 2.0, 0);
        poseStack.scale(textScale, textScale, 1.0f);
        drawCenteredString(poseStack, 
            mc.font, 
            new net.minecraft.network.chat.TextComponent(buttonText), 
            0, 
            0, 
            textColor
        );
        poseStack.popPose();
    }
}

