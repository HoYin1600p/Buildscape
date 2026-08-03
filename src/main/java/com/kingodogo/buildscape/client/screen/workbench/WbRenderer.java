package com.kingodogo.buildscape.client.screen.workbench;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * WbRenderer — Builders Workbench custom texture 9-slice rendering library.
 */
public final class WbRenderer {

    // ── Texture Resources ─────────────────────────────────────────────────────
    public static final ResourceLocation BG_WORKSTATION = new ResourceLocation("buildscape", "textures/gui/builders_workbench/builders_workbench_bg.png");
    public static final ResourceLocation BG_TOP_LEFT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_top_left.png");
    public static final ResourceLocation BG_TOP_RIGHT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_top_right.png");
    public static final ResourceLocation BG_BOTTOM_LEFT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_bottom_left.png");
    public static final ResourceLocation BG_BOTTOM_RIGHT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_bottom_right.png");
    public static final ResourceLocation BG_MIDDLE_TOP = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_middle_top.png");
    public static final ResourceLocation BG_MIDDLE_BOTTOM = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_middle_bottom.png");
    public static final ResourceLocation BG_MIDDLE_LEFT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_middle_left.png");
    public static final ResourceLocation BG_MIDDLE_RIGHT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_middle_right.png");
    public static final ResourceLocation BG_CENTER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/bg_center.png");
    public static final ResourceLocation SLOT_INPUT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/input_slot.png");
    public static final ResourceLocation SLOT_OUTPUT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/output_slot.png");
    public static final ResourceLocation SLOT_RESULT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/result_slot.png");
    public static final ResourceLocation BTN_ALL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/all.png");
    public static final ResourceLocation BTN_ALL_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/all_hover.png");
    public static final ResourceLocation BTN_ALL_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/all_selected.png");
    public static final ResourceLocation BTN_FILTERED = new ResourceLocation("buildscape", "textures/gui/builders_workbench/filtered.png");
    public static final ResourceLocation BTN_FILTERED_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/filtered_hover.png");
    public static final ResourceLocation BTN_FILTERED_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/filtered_selected.png");
    public static final ResourceLocation BTN_SURVIVAL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/survival_plus.png");
    public static final ResourceLocation BTN_SURVIVAL_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/survival_plus_hover.png");
    public static final ResourceLocation BTN_SURVIVAL_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/survival_plus_selected.png");
    public static final ResourceLocation POUCH_ICON = new ResourceLocation("buildscape", "textures/item/builders_pouch.png");
    // Colors
    public static final int COLOR_BG_FILL = 0xFFDA9F6C; // exact brown color of builders_workbench_bg center
    public static final ResourceLocation VANILLA_CONTAINER = new ResourceLocation("textures/gui/container/generic_54.png");

    private WbRenderer() {
    }

    // ── Float blit helpers to prevent edge-bleeding wrap-around ────────────────
    public static void blitFloat(PoseStack ps, int x, int y, int width, int height, float u0, float v0, float u1, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        Matrix4f matrix = ps.last().pose();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (float) x, (float) (y + height), 0.0F).uv(u0, v1).endVertex();
        bufferbuilder.vertex(matrix, (float) (x + width), (float) (y + height), 0.0F).uv(u1, v1).endVertex();
        bufferbuilder.vertex(matrix, (float) (x + width), (float) y, 0.0F).uv(u1, v0).endVertex();
        bufferbuilder.vertex(matrix, (float) x, (float) y, 0.0F).uv(u0, v0).endVertex();
        tesselator.end();
    }

    // ── 9-Slice Renderer ──────────────────────────────────────────────────────

    public static void drawTexture(PoseStack ps, ResourceLocation texture, int x, int y, int width, int height,
                                   float u, float v, float uW, float vH, float texW, float texH) {
        RenderSystem.setShaderTexture(0, texture);
        float inset = 0.01f;
        float u0 = (u + inset) / texW;
        float u1 = (u + uW - inset) / texW;
        float v0 = (v + inset) / texH;
        float v1 = (v + vH - inset) / texH;
        blitFloat(ps, x, y, width, height, u0, v0, u1, v1);
    }

    // ── Workstation Renderers ─────────────────────────────────────────────────

    public static void draw9Slice(PoseStack ps, ResourceLocation texture, int x, int y, int w, int h, int border) {
        int texW = 23;
        int texH = 23;
        int srcBorder = 10;
        RenderSystem.setShaderTexture(0, texture);

        // 1. Top-Left Corner
        GuiComponent.blit(ps, x, y, border, border, 0f, 0f, srcBorder, srcBorder, texW, texH);
        // 2. Top-Right Corner
        GuiComponent.blit(ps, x + w - border, y, border, border, (float) (texW - srcBorder), 0f, srcBorder, srcBorder, texW, texH);
        // 3. Bottom-Left Corner
        GuiComponent.blit(ps, x, y + h - border, border, border, 0f, (float) (texH - srcBorder), srcBorder, srcBorder, texW, texH);
        // 4. Bottom-Right Corner
        GuiComponent.blit(ps, x + w - border, y + h - border, border, border, (float) (texW - srcBorder), (float) (texH - srcBorder), srcBorder, srcBorder, texW, texH);

        // 5. Top Edge (Stretched)
        GuiComponent.blit(ps, x + border, y, w - border * 2, border, (float) (srcBorder + 1), 0f, 1, srcBorder, texW, texH);
        // 6. Bottom Edge (Stretched)
        GuiComponent.blit(ps, x + border, y + h - border, w - border * 2, border, (float) (srcBorder + 1), (float) (texH - srcBorder), 1, srcBorder, texW, texH);
        // 7. Left Edge (Stretched)
        GuiComponent.blit(ps, x, y + border, border, h - border * 2, 0f, (float) (srcBorder + 1), srcBorder, 1, texW, texH);
        // 8. Right Edge (Stretched)
        GuiComponent.blit(ps, x + w - border, y + border, border, h - border * 2, (float) (texW - srcBorder), (float) (srcBorder + 1), srcBorder, 1, texW, texH);

        // 9. Center (Stretched)
        GuiComponent.blit(ps, x + border, y + border, w - border * 2, h - border * 2, (float) (srcBorder + 1), (float) (srcBorder + 1), 1, 1, texW, texH);
    }

    public static void drawSeparated9Slice(PoseStack ps, int x, int y, int w, int h, int border) {
        // 1. Center (Stretched/Filled)
        drawTexture(ps, BG_CENTER, x + border, y + border, w - border * 2, h - border * 2, 0f, 0f, 1f, 1f, 1f, 1f);

        // 2. Top Edge
        drawTexture(ps, BG_MIDDLE_TOP, x + border, y, w - border * 2, border, 0f, 0f, 1f, 10f, 1f, 10f);

        // 3. Bottom Edge
        drawTexture(ps, BG_MIDDLE_BOTTOM, x + border, y + h - border, w - border * 2, border, 0f, 0f, 1f, 10f, 1f, 10f);

        // 4. Left Edge
        drawTexture(ps, BG_MIDDLE_LEFT, x, y + border, border, h - border * 2, 0f, 0f, 10f, 1f, 10f, 1f);

        // 5. Right Edge
        drawTexture(ps, BG_MIDDLE_RIGHT, x + w - border, y + border, border, h - border * 2, 0f, 0f, 10f, 1f, 10f, 1f);

        // 6. Corners (10x10 src, border x border dst)
        drawTexture(ps, BG_TOP_LEFT, x, y, border, border, 0f, 0f, 10f, 10f, 10f, 10f);
        drawTexture(ps, BG_TOP_RIGHT, x + w - border, y, border, border, 0f, 0f, 10f, 10f, 10f, 10f);
        drawTexture(ps, BG_BOTTOM_LEFT, x, y + h - border, border, border, 0f, 0f, 10f, 10f, 10f, 10f);
        drawTexture(ps, BG_BOTTOM_RIGHT, x + w - border, y + h - border, border, border, 0f, 0f, 10f, 10f, 10f, 10f);
    }

    public static void drawWorkstationBG(PoseStack ps, int x, int y, int w, int h) {
        // 1. Fill background color seamlessly first (solid flat brown)
        GuiComponent.fill(ps, x, y - 12, x + 50, y, COLOR_BG_FILL); // Left ear
        GuiComponent.fill(ps, x + w - 50, y - 12, x + w, y, COLOR_BG_FILL); // Right ear
        GuiComponent.fill(ps, x, y, x + w, y + h, COLOR_BG_FILL); // Main body

        // 2. Left Edge: single continuous vertical edge from y - 12 to y + h
        // Top-Left Corner at x, y - 12
        drawTexture(ps, BG_TOP_LEFT, x, y - 12, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);
        // Left Edge from y - 7 to y + h - 5
        drawTexture(ps, BG_MIDDLE_LEFT, x, y - 7, 5, h + 2, 0f, 0f, 10f, 1f, 10f, 1f);
        // Bottom-Left Corner at x, y + h - 5
        drawTexture(ps, BG_BOTTOM_LEFT, x, y + h - 5, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);

        // 3. Right Edge: single continuous vertical edge from y - 12 to y + h
        // Top-Right Corner at x + w - 5, y - 12
        drawTexture(ps, BG_TOP_RIGHT, x + w - 5, y - 12, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);
        // Right Edge from y - 7 to y + h - 5
        drawTexture(ps, BG_MIDDLE_RIGHT, x + w - 5, y - 7, 5, h + 2, 0f, 0f, 10f, 1f, 10f, 1f);
        // Bottom-Right Corner at x + w - 5, y + h - 5
        drawTexture(ps, BG_BOTTOM_RIGHT, x + w - 5, y + h - 5, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);

        // 4. Left ear top & right edges, Right ear top & left edges
        // Left ear top edge
        drawTexture(ps, BG_MIDDLE_TOP, x + 5, y - 12, 40, 5, 0f, 0f, 1f, 10f, 1f, 10f);
        // Left ear top-right corner
        drawTexture(ps, BG_TOP_RIGHT, x + 45, y - 12, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);
        // Left ear right edge from y - 7 to y
        drawTexture(ps, BG_MIDDLE_RIGHT, x + 45, y - 7, 5, 7, 0f, 0f, 10f, 1f, 10f, 1f);

        // Right ear top-left corner
        drawTexture(ps, BG_TOP_LEFT, x + w - 50, y - 12, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);
        // Right ear top edge
        drawTexture(ps, BG_MIDDLE_TOP, x + w - 45, y - 12, 40, 5, 0f, 0f, 1f, 10f, 1f, 10f);
        // Right ear left edge from y - 7 to y
        drawTexture(ps, BG_MIDDLE_LEFT, x + w - 50, y - 7, 5, 7, 0f, 0f, 10f, 1f, 10f, 1f);

        // 5. Dip bottom & inner corners
        // Left inner corner (RT shape at dip bottom)
        drawTexture(ps, BG_TOP_RIGHT, x + 45, y, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);
        // Dip bottom edge
        drawTexture(ps, BG_MIDDLE_TOP, x + 50, y, w - 100, 5, 0f, 0f, 1f, 10f, 1f, 10f);
        // Right inner corner (LT shape at dip bottom)
        drawTexture(ps, BG_TOP_LEFT, x + w - 50, y, 5, 5, 0f, 0f, 10f, 10f, 10f, 10f);

        // 6. Bottom edge of main body
        drawTexture(ps, BG_MIDDLE_BOTTOM, x + 5, y + h - 5, w - 10, 5, 0f, 0f, 1f, 10f, 1f, 10f);
    }

    /**
     * Draws the custom ears-shape workstation container with a cutout at the bottom for the player inventory.
     */
    public static void drawWorkstationBGWithCutout(PoseStack ps, int x, int y, int w, int h_top, int y_inv, int h_inv) {
        // 1. Draw workstation top panel
        drawWorkstationBG(ps, x, y, w, h_top);

        // 2. Draw left vertical strip (height = 72)
        drawInsetBox(ps, x, y_inv, 32, 72);

        // 3. Draw right vertical strip (height = 72)
        drawInsetBox(ps, x + w - 32, y_inv, 32, 72);

        // 4. Draw bottom bar under inventory
        drawInsetBox(ps, x + 32, y_inv + h_inv - 6, 176, 12);

        // 5. Connect the vertical borders of the cutout
        RenderSystem.setShaderTexture(0, BG_WORKSTATION);
        int connHeight = h_inv - 6 - 72;
        // Left cutout vertical border connector (RM edge: u=13, v=11)
        GuiComponent.blit(ps, x + 22, y_inv + 72, 10, connHeight, 13f, 11f, 10, 1, 23, 23);
        // Right cutout vertical border connector (LM edge: u=0, v=11)
        GuiComponent.blit(ps, x + w - 32, y_inv + 72, 10, connHeight, 0f, 11f, 10, 1, 23, 23);

        // 6. Seamlessly clear horizontal borders at the overlaps
        GuiComponent.fill(ps, x + 5, y_inv - 5, x + 27, y_inv + 5, COLOR_BG_FILL);
        GuiComponent.fill(ps, x + w - 27, y_inv - 5, x + w - 5, y_inv + 5, COLOR_BG_FILL);
    }

    /**
     * Draws standard 9-slice inset boxes.
     */
    public static void drawInsetBox(PoseStack ps, int x, int y, int w, int h) {
        drawSeparated9Slice(ps, x, y, w, h, 5);
    }

    // ── Filter Buttons ────────────────────────────────────────────────────────

    /**
     * Draws a 18x18 slot background using custom textures.
     */
    public static void drawSlotTexture(PoseStack ps, ResourceLocation texture, int x, int y) {
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(ps, x, y, 0, 0, 18, 18, 18, 18);
    }

    // ── Tab buttons ───────────────────────────────────────────────────────────

    public static void drawFilterButton(PoseStack ps, int x, int y, int index, int currentMask, boolean hovered) {
        ResourceLocation normalTex, hoverTex, selectedTex;
        if (index == 0) {
            normalTex = BTN_ALL;
            hoverTex = BTN_ALL_HOVER;
            selectedTex = BTN_ALL_SEL;
        } else if (index == 1) {
            normalTex = BTN_FILTERED;
            hoverTex = BTN_FILTERED_HOVER;
            selectedTex = BTN_FILTERED_SEL;
        } else {
            normalTex = BTN_SURVIVAL;
            hoverTex = BTN_SURVIVAL_HOVER;
            selectedTex = BTN_SURVIVAL_SEL;
        }

        ResourceLocation activeTex = (currentMask == index) ? selectedTex : (hovered ? hoverTex : normalTex);
        RenderSystem.setShaderTexture(0, activeTex);
        GuiComponent.blit(ps, x, y, 0, 0, 18, 18, 18, 18);
    }

    // ── Vanilla Inventory Rendering ──────────────────────────────────────────

    public static void drawTab(PoseStack ps, Font font, int x, int y, int w, int h,
                               String label, boolean active, boolean hovered) {
        // Render tab button inside top dip
        int bg = active ? 0xFF0D121B : (hovered ? 0xFF1E2838 : 0xFF070A10);
        int border = active ? 0xFF00FFDD : (hovered ? 0xFF0099AA : 0xFF2D3C55);

        // Draw beveled tab
        GuiComponent.fill(ps, x, y, x + w, y + h, bg);
        GuiComponent.fill(ps, x, y, x + w, y + 1, border); // top
        GuiComponent.fill(ps, x, y, x + 1, y + h, border); // left
        GuiComponent.fill(ps, x + w - 1, y, x + w, y + h, border); // right

        int textColor = active ? 0xFF00FFFF : (hovered ? 0xFFBBBBDD : 0xFF556A8A);
        int tw = font.width(label);
        font.draw(ps, new net.minecraft.network.chat.TextComponent(label), x + (w - tw) / 2.0f, y + (h - 8) / 2.0f, textColor);
    }

    public static void drawVanillaInventory(PoseStack ps, Font font, int x, int y, int w, int h) {
        RenderSystem.setShaderTexture(0, VANILLA_CONTAINER);
        GuiComponent.blit(ps, x, y, 0, 126, 176, 96, 256, 256);
    }

    // ── Animated processing arrow ─────────────────────────────────────────────

    public static void drawProcessArrow(PoseStack ps, int x, int y, int w, float progress) {
        // Draw track background (dark line)
        int trackY = y + 6; // Center vertically in a 16px high area (pouch is 16x16)

        // Draw a neat 3px thick track line
        GuiComponent.fill(ps, x, trackY, x + w - 6, trackY + 3, 0xFF0D121B);
        GuiComponent.fill(ps, x, trackY + 1, x + w - 6, trackY + 2, 0xFF2D3C55); // inner highlight

        // Draw an arrow head at the right end pointing right
        // Tip is at x + w, base is at x + w - 6
        int tipX = x + w;
        int baseY = trackY + 1;
        GuiComponent.fill(ps, tipX - 6, baseY - 3, tipX - 4, baseY + 6, 0xFF0D121B);
        GuiComponent.fill(ps, tipX - 4, baseY - 2, tipX - 2, baseY + 5, 0xFF0D121B);
        GuiComponent.fill(ps, tipX - 2, baseY - 1, tipX, baseY + 4, 0xFF0D121B);

        // Highlight for arrow head
        GuiComponent.fill(ps, tipX - 5, baseY - 2, tipX - 4, baseY + 5, 0xFF2D3C55);
        GuiComponent.fill(ps, tipX - 3, baseY - 1, tipX - 2, baseY + 4, 0xFF2D3C55);
        GuiComponent.fill(ps, tipX - 1, baseY, tipX, baseY + 3, 0xFF2D3C55);

        // If copying, draw the pouch icon moving from left to right along the track
        if (progress > 0.0f) {
            int pouchX = x + (int) (progress * (w - 16));
            RenderSystem.setShaderTexture(0, POUCH_ICON);
            blitFloat(ps, pouchX, y, 16, 16, 0f, 0f, 1f, 1f);
        }
    }

    // ── Action button ─────────────────────────────────────────────────────────

    public static void drawActionButton(PoseStack ps, Font font, int x, int y, int w, int h,
                                        String label, boolean hovered, boolean active, int accentColor) {
        int bg = active ? blendColors(0xFF070A0F, (60 << 24) | (accentColor & 0x00FFFFFF))
                : (hovered ? 0xFF1E2838 : 0xFF0D121B);
        int border = hovered || active ? accentColor : 0xFF2A374E;

        GuiComponent.fill(ps, x, y, x + w, y + h, bg);
        GuiComponent.fill(ps, x, y, x + w, y + 1, border);
        GuiComponent.fill(ps, x, y + h - 1, x + w, y + h, border);
        GuiComponent.fill(ps, x, y, x + 1, y + h, border);
        GuiComponent.fill(ps, x + w - 1, y, x + w, y + h, border);

        int textColor = hovered || active ? 0xFF00FFFF : 0xFF8A9AB0;
        int tw = font.width(label);
        font.draw(ps, new net.minecraft.network.chat.TextComponent(label), x + (w - tw) / 2.0f, y + (h - 8) / 2.0f, textColor);
    }

    // ── Animated border (marching ants) ──────────────────────────────────────

    public static void drawAnimatedBorder(PoseStack ps, int x, int y, int w, int h,
                                          int color, float tick) {
        int offset = (int) (tick * 2) % 8;
        for (int i = 0; i < w; i++) {
            if ((i + offset) % 8 < 4) {
                GuiComponent.fill(ps, x + i, y, x + i + 1, y + 1, color);
                GuiComponent.fill(ps, x + i, y + h - 1, x + i + 1, y + h, color);
            }
        }
        for (int i = 0; i < h; i++) {
            if ((i + offset) % 8 < 4) {
                GuiComponent.fill(ps, x, y + i, x + 1, y + i + 1, color);
                GuiComponent.fill(ps, x + w - 1, y + i, x + w, y + i + 1, color);
            }
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    public static int interpolateColor(int colorStart, int colorEnd, float ratio) {
        int a1 = (colorStart >> 24) & 0xFF;
        int r1 = (colorStart >> 16) & 0xFF;
        int g1 = (colorStart >> 8) & 0xFF;
        int b1 = colorStart & 0xFF;

        int a2 = (colorEnd >> 24) & 0xFF;
        int r2 = (colorEnd >> 16) & 0xFF;
        int g2 = (colorEnd >> 8) & 0xFF;
        int b2 = colorEnd & 0xFF;

        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int blendColors(int dst, int src) {
        int sa = (src >> 24) & 0xFF;
        int sr = (src >> 16) & 0xFF;
        int sg = (src >> 8) & 0xFF;
        int sb = src & 0xFF;
        int da = (dst >> 24) & 0xFF;
        int dr = (dst >> 16) & 0xFF;
        int dg = (dst >> 8) & 0xFF;
        int db = dst & 0xFF;
        int oa = sa + da * (255 - sa) / 255;
        int or_ = (sr * sa + dr * (255 - sa)) / 255;
        int og = (sg * sa + dg * (255 - sa)) / 255;
        int ob = (sb * sa + db * (255 - sa)) / 255;
        return (oa << 24) | (or_ << 16) | (og << 8) | ob;
    }
}
