package com.kingodogo.buildscape.client.screen.workbench;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * WbRenderer - drawing helpers for the Builders Workbench screens.
 *
 * <p>The static part of each builder tab (panel, frames, slot backgrounds and the
 * player inventory) is baked into a single background sheet. Everything that has
 * more than one visual state stays here and is drawn on top of that sheet:
 * tab buttons, filter buttons and the animated copy arrow.
 */
public final class WbRenderer {

    // Backgrounds
    public static final ResourceLocation BG_COLOR_BUILDER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_bg.png");
    public static final ResourceLocation BG_GRADIENT_BUILDER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradient_builder_bg.png");

    // Tabs - each sprite already contains the plate and its icon, one per state.
    public static final ResourceLocation TAB_COLOR = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_tab.png");
    public static final ResourceLocation TAB_COLOR_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_tab_hover.png");
    public static final ResourceLocation TAB_COLOR_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_tab_selected.png");
    public static final ResourceLocation TAB_GRADIENT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab.png");
    public static final ResourceLocation TAB_GRADIENT_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab_hover.png");
    public static final ResourceLocation TAB_GRADIENT_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab_selected.png");

    // Copy arrow
    public static final ResourceLocation BUILDERS_ARROW = new ResourceLocation("buildscape", "textures/gui/builders_workbench/builders_arrow.png");
    public static final ResourceLocation BUILDERS_ARROW_ACTIVE = new ResourceLocation("buildscape", "textures/gui/builders_workbench/builders_arrow_active.png");

    // Filter buttons
    public static final ResourceLocation BTN_ALL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/all.png");
    public static final ResourceLocation BTN_ALL_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/all_hover.png");
    public static final ResourceLocation BTN_ALL_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/all_selected.png");
    public static final ResourceLocation BTN_TRANSPARENT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/transparent.png");
    public static final ResourceLocation BTN_TRANSPARENT_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/transparent_hover.png");
    public static final ResourceLocation BTN_TRANSPARENT_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/transparent_selected.png");
    public static final ResourceLocation BTN_NON_FULL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/non_full.png");
    public static final ResourceLocation BTN_NON_FULL_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/non_full_hover.png");
    public static final ResourceLocation BTN_NON_FULL_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/non_full_selected.png");

    // Sizes
    /** Both builder background sheets are 256x256 with the artwork anchored at (0,0). */
    public static final int SHEET_SIZE = 256;
    /** Tab sprites are 17x17 with the plate and icon already composed by the artist. */
    public static final int TAB_SIZE = 17;
    public static final int BUTTON_SIZE = 18;
    public static final int ARROW_W = 48;
    public static final int ARROW_H = 16;

    private WbRenderer() {
    }

    /**
     * Blits with explicit float UVs. Needed for the partially filled arrow, where the
     * horizontal UV has to be cut at an arbitrary fraction instead of a whole pixel.
     */
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

    /**
     * Blits a full builder background out of its 256x256 sheet. The screen passes its
     * own imageWidth / imageHeight, so the sheet stays the single source of layout truth.
     */
    public static void drawBuilderBG(PoseStack ps, ResourceLocation texture, int x, int y, int w, int h) {
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(ps, x, y, 0f, 0f, w, h, SHEET_SIZE, SHEET_SIZE);
    }

    /**
     * Draws one tab. The icon is part of the sprite, so nothing is composed or scaled
     * here - picking the right state sprite is the whole job.
     */
    public static void drawTabButton(PoseStack ps, int x, int y, boolean active, boolean hovered,
                                     ResourceLocation normal, ResourceLocation hover, ResourceLocation selected) {
        ResourceLocation tex = active ? selected : (hovered ? hover : normal);
        RenderSystem.setShaderTexture(0, tex);
        GuiComponent.blit(ps, x, y, 0, 0, TAB_SIZE, TAB_SIZE, TAB_SIZE, TAB_SIZE);
    }

    public static void drawFilterButton(PoseStack ps, int x, int y, int index, int currentMask, boolean hovered) {
        ResourceLocation normalTex, hoverTex, selectedTex;
        if (index == 0) {
            normalTex = BTN_ALL;
            hoverTex = BTN_ALL_HOVER;
            selectedTex = BTN_ALL_SEL;
        } else if (index == 1) {
            normalTex = BTN_TRANSPARENT;
            hoverTex = BTN_TRANSPARENT_HOVER;
            selectedTex = BTN_TRANSPARENT_SEL;
        } else {
            normalTex = BTN_NON_FULL;
            hoverTex = BTN_NON_FULL_HOVER;
            selectedTex = BTN_NON_FULL_SEL;
        }

        ResourceLocation activeTex = (currentMask & (1 << index)) != 0
                ? selectedTex : (hovered ? hoverTex : normalTex);
        RenderSystem.setShaderTexture(0, activeTex);
        GuiComponent.blit(ps, x, y, 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
    }

    /**
     * Draws the idle arrow, then overlays the active variant clipped to {@code progress}
     * (0..1) so the arrow appears to fill from left to right while copying.
     */
    public static void drawCopyArrow(PoseStack ps, int x, int y, float progress) {
        RenderSystem.setShaderTexture(0, BUILDERS_ARROW);
        blitFloat(ps, x, y, ARROW_W, ARROW_H, 0f, 0f, 1f, 1f);

        float p = Math.max(0f, Math.min(1f, progress));
        if (p > 0f) {
            RenderSystem.setShaderTexture(0, BUILDERS_ARROW_ACTIVE);
            blitFloat(ps, x, y, (int) (ARROW_W * p), ARROW_H, 0f, 0f, p, 1f);
        }
    }
}
