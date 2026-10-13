package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modern animated configuration screen for Buildscape.
 * Features:
 * - Opening and closing cubic-eased fade & scale transitions.
 * - Collapsible / expandable sidenav (icons-only vs full labels) with smooth width interpolation.
 * - 75% opacity dark gray color palette (0xBF...).
 * - Responsive content panel dynamically adjusting to sidebar width.
 * - Interactive test tabs (Pillars, Particles, Identifiers, Settings, About).
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
    private static final int OPEN_DURATION_MS = 240;
    private static final int CLOSE_DURATION_MS = 180;
    private static final int NAV_DURATION_MS = 160;

    private long openStartTime;
    private long closeStartTime = -1;
    private long lastFrameTime;

    private boolean isOpening = true;
    private boolean isClosing = false;
    private float openProgress = 0.0f;
    private float closeProgress = 1.0f;

    // --- Sidenav Configuration & Animation ---
    public static final int COLLAPSED_NAV_WIDTH = 44;
    public static final int EXPANDED_NAV_WIDTH = 152;
    private boolean isNavExpanded = false;
    private float navProgress = 0.0f; // 0.0 = collapsed, 1.0 = expanded

    // --- Colors (75% opacity gray palette: 0xBF...) ---
    public static final int COLOR_OVERLAY = 0xBF101012;
    public static final int COLOR_CONTAINER_BG = 0xBF1E1E22;
    public static final int COLOR_CONTAINER_BORDER = 0x35FFFFFF;
    public static final int COLOR_HEADER_BG = 0xBF25252A;
    public static final int COLOR_SIDENAV_BG = 0xBF242428;
    public static final int COLOR_CONTENT_BG = 0xBF18181B;
    public static final int COLOR_CARD_BG = 0xBF2B2B30;
    public static final int COLOR_CARD_BORDER = 0x22FFFFFF;
    public static final int COLOR_DIVIDER = 0x1FFFFFFF;
    public static final int COLOR_ACCENT = 0xFF4A80E8;
    public static final int COLOR_TEXT_PRIMARY = 0xFFFFFFFF;
    public static final int COLOR_TEXT_SECONDARY = 0xFFAAAAAA;
    public static final int COLOR_TEXT_MUTED = 0xFF707075;

    // --- Navigation Tabs ---
    public static class NavItem {
        public final String id;
        public final String icon;
        public final String label;
        public final String description;

        public NavItem(String id, String icon, String label, String description) {
            this.id = id;
            this.icon = icon;
            this.label = label;
            this.description = description;
        }
    }

    private final List<NavItem> navItems = new ArrayList<>();
    private int selectedTabIndex = 0;

    // Legacy tab support
    private AbstractConfigTab activeTab;
    private final List<GuiEventListener> widgets = new ArrayList<>();
    private final List<GuiEventListener> tabWidgets = new ArrayList<>();
    private Button closeButton;

    // --- Interactive Test State Variables ---
    private boolean testPillarHighlight = true;
    private int testPillarDistance = 6;
    private int testPillarModeIndex = 0;
    private static final String[] PILLAR_MODES = {"Adaptive", "Strict", "All Blocks"};

    private boolean testParticleEnabled = true;
    private int testParticleDensityIndex = 1;
    private static final String[] PARTICLE_DENSITIES = {"Low (25%)", "Medium (50%)", "High (100%)", "Ultra (200%)"};
    private int testSelectedColorIndex = 0;
    private static final int[] TEST_SWATCH_COLORS = {0xFF388E3C, 0xFFFFA000, 0xFF7B1FA2, 0xFF1976D2};
    private static final String[] TEST_SWATCH_NAMES = {"Emerald", "Amber", "Amethyst", "Azure"};

    private boolean testAutoSnapping = true;
    private int testAnimSpeedIndex = 0;
    private static final String[] ANIM_SPEEDS = {"Smooth (Normal)", "Snappy (Fast)", "Instant (Off)"};

    public BuildScapeConfigScreen(Screen parent) {
        this.parent = parent;
        this.title = ComponentHelper.translatable("buildscape.config.title");

        this.openStartTime = System.currentTimeMillis();
        this.lastFrameTime = System.currentTimeMillis();

        initNavItems();
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem("pillars", "◆", "Pillars", "Structural pillar connections and visual rendering."));
        navItems.add(new NavItem("particles", "✦", "Particles", "Particle effects, density, and ambient emission."));
        navItems.add(new NavItem("identifiers", "▲", "Pillar IDs", "Block placement bounds and registry identifiers."));
        navItems.add(new NavItem("settings", "⚙", "Settings", "Interface preferences, animation speed, and defaults."));
        navItems.add(new NavItem("about", "ℹ", "About", "Buildscape version specifications and architecture."));
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

        // Native bottom Done button
        int btnW = 100;
        int btnH = 20;
        int btnX = width - btnW - 24;
        int btnY = height - btnH - 14;

        closeButton = Services.PLATFORM.createButton(
                btnX, btnY, btnW, btnH,
                ComponentHelper.translatable("gui.done"),
                (btn) -> startClosing()
        );
        addManagedWidget(closeButton);

        if (activeTab != null) {
            activeTab.init();
        }
    }

    // --- Screen Animation Handling ---
    private float updateAnimations() {
        long now = System.currentTimeMillis();
        float deltaSec = Math.max(0.001f, (now - lastFrameTime) / 1000.0f);
        lastFrameTime = now;

        // Opening animation
        if (isOpening) {
            float elapsed = (now - openStartTime) / (float) OPEN_DURATION_MS;
            if (elapsed >= 1.0f) {
                openProgress = 1.0f;
                isOpening = false;
            } else {
                openProgress = easeOutCubic(elapsed);
            }
        }

        // Closing animation
        if (isClosing) {
            float elapsed = (now - closeStartTime) / (float) CLOSE_DURATION_MS;
            if (elapsed >= 1.0f) {
                closeProgress = 0.0f;
                isClosing = false;
                // Transition out to parent
                Services.PLATFORM.openScreen(parent);
                return 0.0f;
            } else {
                closeProgress = 1.0f - easeInCubic(elapsed);
            }
        }

        // Sidenav width animation (frame-rate independent lerp)
        float targetNav = isNavExpanded ? 1.0f : 0.0f;
        float navDelta = deltaSec * (1000.0f / NAV_DURATION_MS);
        if (navProgress < targetNav) {
            navProgress = Math.min(targetNav, navProgress + navDelta);
        } else if (navProgress > targetNav) {
            navProgress = Math.max(targetNav, navProgress - navDelta);
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

    public void toggleNav() {
        this.isNavExpanded = !this.isNavExpanded;
    }

    // --- Color Alpha Adjustment Helper ---
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

        // 1. Full-screen dimmed backdrop (75% opacity)
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(COLOR_OVERLAY, alpha));

        // 2. Compute main dialog window dimensions
        int marginX = Math.max(16, (int) (width * 0.04));
        int marginY = Math.max(14, (int) (height * 0.05));
        int winX = marginX;
        int winY = marginY;
        int winW = width - marginX * 2;
        int winH = height - marginY * 2;

        // Subtle slide-in effect during open/close
        int offsetY = Math.round((1.0f - alpha) * 12.0f);
        winY += offsetY;

        // 3. Main container background and border
        Services.PLATFORM.fill(poseStackOrGraphics, winX, winY, winX + winW, winY + winH, applyAlpha(COLOR_CONTAINER_BG, alpha));
        drawBorder(poseStackOrGraphics, winX, winY, winW, winH, applyAlpha(COLOR_CONTAINER_BORDER, alpha));

        // 4. Header Bar
        int headerH = 34;
        Services.PLATFORM.fill(poseStackOrGraphics, winX, winY, winX + winW, winY + headerH, applyAlpha(COLOR_HEADER_BG, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, winX, winY + headerH - 1, winX + winW, winY + headerH, applyAlpha(COLOR_DIVIDER, alpha));

        // Header Title & Version Badge
        String titleStr = "BUILDSCAPE";
        drawText(poseStackOrGraphics, font, titleStr, winX + 14, winY + 12, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        int titleW = font.width(titleStr);
        drawText(poseStackOrGraphics, font, "v4.0.3", winX + 18 + titleW, winY + 12, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // Header Top-Right Close Button [ ✕ ]
        int closeBtnSize = 20;
        int closeBtnX = winX + winW - closeBtnSize - 8;
        int closeBtnY = winY + 7;
        boolean hoverClose = mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize;
        if (hoverClose) {
            Services.PLATFORM.fill(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnX + closeBtnSize, closeBtnY + closeBtnSize, applyAlpha(0x40FFFFFF, alpha));
        }
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeBtnX + closeBtnSize / 2, closeBtnY + 6, applyAlpha(hoverClose ? 0xFFFFFFFF : 0xFFAAAAAA, alpha));

        // 5. Left Sidenav Panel
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int navX = winX;
        int navY = winY + headerH;
        int navH = winH - headerH;

        // Sidenav background
        Services.PLATFORM.fill(poseStackOrGraphics, navX, navY, navX + currentNavW, navY + navH, applyAlpha(COLOR_SIDENAV_BG, alpha));
        // Vertical divider between sidenav and content
        Services.PLATFORM.fill(poseStackOrGraphics, navX + currentNavW - 1, navY, navX + currentNavW, navY + navH, applyAlpha(COLOR_DIVIDER, alpha));

        // Sidenav Toggle Button at top of sidenav
        int toggleH = 26;
        int toggleY = navY + 4;
        boolean hoverToggle = mouseX >= navX + 4 && mouseX <= navX + currentNavW - 4 && mouseY >= toggleY && mouseY <= toggleY + toggleH;
        if (hoverToggle) {
            Services.PLATFORM.fill(poseStackOrGraphics, navX + 4, toggleY, navX + currentNavW - 4, toggleY + toggleH, applyAlpha(0x25FFFFFF, alpha));
        }
        String toggleIcon = isNavExpanded ? "«" : "»";
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, toggleIcon, navX + COLLAPSED_NAV_WIDTH / 2, toggleY + 8, applyAlpha(COLOR_TEXT_PRIMARY, alpha));

        if (navProgress > 0.3f) {
            Services.PLATFORM.enableScissor(poseStackOrGraphics, navX + COLLAPSED_NAV_WIDTH, toggleY, currentNavW - COLLAPSED_NAV_WIDTH - 6, toggleH);
            float textAlpha = alpha * Math.max(0.0f, (navProgress - 0.3f) / 0.7f);
            drawText(poseStackOrGraphics, font, "Collapse Menu", navX + COLLAPSED_NAV_WIDTH + 4, toggleY + 9, applyAlpha(COLOR_TEXT_SECONDARY, textAlpha));
            Services.PLATFORM.disableScissor(poseStackOrGraphics);
        }

        // Sidenav Tab Items
        int itemStartY = navY + 36;
        int itemH = 30;
        int itemGap = 3;

        String hoveredTooltip = null;

        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int itemY = itemStartY + i * (itemH + itemGap);
            boolean isSelected = (i == selectedTabIndex);
            boolean hoverItem = mouseX >= navX + 3 && mouseX <= navX + currentNavW - 3 && mouseY >= itemY && mouseY <= itemY + itemH;

            // Highlight backgrounds
            if (isSelected) {
                Services.PLATFORM.fill(poseStackOrGraphics, navX + 3, itemY, navX + currentNavW - 3, itemY + itemH, applyAlpha(0x35FFFFFF, alpha));
                // Active indicator pill on left edge
                Services.PLATFORM.fill(poseStackOrGraphics, navX + 3, itemY + 3, navX + 6, itemY + itemH - 3, applyAlpha(COLOR_ACCENT, alpha));
            } else if (hoverItem) {
                Services.PLATFORM.fill(poseStackOrGraphics, navX + 3, itemY, navX + currentNavW - 3, itemY + itemH, applyAlpha(0x18FFFFFF, alpha));
            }

            // Tab icon (always centered in the collapsed section)
            int iconColor = isSelected ? COLOR_TEXT_PRIMARY : (hoverItem ? 0xFFDDDDDD : COLOR_TEXT_SECONDARY);
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, item.icon, navX + COLLAPSED_NAV_WIDTH / 2, itemY + 11, applyAlpha(iconColor, alpha));

            // Tab label text (visible during expansion)
            if (navProgress > 0.15f) {
                Services.PLATFORM.enableScissor(poseStackOrGraphics, navX + COLLAPSED_NAV_WIDTH, itemY, currentNavW - COLLAPSED_NAV_WIDTH - 6, itemH);
                float labelAlpha = alpha * Math.max(0.0f, (navProgress - 0.15f) / 0.85f);
                int textColor = isSelected ? COLOR_TEXT_PRIMARY : COLOR_TEXT_SECONDARY;
                drawText(poseStackOrGraphics, font, item.label, navX + COLLAPSED_NAV_WIDTH + 4, itemY + 11, applyAlpha(textColor, labelAlpha));
                Services.PLATFORM.disableScissor(poseStackOrGraphics);
            }

            // Hover tooltip when collapsed
            if (hoverItem && navProgress < 0.4f) {
                hoveredTooltip = item.label;
            }
        }

        // 6. Right Content Panel (Dynamic layout responding to currentNavW)
        int contentX = navX + currentNavW;
        int contentY = navY;
        int contentW = winW - currentNavW;
        int contentH = navH;

        // Content panel background
        Services.PLATFORM.fill(poseStackOrGraphics, contentX, contentY, contentX + contentW, contentY + contentH, applyAlpha(COLOR_CONTENT_BG, alpha));

        // Render Active Tab Content inside content viewport
        Services.PLATFORM.enableScissor(poseStackOrGraphics, contentX, contentY, contentW, contentH);
        renderTabContent(poseStackOrGraphics, font, contentX, contentY, contentW, contentH, mouseX, mouseY, alpha);
        Services.PLATFORM.disableScissor(poseStackOrGraphics);

        // 7. Render Legacy Tab Content (if attached)
        if (activeTab != null) {
            activeTab.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
            activeTab.renderTooltips(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }

        // 8. Tooltip rendering (collapsed tab tooltips or toggle hints)
        if (hoveredTooltip != null) {
            Services.PLATFORM.renderComponentTooltip(
                    poseStackOrGraphics, font,
                    Collections.singletonList(ComponentHelper.literal(hoveredTooltip)),
                    mouseX + 12, mouseY
            );
        } else if (hoverToggle && navProgress < 0.2f) {
            Services.PLATFORM.renderComponentTooltip(
                    poseStackOrGraphics, font,
                    Collections.singletonList(ComponentHelper.literal("Expand Menu")),
                    mouseX + 12, mouseY
            );
        }
    }

    // --- Content Panel Rendering ---
    private void renderTabContent(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        if (selectedTabIndex < 0 || selectedTabIndex >= navItems.size()) return;
        NavItem current = navItems.get(selectedTabIndex);

        int padX = 20;
        int curY = y + 16;

        // Section Title & Subtitle Header
        String fullTitle = current.icon + "  " + current.label;
        drawText(poseStackOrGraphics, font, fullTitle, x + padX, curY, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        curY += 14;
        drawText(poseStackOrGraphics, font, current.description, x + padX, curY, applyAlpha(COLOR_TEXT_MUTED, alpha));
        curY += 18;

        // Thin divider
        Services.PLATFORM.fill(poseStackOrGraphics, x + padX, curY, x + w - padX, curY + 1, applyAlpha(COLOR_DIVIDER, alpha));
        curY += 14;

        int cardW = Math.min(w - padX * 2, 480);

        // Render Tab-Specific Interactive Test Cards
        switch (current.id) {
            case "pillars" -> renderPillarsTab(poseStackOrGraphics, font, x + padX, curY, cardW, mouseX, mouseY, alpha);
            case "particles" -> renderParticlesTab(poseStackOrGraphics, font, x + padX, curY, cardW, mouseX, mouseY, alpha);
            case "identifiers" -> renderIdentifiersTab(poseStackOrGraphics, font, x + padX, curY, cardW, mouseX, mouseY, alpha);
            case "settings" -> renderSettingsTab(poseStackOrGraphics, font, x + padX, curY, cardW, mouseX, mouseY, alpha);
            case "about" -> renderAboutTab(poseStackOrGraphics, font, x + padX, curY, cardW, mouseX, mouseY, alpha);
        }
    }

    // --- Tab 1: Pillars ---
    private void renderPillarsTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;

        // Card 1: Pillar Highlight Glow (Toggle)
        int cardH = 46;
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Pillar Structural Glow", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Highlights aligned pillars with adaptive outline shader.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int toggleBtnW = 60;
        int toggleBtnH = 22;
        int toggleBtnX = cardX + cardW - toggleBtnW - 12;
        int toggleBtnY = curY + 12;
        renderToggleSwitch(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, testPillarHighlight, mouseX, mouseY, alpha);

        curY += cardH + 10;

        // Card 2: Connection Distance (Counter)
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Connection Search Radius", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Maximum search distance for continuous pillar joints.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int counterW = 90;
        int counterH = 22;
        int counterX = cardX + cardW - counterW - 12;
        int counterY = curY + 12;
        renderCounterWidget(poseStackOrGraphics, font, counterX, counterY, counterW, counterH, testPillarDistance + " blocks", mouseX, mouseY, alpha);

        curY += cardH + 10;

        // Card 3: Connection Mode (Cycle Button)
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Joint Linking Logic", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Rules governing multi-block vertical beam connections.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int cycleBtnW = 100;
        int cycleBtnH = 22;
        int cycleBtnX = cardX + cardW - cycleBtnW - 12;
        int cycleBtnY = curY + 12;
        renderCycleButton(poseStackOrGraphics, font, cycleBtnX, cycleBtnY, cycleBtnW, cycleBtnH, PILLAR_MODES[testPillarModeIndex], mouseX, mouseY, alpha);
    }

    // --- Tab 2: Particles ---
    private void renderParticlesTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;

        // Card 1: Ambient FX Toggle
        int cardH = 46;
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Pillar Core Sparks", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Enables subtle kinetic sparkles around active pillar nodes.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int toggleBtnW = 60;
        int toggleBtnH = 22;
        int toggleBtnX = cardX + cardW - toggleBtnW - 12;
        int toggleBtnY = curY + 12;
        renderToggleSwitch(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, testParticleEnabled, mouseX, mouseY, alpha);

        curY += cardH + 10;

        // Card 2: Density Selector
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Particle Density Multiplier", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Controls performance and particle emission budget.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int cycleBtnW = 110;
        int cycleBtnH = 22;
        int cycleBtnX = cardX + cardW - cycleBtnW - 12;
        int cycleBtnY = curY + 12;
        renderCycleButton(poseStackOrGraphics, font, cycleBtnX, cycleBtnY, cycleBtnW, cycleBtnH, PARTICLE_DENSITIES[testParticleDensityIndex], mouseX, mouseY, alpha);

        curY += cardH + 10;

        // Card 3: Color Palette Swatch Picker
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Particle Accent Tint", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Active: " + TEST_SWATCH_NAMES[testSelectedColorIndex], cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int swatchesTotalW = 4 * 22 + 3 * 6;
        int swatchesX = cardX + cardW - swatchesTotalW - 12;
        int swatchesY = curY + 12;
        for (int i = 0; i < TEST_SWATCH_COLORS.length; i++) {
            int swX = swatchesX + i * 28;
            boolean isSelected = (i == testSelectedColorIndex);
            boolean hoverSw = mouseX >= swX && mouseX <= swX + 22 && mouseY >= swatchesY && mouseY <= swatchesY + 22;

            Services.PLATFORM.fill(poseStackOrGraphics, swX, swatchesY, swX + 22, swatchesY + 22, applyAlpha(TEST_SWATCH_COLORS[i], alpha));
            if (isSelected) {
                drawBorder(poseStackOrGraphics, swX - 2, swatchesY - 2, 26, 26, applyAlpha(0xFFFFFFFF, alpha));
            } else if (hoverSw) {
                drawBorder(poseStackOrGraphics, swX - 1, swatchesY - 1, 24, 24, applyAlpha(0x80FFFFFF, alpha));
            }
        }
    }

    // --- Tab 3: Identifiers ---
    private void renderIdentifiersTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;

        // Card 1: ID Status
        int cardH = 50;
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Pillar Registry Mapping", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "All 14 pillar block models synchronized with PillarIdManager.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int badgeW = 74;
        int badgeH = 20;
        int badgeX = cardX + cardW - badgeW - 12;
        int badgeY = curY + 15;
        Services.PLATFORM.fill(poseStackOrGraphics, badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, applyAlpha(0x35388E3C, alpha));
        drawBorder(poseStackOrGraphics, badgeX, badgeY, badgeW, badgeH, applyAlpha(0xFF4CAF50, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "● SYNCHED", badgeX + badgeW / 2, badgeY + 6, applyAlpha(0xFF81C784, alpha));

        curY += cardH + 10;

        // Card 2: Auto-Snapping Bounds
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Voxel Auto-Snapping", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Automatically snaps placement collision boxes to block grid.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int toggleBtnW = 60;
        int toggleBtnH = 22;
        int toggleBtnX = cardX + cardW - toggleBtnW - 12;
        int toggleBtnY = curY + 14;
        renderToggleSwitch(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, testAutoSnapping, mouseX, mouseY, alpha);
    }

    // --- Tab 4: Settings ---
    private void renderSettingsTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;

        // Card 1: Animation Speed
        int cardH = 46;
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Interface Animation Speed", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "Sets interpolation timing for dialog and sidebar transitions.", cardX + 12, curY + 24, applyAlpha(COLOR_TEXT_MUTED, alpha));

        int cycleBtnW = 120;
        int cycleBtnH = 22;
        int cycleBtnX = cardX + cardW - cycleBtnW - 12;
        int cycleBtnY = curY + 12;
        renderCycleButton(poseStackOrGraphics, font, cycleBtnX, cycleBtnY, cycleBtnW, cycleBtnH, ANIM_SPEEDS[testAnimSpeedIndex], mouseX, mouseY, alpha);

        curY += cardH + 10;

        // Card 2: Platform Diagnostics Badge
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, 58, alpha);
        drawText(poseStackOrGraphics, font, "Active Engine Platform", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        String diag = "VersionCluster: v26x  |  Loader: NeoForge 26.2  |  Java: 25";
        drawText(poseStackOrGraphics, font, diag, cardX + 12, curY + 26, applyAlpha(COLOR_TEXT_SECONDARY, alpha));
        drawText(poseStackOrGraphics, font, "Shared Common Neutral Core with Offline Remapping Active.", cardX + 12, curY + 40, applyAlpha(COLOR_TEXT_MUTED, alpha));
    }

    // --- Tab 5: About ---
    private void renderAboutTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;

        int cardH = 92;
        renderCardBg(poseStackOrGraphics, cardX, curY, cardW, cardH, alpha);
        drawText(poseStackOrGraphics, font, "Buildscape UI Redesign Canvas", cardX + 12, curY + 10, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
        drawText(poseStackOrGraphics, font, "High-performance modular configuration interface.", cardX + 12, curY + 26, applyAlpha(COLOR_TEXT_SECONDARY, alpha));
        drawText(poseStackOrGraphics, font, "Clean 75% opacity dark-slate gray architecture.", cardX + 12, curY + 42, applyAlpha(COLOR_TEXT_MUTED, alpha));
        drawText(poseStackOrGraphics, font, "Zero legacy stubs; all logic routed via Services.PLATFORM.", cardX + 12, curY + 58, applyAlpha(COLOR_TEXT_MUTED, alpha));
        drawText(poseStackOrGraphics, font, "Designed for parity across 11 Minecraft loader targets.", cardX + 12, curY + 74, applyAlpha(COLOR_TEXT_MUTED, alpha));
    }

    private void drawText(Object poseStackOrGraphics, Font font, String text, int x, int y, int color) {
        Services.PLATFORM.drawShadow(poseStackOrGraphics, font, text, x, y, color);
    }

    // --- Reusable Component Renderers ---
    private void renderCardBg(Object poseStackOrGraphics, int x, int y, int w, int h, float alpha) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(COLOR_CARD_BG, alpha));
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(COLOR_CARD_BORDER, alpha));
    }

    private void renderToggleSwitch(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, boolean state, int mouseX, int mouseY, float alpha) {
        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bg = state ? 0xFF2E7D32 : 0xBF3E3E44;
        if (hover) bg = state ? 0xFF388E3C : 0xBF4E4E56;

        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(hover ? 0x80FFFFFF : 0x30FFFFFF, alpha));

        String text = state ? "ON" : "OFF";
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, x + w / 2, y + 7, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
    }

    private void renderCounterWidget(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String valueText, int mouseX, int mouseY, float alpha) {
        // [ - ] value [ + ]
        int btnW = 20;

        // Minus button
        boolean hoverMinus = mouseX >= x && mouseX <= x + btnW && mouseY >= y && mouseY <= y + h;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + btnW, y + h, applyAlpha(hoverMinus ? 0x40FFFFFF : 0x20FFFFFF, alpha));
        drawBorder(poseStackOrGraphics, x, y, btnW, h, applyAlpha(0x30FFFFFF, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "-", x + btnW / 2, y + 7, applyAlpha(COLOR_TEXT_PRIMARY, alpha));

        // Center value display
        int centerW = w - btnW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, x + btnW, y, x + btnW + centerW, y + h, applyAlpha(0x18FFFFFF, alpha));
        drawBorder(poseStackOrGraphics, x + btnW, y, centerW, h, applyAlpha(0x20FFFFFF, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, valueText, x + btnW + centerW / 2, y + 7, applyAlpha(COLOR_TEXT_PRIMARY, alpha));

        // Plus button
        int plusX = x + w - btnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + btnW && mouseY >= y && mouseY <= y + h;
        Services.PLATFORM.fill(poseStackOrGraphics, plusX, y, plusX + btnW, y + h, applyAlpha(hoverPlus ? 0x40FFFFFF : 0x20FFFFFF, alpha));
        drawBorder(poseStackOrGraphics, plusX, y, btnW, h, applyAlpha(0x30FFFFFF, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+", plusX + btnW / 2, y + 7, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
    }

    private void renderCycleButton(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String text, int mouseX, int mouseY, float alpha) {
        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(hover ? 0x40FFFFFF : 0x20FFFFFF, alpha));
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(hover ? 0x80FFFFFF : 0x30FFFFFF, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, x + w / 2, y + 7, applyAlpha(COLOR_TEXT_PRIMARY, alpha));
    }

    private static void drawBorder(Object poseStackOrGraphics, int x, int y, int w, int h, int color) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + 1, x + 1, y + h - 1, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    // --- Mouse & Keyboard Event Handlers ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isClosing) return true; // Ignore clicks during closing transition

        int marginX = Math.max(16, (int) (width * 0.04));
        int marginY = Math.max(14, (int) (height * 0.05));
        int winX = marginX;
        int winY = marginY;
        int winW = width - marginX * 2;
        int winH = height - marginY * 2;
        int headerH = 34;

        // 1. Top-Right Close Button [ ✕ ]
        int closeBtnSize = 20;
        int closeBtnX = winX + winW - closeBtnSize - 8;
        int closeBtnY = winY + 7;
        if (mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize) {
            startClosing();
            return true;
        }

        // 2. Sidenav Toggle Button
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int navX = winX;
        int navY = winY + headerH;
        int toggleY = navY + 4;
        int toggleH = 26;
        if (mouseX >= navX + 4 && mouseX <= navX + currentNavW - 4 && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
            toggleNav();
            return true;
        }

        // 3. Sidenav Tab Items
        int itemStartY = navY + 36;
        int itemH = 30;
        int itemGap = 3;
        for (int i = 0; i < navItems.size(); i++) {
            int itemY = itemStartY + i * (itemH + itemGap);
            if (mouseX >= navX + 3 && mouseX <= navX + currentNavW - 3 && mouseY >= itemY && mouseY <= itemY + itemH) {
                selectedTabIndex = i;
                return true;
            }
        }

        // 4. Content Area Interactive Cards
        int padX = 20;
        int contentX = navX + currentNavW;
        int cardX = contentX + padX;
        int cardW = Math.min(winW - currentNavW - padX * 2, 480);
        int curY = navY + 16 + 14 + 18 + 14; // header offset

        NavItem current = navItems.get(selectedTabIndex);
        if ("pillars".equals(current.id)) {
            // Card 1: Toggle
            int toggleBtnW = 60;
            int toggleBtnH = 22;
            int toggleBtnX = cardX + cardW - toggleBtnW - 12;
            int toggleBtnY = curY + 12;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                testPillarHighlight = !testPillarHighlight;
                return true;
            }
            curY += 46 + 10;

            // Card 2: Counter
            int counterW = 90;
            int counterH = 22;
            int counterX = cardX + cardW - counterW - 12;
            int counterY = curY + 12;
            int btnW = 20;
            if (mouseY >= counterY && mouseY <= counterY + counterH) {
                if (mouseX >= counterX && mouseX <= counterX + btnW) {
                    testPillarDistance = Math.max(1, testPillarDistance - 1);
                    return true;
                } else if (mouseX >= counterX + counterW - btnW && mouseX <= counterX + counterW) {
                    testPillarDistance = Math.min(32, testPillarDistance + 1);
                    return true;
                }
            }
            curY += 46 + 10;

            // Card 3: Cycle
            int cycleBtnW = 100;
            int cycleBtnH = 22;
            int cycleBtnX = cardX + cardW - cycleBtnW - 12;
            int cycleBtnY = curY + 12;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH) {
                testPillarModeIndex = (testPillarModeIndex + 1) % PILLAR_MODES.length;
                return true;
            }
        } else if ("particles".equals(current.id)) {
            // Card 1: Toggle
            int toggleBtnW = 60;
            int toggleBtnH = 22;
            int toggleBtnX = cardX + cardW - toggleBtnW - 12;
            int toggleBtnY = curY + 12;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                testParticleEnabled = !testParticleEnabled;
                return true;
            }
            curY += 46 + 10;

            // Card 2: Cycle
            int cycleBtnW = 110;
            int cycleBtnH = 22;
            int cycleBtnX = cardX + cardW - cycleBtnW - 12;
            int cycleBtnY = curY + 12;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH) {
                testParticleDensityIndex = (testParticleDensityIndex + 1) % PARTICLE_DENSITIES.length;
                return true;
            }
            curY += 46 + 10;

            // Card 3: Swatches
            int swatchesTotalW = 4 * 22 + 3 * 6;
            int swatchesX = cardX + cardW - swatchesTotalW - 12;
            int swatchesY = curY + 12;
            for (int i = 0; i < TEST_SWATCH_COLORS.length; i++) {
                int swX = swatchesX + i * 28;
                if (mouseX >= swX && mouseX <= swX + 22 && mouseY >= swatchesY && mouseY <= swatchesY + 22) {
                    testSelectedColorIndex = i;
                    return true;
                }
            }
        } else if ("identifiers".equals(current.id)) {
            curY += 50 + 10;
            // Card 2: Auto-Snapping Toggle
            int toggleBtnW = 60;
            int toggleBtnH = 22;
            int toggleBtnX = cardX + cardW - toggleBtnW - 12;
            int toggleBtnY = curY + 14;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                testAutoSnapping = !testAutoSnapping;
                return true;
            }
        } else if ("settings".equals(current.id)) {
            // Card 1: Speed cycle
            int cycleBtnW = 120;
            int cycleBtnH = 22;
            int cycleBtnX = cardX + cardW - cycleBtnW - 12;
            int cycleBtnY = curY + 12;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH) {
                testAnimSpeedIndex = (testAnimSpeedIndex + 1) % ANIM_SPEEDS.length;
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
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int marginX = Math.max(16, (int) (width * 0.04));
        return marginX + currentNavW;
    }

    public int getContentY() {
        int marginY = Math.max(14, (int) (height * 0.05));
        return marginY + 34;
    }

    public int getContentWidth() {
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int marginX = Math.max(16, (int) (width * 0.04));
        return (width - marginX * 2) - currentNavW;
    }

    public int getContentHeight() {
        int marginY = Math.max(14, (int) (height * 0.05));
        return (height - marginY * 2) - 34;
    }

    public int getSidebarWidth() {
        return Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
    }

    public static int scaleSize(int baseSize) { return baseSize; }
    public static int getScaledButtonHeight() { return 20; }
    public static int getScaledEditBoxHeight() { return 18; }
    public static float getStandardTextScale() { return 1.0f; }
}
