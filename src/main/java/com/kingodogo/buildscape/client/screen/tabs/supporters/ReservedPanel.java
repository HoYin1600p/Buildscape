package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

/**
 * Reserved Panel (Panel 4)
 * 
 * Placeholder panel that renders a black bordered box only.
 * No logic for now - can be repurposed later without refactoring.
 * 
 * Dimensions: 33% width × 49% height
 * Position: (11%, 51%)
 */
public class ReservedPanel extends BasePanel {
    
    @Override
    public void init() {
        // No initialization needed for placeholder
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Render black bordered box
        // Border color: white/gray
        int borderColor = 0xFF808080;
        int fillColor = 0xFF000000;
        
        // Draw border (outer rectangle)
        GuiComponent.fill(poseStack, startX, startY, endX, endY, borderColor);
        
        // Draw fill (inner rectangle, leaving 1 pixel border)
        GuiComponent.fill(poseStack, startX + 1, startY + 1, endX - 1, endY - 1, fillColor);
    }
}

