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
 * Modern animated configuration screen for Buildscape.
 * Features:
 * - Seamless monochrome (White, Gray, Black) beveled frame & card architecture.
 * - Opening and closing cubic-eased fade & scale transitions.
 * - Collapsible / expandable sidenav with smooth width interpolation.
 * - 75% opacity dark charcoal background with multi-layered beveled borders.
 * - Real Buildscape options: Structural Pillars, Particles, World Settings, Pillar IDs, and System.
 * - All text rendered in crisp pure white.
 * - Centered, non-spilling layout with zero legacy stubs.
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
    private static final int NAV_DURATION_MS = 150;

    private long openStartTime;
    private long closeStartTime = -1;
    private long lastFrameTime;

    private boolean isOpening = true;
    private boolean isClosing = false;
    private float openProgress = 0.0f;
    private float closeProgress = 1.0f;

    // --- Sidenav Configuration & Animation ---
    public static final int COLLAPSED_NAV_WIDTH = 42;
    public static final int EXPANDED_NAV_WIDTH = 138;
    private boolean isNavExpanded = false;
    private float navProgress = 0.0f; // 0.0 = collapsed, 1.0 = expanded

    // --- Monochrome Colors (White, Gray, Black) ---
    public static final int COLOR_OVERLAY = 0xBF0A0A0C;           // 75% dark backdrop
    public static final int COLOR_CONTAINER_BG = 0xBF1A1A1D;      // 75% window background
    public static final int COLOR_HEADER_BG = 0xBF222226;         // 75% header background
    public static final int COLOR_SIDENAV_BG = 0xBF161619;        // 75% sidebar background
    public static final int COLOR_CONTENT_BG = 0xBF141416;        // 75% content panel background
    public static final int COLOR_CARD_BG = 0xBF1E1E22;           // 75% card background
    public static final int COLOR_CARD_BG_HOVER = 0xBF28282D;     // 75% card hover
    public static final int COLOR_CARD_BG_ACTIVE = 0xBF323238;    // 75% card active

    // Bevel & Border Monochrome Constants
    public static final int COLOR_BORDER_BLACK = 0xFF08080A;      // Outer crisp black edge
    public static final int COLOR_BORDER_DARK = 0xFF28282C;       // Inner recessed shadow
    public static final int COLOR_BORDER_MID = 0xFF4A4A52;        // Default mid-gray border
    public static final int COLOR_BORDER_LIGHT = 0xFF767680;      // Highlight bevel edge
    public static final int COLOR_BORDER_WHITE = 0xFFFFFFFF;      // Active / selected white border

    public static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;        // Pure white text
    public static final int COLOR_TEXT_DIM = 0xFFCCCCCC;          // Soft white text

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
    private boolean startExpanded = false;

    public BuildScapeConfigScreen(Screen parent) {
        this.parent = parent;
        this.title = ComponentHelper.translatable("buildscape.config.title");

        this.openStartTime = System.currentTimeMillis();
        this.lastFrameTime = System.currentTimeMillis();

        initNavItems();
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem("pillars", "◆", "Pillars", "Structural multi-block pillar joint rendering."));
        navItems.add(new NavItem("particles", "✦", "Particles", "Ambient kinetic effects, patterns, and density."));
        navItems.add(new NavItem("world", "⚙", "World", "Global gameplay mechanics and block behaviors."));
        navItems.add(new NavItem("identifiers", "▲", "Pillar IDs", "Block placement bounds and registry synchronization."));
        navItems.add(new NavItem("system", "ℹ", "System", "Client interface preferences and diagnostics."));
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

        // 2. Compute main dialog window dimensions (centered, bounded so nothing spills)
        int winW = Math.min(width - 24, 520);
        int winH = Math.min(height - 24, 300);
        int winX = (width - winW) / 2;
        int winY = (height - winH) / 2;

        // Subtle slide-in effect during open/close
        int offsetY = Math.round((1.0f - alpha) * 8.0f);
        winY += offsetY;

        // 3. Main container: Multi-layer beveled frame (Black, Gray, White)
        drawBeveledFrame(poseStackOrGraphics, winX, winY, winW, winH, COLOR_CONTAINER_BG, alpha);

        // 4. Header Bar
        int headerH = 30;
        Services.PLATFORM.fill(poseStackOrGraphics, winX + 2, winY + 2, winX + winW - 2, winY + headerH, applyAlpha(COLOR_HEADER_BG, alpha));
        // Header bottom divider groove (1px dark + 1px light)
        Services.PLATFORM.fill(poseStackOrGraphics, winX + 2, winY + headerH - 1, winX + winW - 2, winY + headerH, applyAlpha(COLOR_BORDER_BLACK, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, winX + 2, winY + headerH, winX + winW - 2, winY + headerH + 1, applyAlpha(COLOR_BORDER_DARK, alpha));

        // Header Title & Version Badge in Pure White
        String titleStr = "BUILDSCAPE";
        drawText(poseStackOrGraphics, font, titleStr, winX + 10, winY + 10, applyAlpha(COLOR_TEXT_WHITE, alpha));
        int titleW = font.width(titleStr);
        drawText(poseStackOrGraphics, font, "v4.0.3", winX + 14 + titleW, winY + 10, applyAlpha(COLOR_TEXT_DIM, alpha));

        // Header Top-Right Close Button [ ✕ ] (Beveled monochrome square)
        int closeBtnSize = 18;
        int closeBtnX = winX + winW - closeBtnSize - 6;
        int closeBtnY = winY + 6;
        boolean hoverClose = mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize;
        drawBeveledButton(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnSize, closeBtnSize, hoverClose, false, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeBtnX + closeBtnSize / 2, closeBtnY + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // 5. Left Sidenav Panel
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int navX = winX + 2;
        int navY = winY + headerH + 1;
        int navH = winH - headerH - 3;

        // Sidenav background
        Services.PLATFORM.fill(poseStackOrGraphics, navX, navY, navX + currentNavW, navY + navH, applyAlpha(COLOR_SIDENAV_BG, alpha));
        // Sidenav right divider groove (1px dark + 1px light)
        Services.PLATFORM.fill(poseStackOrGraphics, navX + currentNavW - 2, navY, navX + currentNavW - 1, navY + navH, applyAlpha(COLOR_BORDER_BLACK, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, navX + currentNavW - 1, navY, navX + currentNavW, navY + navH, applyAlpha(COLOR_BORDER_DARK, alpha));

        // Sidenav Toggle Button at top of sidenav
        int toggleH = 22;
        int toggleY = navY + 4;
        int toggleW = currentNavW - 8;
        boolean hoverToggle = mouseX >= navX + 4 && mouseX <= navX + 4 + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;
        drawBeveledButton(poseStackOrGraphics, navX + 4, toggleY, toggleW, toggleH, hoverToggle, false, alpha);

        String toggleIcon = isNavExpanded ? "◀" : "☰";
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, toggleIcon, navX + COLLAPSED_NAV_WIDTH / 2, toggleY + 7, applyAlpha(COLOR_TEXT_WHITE, alpha));

        if (navProgress > 0.45f) {
            float textAlpha = alpha * Math.max(0.0f, (navProgress - 0.45f) / 0.55f);
            drawText(poseStackOrGraphics, font, "Collapse", navX + COLLAPSED_NAV_WIDTH + 2, toggleY + 7, applyAlpha(COLOR_TEXT_WHITE, textAlpha));
        }

        // Sidenav Tab Items (Beveled Tiles)
        int itemStartY = navY + 30;
        int itemH = 26;
        int itemGap = 3;

        String hoveredTooltip = null;

        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int itemY = itemStartY + i * (itemH + itemGap);
            boolean isSelected = (i == selectedTabIndex);
            int itemW = currentNavW - 8;
            boolean hoverItem = mouseX >= navX + 4 && mouseX <= navX + 4 + itemW && mouseY >= itemY && mouseY <= itemY + itemH;

            // Render beveled tab tile
            drawBeveledTabTile(poseStackOrGraphics, navX + 4, itemY, itemW, itemH, isSelected, hoverItem, alpha);

            // Tab icon (centered in the collapsed tile section in pure white)
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, item.icon, navX + COLLAPSED_NAV_WIDTH / 2, itemY + 9, applyAlpha(COLOR_TEXT_WHITE, alpha));

            // Tab label text (visible during expansion in pure white)
            if (navProgress > 0.35f) {
                float labelAlpha = alpha * Math.max(0.0f, (navProgress - 0.35f) / 0.65f);
                drawText(poseStackOrGraphics, font, item.label, navX + COLLAPSED_NAV_WIDTH + 2, itemY + 9, applyAlpha(COLOR_TEXT_WHITE, labelAlpha));
            }

            // Hover tooltip when collapsed
            if (hoverItem && navProgress < 0.35f) {
                hoveredTooltip = item.label;
            }
        }

        // 6. Right Content Panel
        int contentX = navX + currentNavW;
        int contentY = navY;
        int contentW = (winX + winW - 2) - contentX;
        int contentH = navH;

        // Content panel background
        Services.PLATFORM.fill(poseStackOrGraphics, contentX, contentY, contentX + contentW, contentY + contentH, applyAlpha(COLOR_CONTENT_BG, alpha));

        // Render Active Tab Content inside content viewport
        renderTabContent(poseStackOrGraphics, font, contentX, contentY, contentW, contentH, mouseX, mouseY, alpha);

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

        int padX = 12;
        int curY = y + 10;

        // Section Title & Subtitle Header in Pure White
        String fullTitle = current.icon + "  " + current.label;
        drawText(poseStackOrGraphics, font, fullTitle, x + padX, curY, applyAlpha(COLOR_TEXT_WHITE, alpha));
        curY += 13;
        drawText(poseStackOrGraphics, font, current.description, x + padX, curY, applyAlpha(COLOR_TEXT_DIM, alpha));
        curY += 14;

        // Subtle divider groove
        Services.PLATFORM.fill(poseStackOrGraphics, x + padX, curY, x + w - padX, curY + 1, applyAlpha(COLOR_BORDER_BLACK, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + padX, curY + 1, x + w - padX, curY + 2, applyAlpha(COLOR_BORDER_DARK, alpha));
        curY += 8;

        // Perfectly bounded card width so nothing spills out
        int cardW = w - padX * 2;
        int cardX = x + padX;

        // Render Tab-Specific Real Configuration Cards
        switch (current.id) {
            case "pillars" -> renderPillarsTab(poseStackOrGraphics, font, cardX, curY, cardW, mouseX, mouseY, alpha);
            case "particles" -> renderParticlesTab(poseStackOrGraphics, font, cardX, curY, cardW, mouseX, mouseY, alpha);
            case "world" -> renderWorldSettingsTab(poseStackOrGraphics, font, cardX, curY, cardW, mouseX, mouseY, alpha);
            case "identifiers" -> renderIdentifiersTab(poseStackOrGraphics, font, cardX, curY, cardW, mouseX, mouseY, alpha);
            case "system" -> renderSystemTab(poseStackOrGraphics, font, cardX, curY, cardW, mouseX, mouseY, alpha);
        }
    }

    // --- Tab 0: Pillars ---
    private void renderPillarsTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;
        int cardH = 36;

        // Card 1: Pillar Structural Glow (Toggle)
        boolean hover1 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover1, false, alpha);
        drawText(poseStackOrGraphics, font, "Pillar Structural Glow", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Adaptive outline shader on linked pillar joints.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int toggleBtnW = 46;
        int toggleBtnH = 18;
        int toggleBtnX = cardX + cardW - toggleBtnW - 8;
        int toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, pillarGlowEnabled, mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 2: Joint Connection Radius (Counter)
        boolean hover2 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover2, false, alpha);
        drawText(poseStackOrGraphics, font, "Connection Search Radius", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Maximum block search distance for continuous joints.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int counterW = 76;
        int counterH = 18;
        int counterX = cardX + cardW - counterW - 8;
        int counterY = curY + 9;
        renderMonochromeCounter(poseStackOrGraphics, font, counterX, counterY, counterW, counterH, pillarJointRadius + " blk", mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 3: Multi-Block Linking Mode (Cycle Button)
        boolean hover3 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover3, false, alpha);
        drawText(poseStackOrGraphics, font, "Multi-Block Linking Logic", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Rules governing vertical structural joints.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int cycleBtnW = 82;
        int cycleBtnH = 18;
        int cycleBtnX = cardX + cardW - cycleBtnW - 8;
        int cycleBtnY = curY + 9;
        renderMonochromeCycle(poseStackOrGraphics, font, cycleBtnX, cycleBtnY, cycleBtnW, cycleBtnH, PILLAR_LINKING_MODES[pillarLinkingModeIndex], mouseX, mouseY, alpha);
    }

    // --- Tab 1: Particles ---
    private void renderParticlesTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;
        int cardH = 36;

        // Card 1: Ambient FX Toggle
        boolean hover1 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover1, false, alpha);
        drawText(poseStackOrGraphics, font, "Pillar Core Sparks", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Subtle kinetic ambient sparks around nodes.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int toggleBtnW = 46;
        int toggleBtnH = 18;
        int toggleBtnX = cardX + cardW - toggleBtnW - 8;
        int toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, pillarSparksEnabled, mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 2: Emission Pattern (Real Buildscape Patterns)
        boolean hover2 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover2, false, alpha);
        drawText(poseStackOrGraphics, font, "Particle Pattern Style", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Kinetic trajectory solver for pillar ambient FX.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int cycleBtnW = 82;
        int cycleBtnH = 18;
        int cycleBtnX = cardX + cardW - cycleBtnW - 8;
        int cycleBtnY = curY + 9;
        renderMonochromeCycle(poseStackOrGraphics, font, cycleBtnX, cycleBtnY, cycleBtnW, cycleBtnH, PARTICLE_PATTERNS[particlePatternIndex], mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 3: Density Selector
        boolean hover3 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover3, false, alpha);
        drawText(poseStackOrGraphics, font, "Particle Density Budget", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Multiplier budget for client particle count.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int densityBtnW = 72;
        int densityBtnH = 18;
        int densityBtnX = cardX + cardW - densityBtnW - 8;
        int densityBtnY = curY + 9;
        renderMonochromeCycle(poseStackOrGraphics, font, densityBtnX, densityBtnY, densityBtnW, densityBtnH, PARTICLE_DENSITIES[particleDensityIndex], mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 4: Monochrome Color Swatches & Inset Tiles (White, Silver, Graphite, Obsidian)
        boolean hover4 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover4, false, alpha);
        drawText(poseStackOrGraphics, font, "Monochrome Accent Slot", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Selected: " + MONO_SWATCH_NAMES[selectedSwatchIndex], cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int swatchesTotalW = 4 * 18 + 3 * 5;
        int swatchesX = cardX + cardW - swatchesTotalW - 8;
        int swatchesY = curY + 9;
        for (int i = 0; i < MONO_SWATCH_COLORS.length; i++) {
            int swX = swatchesX + i * 23;
            boolean isSelected = (i == selectedSwatchIndex);
            boolean hoverSw = mouseX >= swX && mouseX <= swX + 18 && mouseY >= swatchesY && mouseY <= swatchesY + 18;
            renderMonochromeSwatchTile(poseStackOrGraphics, swX, swatchesY, 18, 18, MONO_SWATCH_COLORS[i], isSelected, hoverSw, alpha);
        }
    }

    // --- Tab 2: World Settings (Real Buildscape Mechanics) ---
    private void renderWorldSettingsTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;
        int cardH = 36;

        // Card 1: Creative Tree Breaker
        boolean hover1 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover1, false, alpha);
        drawText(poseStackOrGraphics, font, "Creative Tree Breaker", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Instantly harvests contiguous trees in creative mode.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int toggleBtnW = 46;
        int toggleBtnH = 18;
        int toggleBtnX = cardX + cardW - toggleBtnW - 8;
        int toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, creativeTreeBreaker, mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 2: Shulker Box Preview
        boolean hover2 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover2, false, alpha);
        drawText(poseStackOrGraphics, font, "Shulker Box Tooltip Preview", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Inspect container inventory directly on item hover.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        toggleBtnX = cardX + cardW - toggleBtnW - 8;
        toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, shulkerPreview, mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 3: Fast Leaf Decay
        boolean hover3 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover3, false, alpha);
        drawText(poseStackOrGraphics, font, "Accelerated Leaf Decay", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Rapidly clears foliage upon wood trunk breakdown.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        toggleBtnX = cardX + cardW - toggleBtnW - 8;
        toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, fastLeafDecay, mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 4: Mob Griefing Defense
        boolean hover4 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover4, false, alpha);
        drawText(poseStackOrGraphics, font, "Mob Terrain Protection", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Prevents Creeper, Ghast & Enderman block damage.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        toggleBtnX = cardX + cardW - toggleBtnW - 8;
        toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, mobGriefingDefense, mouseX, mouseY, alpha);
    }

    // --- Tab 3: Identifiers ---
    private void renderIdentifiersTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;
        int cardH = 36;

        // Card 1: ID Status
        boolean hover1 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover1, false, alpha);
        drawText(poseStackOrGraphics, font, "Pillar Registry Mapping", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "14 pillar models synchronized with PillarIdManager.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int badgeW = 74;
        int badgeH = 18;
        int badgeX = cardX + cardW - badgeW - 8;
        int badgeY = curY + 9;
        drawBeveledBadge(poseStackOrGraphics, font, badgeX, badgeY, badgeW, badgeH, "● SYNCHED", alpha);

        curY += cardH + 6;

        // Card 2: Auto-Snapping Bounds
        boolean hover2 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover2, false, alpha);
        drawText(poseStackOrGraphics, font, "Voxel Auto-Snapping", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Snaps placement collision boxes to block grid.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int toggleBtnW = 46;
        int toggleBtnH = 18;
        int toggleBtnX = cardX + cardW - toggleBtnW - 8;
        int toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, autoSnappingEnabled, mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 3: Strict Collision Check
        boolean hover3 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover3, false, alpha);
        drawText(poseStackOrGraphics, font, "Strict Boundary Validation", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Verifies adjacent free space prior to structure place.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        toggleBtnX = cardX + cardW - toggleBtnW - 8;
        toggleBtnY = curY + 9;
        renderMonochromeToggle(poseStackOrGraphics, font, toggleBtnX, toggleBtnY, toggleBtnW, toggleBtnH, strictBoundsCheck, mouseX, mouseY, alpha);
    }

    // --- Tab 4: System & Diagnostics ---
    private void renderSystemTab(Object poseStackOrGraphics, Font font, int cardX, int startY, int cardW, int mouseX, int mouseY, float alpha) {
        int curY = startY;
        int cardH = 36;

        // Card 1: Animation Speed
        boolean hover1 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH, hover1, false, alpha);
        drawText(poseStackOrGraphics, font, "Interface Animation Timing", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, "Interpolation timing for dialog transitions.", cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));

        int cycleBtnW = 72;
        int cycleBtnH = 18;
        int cycleBtnX = cardX + cardW - cycleBtnW - 8;
        int cycleBtnY = curY + 9;
        renderMonochromeCycle(poseStackOrGraphics, font, cycleBtnX, cycleBtnY, cycleBtnW, cycleBtnH, ANIM_SPEEDS[animSpeedIndex], mouseX, mouseY, alpha);

        curY += cardH + 6;

        // Card 2: Platform Architecture
        boolean hover2 = isMouseOver(mouseX, mouseY, cardX, curY, cardW, cardH + 10);
        drawInsetCard(poseStackOrGraphics, cardX, curY, cardW, cardH + 10, hover2, false, alpha);
        drawText(poseStackOrGraphics, font, "Engine Platform Specifications", cardX + 8, curY + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        String diag = "VersionCluster: v26x  |  Loader: NeoForge 26.2";
        drawText(poseStackOrGraphics, font, diag, cardX + 8, curY + 19, applyAlpha(COLOR_TEXT_DIM, alpha));
        drawText(poseStackOrGraphics, font, "Shared Neutral Core with Offline Remapping Active.", cardX + 8, curY + 31, applyAlpha(COLOR_TEXT_DIM, alpha));
    }

    private void drawText(Object poseStackOrGraphics, Font font, String text, int x, int y, int color) {
        Services.PLATFORM.drawShadow(poseStackOrGraphics, font, text, x, y, color);
    }

    // --- Monochrome Bevel & Border Drawing Engine ---
    /**
     * Draws the main window frame with crisp Minecraft-style beveled borders.
     * Outer black outline, inner silver/gray highlight, and recessed shadow.
     */
    private void drawBeveledFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        // Fill background
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bgRgb, alpha));

        // Outer 1px: Black border
        int black = applyAlpha(COLOR_BORDER_BLACK, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);

        // Inner 1px: Highlight top & left (light gray), shadow bottom & right (dark gray)
        int light = applyAlpha(COLOR_BORDER_LIGHT, alpha);
        int dark = applyAlpha(COLOR_BORDER_DARK, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + 2, light);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + 2, y + h - 1, light);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + h - 2, x + w - 1, y + h - 1, dark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, dark);
    }

    /**
     * Draws an inset card or row with a beveled groove.
     */
    private void drawInsetCard(Object poseStackOrGraphics, int x, int y, int w, int h, boolean hovered, boolean selected, float alpha) {
        int bg = selected ? COLOR_CARD_BG_ACTIVE : (hovered ? COLOR_CARD_BG_HOVER : COLOR_CARD_BG);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

        // Outer card border
        int border = selected ? COLOR_BORDER_WHITE : (hovered ? COLOR_BORDER_LIGHT : COLOR_BORDER_MID);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, applyAlpha(border, alpha));

        // Subtle inset top-left shadow for tactile depth
        if (!selected) {
            int innerShadow = applyAlpha(0x50000000, alpha);
            Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + 2, innerShadow);
            Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + 2, y + h - 1, innerShadow);
        }
    }

    /**
     * Draws a beveled square/rectangular tab tile on the sidenav.
     */
    private void drawBeveledTabTile(Object poseStackOrGraphics, int x, int y, int w, int h, boolean selected, boolean hovered, float alpha) {
        int bg = selected ? 0xBF323238 : (hovered ? 0xBF242429 : 0xBF1A1A1E);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

        // Border
        int border = selected ? COLOR_BORDER_WHITE : (hovered ? COLOR_BORDER_LIGHT : COLOR_BORDER_DARK);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, applyAlpha(border, alpha));

        // If selected: Solid pure white left accent bar
        if (selected) {
            Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 3, y + h, applyAlpha(COLOR_BORDER_WHITE, alpha));
        }
    }

    /**
     * Draws a beveled monochrome button.
     */
    private void drawBeveledButton(Object poseStackOrGraphics, int x, int y, int w, int h, boolean hovered, boolean pressed, float alpha) {
        int bg = pressed ? 0xBF18181A : (hovered ? 0xBF323238 : 0xBF222226);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

        // Outer border
        int border = hovered ? COLOR_BORDER_WHITE : COLOR_BORDER_MID;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, applyAlpha(border, alpha));
    }

    /**
     * Monochrome toggle button (White/Gray/Black).
     */
    private void renderMonochromeToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, boolean state, int mouseX, int mouseY, float alpha) {
        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bg = state ? 0xBF3A3A42 : 0xBF1A1A1E;
        if (hover) bg = state ? 0xBF484852 : 0xBF242429;

        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

        // Border: Pure white when active or hovered
        int border = state ? COLOR_BORDER_WHITE : (hover ? COLOR_BORDER_LIGHT : COLOR_BORDER_MID);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, applyAlpha(border, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, applyAlpha(border, alpha));

        String text = state ? "ON" : "OFF";
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, x + w / 2, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    /**
     * Monochrome counter widget [-] value [+].
     */
    private void renderMonochromeCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String valueText, int mouseX, int mouseY, float alpha) {
        int btnW = 16;

        // Minus button
        boolean hoverMinus = mouseX >= x && mouseX <= x + btnW && mouseY >= y && mouseY <= y + h;
        drawBeveledButton(poseStackOrGraphics, x, y, btnW, h, hoverMinus, false, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "-", x + btnW / 2, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Center value inset box
        int centerW = w - btnW * 2;
        int centerBg = 0xBF161619;
        Services.PLATFORM.fill(poseStackOrGraphics, x + btnW, y, x + btnW + centerW, y + h, applyAlpha(centerBg, alpha));
        // 1px border around center
        Services.PLATFORM.fill(poseStackOrGraphics, x + btnW, y, x + btnW + centerW, y + 1, applyAlpha(COLOR_BORDER_MID, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + btnW, y + h - 1, x + btnW + centerW, y + h, applyAlpha(COLOR_BORDER_MID, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, valueText, x + btnW + centerW / 2, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Plus button
        int plusX = x + w - btnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + btnW && mouseY >= y && mouseY <= y + h;
        drawBeveledButton(poseStackOrGraphics, plusX, y, btnW, h, hoverPlus, false, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+", plusX + btnW / 2, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    /**
     * Monochrome cycle button.
     */
    private void renderMonochromeCycle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String text, int mouseX, int mouseY, float alpha) {
        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, hover, false, alpha);
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, x + w / 2, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    /**
     * Monochrome square swatch tile (beveled).
     */
    private void renderMonochromeSwatchTile(Object poseStackOrGraphics, int x, int y, int w, int h, int colorRgb, boolean selected, boolean hover, float alpha) {
        // Outer border
        int border = selected ? COLOR_BORDER_WHITE : (hover ? COLOR_BORDER_LIGHT : COLOR_BORDER_BLACK);
        Services.PLATFORM.fill(poseStackOrGraphics, x - 1, y - 1, x + w + 1, y + h + 1, applyAlpha(border, alpha));

        // Swatch fill
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(colorRgb, alpha));

        // Inset highlight/shadow
        int innerShadow = applyAlpha(0x40000000, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, innerShadow);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, innerShadow);
    }

    /**
     * Monochrome status badge.
     */
    private void drawBeveledBadge(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String text, float alpha) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(0xBF24242A, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, applyAlpha(COLOR_BORDER_LIGHT, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, applyAlpha(COLOR_BORDER_LIGHT, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, applyAlpha(COLOR_BORDER_LIGHT, alpha));
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, applyAlpha(COLOR_BORDER_LIGHT, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, x + w / 2, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    // --- Mouse & Keyboard Event Handlers ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isClosing) return true;

        int winW = Math.min(width - 24, 520);
        int winH = Math.min(height - 24, 300);
        int winX = (width - winW) / 2;
        int winY = (height - winH) / 2;
        int headerH = 30;

        // 1. Top-Right Close Button [ ✕ ]
        int closeBtnSize = 18;
        int closeBtnX = winX + winW - closeBtnSize - 6;
        int closeBtnY = winY + 6;
        if (mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize) {
            startClosing();
            return true;
        }

        // 2. Sidenav Toggle Button
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int navX = winX + 2;
        int navY = winY + headerH + 1;
        int toggleY = navY + 4;
        int toggleH = 22;
        int toggleW = currentNavW - 8;
        if (mouseX >= navX + 4 && mouseX <= navX + 4 + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
            toggleNav();
            return true;
        }

        // 3. Sidenav Tab Items
        int itemStartY = navY + 30;
        int itemH = 26;
        int itemGap = 3;
        int itemW = currentNavW - 8;
        for (int i = 0; i < navItems.size(); i++) {
            int itemY = itemStartY + i * (itemH + itemGap);
            if (mouseX >= navX + 4 && mouseX <= navX + 4 + itemW && mouseY >= itemY && mouseY <= itemY + itemH) {
                selectedTabIndex = i;
                return true;
            }
        }

        // 4. Content Area Interactive Cards
        int padX = 12;
        int contentX = navX + currentNavW;
        int cardX = contentX + padX;
        int cardW = (winX + winW - 2) - contentX - padX * 2;
        int curY = navY + 10 + 13 + 14 + 10; // header offset

        NavItem current = navItems.get(selectedTabIndex);
        if ("pillars".equals(current.id)) {
            // Card 1: Toggle
            int toggleBtnW = 46;
            int toggleBtnH = 18;
            int toggleBtnX = cardX + cardW - toggleBtnW - 8;
            int toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                pillarGlowEnabled = !pillarGlowEnabled;
                return true;
            }
            curY += 36 + 6;

            // Card 2: Counter
            int counterW = 76;
            int counterH = 18;
            int counterX = cardX + cardW - counterW - 8;
            int counterY = curY + 9;
            int btnW = 16;
            if (mouseY >= counterY && mouseY <= counterY + counterH) {
                if (mouseX >= counterX && mouseX <= counterX + btnW) {
                    pillarJointRadius = Math.max(1, pillarJointRadius - 1);
                    return true;
                } else if (mouseX >= counterX + counterW - btnW && mouseX <= counterX + counterW) {
                    pillarJointRadius = Math.min(32, pillarJointRadius + 1);
                    return true;
                }
            }
            curY += 36 + 6;

            // Card 3: Cycle
            int cycleBtnW = 82;
            int cycleBtnH = 18;
            int cycleBtnX = cardX + cardW - cycleBtnW - 8;
            int cycleBtnY = curY + 9;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH) {
                pillarLinkingModeIndex = (pillarLinkingModeIndex + 1) % PILLAR_LINKING_MODES.length;
                return true;
            }
        } else if ("particles".equals(current.id)) {
            // Card 1: Toggle
            int toggleBtnW = 46;
            int toggleBtnH = 18;
            int toggleBtnX = cardX + cardW - toggleBtnW - 8;
            int toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                pillarSparksEnabled = !pillarSparksEnabled;
                return true;
            }
            curY += 36 + 6;

            // Card 2: Pattern Cycle
            int cycleBtnW = 82;
            int cycleBtnH = 18;
            int cycleBtnX = cardX + cardW - cycleBtnW - 8;
            int cycleBtnY = curY + 9;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH) {
                particlePatternIndex = (particlePatternIndex + 1) % PARTICLE_PATTERNS.length;
                return true;
            }
            curY += 36 + 6;

            // Card 3: Density Cycle
            int densityBtnW = 72;
            int densityBtnH = 18;
            int densityBtnX = cardX + cardW - densityBtnW - 8;
            int densityBtnY = curY + 9;
            if (mouseX >= densityBtnX && mouseX <= densityBtnX + densityBtnW && mouseY >= densityBtnY && mouseY <= densityBtnY + densityBtnH) {
                particleDensityIndex = (particleDensityIndex + 1) % PARTICLE_DENSITIES.length;
                return true;
            }
            curY += 36 + 6;

            // Card 4: Swatches
            int swatchesTotalW = 4 * 18 + 3 * 5;
            int swatchesX = cardX + cardW - swatchesTotalW - 8;
            int swatchesY = curY + 9;
            for (int i = 0; i < MONO_SWATCH_COLORS.length; i++) {
                int swX = swatchesX + i * 23;
                if (mouseX >= swX && mouseX <= swX + 18 && mouseY >= swatchesY && mouseY <= swatchesY + 18) {
                    selectedSwatchIndex = i;
                    return true;
                }
            }
        } else if ("world".equals(current.id)) {
            // Card 1: Tree Breaker
            int toggleBtnW = 46;
            int toggleBtnH = 18;
            int toggleBtnX = cardX + cardW - toggleBtnW - 8;
            int toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                creativeTreeBreaker = !creativeTreeBreaker;
                return true;
            }
            curY += 36 + 6;

            // Card 2: Shulker Preview
            toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                shulkerPreview = !shulkerPreview;
                return true;
            }
            curY += 36 + 6;

            // Card 3: Fast Leaf Decay
            toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                fastLeafDecay = !fastLeafDecay;
                return true;
            }
            curY += 36 + 6;

            // Card 4: Mob Griefing Defense
            toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                mobGriefingDefense = !mobGriefingDefense;
                return true;
            }
        } else if ("identifiers".equals(current.id)) {
            curY += 36 + 6;
            // Card 2: Auto-Snapping Toggle
            int toggleBtnW = 46;
            int toggleBtnH = 18;
            int toggleBtnX = cardX + cardW - toggleBtnW - 8;
            int toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                autoSnappingEnabled = !autoSnappingEnabled;
                return true;
            }
            curY += 36 + 6;

            // Card 3: Strict Bounds Check Toggle
            toggleBtnY = curY + 9;
            if (mouseX >= toggleBtnX && mouseX <= toggleBtnX + toggleBtnW && mouseY >= toggleBtnY && mouseY <= toggleBtnY + toggleBtnH) {
                strictBoundsCheck = !strictBoundsCheck;
                return true;
            }
        } else if ("system".equals(current.id)) {
            // Card 1: Speed cycle
            int cycleBtnW = 72;
            int cycleBtnH = 18;
            int cycleBtnX = cardX + cardW - cycleBtnW - 8;
            int cycleBtnY = curY + 9;
            if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH) {
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
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int winW = Math.min(width - 24, 520);
        int winX = (width - winW) / 2;
        return winX + currentNavW;
    }

    public int getContentY() {
        int winH = Math.min(height - 24, 300);
        int winY = (height - winH) / 2;
        return winY + 30;
    }

    public int getContentWidth() {
        int currentNavW = Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
        int winW = Math.min(width - 24, 520);
        return winW - currentNavW;
    }

    public int getContentHeight() {
        int winH = Math.min(height - 24, 300);
        return winH - 30;
    }

    public int getSidebarWidth() {
        return Math.round(COLLAPSED_NAV_WIDTH + (EXPANDED_NAV_WIDTH - COLLAPSED_NAV_WIDTH) * navProgress);
    }

    public static int scaleSize(int baseSize) { return baseSize; }
    public static int getScaledButtonHeight() { return 20; }
    public static int getScaledEditBoxHeight() { return 18; }
    public static float getStandardTextScale() { return 1.0f; }
}
