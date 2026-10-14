package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.platform.Services;

import java.util.Arrays;
import java.util.List;

// Modular GUI background registry and pixel-art Minecraft backdrop renderer for Buildscape configuration screens.
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

    // --- 16x16 Minecraft Item Sprite Matrices ---

    // Authentic 16x16 Minecraft Sword Matrix (hilt bottom-left to tip top-right)
    private static final int[][] SWORD_SPRITE = {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 1, 3, 2, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 5, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 4, 5, 5, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 5, 4, 1, 6, 1, 1, 5, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 1, 6, 7, 1, 4, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 4, 6, 7, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, 5, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    // Authentic 16x16 Minecraft Pickaxe Matrix (handle bottom-left to curved head top-right)
    private static final int[][] PICKAXE_SPRITE = {
            { 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 3, 3, 3, 3, 2, 2, 2, 1, 1, 0 },
            { 0, 0, 0, 1, 3, 3, 2, 2, 2, 2, 2, 2, 1, 2, 3, 1 },
            { 0, 0, 1, 3, 2, 1, 0, 0, 0, 0, 0, 1, 2, 2, 2, 1 },
            { 0, 1, 3, 2, 1, 0, 0, 0, 0, 0, 1, 6, 1, 2, 2, 1 },
            { 0, 1, 2, 1, 0, 0, 0, 0, 0, 1, 7, 6, 1, 1, 1, 0 },
            { 1, 2, 1, 0, 0, 0, 0, 0, 1, 7, 6, 1, 0, 0, 0, 0 },
            { 1, 1, 0, 0, 0, 0, 0, 1, 7, 6, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 1, 7, 6, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 7, 6, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 7, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 7, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 7, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 7, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, 7, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    // 8x8 Creeper Mask: 1 = dark cutout, 0 = skin
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

    public static final Background CREEPER = new Background(
            "creeper", "Creeper Green",
            ConfigBackgrounds::drawCreeperBackground
    );

    public static final Background GOLDEN_CREEPER = new Background(
            "golden_creeper", "Golden Creeper",
            ConfigBackgrounds::drawGoldenCreeperBackground
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
            "redstone_circuit", "Redstone Logic",
            ConfigBackgrounds::drawRedstoneCircuitBackground
    );

    private static final List<Background> BACKGROUNDS = Arrays.asList(
            NONE,
            CREEPER,
            GOLDEN_CREEPER,
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
    // Background Renderers: Pixel-Art Minecraft Backdrops
    // =========================================================================

    // 1. CREEPER: Iconic Green Creeper Face & Emerald Wash
    public static void drawCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.07f : 0.12f;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, 0xFF0B2914, 0xFF166534, 0xFF061A0C, alpha * washAlpha);

        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 24);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(144, Math.max(48, (faceSize / 8) * 8));
        int cellSize = faceSize / 8;

        int faceX = x + (width - faceSize) / 2;
        int faceY = y + (height - faceSize) / 2;

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

        // Flanking chevrons
        int chevH = faceSize / 2;
        int chevY = faceY + (faceSize - chevH) / 2;
        if (faceX - 16 > x + 8) drawChevron(poseStackOrGraphics, faceX - 16, chevY, 8, chevH, true, headBorder, alpha * headAlpha);
        if (faceX + faceSize + 24 < x + width) drawChevron(poseStackOrGraphics, faceX + faceSize + 16, chevY, 8, chevH, false, headBorder, alpha * headAlpha);
    }

    // 2. GOLDEN CREEPER: Shimmering Gold Creeper Face & Warm Amber Wash
    public static void drawGoldenCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(poseStackOrGraphics, x, y, width, height, 0xFF2B1D04, 0xFFB45309, 0xFF1C1202, alpha * washAlpha);

        int maxFaceW = Math.max(32, width - 24);
        int maxFaceH = Math.max(32, height - 24);
        int faceSize = Math.min(maxFaceW, maxFaceH);
        faceSize = Math.min(144, Math.max(48, (faceSize / 8) * 8));
        int cellSize = faceSize / 8;

        int faceX = x + (width - faceSize) / 2;
        int faceY = y + (height - faceSize) / 2;

        float headAlpha = light ? 0.18f : 0.25f;
        float cutoutAlpha = light ? 0.35f : 0.45f;

        // Radiant multi-tone golden skin shades
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

        // Flanking golden chevrons
        int chevH = faceSize / 2;
        int chevY = faceY + (faceSize - chevH) / 2;
        if (faceX - 16 > x + 8) drawChevron(poseStackOrGraphics, faceX - 16, chevY, 8, chevH, true, goldBorder, alpha * headAlpha);
        if (faceX + faceSize + 24 < x + width) drawChevron(poseStackOrGraphics, faceX + faceSize + 16, chevY, 8, chevH, false, goldBorder, alpha * headAlpha);
    }

    // 3. CROSSED SWORDS: Authentic 16x16 Minecraft Diamond Swords crossed diagonally
    public static void drawCrossedSwordsBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float spriteAlpha = light ? 0.22f : 0.32f;

        // Diamond Sword 16x16 palette
        int[] palette = {
                0x00000000, // 0: transparent
                light ? 0xFF0369A1 : 0xFF0C4A6E, // 1: outline dark cyan
                light ? 0xFF0284C7 : 0xFF38BDF8, // 2: diamond blade body
                light ? 0xFF38BDF8 : 0xFFE0F2FE, // 3: bright diamond specular highlight
                light ? 0xFF1E293B : 0xFF334155, // 4: guard dark outline
                light ? 0xFFD97706 : 0xFFF59E0B, // 5: guard gold inlay
                light ? 0xFF78350F : 0xFF92400E, // 6: wood grip
                light ? 0xFF92400E : 0xFFD97706  // 7: light wood highlight
        };

        // Determine pixel size: scale 16x16 sprite to fill ~120-160px
        int targetSpan = Math.min(width - 24, height - 24);
        int pixelSize = Math.max(3, Math.min(8, targetSpan / 18));
        int spriteDim = 16 * pixelSize;

        int cx = x + (width - spriteDim) / 2;
        int cy = y + (height - spriteDim) / 2;

        // Subtle diamond blue ambient ring behind intersection
        int ringR = spriteDim / 2 + 4;
        drawOctagonRing(poseStackOrGraphics, x + width / 2, y + height / 2, ringR, 2, palette[2], alpha * (spriteAlpha * 0.40f));

        // Sword 1: Normal (pointing up-right)
        drawPixelMatrix(poseStackOrGraphics, cx, cy, SWORD_SPRITE, palette, pixelSize, false, alpha * spriteAlpha);

        // Sword 2: Horizontally Mirrored (pointing up-left)
        drawPixelMatrix(poseStackOrGraphics, cx, cy, SWORD_SPRITE, palette, pixelSize, true, alpha * spriteAlpha);
    }

    // 4. CROSSED PICKAXES: Authentic 16x16 Minecraft Diamond Pickaxes crossed symmetrically
    public static void drawPickaxeBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float spriteAlpha = light ? 0.22f : 0.32f;

        // Diamond Pickaxe 16x16 palette
        int[] palette = {
                0x00000000, // 0: transparent
                light ? 0xFF0369A1 : 0xFF0C4A6E, // 1: outline dark cyan
                light ? 0xFF0284C7 : 0xFF38BDF8, // 2: diamond pickaxe body
                light ? 0xFF38BDF8 : 0xFFE0F2FE, // 3: bright diamond specular highlight
                light ? 0xFF1E293B : 0xFF334155, // 4: dark metal
                light ? 0xFFD97706 : 0xFFF59E0B, // 5: gold highlight
                light ? 0xFF78350F : 0xFF92400E, // 6: wood handle
                light ? 0xFF92400E : 0xFFD97706  // 7: light wood highlight
        };

        int targetSpan = Math.min(width - 24, height - 24);
        int pixelSize = Math.max(3, Math.min(8, targetSpan / 18));
        int spriteDim = 16 * pixelSize;

        int cx = x + (width - spriteDim) / 2;
        int cy = y + (height - spriteDim) / 2;

        // Subtle amber/cyan ambient ring
        int ringR = spriteDim / 2 + 4;
        drawOctagonRing(poseStackOrGraphics, x + width / 2, y + height / 2, ringR, 2, palette[2], alpha * (spriteAlpha * 0.40f));

        // Pickaxe 1: Normal
        drawPixelMatrix(poseStackOrGraphics, cx, cy, PICKAXE_SPRITE, palette, pixelSize, false, alpha * spriteAlpha);

        // Pickaxe 2: Mirrored
        drawPixelMatrix(poseStackOrGraphics, cx, cy, PICKAXE_SPRITE, palette, pixelSize, true, alpha * spriteAlpha);
    }

    // 5. NETHER PORTAL: Authentic 4x5 Minecraft Obsidian Blocks Frame with Swirling Purple Portal Core
    public static void drawNetherPortalBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float portalAlpha = light ? 0.22f : 0.32f;

        // Portal frame is 4 blocks wide x 5 blocks high (standard Minecraft portal)
        int blockW = Math.max(16, Math.min(32, width / 7));
        int blockH = blockW;

        int portalW = blockW * 4;
        int portalH = blockH * 5;

        int px = x + (width - portalW) / 2;
        int py = y + (height - portalH) / 2;

        // 1. Obsidian Block Palette (deep purple/black mottled blocks)
        int[] obsShades = { 0xFF140C26, 0xFF1F1238, 0xFF2A164C, 0xFF0D061A, 0xFF1A0E30 };

        // Draw 4x5 block grid (outer perimeter = obsidian, inner 2x3 = portal core)
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                boolean isFrame = (row == 0 || row == 4 || col == 0 || col == 3);
                int bx = px + col * blockW;
                int by = py + row * blockH;

                if (isFrame) {
                    // Obsidian block with authentic 4-quadrant pixel mottling
                    int halfW = blockW / 2;
                    int halfH = blockH / 2;
                    int s1 = obsShades[(row * 4 + col) % obsShades.length];
                    int s2 = obsShades[(row * 4 + col + 1) % obsShades.length];
                    int s3 = obsShades[(row * 4 + col + 2) % obsShades.length];
                    int s4 = obsShades[(row * 4 + col + 3) % obsShades.length];

                    Services.PLATFORM.fill(poseStackOrGraphics, bx, by, bx + halfW, by + halfH, applyAlpha(s1, alpha * portalAlpha * 1.3f));
                    Services.PLATFORM.fill(poseStackOrGraphics, bx + halfW, by, bx + blockW, by + halfH, applyAlpha(s2, alpha * portalAlpha * 1.3f));
                    Services.PLATFORM.fill(poseStackOrGraphics, bx, by + halfH, bx + halfW, by + blockH, applyAlpha(s3, alpha * portalAlpha * 1.3f));
                    Services.PLATFORM.fill(poseStackOrGraphics, bx + halfW, by + halfH, bx + blockW, by + blockH, applyAlpha(s4, alpha * portalAlpha * 1.3f));

                    // 1px beveled block perimeter
                    drawBorder(poseStackOrGraphics, bx, by, blockW, blockH, applyAlpha(0xFF3B1D66, alpha * portalAlpha * 0.9f));
                }
            }
        }

        // 2. Inner Portal Vortex (2 blocks wide x 3 blocks high)
        int coreX = px + blockW;
        int coreY = py + blockH;
        int coreW = blockW * 2;
        int coreH = blockH * 3;

        // Mystical purple gradient core
        int topViolet = 0xFF581C87;
        int midMagenta = 0xFFA855F7;
        int botMidnight = 0xFF2E1065;
        drawVerticalGradientRect(poseStackOrGraphics, coreX, coreY, coreW, coreH, topViolet, midMagenta, botMidnight, alpha * portalAlpha);

        // Pixelated wavy portal columns
        int colCount = 6;
        int colW = Math.max(2, coreW / colCount);
        int[] purpleSwirls = { 0xFF7E22CE, 0xFF9333EA, 0xFFA855F7, 0xFFC084FC, 0xFF9333EA, 0xFF6B21A8 };

        for (int c = 0; c < colCount; c++) {
            int cx = coreX + c * colW;
            int curColW = (c == colCount - 1) ? (coreX + coreW - cx) : colW;
            int colTint = purpleSwirls[c % purpleSwirls.length];

            for (int r = 0; r < coreH; r += 4) {
                int waveShift = (int) (Math.sin((r + c * 8) * 0.2) * 2);
                int segH = Math.min(4, coreH - r);
                Services.PLATFORM.fill(poseStackOrGraphics, cx + waveShift, coreY + r, cx + waveShift + curColW, coreY + r + segH, applyAlpha(colTint, alpha * portalAlpha * 0.45f));
            }
        }

        // Floating glowing portal particles
        int[] partOffsets = { 6, 14, 24, 38, 50, 64, 76, 92 };
        for (int i = 0; i < partOffsets.length; i++) {
            int offY = partOffsets[i];
            if (offY + 4 >= coreH) continue;
            int offX = (int) ((Math.sin(i * 1.9) * 0.5 + 0.5) * (coreW - 8)) + 4;
            int sz = (i % 2 == 0) ? 3 : 2;
            Services.PLATFORM.fill(poseStackOrGraphics, coreX + offX, coreY + offY, coreX + offX + sz, coreY + offY + sz, applyAlpha(0xFFE9D5FF, alpha * portalAlpha * 1.5f));
        }

        // Portal inner frame edge shadow
        drawBorder(poseStackOrGraphics, coreX, coreY, coreW, coreH, applyAlpha(0xFF140C26, alpha * portalAlpha * 1.4f));
    }

    // 6. REDSTONE LOGIC: Dappled Redstone Dust Tracks, Repeaters & Logic Gates
    public static void drawRedstoneCircuitBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float traceAlpha = light ? 0.20f : 0.30f;

        int redLit = 0xFFEF4444;
        int redMid = 0xFFDC2626;
        int redDark = 0xFF991B1B;
        int stoneBase = light ? 0xFF94A3B8 : 0xFF475569;
        int torchWood = 0xFF78350F;

        int inset = 18;
        int trackW = width - inset * 2;
        int trackH = height - inset * 2;

        // Perimeter Redstone Dust Line (dappled 3px wide wire)
        drawRedstoneWire(poseStackOrGraphics, x + inset, y + inset, trackW, true, redLit, redMid, redDark, alpha * traceAlpha);
        drawRedstoneWire(poseStackOrGraphics, x + inset, y + inset + trackH, trackW, true, redLit, redMid, redDark, alpha * traceAlpha);
        drawRedstoneWire(poseStackOrGraphics, x + inset, y + inset, trackH, false, redLit, redMid, redDark, alpha * traceAlpha);
        drawRedstoneWire(poseStackOrGraphics, x + inset + trackW, y + inset, trackH, false, redLit, redMid, redDark, alpha * traceAlpha);

        int cx = x + width / 2;
        int cy = y + height / 2;

        // Cross Redstone Tracks connecting center
        drawRedstoneWire(poseStackOrGraphics, cx - 1, y + inset, cy - (y + inset), false, redLit, redMid, redDark, alpha * traceAlpha);
        drawRedstoneWire(poseStackOrGraphics, cx - 1, cy, (y + inset + trackH) - cy, false, redLit, redMid, redDark, alpha * traceAlpha);
        drawRedstoneWire(poseStackOrGraphics, x + inset, cy - 1, cx - (x + inset), true, redLit, redMid, redDark, alpha * traceAlpha);
        drawRedstoneWire(poseStackOrGraphics, cx, cy - 1, (x + inset + trackW) - cx, true, redLit, redMid, redDark, alpha * traceAlpha);

        // Center Minecraft Redstone Repeater Component
        int repW = 34;
        int repH = 22;
        int repX = cx - repW / 2;
        int repY = cy - repH / 2;

        // Smooth stone slab base
        Services.PLATFORM.fill(poseStackOrGraphics, repX, repY, repX + repW, repY + repH, applyAlpha(stoneBase, alpha * traceAlpha * 1.1f));
        drawBorder(poseStackOrGraphics, repX, repY, repW, repH, applyAlpha(0xFF1E293B, alpha * traceAlpha * 1.3f));

        // Two Redstone Torches on the Repeater (Stationary Lit Torch + Adjustable Slider Torch)
        // Torch 1: Left
        int t1x = repX + 8;
        int t1y = repY + 6;
        Services.PLATFORM.fill(poseStackOrGraphics, t1x, t1y + 4, t1x + 3, t1y + 10, applyAlpha(torchWood, alpha * traceAlpha * 1.5f));
        Services.PLATFORM.fill(poseStackOrGraphics, t1x - 1, t1y, t1x + 4, t1y + 4, applyAlpha(redLit, alpha * traceAlpha * 1.8f));

        // Torch 2: Right
        int t2x = repX + repW - 11;
        int t2y = repY + 6;
        Services.PLATFORM.fill(poseStackOrGraphics, t2x, t2y + 4, t2x + 3, t2y + 10, applyAlpha(torchWood, alpha * traceAlpha * 1.5f));
        Services.PLATFORM.fill(poseStackOrGraphics, t2x - 1, t2y, t2x + 4, t2y + 4, applyAlpha(redLit, alpha * traceAlpha * 1.8f));

        // Redstone Dust Connection Arrow on Repeater Base
        drawRotatedDiamond(poseStackOrGraphics, cx, cy, 3, redLit, alpha * traceAlpha * 1.6f);
    }

    // =========================================================================
    // Drawing Helpers & Pixel Matrix Renderers
    // =========================================================================

    public static void drawPixelMatrix(Object poseStackOrGraphics, int startX, int startY, int[][] matrix, int[] palette, int pixelSize, boolean mirrorH, float alpha) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int colIdx = mirrorH ? (cols - 1 - c) : c;
                int val = matrix[r][colIdx];
                if (val <= 0 || val >= palette.length) continue;
                int color = palette[val];
                int px = startX + c * pixelSize;
                int py = startY + r * pixelSize;
                Services.PLATFORM.fill(poseStackOrGraphics, px, py, px + pixelSize, py + pixelSize, applyAlpha(color, alpha));
            }
        }
    }

    private static void drawRedstoneWire(Object poseStackOrGraphics, int x, int y, int length, boolean horizontal, int colLit, int colMid, int colDark, float alpha) {
        int[] shades = { colLit, colMid, colDark, colMid, colLit };
        int wireThickness = 3;

        if (horizontal) {
            for (int i = 0; i < length; i += 2) {
                int col = shades[(i / 2) % shades.length];
                int segW = Math.min(2, length - i);
                Services.PLATFORM.fill(poseStackOrGraphics, x + i, y, x + i + segW, y + wireThickness, applyAlpha(col, alpha));
            }
        } else {
            for (int j = 0; j < length; j += 2) {
                int col = shades[(j / 2) % shades.length];
                int segH = Math.min(2, length - j);
                Services.PLATFORM.fill(poseStackOrGraphics, x, y + j, x + wireThickness, y + j + segH, applyAlpha(col, alpha));
            }
        }
    }

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

        int cornerOffset = (int) (r * 0.707f);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - cornerOffset, cy - r, cx + cornerOffset, cy - r + t, c);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - cornerOffset, cy + r - t, cx + cornerOffset, cy + r, c);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - r, cy - cornerOffset, cx - r + t, cy + cornerOffset, c);
        Services.PLATFORM.fill(poseStackOrGraphics, cx + r - t, cy - cornerOffset, cx + r, cy + cornerOffset, c);
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
}
