package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.platform.Services;

import java.util.Arrays;
import java.util.List;

// Modular GUI background registry and drawing helper for Buildscape configuration screens.
public class ConfigBackgrounds {

    @FunctionalInterface
    public interface BackgroundRenderer {
        void render(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme);
    }

    public static class Background {
        public final String id;
        public final String displayName;
        public final BackgroundRenderer renderer;

        public Background(String id, String displayName, BackgroundRenderer renderer) {
            this.id = id;
            this.displayName = displayName;
            this.renderer = renderer;
        }

        public void draw(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
            if (renderer != null) {
                renderer.render(poseStackOrGraphics, x, y, width, height, alpha, theme);
            }
        }
    }

    // 8x8 Creeper face texture map with mottled green skin and dark facial features
    private static final int[][] CREEPER_TEXTURE_MAP = {
            { 0xFF3F8A4A, 0xFF4FA85B, 0xFF2E6F40, 0xFF4FA85B, 0xFF3F8A4A, 0xFF60BA6E, 0xFF2E6F40, 0xFF4FA85B },
            { 0xFF4FA85B, 0xFF142416, 0xFF142416, 0xFF3F8A4A, 0xFF2E6F40, 0xFF142416, 0xFF142416, 0xFF3F8A4A },
            { 0xFF2E6F40, 0xFF142416, 0xFF142416, 0xFF4FA85B, 0xFF60BA6E, 0xFF142416, 0xFF142416, 0xFF2E6F40 },
            { 0xFF3F8A4A, 0xFF4FA85B, 0xFF3F8A4A, 0xFF142416, 0xFF142416, 0xFF3F8A4A, 0xFF4FA85B, 0xFF3F8A4A },
            { 0xFF60BA6E, 0xFF2E6F40, 0xFF142416, 0xFF142416, 0xFF142416, 0xFF142416, 0xFF2E6F40, 0xFF60BA6E },
            { 0xFF4FA85B, 0xFF3F8A4A, 0xFF142416, 0xFF142416, 0xFF142416, 0xFF142416, 0xFF3F8A4A, 0xFF4FA85B },
            { 0xFF2E6F40, 0xFF4FA85B, 0xFF142416, 0xFF3F8A4A, 0xFF60BA6E, 0xFF142416, 0xFF4FA85B, 0xFF2E6F40 },
            { 0xFF3F8A4A, 0xFF2E6F40, 0xFF4FA85B, 0xFF3F8A4A, 0xFF4FA85B, 0xFF2E6F40, 0xFF3F8A4A, 0xFF4FA85B }
    };

    public static final Background NONE = new Background(
            "none", "None",
            (poseStackOrGraphics, x, y, width, height, alpha, theme) -> {}
    );

    public static final Background CREEPER = new Background(
            "creeper", "Creeper",
            ConfigBackgrounds::drawCreeperBackground
    );

    public static final Background GREEN_GRADIENT = new Background(
            "green_gradient", "Green Gradient",
            ConfigBackgrounds::drawGreenGradientBackground
    );

    public static final Background DIGITAL_GRID = new Background(
            "digital_grid", "Digital Grid",
            ConfigBackgrounds::drawDigitalGridBackground
    );

    public static final Background EMERALD_GLOW = new Background(
            "emerald_glow", "Emerald Glow",
            ConfigBackgrounds::drawEmeraldGlowBackground
    );

    public static final Background AURORA = new Background(
            "aurora", "Aurora Borealis",
            ConfigBackgrounds::drawAuroraBackground
    );

    private static final List<Background> BACKGROUNDS = Arrays.asList(
            NONE,
            CREEPER,
            GREEN_GRADIENT,
            DIGITAL_GRID,
            EMERALD_GLOW,
            AURORA
    );

    private static int activeBackgroundIndex = 0;

    static {
        try {
            String saved = BuildscapeClientConfig.get().getUiBackground();
            setBackgroundById(saved);
        } catch (Exception ignored) {}
    }

    public static List<Background> getAllBackgrounds() {
        return BACKGROUNDS;
    }

    public static Background getCurrentBackground() {
        if (activeBackgroundIndex < 0 || activeBackgroundIndex >= BACKGROUNDS.size()) {
            activeBackgroundIndex = 0;
        }
        return BACKGROUNDS.get(activeBackgroundIndex);
    }

    public static int getActiveBackgroundIndex() {
        return activeBackgroundIndex;
    }

    public static Background cycleNextBackground() {
        activeBackgroundIndex = (activeBackgroundIndex + 1) % BACKGROUNDS.size();
        Background bg = getCurrentBackground();
        try {
            BuildscapeClientConfig.get().setUiBackground(bg.id);
        } catch (Exception ignored) {}
        return bg;
    }

    public static void setBackgroundIndex(int index) {
        if (index >= 0 && index < BACKGROUNDS.size()) {
            activeBackgroundIndex = index;
            try {
                BuildscapeClientConfig.get().setUiBackground(BACKGROUNDS.get(index).id);
            } catch (Exception ignored) {}
        }
    }

    public static void setBackgroundById(String id) {
        if (id == null) return;
        for (int i = 0; i < BACKGROUNDS.size(); i++) {
            if (BACKGROUNDS.get(i).id.equalsIgnoreCase(id)) {
                activeBackgroundIndex = i;
                return;
            }
        }
    }

    public static Background getById(String id) {
        for (Background b : BACKGROUNDS) {
            if (b.id.equalsIgnoreCase(id)) return b;
        }
        return NONE;
    }

    public static void drawCurrentBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        getCurrentBackground().draw(poseStackOrGraphics, x, y, width, height, alpha, theme);
    }

    // --- Renderer Implementations ---

    public static void drawCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        // 1. Subtle green ambient backdrop wash
        float baseAlpha = (theme != null && theme.isLight()) ? 0.07f : 0.12f;
        int topCol = 0xFF0E2816;
        int midCol = 0xFF1B4E26;
        int botCol = 0xFF081A0E;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, topCol, midCol, botCol, alpha * baseAlpha);

        // 2. Centered Creeper Face Watermark
        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 24);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(160, Math.max(48, faceSize));
        int cellSize = Math.max(2, faceSize / 8);
        faceSize = cellSize * 8;

        int faceX = x + (width - faceSize) / 2;
        int faceY = y + (height - faceSize) / 2;

        float skinAlpha = (theme != null && theme.isLight()) ? 0.14f : 0.18f;
        float darkFeatureAlpha = (theme != null && theme.isLight()) ? 0.28f : 0.36f;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int cellColor = CREEPER_TEXTURE_MAP[row][col];
                boolean isDarkFeature = (cellColor == 0xFF142416);
                float a = (isDarkFeature ? darkFeatureAlpha : skinAlpha) * alpha;

                int cx = faceX + col * cellSize;
                int cy = faceY + row * cellSize;
                Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(cellColor, a));
            }
        }

        // 3. Crisp 1px perimeter border around the Creeper face
        int borderCol = (theme != null && theme.isLight()) ? 0xFF1B4E26 : 0xFF34D399;
        float borderAlpha = (theme != null && theme.isLight()) ? 0.18f : 0.22f;
        drawBorder(poseStackOrGraphics, faceX - 1, faceY - 1, faceSize + 2, faceSize + 2, applyAlpha(borderCol, alpha * borderAlpha));
    }

    public static void drawGreenGradientBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        float intensity = (theme != null && theme.isLight()) ? 0.12f : 0.22f;
        int topCol = 0xFF0B2412;
        int midCol = 0xFF166534;
        int botCol = 0xFF051B0C;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, topCol, midCol, botCol, alpha * intensity);

        // Soft horizontal emerald sheen through the vertical center
        int sheenH = Math.max(12, height / 3);
        int sheenY = y + (height - sheenH) / 2;
        drawHorizontalGradientLine(poseStackOrGraphics, x, sheenY, width, 0x0010B981, 0xFF22C55E, alpha * intensity * 0.7f);
        drawHorizontalGradientLine(poseStackOrGraphics, x, sheenY + sheenH, width, 0xFF22C55E, 0x0010B981, alpha * intensity * 0.7f);
    }

    public static void drawDigitalGridBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        // Dark tech backing wash
        float washAlpha = (theme != null && theme.isLight()) ? 0.05f : 0.12f;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + height, applyAlpha(0xFF06180C, alpha * washAlpha));

        int step = 20;
        int gridCol = (theme != null && theme.isLight()) ? 0xFF166534 : 0xFF22C55E;
        float lineAlpha = (theme != null && theme.isLight()) ? 0.08f : 0.12f;
        float dotAlpha = (theme != null && theme.isLight()) ? 0.22f : 0.32f;

        // Vertical lines
        for (int lx = x + step; lx < x + width; lx += step) {
            Services.PLATFORM.fill(poseStackOrGraphics, lx, y, lx + 1, y + height, applyAlpha(gridCol, alpha * lineAlpha));
        }

        // Horizontal lines
        for (int ly = y + step; ly < y + height; ly += step) {
            Services.PLATFORM.fill(poseStackOrGraphics, x, ly, x + width, ly + 1, applyAlpha(gridCol, alpha * lineAlpha));
        }

        // Highlight dots at intersections
        for (int lx = x + step; lx < x + width; lx += step) {
            for (int ly = y + step; ly < y + height; ly += step) {
                Services.PLATFORM.fill(poseStackOrGraphics, lx - 1, ly - 1, lx + 2, ly + 2, applyAlpha(0xFF4ADE80, alpha * dotAlpha));
            }
        }
    }

    public static void drawEmeraldGlowBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        int layers = 7;
        float baseAlpha = (theme != null && theme.isLight()) ? 0.04f : 0.06f;

        for (int i = 0; i < layers; i++) {
            float frac = (float) i / (float) layers;
            int insetX = (int) (width * 0.42f * (1.0f - frac));
            int insetY = (int) (height * 0.42f * (1.0f - frac));

            int gx = x + insetX;
            int gy = y + insetY;
            int gw = Math.max(1, width - insetX * 2);
            int gh = Math.max(1, height - insetY * 2);

            int col = lerpColor(0xFF04200E, 0xFF34D399, frac);
            Services.PLATFORM.fill(poseStackOrGraphics, gx, gy, gx + gw, gy + gh, applyAlpha(col, alpha * baseAlpha * (1.0f + frac)));
        }
    }

    public static void drawAuroraBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        float intensity = (theme != null && theme.isLight()) ? 0.09f : 0.18f;

        // Aurora color stops: emerald green -> turquoise cyan -> deep teal -> forest green
        int[] stops = { 0xFF064E3B, 0xFF059669, 0xFF06B6D4, 0xFF10B981, 0xFF042F2E };
        int bandH = Math.max(1, height / stops.length);

        for (int i = 0; i < stops.length; i++) {
            int by = y + i * bandH;
            int nextCol = (i < stops.length - 1) ? stops[i + 1] : stops[i];
            int curH = (i == stops.length - 1) ? (y + height - by) : bandH;
            if (curH <= 0) continue;

            for (int j = 0; j < curH; j++) {
                float t = (float) j / Math.max(1, curH);
                int col = lerpColor(stops[i], nextCol, t);
                Services.PLATFORM.fill(poseStackOrGraphics, x, by + j, x + width, by + j + 1, applyAlpha(col, alpha * intensity));
            }
        }
    }

    // --- Drawing Utilities ---

    public static int applyAlpha(int argb, float alpha) {
        int a = (int) (((argb >> 24) & 0xFF) * Math.max(0.0f, Math.min(1.0f, alpha)));
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    public static int lerpColor(int startCol, int endCol, float t) {
        float clamped = Math.max(0.0f, Math.min(1.0f, t));
        int a1 = (startCol >> 24) & 0xFF;
        int r1 = (startCol >> 16) & 0xFF;
        int g1 = (startCol >> 8) & 0xFF;
        int b1 = startCol & 0xFF;

        int a2 = (endCol >> 24) & 0xFF;
        int r2 = (endCol >> 16) & 0xFF;
        int g2 = (endCol >> 8) & 0xFF;
        int b2 = endCol & 0xFF;

        int a = (int) (a1 + (a2 - a1) * clamped);
        int r = (int) (r1 + (r2 - r1) * clamped);
        int g = (int) (g1 + (g2 - g1) * clamped);
        int b = (int) (b1 + (b2 - b1) * clamped);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void drawBorder(Object poseStackOrGraphics, int x, int y, int w, int h, int color) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, color);
    }

    public static void drawHorizontalGradientLine(Object poseStackOrGraphics, int x, int y, int w, int startCol, int endCol, float alpha) {
        if (w <= 0) return;
        for (int i = 0; i < w; i++) {
            float t = (float) i / Math.max(1, w - 1);
            int col = lerpColor(startCol, endCol, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + i, y, x + i + 1, y + 1, applyAlpha(col, alpha));
        }
    }

    public static void drawVerticalGradientRect(Object poseStackOrGraphics, int x, int y, int w, int h, int topCol, int midCol, int botCol, float alpha) {
        if (h <= 0 || w <= 0) return;
        int halfH = h / 2;
        for (int j = 0; j < h; j++) {
            int col;
            if (j < halfH) {
                float t = (float) j / Math.max(1, halfH);
                col = lerpColor(topCol, midCol, t);
            } else {
                float t = (float) (j - halfH) / Math.max(1, h - halfH);
                col = lerpColor(midCol, botCol, t);
            }
            Services.PLATFORM.fill(poseStackOrGraphics, x, y + j, x + w, y + j + 1, applyAlpha(col, alpha));
        }
    }
}
