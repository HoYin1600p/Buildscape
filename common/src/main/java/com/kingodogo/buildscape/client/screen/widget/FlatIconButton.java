package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class FlatIconButton {
    public int x;
    public int y;
    public int width;
    public int height;

    private final Button button;
    private final int colorNormal;
    private final int colorHover;
    private final int colorBorder;

    public FlatIconButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.colorNormal = 0xFF333333;
        this.colorHover = 0xFF555555;
        this.colorBorder = 0xFF666666;
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

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public void setActive(boolean active) { this.button.active = active; }
    public boolean isActive() { return this.button.active; }
    public void setVisible(boolean visible) { this.button.visible = visible; }
    public boolean isVisible() { return this.button.visible; }

    public void render(Button btn, Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        syncPosition();
        int curX = this.x;
        int curY = this.y;
        int curWidth = this.width;
        int curHeight = this.height;
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= curX && mouseY >= curY && mouseX < curX + curWidth && mouseY < curY + curHeight;

        int bgColor = hovered ? colorHover : colorNormal;
        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + curHeight, bgColor);

        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + 1, colorBorder);
        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY + curHeight - 1, curX + curWidth, curY + curHeight, colorBorder);
        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + 1, curY + curHeight, colorBorder);
        Services.PLATFORM.fill(poseStackOrGraphics, curX + curWidth - 1, curY, curX + curWidth, curY + curHeight, colorBorder);

        String text = btn.getMessage().getString();
        int textColor = hovered ? 0xFFFFAA00 : 0xFFCCCCCC;

        int textWidth = mc.font.width(text);
        Services.PLATFORM.draw(poseStackOrGraphics, mc.font, text, curX + (curWidth - textWidth) / 2 + 1, curY + (curHeight - 8) / 2, textColor);
    }
}
