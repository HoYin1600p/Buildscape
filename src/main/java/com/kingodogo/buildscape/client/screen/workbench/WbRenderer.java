package com.kingodogo.buildscape.client.screen.workbench;

import com.kingodogo.buildscape.util.ColorGradientSolver;
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

    // Title graphics - baked lettering, drawn in place of the vanilla font.
    public static final ResourceLocation TITLE_COLOR = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_title.png");
    public static final ResourceLocation TITLE_GRADIENT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradient_builder_title.png");

    // Copy arrow
    public static final ResourceLocation BUILDERS_ARROW = new ResourceLocation("buildscape", "textures/gui/builders_workbench/builders_arrow.png");
    public static final ResourceLocation BUILDERS_ARROW_ACTIVE = new ResourceLocation("buildscape", "textures/gui/builders_workbench/builders_arrow_active.png");

    // Filter buttons
    public static final ResourceLocation BTN_SOLID = new ResourceLocation("buildscape", "textures/gui/builders_workbench/solid.png");
    public static final ResourceLocation BTN_SOLID_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/solid_hover.png");
    public static final ResourceLocation BTN_SOLID_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/solid_selected.png");
    public static final ResourceLocation BTN_SOLID_STRICT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/solid_shift_selected.png");
    public static final ResourceLocation BTN_TRANSPARENT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/transparent.png");
    public static final ResourceLocation BTN_TRANSPARENT_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/transparent_hover.png");
    public static final ResourceLocation BTN_TRANSPARENT_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/transparent_selected.png");
    public static final ResourceLocation BTN_TRANSPARENT_STRICT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/transparent_shift_selected.png");
    public static final ResourceLocation BTN_NON_FULL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/non_full.png");
    public static final ResourceLocation BTN_NON_FULL_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/non_full_hover.png");
    public static final ResourceLocation BTN_NON_FULL_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/non_full_selected.png");
    public static final ResourceLocation BTN_NON_FULL_STRICT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/non_full_shift_selected.png");
    public static final ResourceLocation BTN_SINGLE_TEXTURE = new ResourceLocation("buildscape", "textures/gui/builders_workbench/single_texture.png");
    public static final ResourceLocation BTN_SINGLE_TEXTURE_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/single_texture_hover.png");
    public static final ResourceLocation BTN_SINGLE_TEXTURE_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/single_texture_selected.png");
    public static final ResourceLocation BTN_MATCH_SHAPE = new ResourceLocation("buildscape", "textures/gui/builders_workbench/match_shape.png");
    public static final ResourceLocation BTN_MATCH_SHAPE_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/match_shape_hover.png");
    public static final ResourceLocation BTN_MATCH_SHAPE_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/match_shape_selected.png");

    // Sizes
    /** Both builder background sheets are 256x256 with the artwork anchored at (0,0). */
    public static final int SHEET_SIZE = 256;
    /** Tab sprites are 17x17 with the plate and icon already composed by the artist. */
    public static final int TAB_SIZE = 17;
    public static final int BUTTON_SIZE = 18;
    public static final int MODIFIER_SIZE = 11;
    public static final int ARROW_W = 48;
    public static final int ARROW_H = 16;
    /** Every title graphic is baked at (0,0) of a 128x16 sheet. */
    public static final int TITLE_SHEET_W = 128;
    public static final int TITLE_SHEET_H = 16;

    // Title graphic metrics on their 128x16 sheets: {width, height, bodyY, bodyHeight}.
    // bodyY/bodyHeight describe the block of actual letters. They differ from the full
    // ink box when a glyph reaches above the caps line - the pouch apostrophe occupies
    // row 0 on its own, and centring the full box on that row visibly drops the whole
    // caption. Centring the letter body instead makes all three captions sit alike.
    public static final int[] TITLE_INK_COLOR = {60, 7, 0, 7};
    public static final int[] TITLE_INK_GRADIENT = {74, 7, 0, 7};
    public static final int[] TITLE_INK_POUCH = {68, 8, 1, 7};

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
        ResourceLocation normalTex, hoverTex, selectedTex, strictTex;
        if (index == 0) {
            normalTex = BTN_SOLID;
            hoverTex = BTN_SOLID_HOVER;
            selectedTex = BTN_SOLID_SEL;
            strictTex = BTN_SOLID_STRICT;
        } else if (index == 1) {
            normalTex = BTN_TRANSPARENT;
            hoverTex = BTN_TRANSPARENT_HOVER;
            selectedTex = BTN_TRANSPARENT_SEL;
            strictTex = BTN_TRANSPARENT_STRICT;
        } else {
            normalTex = BTN_NON_FULL;
            hoverTex = BTN_NON_FULL_HOVER;
            selectedTex = BTN_NON_FULL_SEL;
            strictTex = BTN_NON_FULL_STRICT;
        }

        int categoryBit = 1 << index;
        int strictBit = categoryBit << ColorGradientSolver.STRICT_SHIFT;
        ResourceLocation activeTex = (currentMask & strictBit) != 0 ? strictTex
                : (currentMask & categoryBit) != 0 ? selectedTex
                : hovered ? hoverTex : normalTex;
        RenderSystem.setShaderTexture(0, activeTex);
        GuiComponent.blit(ps, x, y, 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
    }

    public static void drawModifierButton(PoseStack ps, int x, int y, boolean selected, boolean hovered,
                                          ResourceLocation normal, ResourceLocation hover,
                                          ResourceLocation selectedTexture) {
        RenderSystem.setShaderTexture(0, selected ? selectedTexture : hovered ? hover : normal);
        GuiComponent.blit(ps, x, y, 0, 0, MODIFIER_SIZE, MODIFIER_SIZE, MODIFIER_SIZE, MODIFIER_SIZE);
    }

    /**
     * Draws a title graphic at native size, centred on the banner rectangle.
     *
     * <p>No scaling: the lettering is pixel art and any non-integer factor turns it to
     * mush. A graphic wider than its banner therefore overhangs rather than shrinking -
     * the artwork is the thing that gets adjusted, not the rendering.
     *
     * @param ink {width, height, bodyY, bodyHeight} - see the TITLE_INK_* constants
     */
    public static void drawTitleImage(PoseStack ps, ResourceLocation title, int[] ink,
                                      int bannerX, int bannerY, int bannerW, int bannerH) {
        int inkW = ink[0], inkH = ink[1], bodyY = ink[2], bodyH = ink[3];

        // Integer division, not Math.round: when the leftover is odd the extra pixel has
        // to land somewhere, and floor puts it on the right the way vanilla centres text.
        // The banner interior is 77px while every title is an even width, so that
        // leftover is always odd today - widening the banner to 78 in the artwork makes
        // all three captions land dead centre with no code change.
        int x = bannerX + (bannerW - inkW) / 2;
        // Position the letter body in the middle, then step back to the sheet's origin.
        int y = bannerY + (bannerH - bodyH) / 2 - bodyY;

        RenderSystem.setShaderTexture(0, title);
        GuiComponent.blit(ps, x, y, inkW, inkH, 0f, 0f, inkW, inkH, TITLE_SHEET_W, TITLE_SHEET_H);
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
