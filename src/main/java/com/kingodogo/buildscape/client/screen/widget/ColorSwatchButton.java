package com.kingodogo.buildscape.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

public class ColorSwatchButton extends Button {
    private int color = 0xFFFFFF;
    private boolean isSelected = false;
    
    public ColorSwatchButton(int x, int y, int width, int height, int color, OnPress onPress) {
        super(x, y, width, height, TextComponent.EMPTY, onPress);
        this.color = color;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public int getColor() {
        return color;
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int swatchX = this.x;
        int swatchY = this.y;
        int swatchSize = this.width;

        int displayColor = color;
        if (!this.active) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            r = (int)(r * 0.4f);
            g = (int)(g * 0.4f);
            b = (int)(b * 0.4f);
            displayColor = (r << 16) | (g << 8) | b;
        }
        GuiComponent.fill(poseStack, swatchX, swatchY, swatchX + swatchSize, swatchY + swatchSize, 0xFF000000 | displayColor);

        int borderColor = this.active ? 0xFFFFFFFF : 0xFF666666;
        GuiComponent.fill(poseStack, swatchX, swatchY, swatchX + swatchSize, swatchY + 1, borderColor);
        GuiComponent.fill(poseStack, swatchX, swatchY + swatchSize - 1, swatchX + swatchSize, swatchY + swatchSize, borderColor);
        GuiComponent.fill(poseStack, swatchX, swatchY, swatchX + 1, swatchY + swatchSize, borderColor);
        GuiComponent.fill(poseStack, swatchX + swatchSize - 1, swatchY, swatchX + swatchSize, swatchY + swatchSize, borderColor);

        if (isSelected && this.active) {
            int borderSize = Math.max(1, com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(2));
            GuiComponent.fill(poseStack, swatchX - borderSize, swatchY - borderSize, swatchX + swatchSize + borderSize, swatchY, 0xFFFF00FF);
            GuiComponent.fill(poseStack, swatchX - borderSize, swatchY + swatchSize, swatchX + swatchSize + borderSize, swatchY + borderSize, 0xFFFF00FF);
            GuiComponent.fill(poseStack, swatchX - borderSize, swatchY - borderSize, swatchX, swatchY + swatchSize + borderSize, 0xFFFF00FF);
            GuiComponent.fill(poseStack, swatchX + swatchSize, swatchY - borderSize, swatchX + swatchSize + borderSize, swatchY + swatchSize + borderSize, 0xFFFF00FF);
        }
    }
}

