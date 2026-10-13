package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.RequestPillarIdsPacket;
import com.kingodogo.buildscape.network.UpdateAllPillarIdsPacket;
import com.kingodogo.buildscape.network.UpdateGameRulePacket;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Unified Single-Panel Buildscape Configuration Screen.
 * Design Invariants:
 * - 1 Big Unified Panel (no split/disconnected panels).
 * - Exact metallic border gradients, black outline, and recessed inner groove.
 * - Solid opaque charcoal & slate palette (no 75% transparency).
 * - Collapsible left navigation bar (icon-only mode or expanded icon+label mode).
 * - Scrollable configuration viewport.
 * - Direct real-time binding to real Buildscape configs:
 *     * World Mechanics (CosmeticsConfig, ModGameRules, UpdateGameRulePacket)
 *     * Pillar Particles (PillarParticleConfig, saveProperties)
 *     * Pillar IDs & Grid Snapping (PillarIdManager, RequestPillarIdsPacket)
 *     * Trigger Items (PillarParticleConfig item list & registry)
 *     * Engine & Platform System
 * - Cubic easing open/close animations.
 * - Pure white and crisp silver text with high-contrast shadows.
 */
public class BuildScapeConfigScreen implements IScreenDelegate {

    private final Screen parent;
    private final Component title;
    private Screen wrapperScreen;

    private Consumer<AbstractWidget> addWidgetConsumer;
    private Consumer<GuiEventListener> removeWidgetConsumer;

    public int width;
    public int height;

    // --- Animation Timings & States ---
    private static final int OPEN_DURATION_MS = 220;
    private static final int CLOSE_DURATION_MS = 160;

    private long openStartTime;
    private long closeStartTime = -1;
    private boolean isOpening = true;
    private boolean isClosing = false;
    private float openProgress = 0.0f;
    private float closeProgress = 1.0f;

    // --- Palette Constants (Solid Opaque, No 75% transparency) ---
    public static final int COLOR_OVERLAY = 0xD808080A;
    public static final int COLOR_PANEL_BG = 0xFF161618;
    public static final int COLOR_INNER_DARK = 0xFF101012;
    public static final int COLOR_HEADER_BG = 0xFF1E1E22;

    // Metallic Beveled Border Gradients
    public static final int COLOR_FRAME_BLACK = 0xFF000000;
    public static final int COLOR_BORDER_GRADIENT_START = 0xFF707078;
    public static final int COLOR_BORDER_GRADIENT_END = 0xFF3E3E44;
    public static final int COLOR_BORDER_SHADOW = 0xFF1A1A1E;

    // Text & Accents
    public static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    public static final int COLOR_TEXT_SILVER = 0xFFCCCCCC;
    public static final int COLOR_TEXT_MUTED = 0xFF84848C;

    // Interactive Widget Colors
    public static final int COLOR_ROW_BG = 0xFF18181D;
    public static final int COLOR_ROW_BG_HOVER = 0xFF202026;
    public static final int COLOR_ROW_BORDER = 0xFF26262E;
    public static final int COLOR_ROW_BORDER_HOVER = 0xFF4A4A56;

    public static final int COLOR_TOGGLE_ON_BG = 0xFF142E18;
    public static final int COLOR_TOGGLE_ON_BORDER = 0xFF4ADE80;
    public static final int COLOR_TOGGLE_ON_TEXT = 0xFF4ADE80;

    public static final int COLOR_TOGGLE_OFF_BG = 0xFF1C1C20;
    public static final int COLOR_TOGGLE_OFF_BORDER = 0xFF383842;
    public static final int COLOR_TOGGLE_OFF_TEXT = 0xFF888890;

    public static final int COLOR_BTN_BG = 0xFF202028;
    public static final int COLOR_BTN_BORDER = 0xFF444452;
    public static final int COLOR_BTN_BORDER_HOVER = 0xFF707082;

    // --- Sidenav Specification ---
    public static class NavItem {
        public final String id;
        public final String icon;
        public final String title;
        public final String subtitle;

        public NavItem(String id, String icon, String title, String subtitle) {
            this.id = id;
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    private final List<NavItem> navItems = new ArrayList<>();
    private int selectedTabIndex = 0;
    private boolean navExpanded = true;

    // --- Scroll & Viewport State ---
    private double scrollOffset = 0.0;
    private double maxScroll = 0.0;

    // --- Particle Settings Options Arrays ---
    private static final String[] PARTICLE_PATTERNS = {
            "ring", "beam", "spiral", "fountain", "pulse", "burst", "snowflake"
    };
    private static final int[] PARTICLE_DENSITIES = {1, 2, 4, 8};
    private static final double[] PARTICLE_SPEEDS = {0.01D, 0.02D, 0.05D, 0.10D, 0.20D};
    private static final double[] PARTICLE_SPREADS = {0.05D, 0.10D, 0.20D, 0.35D, 0.50D};
    private static final int[] PARTICLE_LIFETIMES = {10, 20, 30, 45, 60};
    private static final double[] PATTERN_INTENSITIES = {0.5D, 1.0D, 1.5D, 2.0D};

    // --- Local Client Preferences ---
    private boolean autoSnappingEnabled = true;
    private boolean strictBoundsCheck = true;
    private int animSpeedIndex = 0;
    private static final String[] ANIM_SPEEDS = {"Smooth", "Snappy", "Instant"};

    // Legacy tab & widget support
    private AbstractConfigTab activeTab;
    private final List<GuiEventListener> widgets = new ArrayList<>();
    private final List<GuiEventListener> tabWidgets = new ArrayList<>();

    public BuildScapeConfigScreen(Screen parent) {
        this.parent = parent;
        this.title = ComponentHelper.translatable("buildscape.config.title");

        this.openStartTime = System.currentTimeMillis();
        initNavItems();
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem("world", "⚙", "World", "World physics, instant felling, and game mechanics"));
        navItems.add(new NavItem("particles", "✦", "Particles", "Ambient pillar particle trajectories and kinetic FX"));
        navItems.add(new NavItem("pillars", "▲", "Pillars", "Voxel grid placement auto-snapping and ID sync"));
        navItems.add(new NavItem("items", "☵", "Trigger Items", "Item registry for pillar activation and particles"));
        navItems.add(new NavItem("system", "ℹ", "System", "Engine version, permissions, and client preferences"));
    }

    @Override
    public void setWrapperScreen(Object screen) {
        if (screen instanceof Screen s) {
            this.wrapperScreen = s;
        }
    }

    public Screen getWrapperScreen() {
        return wrapperScreen != null ? wrapperScreen : parent;
    }

    @Override
    public void init(Minecraft mc, int width, int height, Consumer<AbstractWidget> addWidget, Consumer<GuiEventListener> removeWidget) {
        this.width = width;
        this.height = height;
        this.addWidgetConsumer = addWidget;
        this.removeWidgetConsumer = removeWidget;

        this.widgets.clear();
        this.tabWidgets.clear();

        if (activeTab != null) {
            activeTab.init();
        }
    }

    // --- Animation Handling ---
    private float updateAnimations() {
        long now = System.currentTimeMillis();

        if (isOpening) {
            float elapsed = (now - openStartTime) / (float) OPEN_DURATION_MS;
            if (elapsed >= 1.0f) {
                openProgress = 1.0f;
                isOpening = false;
            } else {
                openProgress = easeOutCubic(elapsed);
            }
        }

        if (isClosing) {
            float elapsed = (now - closeStartTime) / (float) CLOSE_DURATION_MS;
            if (elapsed >= 1.0f) {
                closeProgress = 0.0f;
                isClosing = false;
                Services.PLATFORM.openScreen(parent);
                return 0.0f;
            } else {
                closeProgress = 1.0f - easeInCubic(elapsed);
            }
        }

        return isClosing ? closeProgress : openProgress;
    }

    private static float easeOutCubic(float t) {
        float inv = 1.0f - Math.min(1.0f, Math.max(0.0f, t));
        return 1.0f - inv * inv * inv;
    }

    private static float easeInCubic(float t) {
        float val = Math.min(1.0f, Math.max(0.0f, t));
        return val * val * val;
    }

    public void startClosing() {
        if (isClosing) return;
        isClosing = true;
        isOpening = false;
        closeStartTime = System.currentTimeMillis();
    }

    private static int applyAlpha(int argb, float factor) {
        int a = (argb >>> 24) & 0xFF;
        int rgb = argb & 0x00FFFFFF;
        int newA = Math.max(0, Math.min(255, Math.round(a * factor)));
        return (newA << 24) | rgb;
    }

    public boolean hasOpAccess() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        return Services.PLATFORM.hasPlayerPermissions(mc.player, 2);
    }

    private UUID getPlayerUuid() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null ? mc.player.getUUID() : null;
    }

    private Level getLevel() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level;
    }

    // --- Main Rendering ---
    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        float alpha = updateAnimations();
        if (alpha <= 0.001f) return;

        Font font = Minecraft.getInstance().font;

        // 1. Full-screen dimmed backdrop
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(COLOR_OVERLAY, alpha));

        // 2. Geometry: 1 Big Unified Panel centered on screen
        int panelW = Math.min(width - 32, 530);
        int panelH = Math.min(height - 24, 276);
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        // Smooth slide-in vertical offset
        int offsetY = Math.round((1.0f - alpha) * 8.0f);
        panelY += offsetY;

        // 3. Render 1 Big Frame with metallic bevels
        drawVaultHuntersFrame(poseStackOrGraphics, panelX, panelY, panelW, panelH, COLOR_PANEL_BG, alpha);

        // 4. Header Bar
        int headerH = 24;
        Services.PLATFORM.fill(poseStackOrGraphics, panelX + 2, panelY + 2, panelX + panelW - 2, panelY + headerH, applyAlpha(COLOR_HEADER_BG, alpha));
        drawGroove(poseStackOrGraphics, panelX + 2, panelY + headerH, panelW - 4, true, alpha);

        // Header Title
        drawText(poseStackOrGraphics, font, "⚙ Buildscape Configuration", panelX + 8, panelY + 7, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Target version indicator
        String versionTag = "v4.0.3 · NeoForge 26.2";
        int tagW = font.width(versionTag);
        drawText(poseStackOrGraphics, font, versionTag, panelX + panelW - tagW - 28, panelY + 7, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // Close Button [ ✕ ]
        int closeBtnSize = 14;
        int closeBtnX = panelX + panelW - closeBtnSize - 7;
        int closeBtnY = panelY + 5;
        boolean hoverClose = mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize;
        Services.PLATFORM.fill(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnX + closeBtnSize, closeBtnY + closeBtnSize, applyAlpha(hoverClose ? 0xFF363640 : 0xFF202026, alpha));
        drawBorder(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnSize, closeBtnSize, applyAlpha(hoverClose ? 0xFF666675 : 0xFF303038, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeBtnX + closeBtnSize / 2, closeBtnY + 3, applyAlpha(hoverClose ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // 5. Layout Sidenav & Content
        int navW = navExpanded ? 104 : 30;
        int navX = panelX + 4;
        int navY = panelY + headerH + 4;
        int navH = panelH - headerH - 8;

        int contentX = navX + navW + 4;
        int contentY = navY;
        int contentW = (panelX + panelW - 4) - contentX;
        int contentH = navH;

        // Render Left Navigation Strip
        renderSidenav(poseStackOrGraphics, font, navX, navY, navW, navH, mouseX, mouseY, alpha);

        // Render Right Main Configuration Content Area
        renderContentArea(poseStackOrGraphics, font, contentX, contentY, contentW, contentH, mouseX, mouseY, alpha);

        // Render legacy activeTab if any
        if (activeTab != null) {
            activeTab.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
            activeTab.renderTooltips(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
    }

    // --- Sidenav Rendering ---
    private void renderSidenav(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        drawInsetBox(poseStackOrGraphics, x, y, w, h, 0xFF111113, alpha);

        int tabH = 26;
        int tabGap = 3;
        int startY = y + 4;
        String hoveredTooltip = null;

        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int tabY = startY + i * (tabH + tabGap);
            int tabW = w - 6;
            int tabX = x + 3;

            boolean isSelected = (i == selectedTabIndex);
            boolean hover = mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH;

            int bg = isSelected ? 0xFF24242C : (hover ? 0xFF1A1A20 : 0xFF131316);
            Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY, tabX + tabW, tabY + tabH, applyAlpha(bg, alpha));

            int border = isSelected ? 0xFF545462 : (hover ? 0xFF34343E : 0xFF1C1C22);
            drawBorder(poseStackOrGraphics, tabX, tabY, tabW, tabH, applyAlpha(border, alpha));

            if (isSelected) {
                // Left active accent bar
                Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY, tabX + 2, tabY + tabH, applyAlpha(COLOR_TEXT_WHITE, alpha));
            }

            if (navExpanded) {
                // Icon + Text
                int iconCol = isSelected ? COLOR_TEXT_WHITE : (hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                drawText(poseStackOrGraphics, font, item.icon, tabX + 7, tabY + 9, applyAlpha(iconCol, alpha));

                int textCol = isSelected ? COLOR_TEXT_WHITE : (hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                drawText(poseStackOrGraphics, font, item.title, tabX + 22, tabY + 9, applyAlpha(textCol, alpha));
            } else {
                // Minimized Icon Only
                int iconCol = isSelected ? COLOR_TEXT_WHITE : (hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, item.icon, tabX + tabW / 2, tabY + 9, applyAlpha(iconCol, alpha));

                if (hover) {
                    hoveredTooltip = item.title;
                }
            }
        }

        // Bottom Toggle: Collapse / Expand Button
        int toggleH = 16;
        int toggleW = w - 6;
        int toggleX = x + 3;
        int toggleY = y + h - toggleH - 3;
        boolean hoverToggle = mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;

        Services.PLATFORM.fill(poseStackOrGraphics, toggleX, toggleY, toggleX + toggleW, toggleY + toggleH, applyAlpha(hoverToggle ? 0xFF282832 : 0xFF18181E, alpha));
        drawBorder(poseStackOrGraphics, toggleX, toggleY, toggleW, toggleH, applyAlpha(hoverToggle ? 0xFF50505E : 0xFF26262E, alpha));

        String toggleLabel = navExpanded ? "◀ Collapse" : "▶";
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, toggleLabel, toggleX + toggleW / 2, toggleY + 4, applyAlpha(hoverToggle ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, alpha));

        if (hoveredTooltip != null) {
            Services.PLATFORM.renderComponentTooltip(
                    poseStackOrGraphics, font,
                    Collections.singletonList(ComponentHelper.literal(hoveredTooltip)),
                    mouseX + 12, mouseY
            );
        }
    }

    // --- Content Area Rendering ---
    private void renderContentArea(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        drawInsetBox(poseStackOrGraphics, x, y, w, h, COLOR_INNER_DARK, alpha);

        if (selectedTabIndex < 0 || selectedTabIndex >= navItems.size()) return;
        NavItem current = navItems.get(selectedTabIndex);

        // Header bar inside content area
        int catHeaderH = 22;
        drawText(poseStackOrGraphics, font, current.icon + "  " + current.title.toUpperCase(), x + 8, y + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, current.subtitle, x + 8 + font.width(current.icon + "  " + current.title.toUpperCase()) + 10, y + 7, applyAlpha(COLOR_TEXT_MUTED, alpha));
        drawGroove(poseStackOrGraphics, x + 2, y + catHeaderH, w - 4, true, alpha);

        // Scrollable Config Rows Viewport
        int viewY = y + catHeaderH + 3;
        int viewH = h - catHeaderH - 6;
        int rowH = 26;
        int rowGap = 3;

        int rowW = w - 12;
        int rowX = x + 6;

        int totalRows = getRowCountForTab(current.id);
        int totalContentHeight = totalRows * (rowH + rowGap) + 4;
        maxScroll = Math.max(0, totalContentHeight - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        int startY = viewY + 2 - (int) scrollOffset;

        // Render Rows
        switch (current.id) {
            case "world" -> renderWorldSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "particles" -> renderParticleSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "pillars" -> renderPillarIdRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "items" -> renderTriggerItemRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "system" -> renderSystemRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
        }

        // Scrollbar if needed
        if (maxScroll > 0) {
            int scrollbarW = 4;
            int scrollbarX = x + w - scrollbarW - 3;
            int trackH = viewH;
            int thumbH = Math.max(16, (int) ((viewH / (float) totalContentHeight) * trackH));
            int thumbY = viewY + (int) ((scrollOffset / maxScroll) * (trackH - thumbH));

            Services.PLATFORM.fill(poseStackOrGraphics, scrollbarX, viewY, scrollbarX + scrollbarW, viewY + trackH, applyAlpha(0xFF141418, alpha));
            Services.PLATFORM.fill(poseStackOrGraphics, scrollbarX, thumbY, scrollbarX + scrollbarW, thumbY + thumbH, applyAlpha(0xFF484854, alpha));
        }
    }

    private int getRowCountForTab(String tabId) {
        return switch (tabId) {
            case "world" -> 8;
            case "particles" -> 8;
            case "pillars" -> 5;
            case "items" -> 4;
            case "system" -> 5;
            default -> 0;
        };
    }

    // --- Tab 0: World Mechanics Rows ---
    private void renderWorldSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        UUID playerUuid = getPlayerUuid();
        Level level = getLevel();

        boolean treeBreaker = CosmeticsConfig.get().getCreativeTreeBreaker(playerUuid);
        boolean shulkerPeek = CosmeticsConfig.get().getShulkerPreview(playerUuid);
        boolean fastLeaves = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.FAST_LEAF_DECAY, ModGameRules.clientFastLeafDecay) : ModGameRules.clientFastLeafDecay;
        boolean noEnderman = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing) : ModGameRules.clientDisableEndermanGriefing;
        boolean noCreeper = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing) : ModGameRules.clientDisableCreeperGriefing;
        boolean noGhast = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_GHAST_GRIEFING, ModGameRules.clientDisableGhastGriefing) : ModGameRules.clientDisableGhastGriefing;
        boolean cakeStack = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.IS_CAKE_STACK, ModGameRules.clientCakeStacking) : ModGameRules.clientCakeStacking;
        boolean waterStack = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.IS_WATER_BOTTLE_STACK, ModGameRules.clientWaterBottleStacking) : ModGameRules.clientWaterBottleStacking;

        int curY = startY;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Creative Tree Breaker", "Instantly fell whole trees in creative mode", treeBreaker, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Shulker Box Preview", "Inspect container contents tooltip on hover", shulkerPeek, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Fast Leaf Decay", "Rapid leaf despawning upon trunk destruction (GameRule)", fastLeaves, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Enderman Grief Defense", "Prevent Endermen from picking up world blocks (GameRule)", noEnderman, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Creeper Grief Defense", "Prevent Creeper explosion damage to terrain (GameRule)", noCreeper, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Ghast Grief Defense", "Prevent Ghast fireball explosions from destroying blocks (GameRule)", noGhast, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Cake Stacking", "Allow cake items to stack up to 64 in inventories (GameRule)", cakeStack, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Water Bottle Stacking", "Allow water bottles to stack up to 16 in inventories (GameRule)", waterStack, mouseX, mouseY, alpha);
    }

    // --- Tab 1: Pillar Particles Rows ---
    private void renderParticleSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();

        int curY = startY;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Use Particle Pattern", "Toggle ambient geometric pattern emission", cfg.use_pattern, mouseX, mouseY, alpha);
        curY += h + gap;

        String patternDisplay = cfg.pattern.substring(0, 1).toUpperCase() + cfg.pattern.substring(1);
        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Trajectory Pattern", "Kinetic flight path solver formula", patternDisplay, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Density", "Multi-column particle count multiplier", cfg.particle_density + "x", mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Velocity", "Initial ejection speed vector magnitude", String.format("%.2f", cfg.particle_speed), mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Spread", "Apex dispersion radius factor", String.format("%.2f", cfg.particle_spread), mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Lifetime", "Duration in ticks until particle despawn", cfg.particle_lifetime + " ticks", mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Intensity", "Kinetic impulse pattern power multiplier", String.format("%.1fx", cfg.pattern_intensity), mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowPalette(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Palette Colors", cfg.particle_color, cfg.max_particle_color, mouseX, mouseY, alpha);
    }

    // --- Tab 2: Pillar IDs Rows ---
    private void renderPillarIdRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        int pillarCount = PillarIdManager.getClient().getPillarCount();

        int curY = startY;

        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Synchronized Pillars", "Authoritative pillar columns mapped in client world", pillarCount + " Active", alpha);
        curY += h + gap;

        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Request Pillar Sync", "Query server for full pillar ID coordinate registry", "⟳ Sync", mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Auto-Snapping Bounds", "Snap placement cursor to nearest voxel pillar node", autoSnappingEnabled, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Strict Collision Check", "Reject placement collisions with non-air voxels", strictBoundsCheck, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Clear Pillar Registry", "Purge all synchronized pillar identifiers from client cache", "✕ Clear", mouseX, mouseY, alpha);
    }

    // --- Tab 3: Trigger Items Rows ---
    private void renderTriggerItemRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        int itemCount = cfg.items != null ? cfg.items.size() : 0;

        int curY = startY;

        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Registered Trigger Items", "Item identifiers that activate pillar particles", itemCount + " Items", alpha);
        curY += h + gap;

        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Reset Default Items", "Restore vanilla diamond, netherite, emerald & valuable list", "↺ Defaults", mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Save Items To Disk", "Commit current item triggers to buildscape/pillar-items.json", "💾 Save", mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Item Matching Cache", "High performance ConcurrentHashMap item validator", "Active", alpha);
    }

    // --- Tab 4: System Rows ---
    private void renderSystemRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        int curY = startY;

        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Screen Animation", "UI opening & closing cubic transition velocity", ANIM_SPEEDS[animSpeedIndex], mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Buildscape Engine", "Unified cross-cluster mod compilation jar", "4.0.3", alpha);
        curY += h + gap;

        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "VersionCluster Target", "Current active Minecraft engine platform", "v26x (NeoForge 26.2)", alpha);
        curY += h + gap;

        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Common Neutral Core", "Direct loader neutrality and registry safety", "Enforced", alpha);
        curY += h + gap;

        String opStatus = hasOpAccess() ? "OP Level 2+ (Full Access)" : "Standard (Client Settings)";
        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Operator Privileges", "Server GameRule authorization state", opStatus, alpha);
    }

    // --- Reusable Row Renderers ---
    private void renderRowCardBase(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;

        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bg = hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

        int border = hover ? COLOR_ROW_BORDER_HOVER : COLOR_ROW_BORDER;
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(border, alpha));

        // Label in pure white
        drawText(poseStackOrGraphics, font, label, x + 8, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Subtitle / description in muted silver
        if (desc != null) {
            drawText(poseStackOrGraphics, font, desc, x + 8, y + 15, applyAlpha(COLOR_TEXT_MUTED, alpha));
        }
    }

    private void renderConfigRowToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, boolean active, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 38;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int bg = active ? COLOR_TOGGLE_ON_BG : COLOR_TOGGLE_OFF_BG;
        if (hoverBtn) bg = active ? 0xFF1C3C22 : 0xFF26262E;

        Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, applyAlpha(bg, alpha));
        int border = active ? COLOR_TOGGLE_ON_BORDER : COLOR_TOGGLE_OFF_BORDER;
        drawBorder(poseStackOrGraphics, btnX, btnY, btnW, btnH, applyAlpha(border, alpha));

        String text = active ? "ON" : "OFF";
        int textCol = active ? COLOR_TOGGLE_ON_TEXT : COLOR_TOGGLE_OFF_TEXT;
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, btnX + btnW / 2, btnY + 3, applyAlpha(textCol, alpha));
    }

    private void renderConfigRowCycle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String value, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 82;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int bg = hoverBtn ? 0xFF2A2A34 : COLOR_BTN_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, applyAlpha(bg, alpha));

        int border = hoverBtn ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER;
        drawBorder(poseStackOrGraphics, btnX, btnY, btnW, btnH, applyAlpha(border, alpha));

        String text = "‹ " + value + " ›";
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, btnX + btnW / 2, btnY + 3, applyAlpha(hoverBtn ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String value, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int counterW = 76;
        int counterH = 14;
        int counterX = x + w - counterW - 8;
        int counterY = y + (h - counterH) / 2;
        int stepBtnW = 14;

        // Minus button
        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        Services.PLATFORM.fill(poseStackOrGraphics, counterX, counterY, counterX + stepBtnW, counterY + counterH, applyAlpha(hoverMinus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, counterX, counterY, stepBtnW, counterH, applyAlpha(hoverMinus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "−", counterX + stepBtnW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Value text
        int valX = counterX + stepBtnW;
        int valW = counterW - stepBtnW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(0xFF141418, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, value, valX + valW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Plus button
        int plusX = counterX + counterW - stepBtnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        Services.PLATFORM.fill(poseStackOrGraphics, plusX, counterY, plusX + stepBtnW, counterY + counterH, applyAlpha(hoverPlus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, plusX, counterY, stepBtnW, counterH, applyAlpha(hoverPlus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+", plusX + stepBtnW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private void renderConfigRowButton(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String btnText, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 68;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int bg = hoverBtn ? 0xFF2E2E38 : COLOR_BTN_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, applyAlpha(bg, alpha));

        int border = hoverBtn ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER;
        drawBorder(poseStackOrGraphics, btnX, btnY, btnW, btnH, applyAlpha(border, alpha));

        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, btnText, btnX + btnW / 2, btnY + 3, applyAlpha(hoverBtn ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowBadge(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String badgeText, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, -1, -1, alpha);

        int textW = font.width(badgeText);
        int badgeW = textW + 10;
        int badgeH = 14;
        int badgeX = x + w - badgeW - 8;
        int badgeY = y + (h - badgeH) / 2;

        Services.PLATFORM.fill(poseStackOrGraphics, badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, applyAlpha(0xFF141418, alpha));
        drawBorder(poseStackOrGraphics, badgeX, badgeY, badgeW, badgeH, applyAlpha(0xFF383842, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, badgeText, badgeX + badgeW / 2, badgeY + 3, applyAlpha(COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowPalette(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, List<String> colors, int maxColors, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, "Max active colors: " + maxColors + " / " + colors.size(), mouseX, mouseY, alpha);

        int swatchSize = 12;
        int swatchGap = 3;
        int swatchesTotalW = Math.min(7, colors.size()) * (swatchSize + swatchGap);
        int swatchStartX = x + w - swatchesTotalW - 8;
        int swatchY = y + (h - swatchSize) / 2;

        for (int i = 0; i < Math.min(7, colors.size()); i++) {
            int swX = swatchStartX + i * (swatchSize + swatchGap);
            int colorInt = 0xFFFFFFFF;
            try {
                String hex = colors.get(i).replace("#", "");
                colorInt = 0xFF000000 | Integer.parseInt(hex, 16);
            } catch (Exception ignored) {}

            boolean active = i < maxColors;
            Services.PLATFORM.fill(poseStackOrGraphics, swX, swatchY, swX + swatchSize, swatchY + swatchSize, applyAlpha(colorInt, active ? alpha : alpha * 0.35f));
            int borderCol = active ? 0xFFFFFFFF : 0xFF404048;
            drawBorder(poseStackOrGraphics, swX, swatchY, swatchSize, swatchSize, applyAlpha(borderCol, alpha));
        }
    }

    // --- Vault Hunters Border & Bevel Engine ---
    private void drawVaultHuntersFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        // Solid opaque background
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bgRgb, alpha));

        // Outer 1px: Black
        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);

        // Top Gradient Border
        for (int i = 1; i < w - 1; i++) {
            float t = (float) i / (w - 2);
            int col = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + i, y + 1, x + i + 1, y + 2, applyAlpha(col, alpha));
        }

        // Left Gradient Border
        for (int j = 1; j < h - 1; j++) {
            float t = (float) j / (h - 2);
            int col = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_SHADOW, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + j, x + 2, y + j + 1, applyAlpha(col, alpha));
        }

        // Bottom and Right Inner Shadows
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + h - 2, x + w - 1, y + h - 1, shadow);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, shadow);
    }

    private void drawInsetBox(Object poseStackOrGraphics, int x, int y, int w, int h, int fillRgb, float alpha) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(fillRgb, alpha));
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(COLOR_FRAME_BLACK, alpha));
    }

    private void drawGroove(Object poseStackOrGraphics, int x, int y, int w, boolean horizontal, float alpha) {
        int dark = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int light = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        if (horizontal) {
            Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, dark);
            Services.PLATFORM.fill(poseStackOrGraphics, x, y + 1, x + w, y + 2, light);
        }
    }

    private static void drawBorder(Object poseStackOrGraphics, int x, int y, int w, int h, int color) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + 1, x + 1, y + h - 1, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    private int lerpColor(int c1, int c2, float t) {
        int a1 = (c1 >> 24) & 0xFF;
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int a2 = (c2 >> 24) & 0xFF;
        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int a = Math.round(a1 + (a2 - a1) * t);
        int r = Math.round(r1 + (r2 - r1) * t);
        int g = Math.round(g1 + (g2 - g1) * t);
        int b = Math.round(b1 + (b2 - b1) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void drawText(Object poseStackOrGraphics, Font font, String text, int x, int y, int color) {
        Services.PLATFORM.drawShadow(poseStackOrGraphics, font, text, x, y, color);
    }

    // --- Mouse & Keyboard Event Handlers ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isClosing) return true;

        int panelW = Math.min(width - 32, 530);
        int panelH = Math.min(height - 24, 276);
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        // 1. Close Button [ ✕ ]
        int closeBtnSize = 14;
        int closeBtnX = panelX + panelW - closeBtnSize - 7;
        int closeBtnY = panelY + 5;
        if (mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize) {
            startClosing();
            return true;
        }

        // 2. Sidenav Tabs & Toggle
        int headerH = 24;
        int navW = navExpanded ? 104 : 30;
        int navX = panelX + 4;
        int navY = panelY + headerH + 4;
        int navH = panelH - headerH - 8;

        // Sidenav Collapse/Expand button
        int toggleH = 16;
        int toggleW = navW - 6;
        int toggleX = navX + 3;
        int toggleY = navY + navH - toggleH - 3;
        if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
            navExpanded = !navExpanded;
            return true;
        }

        // Sidenav Tab Items
        int tabH = 26;
        int tabGap = 3;
        int startY = navY + 4;
        for (int i = 0; i < navItems.size(); i++) {
            int tabY = startY + i * (tabH + tabGap);
            int tabW = navW - 6;
            int tabX = navX + 3;
            if (mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH) {
                if (selectedTabIndex != i) {
                    selectedTabIndex = i;
                    scrollOffset = 0.0;
                }
                return true;
            }
        }

        // 3. Right Content Viewport Controls
        int contentX = navX + navW + 4;
        int contentY = navY;
        int contentW = (panelX + panelW - 4) - contentX;
        int contentH = navH;

        int catHeaderH = 22;
        int viewY = contentY + catHeaderH + 3;
        int viewH = contentH - catHeaderH - 6;

        if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= viewY && mouseY <= viewY + viewH) {
            if (handleContentClick(mouseX, mouseY, contentX + 6, viewY + 2 - (int) scrollOffset, contentW - 12, 26, 3, viewY, viewH)) {
                return true;
            }
        }

        if (activeTab != null && activeTab.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return false;
    }

    private boolean handleContentClick(double mouseX, double mouseY, int x, int startY, int w, int h, int gap, int clipY, int clipH) {
        NavItem current = navItems.get(selectedTabIndex);
        int curY = startY;

        switch (current.id) {
            case "world" -> {
                UUID playerUuid = getPlayerUuid();
                Level level = getLevel();

                // Row 0: Tree Breaker
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean val = !CosmeticsConfig.get().getCreativeTreeBreaker(playerUuid);
                    CosmeticsConfig.get().setCreativeTreeBreaker(playerUuid, val);
                    return true;
                }
                curY += h + gap;

                // Row 1: Shulker Preview
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean val = !CosmeticsConfig.get().getShulkerPreview(playerUuid);
                    CosmeticsConfig.get().setShulkerPreview(playerUuid, val);
                    return true;
                }
                curY += h + gap;

                // Row 2: Fast Leaf Decay
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.FAST_LEAF_DECAY, ModGameRules.clientFastLeafDecay) : ModGameRules.clientFastLeafDecay;
                    ModGameRules.clientFastLeafDecay = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("fastLeafDecay", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 3: Enderman Grief
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing) : ModGameRules.clientDisableEndermanGriefing;
                    ModGameRules.clientDisableEndermanGriefing = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("disableEndermanGriefing", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 4: Creeper Grief
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing) : ModGameRules.clientDisableCreeperGriefing;
                    ModGameRules.clientDisableCreeperGriefing = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("disableCreeperGriefing", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 5: Ghast Grief
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_GHAST_GRIEFING, ModGameRules.clientDisableGhastGriefing) : ModGameRules.clientDisableGhastGriefing;
                    ModGameRules.clientDisableGhastGriefing = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("disableGhastGriefing", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 6: Cake Stacking
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.IS_CAKE_STACK, ModGameRules.clientCakeStacking) : ModGameRules.clientCakeStacking;
                    ModGameRules.clientCakeStacking = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("isCakeStack", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 7: Water Bottle Stacking
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.IS_WATER_BOTTLE_STACK, ModGameRules.clientWaterBottleStacking) : ModGameRules.clientWaterBottleStacking;
                    ModGameRules.clientWaterBottleStacking = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("isWaterbottleStack", !cur));
                    return true;
                }
            }
            case "particles" -> {
                PillarParticleConfig cfg = PillarParticleConfig.get();

                // Row 0: Use Pattern Toggle
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    cfg.use_pattern = !cfg.use_pattern;
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // Row 1: Trajectory Pattern Cycle
                if (checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    int pIndex = 0;
                    for (int i = 0; i < PARTICLE_PATTERNS.length; i++) {
                        if (PARTICLE_PATTERNS[i].equalsIgnoreCase(cfg.pattern)) {
                            pIndex = i;
                            break;
                        }
                    }
                    cfg.pattern = PARTICLE_PATTERNS[(pIndex + 1) % PARTICLE_PATTERNS.length];
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // Row 2: Density Stepper
                int densityStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (densityStep != 0) {
                    int dIndex = 1;
                    for (int i = 0; i < PARTICLE_DENSITIES.length; i++) {
                        if (PARTICLE_DENSITIES[i] == cfg.particle_density) {
                            dIndex = i;
                            break;
                        }
                    }
                    int nextIdx = Math.max(0, Math.min(PARTICLE_DENSITIES.length - 1, dIndex + densityStep));
                    cfg.particle_density = PARTICLE_DENSITIES[nextIdx];
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // Row 3: Velocity (Speed) Stepper
                int speedStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (speedStep != 0) {
                    int sIndex = 1;
                    for (int i = 0; i < PARTICLE_SPEEDS.length; i++) {
                        if (Math.abs(PARTICLE_SPEEDS[i] - cfg.particle_speed) < 0.005) {
                            sIndex = i;
                            break;
                        }
                    }
                    int nextIdx = Math.max(0, Math.min(PARTICLE_SPEEDS.length - 1, sIndex + speedStep));
                    cfg.particle_speed = PARTICLE_SPEEDS[nextIdx];
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // Row 4: Spread Stepper
                int spreadStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (spreadStep != 0) {
                    int spIndex = 1;
                    for (int i = 0; i < PARTICLE_SPREADS.length; i++) {
                        if (Math.abs(PARTICLE_SPREADS[i] - cfg.particle_spread) < 0.01) {
                            spIndex = i;
                            break;
                        }
                    }
                    int nextIdx = Math.max(0, Math.min(PARTICLE_SPREADS.length - 1, spIndex + spreadStep));
                    cfg.particle_spread = PARTICLE_SPREADS[nextIdx];
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // Row 5: Lifetime Stepper
                int lifeStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (lifeStep != 0) {
                    int lIndex = 1;
                    for (int i = 0; i < PARTICLE_LIFETIMES.length; i++) {
                        if (PARTICLE_LIFETIMES[i] == cfg.particle_lifetime) {
                            lIndex = i;
                            break;
                        }
                    }
                    int nextIdx = Math.max(0, Math.min(PARTICLE_LIFETIMES.length - 1, lIndex + lifeStep));
                    cfg.particle_lifetime = PARTICLE_LIFETIMES[nextIdx];
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // Row 6: Pattern Intensity Stepper
                int intStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (intStep != 0) {
                    int itIndex = 1;
                    for (int i = 0; i < PATTERN_INTENSITIES.length; i++) {
                        if (Math.abs(PATTERN_INTENSITIES[i] - cfg.pattern_intensity) < 0.1) {
                            itIndex = i;
                            break;
                        }
                    }
                    int nextIdx = Math.max(0, Math.min(PATTERN_INTENSITIES.length - 1, itIndex + intStep));
                    cfg.pattern_intensity = PATTERN_INTENSITIES[nextIdx];
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // Row 7: Swatch click
                if (curY + h >= clipY && curY <= clipY + clipH) {
                    int swatchSize = 12;
                    int swatchGap = 3;
                    int swatchesTotalW = Math.min(7, cfg.particle_color.size()) * (swatchSize + swatchGap);
                    int swatchStartX = x + w - swatchesTotalW - 8;
                    int swatchY = curY + (h - swatchSize) / 2;

                    for (int i = 0; i < Math.min(7, cfg.particle_color.size()); i++) {
                        int swX = swatchStartX + i * (swatchSize + swatchGap);
                        if (mouseX >= swX && mouseX <= swX + swatchSize && mouseY >= swatchY && mouseY <= swatchY + swatchSize) {
                            cfg.max_particle_color = i + 1;
                            cfg.saveProperties();
                            return true;
                        }
                    }
                }
            }
            case "pillars" -> {
                curY += h + gap; // Row 0 is badge

                // Row 1: Sync button
                if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH, 68)) {
                    PacketFactory.sendToServer(new RequestPillarIdsPacket());
                    return true;
                }
                curY += h + gap;

                // Row 2: Auto-snapping toggle
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    autoSnappingEnabled = !autoSnappingEnabled;
                    return true;
                }
                curY += h + gap;

                // Row 3: Strict bounds check toggle
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    strictBoundsCheck = !strictBoundsCheck;
                    return true;
                }
                curY += h + gap;

                // Row 4: Clear button
                if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH, 68)) {
                    PacketFactory.sendToServer(new UpdateAllPillarIdsPacket(new ArrayList<>()));
                    return true;
                }
            }
            case "items" -> {
                curY += h + gap; // Row 0 is badge

                // Row 1: Reset defaults
                if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH, 68)) {
                    PillarParticleConfig cfg = PillarParticleConfig.get();
                    if (cfg.items != null) {
                        cfg.items.add("minecraft:diamond");
                        cfg.items.add("minecraft:netherite_ingot");
                        cfg.items.add("minecraft:nether_star");
                        cfg.items.add("minecraft:heart_of_the_sea");
                        cfg.items.add("minecraft:trident");
                        cfg.items.add("minecraft:emerald");
                        cfg.items.add("minecraft:dragon_breath");
                        cfg.items.add("minecraft:netherite_scrap");
                        cfg.items.add("minecraft:totem_of_undying");
                        cfg.items.add("minecraft:spyglass");
                        cfg.items.add("minecraft:elytra");
                    }
                    cfg.saveItemsToDisk();
                    return true;
                }
                curY += h + gap;

                // Row 2: Save items
                if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH, 68)) {
                    PillarParticleConfig.get().saveItems();
                    return true;
                }
            }
            case "system" -> {
                // Row 0: Animation cycle
                if (checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    animSpeedIndex = (animSpeedIndex + 1) % ANIM_SPEEDS.length;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkToggleClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y + h < clipY || y > clipY + clipH) return false;
        int btnW = 38;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
    }

    private boolean checkCycleClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y + h < clipY || y > clipY + clipH) return false;
        int btnW = 82;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
    }

    private int checkCounterClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y + h < clipY || y > clipY + clipH) return 0;
        int counterW = 76;
        int counterH = 14;
        int counterX = x + w - counterW - 8;
        int counterY = y + (h - counterH) / 2;
        int stepBtnW = 14;

        if (mouseY >= counterY && mouseY <= counterY + counterH) {
            if (mouseX >= counterX && mouseX <= counterX + stepBtnW) {
                return -1;
            } else if (mouseX >= counterX + counterW - stepBtnW && mouseX <= counterX + counterW) {
                return 1;
            }
        }
        return 0;
    }

    private boolean checkButtonClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH, int btnW) {
        if (y + h < clipY || y > clipY + clipH) return false;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (activeTab != null && activeTab.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (activeTab != null && activeTab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (maxScroll > 0) {
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - delta * 18.0));
            return true;
        }
        if (activeTab != null && activeTab.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (activeTab != null && activeTab.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) { // ESC key
            startClosing();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (activeTab != null && activeTab.charTyped(codePoint, modifiers)) {
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
    }

    @Override
    public void onClose() {
        if (!isClosing) {
            startClosing();
        }
    }

    // --- Legacy & Widget Interoperability Helpers ---
    public void addManagedWidget(AbstractWidget widget) {
        widgets.add(widget);
        if (addWidgetConsumer != null) {
            addWidgetConsumer.accept(widget);
        }
    }

    public void addTabWidget(GuiEventListener widget) {
        tabWidgets.add(widget);
        if (widget instanceof AbstractWidget abstractWidget && addWidgetConsumer != null) {
            addWidgetConsumer.accept(abstractWidget);
        }
    }

    public AbstractWidget addCustomWidget(com.kingodogo.buildscape.client.screen.widget.ICustomWidget widget) {
        AbstractWidget nativeWidget = Services.PLATFORM.wrapCustomWidget(
                widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(),
                ComponentHelper.empty(), widget
        );
        addTabWidget(nativeWidget);
        return nativeWidget;
    }

    public void removeTabWidget(GuiEventListener widget) {
        tabWidgets.remove(widget);
        if (removeWidgetConsumer != null) {
            removeWidgetConsumer.accept(widget);
        }
    }

    public void setActiveTab(AbstractConfigTab tab) {
        if (this.activeTab != null) {
            this.activeTab.onClose();
        }
        for (GuiEventListener widget : new ArrayList<>(tabWidgets)) {
            removeTabWidget(widget);
        }
        this.activeTab = tab;
        if (this.activeTab != null && width > 0 && height > 0) {
            this.activeTab.init();
        }
    }

    public AbstractConfigTab getActiveTab() {
        return activeTab;
    }

    public int getContentX() {
        int panelW = Math.min(width - 32, 530);
        int panelX = (width - panelW) / 2;
        int navW = navExpanded ? 104 : 30;
        return panelX + 4 + navW + 4;
    }

    public int getContentY() {
        int panelH = Math.min(height - 24, 276);
        int panelY = (height - panelH) / 2;
        return panelY + 24 + 4;
    }

    public int getContentWidth() {
        int panelW = Math.min(width - 32, 530);
        int navW = navExpanded ? 104 : 30;
        return panelW - 8 - navW - 4;
    }

    public int getContentHeight() {
        int panelH = Math.min(height - 24, 276);
        return panelH - 24 - 8;
    }

    public int getSidebarWidth() {
        return navExpanded ? 104 : 30;
    }

    public static int scaleSize(int baseSize) { return baseSize; }
    public static int getScaledButtonHeight() { return 20; }
    public static int getScaledEditBoxHeight() { return 18; }
    public static float getStandardTextScale() { return 1.0f; }
}
