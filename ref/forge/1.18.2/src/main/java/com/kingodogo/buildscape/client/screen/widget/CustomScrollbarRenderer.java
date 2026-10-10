package com.kingodogo.buildscape.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class CustomScrollbarRenderer {
    private static final ResourceLocation CUSTOM_SCROLLER_TEXTURE = new ResourceLocation("buildscape",
            "textures/gui/custom_scroller.png");

    private static final int SCROLLBAR_WIDTH = 8;
    private static final int MIN_THUMB_HEIGHT = 21;

    private boolean isDraggingScrollbar = false;
    private boolean isDraggingContent = false;
    private double thumbClickOffsetY = 0;
    private double scrollbarDragStartY = 0;
    private double scrollbarDragStartOffset = 0;
    private double contentDragStartY = 0;
    private double contentDragStartOffset = 0;

    public static int getScrollbarWidth() {
        return SCROLLBAR_WIDTH;
    }

    public boolean isDragging() {
        return isDraggingScrollbar || isDraggingContent;
    }

    public void renderScrollbar(PoseStack poseStack, int x, int y, int height,
            double scrollOffset, double maxScroll, double visibleRatio) {
        if (maxScroll <= 0) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0, 0, 400);
        RenderSystem.disableDepthTest();

        double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;

        int trackX = x + (SCROLLBAR_WIDTH / 2);
        GuiComponent.fill(poseStack, trackX, y, trackX + 1, y + height, 0x80000000);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CUSTOM_SCROLLER_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int scrollerWidth = 8;
        int scrollerHeight = 17;

        int usableTrackHeight = height - scrollerHeight;
        int thumbY = y + (int) (scrollRatio * usableTrackHeight);

        GuiComponent.blit(poseStack, x, thumbY, 0, 0, scrollerWidth, scrollerHeight, 8, 17);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        poseStack.popPose();
    }

    public double handleMouseClick(double mouseX, double mouseY, int button,
            int scrollbarX, int scrollbarY, int scrollbarHeight,
            int contentX, int contentY, int contentWidth, int contentHeight,
            double scrollOffset, double maxScroll, double visibleRatio) {
        if (button != 0) {
            return -1;
        }

        if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH + 10 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {

            int scrollerHeight = 17;
            int usableTrackHeight = scrollbarHeight - scrollerHeight;
            double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
            int thumbY = scrollbarY + (int) (scrollRatio * usableTrackHeight);

            if (mouseY < thumbY || mouseY > thumbY + scrollerHeight) {
                thumbClickOffsetY = scrollerHeight / 2.0;
                double clickRatio = usableTrackHeight > 0
                        ? Math.max(0, Math.min(1, (mouseY - scrollbarY - thumbClickOffsetY) / usableTrackHeight))
                        : 0;
                scrollOffset = clickRatio * maxScroll;
            } else {
                thumbClickOffsetY = mouseY - thumbY;
            }

            isDraggingScrollbar = true;
            scrollbarDragStartY = mouseY;
            scrollbarDragStartOffset = scrollOffset;

            return scrollOffset;
        }

        return -1;
    }

    public double handleMouseDrag(double mouseY, int scrollbarY, int scrollbarHeight,
            double maxScroll, double visibleRatio, double dragSensitivity) {
        if (isDraggingScrollbar) {
            int scrollerHeight = 17;
            int usableTrackHeight = scrollbarHeight - scrollerHeight;

            if (usableTrackHeight <= 0) return 0;

            double targetThumbY = mouseY - thumbClickOffsetY;
            double thumbTargetRatio = Math.max(0, Math.min(1, (targetThumbY - scrollbarY) / usableTrackHeight));

            return thumbTargetRatio * maxScroll;
        }

        if (isDraggingContent) {
            double dragDelta = (mouseY - contentDragStartY) * dragSensitivity;
            double newOffset = contentDragStartOffset - dragDelta;
            return Math.max(0, Math.min(maxScroll, newOffset));
        }

        return -1;
    }

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
