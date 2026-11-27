package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ConfigCategoryButton {
    public int x;
    public int y;
    public int width;
    public int height;

    private final Button button;
    private boolean active = false;
    private float customTextScale = 1.0f;

    public ConfigCategoryButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.button = Services.PLATFORM.createCustomButton(x, y, width, height, message, onPress, this::render);
    }

    public Button getButton() {
        syncPosition();
        return button;
    }

    public Component getMessage() {
        return button.getMessage();
    }

    public void syncPosition() {
        WidgetLayoutHelper.setPosition(this.button, this.x, this.y);
        this.button.setWidth(this.width);
        WidgetLayoutHelper.setWidgetHeight(this.button, this.height);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTextScale(float scale) {
        this.customTextScale = scale;
    }

    public void setHeight(int height) {
        this.height = height;
        WidgetLayoutHelper.setWidgetHeight(this.button, height);
    }

    public void setWidth(int width) {
        this.width = width;
        this.button.setWidth(width);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        WidgetLayoutHelper.setPosition(this.button, x, y);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void render(Button btn, Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        syncPosition();
        Minecraft mc = Minecraft.getInstance();
        int curX = this.x;
        int curY = this.y;
        int curWidth = this.width;
        int curHeight = this.height;
        boolean hovered = mouseX >= curX && mouseY >= curY && mouseX < curX + curWidth && mouseY < curY + curHeight;

        int bgColor = active ? 0xFF1E5B3D : (hovered ? 0xFF3D3D3D : 0xFF2D2D2D);
        int borderColor = active ? 0xFF50FF8A : (hovered ? 0xFF808080 : 0xFF404040);

        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + curHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, curX + 1, curY + 1, curX + curWidth - 1, curY + curHeight - 1, bgColor);

        if (active) {
            Services.PLATFORM.fill(poseStackOrGraphics, curX + 1, curY + 1, curX + curWidth - 1, curY + 2, 0x40FFFFFF);
        }

        String buttonText = btn.getMessage().getString();
        int textColor = active ? 0xFFFFFF : (hovered ? 0xFFFFFF : 0xCCCCCC);

        int borderPadding = BuildScapeConfigScreen.scaleSize(6);
        int availableWidth = curWidth - borderPadding * 2;

        float finalScale = this.customTextScale;
        int textWidth = mc.font.width(buttonText);

        if (textWidth * finalScale > availableWidth) {
            int truncatedWidth = (int) ((availableWidth) / finalScale) - mc.font.width("...");
            if (truncatedWidth > 0) {
                String truncated = mc.font.plainSubstrByWidth(buttonText, truncatedWidth);
                buttonText = truncated + "...";
            }
        }

        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        float scaledHeight = 8 * finalScale;
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, curX + borderPadding, curY + (curHeight - scaledHeight) / 2.0f, 0);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, finalScale, finalScale);

        Services.PLATFORM.drawShadow(poseStackOrGraphics, mc.font, buttonText, 0, 0, textColor);
        Services.PLATFORM.popGuiPose(poseStackOrGraphics);
    }
}
