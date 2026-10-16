package com.kingodogo.buildscape.adapter.v26x.client;

/** Converts the common framebuffer clip contract to the extractor's GUI coordinates. */
public record FramebufferScissor(int left, int top, int right, int bottom) {
    public static FramebufferScissor toGui(int x, int y, int width, int height,
            int framebufferHeight, double guiScale) {
        int left = (int) Math.floor(x / guiScale);
        int top = (int) Math.floor((framebufferHeight - y - Math.max(0, height)) / guiScale);
        int right = width <= 0 ? left : (int) Math.ceil((x + width) / guiScale);
        int bottom = height <= 0 ? top : (int) Math.ceil((framebufferHeight - y) / guiScale);
        return new FramebufferScissor(left, top, right, bottom);
    }
}
