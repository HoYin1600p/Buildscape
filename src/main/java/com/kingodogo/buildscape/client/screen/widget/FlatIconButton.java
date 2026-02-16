
package com.kingodogo.buildscape.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class FlatIconButton extends Button {
    private final int colorNormal;
    private final int colorHover;
    private final int colorBorder;

    public FlatIconButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
        this.colorNormal = 0xFF333333;
        this.colorHover = 0xFF555555;
        this.colorBorder = 0xFF666666;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = isHoveredOrFocused();

        int bgColor = hovered ? colorHover : colorNormal;
        GuiComponent.fill(poseStack, x, y, x + width, y + height, bgColor);

        GuiComponent.fill(poseStack, x, y, x + width, y + 1, colorBorder); // Top
        GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, colorBorder); // Bottom
        GuiComponent.fill(poseStack, x, y, x + 1, y + height, colorBorder); // Left
        GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, colorBorder); // Right

        String text = getMessage().getString();
        int textColor = hovered ? 0xFFFFAA00 : 0xFFCCCCCC; // Orange on hover, Gray normal

        int textWidth = mc.font.width(text);
        mc.font.draw(poseStack, text, x + (width - textWidth) / 2 + 1, y + (height - 8) / 2, textColor);
    }
}
