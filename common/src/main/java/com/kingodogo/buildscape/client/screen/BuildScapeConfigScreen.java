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
import com.kingodogo.buildscape.network.UpdatePillarDataPacket;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;

/**
 * Unified Single-Panel Buildscape Configuration Screen.
 * Design Invariants:
 * - 1 Big Unified Panel with sleek 1-pixel wide metallic border gradient.
 * - Items Tab with authentic inventory slot grid and toggle selection.
 * - Draggable 2D Color Picker (Saturation x Brightness + Hue spectrum slider).
 * - Pillar IDs Tab with placed pillar listing and full per-pillar customization.
 * - Spill-proof scrollable viewport (no rows spill past bottom frame).
 * - Normalized professional naming and descriptions.
 * - Direct real-time binding to real Buildscape configs & network packets.
 * - Solid opaque charcoal & slate palette (no 75% transparency).
 * - Cubic easing open/close animations.
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

    // --- Palette Constants (Solid Opaque) ---
    public static final int COLOR_OVERLAY = 0xD808080A;
    public static final int COLOR_PANEL_BG = 0xFF161618;
    public static final int COLOR_INNER_DARK = 0xFF101012;
    public static final int COLOR_HEADER_BG = 0xFF1E1E22;

    // Metallic Beveled Border Gradients (1px wide)
    public static final int COLOR_FRAME_BLACK = 0xFF000000;
    public static final int COLOR_BORDER_GRADIENT_START = 0xFF848492;
    public static final int COLOR_BORDER_GRADIENT_END = 0xFF3C3C44;
    public static final int COLOR_BORDER_SHADOW = 0xFF18181E;

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
            "none", "ring", "beam", "spiral", "fountain", "pulse", "burst", "snowflake"
    };

    // --- Presets & Item Cache ---
    private int selectedPresetIndex = 0;
    private int itemFilterMode = 0; // 0=All Items, 1=Inventory, 2=Configured
    private final List<Item> availableItems = new ArrayList<>();
    private final Map<Item, ItemStack> itemStackCache = new HashMap<>();
    private final Map<String, ItemStack> stringToStackCache = new HashMap<>();

    // --- Draggable Color Picker Modal State ---
    private boolean showColorModal = false;
    private boolean isEditingPillarColor = false;
    private String editingPillarId = null;
    private int editingColorIndex = 0;
    private float pickerHue = 42.0f;       // 0 to 360
    private float pickerSat = 0.89f;      // 0.0 to 1.0
    private float pickerBri = 1.0f;       // 0.0 to 1.0
    private boolean draggingGradient = false;
    private boolean draggingHue = false;

    private static final int[] PALETTE_PRESETS = {
            0xFFFFFFFF, 0xFFFFB81C, 0xFFFF3B30, 0xFF34C759,
            0xFF00C7BE, 0xFF5856D6, 0xFFFF9500, 0xFFFF2D55,
            0xFF3CDFFF, 0xFFE8FEFD, 0xFF8E8E93, 0xFF1C1C1E
    };

    // --- Per-Pillar Customization Detail View ---
    private String selectedPillarDetailId = null;

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
        cacheAvailableItems();
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem("items", "☵", "Items", "Configure activation trigger items and presets"));
        navItems.add(new NavItem("particles", "✦", "Particles", "Configure kinetic trajectory and emission mechanics"));
        navItems.add(new NavItem("pillars", "▲", "Pillar IDs", "Manage and customize in-world synchronized pillar columns"));
        navItems.add(new NavItem("world", "⚙", "World Settings", "Control world griefing, felling, and inventory mechanics"));
        navItems.add(new NavItem("system", "ℹ", "System", "Engine architecture and client preferences"));
    }

    private void cacheAvailableItems() {
        if (!availableItems.isEmpty()) return;
        for (Item item : Services.PLATFORM.getAllItems()) {
            if (item != null && item != Items.AIR) {
                availableItems.add(item);
            }
        }
        if (availableItems.isEmpty()) {
            for (int rawId = 1; rawId < 2500; rawId++) {
                Item item = Services.PLATFORM.getItemByRawId(rawId);
                if (item != null && item != Items.AIR) {
                    availableItems.add(item);
                }
            }
        }
    }

    private List<Item> getPlayerInventoryItems() {
        Set<Item> set = new LinkedHashSet<>();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
                ItemStack stack = mc.player.getInventory().getItem(i);
                if (!stack.isEmpty() && stack.getItem() != Items.AIR) {
                    set.add(stack.getItem());
                }
            }
        }
        return new ArrayList<>(set);
    }

    private List<Item> getConfiguredItemsList() {
        List<Item> result = new ArrayList<>();
        PillarParticleConfig cfg = PillarParticleConfig.get();
        for (String idStr : cfg.items) {
            if (!idStr.startsWith("#")) {
                CommonId cid = CommonId.tryParse(idStr);
                if (cid != null) {
                    Item item = Services.PLATFORM.getItem(cid);
                    if (item != null && item != Items.AIR) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    private ItemStack getStackForIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty() || identifier.startsWith("#")) {
            return null;
        }
        return stringToStackCache.computeIfAbsent(identifier, idStr -> {
            CommonId cid = CommonId.tryParse(idStr);
            if (cid != null) {
                Item item = Services.PLATFORM.getItem(cid);
                if (item != null && item != Items.AIR) {
                    return new ItemStack(item);
                }
            }
            return null;
        });
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

    // --- Compatibility Methods for Legacy Widgets & Tabs ---
    private static final int REFERENCE_WIDTH = 1920;
    private static final int REFERENCE_HEIGHT = 1080;
    private static final double REFERENCE_GUI_SCALE = 2.0;

    public static double calculateScaleFactor() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return 1.0;
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();
        double currentGuiScale = mc.getWindow().getGuiScale();
        int effectiveWidth = (int) (windowWidth / currentGuiScale);
        int effectiveHeight = (int) (windowHeight / currentGuiScale);
        double widthScale = (double) effectiveWidth / REFERENCE_WIDTH;
        double heightScale = (double) effectiveHeight / REFERENCE_HEIGHT;
        double resolutionScale = Math.min(widthScale, heightScale);
        double guiScaleFactor = currentGuiScale / REFERENCE_GUI_SCALE;
        double combinedScale = (resolutionScale * 0.7) + (guiScaleFactor * 0.3);
        return Math.max(0.5, Math.min(2.0, combinedScale));
    }

    public static int scaleSize(int baseSize) {
        double scaleFactor = calculateScaleFactor();
        int scaled = (int) (baseSize * scaleFactor);
        return (scaled / 2) * 2;
    }

    public static int scaleSizeConstrained(int baseSize, int minSize, int maxSize) {
        int scaled = scaleSize(baseSize);
        return Math.max(minSize, Math.min(maxSize, scaled));
    }

    public static int getScaledSpacing() {
        return scaleSizeConstrained(10, 6, 20);
    }

    public static int getScaledCategoryButtonHeight() {
        return scaleSizeConstrained(20, 16, 32);
    }

    public static int getScaledCategoryButtonSpacing() {
        return scaleSize(2);
    }

    public static int getScaledButtonHeight() {
        return scaleSizeConstrained(20, 16, 32);
    }

    public static int getScaledEditBoxHeight() {
        return scaleSizeConstrained(20, 16, 32);
    }

    public static float getStandardTextScale() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return 1.0f;
        double guiScale = mc.getWindow().getGuiScale();
        if (guiScale >= 3.0) return 0.75f;
        if (guiScale >= 2.5) return 0.85f;
        return 1.0f;
    }

    public int getContentX() {
        return (int) (width * 0.15);
    }

    public int getContentY() {
        return 30;
    }

    public int getContentWidth() {
        return (int) (width * 0.42);
    }

    public int getRightPanelX() {
        return getContentX() + getContentWidth() + 10;
    }

    public int getRightPanelWidth() {
        return (int) (width * 0.42);
    }

    public int getContentHeight() {
        return height - 40;
    }

    public int getVerticalPanelGap() {
        return 4;
    }

    public AbstractConfigTab getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(AbstractConfigTab tab) {
        if (this.activeTab != null) {
            this.activeTab.onClose();
        }
        this.activeTab = tab;
        if (this.activeTab != null) {
            this.activeTab.init();
        }
    }

    public void addTabWidget(GuiEventListener widget) {
        if (widget instanceof AbstractWidget abstractWidget && addWidgetConsumer != null) {
            this.addWidgetConsumer.accept(abstractWidget);
        }
        tabWidgets.add(widget);
    }

    public GuiEventListener addCustomWidget(com.kingodogo.buildscape.client.screen.widget.ICustomWidget widget) {
        if (widget == null || addWidgetConsumer == null) return null;
        AbstractWidget nativeWidget = Services.PLATFORM.wrapCustomWidget(
                widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(),
                ComponentHelper.literal(""), widget);
        addWidgetConsumer.accept(nativeWidget);
        tabWidgets.add(nativeWidget);
        return nativeWidget;
    }

    public void removeTabWidget(GuiEventListener widget) {
        if (widget != null && removeWidgetConsumer != null) {
            this.removeWidgetConsumer.accept(widget);
        }
        tabWidgets.remove(widget);
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
        int panelW = Math.min(width - 20, 560);
        int panelH = Math.min(height - 16, 304);
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        int offsetY = Math.round((1.0f - alpha) * 8.0f);
        panelY += offsetY;

        // 3. Render 1 Big Frame with sleek 1-pixel wide metallic gradient
        drawVaultHuntersFrame(poseStackOrGraphics, panelX, panelY, panelW, panelH, COLOR_PANEL_BG, alpha);

        // 4. Header Bar
        int headerH = 26;
        int headerX = panelX + 3;
        int headerY = panelY + 3;
        int headerW = panelW - 6;
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
        int navX = panelX + 4;
        int navY = headerY + headerH + 4;
        int navH = panelH - headerH - 12;

        int contentX = navX + navW + 4;
        int contentY = navY;
        int contentW = (panelX + panelW - 4) - contentX;
        int contentH = navH;

        // Render Left Navigation Strip
        renderSidenav(poseStackOrGraphics, font, navX, navY, navW, navH, mouseX, mouseY, alpha);

        // Render Right Main Configuration Content Area
        renderContentArea(poseStackOrGraphics, font, contentX, contentY, contentW, contentH, mouseX, mouseY, alpha);

        // 6. Render Draggable Color Picker Modal
        if (showColorModal) {
            renderColorPickerModal(poseStackOrGraphics, font, mouseX, mouseY, alpha);
        }

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

        int catHeaderH = 22;
        drawText(poseStackOrGraphics, font, current.icon + "  " + current.title.toUpperCase(), x + 8, y + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, current.subtitle, x + 8 + font.width(current.icon + "  " + current.title.toUpperCase()) + 10, y + 7, applyAlpha(COLOR_TEXT_MUTED, alpha));
        drawGroove(poseStackOrGraphics, x + 2, y + catHeaderH, w - 4, true, alpha);

        // Strict Spill-Proof Viewport
        int viewY = y + catHeaderH + 4;
        int viewH = h - catHeaderH - 8;
        int rowH = 26;
        int rowGap = 3;

        int rowW = w - 14;
        int rowX = x + 6;

        int totalContentHeight = calculateTotalContentHeight(current.id, rowW, rowH, rowGap);
        maxScroll = Math.max(0, totalContentHeight - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        int startY = viewY + 2 - (int) scrollOffset;

        switch (current.id) {
            case "items" -> renderItemInventoryGrid(poseStackOrGraphics, font, rowX, startY, rowW, viewY, viewH, mouseX, mouseY, alpha);
            case "particles" -> renderParticleSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "pillars" -> {
                if (selectedPillarDetailId != null) {
                    renderPillarDetailRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
                } else {
                    renderPillarIdRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
                }
            }
            case "world" -> renderWorldSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "system" -> renderSystemRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
        }

        // Custom Scrollbar
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

    private int calculateTotalContentHeight(String tabId, int contentW, int rowH, int gap) {
        if ("items".equals(tabId)) {
            int slotSize = 20;
            int slotGap = 2;
            int cols = Math.max(1, (contentW - 12) / (slotSize + slotGap));
            List<Item> items = getFilteredItemsList();
            int rows = (int) Math.ceil((double) items.size() / cols);
            return 64 + rows * (slotSize + slotGap) + 16;
        }
        if ("pillars".equals(tabId) && selectedPillarDetailId != null) {
            return 8 * (rowH + gap) + 16;
        }
        int count = switch (tabId) {
            case "particles" -> 11;
            case "pillars" -> 3 + Math.max(1, PillarIdManager.getClient().getPillarCount());
            case "world" -> 8;
            case "system" -> 5;
            default -> 0;
        };
        return count * (rowH + gap) + 6;
    }

    private List<Item> getFilteredItemsList() {
        return switch (itemFilterMode) {
            case 1 -> getPlayerInventoryItems();
            case 2 -> getConfiguredItemsList();
            default -> availableItems;
        };
    }

    // --- Tab 0: Items (Authentic Inventory Slot Matrix & Toggle Selection) ---
    private void renderItemInventoryGrid(Object poseStackOrGraphics, Font font, int x, int startY, int w, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        PresetsConfig presetsCfg = PresetsConfig.get();
        List<PresetsConfig.Preset> presets = presetsCfg.getPresets();
        String currentPresetName = presets.isEmpty() ? "Default" : presets.get(Math.min(selectedPresetIndex, presets.size() - 1)).name;

        int curY = startY;

        // Top Bar 1: Preset & Mode Filters
        int topBarH = 22;
        if (curY >= clipY - topBarH && curY + topBarH <= clipY + clipH + topBarH) {
            if (curY >= clipY && curY + topBarH <= clipY + clipH) {
                // Preset cycle
                int cycleW = 74;
                int btnH = 14;
                int cycleX = x + 2;
                int cycleY = curY + 4;
                boolean hoverCycle = mouseX >= cycleX && mouseX <= cycleX + cycleW && mouseY >= cycleY && mouseY <= cycleY + btnH;
                Services.PLATFORM.fill(poseStackOrGraphics, cycleX, cycleY, cycleX + cycleW, cycleY + btnH, applyAlpha(hoverCycle ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
                drawBorder(poseStackOrGraphics, cycleX, cycleY, cycleW, btnH, applyAlpha(hoverCycle ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "‹ " + currentPresetName + " ›", cycleX + cycleW / 2, cycleY + 3, applyAlpha(hoverCycle ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

                int applyW = 38;
                int applyX = cycleX + cycleW + 3;
                boolean hoverApply = mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= cycleY && mouseY <= cycleY + btnH;
                Services.PLATFORM.fill(poseStackOrGraphics, applyX, cycleY, applyX + applyW, cycleY + btnH, applyAlpha(hoverApply ? 0xFF1C3C22 : COLOR_TOGGLE_ON_BG, alpha));
                drawBorder(poseStackOrGraphics, applyX, cycleY, applyW, btnH, applyAlpha(COLOR_TOGGLE_ON_BORDER, alpha));
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Apply", applyX + applyW / 2, cycleY + 3, applyAlpha(COLOR_TOGGLE_ON_TEXT, alpha));

                int saveW = 36;
                int saveX = applyX + applyW + 3;
                boolean hoverSave = mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= cycleY && mouseY <= cycleY + btnH;
                Services.PLATFORM.fill(poseStackOrGraphics, saveX, cycleY, saveX + saveW, cycleY + btnH, applyAlpha(hoverSave ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
                drawBorder(poseStackOrGraphics, saveX, cycleY, saveW, btnH, applyAlpha(hoverSave ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Save", saveX + saveW / 2, cycleY + 3, applyAlpha(hoverSave ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

                // Filter Buttons: [All] [Inventory] [Configured]
                String[] filterLabels = {"All Items", "Inventory", "Active (" + cfg.items.size() + ")"};
                int filterW = 60;
                int rightFiltersX = x + w - (filterLabels.length * (filterW + 3));
                for (int f = 0; f < filterLabels.length; f++) {
                    int fx = rightFiltersX + f * (filterW + 3);
                    boolean isFActive = (itemFilterMode == f);
                    boolean hoverF = mouseX >= fx && mouseX <= fx + filterW && mouseY >= cycleY && mouseY <= cycleY + btnH;
                    int fBg = isFActive ? 0xFF2A2A38 : (hoverF ? 0xFF24242C : 0xFF16161A);
                    int fBorder = isFActive ? 0xFF64647C : (hoverF ? 0xFF40404C : 0xFF26262E);
                    Services.PLATFORM.fill(poseStackOrGraphics, fx, cycleY, fx + filterW, cycleY + btnH, applyAlpha(fBg, alpha));
                    drawBorder(poseStackOrGraphics, fx, cycleY, filterW, btnH, applyAlpha(fBorder, alpha));
                    int fText = isFActive ? COLOR_TEXT_WHITE : (hoverF ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                    Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, filterLabels[f], fx + filterW / 2, cycleY + 3, applyAlpha(fText, alpha));
                }
            }
        }
        curY += topBarH;

        // Top Bar 2: Quick Tag Chips Row
        int tagBarH = 18;
        if (curY >= clipY && curY + tagBarH <= clipY + clipH) {
            drawText(poseStackOrGraphics, font, "Tags:", x + 4, curY + 4, applyAlpha(COLOR_TEXT_MUTED, alpha));

            String[] tagIds = {"#the_vault:keys", "#the_vault:gems", "#c:ores", "#the_vault:vault_gear"};
            String[] tagNames = {"#keys", "#gems", "#ores", "#vault"};
            int chipW = 54;
            int chipH = 13;
            int chipStartX = x + 38;

            for (int t = 0; t < tagIds.length; t++) {
                int chipX = chipStartX + t * (chipW + 4);
                boolean isTagActive = cfg.items.contains(tagIds[t]);
                boolean hoverChip = mouseX >= chipX && mouseX <= chipX + chipW && mouseY >= curY + 2 && mouseY <= curY + 2 + chipH;

                int chipBg = isTagActive ? 0xFF142E18 : (hoverChip ? 0xFF2A2A34 : 0xFF18181E);
                int chipBorder = isTagActive ? 0xFF4ADE80 : (hoverChip ? 0xFF545464 : 0xFF2C2C36);
                Services.PLATFORM.fill(poseStackOrGraphics, chipX, curY + 2, chipX + chipW, curY + 2 + chipH, applyAlpha(chipBg, alpha));
                drawBorder(poseStackOrGraphics, chipX, curY + 2, chipW, chipH, applyAlpha(chipBorder, alpha));

                int chipText = isTagActive ? COLOR_TOGGLE_ON_TEXT : (hoverChip ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER);
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, tagNames[t], chipX + chipW / 2, curY + 4, applyAlpha(chipText, alpha));
            }

            // Defaults button
            int defBtnW = 60;
            int defBtnX = x + w - defBtnW - 2;
            boolean hoverDef = mouseX >= defBtnX && mouseX <= defBtnX + defBtnW && mouseY >= curY + 2 && mouseY <= curY + 2 + chipH;
            Services.PLATFORM.fill(poseStackOrGraphics, defBtnX, curY + 2, defBtnX + defBtnW, curY + 2 + chipH, applyAlpha(hoverDef ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, defBtnX, curY + 2, defBtnW, chipH, applyAlpha(hoverDef ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "↺ Defaults", defBtnX + defBtnW / 2, curY + 4, applyAlpha(hoverDef ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        }
        curY += tagBarH + 4;

        // Inventory Slot Matrix
        List<Item> itemsToDisplay = getFilteredItemsList();
        int slotSize = 20;
        int slotGap = 2;
        int cols = Math.max(1, (w - 12) / (slotSize + slotGap));
        int gridStartX = x + 4;

        ItemStack hoveredStack = null;
        boolean hoveredIsActive = false;

        for (int i = 0; i < itemsToDisplay.size(); i++) {
            Item item = itemsToDisplay.get(i);
            int col = i % cols;
            int row = i / cols;
            int slotX = gridStartX + col * (slotSize + slotGap);
            int slotY = curY + row * (slotSize + slotGap);

            if (slotY + slotSize < clipY || slotY > clipY + clipH) {
                continue;
            }

            CommonId cid = Services.PLATFORM.getItemId(item);
            String idStr = cid != null ? cid.toString() : "";
            boolean isSelected = cfg.items.contains(idStr);

            boolean hover = mouseX >= slotX && mouseX <= slotX + slotSize && mouseY >= slotY && mouseY <= slotY + slotSize &&
                    mouseY >= clipY && mouseY <= clipY + clipH;

            // Slot Background (Beveled inventory slot look)
            int bg = isSelected ? 0xFF142E18 : (hover ? 0xFF24242C : 0xFF121216);
            Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY, slotX + slotSize, slotY + slotSize, applyAlpha(bg, alpha));

            // Slot Border
            int border = isSelected ? 0xFF4ADE80 : (hover ? 0xFF6A6A7A : 0xFF24242C);
            drawBorder(poseStackOrGraphics, slotX, slotY, slotSize, slotSize, applyAlpha(border, alpha));

            // 3D Item Render
            ItemStack stack = itemStackCache.computeIfAbsent(item, ItemStack::new);
            Services.PLATFORM.renderGuiItem(poseStackOrGraphics, stack, slotX + 2, slotY + 2);

            // Active check badge indicator
            if (isSelected) {
                Services.PLATFORM.fill(poseStackOrGraphics, slotX + slotSize - 6, slotY, slotX + slotSize, slotY + 6, applyAlpha(0xFF4ADE80, alpha));
            }

            if (hover) {
                hoveredStack = stack;
                hoveredIsActive = isSelected;
            }
        }

        // Render hover tooltip
        if (hoveredStack != null) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(hoveredStack.getHoverName());
            tooltip.add(ComponentHelper.literal(hoveredIsActive ? "§a[Active Trigger - Click to Remove]" : "§7[Click to Add Trigger]"));
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, font, tooltip, mouseX + 12, mouseY);
        }
    }

    // --- Tab 1: Particles (Full Controls & Color Swatch Customizer) ---
    private void renderParticleSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        int curY = startY;

        // 1. Mode Toggle
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Use Particle Pattern", "Enable kinetic geometric pattern particle solver", cfg.use_pattern, mouseX, mouseY, alpha);
        curY += h + gap;

        // 2. Pattern Cycle
        String patternDisplay = cfg.pattern.substring(0, 1).toUpperCase() + cfg.pattern.substring(1);
        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Trajectory Pattern", "Current geometric particle animation pattern", patternDisplay, mouseX, mouseY, alpha);
        curY += h + gap;

        // 3. Pattern Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Speed", "Rotational velocity multiplier for animated patterns", String.format("%.2f", cfg.pattern_speed), mouseX, mouseY, alpha);
        curY += h + gap;

        // 4. Pattern Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Spread", "Dispersion radius factor for geometric patterns", String.format("%.2f", cfg.pattern_spread), mouseX, mouseY, alpha);
        curY += h + gap;

        // 5. Pattern Intensity
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Intensity", "Impulse power multiplier for geometric patterns", String.format("%.1fx", cfg.pattern_intensity), mouseX, mouseY, alpha);
        curY += h + gap;

        // 6. Basic Particle Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Speed", "Velocity magnitude for base pillar particles", String.format("%.2f", cfg.particle_speed), mouseX, mouseY, alpha);
        curY += h + gap;

        // 7. Basic Particle Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Spread", "Radial dispersion angle for base pillar particles", String.format("%.2f", cfg.particle_spread), mouseX, mouseY, alpha);
        curY += h + gap;

        // 8. Particle Lifetime
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Lifetime", "Duration in ticks before emitted particles despawn", cfg.particle_lifetime + " ticks", mouseX, mouseY, alpha);
        curY += h + gap;

        // 9. Particle Density
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Density", "Quantity of particles emitted per pillar column", cfg.particle_density + "x", mouseX, mouseY, alpha);
        curY += h + gap;

        // 10. Swatches & Active Colors
        renderConfigRowPalette(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Color Palette (Click to Drag & Pick)", cfg.particle_color, cfg.max_particle_color, mouseX, mouseY, alpha);
        curY += h + gap;

        // 11. Reset Colors Action
        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Reset Palette", "Restore default hexadecimal color swatches", "↺ Reset Colors", mouseX, mouseY, alpha);
    }

    // --- Tab 2: Pillar IDs List View ---
    private void renderPillarIdRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        int pillarCount = PillarIdManager.getClient().getPillarCount();
        int curY = startY;

        // Row 0: Synchronizer & Global Actions
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar Synchronizer", pillarCount + " authoritative pillar columns mapped in world", mouseX, mouseY, alpha);

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
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Auto-Snapping Grid", "Snap pillar placement cursor to the nearest voxel grid node", autoSnappingEnabled, mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 2: Strict collision
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Strict Collision Check", "Prevent placing pillars overlapping with non-air blocks", strictBoundsCheck, mouseX, mouseY, alpha);
        curY += h + gap;

        // Rows 3+: Individual Pillar Rows with Customize & Remove Buttons
        Collection<PillarIdManager.PillarData> pillars = PillarIdManager.getClient().getAllData();
        if (pillars.isEmpty()) {
            if (curY >= clipY && curY + h <= clipY + clipH) {
                renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "No Pillars Registered", "Place pillar blocks in world to synchronize columns", "Empty", alpha);
            }
        } else {
            for (PillarIdManager.PillarData data : pillars) {
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    String patternDesc = (data.pattern != null && !data.pattern.isEmpty()) ? " · Pattern: " + data.pattern : "";
                    String desc = String.format("Pos: [%d, %d, %d]%s", data.x, data.y, data.z, patternDesc);
                    renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar: " + data.id, desc, mouseX, mouseY, alpha);

                    // Customize Button
                    int custW = 64;
                    int btnH = 14;
                    int custX = x + w - custW - 58;
                    int custY = curY + (h - btnH) / 2;
                    boolean hoverCust = mouseX >= custX && mouseX <= custX + custW && mouseY >= custY && mouseY <= custY + btnH;
                    Services.PLATFORM.fill(poseStackOrGraphics, custX, custY, custX + custW, custY + btnH, applyAlpha(hoverCust ? 0xFF2A2A38 : COLOR_BTN_BG, alpha));
                    drawBorder(poseStackOrGraphics, custX, custY, custW, btnH, applyAlpha(hoverCust ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
                    Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✎ Customize", custX + custW / 2, custY + 3, applyAlpha(hoverCust ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

                    // Remove Button
                    int removeW = 50;
                    int removeX = x + w - removeW - 6;
                    int removeY = custY;
                    boolean hoverRem = mouseX >= removeX && mouseX <= removeX + removeW && mouseY >= removeY && mouseY <= removeY + btnH;
                    Services.PLATFORM.fill(poseStackOrGraphics, removeX, removeY, removeX + removeW, removeY + btnH, applyAlpha(hoverRem ? 0xFF3D1616 : 0xFF241414, alpha));
                    drawBorder(poseStackOrGraphics, removeX, removeY, removeW, btnH, applyAlpha(hoverRem ? 0xFFE05050 : 0xFF882828, alpha));
                    Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Remove", removeX + removeW / 2, removeY + 3, applyAlpha(hoverRem ? 0xFFFFFFFF : 0xFFE05050, alpha));
                }
                curY += h + gap;
            }
        }
    }

    // --- Tab 2 (Detail): Per-Pillar Customization View ---
    private void renderPillarDetailRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(selectedPillarDetailId);
        if (data == null) {
            selectedPillarDetailId = null;
            return;
        }

        int curY = startY;

        // Row 0: Back Button & Header Info
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar: " + data.id, String.format("Pos: [%d, %d, %d] · Dim: %s", data.x, data.y, data.z, data.dimension), mouseX, mouseY, alpha);

            int backW = 68;
            int btnH = 14;
            int backX = x + w - backW - 6;
            int backY = curY + (h - btnH) / 2;
            boolean hoverBack = mouseX >= backX && mouseX <= backX + backW && mouseY >= backY && mouseY <= backY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, backX, backY, backX + backW, backY + btnH, applyAlpha(hoverBack ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, backX, backY, backW, btnH, applyAlpha(hoverBack ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "◀ Back", backX + backW / 2, backY + 3, applyAlpha(hoverBack ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        }
        curY += h + gap;

        // Row 1: Use Pattern Override
        boolean usePat = data.use_pattern != null ? data.use_pattern : false;
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Use Custom Pattern", "Override global particle pattern for this specific pillar", usePat, mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 2: Pattern Cycle
        String curPat = data.pattern != null && !data.pattern.isEmpty() ? data.pattern : "default";
        String patDisplay = curPat.substring(0, 1).toUpperCase() + curPat.substring(1);
        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Trajectory Pattern", "Geometric animation solver for this pillar", patDisplay, mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 3: Pattern Speed
        double speed = data.pattern_speed != null ? data.pattern_speed : PillarParticleConfig.get().pattern_speed;
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Speed", "Rotational speed multiplier for this pillar", String.format("%.2f", speed), mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 4: Pattern Spread
        double spread = data.pattern_spread != null ? data.pattern_spread : PillarParticleConfig.get().pattern_spread;
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Spread", "Dispersion radius factor for this pillar", String.format("%.2f", spread), mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 5: Pattern Intensity
        double intensity = data.pattern_intensity != null ? data.pattern_intensity : PillarParticleConfig.get().pattern_intensity;
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Intensity", "Impulse power multiplier for this pillar", String.format("%.1fx", intensity), mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 6: Per-Pillar Color Palette Swatches
        List<String> colors = data.dyeColors != null ? data.dyeColors : new ArrayList<>();
        int maxColors = data.max_particle_color != null ? data.max_particle_color : Math.min(5, Math.max(1, colors.size()));
        renderConfigRowPalette(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar Colors (Click to Drag & Pick)", colors, maxColors, mouseX, mouseY, alpha);
        curY += h + gap;

        // Row 7: Save & Sync Pillar Configuration
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Save Pillar Changes", "Send synchronized particle updates to server and clients", mouseX, mouseY, alpha);

            int saveW = 80;
            int btnH = 14;
            int saveX = x + w - saveW - 6;
            int saveY = curY + (h - btnH) / 2;
            boolean hoverSave = mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= saveY && mouseY <= saveY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, saveX, saveY, saveX + saveW, saveY + btnH, applyAlpha(hoverSave ? 0xFF1C3C22 : COLOR_TOGGLE_ON_BG, alpha));
            drawBorder(poseStackOrGraphics, saveX, saveY, saveW, btnH, applyAlpha(COLOR_TOGGLE_ON_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✓ Save Pillar", saveX + saveW / 2, saveY + 3, applyAlpha(COLOR_TOGGLE_ON_TEXT, alpha));
        }
    }

    // --- Tab 3: World Settings Rows (Normalized Professional Descriptions) ---
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

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Creative Tree Breaker", "Break entire trees at once in Creative mode.", treeBreaker, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Shulker Box Preview", "Preview shulker box inventory contents on hover.", shulkerPeek, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Fast Leaf Decay", "Leaves decay rapidly when tree trunks are broken.", fastLeaves, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Disable Enderman Griefing", "Prevent Endermen from picking up or placing blocks.", endermanGrief, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Disable Creeper Griefing", "Prevent Creepers from destroying blocks when exploding.", creeperGrief, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Disable Ghast Griefing", "Prevent Ghast fireballs from destroying blocks.", ghastGrief, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Cake Stacking", "Allow cakes to stack up to 64 in player inventories.", cakeStack, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Water Bottle Stacking", "Allow water bottles to stack up to 16 in player inventories.", waterStack, mouseX, mouseY, alpha);
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

    // --- Draggable Color Picker Modal (2D Saturation x Brightness + Hue Spectrum Slider) ---
    private void renderColorPickerModal(Object poseStackOrGraphics, Font font, int mouseX, int mouseY, float alpha) {
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(0xA0000000, alpha));

        int modalW = 340;
        int modalH = 196;
        int modalX = (width - modalW) / 2;
        int modalY = (height - modalH) / 2;

        drawVaultHuntersFrame(poseStackOrGraphics, modalX, modalY, modalW, modalH, COLOR_PANEL_BG, alpha);

        int headH = 24;
        Services.PLATFORM.fill(poseStackOrGraphics, modalX + 3, modalY + 3, modalX + modalW - 3, modalY + headH, applyAlpha(COLOR_HEADER_BG, alpha));
        drawGroove(poseStackOrGraphics, modalX + 3, modalY + headH, modalW - 6, true, alpha);
        String modalTitle = isEditingPillarColor ? "Customize Pillar Color" : "Customize Particle Color";
        drawText(poseStackOrGraphics, font, modalTitle, modalX + 8, modalY + 8, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Close button
        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 6;
        int closeY = modalY + 5;
        boolean hoverClose = mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize;
        Services.PLATFORM.fill(poseStackOrGraphics, closeX, closeY, closeX + closeSize, closeY + closeSize, applyAlpha(hoverClose ? 0xFF3D1616 : 0xFF241414, alpha));
        drawBorder(poseStackOrGraphics, closeX, closeY, closeSize, closeSize, applyAlpha(hoverClose ? 0xFFE05050 : 0xFF882828, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeX + closeSize / 2, closeY + 3, applyAlpha(hoverClose ? 0xFFFFFFFF : 0xFFE05050, alpha));

        // 1. 2D Saturation x Brightness Draggable Box
        int gradBoxX = modalX + 16;
        int gradBoxY = modalY + headH + 12;
        int gradBoxW = 110;
        int gradBoxH = 88;

        // Render 2D Gradient Box (Hue as base, Saturation horizontally, Brightness vertically)
        for (int x = 0; x < gradBoxW; x += 2) {
            float sat = x / (float) gradBoxW;
            int topRgb = hsbToRgb(pickerHue, sat, 1.0f);
            for (int y = 0; y < gradBoxH; y += 2) {
                float bri = 1.0f - (y / (float) gradBoxH);
                int pixelCol = scaleBrightness(topRgb, bri);
                Services.PLATFORM.fill(poseStackOrGraphics, gradBoxX + x, gradBoxY + y, gradBoxX + x + 2, gradBoxY + y + 2, applyAlpha(pixelCol, alpha));
            }
        }
        drawBorder(poseStackOrGraphics, gradBoxX - 1, gradBoxY - 1, gradBoxW + 2, gradBoxH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        // Crosshair Indicator on 2D Box
        int curCrossX = gradBoxX + (int) (pickerSat * gradBoxW);
        int curCrossY = gradBoxY + (int) ((1.0f - pickerBri) * gradBoxH);
        drawBorder(poseStackOrGraphics, curCrossX - 3, curCrossY - 3, 6, 6, applyAlpha(COLOR_FRAME_BLACK, alpha));
        drawBorder(poseStackOrGraphics, curCrossX - 2, curCrossY - 2, 4, 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // 2. Vertical Hue Rainbow Slider
        int hueX = gradBoxX + gradBoxW + 10;
        int hueY = gradBoxY;
        int hueW = 16;
        int hueH = gradBoxH;

        for (int y = 0; y < hueH; y += 2) {
            float h = (y / (float) hueH) * 360.0f;
            int col = hsbToRgb(h, 1.0f, 1.0f);
            Services.PLATFORM.fill(poseStackOrGraphics, hueX, hueY + y, hueX + hueW, hueY + y + 2, applyAlpha(col, alpha));
        }
        drawBorder(poseStackOrGraphics, hueX - 1, hueY - 1, hueW + 2, hueH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        // Hue Slider Pointer
        int curHueY = hueY + (int) ((pickerHue / 360.0f) * hueH);
        Services.PLATFORM.fill(poseStackOrGraphics, hueX - 2, curHueY - 1, hueX + hueW + 2, curHueY + 2, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawBorder(poseStackOrGraphics, hueX - 2, curHueY - 1, hueW + 4, 3, applyAlpha(COLOR_FRAME_BLACK, alpha));

        // 3. Right Side: Current Color Preview Box & Hex String
        int currentColorInt = hsbToRgb(pickerHue, pickerSat, pickerBri);
        int r = (currentColorInt >> 16) & 0xFF;
        int g = (currentColorInt >> 8) & 0xFF;
        int b = currentColorInt & 0xFF;
        String hexString = String.format("#%02X%02X%02X", r, g, b);

        int previewX = hueX + hueW + 14;
        int previewY = gradBoxY;
        int previewSize = 36;
        Services.PLATFORM.fill(poseStackOrGraphics, previewX, previewY, previewX + previewSize, previewY + previewSize, applyAlpha(currentColorInt, alpha));
        drawBorder(poseStackOrGraphics, previewX, previewY, previewSize, previewSize, applyAlpha(COLOR_FRAME_BLACK, alpha));

        drawText(poseStackOrGraphics, font, hexString, previewX, previewY + previewSize + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // 4. Preset Swatches Grid (12 Vibrant Presets)
        int presetStartX = previewX + previewSize + 8;
        int presetStartY = previewY;
        int swatchSize = 13;
        int swatchGap = 3;
        for (int p = 0; p < PALETTE_PRESETS.length; p++) {
            int px = presetStartX + (p % 4) * (swatchSize + swatchGap);
            int py = presetStartY + (p / 4) * (swatchSize + swatchGap);
            Services.PLATFORM.fill(poseStackOrGraphics, px, py, px + swatchSize, py + swatchSize, applyAlpha(PALETTE_PRESETS[p], alpha));
            drawBorder(poseStackOrGraphics, px, py, swatchSize, swatchSize, applyAlpha(0xFF000000, alpha));
        }

        // 5. Apply Button
        int applyW = 90;
        int applyH = 16;
        int applyX = modalX + modalW - applyW - 16;
        int applyY = modalY + modalH - applyH - 12;
        boolean hoverApply = mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + applyH;
        Services.PLATFORM.fill(poseStackOrGraphics, applyX, applyY, applyX + applyW, applyY + applyH, applyAlpha(hoverApply ? 0xFF1C3C22 : COLOR_TOGGLE_ON_BG, alpha));
        drawBorder(poseStackOrGraphics, applyX, applyY, applyW, applyH, applyAlpha(COLOR_TOGGLE_ON_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✓ Apply Color", applyX + applyW / 2, applyY + 4, applyAlpha(COLOR_TOGGLE_ON_TEXT, alpha));
    }

    // --- Vault Hunters Border & Bevel Engine (Sleek 1-Pixel Wide Metallic Gradient) ---
    private void drawVaultHuntersFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bgRgb, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);

        // 1. Layer 1: Outermost 1px Black Border
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);

        // 2. Layer 2: Strictly 1-Pixel Wide Metallic Gradient Highlight (Top & Left) & Recessed Shadow (Bottom & Right)
        for (int i = 1; i < w - 1; i++) {
            float t = (float) (i - 1) / Math.max(1, w - 2);
            int col = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + i, y + 1, x + i + 1, y + 2, applyAlpha(col, alpha));
        }
        for (int j = 1; j < h - 1; j++) {
            float t = (float) (j - 1) / Math.max(1, h - 2);
            int col = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_SHADOW, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + j, x + 2, y + j + 1, applyAlpha(col, alpha));
        }
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + h - 2, x + w - 1, y + h - 1, shadow);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, shadow);

        // 3. Layer 3: Recessed 1px Inner Dark Shadow
        int innerDark = applyAlpha(0xFF101012, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + 2, x + w - 2, y + 3, innerDark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + 2, x + 3, y + h - 2, innerDark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + h - 3, x + w - 2, y + h - 2, innerDark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 3, y + 2, x + w - 2, y + h - 2, innerDark);
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
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y + 1, x + w - 1, y + h - 1, color);
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

    // --- Config Row Render Helpers ---
    private void renderRowCardBase(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;

        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        int bg = hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bg, alpha));

        int border = hover ? COLOR_ROW_BORDER_HOVER : COLOR_ROW_BORDER;
        drawBorder(poseStackOrGraphics, x, y, w, h, applyAlpha(border, alpha));

        drawText(poseStackOrGraphics, font, label, x + 6, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, desc, x + 6, y + 15, applyAlpha(COLOR_TEXT_MUTED, alpha));
    }

    private void renderConfigRowToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, boolean value, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 38;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int bg = value ? COLOR_TOGGLE_ON_BG : COLOR_TOGGLE_OFF_BG;
        int border = value ? COLOR_TOGGLE_ON_BORDER : (hoverBtn ? 0xFF585868 : COLOR_TOGGLE_OFF_BORDER);
        int textCol = value ? COLOR_TOGGLE_ON_TEXT : (hoverBtn ? COLOR_TEXT_WHITE : COLOR_TOGGLE_OFF_TEXT);

        Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, applyAlpha(bg, alpha));
        drawBorder(poseStackOrGraphics, btnX, btnY, btnW, btnH, applyAlpha(border, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, value ? "ON" : "OFF", btnX + btnW / 2, btnY + 3, applyAlpha(textCol, alpha));
    }

    private void renderConfigRowCycle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String currentVal, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 82;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        int bg = hoverBtn ? 0xFF2E2E38 : COLOR_BTN_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, applyAlpha(bg, alpha));

        int border = hoverBtn ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER;
        drawBorder(poseStackOrGraphics, btnX, btnY, btnW, btnH, applyAlpha(border, alpha));

        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "‹ " + currentVal + " ›", btnX + btnW / 2, btnY + 3, applyAlpha(hoverBtn ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String valText, int mouseX, int mouseY, float alpha) {
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
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, valText, valX + valW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

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

        // Clickable Swatches
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

    // --- Color Math Helpers ---
    private static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation <= 0.0001f) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue / 360.0f) * 360.0f) / 60.0f;
            int i = (int) Math.floor(h);
            float f = h - i;
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch (i) {
                case 0 -> { r = Math.round(brightness * 255); g = Math.round(t * 255); b = Math.round(p * 255); }
                case 1 -> { r = Math.round(q * 255); g = Math.round(brightness * 255); b = Math.round(p * 255); }
                case 2 -> { r = Math.round(p * 255); g = Math.round(brightness * 255); b = Math.round(t * 255); }
                case 3 -> { r = Math.round(p * 255); g = Math.round(q * 255); b = Math.round(brightness * 255); }
                case 4 -> { r = Math.round(t * 255); g = Math.round(p * 255); b = Math.round(brightness * 255); }
                default -> { r = Math.round(brightness * 255); g = Math.round(p * 255); b = Math.round(q * 255); }
            }
        }
        return 0xFF000000 | (clamp255(r) << 16) | (clamp255(g) << 8) | clamp255(b);
    }

    private static void rgbToHsb(int rgb, float[] hsb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        float brightness = max / 255.0f;
        float saturation = max == 0 ? 0 : delta / (float) max;
        float hue = 0;
        if (delta > 0.0001f) {
            if (max == r) {
                hue = ((g - b) / delta) % 6.0f;
            } else if (max == g) {
                hue = ((b - r) / delta) + 2.0f;
            } else {
                hue = ((r - g) / delta) + 4.0f;
            }
            hue *= 60.0f;
            if (hue < 0) hue += 360.0f;
        }
        hsb[0] = hue;
        hsb[1] = saturation;
        hsb[2] = brightness;
    }

    private static int scaleBrightness(int color, float ratio) {
        int red = Math.round(((color >> 16) & 0xFF) * ratio);
        int green = Math.round(((color >> 8) & 0xFF) * ratio);
        int blue = Math.round((color & 0xFF) * ratio);
        return 0xFF000000 | (clamp255(red) << 16) | (clamp255(green) << 8) | clamp255(blue);
    }

    private static int clamp255(int val) {
        return Math.max(0, Math.min(255, val));
    }

    // --- Mouse & Keyboard Event Handlers ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isClosing) return true;

        // Modal Clicks Handle First
        if (showColorModal) {
            return handleColorModalClick(mouseX, mouseY);
        }

        int panelW = Math.min(width - 20, 560);
        int panelH = Math.min(height - 16, 304);
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2;

        int headerH = 26;
        int headerX = panelX + 3;
        int headerY = panelY + 3;
        int headerW = panelW - 6;

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
        int navX = panelX + 4;
        int navY = headerY + headerH + 4;
        int navH = panelH - headerH - 12;

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
                    selectedPillarDetailId = null;
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

                // Top Bar 1: Preset management & filter buttons
                int topBarH = 22;
                if (curY >= clipY && curY + topBarH <= clipY + clipH) {
                    int cycleW = 74;
                    int btnH = 14;
                    int cycleX = x + 2;
                    int cycleY = curY + 4;
                    if (mouseX >= cycleX && mouseX <= cycleX + cycleW && mouseY >= cycleY && mouseY <= cycleY + btnH) {
                        if (!presets.isEmpty()) {
                            selectedPresetIndex = (selectedPresetIndex + 1) % presets.size();
                        }
                        return true;
                    }

                    int applyW = 38;
                    int applyX = cycleX + cycleW + 3;
                    if (mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= cycleY && mouseY <= cycleY + btnH) {
                        if (!presets.isEmpty()) {
                            PresetsConfig.Preset p = presets.get(Math.min(selectedPresetIndex, presets.size() - 1));
                            cfg.items.clear();
                            cfg.items.addAll(p.items);
                            cfg.saveItemsToDisk();
                        }
                        return true;
                    }

                    int saveW = 36;
                    int saveX = applyX + applyW + 3;
                    if (mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= cycleY && mouseY <= cycleY + btnH) {
                        cfg.saveItems();
                        return true;
                    }

                    // Filter Buttons: [All] [Inventory] [Configured]
                    int filterW = 60;
                    int rightFiltersX = x + w - (3 * (filterW + 3));
                    for (int f = 0; f < 3; f++) {
                        int fx = rightFiltersX + f * (filterW + 3);
                        if (mouseX >= fx && mouseX <= fx + filterW && mouseY >= cycleY && mouseY <= cycleY + btnH) {
                            itemFilterMode = f;
                            return true;
                        }
                    }
                }
                curY += topBarH;

                // Top Bar 2: Tag Presets Quick Buttons & Defaults
                int tagBarH = 18;
                if (curY >= clipY && curY + tagBarH <= clipY + clipH) {
                    String[] tagIds = {"#the_vault:keys", "#the_vault:gems", "#c:ores", "#the_vault:vault_gear"};
                    int chipW = 54;
                    int chipH = 13;
                    int chipStartX = x + 38;

                    for (int t = 0; t < tagIds.length; t++) {
                        int chipX = chipStartX + t * (chipW + 4);
                        if (mouseX >= chipX && mouseX <= chipX + chipW && mouseY >= curY + 2 && mouseY <= curY + 2 + chipH) {
                            if (cfg.items.contains(tagIds[t])) {
                                cfg.items.remove(tagIds[t]);
                            } else {
                                cfg.items.add(tagIds[t]);
                            }
                            cfg.saveItemsToDisk();
                            return true;
                        }
                    }

                    int defBtnW = 60;
                    int defBtnX = x + w - defBtnW - 2;
                    if (mouseX >= defBtnX && mouseX <= defBtnX + defBtnW && mouseY >= curY + 2 && mouseY <= curY + 2 + chipH) {
                        cfg.items.clear();
                        cfg.items.add("minecraft:diamond");
                        cfg.items.add("minecraft:netherite_ingot");
                        cfg.items.add("minecraft:nether_star");
                        cfg.items.add("the_vault:echo_pog");
                        cfg.items.add("#the_vault:keys");
                        cfg.saveItemsToDisk();
                        return true;
                    }
                }
                curY += tagBarH + 4;

                // Slot clicks
                List<Item> itemsToDisplay = getFilteredItemsList();
                int slotSize = 20;
                int slotGap = 2;
                int cols = Math.max(1, (w - 12) / (slotSize + slotGap));
                int gridStartX = x + 4;

                for (int i = 0; i < itemsToDisplay.size(); i++) {
                    Item item = itemsToDisplay.get(i);
                    int col = i % cols;
                    int row = i / cols;
                    int slotX = gridStartX + col * (slotSize + slotGap);
                    int slotY = curY + row * (slotSize + slotGap);

                    if (slotY + slotSize < clipY || slotY > clipY + clipH) {
                        continue;
                    }

                    if (mouseX >= slotX && mouseX <= slotX + slotSize && mouseY >= slotY && mouseY <= slotY + slotSize) {
                        CommonId cid = Services.PLATFORM.getItemId(item);
                        if (cid != null) {
                            String idStr = cid.toString();
                            if (cfg.items.contains(idStr)) {
                                cfg.items.remove(idStr);
                            } else {
                                cfg.items.add(idStr);
                            }
                            cfg.saveItemsToDisk();
                        }
                        return true;
                    }
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

                // 2. Pattern Cycle
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

                // 10. Swatch Clicks -> Opens Draggable Color Picker Modal!
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
                            editingColorIndex = i;
                            isEditingPillarColor = false;
                            editingPillarId = null;
                            String hex = cfg.particle_color.get(i).replace("#", "");
                            try {
                                int rgb = Integer.parseInt(hex, 16);
                                float[] hsb = new float[3];
                                rgbToHsb(rgb, hsb);
                                pickerHue = hsb[0];
                                pickerSat = hsb[1];
                                pickerBri = hsb[2];
                            } catch (Exception ignored) {}
                            showColorModal = true;
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
                if (selectedPillarDetailId != null) {
                    PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(selectedPillarDetailId);
                    if (data == null) {
                        selectedPillarDetailId = null;
                        return false;
                    }

                    // Row 0: Back Button
                    if (curY >= clipY && curY + h <= clipY + clipH) {
                        int backW = 68;
                        int btnH = 14;
                        int backX = x + w - backW - 6;
                        int backY = curY + (h - btnH) / 2;
                        if (mouseX >= backX && mouseX <= backX + backW && mouseY >= backY && mouseY <= backY + btnH) {
                            selectedPillarDetailId = null;
                            return true;
                        }
                    }
                    curY += h + gap;

                    // Row 1: Use Pattern Override
                    if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                        data.use_pattern = data.use_pattern == null || !data.use_pattern;
                        return true;
                    }
                    curY += h + gap;

                    // Row 2: Pattern Cycle
                    if (checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                        int pIndex = 0;
                        for (int i = 0; i < PARTICLE_PATTERNS.length; i++) {
                            if (PARTICLE_PATTERNS[i].equalsIgnoreCase(data.pattern != null ? data.pattern : "default")) {
                                pIndex = i;
                                break;
                            }
                        }
                        data.pattern = PARTICLE_PATTERNS[(pIndex + 1) % PARTICLE_PATTERNS.length];
                        return true;
                    }
                    curY += h + gap;

                    // Row 3: Pattern Speed
                    int spdStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                    if (spdStep != 0) {
                        double cur = data.pattern_speed != null ? data.pattern_speed : PillarParticleConfig.get().pattern_speed;
                        data.pattern_speed = Math.max(0.01D, Math.min(0.50D, Math.round((cur + spdStep * 0.01D) * 100.0) / 100.0));
                        return true;
                    }
                    curY += h + gap;

                    // Row 4: Pattern Spread
                    int sprStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                    if (sprStep != 0) {
                        double cur = data.pattern_spread != null ? data.pattern_spread : PillarParticleConfig.get().pattern_spread;
                        data.pattern_spread = Math.max(0.01D, Math.min(0.50D, Math.round((cur + sprStep * 0.01D) * 100.0) / 100.0));
                        return true;
                    }
                    curY += h + gap;

                    // Row 5: Pattern Intensity
                    int intStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                    if (intStep != 0) {
                        double cur = data.pattern_intensity != null ? data.pattern_intensity : PillarParticleConfig.get().pattern_intensity;
                        data.pattern_intensity = Math.max(0.1D, Math.min(5.0D, Math.round((cur + intStep * 0.1D) * 10.0) / 10.0));
                        return true;
                    }
                    curY += h + gap;

                    // Row 6: Per-Pillar Color Palette
                    if (curY >= clipY && curY + h <= clipY + clipH) {
                        List<String> colors = data.dyeColors;
                        int counterW = 60;
                        int counterH = 14;
                        int counterX = x + w - counterW - 8;
                        int counterY = curY + (h - counterH) / 2;
                        int stepBtnW = 12;

                        if (mouseY >= counterY && mouseY <= counterY + counterH) {
                            if (mouseX >= counterX && mouseX <= counterX + stepBtnW) {
                                int curMax = data.max_particle_color != null ? data.max_particle_color : colors.size();
                                data.max_particle_color = Math.max(1, curMax - 1);
                                return true;
                            } else if (mouseX >= counterX + counterW - stepBtnW && mouseX <= counterX + counterW) {
                                int curMax = data.max_particle_color != null ? data.max_particle_color : colors.size();
                                data.max_particle_color = Math.min(5, curMax + 1);
                                return true;
                            }
                        }

                        int swatchSize = 12;
                        int swatchGap = 3;
                        int swatchesTotalW = Math.min(5, colors.size()) * (swatchSize + swatchGap);
                        int swatchStartX = counterX - swatchesTotalW - 8;
                        int swatchY = curY + (h - swatchSize) / 2;

                        for (int i = 0; i < Math.min(5, colors.size()); i++) {
                            int swX = swatchStartX + i * (swatchSize + swatchGap);
                            if (mouseX >= swX && mouseX <= swX + swatchSize && mouseY >= swatchY && mouseY <= swatchY + swatchSize) {
                                editingColorIndex = i;
                                isEditingPillarColor = true;
                                editingPillarId = data.id;
                                String hex = colors.get(i).replace("#", "");
                                try {
                                    int rgb = Integer.parseInt(hex, 16);
                                    float[] hsb = new float[3];
                                    rgbToHsb(rgb, hsb);
                                    pickerHue = hsb[0];
                                    pickerSat = hsb[1];
                                    pickerBri = hsb[2];
                                } catch (Exception ignored) {}
                                showColorModal = true;
                                return true;
                            }
                        }
                    }
                    curY += h + gap;

                    // Row 7: Save Pillar Changes
                    if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH, 80)) {
                        PacketFactory.sendToServer(new UpdatePillarDataPacket(
                                data.id,
                                data.pattern,
                                data.use_pattern,
                                data.pattern_speed,
                                data.pattern_spread,
                                data.pattern_intensity,
                                data.max_particle_color,
                                data.dyeColors
                        ));
                        PillarIdManager.getClient().save();
                        selectedPillarDetailId = null;
                        return true;
                    }
                } else {
                    // Row 0: Reload & Clear
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

                    // Rows 3+: Individual Pillar Rows
                    Collection<PillarIdManager.PillarData> pillars = PillarIdManager.getClient().getAllData();
                    for (PillarIdManager.PillarData data : pillars) {
                        if (curY >= clipY && curY + h <= clipY + clipH) {
                            int custW = 64;
                            int btnH = 14;
                            int custX = x + w - custW - 58;
                            int custY = curY + (h - btnH) / 2;

                            // Customize click
                            if (mouseX >= custX && mouseX <= custX + custW && mouseY >= custY && mouseY <= custY + btnH) {
                                selectedPillarDetailId = data.id;
                                return true;
                            }

                            // Remove click
                            int removeW = 50;
                            int removeX = x + w - removeW - 6;
                            if (mouseX >= removeX && mouseX <= removeX + removeW && mouseY >= custY && mouseY <= custY + btnH) {
                                PacketFactory.sendToServer(new RemovePillarPacket(Collections.singletonList(data.id)));
                                return true;
                            }
                        }
                        curY += h + gap;
                    }
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

                // Row 3: Disable Enderman Griefing
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing) : ModGameRules.clientDisableEndermanGriefing;
                    ModGameRules.clientDisableEndermanGriefing = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("disableEndermanGriefing", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 4: Disable Creeper Griefing
                if (checkToggleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    boolean cur = level != null ? Services.PLATFORM.getGameRuleBoolean(level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing) : ModGameRules.clientDisableCreeperGriefing;
                    ModGameRules.clientDisableCreeperGriefing = !cur;
                    PacketFactory.sendToServer(new UpdateGameRulePacket("disableCreeperGriefing", !cur));
                    return true;
                }
                curY += h + gap;

                // Row 5: Disable Ghast Griefing
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

    private boolean handleColorModalClick(double mouseX, double mouseY) {
        int modalW = 340;
        int modalH = 196;
        int modalX = (width - modalW) / 2;
        int modalY = (height - modalH) / 2;

        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 6;
        int closeY = modalY + 5;
        if (mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize) {
            showColorModal = false;
            draggingGradient = false;
            draggingHue = false;
            return true;
        }

        int headH = 24;
        int gradBoxX = modalX + 16;
        int gradBoxY = modalY + headH + 12;
        int gradBoxW = 110;
        int gradBoxH = 88;

        // 1. Click inside 2D Gradient Box -> start dragging
        if (mouseX >= gradBoxX && mouseX <= gradBoxX + gradBoxW && mouseY >= gradBoxY && mouseY <= gradBoxY + gradBoxH) {
            draggingGradient = true;
            pickerSat = (float) Math.max(0.0, Math.min(1.0, (mouseX - gradBoxX) / (double) gradBoxW));
            pickerBri = (float) Math.max(0.0, Math.min(1.0, 1.0 - (mouseY - gradBoxY) / (double) gradBoxH));
            return true;
        }

        // 2. Click inside Hue Slider -> start dragging
        int hueX = gradBoxX + gradBoxW + 10;
        int hueY = gradBoxY;
        int hueW = 16;
        int hueH = gradBoxH;
        if (mouseX >= hueX && mouseX <= hueX + hueW && mouseY >= hueY && mouseY <= hueY + hueH) {
            draggingHue = true;
            pickerHue = (float) Math.max(0.0, Math.min(360.0, ((mouseY - hueY) / (double) hueH) * 360.0));
            return true;
        }

        // 3. Preset Swatches Click
        int previewX = hueX + hueW + 14;
        int previewSize = 36;
        int presetStartX = previewX + previewSize + 8;
        int presetStartY = gradBoxY;
        int swatchSize = 13;
        int swatchGap = 3;
        for (int p = 0; p < PALETTE_PRESETS.length; p++) {
            int px = presetStartX + (p % 4) * (swatchSize + swatchGap);
            int py = presetStartY + (p / 4) * (swatchSize + swatchGap);
            if (mouseX >= px && mouseX <= px + swatchSize && mouseY >= py && mouseY <= py + swatchSize) {
                int col = PALETTE_PRESETS[p];
                float[] hsb = new float[3];
                rgbToHsb(col, hsb);
                pickerHue = hsb[0];
                pickerSat = hsb[1];
                pickerBri = hsb[2];
                return true;
            }
        }

        // 4. Apply Button
        int applyW = 90;
        int applyH = 16;
        int applyX = modalX + modalW - applyW - 16;
        int applyY = modalY + modalH - applyH - 12;
        if (mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + applyH) {
            int currentColorInt = hsbToRgb(pickerHue, pickerSat, pickerBri);
            int r = (currentColorInt >> 16) & 0xFF;
            int g = (currentColorInt >> 8) & 0xFF;
            int b = currentColorInt & 0xFF;
            String hexString = String.format("#%02X%02X%02X", r, g, b);

            if (isEditingPillarColor && editingPillarId != null) {
                PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(editingPillarId);
                if (data != null) {
                    if (editingColorIndex < data.dyeColors.size()) {
                        data.dyeColors.set(editingColorIndex, hexString);
                    } else {
                        data.dyeColors.add(hexString);
                    }
                }
            } else {
                PillarParticleConfig cfg = PillarParticleConfig.get();
                if (editingColorIndex < cfg.particle_color.size()) {
                    cfg.particle_color.set(editingColorIndex, hexString);
                } else {
                    cfg.particle_color.add(hexString);
                }
                cfg.saveProperties();
            }

            showColorModal = false;
            draggingGradient = false;
            draggingHue = false;
            return true;
        }

        return true;
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
        draggingGradient = false;
        draggingHue = false;
        if (activeTab != null && activeTab.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (showColorModal) {
            int modalW = 340;
            int modalH = 196;
            int modalX = (width - modalW) / 2;
            int modalY = (height - modalH) / 2;
            int headH = 24;

            int gradBoxX = modalX + 16;
            int gradBoxY = modalY + headH + 12;
            int gradBoxW = 110;
            int gradBoxH = 88;

            if (draggingGradient) {
                pickerSat = (float) Math.max(0.0, Math.min(1.0, (mouseX - gradBoxX) / (double) gradBoxW));
                pickerBri = (float) Math.max(0.0, Math.min(1.0, 1.0 - (mouseY - gradBoxY) / (double) gradBoxH));
                return true;
            }

            int hueX = gradBoxX + gradBoxW + 10;
            int hueY = gradBoxY;
            int hueH = gradBoxH;
            if (draggingHue) {
                pickerHue = (float) Math.max(0.0, Math.min(360.0, ((mouseY - hueY) / (double) hueH) * 360.0));
                return true;
            }
        }

        if (activeTab != null && activeTab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (showColorModal) return true;

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
        if (showColorModal) {
            if (keyCode == 256) {
                showColorModal = false;
                draggingGradient = false;
                draggingHue = false;
                return true;
            }
            return true;
        }
        if (activeTab != null && activeTab.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) { // ESC key
            if (selectedPillarDetailId != null) {
                selectedPillarDetailId = null;
                return true;
            }
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
}
