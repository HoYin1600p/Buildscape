package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class IntSliderWidget implements ICustomWidget {
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean active = true;
    private boolean focused = false;

    private final int minValue;
    private final int maxValue;
    private int currentValue;
    private double value;
    private final Consumer<Integer> onValueChanged;
    private Component message;

    public IntSliderWidget(int x, int y, int width, int height, Component message, int minValue, int maxValue, int initialValue, Consumer<Integer> onValueChanged) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = message;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = initialValue;
        this.value = (initialValue - minValue) / (double) (maxValue - minValue);
        this.onValueChanged = onValueChanged;
        updateMessage();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    protected void updateMessage() {
        this.setMessage(ComponentHelper.literal(String.valueOf(currentValue)));
    }

    public int getValue() {
        return currentValue;
    }

    public void setValue(int value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
        this.value = (currentValue - minValue) / (double) (maxValue - minValue);
        updateMessage();
    }

    private void setValueFromMouse(double mouseX) {
        int handleWidth = 8;
        this.value = Math.max(0.0, Math.min(1.0, (mouseX - (this.x + handleWidth / 2.0)) / (double) (this.width - handleWidth)));
        this.currentValue = (int) Math.round(minValue + this.value * (maxValue - minValue));
        if (onValueChanged != null) {
            onValueChanged.accept(currentValue);
        }
        updateMessage();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && button == 0 && isMouseOver(mouseX, mouseY)) {
            setValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.active && this.visible && button == 0) {
            setValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        Minecraft mc = Minecraft.getInstance();

        boolean hovered = isMouseOver(mouseX, mouseY);
        int borderColor = (hovered || focused) ? 0xFFFFFFFF : 0xFF666666;
        int backgroundColor = 0xFF222222;

        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + height - 1, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + width - 1, y, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + width - 1, y + height - 1, backgroundColor);

        int handleWidth = 8;
        int handleX = x + (int) (this.value * (width - handleWidth));
        handleX = Math.max(x, Math.min(x + width - handleWidth, handleX));

        int handleColor = hovered ? 0xFFAAAAAA : 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, handleX, y + 1, handleX + handleWidth, y + height - 1, handleColor);

        int color = getCustomColor(currentValue);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, mc.font, getMessage(), x + width / 2, y + (height - 8) / 2, color);
    }

    private int getCustomColor(int val) {
        switch (val) {
            case 1:
                return 0xFFFFFF;
            case 2:
                return 0x55FF55;
            case 3:
                return 0x55FFFF;
            case 4:
                return 0xFF5555;
            case 5:
                return 0xFF55FF;
            case 6:
                return 0xFFAA00;
            case 7:
                return 0xFFFF55;
            default:
                return 0xFFFFFF;
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
}
