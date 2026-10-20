package com.kingodogo.buildscape.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;

/** Render-thread-only coloured quads. Flush before text, textures, items or scissor changes. */
final class ConfigGuiBatch {
    private static final BufferBuilder BUFFER = new BufferBuilder(65536);
    private static boolean drawing;

    private ConfigGuiBatch() {}

    static void fill(PoseStack pose, int x0, int y0, int x1, int y1, int color) {
        gradient(pose, Math.min(x0, x1), Math.min(y0, y1), Math.max(x0, x1), Math.max(y0, y1),
                color, color, color, color);
    }

    /** Colours in clockwise order starting at the top left. Positions are transformed when queued. */
    static void gradient(PoseStack pose, int x0, int y0, int x1, int y1,
            int topLeft, int topRight, int bottomRight, int bottomLeft) {
        if (x0 == x1 || y0 == y1) {
            return;
        }
        if (!drawing) {
            BUFFER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            drawing = true;
        }
        Matrix4f matrix = pose.last().pose();
        vertex(matrix, x0, y1, bottomLeft);
        vertex(matrix, x1, y1, bottomRight);
        vertex(matrix, x1, y0, topRight);
        vertex(matrix, x0, y0, topLeft);
    }

    private static void vertex(Matrix4f matrix, int x, int y, int color) {
        BUFFER.vertex(matrix, x, y, 0).color((color >> 16) & 255, (color >> 8) & 255,
                color & 255, (color >>> 24) & 255).endVertex();
    }

    static void flush() {
        BufferBuilder ready = finishPending();
        if (ready == null) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.end(ready);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /** BufferUploader consumes a completed draw state; it does not call BufferBuilder.end(). */
    static BufferBuilder finishPending() {
        if (!drawing) {
            return null;
        }
        drawing = false;
        BUFFER.end();
        return BUFFER;
    }

    /** Two layered gradients produce the full HSV field without rasterising a thousand cells. */
    static void colorField(PoseStack pose, int x, int y, int width, int height, int hue, float alpha) {
        if (width <= 0 || height <= 0 || alpha <= 0) {
            return;
        }
        hue &= 0xFFFFFF;
        // During fades, draw a short strip mesh so every fragment has the requested opacity.
        // Layering two translucent quads would incorrectly make the bottom more opaque.
        if (alpha < 0.999f) {
            int opacity = Math.round(Math.max(0, Math.min(1, alpha)) * 255) << 24;
            for (int row = 0; row < height; row += 3) {
                int end = Math.min(height, row + 3);
                float top = 1 - row / (float) height, bottom = 1 - end / (float) height;
                gradient(pose, x, y + row, x + width, y + end,
                        opacity | brightness(0xFFFFFF, top), opacity | brightness(hue, top),
                        opacity | brightness(hue, bottom), opacity | brightness(0xFFFFFF, bottom));
            }
            return;
        }
        // Flush between layers: their alpha blending order must be explicit.
        int opacity = Math.round(Math.max(0, Math.min(1, alpha)) * 255) << 24;
        gradient(pose, x, y, x + width, y + height, opacity | 0xFFFFFF, opacity | hue,
                opacity | hue, opacity | 0xFFFFFF);
        flush();
        gradient(pose, x, y, x + width, y + height, 0, 0, opacity, opacity);
    }

    private static int brightness(int rgb, float factor) {
        return (Math.round(((rgb >> 16) & 255) * factor) << 16)
                | (Math.round(((rgb >> 8) & 255) * factor) << 8)
                | Math.round((rgb & 255) * factor);
    }
}
