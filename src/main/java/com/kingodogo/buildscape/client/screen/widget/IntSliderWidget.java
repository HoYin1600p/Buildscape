package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.active) {
            return false;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}

