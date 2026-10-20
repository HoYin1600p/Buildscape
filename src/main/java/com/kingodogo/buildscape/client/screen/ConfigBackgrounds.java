package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.config.ConfigPaths;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Modular GUI background registry and backdrop renderer for Buildscape configuration screens.
 * Provides optimized hardware-shaded backdrop gradients and pixel-perfect mob face presets
 * featuring authentic Minecraft Creeper color variations (Green, Golden, Crimson, Warped, Blue, Purple, Red, Orange, White, Black, Yellow, Lime, Cyan, Light Blue, Pink, Magenta, Brown, Gray).
 */
public class ConfigBackgrounds {

    @FunctionalInterface
    public interface BackgroundRenderer {
        void render(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme);
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

        public void draw(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
            if (renderer != null) {
                renderer.render(poseStack, x, y, width, height, alpha, theme);
            }
        }
    }

    /**
     * Common face positioning helper for 8x8 pixel mob backdrops.
     * Computes integer cell scaling and viewport centering in one step.
     */
    public static class FaceBounds {
        public final int x;
        public final int y;
        public final int size;
        public final int cellSize;

        public FaceBounds(int x, int y, int width, int height) {
            int maxFaceW = Math.max(32, width - 24);
            int maxFaceH = Math.max(32, height - 32);
            int baseSize = Math.min(maxFaceW, maxFaceH);
            this.size = Math.min(136, Math.max(48, (baseSize / 8) * 8));
            this.cellSize = this.size / 8;
            this.x = x + (width - this.size) / 2;
            int topMargin = 26;
            int availH = Math.max(this.size, height - topMargin);
            this.y = y + topMargin + (availH - this.size) / 2;
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

    // --- Built-in Background Presets ---

    public static final Background NONE = new Background(
            "none", "None",
            (poseStack, x, y, width, height, alpha, theme) -> {}
    );

    // Creeper Presets
    public static final Background GREEN_CREEPER = new Background(
            "green_creeper", "Green Creeper",
            ConfigBackgrounds::drawGreenCreeperBackground
    );

    public static final Background GOLDEN_CREEPER = new Background(
            "golden_creeper", "Golden Creeper",
            ConfigBackgrounds::drawGoldenCreeperBackground
    );

    public static final Background CRIMSON_CREEPER = new Background(
            "crimson_creeper", "Crimson Creeper",
            ConfigBackgrounds::drawCrimsonCreeperBackground
    );

    public static final Background WARPED_CREEPER = new Background(
            "warped_creeper", "Warped Creeper",
            ConfigBackgrounds::drawWarpedCreeperBackground
    );

    public static final Background BLUE_CREEPER = new Background(
            "blue_creeper", "Blue Creeper",
            ConfigBackgrounds::drawBlueCreeperBackground
    );

    public static final Background PURPLE_CREEPER = new Background(
            "purple_creeper", "Purple Creeper",
            ConfigBackgrounds::drawPurpleCreeperBackground
    );

    public static final Background RED_CREEPER = new Background(
            "red_creeper", "Red Creeper",
            ConfigBackgrounds::drawRedCreeperBackground
    );

    public static final Background ORANGE_CREEPER = new Background(
            "orange_creeper", "Orange Creeper",
            ConfigBackgrounds::drawOrangeCreeperBackground
    );

    public static final Background WHITE_CREEPER = new Background(
            "white_creeper", "White Creeper",
            ConfigBackgrounds::drawWhiteCreeperBackground
    );

    public static final Background BLACK_CREEPER = new Background(
            "black_creeper", "Black Creeper",
            ConfigBackgrounds::drawBlackCreeperBackground
    );

    public static final Background YELLOW_CREEPER = new Background(
            "yellow_creeper", "Yellow Creeper",
            ConfigBackgrounds::drawYellowCreeperBackground
    );

    public static final Background LIME_CREEPER = new Background(
            "lime_creeper", "Lime Creeper",
            ConfigBackgrounds::drawLimeCreeperBackground
    );

    public static final Background CYAN_CREEPER = new Background(
            "cyan_creeper", "Cyan Creeper",
            ConfigBackgrounds::drawCyanCreeperBackground
    );

    public static final Background LIGHT_BLUE_CREEPER = new Background(
            "light_blue_creeper", "Light Blue Creeper",
            ConfigBackgrounds::drawLightBlueCreeperBackground
    );

    public static final Background PINK_CREEPER = new Background(
            "pink_creeper", "Pink Creeper",
            ConfigBackgrounds::drawPinkCreeperBackground
    );

    public static final Background MAGENTA_CREEPER = new Background(
            "magenta_creeper", "Magenta Creeper",
            ConfigBackgrounds::drawMagentaCreeperBackground
    );

    public static final Background BROWN_CREEPER = new Background(
            "brown_creeper", "Brown Creeper",
            ConfigBackgrounds::drawBrownCreeperBackground
    );

    public static final Background GRAY_CREEPER = new Background(
            "gray_creeper", "Gray Creeper",
            ConfigBackgrounds::drawGrayCreeperBackground
    );

    private static final List<Background> BACKGROUNDS = new java.util.ArrayList<>(Arrays.asList(
            NONE,
            GREEN_CREEPER,
            GOLDEN_CREEPER,
            CRIMSON_CREEPER,
            WARPED_CREEPER,
            BLUE_CREEPER,
            PURPLE_CREEPER,
            RED_CREEPER,
            ORANGE_CREEPER,
            WHITE_CREEPER,
            BLACK_CREEPER,
            YELLOW_CREEPER,
            LIME_CREEPER,
            CYAN_CREEPER,
            LIGHT_BLUE_CREEPER,
            PINK_CREEPER,
            MAGENTA_CREEPER,
            BROWN_CREEPER,
            GRAY_CREEPER
    ));

    private static final List<String> BUILT_IN_IDS;
    static {
        List<String> ids = new ArrayList<>(BACKGROUNDS.size());
        for (Background b : BACKGROUNDS) {
            ids.add(b.id);
        }
        BUILT_IN_IDS = Collections.unmodifiableList(ids);
    }

    private static final Set<String> REGISTERED_BACKGROUND_IDS = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static final Set<String> LOADING_BACKGROUND_IDS = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static final java.util.concurrent.atomic.AtomicBoolean SCANNING = new java.util.concurrent.atomic.AtomicBoolean();
    private static long lastScanTimestamp = 0L;
    private static final long SCAN_INTERVAL_MS = 2000L;

    public static String formatDisplayName(String filenameOrId) {
        if (filenameOrId == null || filenameOrId.isBlank()) return "Custom";
        String s = filenameOrId.trim();

        int dot = s.lastIndexOf('.');
        if (dot > 0) {
            s = s.substring(0, dot);
        }

        s = s.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
        s = s.replace('_', ' ').replace('-', ' ').replace('.', ' ');

        String[] tokens = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(token.charAt(0)));
            if (token.length() > 1) {
                sb.append(token.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return sb.length() > 0 ? sb.toString() : "Custom";
    }

    public static String sanitizeId(String filenameOrId) {
        if (filenameOrId == null || filenameOrId.isBlank()) return "custom";
        String s = filenameOrId.trim();
        int dot = s.lastIndexOf('.');
        if (dot > 0) {
            s = s.substring(0, dot);
        }
        s = s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_");
        if (s.startsWith("_")) s = s.substring(1);
        if (s.endsWith("_")) s = s.substring(0, s.length() - 1);
        return s.isEmpty() ? "custom" : s;
    }

    public static Path getCustomBackgroundsDir() {
        try {
            Path configDir = ConfigPaths.root();
            Path dir = configDir.resolve("buildscape").resolve("backgrounds");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            return dir;
        } catch (Exception ignored) {
            return Path.of("config", "buildscape", "backgrounds");
        }
    }

    public static synchronized void refreshCustomBackgrounds() {
        long now = System.currentTimeMillis();
        if (now - lastScanTimestamp < SCAN_INTERVAL_MS || !SCANNING.compareAndSet(false, true)) {
            return;
        }
        lastScanTimestamp = now;

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                scanCustomBackgrounds();
            } finally {
                SCANNING.set(false);
            }
        }, net.minecraft.Util.backgroundExecutor());
    }

    private static void scanCustomBackgrounds() {
        List<Path> candidateDirs = new java.util.ArrayList<>();
        try {
            Path primary = getCustomBackgroundsDir();
            if (primary != null) candidateDirs.add(primary);
        } catch (Exception ignored) {}

        try {
            Path rootBg = Path.of("backgrounds");
            if (Files.isDirectory(rootBg) && !candidateDirs.contains(rootBg)) {
                candidateDirs.add(rootBg);
            }
        } catch (Exception ignored) {}

        try {
            Path configBg = Path.of("config", "buildscape", "backgrounds");
            if (Files.isDirectory(configBg) && !candidateDirs.contains(configBg)) {
                candidateDirs.add(configBg);
            }
        } catch (Exception ignored) {}

        for (Path dir : candidateDirs) {
            if (!Files.isDirectory(dir)) continue;
            try (Stream<Path> stream = Files.list(dir)) {
                stream.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                        .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                        .forEach(ConfigBackgrounds::loadCustomBackgroundFile);
            } catch (Exception ignored) {}
        }
    }

    private static void loadCustomBackgroundFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String id = sanitizeId(fileName);
        if (id.isEmpty()) return;

        if (REGISTERED_BACKGROUND_IDS.contains(id) || !LOADING_BACKGROUND_IDS.add(id)) return;

        String displayName = formatDisplayName(fileName);
        try (InputStream in = Files.newInputStream(filePath)) {
            NativeImage image = NativeImage.read(in);
            Minecraft.getInstance().execute(() -> {
                DynamicTexture texture = null;
                try {
                    texture = new DynamicTexture(image);
                    ResourceLocation texId = Minecraft.getInstance().getTextureManager().register("custom_bg_" + id, texture);
                    registerImageBackground(id, displayName, texId);
                    if (id.equalsIgnoreCase(BuildscapeClientConfig.get().getUiBackground())) setBackgroundById(id);
                } catch (Exception ignored) {
                    if (texture != null) texture.close(); else image.close();
                } finally {
                    LOADING_BACKGROUND_IDS.remove(id);
                }
            });
        } catch (Exception ignored) {
            LOADING_BACKGROUND_IDS.remove(id);
        }
    }

    public static Background registerImageBackground(String id, String displayName, ResourceLocation textureId) {
        String key = id.toLowerCase(Locale.ROOT);
        REGISTERED_BACKGROUND_IDS.add(key);
        Background bg = new Background(id, displayName, (poseStack, x, y, w, h, a, t) -> drawImageBackground(poseStack, textureId, x, y, w, h, a, t));
        BACKGROUNDS.add(bg);
        return bg;
    }

    private static int activeBackgroundIndex = 0;

    static {
        for (Background b : BACKGROUNDS) {
            REGISTERED_BACKGROUND_IDS.add(b.id.toLowerCase(Locale.ROOT));
        }

        try {
            refreshCustomBackgrounds();
        } catch (Exception ignored) {}

        try {
            String saved = BuildscapeClientConfig.get().getUiBackground();
            setBackgroundById(saved);
        } catch (Exception ignored) {}
    }

    public static List<Background> getAllBackgrounds() {
        long now = System.currentTimeMillis();
        if (now - lastScanTimestamp > SCAN_INTERVAL_MS) {
            refreshCustomBackgrounds();
        }
        return BACKGROUNDS;
    }

    public static Background getCurrentBackground() {
        getAllBackgrounds();
        if (activeBackgroundIndex < 0 || activeBackgroundIndex >= BACKGROUNDS.size()) {
            activeBackgroundIndex = 0;
        }
        return BACKGROUNDS.get(activeBackgroundIndex);
    }

    public static Background cycleNextBackground() {
        getAllBackgrounds();
        activeBackgroundIndex = (activeBackgroundIndex + 1) % BACKGROUNDS.size();
        Background bg = getCurrentBackground();
        try {
            BuildscapeClientConfig.get().setUiBackground(bg.id);
        } catch (Exception ignored) {}
        return bg;
    }

    public static Background cyclePrevBackground() {
        getAllBackgrounds();
        activeBackgroundIndex = (activeBackgroundIndex - 1 + BACKGROUNDS.size()) % BACKGROUNDS.size();
        Background bg = getCurrentBackground();
        try {
            BuildscapeClientConfig.get().setUiBackground(bg.id);
        } catch (Exception ignored) {}
        return bg;
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

    public static void drawCurrentBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        getCurrentBackground().draw(poseStack, x, y, width, height, alpha, theme);
    }

    // External images are shaded to preserve control and text contrast.
    public static void drawImageBackground(PoseStack poseStack, ResourceLocation texture, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f || texture == null) return;
        ConfigGuiBatch.flush();
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(poseStack, x, y, width, height, 0.0f, 0.0f, width, height, width, height);

        boolean light = theme != null && theme.isLight();
        int washCol = light ? 0xFFFFFFFF : 0xFF08080C;
        float washAlpha = light ? 0.32f : 0.42f;
        ConfigGuiBatch.fill(poseStack, x, y, x + width, y + height, applyAlpha(washCol, alpha * washAlpha));
    }

    // --- Creeper Palette Data ---

    private static final int[] ROW_GREEN_TOP = { 0xFF22C55E, 0xFF16A34A, 0xFF15803D, 0xFF16A34A, 0xFF15803D, 0xFF14532D, 0xFF0F3D1E, 0xFF0B2E16 };
    private static final int[] ROW_GREEN_ALT = { 0xFF4ADE80, 0xFF22C55E, 0xFF16A34A, 0xFF15803D, 0xFF16A34A, 0xFF15803D, 0xFF14532D, 0xFF0F3D1E };

    private static final int[] ROW_GOLD_TOP = { 0xFFFDE047, 0xFFFBBF24, 0xFFF59E0B, 0xFFFBBF24, 0xFFF59E0B, 0xFFD97706, 0xFFB45309, 0xFF92400E };
    private static final int[] ROW_GOLD_ALT = { 0xFFFEF08A, 0xFFFDE047, 0xFFFBBF24, 0xFFF59E0B, 0xFFD97706, 0xFFB45309, 0xFF92400E, 0xFF78350F };

    private static final int[] ROW_CRIMSON_TOP = { 0xFFFB7185, 0xFFF43F5E, 0xFFE11D48, 0xFFF43F5E, 0xFFE11D48, 0xFFBE123C, 0xFF9F1239, 0xFF881337 };
    private static final int[] ROW_CRIMSON_ALT = { 0xFFFDA4AF, 0xFFFB7185, 0xFFF43F5E, 0xFFE11D48, 0xFFBE123C, 0xFF9F1239, 0xFF881337, 0xFF4C0519 };

    private static final int[] ROW_WARPED_TOP = { 0xFF2DD4BF, 0xFF14B8A6, 0xFF0D9488, 0xFF14B8A6, 0xFF0D9488, 0xFF0F766E, 0xFF115E59, 0xFF134E4A };
    private static final int[] ROW_WARPED_ALT = { 0xFF5EEAD4, 0xFF2DD4BF, 0xFF14B8A6, 0xFF0D9488, 0xFF0F766E, 0xFF115E59, 0xFF134E4A, 0xFF042F2E };

    private static final int[] ROW_BLUE_TOP = { 0xFF3B82F6, 0xFF2563EB, 0xFF1D4ED8, 0xFF2563EB, 0xFF1D4ED8, 0xFF1E40AF, 0xFF1E3A8A, 0xFF172554 };
    private static final int[] ROW_BLUE_ALT = { 0xFF60A5FA, 0xFF3B82F6, 0xFF2563EB, 0xFF1D4ED8, 0xFF1E40AF, 0xFF1E3A8A, 0xFF172554, 0xFF0F172A };

    private static final int[] ROW_PURPLE_TOP = { 0xFFA855F7, 0xFF9333EA, 0xFF7E22CE, 0xFF9333EA, 0xFF7E22CE, 0xFF6B21A8, 0xFF581C87, 0xFF3B0764 };
    private static final int[] ROW_PURPLE_ALT = { 0xFFC084FC, 0xFFA855F7, 0xFF9333EA, 0xFF7E22CE, 0xFF6B21A8, 0xFF581C87, 0xFF3B0764, 0xFF2E0854 };

    private static final int[] ROW_RED_TOP = { 0xFFEF4444, 0xFFDC2626, 0xFFB91C1C, 0xFFDC2626, 0xFFB91C1C, 0xFF991B1B, 0xFF7F1D1D, 0xFF450A0A };
    private static final int[] ROW_RED_ALT = { 0xFFF87171, 0xFFEF4444, 0xFFDC2626, 0xFFB91C1C, 0xFF991B1B, 0xFF7F1D1D, 0xFF450A0A, 0xFF300606 };

    private static final int[] ROW_ORANGE_TOP = { 0xFFF97316, 0xFFEA580C, 0xFFC2410C, 0xFFEA580C, 0xFFC2410C, 0xFF9A3412, 0xFF7C2D12, 0xFF431407 };
    private static final int[] ROW_ORANGE_ALT = { 0xFFFB923C, 0xFFF97316, 0xFFEA580C, 0xFFC2410C, 0xFF9A3412, 0xFF7C2D12, 0xFF431407, 0xFF2E0E05 };

    private static final int[] ROW_WHITE_TOP = { 0xFFF8FAFC, 0xFFF1F5F9, 0xFFE2E8F0, 0xFFF1F5F9, 0xFFE2E8F0, 0xFFCBD5E1, 0xFF94A3B8, 0xFF64748B };
    private static final int[] ROW_WHITE_ALT = { 0xFFFFFFFF, 0xFFF8FAFC, 0xFFF1F5F9, 0xFFE2E8F0, 0xFFCBD5E1, 0xFF94A3B8, 0xFF64748B, 0xFF475569 };

    private static final int[] ROW_BLACK_TOP = { 0xFF334155, 0xFF1E293B, 0xFF0F172A, 0xFF1E293B, 0xFF0F172A, 0xFF0B1120, 0xFF080C17, 0xFF05070E };
    private static final int[] ROW_BLACK_ALT = { 0xFF475569, 0xFF334155, 0xFF1E293B, 0xFF0F172A, 0xFF0B1120, 0xFF080C17, 0xFF05070E, 0xFF030408 };

    private static final int[] ROW_YELLOW_TOP = { 0xFFFACC15, 0xFFEAB308, 0xFFCA8A04, 0xFFEAB308, 0xFFCA8A04, 0xFFA16207, 0xFF854D0E, 0xFF713F12 };
    private static final int[] ROW_YELLOW_ALT = { 0xFFFEF08A, 0xFFFDE047, 0xFFFACC15, 0xFFEAB308, 0xFFCA8A04, 0xFFA16207, 0xFF854D0E, 0xFF582D09 };

    private static final int[] ROW_LIME_TOP = { 0xFF84CC16, 0xFF65A30D, 0xFF4D7C0F, 0xFF65A30D, 0xFF4D7C0F, 0xFF3F6212, 0xFF365314, 0xFF1A2E05 };
    private static final int[] ROW_LIME_ALT = { 0xFFA3E635, 0xFF84CC16, 0xFF65A30D, 0xFF4D7C0F, 0xFF3F6212, 0xFF365314, 0xFF1A2E05, 0xFF101D03 };

    private static final int[] ROW_CYAN_TOP = { 0xFF06B6D4, 0xFF0891B2, 0xFF0E7490, 0xFF0891B2, 0xFF0E7490, 0xFF155E75, 0xFF164E63, 0xFF083344 };
    private static final int[] ROW_CYAN_ALT = { 0xFF22D3EE, 0xFF06B6D4, 0xFF0891B2, 0xFF0E7490, 0xFF155E75, 0xFF164E63, 0xFF083344, 0xFF04202C };

    private static final int[] ROW_LIGHT_BLUE_TOP = { 0xFF38BDF8, 0xFF0284C7, 0xFF0369A1, 0xFF0284C7, 0xFF0369A1, 0xFF075985, 0xFF0C4A6E, 0xFF082F49 };
    private static final int[] ROW_LIGHT_BLUE_ALT = { 0xFF7DD3FC, 0xFF38BDF8, 0xFF0284C7, 0xFF0369A1, 0xFF075985, 0xFF0C4A6E, 0xFF082F49, 0xFF051E30 };

    private static final int[] ROW_PINK_TOP = { 0xFFF472B6, 0xFFEC4899, 0xFFDB2777, 0xFFEC4899, 0xFFDB2777, 0xFFBE185D, 0xFF9D174D, 0xFF831843 };
    private static final int[] ROW_PINK_ALT = { 0xFFF9A8D4, 0xFFF472B6, 0xFFEC4899, 0xFFDB2777, 0xFFBE185D, 0xFF9D174D, 0xFF831843, 0xFF500728 };

    private static final int[] ROW_MAGENTA_TOP = { 0xFFE879F9, 0xFFD946EF, 0xFFC026D3, 0xFFD946EF, 0xFFC026D3, 0xFFA21CAF, 0xFF86198F, 0xFF701A75 };
    private static final int[] ROW_MAGENTA_ALT = { 0xFFF0ABFC, 0xFFE879F9, 0xFFD946EF, 0xFFC026D3, 0xFFA21CAF, 0xFF86198F, 0xFF701A75, 0xFF4A044E };

    private static final int[] ROW_BROWN_TOP = { 0xFF8D5B4C, 0xFF784537, 0xFF63372C, 0xFF784537, 0xFF63372C, 0xFF502B22, 0xFF3E1F18, 0xFF2C1510 };
    private static final int[] ROW_BROWN_ALT = { 0xFFA67161, 0xFF8D5B4C, 0xFF784537, 0xFF63372C, 0xFF502B22, 0xFF3E1F18, 0xFF2C1510, 0xFF1D0D0A };

    private static final int[] ROW_GRAY_TOP = { 0xFF64748B, 0xFF475569, 0xFF334155, 0xFF475569, 0xFF334155, 0xFF1E293B, 0xFF0F172A, 0xFF020617 };
    private static final int[] ROW_GRAY_ALT = { 0xFF94A3B8, 0xFF64748B, 0xFF475569, 0xFF334155, 0xFF1E293B, 0xFF0F172A, 0xFF020617, 0xFF01030B };

    /**
     * Unified, high-performance Creeper backdrop renderer.
     * Combines Gouraud-shaded wash gradients with batch-optimized face and cutout geometry.
     */
    private static void drawCreeperPreset(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme,
                                         int topWash, int midWash, int botWash,
                                         int[] rowTop, int[] rowAlt,
                                         int borderLight, int borderDark,
                                         int cutoutLight, int cutoutDark) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(poseStack, x, y, width, height, topWash, midWash, botWash, alpha * washAlpha);

        FaceBounds b = new FaceBounds(x, y, width, height);
        float headAlpha = light ? 0.18f : 0.25f;
        float cutoutAlpha = light ? 0.35f : 0.45f;
        int cutoutCol = applyAlpha(light ? cutoutLight : cutoutDark, alpha * cutoutAlpha);

        // 1. Draw skin cells (only for cells outside cutouts, avoiding redundant quad submission)
        for (int r = 0; r < 8; r++) {
            int cy = b.y + r * b.cellSize;
            int col1 = applyAlpha(rowTop[r], alpha * headAlpha);
            int col2 = applyAlpha(rowAlt[r], alpha * headAlpha);
            for (int c = 0; c < 8; c++) {
                if (CREEPER_MASK[r][c] == 0) {
                    int col = ((r + c) % 2 == 0) ? col1 : col2;
                    int cx = b.x + c * b.cellSize;
                    ConfigGuiBatch.fill(poseStack, cx, cy, cx + b.cellSize, cy + b.cellSize, col);
                }
            }
        }

        // 2. Batched merged cutouts (6 quads total instead of 20 individual cutout cells)
        int cs = b.cellSize;
        // Eyes (2x2 each):
        ConfigGuiBatch.fill(poseStack, b.x + cs, b.y + cs, b.x + 3 * cs, b.y + 3 * cs, cutoutCol);
        ConfigGuiBatch.fill(poseStack, b.x + 5 * cs, b.y + cs, b.x + 7 * cs, b.y + 3 * cs, cutoutCol);
        // Nose bridge (2x1):
        ConfigGuiBatch.fill(poseStack, b.x + 3 * cs, b.y + 3 * cs, b.x + 5 * cs, b.y + 4 * cs, cutoutCol);
        // Upper mouth (4x2):
        ConfigGuiBatch.fill(poseStack, b.x + 2 * cs, b.y + 4 * cs, b.x + 6 * cs, b.y + 6 * cs, cutoutCol);
        // Lower mouth fangs (1x1 each at row 6, cols 2 and 5):
        ConfigGuiBatch.fill(poseStack, b.x + 2 * cs, b.y + 6 * cs, b.x + 3 * cs, b.y + 7 * cs, cutoutCol);
        ConfigGuiBatch.fill(poseStack, b.x + 5 * cs, b.y + 6 * cs, b.x + 6 * cs, b.y + 7 * cs, cutoutCol);

        // 3. Border
        int borderCol = light ? borderLight : borderDark;
        drawBorder(poseStack, b.x - 1, b.y - 1, b.size + 2, b.size + 2, applyAlpha(borderCol, alpha * (light ? 0.22f : 0.30f)));
    }

    // --- Creeper Background Implementations ---

    public static void drawGreenCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF0B2914, 0xFF166534, 0xFF061A0C,
                ROW_GREEN_TOP, ROW_GREEN_ALT,
                0xFF15803D, 0xFF34D399,
                0xFF0B2010, 0xFF030D06);
    }

    public static void drawGoldenCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF2B1D04, 0xFFB45309, 0xFF1C1202,
                ROW_GOLD_TOP, ROW_GOLD_ALT,
                0xFFD97706, 0xFFFDE047,
                0xFF261804, 0xFF140B02);
    }

    public static void drawCrimsonCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF26050C, 0xFF9F1239, 0xFF140206,
                ROW_CRIMSON_TOP, ROW_CRIMSON_ALT,
                0xFFBE123C, 0xFFFB7185,
                0xFF24040A, 0xFF120205);
    }

    public static void drawWarpedCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF042022, 0xFF0D6E6E, 0xFF031415,
                ROW_WARPED_TOP, ROW_WARPED_ALT,
                0xFF0D9488, 0xFF2DD4BF,
                0xFF042224, 0xFF021314);
    }

    public static void drawBlueCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF0B192C, 0xFF1E3A8A, 0xFF060D17,
                ROW_BLUE_TOP, ROW_BLUE_ALT,
                0xFF1D4ED8, 0xFF60A5FA,
                0xFF0F172A, 0xFF050811);
    }

    public static void drawPurpleCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF190B28, 0xFF581C87, 0xFF0D0517,
                ROW_PURPLE_TOP, ROW_PURPLE_ALT,
                0xFF7E22CE, 0xFFC084FC,
                0xFF1E0A33, 0xFF0A0214);
    }

    public static void drawRedCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF260808, 0xFF7F1D1D, 0xFF140303,
                ROW_RED_TOP, ROW_RED_ALT,
                0xFFB91C1C, 0xFFF87171,
                0xFF200505, 0xFF0D0202);
    }

    public static void drawOrangeCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF251006, 0xFF7C2D12, 0xFF140803,
                ROW_ORANGE_TOP, ROW_ORANGE_ALT,
                0xFFC2410C, 0xFFFB923C,
                0xFF240D05, 0xFF0E0502);
    }

    public static void drawWhiteCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF141923, 0xFF334155, 0xFF0D1117,
                ROW_WHITE_TOP, ROW_WHITE_ALT,
                0xFF64748B, 0xFFE2E8F0,
                0xFF1E293B, 0xFF0F172A);
    }

    public static void drawBlackCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF0F1219, 0xFF1E2433, 0xFF080A0E,
                ROW_BLACK_TOP, ROW_BLACK_ALT,
                0xFF334155, 0xFF64748B,
                0xFF080B12, 0xFF020306);
    }

    public static void drawYellowCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF2A2304, 0xFF713F12, 0xFF171302,
                ROW_YELLOW_TOP, ROW_YELLOW_ALT,
                0xFFCA8A04, 0xFFFDE047,
                0xFF2B2004, 0xFF140E02);
    }

    public static void drawLimeCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF142006, 0xFF365314, 0xFF0A1003,
                ROW_LIME_TOP, ROW_LIME_ALT,
                0xFF4D7C0F, 0xFFA3E635,
                0xFF192605, 0xFF0B1202);
    }

    public static void drawCyanCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF061E28, 0xFF164E63, 0xFF030F14,
                ROW_CYAN_TOP, ROW_CYAN_ALT,
                0xFF0E7490, 0xFF22D3EE,
                0xFF08222E, 0xFF031017);
    }

    public static void drawLightBlueCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF082030, 0xFF0C4A6E, 0xFF04111A,
                ROW_LIGHT_BLUE_TOP, ROW_LIGHT_BLUE_ALT,
                0xFF0369A1, 0xFF7DD3FC,
                0xFF0A2336, 0xFF04121C);
    }

    public static void drawPinkCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF280B1B, 0xFF831843, 0xFF14050D,
                ROW_PINK_TOP, ROW_PINK_ALT,
                0xFFDB2777, 0xFFF9A8D4,
                0xFF2B0A1D, 0xFF13040D);
    }

    public static void drawMagentaCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF240726, 0xFF701A75, 0xFF120313,
                ROW_MAGENTA_TOP, ROW_MAGENTA_ALT,
                0xFFC026D3, 0xFFF0ABFC,
                0xFF29082B, 0xFF120314);
    }

    public static void drawBrownCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF1F100B, 0xFF3E1F18, 0xFF100805,
                ROW_BROWN_TOP, ROW_BROWN_ALT,
                0xFF63372C, 0xFFA67161,
                0xFF24110C, 0xFF100705);
    }

    public static void drawGrayCreeperBackground(PoseStack poseStack, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStack, x, y, width, height, alpha, theme,
                0xFF141923, 0xFF1E293B, 0xFF0A0D12,
                ROW_GRAY_TOP, ROW_GRAY_ALT,
                0xFF334155, 0xFF94A3B8,
                0xFF131A26, 0xFF080C14);
    }

    // --- Shared Background Drawing Utilities ---

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

    public static void drawBorder(PoseStack poseStack, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        ConfigGuiBatch.fill(poseStack, x, y, x + w, y + 1, color);
        ConfigGuiBatch.fill(poseStack, x, y + h - 1, x + w, y + h, color);
        if (h > 2) {
            ConfigGuiBatch.fill(poseStack, x, y + 1, x + 1, y + h - 1, color);
            ConfigGuiBatch.fill(poseStack, x + w - 1, y + 1, x + w, y + h - 1, color);
        }
    }

    /**
     * Hardware Gouraud-shaded vertical 3-stop gradient rectangle.
     * Submits exactly 2 quads directly to the batch buffer without CPU per-strip rasterization.
     */
    public static void drawVerticalGradientRect(PoseStack poseStack, int x, int y, int w, int h, int topCol, int midCol, int botCol, float alpha) {
        if (h <= 0 || w <= 0 || alpha <= 0.001f) return;

        int halfH = h / 2;
        int topAlphaCol = applyAlpha(topCol, alpha);
        int midAlphaCol = applyAlpha(midCol, alpha);
        int botAlphaCol = applyAlpha(botCol, alpha);

        if (halfH > 0) {
            ConfigGuiBatch.gradient(poseStack, x, y, x + w, y + halfH, topAlphaCol, topAlphaCol, midAlphaCol, midAlphaCol);
        }
        if (h - halfH > 0) {
            ConfigGuiBatch.gradient(poseStack, x, y + halfH, x + w, y + h, midAlphaCol, midAlphaCol, botAlphaCol, botAlphaCol);
        }
    }
}
