package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.mojang.blaze3d.vertex.PoseStack;

public abstract class BasePanel {
    protected int width;
    protected int height;
    protected int startX;
    protected int startY;
    protected int endX;
    protected int endY;
    
    public void setBounds(int startX, int startY, int width, int height) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.endX = startX + width;
        this.endY = startY + height;
    }
    
    public abstract void init();
    
    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick);
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseClicked(mouseX, mouseY, button);
    }
    
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseScrolled(mouseX, mouseY, delta);
    }
    
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    protected boolean handleMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
    
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseReleased(mouseX, mouseY, button);
    }
    
    protected boolean handleMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    protected boolean isInside(double x, double y) {
        return x >= startX && x <= endX && y >= startY && y <= endY;
    }
    
    protected int[] toRelativeCoords(int screenX, int screenY) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        double actualGuiScale = mc.getWindow().getGuiScale();
        double fixedGuiScale = 2.0;

        double relativeXGuiScaled = screenX - startX;
        double relativeYGuiScaled = screenY - startY;

        double scaleFactor = fixedGuiScale / actualGuiScale;
        double relativeX = relativeXGuiScaled / scaleFactor;
        double relativeY = relativeYGuiScaled / scaleFactor;
        
        return new int[]{(int)relativeX, (int)relativeY};
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getStartX() {
        return startX;
    }
    
    public int getStartY() {
        return startY;
    }
}

