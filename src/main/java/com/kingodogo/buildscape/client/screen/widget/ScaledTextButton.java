package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ScaledTextButton extends Button {

    public ScaledTextButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }

    private int customNormalTextColor = 0;
    private int customHoveredTextColor = 0;
    private float customTextScale = 0.0f;

    public void setCustomTextColors(int normalColor, int hoveredColor) {
        this.customNormalTextColor = normalColor;
        this.customHoveredTextColor = hoveredColor;
    }

    public void setTextScale(float scale) {
        this.customTextScale = scale;
    }

    @Override
    public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        int bgColor = hovered ? 0xFF3D3D3D : 0xFF2D2D2D;
        int borderColor = hovered ? 0xFF808080 : 0xFF404040;

        fill(poseStack, x, y, x + width, y + height, borderColor);
        fill(poseStack, x + 1, y + 1, x + width - 1, y + height - 1, bgColor);

        double guiScale = mc.getWindow().getGuiScale();
        float baseTextScale = 1.0f;
        if (guiScale >= 3.0) {
            baseTextScale = 0.75f;
        } else if (guiScale >= 2.5) {
            baseTextScale = 0.85f;
        }

        if (customTextScale > 0) {
            baseTextScale = customTextScale;
        }

        Component message = getMessage();
        String rawText = message.getString();

        int overrideTextColor = 0;
        if (hovered) {
            overrideTextColor = customHoveredTextColor;
        } else {
            overrideTextColor = customNormalTextColor;
        }

        int borderPadding = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(6);
        int availableWidth = width - borderPadding * 2;

        float finalScale = baseTextScale;
        int textWidth = mc.font.width(message);

        boolean useRaw = textWidth * finalScale > availableWidth;

        poseStack.pushPose();
        float scaledHeight = 8 * finalScale;

        float centeredY = this.y + (this.height - scaledHeight) / 2.0f;
        float centeredX = this.x + (this.width - (textWidth * finalScale)) / 2.0f;

        poseStack.translate(centeredX, centeredY, 0);
        poseStack.scale(finalScale, finalScale, 1.0f);

        if (useRaw || overrideTextColor != 0) {
            String textToRender = rawText;
            if (useRaw) {
                int maxCharsWidth = (int) (availableWidth / finalScale) - mc.font.width("...");
                if (maxCharsWidth > 0) textToRender = mc.font.plainSubstrByWidth(textToRender, maxCharsWidth) + "...";

                int newWidth = mc.font.width(textToRender);
                float newCenteredX = this.x + (this.width - (newWidth * finalScale)) / 2.0f;

                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(newCenteredX, centeredY, 0);
                poseStack.scale(finalScale, finalScale, 1.0f);
            }
            int color = (overrideTextColor != 0) ? overrideTextColor : (hovered ? 0xFFFFFF : 0xCCCCCC);
            mc.font.drawShadow(poseStack, textToRender, 0, 0, color);
        } else {
            mc.font.drawShadow(poseStack, message, 0, 0, 0xFFFFFF);
        }

        poseStack.popPose();
    }
}
