package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.Button;

@FunctionalInterface
public interface CustomButtonRenderer {
    void render(Button button, Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick);
}
