package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class WideButton {
    public int x;
    public int y;
    public int width;
    public int height;

    private final Button button;

    public WideButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
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
        boolean hovered = mouseX >= curX && mouseY >= curY && mouseX < curX + curWidth && mouseY < curY + curHeight;

        int bgColor = hovered ? 0xFFE0E0E0 : 0xFFC0C0C0;
        if (!btn.active) {
            bgColor = 0xFF808080;
        }

        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + curHeight, bgColor);

        int borderColor = 0xFF000000;
        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY + curHeight - 1, curX + curWidth, curY + curHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + 1, curY + curHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, curX + curWidth - 1, curY, curX + curWidth, curY + curHeight, borderColor);

        int textColor = btn.active ? 0xFFFFFF : 0xA0A0A0;
        Minecraft mc = Minecraft.getInstance();
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics,
            mc.font,
            btn.getMessage(),
            curX + curWidth / 2,
            curY + (curHeight - 8) / 2,
            textColor
        );
    }
}
