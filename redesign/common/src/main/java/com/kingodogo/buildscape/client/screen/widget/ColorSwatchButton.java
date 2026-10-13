package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.client.gui.components.Button;

public class ColorSwatchButton {
    public int x;
    public int y;
    public int width;
    public int height;

    private int color = 0xFFFFFF;
    private boolean isSelected = false;
    public boolean visible = true;
    public boolean active = true;
    private final Button button;

    public ColorSwatchButton(int x, int y, int width, int height, int color, Button.OnPress onPress) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.button = Services.PLATFORM.createCustomButton(x, y, width, height, ComponentHelper.empty(), onPress, this::render);
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

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
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

    public void setActive(boolean active) {
        this.active = active;
        this.button.active = active;
    }

    public boolean isActive() {
        return this.button.active;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.button.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) return false;
        return Services.PLATFORM.widgetMouseClicked(this.button, mouseX, mouseY, button);
    }

    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;
        render(this.button, poseStackOrGraphics, mouseX, mouseY, partialTick);
    }

    public void render(Button btn, Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible || !btn.visible) return;
        syncPosition();
        int swatchX = this.x;
        int swatchY = this.y;
        int swatchSize = this.width;

        int displayColor = color;
        if (!btn.active) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            r = (int)(r * 0.4f);
            g = (int)(g * 0.4f);
            b = (int)(b * 0.4f);
            displayColor = (r << 16) | (g << 8) | b;
        }

        Services.PLATFORM.fill(poseStackOrGraphics, swatchX, swatchY, swatchX + swatchSize, swatchY + swatchSize, 0xFF000000 | (displayColor & 0xFFFFFF));

        int borderColor = btn.active ? 0xFFFFFFFF : 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, swatchX, swatchY, swatchX + swatchSize, swatchY + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, swatchX, swatchY + swatchSize - 1, swatchX + swatchSize, swatchY + swatchSize, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, swatchX, swatchY, swatchX + 1, swatchY + swatchSize, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, swatchX + swatchSize - 1, swatchY, swatchX + swatchSize, swatchY + swatchSize, borderColor);

        if (isSelected && btn.active) {
            int selColor = 0xFF000000;
            int gap = 1;
            Services.PLATFORM.fill(poseStackOrGraphics, swatchX - gap - 1, swatchY - gap - 1, swatchX + swatchSize + gap + 1, swatchY - gap, selColor);
            Services.PLATFORM.fill(poseStackOrGraphics, swatchX - gap - 1, swatchY + swatchSize + gap, swatchX + swatchSize + gap + 1, swatchY + swatchSize + gap + 1, selColor);
            Services.PLATFORM.fill(poseStackOrGraphics, swatchX - gap - 1, swatchY - gap, swatchX - gap, swatchY + swatchSize + gap, selColor);
            Services.PLATFORM.fill(poseStackOrGraphics, swatchX + swatchSize + gap, swatchY - gap, swatchX + swatchSize + gap + 1, swatchY + swatchSize + gap, selColor);
        }
    }
}
