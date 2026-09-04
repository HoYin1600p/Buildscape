package com.kingodogo.buildscape.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.function.Consumer;

public class IntSliderWidget extends AbstractSliderButton {
    private final int minValue;
    private final int maxValue;
    private int currentValue;
    private final Consumer<Integer> onValueChanged;

    public IntSliderWidget(int x, int y, int width, int height, Component message, int minValue, int maxValue, int initialValue, Consumer<Integer> onValueChanged) {
        super(x, y, width, height, message, (initialValue - minValue) / (double) (maxValue - minValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = initialValue;
        this.onValueChanged = onValueChanged;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(new TextComponent(String.valueOf(currentValue)));
    }

    @Override
    protected void applyValue() {
        this.currentValue = (int) Math.round(minValue + value * (maxValue - minValue));
        if (onValueChanged != null) {
            onValueChanged.accept(currentValue);
        }
        updateMessage();
    }

    public int getValue() {
        return currentValue;
    }

    public void setValue(int value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
        this.value = (currentValue - minValue) / (double) (maxValue - minValue);
        updateMessage();
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF666666;
        int backgroundColor = 0xFF222222;

        fill(poseStack, x, y, x + width, y + 1, borderColor);
        fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
        fill(poseStack, x, y, x + 1, y + height, borderColor);
        fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);
        fill(poseStack, x + 1, y + 1, x + width - 1, y + height - 1, backgroundColor);

        int handleWidth = 8;
        int handleX = x + (int) (this.value * (width - handleWidth));
        handleX = Math.max(x, Math.min(x + width - handleWidth, handleX));

        boolean hovered = isMouseOver(mouseX, mouseY);
        int handleColor = hovered ? 0xFFAAAAAA : 0xFF666666;
        fill(poseStack, handleX, y + 1, handleX + handleWidth, y + height - 1, handleColor);

        int color = getCustomColor(currentValue);
        drawCenteredString(poseStack, mc.font, getMessage(), x + width / 2, y + (height - 8) / 2, color);
    }

    private int getCustomColor(int value) {
        switch (value) {
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
}

