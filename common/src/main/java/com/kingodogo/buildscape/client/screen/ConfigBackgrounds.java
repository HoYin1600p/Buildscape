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

    public static final Background NONE = new Background(
            "none", "None",
            (poseStackOrGraphics, x, y, width, height, alpha, theme) -> {}
    );

    public static final Background CREEPER = new Background(
            "creeper", "Creeper",
            ConfigBackgrounds::drawCreeperBackground
    );

    public static final Background SWORDS = new Background(
            "swords", "Crossed Swords",
            ConfigBackgrounds::drawCrossedSwordsBackground
    );

    public static final Background PICKAXE = new Background(
            "pickaxe", "Crossed Pickaxes",
            ConfigBackgrounds::drawPickaxeBackground
    );

    public static final Background NETHER_PORTAL = new Background(
            "nether_portal", "Nether Portal",
            ConfigBackgrounds::drawNetherPortalBackground
    );

    public static final Background REDSTONE_CIRCUIT = new Background(
            "redstone_circuit", "Redstone Circuit",
            ConfigBackgrounds::drawRedstoneCircuitBackground
    );

    private static final List<Background> BACKGROUNDS = Arrays.asList(
            NONE,
            CREEPER,
            SWORDS,
            PICKAXE,
            NETHER_PORTAL,
            REDSTONE_CIRCUIT
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

    // =========================================================================
    // Background Renderers: Themed Geometric Shapes with Alpha-Safe Backdrop
    // =========================================================================

    // 1. CREEPER: Iconic Creeper Face & Crest with multi-shade green gradients
    public static void drawCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();

        // Ambient green gradient wash
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, 0xFF0B2914, 0xFF166534, 0xFF061A0C, alpha * washAlpha);

        // Center Creeper Head geometry
        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 24);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(144, Math.max(48, (faceSize / 8) * 8));
        int cellSize = faceSize / 8;

        int faceX = x + (width - faceSize) / 2;
        int faceY = y + (height - faceSize) / 2;

        float headAlpha = light ? 0.16f : 0.22f;
        float cutoutAlpha = light ? 0.32f : 0.42f;

        // Authentic green gradient skin palette per row (brighter on top, deeper at bottom)
        int[] rowGreenTop = { 0xFF22C55E, 0xFF16A34A, 0xFF15803D, 0xFF16A34A, 0xFF15803D, 0xFF14532D, 0xFF0F3D1E, 0xFF0B2E16 };
        int[] rowGreenAlt = { 0xFF4ADE80, 0xFF22C55E, 0xFF16A34A, 0xFF15803D, 0xFF16A34A, 0xFF15803D, 0xFF14532D, 0xFF0F3D1E };

        // 8x8 Creeper mask: 1 = dark cutout, 0 = green skin
        int[][] creeperMask = {
                { 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 0, 1, 1, 0 },
                { 0, 1, 1, 0, 0, 1, 1, 0 },
                { 0, 0, 0, 1, 1, 0, 0, 0 },
                { 0, 0, 1, 1, 1, 1, 0, 0 },
                { 0, 0, 1, 1, 1, 1, 0, 0 },
                { 0, 0, 1, 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0 }
        };

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int cx = faceX + c * cellSize;
                int cy = faceY + r * cellSize;
                boolean isCutout = creeperMask[r][c] == 1;

                if (isCutout) {
                    int darkCol = light ? 0xFF0B2010 : 0xFF030D06;
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(darkCol, alpha * cutoutAlpha));
                } else {
                    int greenCol = ((r + c) % 2 == 0) ? rowGreenTop[r] : rowGreenAlt[r];
                    Services.PLATFORM.fill(poseStackOrGraphics, cx, cy, cx + cellSize, cy + cellSize, applyAlpha(greenCol, alpha * headAlpha));
                }
            }
        }

        // Head outer beveled highlight border
        int headBorder = light ? 0xFF15803D : 0xFF34D399;
        drawBorder(poseStackOrGraphics, faceX - 1, faceY - 1, faceSize + 2, faceSize + 2, applyAlpha(headBorder, alpha * (light ? 0.22f : 0.30f)));

        // Flanking Chevron Brackets
        int chevH = faceSize / 2;
        int chevY = faceY + (faceSize - chevH) / 2;
        int chevLeftX = faceX - 16;
        int chevRightX = faceX + faceSize + 16;

        if (chevLeftX > x + 8) {
            drawChevron(poseStackOrGraphics, chevLeftX, chevY, 8, chevH, true, headBorder, alpha * headAlpha);
        }
        if (chevRightX + 8 < x + width - 8) {
            drawChevron(poseStackOrGraphics, chevRightX, chevY, 8, chevH, false, headBorder, alpha * headAlpha);
        }
    }

    // 2. CROSSED SWORDS: Heraldic crossed broadswords with faceted circular crest
    public static void drawCrossedSwordsBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        int cx = x + width / 2;
        int cy = y + height / 2;

        int span = Math.min(width, height) / 3;
        span = Math.max(36, Math.min(84, span));

        float emblemAlpha = light ? 0.18f : 0.26f;
        int bladeCol = light ? 0xFF0284C7 : 0xFF38BDF8;
        int guardCol = light ? 0xFF1E293B : 0xFF94A3B8;

        // Background circular halo ring
        int ringR = span - 6;
        drawOctagonRing(poseStackOrGraphics, cx, cy, ringR, 2, bladeCol, alpha * (emblemAlpha * 0.55f));

        // Diagonal Sword 1: Top-Left to Bottom-Right
        drawDiagonalBar(poseStackOrGraphics, cx - span, cy - span, cx + span, cy + span, 4, bladeCol, alpha * emblemAlpha);
        // Guard 1
        int g1x = cx + (int)(span * 0.45f);
        int g1y = cy + (int)(span * 0.45f);
        drawDiagonalBar(poseStackOrGraphics, g1x - 9, g1y + 9, g1x + 9, g1y - 9, 3, guardCol, alpha * emblemAlpha);

        // Diagonal Sword 2: Top-Right to Bottom-Left
        drawDiagonalBar(poseStackOrGraphics, cx + span, cy - span, cx - span, cy + span, 4, bladeCol, alpha * emblemAlpha);
        // Guard 2
        int g2x = cx - (int)(span * 0.45f);
        int g2y = cy + (int)(span * 0.45f);
        drawDiagonalBar(poseStackOrGraphics, g2x - 9, g2y - 9, g2x + 9, g2y + 9, 3, guardCol, alpha * emblemAlpha);

        // Center cross boss diamond
        drawRotatedDiamond(poseStackOrGraphics, cx, cy, 7, guardCol, alpha * emblemAlpha * 1.2f);
    }

    // 3. CROSSED PICKAXES: Miner's pickaxe emblem with central diamond prism
    public static void drawPickaxeBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        int cx = x + width / 2;
        int cy = y + height / 2;

        int span = Math.min(width, height) / 3;
        span = Math.max(36, Math.min(84, span));

        float emblemAlpha = light ? 0.18f : 0.28f;
        int shaftCol = light ? 0xFF78350F : 0xFFD97706;
        int headCol = light ? 0xFF0284C7 : 0xFF22D3EE;

        // Diagonal Handles
        drawDiagonalBar(poseStackOrGraphics, cx - span, cy - span, cx + span, cy + span, 3, shaftCol, alpha * (emblemAlpha * 0.85f));
        drawDiagonalBar(poseStackOrGraphics, cx + span, cy - span, cx - span, cy + span, 3, shaftCol, alpha * (emblemAlpha * 0.85f));

        // Curved Pickaxe Head 1 at Top-Left
        int h1x = cx - span;
        int h1y = cy - span;
        drawPickaxeHead(poseStackOrGraphics, h1x, h1y, true, headCol, alpha * emblemAlpha);

        // Curved Pickaxe Head 2 at Top-Right
        int h2x = cx + span;
        int h2y = cy - span;
        drawPickaxeHead(poseStackOrGraphics, h2x, h2y, false, headCol, alpha * emblemAlpha);

        // Center faceted diamond gem
        drawRotatedDiamond(poseStackOrGraphics, cx, cy, 8, headCol, alpha * emblemAlpha * 1.3f);
    }

    // 4. NETHER PORTAL: Obsidian monolith frame with mystical dimensional gradient & floating particles
    public static void drawNetherPortalBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        int cx = x + width / 2;
        int cy = y + height / 2;

        int pw = Math.max(48, Math.min(108, width / 3));
        int ph = Math.max(68, Math.min(160, (int) (height * 0.72f)));
        int px = cx - pw / 2;
        int py = cy - ph / 2;

        float portalAlpha = light ? 0.18f : 0.28f;

        // Inner dimensional vortex vertical gradient
        int topViolet = 0xFF3B0764;
        int midMagenta = 0xFFA855F7;
        int botMidnight = 0xFF1E0836;
        drawVerticalGradientRect(poseStackOrGraphics, px + 5, py + 5, pw - 10, ph - 10, topViolet, midMagenta, botMidnight, alpha * portalAlpha);

        // Floating geometric portal particles
        int partCol = 0xFFE9D5FF;
        int[] partOffsets = { 10, 18, 26, 40, 52, 68, 84, 96, 110 };
        for (int i = 0; i < partOffsets.length; i++) {
            int offY = partOffsets[i];
            if (offY + 4 >= ph - 10) continue;
            int offX = (int) ((Math.sin(i * 1.7) * 0.5 + 0.5) * (pw - 20)) + 6;
            int sz = (i % 2 == 0) ? 3 : 2;
            Services.PLATFORM.fill(poseStackOrGraphics, px + offX, py + offY, px + offX + sz, py + offY + sz, applyAlpha(partCol, alpha * portalAlpha * 1.4f));
        }

        // Obsidian Frame (Outer 5px stepped border)
        int obsOuter = light ? 0xFF0F0A1C : 0xFF140C26;
        int obsBevel = light ? 0xFF281845 : 0xFF4C1D95;

        // Top, Bottom, Left, Right 5px thick obsidian bars
        Services.PLATFORM.fill(poseStackOrGraphics, px, py, px + pw, py + 5, applyAlpha(obsOuter, alpha * (portalAlpha * 1.2f)));
        Services.PLATFORM.fill(poseStackOrGraphics, px, py + ph - 5, px + pw, py + ph, applyAlpha(obsOuter, alpha * (portalAlpha * 1.2f)));
        Services.PLATFORM.fill(poseStackOrGraphics, px, py, px + 5, py + ph, applyAlpha(obsOuter, alpha * (portalAlpha * 1.2f)));
        Services.PLATFORM.fill(poseStackOrGraphics, px + pw - 5, py, px + pw, py + ph, applyAlpha(obsOuter, alpha * (portalAlpha * 1.2f)));

        // Specular inner edge lines
        drawBorder(poseStackOrGraphics, px + 4, py + 4, pw - 8, ph - 8, applyAlpha(obsBevel, alpha * (portalAlpha * 1.5f)));
    }

    // 5. REDSTONE CIRCUIT: Interlocking conduit traces with logic nodes & corner chamfers
    public static void drawRedstoneCircuitBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float traceAlpha = light ? 0.16f : 0.25f;

        int redBright = 0xFFEF4444;
        int redDeep = 0xFF991B1B;

        int inset1 = 14;
        int inset2 = 26;

        // Outer conduit ring with corner cutouts
        drawBorder(poseStackOrGraphics, x + inset1, y + inset1, width - inset1 * 2, height - inset1 * 2, applyAlpha(redDeep, alpha * traceAlpha));
        drawBorder(poseStackOrGraphics, x + inset2, y + inset2, width - inset2 * 2, height - inset2 * 2, applyAlpha(redBright, alpha * (traceAlpha * 0.8f)));

        // Cross-conduit connecting rays from outer to inner ring
        int cx = x + width / 2;
        int cy = y + height / 2;

        Services.PLATFORM.fill(poseStackOrGraphics, cx - 1, y + inset1, cx + 1, y + inset2, applyAlpha(redBright, alpha * traceAlpha));
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 1, y + height - inset2, cx + 1, y + height - inset1, applyAlpha(redBright, alpha * traceAlpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + inset1, cy - 1, x + inset2, cy + 1, applyAlpha(redBright, alpha * traceAlpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + width - inset2, cy - 1, x + width - inset1, cy + 1, applyAlpha(redBright, alpha * traceAlpha));

        // Logic Diode / Comparator Diamond Nodes along the bus
        drawRotatedDiamond(poseStackOrGraphics, cx, y + inset1, 4, redBright, alpha * traceAlpha * 1.4f);
        drawRotatedDiamond(poseStackOrGraphics, cx, y + height - inset1, 4, redBright, alpha * traceAlpha * 1.4f);
        drawRotatedDiamond(poseStackOrGraphics, x + inset1, cy, 4, redBright, alpha * traceAlpha * 1.4f);
        drawRotatedDiamond(poseStackOrGraphics, x + width - inset1, cy, 4, redBright, alpha * traceAlpha * 1.4f);

        // Center glowing logic chip
        int chipSize = 22;
        int chipX = cx - chipSize / 2;
        int chipY = cy - chipSize / 2;
        drawBorder(poseStackOrGraphics, chipX, chipY, chipSize, chipSize, applyAlpha(redBright, alpha * traceAlpha * 1.3f));
        drawRotatedDiamond(poseStackOrGraphics, cx, cy, 5, redBright, alpha * traceAlpha * 1.5f);
    }

    // =========================================================================
    // Geometric Drawing Primitives
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

    public static void drawDiagonalBar(Object poseStackOrGraphics, int x1, int y1, int x2, int y2, int thickness, int color, float alpha) {
        int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        if (steps <= 0) return;

        int c = applyAlpha(color, alpha);
        int halfThick = thickness / 2;

        for (int i = 0; i <= steps; i++) {
            float t = (float) i / (float) steps;
            int px = (int) Math.round(x1 + (x2 - x1) * t);
            int py = (int) Math.round(y1 + (y2 - y1) * t);
            Services.PLATFORM.fill(poseStackOrGraphics, px - halfThick, py - halfThick, px + halfThick + 1, py + halfThick + 1, c);
        }
    }

    public static void drawRotatedDiamond(Object poseStackOrGraphics, int cx, int cy, int radius, int color, float alpha) {
        int c = applyAlpha(color, alpha);
        for (int dy = -radius; dy <= radius; dy++) {
            int span = radius - Math.abs(dy);
            Services.PLATFORM.fill(poseStackOrGraphics, cx - span, cy + dy, cx + span + 1, cy + dy + 1, c);
        }
    }

    public static void drawOctagonRing(Object poseStackOrGraphics, int cx, int cy, int radius, int thickness, int color, float alpha) {
        int c = applyAlpha(color, alpha);
        int r = radius;
        int t = thickness;

        // 8 symmetrical line segments forming an octagon
        int cornerOffset = (int) (r * 0.707f);
        // Top and bottom horizontal segments
        Services.PLATFORM.fill(poseStackOrGraphics, cx - cornerOffset, cy - r, cx + cornerOffset, cy - r + t, c);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - cornerOffset, cy + r - t, cx + cornerOffset, cy + r, c);
        // Left and right vertical segments
        Services.PLATFORM.fill(poseStackOrGraphics, cx - r, cy - cornerOffset, cx - r + t, cy + cornerOffset, c);
        Services.PLATFORM.fill(poseStackOrGraphics, cx + r - t, cy - cornerOffset, cx + r, cy + cornerOffset, c);

        // Diagonal connecting corners
        drawDiagonalBar(poseStackOrGraphics, cx - r, cy - cornerOffset, cx - cornerOffset, cy - r, t, color, alpha);
        drawDiagonalBar(poseStackOrGraphics, cx + cornerOffset, cy - r, cx + r, cy - cornerOffset, t, color, alpha);
        drawDiagonalBar(poseStackOrGraphics, cx - r, cy + cornerOffset, cx - cornerOffset, cy + r, t, color, alpha);
        drawDiagonalBar(poseStackOrGraphics, cx + cornerOffset, cy + r, cx + r, cy + cornerOffset, t, color, alpha);
    }

    public static void drawChevron(Object poseStackOrGraphics, int x, int y, int w, int h, boolean pointingLeft, int color, float alpha) {
        int c = applyAlpha(color, alpha);
        int halfH = h / 2;
        int tipX = pointingLeft ? x : x + w;
        int baseExtX = pointingLeft ? x + w : x;

        for (int dy = 0; dy <= halfH; dy++) {
            float frac = (float) dy / (float) halfH;
            int px = (int) Math.round(tipX + (baseExtX - tipX) * frac);
            Services.PLATFORM.fill(poseStackOrGraphics, px, y + halfH - dy, px + 2, y + halfH - dy + 1, c);
            Services.PLATFORM.fill(poseStackOrGraphics, px, y + halfH + dy, px + 2, y + halfH + dy + 1, c);
        }
    }

    public static void drawPickaxeHead(Object poseStackOrGraphics, int tipX, int tipY, boolean leftSided, int color, float alpha) {
        int c = applyAlpha(color, alpha);
        int span = 18;
        int dir = leftSided ? -1 : 1;

        // Crescent curve of the pickaxe head
        for (int i = 0; i < span; i++) {
            int px = tipX + dir * (i - span / 2);
            int py = tipY + (int) Math.round(Math.pow((double) i / (span / 2) - 1.0, 2) * 5.0);
            Services.PLATFORM.fill(poseStackOrGraphics, px - 1, py - 1, px + 2, py + 2, c);
        }
    }
}
