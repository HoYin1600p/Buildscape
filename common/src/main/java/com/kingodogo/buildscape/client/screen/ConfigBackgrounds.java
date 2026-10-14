package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.platform.Services;

import java.util.Arrays;
import java.util.List;

// Modular GUI background registry and Creeper backdrop renderer for Buildscape configuration screens.
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

    // 8x8 Creeper Face Mask: 1 = dark cutout, 0 = colored skin
    private static final int[][] CREEPER_MASK = {
            { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 1, 0, 0, 1, 1, 0 },
            { 0, 1, 1, 0, 0, 1, 1, 0 },
            { 0, 0, 0, 1, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 0, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    // Registered Backgrounds
    public static final Background NONE = new Background(
            "none", "None",
            (poseStackOrGraphics, x, y, width, height, alpha, theme) -> {}
    );

    public static final Background GREEN_CREEPER = new Background(
            "green_creeper", "Green Creeper",
            ConfigBackgrounds::drawGreenCreeperBackground
    );

    public static final Background GOLDEN_CREEPER = new Background(
            "golden_creeper", "Golden Creeper",
            ConfigBackgrounds::drawGoldenCreeperBackground
    );

    public static final Background DIAMOND_CREEPER = new Background(
            "diamond_creeper", "Diamond Creeper",
            ConfigBackgrounds::drawDiamondCreeperBackground
    );

    public static final Background CRIMSON_CREEPER = new Background(
            "crimson_creeper", "Crimson Creeper",
            ConfigBackgrounds::drawCrimsonCreeperBackground
    );

    private static final List<Background> BACKGROUNDS = Arrays.asList(
            NONE,
            GREEN_CREEPER,
            GOLDEN_CREEPER,
            DIAMOND_CREEPER,
            CRIMSON_CREEPER
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

    public static Background cyclePrevBackground() {
        activeBackgroundIndex = (activeBackgroundIndex - 1 + BACKGROUNDS.size()) % BACKGROUNDS.size();
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
        if ("creeper".equalsIgnoreCase(id)) id = "green_creeper";
        for (int i = 0; i < BACKGROUNDS.size(); i++) {
            if (BACKGROUNDS.get(i).id.equalsIgnoreCase(id)) {
                activeBackgroundIndex = i;
                return;
            }
        }
    }

    public static Background getById(String id) {
        if ("creeper".equalsIgnoreCase(id)) return GREEN_CREEPER;
        for (Background b : BACKGROUNDS) {
            if (b.id.equalsIgnoreCase(id)) return b;
        }
        return NONE;
    }

    public static void drawCurrentBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        getCurrentBackground().draw(poseStackOrGraphics, x, y, width, height, alpha, theme);
    }

    // =========================================================================
    // Creeper Background Renderers (Zero artifacts, authentic gradients)
    // =========================================================================

    // 1. GREEN CREEPER: Iconic Green Creeper Face & Emerald Gradient Wash
    public static void drawGreenCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.07f : 0.12f;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, 0xFF0B2914, 0xFF166534, 0xFF061A0C, alpha * washAlpha);

        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 32);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(136, Math.max(48, (faceSize / 8) * 8));
        int cellSize = faceSize / 8;

        int faceX = x + (width - faceSize) / 2;
        int topMargin = 26;
        int availH = Math.max(faceSize, height - topMargin);
        int faceY = y + topMargin + (availH - faceSize) / 2;

        float headAlpha = light ? 0.16f : 0.22f;
        float cutoutAlpha = light ? 0.32f : 0.42f;

        int[] rowGreenTop = { 0xFF22C55E, 0xFF16A34A, 0xFF15803D, 0xFF16A34A, 0xFF15803D, 0xFF14532D, 0xFF0F3D1E, 0xFF0B2E16 };
        int[] rowGreenAlt = { 0xFF4ADE80, 0xFF22C55E, 0xFF16A34A, 0xFF15803D, 0xFF16A34A, 0xFF15803D, 0xFF14532D, 0xFF0F3D1E };

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int cx = faceX + c * cellSize;
                int cy = faceY + r * cellSize;
                boolean isCutout = CREEPER_MASK[r][c] == 1;

                if (isCutout) {
                    int darkCol = light ? 0xFF0B2010 : 0xFF030D06;
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(darkCol, alpha * cutoutAlpha));
                } else {
                    int greenCol = ((r + c) % 2 == 0) ? rowGreenTop[r] : rowGreenAlt[r];
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(greenCol, alpha * headAlpha));
                }
            }
        }

        int headBorder = light ? 0xFF15803D : 0xFF34D399;
        drawBorder(poseStackOrGraphics, faceX - 1, faceY - 1, faceSize + 2, faceSize + 2, applyAlpha(headBorder, alpha * (light ? 0.20f : 0.28f)));
    }

    // 2. GOLDEN CREEPER: Shimmering Gold Creeper Face & Warm Amber Wash
    public static void drawGoldenCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, 0xFF2B1D04, 0xFFB45309, 0xFF1C1202, alpha * washAlpha);

        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 32);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(136, Math.max(48, (faceSize / 8) * 8));
        int cellSize = faceSize / 8;

        int faceX = x + (width - faceSize) / 2;
        int topMargin = 26;
        int availH = Math.max(faceSize, height - topMargin);
        int faceY = y + topMargin + (availH - faceSize) / 2;

        float headAlpha = light ? 0.18f : 0.25f;
        float cutoutAlpha = light ? 0.35f : 0.45f;

        int[] rowGoldTop = { 0xFFFDE047, 0xFFFBBF24, 0xFFF59E0B, 0xFFFBBF24, 0xFFF59E0B, 0xFFD97706, 0xFFB45309, 0xFF92400E };
        int[] rowGoldAlt = { 0xFFFEF08A, 0xFFFDE047, 0xFFFBBF24, 0xFFF59E0B, 0xFFD97706, 0xFFB45309, 0xFF92400E, 0xFF78350F };

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int cx = faceX + c * cellSize;
                int cy = faceY + r * cellSize;
                boolean isCutout = CREEPER_MASK[r][c] == 1;

                if (isCutout) {
                    int darkCol = light ? 0xFF261804 : 0xFF140B02;
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(darkCol, alpha * cutoutAlpha));
                } else {
                    int goldCol = ((r + c) % 2 == 0) ? rowGoldTop[r] : rowGoldAlt[r];
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(goldCol, alpha * headAlpha));
                }
            }
        }

        int goldBorder = light ? 0xFFD97706 : 0xFFFDE047;
        drawBorder(poseStackOrGraphics, faceX - 1, faceY - 1, faceSize + 2, faceSize + 2, applyAlpha(goldBorder, alpha * (light ? 0.25f : 0.32f)));
    }

    // 3. DIAMOND CREEPER: Oceanic Cyan Creeper Face & Sapphire Wash
    public static void drawDiamondCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.07f : 0.13f;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, 0xFF06182B, 0xFF0284C7, 0xFF030D17, alpha * washAlpha);

        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 32);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(136, Math.max(48, (faceSize / 8) * 8));
        int cellSize = faceSize / 8;

        int faceX = x + (width - faceSize) / 2;
        int topMargin = 26;
        int availH = Math.max(faceSize, height - topMargin);
        int faceY = y + topMargin + (availH - faceSize) / 2;

        float headAlpha = light ? 0.18f : 0.25f;
        float cutoutAlpha = light ? 0.35f : 0.45f;

        int[] rowDiamondTop = { 0xFF7DD3FC, 0xFF38BDF8, 0xFF0284C7, 0xFF38BDF8, 0xFF0284C7, 0xFF0369A1, 0xFF075985, 0xFF0C4A6E };
        int[] rowDiamondAlt = { 0xFFBAE6FD, 0xFF7DD3FC, 0xFF38BDF8, 0xFF0284C7, 0xFF0369A1, 0xFF075985, 0xFF0C4A6E, 0xFF082F49 };

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int cx = faceX + c * cellSize;
                int cy = faceY + r * cellSize;
                boolean isCutout = CREEPER_MASK[r][c] == 1;

                if (isCutout) {
                    int darkCol = light ? 0xFF051726 : 0xFF020C14;
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(darkCol, alpha * cutoutAlpha));
                } else {
                    int diamondCol = ((r + c) % 2 == 0) ? rowDiamondTop[r] : rowDiamondAlt[r];
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(diamondCol, alpha * headAlpha));
                }
            }
        }

        int diamondBorder = light ? 0xFF0284C7 : 0xFF38BDF8;
        drawBorder(poseStackOrGraphics, faceX - 1, faceY - 1, faceSize + 2, faceSize + 2, applyAlpha(diamondBorder, alpha * (light ? 0.25f : 0.32f)));
    }

    // 4. CRIMSON CREEPER: Rich Ruby Creeper Face & Nether Crimson Wash
    public static void drawCrimsonCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, 0xFF26050C, 0xFF9F1239, 0xFF140206, alpha * washAlpha);

        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 32);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(136, Math.max(48, (faceSize / 8) * 8));
        int cellSize = faceSize / 8;

        int faceX = x + (width - faceSize) / 2;
        int topMargin = 26;
        int availH = Math.max(faceSize, height - topMargin);
        int faceY = y + topMargin + (availH - faceSize) / 2;

        float headAlpha = light ? 0.18f : 0.25f;
        float cutoutAlpha = light ? 0.35f : 0.45f;

        int[] rowCrimsonTop = { 0xFFFB7185, 0xFFF43F5E, 0xFFE11D48, 0xFFF43F5E, 0xFFE11D48, 0xFFBE123C, 0xFF9F1239, 0xFF881337 };
        int[] rowCrimsonAlt = { 0xFFFDA4AF, 0xFFFB7185, 0xFFF43F5E, 0xFFE11D48, 0xFFBE123C, 0xFF9F1239, 0xFF881337, 0xFF4C0519 };

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int cx = faceX + c * cellSize;
                int cy = faceY + r * cellSize;
                boolean isCutout = CREEPER_MASK[r][c] == 1;

                if (isCutout) {
                    int darkCol = light ? 0xFF24040A : 0xFF120205;
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(darkCol, alpha * cutoutAlpha));
                } else {
                    int crimsonCol = ((r + c) % 2 == 0) ? rowCrimsonTop[r] : rowCrimsonAlt[r];
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(crimsonCol, alpha * headAlpha));
                }
            }
        }

        int crimsonBorder = light ? 0xFFBE123C : 0xFFFB7185;
        drawBorder(poseStackOrGraphics, faceX - 1, faceY - 1, faceSize + 2, faceSize + 2, applyAlpha(crimsonBorder, alpha * (light ? 0.25f : 0.32f)));
    }

    // =========================================================================
    // Drawing Helpers
    // =========================================================================

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
