package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Vault-Hunters styled dual-panel configuration screen for Buildscape.
 * Features:
 * - Solid opaque monochrome & parchment palette (no 75% opacity).
 * - Exact beveled gradient borders with corner highlights and metallic shading.
 * - Left tab strip attached directly to the panel border.
 * - Left panel: Companion-style master card list with active green border and bronze gradient.
 * - Right panel: Parchment header summary card + dark modifiers & controls viewport with scrollbar.
 * - Real Buildscape configuration logic (Pillars, Particles, World, IDs, System).
 * - Cubic ease open/close animations.
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
    private long lastFrameTime;

    private boolean isOpening = true;
    private boolean isClosing = false;
    private float openProgress = 0.0f;
    private float closeProgress = 1.0f;

    // --- Palette Constants (Solid Opaque, No 75% transparency) ---
    public static final int COLOR_OVERLAY = 0xE00A0A0C;           // Dimmed dark background
    public static final int COLOR_PANEL_BG = 0xFF18181A;          // Solid dark charcoal panel
    public static final int COLOR_INNER_DARK = 0xFF101012;        // Inset dark groove / slot fill
    public static final int COLOR_HEADER_BG = 0xFF202024;         // Header bar fill

    // Parchment Card (Right top summary card in reference image)
    public static final int COLOR_PARCHMENT_TOP = 0xFFF5E6CA;
    public static final int COLOR_PARCHMENT_BOT = 0xFFDEC8A2;
    public static final int COLOR_PARCHMENT_BORDER = 0xFF3D3222;
    public static final int COLOR_TEXT_PARCHMENT_DARK = 0xFF241A0E;
    public static final int COLOR_TEXT_PARCHMENT_GOLD = 0xFFB87800;

    // Active Card Green Glow (from reference image)
    public static final int COLOR_ACTIVE_GREEN_BORDER = 0xFF4ADE80;
    public static final int COLOR_ACTIVE_BRONZE_GRADIENT = 0xFF3A3022;

    // Beveled Metallic Border Gradients
    public static final int COLOR_FRAME_BLACK = 0xFF000000;
    public static final int COLOR_BORDER_GRADIENT_START = 0xFF707078;
    public static final int COLOR_BORDER_GRADIENT_END = 0xFF34343A;
    public static final int COLOR_BORDER_SHADOW = 0xFF1C1C20;

    // Text & Accents
    public static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    public static final int COLOR_TEXT_SILVER = 0xFFCCCCCC;
    public static final int COLOR_TEXT_MUTED = 0xFF888890;
    public static final int COLOR_GOLD_BAR = 0xFFE5A100;

    // --- Navigation Tabs ---
    public static class NavItem {
        public final String id;
        public final String icon;
        public final String title;
        public final String subtitle;
        public final int level;

        public NavItem(String id, String icon, String title, String subtitle, int level) {
            this.id = id;
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.level = level;
        }
    }

    private final List<NavItem> navItems = new ArrayList<>();
    private int selectedTabIndex = 0;

    // Legacy tab support
    private AbstractConfigTab activeTab;
    private final List<GuiEventListener> widgets = new ArrayList<>();
    private final List<GuiEventListener> tabWidgets = new ArrayList<>();

    // --- Real Buildscape Configuration States ---
    // Tab 0: Pillars
    private boolean pillarGlowEnabled = true;
    private int pillarJointRadius = 8;
    private int pillarLinkingModeIndex = 0;
    private static final String[] PILLAR_LINKING_MODES = {"Adaptive", "Strict", "All Blocks"};

    // Tab 1: Particles
    private boolean pillarSparksEnabled = true;
    private int particlePatternIndex = 0;
    private static final String[] PARTICLE_PATTERNS = {"Beam", "Spiral", "Fountain", "Pulse", "Ring", "Burst", "Snowflake"};
    private int particleDensityIndex = 1;
    private static final String[] PARTICLE_DENSITIES = {"25%", "50%", "100%", "200%"};
    private int selectedSwatchIndex = 0;
    private static final int[] MONO_SWATCH_COLORS = {0xFFFFFFFF, 0xFFB0B0B8, 0xFF585860, 0xFF1C1C20};
    private static final String[] MONO_SWATCH_NAMES = {"Pure White", "Silver", "Graphite", "Obsidian"};

    // Tab 2: World Settings
    private boolean creativeTreeBreaker = true;
    private boolean shulkerPreview = true;
    private boolean fastLeafDecay = true;
    private boolean mobGriefingDefense = false;

    // Tab 3: Pillar IDs & Snapping
    private boolean autoSnappingEnabled = true;
    private boolean strictBoundsCheck = true;

    // Tab 4: System & Preferences
    private int animSpeedIndex = 0;
    private static final String[] ANIM_SPEEDS = {"Smooth", "Snappy", "Instant"};

    public BuildScapeConfigScreen(Screen parent) {
        this.parent = parent;
        this.title = ComponentHelper.translatable("buildscape.config.title");

        this.openStartTime = System.currentTimeMillis();
        this.lastFrameTime = System.currentTimeMillis();

        initNavItems();
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem("pillars", "◆", "Pillars & Joints", "Multi-block beam connection logic", 8));
        navItems.add(new NavItem("particles", "✦", "Particle FX", "Ambient kinetic trajectory solver", 12));
        navItems.add(new NavItem("world", "⚙", "World Mechanics", "Block physics and gameplay behaviors", 10));
        navItems.add(new NavItem("identifiers", "▲", "Pillar IDs & Bounds", "Voxel grid placement auto-snapping", 6));
        navItems.add(new NavItem("system", "ℹ", "Engine & System", "Platform specifications and performance", 16));
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

    // --- Screen Animation Handling ---
    private float updateAnimations() {
        long now = System.currentTimeMillis();
        lastFrameTime = now;

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

    // --- Main Rendering ---
    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        float alpha = updateAnimations();
        if (alpha <= 0.001f) return;

        Font font = Minecraft.getInstance().font;

        // 1. Full-screen dimmed backdrop
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(COLOR_OVERLAY, alpha));

        // 2. Main Window Geometry (Vault Hunters Style 2-Panel Split)
        int totalW = Math.min(width - 40, 560);
        int totalH = Math.min(height - 24, 252);
        int leftStripW = 24;
        int panelGap = 5;

        int mainX = (width - totalW) / 2 + leftStripW / 2;
        int mainY = (height - totalH) / 2;

        int leftPanelW = (int) ((totalW - leftStripW - panelGap) * 0.58);
        int rightPanelW = (totalW - leftStripW - panelGap) - leftPanelW;

        int leftPanelX = mainX;
        int leftPanelY = mainY;
        int rightPanelX = leftPanelX + leftPanelW + panelGap;
        int rightPanelY = mainY;

        // Slide-in vertical offset
        int offsetY = Math.round((1.0f - alpha) * 8.0f);
        leftPanelY += offsetY;
        rightPanelY += offsetY;

        // 3. Render Outer Left Tab Strip (Attached to the left panel border)
        renderOuterTabStrip(poseStackOrGraphics, font, leftPanelX - leftStripW + 1, leftPanelY + 6, leftStripW, mouseX, mouseY, alpha);

        // 4. Render Left Master Panel (Card List)
        renderLeftMasterPanel(poseStackOrGraphics, font, leftPanelX, leftPanelY, leftPanelW, totalH, mouseX, mouseY, alpha);

        // 5. Render Right Detail Panel (Parchment Card + Modifiers)
        renderRightDetailPanel(poseStackOrGraphics, font, rightPanelX, rightPanelY, rightPanelW, totalH, mouseX, mouseY, alpha);

        // 6. Render Legacy Content (if attached)
        if (activeTab != null) {
            activeTab.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
            activeTab.renderTooltips(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
    }

    // --- Left Outer Tab Strip ---
    private void renderOuterTabStrip(Object poseStackOrGraphics, Font font, int x, int startY, int stripW, int mouseX, int mouseY, float alpha) {
        int tabH = 22;
        int tabGap = 3;

        String hoveredTooltip = null;

        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int tabY = startY + i * (tabH + tabGap);
            boolean isSelected = (i == selectedTabIndex);
            boolean hover = mouseX >= x && mouseX <= x + stripW && mouseY >= tabY && mouseY <= tabY + tabH;

            // Tab background & border
            int bg = isSelected ? COLOR_PANEL_BG : (hover ? 0xFF24242A : 0xFF121214);
            Services.PLATFORM.fill(poseStackOrGraphics, x, tabY, x + stripW, tabY + tabH, applyAlpha(bg, alpha));

            // Outer border: Black
            int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
            Services.PLATFORM.fill(poseStackOrGraphics, x, tabY, x + stripW, tabY + 1, black);           // Top
            Services.PLATFORM.fill(poseStackOrGraphics, x, tabY + tabH - 1, x + stripW, tabY + tabH, black); // Bottom
            Services.PLATFORM.fill(poseStackOrGraphics, x, tabY, x + 1, tabY + tabH, black);           // Left

            // If not selected: draw right border separating tab from panel
            if (!isSelected) {
                Services.PLATFORM.fill(poseStackOrGraphics, x + stripW - 1, tabY, x + stripW, tabY + tabH, black);
            }

            // Bevel highlight
            int bevel = applyAlpha(isSelected ? 0xFF666670 : (hover ? 0xFF4A4A52 : 0xFF222226), alpha);
            Services.PLATFORM.fill(poseStackOrGraphics, x + 1, tabY + 1, x + stripW - 1, tabY + 2, bevel);
            Services.PLATFORM.fill(poseStackOrGraphics, x + 1, tabY + 1, x + 2, tabY + tabH - 1, bevel);

            // Icon
            int iconColor = isSelected ? COLOR_TEXT_WHITE : (hover ? 0xFFE0E0E0 : COLOR_TEXT_MUTED);
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, item.icon, x + stripW / 2 - 1, tabY + 7, applyAlpha(iconColor, alpha));

            if (hover) {
                hoveredTooltip = item.title;
            }
        }

        if (hoveredTooltip != null) {
            Services.PLATFORM.renderComponentTooltip(
                    poseStackOrGraphics, font,
                    Collections.singletonList(ComponentHelper.literal(hoveredTooltip)),
                    mouseX + 12, mouseY
            );
        }
    }

    // --- Left Master Panel ---
    private void renderLeftMasterPanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        // Vault Hunters beveled outer frame
        drawVaultHuntersFrame(poseStackOrGraphics, x, y, w, h, COLOR_PANEL_BG, alpha);

        // Header Section (Title + Search Box)
        int headerH = 26;
        Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + 2, x + w - 2, y + headerH, applyAlpha(COLOR_HEADER_BG, alpha));
        drawGroove(poseStackOrGraphics, x + 2, y + headerH, w - 4, true, alpha);

        // Header Title: Icon + Title + Count Badge
        drawText(poseStackOrGraphics, font, "⚙ Buildscape", x + 8, y + 8, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "x" + navItems.size(), x + 76, y + 8, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // Inset Search Input Box (Vault Hunters style)
        int searchW = 100;
        int searchH = 16;
        int searchX = x + w - searchW - 24;
        int searchY = y + 5;
        drawInsetBox(poseStackOrGraphics, searchX, searchY, searchW, searchH, COLOR_INNER_DARK, alpha);
        drawText(poseStackOrGraphics, font, "config...", searchX + 6, searchY + 4, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // Help '?' button
        int helpX = x + w - 18;
        int helpY = y + 5;
        drawText(poseStackOrGraphics, font, "?", helpX + 3, helpY + 4, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // Master Card List (Stack of Companion-style cards)
        int cardStartY = y + headerH + 5;
        int cardH = 40;
        int cardGap = 3;
        int cardW = w - 10;
        int cardX = x + 5;

        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int cardY = cardStartY + i * (cardH + cardGap);
            boolean isSelected = (i == selectedTabIndex);
            boolean hover = mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + cardH;

            renderMasterCard(poseStackOrGraphics, font, cardX, cardY, cardW, cardH, item, isSelected, hover, alpha);
        }
    }

    // --- Master Card (Companion Row in reference image) ---
    private void renderMasterCard(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, NavItem item, boolean isSelected, boolean hover, float alpha) {
        if (isSelected) {
            // Active card: Horizontal caramel/bronze gradient fading into dark charcoal
            drawHorizontalGradient(poseStackOrGraphics, x, y, (int) (w * 0.45), h, COLOR_ACTIVE_BRONZE_GRADIENT, 0xFF1C1C20, alpha);
            Services.PLATFORM.fill(poseStackOrGraphics, x + (int) (w * 0.45), y, x + w, y + h, applyAlpha(0xFF1C1C20, alpha));

            // Vibrant green active border (from reference image)
            drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(COLOR_ACTIVE_GREEN_BORDER, alpha));
        } else {
            // Inactive card: Dark charcoal with subtle border
            int bg = hover ? 0xFF222228 : 0xFF19191D;
            Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

            int border = hover ? 0xFF4A4A54 : 0xFF26262C;
            drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(border, alpha));
        }

        // Left Inset Avatar / Icon Box (30x30 square)
        int boxSize = 30;
        int boxX = x + 5;
        int boxY = y + (h - boxSize) / 2;
        drawInsetBox(poseStackOrGraphics, boxX, boxY, boxSize, boxSize, 0xFF101012, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, item.icon, boxX + boxSize / 2, boxY + 11, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Title in bold white
        drawText(poseStackOrGraphics, font, item.title, boxX + boxSize + 8, y + 8, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Subtitle / level indicator
        drawText(poseStackOrGraphics, font, "LVL " + item.level, boxX + boxSize + 8, y + 22, applyAlpha(COLOR_GOLD_BAR, alpha));

        // Segmented Gold Progress Bar (Vault Hunters style)
        int barX = boxX + boxSize + 44;
        int barY = y + 23;
        int segments = 8;
        int segW = 6;
        int segH = 5;
        for (int s = 0; s < segments; s++) {
            boolean filled = s < (item.level % 8 + 3);
            int segCol = filled ? COLOR_GOLD_BAR : 0xFF2C2414;
            Services.PLATFORM.fill(poseStackOrGraphics, barX + s * (segW + 1), barY, barX + s * (segW + 1) + segW, barY + segH, applyAlpha(segCol, alpha));
            drawBorder(poseStackOrGraphics, barX + s * (segW + 1), barY, segW, segH, applyAlpha(COLOR_FRAME_BLACK, alpha));
        }

        // Right-side metrics (e.g. status badge)
        String metric = switch (item.id) {
            case "pillars" -> "★ 14";
            case "particles" -> "✨ " + PARTICLE_PATTERNS[particlePatternIndex];
            case "world" -> "⚙ OK";
            case "identifiers" -> "● SYNC";
            case "system" -> "26.2";
            default -> "●";
        };
        int metricW = font.width(metric);
        drawText(poseStackOrGraphics, font, metric, x + w - metricW - 10, y + 15, applyAlpha(COLOR_TEXT_SILVER, alpha));
    }

    // --- Right Detail Panel ---
    private void renderRightDetailPanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        // Vault Hunters beveled outer frame
        drawVaultHuntersFrame(poseStackOrGraphics, x, y, w, h, COLOR_PANEL_BG, alpha);

        if (selectedTabIndex < 0 || selectedTabIndex >= navItems.size()) return;
        NavItem current = navItems.get(selectedTabIndex);

        // 1. Top Parchment Summary Card (Exact recreation of reference character card)
        int parchmentH = 88;
        int parchmentW = w - 8;
        int parchmentX = x + 4;
        int parchmentY = y + 4;

        renderParchmentCard(poseStackOrGraphics, font, parchmentX, parchmentY, parchmentW, parchmentH, current, alpha);

        // Close button [ ✕ ] in top-right of parchment card
        int closeBtnSize = 14;
        int closeBtnX = parchmentX + parchmentW - closeBtnSize - 4;
        int closeBtnY = parchmentY + 4;
        boolean hoverClose = mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize;
        Services.PLATFORM.fill(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnX + closeBtnSize, closeBtnY + closeBtnSize, applyAlpha(hoverClose ? 0x40000000 : 0x20000000, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeBtnX + closeBtnSize / 2, closeBtnY + 3, applyAlpha(COLOR_TEXT_PARCHMENT_DARK, alpha));

        // 2. Bottom Section: Modifiers & Real Configuration Viewport
        int controlsY = parchmentY + parchmentH + 4;
        int controlsH = h - (parchmentH + 12);
        int controlsW = w - 8;
        int controlsX = x + 4;

        renderControlsSection(poseStackOrGraphics, font, controlsX, controlsY, controlsW, controlsH, current, mouseX, mouseY, alpha);
    }

    // --- Top Parchment Card (Vault Hunters Character Card) ---
    private void renderParchmentCard(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, NavItem current, float alpha) {
        // Parchment vertical gradient (cream / tan)
        drawVerticalGradient(poseStackOrGraphics, x, y, w, h, COLOR_PARCHMENT_TOP, COLOR_PARCHMENT_BOT, alpha);
        // Outer dark border
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(COLOR_PARCHMENT_BORDER, alpha));

        // Left Avatar Slot (24x24)
        int slotSize = 24;
        int slotX = x + 6;
        int slotY = y + 6;
        drawInsetBox(poseStackOrGraphics, slotX, slotY, slotSize, slotSize, 0xFF241A0E, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, current.icon, slotX + slotSize / 2, slotY + 8, applyAlpha(COLOR_PARCHMENT_TOP, alpha));

        // Title in dark bold parchment text
        drawText(poseStackOrGraphics, font, current.title.toUpperCase(), slotX + slotSize + 6, y + 6, applyAlpha(COLOR_TEXT_PARCHMENT_DARK, alpha));

        // Level & Segmented Progress Bar
        drawText(poseStackOrGraphics, font, "LVL " + current.level, slotX + slotSize + 6, y + 17, applyAlpha(COLOR_TEXT_PARCHMENT_GOLD, alpha));

        int barX = slotX + slotSize + 40;
        int barY = y + 17;
        int segments = 12;
        int segW = 5;
        int segH = 5;
        for (int s = 0; s < segments; s++) {
            boolean filled = s < (current.level % 12 + 4);
            int segCol = filled ? COLOR_GOLD_BAR : 0xFF7A6A50;
            Services.PLATFORM.fill(poseStackOrGraphics, barX + s * (segW + 1), barY, barX + s * (segW + 1) + segW, barY + segH, applyAlpha(segCol, alpha));
            drawBorder(poseStackOrGraphics, barX + s * (segW + 1), barY, segW, segH, applyAlpha(COLOR_FRAME_BLACK, alpha));
        }

        // Stats Lines in dark parchment text (from reference image)
        int statY = y + 32;
        int lineGap = 12;

        switch (current.id) {
            case "pillars" -> {
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY, "Joint Radius", pillarJointRadius + " Blocks", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap, "Linking Mode", PILLAR_LINKING_MODES[pillarLinkingModeIndex], alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 2, "Glow Shader", pillarGlowEnabled ? "Active" : "Disabled", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 3, "Registry Models", "14 Synched", alpha);
            }
            case "particles" -> {
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY, "Trajectory", PARTICLE_PATTERNS[particlePatternIndex], alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap, "Density Budget", PARTICLE_DENSITIES[particleDensityIndex], alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 2, "Core Sparks", pillarSparksEnabled ? "Active" : "Muted", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 3, "Accent Slot", MONO_SWATCH_NAMES[selectedSwatchIndex], alpha);
            }
            case "world" -> {
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY, "Tree Breaker", creativeTreeBreaker ? "Creative" : "Vanilla", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap, "Shulker Peek", shulkerPreview ? "Enabled" : "Disabled", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 2, "Leaf Decay", fastLeafDecay ? "Accelerated" : "Vanilla", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 3, "Mob Griefing", mobGriefingDefense ? "Protected" : "Default", alpha);
            }
            case "identifiers" -> {
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY, "Placement Snap", autoSnappingEnabled ? "Auto" : "Manual", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap, "Bounds Check", strictBoundsCheck ? "Strict" : "Permissive", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 2, "Model Mapping", "Synchronized", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 3, "Voxel Collision", "Enforced", alpha);
            }
            case "system" -> {
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY, "Cluster Target", "v26x (26.2)", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap, "Engine Loader", "NeoForge 26.2", alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 2, "Animation", ANIM_SPEEDS[animSpeedIndex], alpha);
                drawParchmentStatLine(poseStackOrGraphics, font, x + 6, statY + lineGap * 3, "Common Neutral", "Active", alpha);
            }
        }
    }

    private void drawParchmentStatLine(Object poseStackOrGraphics, Font font, int x, int y, String label, String value, float alpha) {
        drawText(poseStackOrGraphics, font, label, x, y, applyAlpha(COLOR_TEXT_PARCHMENT_DARK, alpha));
        drawText(poseStackOrGraphics, font, value, x + 105, y, applyAlpha(COLOR_TEXT_PARCHMENT_DARK, alpha));
    }

    // --- Bottom Controls Section (Modifiers Viewport) ---
    private void renderControlsSection(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, NavItem current, int mouseX, int mouseY, float alpha) {
        // Dark inset container
        drawInsetBox(poseStackOrGraphics, x, y, w, h, 0xFF121214, alpha);

        // Section Title: "Modifiers" (from reference image)
        drawText(poseStackOrGraphics, font, "Modifiers & Options", x + 8, y + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int curY = y + 20;
        int rowH = 24;

        // Configuration Control Rows
        switch (current.id) {
            case "pillars" -> {
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Outline Shader", pillarGlowEnabled, mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowCounter(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Joint Radius", pillarJointRadius + " blk", mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowCycle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Joint Linking", PILLAR_LINKING_MODES[pillarLinkingModeIndex], mouseX, mouseY, alpha);
            }
            case "particles" -> {
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Pillar Sparks", pillarSparksEnabled, mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowCycle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Trajectory Pattern", PARTICLE_PATTERNS[particlePatternIndex], mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowCycle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Density Budget", PARTICLE_DENSITIES[particleDensityIndex], mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowSwatches(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, mouseX, mouseY, alpha);
            }
            case "world" -> {
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Creative Tree Breaker", creativeTreeBreaker, mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Shulker Box Peek", shulkerPreview, mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Fast Leaf Decay", fastLeafDecay, mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Mob Griefing Defense", mobGriefingDefense, mouseX, mouseY, alpha);
            }
            case "identifiers" -> {
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Auto-Snapping Bounds", autoSnappingEnabled, mouseX, mouseY, alpha);
                curY += rowH + 3;
                renderConfigRowToggle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Strict Collision Check", strictBoundsCheck, mouseX, mouseY, alpha);
            }
            case "system" -> {
                renderConfigRowCycle(poseStackOrGraphics, font, x + 6, curY, w - 12, rowH, "Animation Timing", ANIM_SPEEDS[animSpeedIndex], mouseX, mouseY, alpha);
            }
        }

        // Vault Hunters Badges Row at bottom
        int badgesY = y + h - 26;
        drawText(poseStackOrGraphics, font, "Badges", x + 8, badgesY - 11, applyAlpha(COLOR_TEXT_WHITE, alpha));
        for (int b = 0; b < 4; b++) {
            int badgeX = x + 8 + b * 22;
            String icon = switch (b) {
                case 0 -> "★";
                case 1 -> "🏆";
                case 2 -> "🛡";
                default -> "⚡";
            };
            drawInsetBox(poseStackOrGraphics, badgeX, badgesY, 18, 18, 0xFF1C1C22, alpha);
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, icon, badgeX + 9, badgesY + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
        }

        // Right Scrollbar Track & Thumb (Vault Hunters style)
        int scrollX = x + w - 7;
        int scrollY = y + 2;
        int scrollH = h - 4;
        Services.PLATFORM.fill(poseStackOrGraphics, scrollX, scrollY, scrollX + 5, scrollY + scrollH, applyAlpha(0xFF0C0C0E, alpha));
        drawBorder(poseStackOrGraphics, scrollX, scrollY, 5, scrollH, applyAlpha(COLOR_FRAME_BLACK, alpha));

        // Slider thumb
        int thumbY = scrollY + 4;
        int thumbH = Math.max(20, scrollH / 3);
        Services.PLATFORM.fill(poseStackOrGraphics, scrollX + 1, thumbY, scrollX + 4, thumbY + thumbH, applyAlpha(0xFF888890, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, scrollX + 1, thumbY, scrollX + 4, thumbY + 1, applyAlpha(0xFFD0D0D8, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, scrollX + 1, thumbY + thumbH - 1, scrollX + 4, thumbY + thumbH, applyAlpha(0xFF404048, alpha));
    }

    // --- Row Controls ---
    private void renderConfigRowToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String label, boolean state, int mouseX, int mouseY, float alpha) {
        drawText(poseStackOrGraphics, font, label, x + 4, y + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int btnW = 44;
        int btnH = 16;
        int btnX = x + w - btnW - 12;
        int btnY = y + 4;

        boolean hover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int bg = state ? 0xFF36363E : 0xFF18181C;
        if (hover) bg = state ? 0xFF44444E : 0xFF24242A;

        Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, applyAlpha(bg, alpha));
        int border = state ? COLOR_BORDER_GRADIENT_START : 0xFF303036;
        drawBorder(poseStackOrGraphics, btnX, btnY, btnW, btnH, applyAlpha(border, alpha));

        String text = state ? "ON" : "OFF";
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, btnX + btnW / 2, btnY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private void renderConfigRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String label, String valueText, int mouseX, int mouseY, float alpha) {
        drawText(poseStackOrGraphics, font, label, x + 4, y + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int btnW = 14;
        int counterW = 66;
        int counterH = 16;
        int counterX = x + w - counterW - 12;
        int counterY = y + 4;

        // Minus
        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + btnW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawButtonTile(poseStackOrGraphics, counterX, counterY, btnW, counterH, hoverMinus, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "-", counterX + btnW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Center Box
        int centerW = counterW - btnW * 2;
        drawInsetBox(poseStackOrGraphics, counterX + btnW, counterY, centerW, counterH, 0xFF101012, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, valueText, counterX + btnW + centerW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Plus
        int plusX = counterX + counterW - btnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + btnW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawButtonTile(poseStackOrGraphics, plusX, counterY, btnW, counterH, hoverPlus, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+", plusX + btnW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private void renderConfigRowCycle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String label, String valueText, int mouseX, int mouseY, float alpha) {
        drawText(poseStackOrGraphics, font, label, x + 4, y + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int btnW = 66;
        int btnH = 16;
        int btnX = x + w - btnW - 12;
        int btnY = y + 4;

        boolean hover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        drawButtonTile(poseStackOrGraphics, btnX, btnY, btnW, btnH, hover, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, valueText, btnX + btnW / 2, btnY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private void renderConfigRowSwatches(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        drawText(poseStackOrGraphics, font, "Accent Tint", x + 4, y + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int totalSw = 4 * 14 + 3 * 3;
        int swX = x + w - totalSw - 12;
        int swY = y + 4;

        for (int i = 0; i < MONO_SWATCH_COLORS.length; i++) {
            int itemX = swX + i * 17;
            boolean isSelected = (i == selectedSwatchIndex);
            boolean hover = mouseX >= itemX && mouseX <= itemX + 14 && mouseY >= swY && mouseY <= swY + 14;

            int border = isSelected ? COLOR_TEXT_WHITE : (hover ? COLOR_BORDER_GRADIENT_START : COLOR_FRAME_BLACK);
            drawBorder(poseStackOrGraphics, itemX - 1, swY - 1, 16, 16, applyAlpha(border, alpha));
            Services.PLATFORM.fill(poseStackOrGraphics, itemX, swY, itemX + 14, swY + 14, applyAlpha(MONO_SWATCH_COLORS[i], alpha));
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

    private void drawButtonTile(Object poseStackOrGraphics, int x, int y, int w, int h, boolean hover, float alpha) {
        int bg = hover ? 0xFF303038 : 0xFF202026;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));
        int border = hover ? COLOR_BORDER_GRADIENT_START : 0xFF36363E;
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(border, alpha));
    }

    private void drawHorizontalGradient(Object poseStackOrGraphics, int x, int y, int w, int h, int c1, int c2, float alpha) {
        for (int i = 0; i < w; i++) {
            float t = (float) i / Math.max(1, w - 1);
            int col = lerpColor(c1, c2, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + i, y, x + i + 1, y + h, applyAlpha(col, alpha));
        }
    }

    private void drawVerticalGradient(Object poseStackOrGraphics, int x, int y, int w, int h, int c1, int c2, float alpha) {
        for (int j = 0; j < h; j++) {
            float t = (float) j / Math.max(1, h - 1);
            int col = lerpColor(c1, c2, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x, y + j, x + w, y + j + 1, applyAlpha(col, alpha));
        }
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

        int totalW = Math.min(width - 40, 560);
        int totalH = Math.min(height - 24, 252);
        int leftStripW = 24;
        int panelGap = 5;

        int mainX = (width - totalW) / 2 + leftStripW / 2;
        int mainY = (height - totalH) / 2;

        int leftPanelW = (int) ((totalW - leftStripW - panelGap) * 0.58);
        int rightPanelW = (totalW - leftStripW - panelGap) - leftPanelW;

        int leftPanelX = mainX;
        int leftPanelY = mainY;
        int rightPanelX = leftPanelX + leftPanelW + panelGap;
        int rightPanelY = mainY;

        // 1. Check Close button [ ✕ ] on right panel
        int parchmentW = rightPanelW - 8;
        int closeBtnSize = 14;
        int closeBtnX = (rightPanelX + 4) + parchmentW - closeBtnSize - 4;
        int closeBtnY = (rightPanelY + 4) + 4;
        if (mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize) {
            startClosing();
            return true;
        }

        // 2. Check Outer Left Tab Strip
        int tabH = 22;
        int tabGap = 3;
        int tabStripX = leftPanelX - leftStripW + 1;
        int tabStripY = leftPanelY + 6;
        for (int i = 0; i < navItems.size(); i++) {
            int tabY = tabStripY + i * (tabH + tabGap);
            if (mouseX >= tabStripX && mouseX <= tabStripX + leftStripW && mouseY >= tabY && mouseY <= tabY + tabH) {
                selectedTabIndex = i;
                return true;
            }
        }

        // 3. Check Left Master Card List
        int cardStartY = leftPanelY + 26 + 5;
        int cardH = 40;
        int cardW = leftPanelW - 10;
        int cardX = leftPanelX + 5;
        for (int i = 0; i < navItems.size(); i++) {
            int cardY = cardStartY + i * (cardH + 3);
            if (mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + cardH) {
                selectedTabIndex = i;
                return true;
            }
        }

        // 4. Check Right Panel Controls
        int parchmentH = 88;
        int controlsY = (rightPanelY + 4) + parchmentH + 4;
        int controlsW = rightPanelW - 8;
        int controlsX = rightPanelX + 4;
        int curY = controlsY + 20;
        int rowH = 24;

        NavItem current = navItems.get(selectedTabIndex);
        if ("pillars".equals(current.id)) {
            // Row 1: Toggle
            int btnW = 44;
            int btnH = 16;
            int btnX = (controlsX + 6) + (controlsW - 12) - btnW - 12;
            int btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                pillarGlowEnabled = !pillarGlowEnabled;
                return true;
            }
            curY += rowH + 3;

            // Row 2: Counter
            int counterW = 66;
            int counterX = (controlsX + 6) + (controlsW - 12) - counterW - 12;
            int counterY = curY + 4;
            int stepBtnW = 14;
            if (mouseY >= counterY && mouseY <= counterY + 16) {
                if (mouseX >= counterX && mouseX <= counterX + stepBtnW) {
                    pillarJointRadius = Math.max(1, pillarJointRadius - 1);
                    return true;
                } else if (mouseX >= counterX + counterW - stepBtnW && mouseX <= counterX + counterW) {
                    pillarJointRadius = Math.min(32, pillarJointRadius + 1);
                    return true;
                }
            }
            curY += rowH + 3;

            // Row 3: Cycle
            int cycleBtnW = 66;
            int cycleBtnX = (controlsX + 6) + (controlsW - 12) - cycleBtnW - 12;
            int cycleBtnY = curY + 4;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + 16) {
                pillarLinkingModeIndex = (pillarLinkingModeIndex + 1) % PILLAR_LINKING_MODES.length;
                return true;
            }
        } else if ("particles".equals(current.id)) {
            // Row 1: Toggle
            int btnW = 44;
            int btnX = (controlsX + 6) + (controlsW - 12) - btnW - 12;
            int btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
                pillarSparksEnabled = !pillarSparksEnabled;
                return true;
            }
            curY += rowH + 3;

            // Row 2: Pattern Cycle
            int cycleBtnW = 66;
            int cycleBtnX = (controlsX + 6) + (controlsW - 12) - cycleBtnW - 12;
            int cycleBtnY = curY + 4;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + 16) {
                particlePatternIndex = (particlePatternIndex + 1) % PARTICLE_PATTERNS.length;
                return true;
            }
            curY += rowH + 3;

            // Row 3: Density Cycle
            cycleBtnY = curY + 4;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + 16) {
                particleDensityIndex = (particleDensityIndex + 1) % PARTICLE_DENSITIES.length;
                return true;
            }
            curY += rowH + 3;

            // Row 4: Swatches
            int totalSw = 4 * 14 + 3 * 3;
            int swX = (controlsX + 6) + (controlsW - 12) - totalSw - 12;
            int swY = curY + 4;
            for (int s = 0; s < MONO_SWATCH_COLORS.length; s++) {
                int itemX = swX + s * 17;
                if (mouseX >= itemX && mouseX <= itemX + 14 && mouseY >= swY && mouseY <= swY + 14) {
                    selectedSwatchIndex = s;
                    return true;
                }
            }
        } else if ("world".equals(current.id)) {
            int btnW = 44;
            int btnX = (controlsX + 6) + (controlsW - 12) - btnW - 12;

            // Row 1: Tree Breaker
            int btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
                creativeTreeBreaker = !creativeTreeBreaker;
                return true;
            }
            curY += rowH + 3;

            // Row 2: Shulker Preview
            btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
                shulkerPreview = !shulkerPreview;
                return true;
            }
            curY += rowH + 3;

            // Row 3: Fast Leaf Decay
            btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
                fastLeafDecay = !fastLeafDecay;
                return true;
            }
            curY += rowH + 3;

            // Row 4: Mob Griefing Defense
            btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
                mobGriefingDefense = !mobGriefingDefense;
                return true;
            }
        } else if ("identifiers".equals(current.id)) {
            int btnW = 44;
            int btnX = (controlsX + 6) + (controlsW - 12) - btnW - 12;
            int btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
                autoSnappingEnabled = !autoSnappingEnabled;
                return true;
            }
            curY += rowH + 3;

            btnY = curY + 4;
            if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
                strictBoundsCheck = !strictBoundsCheck;
                return true;
            }
        } else if ("system".equals(current.id)) {
            int cycleBtnW = 66;
            int cycleBtnX = (controlsX + 6) + (controlsW - 12) - cycleBtnW - 12;
            int cycleBtnY = curY + 4;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + 16) {
                animSpeedIndex = (animSpeedIndex + 1) % ANIM_SPEEDS.length;
                return true;
            }
        }

        if (activeTab != null && activeTab.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return false;
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
        int totalW = Math.min(width - 40, 560);
        int leftStripW = 24;
        int panelGap = 5;
        int mainX = (width - totalW) / 2 + leftStripW / 2;
        int leftPanelW = (int) ((totalW - leftStripW - panelGap) * 0.58);
        return mainX + leftPanelW + panelGap;
    }

    public int getContentY() {
        int totalH = Math.min(height - 24, 252);
        int mainY = (height - totalH) / 2;
        return mainY + 28;
    }

    public int getContentWidth() {
        int totalW = Math.min(width - 40, 560);
        int leftStripW = 24;
        int panelGap = 5;
        int leftPanelW = (int) ((totalW - leftStripW - panelGap) * 0.58);
        return (totalW - leftStripW - panelGap) - leftPanelW;
    }

    public int getContentHeight() {
        int totalH = Math.min(height - 24, 252);
        return totalH - 28;
    }

    public int getSidebarWidth() {
        int totalW = Math.min(width - 40, 560);
        int leftStripW = 24;
        int panelGap = 5;
        return (int) ((totalW - leftStripW - panelGap) * 0.58);
    }

    public static int scaleSize(int baseSize) { return baseSize; }
    public static int getScaledButtonHeight() { return 20; }
    public static int getScaledEditBoxHeight() { return 18; }
    public static float getStandardTextScale() { return 1.0f; }
}
