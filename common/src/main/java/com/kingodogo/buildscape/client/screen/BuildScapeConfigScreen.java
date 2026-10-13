package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.config.PresetsConfig;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.RemovePillarPacket;
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
 * - 1 Big Unified Panel with bold, rich 5px metallic bordering.
 * - Spill-proof scrollable viewport (no rows spill past bottom frame).
 * - Exact naming: Enderman Griefing, Creeper Griefing, Ghast Griefing, etc.
 * - Complete preservation of earlier tabs and mechanics:
 *     * Items (Presets management, active item triggers, quick-add tags/items, removal)
 *     * Particles (Use Pattern toggle, Trajectory, Pattern Speed/Spread/Intensity,
 *                  Basic Speed/Spread/Lifetime/Density, and Palette Hex Swatches)
 *     * Pillar IDs (Synchronized pillar list with X/Y/Z, dimensions, variants, remove per pillar,
 *                   reload from server, clear all, auto-snapping & bounds collision checks)
 *     * World Settings (Creative Tree Breaker, Shulker Box Preview, Fast Leaf Decay,
 *                       Enderman Griefing, Creeper Griefing, Ghast Griefing, Cake Stacking, Water Bottle Stacking)
 *     * System (UI Animation speeds, version cluster, permissions)
 * - Solid opaque palette (no 75% transparency).
 * - Cubic ease animations.
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
    public static final int COLOR_BORDER_GRADIENT_START = 0xFF767682;
    public static final int COLOR_BORDER_GRADIENT_END = 0xFF3C3C44;
    public static final int COLOR_BORDER_SHADOW = 0xFF18181C;

    // Text & Accents
    public static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    public static final int COLOR_TEXT_SILVER = 0xFFCCCCCC;
    public static final int COLOR_TEXT_MUTED = 0xFF84848C;

    // Interactive Widget Colors
    public static final int COLOR_ROW_BG = 0xFF18181D;
    public static final int COLOR_ROW_BG_HOVER = 0xFF22222A;
    public static final int COLOR_ROW_BORDER = 0xFF282830;
    public static final int COLOR_ROW_BORDER_HOVER = 0xFF4E4E5C;

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

    // --- Preset & Items Options ---
    private int selectedPresetIndex = 0;
    private static final String[] QUICK_ADD_ITEMS = {
            "minecraft:diamond",
            "minecraft:netherite_ingot",
            "minecraft:nether_star",
            "the_vault:echo_pog",
            "#the_vault:keys",
            "#the_vault:gems",
            "#c:ores"
    };

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
        navItems.add(new NavItem("items", "☵", "Items", "Pillar trigger items, tag selectors & presets"));
        navItems.add(new NavItem("particles", "✦", "Particles", "Kinetic patterns, velocities, lifetimes & colors"));
        navItems.add(new NavItem("pillars", "▲", "Pillar IDs", "Synchronized pillar columns, positions & variants"));
        navItems.add(new NavItem("world", "⚙", "World Settings", "Griefing rules, instant felling & game mechanics"));
        navItems.add(new NavItem("system", "ℹ", "System", "Engine target, permissions & client diagnostics"));
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

        // 2. Geometry: 1 Big Unified Panel centered on screen with increased border clearance
        int panelW = Math.min(width - 20, 560);
        int panelH = Math.min(height - 16, 304);
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        // Smooth slide-in vertical offset
        int offsetY = Math.round((1.0f - alpha) * 8.0f);
        panelY += offsetY;

        // 3. Render 1 Big Frame with thick 5px metallic bevels
        drawVaultHuntersFrame(poseStackOrGraphics, panelX, panelY, panelW, panelH, COLOR_PANEL_BG, alpha);

        // 4. Header Bar
        int headerH = 26;
        int headerX = panelX + 5;
        int headerY = panelY + 5;
        int headerW = panelW - 10;
        Services.PLATFORM.fill(poseStackOrGraphics, headerX, headerY, headerX + headerW, headerY + headerH, applyAlpha(COLOR_HEADER_BG, alpha));
        drawGroove(poseStackOrGraphics, headerX, headerY + headerH, headerW, true, alpha);

        // Header Title
        drawText(poseStackOrGraphics, font, "⚙ Buildscape Configuration", headerX + 8, headerY + 8, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Target version indicator
        String versionTag = "v4.0.3 · NeoForge 26.2";
        int tagW = font.width(versionTag);
        drawText(poseStackOrGraphics, font, versionTag, headerX + headerW - tagW - 28, headerY + 8, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // Close Button [ ✕ ]
        int closeBtnSize = 14;
        int closeBtnX = headerX + headerW - closeBtnSize - 6;
        int closeBtnY = headerY + 6;
        boolean hoverClose = mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize;
        Services.PLATFORM.fill(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnX + closeBtnSize, closeBtnY + closeBtnSize, applyAlpha(hoverClose ? 0xFF383844 : 0xFF222228, alpha));
        drawBorder(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnSize, closeBtnSize, applyAlpha(hoverClose ? 0xFF6C6C7C : 0xFF34343E, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeBtnX + closeBtnSize / 2, closeBtnY + 3, applyAlpha(hoverClose ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // 5. Layout Sidenav & Content Container
        int navW = navExpanded ? 106 : 30;
        int navX = panelX + 5;
        int navY = headerY + headerH + 4;
        int navH = panelH - headerH - 14;

        int contentX = navX + navW + 4;
        int contentY = navY;
        int contentW = (panelX + panelW - 5) - contentX;
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

            int border = isSelected ? 0xFF585866 : (hover ? 0xFF363640 : 0xFF1C1C22);
            drawBorder(poseStackOrGraphics, tabX, tabY, tabW, tabH, applyAlpha(border, alpha));

            if (isSelected) {
                // Left active accent bar
                Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY, tabX + 2, tabY + tabH, applyAlpha(COLOR_TEXT_WHITE, alpha));
            }

            if (navExpanded) {
                int iconCol = isSelected ? COLOR_TEXT_WHITE : (hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                drawText(poseStackOrGraphics, font, item.icon, tabX + 6, tabY + 9, applyAlpha(iconCol, alpha));

                int textCol = isSelected ? COLOR_TEXT_WHITE : (hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                drawText(poseStackOrGraphics, font, item.title, tabX + 20, tabY + 9, applyAlpha(textCol, alpha));
            } else {
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

        // Spill-proof Scrollable Config Rows Viewport (Enforces 8px bottom clearance)
        int viewY = y + catHeaderH + 4;
        int viewH = h - catHeaderH - 8;
        int rowH = 26;
        int rowGap = 3;

        int rowW = w - 14;
        int rowX = x + 6;

        int totalContentHeight = calculateTotalContentHeight(current.id, rowH, rowGap);
        maxScroll = Math.max(0, totalContentHeight - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        int startY = viewY + 2 - (int) scrollOffset;

        // Render Rows with strict viewport clipping
        switch (current.id) {
            case "items" -> renderItemSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "particles" -> renderParticleSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "pillars" -> renderPillarIdRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "world" -> renderWorldSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "system" -> renderSystemRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
        }

        // Spill-proof Scrollbar on right edge
        if (maxScroll > 0) {
            int scrollbarW = 4;
            int scrollbarX = x + w - scrollbarW - 4;
            int trackH = viewH;
            int thumbH = Math.max(16, (int) ((viewH / (float) totalContentHeight) * trackH));
            int thumbY = viewY + (int) ((scrollOffset / maxScroll) * (trackH - thumbH));

            Services.PLATFORM.fill(poseStackOrGraphics, scrollbarX, viewY, scrollbarX + scrollbarW, viewY + trackH, applyAlpha(0xFF141418, alpha));
            Services.PLATFORM.fill(poseStackOrGraphics, scrollbarX, thumbY, scrollbarX + scrollbarW, thumbY + thumbH, applyAlpha(0xFF4C4C58, alpha));
        }
    }

    private int calculateTotalContentHeight(String tabId, int rowH, int gap) {
        int count = switch (tabId) {
            case "items" -> 3 + (PillarParticleConfig.get().items != null ? PillarParticleConfig.get().items.size() : 0);
            case "particles" -> 11;
            case "pillars" -> 3 + Math.max(1, PillarIdManager.getClient().getPillarCount());
            case "world" -> 8;
            case "system" -> 5;
            default -> 0;
        };
        return count * (rowH + gap) + 6;
    }

    // --- Tab 0: Items (Presets & Trigger Items) ---
    private void renderItemSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        PresetsConfig presetsCfg = PresetsConfig.get();
        List<PresetsConfig.Preset> presets = presetsCfg.getPresets();
        String currentPresetName = presets.isEmpty() ? "Default" : presets.get(Math.min(selectedPresetIndex, presets.size() - 1)).name;

        int curY = startY;

        // Row 0: Preset Management Header
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Active Preset", "Select and switch between pillar trigger item presets", mouseX, mouseY, alpha);

            // Cycle preset button
            int cycleW = 76;
            int btnH = 14;
            int cycleX = x + w - cycleW - 96;
            int cycleY = curY + (h - btnH) / 2;
            boolean hoverCycle = mouseX >= cycleX && mouseX <= cycleX + cycleW && mouseY >= cycleY && mouseY <= cycleY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, cycleX, cycleY, cycleX + cycleW, cycleY + btnH, applyAlpha(hoverCycle ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, cycleX, cycleY, cycleW, btnH, applyAlpha(hoverCycle ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "‹ " + currentPresetName + " ›", cycleX + cycleW / 2, cycleY + 3, applyAlpha(hoverCycle ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

            // Apply button
            int applyW = 42;
            int applyX = x + w - applyW - 48;
            int applyY = cycleY;
            boolean hoverApply = mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, applyX, applyY, applyX + applyW, applyY + btnH, applyAlpha(hoverApply ? 0xFF1C3C22 : COLOR_TOGGLE_ON_BG, alpha));
            drawBorder(poseStackOrGraphics, applyX, applyY, applyW, btnH, applyAlpha(COLOR_TOGGLE_ON_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Apply", applyX + applyW / 2, applyY + 3, applyAlpha(COLOR_TOGGLE_ON_TEXT, alpha));

            // Save button
            int saveW = 40;
            int saveX = x + w - saveW - 6;
            int saveY = cycleY;
            boolean hoverSave = mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= saveY && mouseY <= saveY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, saveX, saveY, saveX + saveW, saveY + btnH, applyAlpha(hoverSave ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, saveX, saveY, saveW, btnH, applyAlpha(hoverSave ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Save", saveX + saveW / 2, saveY + 3, applyAlpha(hoverSave ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        }
        curY += h + gap;

        // Row 1: Actions (Reset defaults & Save to disk)
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar Trigger Registry", cfg.items.size() + " total item triggers configured", mouseX, mouseY, alpha);

            int btnW = 86;
            int btnH = 14;
            int resetX = x + w - (btnW * 2) - 12;
            int resetY = curY + (h - btnH) / 2;
            boolean hoverReset = mouseX >= resetX && mouseX <= resetX + btnW && mouseY >= resetY && mouseY <= resetY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, resetX, resetY, resetX + btnW, resetY + btnH, applyAlpha(hoverReset ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, resetX, resetY, btnW, btnH, applyAlpha(hoverReset ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "↺ Defaults", resetX + btnW / 2, resetY + 3, applyAlpha(hoverReset ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

            int saveX = x + w - btnW - 6;
            boolean hoverSave = mouseX >= saveX && mouseX <= saveX + btnW && mouseY >= resetY && mouseY <= resetY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, saveX, resetY, saveX + btnW, resetY + btnH, applyAlpha(hoverSave ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, saveX, resetY, btnW, btnH, applyAlpha(hoverSave ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "💾 Save Disk", saveX + btnW / 2, resetY + 3, applyAlpha(hoverSave ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        }
        curY += h + gap;

        // Row 2: Quick-Add Tags & Valuables
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Quick Add Items/Tags", "Click to quickly append popular items to current trigger list", mouseX, mouseY, alpha);

            int chipW = 54;
            int chipH = 14;
            int chipY = curY + (h - chipH) / 2;
            String[] chips = {"+ Diam", "+ Neth", "+ Echo", "+ #keys"};
            for (int c = 0; c < chips.length; c++) {
                int chipX = x + w - ((chips.length - c) * (chipW + 4)) - 2;
                boolean hoverChip = mouseX >= chipX && mouseX <= chipX + chipW && mouseY >= chipY && mouseY <= chipY + chipH;
                Services.PLATFORM.fill(poseStackOrGraphics, chipX, chipY, chipX + chipW, chipY + chipH, applyAlpha(hoverChip ? 0xFF2C2C36 : COLOR_BTN_BG, alpha));
                drawBorder(poseStackOrGraphics, chipX, chipY, chipW, chipH, applyAlpha(hoverChip ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, chips[c], chipX + chipW / 2, chipY + 3, applyAlpha(hoverChip ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
            }
        }
        curY += h + gap;

        // Rows 3+: Configured Items List with [ ✕ Remove ] button
        List<String> itemsList = new ArrayList<>(cfg.items);
        for (String itemStr : itemsList) {
            if (curY >= clipY && curY + h <= clipY + clipH) {
                boolean isTag = itemStr.startsWith("#");
                String badge = isTag ? "[TAG]" : "[ITEM]";
                int badgeCol = isTag ? 0xFF60A5FA : 0xFF4ADE80;

                renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, itemStr, badge + "  Triggers ambient pillar FX", mouseX, mouseY, alpha);

                // Remove button
                int removeW = 20;
                int removeH = 14;
                int removeX = x + w - removeW - 8;
                int removeY = curY + (h - removeH) / 2;
                boolean hoverRem = mouseX >= removeX && mouseX <= removeX + removeW && mouseY >= removeY && mouseY <= removeY + removeH;
                Services.PLATFORM.fill(poseStackOrGraphics, removeX, removeY, removeX + removeW, removeY + removeH, applyAlpha(hoverRem ? 0xFF3D1616 : 0xFF241414, alpha));
                drawBorder(poseStackOrGraphics, removeX, removeY, removeW, removeH, applyAlpha(hoverRem ? 0xFFE05050 : 0xFF882828, alpha));
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", removeX + removeW / 2, removeY + 3, applyAlpha(hoverRem ? 0xFFFFFFFF : 0xFFE05050, alpha));
            }
            curY += h + gap;
        }
    }

    // --- Tab 1: Particles (Full Options) ---
    private void renderParticleSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        int curY = startY;

        // 1. Mode Toggle
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Use Particle Pattern", "Toggle geometric pattern emission solver", cfg.use_pattern, mouseX, mouseY, alpha);
        curY += h + gap;

        // 2. Pattern Cycle
        String patternDisplay = cfg.pattern.substring(0, 1).toUpperCase() + cfg.pattern.substring(1);
        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Trajectory Pattern", "Kinetic flight path solver formula", patternDisplay, mouseX, mouseY, alpha);
        curY += h + gap;

        // 3. Pattern Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Speed", "Rotational animation speed multiplier", String.format("%.2f", cfg.pattern_speed), mouseX, mouseY, alpha);
        curY += h + gap;

        // 4. Pattern Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Spread", "Geometric particle dispersion angle", String.format("%.2f", cfg.pattern_spread), mouseX, mouseY, alpha);
        curY += h + gap;

        // 5. Pattern Intensity
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Intensity", "Kinetic impulse pattern power multiplier", String.format("%.1fx", cfg.pattern_intensity), mouseX, mouseY, alpha);
        curY += h + gap;

        // 6. Basic Particle Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Velocity (Speed)", "Linear particle ejection velocity", String.format("%.2f", cfg.particle_speed), mouseX, mouseY, alpha);
        curY += h + gap;

        // 7. Basic Particle Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Spread", "Apex dispersion radius factor", String.format("%.2f", cfg.particle_spread), mouseX, mouseY, alpha);
        curY += h + gap;

        // 8. Particle Lifetime
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Lifetime", "Duration in ticks until particle despawn", cfg.particle_lifetime + " ticks", mouseX, mouseY, alpha);
        curY += h + gap;

        // 9. Particle Density
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Density", "Multi-column particle emission rate", cfg.particle_density + "x", mouseX, mouseY, alpha);
        curY += h + gap;

        // 10. Palette Colors Swatches & Max Colors
        renderConfigRowPalette(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Palette Colors", cfg.particle_color, cfg.max_particle_color, mouseX, mouseY, alpha);
        curY += h + gap;

        // 11. Reset Colors Action
        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Reset Color Palette", "Restore default hexadecimal particle swatches", "↺ Reset Colors", mouseX, mouseY, alpha);
    }

    // --- Tab 2: Pillar IDs Rows ---
    private void renderPillarIdRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        int pillarCount = PillarIdManager.getClient().getPillarCount();
        int curY = startY;

        // Row 0: Actions (Reload & Clear)
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar Synchronizer", pillarCount + " active pillars mapped in world", mouseX, mouseY, alpha);

            int btnW = 76;
            int btnH = 14;
            int reloadX = x + w - (btnW * 2) - 12;
            int reloadY = curY + (h - btnH) / 2;
            boolean hoverReload = mouseX >= reloadX && mouseX <= reloadX + btnW && mouseY >= reloadY && mouseY <= reloadY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, reloadX, reloadY, reloadX + btnW, reloadY + btnH, applyAlpha(hoverReload ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, reloadX, reloadY, btnW, btnH, applyAlpha(hoverReload ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "⟳ Sync", reloadX + btnW / 2, reloadY + 3, applyAlpha(hoverReload ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

            int clearX = x + w - btnW - 6;
            boolean hoverClear = mouseX >= clearX && mouseX <= clearX + btnW && mouseY >= reloadY && mouseY <= reloadY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, clearX, reloadY, clearX + btnW, reloadY + btnH, applyAlpha(hoverClear ? 0xFF3D1616 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, clearX, reloadY, btnW, btnH, applyAlpha(hoverClear ? 0xFFE05050 : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕ Clear", clearX + btnW / 2, reloadY + 3, applyAlpha(hoverClear ? 0xFFFFFFFF : COLOR_TEXT_SILVER, alpha));
        }
        curY += h + gap;

        // Row 1: Auto-snapping
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Auto-Snapping Bounds", "Snap placement cursor to nearest voxel pillar node", autoSnappingEnabled, mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 2: Strict collision
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Strict Collision Check", "Reject placement collisions with non-air voxels", strictBoundsCheck, mouseX, mouseY, alpha);
        curY += h + gap;

        // Rows 3+: Synchronized Pillars List
        java.util.Collection<PillarIdManager.PillarData> pillars = PillarIdManager.getClient().getAllData();
        if (pillars.isEmpty()) {
            if (curY >= clipY && curY + h <= clipY + clipH) {
                renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "No Pillars Registered", "Place pillar blocks in world to synchronize columns", "Empty", alpha);
            }
        } else {
            for (PillarIdManager.PillarData data : pillars) {
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    String desc = String.format("Pos: [%d, %d, %d] · Dim: %s", data.x, data.y, data.z, data.dimension);
                    renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar: " + data.id, desc, mouseX, mouseY, alpha);

                    int removeW = 50;
                    int removeH = 14;
                    int removeX = x + w - removeW - 8;
                    int removeY = curY + (h - removeH) / 2;
                    boolean hoverRem = mouseX >= removeX && mouseX <= removeX + removeW && mouseY >= removeY && mouseY <= removeY + removeH;
                    Services.PLATFORM.fill(poseStackOrGraphics, removeX, removeY, removeX + removeW, removeY + removeH, applyAlpha(hoverRem ? 0xFF3D1616 : 0xFF241414, alpha));
                    drawBorder(poseStackOrGraphics, removeX, removeY, removeW, removeH, applyAlpha(hoverRem ? 0xFFE05050 : 0xFF882828, alpha));
                    Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Remove", removeX + removeW / 2, removeY + 3, applyAlpha(hoverRem ? 0xFFFFFFFF : 0xFFE05050, alpha));
                }
                curY += h + gap;
            }
        }
    }

    // --- Tab 3: World Settings Rows (Exact Requested Names) ---
    private void renderWorldSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        UUID playerUuid = getPlayerUuid();
        Level level = getLevel();

        boolean treeBreaker = CosmeticsConfig.get().getCreativeTreeBreaker(playerUuid);
        boolean shulkerPeek = CosmeticsConfig.get().getShulkerPreview(playerUuid);
        boolean fastLeaves = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.FAST_LEAF_DECAY, ModGameRules.clientFastLeafDecay) : ModGameRules.clientFastLeafDecay;
        boolean endermanGrief = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing) : ModGameRules.clientDisableEndermanGriefing;
        boolean creeperGrief = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing) : ModGameRules.clientDisableCreeperGriefing;
        boolean ghastGrief = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_GHAST_GRIEFING, ModGameRules.clientDisableGhastGriefing) : ModGameRules.clientDisableGhastGriefing;
        boolean cakeStack = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.IS_CAKE_STACK, ModGameRules.clientCakeStacking) : ModGameRules.clientCakeStacking;
        boolean waterStack = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.IS_WATER_BOTTLE_STACK, ModGameRules.clientWaterBottleStacking) : ModGameRules.clientWaterBottleStacking;

        int curY = startY;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Creative Tree Breaker", "Instantly fell whole trees in creative mode", treeBreaker, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Shulker Box Preview", "Inspect container inventory contents tooltip on hover", shulkerPeek, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Fast Leaf Decay", "Rapid leaf despawning upon trunk destruction (GameRule)", fastLeaves, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Enderman Griefing", "Disable Endermen from picking up world blocks (GameRule)", endermanGrief, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Creeper Griefing", "Disable Creeper explosion terrain destruction (GameRule)", creeperGrief, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Ghast Griefing", "Disable Ghast fireball terrain destruction (GameRule)", ghastGrief, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Cake Stacking", "Allow cake items to stack up to 64 in inventories (GameRule)", cakeStack, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Water Bottle Stacking", "Allow water bottles to stack up to 16 in inventories (GameRule)", waterStack, mouseX, mouseY, alpha);
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

    // --- Reusable Row Renderers with Strict Spill-Proof Boundary Checks ---
    private void renderRowCardBase(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;

        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bg = hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

        int border = hover ? COLOR_ROW_BORDER_HOVER : COLOR_ROW_BORDER;
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(border, alpha));

        drawText(poseStackOrGraphics, font, label, x + 8, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));

        if (desc != null) {
            drawText(poseStackOrGraphics, font, desc, x + 8, y + 15, applyAlpha(COLOR_TEXT_MUTED, alpha));
        }
    }

    private void renderConfigRowToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, boolean active, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
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
        if (y < clipY || y + h > clipY + clipH) return;
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
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int counterW = 76;
        int counterH = 14;
        int counterX = x + w - counterW - 8;
        int counterY = y + (h - counterH) / 2;
        int stepBtnW = 14;

        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        Services.PLATFORM.fill(poseStackOrGraphics, counterX, counterY, counterX + stepBtnW, counterY + counterH, applyAlpha(hoverMinus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, counterX, counterY, stepBtnW, counterH, applyAlpha(hoverMinus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "−", counterX + stepBtnW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int valX = counterX + stepBtnW;
        int valW = counterW - stepBtnW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(0xFF141418, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, value, valX + valW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepBtnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        Services.PLATFORM.fill(poseStackOrGraphics, plusX, counterY, plusX + stepBtnW, counterY + counterH, applyAlpha(hoverPlus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, plusX, counterY, stepBtnW, counterH, applyAlpha(hoverPlus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+", plusX + stepBtnW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private void renderConfigRowButton(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String btnText, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 86;
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
        if (y < clipY || y + h > clipY + clipH) return;
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
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, "Max active colors: " + maxColors + " / " + colors.size(), mouseX, mouseY, alpha);

        int counterW = 60;
        int counterH = 14;
        int counterX = x + w - counterW - 8;
        int counterY = y + (h - counterH) / 2;
        int stepBtnW = 12;

        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        Services.PLATFORM.fill(poseStackOrGraphics, counterX, counterY, counterX + stepBtnW, counterY + counterH, applyAlpha(hoverMinus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, counterX, counterY, stepBtnW, counterH, applyAlpha(hoverMinus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "−", counterX + stepBtnW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int valX = counterX + stepBtnW;
        int valW = counterW - stepBtnW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(0xFF141418, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, String.valueOf(maxColors), valX + valW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepBtnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        Services.PLATFORM.fill(poseStackOrGraphics, plusX, counterY, plusX + stepBtnW, counterY + counterH, applyAlpha(hoverPlus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, plusX, counterY, stepBtnW, counterH, applyAlpha(hoverPlus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+", plusX + stepBtnW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Color swatches displayed to the left of the counter
        int swatchSize = 12;
        int swatchGap = 3;
        int swatchesTotalW = Math.min(7, colors.size()) * (swatchSize + swatchGap);
        int swatchStartX = counterX - swatchesTotalW - 8;
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

    // --- Vault Hunters Border & Bevel Engine (Rich 5px Metallic Framing) ---
    private void drawVaultHuntersFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        // Solid opaque background
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bgRgb, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);

        // 1. Layer 1: Outermost 1px Black Border
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);

        // 2. Layer 2: Metallic Highlight (silver gradient top & left, dark bottom & right)
        int lightHighlight = applyAlpha(0xFF888894, alpha);
        int darkHighlight = applyAlpha(0xFF16161A, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + 2, lightHighlight);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + 2, y + h - 1, lightHighlight);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + h - 2, x + w - 1, y + h - 1, darkHighlight);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, darkHighlight);

        // 3. Layer 3 & 4: Rich Metallic Gradient Bands (2px thick)
        for (int i = 2; i < w - 2; i++) {
            float t = (float) (i - 2) / Math.max(1, w - 4);
            int col1 = lerpColor(0xFF6E6E78, 0xFF42424A, t);
            int col2 = lerpColor(0xFF5A5A64, 0xFF36363C, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + i, y + 2, x + i + 1, y + 3, applyAlpha(col1, alpha));
            Services.PLATFORM.fill(poseStackOrGraphics, x + i, y + 3, x + i + 1, y + 4, applyAlpha(col2, alpha));
        }
        for (int j = 2; j < h - 2; j++) {
            float t = (float) (j - 2) / Math.max(1, h - 4);
            int col1 = lerpColor(0xFF6E6E78, 0xFF28282E, t);
            int col2 = lerpColor(0xFF5A5A64, 0xFF202026, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + j, x + 3, y + j + 1, applyAlpha(col1, alpha));
            Services.PLATFORM.fill(poseStackOrGraphics, x + 3, y + j, x + 4, y + j + 1, applyAlpha(col2, alpha));
        }

        // Bottom and Right 2px gradient bands
        int shadow1 = applyAlpha(0xFF222228, alpha);
        int shadow2 = applyAlpha(0xFF1A1A1E, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + h - 4, x + w - 2, y + h - 3, shadow1);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + h - 3, x + w - 2, y + h - 2, shadow2);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 4, y + 2, x + w - 3, y + h - 2, shadow1);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 3, y + 2, x + w - 2, y + h - 2, shadow2);

        // 4. Layer 5: Inner Inset Shadow Groove (1px)
        int groove = applyAlpha(0xFF101012, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 4, y + 4, x + w - 4, y + 5, groove);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 4, y + 4, x + 5, y + h - 4, groove);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 4, y + h - 5, x + w - 4, y + h - 4, groove);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 5, y + 4, x + w - 4, y + h - 4, groove);
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

        int panelW = Math.min(width - 20, 560);
        int panelH = Math.min(height - 16, 304);
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        int headerH = 26;
        int headerX = panelX + 5;
        int headerY = panelY + 5;
        int headerW = panelW - 10;

        // 1. Close Button [ ✕ ]
        int closeBtnSize = 14;
        int closeBtnX = headerX + headerW - closeBtnSize - 6;
        int closeBtnY = headerY + 6;
        if (mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize) {
            startClosing();
            return true;
        }

        // 2. Sidenav Tabs & Toggle
        int navW = navExpanded ? 106 : 30;
        int navX = panelX + 5;
        int navY = headerY + headerH + 4;
        int navH = panelH - headerH - 14;

        int toggleH = 16;
        int toggleW = navW - 6;
        int toggleX = navX + 3;
        int toggleY = navY + navH - toggleH - 3;
        if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
            navExpanded = !navExpanded;
            return true;
        }

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

        // 3. Right Content Viewport Controls with strict viewport checks
        int contentX = navX + navW + 4;
        int contentY = navY;
        int contentW = (panelX + panelW - 5) - contentX;
        int contentH = navH;

        int catHeaderH = 22;
        int viewY = contentY + catHeaderH + 4;
        int viewH = contentH - catHeaderH - 8;

        if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= viewY && mouseY <= viewY + viewH) {
            if (handleContentClick(mouseX, mouseY, contentX + 6, viewY + 2 - (int) scrollOffset, contentW - 14, 26, 3, viewY, viewH)) {
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
            case "items" -> {
                PillarParticleConfig cfg = PillarParticleConfig.get();
                PresetsConfig presetsCfg = PresetsConfig.get();
                List<PresetsConfig.Preset> presets = presetsCfg.getPresets();

                // Row 0: Preset Management Header
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    int cycleW = 76;
                    int btnH = 14;
                    int cycleX = x + w - cycleW - 96;
                    int cycleY = curY + (h - btnH) / 2;
                    if (mouseX >= cycleX && mouseX <= cycleX + cycleW && mouseY >= cycleY && mouseY <= cycleY + btnH) {
                        if (!presets.isEmpty()) {
                            selectedPresetIndex = (selectedPresetIndex + 1) % presets.size();
                        }
                        return true;
                    }

                    int applyW = 42;
                    int applyX = x + w - applyW - 48;
                    int applyY = cycleY;
                    if (mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + btnH) {
                        if (!presets.isEmpty()) {
                            PresetsConfig.Preset p = presets.get(Math.min(selectedPresetIndex, presets.size() - 1));
                            cfg.items.clear();
                            cfg.items.addAll(p.items);
                            cfg.saveItemsToDisk();
                        }
                        return true;
                    }

                    int saveW = 40;
                    int saveX = x + w - saveW - 6;
                    int saveY = cycleY;
                    if (mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= saveY && mouseY <= saveY + btnH) {
                        cfg.saveItems();
                        return true;
                    }
                }
                curY += h + gap;

                // Row 1: Actions (Reset defaults & Save to disk)
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    int btnW = 86;
                    int btnH = 14;
                    int resetX = x + w - (btnW * 2) - 12;
                    int resetY = curY + (h - btnH) / 2;
                    if (mouseX >= resetX && mouseX <= resetX + btnW && mouseY >= resetY && mouseY <= resetY + btnH) {
                        cfg.items.clear();
                        for (String defItem : QUICK_ADD_ITEMS) {
                            cfg.items.add(defItem);
                        }
                        cfg.saveItemsToDisk();
                        return true;
                    }

                    int saveX = x + w - btnW - 6;
                    if (mouseX >= saveX && mouseX <= saveX + btnW && mouseY >= resetY && mouseY <= resetY + btnH) {
                        cfg.saveItems();
                        return true;
                    }
                }
                curY += h + gap;

                // Row 2: Quick-Add Tags & Valuables
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    int chipW = 54;
                    int chipH = 14;
                    int chipY = curY + (h - chipH) / 2;
                    String[] quickItems = {"minecraft:diamond", "minecraft:netherite_ingot", "the_vault:echo_pog", "#the_vault:keys"};
                    for (int c = 0; c < quickItems.length; c++) {
                        int chipX = x + w - ((quickItems.length - c) * (chipW + 4)) - 2;
                        if (mouseX >= chipX && mouseX <= chipX + chipW && mouseY >= chipY && mouseY <= chipY + chipH) {
                            cfg.items.add(quickItems[c]);
                            cfg.saveItemsToDisk();
                            return true;
                        }
                    }
                }
                curY += h + gap;

                // Rows 3+: Configured Items removal
                List<String> itemsList = new ArrayList<>(cfg.items);
                for (String itemStr : itemsList) {
                    if (curY >= clipY && curY + h <= clipY + clipH) {
                        int removeW = 20;
                        int removeH = 14;
                        int removeX = x + w - removeW - 8;
                        int removeY = curY + (h - removeH) / 2;
                        if (mouseX >= removeX && mouseX <= removeX + removeW && mouseY >= removeY && mouseY <= removeY + removeH) {
                            cfg.items.remove(itemStr);
                            cfg.saveItemsToDisk();
                            return true;
                        }
                    }
                    curY += h + gap;
                }
            }
            case "particles" -> {
                PillarParticleConfig cfg = PillarParticleConfig.get();

                // 1. Mode Toggle
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    cfg.use_pattern = !cfg.use_pattern;
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 2. Pattern Selector
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

                // 3. Pattern Speed
                int pSpeedStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (pSpeedStep != 0) {
                    cfg.pattern_speed = Math.max(0.01D, Math.min(0.50D, Math.round((cfg.pattern_speed + pSpeedStep * 0.01D) * 100.0) / 100.0));
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 4. Pattern Spread
                int pSpreadStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (pSpreadStep != 0) {
                    cfg.pattern_spread = Math.max(0.01D, Math.min(0.50D, Math.round((cfg.pattern_spread + pSpreadStep * 0.01D) * 100.0) / 100.0));
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 5. Pattern Intensity
                int pIntStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (pIntStep != 0) {
                    cfg.pattern_intensity = Math.max(0.1D, Math.min(5.0D, Math.round((cfg.pattern_intensity + pIntStep * 0.1D) * 10.0) / 10.0));
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 6. Basic Speed
                int speedStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (speedStep != 0) {
                    cfg.particle_speed = Math.max(0.01D, Math.min(0.50D, Math.round((cfg.particle_speed + speedStep * 0.01D) * 100.0) / 100.0));
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 7. Basic Spread
                int spreadStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (spreadStep != 0) {
                    cfg.particle_spread = Math.max(0.01D, Math.min(0.50D, Math.round((cfg.particle_spread + spreadStep * 0.01D) * 100.0) / 100.0));
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 8. Lifetime
                int lifeStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (lifeStep != 0) {
                    cfg.particle_lifetime = Math.max(5, Math.min(120, cfg.particle_lifetime + lifeStep * 5));
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 9. Density
                int denStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (denStep != 0) {
                    cfg.particle_density = Math.max(1, Math.min(16, cfg.particle_density + denStep));
                    cfg.saveProperties();
                    return true;
                }
                curY += h + gap;

                // 10. Max Colors Stepper & Swatch Clicks
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    int counterW = 60;
                    int counterH = 14;
                    int counterX = x + w - counterW - 8;
                    int counterY = curY + (h - counterH) / 2;
                    int stepBtnW = 12;

                    if (mouseY >= counterY && mouseY <= counterY + counterH) {
                        if (mouseX >= counterX && mouseX <= counterX + stepBtnW) {
                            cfg.max_particle_color = Math.max(1, cfg.max_particle_color - 1);
                            cfg.saveProperties();
                            return true;
                        } else if (mouseX >= counterX + counterW - stepBtnW && mouseX <= counterX + counterW) {
                            cfg.max_particle_color = Math.min(Math.min(7, cfg.particle_color.size()), cfg.max_particle_color + 1);
                            cfg.saveProperties();
                            return true;
                        }
                    }

                    int swatchSize = 12;
                    int swatchGap = 3;
                    int swatchesTotalW = Math.min(7, cfg.particle_color.size()) * (swatchSize + swatchGap);
                    int swatchStartX = counterX - swatchesTotalW - 8;
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
                curY += h + gap;

                // 11. Reset Colors Action
                if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH, 86)) {
                    cfg.particle_color.clear();
                    cfg.particle_color.add("#FFB81C");
                    cfg.particle_color.add("#FFFFFF");
                    cfg.particle_color.add("#FFFF00");
                    cfg.particle_color.add("#E8FEFD");
                    cfg.particle_color.add("#FF5C00");
                    cfg.particle_color.add("#3CDFFF");
                    cfg.max_particle_color = 3;
                    cfg.saveProperties();
                    return true;
                }
            }
            case "pillars" -> {
                // Row 0: Actions (Reload & Clear)
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    int btnW = 76;
                    int btnH = 14;
                    int reloadX = x + w - (btnW * 2) - 12;
                    int reloadY = curY + (h - btnH) / 2;
                    if (mouseX >= reloadX && mouseX <= reloadX + btnW && mouseY >= reloadY && mouseY <= reloadY + btnH) {
                        PacketFactory.sendToServer(new RequestPillarIdsPacket());
                        return true;
                    }

                    int clearX = x + w - btnW - 6;
                    if (mouseX >= clearX && mouseX <= clearX + btnW && mouseY >= reloadY && mouseY <= reloadY + btnH) {
                        PacketFactory.sendToServer(new UpdateAllPillarIdsPacket(new ArrayList<>()));
                        return true;
                    }
                }
                curY += h + gap;

                // Row 1: Auto-snapping
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    autoSnappingEnabled = !autoSnappingEnabled;
                    return true;
                }
                curY += h + gap;

                // Row 2: Strict collision
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    strictBoundsCheck = !strictBoundsCheck;
                    return true;
                }
                curY += h + gap;

                // Rows 3+: Individual Pillar Removal
                java.util.Collection<PillarIdManager.PillarData> pillars = PillarIdManager.getClient().getAllData();
                for (PillarIdManager.PillarData data : pillars) {
                    if (curY >= clipY && curY + h <= clipY + clipH) {
                        int removeW = 50;
                        int removeH = 14;
                        int removeX = x + w - removeW - 8;
                        int removeY = curY + (h - removeH) / 2;
                        if (mouseX >= removeX && mouseX <= removeX + removeW && mouseY >= removeY && mouseY <= removeY + removeH) {
                            PacketFactory.sendToServer(new RemovePillarPacket(Collections.singletonList(data.id)));
                            return true;
                        }
                    }
                    curY += h + gap;
                }
            }
            case "world" -> {
                UUID playerUuid = getPlayerUuid();
                Level level = getLevel();

                // Row 0: Creative Tree Breaker
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean val = !CosmeticsConfig.get().getCreativeTreeBreaker(playerUuid);
                    CosmeticsConfig.get().setCreativeTreeBreaker(playerUuid, val);
                    return true;
                }
                curY += h + gap;

                // Row 1: Shulker Box Preview
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

                // Row 3: Enderman Griefing
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing) : ModGameRules.clientDisableEndermanGriefing;
                    ModGameRules.clientDisableEndermanGriefing = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("disableEndermanGriefing", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 4: Creeper Griefing
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing) : ModGameRules.clientDisableCreeperGriefing;
                    ModGameRules.clientDisableCreeperGriefing = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("disableCreeperGriefing", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 5: Ghast Griefing
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
            case "system" -> {
                if (checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    animSpeedIndex = (animSpeedIndex + 1) % ANIM_SPEEDS.length;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkToggleClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y < clipY || y + h > clipY + clipH) return false;
        int btnW = 38;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
    }

    private boolean checkCycleClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y < clipY || y + h > clipY + clipH) return false;
        int btnW = 82;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
    }

    private int checkCounterClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y < clipY || y + h > clipY + clipH) return 0;
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
        if (y < clipY || y + h > clipY + clipH) return false;
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
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - delta * 20.0));
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
        int panelW = Math.min(width - 20, 560);
        int panelX = (width - panelW) / 2;
        int navW = navExpanded ? 106 : 30;
        return panelX + 5 + navW + 4;
    }

    public int getContentY() {
        int panelH = Math.min(height - 16, 304);
        int panelY = (height - panelH) / 2;
        return panelY + 26 + 4;
    }

    public int getContentWidth() {
        int panelW = Math.min(width - 20, 560);
        int navW = navExpanded ? 106 : 30;
        return panelW - 10 - navW - 4;
    }

    public int getContentHeight() {
        int panelH = Math.min(height - 16, 304);
        return panelH - 26 - 14;
    }

    public int getSidebarWidth() {
        return navExpanded ? 106 : 30;
    }

    public static int scaleSize(int baseSize) { return baseSize; }
    public static int getScaledButtonHeight() { return 20; }
    public static int getScaledEditBoxHeight() { return 18; }
    public static float getStandardTextScale() { return 1.0f; }
}
