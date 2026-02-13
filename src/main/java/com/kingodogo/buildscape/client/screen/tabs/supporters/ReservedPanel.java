package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class ReservedPanel extends BasePanel {
    
    @Override
    public void init() {
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int borderColor = 0xFF808080;
        int fillColor = 0xFF000000;

        GuiComponent.fill(poseStack, startX, startY, endX, endY, borderColor);

        GuiComponent.fill(poseStack, startX + 1, startY + 1, endX - 1, endY - 1, fillColor);
    }
}

