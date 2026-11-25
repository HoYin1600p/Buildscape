package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ScaledTextButton {

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean active = true;
    private final Button button;
    private int customNormalTextColor = 0;
    private int customHoveredTextColor = 0;
    private float customTextScale = 0.0f;

    public ScaledTextButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
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

    public void syncPosition() {
        WidgetLayoutHelper.setPosition(this.button, this.x, this.y);
        this.button.setWidth(this.width);
        WidgetLayoutHelper.setWidgetHeight(this.button, this.height);
        this.button.visible = this.visible;
        this.button.active = this.active;
    }

    public void setCustomTextColors(int normalColor, int hoveredColor) {
        this.customNormalTextColor = normalColor;
        this.customHoveredTextColor = hoveredColor;
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

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.button.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.button.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setMessage(Component message) {
        this.button.setMessage(message);
    }

    public Component getMessage() {
        return this.button.getMessage();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible || !this.active) return false;
        return Services.PLATFORM.widgetMouseClicked(this.button, mouseX, mouseY, button);
    }

    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        render(this.button, poseStackOrGraphics, mouseX, mouseY, partialTick);
    }

    public void render(Button btn, Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible || !btn.visible) return;
        syncPosition();
        Minecraft mc = Minecraft.getInstance();
        int curX = this.x;
        int curY = this.y;
        int curWidth = this.width;
        int curHeight = this.height;
        boolean hovered = mouseX >= curX && mouseY >= curY && mouseX < curX + curWidth && mouseY < curY + curHeight;

        int bgColor = hovered ? 0xFF3D3D3D : 0xFF2D2D2D;
        int borderColor = hovered ? 0xFF808080 : 0xFF404040;

        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + curHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, curX + 1, curY + 1, curX + curWidth - 1, curY + curHeight - 1, bgColor);

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

        Component message = btn.getMessage();
        String rawText = message.getString();

        int overrideTextColor = 0;
        if (hovered) {
            overrideTextColor = customHoveredTextColor;
        } else {
            overrideTextColor = customNormalTextColor;
        }

        int borderPadding = BuildScapeConfigScreen.scaleSize(6);
        int availableWidth = curWidth - borderPadding * 2;

        float finalScale = baseTextScale;
        int textWidth = mc.font.width(message);

        boolean useRaw = textWidth * finalScale > availableWidth;

        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        float scaledHeight = 8 * finalScale;

        float centeredY = curY + (curHeight - scaledHeight) / 2.0f;
        float centeredX = curX + (curWidth - (textWidth * finalScale)) / 2.0f;

        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, centeredX, centeredY, 0);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, finalScale, finalScale);

        if (useRaw || overrideTextColor != 0) {
            String textToRender = rawText;
            if (useRaw) {
                int maxCharsWidth = (int) (availableWidth / finalScale) - mc.font.width("...");
                if (maxCharsWidth > 0) textToRender = mc.font.plainSubstrByWidth(textToRender, maxCharsWidth) + "...";

                int newWidth = mc.font.width(textToRender);
                float newCenteredX = curX + (curWidth - (newWidth * finalScale)) / 2.0f;

                Services.PLATFORM.popGuiPose(poseStackOrGraphics);
                Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
                Services.PLATFORM.translateGuiPose(poseStackOrGraphics, newCenteredX, centeredY, 0);
                Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, finalScale, finalScale);
            }
            int color = (overrideTextColor != 0) ? overrideTextColor : (hovered ? 0xFFFFFF : 0xCCCCCC);
            Services.PLATFORM.drawShadow(poseStackOrGraphics, mc.font, textToRender, 0, 0, color);
        } else {
            Services.PLATFORM.drawShadow(poseStackOrGraphics, mc.font, rawText, 0, 0, 0xFFFFFF);
        }

        Services.PLATFORM.popGuiPose(poseStackOrGraphics);
    }
}
