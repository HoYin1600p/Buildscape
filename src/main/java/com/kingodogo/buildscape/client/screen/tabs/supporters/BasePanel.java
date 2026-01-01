package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Abstract base class for all panels in the Supporters-Only tab.
 * 
 * All panels use pixel-based positioning with bounds checking.
 * Coordinates are relative to panel origin (startX, startY).
 * 
 * width / height controls ONLY this panel size
 * posStartX / posStartY moves the entire panel
 * Changing this does NOT affect other panels
 */
public abstract class BasePanel {
    protected int width;
    protected int height;
    protected int startX;
    protected int startY;
    protected int endX;
    protected int endY;
    
    /**
     * Initialize panel with pixel-based bounds.
     * Called once during layout calculation.
     * 
     * @param startX Starting X position in pixels (absolute screen coordinates)
     * @param startY Starting Y position in pixels (absolute screen coordinates)
     * @param width Panel width in pixels
     * @param height Panel height in pixels
     */
    public void setBounds(int startX, int startY, int width, int height) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.endX = startX + width;
        this.endY = startY + height;
    }
    
    /**
     * Layout-time initialization.
     * Called after setBounds() to allow panel to set up internal components.
     */
    public abstract void init();
    
    /**
     * Render the panel.
     * All rendering must be relative to panel origin (startX, startY).
     * Nothing may render outside panel bounds.
     * 
     * @param poseStack The pose stack for rendering
     * @param mouseX Mouse X position (absolute screen coordinates)
     * @param mouseY Mouse Y position (absolute screen coordinates)
     * @param partialTick Partial tick for smooth animations
     */
    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick);
    
    /**
     * Handle mouse click events.
     * Only processes clicks within panel bounds.
     * 
     * @param mouseX Mouse X position (absolute screen coordinates)
     * @param mouseY Mouse Y position (absolute screen coordinates)
     * @param button Mouse button (0 = left, 1 = right, 2 = middle)
     * @return true if click was handled, false otherwise
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseClicked(mouseX, mouseY, button);
    }
    
    /**
     * Internal mouse click handler.
     * Override this to handle clicks within panel bounds.
     * Coordinates are absolute screen coordinates.
     */
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
    
    /**
     * Handle mouse scroll events.
     * Only processes scrolls within panel bounds.
     * 
     * @param mouseX Mouse X position (absolute screen coordinates)
     * @param mouseY Mouse Y position (absolute screen coordinates)
     * @param delta Scroll delta
     * @return true if scroll was handled, false otherwise
     */
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseScrolled(mouseX, mouseY, delta);
    }
    
    /**
     * Internal mouse scroll handler.
     * Override this to handle scrolls within panel bounds.
     */
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }
    
    /**
     * Handle mouse drag events.
     * Only processes drags within panel bounds.
     * 
     * @param mouseX Mouse X position (absolute screen coordinates)
     * @param mouseY Mouse Y position (absolute screen coordinates)
     * @param button Mouse button (0 = left, 1 = right, 2 = middle)
     * @param dragX Drag delta X
     * @param dragY Drag delta Y
     * @return true if drag was handled, false otherwise
     */
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    /**
     * Internal mouse drag handler.
     * Override this to handle drags within panel bounds.
     */
    protected boolean handleMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }
    
    /**
     * Handle mouse release events.
     * Only processes releases within panel bounds.
     * 
     * @param mouseX Mouse X position (absolute screen coordinates)
     * @param mouseY Mouse Y position (absolute screen coordinates)
     * @param button Mouse button (0 = left, 1 = right, 2 = middle)
     * @return true if release was handled, false otherwise
     */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return handleMouseReleased(mouseX, mouseY, button);
    }
    
    /**
     * Internal mouse release handler.
     * Override this to handle releases within panel bounds.
     */
    protected boolean handleMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    /**
     * Check if a point is inside the panel bounds.
     * 
     * Mouse coordinates and panel bounds are both in GUI-scaled space.
     * No conversion needed - direct comparison.
     * 
     * @param x X coordinate (absolute screen coordinates in GUI-scaled space)
     * @param y Y coordinate (absolute screen coordinates in GUI-scaled space)
     * @return true if point is inside panel bounds
     */
    protected boolean isInside(double x, double y) {
        // Panel bounds and mouse coordinates are both in GUI-scaled space
        // Direct comparison - no conversion needed
        return x >= startX && x <= endX && y >= startY && y <= endY;
    }
    
    /**
     * Convert absolute screen coordinates to panel-relative coordinates.
     * 
     * Mouse coordinates and panel bounds are both in GUI-scaled space.
     * Calculate relative coordinates, then convert to scale 2.0 space for rendering.
     * 
     * @param screenX Absolute screen X coordinate (in GUI-scaled space)
     * @param screenY Absolute screen Y coordinate (in GUI-scaled space)
     * @return Array with [relativeX, relativeY] in scale 2.0 space (for rendering)
     */
    protected int[] toRelativeCoords(int screenX, int screenY) {
        // Get actual GUI scale
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        double actualGuiScale = mc.getWindow().getGuiScale();
        double fixedGuiScale = 2.0;
        
        // Calculate relative coordinates in GUI-scaled space
        double relativeXGuiScaled = screenX - startX;
        double relativeYGuiScaled = screenY - startY;
        
        // Convert to scale 2.0 space for rendering (panels render at scale 2.0)
        // The scale transformation in render() will scale by (2.0 / actualGuiScale)
        // So we need to divide by scaleFactor to get coordinates in scale 2.0 space
        double scaleFactor = fixedGuiScale / actualGuiScale;
        double relativeX = relativeXGuiScaled / scaleFactor;
        double relativeY = relativeYGuiScaled / scaleFactor;
        
        return new int[]{(int)relativeX, (int)relativeY};
    }
    
    /**
     * Get panel width in pixels.
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Get panel height in pixels.
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Get panel start X position in pixels.
     */
    public int getStartX() {
        return startX;
    }
    
    /**
     * Get panel start Y position in pixels.
     */
    public int getStartY() {
        return startY;
    }
}

