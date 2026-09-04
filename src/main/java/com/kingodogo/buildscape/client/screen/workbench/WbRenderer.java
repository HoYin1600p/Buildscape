package com.kingodogo.buildscape.client.screen.workbench;

import com.kingodogo.buildscape.util.ColorGradientSolver;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public final class WbRenderer {

    public static final ResourceLocation BG_COLOR_BUILDER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_bg.png");
    public static final ResourceLocation BG_GRADIENT_BUILDER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradient_builder_bg.png");

    public static final ResourceLocation TAB_COLOR = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_tab.png");
    public static final ResourceLocation TAB_COLOR_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_tab_hover.png");
    public static final ResourceLocation TAB_COLOR_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_tab_selected.png");
    public static final ResourceLocation TAB_GRADIENT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab.png");
    public static final ResourceLocation TAB_GRADIENT_HOVER = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab_hover.png");
    public static final ResourceLocation TAB_GRADIENT_SEL = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab_selected.png");

    public static final ResourceLocation TITLE_COLOR = new ResourceLocation("buildscape", "textures/gui/builders_workbench/color_builder_title.png");
    public static final ResourceLocation TITLE_GRADIENT = new ResourceLocation("buildscape", "textures/gui/builders_workbench/gradient_builder_title.png");

    public static final ResourceLocation BUILDERS_ARROW = new ResourceLocation("buildscape", "textures/gui/builders_workbench/builders_arrow.png");
    public static final ResourceLocation BUILDERS_ARROW_ACTIVE = new ResourceLocation("buildscape", "textures/gui/builders_workbench/builders_arrow_active.png");

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

    public static final int SHEET_SIZE = 256;
    public static final int TAB_SIZE = 17;
    public static final int BUTTON_SIZE = 18;
    public static final int MODIFIER_SIZE = 11;
    public static final int ARROW_W = 48;
    public static final int ARROW_H = 16;
    public static final int TITLE_SHEET_W = 128;
    public static final int TITLE_SHEET_H = 16;

    public static final int[] TITLE_INK_COLOR = {60, 7, 0, 7};
    public static final int[] TITLE_INK_GRADIENT = {74, 7, 0, 7};
    public static final int[] TITLE_INK_POUCH = {68, 8, 1, 7};

    private WbRenderer() {
    }

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

    public static void drawBuilderBG(PoseStack ps, ResourceLocation texture, int x, int y, int w, int h) {
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(ps, x, y, 0f, 0f, w, h, SHEET_SIZE, SHEET_SIZE);
    }

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

    public static void drawTitleImage(PoseStack ps, ResourceLocation title, int[] ink,
                                      int bannerX, int bannerY, int bannerW, int bannerH) {
        int inkW = ink[0], inkH = ink[1], bodyY = ink[2], bodyH = ink[3];

        int x = bannerX + (bannerW - inkW) / 2;
        int y = bannerY + (bannerH - bodyH) / 2 - bodyY;

        RenderSystem.setShaderTexture(0, title);
        GuiComponent.blit(ps, x, y, inkW, inkH, 0f, 0f, inkW, inkH, TITLE_SHEET_W, TITLE_SHEET_H);
    }

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
