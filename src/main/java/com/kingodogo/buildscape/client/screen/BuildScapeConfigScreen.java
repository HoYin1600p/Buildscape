package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.PillarMarkerManager;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.config.PresetsConfig;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import com.kingodogo.buildscape.network.EditRefusedPacket;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.RemovePillarPacket;
import com.kingodogo.buildscape.network.RequestPillarIdsPacket;
import com.kingodogo.buildscape.network.ResetPillarsPacket;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import com.kingodogo.buildscape.network.UpdateGameRulePacket;
import com.kingodogo.buildscape.network.UpdatePillarDataPacket;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.Util;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


// Unified Single-Panel Buildscape Configuration Screen.
public class BuildScapeConfigScreen extends Screen {

    private record TextLayoutKey(Font font, String text, int width, int lines) {}
    private final ConfigRenderWork.Cache<TextLayoutKey, List<String>> textLayouts = new ConfigRenderWork.Cache<>(256);
    private final Map<Item, String> searchNames = new HashMap<>();
    private final Map<Item, String> searchIds = new HashMap<>();
    private long resourceGeneration = -1;
    private Collection<String> validatedItems;
    private Set<String> invalidItems = Collections.emptySet();

    private void refreshResourceCaches() {
        long generation = ConfigScreenResources.generation();
        if (resourceGeneration == generation) return;
        resourceGeneration = generation;
        textLayouts.clear();
        searchNames.clear();
        searchIds.clear();
        itemsListDirty = true;
        tagsListDirty = true;
    }

    record ConfigLink(String title, String description, String url, ResourceLocation icon) {
        public ConfigLink(String title, String description, String url) {
            this(title, description, url, null);
        }
    }

    record PresetSideTabGeometry(int x, int y, int width, int height, int centerX, int centerY) {}
    record PresetPanelGeometry(int x, int y, int width) {}
    record SidePanelContentGeometry(int headerX, int headerY, int headerWidth,
                                    int contentX, int contentY, int contentWidth, int contentHeight) {}

    record ItemToolbarGeometry(int filterWidth, int searchWidth, int namespaceWidth, int gap, int totalWidth) {}

    static PresetSideTabGeometry calculatePresetSideTabGeometry(int frameRight, int tabY, boolean hovered) {
        int width = hovered ? 32 : 30;
        int height = 30;
        int x = frameRight - 2;
        return new PresetSideTabGeometry(x, tabY, width, height, x + width / 2, tabY + height / 2);
    }

    static PresetPanelGeometry calculatePresetPanelGeometry(int contentX, int contentY, int contentWidth,
                                                             int contentHeight, int groupHeight, int maximumWidth) {
        int width = Math.min(Math.max(1, maximumWidth), Math.max(1, contentWidth - 16));
        int x = contentX + (contentWidth - width) / 2;
        int y = contentY + Math.max(8, (contentHeight - groupHeight) / 2);
        return new PresetPanelGeometry(x, y, width);
    }

    static int cyclePresetIndex(int currentIndex, int direction, int presetCount) {
        return presetCount <= 0 ? 0 : Math.floorMod(currentIndex + Integer.signum(direction), presetCount);
    }

    static SidePanelContentGeometry calculateSidePanelContentGeometry(int panelX, int panelY, int panelWidth, int panelHeight) {
        int inset = 8;
        int headerY = panelY + 9;
        int headerHeight = 22;
        int contentY = headerY + headerHeight + 4;
        return new SidePanelContentGeometry(panelX + inset, headerY, panelWidth - inset * 2,
                panelX + inset, contentY, panelWidth - inset * 2, panelY + panelHeight - inset - contentY);
    }

    static float interpolatePresetDeleteReveal(long now, long startedAt, float from, float to, int durationMs) {
        float progress = durationMs <= 0 ? 1.0F : Math.min(1.0F, Math.max(0.0F, (now - startedAt) / (float) durationMs));
        return from + (to - from) * progress;
    }

    static String applyPresetNamePaste(String currentValue, boolean selected, String clipboardValue) {
        String current = currentValue == null ? "" : currentValue;
        String pasted = clipboardValue == null ? "" : clipboardValue.replaceAll("[\\p{Cntrl}\\s]+", " ").trim();
        String combined = selected ? pasted : current + pasted;
        return combined.substring(0, Math.min(32, combined.length()));
    }

    static String applyPresetNameBackspace(String currentValue, boolean selected) {
        String current = currentValue == null ? "" : currentValue;
        return selected || current.isEmpty() ? "" : current.substring(0, current.length() - 1);
    }

    static ItemToolbarGeometry calculateItemToolbarGeometry(int availableWidth) {
        int gap = 3;
        int safeWidth = Math.max(120, availableWidth);
        int filterWidth = safeWidth >= 255 ? Math.max(60, Math.min(92, safeWidth / 8))
                : Math.max(24, (safeWidth - 51) / 4);
        int namespaceWidth = safeWidth >= 255 ? Math.max(62, Math.min(128, safeWidth / 6)) : Math.max(30, filterWidth);
        int searchWidth = Math.max(1, safeWidth - filterWidth * 3 - namespaceWidth - gap * 4);
        return new ItemToolbarGeometry(filterWidth, searchWidth, namespaceWidth, gap,
                filterWidth * 3 + searchWidth + namespaceWidth + gap * 4);
    }

    static List<String> getCompactFilterLabels() {
        return List.of("All", "Inventory", "Active");
    }

    static String getNamespaceSelectorLabel(int selectedIndex, List<String> namespaces) {
        return selectedIndex >= 0 && selectedIndex < namespaces.size() ? namespaces.get(selectedIndex) : "All";
    }

    static int getSystemSettingRowCount() {
        return 4;
    }

    static int calculateCycleDescriptionWidth(int rowWidth, int controlWidth) {
        return Math.max(1, rowWidth - controlWidth - 20);
    }

    static int getItemToolbarControlHeight() {
        return 15;
    }

    static int getSystemSettingRowHeight() {
        return 36;
    }

    static int getWorldSettingRowHeight() {
        return 36;
    }

    static int calculateWorldContentHeight(int gap) {
        return 14 + 14 + 9 * (getWorldSettingRowHeight() + gap) - gap;
    }

    static int calculateToggleDescriptionWidth(int rowWidth, int controlWidth) {
        return Math.max(1, rowWidth - controlWidth - 20);
    }

    static int calculateSystemContentHeight(int gap) {
        return getSystemSettingRowCount() * (getSystemSettingRowHeight() + gap) - gap;
    }

    static String applySearchCharacter(String value, boolean selectAll, char character) {
        return selectAll ? String.valueOf(character) : value + character;
    }

    static String applySearchBackspace(String value, boolean selectAll) {
        if (selectAll) return "";
        return value.isEmpty() ? value : value.substring(0, value.length() - 1);
    }

    static float calculateItemContentTransitionAlpha(long now, long startedAt, int duration) {
        if (duration <= 0) return 1.0F;
        float progress = Math.min(1.0F, Math.max(0.0F, (now - startedAt) / (float) duration));
        return 0.45F + 0.55F * easeOutCubic(progress);
    }

    static boolean shouldCacheTagDiscovery(Collection<String> tags) {
        return tags != null && !tags.isEmpty();
    }

    static List<String> mergeDiscoveredTags(Collection<String> registryTags, Collection<String> holderTags) {
        Set<String> merged = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (registryTags != null) {
            for (String tag : registryTags) {
                if (tag != null && !tag.isBlank()) merged.add(tag.startsWith("#") ? tag : "#" + tag);
            }
        }
        if (holderTags != null) {
            for (String tag : holderTags) {
                if (tag != null && !tag.isBlank()) merged.add(tag.startsWith("#") ? tag : "#" + tag);
            }
        }
        return new ArrayList<>(merged);
    }

    static int resolveClientPermissionLevel(int syncedLevel, boolean localLevelTwo, boolean localLevelFour) {
        return PillarParticleConfig.resolveClientPermissionLevel(syncedLevel, localLevelTwo, localLevelFour);
    }

    static float readOnlyFeedbackAlpha(long now, long startedAt) {
        if (startedAt < 0L) return 0.0F;
        long elapsed = Math.max(0L, now - startedAt);
        return elapsed >= 600L ? 0.0F : 1.0F - elapsed / 600.0F;
    }

    static List<String> getWorldSettingSections() {
        return List.of("Player Settings", "World Rules");
    }

    // UI textures under textures/client/ui/
    static final ResourceLocation NAV_ITEMS_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/nav_items.png");
    static final ResourceLocation NAV_PARTICLES_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/nav_particles.png");
    static final ResourceLocation NAV_PILLARS_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/nav_pillars.png");
    static final ResourceLocation NAV_WORLD_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/nav_world.png");
    static final ResourceLocation NAV_SYSTEM_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/nav_system.png");
    static final ResourceLocation NAV_LINKS_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/nav_links.png");

    static final ResourceLocation SUBTAB_ITEMS_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/subtab_items.png");
    static final ResourceLocation SUBTAB_TAGS_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/subtab_tags.png");

    static final ResourceLocation PANEL_PILLAR_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/panel_pillar.png");
    static final ResourceLocation PANEL_COLOR_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/panel_color.png");

    static final ResourceLocation TOGGLE_ON_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/toggle_on.png");
    static final ResourceLocation TOGGLE_ON_TINT_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/toggle_on_tint.png");
    static final ResourceLocation TOGGLE_OFF_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/toggle_off.png");
    static final ResourceLocation TOGGLE_OFF_TINT_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/toggle_off_tint.png");


    static final ResourceLocation LINK_WEBSITE_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/link_website.png");
    static final ResourceLocation LINK_BUGS_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/link_bugs.png");
    static final ResourceLocation LINK_KOFI_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/link_kofi.png");
    static final ResourceLocation LINK_CURSEFORGE_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/link_curseforge.png");
    static final ResourceLocation LINK_MODRINTH_TEXTURE = new ResourceLocation("buildscape", "textures/client/ui/link_modrinth.png");

    private static final List<ConfigLink> CONFIG_LINKS = Collections.unmodifiableList(Arrays.asList(
            new ConfigLink("Buildscape Website", "News, documentation, and project information", "https://buildscape.online/", LINK_WEBSITE_TEXTURE),
            new ConfigLink("Report a Bug", "Report issues with Buildscape", "https://buildscape.online/report", LINK_BUGS_TEXTURE),
            new ConfigLink("Ko-fi", "Support continued Buildscape development", "https://ko-fi.com/itzmedga", LINK_KOFI_TEXTURE),
            new ConfigLink("CurseForge", "View releases on CurseForge", "https://www.curseforge.com/minecraft/mc-mods/buildscape", LINK_CURSEFORGE_TEXTURE),
            new ConfigLink("Modrinth", "View releases on Modrinth", "https://modrinth.com/project/9qweL5rw", LINK_MODRINTH_TEXTURE)
    ));

    static List<ConfigLink> getConfigLinks() {
        return CONFIG_LINKS;
    }

    private final Screen parent;
    private Screen wrapperScreen;

    private Consumer<AbstractWidget> addWidgetConsumer;
    private Consumer<GuiEventListener> removeWidgetConsumer;

    // Local client preferences and animation scaling.
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

    public static float easeInCubic(float t) {
        float clamped = Math.min(1.0f, Math.max(0.0f, t));
        return clamped * clamped * clamped;
    }

    private long openStartTime;
    private long closeStartTime = -1;
    private boolean isOpening = true;
    private boolean isClosing = false;
    private float openProgress = 0.0f;
    private float closeProgress = 1.0f;

    // Active theme palette.
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

    // Side navigation state.
    public static class NavItem {
        public final String id;
        public final String icon;
        public final ResourceLocation iconTexture;
        public final String title;
        public final String subtitle;

        public NavItem(String id, String icon, String title, String subtitle) {
            this(id, icon, null, title, subtitle);
        }

        public NavItem(String id, String icon, ResourceLocation iconTexture, String title, String subtitle) {
            this.id = id;
            this.icon = icon;
            this.iconTexture = iconTexture;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    private final List<NavItem> navItems = new ArrayList<>();
    private int selectedTabIndex = 0;
    private boolean navExpanded = false;

    // Scroll and viewport state.
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

    // Particle setting options.
    private static final String[] GLOBAL_PARTICLE_PATTERNS = {
            "none", "ring", "beam", "spiral", "fountain", "pulse", "burst", "snowflake"
    };
    private static final String[] PARTICLE_PATTERNS = {
            "none", "default", "beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"
    };

    // Pillar selection state.
    private final Set<String> selectedPillarIds = new HashSet<>();
    private long lastPillarRefreshCheck = 0L;
    private long lastPillarRowClickTime = 0L;
    private String lastPillarRowClickId = null;

    // Cached tab view state.
    private final TabCacheManager tabCache = new TabCacheManager();

    // Session-wide registry caches prevent first-open discovery work.
    private static final List<Item> STATIC_AVAILABLE_ITEMS = new ArrayList<>();
    private static final List<String> STATIC_AVAILABLE_MOD_NAMESPACES = new ArrayList<>();
    private static volatile boolean itemsCached = false;

    private static final List<String> STATIC_ALL_REGISTERED_TAGS = new ArrayList<>();
    private static final Map<String, Integer> STATIC_TAG_ITEM_COUNTS = new HashMap<>();
    private static volatile boolean tagsCached = false;

    // Cooldown on disk synchronization for pillar markers
    private static long lastPillarsSyncTime = 0L;
    private static final long PILLARS_SYNC_COOLDOWN_MS = 5000L;

    // Preset, item, and tag state.
    private int selectedPresetIndex = 0;
    // Edits wait here and go to the server together, three seconds after the last one or when the screen closes.
    private final ConfigUploadBatch uploadBatch = new ConfigUploadBatch();
    // The same wait for the pillar detail counters: one pending packet per pillar, the latest values win.
    private final PillarUploadBatch<UpdatePillarDataPacket> pillarUploadBatch = new PillarUploadBatch<>();
    private final ParticleRange.Migration rangeMigration = new ParticleRange.Migration();
    private String headerNotice = null;
    private long headerNoticeUntil = 0L;
    private int itemsSubTab = 0; // 0 = Display Items, 1 = Display Tags
    private int itemFilterMode = 0; // 0=All Items, 1=Inventory, 2=Active
    private int tagFilterMode = 0; // 0=All Tags, 1=Inventory, 2=Active
    private int currentTagModIndex = 0;
    private int itemSearchNamespaceIndex = -1;
    private int tagSearchNamespaceIndex = -1;
    private int focusedSearchField = 0;
    private boolean searchTextSelected = false;
    private int lastSearchClickField = 0;
    private long lastSearchClickTime = 0L;
    private long itemContentTransitionStartTime = 0L;
    private boolean itemContentTransitioning = false;
    private boolean presetNameFocused = false;
    private boolean presetNameSelected = false;
    private float presetDeleteReveal = 0.0F;
    private float presetDeleteRevealFrom = 0.0F;
    private float presetDeleteRevealTarget = 0.0F;
    private long presetDeleteRevealStartedAt = 0L;
    private String presetNameInput = "";
    private String pendingDestructiveAction = null;
    private long pendingDestructiveActionUntil = 0L;
    private String itemSearchFilter = "";
    private String tagSearchFilter = "";
    private final String[] TAG_CATEGORY_CHIPS = {"All", "keys", "gems", "ores", "vault", "swords", "gear"};
    private int activeTagChipIndex = 0;

    private final List<Item> availableItems = new ArrayList<>();
    private final List<String> availableModNamespaces = new ArrayList<>();
    private final List<String> availableTagNamespaces = new ArrayList<>();
    private int currentModIndex = 0;
    private final List<Item> cachedInventoryItems = new ArrayList<>();
    private final Set<Item> cachedActiveItems = new HashSet<>();
    private final Set<String> cachedActiveTags = new HashSet<>();
    private final List<Item> cachedFilteredItems = new ArrayList<>();
    private boolean itemsListDirty = true;

    private final List<String> allRegisteredTags = new ArrayList<>();
    private final Map<String, Integer> tagItemCounts = new HashMap<>();
    private final List<String> cachedFilteredTags = new ArrayList<>();
    private boolean tagsListDirty = true;
    private boolean inventoryTagsDirty = true;
    private final Map<Item, ItemStack> itemStackCache = new HashMap<>();
    private final GuiItemGridBatch itemGridBatch = new GuiItemGridBatch();
    private final Map<String, ItemStack> stringToStackCache = new HashMap<>();

    // Deferred Tooltip State (prevents scale drift on Scale 3/4)
    private List<Component> deferredTooltip = null;
    private Component hoveredRefusalTooltip = null;
    private int deferredTooltipX = 0;
    private int deferredTooltipY = 0;
    private int rawMouseX = 0;
    private int rawMouseY = 0;
    private long readOnlyDeniedAt = -1L;
    private int readOnlyDeniedX = 0;
    private int readOnlyDeniedY = 0;

    // Color picker state.
    private boolean showColorModal = false;
    private boolean isEditingPillarColor = false;
    private String editingPillarId = null;
    private int editingColorIndex = 0;
    private float pickerHue = 42.0f;       // 0 to 360
    private float pickerSat = 0.89f;      // 0.0 to 1.0
    private float pickerBri = 1.0f;       // 0.0 to 1.0
    private boolean draggingGradient = false;
    private boolean draggingHue = false;
    private int inlinePickerGradX = 0;
    private int inlinePickerGradY = 0;
    private int inlinePickerGradW = 0;
    private int inlinePickerGradH = 0;
    private int inlinePickerHueX = 0;
    private int inlinePickerHueY = 0;
    private int inlinePickerHueW = 0;
    private int inlinePickerHueH = 0;
    private boolean hexColorFocused = false;
    private boolean hexColorSelected = false;
    private String hexColorInput = "";

    private static final int[] PALETTE_PRESETS = {
            0xFFFFFFFF, 0xFFFFB81C, 0xFFFF3B30, 0xFF34C759,
            0xFF00C7BE, 0xFF5856D6, 0xFFFF9500, 0xFFFF2D55,
            0xFF3CDFFF, 0xFFE8FEFD, 0xFF8E8E93, 0xFF1C1C1E
    };

    // Per-pillar detail state.
    private String selectedPillarDetailId = null;
    private String closingPillarDetailId = null;
    private String prevPillarDetailId = null;
    private long pillarSwitchStartTime = 0L;
    private boolean isPillarSwitching = false;

    // Sliding side-panel state.
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
    private boolean pendingPresetsAutoOpen = false;

    // Sidenav expansion animation
    private float themeToggleAnimPos = -1.0f;
    private float themeToggleAnimFrom = 0.0f;
    private long themeToggleAnimStartTime = 0L;
    private String themeToggleLastThemeId = null;

    private static final Map<String, Float> toggleAnimProgressMap = new HashMap<>();
    private static final Map<String, Long> toggleAnimStartTimeMap = new HashMap<>();
    private static final Map<String, Boolean> toggleLastStateMap = new HashMap<>();

    private float getThemeTargetPos(ConfigThemes.Theme theme) {
        if (theme != null && "light".equals(theme.id)) return 1.0f;
        return 0.0f; // 2-stage toggle: Moon on 0.0f for Dark and all other custom themes
    }

    private float navAnimProgress = 0.0f; // 0.0 = collapsed, 1.0 = expanded
    private boolean isNavAnimating = false;
    private long navAnimStartTime = 0L;

    public void toggleNavExpansion() {
        navExpanded = !navExpanded;
        try {
            BuildscapeClientConfig.get().setNavbarExpanded(navExpanded);
        } catch (Exception ignored) {}
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
        // Save viewport state of outgoing tab into cache
        if (selectedTabIndex >= 0 && selectedTabIndex < navItems.size()) {
            String prevId = navItems.get(selectedTabIndex).id;
            TabCacheManager.TabViewState oldState = tabCache.getOrCreate(prevId);
            oldState.scrollOffset = scrollOffset;
            oldState.targetScrollOffset = targetScrollOffset;
            oldState.maxScroll = maxScroll;
        }

        prevTabIndex = selectedTabIndex;
        selectedTabIndex = newIndex;
        try {
            BuildscapeClientConfig.get().setLastActiveTab(newIndex);
        } catch (Exception ignored) {}

        // Restore viewport state of incoming tab from cache
        String nextId = navItems.get(newIndex).id;
        TabCacheManager.TabViewState newState = tabCache.getOrCreate(nextId);
        if (!newState.isLoaded) {
            initTabState(nextId, newState);
        }
        scrollOffset = newState.scrollOffset;
        targetScrollOffset = newState.targetScrollOffset;
        maxScroll = newState.maxScroll;
        isDraggingScrollbar = false;

        // Smoothly retract side panel while preserving its displayed content during slide-out
        if (sidePanelProgress > 0.001f && sidePanelType != SidePanelType.NONE) {
            closingPillarDetailId = selectedPillarDetailId;
            closeSidePanel();
        } else {
            selectedPillarDetailId = null;
            closingPillarDetailId = null;
        }

        if (newIndex != 0) {
            pendingPresetsAutoOpen = false;
        }
        if (newIndex == 0 && BuildscapeClientConfig.get().isSidePanelExpanded()) {
            openSidePanel(SidePanelType.PRESETS);
        }

        SidePanelLayout layout = calculateLayout();
        int tabH = 26;
        int tabGap = 3;
        int panelY = layout.panelY;
        int headerH = 24;
        int navY = panelY + 7 + headerH + 5;
        int startY = navY + 4;
        prevNavIndicatorY = currentIndicatorY > 0.01f ? currentIndicatorY : (startY + prevTabIndex * (tabH + tabGap));
        targetNavIndicatorY = startY + newIndex * (tabH + tabGap);
        navIndicatorStartTime = System.currentTimeMillis();
        tabTransitionStartTime = System.currentTimeMillis();
        isTabTransitioning = true;
    }

    private void initTabState(String tabId, TabCacheManager.TabViewState state) {
        switch (tabId) {
            case "items" -> {
                refreshInventoryItemsCache();
                rebuildActiveItemsCache();
                itemsListDirty = true;
            }
            case "pillars" -> {
                syncPillars();
            }
        }
        state.isLoaded = true;
        state.isDirty = false;
    }

    public void openSidePanel(SidePanelType type) {
        pendingPresetsAutoOpen = false;
        if (type == SidePanelType.PRESETS) {
            try {
                BuildscapeClientConfig.get().setSidePanelExpanded(true);
            } catch (Exception ignored) {}
        }
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

        // The pillar left behind gets its latest values sent before the new one is shown.
        for (UpdatePillarDataPacket packet : pillarUploadBatch.takeAllExcept(pillarId)) {
            sendPillarPacket(packet);
        }
        selectedPillarDetailId = pillarId;
        closingPillarDetailId = null;
        openSidePanel(SidePanelType.PILLAR_DETAIL);
    }

    public void closeSidePanel() {
        pendingPresetsAutoOpen = false;
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

    // Local client preferences.
    private boolean autoSnappingEnabled = true;
    private boolean strictBoundsCheck = true;

    // Legacy tab & widget support
    private AbstractConfigTab activeTab;
    private final List<GuiEventListener> widgets = new ArrayList<>();
    private final List<GuiEventListener> tabWidgets = new ArrayList<>();
    private static volatile BuildScapeConfigScreen currentInstance;
    private static final ConfigWarmupState CONFIG_WARMUP_STATE = new ConfigWarmupState();

    static final class ConfigWarmupState {
        private final AtomicBoolean running = new AtomicBoolean();
        private volatile boolean complete;

        boolean tryBegin() {
            return !complete && running.compareAndSet(false, true);
        }

        void finish(boolean succeeded) {
            if (succeeded) {
                complete = true;
            }
            running.set(false);
        }

        boolean isComplete() {
            return complete;
        }
    }

    public static void onPillarsSynced() {
        BuildScapeConfigScreen screen = currentInstance;
        if (screen != null) {
            screen.tabCache.invalidate("pillars");
        }
    }

    public static boolean isItemsCached() {
        return CONFIG_WARMUP_STATE.isComplete();
    }

    public static void warmupAsync() {
        if (!CONFIG_WARMUP_STATE.tryBegin()) return;
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            boolean succeeded = false;
            try {
                populateStaticItemCache();
                BuildscapeClientConfig.get();
                PresetsConfig.get();
                ConfigThemes.getAllThemes();
                ConfigBackgrounds.refreshCustomBackgrounds();
                succeeded = true;
            } catch (Throwable error) {
                com.kingodogo.buildscape.BuildScape.LOGGER.warn("Failed to warm up the config screen", error);
            } finally {
                CONFIG_WARMUP_STATE.finish(succeeded);
            }
        });
    }

    static boolean matchesConfigSearch(String id, String displayName, String query) {
        String normalizedQuery = normalizeSearchText(query);
        if (normalizedQuery.isEmpty()) {
            return true;
        }
        String normalizedId = normalizeSearchText(id);
        String normalizedName = normalizeSearchText(displayName);
        return normalizedId.contains(normalizedQuery) || normalizedName.contains(normalizedQuery);
    }

    private static String normalizeSearchText(String value) {
        return ConfigRenderWork.normalize(value);
    }

    static List<Double> resetSearchScrollOffsets(double scrollOffset, double targetScrollOffset) {
        return List.of(0.0D, 0.0D);
    }

    private void resetSearchViewport() {
        List<Double> offsets = resetSearchScrollOffsets(scrollOffset, targetScrollOffset);
        scrollOffset = offsets.get(0);
        targetScrollOffset = offsets.get(1);
    }

    private void startItemContentTransition() {
        itemContentTransitionStartTime = System.currentTimeMillis();
        itemContentTransitioning = true;
    }

    private void clearActiveSearch() {
        if (focusedSearchField == 1) {
            itemSearchFilter = "";
            itemsListDirty = true;
        } else if (focusedSearchField == 2) {
            tagSearchFilter = "";
            tagsListDirty = true;
        }
        searchTextSelected = false;
        resetSearchViewport();
        startItemContentTransition();
    }

    static List<String> discoverNamespaces(Collection<String> identifiers) {
        Set<String> namespaces = new TreeSet<>();
        if (identifiers == null) {
            return new ArrayList<>();
        }
        for (String identifier : identifiers) {
            if (identifier == null) {
                continue;
            }
            String normalizedIdentifier = identifier.startsWith("#") ? identifier.substring(1) : identifier;
            int separator = normalizedIdentifier.indexOf(':');
            if (separator > 0 && separator < normalizedIdentifier.length() - 1) {
                namespaces.add(normalizedIdentifier.substring(0, separator).toLowerCase(Locale.ROOT));
            }
        }
        return new ArrayList<>(namespaces);
    }

    static List<Integer> calculateSearchControlWidths(int availableWidth, int preferredNamespaceWidth, int gap) {
        int safeGap = Math.max(0, Math.min(gap, Math.max(0, availableWidth - 2)));
        int namespaceWidth = Math.max(1, Math.min(preferredNamespaceWidth, Math.max(1, (availableWidth - safeGap) / 2)));
        int searchWidth = Math.max(1, availableWidth - safeGap - namespaceWidth);
        return Arrays.asList(searchWidth, namespaceWidth, safeGap);
    }

    static double adjustConfigValue(double value, int direction, double fineStep, boolean accelerated,
                                    double minimum, double maximum, int decimalPlaces) {
        double step = fineStep * (accelerated ? 10.0D : 1.0D);
        double scale = Math.pow(10.0D, Math.max(0, decimalPlaces));
        double adjusted = Math.round((value + Integer.signum(direction) * step) * scale) / scale;
        return Math.max(minimum, Math.min(maximum, adjusted));
    }

    static void resetParticleProperties(PillarParticleConfig config) {
        config.particle_speed = 0.02D;
        config.particle_spread = 0.1D;
        config.particle_lifetime = 20;
        config.particle_density = 2;
        config.use_pattern = true;
        config.pattern = "ring";
        config.pattern_speed = 0.05D;
        config.pattern_spread = 0.05D;
        config.pattern_intensity = 1.0D;
    }

    static void resetParticleColors(PillarParticleConfig config) {
        config.particle_color.clear();
        config.particle_color.addAll(Arrays.asList(PillarParticleConfig.DEFAULT_COLORS));
        config.max_particle_color = 3;
    }

    private static synchronized void populateStaticItemCache() {
        if (itemsCached) return;
        List<Item> items = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item != null && item != Items.AIR) {
                items.add(item);
            }
        }
        if (items.isEmpty()) {
            for (int rawId = 1; rawId < 2500; rawId++) {
                Item item = getItemByRawId(rawId);
                if (item != null && item != Items.AIR) {
                    items.add(item);
                }
            }
        }
        Set<String> modSet = new LinkedHashSet<>();
        modSet.add("minecraft");
        modSet.add("buildscape");
        for (Item item : items) {
            ResourceLocation cid = ForgeRegistries.ITEMS.getKey(item);
            if (cid != null && cid.getNamespace() != null && !cid.getNamespace().isEmpty()) {
                modSet.add(cid.getNamespace());
            }
        }
        synchronized (STATIC_AVAILABLE_ITEMS) {
            STATIC_AVAILABLE_ITEMS.clear();
            STATIC_AVAILABLE_ITEMS.addAll(items);
            STATIC_AVAILABLE_MOD_NAMESPACES.clear();
            STATIC_AVAILABLE_MOD_NAMESPACES.addAll(modSet);
            itemsCached = true;
        }
    }

    private static synchronized void populateStaticTagCache() {
        if (tagsCached) return;
        Map<String, Integer> counts = new HashMap<>();
        List<String> holderTags = new ArrayList<>();
        synchronized (STATIC_AVAILABLE_ITEMS) {
            for (Item item : STATIC_AVAILABLE_ITEMS) {
                try {
                    item.builtInRegistryHolder().tags().forEach(tagKey -> {
                        String id = "#" + tagKey.location();
                        holderTags.add(id);
                        counts.put(id, counts.getOrDefault(id, 0) + 1);
                    });
                } catch (Exception ignored) {}
            }
        }
        List<String> tags = mergeDiscoveredTags(getAllItemTagIds(), holderTags);
        synchronized (STATIC_ALL_REGISTERED_TAGS) {
            STATIC_ALL_REGISTERED_TAGS.clear();
            STATIC_ALL_REGISTERED_TAGS.addAll(tags);
            STATIC_TAG_ITEM_COUNTS.clear();
            STATIC_TAG_ITEM_COUNTS.putAll(counts);
            tagsCached = shouldCacheTagDiscovery(tags);
        }
    }

    public BuildScapeConfigScreen(Screen parent) {
        super(ComponentHelper.translatable("buildscape.config.title"));
        this.parent = parent;

        this.openStartTime = 0L;
        try {
            BuildscapeClientConfig cfg = BuildscapeClientConfig.get();
            String savedTheme = cfg.getUiTheme();
            applyTheme(ConfigThemes.getById(savedTheme));
            ConfigBackgrounds.setBackgroundById(cfg.getUiBackground());
            animSpeedIndex = Math.max(0, Math.min(ANIM_SPEEDS.length - 1, cfg.getAnimationSpeedIndex()));
            this.selectedPresetIndex = presetIndexFor(PresetsConfig.get().getPresetKeys(), cfg.getLastPreset());
            int savedTab = cfg.getLastActiveTab();
            if (savedTab >= 0 && savedTab < 6) {
                this.selectedTabIndex = savedTab;
            }
            if (this.selectedTabIndex == 0 && cfg.isSidePanelExpanded()) {
                this.pendingPresetsAutoOpen = true;
                this.sidePanelType = SidePanelType.NONE;
                this.sidePanelProgress = 0.0f;
                this.isSidePanelOpening = false;
                this.isSidePanelClosing = false;
            } else {
                this.pendingPresetsAutoOpen = false;
                this.sidePanelType = SidePanelType.NONE;
                this.sidePanelProgress = 0.0f;
            }
            this.navExpanded = cfg.isNavbarExpanded();
            this.navAnimProgress = this.navExpanded ? 1.0f : 0.0f;
        } catch (Exception ignored) {}
        initNavItems();
        cacheAvailableItems();
    }

    static List<NavItem> createDefaultNavItems() {
        return List.of(
                new NavItem("items", "☵", NAV_ITEMS_TEXTURE, "Items & Tags", "Display items, tags, and presets"),
                new NavItem("particles", "✦", NAV_PARTICLES_TEXTURE, "Particles", "Particle speed, patterns, and colors"),
                new NavItem("pillars", "▲", NAV_PILLARS_TEXTURE, "Pillar IDs", "List and customize placed pillar blocks"),
                new NavItem("world", "⚙", NAV_WORLD_TEXTURE, "World Settings", "Griefing rules, tree felling, and stacking"),
                new NavItem("system", "ℹ", NAV_SYSTEM_TEXTURE, "System", "Client preferences and mod updates"),
                new NavItem("links", "↗", NAV_LINKS_TEXTURE, "Links", "Buildscape resources, releases, and support")
        );
    }

    private void initNavItems() {
        navItems.clear();
        navItems.addAll(createDefaultNavItems());
    }

    private void cacheAvailableItems() {
        if (!itemsCached) {
            warmupAsync();
            return;
        }
        if (availableItems.isEmpty() && !STATIC_AVAILABLE_ITEMS.isEmpty()) {
            synchronized (STATIC_AVAILABLE_ITEMS) {
                availableItems.clear();
                availableItems.addAll(STATIC_AVAILABLE_ITEMS);
                availableModNamespaces.clear();
                availableModNamespaces.addAll(STATIC_AVAILABLE_MOD_NAMESPACES);
            }
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private void rebuildActiveItemsCache() {
        collectActiveSelection(PillarParticleConfig.get().items, cachedActiveItems, cachedActiveTags);
        itemsListDirty = true;
        tagsListDirty = true;
    }

    static void collectActiveSelection(Collection<String> configured, Set<Item> items, Set<String> tags) {
        items.clear();
        tags.clear();
        for (String s : configured) {
            if (s.startsWith("#")) {
                tags.add(s);
            } else {
                ResourceLocation cid = ResourceLocation.tryParse(s);
                if (cid != null) {
                    Item item = ForgeRegistries.ITEMS.getValue(cid);
                    if (item != null && item != Items.AIR) {
                        items.add(item);
                    }
                }
            }
        }
    }

    /**
     * The server config arrived: show its selections, not what was clicked before it answered. Edits still
     * waiting to be uploaded are laid back over it so another player's save does not discard them.
     */
    public void onServerConfigSynced() {
        uploadBatch.reapply(PillarParticleConfig.get());
        rebuildActiveItemsCache();
    }

    /** The server refused an edit from this screen: show the same X feedback as a locally refused click. */
    public void onServerRefusal(EditRefusedPacket.Reason reason, ConfigSetting setting) {
        uploadBatch.refuse(setting, PillarParticleConfig.get());
        rebuildActiveItemsCache();
        readOnlyDeniedAt = System.currentTimeMillis();
        readOnlyDeniedX = rawMouseX;
        readOnlyDeniedY = rawMouseY;
    }

    private void loadRegisteredTags() {
        if (tagsCached) {
            if (allRegisteredTags.isEmpty()) {
                synchronized (STATIC_ALL_REGISTERED_TAGS) {
                    allRegisteredTags.clear();
                    allRegisteredTags.addAll(STATIC_ALL_REGISTERED_TAGS);
                    tagItemCounts.clear();
                    tagItemCounts.putAll(STATIC_TAG_ITEM_COUNTS);
                    refreshTagNamespaces();
                }
            }
            return;
        }
        populateStaticTagCache();
        if (tagsCached) {
            loadRegisteredTags();
        }
    }

    private void refreshTagNamespaces() {
        availableTagNamespaces.clear();
        availableTagNamespaces.addAll(discoverNamespaces(allRegisteredTags));
        if (tagSearchNamespaceIndex >= availableTagNamespaces.size()) {
            tagSearchNamespaceIndex = -1;
        }
    }

    private List<Item> getPlayerInventoryItems() {
        if (cachedInventoryItems.isEmpty()) {
            refreshInventoryItemsCache();
        }
        return cachedInventoryItems;
    }

    private void refreshInventoryItemsCache() {
        cachedInventoryItems.clear();
        inventoryTagsDirty = true;
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
        refreshResourceCaches();
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
                for (Item item : availableItems) {
                    if (cachedActiveItems.contains(item)) {
                        cachedFilteredItems.add(item);
                    }
                }
            }
        }
        String selectedNamespace = itemSearchNamespaceIndex >= 0 && itemSearchNamespaceIndex < availableModNamespaces.size()
                ? availableModNamespaces.get(itemSearchNamespaceIndex) : null;
        String query = normalizeSearchText(itemSearchFilter);
        if (query.isEmpty() && selectedNamespace == null) return;
        cachedFilteredItems.removeIf(item -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id == null || selectedNamespace != null && !selectedNamespace.equalsIgnoreCase(id.getNamespace())) {
                return true;
            }
            if (query.isEmpty()) return false;
            String searchableId = searchIds.computeIfAbsent(item, key -> normalizeSearchText(id.toString()));
            return !ConfigRenderWork.matches(searchableId, query, () -> searchNames.computeIfAbsent(item, key ->
                    normalizeSearchText(itemStackCache.computeIfAbsent(key, ItemStack::new).getHoverName().getString())));
        });
    }

    private final Set<String> cachedInventoryTags = new HashSet<>();

    private Set<String> getInventoryTagsSet() {
        if (!inventoryTagsDirty && !cachedInventoryTags.isEmpty()) {
            return cachedInventoryTags;
        }
        cachedInventoryTags.clear();
        for (Item item : getPlayerInventoryItems()) {
            try {
                item.builtInRegistryHolder().tags().forEach(tk -> cachedInventoryTags.add("#" + tk.location().toString()));
            } catch (Exception ignored) {}
        }
        inventoryTagsDirty = false;
        return cachedInventoryTags;
    }

    private List<String> getFilteredTagsList() {
        if (tagsCached && allRegisteredTags.isEmpty()) {
            synchronized (STATIC_ALL_REGISTERED_TAGS) {
                allRegisteredTags.clear();
                allRegisteredTags.addAll(STATIC_ALL_REGISTERED_TAGS);
                tagItemCounts.clear();
                tagItemCounts.putAll(STATIC_TAG_ITEM_COUNTS);
            }
        }
        if (!tagsCached) {
            loadRegisteredTags();
        }
        if (!tagsListDirty) {
            return cachedFilteredTags;
        }
        cachedFilteredTags.clear();
        PillarParticleConfig cfg = PillarParticleConfig.get();

        Set<String> invTagSet = (tagFilterMode == 1) ? getInventoryTagsSet() : null;
        String selectedNamespace = tagSearchNamespaceIndex >= 0 && tagSearchNamespaceIndex < availableTagNamespaces.size()
                ? availableTagNamespaces.get(tagSearchNamespaceIndex) : null;

        for (String tagId : allRegisteredTags) {
            String normalizedTag = tagId.startsWith("#") ? tagId.substring(1) : tagId;
            if (selectedNamespace != null && !normalizedTag.regionMatches(true, 0, selectedNamespace + ":", 0, selectedNamespace.length() + 1)) {
                continue;
            }
            if (!matchesConfigSearch(tagId, normalizedTag, tagSearchFilter)) {
                continue;
            }
            switch (tagFilterMode) {
                case 0 -> cachedFilteredTags.add(tagId);
                case 1 -> {
                    if (invTagSet != null && invTagSet.contains(tagId)) {
                        cachedFilteredTags.add(tagId);
                    }
                }
                case 2 -> {
                    if (cfg.items.contains(tagId)) {
                        cachedFilteredTags.add(tagId);
                    }
                }
            }
        }
        tagsListDirty = false;
        return cachedFilteredTags;
    }

    private ItemStack getStackForIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty() || identifier.startsWith("#")) {
            return null;
        }
        return stringToStackCache.computeIfAbsent(identifier, idStr -> {
            ResourceLocation cid = ResourceLocation.tryParse(idStr);
            if (cid != null) {
                Item item = ForgeRegistries.ITEMS.getValue(cid);
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
        long now = System.currentTimeMillis();
        if (now - lastPillarsSyncTime < PILLARS_SYNC_COOLDOWN_MS) {
            return;
        }
        lastPillarsSyncTime = now;
        ModMessages.INSTANCE.sendToServer(new RequestPillarIdsPacket());
    }

    public void setWrapperScreen(Object screen) {
        if (screen instanceof Screen s) {
            this.wrapperScreen = s;
        }
    }

    public Screen getWrapperScreen() {
        return wrapperScreen != null ? wrapperScreen : parent;
    }

    @Override
    protected void init() {
        uploadBatch.rebaseIfIdle(PillarParticleConfig.get());
        super.init();
        init(this.minecraft, this.width, this.height, this::addWidget, this::removeWidget);
    }

    public void init(Minecraft mc, int width, int height, Consumer<AbstractWidget> addWidget, Consumer<GuiEventListener> removeWidget) {
        currentInstance = this;
        this.openStartTime = 0L;
        this.isOpening = true;
        this.openProgress = 0.0f;

        this.addWidgetConsumer = addWidget;
        this.removeWidgetConsumer = removeWidget;

        this.widgets.clear();
        this.tabWidgets.clear();

        cacheAvailableItems();

        String activeTabId = (selectedTabIndex >= 0 && selectedTabIndex < navItems.size()) ? navItems.get(selectedTabIndex).id : "items";
        TabCacheManager.TabViewState state = tabCache.getOrCreate(activeTabId);
        if (!state.isLoaded) {
            initTabState(activeTabId, state);
        }

        SidePanelLayout layout = calculateLayout();
        int tabH = 26;
        int tabGap = 3;
        int panelY = layout.panelY;
        int headerH = 24;
        int navY = panelY + 7 + headerH + 5;
        int startY = navY + 4;
        currentIndicatorY = targetNavIndicatorY = prevNavIndicatorY = startY + selectedTabIndex * (tabH + tabGap);

        if (activeTab != null) {
            activeTab.init();
        }

    }

    @Override
    public void tick() {
        if (uploadBatch.isDue(System.currentTimeMillis())) {
            flushConfigUpload();
        }
        // Counter clicks go out when their pillar's wait ends, or once its detail panel is no longer selected.
        for (UpdatePillarDataPacket packet : pillarUploadBatch.takeDue(System.currentTimeMillis())) {
            sendPillarPacket(packet);
        }
        for (UpdatePillarDataPacket packet : pillarUploadBatch.takeAllExcept(selectedPillarDetailId)) {
            sendPillarPacket(packet);
        }
        if (availableItems.isEmpty() && itemsCached) {
            cacheAvailableItems();
            rebuildActiveItemsCache();
            loadRegisteredTags();
            itemsListDirty = true;
            tagsListDirty = true;
        }
    }

    // Adaptive GUI scaling.
    public float getGuiUiScale() {
        Minecraft mc = Minecraft.getInstance();
        double mcScale = mc.getWindow() != null ? mc.getWindow().getGuiScale() : 2.0;
        return GuiScaleHelper.calculateUiScale(width, height, mcScale);
    }

    public int getEffectiveWidth() {
        return GuiScaleHelper.getEffectiveWidth(width, getGuiUiScale());
    }

    public int getEffectiveHeight() {
        return GuiScaleHelper.getEffectiveHeight(height, getGuiUiScale());
    }

    // Legacy widget and tab compatibility.
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
        if (widget == null) return null;
        AbstractWidget nativeWidget = new AbstractWidget(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), ComponentHelper.literal("")) {
            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                widget.render(poseStack, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (widget.mouseClicked(mouseX, mouseY, button)) return true;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public boolean mouseReleased(double mouseX, double mouseY, int button) {
                if (widget.mouseReleased(mouseX, mouseY, button)) return true;
                return super.mouseReleased(mouseX, mouseY, button);
            }

            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
                if (widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
                return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }

            @Override
            public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
                if (widget.mouseScrolled(mouseX, mouseY, delta)) return true;
                return super.mouseScrolled(mouseX, mouseY, delta);
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (widget.keyPressed(keyCode, scanCode, modifiers)) return true;
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            @Override
            public boolean charTyped(char codePoint, int modifiers) {
                if (widget.charTyped(codePoint, modifiers)) return true;
                return super.charTyped(codePoint, modifiers);
            }

            @Override
            public void updateNarration(NarrationElementOutput narrationElementOutput) {}
        };
        this.addRenderableWidget(nativeWidget);
        tabWidgets.add(nativeWidget);
        return nativeWidget;
    }

    public void removeTabWidget(GuiEventListener widget) {
        if (widget != null && removeWidgetConsumer != null) {
            this.removeWidgetConsumer.accept(widget);
        }
        tabWidgets.remove(widget);
    }

    // Animation state updates.
    private float updateAnimations() {
        long now = System.currentTimeMillis();

        if (isOpening) {
            if (openStartTime <= 0L) {
                openStartTime = now;
            }
            int duration = getOpenAnimMs();
            float elapsed = (now - openStartTime) / (float) duration;
            if (elapsed >= 1.0f) {
                openProgress = 1.0f;
                isOpening = false;
                if (pendingPresetsAutoOpen) {
                    pendingPresetsAutoOpen = false;
                    openSidePanel(SidePanelType.PRESETS);
                }
            } else {
                openProgress = easeOutSine(elapsed);
            }
        }

        if (isClosing) {
            int duration = getCloseAnimMs();
            float elapsed = (now - closeStartTime) / (float) duration;
            if (elapsed >= 1.0f) {
                closeProgress = 0.0f;
                finishClosing();
                return 0.0f;
            } else {
                closeProgress = Math.max(0.0f, 1.0f - easeInCubic(elapsed));
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

    private void finishClosing() {
        if (currentInstance == this) {
            currentInstance = null;
        }
        if (activeTab != null) {
            activeTab.onClose();
        }
        if (closeToWorld) {
            closeToWorld = false;
            openScreen(null);
        } else {
            openScreen(parent);
        }
    }

    public void startClosing() {
        startClosing(false);
    }

    public void startClosing(boolean toWorld) {
        if (isClosing) return;
        this.pendingPresetsAutoOpen = false;
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
        return resolveClientPermissionLevel(-1,
                hasPlayerPermissions(mc.player, 2),
                hasPlayerPermissions(mc.player, PillarParticleConfig.CONFIG_PERMISSION_LEVEL)) >= 2;
    }

    public boolean hasConfigAccess() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && resolveClientPermissionLevel(-1,
                hasPlayerPermissions(mc.player, 2),
                hasPlayerPermissions(mc.player, PillarParticleConfig.CONFIG_PERMISSION_LEVEL))
                >= PillarParticleConfig.CONFIG_PERMISSION_LEVEL;
    }

    private boolean hasEditAccess(PillarParticleConfig.EditScope scope) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        PillarParticleConfig cfg = PillarParticleConfig.get();
        int permissionLevel = resolveClientPermissionLevel(-1,
                hasPlayerPermissions(mc.player, 2),
                hasPlayerPermissions(mc.player, PillarParticleConfig.CONFIG_PERMISSION_LEVEL));
        return PillarParticleConfig.canEdit(cfg.edit_permission_mode, permissionLevel, scope);
    }

    static boolean canModifyConfig(boolean hasOpAccess, boolean clientOnlyAction) {
        return clientOnlyAction || hasOpAccess;
    }

    static boolean isConfirmedAction(String pendingAction, String requestedAction) {
        return requestedAction != null && requestedAction.equals(pendingAction);
    }

    static String normalizePresetName(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) normalized = "Custom Preset";
        return normalized.substring(0, Math.min(32, normalized.length()));
    }

    static int moveSelection(int current, int direction, int count) {
        if (count <= 0) return 0;
        return Math.floorMod(current + direction, count);
    }

    static List<List<String>> removalBatches(List<String> ids) {
        List<List<String>> batches = new ArrayList<>();
        for (int from = 0; from < ids.size(); from += RemovePillarPacket.MAX_IDS) {
            batches.add(new ArrayList<>(ids.subList(from, Math.min(ids.size(), from + RemovePillarPacket.MAX_IDS))));
        }
        return batches;
    }

    /** Records an edit of one setting group; the upload follows {@link ConfigUploadBatch#DELAY_MS} after the last edit. */
    private void queueConfigUpload(ConfigSetting setting) {
        uploadBatch.edited(setting, PillarParticleConfig.get(), System.currentTimeMillis());
    }

    /** Holds the pillar's latest values; they are sent {@link PillarUploadBatch#DELAY_MS} after its last edit. */
    private void queuePillarUpload(PillarIdManager.PillarData data) {
        pillarUploadBatch.edited(data.id, new UpdatePillarDataPacket(data), System.currentTimeMillis());
    }

    /** The Save Pillar button: sends the pillar now, with anything it still had waiting. */
    private void sendPillarUploadNow(PillarIdManager.PillarData data) {
        pillarUploadBatch.take(data.id);
        sendPillarPacket(new UpdatePillarDataPacket(data));
    }

    /** Sends every waiting pillar edit: the screen is closing. */
    private void flushPillarUploads() {
        for (UpdatePillarDataPacket packet : pillarUploadBatch.takeAll()) {
            sendPillarPacket(packet);
        }
    }

    private void sendPillarPacket(UpdatePillarDataPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;
        ModMessages.INSTANCE.sendToServer(packet);
    }

    /** Sends the queued edits now: the wait ran out, or the screen is closing. */
    private void flushConfigUpload() {
        if (!uploadBatch.hasPending()) return;
        PillarParticleConfig config = PillarParticleConfig.get();
        // Edit access can be lost during the wait (deop, policy change): those edits are not sent, so put the
        // server's values back and give the refusal X instead of leaving a change on screen the server never got.
        boolean reverted = false;
        for (ConfigSetting setting : uploadBatch.pendingSettings()) {
            if (setting.isUploaded() && !canUpload(setting, config)) {
                uploadBatch.revert(setting, config);
                reverted = true;
            }
        }
        if (reverted) {
            flashDenied();
            rebuildActiveItemsCache();
        }
        if (!uploadBatch.hasPending()) return;
        Set<ConfigSetting> changed = uploadBatch.take();
        uploadBatch.measureFrom(config);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;
        persistServerConfig(config, changed);
    }

    private boolean canUpload(ConfigSetting setting, PillarParticleConfig config) {
        return hasEditAccess(setting.scope)
                && (setting.itemsFile ? config.canEditItems() : config.canEditProperties());
    }

    private void persistServerConfig(PillarParticleConfig config, Set<ConfigSetting> changed) {
        java.util.Set<ConfigSetting> settings =
                java.util.EnumSet.noneOf(ConfigSetting.class);
        for (ConfigSetting setting : changed) {
            if (setting.isUploaded() && canUpload(setting, config)) {
                settings.add(setting);
            }
        }
        if (settings.isEmpty()) return;
        for (UpdateConfigPacket chunk : UpdateConfigPacket.split(config, settings)) {
            ModMessages.INSTANCE.sendToServer(chunk);
        }
    }

    private void showHeaderNotice(String text) {
        headerNotice = text;
        headerNoticeUntil = System.currentTimeMillis() + ParticleRange.MIGRATION_WINDOW_MS;
    }

    private void flashDenied() {
        readOnlyDeniedAt = System.currentTimeMillis();
        readOnlyDeniedX = rawMouseX;
        readOnlyDeniedY = rawMouseY;
    }

    /**
     * The counter's next value. A value outside the screen's range is left alone by everything else; the first
     * click on it only says so, and the second sets it in range. Null means this click changed nothing.
     */
    private Double stepRangeValue(ParticleRange range, String scope, double current, int direction) {
        switch (rangeMigration.click(range, scope, current, System.currentTimeMillis())) {
            case WARN -> {
                showHeaderNotice(range.warning(current));
                flashDenied();
                return null;
            }
            case COMMIT -> {
                showHeaderNotice(range.migrated(current));
                return range.nearestInRange(current);
            }
            default -> {
                return adjustConfigValue(current, direction, range.step, range.decimals > 0 && Screen.hasControlDown(),
                        range.min, range.max, range.decimals);
            }
        }
    }

    /** Index of the remembered preset in the drawer's list; Default (0) when it is gone or none was applied. */
    static int presetIndexFor(List<String> keys, String remembered) {
        int index = remembered == null ? -1 : keys.indexOf(remembered);
        return Math.max(0, index);
    }

    private void rememberPreset(String key) {
        try {
            BuildscapeClientConfig.get().setLastPreset(key);
        } catch (Exception ignored) {
        }
    }

    private boolean confirmDestructiveAction(String action) {
        long now = System.currentTimeMillis();
        if (isConfirmedAction(pendingDestructiveAction, action) && now <= pendingDestructiveActionUntil) {
            pendingDestructiveAction = null;
            pendingDestructiveActionUntil = 0L;
            return true;
        }
        pendingDestructiveAction = action;
        pendingDestructiveActionUntil = now + 4000L;
        return false;
    }

    private boolean requireConfigModificationAccess() {
        String tabId = selectedTabIndex >= 0 && selectedTabIndex < navItems.size() ? navItems.get(selectedTabIndex).id : "";
        return requireConfigModificationAccess(settingForTab(tabId, sidePanelType == SidePanelType.PRESETS));
    }

    /** The operation a control on this tab performs; the server applies the same rule to the matching packet. */
    static ConfigSetting settingForTab(String tabId, boolean presetsPanel) {
        if (presetsPanel || "items".equals(tabId)) return ConfigSetting.ITEMS;
        if ("pillars".equals(tabId)) return ConfigSetting.PILLAR_EDIT;
        if ("world".equals(tabId)) return ConfigSetting.WORLD_RULES;
        return ConfigSetting.PARTICLES;
    }

    /** Direction of a tab-navigation key (left, right, tab), or 0 for any other key. */
    static int tabKeyDirection(int keyCode) {
        return keyCode == 263 ? -1 : keyCode == 262 || keyCode == 258 ? 1 : 0;
    }

    private boolean requireConfigModificationAccess(ConfigSetting setting) {
        return requireConfigModificationAccess(setting.scope);
    }

    static Component refusalText(PillarParticleConfig.EditScope scope, boolean permission, boolean filesEditable) {
        if (!permission) {
            return ComponentHelper.literal(scope == PillarParticleConfig.EditScope.POLICY
                    ? "Permission level 4 is required to change the edit policy."
                    : "This setting is locked by the server edit policy.");
        }
        return filesEditable ? null : new TranslatableComponent("buildscape.config.error.read_only");
    }

    static Component hoverRefusal(boolean hovered, PillarParticleConfig.EditPermissionMode mode, int permissionLevel,
                                  ConfigSetting setting, boolean filesEditable) {
        return hovered ? refusalText(setting.scope, setting.canEdit(mode, permissionLevel), filesEditable) : null;
    }

    private void hoverConfigAccess(boolean hovered, ConfigSetting setting,
                                   boolean items, boolean properties, boolean presets) {
        if (!hovered) return;
        PillarParticleConfig cfg = PillarParticleConfig.get();
        Minecraft mc = Minecraft.getInstance();
        int level = resolveClientPermissionLevel(-1, hasPlayerPermissions(mc.player, 2),
                hasPlayerPermissions(mc.player, PillarParticleConfig.CONFIG_PERMISSION_LEVEL));
        Component refusal = hoverRefusal(hovered, cfg.edit_permission_mode, level, setting,
                (!items || cfg.canEditItems()) && (!properties || cfg.canEditProperties())
                        && (!presets || PresetsConfig.get().canEdit()));
        if (refusal != null) hoveredRefusalTooltip = refusal;
    }

    private void hoverWorldOrPillarAccess(boolean hovered, String label) {
        String tabId = selectedTabIndex >= 0 && selectedTabIndex < navItems.size() ? navItems.get(selectedTabIndex).id : "";
        if ("pillars".equals(tabId) || "world".equals(tabId)
                && !"Creative Tree Breaker".equals(label) && !"Shulker Box Preview".equals(label)) {
            hoverConfigAccess(hovered, settingForTab(tabId, false), false, false, false);
        }
    }

    private boolean requireConfigModificationAccess(PillarParticleConfig.EditScope scope) {
        if (hasEditAccess(scope)) {
            return true;
        }
        readOnlyDeniedAt = System.currentTimeMillis();
        readOnlyDeniedX = rawMouseX;
        readOnlyDeniedY = rawMouseY;
        deferredTooltip = Collections.singletonList(refusalText(scope, false, true));
        deferredTooltipX = rawMouseX + 12;
        deferredTooltipY = rawMouseY;
        return false;
    }

    private boolean requireConfigFilesEditable(boolean items, boolean properties, boolean presets) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        if ((!items || cfg.canEditItems()) && (!properties || cfg.canEditProperties())
                && (!presets || PresetsConfig.get().canEdit())) return true;
        showConfigFilesReadOnly();
        return false;
    }

    private void showConfigFilesReadOnly() {
        readOnlyDeniedAt = System.currentTimeMillis();
        readOnlyDeniedX = rawMouseX;
        readOnlyDeniedY = rawMouseY;
        deferredTooltip = Collections.singletonList(refusalText(null, true, false));
        deferredTooltipX = rawMouseX + 12;
        deferredTooltipY = rawMouseY;
        com.kingodogo.buildscape.BuildScape.LOGGER.warn("Config edit refused because a config file is read-only");
    }

    private boolean showPresetReadOnlyFailure(PresetsConfig presetsCfg) {
        if (!"unreadable".equals(presetsCfg.getLastEditFailure())) return false;
        showConfigFilesReadOnly();
        return true;
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
        GuiScaleHelper.LayoutResult r = GuiScaleHelper.calculateLayout(effW, effH, sidePanelProgress, sidePanelType);
        return new SidePanelLayout(r.panelX, r.panelY, r.panelW, r.panelH,
                r.frameX, r.frameY, r.frameW, r.frameH, r.frameRight,
                r.sidePanelX, r.sidePanelY, r.sidePanelW, r.sidePanelH, false);
    }

    // Main screen rendering.
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBatched(poseStack, mouseX, mouseY, partialTick);
    }

    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        renderBatched(poseStackOrGraphics, mouseX, mouseY, partialTick);
    }

    private void renderBatched(Object graphics, int mouseX, int mouseY, float partialTick) {
        refreshResourceCaches();
        try {
            renderInternal(graphics, mouseX, mouseY, partialTick);
        } finally {
            ConfigGuiBatch.flush();
        }
    }

    private void renderInternal(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (isClosing && closeProgress <= 0.001f && closeStartTime > 0L) {
            return;
        }

        float alpha = updateAnimations();
        if (isClosing && closeProgress <= 0.001f) {
            return;
        }
        if (alpha <= 0.001f && !isOpening) return;
        alpha = Math.max(0.01f, Math.min(1.0f, alpha));

        // If closing and parent screen exists, render parent behind the shrinking panel
        if (isClosing && parent != null) {
            PoseStack parentPose = asPose(poseStackOrGraphics);
            parentPose.pushPose();
            try {
                parent.render(parentPose, -999, -999, partialTick);
            } catch (Exception ignored) {
            } finally {
                parentPose.popPose();
            }
        }

        this.rawMouseX = mouseX;
        this.rawMouseY = mouseY;
        this.deferredTooltip = null;
        this.hoveredRefusalTooltip = null;

        Font font = Minecraft.getInstance().font;

        // 1. Full-screen dimmed backdrop (draw before pose scaling to cover physical window)
        fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(COLOR_OVERLAY, alpha));

        // Compute B button center for closing zoom animation
        boolean applyClosingTransform = isClosing && closeStartTime > 0L;
        if (applyClosingTransform) {
            int bCenterX = width / 2 + 104 + 10;
            int bCenterY = height / 4 + 72 + 10;
            if (parent != null) {
                try {
                    for (GuiEventListener child : parent.children()) {
                        if (child instanceof AbstractWidget widget) {
                            if ("B".equals(widget.getMessage().getString())) {
                                bCenterX = widget.x + widget.getWidth() / 2;
                                bCenterY = widget.y + widget.getHeight() / 2;
                                break;
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }

            long now = System.currentTimeMillis();
            int duration = getCloseAnimMs();
            float t = Math.min(1.0f, (now - closeStartTime) / (float) duration);
            float ease = easeInCubic(t);

            // Zoom out scale: from 1.0 down to 0.04
            float zoomScale = Math.max(0.04f, 1.0f - 0.96f * ease);

            // Geometry center in screen coordinates
            SidePanelLayout baseLayout = calculateLayout();
            float panelCenterX = baseLayout.frameX + baseLayout.frameW / 2.0f;
            float panelCenterY = baseLayout.frameY + baseLayout.frameH / 2.0f;

            float curX = panelCenterX + (bCenterX - panelCenterX) * ease;
            float curY = panelCenterY + (bCenterY - panelCenterY) * ease;

            pushGuiPose(poseStackOrGraphics);
            translateGuiPose(poseStackOrGraphics, curX, curY, 0);
            scaleGuiPose(poseStackOrGraphics, zoomScale, zoomScale);
            translateGuiPose(poseStackOrGraphics, -panelCenterX, -panelCenterY, 0);

            // Mirror on ModelViewStack so item icons zoom with the panel
            PoseStack modelView = RenderSystem.getModelViewStack();
            modelView.pushPose();
            modelView.translate(curX, curY, 0);
            modelView.scale(zoomScale, zoomScale, 1.0f);
            modelView.translate(-panelCenterX, -panelCenterY, 0);
            RenderSystem.applyModelViewMatrix();
        }

        float uiScale = getGuiUiScale();
        boolean scaled = Math.abs(uiScale - 1.0f) > 0.001f;
        if (scaled) {
            pushGuiPose(poseStackOrGraphics);
            scaleGuiPose(poseStackOrGraphics, uiScale, uiScale);
            scissorUiScale = uiScale;
        }

        int effMouseX = scaled ? (int) Math.round(mouseX / uiScale) : mouseX;
        int effMouseY = scaled ? (int) Math.round(mouseY / uiScale) : mouseY;

        // 2. Geometry: Unified Responsive SidePanelLayout
        updateSidePanelAnimation();
        SidePanelLayout layout = calculateLayout();

        int offsetY = isClosing ? 0 : Math.round((1.0f - alpha) * 8.0f);
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

        // 2b. Connected Sliding Right Side Panel (sits to the right of the shrunk main chassis)
        if (sidePanelProgress > 0.001f && sidePanelType != SidePanelType.NONE) {
            if (sidePanelW > 0 && sidePanelH > 0) {
                enableGuiScissor(poseStackOrGraphics, sidePanelX, sidePanelY, sidePanelW, sidePanelH);
                renderSidePanel(poseStackOrGraphics, font, sidePanelX, sidePanelY, sidePanelW, sidePanelH, effMouseX, effMouseY, alpha);
                disableGuiScissor(poseStackOrGraphics);
            }
        }

        // 3. Layout Dimensions for Compartments
        int headerH = 24;
        int headerX = panelX + 7;
        int headerY = panelY + 7;
        int headerW = panelW - 14;

        updateNavAnimation();
        int navW = (int) Math.round(30 + 76 * navAnimProgress);
        int navX = panelX + 7;
        int navY = headerY + headerH + 5;
        int navH = panelH - headerH - 19;

        int contentX = navX + navW + 5;
        int contentY = navY;
        int contentW = (panelX + panelW - 7) - contentX;
        int contentH = navH;

        // 3b. Composite Master Chassis Frame (Single unified frame pass for outer chassis and inner basins)
        drawMasterChassisFrame(poseStackOrGraphics, frameX, frameY, frameW, frameH,
                headerX, headerY, headerW, headerH,
                navX, navY, navW, navH,
                contentX, contentY, contentW, contentH, alpha);

        // 4. Header Bar Contents (Forge 1.18.2 on top left, mod version on top right, Crest Badge, Close button)
        drawText(poseStackOrGraphics, font, "Forge 1.18.2", headerX + 8, headerY + 7, applyAlpha(COLOR_TEXT_WHITE, alpha));

        String versionTag = getModVersionTag();
        int tagW = font.width(versionTag);
        int versionTagX = headerX + headerW - tagW - 28;
        drawText(poseStackOrGraphics, font, versionTag, versionTagX, headerY + 7, applyAlpha(COLOR_TEXT_MUTED, alpha));

        // 2-Stage Theme Toggle Button (Dark 🌙 <-> Light ☀️)
        int themeToggleW = 44;
        int themeToggleH = 16;
        int themeToggleX = versionTagX - themeToggleW - 10;
        int themeToggleY = headerY + (headerH - themeToggleH) / 2;
        renderHeaderThemeToggle(poseStackOrGraphics, font, themeToggleX, themeToggleY, themeToggleW, themeToggleH, effMouseX, effMouseY, alpha);

        drawTopCrestBadge(poseStackOrGraphics, font, frameX, frameY, frameW, alpha);

        int closeBtnSize = 14;
        int closeBtnX = headerX + headerW - closeBtnSize - 5;
        int closeBtnY = headerY + 5;
        boolean hoverClose = effMouseX >= closeBtnX && effMouseX <= closeBtnX + closeBtnSize && effMouseY >= closeBtnY && effMouseY <= closeBtnY + closeBtnSize;
        drawBeveledButton(poseStackOrGraphics, closeBtnX, closeBtnY, closeBtnSize, closeBtnSize, hoverClose ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverClose, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✕", closeBtnX + closeBtnSize / 2, closeBtnY + 3, applyAlpha(hoverClose ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // 5. Render Left Navigation Strip
        renderSidenav(poseStackOrGraphics, font, navX, navY, navW, navH, effMouseX, effMouseY, alpha);

        // 6. Render Right Main Configuration Content Area
        renderContentArea(poseStackOrGraphics, font, contentX, contentY, contentW, contentH, frameRight, effMouseX, effMouseY, alpha);

        // 7. Fallback Color Picker Modal if explicitly toggled
        if (showColorModal) {
            hoveredRefusalTooltip = null;
            renderColorPickerModal(poseStackOrGraphics, font, effMouseX, effMouseY, alpha);
        }

        // Render legacy activeTab if any
        if (activeTab != null) {
            ConfigGuiBatch.flush();
            activeTab.render(poseStackOrGraphics, effMouseX, effMouseY, partialTick);
            activeTab.renderTooltips(poseStackOrGraphics, effMouseX, effMouseY, partialTick);
        }

        if (scaled) {
            popGuiPose(poseStackOrGraphics);
            scissorUiScale = 1.0f;
        }

        if (applyClosingTransform) {
            popGuiPose(poseStackOrGraphics);
            PoseStack modelView = RenderSystem.getModelViewStack();
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        if (!isClosing) {
            float deniedAlpha = readOnlyFeedbackAlpha(System.currentTimeMillis(), readOnlyDeniedAt) * alpha;
            if (deniedAlpha > 0.0F) {
                int pulse = 7 + Math.round(3.0F * deniedAlpha);
                int color = applyAlpha(0xFFFF3B30, deniedAlpha);
                drawBorder(poseStackOrGraphics, readOnlyDeniedX - pulse, readOnlyDeniedY - pulse, pulse * 2, pulse * 2, color);
                drawText(poseStackOrGraphics, font, "✕", readOnlyDeniedX - 3, readOnlyDeniedY - 4, color);
            }

            // Render deferred tooltip in unscaled screen coordinates
            if (hoveredRefusalTooltip != null) {
                deferredTooltip = deferredTooltip == null ? new ArrayList<>() : new ArrayList<>(deferredTooltip);
                deferredTooltip.add(hoveredRefusalTooltip);
                deferredTooltipX = rawMouseX + 12;
                deferredTooltipY = rawMouseY;
            }
            if (deferredTooltip != null && !deferredTooltip.isEmpty()) {
                renderComponentTooltip(poseStackOrGraphics, deferredTooltip, deferredTooltipX, deferredTooltipY);
            }
        }
    }

    // Unified chassis frame layout.
    private void drawMasterChassisFrame(Object poseStackOrGraphics, int frameX, int frameY, int frameW, int frameH,
                                        int headerX, int headerY, int headerW, int headerH,
                                        int navX, int navY, int navW, int navH,
                                        int contentX, int contentY, int contentW, int contentH, float alpha) {
        // 1. Outer Chassis Body & Metallic Border Frame
        drawBaseFrameBorder(poseStackOrGraphics, frameX, frameY, frameW, frameH, COLOR_PANEL_BG, true, alpha);

        // 2. Header Bar Basin
        drawBeveledComponent(poseStackOrGraphics, headerX, headerY, headerW, headerH, COLOR_HEADER_BG, alpha);

        // 3. Sidenav Container Basin
        drawBeveledComponent(poseStackOrGraphics, navX, navY, navW, navH, COLOR_INNER_DARK, alpha);

        // 4. Main Content Container Basin
        drawBeveledComponent(poseStackOrGraphics, contentX, contentY, contentW, contentH, COLOR_INNER_DARK, alpha);
    }

    // Side navigation rendering.
    private void renderSidenav(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
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
        fill(poseStackOrGraphics, indX, indY, indX + 2, indY + tabH, applyAlpha(COLOR_ACCENT, alpha));
        // Top edge sheen
        fill(poseStackOrGraphics, indX + 2, indY, indX + indW, indY + 1, applyAlpha(COLOR_ACCENT_GLOW, alpha));

        // 3. Draw icons and labels with proximity-based illumination
        for (int i = 0; i < navItems.size(); i++) {
            NavItem item = navItems.get(i);
            int tabY = startY + i * (tabH + tabGap);
            int tabW = w - 6;
            int tabX = x + 3;

            boolean hover = mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH;

            float dist = Math.abs(tabY - currentIndicatorY) / (float) (tabH + tabGap);
            float selectWeight = Math.max(0.0f, 1.0f - dist * 1.5f);

            int baseIconCol = hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED;
            int iconCol = lerpColor(baseIconCol, COLOR_TEXT_WHITE, selectWeight);

            if (item.iconTexture != null) {
                int iconCollapsedX = tabX + (tabW - 16) / 2;
                int iconExpandedX = tabX + 5;
                int iconX = (int) Math.round(iconCollapsedX + (iconExpandedX - iconCollapsedX) * navAnimProgress);
                int iconY = tabY + (tabH - 16) / 2;
                drawTintedTexture(poseStackOrGraphics, item.iconTexture, iconX, iconY, 16, 16, iconCol, alpha);
            } else {
                int iconCollapsedX = tabX + (tabW / 2) - (font.width(item.icon) / 2);
                int iconExpandedX = tabX + 6;
                int iconX = (int) Math.round(iconCollapsedX + (iconExpandedX - iconCollapsedX) * navAnimProgress);
                drawText(poseStackOrGraphics, font, item.icon, iconX, tabY + 9, applyAlpha(iconCol, alpha));
            }

            if (navAnimProgress > 0.15f) {
                float textAlpha = alpha * Math.min(1.0f, (navAnimProgress - 0.15f) / 0.85f);
                int baseTextCol = hover ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED;
                int textCol = lerpColor(baseTextCol, COLOR_TEXT_WHITE, selectWeight);
                int titleX = item.iconTexture != null ? tabX + 25 : tabX + 20;
                drawText(poseStackOrGraphics, font, item.title, titleX, tabY + 9, applyAlpha(textCol, textAlpha));
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

    // Content area rendering.
    private void renderContentArea(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int frameRight, int mouseX, int mouseY, float alpha) {
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

        int catHeaderH = 30;
        PillarParticleConfig cfg = PillarParticleConfig.get();
        if (validatedItems != cfg.items) {
            validatedItems = cfg.items;
            invalidItems = cfg.getInvalidItems();
        }
        Set<String> invalidItems = "items".equals(current.id) ? this.invalidItems : Collections.emptySet();
        boolean noticeShown = headerNotice != null && System.currentTimeMillis() <= headerNoticeUntil;
        int textY = y + (catHeaderH - 8) / 2;
        int presetBtnW = 32;
        int presetBtnH = 22;
        int presetBtnX = (x + w) - presetBtnW - 4;
        int presetBtnY = y + (catHeaderH - presetBtnH) / 2;
        int maxHeaderRight = "items".equals(current.id) ? (presetBtnX - 8) : (x + w - 12);
        if (current.iconTexture != null) {
            drawTintedTexture(poseStackOrGraphics, current.iconTexture, x + 8, y + (catHeaderH - 16) / 2, 16, 16, COLOR_TEXT_WHITE, tabAlpha);
            drawText(poseStackOrGraphics, font, current.title.toUpperCase(), x + 28, textY, applyAlpha(COLOR_TEXT_WHITE, tabAlpha));
            if (invalidItems.isEmpty() && !noticeShown) {
                int titleW = font.width(current.title.toUpperCase());
                int subX = x + 28 + titleW + 8;
                int maxSubW = maxHeaderRight - subX;
                if (maxSubW > 20) {
                    drawText(poseStackOrGraphics, font, trimTextToWidth(font, current.subtitle, maxSubW, true), subX, textY, applyAlpha(COLOR_TEXT_MUTED, tabAlpha));
                }
            }
        } else {
            drawText(poseStackOrGraphics, font, current.icon + "  " + current.title.toUpperCase(), x + 8, textY, applyAlpha(COLOR_TEXT_WHITE, tabAlpha));
            if (invalidItems.isEmpty() && !noticeShown) {
                int titleW = font.width(current.icon + "  " + current.title.toUpperCase());
                int subX = x + 8 + titleW + 8;
                int maxSubW = maxHeaderRight - subX;
                if (maxSubW > 20) {
                    drawText(poseStackOrGraphics, font, trimTextToWidth(font, current.subtitle, maxSubW, true), subX, textY, applyAlpha(COLOR_TEXT_MUTED, tabAlpha));
                }
            }
        }
        PillarParticleConfig.EditScope tabScope = settingForTab(current.id, false).scope;
        boolean restricted = ("items".equals(current.id) || "particles".equals(current.id)
                || "pillars".equals(current.id) || "world".equals(current.id)) && !hasEditAccess(tabScope)
                || ("items".equals(current.id) && !cfg.canEditItems())
                || ("particles".equals(current.id) && !cfg.canEditProperties());
        boolean limited = "particles".equals(current.id) && cfg.canEditProperties() && !hasEditAccess(ConfigSetting.PARTICLES.scope)
                && hasEditAccess(ConfigSetting.COLORS.scope);
        if (restricted || limited) {
            int badgeW = 62;
            int badgeX = maxHeaderRight - badgeW;
            float pulse = 0.80F + 0.20F * (float) Math.sin(System.currentTimeMillis() / 180.0D);
            drawBeveledButton(poseStackOrGraphics, badgeX, y + (catHeaderH - 14) / 2, badgeW, 14, limited ? COLOR_BTN_BG_ACTIVE : 0xFF351616, false, true, tabAlpha);
            drawCenteredText(poseStackOrGraphics, font, limited ? "LIMITED" : "READ ONLY", badgeX + badgeW / 2, textY,
                    applyAlpha(limited ? COLOR_ACCENT : 0xFFFF5A52, tabAlpha * pulse));
        }
        if (noticeShown) {
            int invalidW = invalidItems.isEmpty() ? 0 : font.width(new TranslatableComponent("buildscape.config.invalid_items", invalidItems.size())) + 10;
            int headerNoticeX = maxHeaderRight - (restricted || limited ? 68 : 0) - invalidW - font.width(headerNotice);
            drawText(poseStackOrGraphics, font, headerNotice, headerNoticeX, textY, applyAlpha(0xFFFFFF55, tabAlpha));
        }
        if (!invalidItems.isEmpty()) {
            Component notice = new TranslatableComponent("buildscape.config.invalid_items", invalidItems.size());
            int noticeW = font.width(notice);
            int noticeX = maxHeaderRight - (restricted || limited ? 68 : 0) - noticeW;
            drawText(poseStackOrGraphics, font, notice.getString(), noticeX, textY, applyAlpha(0xFFFFFF55, tabAlpha));
            if (mouseX >= noticeX && mouseX <= noticeX + noticeW && mouseY >= y + 4 && mouseY <= y + catHeaderH - 4) {
                deferredTooltip = Collections.singletonList(ComponentHelper.literal(String.join(", ", invalidItems)));
                deferredTooltipX = rawMouseX + 12;
                deferredTooltipY = rawMouseY;
            }
        }
        // Inline Presets Toggle Button (positioned cleanly inside items category header)
        if ("items".equals(current.id)) {
            boolean presetOpen = sidePanelProgress > 0.05f && sidePanelType == SidePanelType.PRESETS && !isSidePanelClosing;
            boolean hoverPresetBtn = mouseX >= presetBtnX && mouseX <= presetBtnX + presetBtnW
                    && mouseY >= presetBtnY && mouseY <= presetBtnY + presetBtnH;
            drawInlinePresetsTab(poseStackOrGraphics, presetBtnX, presetBtnY, presetBtnW, presetBtnH, hoverPresetBtn, presetOpen, tabAlpha);

            if (hoverPresetBtn && !presetOpen) {
                deferredTooltip = Collections.singletonList(ComponentHelper.literal("Presets (Click to Open)"));
                deferredTooltipX = rawMouseX + 12;
                deferredTooltipY = rawMouseY;
            } else if (hoverPresetBtn) {
                deferredTooltip = Collections.singletonList(ComponentHelper.literal("Presets (Click to Close)"));
                deferredTooltipX = rawMouseX + 12;
                deferredTooltipY = rawMouseY;
            }
        }

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
                case "world" -> renderWorldSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, getWorldSettingRowHeight(), rowGap, clipY, viewH, mouseX, mouseY, tabAlpha);
                case "system" -> renderSystemRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, clipY, viewH, mouseX, mouseY, tabAlpha);
                case "links" -> renderLinksRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, clipY, viewH, mouseX, mouseY, tabAlpha);
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
        if ("system".equals(tabId)) {
            return calculateSystemContentHeight(gap) + 6;
        }
        if ("world".equals(tabId)) {
            return calculateWorldContentHeight(gap) + 6;
        }
        int count = switch (tabId) {
            case "particles" -> 12;
            case "links" -> CONFIG_LINKS.size();
            default -> 0;
        };
        return count * (rowH + gap) + 6;
    }

    private void renderLinksRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap,
                                 int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        int curY = startY;
        for (ConfigLink link : CONFIG_LINKS) {
            renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH,
                    link.title(), link.description(), "Open ↗", link.icon(), mouseX, mouseY, alpha);
            curY += h + gap;
        }
    }

    // Items and tags panel rendering.
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
        int sub0TextCol = isSub0 ? COLOR_TEXT_WHITE : (hoverSub0 ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
        String sub0Text = "Display Items (" + cachedActiveItems.size() + ")";
        int sub0IconSize = 12;
        int sub0ContentW = sub0IconSize + 4 + font.width(sub0Text);
        int sub0StartX = sub0X + (subBtnW - sub0ContentW) / 2;
        drawTintedTexture(poseStackOrGraphics, SUBTAB_ITEMS_TEXTURE, sub0StartX, subY + (subBtnH - sub0IconSize) / 2, sub0IconSize, sub0IconSize, sub0TextCol, alpha);
        drawText(poseStackOrGraphics, font, sub0Text, sub0StartX + sub0IconSize + 4, subY + 4, applyAlpha(sub0TextCol, alpha));

        int sub1X = sub0X + subBtnW + 4;
        boolean isSub1 = (itemsSubTab == 1);
        boolean hoverSub1 = mouseX >= sub1X && mouseX <= sub1X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH;
        drawBeveledButton(poseStackOrGraphics, sub1X, subY, subBtnW, subBtnH, isSub1 ? COLOR_BTN_BG_ACTIVE : COLOR_BTN_BG, hoverSub1, isSub1, alpha);
        int sub1TextCol = isSub1 ? COLOR_TEXT_WHITE : (hoverSub1 ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED);
        String sub1Text = "Display Tags (" + cachedActiveTags.size() + ")";
        int sub1IconSize = 12;
        int sub1ContentW = sub1IconSize + 4 + font.width(sub1Text);
        int sub1StartX = sub1X + (subBtnW - sub1ContentW) / 2;
        drawTintedTexture(poseStackOrGraphics, SUBTAB_TAGS_TEXTURE, sub1StartX, subY + (subBtnH - sub1IconSize) / 2, sub1IconSize, sub1IconSize, sub1TextCol, alpha);
        drawText(poseStackOrGraphics, font, sub1Text, sub1StartX + sub1IconSize + 4, subY + 4, applyAlpha(sub1TextCol, alpha));

        curY += row1H + 2;

        // Compact filters, search, and namespace selector share one stable toolbar.
        int bar2H = 16;
        int pillH = getItemToolbarControlHeight();
        int pillY = curY + 1;
        int toolbarX = x + 6;
        int toolbarAvailableW = Math.max(120, w - 18);
        ItemToolbarGeometry toolbar = calculateItemToolbarGeometry(toolbarAvailableW);
        int pillW = toolbar.filterWidth();
        List<String> filterLabels = getCompactFilterLabels();
        for (int f = 0; f < 3; f++) {
            int px = toolbarX + f * (pillW + toolbar.gap());
            boolean isF = (itemsSubTab == 0 ? itemFilterMode : tagFilterMode) == f;
            boolean hoverPill = mouseX >= px && mouseX <= px + pillW && mouseY >= pillY && mouseY <= pillY + pillH;
            drawBeveledButton(poseStackOrGraphics, px, pillY, pillW, pillH, isF ? COLOR_BTN_BG_ACTIVE : COLOR_BTN_BG, hoverPill, isF, alpha);
            drawCenteredText(poseStackOrGraphics, font, trimTextToWidth(font, filterLabels.get(f), pillW - 4, false), px + pillW / 2, pillY + 3,
                    applyAlpha(isF ? COLOR_TEXT_WHITE : (hoverPill ? COLOR_TEXT_SILVER : COLOR_TEXT_MUTED), alpha));
        }

        int searchX = toolbarX + 3 * (pillW + toolbar.gap());
        int searchW = toolbar.searchWidth();
        int namespaceW = toolbar.namespaceWidth();
        int searchY = pillY;
        int searchH = getItemToolbarControlHeight();
        boolean itemSearch = itemsSubTab == 0;
        String searchValue = itemSearch ? itemSearchFilter : tagSearchFilter;
        boolean searchFocused = focusedSearchField == (itemSearch ? 1 : 2);
        drawBeveledButton(poseStackOrGraphics, searchX, searchY, searchW, searchH, COLOR_SLOT_BG, false, searchFocused, alpha);
        if (searchFocused) {
            drawBorder(poseStackOrGraphics, searchX, searchY, searchW, searchH, applyAlpha(COLOR_ACCENT, alpha));
            drawBorder(poseStackOrGraphics, searchX + 1, searchY + 1, Math.max(1, searchW - 2), Math.max(1, searchH - 2),
                    applyAlpha(COLOR_ACCENT_GLOW, alpha * 0.65F));
        }
        boolean caretVisible = searchFocused && (System.currentTimeMillis() / 450L) % 2L == 0L;
        String searchText = searchValue.isEmpty()
                ? (searchFocused ? "Type to search..." : "Search " + (itemSearch ? "items" : "tags") + "...")
                : searchValue + (caretVisible ? "|" : "");
        searchText = trimTextToWidth(font, searchText, Math.max(1, searchW - 10), !searchValue.isEmpty());
        if (searchFocused && searchTextSelected && !searchValue.isEmpty()) {
            int selectedWidth = Math.min(searchW - 8, font.width(trimTextToWidth(font, searchValue, Math.max(1, searchW - 10), true)));
            fill(poseStackOrGraphics, searchX + 4, searchY + 3, searchX + 5 + selectedWidth,
                    searchY + searchH - 2, applyAlpha(COLOR_ACCENT, alpha * 0.45F));
        }
        drawText(poseStackOrGraphics, font, searchText, searchX + 5, searchY + 4,
                applyAlpha(searchFocused ? COLOR_TEXT_WHITE : searchValue.isEmpty() ? COLOR_TEXT_MUTED : COLOR_TEXT_SILVER, alpha));

        List<String> namespaces = itemSearch ? availableModNamespaces : availableTagNamespaces;
        int namespaceIndex = itemSearch ? itemSearchNamespaceIndex : tagSearchNamespaceIndex;
        String namespaceLabel = getNamespaceSelectorLabel(namespaceIndex, namespaces);
        namespaceLabel = trimTextToWidth(font, namespaceLabel, Math.max(1, namespaceW - 8), false);
        int namespaceX = searchX + searchW + toolbar.gap();
        boolean namespaceHover = mouseX >= namespaceX && mouseX <= namespaceX + namespaceW && mouseY >= searchY && mouseY <= searchY + searchH;
        drawBeveledButton(poseStackOrGraphics, namespaceX, searchY, namespaceW, searchH, COLOR_BTN_BG, namespaceHover, namespaceIndex >= 0, alpha);
        drawCenteredText(poseStackOrGraphics, font, namespaceLabel, namespaceX + namespaceW / 2, searchY + 4,
                applyAlpha(namespaceHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, alpha));

        curY += bar2H + 3;

        // Viewport bounds for scrollable content strictly below fixed toolbars
        int transitionDuration = getTabTransitionAnimMs();
        float contentTransitionAlpha = itemContentTransitioning
                ? calculateItemContentTransitionAlpha(System.currentTimeMillis(), itemContentTransitionStartTime, transitionDuration) : 1.0F;
        if (contentTransitionAlpha >= 1.0F) itemContentTransitioning = false;
        int viewportY = curY + (itemContentTransitioning ? Math.round((1.0F - contentTransitionAlpha) * 4.0F) : 0);
        alpha *= contentTransitionAlpha;
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

            itemGridBatch.clear();
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
                    fill(poseStackOrGraphics, slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, applyAlpha(bg, alpha));

                    if (isSelected) {
                        int emTopLeft = applyAlpha(hover ? 0xFF86EFAC : 0xFF4ADE80, alpha);
                        int emBottomRight = applyAlpha(0xFF14532D, alpha);
                        fill(poseStackOrGraphics, slotX, slotY, slotX + slotSize, slotY + 1, emTopLeft);
                        fill(poseStackOrGraphics, slotX, slotY + 1, slotX + 1, slotY + slotSize - 1, emTopLeft);
                        fill(poseStackOrGraphics, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, emBottomRight);
                        fill(poseStackOrGraphics, slotX + slotSize - 1, slotY + 1, slotX + slotSize, slotY + slotSize - 1, emBottomRight);
                    } else if (hover) {
                        int hTopLeft = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
                        int hBottomRight = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
                        fill(poseStackOrGraphics, slotX, slotY, slotX + slotSize, slotY + 1, hTopLeft);
                        fill(poseStackOrGraphics, slotX, slotY + 1, slotX + 1, slotY + slotSize - 1, hTopLeft);
                        fill(poseStackOrGraphics, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, hBottomRight);
                        fill(poseStackOrGraphics, slotX + slotSize - 1, slotY + 1, slotX + slotSize, slotY + slotSize - 1, hBottomRight);
                    } else {
                        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
                        int light = applyAlpha(COLOR_SLOT_BORDER, alpha);
                        fill(poseStackOrGraphics, slotX, slotY, slotX + slotSize, slotY + 1, dark);
                        fill(poseStackOrGraphics, slotX, slotY + 1, slotX + 1, slotY + slotSize, dark);
                        fill(poseStackOrGraphics, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, light);
                        fill(poseStackOrGraphics, slotX + slotSize - 1, slotY, slotX + slotSize, slotY + slotSize, light);
                    }

                    ItemStack stack = itemStackCache.computeIfAbsent(item, ItemStack::new);
                    itemGridBatch.add(stack, slotX + 2, slotY + 2);

                    if (hover) {
                        hoveredStack = stack;
                        hoveredIsActive = isSelected;
                    }
                }
            }
            itemGridBatch.render(scissorUiScale, icon ->
                    renderGuiItem(poseStackOrGraphics, icon.stack(), icon.x(), icon.y()));
            disableGuiScissor(poseStackOrGraphics);

            renderCustomScrollbar(poseStackOrGraphics, scrollbarTrackX, viewportY, scrollbarW, viewportH, scrollOffset, maxScroll, mouseX, mouseY, alpha);

            if (hoveredStack != null) {
                hoverConfigAccess(true, ConfigSetting.ITEMS, true, false, false);
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

            int[] visible = ConfigRenderWork.visibleRows(viewportY - startY, viewportH, tagRowH + tagRowGap, tagsToDisplay.size());
            for (int ti = visible[0]; ti < visible[1]; ti++) {
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
                    hoverConfigAccess(true, ConfigSetting.ITEMS, true, false, false);
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

    private static String trimTextToWidth(Font font, String text, int maximumWidth, boolean keepEnd) {
        String result = text == null ? "" : text;
        while (result.length() > 1 && font.width(result) > maximumWidth) {
            result = keepEnd ? result.substring(1) : result.substring(0, result.length() - 1);
        }
        return result;
    }

    // Particle controls and color customization.
    private void renderParticleSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        for (int row = 0; row < 12; row++) {
            int rowY = startY + row * (h + gap);
            boolean hovered = row == 0 ? checkToggleClick(mouseX, mouseY, x, rowY, w, h, clipY, clipH)
                    : row == 1 ? checkCycleClick(mouseX, mouseY, x, rowY, w, h, clipY, clipH) != 0
                    : row < 9 ? checkCounterClick(mouseX, mouseY, x, rowY, w, h, clipY, clipH) != 0
                    : row == 9 ? paletteControlHovered(mouseX, mouseY, x, rowY, w, h, clipY, clipH)
                    : checkButtonClick(mouseX, mouseY, x, rowY, w, h, clipY, clipH,
                            getConfigRowButtonWidth(font, row == 10 ? "↻ Reset Properties" : "↻ Reset Colors"));
            hoverConfigAccess(hovered, row == 9 || row == 11
                    ? ConfigSetting.COLORS : ConfigSetting.PARTICLES,
                    false, true, false);
        }
        int curY = startY;

        // 1. Mode Toggle
        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Use Particle Pattern", "Enable animated particle patterns", cfg.use_pattern, mouseX, mouseY, alpha);
        curY += h + gap;

        // 2. Pattern Cycle
        String patternDisplay = cfg.pattern.substring(0, 1).toUpperCase() + cfg.pattern.substring(1);
        renderConfigRowCycle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Pattern", "Select particle movement animation", patternDisplay, mouseX, mouseY, alpha);
        curY += h + gap;

        // 3. Pattern Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Speed", "Animation rotation and movement speed", ParticleRange.PATTERN_SPEED, cfg.pattern_speed, mouseX, mouseY, alpha);
        curY += h + gap;

        // 4. Pattern Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Spread", "Particle spread radius", ParticleRange.PATTERN_SPREAD, cfg.pattern_spread, mouseX, mouseY, alpha);
        curY += h + gap;

        // 5. Pattern Intensity
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pattern Intensity", "Particle spawn rate and density multiplier", ParticleRange.PATTERN_INTENSITY, cfg.pattern_intensity, mouseX, mouseY, alpha);
        curY += h + gap;

        // 6. Basic Particle Speed
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Speed", "Base vertical speed of particles", ParticleRange.PARTICLE_SPEED, cfg.particle_speed, mouseX, mouseY, alpha);
        curY += h + gap;

        // 7. Basic Particle Spread
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Spread", "Base spread distance of particles", ParticleRange.PARTICLE_SPREAD, cfg.particle_spread, mouseX, mouseY, alpha);
        curY += h + gap;

        // 8. Particle Lifetime
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Lifetime", "Particle duration in ticks before fading", ParticleRange.PARTICLE_LIFETIME, cfg.particle_lifetime, mouseX, mouseY, alpha);
        curY += h + gap;

        // 9. Particle Density
        renderConfigRowCounter(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Density", "Particle count emitted per pillar", ParticleRange.PARTICLE_DENSITY, cfg.particle_density, mouseX, mouseY, alpha);
        curY += h + gap;

        // 10. Swatches & Active Colors
        renderConfigRowPalette(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Particle Colors", cfg.particle_color, cfg.max_particle_color, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Reset Properties", "Restore default particle behavior", "↻ Reset Properties", mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Reset Colors", "Restore default particle colors", "↻ Reset Colors", mouseX, mouseY, alpha);
    }

    // Server-authoritative pillar table.
    private void renderPillarsPanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int catHeaderH, int mouseX, int mouseY, float alpha) {
        renderPillarTablePanel(poseStackOrGraphics, font, x, y, w, h, catHeaderH, mouseX, mouseY, alpha);
    }

    private void renderPillarTablePanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int catHeaderH, int mouseX, int mouseY, float alpha) {
        PillarIdManager manager = PillarIdManager.getClient();
        Collection<PillarIdManager.PillarData> pillars = manager.getAllData();
        List<PillarIdManager.PillarData> pillarList = new ArrayList<>(pillars);
        pillarList.sort(Comparator.comparing(d -> d.id != null ? d.id : ""));

        int curY = y + catHeaderH + 4;

        // Fixed action toolbar.
        int bar1H = 20;
        int btnH = 14;
        int barY = curY + (bar1H - btnH) / 2;

        // [ ⟳ Reload ]
        int relW = Math.max(60, font.width("⟳ Reload") + 14);
        int relX = x + 6;
        boolean hoverRel = mouseX >= relX && mouseX <= relX + relW && mouseY >= barY && mouseY <= barY + btnH;
        drawBeveledButton(poseStackOrGraphics, relX, barY, relW, btnH, hoverRel ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverRel, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "⟳ Reload", relX + relW / 2, barY + 3, applyAlpha(hoverRel ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // [ ✕ Remove Selected (<N>) ]
        int selCount = selectedPillarIds.size();
        int remSelW = Math.max(105, font.width("✕ Remove Selected") + 14);
        int remSelX = relX + relW + 4;
        boolean hasSel = selCount > 0;
        boolean hoverRemSel = hasSel && mouseX >= remSelX && mouseX <= remSelX + remSelW && mouseY >= barY && mouseY <= barY + btnH;
        hoverConfigAccess(hoverRemSel, ConfigSetting.PILLAR_EDIT, false, false, false);
        int remSelBg = hasSel ? (hoverRemSel ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414)) : COLOR_BTN_BG;
        drawBeveledButton(poseStackOrGraphics, remSelX, barY, remSelW, btnH, remSelBg, hoverRemSel, false, alpha);
        boolean confirmingSelected = isConfirmedAction(pendingDestructiveAction, "remove:selected")
                && System.currentTimeMillis() <= pendingDestructiveActionUntil;
        String remSelLabel = confirmingSelected ? "Confirm Remove" : hasSel ? ("✕ Remove (" + selCount + ")") : "✕ Remove Selected";
        int remSelTextCol = hasSel ? (hoverRemSel ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050) : COLOR_TEXT_MUTED;
        drawCenteredText(poseStackOrGraphics, font, remSelLabel, remSelX + remSelW / 2, barY + 3, applyAlpha(remSelTextCol, alpha));

        // [ ✕ Remove All ]
        int remAllW = Math.max(75, font.width("✕ Remove All") + 14);
        int remAllX = remSelX + remSelW + 4;
        boolean hasAny = !pillarList.isEmpty();
        boolean hoverRemAll = hasAny && mouseX >= remAllX && mouseX <= remAllX + remAllW && mouseY >= barY && mouseY <= barY + btnH;
        hoverConfigAccess(hoverRemAll, ConfigSetting.PILLAR_EDIT, false, false, false);
        int remAllBg = hasAny ? (hoverRemAll ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414)) : COLOR_BTN_BG;
        drawBeveledButton(poseStackOrGraphics, remAllX, barY, remAllW, btnH, remAllBg, hoverRemAll, false, alpha);
        int remAllTextCol = hasAny ? (hoverRemAll ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050) : COLOR_TEXT_MUTED;
        boolean confirmingAll = isConfirmedAction(pendingDestructiveAction, "remove:all")
                && System.currentTimeMillis() <= pendingDestructiveActionUntil;
        drawCenteredText(poseStackOrGraphics, font, confirmingAll ? "Confirm All" : "✕ Remove All", remAllX + remAllW / 2, barY + 3, applyAlpha(remAllTextCol, alpha));

        int resetW = Math.max(78, font.width("↻ Reset Selected") + 14);
        int resetX = remAllX + remAllW + 4;
        boolean hoverReset = hasSel && mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= barY && mouseY <= barY + btnH;
        hoverConfigAccess(hoverReset, ConfigSetting.PILLAR_EDIT, false, false, false);
        drawBeveledButton(poseStackOrGraphics, resetX, barY, resetW, btnH, COLOR_BTN_BG, hoverReset, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "↻ Reset Selected", resetX + resetW / 2, barY + 3,
                applyAlpha(hasSel ? (hoverReset ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER) : COLOR_TEXT_MUTED, alpha));

        curY += bar1H + 2;

        // Fixed table headers.
        int headH = 16;
        int scrollbarW = 6;
        int scrollbarTrackX = x + w - scrollbarW - 5;
        int availW = (scrollbarTrackX - 6) - (x + 6);
        int colX = x + 6;

        drawBeveledButton(poseStackOrGraphics, colX, curY, availW, headH, COLOR_HEADER_BG, false, true, alpha);

        int chkW = 16;
        int iconColW = 38;
        int colorsColW = 68;
        int dimColW = 58;
        int coordsColW = 96;
        int actionsColW = 58;
        int gap = 4;
        int idColW = Math.max(55, availW - (chkW + iconColW + colorsColW + dimColW + coordsColW + actionsColW + (gap * 6)));

        // Header Checkbox
        boolean allChecked = hasAny && selectedPillarIds.size() == pillarList.size();
        boolean someChecked = !selectedPillarIds.isEmpty() && !allChecked;
        int chkBoxSize = 10;
        int chkX = colX + (chkW - chkBoxSize) / 2;
        int chkY = curY + (headH - chkBoxSize) / 2;
        drawBeveledButton(poseStackOrGraphics, chkX, chkY, chkBoxSize, chkBoxSize, COLOR_SLOT_BG, false, allChecked, alpha);
        if (allChecked) {
            fill(poseStackOrGraphics, chkX + 2, chkY + 2, chkX + chkBoxSize - 2, chkY + chkBoxSize - 2, applyAlpha(0xFF22C55E, alpha));
        } else if (someChecked) {
            fill(poseStackOrGraphics, chkX + 2, chkY + (chkBoxSize / 2) - 1, chkX + chkBoxSize - 2, chkY + (chkBoxSize / 2) + 1, applyAlpha(0xFF22C55E, alpha));
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

        // Scrollable pillar rows.
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
            int[] visible = ConfigRenderWork.visibleRows(viewportY - startY, viewportH, rowH + rowGap, pillarList.size());
            for (int r = visible[0]; r < visible[1]; r++) {
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
                    fill(poseStackOrGraphics, rChkX + 2, rChkY + 2, rChkX + chkBoxSize - 2, rChkY + chkBoxSize - 2, applyAlpha(0xFF22C55E, alpha));
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
                        renderGuiItem(poseStackOrGraphics, pillarTypeStack, iconStartX, iconY);
                    }
                    if (!dispStack.isEmpty()) {
                        renderGuiItem(poseStackOrGraphics, dispStack, iconStartX + 18, iconY);
                    }
                } else {
                    drawCenteredText(poseStackOrGraphics, font, "—", rcX + iconColW / 2, rowY + 8, applyAlpha(COLOR_TEXT_MUTED, alpha));
                }
                rcX += iconColW + gap;

                // COLORS Column (Consistent same-sized mini swatches)
                List<String> colors = (data.dyeColors != null && !data.dyeColors.isEmpty()) ? data.dyeColors : PillarParticleConfig.get().particle_color;
                int numSwatches = Math.min(7, colors != null ? colors.size() : 0);
                if (numSwatches > 0) {
                    int swSize = 7;
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
                        fill(poseStackOrGraphics, swX, swY, swX + swSize, swY + swSize, applyAlpha(colInt, alpha));
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

    // Per-pillar customization view.
    private void renderPillarDetailPanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int catHeaderH, int mouseX, int mouseY, float alpha) {
        PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(selectedPillarDetailId);
        if (data == null) {
            selectedPillarDetailId = null;
            return;
        }

        PillarParticleConfig globalCfg = PillarParticleConfig.get();

        int curY = y + catHeaderH + 4;

        // Fixed detail header.
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
        hoverConfigAccess(hoverSave, ConfigSetting.PILLAR_EDIT, false, false, false);
        drawBeveledGreenButton(poseStackOrGraphics, saveX, barY, saveW, btnH, hoverSave, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Save Pillar", saveX + saveW / 2, barY + 3, applyAlpha(hoverSave ? 0xFFFFFFFF : COLOR_TOGGLE_ON_TEXT, alpha));

        curY += bar1H + 3;

        // Scrollable detail rows.
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
        renderConfigRowCounter(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pattern Speed", "Animation rotation and movement speed", ParticleRange.PATTERN_SPEED, speed, mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 4: Pattern Spread Counter (fallback to global pattern_spread)
        double spread = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
        renderConfigRowCounter(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pattern Spread", "Particle spread radius", ParticleRange.PATTERN_SPREAD, spread, mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 5: Pattern Intensity Counter (fallback to global pattern_intensity)
        double intensity = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
        renderConfigRowCounter(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pattern Intensity", "Particle spawn rate and density multiplier", ParticleRange.PATTERN_INTENSITY, intensity, mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 6: Per-Pillar Color Palette (fallback to global particle_color & max_particle_color)
        List<String> colors = new ArrayList<>(data.dyeColors != null && !data.dyeColors.isEmpty() ? data.dyeColors : globalCfg.particle_color);
        while (colors.size() < 7) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(7, Math.max(1, colors.size())));
        renderConfigRowPalette(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Pillar Colors", colors, maxColors, mouseX, mouseY, alpha);
        rY += rowH + rowGap;

        // Row 7: Save & Sync Pillar Configuration
        renderConfigRowButton(poseStackOrGraphics, font, rowX, rY, rowW, rowH, viewportY, viewportH, "Save Changes", "Save and sync pillar settings to server", "✓ Save", mouseX, mouseY, alpha);

        disableGuiScissor(poseStackOrGraphics);

        renderCustomScrollbar(poseStackOrGraphics, scrollbarTrackX, viewportY, scrollbarW, viewportH, scrollOffset, maxScroll, mouseX, mouseY, alpha);
    }

    // World setting rows.
    private void renderWorldSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        UUID playerUuid = getPlayerUuid();
        Level level = getLevel();

        boolean treeBreaker = CosmeticsConfig.get().getCreativeTreeBreaker(playerUuid);
        boolean shulkerPeek = CosmeticsConfig.get().getShulkerPreview(playerUuid);
        boolean fastLeaves = level != null ? getGameRuleBoolean(level, ModGameRules.FAST_LEAF_DECAY, ModGameRules.clientFastLeafDecay) : ModGameRules.clientFastLeafDecay;
        boolean endermanGrief = level != null ? getGameRuleBoolean(level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing) : ModGameRules.clientDisableEndermanGriefing;
        boolean creeperGrief = level != null ? getGameRuleBoolean(level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing) : ModGameRules.clientDisableCreeperGriefing;
        boolean ghastGrief = level != null ? getGameRuleBoolean(level, ModGameRules.DISABLE_GHAST_GRIEFING, ModGameRules.clientDisableGhastGriefing) : ModGameRules.clientDisableGhastGriefing;
        boolean cakeStack = level != null ? getGameRuleBoolean(level, ModGameRules.IS_CAKE_STACK, ModGameRules.clientCakeStacking) : ModGameRules.clientCakeStacking;
        boolean waterStack = level != null ? getGameRuleBoolean(level, ModGameRules.IS_WATER_BOTTLE_STACK, ModGameRules.clientWaterBottleStacking) : ModGameRules.clientWaterBottleStacking;

        int curY = startY;

        renderSectionDivider(poseStackOrGraphics, font, x, curY, w, "Player Settings", alpha);
        curY += 14;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Creative Tree Breaker", "Break entire trees at once in Creative mode.", treeBreaker, mouseX, mouseY, alpha);
        curY += h + gap;

        renderConfigRowToggle(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Shulker Box Preview", "Preview shulker box inventory contents on hover.", shulkerPeek, mouseX, mouseY, alpha);
        curY += h + gap;

        renderSectionDivider(poseStackOrGraphics, font, x, curY, w, "World Rules", alpha);
        curY += 14;

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

    private void renderSectionDivider(Object poseStackOrGraphics, Font font, int x, int y, int w, String label, float alpha) {
        drawText(poseStackOrGraphics, font, label.toUpperCase(Locale.ROOT), x + 5, y + 2, applyAlpha(COLOR_ACCENT, alpha));
        int lineX = x + 12 + font.width(label.toUpperCase(Locale.ROOT));
        if (lineX < x + w - 4) {
            fill(poseStackOrGraphics, lineX, y + 6, x + w - 4, y + 7, applyAlpha(COLOR_ROW_BORDER, alpha));
        }
    }

    // System setting rows.
    private void renderSystemRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        int curY = startY;
        int systemRowH = getSystemSettingRowHeight();

        renderConfigRowCycleDetailed(poseStackOrGraphics, font, x, curY, w, systemRowH, clipY, clipH, "UI Theme", "Visual color palette for configuration interface", currentTheme.displayName, mouseX, mouseY, alpha);
        curY += systemRowH + gap;

        renderConfigRowCycleDetailed(poseStackOrGraphics, font, x, curY, w, systemRowH, clipY, clipH, "UI Background", "Decorative backdrop pattern applied over the theme", ConfigBackgrounds.getCurrentBackground().displayName, mouseX, mouseY, alpha);
        curY += systemRowH + gap;

        renderConfigRowCycleDetailed(poseStackOrGraphics, font, x, curY, w, systemRowH, clipY, clipH, "Screen Animation", "Smooth UI transitions and animation speed", ANIM_SPEEDS[animSpeedIndex], mouseX, mouseY, alpha);
        curY += systemRowH + gap;

        PillarParticleConfig.EditPermissionMode policy = PillarParticleConfig.get().edit_permission_mode;
        hoverConfigAccess(checkCycleClick(mouseX, mouseY, x, curY, w, systemRowH, clipY, clipH) != 0,
                ConfigSetting.POLICY, false, true, false);
        renderConfigRowCycleDetailed(poseStackOrGraphics, font, x, curY, w, systemRowH, clipY, clipH, "Edit Permissions",
                "Choose Strict (level 4), Standard (limited level 2), or Open access", capitalize(policy.name().toLowerCase(Locale.ROOT)), mouseX, mouseY, alpha);
    }

    // Draggable saturation, brightness, and hue picker.
    private void renderColorPickerModal(Object poseStackOrGraphics, Font font, int mouseX, int mouseY, float alpha) {
        int effW = getEffectiveWidth();
        int effH = getEffectiveHeight();
        fill(poseStackOrGraphics, 0, 0, effW, effH, applyAlpha(0xA0000000, alpha));

        int modalW = 340;
        int modalH = 196;
        int modalX = (effW - modalW) / 2;
        int modalY = (effH - modalH) / 2;

        drawBaseFrameBorder(poseStackOrGraphics, modalX, modalY, modalW, modalH, COLOR_PANEL_BG, alpha);

        int headH = 24;
        fill(poseStackOrGraphics, modalX + 7, modalY + 7, modalX + modalW - 7, modalY + headH, applyAlpha(COLOR_HEADER_BG, alpha));
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

        ConfigGuiBatch.colorField(asPose(poseStackOrGraphics), gradBoxX, gradBoxY,
                gradBoxW, gradBoxH, hsbToRgb(pickerHue, 1, 1), alpha);
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
            fill(poseStackOrGraphics, hueX, hueY + y, hueX + hueW, hueY + y + hh, applyAlpha(col, alpha));
        }
        drawBorder(poseStackOrGraphics, hueX - 1, hueY - 1, hueW + 2, hueH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        int curHueY = hueY + (int) ((pickerHue / 360.0f) * hueH);
        fill(poseStackOrGraphics, hueX - 2, curHueY - 1, hueX + hueW + 2, curHueY + 2, applyAlpha(COLOR_TEXT_WHITE, alpha));
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
        fill(poseStackOrGraphics, previewX, previewY, previewX + previewSize, previewY + previewSize, applyAlpha(currentColorInt, alpha));
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
            fill(poseStackOrGraphics, px, py, px + swatchSize, py + swatchSize, applyAlpha(PALETTE_PRESETS[p], alpha));
            drawBorder(poseStackOrGraphics, px, py, swatchSize, swatchSize, applyAlpha(0xFF000000, alpha));
        }

        // 5. Apply Button
        int applyW = 90;
        int applyH = 16;
        int applyX = modalX + modalW - applyW - 16;
        int applyY = modalY + modalH - applyH - 12;
        boolean hoverApply = mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + applyH;
        hoverConfigAccess(hoverApply, isEditingPillarColor ? ConfigSetting.PILLAR_EDIT
                : ConfigSetting.COLORS, false, !isEditingPillarColor, false);
        drawBeveledGreenButton(poseStackOrGraphics, applyX, applyY, applyW, applyH, hoverApply, alpha);
        int applyTextCol = hoverApply ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT;
        drawCenteredText(poseStackOrGraphics, font, "✓ Apply Color", applyX + applyW / 2, applyY + 4, applyAlpha(applyTextCol, alpha));
    }

    // Shared border and bevel rendering.
    private void drawBaseFrameBorder(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        drawBaseFrameBorder(poseStackOrGraphics, x, y, w, h, bgRgb, false, alpha);
    }

    private void drawBaseFrameBorder(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, boolean hasCrestNotch, float alpha) {
        // 1. Outer Chassis Body Fill (Solid metallic chassis backing)
        fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int band = applyAlpha(COLOR_CHASSIS_BAND, alpha);
        int bevelShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int bevelHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);

        int crestW = 100;
        int crestX = x + (w - crestW) / 2;

        // 2. Outermost 1px Black Border
        if (hasCrestNotch) {
            fill(poseStackOrGraphics, x, y, crestX + 1, y + 1, black);
            fill(poseStackOrGraphics, crestX + crestW - 1, y, x + w, y + 1, black);
        } else {
            fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        }
        fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        fill(poseStackOrGraphics, x, y, x + 1, y + h, black);
        fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);

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
        fill(poseStackOrGraphics, x + 1, y + h - 2, x + w - 1, y + h - 2, shadow);
        fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, shadow);

        // 4. Outer Chassis Band (offset 2 to 3)
        for (int o = 2; o <= 3; o++) {
            if (hasCrestNotch) {
                if (crestX > x + o) fill(poseStackOrGraphics, x + o, y + o, crestX, y + o + 1, band);
                if (x + w - o > crestX + crestW) fill(poseStackOrGraphics, crestX + crestW, y + o, x + w - o, y + o + 1, band);
            } else {
                fill(poseStackOrGraphics, x + o, y + o, x + w - o, y + o + 1, band);
            }
            fill(poseStackOrGraphics, x + o, y + h - o - 1, x + w - o, y + h - o, band);
            fill(poseStackOrGraphics, x + o, y + o, x + o + 1, y + h - o, band);
            fill(poseStackOrGraphics, x + w - o - 1, y + o, x + w - o, y + h - o, band);
        }

        // 5. Stepped Bevel Ridge & Groove at offset 5
        int o5 = 5;
        if (hasCrestNotch) {
            if (crestX > x + o5) fill(poseStackOrGraphics, x + o5, y + o5, crestX, y + o5 + 1, bevelShadow);
            if (x + w - o5 > crestX + crestW) fill(poseStackOrGraphics, crestX + crestW, y + o5, x + w - o5, y + o5 + 1, bevelShadow);
        } else {
            fill(poseStackOrGraphics, x + o5, y + o5, x + w - o5, y + o5 + 1, bevelShadow);
        }
        fill(poseStackOrGraphics, x + o5, y + o5, x + o5 + 1, y + h - o5, bevelShadow);
        fill(poseStackOrGraphics, x + o5, y + h - o5 - 1, x + w - o5, y + h - o5, bevelHighlight);
        fill(poseStackOrGraphics, x + w - o5 - 1, y + o5, x + w - o5, y + h - o5, bevelHighlight);

        // 6. Secondary soft metallic transition at offset 8
        int softGroove = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        drawBorder(poseStackOrGraphics, x + 8, y + 8, w - 16, h - 16, softGroove);
    }

    private void drawConnectedSidePanelFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int band = applyAlpha(COLOR_CHASSIS_BAND, alpha);
        int bevelShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int bevelHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);

        fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);
        fill(poseStackOrGraphics, x, y, x + 1, y + h, black);

        drawHorizontalGradientLine(poseStackOrGraphics, x, y + 1, w - 1, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        fill(poseStackOrGraphics, x, y + h - 2, x + w - 1, y + h - 2, shadow);
        fill(poseStackOrGraphics, x + w - 2, y + 1, x + w - 1, y + h - 1, shadow);

        for (int o = 2; o <= 3; o++) {
            fill(poseStackOrGraphics, x, y + o, x + w - o, y + o + 1, band);
            fill(poseStackOrGraphics, x, y + h - o - 1, x + w - o, y + h - o, band);
            fill(poseStackOrGraphics, x + w - o - 1, y + o, x + w - o, y + h - o, band);
        }

        int o5 = 5;
        fill(poseStackOrGraphics, x, y + o5, x + w - o5, y + o5 + 1, bevelShadow);
        fill(poseStackOrGraphics, x, y + h - o5 - 1, x + w - o5, y + h - o5, bevelHighlight);
        fill(poseStackOrGraphics, x + w - o5 - 1, y + o5, x + w - o5, y + h - o5, bevelHighlight);

        if (w > 20 && h > 20) {
            int softGroove = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
            fill(poseStackOrGraphics, x, y + 8, x + w - 8, y + 9, softGroove);
            fill(poseStackOrGraphics, x, y + h - 9, x + w - 8, y + h - 8, softGroove);
            fill(poseStackOrGraphics, x + w - 9, y + 8, x + w - 8, y + h - 8, softGroove);
        }
    }

    private void renderSidePanel(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        if (sidePanelType == SidePanelType.NONE) return;

        drawConnectedSidePanelFrame(poseStackOrGraphics, x, y, w, h, COLOR_PANEL_BG, alpha);

        if (w >= 60 && h >= 40) {
            SidePanelContentGeometry geometry = calculateSidePanelContentGeometry(x, y, w, h);
            int sHeadX = geometry.headerX();
            int sHeadY = geometry.headerY();
            int sHeadW = geometry.headerWidth();
            int sHeadH = 22;
            drawBeveledComponent(poseStackOrGraphics, sHeadX, sHeadY, sHeadW, sHeadH, COLOR_HEADER_BG, alpha);

            String titleText = "Modifiers";
            ResourceLocation panelIcon = null;
            if (sidePanelType == SidePanelType.PILLAR_DETAIL) {
                panelIcon = PANEL_PILLAR_TEXTURE;
                String activeId = selectedPillarDetailId != null ? selectedPillarDetailId : closingPillarDetailId;
                PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(activeId);
                if (data != null && data.id != null) {
                    titleText = "Pillar: " + font.plainSubstrByWidth(data.id, sHeadW - 48);
                } else {
                    titleText = "Pillar Modifiers";
                }
            } else if (sidePanelType == SidePanelType.COLOR_PICKER) {
                panelIcon = PANEL_COLOR_TEXTURE;
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

            int textX = sHeadX + 6 + titleOffsetX;
            if (panelIcon != null) {
                drawTintedTexture(poseStackOrGraphics, panelIcon, textX, sHeadY + (sHeadH - 14) / 2, 14, 14, COLOR_ACCENT, titleAlpha);
                textX += 18;
            }
            drawText(poseStackOrGraphics, font, titleText, textX, sHeadY + 7, applyAlpha(COLOR_TEXT_WHITE, titleAlpha));

            if (sidePanelType == SidePanelType.PILLAR_DETAIL && isPillarSwitching) {
                float glowAlpha = (1.0f - switchProgress) * 0.45f * alpha;
                drawBorder(poseStackOrGraphics, sHeadX, sHeadY, sHeadW, sHeadH, applyAlpha(COLOR_ACCENT, glowAlpha));
            }

            int closeSize = 14;
            int closeX = sHeadX + sHeadW - closeSize - 4;
            int closeY = sHeadY + 4;
            boolean hoverClose = mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize;
            int closeBg = hoverClose ? (currentTheme.isLight() ? 0xFFFEE2E2 : 0xFF3D1616) : (currentTheme.isLight() ? 0xFFF1F5F9 : 0xFF241414);
            drawBeveledButton(poseStackOrGraphics, closeX, closeY, closeSize, closeSize, closeBg, hoverClose, false, alpha);
            int closeCol = hoverClose ? (currentTheme.isLight() ? 0xFFDC2626 : 0xFFFFFFFF) : 0xFFE05050;
            drawCenteredText(poseStackOrGraphics, font, "✕", closeX + closeSize / 2, closeY + 3, applyAlpha(closeCol, alpha));

            int sContentX = geometry.contentX();
            int sContentY = geometry.contentY();
            int sContentW = geometry.contentWidth();
            int sContentH = geometry.contentHeight();

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

    private float updatePresetDeleteReveal(boolean showDelete) {
        float target = showDelete ? 1.0F : 0.0F;
        long now = System.currentTimeMillis();
        int duration = Math.max(1, (int) (180 * getAnimSpeedMultiplier()));
        presetDeleteReveal = interpolatePresetDeleteReveal(now, presetDeleteRevealStartedAt,
                presetDeleteRevealFrom, presetDeleteRevealTarget, duration);
        if (Math.abs(target - presetDeleteRevealTarget) > 0.001F) {
            presetDeleteRevealFrom = presetDeleteReveal;
            presetDeleteRevealTarget = target;
            presetDeleteRevealStartedAt = now;
        }
        presetDeleteReveal = interpolatePresetDeleteReveal(now, presetDeleteRevealStartedAt,
                presetDeleteRevealFrom, presetDeleteRevealTarget, duration);
        return presetDeleteReveal;
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
        float deleteReveal = updatePresetDeleteReveal(!isDefault);
        int groupH = 218;
        PresetPanelGeometry geometry = calculatePresetPanelGeometry(x, y, w, h, groupH, 284);
        int rowX = geometry.x();
        int rowW = geometry.width();
        int curY = geometry.y();
        int cardPad = 6;
        int btnH = 18;

        int selectedCardH = 64;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, selectedCardH, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, "Selected Preset", rowX + cardPad, curY + 5, applyAlpha(COLOR_TEXT_SILVER, alpha));
        int selectorY = curY + 18;
        int selectorX = rowX + cardPad;
        int selectorW = rowW - cardPad * 2;
        int arrowW = 20;
        boolean hoverPrev = mouseX >= selectorX && mouseX <= selectorX + arrowW && mouseY >= selectorY && mouseY <= selectorY + btnH;
        boolean hoverNext = mouseX >= selectorX + selectorW - arrowW && mouseX <= selectorX + selectorW && mouseY >= selectorY && mouseY <= selectorY + btnH;
        drawBeveledButton(poseStackOrGraphics, selectorX, selectorY, arrowW, btnH, hoverPrev ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPrev, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "‹", selectorX + arrowW / 2, selectorY + 5, applyAlpha(hoverPrev ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        int valueX = selectorX + arrowW;
        int valueW = selectorW - arrowW * 2;
        drawBeveledButton(poseStackOrGraphics, valueX, selectorY, valueW, btnH, COLOR_SLOT_BG, false, false, alpha);
        String valueText = currentPresetName + " (" + (currentPreset != null ? currentPreset.items.size() : 0) + ")";
        drawCenteredText(poseStackOrGraphics, font, trimTextToWidth(font, valueText, valueW - 8, false), valueX + valueW / 2, selectorY + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
        int nextX = selectorX + selectorW - arrowW;
        drawBeveledButton(poseStackOrGraphics, nextX, selectorY, arrowW, btnH, hoverNext ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverNext, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "›", nextX + arrowW / 2, selectorY + 5, applyAlpha(hoverNext ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        int applyY = selectorY + btnH + 5;
        boolean hoverApply = mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= applyY && mouseY <= applyY + btnH;
        hoverConfigAccess(hoverApply, ConfigSetting.ITEMS, true, false, true);
        drawBeveledGreenButton(poseStackOrGraphics, selectorX, applyY, selectorW, btnH, hoverApply, alpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Apply Preset", selectorX + selectorW / 2, applyY + 5, applyAlpha(hoverApply ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT, alpha));
        int sectionGap = Math.round(8.0F - 4.0F * deleteReveal);
        int resetGap = Math.round(18.0F - 12.0F * deleteReveal);
        curY += selectedCardH + sectionGap;

        int manageCardH = Math.round(41.0F + 20.0F * deleteReveal);
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, manageCardH, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, "Manage Preset", rowX + cardPad, curY + 5, applyAlpha(COLOR_TEXT_SILVER, alpha));
        int manageY = curY + 19;
        boolean hoverSave = mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= manageY && mouseY <= manageY + btnH;
        hoverConfigAccess(hoverSave, ConfigSetting.ITEMS, false, false, true);
        drawBeveledButton(poseStackOrGraphics, selectorX, manageY, selectorW, btnH, hoverSave ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverSave, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "▣ Save Changes", selectorX + selectorW / 2, manageY + 5, applyAlpha(hoverSave ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        int visibleDeleteH = Math.round(btnH * deleteReveal);
        if (visibleDeleteH > 0) {
            int deleteY = manageY + btnH + 4;
            boolean hoverDelete = mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= deleteY && mouseY <= deleteY + btnH;
            hoverConfigAccess(hoverDelete && deleteReveal > 0.75F, ConfigSetting.ITEMS, true, false, true);
            drawBeveledRedButton(poseStackOrGraphics, selectorX, deleteY, selectorW, visibleDeleteH, hoverDelete, alpha * deleteReveal);
            if (deleteReveal > 0.45F) {
                drawCenteredText(poseStackOrGraphics, font, "✕ Delete Preset", selectorX + selectorW / 2, deleteY + 5,
                        applyAlpha(hoverDelete ? (currentTheme.isLight() ? 0xFF1E293B : 0xFFFFFFFF) : COLOR_TOGGLE_OFF_TEXT, alpha * deleteReveal));
            }
        }
        curY += manageCardH + sectionGap;

        int createCardH = 61;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, createCardH, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, "New Preset", rowX + cardPad, curY + 5, applyAlpha(COLOR_TEXT_SILVER, alpha));
        int nameY = curY + 19;
        boolean hoverName = mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= nameY && mouseY <= nameY + btnH;
        drawBeveledButton(poseStackOrGraphics, selectorX, nameY, selectorW, btnH, COLOR_SLOT_BG, hoverName, presetNameFocused, alpha);
        String nameText = presetNameInput.isEmpty() ? "Preset name..." : presetNameInput + (presetNameFocused && !presetNameSelected ? "_" : "");
        drawText(poseStackOrGraphics, font, trimTextToWidth(font, nameText, selectorW - 10, !presetNameInput.isEmpty()), selectorX + 5, nameY + 5,
                applyAlpha(presetNameSelected ? COLOR_ACCENT : presetNameInput.isEmpty() ? COLOR_TEXT_MUTED : COLOR_TEXT_WHITE, alpha));
        int createY = nameY + btnH + 4;
        boolean hoverNew = mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= createY && mouseY <= createY + btnH;
        hoverConfigAccess(hoverNew, ConfigSetting.ITEMS, false, false, true);
        drawBeveledButton(poseStackOrGraphics, selectorX, createY, selectorW, btnH, hoverNew ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverNew, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+ Create Preset", selectorX + selectorW / 2, createY + 5, applyAlpha(hoverNew ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        curY += createCardH + resetGap;

        boolean hoverReset = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        hoverConfigAccess(hoverReset, ConfigSetting.ITEMS, true, false, true);
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverReset ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverReset, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "↻ Reset to Defaults", rowX + rowW / 2, curY + 5, applyAlpha(hoverReset ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void drawInlinePresetsTab(Object poseStackOrGraphics, int btnX, int btnY, int btnW, int btnH, boolean hover, boolean active, float alpha) {
        // 1. Chassis Body Fill (Matches main panel frame chassis base)
        fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int band = applyAlpha(COLOR_CHASSIS_BAND, alpha);
        int bevelShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int bevelHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);

        // 2. Outermost 1px Black Border (Matches main panel frame)
        fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + 1, black);
        fill(poseStackOrGraphics, btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, black);
        fill(poseStackOrGraphics, btnX, btnY, btnX + 1, btnY + btnH, black);
        fill(poseStackOrGraphics, btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, black);

        // 3. 1-Pixel Wide Metallic Gradient Highlight (Top & Left) & Deep Shadow (Bottom & Right)
        drawHorizontalGradientLine(poseStackOrGraphics, btnX + 1, btnY + 1, btnW - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, btnX + 1, btnY + 1, btnH - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_SHADOW, alpha);
        fill(poseStackOrGraphics, btnX + 1, btnY + btnH - 2, btnX + btnW - 1, btnY + btnH - 1, shadow);
        fill(poseStackOrGraphics, btnX + btnW - 2, btnY + 1, btnX + btnW - 1, btnY + btnH - 1, shadow);

        // 4. Outer Chassis Band (Offset 2, exactly matches main frame chassis band)
        fill(poseStackOrGraphics, btnX + 2, btnY + 2, btnX + btnW - 2, btnY + 3, band);
        fill(poseStackOrGraphics, btnX + 2, btnY + btnH - 3, btnX + btnW - 2, btnY + btnH - 2, band);
        fill(poseStackOrGraphics, btnX + 2, btnY + 2, btnX + 3, btnY + btnH - 2, band);
        fill(poseStackOrGraphics, btnX + btnW - 3, btnY + 2, btnX + btnW - 2, btnY + btnH - 2, band);

        // 5. Stepped Bevel Ridge & Groove (Offset 3, matching main frame stepped bevel)
        fill(poseStackOrGraphics, btnX + 3, btnY + 3, btnX + btnW - 3, btnY + 4, bevelShadow);
        fill(poseStackOrGraphics, btnX + 3, btnY + 3, btnX + 4, btnY + btnH - 3, bevelShadow);
        fill(poseStackOrGraphics, btnX + 3, btnY + btnH - 4, btnX + btnW - 3, btnY + btnH - 3, bevelHighlight);
        fill(poseStackOrGraphics, btnX + btnW - 4, btnY + 3, btnX + btnW - 3, btnY + btnH - 3, bevelHighlight);

        // 6. Inner Face Plate (Offset 4)
        int faceX = btnX + 4;
        int faceY = btnY + 4;
        int faceW = btnW - 8;
        int faceH = btnH - 8;

        int bgCol = active ? (hover ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG_ACTIVE)
                : (hover ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG);
        fill(poseStackOrGraphics, faceX, faceY, faceX + faceW, faceY + faceH, applyAlpha(bgCol, alpha));

        // Active / Hover Accent Framing
        if (active) {
            int accentGlow = applyAlpha(COLOR_ACCENT, (hover ? 0.90f : 0.70f) * alpha);
            drawBorder(poseStackOrGraphics, faceX, faceY, faceW, faceH, accentGlow);
        } else if (hover) {
            int hoverGlow = applyAlpha(COLOR_ROW_BORDER_HOVER, alpha * 0.75f);
            drawBorder(poseStackOrGraphics, faceX, faceY, faceW, faceH, hoverGlow);
        }

        // 7. 3-Slider Equalizer Icon (Theme-unified tracks and beveled knobs)
        int cx = btnX + btnW / 2;
        int cy = btnY + btnH / 2;

        int trkCol = applyAlpha(active ? COLOR_ACCENT : (hover ? COLOR_ROW_BORDER_HOVER : COLOR_CHASSIS_BAND), alpha);
        int knobBase = active ? COLOR_ACCENT : (hover ? COLOR_ACCENT : COLOR_TEXT_WHITE);
        int knobCol = applyAlpha(knobBase, alpha);
        int knobHl = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha * (active ? 0.95f : 0.75f));
        int knobShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha * 0.65f);

        // Track 1 (Top, cy - 4)
        fill(poseStackOrGraphics, cx - 7, cy - 4, cx + 7, cy - 3, trkCol);
        // Knob 1 (Left: cx - 5 to cx - 2, cy - 6 to cy - 1)
        fill(poseStackOrGraphics, cx - 5, cy - 6, cx - 2, cy - 1, knobCol);
        fill(poseStackOrGraphics, cx - 5, cy - 6, cx - 2, cy - 5, knobHl);
        fill(poseStackOrGraphics, cx - 5, cy - 5, cx - 4, cy - 1, knobHl);
        fill(poseStackOrGraphics, cx - 3, cy - 5, cx - 2, cy - 1, knobShadow);
        fill(poseStackOrGraphics, cx - 5, cy - 2, cx - 2, cy - 1, knobShadow);

        // Track 2 (Middle, cy)
        fill(poseStackOrGraphics, cx - 7, cy, cx + 7, cy + 1, trkCol);
        // Knob 2 (Right: cx + 2 to cx + 5, cy - 2 to cy + 3)
        fill(poseStackOrGraphics, cx + 2, cy - 2, cx + 5, cy + 3, knobCol);
        fill(poseStackOrGraphics, cx + 2, cy - 2, cx + 5, cy - 1, knobHl);
        fill(poseStackOrGraphics, cx + 2, cy - 1, cx + 3, cy + 3, knobHl);
        fill(poseStackOrGraphics, cx + 4, cy - 1, cx + 5, cy + 3, knobShadow);
        fill(poseStackOrGraphics, cx + 2, cy + 2, cx + 5, cy + 3, knobShadow);

        // Track 3 (Bottom, cy + 4)
        fill(poseStackOrGraphics, cx - 7, cy + 4, cx + 7, cy + 5, trkCol);
        // Knob 3 (Center: cx - 2 to cx + 1, cy + 2 to cy + 7)
        fill(poseStackOrGraphics, cx - 2, cy + 2, cx + 1, cy + 7, knobCol);
        fill(poseStackOrGraphics, cx - 2, cy + 2, cx + 1, cy + 3, knobHl);
        fill(poseStackOrGraphics, cx - 2, cy + 3, cx - 1, cy + 7, knobHl);
        fill(poseStackOrGraphics, cx, cy + 3, cx + 1, cy + 7, knobShadow);
        fill(poseStackOrGraphics, cx - 2, cy + 6, cx + 1, cy + 7, knobShadow);
    }

    private void drawPresetsSideTab(Object poseStackOrGraphics, Font font, int frameRight, int tabY, int mouseX, int mouseY, float alpha) {
        PresetSideTabGeometry hoverBounds = calculatePresetSideTabGeometry(frameRight, tabY, true);
        boolean hover = mouseX >= hoverBounds.x() && mouseX <= hoverBounds.x() + hoverBounds.width()
                && mouseY >= hoverBounds.y() && mouseY <= hoverBounds.y() + hoverBounds.height();
        PresetSideTabGeometry geometry = calculatePresetSideTabGeometry(frameRight, tabY, hover);
        int tabX = geometry.x();
        int tabH = geometry.height();
        int currentW = geometry.width();

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int sideBand = applyAlpha(COLOR_CHASSIS_BAND, alpha);
        int sideBevelShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int sideBevelHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
        int btnBg = applyAlpha(hover ? COLOR_BTN_BG_HOVER : COLOR_PANEL_BG, alpha);

        fill(poseStackOrGraphics, tabX, tabY, tabX + currentW, tabY + tabH, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        fill(poseStackOrGraphics, tabX, tabY, tabX + currentW, tabY + 1, black);
        fill(poseStackOrGraphics, tabX, tabY + tabH - 1, tabX + currentW, tabY + tabH, black);
        fill(poseStackOrGraphics, tabX + currentW - 1, tabY, tabX + currentW, tabY + tabH, black);

        drawHorizontalGradientLine(poseStackOrGraphics, tabX, tabY + 1, currentW - 1, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        fill(poseStackOrGraphics, tabX, tabY + tabH - 2, tabX + currentW - 1, tabY + tabH - 1, shadow);
        fill(poseStackOrGraphics, tabX + currentW - 2, tabY + 1, tabX + currentW - 1, tabY + tabH - 1, shadow);

        for (int o = 2; o <= 3; o++) {
            fill(poseStackOrGraphics, tabX, tabY + o, tabX + currentW - o, tabY + o + 1, sideBand);
            fill(poseStackOrGraphics, tabX, tabY + tabH - o - 1, tabX + currentW - o, tabY + tabH - o, sideBand);
            fill(poseStackOrGraphics, tabX + currentW - o - 1, tabY + o, tabX + currentW - o, tabY + tabH - o, sideBand);
        }

        int o5 = 5;
        if (currentW > 14 && tabH > 14) {
            fill(poseStackOrGraphics, tabX, tabY + o5, tabX + currentW - o5, tabY + o5 + 1, sideBevelShadow);
            fill(poseStackOrGraphics, tabX, tabY + tabH - o5 - 1, tabX + currentW - o5, tabY + tabH - o5, sideBevelHighlight);
            fill(poseStackOrGraphics, tabX + currentW - o5 - 1, tabY + o5, tabX + currentW - o5, tabY + tabH - o5, sideBevelHighlight);
            fill(poseStackOrGraphics, tabX, tabY + 6, tabX + currentW - 6, tabY + tabH - 6, btnBg);
        } else {
            fill(poseStackOrGraphics, tabX, tabY + 4, tabX + currentW - 4, tabY + tabH - 4, btnBg);
        }

        int cx = geometry.centerX();
        int cy = geometry.centerY() - 1;
        int trkCol = applyAlpha(hover ? (currentTheme.isLight() ? 0xFF64748B : 0xFF94A3B8) : (currentTheme.isLight() ? 0xFF94A3B8 : COLOR_CHASSIS_BAND), alpha);
        int knobCol = applyAlpha(hover ? COLOR_ACCENT : (currentTheme.isLight() ? 0xFF334155 : COLOR_TEXT_WHITE), alpha);
        int knobHighlight = applyAlpha(hover ? 0xFFFFFFFF : (currentTheme.isLight() ? 0xFFFFFFFF : 0xFFCCCCCC), alpha);

        fill(poseStackOrGraphics, cx - 6, cy - 4, cx + 6, cy - 3, trkCol);
        fill(poseStackOrGraphics, cx - 4, cy - 6, cx - 2, cy - 1, knobCol);
        fill(poseStackOrGraphics, cx - 3, cy - 5, cx - 2, cy - 2, knobHighlight);

        fill(poseStackOrGraphics, cx - 6, cy, cx + 6, cy + 1, trkCol);
        fill(poseStackOrGraphics, cx + 2, cy - 2, cx + 4, cy + 3, knobCol);
        fill(poseStackOrGraphics, cx + 3, cy - 1, cx + 4, cy + 2, knobHighlight);

        fill(poseStackOrGraphics, cx - 6, cy + 4, cx + 6, cy + 5, trkCol);
        fill(poseStackOrGraphics, cx - 1, cy + 2, cx + 1, cy + 7, knobCol);
        fill(poseStackOrGraphics, cx, cy + 3, cx + 1, cy + 6, knobHighlight);

        // Draw the exposed edge last so the bevel cannot cover its black outline.
        fill(poseStackOrGraphics, tabX + currentW - 1, tabY, tabX + currentW, tabY + tabH, black);

        if (hover) {
            deferredTooltip = Collections.singletonList(ComponentHelper.literal("Presets (Click to Open)"));
            deferredTooltipX = rawMouseX + 12;
            deferredTooltipY = rawMouseY;
        }
    }

    private void renderHeaderThemeToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int mouseX, int mouseY, float alpha) {
        float target = getThemeTargetPos(currentTheme);
        if (themeToggleAnimPos < 0.0f) {
            themeToggleAnimPos = target;
            themeToggleAnimFrom = target;
            themeToggleLastThemeId = currentTheme.id;
        } else if (themeToggleLastThemeId == null || !themeToggleLastThemeId.equals(currentTheme.id)) {
            themeToggleLastThemeId = currentTheme.id;
            themeToggleAnimFrom = themeToggleAnimPos;
            themeToggleAnimStartTime = System.currentTimeMillis();
        }

        long now = System.currentTimeMillis();
        long elapsed = now - themeToggleAnimStartTime;
        int duration = Math.max(1, (int) (220 * getAnimSpeedMultiplier()));
        if (elapsed < duration) {
            float t = easeOutCubic((float) elapsed / duration);
            themeToggleAnimPos = themeToggleAnimFrom + (target - themeToggleAnimFrom) * t;
        } else {
            themeToggleAnimPos = target;
        }

        float pos = Math.max(0.0f, Math.min(1.0f, themeToggleAnimPos));
        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;

        // 1. Unified Track (matched to current theme slot colors, no random color patches)
        int trackBg = COLOR_SLOT_BG;
        int trackBorder = hover ? COLOR_ROW_BORDER_HOVER : COLOR_SLOT_BORDER;
        renderRoundedPill(poseStackOrGraphics, x, y, w, h, trackBg, trackBorder, alpha);

        // 2. Sliding Rounded Knob (matched to current theme button colors)
        int knobSize = 12;
        int knobTravel = w - knobSize - 4;
        int knobX = (int) Math.round(x + 2 + pos * knobTravel);
        int knobY = y + 2;

        int knobBg = hover ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG;
        int knobBorder = hover ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER;
        renderRoundedKnob(poseStackOrGraphics, knobX, knobY, knobSize, knobBg, knobBorder, alpha);

        // 3. Plain text symbols for Moon and Sun (standard BMP unicode in Minecraft font, no surrogate pairs or VS16)
        String moonEmoji = "\u263E"; // ☾
        String sunEmoji = "\u2600";  // ☀
        int textY = y + 4;

        int leftX = x + 2 + knobSize / 2;
        int rightX = x + w - 2 - knobSize / 2;

        // Ambient unselected emoji on the inactive side of the track
        if (pos > 0.25f) {
            int moonDimCol = applyAlpha(COLOR_TEXT_MUTED, alpha * Math.min(1.0f, pos) * 0.45f);
            drawCenteredText(poseStackOrGraphics, font, moonEmoji, leftX, textY, moonDimCol);
        }
        if (pos < 0.75f) {
            int sunDimCol = applyAlpha(COLOR_TEXT_MUTED, alpha * Math.min(1.0f, 1.0f - pos) * 0.45f);
            drawCenteredText(poseStackOrGraphics, font, sunEmoji, rightX, textY, sunDimCol);
        }

        // Active emoji directly centered on the sliding knob
        int knobCenterX = knobX + knobSize / 2;
        String activeEmoji = pos < 0.5f ? moonEmoji : sunEmoji;
        int activeCol = applyAlpha(COLOR_TEXT_WHITE, alpha);
        drawCenteredText(poseStackOrGraphics, font, activeEmoji, knobCenterX, textY, activeCol);

        if (hover) {
            List<Component> tip = new ArrayList<>();
            if ("light".equals(currentTheme.id)) {
                tip.add(ComponentHelper.literal("Theme: Light Mode §7(Click for Dark)"));
            } else if ("dark".equals(currentTheme.id)) {
                tip.add(ComponentHelper.literal("Theme: Dark Mode §7(Click for Light)"));
            } else {
                tip.add(ComponentHelper.literal("Theme: " + currentTheme.displayName + " §7(Click for Light)"));
            }
            deferredTooltip = tip;
            deferredTooltipX = rawMouseX + 12;
            deferredTooltipY = rawMouseY;
        }
    }

    private void renderRoundedPill(Object poseStackOrGraphics, int x, int y, int w, int h, int bgCol, int borderCol, float alpha) {
        int bg = applyAlpha(bgCol, alpha);
        int border = applyAlpha(borderCol, alpha);

        // Background interior
        fill(poseStackOrGraphics, x + 3, y, x + w - 3, y + h, bg);
        fill(poseStackOrGraphics, x + 1, y + 1, x + 3, y + h - 1, bg);
        fill(poseStackOrGraphics, x + w - 3, y + 1, x + w - 1, y + h - 1, bg);
        fill(poseStackOrGraphics, x, y + 3, x + 1, y + h - 3, bg);
        fill(poseStackOrGraphics, x + w - 1, y + 3, x + w, y + h - 3, bg);

        // Border
        fill(poseStackOrGraphics, x + 3, y, x + w - 3, y + 1, border);
        fill(poseStackOrGraphics, x + 3, y + h - 1, x + w - 3, y + h, border);
        fill(poseStackOrGraphics, x, y + 3, x + 1, y + h - 3, border);
        fill(poseStackOrGraphics, x + w - 1, y + 3, x + w, y + h - 3, border);
        fill(poseStackOrGraphics, x + 1, y + 1, x + 3, y + 2, border);
        fill(poseStackOrGraphics, x + 1, y + 2, x + 2, y + 3, border);
        fill(poseStackOrGraphics, x + w - 3, y + 1, x + w - 1, y + 2, border);
        fill(poseStackOrGraphics, x + w - 2, y + 2, x + w - 1, y + 3, border);
        fill(poseStackOrGraphics, x + 1, y + h - 2, x + 3, y + h - 1, border);
        fill(poseStackOrGraphics, x + 1, y + h - 3, x + 2, y + h - 2, border);
        fill(poseStackOrGraphics, x + w - 3, y + h - 2, x + w - 1, y + h - 1, border);
        fill(poseStackOrGraphics, x + w - 2, y + h - 3, x + w - 1, y + h - 2, border);
    }

    private void renderRoundedKnob(Object poseStackOrGraphics, int x, int y, int size, int bgCol, int borderCol, float alpha) {
        int bg = applyAlpha(bgCol, alpha);
        int border = applyAlpha(borderCol, alpha);

        fill(poseStackOrGraphics, x + 2, y, x + size - 2, y + size, bg);
        fill(poseStackOrGraphics, x + 1, y + 1, x + size - 1, y + size - 1, bg);
        fill(poseStackOrGraphics, x, y + 2, x + size, y + size - 2, bg);

        fill(poseStackOrGraphics, x + 2, y, x + size - 2, y + 1, border);
        fill(poseStackOrGraphics, x + 2, y + size - 1, x + size - 2, y + size, border);
        fill(poseStackOrGraphics, x, y + 2, x + 1, y + size - 2, border);
        fill(poseStackOrGraphics, x + size - 1, y + 2, x + size, y + size - 2, border);
        fill(poseStackOrGraphics, x + 1, y + 1, x + 2, y + 2, border);
        fill(poseStackOrGraphics, x + size - 2, y + 1, x + size - 1, y + 2, border);
        fill(poseStackOrGraphics, x + 1, y + size - 2, x + 2, y + size - 1, border);
        fill(poseStackOrGraphics, x + size - 2, y + size - 2, x + size - 1, y + size - 1, border);
    }

    private void handleHeaderThemeToggleClick(double mouseX, int toggleX, int toggleW) {
        float mid = toggleX + toggleW / 2.0f;
        boolean isLight = currentTheme != null && "light".equals(currentTheme.id);
        if (mouseX < mid) {
            // Clicked Moon / Dark side
            applyTheme(isLight ? ConfigThemes.DARK : ConfigThemes.LIGHT);
        } else {
            // Clicked Sun / Light side
            applyTheme(isLight ? ConfigThemes.DARK : ConfigThemes.LIGHT);
        }
        themeToggleAnimStartTime = System.currentTimeMillis();
        themeToggleAnimFrom = themeToggleAnimPos;
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
            fill(poseStackOrGraphics, x + 2, y + 1, x + w - 2, y + 2, applyAlpha(COLOR_ACCENT, pulseAlpha));
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
        renderSideRowCounter(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Speed", ParticleRange.PATTERN_SPEED, speed, mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 4: Spread Counter
        double spread = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
        renderSideRowCounter(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Spread", ParticleRange.PATTERN_SPREAD, spread, mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 5: Intensity Counter
        double intensity = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
        renderSideRowCounter(poseStackOrGraphics, font, rowX, curY, rowW, rowH, "Intensity", ParticleRange.PATTERN_INTENSITY, intensity, mouseX, mouseY, contentAlpha);
        curY += rowH + rowGap;

        // Row 6: Per-Pillar Colors Palette
        List<String> colors = new ArrayList<>(data.dyeColors != null && !data.dyeColors.isEmpty() ? data.dyeColors : globalCfg.particle_color);
        while (colors.size() < 7) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(7, Math.max(1, colors.size())));
        renderSideRowPalette(poseStackOrGraphics, font, rowX, curY, rowW, 34, colors, maxColors, mouseX, mouseY, contentAlpha);
        curY += 34 + rowGap;

        // Embedded Inline Color Picker at bottom of details panel
        curY = renderInlinePillarColorPicker(poseStackOrGraphics, font, rowX, curY, rowW, data, mouseX, mouseY, contentAlpha);
        curY += rowGap;

        // Row 7: Save Button
        int btnH = 16;
        boolean hoverSave = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        hoverConfigAccess(hoverSave, ConfigSetting.PILLAR_EDIT, false, false, false);
        drawBeveledGreenButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverSave, contentAlpha);
        drawCenteredText(poseStackOrGraphics, font, "✓ Save Pillar", rowX + rowW / 2, curY + 4, applyAlpha(hoverSave ? (currentTheme.isLight() ? 0xFF14532D : 0xFFFFFFFF) : COLOR_TOGGLE_ON_TEXT, contentAlpha));
        curY += btnH + rowGap;

        // Row 8: Locate in World
        boolean hoverLoc = mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH;
        drawBeveledButton(poseStackOrGraphics, rowX, curY, rowW, btnH, hoverLoc ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverLoc, false, contentAlpha);
        drawCenteredText(poseStackOrGraphics, font, "⌖ Locate in World", rowX + rowW / 2, curY + 4, applyAlpha(hoverLoc ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, contentAlpha));

        disableGuiScissor(poseStackOrGraphics);
    }

    private void updateLivePillarColor(PillarIdManager.PillarData data) {
        if (data == null || editingColorIndex < 0) return;
        int currentColorInt = hsbToRgb(pickerHue, pickerSat, pickerBri);
        int r = (currentColorInt >> 16) & 0xFF;
        int g = (currentColorInt >> 8) & 0xFF;
        int b = currentColorInt & 0xFF;
        String hexString = String.format("#%02X%02X%02X", r, g, b);
        if (!hexColorFocused) {
            hexColorInput = hexString;
        }
        if (data.dyeColors == null) {
            data.dyeColors = new ArrayList<>(PillarParticleConfig.get().particle_color);
        } else {
            data.dyeColors = new ArrayList<>(data.dyeColors);
        }
        while (data.dyeColors.size() <= editingColorIndex) {
            data.dyeColors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(data.dyeColors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        data.dyeColors.set(editingColorIndex, hexString);
        PillarIdManager.getClient().updatePillarData(data);
    }

    private int renderInlinePillarColorPicker(Object poseStackOrGraphics, Font font, int x, int curY, int w, PillarIdManager.PillarData data, int mouseX, int mouseY, float alpha) {
        if (editingColorIndex < 0) editingColorIndex = 0;
        int pad = 4;
        int hueW = 12;
        int gradW = w - hueW - pad - 2;
        int gradH = 68;
        int gradX = x + 1;
        int gradY = curY + 1;
        int hueX = gradX + gradW + pad;
        int hueY = gradY;
        int hueH = gradH;

        this.inlinePickerGradX = gradX;
        this.inlinePickerGradY = gradY;
        this.inlinePickerGradW = gradW;
        this.inlinePickerGradH = gradH;
        this.inlinePickerHueX = hueX;
        this.inlinePickerHueY = hueY;
        this.inlinePickerHueW = hueW;
        this.inlinePickerHueH = hueH;

        // 2D Sat x Bri Box
        ConfigGuiBatch.colorField(asPose(poseStackOrGraphics), gradX, gradY, gradW, gradH, hsbToRgb(pickerHue, 1, 1), alpha);
        drawBorder(poseStackOrGraphics, gradX - 1, gradY - 1, gradW + 2, gradH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        // Crosshair
        int crossX = gradX + (int) (pickerSat * gradW);
        int crossY = gradY + (int) ((1.0f - pickerBri) * gradH);
        drawBorder(poseStackOrGraphics, crossX - 3, crossY - 3, 6, 6, applyAlpha(COLOR_FRAME_BLACK, alpha));
        drawBorder(poseStackOrGraphics, crossX - 2, crossY - 2, 4, 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Hue Slider
        for (int hy = 0; hy < hueH; hy += 2) {
            float hVal = (hy / (float) hueH) * 360.0f;
            int col = hsbToRgb(hVal, 1.0f, 1.0f);
            int hh = Math.min(2, hueH - hy);
            fill(poseStackOrGraphics, hueX, hueY + hy, hueX + hueW, hueY + hy + hh, applyAlpha(col, alpha));
        }
        drawBorder(poseStackOrGraphics, hueX - 1, hueY - 1, hueW + 2, hueH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        int thumbY = hueY + (int) ((pickerHue / 360.0f) * hueH);
        fill(poseStackOrGraphics, hueX - 1, thumbY - 1, hueX + hueW + 1, thumbY + 2, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawBorder(poseStackOrGraphics, hueX - 1, thumbY - 1, hueW + 2, 3, applyAlpha(COLOR_FRAME_BLACK, alpha));

        curY += gradH + 5;

        // Preview & Hex Input
        int currentColorInt = hsbToRgb(pickerHue, pickerSat, pickerBri);
        int r = (currentColorInt >> 16) & 0xFF;
        int g = (currentColorInt >> 8) & 0xFF;
        int b = currentColorInt & 0xFF;
        String hexString = String.format("#%02X%02X%02X", r, g, b);
        if (!hexColorFocused) {
            hexColorInput = hexString;
        }

        int prevSize = 14;
        fill(poseStackOrGraphics, gradX, curY, gradX + prevSize, curY + prevSize, applyAlpha(currentColorInt, alpha));
        drawBorder(poseStackOrGraphics, gradX, curY, prevSize, prevSize, applyAlpha(COLOR_FRAME_BLACK, alpha));

        int hexX = gradX + prevSize + 4;
        int hexW = w - prevSize - 6;
        boolean validHex = parseHexColor(hexColorInput) >= 0;
        drawBeveledButton(poseStackOrGraphics, hexX, curY, hexW, prevSize, COLOR_SLOT_BG, false, hexColorFocused, alpha);
        drawBorder(poseStackOrGraphics, hexX, curY, hexW, prevSize,
                applyAlpha(validHex || !hexColorFocused ? COLOR_SLOT_BORDER : COLOR_TOGGLE_OFF_BORDER, alpha));
        String displayedHex = hexColorInput + (hexColorFocused && !hexColorSelected ? "_" : "");
        drawText(poseStackOrGraphics, font, trimTextToWidth(font, displayedHex, hexW - 8, true), hexX + 4, curY + 3,
                applyAlpha(hexColorSelected ? COLOR_ACCENT : COLOR_TEXT_WHITE, alpha));

        curY += prevSize + 4;

        // Quick Preset Swatches
        int pSize = 11;
        int pGap = 3;
        int pCols = Math.max(1, (w - 2) / (pSize + pGap));
        int presetsToShow = Math.min(PALETTE_PRESETS.length, pCols);
        for (int p = 0; p < presetsToShow; p++) {
            int px = gradX + p * (pSize + pGap);
            fill(poseStackOrGraphics, px, curY, px + pSize, curY + pSize, applyAlpha(PALETTE_PRESETS[p], alpha));
            drawBorder(poseStackOrGraphics, px, curY, pSize, pSize, applyAlpha(0xFF000000, alpha));
        }
        curY += pSize + 4;

        return curY;
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

        ConfigGuiBatch.colorField(asPose(poseStackOrGraphics), gradX, gradY,
                gradW, gradH, hsbToRgb(pickerHue, 1, 1), alpha);
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
            fill(poseStackOrGraphics, hueX, hueY + hy, hueX + hueW, hueY + hy + hh, applyAlpha(col, alpha));
        }
        drawBorder(poseStackOrGraphics, hueX - 1, hueY - 1, hueW + 2, hueH + 2, applyAlpha(COLOR_FRAME_BLACK, alpha));

        int thumbY = hueY + (int) ((pickerHue / 360.0f) * hueH);
        fill(poseStackOrGraphics, hueX - 2, thumbY - 1, hueX + hueW + 2, thumbY + 2, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawBorder(poseStackOrGraphics, hueX - 2, thumbY - 1, hueW + 4, 3, applyAlpha(COLOR_FRAME_BLACK, alpha));

        curY += gradH + 8;

        // 2. Preview & Hex
        int currentColorInt = hsbToRgb(pickerHue, pickerSat, pickerBri);
        int r = (currentColorInt >> 16) & 0xFF;
        int g = (currentColorInt >> 8) & 0xFF;
        int b = currentColorInt & 0xFF;
        String hexString = String.format("#%02X%02X%02X", r, g, b);
        if (!hexColorFocused) {
            hexColorInput = hexString;
        }

        int prevSize = 20;
        fill(poseStackOrGraphics, gradX, curY, gradX + prevSize, curY + prevSize, applyAlpha(currentColorInt, alpha));
        drawBorder(poseStackOrGraphics, gradX, curY, prevSize, prevSize, applyAlpha(COLOR_FRAME_BLACK, alpha));
        int hexX = gradX + prevSize + 6;
        int hexW = gradW + hueW + pad - prevSize - 6;
        boolean validHex = parseHexColor(hexColorInput) >= 0;
        drawBeveledButton(poseStackOrGraphics, hexX, curY, hexW, prevSize, COLOR_SLOT_BG, false, hexColorFocused, alpha);
        drawBorder(poseStackOrGraphics, hexX, curY, hexW, prevSize,
                applyAlpha(validHex || !hexColorFocused ? COLOR_SLOT_BORDER : COLOR_TOGGLE_OFF_BORDER, alpha));
        String displayedHex = hexColorInput + (hexColorFocused && !hexColorSelected ? "_" : "");
        drawText(poseStackOrGraphics, font, trimTextToWidth(font, displayedHex, hexW - 10, true), hexX + 5, curY + 6,
                applyAlpha(hexColorSelected ? COLOR_ACCENT : COLOR_TEXT_WHITE, alpha));

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
            fill(poseStackOrGraphics, px, py, px + pSize, py + pSize, applyAlpha(PALETTE_PRESETS[p], alpha));
            drawBorder(poseStackOrGraphics, px, py, pSize, pSize, applyAlpha(0xFF000000, alpha));
        }
        int pRows = (int) Math.ceil((double) PALETTE_PRESETS.length / pCols);
        curY += pRows * (pSize + pGap) + 10;

        // 4. Buttons
        int btnW = w - pad * 2;
        int btnH = 16;
        boolean hoverApply = mouseX >= gradX && mouseX <= gradX + btnW && mouseY >= curY && mouseY <= curY + btnH;
        hoverConfigAccess(hoverApply, isEditingPillarColor ? ConfigSetting.PILLAR_EDIT
                : ConfigSetting.COLORS, false, !isEditingPillarColor, false);
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

        int btnW = 26;
        int btnH = 15;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;
        boolean hover = mouseX >= btnX - 2 && mouseX <= btnX + btnW + 2 && mouseY >= btnY - 2 && mouseY <= btnY + btnH + 2;

        renderToggleButton(poseStackOrGraphics, "side_" + label, btnX, btnY, val, hover, alpha);
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

    private void renderSideRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, String label, ParticleRange range, double value, int mouseX, int mouseY, float alpha) {
        renderSideRowCounter(poseStackOrGraphics, font, x, y, w, h, label, range.formatValue(value), mouseX, mouseY, alpha);
        int counterX = x + w - 74 - 4;
        int counterY = y + (h - 15) / 2;
        if (mouseX >= counterX && mouseX <= counterX + 74 && mouseY >= counterY && mouseY <= counterY + 15) {
            deferredTooltip = Collections.singletonList(ComponentHelper.literal(range.tooltip(value)));
            deferredTooltipX = rawMouseX + 12;
            deferredTooltipY = rawMouseY;
        }
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
        fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, value, valX + valW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, plusX, counterY, stepW, counterH, hoverPlus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPlus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+", plusX + stepW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    private void renderSideRowPalette(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, List<String> colors, int maxColors, int mouseX, int mouseY, float alpha) {
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, COLOR_ROW_BG, false, false, alpha);
        drawText(poseStackOrGraphics, font, "Colors (" + maxColors + "/7)", x + 4, y + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

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
        fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, String.valueOf(maxColors), valX + valW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, plusX, counterY, stepW, counterH, hoverPlus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPlus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+", plusX + stepW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int swSize = 13;
        int swGap = 4;
        int swY = y + 17;
        for (int i = 0; i < 7; i++) {
            int swX = x + 6 + i * (swSize + swGap);
            int colInt = 0xFFFFFFFF;
            if (colors != null && i < colors.size()) {
                try {
                    String hex = colors.get(i).replace("#", "");
                    colInt = 0xFF000000 | Integer.parseInt(hex, 16);
                } catch (Exception ignored) {}
            }

            boolean active = i < maxColors;
            fill(poseStackOrGraphics, swX, swY, swX + swSize, swY + swSize, applyAlpha(colInt, active ? alpha : alpha * 0.30f));
            int borderCol = active ? COLOR_TEXT_WHITE : COLOR_SLOT_BORDER;
            drawBorder(poseStackOrGraphics, swX, swY, swSize, swSize, applyAlpha(borderCol, alpha));

            if ((isEditingPillarColor || sidePanelType == SidePanelType.PILLAR_DETAIL) && i == editingColorIndex) {
                drawBorder(poseStackOrGraphics, swX - 1, swY - 1, swSize + 2, swSize + 2, applyAlpha(COLOR_ACCENT, alpha));
            } else if (mouseX >= swX && mouseX <= swX + swSize && mouseY >= swY && mouseY <= swY + swSize) {
                drawBorder(poseStackOrGraphics, swX - 1, swY - 1, swSize + 2, swSize + 2, applyAlpha(COLOR_ACCENT_GLOW, alpha * 0.7f));
            }
        }
    }
    private void drawHorizontalGradientLine(Object poseStackOrGraphics, int x, int y, int w, int startCol, int endCol, float alpha) {
        if (w <= 0) return;
        if (w <= 6) {
            int col = lerpColor(startCol, endCol, 0.5f);
            fill(poseStackOrGraphics, x, y, x + w, y + 1, applyAlpha(col, alpha));
            return;
        }
        int left = applyAlpha(startCol, alpha), right = applyAlpha(endCol, alpha);
        ConfigGuiBatch.gradient(asPose(poseStackOrGraphics), x, y, x + w, y + 1, left, right, right, left);
    }

    private void drawVerticalGradientLine(Object poseStackOrGraphics, int x, int y, int h, int startCol, int endCol, float alpha) {
        if (h <= 0) return;
        if (h <= 6) {
            int col = lerpColor(startCol, endCol, 0.5f);
            fill(poseStackOrGraphics, x, y, x + 1, y + h, applyAlpha(col, alpha));
            return;
        }
        int top = applyAlpha(startCol, alpha), bottom = applyAlpha(endCol, alpha);
        ConfigGuiBatch.gradient(asPose(poseStackOrGraphics), x, y, x + 1, y + h, top, top, bottom, bottom);
    }

    private void drawBeveledComponent(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        // 1. Interior Fill
        fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bgRgb, alpha));

        // 2. Bottom & Right dark recessed shadow
        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

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
        fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bg, alpha));

        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

        int startCol = active ? COLOR_BEVEL_HIGHLIGHT : (hover ? COLOR_BORDER_GRADIENT_START : COLOR_BTN_BORDER);
        int endCol = active ? COLOR_BORDER_SHADOW : (hover ? COLOR_BORDER_GRADIENT_END : COLOR_BEVEL_SHADOW);

        drawHorizontalGradientLine(poseStackOrGraphics, x, y, w - 1, startCol, endCol, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, x, y, h - 1, startCol, endCol, alpha);
    }

    // Top & Left Metallic Green Button (for Apply / ON states)
    private void drawBeveledGreenButton(Object poseStackOrGraphics, int x, int y, int w, int h, boolean hover, float alpha) {
        boolean light = currentTheme != null && currentTheme.isLight();
        int bg = hover ? (light ? 0xFFBBF7D0 : 0xFF1E4828) : COLOR_TOGGLE_ON_BG;
        fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bg, alpha));

        int dark = applyAlpha(light ? 0xFF86EFAC : 0xFF0A2412, alpha);
        fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

        int startCol = hover ? (light ? 0xFF4ADE80 : 0xFFCEFFE0) : (light ? 0xFF86EFAC : 0xFF86EFAC);
        int endCol = hover ? (light ? 0xFF22C55E : 0xFF4ADE80) : (light ? 0xFF22C55E : 0xFF22C55E);

        drawHorizontalGradientLine(poseStackOrGraphics, x, y, w - 1, startCol, endCol, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, x, y, h - 1, startCol, endCol, alpha);
    }

    // Top & Left Metallic Crimson Red Button (for OFF states / Cancel)
    private void drawBeveledRedButton(Object poseStackOrGraphics, int x, int y, int w, int h, boolean hover, float alpha) {
        boolean light = currentTheme != null && currentTheme.isLight();
        int bg = hover ? (light ? 0xFFE2E8F0 : 0xFF4A1A1A) : COLOR_TOGGLE_OFF_BG;
        fill(poseStackOrGraphics, x + 1, y + 1, x + w - 1, y + h - 1, applyAlpha(bg, alpha));

        int dark = applyAlpha(light ? COLOR_BEVEL_SHADOW : 0xFF200808, alpha);
        fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, dark);
        fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, dark);

        int startCol = hover ? (light ? 0xFFCBD5E1 : 0xFFFFD2D2) : (light ? COLOR_BTN_BORDER : 0xFFFCA5A5);
        int endCol = hover ? (light ? 0xFF94A3B8 : 0xFFF87171) : (light ? COLOR_BEVEL_SHADOW : 0xFFEF4444);

        drawHorizontalGradientLine(poseStackOrGraphics, x, y, w - 1, startCol, endCol, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, x, y, h - 1, startCol, endCol, alpha);
    }

    // Viewport Scissor Helpers
    // Scissor rectangles are given in the layout's effective coordinates; while renderInternal draws
    // the screen shrunk by the UI scale (small GUI canvases), they must be shrunk by the same factor.
    private float scissorUiScale = 1.0f;

    private void enableGuiScissor(Object poseStackOrGraphics, int guiX, int guiY, int guiW, int guiH) {
        if (isClosing) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return;
        int[] rect = scissorRect(guiX, guiY, guiW, guiH, mc.getWindow().getGuiScale() * scissorUiScale,
                mc.getWindow().getHeight());
        if (rect[2] > 0 && rect[3] > 0) {
            enableScissor(poseStackOrGraphics, rect[0], rect[1], rect[2], rect[3]);
        }
    }

    static int[] scissorRect(int guiX, int guiY, int guiW, int guiH, double scale, int windowHeight) {
        return new int[] {
                (int) Math.round(guiX * scale),
                (int) Math.round(windowHeight - (guiY + guiH) * scale),
                (int) Math.round(guiW * scale),
                (int) Math.round(guiH * scale)
        };
    }

    private void disableGuiScissor(Object poseStackOrGraphics) {
        if (isClosing) return;
        disableScissor();
    }

    private void drawInsetBox(Object poseStackOrGraphics, int x, int y, int w, int h, int fillRgb, float alpha) {
        drawBeveledComponent(poseStackOrGraphics, x, y, w, h, fillRgb, alpha);
    }

    private void drawGroove(Object poseStackOrGraphics, int x, int y, int w, boolean horizontal, float alpha) {
        int dark = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int light = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
        if (horizontal) {
            fill(poseStackOrGraphics, x, y, x + w, y + 1, dark);
            fill(poseStackOrGraphics, x, y + 1, x + w, y + 2, light);
        }
    }

    private static void drawBorder(Object poseStackOrGraphics, int x, int y, int w, int h, int color) {
        fill(poseStackOrGraphics, x, y, x + w, y + 1, color);
        fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, color);
        fill(poseStackOrGraphics, x, y + 1, x + 1, y + h - 1, color);
        fill(poseStackOrGraphics, x + w - 1, y + 1, x + w, y + h - 1, color);
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
            draw(poseStackOrGraphics, font, text, x, y, color);
        } else {
            drawShadow(poseStackOrGraphics, font, text, x, y, color);
        }
    }

    private void drawCenteredText(Object poseStackOrGraphics, Font font, String text, int centerX, int y, int color) {
        if (text == null) return;
        int textW = font.width(text);
        int startX = centerX - textW / 2;
        if (currentTheme != null && currentTheme.isLight()) {
            draw(poseStackOrGraphics, font, text, startX, y, color);
        } else {
            drawShadow(poseStackOrGraphics, font, text, startX, y, color);
        }
    }

    // Top-center crest frame.
    private static final ResourceLocation LOGO_TEXTURE = new ResourceLocation("buildscape", "textures/gui/logo.png");

    private void drawTopCrestBadge(Object poseStackOrGraphics, Font font, int frameX, int frameY, int frameW, float alpha) {
        int crestW = 100;
        int crestH = 40;
        int crestX = frameX + (frameW - crestW) / 2;
        int crestY = frameY - 20;

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);
        int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
        int bShadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        int bHighlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);

        // 1. Solid Outer Chassis Body Fill
        fill(poseStackOrGraphics, crestX, crestY, crestX + crestW, crestY + crestH, applyAlpha(COLOR_CHASSIS_BODY, alpha));

        // 2. 1px Black Border Outline (Single continuous 1px black line throughout)
        drawBorder(poseStackOrGraphics, crestX, crestY, crestW, crestH, black);

        // 3. Layer 1: Outer metallic highlight (top & left) & shadow (bottom & right)
        drawHorizontalGradientLine(poseStackOrGraphics, crestX + 1, crestY + 1, crestW - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, alpha);
        drawVerticalGradientLine(poseStackOrGraphics, crestX + 1, crestY + 1, crestH - 2, COLOR_BORDER_GRADIENT_START, COLOR_BORDER_SHADOW, alpha);
        fill(poseStackOrGraphics, crestX + 1, crestY + crestH - 2, crestX + crestW - 1, crestY + crestH - 1, shadow);
        fill(poseStackOrGraphics, crestX + crestW - 2, crestY + 1, crestX + crestW - 1, crestY + crestH - 1, shadow);

        // 4. Layer 3: Inner Recessed Plate with unified 4px border width on all sides
        int plateX = crestX + 4;
        int plateY = crestY + 4;
        int plateW = crestW - 8;
        int plateH = crestH - 8;
        fill(poseStackOrGraphics, plateX, plateY, plateX + plateW, plateY + plateH, applyAlpha(COLOR_INNER_DARK, alpha));

        // Inner plate 1px subtle bevel (shadow top & left, highlight bottom & right)
        fill(poseStackOrGraphics, plateX, plateY, plateX + plateW, plateY + 1, bShadow);
        fill(poseStackOrGraphics, plateX, plateY, plateX + 1, plateY + plateH, bShadow);
        fill(poseStackOrGraphics, plateX, plateY + plateH - 1, plateX + plateW, plateY + plateH, bHighlight);
        fill(poseStackOrGraphics, plateX + plateW - 1, plateY, plateX + plateW, plateY + plateH, bHighlight);

        // 5. Proportional Buildscape Logo Texture (Crisp 256x143 native scaling, unstretched)
        int maxLogoW = plateW - 4;
        int maxLogoH = plateH - 2;
        int logoH = maxLogoH;
        int logoW = (int) Math.round(logoH * (256.0 / 143.0));
        if (logoW > maxLogoW) {
            logoW = maxLogoW;
            logoH = (int) Math.round(logoW * (143.0 / 256.0));
        }
        int logoX = plateX + (plateW - logoW) / 2;
        int logoY = plateY + (plateH - logoH) / 2;

        try {
            blit(poseStackOrGraphics, LOGO_TEXTURE, logoX, logoY, logoW, logoH, 0.0f, 0.0f, 256, 143, 256, 143);
        } catch (Exception e) {
            int textCol = applyAlpha(COLOR_ACCENT, alpha);
            drawCenteredText(poseStackOrGraphics, font, "✦ BUILDSCAPE ✦", crestX + crestW / 2, plateY + (plateH - 8) / 2, textCol);
        }
    }

    // Shared custom scrollbar.
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
        fill(poseStackOrGraphics, trackX, trackY, trackX + trackW, trackY + trackH, applyAlpha(COLOR_SLOT_BG, alpha * 0.75f));
        drawBorder(poseStackOrGraphics, trackX, trackY, trackW, trackH, applyAlpha(COLOR_FRAME_BLACK, alpha * 0.40f));

        // Thumb: clean metallic button with bevels (no weird gradient banding)
        boolean hoverThumb = mouseX >= trackX && mouseX <= trackX + trackW && mouseY >= thumbY && mouseY <= thumbY + thumbH;
        int thumbColor = isDraggingScrollbar ? COLOR_ACCENT : (hoverThumb ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG_ACTIVE);

        // Thumb fill
        fill(poseStackOrGraphics, trackX + 1, thumbY + 1, trackX + trackW - 1, thumbY + thumbH - 1, applyAlpha(thumbColor, alpha));

        // Thumb 1px border & bevel
        int highlight = applyAlpha(COLOR_BEVEL_HIGHLIGHT, alpha);
        int shadow = applyAlpha(COLOR_BEVEL_SHADOW, alpha);
        fill(poseStackOrGraphics, trackX, thumbY, trackX + trackW, thumbY + 1, highlight);
        fill(poseStackOrGraphics, trackX, thumbY + 1, trackX + 1, thumbY + thumbH - 1, highlight);
        fill(poseStackOrGraphics, trackX, thumbY + thumbH - 1, trackX + trackW, thumbY + thumbH, shadow);
        fill(poseStackOrGraphics, trackX + trackW - 1, thumbY + 1, trackX + trackW, thumbY + thumbH - 1, shadow);
    }

    // In-world pillar location handling.
    private void locatePillarInWorld(PillarIdManager.PillarData data) {
        if (data == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            String curDim = getDimensionId(mc.level).toString();
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

    // Configuration row rendering helpers.
    private void renderRowCardBase(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, int mouseX, int mouseY, float alpha) {
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, null, mouseX, mouseY, alpha);
    }

    private void renderRowCardBase(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, ResourceLocation icon, int mouseX, int mouseY, float alpha) {
        if (y + h < clipY || y > clipY + clipH) return;

        boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h && mouseY >= clipY && mouseY <= clipY + clipH;
        int bg = hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG;
        drawBeveledButton(poseStackOrGraphics, x, y, w, h, bg, hover, false, alpha);

        int contentX = x + 6;
        if (icon != null) {
            int iconY = y + (h - 16) / 2;
            int iconCol = hover ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER;
            drawTintedTexture(poseStackOrGraphics, icon, contentX, iconY, 16, 16, iconCol, alpha);
            contentX += 22;
        }

        drawText(poseStackOrGraphics, font, label, contentX, y + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
        if (desc != null && !desc.isEmpty()) {
            drawText(poseStackOrGraphics, font, desc, contentX, y + 15, applyAlpha(COLOR_TEXT_MUTED, alpha));
        }
    }

    private void renderToggleButton(Object poseStackOrGraphics, int x, int y, boolean value, boolean hover, float alpha) {
        renderToggleButton(poseStackOrGraphics, null, x, y, value, hover, alpha);
    }

    private void renderToggleButton(Object poseStackOrGraphics, String key, int x, int y, boolean value, boolean hover, float alpha) {
        if (key == null) {
            key = "tb_" + x + "_" + y;
        }

        float target = value ? 1.0f : 0.0f;
        Float currentProg = toggleAnimProgressMap.get(key);
        Boolean lastState = toggleLastStateMap.get(key);

        if (currentProg == null || lastState == null) {
            currentProg = target;
            toggleAnimProgressMap.put(key, target);
            toggleLastStateMap.put(key, value);
        } else if (lastState != value) {
            toggleLastStateMap.put(key, value);
            toggleAnimStartTimeMap.put(key, System.currentTimeMillis());
        }

        Long startTime = toggleAnimStartTimeMap.get(key);
        float progress = currentProg;
        if (startTime != null) {
            long elapsed = System.currentTimeMillis() - startTime;
            int duration = Math.max(1, (int) (180 * getAnimSpeedMultiplier()));
            if (elapsed < duration) {
                float t = easeOutCubic((float) elapsed / duration);
                float from = value ? 0.0f : 1.0f;
                progress = from + (target - from) * t;
                toggleAnimProgressMap.put(key, progress);
            } else {
                progress = target;
                toggleAnimProgressMap.put(key, target);
                toggleAnimStartTimeMap.remove(key);
            }
        }

        int w = 26;
        int h = 15;
        int accent = hover ? COLOR_ACCENT_GLOW : COLOR_ACCENT;

        if (progress <= 0.001f) {
            drawTintedTexture(poseStackOrGraphics, TOGGLE_OFF_TINT_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, accent, alpha);
            drawTintedTexture(poseStackOrGraphics, TOGGLE_OFF_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, 0xFFFFFFFF, alpha);
        } else if (progress >= 0.999f) {
            drawTintedTexture(poseStackOrGraphics, TOGGLE_ON_TINT_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, accent, alpha);
            drawTintedTexture(poseStackOrGraphics, TOGGLE_ON_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, 0xFFFFFFFF, alpha);
        } else {
            // Smooth cross-fade between OFF and ON states in the active theme accent color
            float offAlpha = (1.0f - progress) * alpha;
            float onAlpha = progress * alpha;

            drawTintedTexture(poseStackOrGraphics, TOGGLE_OFF_TINT_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, accent, offAlpha);
            drawTintedTexture(poseStackOrGraphics, TOGGLE_OFF_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, 0xFFFFFFFF, offAlpha);

            drawTintedTexture(poseStackOrGraphics, TOGGLE_ON_TINT_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, accent, onAlpha);
            drawTintedTexture(poseStackOrGraphics, TOGGLE_ON_TEXTURE, x, y, w, h, 0.0f, 0.0f, 24, 13, 24, 13, 0xFFFFFFFF, onAlpha);
        }
    }

    private void renderConfigRowToggle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, boolean value, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        hoverWorldOrPillarAccess(checkToggleClick(mouseX, mouseY, x, y, w, h, clipY, clipH), label);
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, "", mouseX, mouseY, alpha);

        int btnW = 26;
        int btnH = 15;
        int btnX = x + w - btnW - 12;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX - 2 && mouseX <= btnX + btnW + 2 && mouseY >= btnY - 2 && mouseY <= btnY + btnH + 2;
        renderToggleButton(poseStackOrGraphics, "cfg_" + label, btnX, btnY, value, hoverBtn, alpha);

        if (desc != null && !desc.isEmpty()) {
            int descriptionWidth = calculateToggleDescriptionWidth(w, btnW);
            int maxLines = h >= 32 ? 2 : 1;
            List<String> lines = wrapTextToLines(font, desc, descriptionWidth, maxLines);
            for (int line = 0; line < lines.size(); line++) {
                drawText(poseStackOrGraphics, font, lines.get(line), x + 6, y + 15 + line * 9, applyAlpha(COLOR_TEXT_MUTED, alpha));
            }
        }
    }

    private void renderConfigRowCycle(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String currentVal, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        hoverWorldOrPillarAccess(checkCycleClick(mouseX, mouseY, x, y, w, h, clipY, clipH) != 0, label);
        int cycleW = 138;
        String visibleDescription = trimTextToWidth(font, desc, calculateCycleDescriptionWidth(w, cycleW), false);
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, visibleDescription, mouseX, mouseY, alpha);

        int cycleH = 15;
        int cycleX = x + w - cycleW - 8;
        int cycleY = y + (h - cycleH) / 2;
        int arrowW = 14;

        // 1. Left Arrow Button [ ‹ ]
        boolean hoverPrev = mouseX >= cycleX && mouseX <= cycleX + arrowW && mouseY >= cycleY && mouseY <= cycleY + cycleH;
        drawBeveledButton(poseStackOrGraphics, cycleX, cycleY, arrowW, cycleH, hoverPrev ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPrev, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "‹", cycleX + arrowW / 2, cycleY + 3, applyAlpha(hoverPrev ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // 2. Middle Value Container
        int valX = cycleX + arrowW + 1;
        int valW = cycleW - (arrowW * 2) - 2;
        boolean hoverVal = mouseX >= valX && mouseX <= valX + valW && mouseY >= cycleY && mouseY <= cycleY + cycleH;
        fill(poseStackOrGraphics, valX, cycleY, valX + valW, cycleY + cycleH, applyAlpha(hoverVal ? COLOR_BTN_BG_HOVER : COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, cycleY, valW, cycleH, applyAlpha(hoverVal ? COLOR_ROW_BORDER_HOVER : COLOR_SLOT_BORDER, alpha));

        String displayVal = currentVal;
        if (font.width(displayVal) > valW - 6) {
            while (displayVal.length() > 3 && font.width(displayVal + "…") > valW - 6) {
                displayVal = displayVal.substring(0, displayVal.length() - 1);
            }
            displayVal = displayVal + "…";
        }
        drawCenteredText(poseStackOrGraphics, font, displayVal, valX + valW / 2, cycleY + 3, applyAlpha(hoverVal ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

        // 3. Right Arrow Button [ › ]
        int rightX = cycleX + cycleW - arrowW;
        boolean hoverNext = mouseX >= rightX && mouseX <= rightX + arrowW && mouseY >= cycleY && mouseY <= cycleY + cycleH;
        drawBeveledButton(poseStackOrGraphics, rightX, cycleY, arrowW, cycleH, hoverNext ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverNext, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "›", rightX + arrowW / 2, cycleY + 3, applyAlpha(hoverNext ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowCycleDetailed(Object graphics, Font font, int x, int y, int w, int h, int clipY,
                                              int clipH, String label, String description, String value,
                                              int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderConfigRowCycle(graphics, font, x, y, w, h, clipY, clipH, label, "", value, mouseX, mouseY, alpha);
        int descriptionWidth = calculateCycleDescriptionWidth(w, 138);
        List<String> lines = wrapTextToLines(font, description, descriptionWidth, 2);
        for (int line = 0; line < lines.size(); line++) {
            drawText(graphics, font, lines.get(line), x + 6, y + 15 + line * 9, applyAlpha(COLOR_TEXT_MUTED, alpha));
        }
    }

    private List<String> wrapTextToLines(Font font, String text, int maxWidth, int maxLines) {
        return textLayouts.get(new TextLayoutKey(font, text, maxWidth, maxLines),
                key -> calculateWrappedText(font, text, maxWidth, maxLines));
    }

    private static List<String> calculateWrappedText(Font font, String text, int maxWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        String remaining = text == null ? "" : text.trim();
        while (!remaining.isEmpty() && lines.size() < maxLines) {
            if (font.width(remaining) <= maxWidth) {
                lines.add(remaining);
                break;
            }
            if (lines.size() == maxLines - 1) {
                lines.add(trimTextToWidth(font, remaining, maxWidth, false));
                break;
            }
            String line = trimTextToWidth(font, remaining, maxWidth, false);
            int split = line.lastIndexOf(' ');
            if (split > 0) line = line.substring(0, split);
            if (line.isEmpty()) {
                lines.add(trimTextToWidth(font, remaining, maxWidth, false));
                break;
            }
            lines.add(line);
            remaining = remaining.substring(Math.min(remaining.length(), line.length())).trim();
        }
        return lines;
    }

    private void renderConfigRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String valText, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        hoverWorldOrPillarAccess(checkCounterClick(mouseX, mouseY, x, y, w, h, clipY, clipH) != 0, label);
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
        fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, valX, counterY, valW, counterH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, valText, valX + valW / 2, counterY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = counterX + counterW - stepBtnW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepBtnW && mouseY >= counterY && mouseY <= counterY + counterH;
        drawBeveledButton(poseStackOrGraphics, plusX, counterY, stepBtnW, counterH, hoverPlus ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverPlus, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, "+", plusX + stepBtnW / 2, counterY + 4, applyAlpha(hoverPlus ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    /** A particle counter: the value as stored, and its allowed range in the hover tooltip. */
    private void renderConfigRowCounter(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, ParticleRange range, double value, int mouseX, int mouseY, float alpha) {
        renderConfigRowCounter(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, range.formatValue(value), mouseX, mouseY, alpha);
        if (y < clipY || y + h > clipY + clipH) return;
        int counterX = x + w - 86 - 8;
        int counterY = y + (h - 16) / 2;
        if (mouseX >= counterX && mouseX <= counterX + 86 && mouseY >= counterY && mouseY <= counterY + 16) {
            deferredTooltip = Collections.singletonList(ComponentHelper.literal(range.tooltip(value)));
            deferredTooltipX = rawMouseX + 12;
            deferredTooltipY = rawMouseY;
        }
    }

    private void renderConfigRowButton(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String btnText, int mouseX, int mouseY, float alpha) {
        renderConfigRowButton(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, btnText, null, mouseX, mouseY, alpha);
    }

    private void renderConfigRowButton(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String btnText, ResourceLocation icon, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        hoverWorldOrPillarAccess(checkButtonClick(mouseX, mouseY, x, y, w, h, clipY, clipH, getConfigRowButtonWidth(font, btnText)), label);
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, icon, mouseX, mouseY, alpha);

        int btnW = getConfigRowButtonWidth(font, btnText);
        int btnH = 14;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;

        boolean hoverBtn = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        drawBeveledButton(poseStackOrGraphics, btnX, btnY, btnW, btnH, hoverBtn ? COLOR_BTN_BG_HOVER : COLOR_BTN_BG, hoverBtn, false, alpha);
        drawCenteredText(poseStackOrGraphics, font, btnText, btnX + btnW / 2, btnY + 3, applyAlpha(hoverBtn ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
    }

    private static int getConfigRowButtonWidth(Font font, String buttonText) {
        return Math.max(86, font.width(buttonText) + 14);
    }

    private void renderConfigRowBadge(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, String desc, String badgeText, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        renderRowCardBase(poseStackOrGraphics, font, x, y, w, h, clipY, clipH, label, desc, -1, -1, alpha);

        int textW = font.width(badgeText);
        int badgeW = textW + 10;
        int badgeH = 14;
        int badgeX = x + w - badgeW - 8;
        int badgeY = y + (h - badgeH) / 2;

        fill(poseStackOrGraphics, badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, applyAlpha(COLOR_SLOT_BG, alpha));
        drawBorder(poseStackOrGraphics, badgeX, badgeY, badgeW, badgeH, applyAlpha(COLOR_SLOT_BORDER, alpha));
        drawCenteredText(poseStackOrGraphics, font, badgeText, badgeX + badgeW / 2, badgeY + 3, applyAlpha(COLOR_TEXT_SILVER, alpha));
    }

    private void renderConfigRowPalette(Object poseStackOrGraphics, Font font, int x, int y, int w, int h, int clipY, int clipH, String label, List<String> colors, int maxColors, int mouseX, int mouseY, float alpha) {
        if (y < clipY || y + h > clipY + clipH) return;
        hoverWorldOrPillarAccess(paletteControlHovered(mouseX, mouseY, x, y, w, h, clipY, clipH), label);
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
        fill(poseStackOrGraphics, valX, counterY, valX + valW, counterY + counterH, applyAlpha(COLOR_SLOT_BG, alpha));
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
            fill(poseStackOrGraphics, swX, swatchY, swX + swatchSize, swatchY + swatchSize, applyAlpha(colorInt, active ? alpha : alpha * 0.30f));
            int borderCol = active ? COLOR_TEXT_WHITE : COLOR_SLOT_BORDER;
            drawBorder(poseStackOrGraphics, swX, swatchY, swatchSize, swatchSize, applyAlpha(borderCol, alpha));
        }
    }

    // Color conversion helpers.
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

    static int parseHexColor(String value) {
        if (value == null) return -1;
        String normalized = value.trim();
        if (normalized.startsWith("#")) normalized = normalized.substring(1);
        if (normalized.length() != 6) return -1;
        try {
            return Integer.parseInt(normalized, 16);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private String currentPickerHex() {
        return String.format("#%06X", hsbToRgb(pickerHue, pickerSat, pickerBri) & 0xFFFFFF);
    }

    private boolean applyHexColorInput() {
        int rgb = parseHexColor(hexColorInput);
        if (rgb < 0) return false;
        hexColorInput = String.format("#%06X", rgb);
        float[] hsb = new float[3];
        rgbToHsb(0xFF000000 | rgb, hsb);
        pickerHue = hsb[0];
        pickerSat = hsb[1];
        pickerBri = hsb[2];
        if (sidePanelType == SidePanelType.PILLAR_DETAIL && editingColorIndex >= 0) {
            String activeId = selectedPillarDetailId != null ? selectedPillarDetailId : closingPillarDetailId;
            PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(activeId);
            if (data != null) {
                updateLivePillarColor(data);
            }
        }
        return true;
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

    // Mouse and keyboard event handling.
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

        // 0. Right Side Panel Clicks (when visible — panel is now entirely to the right of the shrunk main frame)
        if (sidePanelProgress > 0.05f && sidePanelType != SidePanelType.NONE && !isSidePanelClosing) {
            if (mouseX >= layout.sidePanelX && mouseX <= layout.sidePanelX + layout.sidePanelW
                    && mouseY >= layout.sidePanelY && mouseY <= layout.sidePanelY + layout.sidePanelH) {
                if (handleSidePanelClick(mouseX, mouseY, button, layout.sidePanelX, layout.sidePanelY, layout.sidePanelW, layout.sidePanelH)) {
                    return true;
                }
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

        // 1b. Header 3-Stage Theme Toggle Button Click
        String versionTag = getModVersionTag();
        int tagW = font.width(versionTag);
        int versionTagX = headerX + headerW - tagW - 28;
        int themeToggleW = 44;
        int themeToggleH = 16;
        int themeToggleX = versionTagX - themeToggleW - 10;
        int themeToggleY = headerY + (headerH - themeToggleH) / 2;
        if (mouseX >= themeToggleX && mouseX <= themeToggleX + themeToggleW
                && mouseY >= themeToggleY && mouseY <= themeToggleY + themeToggleH) {
            handleHeaderThemeToggleClick(mouseX, themeToggleX, themeToggleW);
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

        int catHeaderH = 30;
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

        // Inline Presets Toggle Button (top-right, inside items content header)
        if (selectedTabIndex >= 0 && selectedTabIndex < navItems.size() && "items".equals(navItems.get(selectedTabIndex).id)) {
            int presetBtnW = 32;
            int presetBtnH = 22;
            int presetBtnX = (contentX + contentW) - presetBtnW - 4;
            int presetBtnY = contentY + (catHeaderH - presetBtnH) / 2;
            if (mouseX >= presetBtnX && mouseX <= presetBtnX + presetBtnW
                    && mouseY >= presetBtnY && mouseY <= presetBtnY + presetBtnH) {
                boolean presetOpen = sidePanelProgress > 0.05f && sidePanelType == SidePanelType.PRESETS && !isSidePanelClosing;
                if (presetOpen) {
                    closeSidePanel();
                    try { BuildscapeClientConfig.get().setSidePanelExpanded(false); } catch (Exception ignored) {}
                } else {
                    openSidePanel(SidePanelType.PRESETS);
                }
                return true;
            }
        }

        if (mouseX >= contentX && mouseX <= layout.frameRight && mouseY >= contentY && mouseY <= contentY + contentH) {
            if (handleContentClick(mouseX, mouseY, button, contentX, contentY, contentW, contentH, layout.frameRight, catHeaderH)) {
                return true;
            }
        }

        if (activeTab != null && activeTab.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return false;
    }

    private boolean handleContentClick(double mouseX, double mouseY, int button, int contentX, int contentY, int contentW, int contentH, int frameRight, int catHeaderH) {
        NavItem current = navItems.get(selectedTabIndex);

        switch (current.id) {
            case "items" -> {
                // Inline Presets Toggle Button (top-right of content header)
                int presetBtnW = 32;
                int presetBtnH = 22;
                int presetBtnX = (contentX + contentW) - presetBtnW - 4;
                int presetBtnY = contentY + (catHeaderH - presetBtnH) / 2;
                if (mouseX >= presetBtnX && mouseX <= presetBtnX + presetBtnW
                        && mouseY >= presetBtnY && mouseY <= presetBtnY + presetBtnH) {
                    boolean presetOpen = sidePanelProgress > 0.05f && sidePanelType == SidePanelType.PRESETS && !isSidePanelClosing;
                    if (presetOpen) {
                        closeSidePanel();
                        try { BuildscapeClientConfig.get().setSidePanelExpanded(false); } catch (Exception ignored) {}
                    } else {
                        openSidePanel(SidePanelType.PRESETS);
                    }
                    return true;
                }

                PillarParticleConfig cfg = PillarParticleConfig.get();
                int curY = contentY + catHeaderH + 4;

                // Row 1: Sub-View Switcher (Display Items vs Display Tags)
                int row1H = 18;
                int subBtnW = (contentW - 16) / 2;
                int subBtnH = 15;
                int subY = curY + 1;

                int sub0X = contentX + 6;
                if (mouseX >= sub0X && mouseX <= sub0X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH) {
                    if (itemsSubTab != 0) {
                        itemsSubTab = 0;
                        itemsListDirty = true;
                        startItemContentTransition();
                    }
                    scrollOffset = 0;
                    return true;
                }

                int sub1X = sub0X + subBtnW + 4;
                if (mouseX >= sub1X && mouseX <= sub1X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH) {
                    if (itemsSubTab != 1) {
                        itemsSubTab = 1;
                        tagsListDirty = true;
                        loadRegisteredTags();
                        startItemContentTransition();
                    }
                    scrollOffset = 0;
                    return true;
                }

                curY += row1H + 2;

                int bar2H = 16;
                int toolbarX = contentX + 6;
                ItemToolbarGeometry toolbar = calculateItemToolbarGeometry(Math.max(120, contentW - 18));
                int pillW = toolbar.filterWidth();
                int pillH = getItemToolbarControlHeight();
                int pillY = curY + 1;

                for (int f = 0; f < 3; f++) {
                    int px = toolbarX + f * (pillW + toolbar.gap());
                    if (mouseX >= px && mouseX <= px + pillW && mouseY >= pillY && mouseY <= pillY + pillH) {
                        if (itemsSubTab == 0) {
                            itemFilterMode = f;
                            itemsListDirty = true;
                        } else {
                            tagFilterMode = f;
                            tagsListDirty = true;
                        }
                        startItemContentTransition();
                        scrollOffset = 0;
                        return true;
                    }
                }

                int searchX = toolbarX + 3 * (pillW + toolbar.gap());
                int searchW = toolbar.searchWidth();
                int namespaceW = toolbar.namespaceWidth();
                int searchY = pillY;
                int searchH = getItemToolbarControlHeight();
                if (mouseY >= searchY && mouseY <= searchY + searchH) {
                    if (mouseX >= searchX && mouseX <= searchX + searchW) {
                        focusedSearchField = itemsSubTab == 0 ? 1 : 2;
                        if (button == 1) {
                            clearActiveSearch();
                            return true;
                        }
                        long now = System.currentTimeMillis();
                        if (lastSearchClickField == focusedSearchField && now - lastSearchClickTime <= 350L) {
                            searchTextSelected = true;
                        } else {
                            searchTextSelected = false;
                        }
                        lastSearchClickField = focusedSearchField;
                        lastSearchClickTime = now;
                        return true;
                    }
                    int namespaceX = searchX + searchW + toolbar.gap();
                    if (mouseX >= namespaceX && mouseX <= namespaceX + namespaceW) {
                        List<String> namespaces = itemsSubTab == 0 ? availableModNamespaces : availableTagNamespaces;
                        int currentIndex = itemsSubTab == 0 ? itemSearchNamespaceIndex : tagSearchNamespaceIndex;
                        int direction = button == 1 || Screen.hasShiftDown() ? -1 : 1;
                        int optionCount = namespaces.size() + 1;
                        int option = (currentIndex + 1 + direction + optionCount) % optionCount;
                        if (itemsSubTab == 0) {
                            itemSearchNamespaceIndex = option - 1;
                            itemsListDirty = true;
                        } else {
                            tagSearchNamespaceIndex = option - 1;
                            tagsListDirty = true;
                        }
                        focusedSearchField = 0;
                        searchTextSelected = false;
                        startItemContentTransition();
                        scrollOffset = 0;
                        return true;
                    }
                }
                focusedSearchField = 0;
                searchTextSelected = false;
                curY += bar2H + 3;

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
                                    if (!requireConfigModificationAccess()) return true;
                                    if (!requireConfigFilesEditable(true, false, false)) return true;
                                    Item item = itemsToDisplay.get(index);
                                    ResourceLocation cid = ForgeRegistries.ITEMS.getKey(item);
                                    if (cid != null) {
                                        String idStr = cid.toString();
                                        if (cfg.items.contains(idStr)) {
                                            cfg.items.remove(idStr);
                                            cachedActiveItems.remove(item);
                                        } else {
                                            cfg.items.add(idStr);
                                            cachedActiveItems.add(item);
                                        }
                                        queueConfigUpload(ConfigSetting.ITEMS);
                                        itemsListDirty = true;
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
                                if (!requireConfigModificationAccess()) return true;
                                if (!requireConfigFilesEditable(true, false, false)) return true;
                                String tagId = tagsToDisplay.get(ti);
                                if (cfg.items.contains(tagId)) {
                                    cfg.items.remove(tagId);
                                    cachedActiveTags.remove(tagId);
                                } else {
                                    cfg.items.add(tagId);
                                    cachedActiveTags.add(tagId);
                                }
                                queueConfigUpload(ConfigSetting.ITEMS);
                                tagsListDirty = true;
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
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    cfg.use_pattern = !cfg.use_pattern;
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 2. Pattern Cycle
                int patStep = checkCycleClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (patStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    int pIndex = 0;
                    for (int i = 0; i < GLOBAL_PARTICLE_PATTERNS.length; i++) {
                        if (GLOBAL_PARTICLE_PATTERNS[i].equalsIgnoreCase(cfg.pattern)) {
                            pIndex = i;
                            break;
                        }
                    }
                    cfg.pattern = GLOBAL_PARTICLE_PATTERNS[(pIndex + patStep + GLOBAL_PARTICLE_PATTERNS.length) % GLOBAL_PARTICLE_PATTERNS.length];
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 3. Pattern Speed
                int pSpeedStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (pSpeedStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    Double next = stepRangeValue(ParticleRange.PATTERN_SPEED, "global", cfg.pattern_speed, pSpeedStep);
                    if (next == null) return true;
                    cfg.pattern_speed = next;
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 4. Pattern Spread
                int pSpreadStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (pSpreadStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    Double next = stepRangeValue(ParticleRange.PATTERN_SPREAD, "global", cfg.pattern_spread, pSpreadStep);
                    if (next == null) return true;
                    cfg.pattern_spread = next;
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 5. Pattern Intensity
                int pIntStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (pIntStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    Double next = stepRangeValue(ParticleRange.PATTERN_INTENSITY, "global", cfg.pattern_intensity, pIntStep);
                    if (next == null) return true;
                    cfg.pattern_intensity = next;
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 6. Basic Speed
                int speedStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (speedStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    Double next = stepRangeValue(ParticleRange.PARTICLE_SPEED, "global", cfg.particle_speed, speedStep);
                    if (next == null) return true;
                    cfg.particle_speed = next;
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 7. Basic Spread
                int spreadStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (spreadStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    Double next = stepRangeValue(ParticleRange.PARTICLE_SPREAD, "global", cfg.particle_spread, spreadStep);
                    if (next == null) return true;
                    cfg.particle_spread = next;
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 8. Lifetime
                int lifeStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (lifeStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    Double next = stepRangeValue(ParticleRange.PARTICLE_LIFETIME, "global", cfg.particle_lifetime, lifeStep);
                    if (next == null) return true;
                    cfg.particle_lifetime = (int) Math.round(next);
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 9. Density
                int denStep = checkCounterClick(mouseX, mouseY, x, curY, w, h, clipY, clipH);
                if (denStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    Double next = stepRangeValue(ParticleRange.PARTICLE_DENSITY, "global", cfg.particle_density, denStep);
                    if (next == null) return true;
                    cfg.particle_density = (int) Math.round(next);
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                // 10. Swatch Clicks -> Opens Draggable Color Picker Modal!
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    if (paletteControlHovered((int) mouseX, (int) mouseY, x, curY, w, h, clipY, clipH)) {
                        if (!requireConfigModificationAccess(ConfigSetting.COLORS)) return true;
                        if (!requireConfigFilesEditable(false, true, false)) return true;
                        while (cfg.particle_color.size() < 7) {
                            cfg.particle_color.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(cfg.particle_color.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
                        }
                    }

                    int counterW = 86;
                    int counterH = 16;
                    int counterX = x + w - counterW - 8;
                    int counterY = curY + (h - counterH) / 2;
                    int stepBtnW = 16;

                    if (mouseY >= counterY && mouseY <= counterY + counterH) {
                        if (mouseX >= counterX && mouseX <= counterX + stepBtnW) {
                            if (!requireConfigModificationAccess(ConfigSetting.COLORS)) return true;
                            if (!requireConfigFilesEditable(false, true, false)) return true;
                            cfg.max_particle_color = Math.max(1, cfg.max_particle_color - 1);
                            queueConfigUpload(ConfigSetting.COLORS);
                            return true;
                        } else if (mouseX >= counterX + counterW - stepBtnW && mouseX <= counterX + counterW) {
                            if (!requireConfigModificationAccess(ConfigSetting.COLORS)) return true;
                            if (!requireConfigFilesEditable(false, true, false)) return true;
                            cfg.max_particle_color = Math.min(7, cfg.max_particle_color + 1);
                            queueConfigUpload(ConfigSetting.COLORS);
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
                            if (!requireConfigModificationAccess(ConfigSetting.COLORS)) return true;
                            if (!requireConfigFilesEditable(false, true, false)) return true;
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

                if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH,
                        getConfigRowButtonWidth(Minecraft.getInstance().font, "↻ Reset Properties"))) {
                    if (!requireConfigModificationAccess(ConfigSetting.PARTICLES)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    resetParticleProperties(cfg);
                    queueConfigUpload(ConfigSetting.PARTICLES);
                    return true;
                }
                curY += h + gap;

                if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH,
                        getConfigRowButtonWidth(Minecraft.getInstance().font, "↻ Reset Colors"))) {
                    if (!requireConfigModificationAccess(ConfigSetting.COLORS)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    resetParticleColors(cfg);
                    queueConfigUpload(ConfigSetting.COLORS);
                    return true;
                }
            }
            case "world" -> {
                UUID playerUuid = getPlayerUuid();
                Level level = getLevel();
                int worldRowH = getWorldSettingRowHeight();
                curY += 14;

                // Row 0: Creative Tree Breaker
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    boolean val = !CosmeticsConfig.get().getCreativeTreeBreaker(playerUuid);
                    CosmeticsConfig.get().setCreativeTreeBreaker(playerUuid, val);
                    return true;
                }
                curY += worldRowH + gap;

                // Row 1: Shulker Box Preview
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    boolean val = !CosmeticsConfig.get().getShulkerPreview(playerUuid);
                    CosmeticsConfig.get().setShulkerPreview(playerUuid, val);
                    return true;
                }
                curY += worldRowH + gap;

                curY += 14;

                // Row 2: Fast Leaf Decay
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    if (!requireConfigModificationAccess()) return true;
                    boolean cur = level != null ? getGameRuleBoolean(level, ModGameRules.FAST_LEAF_DECAY, ModGameRules.clientFastLeafDecay) : ModGameRules.clientFastLeafDecay;
                    ModGameRules.clientFastLeafDecay = !cur;
                    ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("fastLeafDecay", !cur));
                    return true;
                }
                curY += worldRowH + gap;

                // Row 3: Disable Enderman Griefing
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    if (!requireConfigModificationAccess()) return true;
                    boolean cur = level != null ? getGameRuleBoolean(level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing) : ModGameRules.clientDisableEndermanGriefing;
                    ModGameRules.clientDisableEndermanGriefing = !cur;
                    ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("disableEndermanGriefing", !cur));
                    return true;
                }
                curY += worldRowH + gap;

                // Row 4: Disable Creeper Griefing
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    if (!requireConfigModificationAccess()) return true;
                    boolean cur = level != null ? getGameRuleBoolean(level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing) : ModGameRules.clientDisableCreeperGriefing;
                    ModGameRules.clientDisableCreeperGriefing = !cur;
                    ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("disableCreeperGriefing", !cur));
                    return true;
                }
                curY += worldRowH + gap;

                // Row 5: Disable Ghast Griefing
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    if (!requireConfigModificationAccess()) return true;
                    boolean cur = level != null ? getGameRuleBoolean(level, ModGameRules.DISABLE_GHAST_GRIEFING, ModGameRules.clientDisableGhastGriefing) : ModGameRules.clientDisableGhastGriefing;
                    ModGameRules.clientDisableGhastGriefing = !cur;
                    ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("disableGhastGriefing", !cur));
                    return true;
                }
                curY += worldRowH + gap;

                // Row 6: Cake Stacking
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    if (!requireConfigModificationAccess()) return true;
                    boolean cur = level != null ? getGameRuleBoolean(level, ModGameRules.IS_CAKE_STACK, ModGameRules.clientCakeStacking) : ModGameRules.clientCakeStacking;
                    ModGameRules.clientCakeStacking = !cur;
                    ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("isCakeStack", !cur));
                    return true;
                }
                curY += worldRowH + gap;

                // Row 7: Water Bottle Stacking
                if (checkToggleClick(mouseX, mouseY, x, curY, w, worldRowH, clipY, clipH)) {
                    if (!requireConfigModificationAccess()) return true;
                    boolean cur = level != null ? getGameRuleBoolean(level, ModGameRules.IS_WATER_BOTTLE_STACK, ModGameRules.clientWaterBottleStacking) : ModGameRules.clientWaterBottleStacking;
                    ModGameRules.clientWaterBottleStacking = !cur;
                    ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("isWaterbottleStack", !cur));
                    return true;
                }
            }
            case "links" -> {
                for (ConfigLink link : CONFIG_LINKS) {
                    if (checkButtonClick(mouseX, mouseY, x, curY, w, h, clipY, clipH, 72)) {
                        try {
                            openUri(new URI(link.url()));
                        } catch (Exception error) {
                            com.kingodogo.buildscape.BuildScape.LOGGER.warn("Unable to open Buildscape link {}", link.url(), error);
                        }
                        return true;
                    }
                    curY += h + gap;
                }
            }
            case "system" -> {
                int systemRowH = getSystemSettingRowHeight();
                // Row 0: UI Theme Cycle
                int themeStep = checkCycleClick(mouseX, mouseY, x, curY, w, systemRowH, clipY, clipH);
                if (themeStep != 0) {
                    ConfigThemes.Theme next = (themeStep < 0) ? ConfigThemes.cyclePrevTheme() : ConfigThemes.cycleNextTheme();
                    applyTheme(next);
                    return true;
                }
                curY += systemRowH + gap;

                // Row 1: UI Background Cycle
                int bgStep = checkCycleClick(mouseX, mouseY, x, curY, w, systemRowH, clipY, clipH);
                if (bgStep != 0) {
                    if (bgStep < 0) {
                        ConfigBackgrounds.cyclePrevBackground();
                    } else {
                        ConfigBackgrounds.cycleNextBackground();
                    }
                    try {
                        BuildscapeClientConfig.get().setUiBackground(ConfigBackgrounds.getCurrentBackground().id);
                    } catch (Exception ignored) {}
                    return true;
                }
                curY += systemRowH + gap;

                // Row 2: Screen Animation Cycle
                int animStep = checkCycleClick(mouseX, mouseY, x, curY, w, systemRowH, clipY, clipH);
                if (animStep != 0) {
                    animSpeedIndex = (animSpeedIndex + animStep + ANIM_SPEEDS.length) % ANIM_SPEEDS.length;
                    try {
                        BuildscapeClientConfig.get().setAnimationSpeedIndex(animSpeedIndex);
                    } catch (Exception ignored) {}
                    return true;
                }
                curY += systemRowH + gap;

                // Row 3: Edit permission policy
                int permissionStep = checkCycleClick(mouseX, mouseY, x, curY, w, systemRowH, clipY, clipH);
                if (permissionStep != 0) {
                    if (!requireConfigModificationAccess(ConfigSetting.POLICY)) return true;
                    if (!requireConfigFilesEditable(false, true, false)) return true;
                    PillarParticleConfig cfg = PillarParticleConfig.get();
                    PillarParticleConfig.EditPermissionMode[] modes = PillarParticleConfig.EditPermissionMode.values();
                    int next = Math.floorMod(cfg.edit_permission_mode.ordinal() + permissionStep, modes.length);
                    cfg.edit_permission_mode = modes[next];
                    queueConfigUpload(ConfigSetting.POLICY);
                    return true;
                }
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
        Font font = Minecraft.getInstance().font;
        int relW = Math.max(60, font.width("⟳ Reload") + 14);
        int relX = x + 6;
        if (mouseX >= relX && mouseX <= relX + relW && mouseY >= barY && mouseY <= barY + btnH) {
            lastPillarsSyncTime = 0L;
            syncPillars();
            return true;
        }

        // [ ✕ Remove Selected (<N>) ]
        int remSelW = Math.max(105, font.width("✕ Remove Selected") + 14);
        int remSelX = relX + relW + 4;
        if (!selectedPillarIds.isEmpty() && mouseX >= remSelX && mouseX <= remSelX + remSelW && mouseY >= barY && mouseY <= barY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            if (!confirmDestructiveAction("remove:selected")) return true;
            List<String> ids = new ArrayList<>(selectedPillarIds);
            for (String id : ids) {
                PillarIdManager.getClient().removePillarData(id);
            }
            for (List<String> batch : removalBatches(ids)) {
                ModMessages.INSTANCE.sendToServer(new RemovePillarPacket(batch));
            }
            selectedPillarIds.clear();
            return true;
        }

        // [ ✕ Remove All ]
        int remAllW = Math.max(75, font.width("✕ Remove All") + 14);
        int remAllX = remSelX + remSelW + 4;
        if (!pillarList.isEmpty() && mouseX >= remAllX && mouseX <= remAllX + remAllW && mouseY >= barY && mouseY <= barY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            if (!confirmDestructiveAction("remove:all")) return true;
            List<String> ids = new ArrayList<>();
            for (PillarIdManager.PillarData data : pillarList) {
                if (data.id != null) {
                    ids.add(data.id);
                    PillarIdManager.getClient().removePillarData(data.id);
                }
            }
            for (List<String> batch : removalBatches(ids)) {
                ModMessages.INSTANCE.sendToServer(new RemovePillarPacket(batch));
            }
            selectedPillarIds.clear();
            return true;
        }

        int resetW = Math.max(78, font.width("↻ Reset Selected") + 14);
        int resetX = remAllX + remAllW + 4;
        if (!selectedPillarIds.isEmpty() && mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= barY && mouseY <= barY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            for (String id : selectedPillarIds) {
                PillarIdManager.clearPillarOverrides(PillarIdManager.getClient().getPillarData(id));
            }
            PillarIdManager.getClient().saveAllPillars();
            ModMessages.INSTANCE.sendToServer(new ResetPillarsPacket(selectedPillarIds));
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
        int colorsColW = 68;
        int dimColW = 58;
        int coordsColW = 96;
        int actionsColW = 58;
        int gap = 4;
        int idColW = Math.max(55, availW - (chkW + iconColW + colorsColW + dimColW + coordsColW + actionsColW + (gap * 6)));

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
                    if (!requireConfigModificationAccess()) return true;
                    PillarIdManager.getClient().removePillarData(data.id);
                    ModMessages.INSTANCE.sendToServer(new RemovePillarPacket(data.id));
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

        if (!requireConfigModificationAccess()) return true;

        // [ ✓ Save Pillar ]
        int saveW = 82;
        int saveX = x + w - saveW - 6;
        if (mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= barY && mouseY <= barY + btnH) {
            PillarIdManager.getClient().saveAllPillars();
            sendPillarUploadNow(data);
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
            queuePillarUpload(data);
            return true;
        }
        rY += rowH + rowGap;

        // Row 2: Trajectory Pattern Cycle
        int pillarPatStep = checkCycleClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (pillarPatStep != 0) {
            String curPat = data.pattern != null && !data.pattern.isEmpty() ? data.pattern : "default";
            int idx = 0;
            for (int i = 0; i < PARTICLE_PATTERNS.length; i++) {
                if (PARTICLE_PATTERNS[i].equalsIgnoreCase(curPat)) {
                    idx = i;
                    break;
                }
            }
            data.pattern = PARTICLE_PATTERNS[(idx + pillarPatStep + PARTICLE_PATTERNS.length) % PARTICLE_PATTERNS.length];
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        rY += rowH + rowGap;

        // Row 3: Pattern Speed Counter
        int speedStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (speedStep != 0) {
            double cur = data.pattern_speed != null ? data.pattern_speed : globalCfg.pattern_speed;
            Double next = stepRangeValue(ParticleRange.PATTERN_SPEED, data.id, cur, speedStep);
            if (next == null) return true;
            data.pattern_speed = next;
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        rY += rowH + rowGap;

        // Row 4: Pattern Spread Counter
        int spreadStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (spreadStep != 0) {
            double cur = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
            Double next = stepRangeValue(ParticleRange.PATTERN_SPREAD, data.id, cur, spreadStep);
            if (next == null) return true;
            data.pattern_spread = next;
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        rY += rowH + rowGap;

        // Row 5: Pattern Intensity Counter
        int intStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (intStep != 0) {
            double cur = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
            Double next = stepRangeValue(ParticleRange.PATTERN_INTENSITY, data.id, cur, intStep);
            if (next == null) return true;
            data.pattern_intensity = next;
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        rY += rowH + rowGap;

        // Row 6: Per-Pillar Color Palette
        List<String> colors = new ArrayList<>(data.dyeColors != null && !data.dyeColors.isEmpty() ? data.dyeColors : globalCfg.particle_color);
        while (colors.size() < 7) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(7, Math.max(1, colors.size())));

        int colorStep = checkCounterClick(mouseX, mouseY, rowX, rY, rowW, rowH, viewportY, viewportH);
        if (colorStep != 0) {
            data.max_particle_color = Math.max(1, Math.min(7, maxColors + colorStep));
            if (data.dyeColors == null) {
                data.dyeColors = new ArrayList<>(colors);
            }
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }

        int counterW = 86;
        int counterX = rowX + rowW - counterW - 8;
        int swatchSize = 13;
        int swatchGap = 3;
        int swatchesTotalW = 7 * (swatchSize + swatchGap) - swatchGap;
        int swatchStartX = counterX - swatchesTotalW - 10;
        int swatchY = rY + (rowH - swatchSize) / 2;

        for (int i = 0; i < 7; i++) {
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
            sendPillarUploadNow(data);
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
            PillarParticleConfig.EditScope colorScope = isEditingPillarColor
                    ? ConfigSetting.PILLAR_EDIT.scope : ConfigSetting.COLORS.scope;
            if (!requireConfigModificationAccess(colorScope)) return true;
            if (!isEditingPillarColor && !requireConfigFilesEditable(false, true, false)) return true;
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
                    } else {
                        data.dyeColors = new ArrayList<>(data.dyeColors);
                    }
                    while (data.dyeColors.size() <= editingColorIndex) {
                        data.dyeColors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(data.dyeColors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
                    }
                    data.dyeColors.set(editingColorIndex, hexString);
                    PillarIdManager.getClient().updatePillarData(data);
                    queuePillarUpload(data);
                }
            } else {
                PillarParticleConfig cfg = PillarParticleConfig.get();
                if (editingColorIndex < cfg.particle_color.size()) {
                    cfg.particle_color.set(editingColorIndex, hexString);
                } else {
                    cfg.particle_color.add(hexString);
                }
                queueConfigUpload(ConfigSetting.COLORS);
            }

            showColorModal = false;
            draggingGradient = false;
            draggingHue = false;
            return true;
        }

        return true;
    }

    private boolean checkSideRowToggleClick(double mouseX, double mouseY, int x, int y, int w, int h) {
        int btnW = 26;
        int btnH = 15;
        int btnX = x + w - btnW - 8;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX - 3 && mouseX <= btnX + btnW + 3 && mouseY >= btnY - 3 && mouseY <= btnY + btnH + 3;
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
        SidePanelContentGeometry geometry = calculateSidePanelContentGeometry(x, y, w, h);
        int sHeadX = geometry.headerX();
        int sHeadY = geometry.headerY();
        int sHeadW = geometry.headerWidth();
        int sHeadH = 22;

        int closeSize = 14;
        int closeX = sHeadX + sHeadW - closeSize - 4;
        int closeY = sHeadY + 4;
        if (mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize) {
            if (sidePanelType == SidePanelType.PRESETS) {
                try {
                    BuildscapeClientConfig.get().setSidePanelExpanded(false);
                } catch (Exception ignored) {}
            }
            closeSidePanel();
            return true;
        }

        int sContentX = geometry.contentX();
        int sContentY = geometry.contentY();
        int sContentW = geometry.contentWidth();
        int sContentH = geometry.contentHeight();

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
        float deleteReveal = updatePresetDeleteReveal(!isDefault);
        int groupH = 218;
        PresetPanelGeometry geometry = calculatePresetPanelGeometry(x, y, w, h, groupH, 284);
        int rowX = geometry.x();
        int rowW = geometry.width();
        int curY = geometry.y();
        int cardPad = 6;
        int btnH = 18;
        int selectorX = rowX + cardPad;
        int selectorW = rowW - cardPad * 2;
        int arrowW = 20;
        int selectorY = curY + 18;

        if (mouseY >= selectorY && mouseY <= selectorY + btnH && !presets.isEmpty()) {
            if (mouseX >= selectorX && mouseX <= selectorX + arrowW) {
                selectedPresetIndex = cyclePresetIndex(selectedPresetIndex, -1, presets.size());
                presetNameInput = "";
                presetNameFocused = false;
                presetNameSelected = false;
                return true;
            }
            int nextX = selectorX + selectorW - arrowW;
            if (mouseX >= nextX && mouseX <= nextX + arrowW) {
                selectedPresetIndex = cyclePresetIndex(selectedPresetIndex, 1, presets.size());
                presetNameInput = "";
                presetNameFocused = false;
                presetNameSelected = false;
                return true;
            }
        }

        int applyY = selectorY + btnH + 5;
        if (mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= applyY && mouseY <= applyY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            if (!requireConfigFilesEditable(true, false, true)) return true;
            if (!keys.isEmpty() && selectedPresetIndex < keys.size()) {
                presetsCfg.applyPreset(keys.get(selectedPresetIndex));
                if (showPresetReadOnlyFailure(presetsCfg)) return true;
                rememberPreset(keys.get(selectedPresetIndex));
                queueConfigUpload(ConfigSetting.ITEMS);
                rebuildActiveItemsCache();
                itemsListDirty = true;
            }
            return true;
        }

        int sectionGap = Math.round(8.0F - 4.0F * deleteReveal);
        int resetGap = Math.round(18.0F - 12.0F * deleteReveal);
        curY += 64 + sectionGap;
        int manageY = curY + 19;
        if (mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= manageY && mouseY <= manageY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            if (!requireConfigFilesEditable(false, false, true)) return true;
            if (!presets.isEmpty() && selectedPresetIndex < keys.size()) {
                String key = keys.get(selectedPresetIndex);
                PillarParticleConfig cfg = PillarParticleConfig.get();
                String currentName = presets.get(selectedPresetIndex).name;
                boolean saved = presetsCfg.savePreset(key, currentName, new HashSet<>(cfg.items));
                if (showPresetReadOnlyFailure(presetsCfg)) return true;
                if (saved) {
                    rebuildActiveItemsCache();
                    itemsListDirty = true;
                }
            }
            return true;
        }

        if (!isDefault && deleteReveal > 0.75F) {
            int deleteY = manageY + btnH + 4;
            if (mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= deleteY && mouseY <= deleteY + btnH) {
                if (!requireConfigModificationAccess()) return true;
                if (!requireConfigFilesEditable(true, false, true)) return true;
                presetsCfg.deletePreset(currentKey);
                if (showPresetReadOnlyFailure(presetsCfg)) return true;
                selectedPresetIndex = 0;
                presetsCfg.applyPreset("default");
                if (showPresetReadOnlyFailure(presetsCfg)) return true;
                rememberPreset("default");
                queueConfigUpload(ConfigSetting.ITEMS);
                rebuildActiveItemsCache();
                itemsListDirty = true;
                return true;
            }
        }

        int manageCardH = Math.round(41.0F + 20.0F * deleteReveal);
        curY += manageCardH + sectionGap;
        int nameY = curY + 19;
        if (mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= nameY && mouseY <= nameY + btnH) {
            presetNameFocused = true;
            if (button == 1) {
                presetNameInput = "";
                presetNameSelected = false;
            } else {
                presetNameSelected = true;
            }
            return true;
        }

        int createY = nameY + btnH + 4;
        if (mouseX >= selectorX && mouseX <= selectorX + selectorW && mouseY >= createY && mouseY <= createY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            if (!requireConfigFilesEditable(false, false, true)) return true;
            createPresetFromInput();
            return true;
        }

        curY += 61 + resetGap;
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            if (!requireConfigFilesEditable(true, false, true)) return true;
            presetsCfg.applyPreset("default");
            if (showPresetReadOnlyFailure(presetsCfg)) return true;
            rememberPreset("default");
            queueConfigUpload(ConfigSetting.ITEMS);
            selectedPresetIndex = 0;
            rebuildActiveItemsCache();
            itemsListDirty = true;
            return true;
        }

        return false;
    }

    private void createPresetFromInput() {
        PresetsConfig presetsCfg = PresetsConfig.get();
        PillarParticleConfig cfg = PillarParticleConfig.get();
        String newKey = presetsCfg.generatePresetKey();
        boolean saved = presetsCfg.savePreset(newKey, normalizePresetName(presetNameInput), new HashSet<>(cfg.items));
        if (showPresetReadOnlyFailure(presetsCfg)) return;
        if (!saved && "limit".equals(presetsCfg.getLastEditFailure())) {
            flashDenied();
            showHeaderNotice(new TranslatableComponent("buildscape.config.preset.error.limit").getString());
            return;
        }
        if (saved) {
            selectedPresetIndex = presetsCfg.getPresets().size() - 1;
            presetNameInput = "";
            presetNameFocused = false;
            presetNameSelected = false;
            rebuildActiveItemsCache();
            itemsListDirty = true;
        }
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
            if (!requireConfigModificationAccess()) return true;
            boolean cur = data.use_pattern != null ? data.use_pattern : globalCfg.use_pattern;
            data.use_pattern = !cur;
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        curY += rowH + rowGap;

        // Row 2: Pattern Cycle
        if (checkSideRowCycleClick(mouseX, mouseY, rowX, curY, rowW, rowH)) {
            if (!requireConfigModificationAccess()) return true;
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
            queuePillarUpload(data);
            return true;
        }
        curY += rowH + rowGap;

        // Row 3: Speed Counter
        int speedStep = checkSideRowCounterClick(mouseX, mouseY, rowX, curY, rowW, rowH);
        if (speedStep != 0) {
            if (!requireConfigModificationAccess()) return true;
            double cur = data.pattern_speed != null ? data.pattern_speed : globalCfg.pattern_speed;
            Double next = stepRangeValue(ParticleRange.PATTERN_SPEED, data.id, cur, speedStep);
            if (next == null) return true;
            data.pattern_speed = next;
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        curY += rowH + rowGap;

        // Row 4: Spread Counter
        int spreadStep = checkSideRowCounterClick(mouseX, mouseY, rowX, curY, rowW, rowH);
        if (spreadStep != 0) {
            if (!requireConfigModificationAccess()) return true;
            double cur = data.pattern_spread != null ? data.pattern_spread : globalCfg.pattern_spread;
            Double next = stepRangeValue(ParticleRange.PATTERN_SPREAD, data.id, cur, spreadStep);
            if (next == null) return true;
            data.pattern_spread = next;
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        curY += rowH + rowGap;

        // Row 5: Intensity Counter
        int intStep = checkSideRowCounterClick(mouseX, mouseY, rowX, curY, rowW, rowH);
        if (intStep != 0) {
            if (!requireConfigModificationAccess()) return true;
            double cur = data.pattern_intensity != null ? data.pattern_intensity : globalCfg.pattern_intensity;
            Double next = stepRangeValue(ParticleRange.PATTERN_INTENSITY, data.id, cur, intStep);
            if (next == null) return true;
            data.pattern_intensity = next;
            PillarIdManager.getClient().updatePillarData(data);
            queuePillarUpload(data);
            return true;
        }
        curY += rowH + rowGap;

        // Row 6: Palette & Swatches
        List<String> colors = new ArrayList<>(data.dyeColors != null && !data.dyeColors.isEmpty() ? data.dyeColors : globalCfg.particle_color);
        while (colors.size() < 7) {
            colors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(colors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
        }
        int maxColors = data.max_particle_color != null ? data.max_particle_color : (globalCfg.max_particle_color != 0 ? globalCfg.max_particle_color : Math.min(7, Math.max(1, colors.size())));

        int counterW = 60;
        int counterH = 14;
        int counterX = rowX + rowW - counterW - 4;
        int counterY = curY + 2;
        int stepW = 13;
        if (mouseY >= counterY && mouseY <= counterY + counterH) {
            if (mouseX >= counterX && mouseX <= counterX + stepW) {
                if (!requireConfigModificationAccess()) return true;
                data.max_particle_color = Math.max(1, maxColors - 1);
                if (data.dyeColors == null) data.dyeColors = new ArrayList<>(colors);
                PillarIdManager.getClient().updatePillarData(data);
                queuePillarUpload(data);
                return true;
            } else if (mouseX >= counterX + counterW - stepW && mouseX <= counterX + counterW) {
                if (!requireConfigModificationAccess()) return true;
                data.max_particle_color = Math.min(7, maxColors + 1);
                if (data.dyeColors == null) data.dyeColors = new ArrayList<>(colors);
                PillarIdManager.getClient().updatePillarData(data);
                queuePillarUpload(data);
                return true;
            }
        }

        // Swatches click
        int swSize = 13;
        int swGap = 4;
        int swY = curY + 17;
        for (int i = 0; i < 7; i++) {
            int swX = rowX + 6 + i * (swSize + swGap);
            if (mouseX >= swX && mouseX <= swX + swSize && mouseY >= swY && mouseY <= swY + swSize) {
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
                    hexColorInput = colors.get(i);
                } catch (Exception ignored) {}
                hexColorFocused = false;
                return true;
            }
        }
        curY += 34 + rowGap;

        // Embedded Inline Color Picker Clicks
        if (editingColorIndex >= 0 && editingColorIndex < colors.size()) {
            int gradX = inlinePickerGradW > 0 ? inlinePickerGradX : (rowX + 1);
            int gradY = inlinePickerGradH > 0 ? inlinePickerGradY : (curY + 1);
            int gradW = inlinePickerGradW > 0 ? inlinePickerGradW : (rowW - 12 - 4 - 2);
            int gradH = inlinePickerGradH > 0 ? inlinePickerGradH : 68;
            int hueX = inlinePickerHueW > 0 ? inlinePickerHueX : (gradX + gradW + 4);
            int hueY = inlinePickerHueH > 0 ? inlinePickerHueY : gradY;
            int hueW = inlinePickerHueW > 0 ? inlinePickerHueW : 12;
            int hueH = inlinePickerHueH > 0 ? inlinePickerHueH : gradH;

            // 2D Gradient Box Click
            if (mouseX >= gradX && mouseX <= gradX + gradW && mouseY >= gradY && mouseY <= gradY + gradH) {
                if (!requireConfigModificationAccess(ConfigSetting.PILLAR_EDIT)) return true;
                draggingGradient = true;
                pickerSat = Math.max(0.0f, Math.min(1.0f, (float) (mouseX - gradX) / gradW));
                pickerBri = Math.max(0.0f, Math.min(1.0f, 1.0f - ((float) (mouseY - gradY) / gradH)));
                updateLivePillarColor(data);
                hexColorFocused = false;
                return true;
            }

            // Hue Slider Click
            if (mouseX >= hueX && mouseX <= hueX + hueW && mouseY >= hueY && mouseY <= hueY + hueH) {
                if (!requireConfigModificationAccess(ConfigSetting.PILLAR_EDIT)) return true;
                draggingHue = true;
                pickerHue = Math.max(0.0f, Math.min(360.0f, ((float) (mouseY - hueY) / hueH) * 360.0f));
                updateLivePillarColor(data);
                hexColorFocused = false;
                return true;
            }

            curY += gradH + 5;

            // Preview & Hex Input
            int prevSize = 14;
            int hexX = gradX + prevSize + 4;
            int hexW = rowW - prevSize - 6;
            if (mouseX >= hexX && mouseX <= hexX + hexW && mouseY >= curY && mouseY <= curY + prevSize) {
                if (!requireConfigModificationAccess(ConfigSetting.PILLAR_EDIT)) return true;
                hexColorFocused = true;
                hexColorSelected = false;
                return true;
            }

            curY += prevSize + 4;

            // Quick Preset Swatches Click
            int pSize = 11;
            int pGap = 3;
            int pCols = Math.max(1, (rowW - 2) / (pSize + pGap));
            int presetsToShow = Math.min(PALETTE_PRESETS.length, pCols);
            for (int p = 0; p < presetsToShow; p++) {
                int px = gradX + p * (pSize + pGap);
                if (mouseX >= px && mouseX <= px + pSize && mouseY >= curY && mouseY <= curY + pSize) {
                    if (!requireConfigModificationAccess(ConfigSetting.PILLAR_EDIT)) return true;
                    int rgb = PALETTE_PRESETS[p] & 0x00FFFFFF;
                    float[] hsb = new float[3];
                    rgbToHsb(rgb, hsb);
                    pickerHue = hsb[0];
                    pickerSat = hsb[1];
                    pickerBri = hsb[2];
                    updateLivePillarColor(data);
                    hexColorFocused = false;
                    return true;
                }
            }
            curY += pSize + 4 + rowGap;
        }

        // Row 7: Save Button
        int btnH = 16;
        if (mouseX >= rowX && mouseX <= rowX + rowW && mouseY >= curY && mouseY <= curY + btnH) {
            if (!requireConfigModificationAccess()) return true;
            PillarIdManager.getClient().saveAllPillars();
            sendPillarUploadNow(data);
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
        int hexX = gradX + prevSize + 6;
        int hexW = gradW + hueW + pad - prevSize - 6;
        if (mouseX >= hexX && mouseX <= hexX + hexW && mouseY >= curY && mouseY <= curY + prevSize) {
            hexColorFocused = true;
            if (button == 1) {
                hexColorInput = "";
                hexColorSelected = false;
            } else {
                if (hexColorInput.isEmpty()) {
                    hexColorInput = currentPickerHex();
                }
                hexColorSelected = true;
            }
            return true;
        }
        hexColorFocused = false;
        hexColorSelected = false;
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
            if (!requireConfigModificationAccess(isEditingPillarColor ? ConfigSetting.PILLAR_EDIT : ConfigSetting.COLORS)) return true;
            if (!isEditingPillarColor && !requireConfigFilesEditable(false, true, false)) return true;
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
                    } else {
                        data.dyeColors = new ArrayList<>(data.dyeColors);
                    }
                    while (data.dyeColors.size() <= editingColorIndex) {
                        data.dyeColors.add(PillarParticleConfig.DEFAULT_COLORS[Math.min(data.dyeColors.size(), PillarParticleConfig.DEFAULT_COLORS.length - 1)]);
                    }
                    data.dyeColors.set(editingColorIndex, hexString);
                    PillarIdManager.getClient().updatePillarData(data);
                    queuePillarUpload(data);
                }
            } else {
                PillarParticleConfig cfg = PillarParticleConfig.get();
                if (editingColorIndex < cfg.particle_color.size()) {
                    cfg.particle_color.set(editingColorIndex, hexString);
                } else {
                    cfg.particle_color.add(hexString);
                }
                queueConfigUpload(ConfigSetting.COLORS);
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
        int btnW = 26;
        int btnH = 15;
        int btnX = x + w - btnW - 12;
        int btnY = y + (h - btnH) / 2;
        return mouseX >= btnX - 3 && mouseX <= btnX + btnW + 3 && mouseY >= btnY - 3 && mouseY <= btnY + btnH + 3;
    }

    private boolean paletteControlHovered(int mouseX, int mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y < clipY || y + h > clipY + clipH) return false;
        if (checkCounterClick(mouseX, mouseY, x, y, w, h, clipY, clipH) != 0) return true;
        int startX = x + w - 86 - 8 - (7 * 16 - 3) - 10;
        int swatchY = y + (h - 13) / 2;
        for (int i = 0; i < 7; i++) {
            if (mouseX >= startX + i * 16 && mouseX <= startX + i * 16 + 13
                    && mouseY >= swatchY && mouseY <= swatchY + 13) return true;
        }
        return false;
    }

    private int checkCycleClick(double mouseX, double mouseY, int x, int y, int w, int h, int clipY, int clipH) {
        if (y < clipY || y + h > clipY + clipH) return 0;
        int cycleW = 138;
        int cycleH = 15;
        int cycleX = x + w - cycleW - 8;
        int cycleY = y + (h - cycleH) / 2;
        int arrowW = 14;

        if (mouseY >= cycleY && mouseY <= cycleY + cycleH) {
            // Left arrow button: go back
            if (mouseX >= cycleX && mouseX <= cycleX + arrowW) {
                return -1;
            }
            // Right arrow button: go forward
            int rightX = cycleX + cycleW - arrowW;
            if (mouseX >= rightX && mouseX <= rightX + arrowW) {
                return 1;
            }
            // Value container: left half goes back, right half goes forward
            int valX = cycleX + arrowW;
            int valW = cycleW - (arrowW * 2);
            if (mouseX >= valX && mouseX <= valX + valW) {
                return (mouseX < valX + valW / 2.0) ? -1 : 1;
            }
        }
        return 0;
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
            SidePanelContentGeometry geometry = calculateSidePanelContentGeometry(
                    layout.sidePanelX, layout.sidePanelY, layout.sidePanelW, layout.sidePanelH);
            int sContentX = geometry.contentX();
            int sContentY = geometry.contentY();
            int sContentW = geometry.contentWidth();

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

        if (sidePanelType == SidePanelType.PILLAR_DETAIL && editingColorIndex >= 0 && sidePanelProgress > 0.05f) {
            String activeId = selectedPillarDetailId != null ? selectedPillarDetailId : closingPillarDetailId;
            PillarIdManager.PillarData data = PillarIdManager.getClient().getPillarData(activeId);
            if (data != null && inlinePickerGradW > 0 && inlinePickerGradH > 0) {
                if (draggingGradient) {
                    pickerSat = Math.max(0.0f, Math.min(1.0f, (float) (mouseX - inlinePickerGradX) / inlinePickerGradW));
                    pickerBri = Math.max(0.0f, Math.min(1.0f, 1.0f - ((float) (mouseY - inlinePickerGradY) / inlinePickerGradH)));
                    updateLivePillarColor(data);
                    return true;
                }
                if (draggingHue && inlinePickerHueH > 0) {
                    pickerHue = Math.max(0.0f, Math.min(360.0f, ((float) (mouseY - inlinePickerHueY) / inlinePickerHueH) * 360.0f));
                    updateLivePillarColor(data);
                    return true;
                }
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
            if (mouseX >= layout.sidePanelX && mouseX <= layout.sidePanelX + layout.sidePanelW
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
        if (hexColorFocused) {
            if ((modifiers & 2) != 0 && keyCode == 65) {
                hexColorSelected = true;
                return true;
            }
            if ((modifiers & 2) != 0 && keyCode == 86) {
                String clipboard = getClipboardText();
                String candidate = clipboard == null ? "" : clipboard.trim();
                if (parseHexColor(candidate) >= 0) {
                    int rgb = parseHexColor(candidate);
                    hexColorInput = String.format("#%06X", rgb);
                    hexColorSelected = false;
                    applyHexColorInput();
                }
                return true;
            }
            if (keyCode == 259) {
                if (hexColorSelected) {
                    hexColorInput = "";
                    hexColorSelected = false;
                } else if (!hexColorInput.isEmpty()) {
                    hexColorInput = hexColorInput.substring(0, hexColorInput.length() - 1);
                }
                return true;
            }
            if (keyCode == 257) {
                if (applyHexColorInput()) {
                    hexColorFocused = false;
                    hexColorSelected = false;
                }
                return true;
            }
            if (keyCode == 256) {
                hexColorInput = currentPickerHex();
                hexColorFocused = false;
                hexColorSelected = false;
                return true;
            }
        }
        if (presetNameFocused) {
            if ((modifiers & 2) != 0 && keyCode == 65) {
                presetNameSelected = true;
                return true;
            }
            if ((modifiers & 2) != 0 && keyCode == 86) {
                presetNameInput = applyPresetNamePaste(presetNameInput, presetNameSelected, getClipboardText());
                presetNameSelected = false;
                return true;
            }
            if (keyCode == 259) {
                presetNameInput = applyPresetNameBackspace(presetNameInput, presetNameSelected);
                presetNameSelected = false;
                return true;
            }
            if (keyCode == 257) {
                if (!requireConfigModificationAccess()) return true;
                if (!requireConfigFilesEditable(false, false, true)) return true;
                createPresetFromInput();
                return true;
            }
            if (keyCode == 256) {
                presetNameFocused = false;
                presetNameSelected = false;
                return true;
            }
        }
        if (focusedSearchField != 0) {
            if ((modifiers & 2) != 0 && keyCode == 65) {
                searchTextSelected = true;
                return true;
            }
            if (keyCode == 259) {
                if (focusedSearchField == 1) {
                    itemSearchFilter = applySearchBackspace(itemSearchFilter, searchTextSelected);
                    itemsListDirty = true;
                } else if (focusedSearchField == 2) {
                    tagSearchFilter = applySearchBackspace(tagSearchFilter, searchTextSelected);
                    tagsListDirty = true;
                }
                searchTextSelected = false;
                resetSearchViewport();
                return true;
            }
            if (keyCode == 256 || keyCode == 257) {
                focusedSearchField = 0;
                searchTextSelected = false;
                return true;
            }
        }
        if ((modifiers & 2) != 0 && keyCode == 70 && selectedTabIndex == 0) {
            focusedSearchField = itemsSubTab == 0 ? 1 : 2;
            searchTextSelected = false;
            return true;
        }
        int tabDirection = tabKeyDirection(keyCode);
        if (tabDirection != 0) {
            selectTab(moveSelection(selectedTabIndex, tabDirection, navItems.size()));
            focusedSearchField = 0;
            searchTextSelected = false;
            presetNameFocused = false;
            presetNameSelected = false;
            return true;
        }
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
            if (showColorModal) {
                showColorModal = false;
                draggingGradient = false;
                draggingHue = false;
                return true;
            }
            if (sidePanelProgress > 0.05f && sidePanelType == SidePanelType.COLOR_PICKER) {
                closeSidePanel();
                return true;
            }
            if (selectedPillarDetailId != null && sidePanelType == SidePanelType.PILLAR_DETAIL) {
                selectedPillarDetailId = null;
                closeSidePanel();
                return true;
            }
            startClosing();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (hexColorFocused && (codePoint == '#' || Character.digit(codePoint, 16) >= 0)) {
            String next = hexColorSelected ? "" : hexColorInput;
            if (next.length() < 7) {
                hexColorInput = next + Character.toUpperCase(codePoint);
                hexColorSelected = false;
                if (parseHexColor(hexColorInput) >= 0) {
                    applyHexColorInput();
                }
            }
            return true;
        }
        if (presetNameFocused && !Character.isISOControl(codePoint)) {
            String next = presetNameSelected ? "" : presetNameInput;
            if (next.length() < 32) {
                presetNameInput = next + codePoint;
                presetNameSelected = false;
            }
            return true;
        }
        if (focusedSearchField != 0 && !Character.isISOControl(codePoint)) {
            if (focusedSearchField == 1 && (searchTextSelected || itemSearchFilter.length() < 64)) {
                itemSearchFilter = applySearchCharacter(itemSearchFilter, searchTextSelected, codePoint);
                itemsListDirty = true;
            } else if (focusedSearchField == 2 && (searchTextSelected || tagSearchFilter.length() < 64)) {
                tagSearchFilter = applySearchCharacter(tagSearchFilter, searchTextSelected, codePoint);
                tagsListDirty = true;
            }
            searchTextSelected = false;
            resetSearchViewport();
            return true;
        }
        if (activeTab != null && activeTab.charTyped(codePoint, modifiers)) {
            return true;
        }
        return false;
    }

    @Override
    public Component getNarrationMessage() {
        String tab = selectedTabIndex >= 0 && selectedTabIndex < navItems.size()
                ? navItems.get(selectedTabIndex).title : "Configuration";
        String state = presetNameFocused ? ". Editing preset name"
                : focusedSearchField != 0 ? ". Editing search" : ". Use left and right arrows or Tab to change tabs";
        return ComponentHelper.literal("Buildscape Configuration. " + tab + state);
    }

    @Override
    public void removed() {
        flushConfigUpload();
        flushPillarUploads();
        super.removed();
    }

    @Override
    public void onClose() {
        if (!isClosing) {
            startClosing();
            return;
        }
        finishClosing();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // --- Forge 1.18.2 Direct Render and Platform Helpers ---
    private static PoseStack asPose(Object poseStackOrGraphics) {
        return (PoseStack) poseStackOrGraphics;
    }

    private static void fill(Object poseStackOrGraphics, int minX, int minY, int maxX, int maxY, int color) {
        ConfigGuiBatch.fill(asPose(poseStackOrGraphics), minX, minY, maxX, maxY, color);
    }

    private static void draw(Object poseStackOrGraphics, Font font, Component text, float x, float y, int color) {
        ConfigGuiBatch.flush();
        font.draw(asPose(poseStackOrGraphics), text, x, y, color);
    }

    private static void draw(Object poseStackOrGraphics, Font font, String text, float x, float y, int color) {
        ConfigGuiBatch.flush();
        font.draw(asPose(poseStackOrGraphics), text, x, y, color);
    }

    private static void drawShadow(Object poseStackOrGraphics, Font font, Component text, float x, float y, int color) {
        ConfigGuiBatch.flush();
        font.drawShadow(asPose(poseStackOrGraphics), text, x, y, color);
    }

    private static void drawShadow(Object poseStackOrGraphics, Font font, String text, float x, float y, int color) {
        ConfigGuiBatch.flush();
        font.drawShadow(asPose(poseStackOrGraphics), text, x, y, color);
    }

    private static void drawCenteredString(Object poseStackOrGraphics, Font font, Component text, int x, int y, int color) {
        ConfigGuiBatch.flush();
        GuiComponent.drawCenteredString(asPose(poseStackOrGraphics), font, text, x, y, color);
    }

    private static void drawCenteredString(Object poseStackOrGraphics, Font font, String text, int x, int y, int color) {
        ConfigGuiBatch.flush();
        GuiComponent.drawCenteredString(asPose(poseStackOrGraphics), font, text, x, y, color);
    }

    private static void blit(Object poseStackOrGraphics, ResourceLocation texture, int x, int y, float u, float v, int width, int height, int sheetW, int sheetH) {
        ConfigGuiBatch.flush();
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(asPose(poseStackOrGraphics), x, y, u, v, width, height, sheetW, sheetH);
    }

    private static void blit(Object poseStackOrGraphics, ResourceLocation texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH) {
        ConfigGuiBatch.flush();
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(asPose(poseStackOrGraphics), x, y, width, height, u, v, uWidth, vHeight, sheetW, sheetH);
    }

    private static void drawTintedTexture(Object poseStackOrGraphics, ResourceLocation texture, int x, int y, int width, int height, int color, float alpha) {
        drawTintedTexture(poseStackOrGraphics, texture, x, y, width, height, 0.0f, 0.0f, width, height, width, height, color, alpha);
    }

    private static void drawTintedTexture(Object poseStackOrGraphics, ResourceLocation texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH, int color, float alpha) {
        if (texture == null) return;
        ConfigGuiBatch.flush();
        int rawA = (color >> 24) & 0xFF;
        float a = (rawA == 0 ? 1.0f : (rawA / 255.0f)) * alpha;
        if (a <= 0.001f) return;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(asPose(poseStackOrGraphics), x, y, width, height, u, v, uWidth, vHeight, sheetW, sheetH);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void pushGuiPose(Object poseStackOrGraphics) {
        asPose(poseStackOrGraphics).pushPose();
    }

    private static void popGuiPose(Object poseStackOrGraphics) {
        asPose(poseStackOrGraphics).popPose();
    }

    private static void scaleGuiPose(Object poseStackOrGraphics, float x, float y) {
        asPose(poseStackOrGraphics).scale(x, y, 1.0f);
    }

    private static void translateGuiPose(Object poseStackOrGraphics, float x, float y, float z) {
        asPose(poseStackOrGraphics).translate(x, y, z);
    }

    private static void enableScissor(Object poseStackOrGraphics, int x, int y, int width, int height) {
        ConfigGuiBatch.flush();
        RenderSystem.enableScissor(x, y, width, height);
    }

    private static void disableScissor() {
        ConfigGuiBatch.flush();
        RenderSystem.disableScissor();
    }

    /**
     * Items are drawn through the global model-view stack, not the screen's PoseStack, so the
     * shrink scale applied to the PoseStack in renderInternal does not reach them. Mirror it here
     * whenever it is active so icons land in their slots.
     */
    private void renderGuiItem(Object poseStackOrGraphics, ItemStack stack, int x, int y) {
        ConfigGuiBatch.flush();
        float scale = scissorUiScale;
        if (!GuiScaleHelper.isShrunk(scale)) {
            Minecraft.getInstance().getItemRenderer().renderGuiItem(stack, x, y);
            return;
        }
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.scale(scale, scale, 1.0f);
        RenderSystem.applyModelViewMatrix();
        try {
            Minecraft.getInstance().getItemRenderer().renderGuiItem(stack, x, y);
        } finally {
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private void renderComponentTooltip(Object poseStackOrGraphics, List<Component> tooltips, int mouseX, int mouseY) {
        ConfigGuiBatch.flush();
        this.renderComponentTooltip(asPose(poseStackOrGraphics), tooltips, mouseX, mouseY);
    }

    private static String getClipboardText() {
        Minecraft mc = Minecraft.getInstance();
        return mc.keyboardHandler != null ? mc.keyboardHandler.getClipboard() : "";
    }

    private static void openUri(URI uri) {
        Util.getPlatform().openUri(uri);
    }

    private void openScreen(Screen screen) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            mc.setScreen(screen);
        }
    }

    private static Item getItemByRawId(int rawId) {
        return Item.byId(rawId);
    }

    private static ResourceLocation getDimensionId(Level level) {
        if (level != null) {
            return level.dimension().location();
        }
        return new ResourceLocation("minecraft", "overworld");
    }

    private static boolean getGameRuleBoolean(Level level, net.minecraft.world.level.GameRules.Key<net.minecraft.world.level.GameRules.BooleanValue> rule, boolean fallback) {
        if (level != null) {
            return level.getGameRules().getBoolean(rule);
        }
        return fallback;
    }

    private static boolean hasPlayerPermissions(net.minecraft.world.entity.player.Player player, int permissionLevel) {
        return player != null && player.hasPermissions(permissionLevel);
    }

    private static Set<String> getAllItemTagIds() {
        Set<String> tagIds = new HashSet<>();
        for (net.minecraft.tags.TagKey<Item> tagKey : ForgeRegistries.ITEMS.tags().getTagNames().toList()) {
            tagIds.add(tagKey.location().toString());
        }
        return tagIds;
    }

    private static String getModVersionTag() {
        String modVersion = "4.1.0";
        try {
            net.minecraftforge.fml.ModContainer container = net.minecraftforge.fml.ModList.get().getModContainerById("buildscape").orElse(null);
            if (container != null) {
                modVersion = container.getModInfo().getVersion().toString();
            }
        } catch (Throwable ignored) {}
        return "v" + modVersion;
    }

}
