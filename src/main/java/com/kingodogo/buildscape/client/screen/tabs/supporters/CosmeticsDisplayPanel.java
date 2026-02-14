package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.screen.widget.ColorPickerWidget;
import com.kingodogo.buildscape.client.screen.tabs.supporters.CosmeticColorPickerWidget;
// Note: imports might be redundant if in same package but clean to be explicit or rely on package match
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CosmeticsDisplayPanel extends BasePanel {

    private static final double FIXED_GUI_SCALE = 2.0;

    private static final int ITEMS_PER_ROW = 4;

    private static final int PADDING = 8;
    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer scrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();

    private int itemSize = 80;
    private int itemSpacing = 10;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;
    private static final int FILTER_BUTTON_AREA_HEIGHT = 30;
    private static final int ITEM_AREA_TOP_SPACING = 5;

    public enum CosmeticType {
        ALL("All", 0xFFFFFF),
        WINGS("Wings", 0x00FF00),
        PARTICLES("Particles", 0xFFFF00),
        GEAR("Gear", 0xFF8800);

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

    private final java.util.Map<String, Long> itemAnimationTimes = new java.util.HashMap<>();

    private final java.util.Map<String, Long> itemRotationTimes = new java.util.HashMap<>();

    private float rotation = 0.0f;
    private float zoom = 1.0f;
    private long itemStartTime = 0;
    private String lastSelectedCosmeticId = null;
    private float bobOffset = 0.0f;

    private CosmeticColorPickerWidget colorPicker = null;
    private String selectedCosmeticForColor = null;
    private static final int COLOR_BOX_SIZE = 12;

    @Override
    public void init() {
        itemsPerRow = ITEMS_PER_ROW;

        int gapSize = (int) (width * 0.014);
        gapSize = Math.max(2, gapSize);

        int scrollbarReservedSpace = com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer
                .getScrollbarWidth() + gapSize + 5; // +5 for offset
        int availableWidth = width - scrollbarReservedSpace;

        itemSize = (availableWidth - 4 * gapSize) / 4;

        itemSpacing = gapSize;

        itemSize = Math.max(32, itemSize);

        int titleHeight = 15;
        int availableHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT;
        visibleRows = Math.max(1, availableHeight / (itemSize + itemSpacing));

        itemStartTime = System.nanoTime() / 1000000L;

        updateCosmeticList();
    }

    private boolean isAnimatedEntity(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return false;
        }

        String idLower = cosmeticId.toLowerCase();

        if (idLower.contains("elytra") || idLower.contains("wing")) {
            return true;
        }

        if (idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail")) {
            return true;
        }

        ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
        if (stack != null && !stack.isEmpty()) {
            Item item = stack.getItem();
            return item instanceof ElytraItem;
        }

        return false;
    }

    private float getAnimationProgress(String cosmeticId, float partialTick, float speedMultiplier) {
        if (!isAnimatedEntity(cosmeticId)) {
            return 0.0f;
        }

        Long startTime = itemAnimationTimes.get(cosmeticId);
        if (startTime == null) {
            startTime = System.nanoTime() / 1000000L;
            itemAnimationTimes.put(cosmeticId, startTime);
        }

        long currentTime = System.nanoTime() / 1000000L;
        float elapsedSeconds = (currentTime - startTime + partialTick * 50.0f) / 1000.0f;
        elapsedSeconds *= speedMultiplier;

        String idLower = cosmeticId.toLowerCase();

        if (idLower.contains("elytra") || idLower.contains("wing")) {
            return (float) (Math.sin(elapsedSeconds * Math.PI) * 0.5 + 0.5);
        }

        if (idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail")) {
            return (float) (Math.sin(elapsedSeconds * Math.PI * 2.0) * 0.5 + 0.5);
        }

        return (float) (elapsedSeconds % 1.0);
    }

    private boolean matchesFilter(String cosmeticId) {
        if (currentFilter == CosmeticType.ALL) {
            return true;
        }

        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return false;
        }

        String idLower = cosmeticId.toLowerCase();

        com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = com.kingodogo.buildscape.cosmetics.CosmeticManager
                .getInstance().getMetadata(cosmeticId);

        switch (currentFilter) {
            case WINGS:
                if (metadata != null
                        && metadata.type() == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.WINGS) {
                    return true;
                }
                return idLower.contains("elytra") || idLower.contains("wing");
            case PARTICLES:
                if (metadata != null
                        && metadata
                                .type() == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.PARTICLE_TRAIL) {
                    return true;
                }
                return idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail");
            case GEAR:
                if (metadata != null
                        && metadata.type() == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD) {
                    return true;
                }
                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                if (stack != null && !stack.isEmpty()) {
                    net.minecraft.world.item.Item item = stack.getItem();
                    return item instanceof net.minecraft.world.item.ArmorItem
                            || item instanceof net.minecraft.world.item.SwordItem
                            || item instanceof net.minecraft.world.item.BowItem
                            || item instanceof net.minecraft.world.item.TridentItem
                            || item instanceof net.minecraft.world.item.AxeItem
                            || idLower.contains("helmet") || idLower.contains("chestplate")
                            || idLower.contains("leggings") || idLower.contains("boots")
                            || idLower.contains("sword") || idLower.contains("bow")
                            || idLower.contains("trident") || idLower.contains("axe")
                            || idLower.contains("hat");
                }
                return idLower.contains("helmet") || idLower.contains("chestplate")
                        || idLower.contains("leggings") || idLower.contains("boots")
                        || idLower.contains("sword") || idLower.contains("bow")
                        || idLower.contains("trident") || idLower.contains("axe")
                        || idLower.contains("hat");
            default:
                return true;
        }
    }

    public void updateCosmeticList() {
        filteredCosmeticIds = allCosmeticIds.stream()
                .filter(this::matchesFilter)
                .collect(Collectors.toList());

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

        for (String cosmeticId : filteredCosmeticIds) {
            cosmeticRegistry.resolveToItemStack(cosmeticId);
        }

        long currentTime = System.nanoTime() / 1000000L;
        for (String cosmeticId : filteredCosmeticIds) {
            if (isAnimatedEntity(cosmeticId) && !itemAnimationTimes.containsKey(cosmeticId)) {
                itemAnimationTimes.put(cosmeticId, currentTime);
            }

            if (!itemRotationTimes.containsKey(cosmeticId)) {
                int index = filteredCosmeticIds.indexOf(cosmeticId);
                int staggerOffset = index * 200;
                itemRotationTimes.put(cosmeticId, currentTime - staggerOffset);
            }
        }
    }

    public void setAllCosmeticIds(List<String> cosmeticIds) {
        this.allCosmeticIds = new ArrayList<>();
        if (cosmeticIds != null) {
            this.allCosmeticIds.addAll(cosmeticIds);
        }
        updateCosmeticList();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        int titleHeight = 15;
        int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
        int scissorHeight = height - (renderStartY - startY);

        int windowHeight = mc.getWindow().getHeight();
        double actualGuiScale = mc.getWindow().getGuiScale();

        int scissorX = (int) (startX * actualGuiScale);
        int scissorWidth = (int) (width * actualGuiScale);
        int renderStartYActual = (int) (renderStartY * actualGuiScale);
        int scissorHeightActual = (int) (scissorHeight * actualGuiScale);
        int scissorYAdjusted = windowHeight - (renderStartYActual + scissorHeightActual);
        int scissorHeightScaled = scissorHeightActual;

        int relativeMouseX = mouseX - startX;
        int relativeMouseY = mouseY - startY;

        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);

        // Draw border around panel (debug mode)
        if (com.kingodogo.buildscape.client.screen.DebugRenderConfig.RENDER_PANEL_BORDERS) {
            int borderColor = com.kingodogo.buildscape.client.screen.DebugRenderConfig.PANEL_BORDER_COLOR;
            GuiComponent.fill(poseStack, startX, startY, endX, startY + 1, borderColor); // Top
            GuiComponent.fill(poseStack, startX, endY - 1, endX, endY, borderColor); // Bottom
            GuiComponent.fill(poseStack, startX, startY, startX + 1, endY, borderColor); // Left
            GuiComponent.fill(poseStack, endX - 1, startY, endX, endY, borderColor); // Right
        }

        String title = "Available Cosmetics";
        int titleWidth = mc.font.width(title);
        mc.font.draw(poseStack, title,
                startX + (width - titleWidth) / 2,
                startY + PADDING,
                0xFFFFFF);

        int numTabs = CosmeticType.values().length;
        int resetButtonWidth = 20;
        int resetButtonHeight = 20;
        int spacing = BUTTON_SPACING;

        int availableWidth = width - PADDING * 2;
        int reservedForReset = resetButtonWidth + spacing;

        int buttonWidth = (availableWidth - reservedForReset - spacing * (numTabs - 1)) / numTabs;

        int buttonY = startY + PADDING + 15;
        int buttonX = startX + PADDING;

        for (CosmeticType type : CosmeticType.values()) {
            boolean isSelected = type == currentFilter;
            boolean isHovered = mouseX >= buttonX && mouseX < buttonX + buttonWidth
                    && mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT;

            int bgColor = isSelected ? 0xAA000000 : (isHovered ? 0xAA333333 : 0xAA222222);
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonWidth, buttonY + BUTTON_HEIGHT, bgColor);

            int borderColor = isSelected ? type.getColor() : 0xFF666666;
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonWidth, buttonY + 1, borderColor);
            GuiComponent.fill(poseStack, buttonX, buttonY + BUTTON_HEIGHT - 1, buttonX + buttonWidth,
                    buttonY + BUTTON_HEIGHT, borderColor);
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + 1, buttonY + BUTTON_HEIGHT, borderColor);
            GuiComponent.fill(poseStack, buttonX + buttonWidth - 1, buttonY, buttonX + buttonWidth,
                    buttonY + BUTTON_HEIGHT, borderColor);

            int textColor = isSelected ? type.getColor() : 0xCCCCCC;
            int textWidth = mc.font.width(type.getName());
            mc.font.draw(poseStack, type.getName(),
                    buttonX + (buttonWidth - textWidth) / 2,
                    buttonY + (BUTTON_HEIGHT - 8) / 2,
                    textColor);

            buttonX += buttonWidth + spacing;
        }

        // Reset Button positioned after the tabs
        int resetButtonX = buttonX;
        int resetButtonY = buttonY;

        isHoveringResetButton = mouseX >= resetButtonX
                && mouseX < resetButtonX + resetButtonWidth
                && mouseY >= resetButtonY
                && mouseY < resetButtonY + BUTTON_HEIGHT;

        int resetBgColor = isHoveringResetButton ? 0xFF555555 : 0xFF333333;
        GuiComponent.fill(poseStack, resetButtonX, resetButtonY,
                resetButtonX + resetButtonWidth, resetButtonY + BUTTON_HEIGHT, resetBgColor);

        // Match tab border style
        int resetBorderColor = 0xFF666666;
        GuiComponent.fill(poseStack, resetButtonX, resetButtonY, resetButtonX + resetButtonWidth, resetButtonY + 1,
                resetBorderColor); // Top
        GuiComponent.fill(poseStack, resetButtonX, resetButtonY + BUTTON_HEIGHT - 1, resetButtonX + resetButtonWidth,
                resetButtonY + BUTTON_HEIGHT, resetBorderColor); // Bottom
        GuiComponent.fill(poseStack, resetButtonX, resetButtonY, resetButtonX + 1, resetButtonY + BUTTON_HEIGHT,
                resetBorderColor); // Left
        GuiComponent.fill(poseStack, resetButtonX + resetButtonWidth - 1, resetButtonY, resetButtonX + resetButtonWidth,
                resetButtonY + BUTTON_HEIGHT, resetBorderColor); // Right

        int iconColor = isHoveringResetButton ? 0xFFFFAA00 : 0xFFCCCCCC;
        String resetIcon = "⟲"; // \u27F2
        int iconWidth = mc.font.width(resetIcon);
        int iconX = resetButtonX + (resetButtonWidth - iconWidth) / 2 + 1; // +1 to center visually
        int iconY = resetButtonY + (BUTTON_HEIGHT - 8) / 2;
        mc.font.draw(poseStack, resetIcon, iconX, iconY, iconColor);

        RenderSystem.enableScissor(scissorX, scissorYAdjusted, scissorWidth, scissorHeightScaled);

        int renderHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT - ITEM_AREA_TOP_SPACING;

        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double totalContentHeight = totalRows * (itemSize + itemSpacing);
        double maxScroll = Math.max(0, totalContentHeight - renderHeight);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        double rowHeight = itemSize + itemSpacing;
        int startRow = (int) (scrollOffset / rowHeight);
        startRow = Math.max(0, startRow - 2);
        int endRow = Math.min(startRow + visibleRows + 4, totalRows);

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

        String selectedCosmeticId = state.getSelectedCosmeticId();
        if (selectedCosmeticId != null && !selectedCosmeticId.equals(lastSelectedCosmeticId)) {
            itemStartTime = System.nanoTime() / 1000000L;
            lastSelectedCosmeticId = selectedCosmeticId;
        }

        if (selectedCosmeticId != null) {
            long currentTime = System.nanoTime() / 1000000L;
            float elapsedSeconds = (currentTime - itemStartTime) / 1000.0f;
            rotation = (elapsedSeconds * 90.0f) % 360.0f;
            bobOffset = (float) Math.sin(elapsedSeconds * 2.0f) * 0.05f;
        }

        for (int row = startRow; row < endRow; row++) {
            double rowYDouble = renderStartY + (row * rowHeight) - scrollOffset;
            int rowY = (int) rowYDouble;

            if (rowY + itemSize < renderStartY - itemSize || rowY > renderStartY + renderHeight + itemSize) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredCosmeticIds.size()) {
                    break;
                }

                String cosmeticId = filteredCosmeticIds.get(index);
                int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

                boolean isHovered = relativeMouseX >= (itemX - startX) && relativeMouseX < (itemX - startX + itemSize)
                        && relativeMouseY >= (rowY - startY) && relativeMouseY < (rowY - startY + itemSize);

                if (isHovered && colorPicker != null) {
                    int headerHeight = 20;
                    if (mouseX >= colorPicker.x && mouseX < colorPicker.x + colorPicker.getWidth()
                            && mouseY >= colorPicker.y - headerHeight
                            && mouseY < colorPicker.y + colorPicker.getHeight()) {
                        isHovered = false;
                    }
                }

                boolean isUnlocked = state.isUnlocked(cosmeticId);
                boolean isSelected = cosmeticId.equals(selectedCosmeticId);
                boolean isEquipped = state.isEquipped(cosmeticId);

                int bgColor;
                if (isEquipped) {
                    bgColor = 0x8000FF00;
                } else if (isSelected) {
                    bgColor = 0x80FFFFFF;
                } else if (isHovered) {
                    bgColor = isUnlocked ? 0x40CCCCCC : 0x40CC0000;
                } else {
                    bgColor = isUnlocked ? 0x33CCCCCC : 0x33CC0000;
                }
                GuiComponent.fill(poseStack, itemX, rowY, itemX + itemSize, rowY + itemSize, bgColor);

                if (isEquipped) {
                    int borderColor = 0xFF00FF00;
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + itemSize + 1, rowY + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY + itemSize - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + 1, rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX + itemSize - 1, rowY - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                } else if (isSelected) {
                    int borderColor = 0xFFFFFFFF;
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + itemSize + 1, rowY + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY + itemSize - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + 1, rowY + itemSize + 1, borderColor);
                    GuiComponent.fill(poseStack, itemX + itemSize - 1, rowY - 1, itemX + itemSize + 1,
                            rowY + itemSize + 1, borderColor);
                }

                boolean isParticleTrail = cosmeticId != null
                        && cosmeticId.toLowerCase().contains("particle")
                        && (cosmeticId.toLowerCase().contains("trail")
                                || cosmeticId.toLowerCase().contains("star")
                                || cosmeticId.toLowerCase().contains("sparkle")
                                || cosmeticId.toLowerCase().contains("effect"));

                com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata headMetadata = com.kingodogo.buildscape.cosmetics.CosmeticManager
                        .getInstance().getMetadata(cosmeticId);
                boolean isHeadCosmetic = headMetadata != null
                        && headMetadata.type() == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD;

                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);

                if (isHeadCosmetic && (stack == null || stack.isEmpty())) {
                    stack = new ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET);
                }

                if (stack == null || stack.isEmpty()) {
                    stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                }

                if (stack != null && !stack.isEmpty() || isParticleTrail || isHeadCosmetic) {
                    boolean isAnimated = isAnimatedEntity(cosmeticId);

                    if (isAnimated) {
                        if (!itemAnimationTimes.containsKey(cosmeticId)) {
                            long baseTime = System.nanoTime() / 1000000L;
                            int staggerOffset = index * 100;
                            itemAnimationTimes.put(cosmeticId, baseTime - staggerOffset);
                        }
                    }

                    try {
                        if (isSelected && !isParticleTrail) {
                            render3DItemPreview(poseStack, stack, itemX + itemSize / 2, rowY + itemSize / 2,
                                    partialTick);
                        } else if (isAnimated || isParticleTrail) {
                            renderAnimatedEntity(poseStack, stack, cosmeticId, itemX + itemSize / 2,
                                    rowY + itemSize / 2, partialTick, false);
                        } else {
                            render3DItemLikePillar(poseStack, stack, cosmeticId, itemX + itemSize / 2,
                                    rowY + itemSize / 2, partialTick);
                        }
                    } catch (Exception e) {
                        BuildScape.getLogger().warn("Failed to render cosmetic item (general): " + cosmeticId, e);
                        mc.font.draw(poseStack, "?", itemX + itemSize / 2 - 3, rowY + itemSize / 2 - 3, 0xFF0000);
                    }
                } else if (!isParticleTrail) {
                    mc.font.draw(poseStack, "?", itemX + itemSize / 2 - 3, rowY + itemSize / 2 - 3, 0xFF0000);
                } else {
                    String sparkle = "✨";
                    int sparkleWidth = mc.font.width(sparkle);
                    mc.font.draw(poseStack, sparkle,
                            itemX + itemSize / 2 - sparkleWidth / 2,
                            rowY + itemSize / 2 - 4,
                            0xFFFF00);
                }

                if (!isUnlocked) {
                    GuiComponent.fill(poseStack, itemX + itemSize - 8, rowY, itemX + itemSize, rowY + 8, 0xFF000000);
                    mc.font.draw(poseStack, "🔒", itemX + itemSize - 7, rowY + 1, 0xFFFFFF);
                }
            }
        }

        for (int row = startRow; row < endRow; row++) {
            double rowYDouble = renderStartY + (row * rowHeight) - scrollOffset;
            int rowY = (int) rowYDouble;

            if (rowY + itemSize < renderStartY - itemSize || rowY > renderStartY + renderHeight + itemSize) {
                continue;
            }

            for (int col = 0; col < itemsPerRow; col++) {
                int index = row * itemsPerRow + col;
                if (index >= filteredCosmeticIds.size()) {
                    break;
                }

                String cosmeticId = filteredCosmeticIds.get(index);
                int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

                CosmeticManager cosmeticManager = CosmeticManager.getInstance();
                if (cosmeticManager.supportsColor(cosmeticId)) {
                    UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
                    CosmeticsConfig config = CosmeticsConfig.get();
                    String hexColor = playerUuid != null ? config.getCosmeticColor(playerUuid, cosmeticId) : null;

                    int color = 0xFFFFFF;
                    if (hexColor != null && !hexColor.isEmpty()) {
                        try {
                            String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
                            color = Integer.parseInt(hex, 16);
                        } catch (NumberFormatException e) {
                        }
                    }

                    poseStack.pushPose();
                    poseStack.translate(0, 0, 100);

                    int colorBoxX = itemX + itemSize - COLOR_BOX_SIZE - 2;
                    int colorBoxY = rowY + itemSize - COLOR_BOX_SIZE - 2;

                    GuiComponent.fill(poseStack, colorBoxX - 1, colorBoxY - 1,
                            colorBoxX + COLOR_BOX_SIZE + 1, colorBoxY + COLOR_BOX_SIZE + 1, 0xFF000000);
                    GuiComponent.fill(poseStack, colorBoxX, colorBoxY,
                            colorBoxX + COLOR_BOX_SIZE, colorBoxY + COLOR_BOX_SIZE, 0xFF000000 | color);

                    poseStack.popPose();
                }
            }
        }

        if (maxScroll > 0) {
            int scrollbarX = endX
                    - com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer.getScrollbarWidth() - 5;
            int scrollbarY = renderStartY;
            int scrollbarHeight = renderHeight;

            double visibleRatio = visibleRows / (double) totalRows;
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, maxScroll, visibleRatio);
        }

        RenderSystem.disableScissor();

    }

    public void renderTooltips(PoseStack poseStack, double mouseX, double mouseY) {
        if (mouseX < startX || mouseX >= startX + width || mouseY < startY || mouseY >= startY + height) {
            return;
        }

        if (isHoveringResetButton) {
            java.util.List<net.minecraft.network.chat.Component> tooltip = new java.util.ArrayList<>();
            tooltip.add(new net.minecraft.network.chat.TextComponent("Reset Color Picker Position"));
            mc.screen.renderComponentTooltip(poseStack, tooltip, (int) mouseX, (int) mouseY);
            return;
        }

        if (colorPicker != null) {
            int headerHeight = 20;
            boolean overPicker = mouseX >= colorPicker.x && mouseX < colorPicker.x + colorPicker.getWidth()
                    && mouseY >= colorPicker.y - headerHeight && mouseY < colorPicker.y + colorPicker.getHeight();
            if (overPicker) {
                return;
            }
        }

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
                if (index >= filteredCosmeticIds.size()) {
                    break;
                }

                String cosmeticId = filteredCosmeticIds.get(index);
                int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

                if (mouseX >= itemX && mouseX < itemX + itemSize
                        && mouseY >= rowY && mouseY < rowY + itemSize) {
                    hoveredCosmeticId = cosmeticId;
                    break;
                }
            }
            if (hoveredCosmeticId != null) {
                break;
            }
        }

        // Removed setPreviewCosmeticId to separate hover states

        if (hoveredCosmeticId != null && !hoveredCosmeticId.isEmpty()) {
            String tooltipText = null;
            // Prioritize Metadata Name
            CosmeticManager cosmeticManager = CosmeticManager.getInstance();
            com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager
                    .getMetadata(hoveredCosmeticId);

            if (metadata != null && metadata.name() != null && !metadata.name().isEmpty()) {
                tooltipText = metadata.name();
            } else {
                CosmeticRegistry registry = CosmeticRegistry.getInstance();
                ItemStack stack = registry.resolveToItemStack(hoveredCosmeticId);
                if (stack != null && !stack.isEmpty()) {
                    net.minecraft.network.chat.Component hoverName = stack.getHoverName();
                    if (hoverName != null) {
                        tooltipText = hoverName.getString();
                    }
                }
            }

            if (tooltipText == null || tooltipText.isEmpty() || tooltipText.equals(hoveredCosmeticId) || "Nether Star".equals(tooltipText)) {
                String idPart = hoveredCosmeticId;
                if (hoveredCosmeticId.startsWith("buildscape:cosmatics/")) {
                    idPart = hoveredCosmeticId.substring(hoveredCosmeticId.lastIndexOf("/") + 1);
                }
                tooltipText = idPart.replace("_", " ");
                // Capitalize first letter of each word
                String[] words = tooltipText.split(" ");
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    if (word.length() > 0) {
                        sb.append(Character.toUpperCase(word.charAt(0)));
                        if (word.length() > 1) {
                            sb.append(word.substring(1));
                        }
                        sb.append(" ");
                    }
                }
                tooltipText = sb.toString().trim();
            }

            RenderSystem.disableScissor();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            poseStack.pushPose();
            poseStack.translate(0, 0, 500);

            int textWidth = mc.font.width(tooltipText);
            int textHeight = mc.font.lineHeight;
            int padding = 3;
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

            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight,
                    0xF0000000);
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + 1, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY + tooltipHeight - 1, tooltipX + tooltipWidth,
                    tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + 1, tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX + tooltipWidth - 1, tooltipY, tooltipX + tooltipWidth,
                    tooltipY + tooltipHeight, 0xFFCCCCCC);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            mc.font.draw(poseStack, tooltipText, tooltipX + padding, tooltipY + padding, 0xFFFFFF);

            poseStack.popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }

    public void renderColorPickerOverlay(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (colorPicker == null || selectedCosmeticForColor == null) {
            return;
        }

        RenderSystem.disableScissor();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        poseStack.pushPose();
        poseStack.translate(0, 0, 500);

        int headerHeight = 14;

        GuiComponent.fill(poseStack, colorPicker.x, colorPicker.y - headerHeight,
                colorPicker.x + colorPicker.getWidth(), colorPicker.y, 0xFF222222);

        GuiComponent.fill(poseStack, colorPicker.x, colorPicker.y,
                colorPicker.x + colorPicker.getWidth(), colorPicker.y + colorPicker.getHeight(), 0xFF151515);

        GuiComponent.fill(poseStack, colorPicker.x - 1, colorPicker.y - headerHeight - 1,
                colorPicker.x + colorPicker.getWidth() + 1, colorPicker.y - headerHeight, 0xFF000000);
        GuiComponent.fill(poseStack, colorPicker.x - 1, colorPicker.y - headerHeight,
                colorPicker.x, colorPicker.y + colorPicker.getHeight() + 1, 0xFF000000);
        GuiComponent.fill(poseStack, colorPicker.x + colorPicker.getWidth(), colorPicker.y - headerHeight,
                colorPicker.x + colorPicker.getWidth() + 1, colorPicker.y + colorPicker.getHeight() + 1, 0xFF000000);
        GuiComponent.fill(poseStack, colorPicker.x - 1, colorPicker.y + colorPicker.getHeight(),
                colorPicker.x + colorPicker.getWidth() + 1, colorPicker.y + colorPicker.getHeight() + 1, 0xFF000000);
        GuiComponent.fill(poseStack, colorPicker.x, colorPicker.y - 1,
                colorPicker.x + colorPicker.getWidth(), colorPicker.y, 0xFF000000);

        colorPicker.renderButton(poseStack, mouseX, mouseY, partialTick);

        String headerTitle = "Color Picker";
        float scale = colorPicker.getCurrentScale();

        int titleWidth = mc.font.width(headerTitle);
        int titleX = colorPicker.x + (colorPicker.getWidth() - (int) (titleWidth * scale)) / 2;
        int titleY = colorPicker.y - headerHeight + (headerHeight - 8) / 2 + 1;

        poseStack.pushPose();
        poseStack.translate(titleX, titleY, 0);
        poseStack.scale(scale, scale, 1.0f);
        mc.font.draw(poseStack, headerTitle, 0, 0, 0xFFE0E0E0);
        poseStack.popPose();

        int handleWidth = (int) (15 * scale);
        int lineThickness = (int) (2 * scale);
        int lineSpacing = (int) (4 * scale);

        int buttonSpacing = 2;
        int closeX = colorPicker.x + colorPicker.getWidth() - (int) (12 * scale) - buttonSpacing;
        int closeY = colorPicker.y - headerHeight + (headerHeight - 8) / 2 + 1;

        poseStack.pushPose();
        poseStack.translate(closeX, closeY, 0);
        poseStack.scale(scale, scale, 1.0f);
        mc.font.draw(poseStack, "x", 0, 0, 0xFFFF5555);
        poseStack.popPose();

        int resetX = closeX - (int) (12 * scale) - buttonSpacing;
        int resetY = closeY;

        poseStack.pushPose();
        poseStack.translate(resetX, resetY, 0);
        poseStack.scale(scale, scale, 1.0f);
        mc.font.draw(poseStack, "⟲", 0, 0, 0xFF55FF55);
        poseStack.popPose();

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private void renderAnimatedEntity(PoseStack poseStack, ItemStack stack, String cosmeticId, int centerX, int centerY,
            float partialTick, boolean isHovered) {
        Level level = mc.level;
        if (level == null) {
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        float animSpeedMultiplier = 1.0f;

        float animProgress = getAnimationProgress(cosmeticId, partialTick, animSpeedMultiplier);
        String idLower = cosmeticId.toLowerCase();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        poseStack.translate(centerX, centerY, 100.0f);

        if (idLower.contains("elytra") || idLower.contains("wing")) {
            float wingScaleBase = 0.8f;
            float wingScaleRange = 0.4f;
            float wingScale = wingScaleBase + animProgress * wingScaleRange;
            float baseScale = itemSize * 0.46875f * wingScale;
            poseStack.scale(baseScale, -baseScale, baseScale);

            float wingRotationMax = 30.0f;
            float wingRotation = (animProgress - 0.5f) * wingRotationMax;
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(wingRotation));
        } else if (idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail")) {
            poseStack.popPose();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();

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
                }
            }

            float scaleFactor = itemSize / 64.0f;

            int particleCount = 4;
            for (int i = 0; i < particleCount; i++) {
                float baseOffsetY = -14.0f * scaleFactor;
                float trailOffset = (animProgress * 24.0f * scaleFactor);
                float offsetY = baseOffsetY + trailOffset + (i * 6.0f * scaleFactor);
                float offsetX = (float) (Math.sin(animProgress * Math.PI * 2 + i) * 6.0f * scaleFactor);

                float particleX = centerX + offsetX;
                float particleY = centerY + offsetY;

                float trailProgress = (i + animProgress) / particleCount;
                float particleSize = 8.0f * scaleFactor * (1.0f - trailProgress * 0.5f);
                float alpha = 0.9f - (trailProgress * 0.6f);

                RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);

                poseStack.pushPose();
                poseStack.translate(particleX, particleY, 0);
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(animProgress * 360.0f + i * 45.0f));

                float halfSize = particleSize / 2.0f;

                CosmeticManager manager = CosmeticManager.getInstance();
                String shape = manager.getParticleShape(cosmeticId);

                net.minecraft.resources.ResourceLocation textureLoc = null;

                if (shape.equals("heart")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/heart_blank.png");
                } else if (shape.equals("bubble")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/bubble.png");
                } else if (shape.equals("note")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/note.png");
                } else if (shape.equals("cherry")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/cherry_0.png");
                } else if (shape.equals("cherry_leaves")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/cherry_0.png");
                } else if (shape.equals("firework")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation("minecraft",
                            "textures/particle/spark_0.png");
                } else if (shape.equals("cake")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/cake_1.png");
                } else if (shape.equals("snowflake")) {
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/snowflake_1.png");
                } else {
                    textureLoc = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID,
                            "textures/particle/glow_lime_sparkle.png");
                }

                if (textureLoc != null) {
                    RenderSystem.setShaderTexture(0, textureLoc);

                    // Apply custom color only to colorable particles (heart, sparkle, cake, cherry)
                    // Non-colorable particles (snowflake, firework, note, bubble, cherry_leaves)
                    // use white
                    boolean isColorable = manager.supportsColor(cosmeticId);
                    if (isColorable) {
                        RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);
                    } else {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
                    }

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

                    // Sparkle particle has 10 frames stacked vertically, so we only render the
                    // first frame
                    float minV = 0.0f;
                    float maxV = 1.0f;
                    if (shape.equals("sparkle")) {
                        // Render only the first frame (1/10th of the texture)
                        minV = 0.0f;
                        maxV = 0.1f; // 1/10th of the texture height
                    }

                    bufferbuilder.vertex(poseStack.last().pose(), x0, y1, 0).uv(0.0f, maxV).endVertex();
                    bufferbuilder.vertex(poseStack.last().pose(), x1, y1, 0).uv(1.0f, maxV).endVertex();
                    bufferbuilder.vertex(poseStack.last().pose(), x1, y0, 0).uv(1.0f, minV).endVertex();
                    bufferbuilder.vertex(poseStack.last().pose(), x0, y0, 0).uv(0.0f, minV).endVertex();
                    tesselator.end();
                }

                if (textureLoc == null) {
                    GuiComponent.fill(poseStack, (int) (-halfSize), (int) (-halfSize), (int) halfSize, (int) halfSize,
                            ((int) (alpha * 255) << 24) | 0xFFFFFF);
                }

                poseStack.popPose();
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableDepthTest();

            return;
        } else {
            float baseScale = itemSize * 0.46875f;
            poseStack.scale(baseScale, -baseScale, baseScale);
            float defaultRotation = animProgress * 360.0f * animSpeedMultiplier;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(defaultRotation));
        }

        BakedModel model = itemRenderer.getModel(stack, level, null, 0);

        boolean hasGlint = stack.hasFoil();

        int lightLevel = 15728880;
        int overlay = 0;

        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED,
                    hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch();
        } catch (Exception e) {
            BuildScape.getLogger().debug("3D animated entity rendering failed, using 2D fallback: " + e.getMessage());
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        poseStack.popPose();

        RenderSystem.disableBlend();
    }

    private void render3DItemLikePillar(PoseStack poseStack, ItemStack stack, String cosmeticId, int centerX,
            int centerY, float partialTick) {
        Level level = mc.level;
        if (level == null) {
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        poseStack.translate(centerX, centerY, 100.0f);

        long currentRenderTime = System.nanoTime() / 1000000L;

        Long startTime = itemRotationTimes.get(cosmeticId);
        if (startTime == null) {
            startTime = currentRenderTime;
            itemRotationTimes.put(cosmeticId, startTime);
        }

        float elapsedSeconds = (currentRenderTime - startTime + partialTick * 50.0f) / 1000.0f;

        float rotationSpeed = 90.0f;
        float rotation = (elapsedSeconds * rotationSpeed) % 360.0f;

        float bobAmount = (float) Math.sin(elapsedSeconds * 2.0f) * 0.05f;
        poseStack.translate(0, bobAmount, 0);

        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        float baseScale = itemSize * 0.390625f;
        poseStack.scale(baseScale, -baseScale, baseScale);

        BakedModel model = itemRenderer.getModel(stack, level, null, 0);

        boolean hasGlint = stack.hasFoil();

        int lightLevel = 15728880;
        int overlay = 0;

        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED,
                    hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch();
        } catch (Exception e) {
            BuildScape.getLogger().debug("3D pillar-style item rendering failed, using 2D fallback: " + e.getMessage());
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        poseStack.popPose();

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    private void render3DItemPreview(PoseStack poseStack, ItemStack stack, int centerX, int centerY,
            float partialTick) {
        Level level = mc.level;
        if (level == null) {
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        float bobY = bobOffset * 5.0f;
        poseStack.translate(centerX, centerY + bobY, 100.0f);

        float baseScale = itemSize * 0.625f * zoom;
        poseStack.scale(baseScale, -baseScale, baseScale);

        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        BakedModel model = itemRenderer.getModel(stack, level, null, 0);

        boolean hasGlint = stack.hasFoil();

        int lightLevel = 15728880;
        int overlay = 0;

        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED,
                    hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch();
        } catch (Exception e) {
            BuildScape.getLogger().debug("3D item rendering failed, using 2D fallback: " + e.getMessage());
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, centerX - 8, centerY - 8);
            return;
        }

        poseStack.popPose();

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (isHoveringResetButton && button == 0) {
            CosmeticsConfig config = CosmeticsConfig.get();
            config.clearColorPickerPosition();

            if (colorPicker != null && selectedCosmeticForColor != null) {
                int itemIndex = filteredCosmeticIds.indexOf(selectedCosmeticForColor);
                if (itemIndex >= 0) {
                    int row = itemIndex / itemsPerRow;
                    int col = itemIndex % itemsPerRow;
                    int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

                    int titleHeight = 15;
                    int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT
                            + ITEM_AREA_TOP_SPACING;
                    int rowY = renderStartY + row * (itemSize + itemSpacing) - (int) scrollOffset;

                    int pickerX = itemX + itemSize + itemSpacing;
                    int pickerY = rowY;

                    if (pickerX + colorPicker.getWidth() > startX + width) {
                        pickerX = itemX - colorPicker.getWidth() - itemSpacing;
                    }
                    if (pickerY + colorPicker.getHeight() > startY + height) {
                        pickerY = startY + height - colorPicker.getHeight() - 10;
                    }
                    if (pickerX < startX) {
                        pickerX = startX + itemSpacing;
                    }
                    if (pickerY < startY) {
                        pickerY = startY + 10;
                    }

                    colorPicker.x = pickerX;
                    colorPicker.y = pickerY;
                    saveColorPickerPosition(pickerX, pickerY);
                }
            }
            return true;
        }

        // Color Picker logic moved to mouseClicked override to support
        // floating/dragging outside panel interaction

        if (button != 0) {
            return false;
        }

        int buttonY = startY + PADDING + 15;
        int buttonX = startX + PADDING;
        int availableWidth = width - PADDING * 2;
        int resetButtonWidth = 20; // Must match render
        int spacing = BUTTON_SPACING;
        int numTabs = CosmeticType.values().length;
        int reservedForReset = resetButtonWidth + spacing;

        int buttonWidth = (availableWidth - reservedForReset - spacing * (numTabs - 1)) / numTabs;

        for (CosmeticType type : CosmeticType.values()) {
            if (mouseX >= buttonX && mouseX < buttonX + buttonWidth
                    && mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT) {
                currentFilter = type;
                updateCosmeticList();
                scrollOffset = 0;
                return true;
            }
            buttonX += buttonWidth + BUTTON_SPACING;
        }

        int titleHeight = 15;
        int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
        if (mouseY < renderStartY) {
            return false;
        }

        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double rowHeight = itemSize + itemSpacing;
        int startRow = (int) (scrollOffset / rowHeight);
        startRow = Math.max(0, startRow - 2);
        int endRow = Math.min(startRow + visibleRows + 4, totalRows);

        int renderHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT - ITEM_AREA_TOP_SPACING;

        int clickedCol = -1;
        int clickedRow = -1;

        for (int row = startRow; row < endRow; row++) {
            double rowYDouble = renderStartY + (row * rowHeight) - scrollOffset;
            int rowY = (int) rowYDouble;

            if (rowY + itemSize < renderStartY - itemSize || rowY > renderStartY + renderHeight + itemSize) {
                continue;
            }

            if (mouseY >= rowY && mouseY < rowY + itemSize) {
                clickedRow = row;
                for (int col = 0; col < itemsPerRow; col++) {
                    int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);
                    if (mouseX >= itemX && mouseX < itemX + itemSize) {
                        clickedCol = col;

                        int index = row * itemsPerRow + col;
                        if (index >= 0 && index < filteredCosmeticIds.size()) {
                            String cosmeticId = filteredCosmeticIds.get(index);
                            CosmeticManager cosmeticManager = CosmeticManager.getInstance();
                            if (cosmeticManager.supportsColor(cosmeticId)) {
                                int colorBoxX = itemX + itemSize - COLOR_BOX_SIZE - 2;
                                int colorBoxY = rowY + itemSize - COLOR_BOX_SIZE - 2;

                                if (mouseX >= colorBoxX && mouseX < colorBoxX + COLOR_BOX_SIZE
                                        && mouseY >= colorBoxY && mouseY < colorBoxY + COLOR_BOX_SIZE) {
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
            return false;
        }

        int row = clickedRow;
        int col = clickedCol;

        if (col < 0 || col >= itemsPerRow) {
            return false;
        }

        int index = row * itemsPerRow + col;
        if (index >= 0 && index < filteredCosmeticIds.size()) {
            String cosmeticId = filteredCosmeticIds.get(index);

            String playerUsername = mc.player != null ? mc.player.getName().getString() : null;
            boolean isDev = playerUsername != null && playerUsername.equalsIgnoreCase("Dev");
            boolean isUnlocked = state.isUnlocked(cosmeticId) || isDev;

            if (!isUnlocked) {
                return true;
            }

            if (state.isEquipped(cosmeticId)) {
                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                int slotIndex = getSlotForCosmetic(stack);

                Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();
                int actualSlot = -1;
                for (Map.Entry<Integer, String> entry : equippedBySlot.entrySet()) {
                    if (cosmeticId.equals(entry.getValue())) {
                        actualSlot = entry.getKey();
                        break;
                    }
                }

                if (actualSlot >= 0) {
                    state.unequipCosmeticFromSlot(actualSlot);
                } else if (slotIndex >= 0) {
                    String equippedInSlot = state.getEquippedCosmeticInSlot(slotIndex);
                    if (cosmeticId.equals(equippedInSlot)) {
                        state.unequipCosmeticFromSlot(slotIndex);
                    } else {
                        state.unequipCosmetic(cosmeticId);
                    }
                } else {
                    state.unequipCosmetic(cosmeticId);
                }

                if (cosmeticId.equals(state.getSelectedCosmeticId())) {
                    state.setSelectedCosmeticId(null);
                }
            } else {
                boolean isParticleTrail = cosmeticId != null
                        && cosmeticId.toLowerCase().contains("particle")
                        && (cosmeticId.toLowerCase().contains("trail")
                                || cosmeticId.toLowerCase().contains("star")
                                || cosmeticId.toLowerCase().contains("sparkle")
                                || cosmeticId.toLowerCase().contains("effect"));

                if (isParticleTrail) {
                    state.equipCosmetic(cosmeticId);
                } else {
                    ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                    int slotIndex = getSlotForCosmetic(stack);
                    if (slotIndex >= 0) {
                        state.equipCosmeticToSlot(slotIndex, cosmeticId);
                    } else {
                        state.equipCosmetic(cosmeticId);
                    }
                }

                state.setSelectedCosmeticId(cosmeticId);
            }

            return true;
        }

        return false;
    }

    private void openColorPicker(String cosmeticId) {
        UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
        if (playerUuid == null) {
            return;
        }

        CosmeticsConfig config = CosmeticsConfig.get();
        String currentColor = config.getCosmeticColor(playerUuid, cosmeticId);

        int color = 0xFFFFFF;
        if (currentColor != null && !currentColor.isEmpty()) {
            try {
                String hex = currentColor.startsWith("#") ? currentColor.substring(1) : currentColor;
                color = Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
            }
        }

        int pickerWidth = (itemSize * 2) + itemSpacing;
        int pickerHeight = itemSize;

        colorPicker = new CosmeticColorPickerWidget(0, 0, pickerWidth, pickerHeight, color, (hexColor) -> {
            if (selectedCosmeticForColor != null && playerUuid != null) {
                config.setCosmeticColor(playerUuid, selectedCosmeticForColor, hexColor);
            }
        });

        selectedCosmeticForColor = cosmeticId;

        int itemIndex = filteredCosmeticIds.indexOf(cosmeticId);
        if (itemIndex >= 0) {
            int row = itemIndex / itemsPerRow;
            int col = itemIndex % itemsPerRow;
            int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);

            int titleHeight = 15;
            int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
            int rowY = renderStartY + row * (itemSize + itemSpacing) - (int) scrollOffset;

            Integer savedX = config.getColorPickerX();
            Integer savedY = config.getColorPickerY();

            int pickerX, pickerY;

            if (savedX != null && savedY != null) {
                pickerX = savedX;
                pickerY = savedY;
            } else {
                pickerX = itemX + itemSize + itemSpacing;
                pickerY = rowY;

                if (pickerX + pickerWidth > startX + width) {
                    pickerX = itemX - pickerWidth - itemSpacing;
                }

                if (pickerX < startX) {
                    pickerX = startX + (width - pickerWidth) / 2;
                }

                pickerX = Math.max(startX + 5, Math.min(pickerX, startX + width - pickerWidth - 5));

                if (pickerY + pickerHeight > startY + height) {
                    pickerY = startY + height - pickerHeight - 10;
                }

                pickerY = Math.max(startY + 30, Math.min(pickerY, startY + height - pickerHeight - 5));
            }

            colorPicker.x = pickerX;
            colorPicker.y = pickerY;
        }
    }

    private boolean isDraggingColorPicker = false;
    private double pickerDragOffsetX = 0;
    private double pickerDragOffsetY = 0;

    private boolean isHoveringResetButton = false;

    private void saveColorPickerPosition(int x, int y) {
        CosmeticsConfig config = CosmeticsConfig.get();
        config.setColorPickerPosition(x, y);
    }

    private void closeColorPicker() {
        selectedCosmeticForColor = null;
    }

    private int getSlotForCosmetic(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }

        net.minecraft.world.item.Item item = stack.getItem();

        if (item instanceof ElytraItem) {
            return 1;
        }

        if (item instanceof net.minecraft.world.item.ArmorItem armor) {
            switch (armor.getSlot()) {
                case HEAD:
                    return 0;
                case CHEST:
                    return 1;
                case LEGS:
                    return 2;
                case FEET:
                    return 3;
                default:
                    return -1;
            }
        }

        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (colorPicker != null && selectedCosmeticForColor != null) {
            int headerHeight = 14;
            if (mouseX >= colorPicker.x && mouseX < colorPicker.x + colorPicker.getWidth()
                    && mouseY >= colorPicker.y - headerHeight && mouseY < colorPicker.y + colorPicker.getHeight()) {

                // Header interaction
                boolean insideHeader = mouseY < colorPicker.y;
                if (insideHeader) {
                    float scale = colorPicker.getCurrentScale();
                    int buttonSpacing = 2;
                    int closeX = colorPicker.x + colorPicker.getWidth() - (int) (12 * scale) - buttonSpacing;
                    int resetX = closeX - (int) (12 * scale) - buttonSpacing;

                    // Close button (approximate area)
                    if (mouseX >= closeX && mouseX <= closeX + (int) (12 * scale)) {
                        if (button == 0) {
                            closeColorPicker();
                            return true;
                        }
                    }

                    // Reset Color button (approximate area)
                    if (mouseX >= resetX && mouseX <= resetX + (int) (12 * scale)) {
                        if (button == 0) {
                            if (mc.player != null) {
                                CosmeticsConfig.get().setCosmeticColor(mc.player.getUUID(), selectedCosmeticForColor,
                                        null);
                                // Refresh listener if easy, but mostly just updating config is enough for next
                                // frame
                                // Re-open/refresh to update picker state to default
                                openColorPicker(selectedCosmeticForColor);
                            }
                            return true;
                        }
                    }

                    if (button == 0) {
                        isDraggingColorPicker = true;
                        pickerDragOffsetX = mouseX - colorPicker.x;
                        pickerDragOffsetY = mouseY - colorPicker.y;
                        return true;
                    }
                }

                colorPicker.mouseClicked(mouseX, mouseY, button);
                return true; // Consume click even if header action didn't fire
            }
        }

        // Scrollbar interaction
        int titleHeight = 15;
        int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
        int renderHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT - ITEM_AREA_TOP_SPACING;

        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double maxScroll = Math.max(0, (totalRows - visibleRows) * (itemSize + itemSpacing));

        if (maxScroll > 0) {
            int scrollbarX = endX
                    - com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer.getScrollbarWidth() - 5;
            int scrollbarY = renderStartY;
            int scrollbarHeight = renderHeight;

            double visibleRatio = visibleRows / (double) totalRows;

            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    startX, renderStartY, width, renderHeight,
                    scrollOffset, maxScroll, visibleRatio);

            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingColorPicker && colorPicker != null) {
            int newX = (int) (mouseX - pickerDragOffsetX);
            int newY = (int) (mouseY - pickerDragOffsetY);

            int headerHeight = 14;
            Minecraft mc = Minecraft.getInstance();
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();

            newX = Math.max(0, Math.min(newX, screenWidth - colorPicker.getWidth()));
            newY = Math.max(-headerHeight + 5, Math.min(newY, screenHeight - colorPicker.getHeight()));

            colorPicker.x = newX;
            colorPicker.y = newY;

            saveColorPickerPosition(newX, newY);

            return true;
        }

        if (colorPicker != null && selectedCosmeticForColor != null && !isDraggingColorPicker) {
            if (colorPicker.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }

            // Consume drag if over picker to prevent background scrolling
            if (mouseX >= colorPicker.x && mouseX < colorPicker.x + colorPicker.getWidth()
                    && mouseY >= colorPicker.y && mouseY < colorPicker.y + colorPicker.getHeight()) {
                return true;
            }
        }

        if (isInside(mouseX, mouseY)) {
            // Check scrollbar drag first
            int titleHeight = 15;
            int renderStartY = startY + PADDING + titleHeight + FILTER_BUTTON_AREA_HEIGHT + ITEM_AREA_TOP_SPACING;
            int renderHeight = height - PADDING * 2 - titleHeight - FILTER_BUTTON_AREA_HEIGHT - ITEM_AREA_TOP_SPACING;

            int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
            double maxScroll = Math.max(0, (totalRows - visibleRows) * (itemSize + itemSpacing));

            if (maxScroll > 0) {
                int scrollbarY = renderStartY;
                int scrollbarHeight = renderHeight;
                double visibleRatio = visibleRows / (double) totalRows;

                double newOffset = scrollbarRenderer.handleMouseDrag(mouseY, scrollbarY, scrollbarHeight,
                        maxScroll, visibleRatio, 1.0);

                if (newOffset >= 0) {
                    scrollOffset = newOffset;
                    return true;
                }
            }

            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingColorPicker = false;

        if (colorPicker != null && selectedCosmeticForColor != null) {
            return colorPicker.mouseReleased(mouseX, mouseY, button);
        }

        if (scrollbarRenderer.handleMouseRelease(button)) {
            return true;
        }

        return false;
    }

    public String getDraggedCosmeticId() {
        return null;
    }

    public void cancelDrag() {
    }

    @Override
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX < startX || mouseX >= endX || mouseY < startY || mouseY >= endY) {
            return false;
        }

        if (net.minecraft.client.gui.screens.Screen.hasControlDown()) {
            zoom += (float) delta * 0.1f;
            zoom = Math.max(0.5f, Math.min(2.0f, zoom));
            return true;
        }

        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        double maxScroll = Math.max(0, (totalRows - visibleRows) * (itemSize + itemSpacing));

        scrollOffset -= delta * 10;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (colorPicker != null && selectedCosmeticForColor != null) {
            if (colorPicker.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (colorPicker != null && selectedCosmeticForColor != null) {
            if (colorPicker.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }
}
