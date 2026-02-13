package com.kingodogo.buildscape.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Utility class for rendering custom scrollbars with drag-to-scroll
 * functionality.
 * Supports both mouse wheel scrolling and click-drag scrolling on content
 * areas.
 */
public class CustomScrollbarRenderer {
    // For Minecraft 1.18.2, use the two-argument constructor
    private static final ResourceLocation CUSTOM_SCROLLER_TEXTURE = new ResourceLocation("buildscape",
            "textures/gui/custom_scroller.png");

    private static final int SCROLLBAR_WIDTH = 8;
    private static final int MIN_THUMB_HEIGHT = 21; // Match new texture height + padding if needed

    // Drag state tracking
    private boolean isDraggingScrollbar = false;
    private boolean isDraggingContent = false;
    private double scrollbarDragStartY = 0;
    private double scrollbarDragStartOffset = 0;
    private double contentDragStartY = 0;
    private double contentDragStartOffset = 0;

    /**
     * Gets the scrollbar width.
     */
    public static int getScrollbarWidth() {
        return SCROLLBAR_WIDTH;
    }

    /**
     * Checks if currently dragging (scrollbar or content).
     */
    public boolean isDragging() {
        return isDraggingScrollbar || isDraggingContent;
    }

    /**
     * Renders the custom scrollbar with texture.
     * 
     * @param poseStack    The pose stack for rendering
     * @param x            X position of the scrollbar
     * @param y            Y position of the scrollbar
     * @param height       Height of the scrollbar track
     * @param scrollOffset Current scroll offset
     * @param maxScroll    Maximum scroll value
     * @param visibleRatio Ratio of visible content to total content (0.0-1.0)
     */
    public void renderScrollbar(PoseStack poseStack, int x, int y, int height,
            double scrollOffset, double maxScroll, double visibleRatio) {
        if (maxScroll <= 0) {
            return;
        }

        // Push pose and disable depth test to render on top (prevent clipping)
        poseStack.pushPose();
        poseStack.translate(0, 0, 400); // Render way in front
        RenderSystem.disableDepthTest(); // Disable depth test to prevent clipping

        // Calculate scroll position ratio
        double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;

        // Render scrollbar track (thin vertical line in the center)
        int trackX = x + (SCROLLBAR_WIDTH / 2);
        GuiComponent.fill(poseStack, trackX, y, trackX + 1, y + height, 0x80000000);

        // Render custom scrollbar thumb as a SINGLE fixed-size element
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CUSTOM_SCROLLER_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // The scroller texture is 8x17, render it at fixed size
        int scrollerWidth = 8; // Use correct texture width
        int scrollerHeight = 17; // Fixed height from texture

        // Calculate thumb position - it moves along the track
        // Leave space at top and bottom for the thumb
        int usableTrackHeight = height - scrollerHeight;
        int thumbY = y + (int) (scrollRatio * usableTrackHeight);

        // Render the scroller texture as a single fixed-size element
        // blit(poseStack, x, y, u, v, width, height, textureWidth, textureHeight)
        GuiComponent.blit(poseStack, x, thumbY, 0, 0, scrollerWidth, scrollerHeight, 8, 17);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest(); // Re-enable depth test

        poseStack.popPose(); // Restore pose
    }

    /**
     * Handles mouse click on scrollbar or content area.
     * 
     * @param mouseX          Mouse X position
     * @param mouseY          Mouse Y position
     * @param button          Mouse button (0 = left click)
     * @param scrollbarX      X position of scrollbar
     * @param scrollbarY      Y position of scrollbar
     * @param scrollbarHeight Height of scrollbar
     * @param contentX        X position of content area
     * @param contentY        Y position of content area
     * @param contentWidth    Width of content area
     * @param contentHeight   Height of content area
     * @param scrollOffset    Current scroll offset
     * @param maxScroll       Maximum scroll value
     * @param visibleRatio    Ratio of visible content to total content
     * @return New scroll offset, or -1 if click was not handled
     */
    public double handleMouseClick(double mouseX, double mouseY, int button,
            int scrollbarX, int scrollbarY, int scrollbarHeight,
            int contentX, int contentY, int contentWidth, int contentHeight,
            double scrollOffset, double maxScroll, double visibleRatio) {
        if (button != 0) {
            return -1;
        }

        // Check if clicking on scrollbar
        if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {

            isDraggingScrollbar = true;
            scrollbarDragStartY = mouseY;
            scrollbarDragStartOffset = scrollOffset;

            // Calculate thumb position with fixed scroller height
            int scrollerHeight = 17; // Fixed size
            int usableTrackHeight = scrollbarHeight - scrollerHeight;
            double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
            int thumbY = scrollbarY + (int) (scrollRatio * usableTrackHeight);

            // If clicking on track (not thumb), jump to that position
            if (mouseY < thumbY || mouseY > thumbY + scrollerHeight) {
                // Calculate new scroll position based on click
                double clickRatio = Math.max(0,
                        Math.min(1, (mouseY - scrollbarY - scrollerHeight / 2.0) / usableTrackHeight));
                return Math.max(0, Math.min(maxScroll, clickRatio * maxScroll));
            }

            return scrollOffset;
        }

        // Check if clicking on content area for drag-to-scroll
        /*
         * // DISABLED: Drag-to-scroll on content area blocks interaction with widgets
         * (buttons, fields)
         * if (mouseX >= contentX && mouseX <= contentX + contentWidth &&
         * mouseY >= contentY && mouseY <= contentY + contentHeight) {
         * 
         * isDraggingContent = true;
         * contentDragStartY = mouseY;
         * contentDragStartOffset = scrollOffset;
         * return scrollOffset;
         * }
         */

        return -1;
    }

    /**
     * Handles mouse drag for scrollbar or content dragging.
     * 
     * @param mouseY          Current mouse Y position
     * @param scrollbarY      Y position of scrollbar
     * @param scrollbarHeight Height of scrollbar
     * @param maxScroll       Maximum scroll value
     * @param visibleRatio    Ratio of visible content to total content
     * @param dragSensitivity Sensitivity multiplier for content dragging (1.0 =
     *                        normal)
     * @return New scroll offset, or -1 if drag was not handled
     */
    public double handleMouseDrag(double mouseY, int scrollbarY, int scrollbarHeight,
            double maxScroll, double visibleRatio, double dragSensitivity) {
        if (isDraggingScrollbar) {
            int scrollerHeight = 17; // Fixed size
            int usableTrackHeight = scrollbarHeight - scrollerHeight;

            double clampedMouseY = Math.max(scrollbarY, Math.min(scrollbarY + scrollbarHeight, mouseY));
            double mouseYRelative = clampedMouseY - scrollbarY;

            double thumbCenterRatio = usableTrackHeight > 0
                    ? Math.max(0, Math.min(1, (mouseYRelative - scrollerHeight / 2.0) / usableTrackHeight))
                    : 0;

            double newOffset = thumbCenterRatio * maxScroll;
            return Math.max(0, Math.min(maxScroll, newOffset));
        }

        if (isDraggingContent) {
            double dragDelta = (mouseY - contentDragStartY) * dragSensitivity;
            double newOffset = contentDragStartOffset - dragDelta;
            return Math.max(0, Math.min(maxScroll, newOffset));
        }

        return -1;
    }

    /**
     * Handles mouse release to stop dragging.
     * 
     * @param button Mouse button that was released
     * @return true if a drag was stopped, false otherwise
     */
    public boolean handleMouseRelease(int button) {
        if (button == 0) {
            boolean wasDragging = isDraggingScrollbar || isDraggingContent;
            isDraggingScrollbar = false;
            isDraggingContent = false;
            return wasDragging;
        }
        return false;
    }
}
