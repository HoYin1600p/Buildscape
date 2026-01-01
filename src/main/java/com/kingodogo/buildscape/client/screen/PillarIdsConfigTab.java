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
import java.util.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class PillarIdsConfigTab extends AbstractConfigTab {
    private static final int HEADER_HEIGHT = 28;
    private static final int ROW_HEIGHT = 32;
    private static final int TABLE_MARGIN = 12;
    private static final int COLUMN_GAP = 8;
    private static final int AUTO_REFRESH_MS = 1500;
    private static final int MAX_COLORS = 5;
    
    private final List<PillarRow> rows = new ArrayList<>();
    private Button reloadButton;
    private Button saveButton;
    private Button addRowButton;
    private double scrollOffset = 0;
    private double maxScroll = 0;
    private long lastRefreshCheck = 0L;
    private String lastSignature = "";
    private boolean dirty = false;
    private Component statusMessage = TextComponent.EMPTY;
    
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
        
        // Controls sit at the top of the content area; actual positions are set during render
        reloadButton = new Button(0, 0, 80, 20,
            new TranslatableComponent("buildscape.config.ids.reload"),
            (btn) -> manualReload());
        saveButton = new Button(0, 0, 80, 20,
            new TranslatableComponent("buildscape.config.ids.save"),
            (btn) -> saveRows());
        addRowButton = new Button(0, 0, 80, 20,
            new TranslatableComponent("buildscape.config.ids.add_row"),
            (btn) -> addEmptyRow());
        
        addTabWidget(reloadButton);
        addTabWidget(saveButton);
        addTabWidget(addRowButton);
        setButtonsVisible(true);
        
        // Kick off a load to ensure the manager has data for the current world
        try {
            PillarIdManager.get().load();
        } catch (Exception ignored) {}
        
        refreshFromManager();
    }
    
    private void manualReload() {
        dirty = false; // Drop unsaved markers when explicitly reloading
        refreshFromManager();
    }
    
    private void refreshFromManager() {
        PillarIdManager manager = PillarIdManager.get();
        try {
            manager.checkAndReload();
        } catch (Exception ignored) {}
        
        Map<String, PillarIdManager.PillarData> snapshot = manager.copyDataSnapshot();
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
    
    private void applySnapshot(Map<String, PillarIdManager.PillarData> snapshot) {
        Set<String> seenIds = new HashSet<>();
        
        // Reuse rows when possible so caret position is preserved
        List<Map.Entry<String, PillarIdManager.PillarData>> entries = new ArrayList<>(snapshot.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));
        
        for (Map.Entry<String, PillarIdManager.PillarData> entry : entries) {
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
            if (row.visible) visibleRows++;
        }
        // maxScroll is updated during render when we know the viewport height; leave base value here
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
                joiner.add(entry.getKey() + "#" + d.dimension + "#" + d.x + "#" + d.y + "#" + d.z + "#" + colors + "#" + d.modifiedTime);
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
            if (!row.visible) continue;
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
        
        PillarIdManager manager = PillarIdManager.get();
        manager.replaceAllPillarData(toSave);
        dirty = false;
        lastSignature = computeSignature(toSave);
        statusMessage = new TranslatableComponent("buildscape.config.ids.status.saved");
    }
    
    private int[] computeColumns(int tableWidth) {
        int usable = tableWidth - COLUMN_GAP * 3;
        int idWidth = (int)(usable * 0.22f);
        int colorsWidth = (int)(usable * 0.36f);
        int dimensionWidth = (int)(usable * 0.16f);
        int coordsWidth = usable - idWidth - colorsWidth - dimensionWidth;
        return new int[] { idWidth, colorsWidth, dimensionWidth, coordsWidth };
    }
    
    private void positionButtons(int contentX, int contentY, int contentWidth) {
        int buttonWidth = 90;
        int spacing = 8;
        int startX = contentX + contentWidth - (buttonWidth * 3 + spacing * 2);
        int y = contentY + 4;
        
        reloadButton.x = startX;
        reloadButton.y = y;
        reloadButton.setWidth(buttonWidth);
        
        addRowButton.x = startX + buttonWidth + spacing;
        addRowButton.y = y;
        addRowButton.setWidth(buttonWidth);
        
        saveButton.x = startX + (buttonWidth + spacing) * 2;
        saveButton.y = y;
        saveButton.setWidth(buttonWidth);
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();
        
        positionButtons(contentX, contentY, contentWidth);
        maybeAutoRefresh();
        
        // Background - low opacity gray (removed green background)
        // GuiComponent.fill(poseStack, contentX, contentY, contentX + contentWidth, contentY + contentHeight, 0xC0256F16);
        
        int tableX = contentX + TABLE_MARGIN;
        int tableY = contentY + 32;
        int tableWidth = contentWidth - TABLE_MARGIN * 2;
        int tableHeight = contentHeight - 56;
        
        int[] columns = computeColumns(tableWidth);
        int rowsStartY = tableY + HEADER_HEIGHT + 4;
        
        int visibleRows = 0;
        for (PillarRow row : rows) {
            if (row.visible) visibleRows++;
        }
        maxScroll = Math.max(0, visibleRows * ROW_HEIGHT - (tableHeight - HEADER_HEIGHT - 4));
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
        
        // Header background - removed
        // GuiComponent.fill(poseStack, tableX, tableY, tableX + tableWidth, tableY + HEADER_HEIGHT, 0xFF5E8C1A);
        // Table border removed
        // drawTableBorder(poseStack, tableX, tableY, tableWidth, tableHeight);
        drawHeader(poseStack, tableX, tableY, columns);
        
        // Rows
        int rowIndex = 0;
        for (PillarRow row : rows) {
            if (!row.visible) continue;
            int rowY = rowsStartY + rowIndex * ROW_HEIGHT - (int)scrollOffset;
            
            if (rowY + ROW_HEIGHT < tableY || rowY > tableY + tableHeight) {
                rowIndex++;
                continue; // Skip off-screen rows
            }
            
            // Store row bounds for double-click detection
            row.setBounds(tableX, rowY, tableWidth, ROW_HEIGHT);
            row.render(poseStack, tableX, rowY, columns);
            rowIndex++;
        }
        
        // Status text
        if (statusMessage != null && !statusMessage.getString().isEmpty()) {
            Minecraft.getInstance().font.draw(poseStack, statusMessage, tableX, contentY + contentHeight - 14, 0xFFFFFF);
        } else if (dirty) {
            Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.unsaved"), tableX, contentY + contentHeight - 14, 0xFFFFFF);
        }
    }
    
    private void drawHeader(PoseStack poseStack, int tableX, int tableY, int[] columns) {
        int x = tableX;
        int headerColor = 0xFFFFFFFF; // Keep text white, but remove white borders
        
        drawCell(poseStack, x, tableY, columns[0], HEADER_HEIGHT, true);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.id"), x + 6, tableY + 8, headerColor);
        x += columns[0] + COLUMN_GAP;
        
        drawCell(poseStack, x, tableY, columns[1], HEADER_HEIGHT, true);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.colors"), x + 6, tableY + 8, headerColor);
        x += columns[1] + COLUMN_GAP;
        
        drawCell(poseStack, x, tableY, columns[2], HEADER_HEIGHT, true);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.dimension"), x + 6, tableY + 8, headerColor);
        x += columns[2] + COLUMN_GAP;
        
        drawCell(poseStack, x, tableY, columns[3], HEADER_HEIGHT, true);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.coords"), x + 6, tableY + 8, headerColor);
    }
    
    private void drawCell(PoseStack poseStack, int x, int y, int width, int height, boolean header) {
        // Background removed - no colorful backgrounds
        // Removed white dashed border
        // drawDashedBorder(poseStack, x, y, width, height, 0xFFFFFFFF);
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
        if (maxScroll <= 0) return false;
        scrollOffset = Mth.clamp(scrollOffset - delta * 12, 0, maxScroll);
        return true;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = false;
        for (PillarRow row : rows) {
            if (!row.visible) continue;
            handled |= row.mouseClicked(mouseX, mouseY, button);
        }
        return handled;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (PillarRow row : rows) {
            if (!row.visible) continue;
            if (row.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (PillarRow row : rows) {
            if (!row.visible) continue;
            if (row.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onClose() {
        setButtonsVisible(false);
        for (PillarRow row : rows) {
            row.setVisible(false);
        }
        super.onClose(); // Remove tracked widgets
    }
    
    private void setButtonsVisible(boolean visible) {
        if (reloadButton != null) reloadButton.visible = visible;
        if (saveButton != null) saveButton.visible = visible;
        if (addRowButton != null) addRowButton.visible = visible;
    }
    
    private void markDirty() {
        dirty = true;
        statusMessage = new TranslatableComponent("buildscape.config.ids.unsaved");
    }
    
    private class PillarRow {
        private final EditBox idField;
        private final List<ColorSwatchButton> colorSwatches;
        private final EditBox dimensionField;
        private final EditBox xField;
        private final EditBox yField;
        private final EditBox zField;
        private boolean visible = true;
        private String id;
        private long createdTime;
        private long modifiedTime;
        
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
            
            idField = new EditBox(mc.font, 0, 0, 80, 18, new TranslatableComponent("buildscape.config.ids.id"));
            idField.setValue(data.id != null ? data.id : "");
            idField.setEditable(false);
            idField.setMaxLength(64);
            idField.setBordered(false); // Remove white border
            // Make ID field focusable so it can be clicked
            idField.setCanLoseFocus(false);
            
            // Create color swatches for each color in the pillar (display-only)
            colorSwatches = new ArrayList<>();
            List<String> colors = data.dyeColors != null ? data.dyeColors : Collections.emptyList();
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
                    0, 0, 20, 20, color,
                    (btn) -> {} // Display-only, no click action
                );
                colorSwatches.add(swatch);
            }
            
            dimensionField = new EditBox(mc.font, 0, 0, 90, 18, new TranslatableComponent("buildscape.config.ids.dimension"));
            dimensionField.setValue(data.dimension != null ? data.dimension : "minecraft:overworld");
            dimensionField.setMaxLength(128);
            dimensionField.setResponder((s) -> markDirty());
            dimensionField.setBordered(false); // Remove white border
            
            xField = new EditBox(mc.font, 0, 0, 48, 18, new TextComponent("x"));
            yField = new EditBox(mc.font, 0, 0, 48, 18, new TextComponent("y"));
            zField = new EditBox(mc.font, 0, 0, 48, 18, new TextComponent("z"));
            xField.setBordered(false); // Remove white border
            yField.setBordered(false); // Remove white border
            zField.setBordered(false); // Remove white border
            // Color coordinates: X=Red, Y=Blue, Z=Green
            xField.setTextColor(0xFFFF0000); // Red
            yField.setTextColor(0xFF0000FF); // Blue
            zField.setTextColor(0xFF00FF00); // Green
            
            xField.setValue(String.valueOf(data.x));
            yField.setValue(String.valueOf(data.y));
            zField.setValue(String.valueOf(data.z));
            xField.setResponder((s) -> markDirty());
            yField.setResponder((s) -> markDirty());
            zField.setResponder((s) -> markDirty());
        }
        
        private void openPillarDetailTab(String pillarId) {
            if (parent != null) {
                parent.setActiveTab(new PillarIdDetailConfigTab(parent, pillarId));
            }
        }
        
        private void apply(PillarIdManager.PillarData data) {
            this.id = data.id;
            idField.setValue(data.id != null ? data.id : "");
            
            // Update color swatches
            List<String> colors = data.dyeColors != null ? data.dyeColors : Collections.emptyList();
            // Remove excess swatches
            while (colorSwatches.size() > colors.size()) {
                colorSwatches.remove(colorSwatches.size() - 1);
            }
            // Update existing swatches and add new ones
            for (int i = 0; i < colors.size(); i++) {
                String colorCode = colors.get(i);
                int color = 0xFFFFFF;
                try {
                    if (colorCode.startsWith("#") && colorCode.length() == 7) {
                        color = Integer.parseInt(colorCode.substring(1), 16);
                    }
                } catch (NumberFormatException e) {
                    // Use default white
                }
                
                if (i < colorSwatches.size()) {
                    colorSwatches.get(i).setColor(color);
                } else {
                    ColorSwatchButton swatch = new ColorSwatchButton(
                        0, 0, 20, 20, color,
                        (btn) -> {} // Display-only, no click action
                    );
                    colorSwatches.add(swatch);
                }
            }
            
            dimensionField.setValue(data.dimension != null ? data.dimension : "minecraft:overworld");
            xField.setValue(String.valueOf(data.x));
            yField.setValue(String.valueOf(data.y));
            zField.setValue(String.valueOf(data.z));
            // Ensure coordinate colors are set when applying data
            xField.setTextColor(0xFFFF0000); // Red
            yField.setTextColor(0xFF0000FF); // Blue
            zField.setTextColor(0xFF00FF00); // Green
            this.createdTime = data.createdTime;
            this.modifiedTime = data.modifiedTime;
        }
        
        private void setVisible(boolean visible) {
            this.visible = visible;
            idField.setVisible(visible);
            for (ColorSwatchButton swatch : colorSwatches) {
                swatch.visible = visible;
            }
            dimensionField.setVisible(visible);
            xField.setVisible(visible);
            yField.setVisible(visible);
            zField.setVisible(visible);
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
            
            // Get colors from swatches - we'll preserve existing colors since swatches are read-only display
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
        
        private void render(PoseStack poseStack, int startX, int y, int[] columns) {
            int x = startX;
            drawCell(poseStack, x, y, columns[0], ROW_HEIGHT, false);
            idField.x = x + 6;
            idField.y = y + (ROW_HEIGHT - 18) / 2;
            idField.setWidth(columns[0] - 12);
            idField.render(poseStack, 0, 0, 0);
            x += columns[0] + COLUMN_GAP;
            
            drawCell(poseStack, x, y, columns[1], ROW_HEIGHT, false);
            // Render color swatches
            int swatchSize = 20;
            int swatchSpacing = 4;
            int swatchStartX = x + 6;
            int swatchY = y + (ROW_HEIGHT - swatchSize) / 2;
            for (int i = 0; i < colorSwatches.size(); i++) {
                ColorSwatchButton swatch = colorSwatches.get(i);
                swatch.x = swatchStartX + i * (swatchSize + swatchSpacing);
                swatch.y = swatchY;
                swatch.renderButton(poseStack, 0, 0, 0);
            }
            x += columns[1] + COLUMN_GAP;
            
            drawCell(poseStack, x, y, columns[2], ROW_HEIGHT, false);
            // Render dimension text only (no icon)
            String dimension = dimensionField.getValue();
            
            // Format dimension name for display (remove "minecraft:" prefix)
            String displayDimension = dimension;
            if (displayDimension != null && displayDimension.contains(":")) {
                String[] parts = displayDimension.split(":");
                if (parts.length > 1) {
                    String dimName = parts[1];
                    // Capitalize first letter
                    displayDimension = dimName.substring(0, 1).toUpperCase() + dimName.substring(1);
                }
            }
            
            // Render dimension text without EditBox (just text)
            int textX = x + 6;
            int textY = y + (ROW_HEIGHT - 9) / 2;
            Minecraft.getInstance().font.draw(poseStack, displayDimension != null ? displayDimension : "", textX, textY, 0xFFFFFFFF);
            x += columns[2] + COLUMN_GAP;
            
            drawCell(poseStack, x, y, columns[3], ROW_HEIGHT, false);
            // Improved coordinate alignment - ensure consistent spacing
            int coordPadding = 6;
            int coordGap = 6; // Slightly increased gap for better alignment
            int coordUsable = columns[3] - coordPadding * 2 - coordGap * 2;
            int coordWidth = coordUsable / 3;
            int coordY = y + (ROW_HEIGHT - 18) / 2; // Original position - user said it was moved too low
            
            // Ensure all coordinates are properly aligned
            xField.x = x + coordPadding;
            xField.y = coordY;
            xField.setWidth(coordWidth);
            
            yField.x = x + coordPadding + coordWidth + coordGap;
            yField.y = coordY;
            yField.setWidth(coordWidth);
            
            zField.x = x + coordPadding + (coordWidth + coordGap) * 2;
            zField.y = coordY;
            zField.setWidth(coordWidth);
            
            // Render coordinates with consistent alignment
            xField.render(poseStack, 0, 0, 0);
            yField.render(poseStack, 0, 0, 0);
            zField.render(poseStack, 0, 0, 0);
        }
        
        private void setBounds(int x, int y, int width, int height) {
            this.rowX = x;
            this.rowY = y;
            this.rowWidth = width;
            this.rowHeight = height;
        }
        
        private boolean isMouseOverRow(double mouseX, double mouseY) {
            return mouseX >= rowX && mouseX < rowX + rowWidth &&
                   mouseY >= rowY && mouseY < rowY + rowHeight;
        }
        
        private boolean mouseClicked(double mouseX, double mouseY, int button) {
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
            // We'll check if mouse is over any part of the row by checking if it's over any field
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
                if (mouseX >= rowX && mouseX < rowX + columns[0]) {
                    // Clicked in ID column but not on ID field - still open detail tab
                    if (button == 0) {
                        openPillarDetailTab(id);
                        return true;
                    }
                }
                clickedOnRow = true;
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
            float yaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
            
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
            double x = centerX - 0.5 + (random.nextDouble() * 1.0);
            double z = centerZ - 0.5 + (random.nextDouble() * 1.0);
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.1, centerZ - 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.1, centerZ + 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, pos.getY() + 0.1, z, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, pos.getY() + 0.1, z, 0, 0, 0);
        }
        
        // Horizontal edges - top
        for (int i = 0; i < 10; i++) {
            double x = centerX - 0.5 + (random.nextDouble() * 1.0);
            double z = centerZ - 0.5 + (random.nextDouble() * 1.0);
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

