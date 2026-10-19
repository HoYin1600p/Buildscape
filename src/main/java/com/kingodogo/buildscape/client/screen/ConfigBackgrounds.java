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
 * including Creeper variations (Green, Golden, Crimson, Warped) and iconic entities (Enderman, Wolf, Fox, Skeleton, Slime).
 */
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
            (poseStackOrGraphics, x, y, width, height, alpha, theme) -> {}
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

    // Entity Presets
    public static final Background ENDERMAN = new Background(
            "enderman", "Enderman",
            ConfigBackgrounds::drawEndermanBackground
    );

    public static final Background WOLF = new Background(
            "wolf", "Wolf",
            ConfigBackgrounds::drawWolfBackground
    );

    public static final Background FOX = new Background(
            "fox", "Fox",
            ConfigBackgrounds::drawFoxBackground
    );

    public static final Background SKELETON = new Background(
            "skeleton", "Skeleton",
            ConfigBackgrounds::drawSkeletonBackground
    );

    public static final Background SLIME = new Background(
            "slime", "Slime",
            ConfigBackgrounds::drawSlimeBackground
    );

    private static final List<Background> BACKGROUNDS = new java.util.ArrayList<>(Arrays.asList(
            NONE,
            GREEN_CREEPER,
            GOLDEN_CREEPER,
            CRIMSON_CREEPER,
            WARPED_CREEPER,
            ENDERMAN,
            WOLF,
            FOX,
            SKELETON,
            SLIME
    ));

    private static final List<String> BUILT_IN_IDS;
    static {
        List<String> ids = new ArrayList<>(BACKGROUNDS.size());
        for (Background b : BACKGROUNDS) {
            ids.add(b.id);
        }
        BUILT_IN_IDS = Collections.unmodifiableList(ids);
    }

    static List<String> getBuiltInBackgroundIds() {
        return BUILT_IN_IDS;
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
        Background bg = new Background(id, displayName, (ps, x, y, w, h, a, t) -> drawImageBackground(ps, textureId, x, y, w, h, a, t));
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

    public static int getActiveBackgroundIndex() {
        return activeBackgroundIndex;
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

    // External images are shaded to preserve control and text contrast.
    public static void drawImageBackground(Object poseStackOrGraphics, ResourceLocation texture, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f || texture == null) return;
        if (poseStackOrGraphics instanceof PoseStack ps) {
            ConfigGuiBatch.flush();
            RenderSystem.setShaderTexture(0, texture);
            GuiComponent.blit(ps, x, y, width, height, 0.0f, 0.0f, width, height, width, height);

            boolean light = theme != null && theme.isLight();
            int washCol = light ? 0xFFFFFFFF : 0xFF08080C;
            float washAlpha = light ? 0.32f : 0.42f;
            ConfigGuiBatch.fill(ps, x, y, x + width, y + height, applyAlpha(washCol, alpha * washAlpha));
        }
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

    /**
     * Unified, high-performance Creeper backdrop renderer.
     * Combines Gouraud-shaded wash gradients with batch-optimized face and cutout geometry.
     */
    private static void drawCreeperPreset(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme,
                                         int topWash, int midWash, int botWash,
                                         int[] rowTop, int[] rowAlt,
                                         int borderLight, int borderDark,
                                         int cutoutLight, int cutoutDark) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;
        if (!(poseStackOrGraphics instanceof PoseStack ps)) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(ps, x, y, width, height, topWash, midWash, botWash, alpha * washAlpha);

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
                    ConfigGuiBatch.fill(ps, cx, cy, cx + b.cellSize, cy + b.cellSize, col);
                }
            }
        }

        // 2. Batched merged cutouts (6 quads total instead of 20 individual cutout cells)
        int cs = b.cellSize;
        // Eyes (2x2 each):
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + cs, b.x + 3 * cs, b.y + 3 * cs, cutoutCol);
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + cs, b.x + 7 * cs, b.y + 3 * cs, cutoutCol);
        // Nose bridge (2x1):
        ConfigGuiBatch.fill(ps, b.x + 3 * cs, b.y + 3 * cs, b.x + 5 * cs, b.y + 4 * cs, cutoutCol);
        // Upper mouth (4x2):
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 4 * cs, b.x + 6 * cs, b.y + 6 * cs, cutoutCol);
        // Lower mouth fangs (1x1 each at row 6, cols 2 and 5):
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 6 * cs, b.x + 3 * cs, b.y + 7 * cs, cutoutCol);
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + 6 * cs, b.x + 6 * cs, b.y + 7 * cs, cutoutCol);

        // 3. Border
        int borderCol = light ? borderLight : borderDark;
        drawBorder(ps, b.x - 1, b.y - 1, b.size + 2, b.size + 2, applyAlpha(borderCol, alpha * (light ? 0.22f : 0.30f)));
    }

    // --- Creeper Background Implementations ---

    public static void drawGreenCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStackOrGraphics, x, y, width, height, alpha, theme,
                0xFF0B2914, 0xFF166534, 0xFF061A0C,
                ROW_GREEN_TOP, ROW_GREEN_ALT,
                0xFF15803D, 0xFF34D399,
                0xFF0B2010, 0xFF030D06);
    }

    public static void drawGoldenCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStackOrGraphics, x, y, width, height, alpha, theme,
                0xFF2B1D04, 0xFFB45309, 0xFF1C1202,
                ROW_GOLD_TOP, ROW_GOLD_ALT,
                0xFFD97706, 0xFFFDE047,
                0xFF261804, 0xFF140B02);
    }

    public static void drawCrimsonCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStackOrGraphics, x, y, width, height, alpha, theme,
                0xFF26050C, 0xFF9F1239, 0xFF140206,
                ROW_CRIMSON_TOP, ROW_CRIMSON_ALT,
                0xFFBE123C, 0xFFFB7185,
                0xFF24040A, 0xFF120205);
    }

    public static void drawWarpedCreeperBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        drawCreeperPreset(poseStackOrGraphics, x, y, width, height, alpha, theme,
                0xFF042022, 0xFF0D6E6E, 0xFF031415,
                ROW_WARPED_TOP, ROW_WARPED_ALT,
                0xFF0D9488, 0xFF2DD4BF,
                0xFF042224, 0xFF021314);
    }

    // --- Entity Background Implementations ---

    public static void drawEndermanBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;
        if (!(poseStackOrGraphics instanceof PoseStack ps)) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(ps, x, y, width, height, 0xFF120320, 0xFF280845, 0xFF090110, alpha * washAlpha);

        FaceBounds b = new FaceBounds(x, y, width, height);
        int cs = b.cellSize;
        float headAlpha = light ? 0.18f : 0.25f;

        // Dark obsidian head base
        ConfigGuiBatch.fill(ps, b.x, b.y, b.x + b.size, b.y + b.size, applyAlpha(0xFF141217, alpha * headAlpha));
        // Subtle top and bottom shade
        ConfigGuiBatch.fill(ps, b.x, b.y, b.x + b.size, b.y + cs * 2, applyAlpha(0xFF0F0E12, alpha * headAlpha));
        ConfigGuiBatch.fill(ps, b.x, b.y + cs * 6, b.x + b.size, b.y + b.size, applyAlpha(0xFF0B0A0E, alpha * headAlpha));

        // Soft eye glow wash
        ConfigGuiBatch.fill(ps, b.x + cs - 1, b.y + 3 * cs - 1, b.x + 7 * cs + 1, b.y + 4 * cs + 1,
                applyAlpha(0xFF7E22CE, alpha * (light ? 0.15f : 0.25f)));

        // Eyes (Row 3, cols 1..2 and 5..6): Outer violet, inner white/magenta pupil
        float eyeAlpha = light ? 0.80f : 0.95f;
        // Left eye
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 3 * cs, b.x + 2 * cs, b.y + 4 * cs, applyAlpha(0xFFC026D3, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 3 * cs, b.x + 3 * cs, b.y + 4 * cs, applyAlpha(0xFFF5D0FE, alpha * eyeAlpha));
        // Right eye
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + 3 * cs, b.x + 6 * cs, b.y + 4 * cs, applyAlpha(0xFFF5D0FE, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y + 3 * cs, b.x + 7 * cs, b.y + 4 * cs, applyAlpha(0xFFC026D3, alpha * eyeAlpha));

        // Subtle mouth slit line (Row 5)
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 5 * cs + cs / 2, b.x + 6 * cs, b.y + 5 * cs + cs / 2 + 1,
                applyAlpha(0xFF261D30, alpha * 0.40f));

        int borderCol = light ? 0xFF9333EA : 0xFFC084FC;
        drawBorder(ps, b.x - 1, b.y - 1, b.size + 2, b.size + 2, applyAlpha(borderCol, alpha * (light ? 0.25f : 0.35f)));
    }

    public static void drawWolfBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;
        if (!(poseStackOrGraphics instanceof PoseStack ps)) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(ps, x, y, width, height, 0xFF141E1C, 0xFF2B3A37, 0xFF0E1413, alpha * washAlpha);

        FaceBounds b = new FaceBounds(x, y, width, height);
        int cs = b.cellSize;
        float headAlpha = light ? 0.18f : 0.26f;

        // Gray wolf head base
        ConfigGuiBatch.fill(ps, b.x, b.y, b.x + b.size, b.y + b.size, applyAlpha(0xFF94A3B8, alpha * headAlpha));

        // Ears (Row 0, cols 1 and 6)
        ConfigGuiBatch.fill(ps, b.x + cs, b.y, b.x + 2 * cs, b.y + cs, applyAlpha(0xFF475569, alpha * headAlpha));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y, b.x + 7 * cs, b.y + cs, applyAlpha(0xFF475569, alpha * headAlpha));
        ConfigGuiBatch.fill(ps, b.x + cs + 1, b.y + 1, b.x + 2 * cs - 1, b.y + cs, applyAlpha(0xFFCBD5E1, alpha * headAlpha));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs + 1, b.y + 1, b.x + 7 * cs - 1, b.y + cs, applyAlpha(0xFFCBD5E1, alpha * headAlpha));

        // Eyes (Row 2, cols 1-2 and 5-6)
        float eyeAlpha = light ? 0.55f : 0.70f;
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 2 * cs, b.x + 3 * cs, b.y + 3 * cs, applyAlpha(0xFF1E293B, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 2 * cs, b.x + 3 * cs, b.y + 3 * cs, applyAlpha(0xFFF8FAFC, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + 2 * cs, b.x + 7 * cs, b.y + 3 * cs, applyAlpha(0xFF1E293B, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + 2 * cs, b.x + 6 * cs, b.y + 3 * cs, applyAlpha(0xFFF8FAFC, alpha * eyeAlpha));

        // Snout (Rows 4-6, cols 2-5)
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 4 * cs, b.x + 6 * cs, b.y + 7 * cs, applyAlpha(0xFFE2E8F0, alpha * headAlpha));
        // Black nose (Row 4, cols 3-4)
        ConfigGuiBatch.fill(ps, b.x + 3 * cs, b.y + 4 * cs, b.x + 5 * cs, b.y + 5 * cs, applyAlpha(0xFF0F172A, alpha * eyeAlpha));
        // Mouth line
        ConfigGuiBatch.fill(ps, b.x + 3 * cs + cs / 2, b.y + 5 * cs, b.x + 4 * cs + cs / 2, b.y + 7 * cs, applyAlpha(0xFF64748B, alpha * headAlpha));

        // Classic red collar (Row 7, cols 1-6)
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 7 * cs, b.x + 7 * cs, b.y + b.size, applyAlpha(0xFFDC2626, alpha * (light ? 0.35f : 0.45f)));

        int borderCol = light ? 0xFF64748B : 0xFF94A3B8;
        drawBorder(ps, b.x - 1, b.y - 1, b.size + 2, b.size + 2, applyAlpha(borderCol, alpha * (light ? 0.22f : 0.30f)));
    }

    public static void drawFoxBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;
        if (!(poseStackOrGraphics instanceof PoseStack ps)) return;

        boolean light = theme != null && theme.isLight();
        // Warm autumnal spruce forest evening glow
        float washAlpha = light ? 0.08f : 0.13f;
        drawVerticalGradientRect(ps, x, y, width, height, 0xFF1F150C, 0xFF522A0C, 0xFF140D06, alpha * washAlpha);

        FaceBounds b = new FaceBounds(x, y, width, height);
        int cs = b.cellSize;
        float headAlpha = light ? 0.22f : 0.30f;

        // Rich warm russet-orange fox base fur (authentic Minecraft fox palette)
        ConfigGuiBatch.fill(ps, b.x, b.y, b.x + b.size, b.y + b.size, applyAlpha(0xFFD45E1B, alpha * headAlpha));

        // Subtle brow shade across rows 1-2 for depth
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + cs, b.x + 7 * cs, b.y + 2 * cs, applyAlpha(0xFFBD4F14, alpha * headAlpha * 0.7f));

        // Dark charcoal ear tips (Row 0, cols 1 and 6)
        ConfigGuiBatch.fill(ps, b.x + cs, b.y, b.x + 2 * cs, b.y + cs, applyAlpha(0xFF2B231D, alpha * headAlpha * 1.3f));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y, b.x + 7 * cs, b.y + cs, applyAlpha(0xFF2B231D, alpha * headAlpha * 1.3f));

        // Soft cream inner ear (Row 1, cols 1 and 6)
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + cs, b.x + 2 * cs, b.y + 2 * cs, applyAlpha(0xFFF5ECE1, alpha * headAlpha));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y + cs, b.x + 7 * cs, b.y + 2 * cs, applyAlpha(0xFFF5ECE1, alpha * headAlpha));

        // Warm dark espresso fox eyes (Row 3, cols 1 and 6)
        float eyeAlpha = light ? 0.70f : 0.85f;
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 3 * cs, b.x + 2 * cs, b.y + 4 * cs, applyAlpha(0xFF1F150E, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y + 3 * cs, b.x + 7 * cs, b.y + 4 * cs, applyAlpha(0xFF1F150E, alpha * eyeAlpha));
        // Subtle warm eye sparkle
        ConfigGuiBatch.fill(ps, b.x + cs + cs / 2, b.y + 3 * cs, b.x + 2 * cs, b.y + 3 * cs + cs / 2, applyAlpha(0xFF8C4A20, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y + 3 * cs, b.x + 6 * cs + cs / 2, b.y + 3 * cs + cs / 2, applyAlpha(0xFF8C4A20, alpha * eyeAlpha));

        // Fluffy ivory/white cheeks (Rows 4-6, cols 0..1 and cols 6..7)
        ConfigGuiBatch.fill(ps, b.x, b.y + 4 * cs, b.x + 2 * cs, b.y + 7 * cs, applyAlpha(0xFFFAF4EB, alpha * headAlpha * 1.15f));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y + 4 * cs, b.x + b.size, b.y + 7 * cs, applyAlpha(0xFFFAF4EB, alpha * headAlpha * 1.15f));

        // Snout center warmth (Rows 4-6, cols 2-5)
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 4 * cs, b.x + 6 * cs, b.y + 7 * cs, applyAlpha(0xFFDC6824, alpha * headAlpha));

        // Dark charcoal button nose (Row 5, cols 3-4)
        ConfigGuiBatch.fill(ps, b.x + 3 * cs, b.y + 5 * cs, b.x + 5 * cs, b.y + 6 * cs, applyAlpha(0xFF211812, alpha * eyeAlpha));

        // Warm russet-amber border
        int borderCol = light ? 0xFFB45309 : 0xFFF59E0B;
        drawBorder(ps, b.x - 1, b.y - 1, b.size + 2, b.size + 2, applyAlpha(borderCol, alpha * (light ? 0.25f : 0.35f)));
    }

    public static void drawSkeletonBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;
        if (!(poseStackOrGraphics instanceof PoseStack ps)) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.07f : 0.12f;
        drawVerticalGradientRect(ps, x, y, width, height, 0xFF14171A, 0xFF2A343D, 0xFF0C0E10, alpha * washAlpha);

        FaceBounds b = new FaceBounds(x, y, width, height);
        int cs = b.cellSize;
        float headAlpha = light ? 0.20f : 0.28f;

        // Bone gray head base
        ConfigGuiBatch.fill(ps, b.x, b.y, b.x + b.size, b.y + b.size, applyAlpha(0xFFCBD5E1, alpha * headAlpha));

        // Hollow eye sockets (Rows 2-3, cols 1-2 and 5-6)
        float socketAlpha = light ? 0.50f : 0.65f;
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 2 * cs, b.x + 3 * cs, b.y + 4 * cs, applyAlpha(0xFF0F172A, alpha * socketAlpha));
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + 2 * cs, b.x + 7 * cs, b.y + 4 * cs, applyAlpha(0xFF0F172A, alpha * socketAlpha));

        // Nasal cavity (Row 4, cols 3-4)
        ConfigGuiBatch.fill(ps, b.x + 3 * cs, b.y + 4 * cs, b.x + 5 * cs, b.y + 5 * cs, applyAlpha(0xFF0F172A, alpha * socketAlpha));

        // Teeth row (Rows 6-7, cols 1-6) with vertical dividing slits
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 6 * cs, b.x + 7 * cs, b.y + 7 * cs, applyAlpha(0xFF94A3B8, alpha * headAlpha));
        ConfigGuiBatch.fill(ps, b.x + 2 * cs, b.y + 6 * cs, b.x + 2 * cs + 1, b.y + 7 * cs, applyAlpha(0xFF0F172A, alpha * socketAlpha));
        ConfigGuiBatch.fill(ps, b.x + 4 * cs, b.y + 6 * cs, b.x + 4 * cs + 1, b.y + 7 * cs, applyAlpha(0xFF0F172A, alpha * socketAlpha));
        ConfigGuiBatch.fill(ps, b.x + 6 * cs, b.y + 6 * cs, b.x + 6 * cs + 1, b.y + 7 * cs, applyAlpha(0xFF0F172A, alpha * socketAlpha));

        int borderCol = light ? 0xFF64748B : 0xFFCBD5E1;
        drawBorder(ps, b.x - 1, b.y - 1, b.size + 2, b.size + 2, applyAlpha(borderCol, alpha * (light ? 0.22f : 0.30f)));
    }

    public static void drawSlimeBackground(Object poseStackOrGraphics, int x, int y, int width, int height, float alpha, ConfigThemes.Theme theme) {
        if (width <= 0 || height <= 0 || alpha <= 0.001f) return;
        if (!(poseStackOrGraphics instanceof PoseStack ps)) return;

        boolean light = theme != null && theme.isLight();
        float washAlpha = light ? 0.08f : 0.14f;
        drawVerticalGradientRect(ps, x, y, width, height, 0xFF0B210F, 0xFF1A5927, 0xFF061409, alpha * washAlpha);

        FaceBounds b = new FaceBounds(x, y, width, height);
        int cs = b.cellSize;
        float outerAlpha = light ? 0.16f : 0.24f;
        float innerAlpha = light ? 0.24f : 0.34f;

        // Outer bouncy translucent lime jelly cube
        ConfigGuiBatch.fill(ps, b.x, b.y, b.x + b.size, b.y + b.size, applyAlpha(0xFF4ADE80, alpha * outerAlpha));
        // Inner denser slime core
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + cs, b.x + 7 * cs, b.y + 7 * cs, applyAlpha(0xFF16A34A, alpha * innerAlpha));

        // Cute dark jelly eyes (Row 3, cols 1-2 and 5-6)
        float eyeAlpha = light ? 0.60f : 0.75f;
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 3 * cs, b.x + 3 * cs, b.y + 4 * cs, applyAlpha(0xFF052E16, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + 3 * cs, b.x + 7 * cs, b.y + 4 * cs, applyAlpha(0xFF052E16, alpha * eyeAlpha));
        // Bright eye highlights
        ConfigGuiBatch.fill(ps, b.x + cs, b.y + 3 * cs, b.x + 2 * cs, b.y + 3 * cs + cs / 2, applyAlpha(0xFF86EFAC, alpha * eyeAlpha));
        ConfigGuiBatch.fill(ps, b.x + 5 * cs, b.y + 3 * cs, b.x + 6 * cs, b.y + 3 * cs + cs / 2, applyAlpha(0xFF86EFAC, alpha * eyeAlpha));

        // Cute 1-cell jelly mouth (Row 5, col 4)
        ConfigGuiBatch.fill(ps, b.x + 4 * cs, b.y + 5 * cs, b.x + 5 * cs, b.y + 6 * cs, applyAlpha(0xFF052E16, alpha * eyeAlpha));

        int borderCol = light ? 0xFF16A34A : 0xFF4ADE80;
        drawBorder(ps, b.x - 1, b.y - 1, b.size + 2, b.size + 2, applyAlpha(borderCol, alpha * (light ? 0.25f : 0.35f)));
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

    public static void drawBorder(Object poseStackOrGraphics, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        if (poseStackOrGraphics instanceof PoseStack ps) {
            ConfigGuiBatch.fill(ps, x, y, x + w, y + 1, color);
            ConfigGuiBatch.fill(ps, x, y + h - 1, x + w, y + h, color);
            if (h > 2) {
                ConfigGuiBatch.fill(ps, x, y + 1, x + 1, y + h - 1, color);
                ConfigGuiBatch.fill(ps, x + w - 1, y + 1, x + w, y + h - 1, color);
            }
        }
    }

    /**
     * Hardware Gouraud-shaded vertical 3-stop gradient rectangle.
     * Submits exactly 2 quads directly to the batch buffer without CPU per-strip rasterization.
     */
    public static void drawVerticalGradientRect(Object poseStackOrGraphics, int x, int y, int w, int h, int topCol, int midCol, int botCol, float alpha) {
        if (h <= 0 || w <= 0 || alpha <= 0.001f) return;
        if (!(poseStackOrGraphics instanceof PoseStack ps)) return;

        int halfH = h / 2;
        int topAlphaCol = applyAlpha(topCol, alpha);
        int midAlphaCol = applyAlpha(midCol, alpha);
        int botAlphaCol = applyAlpha(botCol, alpha);

        if (halfH > 0) {
            ConfigGuiBatch.gradient(ps, x, y, x + w, y + halfH, topAlphaCol, topAlphaCol, midAlphaCol, midAlphaCol);
        }
        if (h - halfH > 0) {
            ConfigGuiBatch.gradient(ps, x, y + halfH, x + w, y + h, midAlphaCol, midAlphaCol, botAlphaCol, botAlphaCol);
        }
    }
}
