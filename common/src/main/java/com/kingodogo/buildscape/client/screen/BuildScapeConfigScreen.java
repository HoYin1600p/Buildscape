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
 * - 1 Big Unified Panel with preserved smooth metallic border gradients.
 * - Spill-proof scrollable viewport (no rows spill past bottom frame).
 * - Normalized professional naming and descriptions.
 * - Full Items tab with presets and interactive 3D Item Selection Modal.
 * - Interactive Color Picker Modal allowing full RGB and swatch customization.
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

    // --- Palette Constants (Solid Opaque, No 75% transparency) ---
    public static final int COLOR_OVERLAY = 0xD808080A;
    public static final int COLOR_PANEL_BG = 0xFF161618;
    public static final int COLOR_INNER_DARK = 0xFF101012;
    public static final int COLOR_HEADER_BG = 0xFF1E1E22;

    // Metallic Beveled Border Gradients
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
            "ring", "beam", "spiral", "fountain", "pulse", "burst", "snowflake"
    };

    // --- Presets & Item Cache ---
    private int selectedPresetIndex = 0;
    private final List<Item> availableItems = new ArrayList<>();
    private final Map<Item, ItemStack> itemStackCache = new HashMap<>();
    private final Map<String, ItemStack> stringToStackCache = new HashMap<>();

    // --- Item Selection Modal State ---
    private boolean showItemModal = false;
    private String itemModalFilter = "";
    private double itemModalScroll = 0.0;
    private double itemModalMaxScroll = 0.0;

    // --- Color Picker Modal State ---
    private boolean showColorModal = false;
    private int editingColorIndex = 0;
    private int pickerR = 255;
    private int pickerG = 184;
    private int pickerB = 28;
    private static final int[] PALETTE_PRESETS = {
            0xFFFFFFFF, 0xFFFFB81C, 0xFFFF3B30, 0xFF34C759,
            0xFF00C7BE, 0xFF5856D6, 0xFFFF9500, 0xFFFF2D55,
            0xFF3CDFFF, 0xFFE8FEFD, 0xFF8E8E93, 0xFF1C1C1E
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
        cacheAvailableItems();
    }

    private void initNavItems() {
        navItems.clear();
        navItems.add(new NavItem("items", "☵", "Items", "Configure activation trigger items and presets"));
        navItems.add(new NavItem("particles", "✦", "Particles", "Configure kinetic trajectory and emission mechanics"));
        navItems.add(new NavItem("pillars", "▲", "Pillar IDs", "Manage synchronized pillar columns and coordinates"));
        navItems.add(new NavItem("world", "⚙", "World Settings", "Control world griefing, felling, and inventory mechanics"));
        navItems.add(new NavItem("system", "ℹ", "System", "Engine architecture and client preferences"));
    }

    private void cacheAvailableItems() {
        if (!availableItems.isEmpty()) return;
        for (int rawId = 1; rawId < 2500; rawId++) {
            Item item = Services.PLATFORM.getItemByRawId(rawId);
            if (item != null && item != Items.AIR) {
                availableItems.add(item);
            }
        }
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

        // 3. Render 1 Big Frame with smooth metallic bevel gradients
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

        // 6. Render Modals (Item Selection Modal & Color Picker Modal)
        if (showItemModal) {
            renderItemSelectionModal(poseStackOrGraphics, font, mouseX, mouseY, alpha);
        } else if (showColorModal) {
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

        int totalContentHeight = calculateTotalContentHeight(current.id, rowH, rowGap);
        maxScroll = Math.max(0, totalContentHeight - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        int startY = viewY + 2 - (int) scrollOffset;

        switch (current.id) {
            case "items" -> renderItemSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "particles" -> renderParticleSettingsRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
            case "pillars" -> renderPillarIdRows(poseStackOrGraphics, font, rowX, startY, rowW, rowH, rowGap, viewY, viewH, mouseX, mouseY, alpha);
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

    // --- Tab 0: Items (Presets & Interactive 3D Item Trigger Registry) ---
    private void renderItemSettingsRows(Object poseStackOrGraphics, Font font, int x, int startY, int w, int h, int gap, int clipY, int clipH, int mouseX, int mouseY, float alpha) {
        PillarParticleConfig cfg = PillarParticleConfig.get();
        PresetsConfig presetsCfg = PresetsConfig.get();
        List<PresetsConfig.Preset> presets = presetsCfg.getPresets();
        String currentPresetName = presets.isEmpty() ? "Default" : presets.get(Math.min(selectedPresetIndex, presets.size() - 1)).name;

        int curY = startY;

        // Row 0: Preset Management Bar
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Active Preset", "Select, load, and persist item configuration presets", mouseX, mouseY, alpha);

            int cycleW = 76;
            int btnH = 14;
            int cycleX = x + w - cycleW - 96;
            int cycleY = curY + (h - btnH) / 2;
            boolean hoverCycle = mouseX >= cycleX && mouseX <= cycleX + cycleW && mouseY >= cycleY && mouseY <= cycleY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, cycleX, cycleY, cycleX + cycleW, cycleY + btnH, applyAlpha(hoverCycle ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, cycleX, cycleY, cycleW, btnH, applyAlpha(hoverCycle ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "‹ " + currentPresetName + " ›", cycleX + cycleW / 2, cycleY + 3, applyAlpha(hoverCycle ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));

            int applyW = 42;
            int applyX = x + w - applyW - 48;
            int applyY = cycleY;
            boolean hoverApply = mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, applyX, applyY, applyX + applyW, applyY + btnH, applyAlpha(hoverApply ? 0xFF1C3C22 : COLOR_TOGGLE_ON_BG, alpha));
            drawBorder(poseStackOrGraphics, applyX, applyY, applyW, btnH, applyAlpha(COLOR_TOGGLE_ON_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Apply", applyX + applyW / 2, applyY + 3, applyAlpha(COLOR_TOGGLE_ON_TEXT, alpha));

            int saveW = 40;
            int saveX = x + w - saveW - 6;
            int saveY = cycleY;
            boolean hoverSave = mouseX >= saveX && mouseX <= saveX + saveW && mouseY >= saveY && mouseY <= saveY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, saveX, saveY, saveX + saveW, saveY + btnH, applyAlpha(hoverSave ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, saveX, saveY, saveW, btnH, applyAlpha(hoverSave ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "Save", saveX + saveW / 2, saveY + 3, applyAlpha(hoverSave ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        }
        curY += h + gap;

        // Row 1: Add Item Modal Launcher & Restore Defaults
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Pillar Trigger Registry", cfg.items.size() + " total item triggers configured", mouseX, mouseY, alpha);

            int addBtnW = 86;
            int btnH = 14;
            int addX = x + w - (addBtnW * 2) - 12;
            int addY = curY + (h - btnH) / 2;
            boolean hoverAdd = mouseX >= addX && mouseX <= addX + addBtnW && mouseY >= addY && mouseY <= addY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, addX, addY, addX + addBtnW, addY + btnH, applyAlpha(hoverAdd ? 0xFF1C3C22 : COLOR_TOGGLE_ON_BG, alpha));
            drawBorder(poseStackOrGraphics, addX, addY, addBtnW, btnH, applyAlpha(COLOR_TOGGLE_ON_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+ Select Item", addX + addBtnW / 2, addY + 3, applyAlpha(COLOR_TOGGLE_ON_TEXT, alpha));

            int defX = x + w - addBtnW - 6;
            boolean hoverDef = mouseX >= defX && mouseX <= defX + addBtnW && mouseY >= addY && mouseY <= addY + btnH;
            Services.PLATFORM.fill(poseStackOrGraphics, defX, addY, defX + addBtnW, addY + btnH, applyAlpha(hoverDef ? 0xFF2E2E38 : COLOR_BTN_BG, alpha));
            drawBorder(poseStackOrGraphics, defX, addY, addBtnW, btnH, applyAlpha(hoverDef ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "↺ Defaults", defX + addBtnW / 2, addY + 3, applyAlpha(hoverDef ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
        }
        curY += h + gap;

        // Row 2: Tag Presets Quick Buttons
        if (curY >= clipY && curY + h <= clipY + clipH) {
            renderRowCardBase(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Quick Append Tags", "Add high-value tag categories to current trigger list", mouseX, mouseY, alpha);

            int chipW = 52;
            int chipH = 14;
            int chipY = curY + (h - chipH) / 2;
            String[] tagChips = {"#keys", "#gems", "#ores", "#vault"};
            for (int c = 0; c < tagChips.length; c++) {
                int chipX = x + w - ((tagChips.length - c) * (chipW + 4)) - 2;
                boolean hoverChip = mouseX >= chipX && mouseX <= chipX + chipW && mouseY >= chipY && mouseY <= chipY + chipH;
                Services.PLATFORM.fill(poseStackOrGraphics, chipX, chipY, chipX + chipW, chipY + chipH, applyAlpha(hoverChip ? 0xFF2C2C36 : COLOR_BTN_BG, alpha));
                drawBorder(poseStackOrGraphics, chipX, chipY, chipW, chipH, applyAlpha(hoverChip ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, tagChips[c], chipX + chipW / 2, chipY + 3, applyAlpha(hoverChip ? COLOR_TEXT_WHITE : COLOR_TEXT_SILVER, alpha));
            }
        }
        curY += h + gap;

        // Rows 3+: Configured Items List with 3D Item Render & [ ✕ ] Button
        List<String> itemsList = new ArrayList<>(cfg.items);
        for (String itemStr : itemsList) {
            if (curY >= clipY && curY + h <= clipY + clipH) {
                boolean isTag = itemStr.startsWith("#");
                ItemStack stack = getStackForIdentifier(itemStr);

                // Row background
                boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= curY && mouseY <= curY + h;
                int bg = hover ? COLOR_ROW_BG_HOVER : COLOR_ROW_BG;
                Services.PLATFORM.fill(poseStackOrGraphics, x, curY, x + w, curY + h, applyAlpha(bg, alpha));
                drawBorder(poseStackOrGraphics, x, curY, w, h, applyAlpha(hover ? COLOR_ROW_BORDER_HOVER : COLOR_ROW_BORDER, alpha));

                // 3D Item Icon or Tag Emblem
                int iconX = x + 6;
                int iconY = curY + (h - 16) / 2;
                if (stack != null && !stack.isEmpty()) {
                    Services.PLATFORM.renderGuiItem(poseStackOrGraphics, stack, iconX, iconY);
                } else {
                    int badgeCol = isTag ? 0xFF3880E0 : 0xFF2A8A3A;
                    Services.PLATFORM.fill(poseStackOrGraphics, iconX, iconY, iconX + 16, iconY + 16, applyAlpha(badgeCol, alpha));
                    drawBorder(poseStackOrGraphics, iconX, iconY, 16, 16, applyAlpha(COLOR_FRAME_BLACK, alpha));
                    Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, isTag ? "#" : "■", iconX + 8, iconY + 4, applyAlpha(COLOR_TEXT_WHITE, alpha));
                }

                // Title & Subtitle
                String title = stack != null ? stack.getHoverName().getString() : itemStr;
                String subtitle = isTag ? "[TAG DEFINITION]  " + itemStr : itemStr;
                drawText(poseStackOrGraphics, font, title, x + 26, curY + 5, applyAlpha(COLOR_TEXT_WHITE, alpha));
                drawText(poseStackOrGraphics, font, subtitle, x + 26, curY + 15, applyAlpha(COLOR_TEXT_MUTED, alpha));

                // Remove button [ ✕ ]
                int removeW = 18;
                int removeH = 14;
                int removeX = x + w - removeW - 6;
                int removeY = curY + (h - removeH) / 2;
                boolean hoverRem = mouseX >= removeX && mouseX <= removeX + removeW && mouseY >= removeY && mouseY <= removeY + removeH;
                Services.PLATFORM.fill(poseStackOrGraphics, removeX, removeY, removeX + removeW, removeY + removeH, applyAlpha(hoverRem ? 0xFF3D1616 : 0xFF241414, alpha));
                drawBorder(poseStackOrGraphics, removeX, removeY, removeW, removeH, applyAlpha(hoverRem ? 0xFFE05050 : 0xFF882828, alpha));
                Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", removeX + removeW / 2, removeY + 3, applyAlpha(hoverRem ? 0xFFFFFFFF : 0xFFE05050, alpha));
            }
            curY += h + gap;
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
        renderConfigRowPalette(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Color Palette (Click to Edit)", cfg.particle_color, cfg.max_particle_color, mouseX, mouseY, alpha);
        curY += h + gap;

        // 11. Reset Colors Action
        renderConfigRowButton(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "Reset Palette", "Restore default hexadecimal color swatches", "↺ Reset Colors", mouseX, mouseY, alpha);
    }

    // --- Tab 2: Pillar IDs Rows ---
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

        // Rows 3+: Individual Pillar Rows
        java.util.Collection<PillarIdManager.PillarData> pillars = PillarIdManager.getClient().getAllData();
        if (pillars.isEmpty()) {
            if (curY >= clipY && curY + h <= clipY + clipH) {
                renderConfigRowBadge(poseStackOrGraphics, font, x, curY, w, h, clipY, clipH, "No Pillars Registered", "Place pillar blocks in world to synchronize columns", "Empty", alpha);
            }
        } else {
            for (PillarIdManager.PillarData data : pillars) {
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    String desc = String.format("Pos: [%d, %d, %d] · Dimension: %s", data.x, data.y, data.z, data.dimension);
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

    // --- Item Selection Modal (Interactive 3D Grid) ---
    private void renderItemSelectionModal(Object poseStackOrGraphics, Font font, int mouseX, int mouseY, float alpha) {
        // Dimmed sub-overlay
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(0xA0000000, alpha));

        int modalW = 420;
        int modalH = 220;
        int modalX = (width - modalW) / 2;
        int modalY = (height - modalH) / 2;

        drawVaultHuntersFrame(poseStackOrGraphics, modalX, modalY, modalW, modalH, COLOR_PANEL_BG, alpha);

        // Header
        int headH = 24;
        Services.PLATFORM.fill(poseStackOrGraphics, modalX + 3, modalY + 3, modalX + modalW - 3, modalY + headH, applyAlpha(COLOR_HEADER_BG, alpha));
        drawGroove(poseStackOrGraphics, modalX + 3, modalY + headH, modalW - 6, true, alpha);
        drawText(poseStackOrGraphics, font, "Select Item To Add", modalX + 8, modalY + 8, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Close button
        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 6;
        int closeY = modalY + 5;
        boolean hoverClose = mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize;
        Services.PLATFORM.fill(poseStackOrGraphics, closeX, closeY, closeX + closeSize, closeY + closeSize, applyAlpha(hoverClose ? 0xFF3D1616 : 0xFF241414, alpha));
        drawBorder(poseStackOrGraphics, closeX, closeY, closeSize, closeSize, applyAlpha(hoverClose ? 0xFFE05050 : 0xFF882828, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeX + closeSize / 2, closeY + 3, applyAlpha(hoverClose ? 0xFFFFFFFF : 0xFFE05050, alpha));

        // Grid Container
        int gridX = modalX + 8;
        int gridY = modalY + headH + 6;
        int gridW = modalW - 16;
        int gridH = modalH - headH - 12;
        drawInsetBox(poseStackOrGraphics, gridX, gridY, gridW, gridH, COLOR_INNER_DARK, alpha);

        int itemSize = 20;
        int cols = (gridW - 10) / itemSize;
        int rowsTotal = (int) Math.ceil((double) availableItems.size() / cols);
        itemModalMaxScroll = Math.max(0, rowsTotal * itemSize - gridH + 8);
        itemModalScroll = Math.max(0, Math.min(itemModalMaxScroll, itemModalScroll));

        int startRow = (int) Math.floor(itemModalScroll / itemSize);
        int endRow = Math.min(rowsTotal, startRow + (gridH / itemSize) + 2);

        ItemStack hoveredStack = null;

        for (int r = startRow; r < endRow; r++) {
            int rowY = gridY + 4 + (r * itemSize) - (int) itemModalScroll;
            if (rowY < gridY || rowY + itemSize > gridY + gridH) continue;

            for (int c = 0; c < cols; c++) {
                int index = r * cols + c;
                if (index >= availableItems.size()) break;

                Item item = availableItems.get(index);
                int tileX = gridX + 4 + c * itemSize;

                ItemStack stack = itemStackCache.computeIfAbsent(item, ItemStack::new);
                boolean hover = mouseX >= tileX && mouseX <= tileX + itemSize && mouseY >= rowY && mouseY <= rowY + itemSize;

                if (hover) {
                    Services.PLATFORM.fill(poseStackOrGraphics, tileX, rowY, tileX + itemSize, rowY + itemSize, applyAlpha(0xFF2E2E38, alpha));
                    drawBorder(poseStackOrGraphics, tileX, rowY, itemSize, itemSize, applyAlpha(0xFF6E6E80, alpha));
                    hoveredStack = stack;
                }

                Services.PLATFORM.renderGuiItem(poseStackOrGraphics, stack, tileX + 2, rowY + 2);
            }
        }

        // Tooltip for hovered item
        if (hoveredStack != null) {
            Services.PLATFORM.renderComponentTooltip(
                    poseStackOrGraphics, font,
                    Collections.singletonList(hoveredStack.getHoverName()),
                    mouseX + 12, mouseY
            );
        }
    }

    // --- Color Picker Modal (Interactive RGB & Swatch Customizer) ---
    private void renderColorPickerModal(Object poseStackOrGraphics, Font font, int mouseX, int mouseY, float alpha) {
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, applyAlpha(0xA0000000, alpha));

        int modalW = 340;
        int modalH = 190;
        int modalX = (width - modalW) / 2;
        int modalY = (height - modalH) / 2;

        drawVaultHuntersFrame(poseStackOrGraphics, modalX, modalY, modalW, modalH, COLOR_PANEL_BG, alpha);

        int headH = 24;
        Services.PLATFORM.fill(poseStackOrGraphics, modalX + 3, modalY + 3, modalX + modalW - 3, modalY + headH, applyAlpha(COLOR_HEADER_BG, alpha));
        drawGroove(poseStackOrGraphics, modalX + 3, modalY + headH, modalW - 6, true, alpha);
        drawText(poseStackOrGraphics, font, "Customize Particle Color", modalX + 8, modalY + 8, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Close button
        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 6;
        int closeY = modalY + 5;
        boolean hoverClose = mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize;
        Services.PLATFORM.fill(poseStackOrGraphics, closeX, closeY, closeX + closeSize, closeY + closeSize, applyAlpha(hoverClose ? 0xFF3D1616 : 0xFF241414, alpha));
        drawBorder(poseStackOrGraphics, closeX, closeY, closeSize, closeSize, applyAlpha(hoverClose ? 0xFFE05050 : 0xFF882828, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✕", closeX + closeSize / 2, closeY + 3, applyAlpha(hoverClose ? 0xFFFFFFFF : 0xFFE05050, alpha));

        int currentColorInt = (0xFF << 24) | ((pickerR & 0xFF) << 16) | ((pickerG & 0xFF) << 8) | (pickerB & 0xFF);
        String hexString = String.format("#%02X%02X%02X", pickerR, pickerG, pickerB);

        // Preview Box
        int previewX = modalX + 16;
        int previewY = modalY + headH + 12;
        int previewSize = 44;
        Services.PLATFORM.fill(poseStackOrGraphics, previewX, previewY, previewX + previewSize, previewY + previewSize, applyAlpha(currentColorInt, alpha));
        drawBorder(poseStackOrGraphics, previewX, previewY, previewSize, previewSize, applyAlpha(COLOR_TEXT_WHITE, alpha));
        drawText(poseStackOrGraphics, font, hexString, previewX, previewY + previewSize + 6, applyAlpha(COLOR_TEXT_WHITE, alpha));

        // Preset Swatches Grid (12 Vibrant Presets)
        int presetStartX = modalX + 76;
        int presetStartY = previewY;
        int swatchSize = 14;
        int swatchGap = 4;
        for (int p = 0; p < PALETTE_PRESETS.length; p++) {
            int px = presetStartX + (p % 6) * (swatchSize + swatchGap);
            int py = presetStartY + (p / 6) * (swatchSize + swatchGap);
            Services.PLATFORM.fill(poseStackOrGraphics, px, py, px + swatchSize, py + swatchSize, applyAlpha(PALETTE_PRESETS[p], alpha));
            drawBorder(poseStackOrGraphics, px, py, swatchSize, swatchSize, applyAlpha(0xFF000000, alpha));
        }

        // RGB Steppers
        int sliderY = previewY + previewSize + 22;
        renderStepperModalRow(poseStackOrGraphics, font, modalX + 16, sliderY, "Red", pickerR, 0, 255, mouseX, mouseY, alpha);
        renderStepperModalRow(poseStackOrGraphics, font, modalX + 16, sliderY + 20, "Green", pickerG, 0, 255, mouseX, mouseY, alpha);
        renderStepperModalRow(poseStackOrGraphics, font, modalX + 16, sliderY + 40, "Blue", pickerB, 0, 255, mouseX, mouseY, alpha);

        // Apply Button
        int applyW = 86;
        int applyH = 16;
        int applyX = modalX + modalW - applyW - 16;
        int applyY = modalY + modalH - applyH - 12;
        boolean hoverApply = mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + applyH;
        Services.PLATFORM.fill(poseStackOrGraphics, applyX, applyY, applyX + applyW, applyY + applyH, applyAlpha(hoverApply ? 0xFF1C3C22 : COLOR_TOGGLE_ON_BG, alpha));
        drawBorder(poseStackOrGraphics, applyX, applyY, applyW, applyH, applyAlpha(COLOR_TOGGLE_ON_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "✓ Apply Color", applyX + applyW / 2, applyY + 4, applyAlpha(COLOR_TOGGLE_ON_TEXT, alpha));
    }

    private void renderStepperModalRow(Object poseStackOrGraphics, Font font, int x, int y, String label, int val, int min, int max, int mouseX, int mouseY, float alpha) {
        drawText(poseStackOrGraphics, font, label + ":", x, y + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int stepW = 14;
        int stepH = 14;
        int minusX = x + 44;
        boolean hoverMinus = mouseX >= minusX && mouseX <= minusX + stepW && mouseY >= y && mouseY <= y + stepH;
        Services.PLATFORM.fill(poseStackOrGraphics, minusX, y, minusX + stepW, y + stepH, applyAlpha(hoverMinus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, minusX, y, stepW, stepH, applyAlpha(hoverMinus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "−", minusX + stepW / 2, y + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int valBoxX = minusX + stepW;
        int valBoxW = 32;
        Services.PLATFORM.fill(poseStackOrGraphics, valBoxX, y, valBoxX + valBoxW, y + stepH, applyAlpha(0xFF141418, alpha));
        drawBorder(poseStackOrGraphics, valBoxX, y, valBoxW, stepH, applyAlpha(COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, String.valueOf(val), valBoxX + valBoxW / 2, y + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

        int plusX = valBoxX + valBoxW;
        boolean hoverPlus = mouseX >= plusX && mouseX <= plusX + stepW && mouseY >= y && mouseY <= y + stepH;
        Services.PLATFORM.fill(poseStackOrGraphics, plusX, y, plusX + stepW, y + stepH, applyAlpha(hoverPlus ? 0xFF30303C : COLOR_BTN_BG, alpha));
        drawBorder(poseStackOrGraphics, plusX, y, stepW, stepH, applyAlpha(hoverPlus ? COLOR_BTN_BORDER_HOVER : COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, "+", plusX + stepW / 2, y + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));
    }

    // --- Vault Hunters Border & Bevel Engine (Smooth Metallic Gradient Frame) ---
    private void drawVaultHuntersFrame(Object poseStackOrGraphics, int x, int y, int w, int h, int bgRgb, float alpha) {
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + h, applyAlpha(bgRgb, alpha));

        int black = applyAlpha(COLOR_FRAME_BLACK, alpha);

        // 1. Layer 1: Outermost 1px Black Border
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + w, y + 1, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + h - 1, x + w, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + h, black);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 1, y, x + w, y + h, black);

        // 2. Layer 2 & 3: Smooth Metallic Gradient Bands (2px thick)
        for (int b = 1; b <= 2; b++) {
            // Top Gradient Band (Bright Silver -> Dark Metallic)
            for (int i = b; i < w - b; i++) {
                float t = (float) (i - b) / Math.max(1, w - b * 2);
                int col = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_GRADIENT_END, t);
                Services.PLATFORM.fill(poseStackOrGraphics, x + i, y + b, x + i + 1, y + b + 1, applyAlpha(col, alpha));
            }
            // Left Gradient Band (Bright Silver -> Deep Shadow)
            for (int j = b; j < h - b; j++) {
                float t = (float) (j - b) / Math.max(1, h - b * 2);
                int col = lerpColor(COLOR_BORDER_GRADIENT_START, COLOR_BORDER_SHADOW, t);
                Services.PLATFORM.fill(poseStackOrGraphics, x + b, y + j, x + b + 1, y + j + 1, applyAlpha(col, alpha));
            }
            // Bottom & Right Deep Recessed Shadows
            int shadow = applyAlpha(COLOR_BORDER_SHADOW, alpha);
            Services.PLATFORM.fill(poseStackOrGraphics, x + b, y + h - b - 1, x + w - b, y + h - b, shadow);
            Services.PLATFORM.fill(poseStackOrGraphics, x + w - b - 1, y + b, x + w - b, y + h - b, shadow);
        }

        // 3. Layer 4: Recessed Inner Shadow Line
        int innerDark = applyAlpha(0xFF101012, alpha);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 3, y + 3, x + w - 3, y + 4, innerDark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 3, y + 3, x + 4, y + h - 3, innerDark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 3, y + h - 4, x + w - 3, y + h - 3, innerDark);
        Services.PLATFORM.fill(poseStackOrGraphics, x + w - 4, y + 3, x + w - 3, y + h - 3, innerDark);
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

    // --- Reusable Row Card Renderers with Boundary Checks ---
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

        int valBoxX = counterX + stepBtnW;
        int valBoxW = counterW - stepBtnW * 2;
        Services.PLATFORM.fill(poseStackOrGraphics, valBoxX, counterY, valBoxX + valBoxW, counterY + counterH, applyAlpha(0xFF141418, alpha));
        drawBorder(poseStackOrGraphics, valBoxX, counterY, valBoxW, counterH, applyAlpha(COLOR_BTN_BORDER, alpha));
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, value, valBoxX + valBoxW / 2, counterY + 3, applyAlpha(COLOR_TEXT_WHITE, alpha));

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

    // --- Mouse & Keyboard Event Handlers ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isClosing) return true;

        // Modal Clicks Handle First
        if (showItemModal) {
            return handleItemModalClick(mouseX, mouseY);
        } else if (showColorModal) {
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

                // Row 0: Preset Management
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

                // Row 1: Add Item Launcher & Defaults
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    int addBtnW = 86;
                    int btnH = 14;
                    int addX = x + w - (addBtnW * 2) - 12;
                    int addY = curY + (h - btnH) / 2;
                    if (mouseX >= addX && mouseX <= addX + addBtnW && mouseY >= addY && mouseY <= addY + btnH) {
                        showItemModal = true;
                        itemModalScroll = 0;
                        return true;
                    }

                    int defX = x + w - addBtnW - 6;
                    if (mouseX >= defX && mouseX <= defX + addBtnW && mouseY >= addY && mouseY <= addY + btnH) {
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
                curY += h + gap;

                // Row 2: Tag Presets Quick Buttons
                if (curY >= clipY && curY + h <= clipY + clipH) {
                    int chipW = 52;
                    int chipH = 14;
                    int chipY = curY + (h - chipH) / 2;
                    String[] tagIds = {"#the_vault:keys", "#the_vault:gems", "#c:ores", "#the_vault:vault_gear"};
                    for (int c = 0; c < tagIds.length; c++) {
                        int chipX = x + w - ((tagIds.length - c) * (chipW + 4)) - 2;
                        if (mouseX >= chipX && mouseX <= chipX + chipW && mouseY >= chipY && mouseY <= chipY + chipH) {
                            cfg.items.add(tagIds[c]);
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
                        int removeW = 18;
                        int removeH = 14;
                        int removeX = x + w - removeW - 6;
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

                // 10. Swatch Clicks -> Opens Color Picker Modal!
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
                            String hex = cfg.particle_color.get(i).replace("#", "");
                            try {
                                int rgb = Integer.parseInt(hex, 16);
                                pickerR = (rgb >> 16) & 0xFF;
                                pickerG = (rgb >> 8) & 0xFF;
                                pickerB = rgb & 0xFF;
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

                // Rows 3+: Pillar Removal
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

    private boolean handleItemModalClick(double mouseX, double mouseY) {
        int modalW = 420;
        int modalH = 220;
        int modalX = (width - modalW) / 2;
        int modalY = (height - modalH) / 2;

        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 6;
        int closeY = modalY + 5;
        if (mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize) {
            showItemModal = false;
            return true;
        }

        int gridX = modalX + 8;
        int gridY = modalY + 24 + 6;
        int gridW = modalW - 16;
        int gridH = modalH - 24 - 12;

        if (mouseX >= gridX && mouseX <= gridX + gridW && mouseY >= gridY && mouseY <= gridY + gridH) {
            int itemSize = 20;
            int cols = (gridW - 10) / itemSize;
            int rowsTotal = (int) Math.ceil((double) availableItems.size() / cols);
            int startRow = (int) Math.floor(itemModalScroll / itemSize);
            int endRow = Math.min(rowsTotal, startRow + (gridH / itemSize) + 2);

            for (int r = startRow; r < endRow; r++) {
                int rowY = gridY + 4 + (r * itemSize) - (int) itemModalScroll;
                if (rowY < gridY || rowY + itemSize > gridY + gridH) continue;

                for (int c = 0; c < cols; c++) {
                    int index = r * cols + c;
                    if (index >= availableItems.size()) break;

                    Item item = availableItems.get(index);
                    int tileX = gridX + 4 + c * itemSize;

                    if (mouseX >= tileX && mouseX <= tileX + itemSize && mouseY >= rowY && mouseY <= rowY + itemSize) {
                        CommonId cid = Services.PLATFORM.getItemId(item);
                        if (cid != null) {
                            PillarParticleConfig.get().items.add(cid.toString());
                            PillarParticleConfig.get().saveItemsToDisk();
                        }
                        showItemModal = false;
                        return true;
                    }
                }
            }
        }

        return true;
    }

    private boolean handleColorModalClick(double mouseX, double mouseY) {
        int modalW = 340;
        int modalH = 190;
        int modalX = (width - modalW) / 2;
        int modalY = (height - modalH) / 2;

        int closeSize = 14;
        int closeX = modalX + modalW - closeSize - 6;
        int closeY = modalY + 5;
        if (mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize) {
            showColorModal = false;
            return true;
        }

        int headH = 24;
        int previewY = modalY + headH + 12;

        // Swatch clicks
        int presetStartX = modalX + 76;
        int presetStartY = previewY;
        int swatchSize = 14;
        int swatchGap = 4;
        for (int p = 0; p < PALETTE_PRESETS.length; p++) {
            int px = presetStartX + (p % 6) * (swatchSize + swatchGap);
            int py = presetStartY + (p / 6) * (swatchSize + swatchGap);
            if (mouseX >= px && mouseX <= px + swatchSize && mouseY >= py && mouseY <= py + swatchSize) {
                int col = PALETTE_PRESETS[p];
                pickerR = (col >> 16) & 0xFF;
                pickerG = (col >> 8) & 0xFF;
                pickerB = col & 0xFF;
                return true;
            }
        }

        // RGB Steppers
        int sliderY = previewY + 44 + 22;
        int stepW = 14;
        int stepH = 14;
        int minusX = modalX + 16 + 44;
        int valBoxW = 32;
        int plusX = minusX + stepW + valBoxW;

        // Red
        if (mouseY >= sliderY && mouseY <= sliderY + stepH) {
            if (mouseX >= minusX && mouseX <= minusX + stepW) {
                pickerR = Math.max(0, pickerR - 15);
                return true;
            } else if (mouseX >= plusX && mouseX <= plusX + stepW) {
                pickerR = Math.min(255, pickerR + 15);
                return true;
            }
        }
        // Green
        if (mouseY >= sliderY + 20 && mouseY <= sliderY + 20 + stepH) {
            if (mouseX >= minusX && mouseX <= minusX + stepW) {
                pickerG = Math.max(0, pickerG - 15);
                return true;
            } else if (mouseX >= plusX && mouseX <= plusX + stepW) {
                pickerG = Math.min(255, pickerG + 15);
                return true;
            }
        }
        // Blue
        if (mouseY >= sliderY + 40 && mouseY <= sliderY + 40 + stepH) {
            if (mouseX >= minusX && mouseX <= minusX + stepW) {
                pickerB = Math.max(0, pickerB - 15);
                return true;
            } else if (mouseX >= plusX && mouseX <= plusX + stepW) {
                pickerB = Math.min(255, pickerB + 15);
                return true;
            }
        }

        // Apply Button
        int applyW = 86;
        int applyH = 16;
        int applyX = modalX + modalW - applyW - 16;
        int applyY = modalY + modalH - applyH - 12;
        if (mouseX >= applyX && mouseX <= applyX + applyW && mouseY >= applyY && mouseY <= applyY + applyH) {
            String hexString = String.format("#%02X%02X%02X", pickerR, pickerG, pickerB);
            PillarParticleConfig cfg = PillarParticleConfig.get();
            if (editingColorIndex < cfg.particle_color.size()) {
                cfg.particle_color.set(editingColorIndex, hexString);
            } else {
                cfg.particle_color.add(hexString);
            }
            cfg.saveProperties();
            showColorModal = false;
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
        if (showItemModal) {
            if (itemModalMaxScroll > 0) {
                itemModalScroll = Math.max(0, Math.min(itemModalMaxScroll, itemModalScroll - delta * 24.0));
                return true;
            }
            return true;
        }
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
        if (showItemModal || showColorModal) {
            if (keyCode == 256) {
                showItemModal = false;
                showColorModal = false;
                return true;
            }
            return true;
        }
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
        return panelX + 4 + navW + 4;
    }

    public int getContentY() {
        int panelH = Math.min(height - 16, 304);
        int panelY = (height - panelH) / 2;
        return panelY + 26 + 4;
    }

    public int getContentWidth() {
        int panelW = Math.min(width - 20, 560);
        int navW = navExpanded ? 106 : 30;
        return panelW - 8 - navW - 4;
    }

    public int getContentHeight() {
        int panelH = Math.min(height - 16, 304);
        return panelH - 26 - 12;
    }

    public int getSidebarWidth() {
        return navExpanded ? 106 : 30;
    }

    public static int scaleSize(int baseSize) { return baseSize; }
    public static int getScaledButtonHeight() { return 20; }
    public static int getScaledEditBoxHeight() { return 18; }
    public static float getStandardTextScale() { return 1.0f; }
}
