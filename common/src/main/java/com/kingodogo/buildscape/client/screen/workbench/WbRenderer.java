package com.kingodogo.buildscape.client.screen.workbench;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;

public final class WbRenderer {

    public static final CommonId BG_COLOR_BUILDER = new CommonId("buildscape", "textures/gui/builders_workbench/color_builder_bg.png");
    public static final CommonId BG_GRADIENT_BUILDER = new CommonId("buildscape", "textures/gui/builders_workbench/gradient_builder_bg.png");

    public static final CommonId TAB_COLOR = new CommonId("buildscape", "textures/gui/builders_workbench/color_builder_tab.png");
    public static final CommonId TAB_COLOR_HOVER = new CommonId("buildscape", "textures/gui/builders_workbench/color_builder_tab_hover.png");
    public static final CommonId TAB_COLOR_SEL = new CommonId("buildscape", "textures/gui/builders_workbench/color_builder_tab_selected.png");
    public static final CommonId TAB_GRADIENT = new CommonId("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab.png");
    public static final CommonId TAB_GRADIENT_HOVER = new CommonId("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab_hover.png");
    public static final CommonId TAB_GRADIENT_SEL = new CommonId("buildscape", "textures/gui/builders_workbench/gradiant_builder_tab_selected.png");

    public static final CommonId TITLE_COLOR = new CommonId("buildscape", "textures/gui/builders_workbench/color_builder_title.png");
    public static final CommonId TITLE_GRADIENT = new CommonId("buildscape", "textures/gui/builders_workbench/gradient_builder_title.png");

    public static final CommonId BUILDERS_ARROW = new CommonId("buildscape", "textures/gui/builders_workbench/builders_arrow.png");
    public static final CommonId BUILDERS_ARROW_ACTIVE = new CommonId("buildscape", "textures/gui/builders_workbench/builders_arrow_active.png");

    public static final CommonId BTN_SOLID = new CommonId("buildscape", "textures/gui/builders_workbench/solid.png");
    public static final CommonId BTN_SOLID_HOVER = new CommonId("buildscape", "textures/gui/builders_workbench/solid_hover.png");
    public static final CommonId BTN_SOLID_SEL = new CommonId("buildscape", "textures/gui/builders_workbench/solid_selected.png");
    public static final CommonId BTN_SOLID_STRICT = new CommonId("buildscape", "textures/gui/builders_workbench/solid_shift_selected.png");
    public static final CommonId BTN_TRANSPARENT = new CommonId("buildscape", "textures/gui/builders_workbench/transparent.png");
    public static final CommonId BTN_TRANSPARENT_HOVER = new CommonId("buildscape", "textures/gui/builders_workbench/transparent_hover.png");
    public static final CommonId BTN_TRANSPARENT_SEL = new CommonId("buildscape", "textures/gui/builders_workbench/transparent_selected.png");
    public static final CommonId BTN_TRANSPARENT_STRICT = new CommonId("buildscape", "textures/gui/builders_workbench/transparent_shift_selected.png");
    public static final CommonId BTN_NON_FULL = new CommonId("buildscape", "textures/gui/builders_workbench/non_full.png");
    public static final CommonId BTN_NON_FULL_HOVER = new CommonId("buildscape", "textures/gui/builders_workbench/non_full_hover.png");
    public static final CommonId BTN_NON_FULL_SEL = new CommonId("buildscape", "textures/gui/builders_workbench/non_full_selected.png");
    public static final CommonId BTN_NON_FULL_STRICT = new CommonId("buildscape", "textures/gui/builders_workbench/non_full_shift_selected.png");
    public static final CommonId BTN_SINGLE_TEXTURE = new CommonId("buildscape", "textures/gui/builders_workbench/single_texture.png");
    public static final CommonId BTN_SINGLE_TEXTURE_HOVER = new CommonId("buildscape", "textures/gui/builders_workbench/single_texture_hover.png");
    public static final CommonId BTN_SINGLE_TEXTURE_SEL = new CommonId("buildscape", "textures/gui/builders_workbench/single_texture_selected.png");
    public static final CommonId BTN_MATCH_SHAPE = new CommonId("buildscape", "textures/gui/builders_workbench/match_shape.png");
    public static final CommonId BTN_MATCH_SHAPE_HOVER = new CommonId("buildscape", "textures/gui/builders_workbench/match_shape_hover.png");
    public static final CommonId BTN_MATCH_SHAPE_SEL = new CommonId("buildscape", "textures/gui/builders_workbench/match_shape_selected.png");

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

    public static void blitFloat(Object ps, int x, int y, int width, int height, float u0, float v0, float u1, float v1) {
        Services.PLATFORM.blit(ps, null, x, y, u0, v0, width, height, 1, 1);
    }

    public static void drawBuilderBG(Object ps, CommonId texture, int x, int y, int w, int h) {
        Services.PLATFORM.blit(ps, texture, x, y, 0f, 0f, w, h, SHEET_SIZE, SHEET_SIZE);
    }

    public static void drawTabButton(Object ps, int x, int y, boolean active, boolean hovered,
                                     CommonId normal, CommonId hover, CommonId selected) {
        CommonId tex = active ? selected : (hovered ? hover : normal);
        Services.PLATFORM.blit(ps, tex, x, y, 0, 0, TAB_SIZE, TAB_SIZE, TAB_SIZE, TAB_SIZE);
    }

    public static void drawFilterButton(Object ps, int x, int y, int index, int currentMask, boolean hovered) {
        CommonId normalTex, hoverTex, selectedTex, strictTex;
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
        CommonId activeTex = (currentMask & strictBit) != 0 ? strictTex
                : (currentMask & categoryBit) != 0 ? selectedTex
                : hovered ? hoverTex : normalTex;
        Services.PLATFORM.blit(ps, activeTex, x, y, 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
    }

    public static void drawModifierButton(Object ps, int x, int y, boolean selected, boolean hovered,
                                          CommonId normal, CommonId hover,
                                          CommonId selectedTexture) {
        CommonId tex = selected ? selectedTexture : hovered ? hover : normal;
        Services.PLATFORM.blit(ps, tex, x, y, 0, 0, MODIFIER_SIZE, MODIFIER_SIZE, MODIFIER_SIZE, MODIFIER_SIZE);
    }

    public static void drawTitleImage(Object ps, CommonId title, int[] ink,
                                      int bannerX, int bannerY, int bannerW, int bannerH) {
        int inkW = ink[0], inkH = ink[1], bodyY = ink[2], bodyH = ink[3];

        int x = bannerX + (bannerW - inkW) / 2;
        int y = bannerY + (bannerH - bodyH) / 2 - bodyY;

        Services.PLATFORM.blit(ps, title, x, y, 0f, 0f, inkW, inkH, TITLE_SHEET_W, TITLE_SHEET_H);
    }

    public static void drawCopyArrow(Object ps, int x, int y, float progress, boolean blocked) {
        Services.PLATFORM.blit(ps, BUILDERS_ARROW, x, y, 0f, 0f, ARROW_W, ARROW_H, ARROW_W, ARROW_H);

        if (blocked) {
            int centerX = x + ARROW_W / 2;
            int centerY = y + ARROW_H / 2;
            int color = 0xFFE84B4B;
            for (int offset = -4; offset <= 4; offset++) {
                Services.PLATFORM.fill(ps, centerX + offset, centerY + offset, centerX + offset + 2, centerY + offset + 2, color);
                Services.PLATFORM.fill(ps, centerX + offset, centerY - offset, centerX + offset + 2, centerY - offset + 2, color);
            }
            return;
        }

        float p = Math.max(0f, Math.min(1f, progress));
        if (p > 0f) {
            Services.PLATFORM.blit(ps, BUILDERS_ARROW_ACTIVE, x, y, 0f, 0f, (int) (ARROW_W * p), ARROW_H, ARROW_W, ARROW_H);
        }
    }
}
