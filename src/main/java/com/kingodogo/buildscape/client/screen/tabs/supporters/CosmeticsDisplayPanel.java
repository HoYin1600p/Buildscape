package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.screen.widget.ColorPickerWidget;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Cosmetics Display Panel (Panel 1)
 * 
 * Shows all available cosmetics in a scrollable grid with filter buttons.
 * Displays locked/unlocked states from API.
 * Resolves cosmetic IDs to ItemStack/Block via CosmeticRegistry for rendering.
 * Supports animated entities (wings, particles, gear) with proper animations.
 * Single click to equip/unequip cosmetics.
 * Uses fixed GUI scale (not affected by user's GUI scale setting).
 * 
 * Dimensions: 55% width × 100% height
 * Position: (12%, 0%)
 */
public class CosmeticsDisplayPanel extends BasePanel {
    // Fixed GUI scale - panel is not affected by user's GUI scale setting
    private static final double FIXED_GUI_SCALE = 2.0;

    // Fixed layout: Always 4 items per row, regardless of GUI scale
    private static final int ITEMS_PER_ROW = 4; // Always render exactly 4 items per row

    private static final int PADDING = 8;
    private static final int SCROLLBAR_WIDTH = 12;

    // Calculated item sizes (based on screen width percentages)
    private int itemSize = 80; // Calculated in init() as 12% of screen width
    private int itemSpacing = 10; // Calculated in init() as 1.2% of screen width
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;
    private static final int FILTER_BUTTON_AREA_HEIGHT = 30;
    private static final int ITEM_AREA_TOP_SPACING = 5; // Extra spacing below filter buttons

    // Cosmetic type filters
    public enum CosmeticType {
        ALL("All", 0xFFFFFF),
        WINGS("Wings", 0x00FF00), // Lime
        PARTICLES("Particles", 0xFFFF00), // Yellow
        GEAR("Gear", 0xFF8800); // Orange

        private final String name;
        private final int color;

        CosmeticType(String name, int color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }
    }

    private final CosmeticRegistry cosmeticRegistry = CosmeticRegistry.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private final Minecraft mc = Minecraft.getInstance();

    private List<String> allCosmeticIds = new ArrayList<>();
    private List<String> filteredCosmeticIds = new ArrayList<>();
    private CosmeticType currentFilter = CosmeticType.ALL;
    private double scrollOffset = 0;
    private int itemsPerRow;
    private int visibleRows;

    // Animation state for animated entities - persists across equip/unequip
    // Key: cosmeticId, Value: animation start time in milliseconds
    private final java.util.Map<String, Long> itemAnimationTimes = new java.util.HashMap<>();

    // Rotation state for all items (like pillars) - each item has its own rotation
    // timer
    // Key: cosmeticId, Value: animation start time in milliseconds
    private final java.util.Map<String, Long> itemRotationTimes = new java.util.HashMap<>();

    // Preview state (from CosmeticPreviewPanel)
    private float rotation = 0.0f;
    private float zoom = 1.0f;
    private long itemStartTime = 0;
    private String lastSelectedCosmeticId = null;
    private float bobOffset = 0.0f;

    // Color selector state
    private ColorPickerWidget colorPicker = null;
    private String selectedCosmeticForColor = null; // Which cosmetic is having its color edited
    private static final int COLOR_BOX_SIZE = 12; // Size of color indicator box

    @Override
    public void init() {
        // Always render exactly 4 items per row, regardless of GUI scale
        itemsPerRow = ITEMS_PER_ROW;

        // FIXED LAYOUT: Always fit exactly 4 items per row, leaving space for scrollbar
        // Layout: gap + item + gap + item + gap + item + gap + item + gap + scrollbar =
        // full width
        // We need to reserve space for the scrollbar so items don't overlap it
        //
        // To prevent items from clipping into scrollbar:
        // - Reserve space for scrollbar: availableWidth = width - SCROLLBAR_WIDTH - gap
        // - Use a small fixed gap size (1.4% of width) for spacing
        // - Calculate item size: itemSize = (availableWidth - 4*gaps) / 4
        // This ensures exactly 4 items fit with equal gaps, ending before scrollbar

        // Calculate gap size as 1.4% of panel width (in GUI-scaled coordinates)
        int gapSize = (int) (width * 0.014);
        gapSize = Math.max(2, gapSize); // Minimum gap of 2 pixels

        // Reserve space for scrollbar (leave a gap before scrollbar)
        int scrollbarReservedSpace = SCROLLBAR_WIDTH + gapSize; // Scrollbar width + gap before it
        int availableWidth = width - scrollbarReservedSpace;

        // Calculate item size to fit 4 items with 4 gaps (between items) in available
        // width
        // Layout: gap + item + gap + item + gap + item + gap + item = availableWidth
        // Total: 4*gaps + 4*itemSize = availableWidth
        itemSize = (availableWidth - 4 * gapSize) / 4;

        // Use the gap size as item spacing
        itemSpacing = gapSize;

        // Ensure minimum item size for very small windows
        itemSize = Math.max(32, itemSize);

        // Calculate visible rows based on panel height (account for title and filter
        // buttons)
        int titleHeight = 15;
        int availableHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT;
        visibleRows = Math.max(1, availableHeight / (itemSize + itemSpacing));

        // Initialize preview animation
        itemStartTime = System.nanoTime() / 1000000L;

        // Load cosmetic IDs from state
        updateCosmeticList();
    }

    /**
     * Check if a cosmetic is an animated entity (wings, particles, etc.).
     */
    private boolean isAnimatedEntity(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return false;
        }

        String idLower = cosmeticId.toLowerCase();

        // Check for wings (elytra, wing items)
        if (idLower.contains("elytra") || idLower.contains("wing")) {
            return true;
        }

        // Check for particles
        if (idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail")) {
            return true;
        }

        // Check for animated gear (could be extended)
        ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
        if (stack != null && !stack.isEmpty()) {
            Item item = stack.getItem();
            // Elytra is animated
            if (item instanceof ElytraItem) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get animation progress for an animated entity (0.0 to 1.0).
     * Wings: opening/closing animation
     * Particles: pulsing/spawning animation
     * 
     * @param speedMultiplier Multiplier for animation speed (1.0 = normal, >1.0 =
     *                        faster)
     */
    private float getAnimationProgress(String cosmeticId, float partialTick, float speedMultiplier) {
        if (!isAnimatedEntity(cosmeticId)) {
            return 0.0f;
        }

        // Get or create animation start time for this item
        Long startTime = itemAnimationTimes.get(cosmeticId);
        if (startTime == null) {
            startTime = System.nanoTime() / 1000000L;
            itemAnimationTimes.put(cosmeticId, startTime);
        }

        long currentTime = System.nanoTime() / 1000000L;
        float elapsedSeconds = (currentTime - startTime + partialTick * 50.0f) / 1000.0f;
        elapsedSeconds *= speedMultiplier; // Apply speed multiplier

        String idLower = cosmeticId.toLowerCase();

        // Wings: opening/closing animation (2 second cycle)
        if (idLower.contains("elytra") || idLower.contains("wing")) {
            // Sine wave for smooth open/close animation
            return (float) (Math.sin(elapsedSeconds * Math.PI) * 0.5 + 0.5); // 0.0 to 1.0
        }

        // Particles: pulsing animation (1 second cycle)
        if (idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail")) {
            // Faster pulse for particles
            return (float) (Math.sin(elapsedSeconds * Math.PI * 2.0) * 0.5 + 0.5); // 0.0 to 1.0
        }

        // Default: slow rotation animation
        return (float) (elapsedSeconds % 1.0);
    }

    /**
     * Check if a cosmetic matches the current filter type.
     */
    private boolean matchesFilter(String cosmeticId) {
        if (currentFilter == CosmeticType.ALL) {
            return true;
        }

        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return false;
        }

        String idLower = cosmeticId.toLowerCase();

        // Check CosmeticManager metadata first for type information
        com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = com.kingodogo.buildscape.cosmetics.CosmeticManager
                .getInstance().getMetadata(cosmeticId);

        switch (currentFilter) {
            case WINGS:
                if (metadata != null
                        && metadata.type == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.WINGS) {
                    return true;
                }
                return idLower.contains("elytra") || idLower.contains("wing");
            case PARTICLES:
                if (metadata != null
                        && metadata.type == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.PARTICLE_TRAIL) {
                    return true;
                }
                return idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail");
            case GEAR:
                // Check if it's a HEAD cosmetic (custom head models like builder's hat)
                if (metadata != null
                        && metadata.type == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD) {
                    return true;
                }
                // Gear includes armor, weapons, tools
                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                if (stack != null && !stack.isEmpty()) {
                    net.minecraft.world.item.Item item = stack.getItem();
                    return item instanceof net.minecraft.world.item.ArmorItem ||
                            item instanceof net.minecraft.world.item.SwordItem ||
                            item instanceof net.minecraft.world.item.BowItem ||
                            item instanceof net.minecraft.world.item.TridentItem ||
                            item instanceof net.minecraft.world.item.AxeItem ||
                            idLower.contains("helmet") || idLower.contains("chestplate") ||
                            idLower.contains("leggings") || idLower.contains("boots") ||
                            idLower.contains("sword") || idLower.contains("bow") ||
                            idLower.contains("trident") || idLower.contains("axe") ||
                            idLower.contains("hat");
                }
                return idLower.contains("helmet") || idLower.contains("chestplate") ||
                        idLower.contains("leggings") || idLower.contains("boots") ||
                        idLower.contains("sword") || idLower.contains("bow") ||
                        idLower.contains("trident") || idLower.contains("axe") ||
                        idLower.contains("hat");
            default:
                return true;
        }
    }

    /**
     * Update the filtered cosmetic list based on current filter.
     */
    public void updateCosmeticList() {
        // Filter cosmetics based on current filter type
        filteredCosmeticIds = allCosmeticIds.stream()
                .filter(this::matchesFilter)
                .collect(Collectors.toList());

        // Sort: unlocked first, then locked
        Set<String> unlocked = state.getUnlockedCosmetics();
        List<String> unlockedList = filteredCosmeticIds.stream()
                .filter(unlocked::contains)
                .collect(Collectors.toList());
        List<String> lockedList = filteredCosmeticIds.stream()
                .filter(id -> !unlocked.contains(id))
                .collect(Collectors.toList());

        filteredCosmeticIds = new ArrayList<>();
        filteredCosmeticIds.addAll(unlockedList);
        filteredCosmeticIds.addAll(lockedList);

        // Pre-resolve and cache ALL items in the filtered list
        // This ensures items are ready to render immediately, not just when clicked
        for (String cosmeticId : filteredCosmeticIds) {
            // Resolve the item stack to cache it in CosmeticRegistry
            // This ensures the item is ready for rendering
            // Even if stack is null, the registry will cache the null result to avoid
            // repeated lookups
            cosmeticRegistry.resolveToItemStack(cosmeticId);
        }

        // Initialize animation timers for all animated items in the filtered list
        // This ensures all animated items start animating immediately
        long currentTime = System.nanoTime() / 1000000L;
        for (String cosmeticId : filteredCosmeticIds) {
            if (isAnimatedEntity(cosmeticId) && !itemAnimationTimes.containsKey(cosmeticId)) {
                // Stagger animation start times slightly for visual variety
                itemAnimationTimes.put(cosmeticId, currentTime);
            }

            // Initialize rotation timers for all items (for 3D pillar-style rendering)
            // Stagger start times slightly so items don't all rotate in sync
            if (!itemRotationTimes.containsKey(cosmeticId)) {
                int index = filteredCosmeticIds.indexOf(cosmeticId);
                int staggerOffset = (int) (index * 200); // 200ms stagger per item for variety
                itemRotationTimes.put(cosmeticId, currentTime - staggerOffset);
            }
        }
    }

    /**
     * Set the list of all available cosmetic IDs.
     * Called when API data is loaded.
     * Shows all cosmetics (not just armor/weapons).
     */
    public void setAllCosmeticIds(List<String> cosmeticIds) {
        this.allCosmeticIds = new ArrayList<>();
        if (cosmeticIds != null) {
            this.allCosmeticIds.addAll(cosmeticIds);
        }
        updateCosmeticList();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // All coordinates are in GUI-scaled space - no scale transformation needed
        // Items will naturally scale with the user's GUI scale setting
        // Items are always sized to fit exactly 4 per row

        // Calculate scissor with actual GUI scale (for proper clipping)
        // Scissor should only clip the item area, not the buttons
        int titleHeight = 15;
        // Add spacing below filter buttons so items don't appear right at the button
        // edge
        int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
        int scissorHeight = height - (renderStartY - startY); // Height from renderStartY to bottom

        int windowHeight = mc.getWindow().getHeight();
        double actualGuiScale = mc.getWindow().getGuiScale();

        // Panel bounds (startX, startY, width, height) are in GUI-scaled coordinates
        // Scissor needs to be in actual window pixels (physical pixels)
        // Convert GUI-scaled coordinates to actual window pixels for scissor
        int scissorX = (int) (startX * actualGuiScale);
        int scissorWidth = (int) (width * actualGuiScale);
        // Scissor Y is from bottom of window
        // renderStartY and scissorHeight are in GUI-scaled coordinates, convert to
        // actual pixels
        int renderStartYActual = (int) (renderStartY * actualGuiScale);
        int scissorHeightActual = (int) (scissorHeight * actualGuiScale);
        int scissorYAdjusted = windowHeight - (renderStartYActual + scissorHeightActual);
        int scissorHeightScaled = scissorHeightActual;

        // Don't enable scissor yet - render buttons first, then enable scissor for
        // items

        // Mouse coordinates are already in GUI-scaled space (same as panel bounds)
        int relativeMouseX = mouseX - startX;
        int relativeMouseY = mouseY - startY;

        // Render background
        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);

        // Render title
        String title = "Available Cosmetics";
        int titleWidth = mc.font.width(title);
        mc.font.draw(poseStack, title,
                startX + (width - titleWidth) / 2,
                startY + PADDING,
                0xFFFFFF);

        // Render filter buttons
        int buttonY = startY + PADDING + 15;
        int buttonX = startX + PADDING;
        int buttonWidth = (width - PADDING * 2 - BUTTON_SPACING * (CosmeticType.values().length - 1))
                / CosmeticType.values().length;

        for (CosmeticType type : CosmeticType.values()) {
            boolean isSelected = type == currentFilter;
            boolean isHovered = relativeMouseX >= buttonX && relativeMouseX < buttonX + buttonWidth &&
                    relativeMouseY >= buttonY && relativeMouseY < buttonY + BUTTON_HEIGHT;

            // Render button background
            int bgColor = isSelected ? 0xAA000000 : (isHovered ? 0xAA333333 : 0xAA222222);
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonWidth, buttonY + BUTTON_HEIGHT, bgColor);

            // Render button border
            int borderColor = isSelected ? type.getColor() : 0xFF666666;
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonWidth, buttonY + 1, borderColor); // Top
            GuiComponent.fill(poseStack, buttonX, buttonY + BUTTON_HEIGHT - 1, buttonX + buttonWidth,
                    buttonY + BUTTON_HEIGHT, borderColor); // Bottom
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + 1, buttonY + BUTTON_HEIGHT, borderColor); // Left
            GuiComponent.fill(poseStack, buttonX + buttonWidth - 1, buttonY, buttonX + buttonWidth,
                    buttonY + BUTTON_HEIGHT, borderColor); // Right

            // Render button text
            int textColor = isSelected ? type.getColor() : 0xCCCCCC;
            int textWidth = mc.font.width(type.getName());
            mc.font.draw(poseStack, type.getName(),
                    buttonX + (buttonWidth - textWidth) / 2,
                    buttonY + (BUTTON_HEIGHT - 8) / 2,
                    textColor);

            buttonX += buttonWidth + BUTTON_SPACING;
        }

        // Now enable scissor for item rendering area (after buttons are rendered)
        RenderSystem.enableScissor(scissorX, scissorYAdjusted, scissorWidth, scissorHeightScaled);

        // Adjust render area to account for title and filter buttons
        // titleHeight and renderStartY are already calculated above for scissor
        int renderHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT - ITEM_AREA_TOP_SPACING;

        // Calculate scroll bounds
        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double maxScroll = Math.max(0, (totalRows - visibleRows) * (itemSize + itemSpacing));
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        // Calculate which rows to render - render all rows that could be visible
        // Add extra rows above and below for smooth scrolling
        double rowHeight = itemSize + itemSpacing;
        int startRow = (int) (scrollOffset / rowHeight);
        // Render 2 extra rows before visible area for smooth scrolling
        startRow = Math.max(0, startRow - 2);
        // Render enough rows to cover visible area + 2 extra rows after
        int endRow = Math.min(startRow + visibleRows + 4, totalRows);

        // Show message if no cosmetics
        if (filteredCosmeticIds.isEmpty()) {
            String noItems = "No cosmetics available";
            int textWidth = mc.font.width(noItems);
            mc.font.draw(poseStack, noItems,
                    startX + (width - textWidth) / 2,
                    renderStartY + renderHeight / 2,
                    0xAAAAAA);
            RenderSystem.disableScissor();
            return;
        }

        // Update preview animation if selected cosmetic changed
        String selectedCosmeticId = state.getSelectedCosmeticId();
        if (selectedCosmeticId != null && !selectedCosmeticId.equals(lastSelectedCosmeticId)) {
            itemStartTime = System.nanoTime() / 1000000L;
            lastSelectedCosmeticId = selectedCosmeticId;
        }

        // Update rotation and bob animation for selected item (3D preview)
        if (selectedCosmeticId != null) {
            long currentTime = System.nanoTime() / 1000000L;
            float elapsedSeconds = (currentTime - itemStartTime) / 1000.0f;
            rotation = (elapsedSeconds * 90.0f) % 360.0f; // 90 degrees per second
            bobOffset = (float) Math.sin(elapsedSeconds * 2.0f) * 0.05f;
        }

        for (int row = startRow; row < endRow; row++) {
            // Calculate row Y position based on scroll offset
            // Formula: rowY = renderStartY + (row * rowHeight) - scrollOffset
            double rowYDouble = renderStartY + (row * rowHeight) - scrollOffset;
            int rowY = (int) rowYDouble;

            // Skip rendering if row is completely outside visible area (with some margin
            // for smooth scrolling)
            if (rowY + itemSize < renderStartY - itemSize || rowY > renderStartY + renderHeight + itemSize) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredCosmeticIds.size())
                    break;

                String cosmeticId = filteredCosmeticIds.get(index);
                // Calculate item X position in GUI-scaled space
                // Calculate item X position: startX + gap + col * (itemSize + gap)
                // This ensures items fill the entire panel width with equal gaps
                int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

                // Check if hovering (use relative mouse coordinates in GUI-scaled space)
                boolean isHovered = relativeMouseX >= (itemX - startX) && relativeMouseX < (itemX - startX + itemSize)
                        &&
                        relativeMouseY >= (rowY - startY) && relativeMouseY < (rowY - startY + itemSize);

                // Check if unlocked
                boolean isUnlocked = state.isUnlocked(cosmeticId);
                boolean isSelected = cosmeticId.equals(selectedCosmeticId);
                boolean isEquipped = state.isEquipped(cosmeticId);

                // Render item slot background
                int bgColor;
                if (isEquipped) {
                    bgColor = 0x8000FF00; // Green highlight for equipped
                } else if (isSelected) {
                    bgColor = 0x80FFFFFF; // White highlight for selected
                } else if (isHovered) {
                    bgColor = isUnlocked ? 0x40CCCCCC : 0x40CC0000; // Gray if unlocked, red if locked
                } else {
                    bgColor = isUnlocked ? 0x33CCCCCC : 0x33CC0000; // Darker if not hovered
                }
                GuiComponent.fill(poseStack, itemX, rowY, itemX + itemSize, rowY + itemSize, bgColor);

                // Render selection/equipped border
                if (isEquipped) {
                    // Green border for equipped items
                    int borderColor = 0xFF00FF00;
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + itemSize + 1, rowY + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY + itemSize - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + 1, rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX + itemSize - 1, rowY - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                } else if (isSelected) {
                    // White border for selected items
                    int borderColor = 0xFFFFFFFF;
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + itemSize + 1, rowY + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY + itemSize - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + 1, rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX + itemSize - 1, rowY - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                }

                // Render item - ALL items render, regardless of equip status
                // Use 3D preview for selected item, animated for animated entities, 2D for
                // others
                // ALWAYS resolve and render - never skip based on equip status

                // Check if it's a particle trail (special handling)
                boolean isParticleTrail = cosmeticId != null &&
                        cosmeticId.toLowerCase().contains("particle") &&
                        (cosmeticId.toLowerCase().contains("trail") ||
                                cosmeticId.toLowerCase().contains("star") ||
                                cosmeticId.toLowerCase().contains("sparkle") ||
                                cosmeticId.toLowerCase().contains("effect"));

                // Check if it's a HEAD cosmetic (custom head model)
                com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata headMetadata = com.kingodogo.buildscape.cosmetics.CosmeticManager
                        .getInstance().getMetadata(cosmeticId);
                boolean isHeadCosmetic = headMetadata != null &&
                        headMetadata.type == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD;

                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);

                // For HEAD cosmetics, use a leather helmet as placeholder for display
                if (isHeadCosmetic && (stack == null || stack.isEmpty())) {
                    stack = new ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET);
                }

                // Ensure stack is valid - if null or empty, try to resolve again
                if (stack == null || stack.isEmpty()) {
                    // Try resolving again in case it failed
                    stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                }

                // For particle trails and HEAD cosmetics, always render (even if stack is null,
                // we'll show placeholder)
                if (stack != null && !stack.isEmpty() || isParticleTrail || isHeadCosmetic) {
                    boolean isAnimated = isAnimatedEntity(cosmeticId);

                    // Initialize animation timer for animated entities if not exists
                    // This ensures all animated items animate continuously from the start
                    if (isAnimated) {
                        if (!itemAnimationTimes.containsKey(cosmeticId)) {
                            // Initialize animation timer when first seen - use staggered start times for
                            // variety
                            long baseTime = System.nanoTime() / 1000000L;
                            // Stagger by index to create wave effect
                            int staggerOffset = (int) (index * 100); // 100ms stagger per item
                            itemAnimationTimes.put(cosmeticId, baseTime - staggerOffset);
                        }
                    }

                    // Render ALL items in 3D like pillars (rotating continuously)
                    // For animated items, show their animations; for regular items, show rotation
                    // Particle trails always use renderAnimatedEntity (even when selected)
                    try {
                        if (isSelected && !isParticleTrail) {
                            // Render 3D preview for selected item (with enhanced rotation/zoom)
                            // Particle trails use renderAnimatedEntity for both selected and non-selected
                            render3DItemPreview(poseStack, stack, itemX + itemSize / 2, rowY + itemSize / 2,
                                    partialTick);
                        } else if (isAnimated || isParticleTrail) {
                            // Render animated entity with animation (particle trails, wings, etc.)
                            // This shows the actual animation, not just rotation
                            // For particle trails, this renders the particle preview
                            renderAnimatedEntity(poseStack, stack, cosmeticId, itemX + itemSize / 2,
                                    rowY + itemSize / 2, partialTick, false);
                        } else {
                            // Render ALL regular items in 3D like pillars (rotating and bobbing)
                            // This is the default rendering for all non-animated items
                            render3DItemLikePillar(poseStack, stack, cosmeticId, itemX + itemSize / 2,
                                    rowY + itemSize / 2, partialTick);
                        }
                    } catch (Exception e) {
                        // If any rendering fails, show placeholder
                        BuildScape.getLogger().warn("Failed to render cosmetic item (general): " + cosmeticId, e);
                        mc.font.draw(poseStack, "?", itemX + itemSize / 2 - 3, rowY + itemSize / 2 - 3, 0xFF0000);
                    }
                } else if (!isParticleTrail) {
                    // Render placeholder for invalid cosmetic - show that item exists but can't be
                    // resolved
                    // (Particle trails are handled above)
                    mc.font.draw(poseStack, "?", itemX + itemSize / 2 - 3, rowY + itemSize / 2 - 3, 0xFF0000);
                } else {
                    // Render particle trail placeholder (sparkle icon)
                    String sparkle = "✨";
                    int sparkleWidth = mc.font.width(sparkle);
                    mc.font.draw(poseStack, sparkle,
                            itemX + itemSize / 2 - sparkleWidth / 2,
                            rowY + itemSize / 2 - 4,
                            0xFFFF00); // Yellow sparkle
                }

                // Render lock icon if locked
                if (!isUnlocked) {
                    GuiComponent.fill(poseStack, itemX + itemSize - 8, rowY, itemX + itemSize, rowY + 8, 0xFF000000);
                    mc.font.draw(poseStack, "🔒", itemX + itemSize - 7, rowY + 1, 0xFFFFFF);
                }

                // Render color indicator box if cosmetic supports color
                CosmeticManager cosmeticManager = CosmeticManager.getInstance();
                if (cosmeticManager.supportsColor(cosmeticId)) {
                    // Get stored color or default
                    UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
                    CosmeticsConfig config = CosmeticsConfig.get();
                    String hexColor = playerUuid != null ? config.getCosmeticColor(playerUuid, cosmeticId) : null;

                    // Default color if not set
                    int color = 0xFFFFFF; // White
                    if (hexColor != null && !hexColor.isEmpty()) {
                        try {
                            String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
                            color = Integer.parseInt(hex, 16);
                        } catch (NumberFormatException e) {
                            // Use default
                        }
                    }

                    // Draw color box in bottom right corner
                    int colorBoxX = itemX + itemSize - COLOR_BOX_SIZE - 2;
                    int colorBoxY = rowY + itemSize - COLOR_BOX_SIZE - 2;

                    // Draw border
                    GuiComponent.fill(poseStack, colorBoxX - 1, colorBoxY - 1,
                            colorBoxX + COLOR_BOX_SIZE + 1, colorBoxY + COLOR_BOX_SIZE + 1, 0xFF000000);
                    // Draw color
                    GuiComponent.fill(poseStack, colorBoxX, colorBoxY,
                            colorBoxX + COLOR_BOX_SIZE, colorBoxY + COLOR_BOX_SIZE, 0xFF000000 | color);
                }
            }
        }

        // Render color picker if open
        if (colorPicker != null && selectedCosmeticForColor != null) {
            // Render header/title bar
            int headerHeight = 20;
            // Draw header background (Darker gray with better visibility)
            GuiComponent.fill(poseStack, colorPicker.x, colorPicker.y - headerHeight,
                    colorPicker.x + colorPicker.getWidth(), colorPicker.y, 0xFF222222);
            // Draw border around header
            GuiComponent.fill(poseStack, colorPicker.x - 1, colorPicker.y - headerHeight - 1,
                    colorPicker.x + colorPicker.getWidth() + 1, colorPicker.y, 0xFFFFFFFF);

            // Draw Title text in header
            String headerTitle = "Color Picker";
            mc.font.draw(poseStack, headerTitle, colorPicker.x + 5, colorPicker.y - headerHeight + 6, 0xFFE0E0E0);

            // Draw drag handle icon (lines) on right side
            int handleX = colorPicker.x + colorPicker.getWidth() - 25;
            int handleY = colorPicker.y - headerHeight / 2 - 2;
            GuiComponent.fill(poseStack, handleX, handleY, handleX + 15, handleY + 1, 0xFFAAAAAA);
            GuiComponent.fill(poseStack, handleX, handleY + 3, handleX + 15, handleY + 4, 0xFFAAAAAA);
            GuiComponent.fill(poseStack, handleX, handleY + 6, handleX + 15, handleY + 7, 0xFFAAAAAA);

            // Draw window border for picker itself (connects to header)
            GuiComponent.fill(poseStack, colorPicker.x - 1, colorPicker.y,
                    colorPicker.x, colorPicker.y + colorPicker.getHeight(), 0xFFFFFFFF);
            GuiComponent.fill(poseStack, colorPicker.x + colorPicker.getWidth(), colorPicker.y,
                    colorPicker.x + colorPicker.getWidth() + 1, colorPicker.y + colorPicker.getHeight(), 0xFFFFFFFF);
            GuiComponent.fill(poseStack, colorPicker.x - 1, colorPicker.y + colorPicker.getHeight(),
                    colorPicker.x + colorPicker.getWidth() + 1, colorPicker.y + colorPicker.getHeight() + 1,
                    0xFFFFFFFF);

            colorPicker.renderButton(poseStack, (int) mouseX, (int) mouseY, partialTick);
        }

        // Render scrollbar if needed
        if (maxScroll > 0) {
            int scrollbarX = endX - SCROLLBAR_WIDTH;
            int scrollbarY = renderStartY;
            int scrollbarHeight = renderHeight;

            // Draw scrollbar track
            GuiComponent.fill(poseStack, scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH,
                    scrollbarY + scrollbarHeight, 0x80000000);

            // Calculate thumb
            int thumbHeight = Math.max(20, (int) (scrollbarHeight * (visibleRows / (double) totalRows)));
            double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
            int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));

            // Draw scrollbar thumb
            GuiComponent.fill(poseStack, scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight,
                    0xFF808080);
            GuiComponent.fill(poseStack, scrollbarX + 1, thumbY + 1, scrollbarX + SCROLLBAR_WIDTH - 1,
                    thumbY + thumbHeight - 1, 0xFFC0C0C0);
        }

        RenderSystem.disableScissor();

        // Tooltips are now rendered at tab level to ensure they're on top of everything
        // Don't render here to avoid being hidden behind other panels
    }

    /**
     * Render tooltips for hovered cosmetic items.
     * Called from tab level to ensure tooltips render on top of all other
     * components.
     */
    public void renderTooltips(PoseStack poseStack, double mouseX, double mouseY) {
        // Check if mouse is within panel bounds
        if (mouseX < startX || mouseX >= startX + width || mouseY < startY || mouseY >= startY + height) {
            return;
        }

        // Find which item is being hovered
        String hoveredCosmeticId = null;

        int titleHeight = 15;
        int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
        int renderHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT - ITEM_AREA_TOP_SPACING;

        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double rowHeight = itemSize + itemSpacing;
        int startRow = (int) (scrollOffset / rowHeight);
        startRow = Math.max(0, startRow - 2);
        int endRow = Math.min(startRow + visibleRows + 4, totalRows);

        for (int row = startRow; row < endRow; row++) {
            double rowYDouble = renderStartY + (row * rowHeight) - scrollOffset;
            int rowY = (int) rowYDouble;

            if (rowY + itemSize < renderStartY - itemSize || rowY > renderStartY + renderHeight + itemSize) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredCosmeticIds.size())
                    break;

                String cosmeticId = filteredCosmeticIds.get(index);
                int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

                if (mouseX >= itemX && mouseX < itemX + itemSize &&
                        mouseY >= rowY && mouseY < rowY + itemSize) {
                    hoveredCosmeticId = cosmeticId;
                    break;
                }
            }
            if (hoveredCosmeticId != null)
                break;
        }

        // Render tooltip if item is hovered
        if (hoveredCosmeticId != null && !hoveredCosmeticId.isEmpty()) {
            // Get tooltip text
            String tooltipText = hoveredCosmeticId;
            CosmeticRegistry registry = CosmeticRegistry.getInstance();
            ItemStack stack = registry.resolveToItemStack(hoveredCosmeticId);
            if (stack != null && !stack.isEmpty()) {
                net.minecraft.network.chat.Component hoverName = stack.getHoverName();
                if (hoverName != null) {
                    tooltipText = hoverName.getString();
                }
            }

            if (tooltipText == null || tooltipText.isEmpty() || tooltipText.equals(hoveredCosmeticId)) {
                CosmeticManager cosmeticManager = CosmeticManager.getInstance();
                com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager
                        .getMetadata(hoveredCosmeticId);
                if (metadata != null && metadata.name != null && !metadata.name.isEmpty()) {
                    tooltipText = metadata.name;
                } else {
                    String idPart = hoveredCosmeticId;
                    if (hoveredCosmeticId.startsWith("buildscape:cosmatics/")) {
                        idPart = hoveredCosmeticId.substring(hoveredCosmeticId.lastIndexOf("/") + 1);
                    }
                    tooltipText = idPart.replace("_", " ");
                }
            }

            if (tooltipText == null || tooltipText.isEmpty()) {
                tooltipText = "Unknown Item";
            }

            // Render tooltip - ensure it's visible
            RenderSystem.disableScissor();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            poseStack.pushPose();
            poseStack.translate(0, 0, 500);

            // Calculate tooltip size with minimal padding
            int textWidth = mc.font.width(tooltipText);
            int textHeight = mc.font.lineHeight;
            int padding = 3; // Minimal padding
            int tooltipWidth = textWidth + padding * 2;
            int tooltipHeight = textHeight + padding * 2;

            int tooltipX = (int) mouseX + 10;
            int tooltipY = (int) mouseY - 12;

            int screenWidth = mc.screen != null ? mc.screen.width : mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.screen != null ? mc.screen.height : mc.getWindow().getGuiScaledHeight();
            if (tooltipX + tooltipWidth > screenWidth) {
                tooltipX = (int) mouseX - tooltipWidth - 10;
            }
            if (tooltipY < 0) {
                tooltipY = (int) mouseY + 20;
            }
            if (tooltipY + tooltipHeight > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 2;
            }

            // Background - tight fit
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight,
                    0xF0000000);
            // Border - 1 pixel border
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + 1, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY + tooltipHeight - 1, tooltipX + tooltipWidth,
                    tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + 1, tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX + tooltipWidth - 1, tooltipY, tooltipX + tooltipWidth,
                    tooltipY + tooltipHeight, 0xFFCCCCCC);

            // Text - centered with padding
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            mc.font.draw(poseStack, tooltipText, tooltipX + padding, tooltipY + padding, 0xFFFFFF);

            poseStack.popPose();

            // Restore state
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }

    /**
     * Render animated entity (wings, particles, etc.) with proper animations.
     * All animated entities animate continuously. Hovering enhances the animation.
     */
    private void renderAnimatedEntity(PoseStack poseStack, ItemStack stack, String cosmeticId, int centerX, int centerY,
            float partialTick, boolean isHovered) {
        Level level = mc.level;
        if (level == null) {
            // Fallback to 2D if no level
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        // NO hover effects - animations are consistent regardless of mouse position
        // Always use normal speed multiplier (1.0f)
        float animSpeedMultiplier = 1.0f;

        // Get animation progress (all animated entities animate continuously)
        float animProgress = getAnimationProgress(cosmeticId, partialTick, animSpeedMultiplier);
        String idLower = cosmeticId.toLowerCase();

        // Set up 3D rendering context
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Setup lighting for GUI 3D rendering
        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);

        // Get buffer source for 3D rendering
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        // Translate to center (GUI coordinates)
        poseStack.translate(centerX, centerY, 100.0f);

        // Apply animation-based transformations
        if (idLower.contains("elytra") || idLower.contains("wing")) {
            // Wings: opening/closing animation
            // Scale wings based on animation progress (0.8 to 1.2 scale)
            // Consistent animation, no hover effects
            float wingScaleBase = 0.8f;
            float wingScaleRange = 0.4f;
            float wingScale = wingScaleBase + animProgress * wingScaleRange;
            // Scale proportional to itemSize (which is calculated to fit 4 per row)
            // itemSize is in GUI-scaled coordinates, so it already scales with GUI scale
            // Scale factor: itemSize * 0.46875 gives good fit for wings (for 128px item
            // size at scale 2, that's 60.0f)
            float baseScale = itemSize * 0.46875f * wingScale;
            poseStack.scale(baseScale, -baseScale, baseScale);

            // Rotate wings slightly based on animation (consistent rotation)
            float wingRotationMax = 30.0f;
            float wingRotation = (animProgress - 0.5f) * wingRotationMax;
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(wingRotation));
        } else if (idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail")) {
            // For particle trails, render a visual representation in the GUI slot
            // Don't spawn particles in the world - render them directly in the slot
            poseStack.popPose();

            // Set up rendering state for GUI particle preview
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();

            // Get particle color for rendering
            UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
            com.kingodogo.buildscape.config.CosmeticsConfig config = com.kingodogo.buildscape.config.CosmeticsConfig
                    .get();
            String storedColor = playerUuid != null ? config.getCosmeticColor(playerUuid, cosmeticId) : null;
            float[] color = com.kingodogo.buildscape.client.ParticleTrailHandler.getParticleColor(cosmeticId);

            if (storedColor != null && !storedColor.isEmpty()) {
                try {
                    String hex = storedColor.startsWith("#") ? storedColor.substring(1) : storedColor;
                    int rgb = Integer.parseInt(hex, 16);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    color = new float[] { r / 255.0f, g / 255.0f, b / 255.0f };
                } catch (NumberFormatException e) {
                    // Use default
                }
            }

            // Render particle preview as animated particles in the slot
            // Use animation progress for movement (glow up effect)
            int particleCount = 4; // Show 4 particles in a trail pattern
            for (int i = 0; i < particleCount; i++) {
                // Calculate position - particles move upward in a trail pattern
                float baseOffsetY = -12.0f; // Start below center
                float trailOffset = (animProgress * 24.0f); // Move up over time
                float offsetY = baseOffsetY + trailOffset + (i * 6.0f); // Stagger particles
                float offsetX = (float) (Math.sin(animProgress * Math.PI * 2 + i) * 6.0f); // Slight horizontal sway

                float particleX = centerX + offsetX;
                float particleY = centerY + offsetY;

                // Size and opacity based on position in trail (fade out as they go up)
                float trailProgress = (i + animProgress) / particleCount;
                float particleSize = 8.0f * (1.0f - trailProgress * 0.5f); // Smaller as they go up, slightly larger
                                                                           // base
                float alpha = 0.9f - (trailProgress * 0.6f); // Fade out

                // Set color with alpha
                RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);

                // Render particle as a star/sparkle shape
                // Use a simple cross pattern to represent sparkle
                poseStack.pushPose();
                poseStack.translate(particleX, particleY, 0);
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(animProgress * 360.0f + i * 45.0f));

                float halfSize = particleSize / 2.0f;

                // Get particle sprite based on shape
                CosmeticManager manager = CosmeticManager.getInstance();
                String shape = manager.getParticleShape(cosmeticId);

                // Use direct texture rendering for specific recognized shapes
                // This bypasses the atlas which seems to be having issues with these textures
                // in the GUI
                net.minecraft.resources.ResourceLocation textureLoc = null;

                if (shape.equals("heart") || idLower.contains("heart")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/heart.png");
                } else if (shape.equals("bubble") || idLower.contains("bubble")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/bubble.png"); // or bubble_pop_0? bubble.png exists
                } else if (shape.equals("note") || idLower.contains("note")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/note.png");
                } else if (shape.equals("cherry_leaves") || idLower.contains("cherry")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/spore_blossom_air.png");
                } else if (shape.equals("firework") || idLower.contains("flash")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/spark_0.png");
                } else if (shape.equals("cake") || idLower.contains("flame")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/flame.png");
                } else if (shape.equals("snowflake") || idLower.contains("snowflake")) {
                    // Custom snowflake texture from ModParticles
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/snowflake_1.png");
                } else {
                    // Default sparkle/star - use generic_0 (part of a sheet usually) or glint
                    // glint.png exists in textures/misc/glint.png but not particle?
                    // Use flash or something reliable
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/flash.png");
                }

                // Render direct texture if set
                if (textureLoc != null) {
                    RenderSystem.setShaderTexture(0, textureLoc);

                    // Use white color if texture provides its own color (like heart)
                    if (shape.equals("heart")) {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
                    } else {
                        RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);
                    }

                    // Render using blit with full UVs (0-1)
                    int x0 = (int) (-halfSize);
                    int x1 = (int) (halfSize);
                    int y0 = (int) (-halfSize);
                    int y1 = (int) (halfSize);

                    com.mojang.blaze3d.vertex.Tesselator tesselator = com.mojang.blaze3d.vertex.Tesselator
                            .getInstance();
                    com.mojang.blaze3d.vertex.BufferBuilder bufferbuilder = tesselator.getBuilder();
                    RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
                    bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
                            com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);
                    bufferbuilder.vertex(poseStack.last().pose(), x0, y1, 0).uv(0.0f, 1.0f).endVertex();
                    bufferbuilder.vertex(poseStack.last().pose(), x1, y1, 0).uv(1.0f, 1.0f).endVertex();
                    bufferbuilder.vertex(poseStack.last().pose(), x1, y0, 0).uv(1.0f, 0.0f).endVertex();
                    bufferbuilder.vertex(poseStack.last().pose(), x0, y0, 0).uv(0.0f, 0.0f).endVertex();
                    tesselator.end();
                }

                // Skip sprite atlas lookup code block which follows
                // We fake the rest by wrapping it in 'if (false)' or just removing it via
                // replace
                // But since I am replacing the block, I just won't include it.

                // Fallback (if somehow textureLoc was null, which it isn't based on
                // implementation)
                if (textureLoc == null) {
                    GuiComponent.fill(poseStack, (int) (-halfSize), (int) (-halfSize), (int) halfSize, (int) halfSize,
                            ((int) (alpha * 255) << 24) | 0xFFFFFF);
                }

                poseStack.popPose();
            }

            // Reset color
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            // Reset color
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableDepthTest();

            // Don't render item for particle trails - visual particles are the preview
            return;
        } else

        {
            // Default: slow rotation (consistent speed)
            // Scale proportional to itemSize (which is calculated to fit 4 per row)
            // itemSize is in GUI-scaled coordinates, so it already scales with GUI scale
            // Scale factor: itemSize * 0.46875 gives good fit (for 128px item size at scale
            // 2, that's 60.0f)
            float baseScale = itemSize * 0.46875f;
            poseStack.scale(baseScale, -baseScale, baseScale);
            float defaultRotation = animProgress * 360.0f * animSpeedMultiplier;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(defaultRotation));
        }

        // Get item model
        BakedModel model = itemRenderer.getModel(stack, level, null, 0);

        // Check if item has enchantments for glint rendering
        boolean hasGlint = stack.hasFoil();

        // Full brightness for GUI rendering
        int lightLevel = 15728880; // Full brightness (15 sky, 15 block)
        int overlay = 0; // No overlay

        // Render the item in 3D
        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED,
                    hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch(); // Flush buffers
        } catch (Exception e) {
            // If 3D rendering fails, fallback to 2D
            BuildScape.getLogger().debug("3D animated entity rendering failed, using 2D fallback: " + e.getMessage());
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        poseStack.popPose();

        // Clean up rendering state
        RenderSystem.disableBlend();
    }

    /**
     * Render item in 3D like pillars do - with continuous rotation and bobbing.
     * This is used for ALL regular items in the cosmetics list.
     */
    private void render3DItemLikePillar(PoseStack poseStack, ItemStack stack, String cosmeticId, int centerX,
            int centerY, float partialTick) {
        Level level = mc.level;
        if (level == null) {
            // Fallback to 2D if no level
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        // Set up 3D rendering context (same as pillar rendering)
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Setup lighting for GUI 3D rendering (same as pillars)
        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);

        // Get buffer source for 3D rendering
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        // Translate to center (GUI coordinates)
        poseStack.translate(centerX, centerY, 100.0f);

        // Calculate rotation like pillars do (90 degrees per second, continuous)
        // Use client-side system time for smooth animation
        long currentRenderTime = System.nanoTime() / 1000000L; // Convert nanoseconds to milliseconds

        // Get or create rotation start time for this item
        Long startTime = itemRotationTimes.get(cosmeticId);
        if (startTime == null) {
            // First time rendering this item - initialize timer
            startTime = currentRenderTime;
            itemRotationTimes.put(cosmeticId, startTime);
        }

        // Calculate elapsed time in seconds since item was first rendered
        float elapsedSeconds = (currentRenderTime - startTime + partialTick * 50.0f) / 1000.0f;

        // Calculate rotation (90 degrees per second, same as pillars)
        float rotationSpeed = 90.0f; // degrees per second
        float rotation = (elapsedSeconds * rotationSpeed) % 360.0f;

        // Add floating/bobbing animation (same as pillars)
        float bobAmount = (float) Math.sin(elapsedSeconds * 2.0f) * 0.05f; // Bob up and down
        poseStack.translate(0, bobAmount, 0);

        // Apply rotation around Y axis (same as pillars)
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        // Apply scale proportional to itemSize (which is calculated to fit 4 per row)
        // itemSize is in GUI-scaled coordinates, so it already scales with GUI scale
        // Scale factor: itemSize * 0.390625 gives good fit (for 128px item size at
        // scale 2, that's 50.0f)
        float baseScale = itemSize * 0.390625f;
        poseStack.scale(baseScale, -baseScale, baseScale); // Invert Y for GUI coordinate system

        // Get item model
        BakedModel model = itemRenderer.getModel(stack, level, null, 0);

        // Check if item has enchantments for glint rendering
        boolean hasGlint = stack.hasFoil();

        // Full brightness for GUI rendering (same as pillars)
        int lightLevel = 15728880; // Full brightness (15 sky, 15 block)
        int overlay = 0; // No overlay

        // Render the item in 3D using FIXED transform type (same as pillars)
        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED,
                    hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch(); // Flush buffers
        } catch (Exception e) {
            // If 3D rendering fails, fallback to 2D
            BuildScape.getLogger().debug("3D pillar-style item rendering failed, using 2D fallback: " + e.getMessage());
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        poseStack.popPose();

        // Clean up rendering state
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    /**
     * Render item in 3D preview (for selected items).
     */
    private void render3DItemPreview(PoseStack poseStack, ItemStack stack, int centerX, int centerY,
            float partialTick) {
        Level level = mc.level;
        if (level == null) {
            // Fallback to 2D if no level
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        // Set up 3D rendering context
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Setup lighting for GUI 3D rendering
        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);

        // Get buffer source for 3D rendering
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        // Translate to center (GUI coordinates)
        float bobY = bobOffset * 5.0f; // Smaller bob for list items
        poseStack.translate(centerX, centerY + bobY, 100.0f);

        // Apply scale proportional to itemSize (which is calculated to fit 4 per row)
        // itemSize is in GUI-scaled coordinates, so it already scales with GUI scale
        // Scale factor: itemSize * 0.625 gives good fit (for 128px item size at scale
        // 2, that's 80.0f)
        float baseScale = itemSize * 0.625f * zoom;
        poseStack.scale(baseScale, -baseScale, baseScale); // Invert Y for GUI coordinate system

        // Apply rotation around Y axis
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        // Get item model
        BakedModel model = itemRenderer.getModel(stack, level, null, 0);

        // Check if item has enchantments for glint rendering
        boolean hasGlint = stack.hasFoil();

        // Full brightness for GUI rendering
        int lightLevel = 15728880; // Full brightness (15 sky, 15 block)
        int overlay = 0; // No overlay

        // Render the item in 3D
        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED,
                    hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch(); // Flush buffers
        } catch (Exception e) {
            // If 3D rendering fails, fallback to 2D
            BuildScape.getLogger().debug("3D item rendering failed, using 2D fallback: " + e.getMessage());
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        poseStack.popPose();

        // Clean up rendering state
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        // Check if clicking on color picker first
        if (colorPicker != null && selectedCosmeticForColor != null) {
            // Check if clicking on header (drag area)
            // Header is 20px high above the color picker
            int headerHeight = 20;
            // Widen hit box significantly to ensure it catches clicks
            // MouseX/Y are doubles, allow some margin
            double hitMargin = 5.0;
            if (mouseX >= colorPicker.x - hitMargin && mouseX < colorPicker.x + colorPicker.getWidth() + hitMargin &&
                    mouseY >= colorPicker.y - headerHeight - hitMargin && mouseY < colorPicker.y + hitMargin) {
                if (button == 0) {
                    isDraggingColorPicker = true;
                    pickerDragOffsetX = mouseX - colorPicker.x;
                    pickerDragOffsetY = mouseY - colorPicker.y;
                    return true;
                }
            }

            // Check if clicking inside color picker content
            boolean handled = colorPicker.mouseClicked(mouseX, mouseY, button);

            // If internal widget handled it, return true
            if (handled) {
                return true;
            }

            // Check if click was within color picker bounds (body or header)
            // If so, CONSUME the click to prevent passthrough, even if the widget didn't do
            // anything with it
            boolean insidePicker = mouseX >= colorPicker.x && mouseX < colorPicker.x + colorPicker.getWidth() &&
                    mouseY >= colorPicker.y && mouseY < colorPicker.y + colorPicker.getHeight();

            // Check header bounds (drag area)
            boolean insideHeader = mouseX >= colorPicker.x - 5 && mouseX < colorPicker.x + colorPicker.getWidth() + 5 &&
                    mouseY >= colorPicker.y - headerHeight - 5 && mouseY < colorPicker.y + 5;

            // If dragging, or inside picker/header, consume the event
            if (isDraggingColorPicker || insidePicker || insideHeader) {
                return true;
            }

            // If click is outside color picker AND header, close it (only if not dragging)
            if (!insidePicker && !insideHeader) {
                // closeColorPicker(); // Don't close automatically for now
            }
        }

        if (button != 0)
            return false; // Only handle left click

        // Mouse coordinates are already in GUI-scaled space (same as panel bounds)
        // No transformation needed

        // Check if clicking on filter buttons
        int buttonY = startY + PADDING + 15;
        int buttonX = startX + PADDING;
        int buttonWidth = (width - PADDING * 2 - BUTTON_SPACING * (CosmeticType.values().length - 1))
                / CosmeticType.values().length;

        for (CosmeticType type : CosmeticType.values()) {
            if (mouseX >= buttonX && mouseX < buttonX + buttonWidth &&
                    mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT) {
                // Change filter
                currentFilter = type;
                updateCosmeticList();
                scrollOffset = 0; // Reset scroll when changing filter
                return true;
            }
            buttonX += buttonWidth + BUTTON_SPACING;
        }

        // Calculate which item was clicked (account for title and filter buttons)
        // Make sure click is below filter buttons
        int titleHeight = 15;
        int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
        if (mouseY < renderStartY) {
            return false; // Clicked above filter buttons
        }

        // Use the same calculation as rendering to ensure accuracy
        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double rowHeight = itemSize + itemSpacing;
        int startRow = (int) (scrollOffset / rowHeight);
        startRow = Math.max(0, startRow - 2); // Match rendering calculation
        int endRow = Math.min(startRow + visibleRows + 4, totalRows);

        // Calculate render height for bounds checking
        int renderHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT - ITEM_AREA_TOP_SPACING;

        // Calculate which item was clicked by checking each visible item
        int clickedCol = -1;
        int clickedRow = -1;

        // Check visible items (same as rendering)
        for (int row = startRow; row < endRow; row++) {
            double rowYDouble = renderStartY + (row * rowHeight) - scrollOffset;
            int rowY = (int) rowYDouble;

            // Skip if row is outside visible area
            if (rowY + itemSize < renderStartY - itemSize || rowY > renderStartY + renderHeight + itemSize) {
                continue;
            }

            if (mouseY >= rowY && mouseY < rowY + itemSize) {
                clickedRow = row;
                // Check which column
                for (int col = 0; col < itemsPerRow; col++) {
                    int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);
                    if (mouseX >= itemX && mouseX < itemX + itemSize) {
                        clickedCol = col;

                        // Check for color box click
                        int index = row * itemsPerRow + col;
                        if (index >= 0 && index < filteredCosmeticIds.size()) {
                            String cosmeticId = filteredCosmeticIds.get(index);
                            CosmeticManager cosmeticManager = CosmeticManager.getInstance();
                            if (cosmeticManager.supportsColor(cosmeticId)) {
                                int colorBoxX = itemX + itemSize - COLOR_BOX_SIZE - 2;
                                int colorBoxY = rowY + itemSize - COLOR_BOX_SIZE - 2;

                                if (mouseX >= colorBoxX && mouseX < colorBoxX + COLOR_BOX_SIZE &&
                                        mouseY >= colorBoxY && mouseY < colorBoxY + COLOR_BOX_SIZE) {
                                    openColorPicker(cosmeticId);
                                    return true;
                                }
                            }
                        }

                        break;
                    }
                }
                break;
            }
        }

        if (clickedRow < 0 || clickedCol < 0 || clickedCol >= itemsPerRow) {
            return false; // Clicked outside items
        }

        int row = clickedRow;
        int col = clickedCol;

        if (col < 0 || col >= itemsPerRow)
            return false;

        int index = row * itemsPerRow + col;
        if (index >= 0 && index < filteredCosmeticIds.size()) {
            String cosmeticId = filteredCosmeticIds.get(index);

            // Check if unlocked (dev always has access)
            String playerUsername = mc.player != null ? mc.player.getName().getString() : null;
            boolean isDev = playerUsername != null && playerUsername.equalsIgnoreCase("Dev");
            boolean isUnlocked = state.isUnlocked(cosmeticId) || isDev;

            if (!isUnlocked) {
                // Can't equip locked items (unless dev)
                return true;
            }

            // Single click equip/unequip (no drag)
            if (state.isEquipped(cosmeticId)) {
                // Unequip - find which slot it's in and unequip from there
                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                int slotIndex = getSlotForCosmetic(stack);

                // Try to find which slot this cosmetic is actually in
                Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();
                int actualSlot = -1;
                for (Map.Entry<Integer, String> entry : equippedBySlot.entrySet()) {
                    if (cosmeticId.equals(entry.getValue())) {
                        actualSlot = entry.getKey();
                        break;
                    }
                }

                if (actualSlot >= 0) {
                    // Found in a slot - unequip from that slot
                    state.unequipCosmeticFromSlot(actualSlot);
                } else if (slotIndex >= 0) {
                    // Not in slot map but is armor type - try unequipping from expected slot
                    String equippedInSlot = state.getEquippedCosmeticInSlot(slotIndex);
                    if (cosmeticId.equals(equippedInSlot)) {
                        state.unequipCosmeticFromSlot(slotIndex);
                    } else {
                        // Remove from equipped set (might be in equipped set but not slot map)
                        state.unequipCosmetic(cosmeticId);
                    }
                } else {
                    // For non-armor cosmetics, remove from equipped set
                    state.unequipCosmetic(cosmeticId);
                }

                // Clear selection when unequipping (don't keep white border)
                if (cosmeticId.equals(state.getSelectedCosmeticId())) {
                    state.setSelectedCosmeticId(null);
                }
            } else {
                // Equip - determine slot based on cosmetic type
                // Check if it's a particle trail (no slot)
                boolean isParticleTrail = cosmeticId != null &&
                        cosmeticId.toLowerCase().contains("particle") &&
                        (cosmeticId.toLowerCase().contains("trail") ||
                                cosmeticId.toLowerCase().contains("star") ||
                                cosmeticId.toLowerCase().contains("sparkle") ||
                                cosmeticId.toLowerCase().contains("effect"));

                if (isParticleTrail) {
                    // Particle trails go directly to equipped set (no slot)
                    state.equipCosmetic(cosmeticId);
                } else {
                    // Regular cosmetics - determine slot
                    ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                    int slotIndex = getSlotForCosmetic(stack);
                    if (slotIndex >= 0) {
                        // Equip to specific slot (this will replace any existing cosmetic in that slot)
                        state.equipCosmeticToSlot(slotIndex, cosmeticId);
                    } else {
                        // For non-armor cosmetics, just add to equipped set
                        state.equipCosmetic(cosmeticId);
                    }
                }

                // Set as selected for preview when equipping
                state.setSelectedCosmeticId(cosmeticId);
            }

            return true;
        }

        return false;
    }

    /**
     * Open color picker for a cosmetic.
     */
    private void openColorPicker(String cosmeticId) {
        UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
        if (playerUuid == null)
            return;

        CosmeticsConfig config = CosmeticsConfig.get();
        String currentColor = config.getCosmeticColor(playerUuid, cosmeticId);

        // Parse current color or use white
        int color = 0xFFFFFF;
        if (currentColor != null && !currentColor.isEmpty()) {
            try {
                String hex = currentColor.startsWith("#") ? currentColor.substring(1) : currentColor;
                color = Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                // Use default
            }
        }

        // Create or update color picker
        if (colorPicker == null) {
            colorPicker = new ColorPickerWidget(0, 0, 260, 220, color, (hexColor) -> {
                // Save color when changed
                if (selectedCosmeticForColor != null && playerUuid != null) {
                    config.setCosmeticColor(playerUuid, selectedCosmeticForColor, hexColor);
                }
            });
        } else {
            colorPicker.setColor(color);
        }

        selectedCosmeticForColor = cosmeticId;

        // Position color picker near the clicked item
        // Calculate item position
        int itemIndex = filteredCosmeticIds.indexOf(cosmeticId);
        if (itemIndex >= 0) {
            int row = itemIndex / itemsPerRow;
            int col = itemIndex % itemsPerRow;
            // Calculate item X position: startX + gap + col * (itemSize + gap)
            int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

            // Calculate render start position (same as in render method)
            int titleHeight = 15;
            int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
            int rowY = renderStartY + (int) scrollOffset + row * (itemSize + itemSpacing);

            // Position picker to the right of the item, or below if not enough space
            int pickerX = itemX + itemSize + 10;
            int pickerY = rowY;

            // Ensure picker doesn't go outside panel bounds
            if (pickerX + 260 > startX + width) {
                pickerX = itemX - 260 - 10; // Position to the left instead
            }
            if (pickerY + 220 > startY + height) {
                pickerY = startY + height - 220 - 10;
            }
            if (pickerX < startX) {
                pickerX = startX + 10;
            }
            if (pickerY < startY) {
                pickerY = startY + 10;
            }

            colorPicker.x = pickerX;
            colorPicker.y = pickerY;
        }
    }

    // State for color picker dragging
    private boolean isDraggingColorPicker = false;
    private double pickerDragOffsetX = 0;
    private double pickerDragOffsetY = 0;

    /**
     * Close color picker.
     */
    private void closeColorPicker() {
        selectedCosmeticForColor = null;
        // Don't remove colorPicker, just hide it
    }

    /**
     * Get slot index for a cosmetic item.
     * Returns -1 if not an armor item.
     */
    private int getSlotForCosmetic(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }

        net.minecraft.world.item.Item item = stack.getItem();

        // Check for elytra (goes in chest slot)
        if (item instanceof ElytraItem) {
            return 1; // Chest slot
        }

        // Check for armor items
        if (item instanceof net.minecraft.world.item.ArmorItem) {
            net.minecraft.world.item.ArmorItem armor = (net.minecraft.world.item.ArmorItem) item;
            switch (armor.getSlot()) {
                case HEAD:
                    return 0; // Head slot
                case CHEST:
                    return 1; // Chest slot
                case LEGS:
                    return 2; // Legs slot
                case FEET:
                    return 3; // Feet slot
                default:
                    return -1;
            }
        }

        return -1;
    }

    /**
     * Handle mouse release (no longer needed for drag, but kept for compatibility).
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Handle dragging color picker window
        if (isDraggingColorPicker && colorPicker != null) {
            colorPicker.x = (int) (mouseX - pickerDragOffsetX);
            colorPicker.y = (int) (mouseY - pickerDragOffsetY);
            return true;
        }

        // Forward to color picker if open
        if (colorPicker != null && selectedCosmeticForColor != null) {
            if (colorPicker.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingColorPicker = false; // Stop dragging window

        // Forward to color picker if open
        if (colorPicker != null && selectedCosmeticForColor != null) {
            if (colorPicker.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        // No drag functionality, so nothing to do here
        return false;
    }

    /**
     * Get the currently dragged cosmetic ID (always returns null now - no drag).
     */
    public String getDraggedCosmeticId() {
        return null; // No drag functionality
    }

    /**
     * Cancel drag operation (no-op now).
     */
    public void cancelDrag() {
        // No drag functionality
    }

    @Override
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        // Mouse coordinates are already in GUI-scaled space (same as panel bounds)
        // Check if mouse is within panel bounds
        if (mouseX < startX || mouseX >= endX || mouseY < startY || mouseY >= endY) {
            return false;
        }

        // Check if Ctrl is pressed for zoom (on selected item)
        if (net.minecraft.client.gui.screens.Screen.hasControlDown()) {
            zoom += (float) delta * 0.1f;
            zoom = Math.max(0.5f, Math.min(2.0f, zoom)); // Clamp between 0.5x and 2x
            return true;
        }

        // Otherwise scroll the list
        // Use item size and spacing in GUI-scaled coordinates
        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double maxScroll = Math.max(0, (totalRows - visibleRows) * (itemSize + itemSpacing));

        scrollOffset -= delta * 10; // Scroll speed
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        return true;
    }
}
