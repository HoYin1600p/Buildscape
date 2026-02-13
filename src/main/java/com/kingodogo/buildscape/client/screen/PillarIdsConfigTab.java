package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.PillarMarkerManager;
import com.kingodogo.buildscape.client.screen.widget.ColorSwatchButton;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

public class PillarIdsConfigTab extends AbstractConfigTab {
    private static final int BASE_HEADER_HEIGHT = 28;
    private static final int BASE_ROW_HEIGHT = 32;
    private static final int BASE_TABLE_MARGIN = 12;
    private static final int BASE_COLUMN_GAP = 8;
    private static final int AUTO_REFRESH_MS = 1500;
    private static final int MAX_COLORS = 5;

    // Helper methods to get scaled values
    private int getHeaderHeight() {
        return BuildScapeConfigScreen.scaleSize(BASE_HEADER_HEIGHT);
    }

    private int getRowHeight() {
        return BuildScapeConfigScreen.scaleSize(BASE_ROW_HEIGHT);
    }

    private int getTableMargin() {
        return BuildScapeConfigScreen.scaleSize(BASE_TABLE_MARGIN);
    }

    private int getColumnGap() {
        return BuildScapeConfigScreen.scaleSize(BASE_COLUMN_GAP);
    }

    private final List<PillarRow> rows = new ArrayList<>();
    private Button reloadButton;
    private Button selectAllButton;
    private Button removeAllButton;
    private Button removeButton;
    private Button applyButton;
    private Button headerSelectAllCheckbox;
    private double scrollOffset = 0;
    private double maxScroll = 0;
    private long lastRefreshCheck = 0L;
    private String lastSignature = "";
    private boolean dirty = false;
    private Component statusMessage = TextComponent.EMPTY;
    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer scrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();

    public PillarIdsConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    @Override
    public void init() {
        rows.clear();
        scrollOffset = 0;
        maxScroll = 0;
        dirty = false;
        statusMessage = TextComponent.EMPTY;

        // Controls sit at the top of the content area; actual positions are set during
        // render
        int buttonWidth = BuildScapeConfigScreen.scaleSize(100);
        int buttonHeight = BuildScapeConfigScreen.getScaledButtonHeight();
        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton reloadBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.ids.reload"),
                (btn) -> manualReload());
        reloadBtn.setCustomTextColors(0xFFFF00, 0xFFFF55);
        reloadButton = reloadBtn;

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton removeAllBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.ids.remove_all"),
                (btn) -> confirmRemoveAll());
        removeAllBtn.setCustomTextColors(0xFF0000, 0xFF5555); // Red text for danger
        removeAllButton = removeAllBtn;

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton removeBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.ids.remove"),
                (btn) -> removeSelected());
        removeBtn.setCustomTextColors(0xFF5555, 0xFF7777); // Lighter red
        removeButton = removeBtn;

        addTabWidget(reloadButton);
        addTabWidget(removeAllButton);
        addTabWidget(removeButton);

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton applyBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.apply"),
                (btn) -> saveRows());
        applyBtn.setCustomTextColors(0x55FF55, 0xAAFFAA); // Green for apply
        applyButton = applyBtn;
        addTabWidget(applyButton);

        // Header Checkbox
        int checkboxSize = BuildScapeConfigScreen.scaleSize(14);
        headerSelectAllCheckbox = new Button(0, 0, checkboxSize, checkboxSize, TextComponent.EMPTY, (btn) -> {
            boolean allChecked = true;
            for (PillarRow row : rows) {
                if (row.visible && !row.isSelected()) {
                    allChecked = false;
                    break;
                }
            }
            boolean newState = !allChecked;
            for (PillarRow row : rows) {
                if (row.visible) {
                    row.setSelected(newState);
                }
            }
        }) {
            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float pt) {
                if (!visible) return;
                int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;
                boolean allChecked = true;
                boolean someChecked = false;
                for (PillarRow row : rows) {
                    if (row.visible) {
                        if (!row.isSelected()) allChecked = false;
                        else someChecked = true;
                    }
                }
                
                GuiComponent.fill(poseStack, x, y, x + width, y + height, 0x80000000); // 50% dark inner bg
                GuiComponent.fill(poseStack, x, y, x + width, y + 1, borderColor);
                GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
                GuiComponent.fill(poseStack, x, y, x + 1, y + height, borderColor);
                GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);
                
                int pad = Math.max(2, BuildScapeConfigScreen.scaleSize(2));
                if (allChecked && someChecked) {
                    GuiComponent.fill(poseStack, x + pad, y + pad, x + width - pad, y + height - pad, 0xFF55FF55);
                } else if (someChecked) {
                    GuiComponent.fill(poseStack, x + pad, y + (height / 2) - 1, x + width - pad, y + (height / 2) + 1, 0xFF55FF55);
                }
            }
        };
        addTabWidget(headerSelectAllCheckbox);

        setButtonsVisible(true);

        // IMPORTANT: Request fresh pillar data from server when opening the tab
        // This ensures we always have the latest data, especially on multiplayer servers
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.getCurrentServer() != null) {
            // Multiplayer - request data from server
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                    new com.kingodogo.buildscape.network.RequestPillarIdsPacket()
            );
        } else {
            // Single-player - load locally
            try {
                PillarIdManager.get().load();
            } catch (Exception ignored) {
            }
        }

        refreshFromManager();
    }

    private void manualReload() {
        dirty = false; // Drop unsaved markers when explicitly reloading
        refreshFromManager();
    }

    public void refreshFromManager() {
        PillarIdManager manager = PillarIdManager.get();
        try {
            // Check and reload if file changed
            manager.checkAndReload();
        } catch (Exception ignored) {
        }

        // IMPORTANT: Get snapshot from manager - this should have colors from file
        Map<String, PillarIdManager.PillarData> snapshot = manager.copyDataSnapshot();

        // Apply snapshot to GUI rows
        applySnapshot(snapshot);
        lastSignature = computeSignature(snapshot);

        if (!manager.hasLoaded()) {
            statusMessage = new TranslatableComponent("buildscape.config.ids.not_ready");
        } else if (snapshot.isEmpty()) {
            statusMessage = new TranslatableComponent("buildscape.config.ids.empty");
        } else if (!dirty) {
            statusMessage = TextComponent.EMPTY;
        }
    }

    private void maybeAutoRefresh() {
        if (dirty) {
            return; // Don't stomp on user edits
        }
        long now = System.currentTimeMillis();
        if (now - lastRefreshCheck < AUTO_REFRESH_MS) {
            return;
        }
        lastRefreshCheck = now;

        PillarIdManager manager = PillarIdManager.get();
        manager.checkAndReload();
        Map<String, PillarIdManager.PillarData> snapshot = manager.copyDataSnapshot();
        String signature = computeSignature(snapshot);
        if (!Objects.equals(signature, lastSignature)) {
            applySnapshot(snapshot);
            lastSignature = signature;
            statusMessage = snapshot.isEmpty()
                    ? new TranslatableComponent("buildscape.config.ids.empty")
                    : TextComponent.EMPTY;
        }
    }

    /**
     * Check if a pillar is within display range of the player.
     * Returns true if the pillar is in the same dimension and within 64 blocks.
     */

    private void applySnapshot(Map<String, PillarIdManager.PillarData> snapshot) {
        Set<String> seenIds = new HashSet<>();

        // Reuse rows when possible so caret position is preserved
        List<Map.Entry<String, PillarIdManager.PillarData>> entries = new ArrayList<>(snapshot.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        for (Map.Entry<String, PillarIdManager.PillarData> entry : entries) {
            // Show all pillars - no range filtering
            PillarRow row = findRow(entry.getKey());
            if (row == null) {
                row = new PillarRow(entry.getValue());
                rows.add(row);
            } else {
                row.apply(entry.getValue());
            }
            row.setVisible(true);
            seenIds.add(entry.getKey());
        }

        // Hide rows that disappeared
        for (PillarRow row : rows) {
            if (!seenIds.contains(row.id)) {
                row.setVisible(false);
            }
        }

        recomputeScroll();
    }

    private PillarRow findRow(String id) {
        for (PillarRow row : rows) {
            if (row.id != null && row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private void recomputeScroll() {
        int visibleRows = 0;
        for (PillarRow row : rows) {
            if (row.visible)
                visibleRows++;
        }
        // maxScroll is updated during render when we know the viewport height; leave
        // base value here
        if (visibleRows == 0) {
            scrollOffset = 0;
            maxScroll = 0;
        }
    }

    private String computeSignature(Map<String, PillarIdManager.PillarData> data) {
        StringJoiner joiner = new StringJoiner("|");
        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    PillarIdManager.PillarData d = entry.getValue();
                    String colors = d.dyeColors != null ? String.join(",", d.dyeColors) : "";
                    joiner.add(entry.getKey() + "#" + d.dimension + "#" + d.x + "#" + d.y + "#" + d.z + "#" + colors
                            + "#" + d.modifiedTime);
                });
        return joiner.toString();
    }

    private void addEmptyRow() {
        PillarIdManager manager = PillarIdManager.get();
        String newId = manager.generatePillarId();

        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = newId;
        data.dimension = "minecraft:overworld";
        data.x = 0;
        data.y = 64;
        data.z = 0;
        data.createdTime = System.currentTimeMillis();
        data.modifiedTime = data.createdTime;

        PillarRow row = new PillarRow(data);
        rows.add(row);
        row.setVisible(true);
        dirty = true;
        statusMessage = new TranslatableComponent("buildscape.config.ids.unsaved");
    }

    private void saveRows() {
        List<String> errors = new ArrayList<>();
        Map<String, PillarIdManager.PillarData> toSave = new LinkedHashMap<>();

        for (PillarRow row : rows) {
            if (!row.visible)
                continue;
            PillarIdManager.PillarData data = row.toData(errors);
            if (data == null) {
                continue;
            }

            if (toSave.containsKey(data.id)) {
                errors.add("Duplicate ID: " + data.id);
                continue;
            }

            toSave.put(data.id, data);
        }

        if (!errors.isEmpty()) {
            statusMessage = new TextComponent(errors.get(0));
            return;
        }

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.playSound(
                net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL,
                1.0f,
                1.0f
            );
        }

        PillarIdManager manager = PillarIdManager.get();
        manager.replaceAllPillarData(toSave);

        // Sync with server
        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                new com.kingodogo.buildscape.network.UpdateAllPillarIdsPacket(toSave)
        );

        dirty = false;
        lastSignature = computeSignature(toSave);
        statusMessage = new TranslatableComponent("buildscape.config.ids.status.saved");
    }

    private int[] computeColumns(int tableWidth) {
        int columnGap = getColumnGap();
        int checkboxWidth = BuildScapeConfigScreen.scaleSize(20); // Space for selection checkbox
        int usable = tableWidth - columnGap * 5 - checkboxWidth; // 5 gaps + checkbox column

        // Minimum widths for readability at all scales
        int minCheckboxWidth = checkboxWidth;
        int minIdWidth = BuildScapeConfigScreen.scaleSize(90);
        int minItemWidth = BuildScapeConfigScreen.scaleSize(64); // Fits base pillar box + displayed item box + gap
        int minColorsWidth = BuildScapeConfigScreen.scaleSize(100);
        int minDimensionWidth = BuildScapeConfigScreen.scaleSize(110);
        int minCoordsWidth = BuildScapeConfigScreen.scaleSize(160);

        // Desired proportions
        int idWidth = Math.max(minIdWidth, (int) (usable * 0.16f));
        int itemWidth = minItemWidth; // Fixed width for item column
        int colorsWidth = Math.max(minColorsWidth, (int) (usable * 0.24f));
        int dimensionWidth = Math.max(minDimensionWidth, (int) (usable * 0.20f));
        int coordsWidth = Math.max(minCoordsWidth, usable - idWidth - itemWidth - colorsWidth - dimensionWidth);

        // Scale down if total exceeds usable
        int totalUsed = idWidth + itemWidth + colorsWidth + dimensionWidth + coordsWidth;
        if (totalUsed > usable) {
            int excess = totalUsed - usable;
            float reducible = (idWidth - minIdWidth) + (colorsWidth - minColorsWidth) + (dimensionWidth - minDimensionWidth);
            if (reducible > 0) {
                float factor = Math.min(1.0f, (float) excess / reducible);
                idWidth -= (int) ((idWidth - minIdWidth) * factor);
                colorsWidth -= (int) ((colorsWidth - minColorsWidth) * factor);
                dimensionWidth -= (int) ((dimensionWidth - minDimensionWidth) * factor);
            }
            coordsWidth = usable - idWidth - itemWidth - colorsWidth - dimensionWidth;
        } else {
            coordsWidth = usable - idWidth - itemWidth - colorsWidth - dimensionWidth;
        }

        return new int[]{minCheckboxWidth, idWidth, itemWidth, colorsWidth, dimensionWidth, coordsWidth};
    }

    private void positionButtons(int contentX, int contentY, int contentWidth) {
        int buttonWidth = BuildScapeConfigScreen.scaleSize(100);
        int buttonHeight = BuildScapeConfigScreen.getScaledButtonHeight();
        int spacing = BuildScapeConfigScreen.scaleSize(8);
        int topMargin = BuildScapeConfigScreen.scaleSize(4);

        int y = contentY + topMargin;
        int x = contentX; // Start x for buttons

        // Reload button on the far left
        reloadButton.x = x;
        reloadButton.y = y;
        reloadButton.setWidth(buttonWidth);
        reloadButton.setHeight(buttonHeight);
        x += buttonWidth + spacing;

        // Remove Selected button next to Reload
        removeButton.x = x;
        removeButton.y = y;
        removeButton.setWidth(buttonWidth);
        removeButton.setHeight(buttonHeight);
        x += buttonWidth + spacing;

        // Apply/Save button next to Remove
        applyButton.x = x;
        applyButton.y = y;
        applyButton.setWidth(buttonWidth);
        applyButton.setHeight(buttonHeight);

        // Remove All button on the far right
        removeAllButton.x = contentX + contentWidth - buttonWidth;
        removeAllButton.y = y;
        removeAllButton.setWidth(buttonWidth);
        removeAllButton.setHeight(buttonHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        // Use full width spanning both left and right areas
        int contentWidth = parent.getRightPanelX() + parent.getRightPanelWidth() - parent.getContentX();
        int contentHeight = parent.getContentHeight();

        positionButtons(contentX, contentY, contentWidth);
        maybeAutoRefresh();

        // Background - low opacity gray (removed green background)
        // GuiComponent.fill(poseStack, contentX, contentY, contentX + contentWidth,
        // contentY + contentHeight, 0xC0256F16);

        int tableMargin = getTableMargin();
        int headerHeight = getHeaderHeight();
        int rowHeight = getRowHeight();
        int buttonAreaHeight = BuildScapeConfigScreen.getScaledButtonHeight() + BuildScapeConfigScreen.scaleSize(12);
        int statusAreaHeight = BuildScapeConfigScreen.scaleSize(20);

        int tableX = contentX + tableMargin;
        int tableY = contentY + buttonAreaHeight;
        int tableWidth = contentWidth - tableMargin * 2;
        int tableHeight = contentHeight - buttonAreaHeight - statusAreaHeight;

        int[] columns = computeColumns(tableWidth);
        // Add extra spacing between header and content for better visual separation
        int headerSpacing = BuildScapeConfigScreen.scaleSize(16);
        int rowsStartY = tableY + headerHeight + headerSpacing;

        int visibleRows = 0;
        for (PillarRow row : rows) {
            if (row.visible)
                visibleRows++;
        }
        // Calculate available height for rows (from rowsStartY to bottom of content)
        int availableRowHeight = tableHeight - headerHeight - headerSpacing;
        maxScroll = Math.max(0, visibleRows * rowHeight - availableRowHeight);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);

        // Header background - removed
        // GuiComponent.fill(poseStack, tableX, tableY, tableX + tableWidth, tableY +
        // HEADER_HEIGHT, 0xFF5E8C1A);
        // Table border removed
        // drawTableBorder(poseStack, tableX, tableY, tableWidth, tableHeight);
        drawHeader(poseStack, tableX, tableY, columns);

        // Calculate actual table width from columns for scrollbar positioning
        int actualTableWidth = 0;
        for (int col : columns) {
            actualTableWidth += col;
        }
        int colGap = getColumnGap();
        actualTableWidth += colGap * 5; // 5 gaps now (added item column)

        // Enable scissoring to prevent rows from rendering above the header or below
        // the visible area
        int scissorX = tableX;
        int scissorY = rowsStartY;
        int scissorWidth = actualTableWidth;
        // Calculate available height for rows (from rowsStartY to bottom of table)
        // Use the same calculation as maxScroll to ensure consistency
        int scissorHeight = availableRowHeight;

        // Convert to screen coordinates for scissor (Minecraft uses bottom-left origin)
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        int scaledScissorX = (int) (scissorX * guiScale);
        int scaledScissorY = (int) (windowHeight - (scissorY + scissorHeight) * guiScale);
        int scaledScissorWidth = (int) (scissorWidth * guiScale);
        int scaledScissorHeight = (int) (scissorHeight * guiScale);

        com.mojang.blaze3d.systems.RenderSystem.enableScissor(scaledScissorX, scaledScissorY, scaledScissorWidth,
                scaledScissorHeight);

        // Reset all row bounds before rendering to avoid stale hits in mouseClicked
        for (PillarRow row : rows) {
            row.resetBounds();
        }

        // Rows - render all visible rows within the scissor area
        int rowIndex = 0;
        int visibleAreaBottom = rowsStartY + scissorHeight; // Bottom of visible area
        for (PillarRow row : rows) {
            if (!row.visible)
                continue;
            int rowY = rowsStartY + rowIndex * rowHeight - (int) scrollOffset;

            // Only skip rows that are completely outside the visible area
            // Allow rows that are partially visible (even if bottom is cut off, we want to
            // see the top)
            if (rowY + rowHeight < rowsStartY || rowY > visibleAreaBottom) {
                rowIndex++;
                continue; // Skip completely off-screen rows
            }

            // Store row bounds for double-click detection
            row.setBounds(tableX, rowY, tableWidth, rowHeight);
            row.render(poseStack, tableX, rowY, columns);
            rowIndex++;
        }

        // Disable scissoring after rendering rows
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();

        // Draw scrollbar if needed - position at the right edge of actual table
        if (maxScroll > 0) {
            // Position at the end of the columns (actualTableWidth)
            int scrollbarX = tableX + actualTableWidth + BuildScapeConfigScreen.scaleSize(4);
            int scrollbarY = rowsStartY;
            int scrollbarHeight = availableRowHeight;

            double visibleRatio = (double) availableRowHeight / (visibleRows * rowHeight);
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, maxScroll, visibleRatio);
        }

        // Status text
        int statusY = contentY + contentHeight - BuildScapeConfigScreen.scaleSize(14);
        if (statusMessage != null && !statusMessage.getString().isEmpty()) {
            Minecraft.getInstance().font.draw(poseStack, statusMessage, tableX, statusY, 0xFFFFFF);
        } else if (dirty) {
            Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.unsaved"),
                    tableX, statusY, 0xFFFFFF);
        }
    }


    private void drawHeader(PoseStack poseStack, int tableX, int tableY, int[] columns) {
        int x = tableX;
        int headerColor = 0xFFCCCCCC; // Light silver text for table headers instead of pure white
        int headerHeight = getHeaderHeight();
        int columnGap = getColumnGap();
        Minecraft mc = Minecraft.getInstance();
        int fontHeight = mc.font.lineHeight;
        int textPadding = BuildScapeConfigScreen.scaleSize(8);

        // Calculate exact mathematical center for vertical text alignment using standard bounds
        int headerTextY = tableY + (headerHeight - fontHeight) / 2 + 1; // +1 to visually snap 'p' descending letters

        // Checkbox column (No bounding cell rendered, just position the interactive Master Checkbox)
        int checkboxSize = BuildScapeConfigScreen.scaleSize(14);
        headerSelectAllCheckbox.x = x + (columns[0] - checkboxSize) / 2;
        headerSelectAllCheckbox.y = tableY + (headerHeight - checkboxSize) / 2;
        headerSelectAllCheckbox.setWidth(checkboxSize);
        headerSelectAllCheckbox.setHeight(checkboxSize);
        headerSelectAllCheckbox.renderButton(poseStack, 0, 0, 0); // Active rendering
        x += columns[0] + columnGap;

        drawCell(poseStack, x, tableY, columns[1], headerHeight, true);
        Component idText = new TranslatableComponent("buildscape.config.ids.id");
        mc.font.draw(poseStack, idText, x + textPadding, headerTextY, headerColor);
        x += columns[1] + columnGap;

        // Item column (NEW)
        drawCell(poseStack, x, tableY, columns[2], headerHeight, true);
        Component itemText = new TranslatableComponent("buildscape.config.ids.item");
        int itemTextWidth = mc.font.width(itemText);
        // Center text horizontally in header cell
        mc.font.draw(poseStack, itemText, x + (columns[2] - itemTextWidth) / 2, headerTextY, headerColor);
        x += columns[2] + columnGap;

        drawCell(poseStack, x, tableY, columns[3], headerHeight, true);
        Component colorsText = new TranslatableComponent("buildscape.config.ids.colors");
        mc.font.draw(poseStack, colorsText, x + textPadding, headerTextY, headerColor);
        x += columns[3] + columnGap;

        drawCell(poseStack, x, tableY, columns[4], headerHeight, true);
        Component dimensionText = new TranslatableComponent("buildscape.config.ids.dimension");
        mc.font.draw(poseStack, dimensionText, x + textPadding, headerTextY, headerColor);
        x += columns[4] + columnGap;

        // Coordinates column - draw X, Y, Z separately aligned to their fields
        // (No drawCell bounding box, coordinates contain their own nested black border)

        // Calculate positions to match the coordinate fields below
        int coordPadding = BuildScapeConfigScreen.scaleSize(8);
        int coordGap = BuildScapeConfigScreen.scaleSize(8);
        int coordUsable = columns[5] - coordPadding * 2 - coordGap * 2;
        int minCoordWidth = BuildScapeConfigScreen.scaleSize(40);
        int coordWidth = Math.max(minCoordWidth, coordUsable / 3);

        // Adjust if needed
        int totalNeeded = coordPadding * 2 + coordWidth * 3 + coordGap * 2;
        if (totalNeeded > columns[5]) {
            coordGap = Math.max(BuildScapeConfigScreen.scaleSize(4),
                    (columns[5] - coordPadding * 2 - coordWidth * 3) / 2);
        }

        // Draw X, Y, Z labels centered in their respective columns
        int xLabelX = x + coordPadding + coordWidth / 2;
        int yLabelX = x + coordPadding + coordWidth + coordGap + coordWidth / 2;
        int zLabelX = x + coordPadding + (coordWidth + coordGap) * 2 + coordWidth / 2;

        // Center text horizontally - use SAME headerTextY as other headers
        int xLabelWidth = mc.font.width("X");
        int yLabelWidth = mc.font.width("Y");
        int zLabelWidth = mc.font.width("Z");

        mc.font.draw(poseStack, "X", xLabelX - xLabelWidth / 2, headerTextY, 0xFFFF0000); // Red
        mc.font.draw(poseStack, "Y", yLabelX - yLabelWidth / 2, headerTextY, 0xFF5555FF); // Light Blue/Purple
        mc.font.draw(poseStack, "Z", zLabelX - zLabelWidth / 2, headerTextY, 0xFF00FF00); // Green
    }

    private void drawCell(PoseStack poseStack, int x, int y, int width, int height, boolean header) {
        // Subtle 1px outline for sleek appeal
        int borderColor = header ? 0x80555555 : 0x60444444; // Slightly brighter border for header
        int bgColor = header ? 0x60222222 : 0x40111111;     // Slightly darker background for rows
        
        // Fill inner background
        GuiComponent.fill(poseStack, x, y, x + width, y + height, bgColor);
        
        // Render 1px outer borders cleanly
        GuiComponent.fill(poseStack, x, y, x + width, y + 1, borderColor); // Top
        GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, borderColor); // Bottom
        GuiComponent.fill(poseStack, x, y, x + 1, y + height, borderColor); // Left
        GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, borderColor); // Right
    }

    private void drawDashedBorder(PoseStack poseStack, int x, int y, int width, int height, int color) {
        int dash = 6;
        int gap = 4;

        // Top
        for (int dx = x; dx < x + width; dx += dash + gap) {
            int end = Math.min(dx + dash, x + width);
            GuiComponent.fill(poseStack, dx, y, end, y + 2, color);
        }
        // Bottom
        for (int dx = x; dx < x + width; dx += dash + gap) {
            int end = Math.min(dx + dash, x + width);
            GuiComponent.fill(poseStack, dx, y + height - 2, end, y + height, color);
        }
        // Left
        for (int dy = y; dy < y + height; dy += dash + gap) {
            int end = Math.min(dy + dash, y + height);
            GuiComponent.fill(poseStack, x, dy, x + 2, end, color);
        }
        // Right
        for (int dy = y; dy < y + height; dy += dash + gap) {
            int end = Math.min(dy + dash, y + height);
            GuiComponent.fill(poseStack, x + width - 2, dy, x + width, end, color);
        }
    }

    private void drawTableBorder(PoseStack poseStack, int x, int y, int width, int height) {
        // Table border - low opacity gray (removed green border)
        int borderColor = 0x33CCCCCC;
        GuiComponent.fill(poseStack, x - 2, y - 2, x + width + 2, y, borderColor);
        GuiComponent.fill(poseStack, x - 2, y + height, x + width + 2, y + height + 2, borderColor);
        GuiComponent.fill(poseStack, x - 2, y, x, y + height, borderColor);
        GuiComponent.fill(poseStack, x + width, y, x + width + 2, y + height, borderColor);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (maxScroll <= 0)
            return false;
        int scrollAmount = BuildScapeConfigScreen.scaleSize(12);
        scrollOffset = Mth.clamp(scrollOffset - delta * scrollAmount, 0, maxScroll);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        // Use full width spanning both left and right areas (Consistency with render!)
        int contentWidth = parent.getRightPanelX() + parent.getRightPanelWidth() - parent.getContentX();
        int contentHeight = parent.getContentHeight();

        // Handle functional row clicks (checkboxes, fields, double-clicks) FIRST
        boolean rowHandled = false;
        for (PillarRow row : rows) {
            if (!row.visible)
                continue;
            if (row.mouseClicked(mouseX, mouseY, button)) {
                rowHandled = true;
                break; // Stop after first row handles it
            }
        }
        if (rowHandled)
            return true;

        // Handle scrollbar click AFTER rows
        if (maxScroll > 0) {
            int tableMargin = getTableMargin();
            int headerHeight = getHeaderHeight();
            int rowHeight = getRowHeight();
            int buttonAreaHeight = BuildScapeConfigScreen.getScaledButtonHeight() + BuildScapeConfigScreen.scaleSize(8);
            int statusAreaHeight = BuildScapeConfigScreen.scaleSize(20);

            int tableX = contentX + tableMargin;
            int tableY = contentY + buttonAreaHeight;
            int tableWidth = contentWidth - tableMargin * 2;
            int tableHeight = contentHeight - buttonAreaHeight - statusAreaHeight;

            int[] columns = computeColumns(tableWidth);
            int headerSpacing = BuildScapeConfigScreen.scaleSize(16);
            int rowsStartY = tableY + headerHeight + headerSpacing;

            int availableRowHeight = tableHeight - headerHeight - headerSpacing;

            // Calculate actual table width for scrollbar interaction
            int actualTableWidth = 0;
            for (int col : columns) {
                actualTableWidth += col;
            }
            int colGap = getColumnGap();
            actualTableWidth += colGap * 4;

            int scrollbarX = tableX + actualTableWidth + BuildScapeConfigScreen.scaleSize(4);
            int scrollbarY = rowsStartY;
            int scrollbarHeight = availableRowHeight;

            int visibleRows = 0;
            for (PillarRow row : rows) {
                if (row.visible)
                    visibleRows++;
            }
            double visibleRatio = (double) availableRowHeight / (visibleRows * rowHeight);

            // Pass 0 for content area if we want to disable content-drag-to-scroll when
            // rows are present
            // This prevents the scrollbar from swallowing clicks meant for buttons above
            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    tableX, rowsStartY, actualTableWidth, availableRowHeight, // Limit drag area to table
                    scrollOffset, maxScroll, visibleRatio);

            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (maxScroll > 0) {
            int contentX = parent.getContentX();
            int contentY = parent.getContentY();
            int contentWidth = parent.getContentWidth();
            int contentHeight = parent.getContentHeight();

            int tableMargin = getTableMargin();
            int headerHeight = getHeaderHeight();
            int rowHeight = getRowHeight();
            int buttonAreaHeight = BuildScapeConfigScreen.getScaledButtonHeight() + BuildScapeConfigScreen.scaleSize(8);
            int statusAreaHeight = BuildScapeConfigScreen.scaleSize(20);

            int tableY = contentY + buttonAreaHeight;
            int tableHeight = contentHeight - buttonAreaHeight - statusAreaHeight;

            int headerSpacing = BuildScapeConfigScreen.scaleSize(16);
            int rowsStartY = tableY + headerHeight + headerSpacing;
            int availableRowHeight = tableHeight - headerHeight - headerSpacing;
            int scrollbarY = rowsStartY;
            int scrollbarHeight = availableRowHeight;

            int visibleRows = 0;
            for (PillarRow row : rows) {
                if (row.visible)
                    visibleRows++;
            }
            double visibleRatio = (double) availableRowHeight / (visibleRows * rowHeight);

            double newOffset = scrollbarRenderer.handleMouseDrag(mouseY, scrollbarY, scrollbarHeight,
                    maxScroll, visibleRatio, 1.0);

            if (newOffset >= 0) {
                scrollOffset = newOffset;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scrollbarRenderer.handleMouseRelease(button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (PillarRow row : rows) {
            if (!row.visible)
                continue;
            if (row.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (PillarRow row : rows) {
            if (!row.visible)
                continue;
            if (row.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClose() {
        if (dirty) {
            saveRows();
        }
        setButtonsVisible(false);
        for (PillarRow row : rows) {
            row.setVisible(false);
        }
        super.onClose(); // Remove tracked widgets
    }

    private void setButtonsVisible(boolean visible) {
        if (reloadButton != null) reloadButton.visible = visible;
        if (removeAllButton != null) removeAllButton.visible = visible;
        if (removeButton != null) removeButton.visible = visible;
        if (headerSelectAllCheckbox != null) headerSelectAllCheckbox.visible = visible;
    }

    private void selectAll() {
        for (PillarRow row : rows) {
            if (row.visible) {
                row.setSelected(true);
            }
        }
    }

    private void confirmRemoveAll() {
        Minecraft.getInstance().setScreen(new net.minecraft.client.gui.screens.ConfirmScreen(
                (confirmed) -> {
                    if (confirmed) {
                        removeAll();
                    }
                    Minecraft.getInstance().setScreen(parent);
                },
                new TranslatableComponent("buildscape.config.ids.remove_all"),
                new TextComponent("Are you sure you want to completely remove all Pillar IDs?")
        ));
    }

    private void removeAll() {
        List<String> idsToRemove = new ArrayList<>();
        for (PillarRow row : rows) {
            if (row.visible && row.id != null) {
                idsToRemove.add(row.id);
            }
        }
        if (!idsToRemove.isEmpty()) {
            PillarIdManager manager = PillarIdManager.get();
            for (String id : idsToRemove) {
                manager.removePillar(id);
            }

            // Sync removal to server
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                    new com.kingodogo.buildscape.network.RemovePillarPacket(idsToRemove)
            );

            refreshFromManager();
            dirty = true;
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.playSound(
                    net.minecraft.sounds.SoundEvents.NOTE_BLOCK_DIDGERIDOO,
                    1.0f,
                    1.0f
                );
            }
            statusMessage = new TranslatableComponent("buildscape.config.ids.removed_all");
        }
    }

    private void removeSelected() {
        List<String> idsToRemove = new ArrayList<>();
        for (PillarRow row : rows) {
            if (row.visible && row.isSelected() && row.id != null) {
                idsToRemove.add(row.id);
            }
        }
        if (!idsToRemove.isEmpty()) {
            PillarIdManager manager = PillarIdManager.get();
            for (String id : idsToRemove) {
                manager.removePillar(id);
            }

            // Sync removal to server
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                    new com.kingodogo.buildscape.network.RemovePillarPacket(idsToRemove)
            );

            refreshFromManager();
            dirty = true;
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.playSound(
                    net.minecraft.sounds.SoundEvents.NOTE_BLOCK_DIDGERIDOO,
                    1.0f,
                    1.0f
                );
            }
            statusMessage = new TranslatableComponent("buildscape.config.ids.removed_selected", idsToRemove.size());
        }
    }

    private void markDirty() {
        dirty = true;
        statusMessage = new TranslatableComponent("buildscape.config.ids.unsaved");
    }

    private class PillarRow {
        private final Button selectCheckbox;
        private final EditBox idField;
        private final List<ColorSwatchButton> colorSwatches;
        private final EditBox dimensionField;
        private final EditBox xField;
        private final EditBox yField;
        private final EditBox zField;
        private boolean visible = true;
        private boolean selected = false;
        private String id;
        private long createdTime;
        private long modifiedTime;
        private net.minecraft.world.item.ItemStack displayedItem = net.minecraft.world.item.ItemStack.EMPTY;
        private net.minecraft.world.item.ItemStack pillarTypeStack = net.minecraft.world.item.ItemStack.EMPTY;

        // Double-click detection
        private long lastClickTime = 0;
        private double lastClickX = -1;
        private double lastClickY = -1;
        private static final long DOUBLE_CLICK_TIME_MS = 300; // 300ms window for double-click
        private static final double DOUBLE_CLICK_DISTANCE = 5.0; // Max pixel distance for double-click

        // Row bounds for click detection
        private int rowX, rowY, rowWidth, rowHeight;

        private PillarRow(PillarIdManager.PillarData data) {
            Minecraft mc = Minecraft.getInstance();
            this.id = data.id;
            this.createdTime = data.createdTime;
            this.modifiedTime = data.modifiedTime;

            // Load items from data
            loadItemStacks(data);

            // Create selection checkbox - Clean 1px border design
            int checkboxSize = BuildScapeConfigScreen.scaleSize(14);
            selectCheckbox = new Button(0, 0, checkboxSize, checkboxSize, TextComponent.EMPTY, (btn) -> {
                selected = !selected;
            }) {
                @Override
                public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float pt) {
                    if (!visible) return;
                    int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;
                    GuiComponent.fill(poseStack, x, y, x + width, y + height, 0x80000000); // 50% dark inner bg
                    GuiComponent.fill(poseStack, x, y, x + width, y + 1, borderColor);
                    GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
                    GuiComponent.fill(poseStack, x, y, x + 1, y + height, borderColor);
                    GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);

                    if (selected) {
                        int pad = Math.max(2, BuildScapeConfigScreen.scaleSize(2));
                        GuiComponent.fill(poseStack, x + pad, y + pad, x + width - pad, y + height - pad, 0xFF55FF55);
                    }
                }
            };
            selectCheckbox.setAlpha(1.0f);

            int editBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
            idField = new EditBox(mc.font, 0, 0, BuildScapeConfigScreen.scaleSize(80), editBoxHeight,
                    new TranslatableComponent("buildscape.config.ids.id"));
            idField.setValue(data.id != null ? data.id : "");
            idField.setEditable(false);
            idField.setMaxLength(64);
            idField.setBordered(false); // Remove white border
            // Make ID field focusable so it can be clicked
            idField.setCanLoseFocus(false);

            // Create color swatches for each color in the pillar (display-only)
            colorSwatches = new ArrayList<>();
            List<String> colors = data.dyeColors != null ? data.dyeColors : Collections.emptyList();
            int swatchSize = BuildScapeConfigScreen.scaleSize(20);
            for (int i = 0; i < colors.size(); i++) {
                final String colorCode = colors.get(i);
                int color = 0xFFFFFF;
                try {
                    if (colorCode.startsWith("#") && colorCode.length() == 7) {
                        color = Integer.parseInt(colorCode.substring(1), 16);
                    }
                } catch (NumberFormatException e) {
                    // Use default white
                }

                ColorSwatchButton swatch = new ColorSwatchButton(
                        0, 0, swatchSize, swatchSize, color,
                        (btn) -> {
                        } // Display-only, no click action
                );
                swatch.visible = true;
                colorSwatches.add(swatch);
            }

            dimensionField = new EditBox(mc.font, 0, 0, BuildScapeConfigScreen.scaleSize(90), editBoxHeight,
                    new TranslatableComponent("buildscape.config.ids.dimension"));
            dimensionField.setValue(data.dimension != null ? data.dimension : "minecraft:overworld");
            dimensionField.setMaxLength(128);
            dimensionField.setEditable(false); // Dimension is not editable
            dimensionField.setBordered(false); // Remove white border

            int coordFieldWidth = BuildScapeConfigScreen.scaleSize(48);
            xField = new EditBox(mc.font, 0, 0, coordFieldWidth, editBoxHeight, new TextComponent("x"));
            yField = new EditBox(mc.font, 0, 0, coordFieldWidth, editBoxHeight, new TextComponent("y"));
            zField = new EditBox(mc.font, 0, 0, coordFieldWidth, editBoxHeight, new TextComponent("z"));
            xField.setBordered(false); // Remove white border
            yField.setBordered(false); // Remove white border
            zField.setBordered(false); // Remove white border
            // Make coordinate fields non-editable
            xField.setEditable(false);
            yField.setEditable(false);
            zField.setEditable(false);

            // Set values first
            xField.setValue(String.valueOf(data.x));
            yField.setValue(String.valueOf(data.y));
            zField.setValue(String.valueOf(data.z));

            // Color coordinates: X=Red, Y=Blue, Z=Green (set AFTER setting values to ensure
            // colors persist)
            xField.setTextColor(0xFFFF0000); // Red
            yField.setTextColor(0xFF5555FF); // Light Blue/Purple
            zField.setTextColor(0xFF00FF00); // Green
        }

        private void loadItemStacks(PillarIdManager.PillarData data) {
            this.displayedItem = net.minecraft.world.item.ItemStack.EMPTY;
            this.pillarTypeStack = net.minecraft.world.item.ItemStack.EMPTY;

            if (data.displayedItem != null && !data.displayedItem.isEmpty()) {
                try {
                    net.minecraft.resources.ResourceLocation itemId = new net.minecraft.resources.ResourceLocation(data.displayedItem);
                    net.minecraft.world.item.Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(itemId);
                    if (item != null) {
                        this.displayedItem = new net.minecraft.world.item.ItemStack(item);
                    }
                } catch (Exception ignored) {
                }
            }

            if (data.pillarType != null && !data.pillarType.isEmpty()) {
                try {
                    net.minecraft.resources.ResourceLocation itemId = new net.minecraft.resources.ResourceLocation(data.pillarType);
                    net.minecraft.world.item.Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(itemId);
                    if (item != null) {
                        this.pillarTypeStack = new net.minecraft.world.item.ItemStack(item);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        private void openPillarDetailTab(String pillarId) {
            if (parent != null) {
                parent.setActiveTab(new PillarIdDetailConfigTab(parent, pillarId));
            }
        }

        /**
         * Load colors directly from NBT/block entity for this pillar.
         * This ensures colors show in GUI even if manager file doesn't have them.
         * Works on both client and server side.
         */
        private List<String> loadColorsFromNBT(PillarIdManager.PillarData data) {
            try {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) {
                    return null;
                }

                // Check if we're in the right dimension
                String currentDimension = PillarIdManager.getDimensionKey(mc.level);
                if (!currentDimension.equals(data.dimension)) {
                    // Not in the right dimension - try server if available
                    net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks
                            .getCurrentServer();
                    if (server != null && server.isRunning()) {
                        for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                            if (level == null)
                                continue;

                            String dimensionKey = PillarIdManager.getDimensionKey(level);
                            if (!dimensionKey.equals(data.dimension)) {
                                continue;
                            }

                            net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(data.x, data.y, data.z);

                            if (!level.isLoaded(pos)) {
                                continue;
                            }

                            net.minecraft.world.level.chunk.ChunkAccess chunk = level.getChunk(pos);
                            if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                                continue;
                            }

                            if (!chunk.getStatus().isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                                continue;
                            }

                            net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
                            if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                                continue;
                            }

                            // Find the bottom of the stack to get the actual block entity with colors
                            net.minecraft.core.BlockPos bottomPos = pillarBE.findStackBottom();
                            net.minecraft.world.level.block.entity.BlockEntity bottomBE = level
                                    .getBlockEntity(bottomPos);

                            if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                                continue;
                            }

                            // Get colors directly from NBT
                            java.util.List<String> nbtColors = bottomPillarBE.getParticleColors();
                            if (nbtColors != null && !nbtColors.isEmpty()) {
                                return new ArrayList<>(nbtColors);
                            }
                        }
                    }
                    return null;
                }

                // We're in the right dimension - use client world
                net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(data.x, data.y, data.z);

                if (!mc.level.isLoaded(pos)) {
                    return null;
                }

                net.minecraft.world.level.block.entity.BlockEntity be = mc.level.getBlockEntity(pos);
                if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                    return null;
                }

                // Find the bottom of the stack to get the actual block entity with colors
                net.minecraft.core.BlockPos bottomPos = pillarBE.findStackBottom();
                net.minecraft.world.level.block.entity.BlockEntity bottomBE = mc.level.getBlockEntity(bottomPos);

                if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                    return null;
                }

                // Get colors directly from NBT
                java.util.List<String> nbtColors = bottomPillarBE.getParticleColors();
                if (nbtColors != null && !nbtColors.isEmpty()) {
                    return new ArrayList<>(nbtColors);
                }
            } catch (Exception e) {
                // Silently fail - will fall back to manager data
            }

            return null;
        }

        private void apply(PillarIdManager.PillarData data) {
            this.id = data.id;
            idField.setValue(data.id != null ? data.id : "");

            // Load displayed item from data
            if (data.displayedItem != null && !data.displayedItem.isEmpty()) {
                try {
                    net.minecraft.resources.ResourceLocation itemId = new net.minecraft.resources.ResourceLocation(data.displayedItem);
                    net.minecraft.world.item.Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(itemId);
                    if (item != null) {
                        this.displayedItem = new net.minecraft.world.item.ItemStack(item);
                    } else {
                        this.displayedItem = net.minecraft.world.item.ItemStack.EMPTY;
                    }
                } catch (Exception e) {
                    this.displayedItem = net.minecraft.world.item.ItemStack.EMPTY;
                }
            } else {
                this.displayedItem = net.minecraft.world.item.ItemStack.EMPTY;
            }

            // IMPORTANT: Use colors from manager file (pillar-ids.dat)
            // File is the source of truth for GUI display
            // Ensure we have a valid list (never null)
            List<String> colors;
            if (data.dyeColors != null && !data.dyeColors.isEmpty()) {
                colors = new ArrayList<>(data.dyeColors);
            } else {
                colors = Collections.emptyList();
            }

            // Clear all existing swatches first
            colorSwatches.clear();

            // Create swatches for all colors - this should create swatches if colors exist
            for (int i = 0; i < colors.size(); i++) {
                String colorCode = colors.get(i);
                int color = 0xFFFFFF;
                try {
                    if (colorCode != null && colorCode.startsWith("#") && colorCode.length() == 7) {
                        color = Integer.parseInt(colorCode.substring(1), 16);
                    }
                } catch (NumberFormatException e) {
                    // Use default white
                }

                ColorSwatchButton swatch = new ColorSwatchButton(
                        0, 0, BuildScapeConfigScreen.scaleSize(20), BuildScapeConfigScreen.scaleSize(20), color,
                        (btn) -> {
                        } // Display-only, no click action
                );
                swatch.visible = true;
                colorSwatches.add(swatch);
            }

            dimensionField.setValue(data.dimension != null ? data.dimension : "minecraft:overworld");
            // Set coordinate values first
            xField.setValue(String.valueOf(data.x));
            yField.setValue(String.valueOf(data.y));
            zField.setValue(String.valueOf(data.z));
            // Ensure coordinate colors are set AFTER setting values to ensure colors
            // persist
            xField.setTextColor(0xFFFF0000); // Red
            yField.setTextColor(0xFF5555FF); // Light Blue/Purple
            zField.setTextColor(0xFF00FF00); // Green
            this.createdTime = data.createdTime;
            this.modifiedTime = data.modifiedTime;

            // Update item stacks
            loadItemStacks(data);
        }

        private void setVisible(boolean visible) {
            this.visible = visible;
            if (selectCheckbox != null)
                selectCheckbox.visible = visible;
            idField.setVisible(visible);
            for (ColorSwatchButton swatch : colorSwatches) {
                swatch.visible = visible;
            }
            dimensionField.setVisible(visible);
            xField.setVisible(visible);
            yField.setVisible(visible);
            zField.setVisible(visible);
        }

        private void setSelected(boolean selected) {
            this.selected = selected;
            updateCheckboxText();
        }

        private boolean isSelected() {
            return selected;
        }

        private void updateCheckboxText() {
            // Deprecated logic. Custom rendering explicitly handles selection states now.
        }

        private PillarIdManager.PillarData toData(List<String> errors) {
            if (id == null || id.isEmpty()) {
                return null;
            }

            PillarIdManager.PillarData data = new PillarIdManager.PillarData();
            data.id = id;
            data.dimension = dimensionField.getValue().trim().isEmpty()
                    ? "minecraft:overworld"
                    : dimensionField.getValue().trim();

            // Get colors from swatches - we'll preserve existing colors since swatches are
            // read-only display
            // Colors are managed in the detail tab
            PillarIdManager manager = PillarIdManager.get();
            PillarIdManager.PillarData existing = manager.getPillarData(id);
            if (existing != null && existing.dyeColors != null) {
                data.dyeColors = new ArrayList<>(existing.dyeColors);
            } else {
                data.dyeColors = new ArrayList<>();
            }

            try {
                data.x = Integer.parseInt(xField.getValue().trim());
                data.y = Integer.parseInt(yField.getValue().trim());
                data.z = Integer.parseInt(zField.getValue().trim());
            } catch (NumberFormatException e) {
                errors.add("Invalid coords for " + id);
                return null;
            }

            data.createdTime = this.createdTime != 0L ? this.createdTime : System.currentTimeMillis();
            data.modifiedTime = System.currentTimeMillis();
            return data;
        }

        private void render(PoseStack poseStack, int startX, int rowY, int[] columns) {
            int rowHeight = PillarIdsConfigTab.this.getRowHeight();
            int columnGap = PillarIdsConfigTab.this.getColumnGap();
            int padding = BuildScapeConfigScreen.scaleSize(8);
            int editBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
            Minecraft mc = Minecraft.getInstance();
            int fontHeight = mc.font.lineHeight;

            // Calculate total row width (for layout purposes only, no border drawn)
            int totalRowWidth = columns[0] + columnGap + columns[1] + columnGap + columns[2] + columnGap + columns[3]
                    + columnGap + columns[4] + columnGap + columns[5];

            // Calculate EXACT center Y position for all elements - use ONE calculation for
            // EVERYTHING
            // This ensures perfect horizontal alignment across all columns
            int rowCenterY = rowY + rowHeight / 2;

            // SIMPLIFIED CENTER ALIGNMENT:
            // Calculate ONE center Y position for the entire row and align ALL elements to
            // it

            int checkboxSize = BuildScapeConfigScreen.scaleSize(18);
            int swatchSize = BuildScapeConfigScreen.scaleSize(20);

            // Use the row center as the reference point for ALL elements
            int alignmentCenterY = rowCenterY;

            // Position checkboxes: vertically centered
            int checkboxCenterY = alignmentCenterY - checkboxSize / 2;

            // Position color swatches: vertically centered
            int swatchCenterY = alignmentCenterY - swatchSize / 2;

            // Position EditBoxes: vertically centered
            int centerY = alignmentCenterY - editBoxHeight / 2;

            // Position text: Mathematically perfectly centered between the cell's top and bottom border
            int textY = rowY + (rowHeight - fontHeight) / 2 + 1; 

            int x = startX;

            // Checkbox column - perfectly centered vertically (No bounding cell rendered)
            selectCheckbox.x = x + (columns[0] - checkboxSize) / 2;
            selectCheckbox.y = checkboxCenterY; // Use calculated center for checkbox
            selectCheckbox.setWidth(checkboxSize);
            selectCheckbox.setHeight(checkboxSize);
            selectCheckbox.renderButton(poseStack, 0, 0, 0);
            x += columns[0] + columnGap;

            // ID column - perfectly centered
            drawCell(poseStack, x, rowY, columns[1], rowHeight, false);
            // Render Pillar ID EditBox background
            idField.x = x + padding;
            idField.y = centerY;
            idField.setWidth(columns[1] - padding * 2);
            // Clear text temporarily to draw it directly for alignment
            String idValue = idField.getValue();
            idField.setValue("");
            idField.render(poseStack, 0, 0, 0);
            idField.setValue(idValue);
            // Draw Pillar ID text directly using the textY calculated at the top
            int idTextX = x + padding;
            mc.font.draw(poseStack, idValue, idTextX, textY, 0xFFFFFFFF);
            x += columns[1] + columnGap;

            // Item column (NEW) - Full height box like other columns
            drawCell(poseStack, x, rowY, columns[2], rowHeight, false);

            int iconSize = 16;
            boolean hasType = pillarTypeStack != null && !pillarTypeStack.isEmpty();
            boolean hasItem = displayedItem != null && !displayedItem.isEmpty();

            if (hasType || hasItem) {
                int spacing = BuildScapeConfigScreen.scaleSize(4);
                int totalWidth = (hasType ? iconSize : 0) + (hasItem ? iconSize : 0) + (hasType && hasItem ? spacing : 0);
                int renderX = x + (columns[2] - totalWidth) / 2;
                int renderY = rowY + (rowHeight - iconSize) / 2;

                if (hasType) {
                    mc.getItemRenderer().renderGuiItem(pillarTypeStack, renderX, renderY);
                    renderX += iconSize + spacing;
                }

                if (hasItem) {
                    mc.getItemRenderer().renderGuiItem(displayedItem, renderX, renderY);
                    mc.getItemRenderer().renderGuiItemDecorations(mc.font, displayedItem, renderX, renderY);
                }
            }
            x += columns[2] + columnGap;

            // Colors column - perfectly centered
            drawCell(poseStack, x, rowY, columns[3], rowHeight, false);
            // Render color swatches - perfectly centered vertically
            int swatchSpacing = BuildScapeConfigScreen.scaleSize(4);
            int swatchStartX = x + padding;
            for (int i = 0; i < colorSwatches.size(); i++) {
                ColorSwatchButton swatch = colorSwatches.get(i);
                swatch.x = swatchStartX + i * (swatchSize + swatchSpacing);
                swatch.y = swatchCenterY; // Use calculated center for swatches
                swatch.setWidth(swatchSize);
                swatch.setHeight(swatchSize);
                swatch.renderButton(poseStack, 0, 0, 0);
            }
            x += columns[3] + columnGap;

            // Dimension column - perfectly centered
            drawCell(poseStack, x, rowY, columns[4], rowHeight, false);
            // Format dimension name for display (remove modid prefix like "minecraft:" and
            // show full name)
            String originalDimension = dimensionField.getValue();
            String displayDimension = formatDimensionName(originalDimension);

            // Render dimension EditBox background (no text)
            dimensionField.x = x + padding;
            dimensionField.y = centerY; // Use EXACT same centerY as all other EditBoxes
            int dimensionWidth = columns[4] - padding * 2;
            dimensionField.setWidth(Math.max(dimensionWidth, BuildScapeConfigScreen.scaleSize(100)));

            // Temporarily clear text to draw formatted version
            String tempValue = dimensionField.getValue();
            dimensionField.setValue("");
            dimensionField.render(poseStack, 0, 0, 0);
            dimensionField.setValue(tempValue);

            // Draw formatted dimension text directly using SAME textY as all other text
            int dimTextWidth = mc.font.width(displayDimension);
            int maxDimWidth = dimensionWidth - BuildScapeConfigScreen.scaleSize(4);
            String finalDimText = displayDimension;
            if (dimTextWidth > maxDimWidth) {
                // Truncate with ellipsis if too long
                finalDimText = mc.font.plainSubstrByWidth(displayDimension, maxDimWidth - mc.font.width("...")) + "...";
                dimTextWidth = mc.font.width(finalDimText);
            }
            int dimTextX = x + padding; // Left-aligned cleanly instead of centered
            mc.font.draw(poseStack, finalDimText, dimTextX, textY, 0xFFFFFFFF);
            x += columns[4] + columnGap;

            // Coordinates column - perfectly centered with border
            // (No drawCell bounding box, coordinates contain their own nested black border)

            // Improved coordinate alignment - ensure consistent spacing with proper scaling
            int coordPadding = BuildScapeConfigScreen.scaleSize(8);
            int coordGap = BuildScapeConfigScreen.scaleSize(8);
            int coordUsable = columns[5] - coordPadding * 2 - coordGap * 2;

            // Ensure minimum width per coordinate field
            int minCoordWidth = BuildScapeConfigScreen.scaleSize(40);
            int coordWidth = Math.max(minCoordWidth, coordUsable / 3);

            // If total width exceeds available space, reduce gap
            int totalNeeded = coordPadding * 2 + coordWidth * 3 + coordGap * 2;
            if (totalNeeded > columns[5]) {
                coordGap = Math.max(BuildScapeConfigScreen.scaleSize(4),
                        (columns[5] - coordPadding * 2 - coordWidth * 3) / 2);
            }

            // Calculate positions for coordinate fields
            int xFieldX = x + coordPadding;
            int yFieldX = x + coordPadding + coordWidth + coordGap;
            int zFieldX = x + coordPadding + (coordWidth + coordGap) * 2;

            // Draw border around all coordinate fields as one component - BLACK border
            int coordBorderColor = 0xFF000000; // Black border for coordinates
            int coordBorderThickness = Math.max(1, BuildScapeConfigScreen.scaleSize(1)); // Ensure at least 1 pixel
            int coordBorderPadding = 2; // Space between border and fields
            int coordGroupX = x + coordPadding - coordBorderPadding;
            int coordGroupY = centerY - coordBorderPadding;
            int coordGroupWidth = (coordWidth + coordGap) * 2 + coordWidth + coordBorderPadding * 2;
            int coordGroupHeight = editBoxHeight + coordBorderPadding * 2;

            // Draw outer border rectangle - ensure no overlap
            // Top border
            GuiComponent.fill(poseStack, coordGroupX, coordGroupY, coordGroupX + coordGroupWidth,
                    coordGroupY + coordBorderThickness, coordBorderColor);
            // Bottom border
            GuiComponent.fill(poseStack, coordGroupX, coordGroupY + coordGroupHeight - coordBorderThickness,
                    coordGroupX + coordGroupWidth, coordGroupY + coordGroupHeight, coordBorderColor);
            // Left border
            GuiComponent.fill(poseStack, coordGroupX, coordGroupY, coordGroupX + coordBorderThickness,
                    coordGroupY + coordGroupHeight, coordBorderColor);
            // Right border
            GuiComponent.fill(poseStack, coordGroupX + coordGroupWidth - coordBorderThickness, coordGroupY,
                    coordGroupX + coordGroupWidth, coordGroupY + coordGroupHeight, coordBorderColor);

            // Vertical dividers between fields - positioned to not overlap with outer
            // border
            // Divider 1: between X and Y fields
            int divider1X = x + coordPadding + coordWidth + coordGap / 2 - coordBorderThickness / 2;
            // Divider 2: between Y and Z fields
            int divider2X = x + coordPadding + coordWidth + coordGap + coordWidth + coordGap / 2
                    - coordBorderThickness / 2;
            // Draw dividers from inner border to inner border (not overlapping outer
            // border)
            int dividerTop = coordGroupY + coordBorderThickness;
            int dividerBottom = coordGroupY + coordGroupHeight - coordBorderThickness;
            GuiComponent.fill(poseStack, divider1X, dividerTop, divider1X + coordBorderThickness, dividerBottom,
                    coordBorderColor);
            GuiComponent.fill(poseStack, divider2X, dividerTop, divider2X + coordBorderThickness, dividerBottom,
                    coordBorderColor);

            // Ensure all coordinates are perfectly aligned - centered vertically
            xField.x = xFieldX;
            xField.y = centerY;
            xField.setWidth(coordWidth);

            yField.x = yFieldX;
            yField.y = centerY;
            yField.setWidth(coordWidth);

            zField.x = zFieldX;
            zField.y = centerY;
            zField.setWidth(coordWidth);

            // Draw coordinate text directly with colors using SAME textY as all other text
            // Center text horizontally within each coordinate field
            String xValue = xField.getValue();
            String yValue = yField.getValue();
            String zValue = zField.getValue();

            int xTextX = xFieldX + (coordWidth - mc.font.width(xValue)) / 2;
            int yTextX = yFieldX + (coordWidth - mc.font.width(yValue)) / 2;
            int zTextX = zFieldX + (coordWidth - mc.font.width(zValue)) / 2;

            // Draw colored coordinate text directly
            mc.font.draw(poseStack, xValue, xTextX, textY, 0xFFFF0000); // Red
            mc.font.draw(poseStack, yValue, yTextX, textY, 0xFF5555FF); // Light Blue/Purple
            mc.font.draw(poseStack, zValue, zTextX, textY, 0xFF00FF00); // Green
        }

        /**
         * Format dimension name for display by removing modid prefix and capitalizing
         * properly.
         * Examples:
         * - "minecraft:overworld" -> "Overworld"
         * - "minecraft:the_nether" -> "The Nether"
         * - "minecraft:the_end" -> "The End"
         * - "overworld" -> "Overworld"
         */
        private String formatDimensionName(String dimension) {
            if (dimension == null || dimension.isEmpty()) {
                return "Overworld"; // Default
            }

            String dimName = dimension;

            // Remove modid prefix (e.g., "minecraft:" or any other modid)
            if (dimName.contains(":")) {
                int colonIndex = dimName.lastIndexOf(":");
                if (colonIndex >= 0 && colonIndex < dimName.length() - 1) {
                    dimName = dimName.substring(colonIndex + 1);
                }
            }

            // Handle underscores and capitalize properly
            // Replace underscores with spaces
            dimName = dimName.replace("_", " ");

            // Capitalize first letter of each word
            if (dimName.length() > 0) {
                String[] words = dimName.split("\\s+");
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (words[i].length() > 0) {
                        if (i > 0)
                            formatted.append(" ");
                        formatted.append(words[i].substring(0, 1).toUpperCase());
                        if (words[i].length() > 1) {
                            formatted.append(words[i].substring(1).toLowerCase());
                        }
                    }
                }
                dimName = formatted.toString();
            }

            return dimName.isEmpty() ? "Overworld" : dimName;
        }

        private void setBounds(int x, int y, int width, int height) {
            this.rowX = x;
            this.rowY = y;
            this.rowWidth = width;
            this.rowHeight = height;
        }

        private void resetBounds() {
            this.rowX = -1000;
            this.rowY = -1000;
            this.rowWidth = 0;
            this.rowHeight = 0;
        }

        private boolean isMouseOverRow(double mouseX, double mouseY) {
            return mouseX >= rowX && mouseX < rowX + rowWidth &&
                    mouseY >= rowY && mouseY < rowY + rowHeight;
        }

        private boolean mouseClicked(double mouseX, double mouseY, int button) {
            // Check if clicking on checkbox
            if (selectCheckbox != null && selectCheckbox.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            // Check if clicking on the ID field area (single click opens detail tab)
            if (button == 0) {
                // Check if mouse is over ID field bounds
                if (mouseX >= idField.x && mouseX < idField.x + idField.getWidth() &&
                        mouseY >= idField.y && mouseY < idField.y + idField.getHeight()) {
                    // Single click on ID field opens detail tab
                    openPillarDetailTab(id);
                    return true;
                }
            }

            // Check if clicking anywhere on the row (not just input fields)
            boolean clickedOnRow = false;
            // We'll check if mouse is over any part of the row by checking if it's over any
            // field
            for (ColorSwatchButton swatch : colorSwatches) {
                clickedOnRow |= swatch.mouseClicked(mouseX, mouseY, button);
            }
            clickedOnRow |= dimensionField.mouseClicked(mouseX, mouseY, button);
            clickedOnRow |= xField.mouseClicked(mouseX, mouseY, button);
            clickedOnRow |= yField.mouseClicked(mouseX, mouseY, button);
            clickedOnRow |= zField.mouseClicked(mouseX, mouseY, button);

            // Also check if clicking anywhere on the row bounds (but not ID field)
            if (!clickedOnRow && isMouseOverRow(mouseX, mouseY)) {
                // Check if it's in the ID column area - use outer class method
                int[] columns = PillarIdsConfigTab.this.computeColumns(rowWidth);
                int columnGap = PillarIdsConfigTab.this.getColumnGap();
                int idColumnStartX = rowX + columns[0] + columnGap; // Skip checkbox column
                if (mouseX >= idColumnStartX && mouseX < idColumnStartX + columns[1]) {
                    // Clicked in ID column but not on ID field - still open detail tab
                    if (button == 0) {
                        openPillarDetailTab(id);
                        return true;
                    }
                }
                clickedOnRow = true;
            }

            // If we get here, they clicked the row but not a specific field that handles it
            if (button == 0 && clickedOnRow) {
                selected = !selected;
            }

            // Check for double-click on the row (for marking pillar)
            if (button == 0 && clickedOnRow) { // Left click
                long currentTime = System.currentTimeMillis();

                // Check if this is a double-click (same position within time window)
                if (lastClickTime > 0 &&
                        currentTime - lastClickTime < DOUBLE_CLICK_TIME_MS &&
                        lastClickX >= 0 && lastClickY >= 0 &&
                        Math.abs(mouseX - lastClickX) < DOUBLE_CLICK_DISTANCE &&
                        Math.abs(mouseY - lastClickY) < DOUBLE_CLICK_DISTANCE) {
                    // Double-click detected - mark pillar and close GUI
                    handleDoubleClick();
                    lastClickTime = 0; // Reset to prevent triple-click
                    lastClickX = -1;
                    lastClickY = -1;
                    return true;
                }

                lastClickTime = currentTime;
                lastClickX = mouseX;
                lastClickY = mouseY;
            }

            return clickedOnRow;
        }

        private void handleDoubleClick() {
            if (id == null || id.isEmpty()) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            Level level = mc.level;

            if (player == null || level == null) {
                return;
            }

            // Get pillar data
            PillarIdManager.PillarData data = toData(new ArrayList<>());
            if (data == null) {
                return;
            }

            BlockPos pillarPos = new BlockPos(data.x, data.y, data.z);
            String dimension = data.dimension;

            // Check if we're in the same dimension
            String currentDimension = level.dimension().location().toString();
            if (!dimension.equals(currentDimension)) {
                // Can't mark pillar in different dimension
                return;
            }

            // Mark the pillar (this will render a bounding box for the whole stack)
            PillarMarkerManager.get().markPillar(id, pillarPos, dimension);

            // Calculate direction to face the pillar
            double dx = pillarPos.getX() - player.getX();
            double dz = pillarPos.getZ() - player.getZ();
            float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;

            // Set player rotation to face the pillar
            player.setYRot(yaw);
            player.setXRot(0.0f);
            player.yRotO = yaw;
            player.xRotO = 0.0f;

            // Close the GUI
            if (parent != null && parent.getMinecraft().screen == parent) {
                parent.getMinecraft().setScreen(null);
            }
        }

        private boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return dimensionField.keyPressed(keyCode, scanCode, modifiers)
                    || xField.keyPressed(keyCode, scanCode, modifiers)
                    || yField.keyPressed(keyCode, scanCode, modifiers)
                    || zField.keyPressed(keyCode, scanCode, modifiers);
        }

        private boolean charTyped(char codePoint, int modifiers) {
            return dimensionField.charTyped(codePoint, modifiers)
                    || xField.charTyped(codePoint, modifiers)
                    || yField.charTyped(codePoint, modifiers)
                    || zField.charTyped(codePoint, modifiers);
        }
    }

    /**
     * Spawns a particle effect around the pillar to mark it visually.
     * Creates a glowing box effect for 2 seconds.
     */
    private void spawnPillarMarkerParticles(Level level, BlockPos pos) {
        if (level == null || !level.isClientSide) {
            return;
        }

        Random random = level.getRandom();
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        // Spawn particles in a box pattern around the block
        // This creates a visible bounding box effect

        // Bottom face corners
        for (int i = 0; i < 20; i++) {
            double x = centerX - 0.5 + random.nextDouble();
            double y = pos.getY() + 0.1;
            double z = centerZ - 0.5 + random.nextDouble();
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        // Top face corners
        for (int i = 0; i < 20; i++) {
            double x = centerX - 0.5 + random.nextDouble();
            double y = pos.getY() + 0.9;
            double z = centerZ - 0.5 + random.nextDouble();
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        // Vertical edges - front
        for (int i = 0; i < 15; i++) {
            double y = pos.getY() + 0.1 + (random.nextDouble() * 0.8);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, y, centerZ - 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, y, centerZ - 0.5, 0, 0, 0);
        }

        // Vertical edges - back
        for (int i = 0; i < 15; i++) {
            double y = pos.getY() + 0.1 + (random.nextDouble() * 0.8);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, y, centerZ + 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, y, centerZ + 0.5, 0, 0, 0);
        }

        // Horizontal edges - bottom
        for (int i = 0; i < 10; i++) {
            double x = centerX - 0.5 + (random.nextDouble());
            double z = centerZ - 0.5 + (random.nextDouble());
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.1, centerZ - 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.1, centerZ + 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, pos.getY() + 0.1, z, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, pos.getY() + 0.1, z, 0, 0, 0);
        }

        // Horizontal edges - top
        for (int i = 0; i < 10; i++) {
            double x = centerX - 0.5 + (random.nextDouble());
            double z = centerZ - 0.5 + (random.nextDouble());
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.9, centerZ - 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.9, centerZ + 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, pos.getY() + 0.9, z, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, pos.getY() + 0.9, z, 0, 0, 0);
        }

        // Add some enchant particles for extra glow
        for (int i = 0; i < 30; i++) {
            double x = centerX + (random.nextDouble() - 0.5) * 1.2;
            double y = pos.getY() + 0.1 + (random.nextDouble() * 0.8);
            double z = centerZ + (random.nextDouble() - 0.5) * 1.2;
            level.addParticle(ParticleTypes.ENCHANT, x, y, z,
                    (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.1);
        }
    }

    private void renderDimensionIcon(PoseStack poseStack, String dimension, int x, int y, int size) {
        int color = 0xFFFFFFFF; // Default white

        if (dimension != null) {
            if (dimension.contains("overworld") || dimension.contains("minecraft:overworld")) {
                color = 0xFF00FF00; // Green for overworld
            } else if (dimension.contains("nether") || dimension.contains("minecraft:the_nether")) {
                color = 0xFFFF0000; // Red for nether
            } else if (dimension.contains("end") || dimension.contains("minecraft:the_end")) {
                color = 0xFF8000FF; // Purple for end
            }
        }

        // Draw a simple colored square as the icon
        GuiComponent.fill(poseStack, x, y, x + size, y + size, color);
        // Draw a border
        GuiComponent.fill(poseStack, x, y, x + size, y + 1, 0xFF000000); // Top
        GuiComponent.fill(poseStack, x, y + size - 1, x + size, y + size, 0xFF000000); // Bottom
        GuiComponent.fill(poseStack, x, y, x + 1, y + size, 0xFF000000); // Left
        GuiComponent.fill(poseStack, x + size - 1, y, x + size, y + size, 0xFF000000); // Right
    }

    @Override
    public String getTabName() {
        return "PillarIds";
    }
}
