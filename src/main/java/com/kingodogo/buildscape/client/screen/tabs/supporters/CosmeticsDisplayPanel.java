package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.stream.Collectors;

public class CosmeticsDisplayPanel extends BasePanel {

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
    private final String lastSelectedCosmeticId = null;
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

        com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance().getMetadata(cosmeticId);
        if (metadata != null && metadata.type() == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD) {
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
        if (mc.level == null) return;

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
        int scissorHeightScaled = Math.max(0, scissorHeightActual);

        int relativeMouseX = mouseX - startX;
        int relativeMouseY = mouseY - startY;

        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);

        // Draw panel border
        int borderColor = 0xFF666666;
        GuiComponent.fill(poseStack, startX - 1, startY - 1, endX + 1, startY, borderColor); // Top
        GuiComponent.fill(poseStack, startX - 1, endY, endX + 1, endY + 1, borderColor); // Bottom
        GuiComponent.fill(poseStack, startX - 1, startY, startX, endY, borderColor); // Left
        GuiComponent.fill(poseStack, endX, startY, endX + 1, endY, borderColor); // Right

        String title = "Available Cosmetics";
        int titleWidth = mc.font.width(title);
        mc.font.draw(poseStack, title,
                startX + (width - titleWidth) / 2,
                startY + PADDING,
                0xFFFFFF);

        int numTabs = CosmeticType.values().length;
        int resetButtonWidth = 20;
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

            int buttonBorderColor = isSelected ? type.getColor() : 0xFF666666;
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonWidth, buttonY + 1, buttonBorderColor);
            GuiComponent.fill(poseStack, buttonX, buttonY + BUTTON_HEIGHT - 1, buttonX + buttonWidth,
                    buttonY + BUTTON_HEIGHT, buttonBorderColor);
            GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + 1, buttonY + BUTTON_HEIGHT, buttonBorderColor);
            GuiComponent.fill(poseStack, buttonX + buttonWidth - 1, buttonY, buttonX + buttonWidth,
                    buttonY + BUTTON_HEIGHT, buttonBorderColor);

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

        int resetBorderColor = 0xFF666666;
        GuiComponent.fill(poseStack, resetButtonX, resetButtonY, resetButtonX + resetButtonWidth, resetButtonY + 1,
                resetBorderColor); // Top
        GuiComponent.fill(poseStack, resetButtonX, resetButtonY + BUTTON_HEIGHT - 1, resetButtonX + resetButtonWidth,
                resetButtonY + BUTTON_HEIGHT, resetBorderColor); // Bottom
        GuiComponent.fill(poseStack, resetButtonX, resetButtonY, resetButtonX + 1, resetButtonY + BUTTON_HEIGHT,
                resetBorderColor); // Left
        GuiComponent.fill(poseStack, resetButtonX + resetButtonWidth - 1, resetButtonY, resetButtonX + resetButtonWidth,
                resetButtonY + BUTTON_HEIGHT, resetBorderColor); // Right

        String resetIcon = "⟲";
        mc.font.draw(poseStack, resetIcon, resetButtonX + (resetButtonWidth - mc.font.width(resetIcon)) / 2 + 1, resetButtonY + (BUTTON_HEIGHT - 8) / 2, isHoveringResetButton ? 0xFFFFAA00 : 0xFFCCCCCC);

        try {
            RenderSystem.enableScissor(scissorX, scissorYAdjusted, scissorWidth, scissorHeightScaled);

            int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
            double totalContentHeight = totalRows * (itemSize + itemSpacing);
            double maxScroll = Math.max(0, totalContentHeight - scissorHeight);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

            int startRow = (int) (scrollOffset / (itemSize + itemSpacing));
            int endRow = Math.min(startRow + visibleRows + 2, totalRows);

            if (filteredCosmeticIds.isEmpty()) {
                String noItems = "No cosmetics available";
                mc.font.draw(poseStack, noItems, startX + (width - mc.font.width(noItems)) / 2, renderStartY + scissorHeight / 2, 0xAAAAAA);
            } else {
                String selectedCosmeticId = state.getSelectedCosmeticId();
                long currentTime = System.nanoTime() / 1000000L;
                float elapsedSeconds = (currentTime - itemStartTime) / 1000.0f;
                rotation = (elapsedSeconds * 90.0f) % 360.0f;
                bobOffset = (float) Math.sin(elapsedSeconds * 2.0f) * 0.05f;

                for (int row = startRow; row < endRow; row++) {
                    int rowY = (int) (renderStartY + (row * (itemSize + itemSpacing)) - scrollOffset);
                    for (int col = 0; col < itemsPerRow; col++) {
                        int index = row * itemsPerRow + col;
                        if (index >= filteredCosmeticIds.size()) break;

                        String cosmeticId = filteredCosmeticIds.get(index);
                        int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);
                        boolean itemHovered = relativeMouseX >= (itemX - startX) && relativeMouseX < (itemX - startX + itemSize) && relativeMouseY >= (rowY - startY) && relativeMouseY < (rowY - startY + itemSize);

                        boolean isUnlocked = state.isUnlocked(cosmeticId);
                        boolean isSelected = cosmeticId.equals(selectedCosmeticId);
                        boolean isEquipped = state.isEquipped(cosmeticId);

                        int bgColor = isEquipped ? 0x8000FF00 : (isSelected ? 0x80FFFFFF : (itemHovered ? (isUnlocked ? 0x40CCCCCC : 0x40CC0000) : (isUnlocked ? 0x33CCCCCC : 0x33CC0000)));
                        GuiComponent.fill(poseStack, itemX, rowY, itemX + itemSize, rowY + itemSize, bgColor);

                        if (isEquipped || isSelected) {
                            int borderCol = isEquipped ? 0xFF00FF8A : 0xFFFFFFFF;
                            GuiComponent.fill(poseStack, itemX - 1, rowY - 1, itemX + itemSize + 1, rowY, borderCol);
                            GuiComponent.fill(poseStack, itemX - 1, rowY + itemSize, itemX + itemSize + 1, rowY + itemSize + 1, borderCol);
                            GuiComponent.fill(poseStack, itemX - 1, rowY, itemX, rowY + itemSize, borderCol);
                            GuiComponent.fill(poseStack, itemX + itemSize, rowY, itemX + itemSize + 1, rowY + itemSize, borderCol);
                        }

                        boolean isParticleTrail = cosmeticId.toLowerCase().contains("particle");
                        boolean isHeadCos = CosmeticManager.getInstance().getMetadata(cosmeticId) != null && CosmeticManager.getInstance().getMetadata(cosmeticId).type() == CosmeticManager.CosmeticType.HEAD;
                        ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);

                        if ((stack != null && !stack.isEmpty()) || isParticleTrail || isHeadCos) {
                            try {
                                if (isSelected && !isParticleTrail) {
                                    render3DItemPreview(poseStack, stack, itemX + itemSize / 2, rowY + itemSize / 2, partialTick);
                                } else if (isAnimatedEntity(cosmeticId) || isParticleTrail) {
                                    renderAnimatedEntity(poseStack, stack, cosmeticId, itemX + itemSize / 2, rowY + itemSize / 2, partialTick, itemHovered);
                                } else {
                                    render3DItemLikePillar(poseStack, stack, cosmeticId, itemX + itemSize / 2, rowY + itemSize / 2, partialTick);
                                }
                            } catch (Exception e) {
                                BuildScape.getLogger().debug("Failed to render item " + cosmeticId + ": " + e.getMessage());
                                mc.font.draw(poseStack, "?", itemX + itemSize / 2 - 2, rowY + itemSize / 2 - 4, 0xFFFFFF);
                            }
                        }

                        if (!isUnlocked) {
                            GuiComponent.fill(poseStack, itemX + itemSize - 8, rowY, itemX + itemSize, rowY + 8, 0xFF000000);
                            mc.font.draw(poseStack, "🔒", itemX + itemSize - 7, rowY + 1, 0xFFFFFF);
                        }
                    }
                }
            }

            renderScrollAndColorBoxes(poseStack, renderStartY, scissorHeight);
        } catch (Exception e) {
            BuildScape.getLogger().error("Supporters tab render exception: " + e.getMessage());
        } finally {
            RenderSystem.disableScissor();
        }
    }

    private void renderScrollAndColorBoxes(PoseStack poseStack, int renderStartY, int scissorHeight) {
        int totalRows = (int) Math.ceil((double) filteredCosmeticIds.size() / itemsPerRow);
        if (totalRows * (itemSize + itemSpacing) > scissorHeight) {
            int scrollbarX = endX - com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer.getScrollbarWidth() - 5;
            double visibleRatio = scissorHeight / (double) (totalRows * (itemSize + itemSpacing));
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, renderStartY, scissorHeight, scrollOffset, Math.max(0, totalRows * (itemSize + itemSpacing) - scissorHeight), visibleRatio);
        }

        for (int i = 0; i < filteredCosmeticIds.size(); i++) {
            String cosmeticId = filteredCosmeticIds.get(i);
            if (CosmeticManager.getInstance().supportsColor(cosmeticId)) {
                int row = i / itemsPerRow;
                int col = i % itemsPerRow;
                int rowY = (int) (renderStartY + (row * (itemSize + itemSpacing)) - scrollOffset);
                if (rowY + itemSize < renderStartY || rowY > renderStartY + scissorHeight) continue;
                
                int itemX = startX + itemSpacing + col * (itemSize + itemSpacing);
                UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
                String hex = playerUuid != null ? CosmeticsConfig.get().getCosmeticColor(playerUuid, cosmeticId) : null;
                int color = 0xFFFFFF;
                if (hex != null && !hex.isEmpty()) {
                    try {
                        color = Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
                    } catch (Exception e) {
                    }
                }

                int cx = itemX + itemSize - COLOR_BOX_SIZE - 2;
                int cy = rowY + itemSize - COLOR_BOX_SIZE - 2;
                GuiComponent.fill(poseStack, cx - 1, cy - 1, cx + COLOR_BOX_SIZE + 1, cy + COLOR_BOX_SIZE + 1, 0xFF000000);
                GuiComponent.fill(poseStack, cx, cy, cx + COLOR_BOX_SIZE, cy + COLOR_BOX_SIZE, 0xFF000000 | color);
            }
        }
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

        float animProgress = getAnimationProgress(cosmeticId, partialTick, 1.0f);
        String idLower = cosmeticId.toLowerCase();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f l1 = new Vector3f(0.2f, 1.0f, -0.7f);
        l1.normalize();
        Vector3f l2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        l2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(l1, l2);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 100.0f);

        if (idLower.contains("elytra") || idLower.contains("wing")) {
            float wingScale = 0.8f + animProgress * 0.4f;
            float baseScale = itemSize * 0.46875f * wingScale;
            poseStack.scale(baseScale, -baseScale, baseScale);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees((animProgress - 0.5f) * 30.0f));
        } else {
            CosmeticManager.CosmeticMetadata meta = CosmeticManager.getInstance().getMetadata(cosmeticId);
            if (meta != null && meta.type() == CosmeticManager.CosmeticType.HEAD) {
                com.kingodogo.buildscape.client.CosmeticRenderHandler.initBuildersHatModel();
                net.minecraft.client.model.geom.ModelPart modelPart = com.kingodogo.buildscape.client.CosmeticRenderHandler.buildersHatModelPart;
                if (modelPart != null) {
                    float baseScale = itemSize * 0.46875f * 1.8f;
                    poseStack.scale(baseScale, -baseScale, baseScale);
                    poseStack.translate(0.0, -0.1 + Math.sin(System.currentTimeMillis() / 400.0) * 0.05f, 0.0);
                    float rot = (System.currentTimeMillis() % 6000) / 6000.0f * 360.0f * (isHovered ? 2.0f : 1.0f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(rot));
                    net.minecraft.resources.ResourceLocation tex = new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID, "textures/cosmatics/builders_hat.png");
                    modelPart.render(poseStack, bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(tex)), 15728880, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
                    bufferSource.endBatch();
                    poseStack.popPose();
                    return;
                }
            }

            if (idLower.contains("particle") || idLower.contains("effect") || idLower.contains("trail")) {
                poseStack.popPose();
                render2DParticles(poseStack, cosmeticId, centerX, centerY, animProgress);
                return;
            } else {
                float baseScale = itemSize * 0.46875f;
                poseStack.scale(baseScale, -baseScale, baseScale);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(animProgress * 360.0f));
            }
        }

        if (stack != null && !stack.isEmpty()) {
            try {
                BakedModel model = itemRenderer.getModel(stack, mc.level, null, 0);
                itemRenderer.render(stack, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.GUI, stack.hasFoil(), poseStack, bufferSource, 15728880, 0, model);
                bufferSource.endBatch();
            } catch (Exception e) {
                BuildScape.getLogger().debug("3D anim fallback: " + e.getMessage());
                poseStack.popPose();
                itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
                return;
            }
        }

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    private void render2DParticles(PoseStack poseStack, String cosmeticId, int centerX, int centerY, float animProgress) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
        float[] color = com.kingodogo.buildscape.client.ParticleTrailHandler.getParticleColor(cosmeticId);
        String hex = (playerUuid != null) ? CosmeticsConfig.get().getCosmeticColor(playerUuid, cosmeticId) : null;
        if (hex != null && !hex.isEmpty()) {
            try {
                int rgb = Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
                color = new float[]{((rgb >> 16) & 0xFF) / 255.0f, ((rgb >> 8) & 0xFF) / 255.0f, (rgb & 0xFF) / 255.0f};
            } catch (Exception e) {
            }
        }

        float pScale = itemSize / 64.0f;
        for (int i = 0; i < 4; i++) {
            float trailProgress = (i + animProgress) / 4.0f;
            float offsetX = (float) (Math.sin(animProgress * Math.PI * 2 + i) * 6.0f * pScale);
            float offsetY = (-14.0f * pScale) + (animProgress * 24.0f * pScale) + (i * 6.0f * pScale);
            float pSize = 8.0f * pScale * (1.0f - trailProgress * 0.5f);

            String shape = CosmeticManager.getInstance().getParticleShape(cosmeticId);
            net.minecraft.resources.ResourceLocation tex = getParticleTexture(shape);
            
            // Check if this is the multi-frame sparkle texture
            boolean isMultiFrameSparkle = tex != null && tex.getPath().endsWith("glow_lime_sparkle.png");
            boolean isBubble = shape.equals("bubble");

            if (isBubble) {
                // For bubble trail, use white color as base (don't tint with cosmetic color)
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.9f - (trailProgress * 0.6f));
            } else {
                RenderSystem.setShaderColor(color[0], color[1], color[2], 0.9f - (trailProgress * 0.6f));
            }

            poseStack.pushPose();
            poseStack.translate(centerX + offsetX, centerY + offsetY, 0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(animProgress * 360.0f + i * 45.0f));

            if (tex != null) {
                RenderSystem.setShaderTexture(0, tex);
                RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
                com.mojang.blaze3d.vertex.BufferBuilder bb = com.mojang.blaze3d.vertex.Tesselator.getInstance().getBuilder();
                bb.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);

                float h = pSize / 2.0f;
                // Default to full texture (single frame)
                float u0 = 0, u1 = 1.0f, v0 = 0, v1 = 1.0f;
                
                if (isMultiFrameSparkle) {
                    // Texture is 46x460, so it's a vertical strip of 10 frames (46x46 each)
                    int totalFrames = 10;
                    
                    // Use index + time for randomized animation
                    int frameIndex = ((i * 3) + (int) (animProgress * 12)) % totalFrames;
                    
                    float frameHeight = 1.0f / (float) totalFrames;
                    
                    // U remains full width (0 to 1)
                    u0 = 0.0f;
                    u1 = 1.0f;
                    
                    // V is sliced vertically
                    v0 = frameIndex * frameHeight;
                    v1 = v0 + frameHeight;
                }

                bb.vertex(poseStack.last().pose(), -h, h, 0).uv(u0, v1).endVertex();
                bb.vertex(poseStack.last().pose(), h, h, 0).uv(u1, v1).endVertex();
                bb.vertex(poseStack.last().pose(), h, -h, 0).uv(u1, v0).endVertex();
                bb.vertex(poseStack.last().pose(), -h, -h, 0).uv(u0, v0).endVertex();
                com.mojang.blaze3d.vertex.Tesselator.getInstance().end();
            }
            poseStack.popPose();
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
    }

    private net.minecraft.resources.ResourceLocation getParticleTexture(String shape) {
        String path = switch (shape) {
            case "heart" -> "textures/particle/heart_blank.png";
            case "bubble" -> "textures/particle/bubble.png";
            case "note" -> "minecraft:textures/particle/note.png";
            case "cherry", "cherry_leaves" -> "textures/particle/cherry_0.png";
            case "firework" -> "minecraft:textures/particle/spark_0.png";
            case "cake" -> "textures/particle/cake_1.png";
            case "snowflake" -> "textures/particle/snowflake_1.png";
            default -> "textures/particle/glow_lime_sparkle.png";
        };
        if (path.startsWith("minecraft:"))
            return new net.minecraft.resources.ResourceLocation("minecraft", path.substring(10));
        return new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID, path);
    }

    private void render3DItemLikePillar(PoseStack poseStack, ItemStack stack, String cosmeticId, int centerX,
            int centerY, float partialTick) {
        if (mc.level == null || stack == null || stack.isEmpty()) {
            if (stack != null && !stack.isEmpty()) {
                itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            }
            return;
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f l1 = new Vector3f(0.2f, 1.0f, -0.7f);
        l1.normalize();
        Vector3f l2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        l2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(l1, l2);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 100.0f);

        long time = System.nanoTime() / 1000000L;
        Long start = itemRotationTimes.get(cosmeticId);
        if (start == null) {
            start = time;
            itemRotationTimes.put(cosmeticId, start);
        }
        float elapsed = (time - start + partialTick * 50.0f) / 1000.0f;

        poseStack.translate(0, Math.sin(elapsed * 2.0f) * 0.05f, 0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees((elapsed * 90.0f) % 360.0f));
        float scale = itemSize * 0.390625f;
        poseStack.scale(scale, -scale, scale);

        try {
            BakedModel model = itemRenderer.getModel(stack, mc.level, null, 0);
            itemRenderer.render(stack, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.GUI, stack.hasFoil(), poseStack, bufferSource, 15728880, 0, model);
            bufferSource.endBatch();
        } catch (Exception e) {
            BuildScape.getLogger().debug("Pillar fallback: " + e.getMessage());
            poseStack.popPose();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            return;
        }

        poseStack.popPose();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    private void render3DItemPreview(PoseStack poseStack, ItemStack stack, int centerX, int centerY,
            float partialTick) {
        if (mc.level == null || stack == null || stack.isEmpty()) {
            if (stack != null && !stack.isEmpty()) {
                itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
            }
            return;
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f l1 = new Vector3f(0.2f, 1.0f, -0.7f);
        l1.normalize();
        Vector3f l2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        l2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(l1, l2);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        poseStack.pushPose();
        float bobY = bobOffset * 5.0f;
        poseStack.translate(centerX, centerY + bobY, 100.0f);
        float scale = itemSize * 0.625f * zoom;
        poseStack.scale(scale, -scale, scale);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        try {
            BakedModel model = itemRenderer.getModel(stack, mc.level, null, 0);
            itemRenderer.render(stack, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.GUI, stack.hasFoil(), poseStack, bufferSource, 15728880, 0, model);
            bufferSource.endBatch();
        } catch (Exception e) {
            BuildScape.getLogger().debug("Preview fallback: " + e.getMessage());
            poseStack.popPose();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
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

        return scrollbarRenderer.handleMouseRelease(button);
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
            return colorPicker.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (colorPicker != null && selectedCosmeticForColor != null) {
            return colorPicker.charTyped(codePoint, modifiers);
        }
        return false;
    }
}
