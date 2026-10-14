package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
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
import com.kingodogo.buildscape.client.PillarMarkerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;


// Unified Single-Panel Buildscape Configuration Screen.
public class BuildScapeConfigScreen implements IScreenDelegate {

    private final Screen parent;
    private final Component title;
    private Screen wrapperScreen;

    private Consumer<AbstractWidget> addWidgetConsumer;
    private Consumer<GuiEventListener> removeWidgetConsumer;

    public int width;
    public int height;

    // --- Local Client Preferences & Dynamic Animation Scaling ---
    private static int animSpeedIndex = 0;
    private static final String[] ANIM_SPEEDS = {"Smooth", "Snappy", "Instant"};

    public static float getAnimSpeedMultiplier() {
        return switch (animSpeedIndex) {
            case 1 -> 0.60f; // Snappy
            case 2 -> 0.001f; // Instant
            default -> 1.0f; // Smooth
        };
    }

    public static int getOpenAnimMs() { return Math.max(1, (int) (300 * getAnimSpeedMultiplier())); }
    public static int getCloseAnimMs() { return Math.max(1, (int) (220 * getAnimSpeedMultiplier())); }
    public static int getNavAnimMs() { return Math.max(1, (int) (260 * getAnimSpeedMultiplier())); }
    public static int getNavIndicatorAnimMs() { return Math.max(1, (int) (260 * getAnimSpeedMultiplier())); }
    public static int getTabTransitionAnimMs() { return Math.max(1, (int) (240 * getAnimSpeedMultiplier())); }
    public static int getSidePanelAnimMs() { return Math.max(1, (int) (280 * getAnimSpeedMultiplier())); }

    public static float easeOutSine(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        return (float) Math.sin(clamped * (Math.PI / 2.0));
    }

    public static float easeInSine(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        return 1.0f - (float) Math.cos(clamped * (Math.PI / 2.0));
    }

    public static float easeOutCubic(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        float f = 1.0f - clamped;
        return 1.0f - f * f * f;
    }

    private long openStartTime;
    private long closeStartTime = -1;
    private boolean isOpening = true;
    private boolean isClosing = false;
    private float openProgress = 0.0f;
    private float closeProgress = 1.0f;

    // --- Active Theme Palette ---
    public static ConfigThemes.Theme currentTheme = ConfigThemes.getCurrentTheme();

    public static int COLOR_OVERLAY = currentTheme.colorOverlay;
    public static int COLOR_PANEL_BG = currentTheme.colorPanelBg;
    public static int COLOR_CHASSIS_BODY = currentTheme.colorChassisBody;
    public static int COLOR_INNER_DARK = currentTheme.colorInnerDark;
    public static int COLOR_HEADER_BG = currentTheme.colorHeaderBg;

    // Metallic Beveled Border Gradients (1px wide)
    public static int COLOR_FRAME_BLACK = currentTheme.colorFrameBlack;
    public static int COLOR_BORDER_GRADIENT_START = currentTheme.colorBorderGradientStart;
    public static int COLOR_BORDER_GRADIENT_END = currentTheme.colorBorderGradientEnd;
    public static int COLOR_BORDER_SHADOW = currentTheme.colorBorderShadow;

    public static int COLOR_CHASSIS_BAND = currentTheme.colorChassisBand;
    public static int COLOR_BEVEL_SHADOW = currentTheme.colorBevelShadow;
    public static int COLOR_BEVEL_HIGHLIGHT = currentTheme.colorBevelHighlight;

    // Text & Accents
    public static int COLOR_TEXT_WHITE = currentTheme.colorTextWhite;
    public static int COLOR_TEXT_SILVER = currentTheme.colorTextSilver;
    public static int COLOR_TEXT_MUTED = currentTheme.colorTextMuted;

    // Interactive Widget Colors
    public static int COLOR_ROW_BG = currentTheme.colorRowBg;
    public static int COLOR_ROW_BG_HOVER = currentTheme.colorRowBgHover;
    public static int COLOR_ROW_BORDER = currentTheme.colorRowBorder;
    public static int COLOR_ROW_BORDER_HOVER = currentTheme.colorRowBorderHover;

    public static int COLOR_TOGGLE_ON_BG = currentTheme.colorToggleOnBg;
    public static int COLOR_TOGGLE_ON_BORDER = currentTheme.colorToggleOnBorder;
    public static int COLOR_TOGGLE_ON_TEXT = currentTheme.colorToggleOnText;

    public static int COLOR_TOGGLE_OFF_BG = currentTheme.colorToggleOffBg;
    public static int COLOR_TOGGLE_OFF_BORDER = currentTheme.colorToggleOffBorder;
    public static int COLOR_TOGGLE_OFF_TEXT = currentTheme.colorToggleOffText;

    public static int COLOR_BTN_BG = currentTheme.colorBtnBg;
    public static int COLOR_BTN_BG_HOVER = currentTheme.colorBtnBgHover;
    public static int COLOR_BTN_BG_ACTIVE = currentTheme.colorBtnBgActive;
    public static int COLOR_BTN_BORDER = currentTheme.colorBtnBorder;
    public static int COLOR_BTN_BORDER_HOVER = currentTheme.colorBtnBorderHover;

    public static int COLOR_SLOT_BG = currentTheme.colorSlotBg;
    public static int COLOR_SLOT_BG_HOVER = currentTheme.colorSlotBgHover;
    public static int COLOR_SLOT_BORDER = currentTheme.colorSlotBorder;

    public static int COLOR_ACCENT = currentTheme.colorAccent;
    public static int COLOR_ACCENT_GLOW = currentTheme.colorAccentGlow;

    public static void applyTheme(ConfigThemes.Theme theme) {
        currentTheme = theme;
        ConfigThemes.setThemeById(theme.id);
        try {
            BuildscapeClientConfig.get().setUiTheme(theme.id);
        } catch (Exception ignored) {}
        COLOR_OVERLAY = theme.colorOverlay;
        COLOR_PANEL_BG = theme.colorPanelBg;
        COLOR_CHASSIS_BODY = theme.colorChassisBody;
        COLOR_INNER_DARK = theme.colorInnerDark;
        COLOR_HEADER_BG = theme.colorHeaderBg;

        COLOR_FRAME_BLACK = theme.colorFrameBlack;
        COLOR_BORDER_GRADIENT_START = theme.colorBorderGradientStart;
        COLOR_BORDER_GRADIENT_END = theme.colorBorderGradientEnd;
        COLOR_BORDER_SHADOW = theme.colorBorderShadow;

        COLOR_CHASSIS_BAND = theme.colorChassisBand;
        COLOR_BEVEL_SHADOW = theme.colorBevelShadow;
        COLOR_BEVEL_HIGHLIGHT = theme.colorBevelHighlight;

        COLOR_TEXT_WHITE = theme.colorTextWhite;
        COLOR_TEXT_SILVER = theme.colorTextSilver;
        COLOR_TEXT_MUTED = theme.colorTextMuted;

        COLOR_ROW_BG = theme.colorRowBg;
        COLOR_ROW_BG_HOVER = theme.colorRowBgHover;
        COLOR_ROW_BORDER = theme.colorRowBorder;
        COLOR_ROW_BORDER_HOVER = theme.colorRowBorderHover;

        COLOR_TOGGLE_ON_BG = theme.colorToggleOnBg;
        COLOR_TOGGLE_ON_BORDER = theme.colorToggleOnBorder;
        COLOR_TOGGLE_ON_TEXT = theme.colorToggleOnText;

        COLOR_TOGGLE_OFF_BG = theme.colorToggleOffBg;
        COLOR_TOGGLE_OFF_BORDER = theme.colorToggleOffBorder;
        COLOR_TOGGLE_OFF_TEXT = theme.colorToggleOffText;

        COLOR_BTN_BG = theme.colorBtnBg;
        COLOR_BTN_BG_HOVER = theme.colorBtnBgHover;
        COLOR_BTN_BG_ACTIVE = theme.colorBtnBgActive;
        COLOR_BTN_BORDER = theme.colorBtnBorder;
        COLOR_BTN_BORDER_HOVER = theme.colorBtnBorderHover;

        COLOR_SLOT_BG = theme.colorSlotBg;
        COLOR_SLOT_BG_HOVER = theme.colorSlotBgHover;
        COLOR_SLOT_BORDER = theme.colorSlotBorder;

        COLOR_ACCENT = theme.colorAccent;
        COLOR_ACCENT_GLOW = theme.colorAccentGlow;
    }

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
    private double targetScrollOffset = 0.0;
    private double maxScroll = 0.0;
    private boolean isDraggingScrollbar = false;
    private double scrollDragStartY = 0.0;
    private double scrollDragStartOffset = 0.0;
    private int currentScrollbarTrackX = 0;
    private int currentScrollbarTrackY = 0;
    private int currentScrollbarTrackW = 6;
    private int currentScrollbarTrackH = 0;
    private int currentScrollbarThumbH = 18;

    // --- Particle Settings Options Arrays ---
    private static final String[] GLOBAL_PARTICLE_PATTERNS = {
            "none", "ring", "beam", "spiral", "fountain", "pulse", "burst", "snowflake"
    };
    private static final String[] PARTICLE_PATTERNS = {
            "none", "default", "beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"
    };

    // --- Pillar IDs State ---
    private final Set<String> selectedPillarIds = new HashSet<>();
    private long lastPillarRefreshCheck = 0L;
    private long lastPillarRowClickTime = 0L;
    private String lastPillarRowClickId = null;

    // --- Presets & Items State ---
    private int selectedPresetIndex = 0;
    private int itemsSubTab = 0; // 0 = Display Items, 1 = Display Tags
    private int itemFilterMode = 0; // 0=All Items, 1=Inventory, 2=Mod Toggles, 3=Active
    private int tagFilterMode = 0; // 0=All Tags, 1=Inventory, 2=Mod Toggles, 3=Active
    private int currentTagModIndex = 0;
    private String tagSearchFilter = "";
    private final String[] TAG_CATEGORY_CHIPS = {"All", "keys", "gems", "ores", "vault", "swords", "gear"};
    private int activeTagChipIndex = 0;

    private final List<Item> availableItems = new ArrayList<>();
    private final List<String> availableModNamespaces = new ArrayList<>();
    private int currentModIndex = 0;
    private final List<Item> cachedInventoryItems = new ArrayList<>();
    private final Set<Item> cachedActiveItems = new HashSet<>();
    private final Set<String> cachedActiveTags = new HashSet<>();
    private final List<Item> cachedFilteredItems = new ArrayList<>();
    private boolean itemsListDirty = true;

    private final List<String> allRegisteredTags = new ArrayList<>();
    private final Map<String, Integer> tagItemCounts = new HashMap<>();
    private final Map<Item, ItemStack> itemStackCache = new HashMap<>();
    private final Map<String, ItemStack> stringToStackCache = new HashMap<>();

    // Deferred Tooltip State (prevents scale drift on Scale 3/4)
    private List<Component> deferredTooltip = null;
    private int deferredTooltipX = 0;
    private int deferredTooltipY = 0;
    private int rawMouseX = 0;
    private int rawMouseY = 0;

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
    private String closingPillarDetailId = null;
    private String prevPillarDetailId = null;
    private long pillarSwitchStartTime = 0L;
    private boolean isPillarSwitching = false;

    // --- Connected Right Side Panel (Sliding Modifier Drawer) ---
    public enum SidePanelType {
        NONE,
        PILLAR_DETAIL,
        COLOR_PICKER,
        PRESETS
    }

    private SidePanelType sidePanelType = SidePanelType.NONE;
    private SidePanelType returnSidePanelType = SidePanelType.NONE;
    private float sidePanelProgress = 0.0f;
    private boolean isSidePanelOpening = false;
    private boolean isSidePanelClosing = false;
    private long sidePanelAnimStartTime = 0L;
    private int sidePanelScrollOffset = 0;
    private int sidePanelMaxScroll = 0;

    // Sidenav expansion animation
    private float navAnimProgress = 1.0f; // 1.0 = expanded, 0.0 = collapsed
    private boolean isNavAnimating = false;
    private long navAnimStartTime = 0L;

    public void toggleNavExpansion() {
        navExpanded = !navExpanded;
        isNavAnimating = true;
        int duration = getNavAnimMs();
        navAnimStartTime = System.currentTimeMillis() - (long) ((navExpanded ? navAnimProgress : (1.0f - navAnimProgress)) * duration);
    }

    private void updateNavAnimation() {
        if (!isNavAnimating) return;
        long elapsed = System.currentTimeMillis() - navAnimStartTime;
        int duration = getNavAnimMs();
        float t = Math.min(1.0f, (float) elapsed / duration);
        float eased = easeOutSine(t);
        if (navExpanded) {
            navAnimProgress = eased;
            if (t >= 1.0f) {
                navAnimProgress = 1.0f;
                isNavAnimating = false;
            }
        } else {
            navAnimProgress = 1.0f - eased;
            if (t >= 1.0f) {
                navAnimProgress = 0.0f;
                isNavAnimating = false;
            }
        }
    }

    // Sidenav indicator glide animation
    private float prevNavIndicatorY = 0.0f;
    private float targetNavIndicatorY = 0.0f;
    private float currentIndicatorY = 0.0f;
    private long navIndicatorStartTime = 0L;

    // Content area tab switch transition
    private int prevTabIndex = 0;
    private long tabTransitionStartTime = 0L;
    private boolean isTabTransitioning = false;

    // System tab states (locked: mod updates hidden and set to false)
    private boolean autoUpdateCheck = false;
    private String updateStatusText = "Up to date ✓";
    private long lastUpdateCheckTime = 0L;

    public void selectTab(int newIndex) {
        if (newIndex < 0 || newIndex >= navItems.size() || selectedTabIndex == newIndex) {
            return;
        }
        prevTabIndex = selectedTabIndex;
        selectedTabIndex = newIndex;
        scrollOffset = 0.0;
        targetScrollOffset = 0.0;
        isDraggingScrollbar = false;

        // Smoothly retract side panel while preserving its displayed content during slide-out
        if (sidePanelProgress > 0.001f && sidePanelType != SidePanelType.NONE) {
            closingPillarDetailId = selectedPillarDetailId;
            closeSidePanel();
        } else {
            selectedPillarDetailId = null;
            closingPillarDetailId = null;
        }

        if ("pillars".equals(navItems.get(newIndex).id)) {
            syncPillars();
        }
        int tabH = 26;
        int tabGap = 3;
        int effH = getEffectiveHeight();
        int panelH = Math.min(effH - 16, 308);
        int panelY = (effH - panelH) / 2;
        int headerH = 24;
        int navY = panelY + 7 + headerH + 5;
        int startY = navY + 4;
        prevNavIndicatorY = currentIndicatorY > 0.01f ? currentIndicatorY : (startY + prevTabIndex * (tabH + tabGap));
        targetNavIndicatorY = startY + newIndex * (tabH + tabGap);
        navIndicatorStartTime = System.currentTimeMillis();
        tabTransitionStartTime = System.currentTimeMillis();
        isTabTransitioning = true;
    }

    public void openSidePanel(SidePanelType type) {
        if (sidePanelType == type && (isSidePanelOpening || sidePanelProgress >= 1.0f)) {
            return;
        }
        sidePanelType = type;
        isSidePanelOpening = true;
        isSidePanelClosing = false;
        int duration = getSidePanelAnimMs();
        sidePanelAnimStartTime = System.currentTimeMillis() - (long) (sidePanelProgress * duration);
    }

    public void openPillarDetailInSidePanel(String pillarId) {
        if (pillarId == null) return;
        if (Objects.equals(selectedPillarDetailId, pillarId) && sidePanelType == SidePanelType.PILLAR_DETAIL && sidePanelProgress >= 0.99f && !isSidePanelClosing) {
            return;
        }

        boolean wasAlreadyOpen = (sidePanelType == SidePanelType.PILLAR_DETAIL && sidePanelProgress > 0.05f && !isSidePanelClosing);
        if (wasAlreadyOpen && !Objects.equals(selectedPillarDetailId, pillarId)) {
            prevPillarDetailId = selectedPillarDetailId;
            pillarSwitchStartTime = System.currentTimeMillis();
            isPillarSwitching = true;
        } else {
            prevPillarDetailId = null;
            pillarSwitchStartTime = System.currentTimeMillis();
            isPillarSwitching = true;
        }

        selectedPillarDetailId = pillarId;
        closingPillarDetailId = null;
        openSidePanel(SidePanelType.PILLAR_DETAIL);
    }

    public void closeSidePanel() {
        if (sidePanelType == SidePanelType.NONE && sidePanelProgress <= 0.0f) {
            return;
        }
        isSidePanelClosing = true;
        isSidePanelOpening = false;
        int duration = getSidePanelAnimMs();
        sidePanelAnimStartTime = System.currentTimeMillis() - (long) ((1.0f - sidePanelProgress) * duration);
    }

    private void updateSidePanelAnimation() {
        if (!isSidePanelOpening && !isSidePanelClosing) return;
        long elapsed = System.currentTimeMillis() - sidePanelAnimStartTime;
        int duration = getSidePanelAnimMs();
        float t = Math.min(1.0f, (float) elapsed / duration);
        if (isSidePanelOpening) {
            sidePanelProgress = easeOutCubic(t);
            if (t >= 1.0f) {
                sidePanelProgress = 1.0f;
                isSidePanelOpening = false;
            }
        } else if (isSidePanelClosing) {
            sidePanelProgress = 1.0f - easeOutCubic(t);
            if (t >= 1.0f) {
                sidePanelProgress = 0.0f;
                isSidePanelClosing = false;
                sidePanelType = SidePanelType.NONE;
                closingPillarDetailId = null;
                selectedPillarDetailId = null;
            }
        }
    }

    // --- Local Client Preferences ---
    private boolean autoSnappingEnabled = true;
    private boolean strictBoundsCheck = true;

    // Legacy tab & widget support
    private AbstractConfigTab activeTab;
    private final List<GuiEventListener> widgets = new ArrayList<>();
    private final List<GuiEventListener> tabWidgets = new ArrayList<>();

    public BuildScapeConfigScreen(Screen parent) {
        this.parent = parent;
        this.title = ComponentHelper.translatable("buildscape.config.title");

        this.openStartTime = System.currentTimeMillis();
        try {
            String saved = BuildscapeClientConfig.get().getUiTheme();
            applyTheme(ConfigThemes.getById(saved));
        } catch (Exception ignored) {}
        initNavItems();
        cacheAvailableItems();
        loadRegisteredTags();
        syncPillars();
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem("items", "☵", "Items & Tags", "Display items, tags, and presets"));
        navItems.add(new NavItem("particles", "✦", "Particles", "Particle speed, patterns, and colors"));
        navItems.add(new NavItem("pillars", "▲", "Pillar IDs", "List and customize placed pillar blocks"));
        navItems.add(new NavItem("world", "⚙", "World Settings", "Griefing rules, tree felling, and stacking"));
        navItems.add(new NavItem("system", "ℹ", "System", "Client preferences and mod updates"));
    }

    private void cacheAvailableItems() {
        if (availableItems.isEmpty()) {
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
            availableModNamespaces.clear();
            Set<String> modSet = new LinkedHashSet<>();
            modSet.add("minecraft");
            modSet.add("buildscape");
            for (Item item : availableItems) {
                CommonId cid = Services.PLATFORM.getItemId(item);
                if (cid != null && cid.getNamespace() != null && !cid.getNamespace().isEmpty()) {
                    modSet.add(cid.getNamespace());
                }
            }
            availableModNamespaces.addAll(modSet);
        }
        rebuildActiveItemsCache();
        refreshInventoryItemsCache();
        itemsListDirty = true;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private void rebuildActiveItemsCache() {
        cachedActiveItems.clear();
        cachedActiveTags.clear();
        PillarParticleConfig cfg = PillarParticleConfig.get();
        for (String s : cfg.items) {
            if (s.startsWith("#")) {
                cachedActiveTags.add(s);
            } else {
                CommonId cid = CommonId.tryParse(s);
                if (cid != null) {
                    Item item = Services.PLATFORM.getItem(cid);
                    if (item != null && item != Items.AIR) {
                        cachedActiveItems.add(item);
                    }
                }
            }
        }
    }

    private void loadRegisteredTags() {
        if (!allRegisteredTags.isEmpty()) return;
        Set<String> seen = new HashSet<>();
        for (Item item : availableItems) {
            try {
                item.builtInRegistryHolder().tags().forEach(tagKey -> {
                    String id = "#" + tagKey.location().toString();
                    if (seen.add(id)) {
                        allRegisteredTags.add(id);
                    }
                    tagItemCounts.put(id, tagItemCounts.getOrDefault(id, 0) + 1);
                });
            } catch (Exception ignored) {}
        }
        allRegisteredTags.sort(String.CASE_INSENSITIVE_ORDER);
    }

    private List<Item> getPlayerInventoryItems() {
        if (cachedInventoryItems.isEmpty()) {
            refreshInventoryItemsCache();
        }
        return cachedInventoryItems;
    }

    private void refreshInventoryItemsCache() {
        cachedInventoryItems.clear();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Set<Item> set = new LinkedHashSet<>();
            for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
                ItemStack stack = mc.player.getInventory().getItem(i);
                if (!stack.isEmpty() && stack.getItem() != Items.AIR) {
                    set.add(stack.getItem());
                }
            }
            cachedInventoryItems.addAll(set);
        }
    }

    private List<Item> getFilteredItemsList() {
        if (itemsListDirty) {
            refreshFilteredItems();
            itemsListDirty = false;
        }
        return cachedFilteredItems;
    }

    private void refreshFilteredItems() {
        cachedFilteredItems.clear();
        switch (itemFilterMode) {
            case 0 -> {
                cachedFilteredItems.addAll(availableItems);
            }
            case 1 -> {
                refreshInventoryItemsCache();
                cachedFilteredItems.addAll(cachedInventoryItems);
            }
            case 2 -> {
                String mod = !availableModNamespaces.isEmpty() ? availableModNamespaces.get(currentModIndex) : "minecraft";
                for (Item item : availableItems) {
                    CommonId cid = Services.PLATFORM.getItemId(item);
                    if (cid != null && cid.getNamespace().equalsIgnoreCase(mod)) {
                        cachedFilteredItems.add(item);
                    }
                }
            }
            case 3 -> {
                for (Item item : availableItems) {
                    if (cachedActiveItems.contains(item)) {
                        cachedFilteredItems.add(item);
                    }
                }
            }
        }
    }

    private final Set<String> cachedInventoryTags = new HashSet<>();

    private Set<String> getInventoryTagsSet() {
        cachedInventoryTags.clear();
        for (Item item : getPlayerInventoryItems()) {
            try {
                item.builtInRegistryHolder().tags().forEach(tk -> cachedInventoryTags.add("#" + tk.location().toString()));
            } catch (Exception ignored) {}
        }
        return cachedInventoryTags;
    }

    private List<String> getFilteredTagsList() {
        loadRegisteredTags();
        PillarParticleConfig cfg = PillarParticleConfig.get();
        List<String> list = new ArrayList<>();

        Set<String> invTagSet = (tagFilterMode == 1) ? getInventoryTagsSet() : null;
        String currentMod = !availableModNamespaces.isEmpty() ? availableModNamespaces.get(currentTagModIndex) : "minecraft";

        for (String tagId : allRegisteredTags) {
            if (!tagSearchFilter.isEmpty() && !tagId.toLowerCase().contains(tagSearchFilter.toLowerCase())) {
                continue;
            }
            switch (tagFilterMode) {
                case 0 -> list.add(tagId);
                case 1 -> {
                    if (invTagSet != null && invTagSet.contains(tagId)) {
                        list.add(tagId);
                    }
                }
                case 2 -> {
                    String ns = tagId.startsWith("#") ? tagId.substring(1) : tagId;
                    int colon = ns.indexOf(':');
                    String tagNs = colon > 0 ? ns.substring(0, colon) : "minecraft";
                    if (tagNs.equalsIgnoreCase(currentMod)) {
                        list.add(tagId);
                    }
                }
                case 3 -> {
                    if (cfg.items.contains(tagId)) {
                        list.add(tagId);
                    }
                }
            }
        }
        return list;
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

    private ItemStack getItemStackFromId(String idStr) {
        ItemStack stack = getStackForIdentifier(idStr);
        return stack != null ? stack : ItemStack.EMPTY;
    }

    private String formatDimensionName(String dimension) {
        if (dimension == null || dimension.isEmpty()) {
            return "Overworld";
        }
        String dim = dimension.toLowerCase();
        if (dim.contains("overworld")) return "Overworld";
        if (dim.contains("the_nether") || dim.contains("nether")) return "The Nether";
        if (dim.contains("the_end") || dim.contains("end")) return "The End";
        int idx = dim.lastIndexOf(':');
        if (idx >= 0 && idx < dim.length() - 1) {
            dim = dim.substring(idx + 1);
        }
        dim = dim.replace('_', ' ');
        return capitalize(dim);
    }

    private void syncPillars() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() != null) {
            try {
                for (var serverLevel : mc.getSingleplayerServer().getAllLevels()) {
                    if (serverLevel != null) {
                        PillarIdManager serverMgr = PillarIdManager.get(serverLevel);
                        if (serverMgr != null) {
                            serverMgr.checkAndReload();
                            Map<String, PillarIdManager.PillarData> snapshot = serverMgr.copyDataSnapshot();
                            if (!snapshot.isEmpty()) {
                                PillarIdManager.getClient().replaceFromServerSync(snapshot.values());
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        PillarIdManager.getClient().checkAndReload();
        PacketFactory.sendToServer(new RequestPillarIdsPacket());
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

        refreshInventoryItemsCache();
        rebuildActiveItemsCache();
        itemsListDirty = true;
        syncPillars();

        int tabH = 26;
        int tabGap = 3;
        int effH = getEffectiveHeight();
        int panelH = Math.min(effH - 16, 308);
        int panelY = (effH - panelH) / 2;
        int headerH = 24;
        int navY = panelY + 7 + headerH + 5;
        int startY = navY + 4;
        currentIndicatorY = targetNavIndicatorY = prevNavIndicatorY = startY + selectedTabIndex * (tabH + tabGap);

        if (activeTab != null) {
            activeTab.init();
        }
    }

    // --- Adaptive GUI Scaling Engine ---
    public float getGuiUiScale() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return 1.0f;
        double guiScale = mc.getWindow().getGuiScale();
        if (guiScale >= 2.5) {
            return (float) (2.0 / guiScale);
        }
        if (width < 820 && width > 0) {
            return Math.max(0.5f, (float) width / 820.0f);
        }
        return 1.0f;
    }

    public int getEffectiveWidth() {
        float s = getGuiUiScale();
        return Math.abs(s - 1.0f) > 0.001f ? (int) Math.ceil(width / s) : width;
    }

    public int getEffectiveHeight() {
        float s = getGuiUiScale();
        return Math.abs(s - 1.0f) > 0.001f ? (int) Math.ceil(height / s) : height;
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
        return (int) (getEffectiveWidth() * 0.15);
    }

    public int getContentY() {
        return 30;
    }

    public int getContentWidth() {
        return (int) (getEffectiveWidth() * 0.42);
    }

    public int getRightPanelX() {
        return getContentX() + getContentWidth() + 10;
    }

    public int getRightPanelWidth() {
        return (int) (getEffectiveWidth() * 0.42);
    }

    public int getContentHeight() {
        return getEffectiveHeight() - 40;
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
            int duration = getOpenAnimMs();
            float elapsed = (now - openStartTime) / (float) duration;
            if (elapsed >= 1.0f) {
                openProgress = 1.0f;
                isOpening = false;
            } else {
                openProgress = easeOutSine(elapsed);
            }
        }

        if (isClosing) {
            int duration = getCloseAnimMs();
            float elapsed = (now - closeStartTime) / (float) duration;
            if (elapsed >= 1.0f) {
                closeProgress = 0.0f;
                isClosing = false;
                if (closeToWorld) {
                    closeToWorld = false;
                    Services.PLATFORM.openScreen(null);
                } else {
                    Services.PLATFORM.openScreen(parent);
                }
                return 0.0f;
            } else {
                closeProgress = 1.0f - easeOutCubic(elapsed);
            }
        }

        // Smooth scrolling dampening
        if (Math.abs(targetScrollOffset - scrollOffset) > 0.05) {
            scrollOffset += (targetScrollOffset - scrollOffset) * 0.35;
        } else {
            scrollOffset = targetScrollOffset;
        }
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        return isClosing ? closeProgress : openProgress;
    }

    private boolean closeToWorld = false;

    public void startClosing() {
        startClosing(false);
    }

    public void startClosing(boolean toWorld) {
        if (isClosing) return;
        this.closeToWorld = toWorld;
        this.isClosing = true;
        this.isOpening = false;
        this.closeStartTime = System.currentTimeMillis();
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

    private static class SidePanelLayout {
        final int panelX;
        final int panelY;
        final int panelW;
        final int panelH;
        final int frameX;
        final int frameY;
        final int frameW;
        final int frameH;
        final int frameRight;
        final int sidePanelX;
        final int sidePanelY;
        final int sidePanelW;
        final int sidePanelH;
        final boolean isOverlay;

        SidePanelLayout(int panelX, int panelY, int panelW, int panelH,
                        int frameX, int frameY, int frameW, int frameH, int frameRight,
                        int sidePanelX, int sidePanelY, int sidePanelW, int sidePanelH, boolean isOverlay) {
            this.panelX = panelX;
            this.panelY = panelY;
            this.panelW = panelW;
            this.panelH = panelH;
            this.frameX = frameX;
            this.frameY = frameY;
            this.frameW = frameW;
            this.frameH = frameH;
            this.frameRight = frameRight;
            this.sidePanelX = sidePanelX;
            this.sidePanelY = sidePanelY;
            this.sidePanelW = sidePanelW;
            this.sidePanelH = sidePanelH;
            this.isOverlay = isOverlay;
        }
    }

    private SidePanelLayout calculateLayout() {
        int effW = getEffectiveWidth();
        int effH = getEffectiveHeight();
        int panelW = Math.min(effW - 20, 560);
        int panelH = Math.min(effH - 16, 308);

        int sidePanelTargetW = 188;

        int panelX = (effW - panelW) / 2;
        int panelY = (effH - panelH) / 2;

        int bevelExpansion = 5;
        int frameX = panelX - bevelExpansion;
        int frameY = panelY - bevelExpansion;
        int frameW = panelW + bevelExpansion * 2;
        int frameH = panelH + bevelExpansion * 2;
        int frameRight = frameX + frameW;

        int sidePanelH;
        int sidePanelY;
        int sidePanelX;
        int sidePanelW;

        if (sidePanelType == SidePanelType.PRESETS) {
            int tabW = 20;
            int tabH = 28;
            int targetW = sidePanelTargetW;
            int targetH = Math.min(264, frameH - 44);
            sidePanelY = frameY + 36;
            sidePanelX = frameRight - 2;
            sidePanelW = (int) Math.round(tabW + (targetW - tabW) * sidePanelProgress);
            sidePanelH = (int) Math.round(tabH + (targetH - tabH) * sidePanelProgress);
        } else {
            sidePanelH = Math.min(264, frameH - 44);
            sidePanelY = frameY + (frameH - sidePanelH) / 2;

            int dockX = frameRight - 2;
            int slideOffset = (int) Math.round((1.0f - sidePanelProgress) * sidePanelTargetW);
            sidePanelX = dockX - slideOffset;
            sidePanelW = sidePanelTargetW;
        }

        return new SidePanelLayout(panelX, panelY, panelW, panelH,
                frameX, frameY, frameW, frameH, frameRight,
                sidePanelX, sidePanelY, sidePanelW, sidePanelH, false);
    }

    // --- Main Rendering ---
    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        float alpha = updateAnimations();
        if (alpha <= 0.001f) return;

        this.rawMouseX = mouseX;
        this.rawMouseY = mouseY;
        this.deferredTooltip = null;

        Font font = Minecraft.getInstance().font;

        // 1. Full-screen dimmed backdrop (draw before pose scaling to cover physical window)
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(COLOR_OVERLAY, alpha));

        float uiScale = getGuiUiScale();
        boolean scaled = Math.abs(uiScale - 1.0f) > 0.001f;
        if (scaled) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, uiScale, uiScale);
        }

        int effMouseX = scaled ? (int) Math.round(mouseX / uiScale) : mouseX;
        int effMouseY = scaled ? (int) Math.round(mouseY / uiScale) : mouseY;

        // 2. Geometry: Unified Responsive SidePanelLayout
        updateSidePanelAnimation();
        SidePanelLayout layout = calculateLayout();

        int offsetY = Math.round((1.0f - alpha) * 8.0f);
        int panelX = layout.panelX;
        int panelY = layout.panelY + offsetY;
        int panelW = layout.panelW;
        int panelH = layout.panelH;

        int frameX = layout.frameX;
        int frameY = layout.frameY + offsetY;
        int frameW = layout.frameW;
        int frameH = layout.frameH;
        int frameRight = layout.frameRight;

        int sidePanelX = layout.sidePanelX;
        int sidePanelY = layout.sidePanelY + offsetY;
        int sidePanelW = layout.sidePanelW;
        int sidePanelH = layout.sidePanelH;

        // 2b. Connected Sliding Right Side Panel (Rendered behind main chassis so it smoothly slides out like a creative tab)
        if (sidePanelProgress > 0.001f && sidePanelType != SidePanelType.NONE) {
            int visibleStartX = Math.max(sidePanelX, frameRight - 2);
            int visibleW = (sidePanelX + sidePanelW) - visibleStartX;
            if (visibleW > 0) {
                enableGuiScissor(poseStackOrGraphics, visibleStartX, sidePanelY, visibleW, sidePanelH);
                renderSidePanel(poseStackOrGraphics, font, sidePanelX, sidePanelY, sidePanelW, sidePanelH, effMouseX, effMouseY, alpha);
                disableGuiScissor(poseStackOrGraphics);
            }
        }

        // 3. Render 1 Big Frame with expanded chassis gap & metallic gradient
        drawVaultHuntersFrame(poseStackOrGraphics, frameX, frameY, frameW, frameH, COLOR_PANEL_BG, true, alpha);

        // 3b. Presets Side Tab (protruding drawer button on right chassis edge when closed)
        if (selectedTabIndex == 0 && sidePanelProgress < 0.05f) {
            drawPresetsSideTab(poseStackOrGraphics, font, frameRight, frameY + 36, effMouseX, effMouseY, alpha);
        }

        // 4. Header Bar (kept exactly as placed rn, with top-left metallic gradient artifact)
        int headerH = 24;
        int headerX = panelX + 7;
        int headerY = panelY + 7;
        int headerW = panelW - 14;
        drawBeveledComponent(poseStackOrGraphics, headerX, headerY, headerW, headerH, COLOR_HEADER_BG, alpha);

        // Header Title
        drawText(poseStackOrGraphics, font, "⚙ Buildscape Configuration", headerX + 8, headerY + 7, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Target version indicator
        String versionTag = "v4.0.3 · NeoForge 26.2";
        int tagW = font.width(versionTag);
        drawText(poseStackOrGraphics, font, versionTag, headerX + headerW - tagW - 28, headerY + 7, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // 4b. Top Center Crest Badge (Beveled Frame with Buildscape Logo, sitting proud over frame & header)
        drawTopCrestBadge(poseStackOrGraphics, font, frameX, frameY, frameW, alpha);

        // Close Button [ ✕ ] with top-left metallic highlight
        int closeBtnSize = 14;
        int closeBtnX = headerX + headerW - closeBtnSize - 5;
        int closeBtnY = headerY + 5;
        boolean hoverClose = effMouseX >= closeBtnX && effMouseX <= closeBtnX + closeBtnSize && effMouseY >= closeBtnY && effMouseY <= closeBtnY + closeBtnSize;
        drawBeveledButton(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnSize, closeBtnSize, hoverClose ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverClose, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✕", closeBtnX + closeBtnSize / 2, closeBtnY + 3, applyAlpha(hoverClose ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // 5. Layout Sidenav & Content Container (with distinct 7px chassis margin)
        updateNavAnimation();
        int navW = (int) Math.round(30 + 76 * navAnimProgress);
        int navX = panelX + 7;
        int navY = headerY + headerH + 5;
        int navH = panelH - headerH - 19;

        int contentX = navX + navW + 5;
        int contentY = navY;
        int contentW = (panelX + panelW - 7) - contentX;
        int contentH = navH;

        // Render Left Navigation Strip
        renderSidenav(poseStackOrGraphics, font, navX, navY, navW, navH, effMouseX, effMouseY, alpha);

        // Render Right Main Configuration Content Area
        renderContentArea(poseStackOrGraphics, font, contentX, contentY, contentW, contentH, effMouseX, effMouseY, alpha);

        // 7. Fallback Color Picker Modal if explicitly toggled
        if (showColorModal) {
            renderColorPickerModal(poseStackOrGraphics, font, effMouseX, effMouseY, alpha);
        }

        // Render legacy activeTab if any
        if (activeTab != null) {
            activeTab.render(poseStackOrGraphics, effMouseX, effMouseY, partialTick);
            activeTab.renderTooltips(poseStackOrGraphics, effMouseX, effMouseY, partialTick);
        }

        if (scaled) {
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        }

        // Render deferred tooltip in unscaled screen coordinates
        if (deferredTooltip != null && !deferredTooltip.isEmpty()) {
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, font, deferredTooltip, deferredTooltipX, deferredTooltipY);
        }
    }

    // --- Sidenav Rendering ---
    private void renderSidenav(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        drawBeveledComponent(poseStackOrGraphics, x, y, w, h, COLOR_INNER_DARK, alpha);

        enableGuiScissor(poseStackOrGraphics, x + 2, y + 2, Math.max(1, w - 4), Math.max(1, h - 4));

        int tabH = 26;
        int tabGap = 3;
        int startY = y + 4;
        String hoveredTooltip = null;

        // Calculate animated gliding indicator Y
        long now = System.currentTimeMillis();
        long indElapsed = now - navIndicatorStartTime;
        int indDuration = getNavIndicatorAnimMs();
        if (indElapsed < indDuration) {
            float t = (float) indElapsed / indDuration;
            float ease = easeOutSine(t);
            currentIndicatorY = prevNavIndicatorY + (targetNavIndicatorY - prevNavIndicatorY) * ease;
        } else {
            currentIndicatorY = targetNavIndicatorY;
        }

        // 1. Draw base tab backgrounds (dark recessed fill)
        for (int i = 0; i < navItems.size(); i++) {
            int tabY = startY + i * (tabH + tabGap);
            int tabW = w - 6;
            int tabX = x + 3;
            boolean hover = mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH;
            int bg = hover ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG;
            drawBeveledButton(poseStackOrGraphics, tabX, tabY, tabW, tabH, bg, hover, false, alpha);
        }

        // 2. Animated sliding selection pill
        int indW = w - 6;
        int indX = x + 3;
        int indY = (int) Math.round(currentIndicatorY);
        drawBeveledButton(poseStackOrGraphics, indX, indY, indW, tabH, COLOR_BTN_BG_ACTIVE, false, true, alpha);
        // Glowing cyan/white left accent bar
        Services.PLATFORM.fill(poseStackOrGraphics, indX, indY, indX + 2, indY + tabH, applyAlpha(COLOR_ACCENT, alpha));
        // Top edge sheen
        Services.PLATFORM.fill(poseStackOrGraphics, indX + 2, indY, indX + indW, indY + 1, applyAlpha(COLOR_ACCENT_GLOW, alpha));

        // 3. Draw icons and labels with proximity-based illumination
        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int tabY = startY + i * (tabH + tabGap);
            int tabW = w - 6;
            int tabX = x + 3;

            boolean hover = mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH;

            float dist = Math.abs(tabY - currentIndicatorY) / (float) (tabH + tabGap);
            float selectWeight = Math.max(0.0f, 1.0f - dist * 1.5f);

            int iconCollapsedX = tabX + (tabW / 2) - (font.width(item.icon) / 2);
            int iconExpandedX = tabX + 6;
            int iconX = (int) Math.round(iconCollapsedX + (iconExpandedX - iconCollapsedX) * navAnimProgress);

            int baseIconCol = hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED;
            int iconCol = lerpColor(baseIconCol, COLOR_TEXT_WHITE, selectWeight);
            drawText(poseStackOrGraphics, font, item.icon, iconX, tabY + 9, applyAlpha(iconCol, alpha));

            if (navAnimProgress > 0.15f) {
                float textAlpha = alpha * Math.min(1.0f, (navAnimProgress - 0.15f) / 0.85f);
                int baseTextCol = hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED;
                int textCol = lerpColor(baseTextCol, COLOR_TEXT_WHITE, selectWeight);
                drawText(poseStackOrGraphics, font, item.title, tabX + 20, tabY + 9, applyAlpha(textCol, textAlpha));
            } else if (hover) {
                hoveredTooltip = item.title;
            }
        }

        // Bottom Toggle: Collapse / Expand Button
        int toggleH = 16;
        int toggleW = w - 6;
        int toggleX = x + 3;
        int toggleY = y + h - toggleH - 3;
        boolean hoverToggle = mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;

        drawBeveledButton(poseStackOrGraphics, toggleX, toggleY, toggleW, toggleH, hoverToggle ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverToggle, false, alpha);

        if (navAnimProgress > 0.4f) {
            float toggleAlpha = alpha * Math.min(1.0f, (navAnimProgress - 0.4f) / 0.6f);
            drawCenteredText(poseStackOrGraphics, font, "◀ Collapse", toggleX + toggleW / 2, toggleY + 4, applyAlpha(hoverToggle ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, toggleAlpha));
        } else {
            float toggleAlpha = alpha * Math.min(1.0f, (1.0f - navAnimProgress / 0.4f));
            drawCenteredText(poseStackOrGraphics, font, "▶", toggleX + toggleW / 2, toggleY + 4, applyAlpha(hoverToggle ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, toggleAlpha));
        }

        disableGuiScissor(poseStackOrGraphics);

        if (hoveredTooltip != null) {
            deferredTooltip = Collections.singletonList(ComponentHelper.literal(hoveredTooltip));
            deferredTooltipX = rawMouseX + 12;
            deferredTooltipY = rawMouseY;
        }
    }

    // --- Content Area Rendering ---
    private void renderContentArea(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        drawBeveledComponent(poseStackOrGraphics, x, y, w, h, COLOR_INNER_DARK, alpha);
        ConfigBackgrounds.drawCurrentBackground(poseStackOrGraphics, x + 1, y + 1, w - 2, h - 2, alpha, currentTheme);

        if (selectedTabIndex < 0 || selectedTabIndex >= navItems.size()) return;
        NavItem current = navItems.get(selectedTabIndex);

        long now = System.currentTimeMillis();
        float transProgress = 1.0f;
        if (isTabTransitioning) {
            long transElapsed = now - tabTransitionStartTime;
            int transDuration = getTabTransitionAnimMs();
            if (transElapsed < transDuration) {
                float t = (float) transElapsed / transDuration;
                transProgress = easeOutCubic(t);
            } else {
                isTabTransitioning = false;
            }
        }

        int slideDir = (selectedTabIndex >= prevTabIndex) ? 1 : -1;
        int slideOffsetY = (int) Math.round((1.0f - transProgress) * 3 * slideDir);
        float tabAlpha = alpha * Math.min(1.0f, 0.65f + 0.35f * transProgress);

        int catHeaderH = 22;
        drawText(poseStackOrGraphics, font, current.icon + "  " + current.title.toUpperCase(), x + 8, y + 6, applyAlpha(COLOR_TEXT_WHITE, tabAlpha));
        drawText(poseStackOrGraphics, font, current.subtitle, x + 8 + font.width(current.icon + "  " + current.title.toUpperCase()) + 10, y + 7, applyAlpha(COLOR_TEXT_MUTED, tabAlpha));
        drawGroove(poseStackOrGraphics, x + 2, y + catHeaderH, w - 4, true, alpha);

        if ("items".equals(current.id)) {
            renderItemAndTagsPanel(poseStackOrGraphics, font, x, y + slideOffsetY, w, h, catHeaderH, mouseX, mouseY, tabAlpha);
        } else if ("pillars".equals(current.id)) {
            renderPillarsPanel(poseStackOrGraphics, font, x, y + slideOffsetY, w, h, catHeaderH, mouseX, mouseY, tabAlpha);
        } else {
            int clipY = y + catHeaderH + 4;
            int viewH = h - catHeaderH - 8;
            int viewY = clipY + slideOffsetY;
            int rowH = 26;
            int rowGap = 3;

            int scrollbarW = 6;
            int scrollbarTrackX = x + w - scrollbarW - 5;
            int rowW = (scrollbarTrackX - 6) - (x + 6);
            int rowX = x + 6;

            int totalContentHeight = calculateTotalContentHeight(current.id, rowW, rowH, rowGap);
            maxScroll = Math.max(0, totalContentHeight - viewH);
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

            int startY = viewY + 2 - (int) scrollOffset;

            enableGuiScissor(poseStackOrGraphics, x + 4, clipY, w - 8, viewH);
            switch (current.id) {
                case "particles" -> renderParticleSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, clipY, viewH, mouseX, mouseY, tabAlpha);
                case "world" -> renderWorldSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, clipY, viewH, mouseX, mouseY, tabAlpha);
                case "system" -> renderSystemRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, clipY, viewH, mouseX, mouseY, tabAlpha);
            }
            disableGuiScissor(poseStackOrGraphics);

            renderCustomScrollbar(poseStackOrGraphics, scrollbarTrackX, clipY, scrollbarW, viewH, scrollOffset, maxScroll, mouseX, mouseY, tabAlpha);
        }
    }

    private int calculateTotalContentHeight(String tabId, int contentW, int rowH, int gap) {
        if ("items".equals(tabId)) {
            if (itemsSubTab == 0) {
                int slotSize = 20;
                int slotGap = 2;
                int step = slotSize + slotGap;
                int cols = Math.max(1, contentW / step);
                List<Item> items = getFilteredItemsList();
                int rows = (int) Math.ceil((double) items.size() / cols);
                return rows * step + 6;
            } else {
                List<String> tags = getFilteredTagsList();
                int tagRowH = 22;
                int tagRowGap = 2;
                return tags.size() * (tagRowH + tagRowGap) + 6;
            }
        }
        int count = switch (tabId) {
            case "particles" -> 11;
            case "world" -> 8;
            case "system" -> 6;
            default -> 0;
        };
        return count * (rowH + gap) + 6;
    }

    // --- Tab 0: Items & Tags Dual Panel with Fixed Toolbars, Scissor Clipping & Beveled Slots ---
    private void renderItemAndTagsPanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int catHeaderH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();

        int curY = y + catHeaderH + 4;

        // Row 1: Sub-View Switcher (Display Items vs Display Tags)
        int row1H = 18;
        int subBtnW = (w - 16) / 2;
        int subBtnH = 15;
        int subY = curY + 1;

        int sub0X = x + 6;
        boolean isSub0 = (itemsSubTab == 0);
        boolean hoverSub0 = mouseX >= sub0X && mouseX <= sub0X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH;
        drawBeveledButton(poseStackOrGraphics, sub0X, subY, subBtnW, subBtnH, isSub0 ? COLOR_BTN_BG_ACTIVE : COLOR_BTN_BG, hoverSub0, isSub0, alpha);
        String sub0Label = "▦ Display Items (" + cachedActiveItems.size() + ")";
        drawCenteredText(poseStackOrGraphics, font, sub0Label, sub0X + subBtnW / 2, subY + 3, applyAlpha(isSub0 ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, alpha));

        int sub1X = sub0X + subBtnW + 4;
        boolean isSub1 = (itemsSubTab == 1);
        boolean hoverSub1 = mouseX >= sub1X && mouseX <= sub1X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH;
        drawBeveledButton(poseStackOrGraphics, sub1X, subY, subBtnW, subBtnH, isSub1 ? COLOR_BTN_BG_ACTIVE : COLOR_BTN_BG, hoverSub1, isSub1, alpha);
        String sub1Label = "🏷 Display Tags (" + cachedActiveTags.size() + ")";
        drawCenteredText(poseStackOrGraphics, font, sub1Label, sub1X + subBtnW / 2, subY + 3, applyAlpha(isSub1 ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, alpha));

        curY += row1H + 2;

        // Row 2: Filter Bar (Fixed at top - 4 pill buttons)
        int bar2H = 16;
        int pillW = (w - 24) / 4;
        int pillH = 13;
        int pillY = curY + 1;

        if (itemsSubTab == 0) {
            String currentMod = !availableModNamespaces.isEmpty() ? availableModNamespaces.get(currentModIndex) : "minecraft";
            String modBtnText = (itemFilterMode == 2)
                    ? (capitalize(currentMod) + " (" + getFilteredItemsList().size() + ")")
                    : "Mod Toggles";
            String[] filterLabels = {
                    "All Items (" + availableItems.size() + ")",
                    "Inventory (" + getPlayerInventoryItems().size() + ")",
                    modBtnText,
                    "Active (" + cachedActiveItems.size() + ")"
            };
            for (int f = 0; f < 4; f++) {
                int px = x + 6 + f * (pillW + 4);
                boolean isF = (itemFilterMode == f);
                boolean hoverPill = mouseX >= px && mouseX <= px + pillW && mouseY >= pillY && mouseY <= pillY + pillH;
                drawBeveledButton(poseStackOrGraphics, px, pillY, pillW, pillH, isF ? COLOR_BTN_BG_ACTIVE : COLOR_BTN_BG, hoverPill, isF, alpha);
                int pText = isF ? COLOR_TEXT_WHITE : (hoverPill ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                drawCenteredText(poseStackOrGraphics, font, filterLabels[f], px + pillW / 2, pillY + 3, applyAlpha(pText, alpha));
            }
        } else {
            String currentMod = !availableModNamespaces.isEmpty() ? availableModNamespaces.get(currentTagModIndex) : "minecraft";
            String modBtnText = (tagFilterMode == 2)
                    ? (capitalize(currentMod) + " (" + getFilteredTagsList().size() + ")")
                    : "Mod Toggles";
            String[] filterLabels = {
                    "All Tags (" + allRegisteredTags.size() + ")",
                    "Inventory (" + getInventoryTagsSet().size() + ")",
                    modBtnText,
                    "Active (" + cachedActiveTags.size() + ")"
            };
            for (int f = 0; f < 4; f++) {
                int px = x + 6 + f * (pillW + 4);
                boolean isF = (tagFilterMode == f);
                boolean hoverPill = mouseX >= px && mouseX <= px + pillW && mouseY >= pillY && mouseY <= pillY + pillH;
                drawBeveledButton(poseStackOrGraphics, px, pillY, pillW, pillH, isF ? COLOR_BTN_BG_ACTIVE : COLOR_BTN_BG, hoverPill, isF, alpha);
                int pText = isF ? COLOR_TEXT_WHITE : (hoverPill ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                drawCenteredText(poseStackOrGraphics, font, filterLabels[f], px + pillW / 2, pillY + 3, applyAlpha(pText, alpha));
            }
        }

        curY += bar2H + 4;

        // Viewport bounds for scrollable content strictly below fixed toolbars
        int viewportY = curY;
        int viewportH = (y + h - 6) - viewportY;

        int scrollbarW = 6;
        int scrollbarTrackX = x + w - scrollbarW - 5;
        int availGridW = (scrollbarTrackX - 6) - (x + 6);

        ItemStack hoveredStack = null;
        boolean hoveredIsActive = false;

        if (itemsSubTab == 0) {
            // Slot Matrix
            List<Item> itemsToDisplay = getFilteredItemsList();
            int slotSize = 20;
            int slotGap = 2;
            int step = slotSize + slotGap;
            int cols = Math.max(1, availGridW / step);
            int totalGridW = cols * step - slotGap;
            int gridStartX = x + 6 + (availGridW - totalGridW) / 2;

            int rows = (int) Math.ceil((double) itemsToDisplay.size() / cols);
            int totalContentHeight = rows * step + 6;
            maxScroll = Math.max(0, totalContentHeight - viewportH);
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

            int startY = viewportY + 2 - (int) scrollOffset;

            enableGuiScissor(poseStackOrGraphics, x + 4, viewportY, w - 8, viewportH);

            int startRow = Math.max(0, (int) (scrollOffset / step));
            int endRow = Math.min(rows, (int) ((scrollOffset + viewportH) / step) + 1);

            for (int r = startRow; r < endRow; r++) {
                for (int c = 0; c < cols; c++) {
                    int index = r * cols + c;
                    if (index >= itemsToDisplay.size()) break;

                    int slotX = gridStartX + c * step;
                    int slotY = startY + r * step;

                    // Strict vertical culling: skip if entirely outside viewport
                    if (slotY + slotSize < viewportY || slotY > viewportY + viewportH) {
                        continue;
                    }

                    Item item = itemsToDisplay.get(index);
                    boolean isSelected = cachedActiveItems.contains(item);

                    boolean hover = mouseX >= slotX && mouseX <= slotX + slotSize && mouseY >= slotY && mouseY <= slotY + slotSize &&
                            mouseY >= viewportY && mouseY <= viewportY + viewportH;

                    int bg = isSelected ? COLOR_TOGGLE_ON_BG : (hover ? COLOR_SLOT_BG_HOVER : COLOR_SLOT_BG);
                    Services.PLATFORM.fill(poseStackOrGraphics, slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, applyAlpha(bg, alpha));

                    if (isSelected) {
                        int emTopLeft = applyAlpha(hover ? 0xFF86EFAC : 0xFF4ADE80, alpha);
                        int emBottomRight = applyAlpha(0xFF14532D, alpha);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY, slotX + slotSize, slotY + 1, emTopLeft);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY + 1, slotX + 1, slotY + slotSize - 1, emTopLeft);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, emBottomRight);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX + slotSize - 1, slotY + 1, slotX + slotSize, slotY + slotSize - 1, emBottomRight);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX + slotSize - 4, slotY + 1, slotX + slotSize - 1, slotY + 4, applyAlpha(0xFF4ADE80, alpha));
                    } else if (hover) {
                        int hTopLeft = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
                        int hBottomRight = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY, slotX + slotSize, slotY + 1, hTopLeft);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY + 1, slotX + 1, slotY + slotSize - 1, hTopLeft);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, hBottomRight);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX + slotSize - 1, slotY + 1, slotX + slotSize, slotY + slotSize - 1, hBottomRight);
                    } else {
                        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
                        int light = applyAlpha(COLOR_SLOT_BORDER, alpha);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY, slotX + slotSize, slotY + 1, dark);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY + 1, slotX + 1, slotY + slotSize, dark);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, light);
                        Services.PLATFORM.fill(poseStackOrGraphics, slotX + slotSize - 1, slotY, slotX + slotSize, slotY + slotSize, light);
                    }

                    ItemStack stack = itemStackCache.computeIfAbsent(item, ItemStack::new);
                    Services.PLATFORM.renderGuiItem(poseStackOrGraphics, stack, slotX + 2, slotY + 2);

                    if (hover) {
                        hoveredStack = stack;
                        hoveredIsActive = isSelected;
                    }
                }
            }
            disableGuiScissor(poseStackOrGraphics);

            renderCustomScrollbar(poseStackOrGraphics, scrollbarTrackX, viewportY, scrollbarW, viewportH, scrollOffset, maxScroll, mouseX, mouseY, alpha);

            if (hoveredStack != null) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(hoveredStack.getHoverName());
                tooltip.add(ComponentHelper.literal(hoveredIsActive ? "§a[Active Display Item - Click to Remove]" : "§7[Click to Add as Display Item]"));
                deferredTooltip = tooltip;
                deferredTooltipX = rawMouseX + 12;
                deferredTooltipY = rawMouseY;
            }
        } else {
            // Sub-Tab 1: Registered Tags Panel
            List<String> tagsToDisplay = getFilteredTagsList();
            int tagRowH = 22;
            int tagRowGap = 2;
            int totalContentHeight = tagsToDisplay.size() * (tagRowH + tagRowGap) + 6;
            maxScroll = Math.max(0, totalContentHeight - viewportH);
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

            int startY = viewportY + 2 - (int) scrollOffset;
            int rowW = availGridW;
            int rowX = x + 6;

            enableGuiScissor(poseStackOrGraphics, x + 4, viewportY, w - 8, viewportH);

            for (int ti = 0; ti < tagsToDisplay.size(); ti++) {
                String tagId = tagsToDisplay.get(ti);
                int rowY = startY + ti * (tagRowH + tagRowGap);
                if (rowY + tagRowH < viewportY || rowY > viewportY + viewportH) continue;

                boolean isActive = cfg.items.contains(tagId);
                boolean hover = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= rowY && mouseY <= rowY + tagRowH &&
                        mouseY >= viewportY && mouseY <= viewportY + viewportH;

                int rBg = isActive ? 0xFF142418 : (hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG);
                drawBeveledButton(poseStackOrGraphics, rowX, rowY, rowW, tagRowH, rBg, hover, isActive, alpha);

                // Emblem '#'
                int emblemSize = 14;
                int emblemX = rowX + 5;
                int emblemY = rowY + (tagRowH - emblemSize) / 2;
                int embCol = isActive ? (currentTheme.isLight() ? 0xFF15803D : 0xFF4ADE80) : COLOR_TEXT_SILVER;
                drawBeveledButton(poseStackOrGraphics, emblemX, emblemY, emblemSize, emblemSize, isActive ? (currentTheme.isLight() ? 0xFFDCFCE7 : 0xFF1A3820) : COLOR_BTN_BG, false, isActive, alpha);
                drawCenteredText(poseStackOrGraphics, font, "#", emblemX + emblemSize / 2, emblemY + 3, applyAlpha(embCol, alpha));

                // Tag Name
                int textCol = isActive ? COLOR_TEXT_WHITE : (hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
                drawText(poseStackOrGraphics, font, tagId, emblemX + emblemSize + 6, rowY + 7, applyAlpha(textCol, alpha));

                // Registered Items Count Badge
                int count = tagItemCounts.getOrDefault(tagId, 1);
                String countStr = count + " items";
                int countW = font.width(countStr) + 8;
                int countX = rowX + rowW - countW - 52;
                int countY = rowY + (tagRowH - 12) / 2;
                drawBeveledButton(poseStackOrGraphics, countX, countY, countW, 12, COLOR_SLOT_BG, false, false, alpha);
                drawCenteredText(poseStackOrGraphics, font, countStr, countX + countW / 2, countY + 2, applyAlpha(COLOR_TEXT_MUTED, alpha));

                // Toggle Button [ ON ] / [ OFF ]
                int tBtnW = 42;
                int tBtnH = 14;
                int tBtnX = rowX + rowW - tBtnW - 6;
                int tBtnY = rowY + (tagRowH - tBtnH) / 2;
                if (isActive) {
                    drawBeveledGreenButton(poseStackOrGraphics, tBtnX, tBtnY, tBtnW, tBtnH, hover, alpha);
                } else {
                    drawBeveledButton(poseStackOrGraphics, tBtnX, tBtnY, tBtnW, tBtnH, COLOR_TOGGLE_OFF_BG, hover, false, alpha);
                }
                int tBtnTextCol = isActive ? (hover ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT) : (hover ? (currentTheme.isLight() ? 0xFF1E293B : 0xFFFFFFFF) : COLOR_TOGGLE_OFF_TEXT);
                drawCenteredText(poseStackOrGraphics, font, isActive ? "ON" : "OFF", tBtnX + tBtnW / 2, tBtnY + 3, applyAlpha(tBtnTextCol, alpha));

                if (hover) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(ComponentHelper.literal(tagId));
                    tooltip.add(ComponentHelper.literal(isActive ? "§a[Active Display Tag - Click to Remove]" : "§7[Click to Add as Display Tag]"));
                    deferredTooltip = tooltip;
                    deferredTooltipX = rawMouseX + 12;
                    deferredTooltipY = rawMouseY;
                }
            }
            disableGuiScissor(poseStackOrGraphics);

            renderCustomScrollbar(poseStackOrGraphics, scrollbarTrackX, viewportY, scrollbarW, viewportH, scrollOffset, maxScroll, mouseX, mouseY, alpha);
        }
    }

    // --- Tab 1: Particles (Full Controls & Color Swatch Customizer) ---
    private void renderParticleSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        int curY = startY;

        // 1. Mode Toggle
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Use Particle Pattern", "Enable animated particle patterns", cfg.use_pattern, mouseX, mouseY, alpha);
        curY += h + gap;

        // 2. Pattern Cycle
        String patternDisplay = cfg.pattern.substring(0, 1).toUpperCase() + cfg.pattern.substring(1);
        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Pattern", "Select particle movement animation", patternDisplay, mouseX, mouseY, alpha);
        curY += h + gap;

        // 3. Pattern Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Speed", "Animation rotation and movement speed", String.format("%.2f", cfg.pattern_speed), mouseX, mouseY, alpha);
        curY += h + gap;

        // 4. Pattern Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Spread", "Particle spread radius", String.format("%.2f", cfg.pattern_spread), mouseX, mouseY, alpha);
        curY += h + gap;

        // 5. Pattern Intensity
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Intensity", "Particle spawn rate and density multiplier", String.format("%.1fx", cfg.pattern_intensity), mouseX, mouseY, alpha);
        curY += h + gap;

        // 6. Basic Particle Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Speed", "Base vertical speed of particles", String.format("%.2f", cfg.particle_speed), mouseX, mouseY, alpha);
        curY += h + gap;

        // 7. Basic Particle Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Spread", "Base spread distance of particles", String.format("%.2f", cfg.particle_spread), mouseX, mouseY, alpha);
        curY += h + gap;

        // 8. Particle Lifetime
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Lifetime", "Particle duration in ticks before fading", cfg.particle_lifetime + " ticks", mouseX, mouseY, alpha);
        curY += h + gap;

        // 9. Particle Density
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Density", "Particle count emitted per pillar", cfg.particle_density + "x", mouseX, mouseY, alpha);
        curY += h + gap;

        // 10. Swatches & Active Colors
        renderConfigRowPalette(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Colors", cfg.particle_color, cfg.max_particle_color, mouseX, mouseY, alpha);
        curY += h + gap;

        // 11. Reset Colors Action
        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Reset Colors", "Restore default particle colors", "↺ Reset Colors", mouseX, mouseY, alpha);
    }

    // --- Tab 2: Pillar IDs Panel (Authoritative in-world synchronized columns) ---
    private void renderPillarsPanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int catHeaderH, int mouseX, int mouseY, float alpha) {
        renderPillarTablePanel(poseStackOrGraphics, font, x, y, w, h, catHeaderH, mouseX, mouseY, alpha);
    }

    private void renderPillarTablePanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int catHeaderH, int mouseX, int mouseY, float alpha) {
        PillarIdManager manager = PillarIdManager.getClient();
        Collection<PillarIdManager.PillarData> pillars = manager.getAllData();
        List<PillarIdManager.PillarData> pillarList = new ArrayList<>(pillars);
        pillarList.sort(Comparator.comparing(d -> d.id != null ? d.id : ""));

        int curY = y + catHeaderH + 4;

        // --- 1. Fixed Top Action Toolbar ---
        int bar1H = 20;
        int btnH = 14;
        int barY = curY + (bar1H - btnH) / 2;

        // [ ⟳ Reload ]
        int relW = 60;
        int relX = x + 6;
        boolean hoverRel = mouseX >= relX && mouseX <= relX + relW && mouseY >= barY && mouseY <= barY + btnH;
        drawBeveledButton(poseStackOrGraphics, relX, barY, relW, btnH, hoverRel ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverRel, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "⟳ Reload", relX + relW / 2, barY + 3, applyAlpha(hoverRel ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // [ ✕ Remove Selected (<N>) ]
        int selCount = selectedPillarIds.size();
        int remSelW = 105;
        int remSelX = relX + relW + 4;
        boolean hasSel = selCount > 0;
        boolean hoverRemSel = hasSel && mouseX >= remSelX && mouseX <= remSelX + remSelW && mouseY >= barY && mouseY <= barY + btnH;
        int remSelBg = hasSel ? (hoverRemSel ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414)) : COLOR_BTN_BG;
        drawBeveledButton(poseStackOrGraphics, remSelX, barY, remSelW, btnH, remSelBg, hoverRemSel, false, alpha);
        String remSelLabel = hasSel ? ("✕ Remove (" + selCount + ")") : "✕ Remove Selected";
        int remSelTextCol = hasSel ? (hoverRemSel ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050) : COLOR_TEXT_MUTED;
        drawCenteredText(poseStackOrGraphics, font, remSelLabel, remSelX + remSelW / 2, barY + 3, applyAlpha(remSelTextCol, alpha));

        // [ ✕ Remove All ]
        int remAllW = 75;
        int remAllX = remSelX + remSelW + 4;
        boolean hasAny = !pillarList.isEmpty();
        boolean hoverRemAll = hasAny && mouseX >= remAllX && mouseX <= remAllX + remAllW && mouseY >= barY && mouseY <= barY + btnH;
        int remAllBg = hasAny ? (hoverRemAll ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414)) : COLOR_BTN_BG;
        drawBeveledButton(poseStackOrGraphics, remAllX, barY, remAllW, btnH, remAllBg, hoverRemAll, false, alpha);
        int remAllTextCol = hasAny ? (hoverRemAll ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050) : COLOR_TEXT_MUTED;
        drawCenteredText(poseStackOrGraphics, font, "✕ Remove All", remAllX + remAllW / 2, barY + 3, applyAlpha(remAllTextCol, alpha));

        // [ ✓ Save ]
        int saveW = 50;
        int saveX = remAllX + remAllW + 4;
        boolean hoverSave = mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= barY && mouseY <= barY + btnH;
        drawBeveledGreenButton(poseStackOrGraphics, saveX, barY, saveW, btnH, hoverSave, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Save", saveX + saveW / 2, barY + 3, applyAlpha(hoverSave ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT, alpha));

        // Right Count Badge
        String badgeText = pillarList.size() + (pillarList.size() == 1 ? " Pillar Placed" : " Pillars Placed");
        int badgeW = font.width(badgeText) + 10;
        int badgeX = x + w - badgeW - 6;
        drawBeveledButton(poseStackOrGraphics, badgeX, barY, badgeW, btnH, COLOR_BTN_BG, false, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, badgeText, badgeX + badgeW / 2, barY + 3, applyAlpha(COLOR_TEXT_SILVER, alpha));

        curY += bar1H + 2;

        // --- 2. Fixed Table Column Headers ---
        int headH = 16;
        int scrollbarW = 6;
        int scrollbarTrackX = x + w - scrollbarW - 5;
        int availW = (scrollbarTrackX - 6) - (x + 6);
        int colX = x + 6;

        drawBeveledButton(poseStackOrGraphics, colX, curY, availW, headH, COLOR_HEADER_BG, false, true, alpha);

        int chkW = 16;
        int iconColW = 38;
        int colorsColW = 48;
        int dimColW = 58;
        int coordsColW = 72;
        int actionsColW = 58;
        int gap = 4;
        int idColW = Math.max(65, availW - (chkW + iconColW + colorsColW + dimColW + coordsColW + actionsColW + (gap * 6)));

        // Header Checkbox
        boolean allChecked = hasAny && selectedPillarIds.size() == pillarList.size();
        boolean someChecked = !selectedPillarIds.isEmpty() && !allChecked;
        int chkBoxSize = 10;
        int chkX = colX + (chkW - chkBoxSize) / 2;
        int chkY = curY + (headH - chkBoxSize) / 2;
        drawBeveledButton(poseStackOrGraphics, chkX, chkY, chkBoxSize, chkBoxSize, COLOR_SLOT_BG, false, allChecked, alpha);
        if (allChecked) {
            Services.PLATFORM.fill(poseStackOrGraphics, chkX + 2, chkY + 2, chkX + chkBoxSize - 2, chkY + chkBoxSize - 2, applyAlpha(0xFF22C55E, alpha));
        } else if (someChecked) {
            Services.PLATFORM.fill(poseStackOrGraphics, chkX + 2, chkY + (chkBoxSize / 2) - 1, chkX + chkBoxSize - 2, chkY + (chkBoxSize / 2) + 1, applyAlpha(0xFF22C55E, alpha));
        }

        int curColX = colX + chkW + gap;

        // ID Header
        drawText(poseStackOrGraphics, font, "ID", curColX + 2, curY + 4, applyAlpha(COLOR_TEXT_SILVER, alpha));
        curColX += idColW + gap;

        // ITEM Header
        drawCenteredText(poseStackOrGraphics, font, "ITEM", curColX + iconColW / 2, curY + 4, applyAlpha(COLOR_TEXT_SILVER, alpha));
        curColX += iconColW + gap;

        // COLORS Header
        drawCenteredText(poseStackOrGraphics, font, "COLORS", curColX + colorsColW / 2, curY + 4, applyAlpha(COLOR_TEXT_SILVER, alpha));
        curColX += colorsColW + gap;

        // DIMENSION Header
        drawCenteredText(poseStackOrGraphics, font, "DIMENSION", curColX + dimColW / 2, curY + 4, applyAlpha(COLOR_TEXT_SILVER, alpha));
        curColX += dimColW + gap;

        // COORDINATES Header (X, Y, Z colored)
        int cx = curColX + coordsColW / 2;
        int coordLabelW = font.width("X  Y  Z");
        int coordStartX = cx - coordLabelW / 2;
        drawText(poseStackOrGraphics, font, "X", coordStartX, curY + 4, applyAlpha(0xFFFF5555, alpha));
        drawText(poseStackOrGraphics, font, "Y", coordStartX + font.width("X  "), curY + 4, applyAlpha(0xFF5599FF, alpha));
        drawText(poseStackOrGraphics, font, "Z", coordStartX + font.width("X  Y  "), curY + 4, applyAlpha(0xFF55FF55, alpha));
        curColX += coordsColW + gap;

        // ACTIONS Header
        drawCenteredText(poseStackOrGraphics, font, "ACTIONS", curColX + actionsColW / 2, curY + 4, applyAlpha(COLOR_TEXT_SILVER, alpha));

        curY += headH + 3;

        // --- 3. Scrollable Rows Viewport ---
        int viewportY = curY;
        int viewportH = (y + h - 6) - viewportY;

        int rowH = 24;
        int rowGap = 2;
        int totalContentHeight = Math.max(1, pillarList.size()) * (rowH + rowGap) + 4;
        maxScroll = Math.max(0, totalContentHeight - viewportH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        int startY = viewportY + 2 - (int) scrollOffset;

        enableGuiScissor(poseStackOrGraphics, x + 4, viewportY, w - 8, viewportH);

        if (pillarList.isEmpty()) {
            int emptyY = startY + 8;
            drawBeveledButton(poseStackOrGraphics, colX, emptyY, availW, 36, COLOR_ROW_BG, false, false, alpha);
            drawCenteredText(poseStackOrGraphics, font, "No placed pillars found in world.", colX + availW / 2, emptyY + 8, applyAlpha(COLOR_TEXT_SILVER, alpha));
            drawCenteredText(poseStackOrGraphics, font, "Place and customize pillar columns to synchronize their IDs.", colX + availW / 2, emptyY + 20, applyAlpha(COLOR_TEXT_MUTED, alpha));
        } else {
            for (int r = 0; r < pillarList.size(); r++) {
                PillarIdManager.PillarData data = pillarList.get(r);
                int rowY = startY + r * (rowH + rowGap);
                if (rowY + rowH < viewportY || rowY > viewportY + viewportH) continue;

                boolean isSelected = selectedPillarIds.contains(data.id);
                boolean isEditingInSidePanel = sidePanelProgress > 0.05f && sidePanelType == SidePanelType.PILLAR_DETAIL && Objects.equals(selectedPillarDetailId, data.id);
                boolean hover = mouseX >= colX && mouseX <= colX + availW && mouseY >= rowY && mouseY <= rowY + rowH &&
                        mouseY >= viewportY && mouseY <= viewportY + viewportH;

                int rowBg = isEditingInSidePanel ? (currentTheme.isLight() ? 0xFFDCFCE7 : 0xFF1C2C22) : (isSelected ? (currentTheme.isLight() ? 0xFFF0FDF4 : 0xFF16241A) : (hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG));
                drawBeveledButton(poseStackOrGraphics, colX, rowY, availW, rowH, rowBg, hover, isSelected || isEditingInSidePanel, alpha);

                // Row Checkbox
                int rChkX = colX + (chkW - chkBoxSize) / 2;
                int rChkY = rowY + (rowH - chkBoxSize) / 2;
                drawBeveledButton(poseStackOrGraphics, rChkX, rChkY, chkBoxSize, chkBoxSize, COLOR_SLOT_BG, hover, isSelected, alpha);
                if (isSelected) {
                    Services.PLATFORM.fill(poseStackOrGraphics, rChkX + 2, rChkY + 2, rChkX + chkBoxSize - 2, rChkY + chkBoxSize - 2, applyAlpha(0xFF22C55E, alpha));
                }

                int rcX = colX + chkW + gap;

                // ID Column (clickable to open detail!)
                String idText = data.id != null ? data.id : "unknown";
                int idTextCol = hover ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER;
                drawText(poseStackOrGraphics, font, font.plainSubstrByWidth(idText, idColW - 4), rcX + 2, rowY + 8, applyAlpha(idTextCol, alpha));
                rcX += idColW + gap;

                // ITEM Column (Pillar Type + Displayed Item icons side by side)
                ItemStack pillarTypeStack = getItemStackFromId(data.pillarType);
                ItemStack dispStack = getItemStackFromId(data.displayedItem);
                int iconY = rowY + (rowH - 16) / 2;
                int iconStartX = rcX + (iconColW - 36) / 2;

                if (!pillarTypeStack.isEmpty() || !dispStack.isEmpty()) {
                    if (!pillarTypeStack.isEmpty()) {
                        Services.PLATFORM.renderGuiItem(poseStackOrGraphics, pillarTypeStack, iconStartX, iconY);
                    }
                    if (!dispStack.isEmpty()) {
                        Services.PLATFORM.renderGuiItem(poseStackOrGraphics, dispStack, iconStartX + 18, iconY);
                    }
                } else {
                    drawCenteredText(poseStackOrGraphics, font, "—", rcX + iconColW / 2, rowY + 8, applyAlpha(COLOR_TEXT_MUTED, alpha));
                }
                rcX += iconColW + gap;

                // COLORS Column (Mini swatches)
                List<String> colors = (data.dyeColors != null && !data.dyeColors.isEmpty()) ? data.dyeColors : PillarParticleConfig.get().particle_color;
                int numSwatches = Math.min(5, colors != null ? colors.size() : 0);
                if (numSwatches > 0) {
                    int swSize = 8;
                    int swGap = 2;
                    int totalSwW = numSwatches * swSize + (numSwatches - 1) * swGap;
                    int swStartX = rcX + (colorsColW - totalSwW) / 2;
                    int swY = rowY + (rowH - swSize) / 2;
                    for (int s = 0; s < numSwatches; s++) {
                        int swX = swStartX + s * (swSize + swGap);
                        int colInt = 0xFFFFFFFF;
                        try {
                            String hex = colors.get(s).replace("#", "");
                            colInt = 0xFF000000 | Integer.parseInt(hex, 16);
                        } catch (Exception ignored) {}
                        Services.PLATFORM.fill(poseStackOrGraphics, swX, swY, swX + swSize, swY + swSize, applyAlpha(colInt, alpha));
                        drawBorder(poseStackOrGraphics, swX, swY, swSize, swSize, applyAlpha(0xFF000000, alpha));
                    }
                } else {
                    drawCenteredText(poseStackOrGraphics, font, "—", rcX + colorsColW / 2, rowY + 8, applyAlpha(COLOR_TEXT_MUTED, alpha));
                }
                rcX += colorsColW + gap;

                // DIMENSION Column
                String dimName = formatDimensionName(data.dimension);
                drawCenteredText(poseStackOrGraphics, font, font.plainSubstrByWidth(dimName, dimColW - 2), rcX + dimColW / 2, rowY + 8, applyAlpha(COLOR_TEXT_MUTED, alpha));
                rcX += dimColW + gap;

                // COORDINATES Column (colored X, Y, Z)
                String coordsStr = String.format("%d %d %d", data.x, data.y, data.z);
                int coordTextW = font.width(coordsStr);
                int cX = rcX + (coordsColW - coordTextW) / 2;
                String xStr = String.valueOf(data.x);
                String yStr = String.valueOf(data.y);
                String zStr = String.valueOf(data.z);
                drawText(poseStackOrGraphics, font, xStr, cX, rowY + 8, applyAlpha(0xFFFF5555, alpha));
                int xW = font.width(xStr + " ");
                drawText(poseStackOrGraphics, font, yStr, cX + xW, rowY + 8, applyAlpha(0xFF5599FF, alpha));
                int yW = font.width(yStr + " ");
                drawText(poseStackOrGraphics, font, zStr, cX + xW + yW, rowY + 8, applyAlpha(0xFF55FF55, alpha));
                rcX += coordsColW + gap;

                // ACTIONS Column ([ ✎ Edit ] and [ ✕ ])
                int actH = 13;
                int actY = rowY + (rowH - actH) / 2;

                int editW = 34;
                int editX = rcX + 2;
                boolean hoverEdit = mouseX >= editX && mouseX <= editX + editW && mouseY >= actY && mouseY <= actY + actH;
                drawBeveledButton(poseStackOrGraphics, editX, actY, editW, actH, hoverEdit ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverEdit, false, alpha);
                drawCenteredText(poseStackOrGraphics, font, "✎ Edit", editX + editW / 2, actY + 2, applyAlpha(hoverEdit ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

                int delW = 16;
                int delX = editX + editW + 4;
                boolean hoverDel = mouseX >= delX && mouseX <= delX + delW && mouseY >= actY && mouseY <= actY + actH;
                int delBg = hoverDel ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414);
                drawBeveledButton(poseStackOrGraphics, delX, actY, delW, actH, delBg, hoverDel, false, alpha);
                int delTextCol = hoverDel ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050;
                drawCenteredText(poseStackOrGraphics, font, "✕", delX + delW / 2, actY + 2, applyAlpha(delTextCol, alpha));
            }
        }

        disableGuiScissor(poseStackOrGraphics);

        renderCustomScrollbar(poseStackOrGraphics, scrollbarTrackX, viewportY, scrollbarW, viewportH, scrollOffset, maxScroll, mouseX, mouseY, alpha);
    }

    // --- Tab 2 (Detail): Per-Pillar Customization View ---
    private void renderPillarDetailPanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int catHeaderH, int mouseX, int mouseY, float alpha) {
        PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(selectedPillarDetailId);
        if (data == null) {
            selectedPillarDetailId = null;
            return;
        }

        PillarParticleConfig globalCfg = PillarParticleConfig.get();

        int curY = y + catHeaderH + 4;

        // --- 1. Fixed Top Detail Header Bar ---
        int bar1H = 20;
        int btnH = 14;
        int barY = curY + (bar1H - btnH) / 2;

        // [ ◀ Back ]
        int backW = 54;
        int backX = x + 6;
        boolean hoverBack = mouseX >= backX && mouseX <= backX + backW && mouseY >= barY && mouseY <= barY + btnH;
        drawBeveledButton(poseStackOrGraphics, backX, barY, backW, btnH, hoverBack ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverBack, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "◀ Back", backX + backW / 2, barY + 3, applyAlpha(hoverBack ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // Title: Pillar ID + coords badge
        String titleText = "Pillar: " + data.id;
        drawText(poseStackOrGraphics, font, titleText, backX + backW + 8, barY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        String locText = String.format("[%s · %d, %d, %d]", formatDimensionName(data.dimension), data.x, data.y, data.z);
        drawText(poseStackOrGraphics, font, locText, backX + backW + 8 + font.width(titleText) + 8, barY + 3, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // [ ✓ Save Pillar ]
        int saveW = 82;
        int saveX = x + w - saveW - 6;
        boolean hoverSave = mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= barY && mouseY <= barY + btnH;
        drawBeveledGreenButton(poseStackOrGraphics, saveX, barY, saveW, btnH, hoverSave, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Save Pillar", saveX + saveW / 2, barY + 3, applyAlpha(hoverSave ? 0xFFFFFFFF : COLOR_TOGGLE_ON_TEXT, alpha));

        curY += bar1H + 3;

        // --- 2. Scrollable Detail Viewport ---
        int viewportY = curY;
        int viewportH = (y + h - 6) - viewportY;
        int rowH = 26;
        int rowGap = 3;

        int scrollbarW = 6;
        int scrollbarTrackX = x + w - scrollbarW - 5;
        int rowW = (scrollbarTrackX - 6) - (x + 6);
        int rowX = x + 6;

        int totalContentHeight = 7 * (rowH + rowGap) + 8;
        maxScroll = Math.max(0, totalContentHeight - viewportH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        int startY = viewportY + 2 - (int) scrollOffset;
        int rY = startY;

        enableGuiScissor(poseStackOrGraphics, x + 4, viewportY, w - 8, viewportH);

        // Row 1: Use Custom Pattern Toggle (fallback to global use_pattern)
        boolean usePat = data.use_pattern != null ? data.use_pattern : globalCfg.use_pattern;
        renderConfigRowToggle(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Use Custom Pattern", "Override global particle pattern for this pillar", usePat, mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 2: Particle Pattern Cycle (fallback to "default")
        String curPat = data.pattern != null && !data.pattern.isEmpty() ? data.pattern : "default";
        String patDisplay = curPat.substring(0, 1).toUpperCase() + curPat.substring(1);
        renderConfigRowCycle(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Particle Pattern", "Select particle movement animation", patDisplay, mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 3: Pattern Speed Counter (fallback to global pattern_speed)
        double speed = data.pattern_speed != null ? data.pattern_speed : globalCfg.pattern_speed;
        renderConfigRowCounter(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pattern Speed", "Animation rotation and movement speed", String.format("%.2f", speed), mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 4: Pattern Spread Counter (fallback to global pattern_spread)
        double spread = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
        renderConfigRowCounter(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pattern Spread", "Particle spread radius", String.format("%.2f", spread), mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 5: Pattern Intensity Counter (fallback to global pattern_intensity)
        double intensity = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
        renderConfigRowCounter(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pattern Intensity", "Particle spawn rate and density multiplier", String.format("%.1fx", intensity), mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 6: Per-Pillar Color Palette (fallback to global particle_color & max_particle_color)
        List<String> colors = (data.dyeColors != null && !data.dyeColors.isEmpty()) ? data.dyeColors : new ArrayList<>(globalCfg.particle_color);
        while (colors.size() < 5) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(5, Math.max(1, colors.size())));
        renderConfigRowPalette(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pillar Colors", colors, maxColors, mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 7: Save & Sync Pillar Configuration
        renderConfigRowButton(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Save Changes", "Save and sync pillar settings to server", "✓ Save", mouseX, mouseY, alpha);

        disableGuiScissor(poseStackOrGraphics);

        renderCustomScrollbar(poseStackOrGraphics, scrollbarTrackX, viewportY, scrollbarW, viewportH, scrollOffset, maxScroll, mouseX, mouseY, alpha);
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

        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "UI Theme", "Visual color palette for configuration interface", currentTheme.displayName, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "UI Background", "Decorative backdrop pattern applied over the theme", ConfigBackgrounds.getCurrentBackground().displayName, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Screen Animation", "Smooth UI transitions and animation speed", ANIM_SPEEDS[animSpeedIndex], mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Mod Version", "Currently installed Buildscape version", "4.0.3", alpha);
        curY += h + gap;

        String opStatus = hasOpAccess() ? "OP (Full Access)" : "Player (Client Only)";
        renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Permissions", "Permission level for editing server game rules", opStatus, alpha);
    }

    // --- Draggable Color Picker Modal (2D Saturation x Brightness + Hue Spectrum Slider) ---
    private void renderColorPickerModal(Object poseStackOrGraphics, Font font, int mouseX, int mouseY, float alpha) {
        int effW = getEffectiveWidth();
        int effH = getEffectiveHeight();
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, effW, effH, applyAlpha(0xA0000000, alpha));

        int modalW = 340;
        int modalH = 196;
        int modalX = (effW - modalW) / 2;
        int modalY = (effH - modalH) / 2;

        drawVaultHuntersFrame(poseStackOrGraphics, modalX, modalY, modalW, modalH, COLOR_PANEL_BG, alpha);

        int headH = 24;
        Services.PLATFORM.fill(poseStackOrGraphics, modalX + 7, modalY + 7, modalX + modalW - 7, modalY + headH, applyAlpha(COLOR_HEADER_BG, alpha));
        drawBorder(poseStackOrGraphics, modalX + 7, modalY + 7, modalW - 14, headH - 7, applyAlpha(COLOR_FRAME_BLACK, alpha));
        String modalTitle = isEditingPillarColor ? "Customize Pillar Color" : "Customize Particle Color";
        drawText(poseStackOrGraphics, font, modalTitle, modalX + 12, modalY + 9, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Close button
        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 10;
        int closeY = modalY + 7;
        boolean hoverClose = mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize;
        int closeBg = hoverClose ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414);
        drawBeveledButton(poseStackOrGraphics, closeX, closeY, closeSize, closeSize, closeBg, hoverClose, false, alpha);
        int closeCol = hoverClose ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050;
        drawCenteredText(poseStackOrGraphics, font, "✕", closeX + closeSize / 2, closeY + 3, applyAlpha(closeCol, alpha));

        // 1. 2D Saturation x Brightness Draggable Box
        int gradBoxX = modalX + 16;
        int gradBoxY = modalY + headH + 12;
        int gradBoxW = 110;
        int gradBoxH = 88;

        for (int x = 0; x < gradBoxW; x += 3) {
            float sat = x / (float) gradBoxW;
            int topRgb = hsbToRgb(pickerHue, sat, 1.0f);
            int gw = Math.min(3, gradBoxW - x);
            for (int y = 0; y < gradBoxH; y += 3) {
                float bri = 1.0f - (y / (float) gradBoxH);
                int pixelCol = scaleBrightness(topRgb, bri);
                int gh = Math.min(3, gradBoxH - y);
                Services.PLATFORM.fill(poseStackOrGraphics, gradBoxX + x, gradBoxY + y, gradBoxX + x + gw, gradBoxY + y + gh, applyAlpha(pixelCol, alpha));
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

        for (int y = 0; y < hueH; y += 3) {
            float h = (y / (float) hueH) * 360.0f;
            int col = hsbToRgb(h, 1.0f, 1.0f);
            int hh = Math.min(3, hueH - y);
            Services.PLATFORM.fill(poseStackOrGraphics, hueX, hueY + y, hueX + hueW, hueY + y + hh, applyAlpha(col, alpha));
        }
        drawBorder(poseStackOrGraphics, hueX - 1, hueY - 1, hueW + 2, hueH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

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
        drawBeveledGreenButton(poseStackOrGraphics, applyX, applyY, applyW, applyH, hoverApply, alpha);
        int applyTextCol = hoverApply ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT;
        drawCenteredText(poseStackOrGraphics, font, "✓ Apply Color", applyX + applyW / 2, applyY + 4, applyAlpha(applyTextCol, alpha));
    }

    // --- Vault Hunters Border & Bevel Engine (Expanded Chassis Gap & Metallic Gradient) ---
    private void drawVaultHuntersFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        drawVaultHuntersFrame(poseStackOrGraphics, x, y, w, h, bgRgb, false, alpha);
    }

    private void drawVaultHuntersFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, boolean hasCrestNotch, float alpha) {
        // 1. Outer Chassis Body Fill (Solid metallic chassis backing)
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int band = applyAlpha(COLOR_CHASSIS_BAND, alpha);
        int bevelShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int bevelHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);

        int crestW = 152;
        int crestX = x + (w - crestW) / 2;

        // 2. Outermost 1px Black Border
        if (hasCrestNotch) {
            Services.PLATFORM.fill(poseStackOrGraphics, x, y, crestX + 1, y + 1, black);
            Services.PLATFORM.fill(poseStackOrGraphics, crestX + crestW - 1, y, x + w, y + 1, black);
        } else {
            Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        }
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);

        // 3. 1-Pixel Wide Metallic Gradient Highlight (Top & Left) & Deep Shadow (Bottom & Right)
        if (hasCrestNotch) {
            // Left top horizontal: from x + 1 to crestX (abuts logo badge's left black border)
            int leftSegW = crestX - (x + 1);
            if (leftSegW > 0) {
                float tLeftEnd = (float) (crestX - x) / (float) Math.max(1, w - 3);
                int cCornerLeft = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, tLeftEnd);
                drawHorizontalGradientLine(poseStackOrGraphics, x + 1, y + 1, leftSegW, COLOR_BORDER_GRADIENT_START, cCornerLeft, alpha);
            }

            // Right top horizontal: from crestX + crestW to x + w - 2 (abuts logo badge's right black border)
            int rightStart = crestX + crestW;
            int rightSegW = (x + w - 2) - rightStart + 1;
            if (rightSegW > 0) {
                float tRightStart = (float) (rightStart - x) / (float) Math.max(1, w - 3);
                int cCornerRight = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, tRightStart);
                drawHorizontalGradientLine(poseStackOrGraphics, rightStart, y + 1, rightSegW, cCornerRight, COLOR_BORDER_GRADIENT_END, alpha);
            }
        } else {
            drawHorizontalGradientLine(poseStackOrGraphics, x + 1, y + 1, w - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        }

        drawVerticalGradientLine(poseStackOrGraphics, x + 1, y + 1, h - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + h - 2, x + w - 1, y + h - 2, shadow);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, shadow);

        // 4. Outer Chassis Band (offset 2 to 3)
        for (int o = 2; o <= 3; o++) {
            if (hasCrestNotch) {
                if (crestX > x + o) Services.PLATFORM.fill(poseStackOrGraphics, x + o, y + o, crestX, y + o + 1, band);
                if (x + w - o > crestX + crestW) Services.PLATFORM.fill(poseStackOrGraphics, crestX + crestW, y + o, x + w - o, y + o + 1, band);
            } else {
                Services.PLATFORM.fill(poseStackOrGraphics, x + o, y + o, x + w - o, y + o + 1, band);
            }
            Services.PLATFORM.fill(poseStackOrGraphics, x + o, y + h - o - 1, x + w - o, y + h - o, band);
            Services.PLATFORM.fill(poseStackOrGraphics, x + o, y + o, x + o + 1, y + h - o, band);
            Services.PLATFORM.fill(poseStackOrGraphics, x + w - o - 1, y + o, x + w - o, y + h - o, band);
        }

        // 5. Stepped Bevel Ridge & Groove at offset 5
        int o5 = 5;
        if (hasCrestNotch) {
            if (crestX > x + o5) Services.PLATFORM.fill(poseStackOrGraphics, x + o5, y + o5, crestX, y + o5 + 1, bevelShadow);
            if (x + w - o5 > crestX + crestW) Services.PLATFORM.fill(poseStackOrGraphics, crestX + crestW, y + o5, x + w - o5, y + o5 + 1, bevelShadow);
        } else {
            Services.PLATFORM.fill(poseStackOrGraphics, x + o5, y + o5, x + w - o5, y + o5 + 1, bevelShadow);
        }
        Services.PLATFORM.fill(poseStackOrGraphics, x + o5, y + o5, x + o5 + 1, y + h - o5, bevelShadow);
        Services.PLATFORM.fill(poseStackOrGraphics, x + o5, y + h - o5 - 1, x + w - o5, y + h - o5, bevelHighlight);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - o5 - 1, y + o5, x + w - o5, y + h - o5, bevelHighlight);

        // 6. Secondary soft metallic transition at offset 8
        int softGroove = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        drawBorder(poseStackOrGraphics, x + 8, y + 8, w - 16, h - 16, softGroove);
    }

    // --- Connected Right Side Panel Frame ---
    private void drawConnectedSidePanelFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        // 1. Outer Chassis Body Fill (Solid metallic chassis backing)
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);

        // 2. Outermost 1px Black Border (Top, Right, Bottom - seamless connection on left under main frame)
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, black); // Tucked under main frame edge

        // 3. 1-Pixel Wide Metallic Gradient Highlight (Top) & Shadow (Bottom & Right)
        drawHorizontalGradientLine(poseStackOrGraphics, x, y + 1, w - 1, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 2, x + w - 1, y + h - 2, shadow);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, shadow);

        // 4. Outer Chassis Band (offset 2 to 4)
        int sideBand = applyAlpha(COLOR_CHASSIS_BAND, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + 2, x + w - 2, y + 3, sideBand);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 3, x + w - 2, y + h - 2, sideBand);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 3, y + 2, x + w - 2, y + h - 2, sideBand);

        // 5. Stepped Bevel Ridge & Groove at offset 4
        int sideBevelShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int sideBevelHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + 4, x + w - 4, y + 5, sideBevelShadow);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 5, x + w - 4, y + h - 4, sideBevelHighlight);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 5, y + 4, x + w - 4, y + h - 4, sideBevelHighlight);
    }

    private void renderSidePanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        if (sidePanelType == SidePanelType.NONE) return;

        // 1. Chassis Frame (animates smoothly in 2 directions for Presets, or slides for others)
        drawConnectedSidePanelFrame(poseStackOrGraphics, x, y, w, h, COLOR_PANEL_BG, alpha);

        if (w >= 60 && h >= 40) {
            // 2. Header
            int sHeadX = x + 6;
            int sHeadY = y + 7;
            int sHeadW = w - 12;
            int sHeadH = 22;
            drawBeveledComponent(poseStackOrGraphics, sHeadX, sHeadY, sHeadW, sHeadH, COLOR_HEADER_BG, alpha);

            String titleText = "Modifiers";
            if (sidePanelType == SidePanelType.PILLAR_DETAIL) {
                String activeId = selectedPillarDetailId != null ? selectedPillarDetailId : closingPillarDetailId;
                PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(activeId);
                if (data != null && data.id != null) {
                    titleText = "Pillar: " + font.plainSubstrByWidth(data.id, sHeadW - 32);
                } else {
                    titleText = "Pillar Modifiers";
                }
            } else if (sidePanelType == SidePanelType.COLOR_PICKER) {
                titleText = isEditingPillarColor ? "Pillar Color" : "Particle Color";
            } else if (sidePanelType == SidePanelType.PRESETS) {
                titleText = "Presets";
            }

            long now = System.currentTimeMillis();
            float switchProgress = 1.0f;
            if (sidePanelType == SidePanelType.PILLAR_DETAIL && isPillarSwitching) {
                long switchElapsed = now - pillarSwitchStartTime;
                int switchDur = Math.max(1, (int) (220 * getAnimSpeedMultiplier()));
                if (switchElapsed < switchDur) {
                    switchProgress = easeOutCubic((float) switchElapsed / switchDur);
                } else {
                    isPillarSwitching = false;
                }
            }

            int titleOffsetX = (sidePanelType == SidePanelType.PILLAR_DETAIL && isPillarSwitching)
                    ? (int) Math.round((1.0f - switchProgress) * 8) : 0;
            float titleAlpha = (sidePanelType == SidePanelType.PILLAR_DETAIL && isPillarSwitching)
                    ? alpha * (0.25f + 0.75f * switchProgress) : alpha;

            drawText(poseStackOrGraphics, font, titleText, sHeadX + 6 + titleOffsetX, sHeadY + 7, applyAlpha(COLOR_TEXT_WHITE, titleAlpha));

            if (sidePanelType == SidePanelType.PILLAR_DETAIL && isPillarSwitching) {
                float glowAlpha = (1.0f - switchProgress) * 0.45f * alpha;
                drawBorder(poseStackOrGraphics, sHeadX, sHeadY, sHeadW, sHeadH, applyAlpha(COLOR_ACCENT, glowAlpha));
            }

            // Close Button [ ✕ ]
            int closeSize = 14;
            int closeX = sHeadX + sHeadW - closeSize - 4;
            int closeY = sHeadY + 4;
            boolean hoverClose = mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize;
            int closeBg = hoverClose ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414);
            drawBeveledButton(poseStackOrGraphics, closeX, closeY, closeSize, closeSize, closeBg, hoverClose, false, alpha);
            int closeCol = hoverClose ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050;
            drawCenteredText(poseStackOrGraphics, font, "✕", closeX + closeSize / 2, closeY + 3, applyAlpha(closeCol, alpha));

            // 3. Content Area
            int sContentX = x + 6;
            int sContentY = sHeadY + sHeadH + 4;
            int sContentW = sHeadW;
            int sContentH = (y + h - 7) - sContentY;

            if (sContentH > 20) {
                if (sidePanelType == SidePanelType.PILLAR_DETAIL) {
                    renderSidePanelPillarDetail(poseStackOrGraphics, font, sContentX, sContentY, sContentW, sContentH, mouseX, mouseY, alpha);
                } else if (sidePanelType == SidePanelType.COLOR_PICKER) {
                    renderSidePanelColorPicker(poseStackOrGraphics, font, sContentX, sContentY, sContentW, sContentH, mouseX, mouseY, alpha);
                } else if (sidePanelType == SidePanelType.PRESETS) {
                    renderSidePanelPresets(poseStackOrGraphics, font, sContentX, sContentY, sContentW, sContentH, mouseX, mouseY, alpha);
                }
            }
        }
    }

    private void renderSidePanelPresets(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        drawBeveledComponent(poseStackOrGraphics, x, y, w, h, COLOR_INNER_DARK, alpha);

        PresetsConfig presetsCfg = PresetsConfig.get();
        List<PresetsConfig.Preset> presets = presetsCfg.getPresets();
        List<String> keys = presetsCfg.getPresetKeys();
        if (selectedPresetIndex >= presets.size()) {
            selectedPresetIndex = Math.max(0, presets.size() - 1);
        }
        PresetsConfig.Preset currentPreset = presets.isEmpty() ? null : presets.get(selectedPresetIndex);
        String currentKey = (keys.isEmpty() || selectedPresetIndex >= keys.size()) ? "default" : keys.get(selectedPresetIndex);
        String currentPresetName = currentPreset != null ? currentPreset.name : "Default";
        boolean isDefault = "default".equalsIgnoreCase(currentKey);

        int pad = 5;
        int curY = y + pad;
        int rowW = w - pad * 2;
        int rowX = x + pad;

        // Card 0: Active Preset selector & count
        int selH = 34;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, selH, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, "Active Preset", rowX + 5, curY + 4, applyAlpha(COLOR_TEXT_SILVER, alpha));

        int cycleBtnH = 15;
        int cycleBtnY = curY + 15;
        int cycleBtnW = rowW - 10;
        int cycleBtnX = rowX + 5;
        boolean hoverCycle = mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH;
        drawBeveledButton(poseStackOrGraphics, cycleBtnX, cycleBtnY, cycleBtnW, cycleBtnH, hoverCycle ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverCycle, false, alpha);
        String cycleLabel = "‹ " + currentPresetName + " (" + (currentPreset != null ? currentPreset.items.size() : 0) + ") ›";
        drawCenteredText(poseStackOrGraphics, font, cycleLabel, cycleBtnX + cycleBtnW / 2, cycleBtnY + 4, applyAlpha(hoverCycle ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        curY += selH + 5;

        // Card 1: Apply Preset button
        int btnH = 18;
        boolean hoverApply = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledGreenButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverApply, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Apply Preset", rowX + rowW / 2, curY + 5, applyAlpha(hoverApply ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT, alpha));
        curY += btnH + 4;

        // Card 2: Save / Overwrite Current Preset button
        boolean hoverSave = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverSave ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverSave, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "💾 Save to Preset", rowX + rowW / 2, curY + 5, applyAlpha(hoverSave ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        curY += btnH + 4;

        // Card 3: + Save as New Preset button
        boolean hoverNew = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverNew ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverNew, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+ Save as New Preset", rowX + rowW / 2, curY + 5, applyAlpha(hoverNew ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        curY += btnH + 4;

        // Card 4: ↺ Reset to Defaults button
        boolean hoverReset = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverReset ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverReset, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "↺ Reset to Defaults", rowX + rowW / 2, curY + 5, applyAlpha(hoverReset ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        curY += btnH + 4;

        // Card 5: Delete Custom Preset button (only if not default)
        if (!isDefault) {
            boolean hoverDelete = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
            drawBeveledRedButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverDelete, alpha);
            drawCenteredText(poseStackOrGraphics, font, "✕ Delete Preset", rowX + rowW / 2, curY + 5, applyAlpha(hoverDelete ? (currentTheme.isLight() ? 0xFF1E293B : 0xFFFFFFFF) : COLOR_TOGGLE_OFF_TEXT, alpha));
        }
    }

    private void drawPresetsSideTab(Object poseStackOrGraphics, Font font, int frameRight, int tabY, int mouseX, int mouseY, float alpha) {
        int tabW = 20;
        int tabH = 28;
        int tabX = frameRight - 2;

        boolean hover = mouseX >= tabX && mouseX <= tabX + tabW + 6 && mouseY >= tabY && mouseY <= tabY + tabH;
        int popOffset = hover ? 3 : 0;
        int currentW = tabW + popOffset;

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int sideBand = applyAlpha(COLOR_CHASSIS_BAND, alpha);
        int sideBevelShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int sideBevelHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
        int btnBg = applyAlpha(hover ? COLOR_BTN_BG_HOVER : COLOR_PANEL_BG, alpha);

        // 1. Solid chassis body backing
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY, tabX + currentW, tabY + tabH, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        // 2. Outermost 1px Black Border (Top, Right, Bottom - seamlessly attached on left to frame)
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY, tabX + currentW, tabY + 1, black);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY + tabH - 1, tabX + currentW, tabY + tabH, black);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX + currentW - 1, tabY, tabX + currentW, tabY + tabH, black);

        // 3. 1-Pixel Wide Metallic Gradient Highlight (Top) & Shadow (Bottom & Right)
        drawHorizontalGradientLine(poseStackOrGraphics, tabX, tabY + 1, currentW - 1, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY + tabH - 2, tabX + currentW - 1, tabY + tabH - 1, shadow);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX + currentW - 2, tabY + 1, tabX + currentW - 1, tabY + tabH - 1, shadow);

        // 4. Outer Chassis Band (offset 2)
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY + 2, tabX + currentW - 2, tabY + 3, sideBand);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY + tabH - 3, tabX + currentW - 2, tabY + tabH - 2, sideBand);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX + currentW - 3, tabY + 2, tabX + currentW - 2, tabY + tabH - 2, sideBand);

        // 5. Stepped Bevel Ridge (offset 3)
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY + 3, tabX + currentW - 3, tabY + 4, sideBevelShadow);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY + tabH - 4, tabX + currentW - 3, tabY + tabH - 3, sideBevelHighlight);
        Services.PLATFORM.fill(poseStackOrGraphics, tabX + currentW - 4, tabY + 3, tabX + currentW - 3, tabY + tabH - 3, sideBevelHighlight);

        // 6. Inner button fill
        Services.PLATFORM.fill(poseStackOrGraphics, tabX, tabY + 4, tabX + currentW - 4, tabY + tabH - 4, btnBg);

        // 7. Presets Equalizer/Sliders Icon
        int cx = tabX + (currentW - 2) / 2;
        int cy = tabY + tabH / 2;
        int trkCol = applyAlpha(hover ? (currentTheme.isLight() ? 0xFF64748B : 0xFF94A3B8) : (currentTheme.isLight() ? 0xFF94A3B8 : COLOR_CHASSIS_BAND), alpha);
        int knobCol = applyAlpha(hover ? COLOR_ACCENT : (currentTheme.isLight() ? 0xFF334155 : COLOR_TEXT_WHITE), alpha);
        int knobHighlight = applyAlpha(hover ? 0xFFFFFFFF : (currentTheme.isLight() ? 0xFFFFFFFF : 0xFFCCCCCC), alpha);

        // Slider 1 (Top: knob at left)
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 5, cy - 6, cx + 5, cy - 5, trkCol);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 3, cy - 8, cx - 1, cy - 3, knobCol);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 2, cy - 7, cx - 1, cy - 4, knobHighlight);

        // Slider 2 (Middle: knob at right)
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 5, cy, cx + 5, cy + 1, trkCol);
        Services.PLATFORM.fill(poseStackOrGraphics, cx + 1, cy - 2, cx + 3, cy + 3, knobCol);
        Services.PLATFORM.fill(poseStackOrGraphics, cx + 2, cy - 1, cx + 3, cy + 2, knobHighlight);

        // Slider 3 (Bottom: knob at center)
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 5, cy + 6, cx + 5, cy + 7, trkCol);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 2, cy + 4, cx, cy + 9, knobCol);
        Services.PLATFORM.fill(poseStackOrGraphics, cx - 1, cy + 5, cx, cy + 8, knobHighlight);

        if (hover) {
            deferredTooltip = Collections.singletonList(ComponentHelper.literal("Presets (Click to Open)"));
            deferredTooltipX = rawMouseX + 12;
            deferredTooltipY = rawMouseY;
        }
    }

    private void renderSidePanelPillarDetail(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        String activeId = selectedPillarDetailId != null ? selectedPillarDetailId : closingPillarDetailId;
        PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(activeId);
        if (data == null) {
            drawBeveledComponent(poseStackOrGraphics, x, y, w, h, COLOR_INNER_DARK, alpha);
            drawCenteredText(poseStackOrGraphics, font, "No pillar selected", x + w / 2, y + h / 2 - 4, applyAlpha(COLOR_TEXT_MUTED, alpha));
            return;
        }

        drawBeveledComponent(poseStackOrGraphics, x, y, w, h, COLOR_INNER_DARK, alpha);

        PillarParticleConfig globalCfg = PillarParticleConfig.get();
        sidePanelScrollOffset = 0;
        sidePanelMaxScroll = 0;

        // Pillar switch transition animation
        long now = System.currentTimeMillis();
        float switchProgress = 1.0f;
        if (isPillarSwitching) {
            long switchElapsed = now - pillarSwitchStartTime;
            int switchDur = Math.max(1, (int) (220 * getAnimSpeedMultiplier()));
            if (switchElapsed < switchDur) {
                switchProgress = easeOutCubic((float) switchElapsed / switchDur);
            } else {
                isPillarSwitching = false;
                prevPillarDetailId = null;
            }
        }

        int slideOffsetX = (int) Math.round((1.0f - switchProgress) * 12);
        float contentAlpha = alpha * Math.min(1.0f, 0.15f + 0.85f * switchProgress);

        if (isPillarSwitching) {
            float pulseAlpha = (1.0f - switchProgress) * 0.45f * alpha;
            Services.PLATFORM.fill(poseStackOrGraphics, x + 2, y + 1, x + w - 2, y + 2, applyAlpha(COLOR_ACCENT, pulseAlpha));
        }

        enableGuiScissor(poseStackOrGraphics, x + 2, y + 2, Math.max(1, w - 4), Math.max(1, h - 4));

        int curY = y + 4;
        int rowH = 22;
        int rowGap = 3;
        int rowX = x + 4 + slideOffsetX;
        int rowW = w - 8;

        // Row 0: Coordinates & Dimension badge
        String dimStr = formatDimensionName(data.dimension);
        String locStr = String.format("%s · %d, %d, %d", dimStr, data.x, data.y, data.z);
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, 15, COLOR_BTN_BG, false, false, contentAlpha);
        drawCenteredText(poseStackOrGraphics, font, font.plainSubstrByWidth(locStr, rowW - 4), rowX + rowW / 2, curY + 4, applyAlpha(COLOR_TEXT_SILVER, contentAlpha));
        curY += 15 + rowGap;

        // Row 1: Custom Pattern Toggle
        boolean usePat = data.use_pattern != null ? data.use_pattern : globalCfg.use_pattern;
        renderSideRowToggle(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Custom Pattern", usePat, mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 2: Particle Pattern Cycle
        String curPat = data.pattern != null && !data.pattern.isEmpty() ? data.pattern : "default";
        String patDisplay = curPat.substring(0, 1).toUpperCase() + curPat.substring(1);
        renderSideRowCycle(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Pattern", patDisplay, mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 3: Speed Counter
        double speed = data.pattern_speed != null ? data.pattern_speed : globalCfg.pattern_speed;
        renderSideRowCounter(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Speed", String.format("%.2f", speed), mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 4: Spread Counter
        double spread = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
        renderSideRowCounter(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Spread", String.format("%.2f", spread), mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 5: Intensity Counter
        double intensity = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
        renderSideRowCounter(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Intensity", String.format("%.1fx", intensity), mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 6: Per-Pillar Colors Palette
        List<String> colors = (data.dyeColors != null && !data.dyeColors.isEmpty()) ? data.dyeColors : new ArrayList<>(globalCfg.particle_color);
        while (colors.size() < 5) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(5, Math.max(1, colors.size())));
        renderSideRowPalette(poseStackOrGraphics, font, rowX, curY, rowW, 34, colors, maxColors, mouseX, mouseY, contentAlpha);
        curY += 34 + rowGap;

        // Row 7: Save Button
        int btnH = 16;
        boolean hoverSave = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledGreenButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverSave, contentAlpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Save Pillar", rowX + rowW / 2, curY + 4, applyAlpha(hoverSave ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT, contentAlpha));
        curY += btnH + rowGap;

        // Row 8: Locate in World
        boolean hoverLoc = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverLoc ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverLoc, false, contentAlpha);
        drawCenteredText(poseStackOrGraphics, font, "⌖ Locate in World", rowX + rowW / 2, curY + 4, applyAlpha(hoverLoc ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, contentAlpha));

        disableGuiScissor(poseStackOrGraphics);
    }

    private void renderSidePanelColorPicker(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        drawBeveledComponent(poseStackOrGraphics, x, y, w, h, COLOR_INNER_DARK, alpha);

        int pad = 6;
        int curY = y + pad;

        // 1. 2D Sat x Bri Box & Hue Slider side by side
        int hueW = 14;
        int gradW = w - pad * 3 - hueW;
        int gradH = 82;
        int gradX = x + pad;
        int gradY = curY;
        int hueX = gradX + gradW + pad;
        int hueY = gradY;
        int hueH = gradH;

        for (int gx = 0; gx < gradW; gx += 3) {
            float sat = gx / (float) gradW;
            int topRgb = hsbToRgb(pickerHue, sat, 1.0f);
            int gw = Math.min(3, gradW - gx);
            for (int gy = 0; gy < gradH; gy += 3) {
                float bri = 1.0f - (gy / (float) gradH);
                int pixelCol = scaleBrightness(topRgb, bri);
                int gh = Math.min(3, gradH - gy);
                Services.PLATFORM.fill(poseStackOrGraphics, gradX + gx, gradY + gy, gradX + gx + gw, gradY + gy + gh, applyAlpha(pixelCol, alpha));
            }
        }
        drawBorder(poseStackOrGraphics, gradX - 1, gradY - 1, gradW + 2, gradH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        // Crosshair
        int crossX = gradX + (int) (pickerSat * gradW);
        int crossY = gradY + (int) ((1.0f - pickerBri) * gradH);
        drawBorder(poseStackOrGraphics, crossX - 3, crossY - 3, 6, 6, applyAlpha(COLOR_FRAME_BLACK, alpha));
        drawBorder(poseStackOrGraphics, crossX - 2, crossY - 2, 4, 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Hue Slider
        for (int hy = 0; hy < hueH; hy += 3) {
            float hVal = (hy / (float) hueH) * 360.0f;
            int col = hsbToRgb(hVal, 1.0f, 1.0f);
            int hh = Math.min(3, hueH - hy);
            Services.PLATFORM.fill(poseStackOrGraphics, hueX, hueY + hy, hueX + hueW, hueY + hy + hh, applyAlpha(col, alpha));
        }
        drawBorder(poseStackOrGraphics, hueX - 1, hueY - 1, hueW + 2, hueH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        int thumbY = hueY + (int) ((pickerHue / 360.0f) * hueH);
        Services.PLATFORM.fill(poseStackOrGraphics, hueX - 2, thumbY - 1, hueX + hueW + 2, thumbY + 2, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawBorder(poseStackOrGraphics, hueX - 2, thumbY - 1, hueW + 4, 3, applyAlpha(COLOR_FRAME_BLACK, alpha));

        curY += gradH + 8;

        // 2. Preview & Hex
        int currentColorInt = hsbToRgb(pickerHue, pickerSat, pickerBri);
        int r = (currentColorInt >> 16) & 0xFF;
        int g = (currentColorInt >> 8) & 0xFF;
        int b = currentColorInt & 0xFF;
        String hexString = String.format("#%02X%02X%02X", r, g, b);

        int prevSize = 20;
        Services.PLATFORM.fill(poseStackOrGraphics, gradX, curY, gradX + prevSize, curY + prevSize, applyAlpha(currentColorInt, alpha));
        drawBorder(poseStackOrGraphics, gradX, curY, prevSize, prevSize, applyAlpha(COLOR_FRAME_BLACK, alpha));
        drawText(poseStackOrGraphics, font, hexString, gradX + prevSize + 6, curY + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));

        curY += prevSize + 8;

        // 3. Preset Swatches
        drawText(poseStackOrGraphics, font, "Presets:", gradX, curY, applyAlpha(COLOR_TEXT_MUTED, alpha));
        curY += 11;
        int pSize = 13;
        int pGap = 3;
        int pCols = Math.max(1, (w - pad * 2) / (pSize + pGap));
        for (int p = 0; p < PALETTE_PRESETS.length; p++) {
            int px = gradX + (p % pCols) * (pSize + pGap);
            int py = curY + (p / pCols) * (pSize + pGap);
            Services.PLATFORM.fill(poseStackOrGraphics, px, py, px + pSize, py + pSize, applyAlpha(PALETTE_PRESETS[p], alpha));
            drawBorder(poseStackOrGraphics, px, py, pSize, pSize, applyAlpha(0xFF000000, alpha));
        }
        int pRows = (int) Math.ceil((double) PALETTE_PRESETS.length / pCols);
        curY += pRows * (pSize + pGap) + 10;

        // 4. Buttons
        int btnW = w - pad * 2;
        int btnH = 16;
        boolean hoverApply = mouseX >= gradX && mouseX <= gradX + btnW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledGreenButton(poseStackOrGraphics, gradX, curY, btnW, btnH, hoverApply, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Apply Color", gradX + btnW / 2, curY + 4, applyAlpha(hoverApply ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT, alpha));
        curY += btnH + 4;

        if (returnSidePanelType == SidePanelType.PILLAR_DETAIL) {
            boolean hoverBack = mouseX >= gradX && mouseX <= gradX + btnW && mouseY >= curY && mouseY <= curY + btnH;
            drawBeveledButton(poseStackOrGraphics, gradX, curY, btnW, btnH, hoverBack ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverBack, false, alpha);
            drawCenteredText(poseStackOrGraphics, font, "◀ Back to Pillar", gradX + btnW / 2, curY + 4, applyAlpha(hoverBack ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        }
    }

    private void renderSideRowToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String label, boolean val, int mouseX, int mouseY, float alpha) {
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, label, x + 4, y + 7, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int btnW = 36;
        int btnH = 15;
        int btnX = x + w - btnW - 4;
        int btnY = y + (h - btnH) / 2;
        boolean hover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

        if (val) {
            drawBeveledGreenButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hover, alpha);
        } else {
            drawBeveledRedButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hover, alpha);
        }
        int textCol = val ? (hover ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT) : (hover ? (currentTheme.isLight() ? 0xFF1E293B : 0xFFFFFFFF) : COLOR_TOGGLE_OFF_TEXT);
        drawCenteredText(poseStackOrGraphics, font, val ? "ON" : "OFF", btnX + btnW / 2, btnY + 4, applyAlpha(textCol, alpha));
    }

    private void renderSideRowCycle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String label, String value, int mouseX, int mouseY, float alpha) {
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, label, x + 4, y + 7, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int btnW = 66;
        int btnH = 15;
        int btnX = x + w - btnW - 4;
        int btnY = y + (h - btnH) / 2;
        boolean hover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        drawBeveledButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hover ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hover, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, font.plainSubstrByWidth(value, btnW - 4), btnX + btnW / 2, btnY + 4, applyAlpha(hover ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderSideRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String label, String value, int mouseX, int mouseY, float alpha) {
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, label, x + 4, y + 7, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int counterW = 74;
        int counterH = 15;
        int counterX = x + w - counterW - 4;
        int counterY = y + (h - counterH) / 2;
        int stepW = 15;

        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + stepW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, counterX, counterY, stepW, counterH, hoverMinus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverMinus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "−", counterX + stepW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int valX = counterX + stepW;
        int valW = counterW - stepW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, value, valX + valW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, plusX, counterY, stepW, counterH, hoverPlus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPlus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+", plusX + stepW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private void renderSideRowPalette(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, List<String> colors, int maxColors, int mouseX, int mouseY, float alpha) {
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, "Colors (" + maxColors + "/5)", x + 4, y + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int counterW = 60;
        int counterH = 14;
        int counterX = x + w - counterW - 4;
        int counterY = y + 2;
        int stepW = 13;

        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + stepW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, counterX, counterY, stepW, counterH, hoverMinus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverMinus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "−", counterX + stepW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int valX = counterX + stepW;
        int valW = counterW - stepW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, String.valueOf(maxColors), valX + valW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, plusX, counterY, stepW, counterH, hoverPlus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPlus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+", plusX + stepW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int swSize = 13;
        int swGap = 4;
        int swY = y + 17;
        for (int i = 0; i < 5; i++) {
            int swX = x + 6 + i * (swSize + swGap);
            int colInt = 0xFFFFFFFF;
            if (colors != null && i < colors.size()) {
                try {
                    String hex = colors.get(i).replace("#", "");
                    colInt = 0xFF000000 | Integer.parseInt(hex, 16);
                } catch (Exception ignored) {}
            }

            boolean active = i < maxColors;
            Services.PLATFORM.fill(poseStackOrGraphics, swX, swY, swX + swSize, swY + swSize, applyAlpha(colInt, active ? alpha : alpha * 0.30f));
            int borderCol = active ? COLOR_TEXT_WHITE : COLOR_SLOT_BORDER;
            drawBorder(poseStackOrGraphics, swX, swY, swSize, swSize, applyAlpha(borderCol, alpha));
        }
    }
    private void drawHorizontalGradientLine(Object poseStackOrGraphics, int x, int y, int w, int startCol, int endCol, float alpha) {
        if (w <= 0) return;
        for (int i = 0; i < w; i++) {
            float t = (float) i / Math.max(1, w - 1);
            int col = lerpColor(startCol, endCol, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x + i, y, x + i + 1, y + 1, applyAlpha(col, alpha));
        }
    }

    private void drawVerticalGradientLine(Object poseStackOrGraphics, int x, int y, int h, int startCol, int endCol, float alpha) {
        if (h <= 0) return;
        for (int j = 0; j < h; j++) {
            float t = (float) j / Math.max(1, h - 1);
            int col = lerpColor(startCol, endCol, t);
            Services.PLATFORM.fill(poseStackOrGraphics, x, y + j, x + 1, y + j + 1, applyAlpha(col, alpha));
        }
    }

    private void drawBeveledComponent(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        // 1. Interior Fill
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bgRgb, alpha));

        // 2. Bottom & Right dark recessed shadow
        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

        // 3. Top & Left Metallic Gradient Highlights
        drawHorizontalGradientLine(poseStackOrGraphics, x, y, w - 1, COLOR_BEVEL_HIGHLIGHT, COLOR_BORDER_SHADOW, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, x, y, h - 1, COLOR_BEVEL_HIGHLIGHT, COLOR_BORDER_SHADOW, alpha);
    }

    // Top & Left Metallic Gradient Button Frame
    private void drawBeveledButton(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, boolean hover, boolean active, float alpha) {
        int bg = bgRgb;
        if (active) {
            bg = hover ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG_ACTIVE;
        } else if (hover) {
            bg = COLOR_BTN_BG_HOVER;
        }
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bg, alpha));

        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

        int startCol = active ? COLOR_BEVEL_HIGHLIGHT : (hover ? COLOR_BORDER_GRADIENT_START : COLOR_BTN_BORDER);
        int endCol = active ? COLOR_BORDER_SHADOW : (hover ? COLOR_BORDER_GRADIENT_END : COLOR_BEVEL_SHADOW);

        drawHorizontalGradientLine(poseStackOrGraphics, x, y, w - 1, startCol, endCol, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, x, y, h - 1, startCol, endCol, alpha);
    }

    // Top & Left Metallic Green Button (for Apply / ON states)
    private void drawBeveledGreenButton(Object poseStackOrGraphics, int x, int y, int w, int h, boolean hover, float alpha) {
        boolean light = currentTheme != null && currentTheme.isLight();
        int bg = hover ? (light ? 0xFFBBF7D0 : 0xFF1E4828) : COLOR_TOGGLE_ON_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bg, alpha));

        int dark = applyAlpha(light ? 0xFF86EFAC : 0xFF0A2412, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

        int startCol = hover ? (light ? 0xFF4ADE80 : 0xFFCEFFE0) : (light ? 0xFF86EFAC : 0xFF86EFAC);
        int endCol = hover ? (light ? 0xFF22C55E : 0xFF4ADE80) : (light ? 0xFF22C55E : 0xFF22C55E);

        drawHorizontalGradientLine(poseStackOrGraphics, x, y, w - 1, startCol, endCol, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, x, y, h - 1, startCol, endCol, alpha);
    }

    // Top & Left Metallic Crimson Red Button (for OFF states / Cancel)
    private void drawBeveledRedButton(Object poseStackOrGraphics, int x, int y, int w, int h, boolean hover, float alpha) {
        boolean light = currentTheme != null && currentTheme.isLight();
        int bg = hover ? (light ? 0xFFE2E8F0 : 0xFF4A1A1A) : COLOR_TOGGLE_OFF_BG;
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bg, alpha));

        int dark = applyAlpha(light ? COLOR_BEVEL_SHADOW : 0xFF200808, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

        int startCol = hover ? (light ? 0xFFCBD5E1 : 0xFFFFD2D2) : (light ? COLOR_BTN_BORDER : 0xFFFCA5A5);
        int endCol = hover ? (light ? 0xFF94A3B8 : 0xFFF87171) : (light ? COLOR_BEVEL_SHADOW : 0xFFEF4444);

        drawHorizontalGradientLine(poseStackOrGraphics, x, y, w - 1, startCol, endCol, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, x, y, h - 1, startCol, endCol, alpha);
    }

    // Viewport Scissor Helpers
    private void enableGuiScissor(Object poseStackOrGraphics, int guiX, int guiY, int guiW, int guiH) {
        Minecraft mc = Minecraft.getInstance();
        double scale = mc.getWindow().getGuiScale();
        int winH = mc.getWindow().getHeight();
        int sx = (int) Math.round(guiX * scale);
        int sy = (int) Math.round(winH - (guiY + guiH) * scale);
        int sw = (int) Math.round(guiW * scale);
        int sh = (int) Math.round(guiH * scale);
        if (sw > 0 && sh > 0) {
            Services.PLATFORM.enableScissor(poseStackOrGraphics, sx, sy, sw, sh);
        }
    }

    private void disableGuiScissor(Object poseStackOrGraphics) {
        Services.PLATFORM.disableScissor(poseStackOrGraphics);
    }

    private void drawInsetBox(Object poseStackOrGraphics, int x, int y, int w, int h, int fillRgb, float alpha) {
        drawBeveledComponent(poseStackOrGraphics, x, y, w, h, fillRgb, alpha);
    }

    private void drawGroove(Object poseStackOrGraphics, int x, int y, int w, boolean horizontal, float alpha) {
        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int light = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
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
        if (currentTheme != null && currentTheme.isLight()) {
            Services.PLATFORM.draw(poseStackOrGraphics, font, text, x, y, color);
        } else {
            Services.PLATFORM.drawShadow(poseStackOrGraphics, font, text, x, y, color);
        }
    }

    private void drawCenteredText(Object poseStackOrGraphics, Font font, String text, int centerX, int y, int color) {
        if (text == null) return;
        int textW = font.width(text);
        int startX = centerX - textW / 2;
        if (currentTheme != null && currentTheme.isLight()) {
            Services.PLATFORM.draw(poseStackOrGraphics, font, text, startX, y, color);
        } else {
            Services.PLATFORM.drawShadow(poseStackOrGraphics, font, text, startX, y, color);
        }
    }

    // --- Top Center Crest Badge (Buildscape Logo Beveled Frame with Seamless U-Shaped Cradle) ---
    private static final CommonId LOGO_TEXTURE = CommonId.of("buildscape", "textures/gui/logo.png");

    private void drawTopCrestBadge(Object poseStackOrGraphics, Font font, int frameX, int frameY, int frameW, float alpha) {
        int crestW = 152;
        int crestH = 46;
        int crestX = frameX + (frameW - crestW) / 2;
        int crestY = frameY - 24;

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int bShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int bHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);

        // 1. Solid Outer Chassis Body Fill
        Services.PLATFORM.fill(poseStackOrGraphics, crestX, crestY, crestX + crestW, crestY + crestH, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        // 2. 1px Black Border Outline (Single continuous 1px black line throughout)
        drawBorder(poseStackOrGraphics, crestX, crestY, crestW, crestH, black);

        // 3. Layer 1: Outer metallic highlight (top & left) & shadow (bottom & right)
        drawHorizontalGradientLine(poseStackOrGraphics, crestX + 1, crestY + 1, crestW - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, crestX + 1, crestY + 1, crestH - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, crestX + 1, crestY + crestH - 2, crestX + crestW - 1, crestY + crestH - 1, shadow);
        Services.PLATFORM.fill(poseStackOrGraphics, crestX + crestW - 2, crestY + 1, crestX + crestW - 1, crestY + crestH - 1, shadow);

        // 4. Layer 3: Inner Recessed Plate with unified 4px border width on all sides
        int plateX = crestX + 4;
        int plateY = crestY + 4;
        int plateW = crestW - 8;
        int plateH = crestH - 8;
        Services.PLATFORM.fill(poseStackOrGraphics, plateX, plateY, plateX + plateW, plateY + plateH, applyAlpha(COLOR_INNER_DARK, alpha));

        // Inner plate 1px subtle bevel (shadow top & left, highlight bottom & right)
        Services.PLATFORM.fill(poseStackOrGraphics, plateX, plateY, plateX + plateW, plateY + 1, bShadow);
        Services.PLATFORM.fill(poseStackOrGraphics, plateX, plateY, plateX + 1, plateY + plateH, bShadow);
        Services.PLATFORM.fill(poseStackOrGraphics, plateX, plateY + plateH - 1, plateX + plateW, plateY + plateH, bHighlight);
        Services.PLATFORM.fill(poseStackOrGraphics, plateX + plateW - 1, plateY, plateX + plateW, plateY + plateH, bHighlight);

        // 5. Proportional Buildscape Logo Texture (Crisp 256x143 native scaling)
        int maxLogoW = plateW - 6;
        int maxLogoH = plateH - 4;
        int logoH = Math.min(36, maxLogoH);
        int logoW = (int) Math.round(logoH * (256.0 / 143.0));
        if (logoW > maxLogoW) {
            logoW = maxLogoW;
            logoH = (int) Math.round(logoW * (143.0 / 256.0));
        }
        int logoX = plateX + (plateW - logoW) / 2;
        int logoY = plateY + (plateH - logoH) / 2;

        try {
            Services.PLATFORM.blit(poseStackOrGraphics, LOGO_TEXTURE, logoX, logoY, logoW, logoH, 0.0f, 0.0f, 256, 143, 256, 143);
        } catch (Exception e) {
            int textCol = applyAlpha(COLOR_ACCENT, alpha);
            drawCenteredText(poseStackOrGraphics, font, "✦ BUILDSCAPE ✦", crestX + crestW / 2, plateY + (plateH - 8) / 2, textCol);
        }
    }

    // --- Unified Custom Scrollbar (6px width, clean metallic bevels, no gradient banding) ---
    private void renderCustomScrollbar(Object poseStackOrGraphics, int trackX, int trackY, int trackW, int trackH, double scroll, double maxSc, int mouseX, int mouseY, float alpha) {
        if (maxSc <= 0) return;

        currentScrollbarTrackX = trackX;
        currentScrollbarTrackY = trackY;
        currentScrollbarTrackW = trackW;
        currentScrollbarTrackH = trackH;

        int thumbH = Math.max(18, (int) ((trackH / (float) (trackH + maxSc)) * trackH));
        currentScrollbarThumbH = thumbH;
        int thumbY = trackY + (int) ((scroll / maxSc) * (trackH - thumbH));

        // Track: clean recessed slot
        Services.PLATFORM.fill(poseStackOrGraphics, trackX, trackY, trackX + trackW, trackY + trackH, applyAlpha(COLOR_SLOT_BG, alpha * 0.75f));
        drawBorder(poseStackOrGraphics, trackX, trackY, trackW, trackH, applyAlpha(COLOR_FRAME_BLACK, alpha * 0.40f));

        // Thumb: clean metallic button with bevels (no weird gradient banding)
        boolean hoverThumb = mouseX >= trackX && mouseX <= trackX + trackW && mouseY >= thumbY && mouseY <= thumbY + thumbH;
        int thumbColor = isDraggingScrollbar ? COLOR_ACCENT : (hoverThumb ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG_ACTIVE);

        // Thumb fill
        Services.PLATFORM.fill(poseStackOrGraphics, trackX + 1, thumbY + 1, trackX + trackW - 1, thumbY + thumbH - 1, applyAlpha(thumbColor, alpha));

        // Thumb 1px border & bevel
        int highlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
        int shadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, trackX, thumbY, trackX + trackW, thumbY + 1, highlight);
        Services.PLATFORM.fill(poseStackOrGraphics, trackX, thumbY + 1, trackX + 1, thumbY + thumbH - 1, highlight);
        Services.PLATFORM.fill(poseStackOrGraphics, trackX, thumbY + thumbH - 1, trackX + trackW, thumbY + thumbH, shadow);
        Services.PLATFORM.fill(poseStackOrGraphics, trackX + trackW - 1, thumbY + 1, trackX + trackW, thumbY + thumbH - 1, shadow);
    }

    // --- Auto-Minimize & In-World Locate Helper ---
    private void locatePillarInWorld(PillarIdManager.PillarData data) {
        if (data == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            String curDim = Services.PLATFORM.getDimensionId(mc.level).toString();
            if (data.dimension != null && data.dimension.equals(curDim)) {
                BlockPos pillarPos = new BlockPos(data.x, data.y, data.z);
                PillarMarkerManager.get().markPillar(data.id, pillarPos, data.dimension);

                double targetX = pillarPos.getX() + 0.5;
                double targetY = pillarPos.getY() + 0.5;
                double targetZ = pillarPos.getZ() + 0.5;
                double eyeY = mc.player.getY() + mc.player.getEyeHeight();

                double dx = targetX - mc.player.getX();
                double dy = targetY - eyeY;
                double dz = targetZ - mc.player.getZ();
                double distXZ = Math.sqrt(dx * dx + dz * dz);

                float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
                float pitch = (float) (-(Math.atan2(dy, distXZ) * 180.0 / Math.PI));

                mc.player.setYRot(yaw);
                mc.player.setXRot(pitch);

                if (sidePanelProgress > 0.001f && sidePanelType != SidePanelType.NONE) {
                    closeSidePanel();
                }
                startClosing(true);
            }
        }
    }

    // --- Config Row Render Helpers ---
    private void renderRowCardBase(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;

        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h && mouseY >= clipY && mouseY <= clipY + clipH;
        int bg = hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG;
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, bg, hover, false, alpha);

        drawText(poseStackOrGraphics, font, label, x + 6, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
        if (desc != null && !desc.isEmpty()) {
            drawText(poseStackOrGraphics, font, desc, x + 6, y + 15, applyAlpha(COLOR_TEXT_MUTED, alpha));
        }
    }

    private void renderConfigRowToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, boolean value, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 38;
        int btnH = 15;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        if (value) {
            drawBeveledGreenButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hoverBtn, alpha);
        } else {
            drawBeveledRedButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hoverBtn, alpha);
        }
        int textCol = value ? (hoverBtn ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT) : (hoverBtn ? (currentTheme.isLight() ? 0xFF1E293B : 0xFFFFFFFF) : COLOR_TOGGLE_OFF_TEXT);
        drawCenteredText(poseStackOrGraphics, font, value ? "ON" : "OFF", btnX + btnW / 2, btnY + 4, applyAlpha(textCol, alpha));
    }

    private void renderConfigRowCycle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String currentVal, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 82;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        drawBeveledButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hoverBtn ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverBtn, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "‹ " + currentVal + " ›", btnX + btnW / 2, btnY + 3, applyAlpha(hoverBtn ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String valText, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int counterW = 86;
        int counterH = 16;
        int counterX = x + w - counterW - 8;
        int counterY = y + (h - counterH) / 2;
        int stepBtnW = 16;

        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, counterX, counterY, stepBtnW, counterH, hoverMinus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverMinus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "−", counterX + stepBtnW / 2, counterY + 4, applyAlpha(hoverMinus ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        int valX = counterX + stepBtnW;
        int valW = counterW - stepBtnW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, valText, valX + valW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepBtnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, plusX, counterY, stepBtnW, counterH, hoverPlus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPlus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+", plusX + stepBtnW / 2, counterY + 4, applyAlpha(hoverPlus ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowButton(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String btnText, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, mouseX, mouseY, alpha);

        int btnW = 86;
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        drawBeveledButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hoverBtn ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverBtn, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, btnText, btnX + btnW / 2, btnY + 3, applyAlpha(hoverBtn ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowBadge(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String badgeText, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, -1, -1, alpha);

        int textW = font.width(badgeText);
        int badgeW = textW + 10;
        int badgeH = 14;
        int badgeX = x + w - badgeW - 8;
        int badgeY = y + (h - badgeH) / 2;

        Services.PLATFORM.fill(poseStackOrGraphics, badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, badgeX, badgeY, badgeW, badgeH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, badgeText, badgeX + badgeW / 2, badgeY + 3, applyAlpha(COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowPalette(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, List<String> colors, int maxColors, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, "Max active colors: " + maxColors + " / 7", mouseX, mouseY, alpha);

        int counterW = 86;
        int counterH = 16;
        int counterX = x + w - counterW - 8;
        int counterY = y + (h - counterH) / 2;
        int stepBtnW = 16;

        boolean hoverMinus = mouseX >= counterX && mouseX <= counterX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, counterX, counterY, stepBtnW, counterH, hoverMinus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverMinus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "−", counterX + stepBtnW / 2, counterY + 4, applyAlpha(hoverMinus ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        int valX = counterX + stepBtnW;
        int valW = counterW - stepBtnW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, String.valueOf(maxColors), valX + valW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepBtnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, plusX, counterY, stepBtnW, counterH, hoverPlus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPlus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+", plusX + stepBtnW / 2, counterY + 4, applyAlpha(hoverPlus ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // Clickable Swatches: all 7 slots
        int swatchSize = 13;
        int swatchGap = 3;
        int swatchesTotalW = 7 * (swatchSize + swatchGap) - swatchGap;
        int swatchStartX = counterX - swatchesTotalW - 10;
        int swatchY = y + (h - swatchSize) / 2;

        for (int i = 0; i < 7; i++) {
            int swX = swatchStartX + i * (swatchSize + swatchGap);
            int colorInt = 0xFFFFFFFF;
            if (colors != null && i < colors.size()) {
                try {
                    String hex = colors.get(i).replace("#", "");
                    colorInt = 0xFF000000 | Integer.parseInt(hex, 16);
                } catch (Exception ignored) {}
            }

            boolean active = i < maxColors;
            Services.PLATFORM.fill(poseStackOrGraphics, swX, swatchY, swX + swatchSize, swatchY + swatchSize, applyAlpha(colorInt, active ? alpha : alpha * 0.30f));
            int borderCol = active ? COLOR_TEXT_WHITE : COLOR_SLOT_BORDER;
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

        float uiScale = getGuiUiScale();
        if (Math.abs(uiScale - 1.0f) > 0.001f) {
            mouseX /= uiScale;
            mouseY /= uiScale;
        }

        if (showColorModal) {
            return handleColorModalClick(mouseX, mouseY);
        }

        SidePanelLayout layout = calculateLayout();
        int panelX = layout.panelX;
        int panelY = layout.panelY;
        int panelW = layout.panelW;
        int panelH = layout.panelH;
        int frameX = layout.frameX;
        int frameY = layout.frameY;
        int frameW = layout.frameW;
        int frameH = layout.frameH;

        // 0. Right Side Panel Clicks (when visible)
        if (sidePanelProgress > 0.05f && sidePanelType != SidePanelType.NONE && !isSidePanelClosing) {
            int activeLeft = Math.max(layout.sidePanelX, layout.frameRight - 2);
            if (mouseX >= activeLeft && mouseX <= layout.sidePanelX + layout.sidePanelW
                    && mouseY >= layout.sidePanelY && mouseY <= layout.sidePanelY + layout.sidePanelH) {
                if (handleSidePanelClick(mouseX, mouseY, button, layout.sidePanelX, layout.sidePanelY, layout.sidePanelW, layout.sidePanelH)) {
                    return true;
                }
            }
        }

        // 0b. Presets Side Tab Click (when Presets panel is closed, clicking the protruding side tab expands it)
        if (selectedTabIndex == 0 && sidePanelProgress < 0.05f) {
            int tabX = layout.frameRight - 2;
            int tabY = frameY + 36;
            int tabW = 20;
            int tabH = 28;
            if (mouseX >= tabX && mouseX <= tabX + tabW + 6 && mouseY >= tabY && mouseY <= tabY + tabH) {
                openSidePanel(SidePanelType.PRESETS);
                return true;
            }
        }

        int headerH = 24;
        int headerX = panelX + 7;
        int headerY = panelY + 7;
        int headerW = panelW - 14;

        // 1. Close Button [ ✕ ]
        int closeBtnSize = 14;
        int closeBtnX = headerX + headerW - closeBtnSize - 5;
        int closeBtnY = headerY + 5;
        if (mouseX >= closeBtnX && mouseX <= closeBtnX + closeBtnSize && mouseY >= closeBtnY && mouseY <= closeBtnY + closeBtnSize) {
            startClosing();
            return true;
        }

        // 2. Sidenav Tabs & Toggle
        int navW = (int) Math.round(30 + 76 * navAnimProgress);
        int navX = panelX + 7;
        int navY = headerY + headerH + 5;
        int navH = panelH - headerH - 19;

        int toggleH = 16;
        int toggleW = navW - 6;
        int toggleX = navX + 3;
        int toggleY = navY + navH - toggleH - 3;
        if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
            toggleNavExpansion();
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
                    selectTab(i);
                }
                return true;
            }
        }

        // 3. Right Content Viewport Controls
        int contentX = navX + navW + 5;
        int contentY = navY;
        int contentW = (panelX + panelW - 7) - contentX;
        int contentH = navH;

        int catHeaderH = 22;
        int viewY = contentY + catHeaderH + 4;
        int viewH = contentH - catHeaderH - 8;

        // 3. Scrollbar Interaction (Drag & Page Jump)
        if (maxScroll > 0 && currentScrollbarTrackW > 0) {
            int pad = 3;
            if (mouseX >= currentScrollbarTrackX - pad && mouseX <= currentScrollbarTrackX + currentScrollbarTrackW + pad &&
                    mouseY >= currentScrollbarTrackY && mouseY <= currentScrollbarTrackY + currentScrollbarTrackH) {
                int thumbY = currentScrollbarTrackY + (int) ((scrollOffset / maxScroll) * (currentScrollbarTrackH - currentScrollbarThumbH));
                if (mouseY >= thumbY && mouseY <= thumbY + currentScrollbarThumbH) {
                    isDraggingScrollbar = true;
                    scrollDragStartY = mouseY;
                    scrollDragStartOffset = scrollOffset;
                } else if (mouseY < thumbY) {
                    targetScrollOffset = Math.max(0, targetScrollOffset - currentScrollbarTrackH * 0.75);
                } else {
                    targetScrollOffset = Math.min(maxScroll, targetScrollOffset + currentScrollbarTrackH * 0.75);
                }
                return true;
            }
        }

        if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= contentY && mouseY <= contentY + contentH) {
            if (handleContentClick(mouseX, mouseY, button, contentX, contentY, contentW, contentH, catHeaderH)) {
                return true;
            }
        }

        if (activeTab != null && activeTab.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return false;
    }

    private boolean handleContentClick(double mouseX, double mouseY, int button, int contentX, int contentY, int contentW, int contentH, int catHeaderH) {
        NavItem current = navItems.get(selectedTabIndex);

        switch (current.id) {
            case "items" -> {
                PillarParticleConfig cfg = PillarParticleConfig.get();
                int curY = contentY + catHeaderH + 4;

                // Row 1: Sub-View Switcher (Display Items vs Display Tags)
                int row1H = 18;
                int subBtnW = (contentW - 16) / 2;
                int subBtnH = 15;
                int subY = curY + 1;

                int sub0X = contentX + 6;
                if (mouseX >= sub0X && mouseX <= sub0X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH) {
                    itemsSubTab = 0;
                    scrollOffset = 0;
                    return true;
                }

                int sub1X = sub0X + subBtnW + 4;
                if (mouseX >= sub1X && mouseX <= sub1X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH) {
                    itemsSubTab = 1;
                    scrollOffset = 0;
                    return true;
                }

                curY += row1H + 2;

                // Row 2: Filter Bar (4 buttons: All, Inventory, Mod Toggles, Active)
                int bar2H = 16;
                int pillW = (contentW - 24) / 4;
                int pillH = 13;
                int pillY = curY + 1;

                if (itemsSubTab == 0) {
                    for (int f = 0; f < 4; f++) {
                        int px = contentX + 6 + f * (pillW + 4);
                        if (mouseX >= px && mouseX <= px + pillW && mouseY >= pillY && mouseY <= pillY + pillH) {
                            if (f == 2) {
                                if (itemFilterMode == 2) {
                                    if (!availableModNamespaces.isEmpty()) {
                                        if (button == 1 || Services.PLATFORM.hasShiftDown()) {
                                            currentModIndex = (currentModIndex - 1 + availableModNamespaces.size()) % availableModNamespaces.size();
                                        } else {
                                            currentModIndex = (currentModIndex + 1) % availableModNamespaces.size();
                                        }
                                    }
                                } else {
                                    itemFilterMode = 2;
                                }
                            } else {
                                itemFilterMode = f;
                            }
                            itemsListDirty = true;
                            scrollOffset = 0;
                            return true;
                        }
                    }
                } else {
                    for (int f = 0; f < 4; f++) {
                        int px = contentX + 6 + f * (pillW + 4);
                        if (mouseX >= px && mouseX <= px + pillW && mouseY >= pillY && mouseY <= pillY + pillH) {
                            if (f == 2) {
                                if (tagFilterMode == 2) {
                                    if (!availableModNamespaces.isEmpty()) {
                                        if (button == 1 || Services.PLATFORM.hasShiftDown()) {
                                            currentTagModIndex = (currentTagModIndex - 1 + availableModNamespaces.size()) % availableModNamespaces.size();
                                        } else {
                                            currentTagModIndex = (currentTagModIndex + 1) % availableModNamespaces.size();
                                        }
                                    }
                                } else {
                                    tagFilterMode = 2;
                                }
                            } else {
                                tagFilterMode = f;
                            }
                            scrollOffset = 0;
                            return true;
                        }
                    }
                }

                curY += bar2H + 4;

                // Viewport Clicks
                int viewportY = curY;
                int viewportH = (contentY + contentH - 6) - viewportY;

                if (mouseY >= viewportY && mouseY <= viewportY + viewportH) {
                    int scrollbarW = 4;
                    int scrollbarTrackX = contentX + contentW - scrollbarW - 5;
                    int availGridW = (scrollbarTrackX - 6) - (contentX + 6);

                    if (itemsSubTab == 0) {
                        List<Item> itemsToDisplay = getFilteredItemsList();
                        int slotSize = 20;
                        int slotGap = 2;
                        int step = slotSize + slotGap;
                        int cols = Math.max(1, availGridW / step);
                        int totalGridW = cols * step - slotGap;
                        int gridStartX = contentX + 6 + (availGridW - totalGridW) / 2;

                        int col = (int) (mouseX - gridStartX) / step;
                        int row = (int) ((mouseY - (viewportY + 2 - (int) scrollOffset)) / step);

                        if (col >= 0 && col < cols && row >= 0) {
                            int index = row * cols + col;
                            if (index < itemsToDisplay.size()) {
                                int slotX = gridStartX + col * step;
                                int slotY = viewportY + 2 - (int) scrollOffset + row * step;
                                if (mouseX >= slotX && mouseX <= slotX + slotSize && mouseY >= slotY && mouseY <= slotY + slotSize) {
                                    Item item = itemsToDisplay.get(index);
                                    CommonId cid = Services.PLATFORM.getItemId(item);
                                    if (cid != null) {
                                        String idStr = cid.toString();
                                        if (cfg.items.contains(idStr)) {
                                            cfg.items.remove(idStr);
                                            cachedActiveItems.remove(item);
                                        } else {
                                            cfg.items.add(idStr);
                                            cachedActiveItems.add(item);
                                        }
                                        cfg.saveItemsToDisk();
                                        if (itemFilterMode == 3) {
                                            itemsListDirty = true;
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                    } else {
                        List<String> tagsToDisplay = getFilteredTagsList();
                        int tagRowH = 22;
                        int tagRowGap = 2;
                        int startY = viewportY + 2 - (int) scrollOffset;
                        int rowW = availGridW;
                        int rowX = contentX + 6;

                        for (int ti = 0; ti < tagsToDisplay.size(); ti++) {
                            int rowY = startY + ti * (tagRowH + tagRowGap);
                            if (rowY + tagRowH < viewportY || rowY > viewportY + viewportH) continue;

                            if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= rowY && mouseY <= rowY + tagRowH) {
                                String tagId = tagsToDisplay.get(ti);
                                if (cfg.items.contains(tagId)) {
                                    cfg.items.remove(tagId);
                                    cachedActiveTags.remove(tagId);
                                } else {
                                    cfg.items.add(tagId);
                                    cachedActiveTags.add(tagId);
                                }
                                cfg.saveItemsToDisk();
                                return true;
                            }
                        }
                    }
                }
            }
            case "pillars" -> {
                return handlePillarsClick(mouseX, mouseY, button, contentX, contentY, contentW, contentH, catHeaderH);
            }
            default -> {
                int viewY = contentY + catHeaderH + 4;
                int viewH = contentH - catHeaderH - 8;
                int scrollbarW = 4;
                int scrollbarTrackX = contentX + contentW - scrollbarW - 5;
                int rowW = (scrollbarTrackX - 6) - (contentX + 6);
                int rowX = contentX + 6;
                int startY = viewY + 2 - (int) scrollOffset;
                int h = 26;
                int gap = 3;
                int curY = startY;
                int clipY = viewY;
                int clipH = viewH;
                int x = rowX;
                int w = rowW;

                switch (current.id) {
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
                    for (int i = 0; i < GLOBAL_PARTICLE_PATTERNS.length; i++) {
                        if (GLOBAL_PARTICLE_PATTERNS[i].equalsIgnoreCase(cfg.pattern)) {
                            pIndex = i;
                            break;
                        }
                    }
                    cfg.pattern = GLOBAL_PARTICLE_PATTERNS[(pIndex + 1) % GLOBAL_PARTICLE_PATTERNS.length];
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
                    while (cfg.particle_color.size() < 7) {
                        cfg.particle_color.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(cfg.particle_color.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
                    }

                    int counterW = 86;
                    int counterH = 16;
                    int counterX = x + w - counterW - 8;
                    int counterY = curY + (h - counterH) / 2;
                    int stepBtnW = 16;

                    if (mouseY >= counterY && mouseY <= counterY + counterH) {
                        if (mouseX >= counterX && mouseX <= counterX + stepBtnW) {
                            cfg.max_particle_color = Math.max(1, cfg.max_particle_color - 1);
                            cfg.saveProperties();
                            return true;
                        } else if (mouseX >= counterX + counterW - stepBtnW && mouseX <= counterX + counterW) {
                            cfg.max_particle_color = Math.min(7, cfg.max_particle_color + 1);
                            cfg.saveProperties();
                            return true;
                        }
                    }

                    int swatchSize = 13;
                    int swatchGap = 3;
                    int swatchesTotalW = 7 * (swatchSize + swatchGap) - swatchGap;
                    int swatchStartX = counterX - swatchesTotalW - 10;
                    int swatchY = curY + (h - swatchSize) / 2;

                    for (int i = 0; i < 7; i++) {
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
                            returnSidePanelType = SidePanelType.NONE;
                            openSidePanel(SidePanelType.COLOR_PICKER);
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
                    cfg.particle_color.add("#9B51E0");
                    cfg.max_particle_color = 3;
                    cfg.saveProperties();
                    return true;
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
                // Row 0: UI Theme Cycle
                if (checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    ConfigThemes.Theme next = ConfigThemes.cycleNextTheme();
                    applyTheme(next);
                    return true;
                }
                curY += h + gap;

                // Row 1: UI Background Cycle
                if (checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    ConfigBackgrounds.cycleNextBackground();
                    return true;
                }
                curY += h + gap;

                // Row 2: Screen Animation Cycle
                if (checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH)) {
                    animSpeedIndex = (animSpeedIndex + 1) % ANIM_SPEEDS.length;
                    return true;
                }
                curY += h + gap;

                // Row 2: Mod Version Badge (non-interactive)
                curY += h + gap;

                // Row 3: Permissions Badge (non-interactive)
            }
                }
            }
        }

        return false;
    }

    private boolean handlePillarsClick(double mouseX, double mouseY, int button, int contentX, int contentY, int contentW, int contentH, int catHeaderH) {
        return handlePillarTableClick(mouseX, mouseY, button, contentX, contentY, contentW, contentH, catHeaderH);
    }

    private boolean handlePillarTableClick(double mouseX, double mouseY, int button, int x, int y, int w, int h, int catHeaderH) {
        List<PillarIdManager.PillarData> pillarList = new ArrayList<>(PillarIdManager.getClient().copyDataSnapshot().values());
        pillarList.sort(Comparator.comparing(p -> p.id != null ? p.id : ""));

        int curY = y + catHeaderH + 4;

        // 1. Top Action Bar
        int bar1H = 20;
        int btnH = 14;
        int barY = curY + (bar1H - btnH) / 2;

        // [ ⟳ Reload ]
        int relW = 60;
        int relX = x + 6;
        if (mouseX >= relX && mouseX <= relX + relW && mouseY >= barY && mouseY <= barY + btnH) {
            syncPillars();
            return true;
        }

        // [ ✕ Remove Selected (<N>) ]
        int remSelW = 105;
        int remSelX = relX + relW + 4;
        if (!selectedPillarIds.isEmpty() && mouseX >= remSelX && mouseX <= remSelX + remSelW && mouseY >= barY && mouseY <= barY + btnH) {
            for (String id : new ArrayList<>(selectedPillarIds)) {
                PillarIdManager.getClient().removePillarData(id);
                PacketFactory.sendToServer(new RemovePillarPacket(id));
            }
            selectedPillarIds.clear();
            return true;
        }

        // [ ✕ Remove All ]
        int remAllW = 75;
        int remAllX = remSelX + remSelW + 4;
        if (!pillarList.isEmpty() && mouseX >= remAllX && mouseX <= remAllX + remAllW && mouseY >= barY && mouseY <= barY + btnH) {
            for (PillarIdManager.PillarData data : pillarList) {
                if (data.id != null) {
                    PillarIdManager.getClient().removePillarData(data.id);
                    PacketFactory.sendToServer(new RemovePillarPacket(data.id));
                }
            }
            selectedPillarIds.clear();
            return true;
        }

        // [ ✓ Save ]
        int saveW = 50;
        int saveX = remAllX + remAllW + 4;
        if (mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= barY && mouseY <= barY + btnH) {
            PillarIdManager.getClient().saveAllPillars();
            PacketFactory.sendToServer(new UpdateAllPillarIdsPacket(PillarIdManager.getClient().copyDataSnapshot()));
            return true;
        }

        curY += bar1H + 2;

        // 2. Fixed Table Column Headers
        int headH = 16;
        int scrollbarW = 4;
        int scrollbarTrackX = x + w - scrollbarW - 5;
        int availW = (scrollbarTrackX - 6) - (x + 6);
        int colX = x + 6;

        int chkW = 16;
        int iconColW = 38;
        int colorsColW = 48;
        int dimColW = 58;
        int coordsColW = 72;
        int actionsColW = 58;
        int gap = 4;
        int idColW = Math.max(65, availW - (chkW + iconColW + colorsColW + dimColW + coordsColW + actionsColW + (gap * 6)));

        // Header Checkbox click
        if (mouseX >= colX && mouseX <= colX + chkW && mouseY >= curY && mouseY <= curY + headH) {
            if (!pillarList.isEmpty() && selectedPillarIds.size() == pillarList.size()) {
                selectedPillarIds.clear();
            } else {
                selectedPillarIds.clear();
                for (PillarIdManager.PillarData data : pillarList) {
                    if (data.id != null) {
                        selectedPillarIds.add(data.id);
                    }
                }
            }
            return true;
        }

        curY += headH + 3;

        // 3. Scrollable Rows Viewport
        int viewportY = curY;
        int viewportH = (y + h - 6) - viewportY;
        if (mouseY < viewportY || mouseY > viewportY + viewportH) {
            return false;
        }

        int rowH = 24;
        int rowGap = 2;
        int startY = viewportY + 2 - (int) scrollOffset;

        for (int r = 0; r < pillarList.size(); r++) {
            PillarIdManager.PillarData data = pillarList.get(r);
            int rowY = startY + r * (rowH + rowGap);
            if (rowY + rowH < viewportY || rowY > viewportY + viewportH) continue;

            if (mouseX >= colX && mouseX <= colX + availW && mouseY >= rowY && mouseY <= rowY + rowH) {
                // Row Checkbox click
                if (mouseX >= colX && mouseX <= colX + chkW) {
                    if (selectedPillarIds.contains(data.id)) {
                        selectedPillarIds.remove(data.id);
                    } else {
                        selectedPillarIds.add(data.id);
                    }
                    return true;
                }

                // ACTIONS column clicks:
                int actH = 13;
                int actY = rowY + (rowH - actH) / 2;
                int rcX = colX + chkW + gap + idColW + gap + iconColW + gap + colorsColW + gap + dimColW + gap + coordsColW + gap;
                int editW = 34;
                int editX = rcX + 2;
                int delW = 16;
                int delX = editX + editW + 4;

                if (mouseX >= editX && mouseX <= editX + editW && mouseY >= actY && mouseY <= actY + actH) {
                    openPillarDetailInSidePanel(data.id);
                    return true;
                }

                if (mouseX >= delX && mouseX <= delX + delW && mouseY >= actY && mouseY <= actY + actH) {
                    PillarIdManager.getClient().removePillarData(data.id);
                    PacketFactory.sendToServer(new RemovePillarPacket(data.id));
                    selectedPillarIds.remove(data.id);
                    if (Objects.equals(selectedPillarDetailId, data.id) && sidePanelType == SidePanelType.PILLAR_DETAIL) {
                        closeSidePanel();
                    }
                    return true;
                }

                // Check ID column click:
                int idX = colX + chkW + gap;
                long now = System.currentTimeMillis();
                boolean isDouble = data.id != null && data.id.equals(lastPillarRowClickId) && (now - lastPillarRowClickTime < 350);
                lastPillarRowClickId = data.id;
                lastPillarRowClickTime = now;

                if (isDouble) {
                    locatePillarInWorld(data);
                    return true;
                }

                if (mouseX >= idX && mouseX <= idX + idColW) {
                    openPillarDetailInSidePanel(data.id);
                    return true;
                }

                // Clicking anywhere else on row toggles selection
                if (selectedPillarIds.contains(data.id)) {
                    selectedPillarIds.remove(data.id);
                } else {
                    selectedPillarIds.add(data.id);
                }
                return true;
            }
        }

        return false;
    }

    private boolean handlePillarDetailClick(double mouseX, double mouseY, int button, int x, int y, int w, int h, int catHeaderH) {
        PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(selectedPillarDetailId);
        if (data == null) {
            selectedPillarDetailId = null;
            return false;
        }

        PillarParticleConfig globalCfg = PillarParticleConfig.get();
        int curY = y + catHeaderH + 4;

        // 1. Top Detail Header Bar
        int bar1H = 20;
        int btnH = 14;
        int barY = curY + (bar1H - btnH) / 2;

        // [ ◀ Back ]
        int backW = 54;
        int backX = x + 6;
        if (mouseX >= backX && mouseX <= backX + backW && mouseY >= barY && mouseY <= barY + btnH) {
            selectedPillarDetailId = null;
            scrollOffset = 0.0;
            return true;
        }

        // [ ✓ Save Pillar ]
        int saveW = 82;
        int saveX = x + w - saveW - 6;
        if (mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= barY && mouseY <= barY + btnH) {
            PillarIdManager.getClient().saveAllPillars();
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }

        curY += bar1H + 3;

        // 2. Scrollable Detail Viewport
        int viewportY = curY;
        int viewportH = (y + h - 6) - viewportY;
        if (mouseY < viewportY || mouseY > viewportY + viewportH) {
            return false;
        }

        int rowH = 26;
        int rowGap = 3;
        int scrollbarW = 4;
        int scrollbarTrackX = x + w - scrollbarW - 5;
        int rowW = (scrollbarTrackX - 6) - (x + 6);
        int rowX = x + 6;

        int startY = viewportY + 2 - (int) scrollOffset;
        int rY = startY;

        // Row 1: Use Custom Pattern Toggle
        if (checkToggleClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH)) {
            boolean cur = data.use_pattern != null ? data.use_pattern : globalCfg.use_pattern;
            data.use_pattern = !cur;
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        rY += rowH + rowGap;

        // Row 2: Trajectory Pattern Cycle
        if (checkCycleClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH)) {
            String curPat = data.pattern != null && !data.pattern.isEmpty() ? data.pattern : "default";
            int idx = 0;
            for (int i = 0; i < PARTICLE_PATTERNS.length; i++) {
                if (PARTICLE_PATTERNS[i].equalsIgnoreCase(curPat)) {
                    idx = i;
                    break;
                }
            }
            data.pattern = PARTICLE_PATTERNS[(idx + 1) % PARTICLE_PATTERNS.length];
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        rY += rowH + rowGap;

        // Row 3: Pattern Speed Counter
        int speedStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (speedStep != 0) {
            double cur = data.pattern_speed != null ? data.pattern_speed : globalCfg.pattern_speed;
            data.pattern_speed = Math.max(0.01D, Math.min(0.50D, Math.round((cur + speedStep * 0.01D) * 100.0) / 100.0));
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        rY += rowH + rowGap;

        // Row 4: Pattern Spread Counter
        int spreadStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (spreadStep != 0) {
            double cur = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
            data.pattern_spread = Math.max(0.01D, Math.min(0.50D, Math.round((cur + spreadStep * 0.01D) * 100.0) / 100.0));
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        rY += rowH + rowGap;

        // Row 5: Pattern Intensity Counter
        int intStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (intStep != 0) {
            double cur = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
            data.pattern_intensity = Math.max(0.1D, Math.min(5.0D, Math.round((cur + intStep * 0.1D) * 10.0) / 10.0));
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        rY += rowH + rowGap;

        // Row 6: Per-Pillar Color Palette
        List<String> colors = (data.dyeColors != null && !data.dyeColors.isEmpty()) ? data.dyeColors : new ArrayList<>(globalCfg.particle_color);
        while (colors.size() < 5) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(5, Math.max(1, colors.size())));

        int colorStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (colorStep != 0) {
            data.max_particle_color = Math.max(1, Math.min(5, maxColors + colorStep));
            if (data.dyeColors == null) {
                data.dyeColors = new ArrayList<>(colors);
            }
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }

        int counterW = 86;
        int counterX = rowX + rowW - counterW - 8;
        int swatchSize = 13;
        int swatchGap = 3;
        int swatchesTotalW = 7 * (swatchSize + swatchGap) - swatchGap;
        int swatchStartX = counterX - swatchesTotalW - 10;
        int swatchY = rY + (rowH - swatchSize) / 2;

        for (int i = 0; i < 5; i++) {
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
        rY += rowH + rowGap;

        // Row 7: Save Pillar Changes Button
        Font font = Minecraft.getInstance().font;
        int saveBtnW = font.width("✓ Save Pillar") + 16;
        if (checkButtonClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH, saveBtnW)) {
            PillarIdManager.getClient().saveAllPillars();
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }

        return false;
    }

    private boolean handleColorModalClick(double mouseX, double mouseY) {
        int effW = getEffectiveWidth();
        int effH = getEffectiveHeight();
        int modalW = 340;
        int modalH = 196;
        int modalX = (effW - modalW) / 2;
        int modalY = (effH - modalH) / 2;

        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 10;
        int closeY = modalY + 7;
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
                    if (data.dyeColors == null) {
                        data.dyeColors = new ArrayList<>(PillarParticleConfig.get().particle_color);
                    }
                    if (editingColorIndex < data.dyeColors.size()) {
                        data.dyeColors.set(editingColorIndex, hexString);
                    } else {
                        data.dyeColors.add(hexString);
                    }
                    PillarIdManager.getClient().updatePillarData(data);
                    PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
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

    private boolean checkSideRowToggleClick(double mouseX, double mouseY, int x, int y, int w, int h) {
        int btnW = 36;
        int btnH = 15;
        int btnX = x + w - btnW - 4;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
    }

    private boolean checkSideRowCycleClick(double mouseX, double mouseY, int x, int y, int w, int h) {
        int btnW = 66;
        int btnH = 15;
        int btnX = x + w - btnW - 4;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
    }

    private int checkSideRowCounterClick(double mouseX, double mouseY, int x, int y, int w, int h) {
        int counterW = 74;
        int counterH = 15;
        int counterX = x + w - counterW - 4;
        int counterY = y + (h - counterH) / 2;
        int stepW = 15;
        if (mouseY >= counterY && mouseY <= counterY + counterH) {
            if (mouseX >= counterX && mouseX <= counterX + stepW) {
                return -1;
            } else if (mouseX >= counterX + counterW - stepW && mouseX <= counterX + counterW) {
                return 1;
            }
        }
        return 0;
    }

    private boolean handleSidePanelClick(double mouseX, double mouseY, int button, int x, int y, int w, int h) {
        int sHeadX = x + 6;
        int sHeadY = y + 7;
        int sHeadW = w - 12;
        int sHeadH = 22;

        // Close button [ ✕ ]
        int closeSize = 14;
        int closeX = sHeadX + sHeadW - closeSize - 4;
        int closeY = sHeadY + 4;
        if (mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize) {
            closeSidePanel();
            return true;
        }

        int sContentX = x + 6;
        int sContentY = sHeadY + sHeadH + 4;
        int sContentW = sHeadW;
        int sContentH = (y + h - 7) - sContentY;

        if (sidePanelType == SidePanelType.PILLAR_DETAIL) {
            return handleSidePanelPillarClick(mouseX, mouseY, button, sContentX, sContentY, sContentW, sContentH);
        } else if (sidePanelType == SidePanelType.COLOR_PICKER) {
            return handleSidePanelColorPickerClick(mouseX, mouseY, button, sContentX, sContentY, sContentW, sContentH);
        } else if (sidePanelType == SidePanelType.PRESETS) {
            return handleSidePanelPresetsClick(mouseX, mouseY, button, sContentX, sContentY, sContentW, sContentH);
        }

        return false;
    }

    private boolean handleSidePanelPresetsClick(double mouseX, double mouseY, int button, int x, int y, int w, int h) {
        PresetsConfig presetsCfg = PresetsConfig.get();
        List<PresetsConfig.Preset> presets = presetsCfg.getPresets();
        List<String> keys = presetsCfg.getPresetKeys();
        if (selectedPresetIndex >= presets.size()) {
            selectedPresetIndex = Math.max(0, presets.size() - 1);
        }
        String currentKey = (keys.isEmpty() || selectedPresetIndex >= keys.size()) ? "default" : keys.get(selectedPresetIndex);
        boolean isDefault = "default".equalsIgnoreCase(currentKey);

        int pad = 5;
        int curY = y + pad;
        int rowW = w - pad * 2;
        int rowX = x + pad;

        // Card 0: Active Preset selector cycle
        int selH = 34;
        int cycleBtnH = 15;
        int cycleBtnY = curY + 15;
        int cycleBtnW = rowW - 10;
        int cycleBtnX = rowX + 5;
        if (mouseX >= cycleBtnX && mouseX <= cycleBtnX + cycleBtnW && mouseY >= cycleBtnY && mouseY <= cycleBtnY + cycleBtnH) {
            if (!presets.isEmpty()) {
                selectedPresetIndex = (selectedPresetIndex + 1) % presets.size();
            }
            return true;
        }
        curY += selH + 5;

        // Card 1: Apply
        int btnH = 18;
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            if (!keys.isEmpty() && selectedPresetIndex < keys.size()) {
                presetsCfg.applyPreset(keys.get(selectedPresetIndex));
                rebuildActiveItemsCache();
                itemsListDirty = true;
            }
            return true;
        }
        curY += btnH + 4;

        // Card 2: Save to Preset
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            if (!presets.isEmpty() && selectedPresetIndex < keys.size()) {
                String key = keys.get(selectedPresetIndex);
                PillarParticleConfig cfg = PillarParticleConfig.get();
                presetsCfg.savePreset(key, presets.get(selectedPresetIndex).name, new HashSet<>(cfg.items));
                rebuildActiveItemsCache();
                itemsListDirty = true;
            }
            return true;
        }
        curY += btnH + 4;

        // Card 3: Save as New Preset
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            PillarParticleConfig cfg = PillarParticleConfig.get();
            String newKey = presetsCfg.generatePresetKey();
            presetsCfg.savePreset(newKey, "Custom " + (presets.size()), new HashSet<>(cfg.items));
            selectedPresetIndex = presetsCfg.getPresets().size() - 1;
            rebuildActiveItemsCache();
            itemsListDirty = true;
            return true;
        }
        curY += btnH + 4;

        // Card 4: Reset to Defaults
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            presetsCfg.applyPreset("default");
            selectedPresetIndex = 0;
            rebuildActiveItemsCache();
            itemsListDirty = true;
            return true;
        }
        curY += btnH + 4;

        // Card 5: Delete Custom Preset (if not default)
        if (!isDefault) {
            if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
                presetsCfg.deletePreset(currentKey);
                selectedPresetIndex = 0;
                presetsCfg.applyPreset("default");
                rebuildActiveItemsCache();
                itemsListDirty = true;
                return true;
            }
        }

        return false;
    }

    private boolean handleSidePanelPillarClick(double mouseX, double mouseY, int button, int x, int y, int w, int h) {
        PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(selectedPillarDetailId);
        if (data == null) return false;

        PillarParticleConfig globalCfg = PillarParticleConfig.get();
        int curY = y + 4 - sidePanelScrollOffset;
        int rowH = 22;
        int rowGap = 3;
        int rowX = x + 4;
        int rowW = w - 8;

        // Skip row 0 (coordinates badge)
        curY += 15 + rowGap;

        // Row 1: Custom Pattern Toggle
        if (checkSideRowToggleClick(mouseX, mouseY, rowX, curY, rowW, rowH)) {
            boolean cur = data.use_pattern != null ? data.use_pattern : globalCfg.use_pattern;
            data.use_pattern = !cur;
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        curY += rowH + rowGap;

        // Row 2: Pattern Cycle
        if (checkSideRowCycleClick(mouseX, mouseY, rowX, curY, rowW, rowH)) {
            String curPat = data.pattern != null && !data.pattern.isEmpty() ? data.pattern : "default";
            int idx = 0;
            for (int i = 0; i < PARTICLE_PATTERNS.length; i++) {
                if (PARTICLE_PATTERNS[i].equalsIgnoreCase(curPat)) {
                    idx = i;
                    break;
                }
            }
            data.pattern = PARTICLE_PATTERNS[(idx + 1) % PARTICLE_PATTERNS.length];
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        curY += rowH + rowGap;

        // Row 3: Speed Counter
        int speedStep = checkSideRowCounterClick(mouseX, mouseY, rowX, curY, rowW, rowH);
        if (speedStep != 0) {
            double cur = data.pattern_speed != null ? data.pattern_speed : globalCfg.pattern_speed;
            data.pattern_speed = Math.max(0.01D, Math.min(0.50D, Math.round((cur + speedStep * 0.01D) * 100.0) / 100.0));
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        curY += rowH + rowGap;

        // Row 4: Spread Counter
        int spreadStep = checkSideRowCounterClick(mouseX, mouseY, rowX, curY, rowW, rowH);
        if (spreadStep != 0) {
            double cur = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
            data.pattern_spread = Math.max(0.01D, Math.min(0.50D, Math.round((cur + spreadStep * 0.01D) * 100.0) / 100.0));
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        curY += rowH + rowGap;

        // Row 5: Intensity Counter
        int intStep = checkSideRowCounterClick(mouseX, mouseY, rowX, curY, rowW, rowH);
        if (intStep != 0) {
            double cur = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
            data.pattern_intensity = Math.max(0.1D, Math.min(5.0D, Math.round((cur + intStep * 0.1D) * 10.0) / 10.0));
            PillarIdManager.getClient().updatePillarData(data);
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        curY += rowH + rowGap;

        // Row 6: Palette & Swatches
        List<String> colors = (data.dyeColors != null && !data.dyeColors.isEmpty()) ? data.dyeColors : new ArrayList<>(globalCfg.particle_color);
        while (colors.size() < 5) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(5, Math.max(1, colors.size())));

        int counterW = 60;
        int counterH = 14;
        int counterX = rowX + rowW - counterW - 4;
        int counterY = curY + 2;
        int stepW = 13;
        if (mouseY >= counterY && mouseY <= counterY + counterH) {
            if (mouseX >= counterX && mouseX <= counterX + stepW) {
                data.max_particle_color = Math.max(1, maxColors - 1);
                if (data.dyeColors == null) data.dyeColors = new ArrayList<>(colors);
                PillarIdManager.getClient().updatePillarData(data);
                PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
                return true;
            } else if (mouseX >= counterX + counterW - stepW && mouseX <= counterX + counterW) {
                data.max_particle_color = Math.min(5, maxColors + 1);
                if (data.dyeColors == null) data.dyeColors = new ArrayList<>(colors);
                PillarIdManager.getClient().updatePillarData(data);
                PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
                return true;
            }
        }

        // Swatches click
        int swSize = 13;
        int swGap = 4;
        int swY = curY + 17;
        for (int i = 0; i < 5; i++) {
            int swX = rowX + 6 + i * (swSize + swGap);
            if (mouseX >= swX && mouseX <= swX + swSize && mouseY >= swY && mouseY <= swY + swSize) {
                editingColorIndex = i;
                isEditingPillarColor = true;
                editingPillarId = data.id;
                returnSidePanelType = SidePanelType.PILLAR_DETAIL;
                String hex = colors.get(i).replace("#", "");
                try {
                    int rgb = Integer.parseInt(hex, 16);
                    float[] hsb = new float[3];
                    rgbToHsb(rgb, hsb);
                    pickerHue = hsb[0];
                    pickerSat = hsb[1];
                    pickerBri = hsb[2];
                } catch (Exception ignored) {}
                openSidePanel(SidePanelType.COLOR_PICKER);
                return true;
            }
        }
        curY += 34 + rowGap;

        // Row 7: Save Button
        int btnH = 16;
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            PillarIdManager.getClient().saveAllPillars();
            PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
            return true;
        }
        curY += btnH + rowGap;

        // Row 8: Locate in World
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            locatePillarInWorld(data);
            return true;
        }

        return false;
    }

    private boolean handleSidePanelColorPickerClick(double mouseX, double mouseY, int button, int x, int y, int w, int h) {
        int pad = 6;
        int curY = y + pad;

        int hueW = 14;
        int gradW = w - pad * 3 - hueW;
        int gradH = 82;
        int gradX = x + pad;
        int gradY = curY;
        int hueX = gradX + gradW + pad;
        int hueY = gradY;
        int hueH = gradH;

        // 1. Click in 2D Box -> start drag
        if (mouseX >= gradX && mouseX <= gradX + gradW && mouseY >= gradY && mouseY <= gradY + gradH) {
            draggingGradient = true;
            pickerSat = Math.max(0.0f, Math.min(1.0f, (float) (mouseX - gradX) / gradW));
            pickerBri = Math.max(0.0f, Math.min(1.0f, 1.0f - ((float) (mouseY - gradY) / gradH)));
            return true;
        }

        // 2. Click in Hue Slider -> start drag
        if (mouseX >= hueX && mouseX <= hueX + hueW && mouseY >= hueY && mouseY <= hueY + hueH) {
            draggingHue = true;
            pickerHue = Math.max(0.0f, Math.min(360.0f, ((float) (mouseY - hueY) / hueH) * 360.0f));
            return true;
        }

        curY += gradH + 8;
        int prevSize = 20;
        curY += prevSize + 8;

        // 3. Preset Swatches
        curY += 11;
        int pSize = 13;
        int pGap = 3;
        int pCols = Math.max(1, (w - pad * 2) / (pSize + pGap));
        for (int p = 0; p < PALETTE_PRESETS.length; p++) {
            int px = gradX + (p % pCols) * (pSize + pGap);
            int py = curY + (p / pCols) * (pSize + pGap);
            if (mouseX >= px && mouseX <= px + pSize && mouseY >= py && mouseY <= py + pSize) {
                int col = PALETTE_PRESETS[p];
                float[] hsb = new float[3];
                rgbToHsb(col, hsb);
                pickerHue = hsb[0];
                pickerSat = hsb[1];
                pickerBri = hsb[2];
                return true;
            }
        }
        int pRows = (int) Math.ceil((double) PALETTE_PRESETS.length / pCols);
        curY += pRows * (pSize + pGap) + 10;

        // 4. Apply Button
        int btnW = w - pad * 2;
        int btnH = 16;
        if (mouseX >= gradX && mouseX <= gradX + btnW && mouseY >= curY && mouseY <= curY + btnH) {
            int currentColorInt = hsbToRgb(pickerHue, pickerSat, pickerBri);
            int r = (currentColorInt >> 16) & 0xFF;
            int g = (currentColorInt >> 8) & 0xFF;
            int b = currentColorInt & 0xFF;
            String hexString = String.format("#%02X%02X%02X", r, g, b);

            if (isEditingPillarColor && editingPillarId != null) {
                PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(editingPillarId);
                if (data != null) {
                    if (data.dyeColors == null) {
                        data.dyeColors = new ArrayList<>(PillarParticleConfig.get().particle_color);
                    }
                    if (editingColorIndex < data.dyeColors.size()) {
                        data.dyeColors.set(editingColorIndex, hexString);
                    } else {
                        data.dyeColors.add(hexString);
                    }
                    PillarIdManager.getClient().updatePillarData(data);
                    PacketFactory.sendToServer(new UpdatePillarDataPacket(data));
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

            if (returnSidePanelType == SidePanelType.PILLAR_DETAIL) {
                openSidePanel(SidePanelType.PILLAR_DETAIL);
            } else {
                closeSidePanel();
            }
            return true;
        }
        curY += btnH + 4;

        // Back to Pillar Button
        if (returnSidePanelType == SidePanelType.PILLAR_DETAIL) {
            if (mouseX >= gradX && mouseX <= gradX + btnW && mouseY >= curY && mouseY <= curY + btnH) {
                openSidePanel(SidePanelType.PILLAR_DETAIL);
                return true;
            }
        }

        return false;
    }

    private boolean checkToggleClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y < clipY || y + h > clipY + clipH) return false;
        int btnW = 38;
        int btnH = 15;
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
        int counterW = 86;
        int counterH = 16;
        int counterX = x + w - counterW - 8;
        int counterY = y + (h - counterH) / 2;
        int stepBtnW = 16;

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
        float uiScale = getGuiUiScale();
        if (Math.abs(uiScale - 1.0f) > 0.001f) {
            mouseX /= uiScale;
            mouseY /= uiScale;
        }
        isDraggingScrollbar = false;
        draggingGradient = false;
        draggingHue = false;
        if (activeTab != null && activeTab.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        float uiScale = getGuiUiScale();
        if (Math.abs(uiScale - 1.0f) > 0.001f) {
            mouseX /= uiScale;
            mouseY /= uiScale;
            dragX /= uiScale;
            dragY /= uiScale;
        }

        if (isDraggingScrollbar && maxScroll > 0) {
            int trackH = currentScrollbarTrackH;
            int thumbH = currentScrollbarThumbH;
            int usableTrack = trackH - thumbH;
            if (usableTrack > 0) {
                double deltaThumb = mouseY - scrollDragStartY;
                double newOffset = scrollDragStartOffset + (deltaThumb / (double) usableTrack) * maxScroll;
                targetScrollOffset = Math.max(0, Math.min(maxScroll, newOffset));
                scrollOffset = targetScrollOffset;
            }
            return true;
        }

        int effW = getEffectiveWidth();
        int effH = getEffectiveHeight();

        if (sidePanelType == SidePanelType.COLOR_PICKER && sidePanelProgress > 0.05f) {
            SidePanelLayout layout = calculateLayout();
            int sHeadY = layout.sidePanelY + 7;
            int sContentX = layout.sidePanelX + 6;
            int sContentY = sHeadY + 22 + 4;
            int sContentW = layout.sidePanelW - 12;

            int pad = 6;
            int hueW = 14;
            int gradW = sContentW - pad * 3 - hueW;
            int gradH = 82;
            int gradX = sContentX + pad;
            int gradY = sContentY + pad;
            int hueX = gradX + gradW + pad;
            int hueY = gradY;
            int hueH = gradH;

            if (draggingGradient) {
                pickerSat = Math.max(0.0f, Math.min(1.0f, (float) (mouseX - gradX) / gradW));
                pickerBri = Math.max(0.0f, Math.min(1.0f, 1.0f - ((float) (mouseY - gradY) / gradH)));
                return true;
            }
            if (draggingHue) {
                pickerHue = Math.max(0.0f, Math.min(360.0f, ((float) (mouseY - hueY) / hueH) * 360.0f));
                return true;
            }
        }

        if (showColorModal) {
            int modalW = 340;
            int modalH = 196;
            int modalX = (effW - modalW) / 2;
            int modalY = (effH - modalH) / 2;
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

        float uiScale = getGuiUiScale();
        if (Math.abs(uiScale - 1.0f) > 0.001f) {
            mouseX /= uiScale;
            mouseY /= uiScale;
        }

        if (sidePanelProgress > 0.05f && sidePanelType != SidePanelType.NONE) {
            SidePanelLayout layout = calculateLayout();
            int activeLeft = Math.max(layout.sidePanelX, layout.frameRight - 2);
            if (mouseX >= activeLeft && mouseX <= layout.sidePanelX + layout.sidePanelW
                    && mouseY >= layout.sidePanelY && mouseY <= layout.sidePanelY + layout.sidePanelH) {
                if (sidePanelMaxScroll > 0) {
                    sidePanelScrollOffset = Math.max(0, Math.min(sidePanelMaxScroll, sidePanelScrollOffset - (int) (delta * 16.0)));
                    return true;
                }
            }
        }

        if (maxScroll > 0) {
            targetScrollOffset = Math.max(0, Math.min(maxScroll, targetScrollOffset - delta * 24.0));
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
            if (sidePanelProgress > 0.05f && sidePanelType != SidePanelType.NONE) {
                if (returnSidePanelType == SidePanelType.PILLAR_DETAIL && sidePanelType == SidePanelType.COLOR_PICKER) {
                    openSidePanel(SidePanelType.PILLAR_DETAIL);
                } else {
                    closeSidePanel();
                }
                return true;
            }
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
