package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.*;
import com.kingodogo.buildscape.client.PillarMarkerManager;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.RemovePillarPacket;
import com.kingodogo.buildscape.network.RequestPillarIdsPacket;
import com.kingodogo.buildscape.network.UpdateAllPillarIdsPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.*;

public class PillarIdsConfigTab extends AbstractConfigTab {
    private static final int BASE_HEADER_HEIGHT = 28;
    private static final int BASE_ROW_HEIGHT = 32;
    private static final int BASE_TABLE_MARGIN = 12;
    private static final int BASE_COLUMN_GAP = 8;
    private static final int AUTO_REFRESH_MS = 1500;
    private static final int MAX_COLORS = 5;

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
    private ScaledTextButton reloadButton;
    private Button selectAllButton;
    private ScaledTextButton removeAllButton;
    private ScaledTextButton removeButton;
    private ScaledTextButton applyButton;
    private Button headerSelectAllCheckbox;
    private double scrollOffset = 0;
    private double maxScroll = 0;
    private long lastRefreshCheck = 0L;
    private String lastSignature = "";
    private boolean dirty = false;
    private Component statusMessage = ComponentHelper.empty();
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    public PillarIdsConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    @Override
    public void init() {
        rows.clear();
        scrollOffset = 0;
        maxScroll = 0;
        dirty = false;
        statusMessage = ComponentHelper.empty();

        int buttonWidth = BuildScapeConfigScreen.scaleSize(100);
        int buttonHeight = BuildScapeConfigScreen.getScaledButtonHeight();
        ScaledTextButton reloadBtn = new ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.ids.reload"),
                (btn) -> manualReload());
        reloadBtn.setCustomTextColors(0xFFFF00, 0xFFFF55);
        reloadButton = reloadBtn;

        ScaledTextButton removeAllBtn = new ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.ids.remove_all"),
                (btn) -> confirmRemoveAll());
        removeAllBtn.setCustomTextColors(0xFF0000, 0xFF5555);
        removeAllButton = removeAllBtn;

        ScaledTextButton removeBtn = new ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.ids.remove"),
                (btn) -> removeSelected());
        removeBtn.setCustomTextColors(0xFF5555, 0xFF7777);
        removeButton = removeBtn;

        addTabWidget(reloadButton);
        addTabWidget(removeAllButton);
        addTabWidget(removeButton);

        ScaledTextButton applyBtn = new ScaledTextButton(
                0, 0, buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.apply"),
                (btn) -> saveRows());
        applyBtn.setCustomTextColors(0x55FF55, 0xAAFFAA);
        applyButton = applyBtn;
        addTabWidget(applyButton);

        int checkboxSize = BuildScapeConfigScreen.scaleSize(14);
        headerSelectAllCheckbox = Services.PLATFORM.createCustomButton(0, 0, checkboxSize, checkboxSize, ComponentHelper.empty(), (btn) -> {
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
        }, (btn, poseStackOrGraphics, mouseX, mouseY, pt) -> {
            if (!btn.visible) return;
            int btnX = WidgetLayoutHelper.getX(btn);
            int btnY = WidgetLayoutHelper.getY(btn);
            int btnW = btn.getWidth();
            int btnH = WidgetLayoutHelper.getHeight(btn);
            int borderColor = btn.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;
            boolean allChecked = true;
            boolean someChecked = false;
            for (PillarRow row : rows) {
                if (row.visible) {
                    if (!row.isSelected()) allChecked = false;
                    else someChecked = true;
                }
            }

            Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, 0x80000000);
            Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + 1, borderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, borderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + 1, btnY + btnH, borderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, borderColor);

            int pad = Math.max(2, BuildScapeConfigScreen.scaleSize(2));
            if (allChecked && someChecked) {
                Services.PLATFORM.fill(poseStackOrGraphics, btnX + pad, btnY + pad, btnX + btnW - pad, btnY + btnH - pad, 0xFF55FF55);
            } else if (someChecked) {
                Services.PLATFORM.fill(poseStackOrGraphics, btnX + pad, btnY + (btnH / 2) - 1, btnX + btnW - pad, btnY + (btnH / 2) + 1, 0xFF55FF55);
            }
        });
        addTabWidget(headerSelectAllCheckbox);

        setButtonsVisible(true);

        Minecraft mc = Minecraft.getInstance();
        if (mc.getCurrentServer() != null) {
            PacketFactory.sendToServer(new RequestPillarIdsPacket());
        } else {
            try {
                PillarIdManager.getClient().markAsLoaded();
            } catch (Exception ignored) {
            }
        }

        refreshFromManager();
    }

    private void manualReload() {
        dirty = false;
        refreshFromManager();
    }

    public void refreshFromManager() {
        PillarIdManager manager = PillarIdManager.getClient();
        try {
            manager.checkAndReload();
        } catch (Exception ignored) {
        }

        Map<String, PillarIdManager.PillarData> snapshot = manager.copyDataSnapshot();

        applySnapshot(snapshot);
        lastSignature = computeSignature(snapshot);

        if (!manager.hasLoaded()) {
            statusMessage = ComponentHelper.translatable("buildscape.config.ids.not_ready");
        } else if (snapshot.isEmpty()) {
            statusMessage = ComponentHelper.translatable("buildscape.config.ids.empty");
        } else if (!dirty) {
            statusMessage = ComponentHelper.empty();
        }
    }

    private void maybeAutoRefresh() {
        if (dirty) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastRefreshCheck < AUTO_REFRESH_MS) {
            return;
        }
        lastRefreshCheck = now;

        PillarIdManager manager = PillarIdManager.getClient();
        manager.checkAndReload();
        Map<String, PillarIdManager.PillarData> snapshot = manager.copyDataSnapshot();
        String signature = computeSignature(snapshot);
        if (!Objects.equals(signature, lastSignature)) {
            applySnapshot(snapshot);
            lastSignature = signature;
            statusMessage = snapshot.isEmpty()
                    ? ComponentHelper.translatable("buildscape.config.ids.empty")
                    : ComponentHelper.empty();
        }
    }

    private void applySnapshot(Map<String, PillarIdManager.PillarData> snapshot) {
        Set<String> seenIds = new HashSet<>();

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
        PillarIdManager manager = PillarIdManager.getClient();
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
        statusMessage = ComponentHelper.translatable("buildscape.config.ids.unsaved");
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
            statusMessage = ComponentHelper.literal(errors.get(0));
            return;
        }

        if (Minecraft.getInstance().player != null) {
            Services.PLATFORM.playNoteBlockBell();
        }

        PillarIdManager manager = PillarIdManager.getClient();
        manager.replaceAllPillarData(toSave);

        PacketFactory.sendToServer(new UpdateAllPillarIdsPacket(toSave));

        dirty = false;
        lastSignature = computeSignature(toSave);
        statusMessage = ComponentHelper.translatable("buildscape.config.ids.status.saved");
    }

    private int[] computeColumns(int tableWidth) {
        int scrollbarWidth = BuildScapeConfigScreen.scaleSize(8);
        int scrollbarPadding = BuildScapeConfigScreen.scaleSize(4);
        int availableWidth = tableWidth - scrollbarWidth - scrollbarPadding;

        int itemWidth = Math.max(42, BuildScapeConfigScreen.scaleSize(54));

        int remainingWidth = availableWidth - itemWidth;

        double leftMarginPercent = 0.2;
        double gapPercent = 0.5;
        double checkboxPercent = 3.5;
        double idPercent = 17.5;
        double colorsPercent = 21.0;
        double dimensionPercent = 14.8;
        double coordsPercent = 20.5;

        double totalGapPercent = gapPercent * 5;

        double totalPercent = leftMarginPercent + checkboxPercent + idPercent +
                colorsPercent + dimensionPercent + coordsPercent + totalGapPercent;

        int leftMargin = (int) (remainingWidth * leftMarginPercent / 100.0);
        int gap = (int) (remainingWidth * gapPercent / 100.0);
        int checkboxWidth = (int) (remainingWidth * checkboxPercent / 100.0);
        int idWidth = (int) (remainingWidth * idPercent / 100.0);
        int colorsWidth = (int) (remainingWidth * colorsPercent / 100.0);
        int dimensionWidth = (int) (remainingWidth * dimensionPercent / 100.0);
        int coordsWidth = (int) (remainingWidth * coordsPercent / 100.0);

        int totalCalculated = leftMargin + checkboxWidth + gap + idWidth + gap + itemWidth + gap +
                colorsWidth + gap + dimensionWidth + gap + coordsWidth;
        int remainder = availableWidth - totalCalculated;
        coordsWidth += remainder;

        return new int[]{leftMargin, checkboxWidth, idWidth, itemWidth, colorsWidth, dimensionWidth, coordsWidth, gap};
    }

    private void positionButtons(int contentX, int contentY, int contentWidth) {
        int buttonWidth = BuildScapeConfigScreen.scaleSize(100);
        int buttonHeight = BuildScapeConfigScreen.getScaledButtonHeight();
        int spacing = BuildScapeConfigScreen.scaleSize(8);
        int topMargin = BuildScapeConfigScreen.scaleSize(4);

        int y = contentY + topMargin;
        int buttonInset = getTableMargin();
        int x = contentX + buttonInset;

        reloadButton.x = x;
        reloadButton.y = y;
        reloadButton.setWidth(buttonWidth);
        reloadButton.setHeight(buttonHeight);
        x += buttonWidth + spacing;

        removeButton.x = x;
        removeButton.y = y;
        removeButton.setWidth(buttonWidth);
        removeButton.setHeight(buttonHeight);
        x += buttonWidth + spacing;

        applyButton.x = x;
        applyButton.y = y;
        applyButton.setWidth(buttonWidth);
        applyButton.setHeight(buttonHeight);

        removeAllButton.x = contentX + contentWidth - buttonWidth - buttonInset;
        removeAllButton.y = y;
        removeAllButton.setWidth(buttonWidth);
        removeAllButton.setHeight(buttonHeight);
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getRightPanelX() + parent.getRightPanelWidth() - parent.getContentX();
        int contentHeight = parent.getContentHeight();

        positionButtons(contentX, contentY, contentWidth);
        maybeAutoRefresh();


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
        int colGap = columns[7];

        int headerSpacing = BuildScapeConfigScreen.scaleSize(16);
        int rowsStartY = tableY + headerHeight + headerSpacing;

        int visibleRows = 0;
        for (PillarRow row : rows) {
            if (row.visible)
                visibleRows++;
        }
        int availableRowHeight = tableHeight - headerHeight - headerSpacing;
        int rowGapForScroll = Math.max(1, (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() * 0.002));
        maxScroll = Math.max(0, visibleRows * (rowHeight + rowGapForScroll) - availableRowHeight);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);

        drawHeader(poseStackOrGraphics, tableX, tableY, columns, colGap);

        int actualTableWidth = 0;
        for (int i = 0; i < 7; i++) {
            actualTableWidth += columns[i];
        }
        actualTableWidth += colGap * 5;

        int scissorX = tableX;
        int scissorY = rowsStartY;
        int scissorWidth = actualTableWidth;
        int scissorHeight = availableRowHeight;

        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        int scaledScissorX = (int) (scissorX * guiScale);
        int scaledScissorY = (int) (windowHeight - (scissorY + scissorHeight) * guiScale);
        int scaledScissorWidth = (int) (scissorWidth * guiScale);
        int scaledScissorHeight = (int) (scissorHeight * guiScale);

        Services.PLATFORM.enableScissor(poseStackOrGraphics, scaledScissorX, scaledScissorY, scaledScissorWidth, scaledScissorHeight);
        try {
            for (PillarRow row : rows) {
                row.resetBounds();
            }

            int rowIndex = 0;
            int visibleAreaBottom = rowsStartY + scissorHeight;
            int rowGap = Math.max(1, (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() * 0.002));
            for (PillarRow row : rows) {
                if (!row.visible)
                    continue;
                int rowY = rowsStartY + rowIndex * (rowHeight + rowGap) - (int) scrollOffset;

                if (rowY + rowHeight < rowsStartY || rowY > visibleAreaBottom) {
                    rowIndex++;
                    continue;
                }

                row.setBounds(tableX, rowY, tableWidth, rowHeight);
                row.render(poseStackOrGraphics, tableX, rowY, columns);
                rowIndex++;
            }


        } finally {
            Services.PLATFORM.disableScissor(poseStackOrGraphics);
        }

        if (maxScroll > 0) {
            int scrollbarX = tableX + actualTableWidth + BuildScapeConfigScreen.scaleSize(4);
            int scrollbarY = rowsStartY;
            int scrollbarHeight = availableRowHeight;

            double visibleRatio = (double) availableRowHeight / (visibleRows * (rowHeight + rowGapForScroll));
            scrollbarRenderer.renderScrollbar(poseStackOrGraphics, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, maxScroll, visibleRatio);
        }

        int borderColor = 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, contentX, contentY, contentX + contentWidth, contentY + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, contentX, contentY + contentHeight - 1, contentX + contentWidth, contentY + contentHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, contentX, contentY, contentX + 1, contentY + contentHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, contentX + contentWidth - 1, contentY, contentX + contentWidth, contentY + contentHeight, borderColor);

        int statusY = contentY + contentHeight - BuildScapeConfigScreen.scaleSize(14);
        if (statusMessage != null && !statusMessage.getString().isEmpty()) {
            Services.PLATFORM.draw(poseStackOrGraphics, Minecraft.getInstance().font, statusMessage.getString(), tableX, statusY, 0xFFFFFF);
        } else if (dirty) {
            Services.PLATFORM.draw(poseStackOrGraphics, Minecraft.getInstance().font, ComponentHelper.translatable("buildscape.config.ids.unsaved").getString(),
                    tableX, statusY, 0xFFFFFF);
        }
    }


    private void drawHeader(Object poseStackOrGraphics, int tableX, int tableY, int[] columns, int columnGap) {
        int x = tableX;
        int headerColor = 0xFFCCCCCC;
        int headerHeight = getHeaderHeight();
        Minecraft mc = Minecraft.getInstance();
        int fontHeight = mc.font.lineHeight;
        int textPadding = BuildScapeConfigScreen.scaleSize(10);

        int headerTextY = tableY + (headerHeight - fontHeight) / 2 + 1;

        x += columns[0];

        int checkboxSize = BuildScapeConfigScreen.scaleSize(14);
        WidgetLayoutHelper.setPosition(headerSelectAllCheckbox, x + (columns[1] - checkboxSize) / 2, tableY + (headerHeight - checkboxSize) / 2);
        headerSelectAllCheckbox.setWidth(checkboxSize);
        WidgetLayoutHelper.setWidgetHeight(headerSelectAllCheckbox, checkboxSize);
        Services.PLATFORM.renderWidget(poseStackOrGraphics, headerSelectAllCheckbox, 0, 0, 0);
        x += columns[1] + columnGap;

        drawCell(poseStackOrGraphics, x, tableY, columns[2], headerHeight, true);
        Component idText = ComponentHelper.translatable("buildscape.config.ids.id");
        String idTextStr = idText.getString();
        int maxIdHeaderWidth = columns[2] - textPadding * 2;
        int idHeaderTextWidth = mc.font.width(idTextStr);
        if (idHeaderTextWidth > maxIdHeaderWidth) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            float scale = (float) maxIdHeaderWidth / (float) idHeaderTextWidth;
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, idTextStr, (x + textPadding) / scale, headerTextY / scale, headerColor);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        } else {
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, idTextStr, x + textPadding, headerTextY, headerColor);
        }
        x += columns[2] + columnGap;

        drawCell(poseStackOrGraphics, x, tableY, columns[3], headerHeight, true);
        Component itemText = ComponentHelper.translatable("buildscape.config.ids.item");
        String itemTextStr = itemText.getString();
        int maxItemHeaderWidth = columns[3] - textPadding * 2;
        int itemTextWidth = mc.font.width(itemTextStr);
        int itemCenterX = x + (columns[3] - itemTextWidth) / 2;
        if (itemTextWidth > maxItemHeaderWidth) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            float scale = (float) maxItemHeaderWidth / (float) itemTextWidth;
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
            int scaledWidth = (int) (itemTextWidth * scale);
            itemCenterX = x + (columns[3] - scaledWidth) / 2;
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, itemTextStr, itemCenterX / scale, headerTextY / scale, headerColor);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        } else {
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, itemTextStr, itemCenterX, headerTextY, headerColor);
        }
        x += columns[3] + columnGap;

        drawCell(poseStackOrGraphics, x, tableY, columns[4], headerHeight, true);
        Component colorsText = ComponentHelper.translatable("buildscape.config.ids.colors");
        String colorsTextStr = colorsText.getString();
        int maxColorsHeaderWidth = columns[4] - textPadding * 2;
        int colorsHeaderTextWidth = mc.font.width(colorsTextStr);
        if (colorsHeaderTextWidth > maxColorsHeaderWidth) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            float scale = (float) maxColorsHeaderWidth / (float) colorsHeaderTextWidth;
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, colorsTextStr, (x + textPadding) / scale, headerTextY / scale, headerColor);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        } else {
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, colorsTextStr, x + textPadding, headerTextY, headerColor);
        }
        x += columns[4] + columnGap;

        drawCell(poseStackOrGraphics, x, tableY, columns[5], headerHeight, true);
        Component dimensionText = ComponentHelper.translatable("buildscape.config.ids.dimension");
        String dimensionTextStr = dimensionText.getString();
        int maxDimensionHeaderWidth = columns[5] - textPadding * 2;
        int dimensionHeaderTextWidth = mc.font.width(dimensionTextStr);
        if (dimensionHeaderTextWidth > maxDimensionHeaderWidth) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            float scale = (float) maxDimensionHeaderWidth / (float) dimensionHeaderTextWidth;
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, dimensionTextStr, (x + textPadding) / scale, headerTextY / scale, headerColor);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        } else {
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, dimensionTextStr, x + textPadding, headerTextY, headerColor);
        }
        x += columns[5] + columnGap;


        int coordPadding = BuildScapeConfigScreen.scaleSize(8);
        int coordGap = BuildScapeConfigScreen.scaleSize(8);
        int coordUsable = columns[6] - coordPadding * 2 - coordGap * 2;
        int minCoordWidth = BuildScapeConfigScreen.scaleSize(40);
        int coordWidth = Math.max(minCoordWidth, coordUsable / 3);

        int totalNeeded = coordPadding * 2 + coordWidth * 3 + coordGap * 2;
        if (totalNeeded > columns[6]) {
            coordGap = Math.max(BuildScapeConfigScreen.scaleSize(4),
                    (columns[6] - coordPadding * 2 - coordWidth * 3) / 2);
        }

        int xLabelX = x + coordPadding + coordWidth / 2;
        int yLabelX = x + coordPadding + coordWidth + coordGap + coordWidth / 2;
        int zLabelX = x + coordPadding + (coordWidth + coordGap) * 2 + coordWidth / 2;

        int xLabelWidth = mc.font.width("X");
        int yLabelWidth = mc.font.width("Y");
        int zLabelWidth = mc.font.width("Z");

        Services.PLATFORM.draw(poseStackOrGraphics, mc.font, "X", xLabelX - xLabelWidth / 2, headerTextY, 0xFFFF0000);
        Services.PLATFORM.draw(poseStackOrGraphics, mc.font, "Y", yLabelX - yLabelWidth / 2, headerTextY, 0xFF5555FF);
        Services.PLATFORM.draw(poseStackOrGraphics, mc.font, "Z", zLabelX - zLabelWidth / 2, headerTextY, 0xFF00FF00);
    }

    private void drawCell(Object poseStackOrGraphics, int x, int y, int width, int height, boolean header) {
        int borderColor = header ? 0x80555555 : 0x60444444;
        int bgColor = header ? 0x60222222 : 0x40111111;

        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + height, bgColor);

        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + height - 1, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + 1, x + 1, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + width - 1, y, x + width, y + height, borderColor);
    }

    private void drawDashedBorder(Object poseStackOrGraphics, int x, int y, int width, int height, int color) {
        int dash = 6;
        int gap = 4;

        for (int dx = x; dx < x + width; dx += dash + gap) {
            int end = Math.min(dx + dash, x + width);
            Services.PLATFORM.fill(poseStackOrGraphics, dx, y, end, y + 2, color);
        }
        for (int dx = x; dx < x + width; dx += dash + gap) {
            int end = Math.min(dx + dash, x + width);
            Services.PLATFORM.fill(poseStackOrGraphics, dx, y + height - 2, end, y + height, color);
        }
        for (int dy = y; dy < y + height; dy += dash + gap) {
            int end = Math.min(dy + dash, y + height);
            Services.PLATFORM.fill(poseStackOrGraphics, x, dy, x + 2, end, color);
        }
        for (int dy = y; dy < y + height; dy += dash + gap) {
            int end = Math.min(dy + dash, y + height);
            Services.PLATFORM.fill(poseStackOrGraphics, x + width - 2, dy, x + width, end, color);
        }
    }

    private void drawTableBorder(Object poseStackOrGraphics, int x, int y, int width, int height) {
        int borderColor = 0x33CCCCCC;
        Services.PLATFORM.fill(poseStackOrGraphics, x - 2, y - 2, x + width + 2, y, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x - 2, y + height, x + width + 2, y + height + 2, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x - 2, y, x, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + width, y, x + width + 2, y + height, borderColor);
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
        int contentWidth = parent.getRightPanelX() + parent.getRightPanelWidth() - parent.getContentX();
        int contentHeight = parent.getContentHeight();

        boolean rowHandled = false;
        for (PillarRow row : rows) {
            if (!row.visible)
                continue;
            if (row.mouseClicked(mouseX, mouseY, button)) {
                rowHandled = true;
                break;
            }
        }
        if (rowHandled)
            return true;

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
            int colGap = columns[7];
            int headerSpacing = BuildScapeConfigScreen.scaleSize(16);
            int rowsStartY = tableY + headerHeight + headerSpacing;

            int availableRowHeight = tableHeight - headerHeight - headerSpacing;

            int actualTableWidth = 0;
            for (int i = 0; i < 7; i++) {
                actualTableWidth += columns[i];
            }
            actualTableWidth += colGap * 5;

            int scrollbarX = tableX + actualTableWidth + BuildScapeConfigScreen.scaleSize(4);
            int scrollbarY = rowsStartY;
            int scrollbarHeight = availableRowHeight;

            int visibleRows = 0;
            for (PillarRow row : rows) {
                if (row.visible)
                    visibleRows++;
            }
            double visibleRatio = (double) availableRowHeight / (visibleRows * rowHeight);

            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    tableX, rowsStartY, actualTableWidth, availableRowHeight,
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
        super.onClose();
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
        Services.PLATFORM.openScreen(new ConfirmScreen(
                (confirmed) -> {
                    if (confirmed) {
                        removeAll();
                    }
                    Services.PLATFORM.openScreen(parent.getWrapperScreen());
                },
                ComponentHelper.translatable("buildscape.config.ids.remove_all"),
                ComponentHelper.literal("Are you sure you want to completely remove all Pillar IDs?")
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
            PillarIdManager manager = PillarIdManager.getClient();
            for (String id : idsToRemove) {
                manager.removePillar(id);
            }

            PacketFactory.sendToServer(new RemovePillarPacket(idsToRemove));

            refreshFromManager();
            dirty = true;
            if (Minecraft.getInstance().player != null) {
                Services.PLATFORM.playNoteBlockDidgeridoo();
            }
            statusMessage = ComponentHelper.translatable("buildscape.config.ids.removed_all");
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
            PillarIdManager manager = PillarIdManager.getClient();
            for (String id : idsToRemove) {
                manager.removePillar(id);
            }

            PacketFactory.sendToServer(new RemovePillarPacket(idsToRemove));

            refreshFromManager();
            dirty = true;
            if (Minecraft.getInstance().player != null) {
                Services.PLATFORM.playNoteBlockDidgeridoo();
            }
            statusMessage = ComponentHelper.translatable("buildscape.config.ids.removed_selected", idsToRemove.size());
        }
    }

    private void markDirty() {
        dirty = true;
        statusMessage = ComponentHelper.translatable("buildscape.config.ids.unsaved");
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
        private ItemStack displayedItem = ItemStack.EMPTY;
        private ItemStack pillarTypeStack = ItemStack.EMPTY;

        private long lastClickTime = 0;
        private double lastClickX = -1;
        private double lastClickY = -1;
        private static final long DOUBLE_CLICK_TIME_MS = 300;
        private static final double DOUBLE_CLICK_DISTANCE = 5.0;

        private int rowX, rowY, rowWidth, rowHeight;

        private PillarRow(PillarIdManager.PillarData data) {
            Minecraft mc = Minecraft.getInstance();
            this.id = data.id;
            this.createdTime = data.createdTime;
            this.modifiedTime = data.modifiedTime;

            loadItemStacks(data);

            int checkboxSize = BuildScapeConfigScreen.scaleSize(14);
            selectCheckbox = Services.PLATFORM.createCustomButton(0, 0, checkboxSize, checkboxSize, ComponentHelper.empty(), (btn) -> {
                selected = !selected;
            }, (btn, poseStackOrGraphics, mouseX, mouseY, pt) -> {
                if (!visible) return;
                int btnX = WidgetLayoutHelper.getX(btn);
                int btnY = WidgetLayoutHelper.getY(btn);
                int btnW = btn.getWidth();
                int btnH = WidgetLayoutHelper.getHeight(btn);
                int borderColor = btn.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFAAAAAA;
                Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + btnH, 0x80000000);
                Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + btnW, btnY + 1, borderColor);
                Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, borderColor);
                Services.PLATFORM.fill(poseStackOrGraphics, btnX, btnY, btnX + 1, btnY + btnH, borderColor);
                Services.PLATFORM.fill(poseStackOrGraphics, btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, borderColor);

                if (selected) {
                    int pad = Math.max(2, BuildScapeConfigScreen.scaleSize(2));
                    Services.PLATFORM.fill(poseStackOrGraphics, btnX + pad, btnY + pad, btnX + btnW - pad, btnY + btnH - pad, 0xFF55FF55);
                }
            });
            selectCheckbox.setAlpha(1.0f);

            int editBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
            idField = new EditBox(mc.font, 0, 0, BuildScapeConfigScreen.scaleSize(80), editBoxHeight,
                    ComponentHelper.translatable("buildscape.config.ids.id"));
            idField.setValue(data.id != null ? data.id : "");
            idField.setEditable(false);
            idField.setMaxLength(64);
            idField.setBordered(false);
            idField.setCanLoseFocus(false);

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
                }

                ColorSwatchButton swatch = new ColorSwatchButton(
                        0, 0, swatchSize, swatchSize, color,
                        (btn) -> {
                        }
                );
                swatch.visible = true;
                colorSwatches.add(swatch);
            }

            dimensionField = new EditBox(mc.font, 0, 0, BuildScapeConfigScreen.scaleSize(90), editBoxHeight,
                    ComponentHelper.translatable("buildscape.config.ids.dimension"));
            dimensionField.setValue(data.dimension != null ? data.dimension : "minecraft:overworld");
            dimensionField.setMaxLength(128);
            dimensionField.setEditable(false);
            dimensionField.setBordered(false);

            int coordFieldWidth = BuildScapeConfigScreen.scaleSize(48);
            xField = new EditBox(mc.font, 0, 0, coordFieldWidth, editBoxHeight, ComponentHelper.literal("x"));
            yField = new EditBox(mc.font, 0, 0, coordFieldWidth, editBoxHeight, ComponentHelper.literal("y"));
            zField = new EditBox(mc.font, 0, 0, coordFieldWidth, editBoxHeight, ComponentHelper.literal("z"));
            xField.setBordered(false);
            yField.setBordered(false);
            zField.setBordered(false);
            xField.setEditable(false);
            yField.setEditable(false);
            zField.setEditable(false);

            xField.setValue(String.valueOf(data.x));
            yField.setValue(String.valueOf(data.y));
            zField.setValue(String.valueOf(data.z));

            xField.setTextColor(0xFFFF0000);
            yField.setTextColor(0xFF5555FF);
            zField.setTextColor(0xFF00FF00);
        }

        private void loadItemStacks(PillarIdManager.PillarData data) {
            this.displayedItem = ItemStack.EMPTY;
            this.pillarTypeStack = ItemStack.EMPTY;

            if (data.displayedItem != null && !data.displayedItem.isEmpty()) {
                try {
                    Item item = Services.PLATFORM.getItem(CommonId.parse(data.displayedItem));
                    if (item != null) {
                        this.displayedItem = new ItemStack(item);
                    }
                } catch (Exception ignored) {
                }
            }

            if (data.pillarType != null && !data.pillarType.isEmpty()) {
                try {
                    Item item = Services.PLATFORM.getItem(CommonId.parse(data.pillarType));
                    if (item != null) {
                        this.pillarTypeStack = new ItemStack(item);
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

        private List<String> loadColorsFromNBT(PillarIdManager.PillarData data) {
            try {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) {
                    return null;
                }

                String currentDimension = PillarIdManager.getDimensionKey(mc.level);
                if (!currentDimension.equals(data.dimension)) {
                    MinecraftServer server = mc.getSingleplayerServer();
                    if (server != null && server.isRunning()) {
                        for (ServerLevel level : server.getAllLevels()) {
                            if (level == null)
                                continue;

                            String dimensionKey = PillarIdManager.getDimensionKey(level);
                            if (!dimensionKey.equals(data.dimension)) {
                                continue;
                            }

                            BlockPos pos = new BlockPos(data.x, data.y, data.z);

                            if (!level.isLoaded(pos)) {
                                continue;
                            }

                            ChunkAccess chunk = level.getChunk(pos);
                            if (!(chunk instanceof LevelChunk)) {
                                continue;
                            }

                            if (!level.isLoaded(pos)) {
                                continue;
                            }

                            BlockEntity be = level.getBlockEntity(pos);
                            if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                                continue;
                            }

                            BlockPos bottomPos = pillarBE.findStackBottom();
                            BlockEntity bottomBE = level.getBlockEntity(bottomPos);

                            if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                                continue;
                            }

                            List<String> nbtColors = bottomPillarBE.getParticleColors();
                            if (nbtColors != null && !nbtColors.isEmpty()) {
                                return new ArrayList<>(nbtColors);
                            }
                        }
                    }
                    return null;
                }

                BlockPos pos = new BlockPos(data.x, data.y, data.z);

                if (!mc.level.isLoaded(pos)) {
                    return null;
                }

                BlockEntity be = mc.level.getBlockEntity(pos);
                if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                    return null;
                }

                BlockPos bottomPos = pillarBE.findStackBottom();
                BlockEntity bottomBE = mc.level.getBlockEntity(bottomPos);

                if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                    return null;
                }

                List<String> nbtColors = bottomPillarBE.getParticleColors();
                if (nbtColors != null && !nbtColors.isEmpty()) {
                    return new ArrayList<>(nbtColors);
                }
            } catch (Exception e) {
            }

            return null;
        }

        private void apply(PillarIdManager.PillarData data) {
            this.id = data.id;
            idField.setValue(data.id != null ? data.id : "");

            if (data.pillarType != null && !data.pillarType.isEmpty()) {
                try {
                    Item item = Services.PLATFORM.getItem(CommonId.parse(data.pillarType));
                    if (item != null) {
                        this.pillarTypeStack = new ItemStack(item);
                    } else {
                        this.pillarTypeStack = ItemStack.EMPTY;
                    }
                } catch (Exception e) {
                    this.pillarTypeStack = ItemStack.EMPTY;
                }
            } else {
                this.pillarTypeStack = ItemStack.EMPTY;
            }

            if (data.displayedItem != null && !data.displayedItem.isEmpty()) {
                try {
                    Item item = Services.PLATFORM.getItem(CommonId.parse(data.displayedItem));
                    if (item != null) {
                        this.displayedItem = new ItemStack(item);
                    } else {
                        this.displayedItem = ItemStack.EMPTY;
                    }
                } catch (Exception e) {
                    this.displayedItem = ItemStack.EMPTY;
                }
            } else {
                this.displayedItem = ItemStack.EMPTY;
            }

            List<String> colors;
            if (data.dyeColors != null && !data.dyeColors.isEmpty()) {
                colors = new ArrayList<>(data.dyeColors);
            } else {
                colors = Collections.emptyList();
            }

            colorSwatches.clear();

            for (int i = 0; i < colors.size(); i++) {
                String colorCode = colors.get(i);
                int color = 0xFFFFFF;
                try {
                    if (colorCode != null && colorCode.startsWith("#") && colorCode.length() == 7) {
                        color = Integer.parseInt(colorCode.substring(1), 16);
                    }
                } catch (NumberFormatException e) {
                }

                ColorSwatchButton swatch = new ColorSwatchButton(
                        0, 0, BuildScapeConfigScreen.scaleSize(20), BuildScapeConfigScreen.scaleSize(20), color,
                        (btn) -> {
                        }
                );
                swatch.visible = true;
                colorSwatches.add(swatch);
            }

            dimensionField.setValue(data.dimension != null ? data.dimension : "minecraft:overworld");
            xField.setValue(String.valueOf(data.x));
            yField.setValue(String.valueOf(data.y));
            zField.setValue(String.valueOf(data.z));
            xField.setTextColor(0xFFFF0000);
            yField.setTextColor(0xFF5555FF);
            zField.setTextColor(0xFF00FF00);
            this.createdTime = data.createdTime;
            this.modifiedTime = data.modifiedTime;

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

            PillarIdManager manager = PillarIdManager.getClient();
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

        private void render(Object poseStackOrGraphics, int startX, int rowY, int[] columns) {
            int rowHeight = PillarIdsConfigTab.this.getRowHeight();
            int columnGap = columns[7];
            int padding = BuildScapeConfigScreen.scaleSize(8);
            int editBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
            Minecraft mc = Minecraft.getInstance();
            int fontHeight = mc.font.lineHeight;

            int totalRowWidth = 0;
            for (int i = 0; i < 7; i++) {
                totalRowWidth += columns[i];
            }
            totalRowWidth += columnGap * 5;

            int rowCenterY = rowY + rowHeight / 2;


            int checkboxSize = BuildScapeConfigScreen.scaleSize(18);
            int swatchSize = BuildScapeConfigScreen.scaleSize(20);

            int alignmentCenterY = rowCenterY;

            int checkboxCenterY = alignmentCenterY - checkboxSize / 2;

            int swatchCenterY = alignmentCenterY - swatchSize / 2;

            int centerY = alignmentCenterY - editBoxHeight / 2;

            int textY = rowY + (rowHeight - fontHeight) / 2 + 1;

            int x = startX;

            x += columns[0];

            WidgetLayoutHelper.setPosition(selectCheckbox, x + (columns[1] - checkboxSize) / 2, checkboxCenterY);
            selectCheckbox.setWidth(checkboxSize);
            WidgetLayoutHelper.setWidgetHeight(selectCheckbox, checkboxSize);
            Services.PLATFORM.renderWidget(poseStackOrGraphics, selectCheckbox, 0, 0, 0);
            x += columns[1] + columnGap;

            drawCell(poseStackOrGraphics, x, rowY, columns[2], rowHeight, false);
            WidgetLayoutHelper.setPosition(idField, x + padding, centerY);
            idField.setWidth(columns[2] - padding * 2);
            WidgetLayoutHelper.setWidgetHeight(idField, editBoxHeight);
            String idValue = idField.getValue();
            idField.setValue("");
            Services.PLATFORM.renderWidget(poseStackOrGraphics, idField, 0, 0, 0);
            idField.setValue(idValue);
            int maxIdWidth = columns[2] - padding * 2 - BuildScapeConfigScreen.scaleSize(4);
            int idTextWidth = mc.font.width(idValue);
            int idTextX = x + padding + BuildScapeConfigScreen.scaleSize(2);

            if (idTextWidth > maxIdWidth) {
                Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
                float scale = (float) maxIdWidth / (float) idTextWidth;
                Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
                float scaledX = idTextX / scale;
                float scaledY = textY / scale;
                Services.PLATFORM.draw(poseStackOrGraphics, mc.font, idValue, scaledX, scaledY, 0xFFFFFFFF);
                Services.PLATFORM.popGuiPose(poseStackOrGraphics);
            } else {
                Services.PLATFORM.draw(poseStackOrGraphics, mc.font, idValue, idTextX, textY, 0xFFFFFFFF);
            }
            x += columns[2] + columnGap;

            drawCell(poseStackOrGraphics, x, rowY, columns[3], rowHeight, false);

            int iconSize = 16;
            boolean hasType = pillarTypeStack != null && !pillarTypeStack.isEmpty();
            boolean hasItem = displayedItem != null && !displayedItem.isEmpty();

            if (hasType || hasItem) {
                int itemGap = BuildScapeConfigScreen.scaleSize(3);

                int itemCount = (hasType ? 1 : 0) + (hasItem ? 1 : 0);
                int totalItemsWidth = itemCount * iconSize + (itemCount > 1 ? itemGap : 0);

                int availableItemWidth = columns[3];
                int itemStartX = x + (availableItemWidth - totalItemsWidth) / 2 - 1;

                itemStartX = Math.max(x + 2, Math.min(itemStartX, x + availableItemWidth - totalItemsWidth - 2));

                int renderX = itemStartX;
                int renderY = rowY + (rowHeight - iconSize) / 2;

                if (hasType) {
                    Services.PLATFORM.renderGuiItem(poseStackOrGraphics, pillarTypeStack, renderX, renderY);
                    if (hasItem) {
                        renderX += iconSize + itemGap;
                    }
                }

                if (hasItem) {
                    Services.PLATFORM.renderGuiItem(poseStackOrGraphics, displayedItem, renderX, renderY);
                    Services.PLATFORM.renderGuiItemDecorations(poseStackOrGraphics, mc.font, displayedItem, renderX, renderY);
                }
            }

            x += columns[3] + columnGap;

            drawCell(poseStackOrGraphics, x, rowY, columns[4], rowHeight, false);
            int swatchSpacing = BuildScapeConfigScreen.scaleSize(2);
            int availableColorWidth = columns[4] - padding * 2;
            int numSwatches = colorSwatches.size();

            if (numSwatches > 0) {
                int maxSwatchSize = (availableColorWidth - (numSwatches - 1) * swatchSpacing) / numSwatches;
                int finalSwatchSize = Math.max(8, Math.min(maxSwatchSize, rowHeight - padding * 2));

                int totalSwatchWidth = numSwatches * finalSwatchSize + (numSwatches - 1) * swatchSpacing;
                if (totalSwatchWidth > availableColorWidth) {
                    finalSwatchSize = (availableColorWidth - (numSwatches - 1) * swatchSpacing) / numSwatches;
                }

                int swatchStartX = x + padding;
                int swatchY = rowY + (rowHeight - finalSwatchSize) / 2;

                for (int i = 0; i < colorSwatches.size(); i++) {
                    ColorSwatchButton swatch = colorSwatches.get(i);
                    swatch.setPosition(swatchStartX + i * (finalSwatchSize + swatchSpacing), swatchY);
                    swatch.setWidth(finalSwatchSize);
                    swatch.setHeight(finalSwatchSize);
                    swatch.render(poseStackOrGraphics, 0, 0, 0);
                }
            }
            x += columns[4] + columnGap;

            drawCell(poseStackOrGraphics, x, rowY, columns[5], rowHeight, false);
            String originalDimension = dimensionField.getValue();
            String displayDimension = formatDimensionName(originalDimension);

            int dimensionWidth = columns[5] - padding * 2;
            WidgetLayoutHelper.setPosition(dimensionField, x + padding, centerY);
            dimensionField.setWidth(dimensionWidth);
            WidgetLayoutHelper.setWidgetHeight(dimensionField, editBoxHeight);

            String tempValue = dimensionField.getValue();
            dimensionField.setValue("");
            Services.PLATFORM.renderWidget(poseStackOrGraphics, dimensionField, 0, 0, 0);
            dimensionField.setValue(tempValue);

            int maxDimWidth = dimensionWidth - BuildScapeConfigScreen.scaleSize(4);
            int dimTextWidth = mc.font.width(displayDimension);
            int dimTextX = x + padding + BuildScapeConfigScreen.scaleSize(2);

            if (dimTextWidth > maxDimWidth) {
                Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
                float scale = (float) maxDimWidth / (float) dimTextWidth;
                Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
                Services.PLATFORM.draw(poseStackOrGraphics, mc.font, displayDimension, dimTextX / scale, textY / scale, 0xFFFFFFFF);
                Services.PLATFORM.popGuiPose(poseStackOrGraphics);
            } else {
                Services.PLATFORM.draw(poseStackOrGraphics, mc.font, displayDimension, dimTextX, textY, 0xFFFFFFFF);
            }
            x += columns[5] + columnGap;


            int coordPadding = BuildScapeConfigScreen.scaleSize(4);
            int coordGap = BuildScapeConfigScreen.scaleSize(4);
            int coordUsable = columns[6] - coordPadding * 2 - coordGap * 2;

            int minCoordWidth = BuildScapeConfigScreen.scaleSize(28);
            int coordWidth = Math.max(minCoordWidth, coordUsable / 3);

            int totalNeeded = coordPadding * 2 + coordWidth * 3 + coordGap * 2;
            if (totalNeeded > columns[6]) {
                coordGap = Math.max(BuildScapeConfigScreen.scaleSize(4),
                        (columns[6] - coordPadding * 2 - coordWidth * 3) / 2);
            }

            int xFieldX = x + coordPadding;
            int yFieldX = x + coordPadding + coordWidth + coordGap;
            int zFieldX = x + coordPadding + (coordWidth + coordGap) * 2;

            int coordBorderColor = 0xFF000000;
            int coordBorderThickness = Math.max(1, BuildScapeConfigScreen.scaleSize(1));
            int coordBorderPadding = 2;
            int coordGroupX = x + coordPadding - coordBorderPadding;
            int coordGroupY = centerY - coordBorderPadding;
            int coordGroupWidth = (coordWidth + coordGap) * 2 + coordWidth + coordBorderPadding * 2;
            int coordGroupHeight = editBoxHeight + coordBorderPadding * 2;

            Services.PLATFORM.fill(poseStackOrGraphics, coordGroupX, coordGroupY, coordGroupX + coordGroupWidth,
                    coordGroupY + coordBorderThickness, coordBorderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, coordGroupX, coordGroupY + coordGroupHeight - coordBorderThickness,
                    coordGroupX + coordGroupWidth, coordGroupY + coordGroupHeight, coordBorderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, coordGroupX, coordGroupY, coordGroupX + coordBorderThickness,
                    coordGroupY + coordGroupHeight, coordBorderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, coordGroupX + coordGroupWidth - coordBorderThickness, coordGroupY,
                    coordGroupX + coordGroupWidth, coordGroupY + coordGroupHeight, coordBorderColor);

            int divider1X = x + coordPadding + coordWidth + coordGap / 2 - coordBorderThickness / 2;
            int divider2X = x + coordPadding + coordWidth + coordGap + coordWidth + coordGap / 2
                    - coordBorderThickness / 2;
            int dividerTop = coordGroupY + coordBorderThickness;
            int dividerBottom = coordGroupY + coordGroupHeight - coordBorderThickness;
            Services.PLATFORM.fill(poseStackOrGraphics, divider1X, dividerTop, divider1X + coordBorderThickness, dividerBottom,
                    coordBorderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, divider2X, dividerTop, divider2X + coordBorderThickness, dividerBottom,
                    coordBorderColor);

            WidgetLayoutHelper.setPosition(xField, xFieldX, centerY);
            xField.setWidth(coordWidth);
            WidgetLayoutHelper.setWidgetHeight(xField, editBoxHeight);

            WidgetLayoutHelper.setPosition(yField, yFieldX, centerY);
            yField.setWidth(coordWidth);
            WidgetLayoutHelper.setWidgetHeight(yField, editBoxHeight);

            WidgetLayoutHelper.setPosition(zField, zFieldX, centerY);
            zField.setWidth(coordWidth);
            WidgetLayoutHelper.setWidgetHeight(zField, editBoxHeight);

            String xValue = xField.getValue();
            String yValue = yField.getValue();
            String zValue = zField.getValue();

            float baseCoordScale = 0.75f;
            int maxCoordTextWidth = coordWidth - BuildScapeConfigScreen.scaleSize(2);

            drawCoordinateValue(poseStackOrGraphics, mc, xValue, xFieldX, coordWidth, textY,
                    baseCoordScale, maxCoordTextWidth, 0xFFFF0000);
            drawCoordinateValue(poseStackOrGraphics, mc, yValue, yFieldX, coordWidth, textY,
                    baseCoordScale, maxCoordTextWidth, 0xFF5555FF);
            drawCoordinateValue(poseStackOrGraphics, mc, zValue, zFieldX, coordWidth, textY,
                    baseCoordScale, maxCoordTextWidth, 0xFF00FF00);
        }

        private void drawCoordinateValue(Object context, Minecraft mc, String value, int fieldX,
                                         int fieldWidth, int textY, float baseScale,
                                         int maxTextWidth, int color) {
            int textWidth = mc.font.width(value);
            float scale = baseScale;
            int scaledWidth = (int) (textWidth * scale);
            if (scaledWidth > maxTextWidth && scaledWidth > 0) {
                scale *= (float) maxTextWidth / scaledWidth;
                scaledWidth = (int) (textWidth * scale);
            }
            int textX = fieldX + (fieldWidth - scaledWidth) / 2;
            Services.PLATFORM.pushGuiPose(context);
            Services.PLATFORM.scaleGuiPose(context, scale, scale);
            Services.PLATFORM.draw(context, mc.font, value, textX / scale, textY / scale, color);
            Services.PLATFORM.popGuiPose(context);
        }

        private String formatDimensionName(String dimension) {
            if (dimension == null || dimension.isEmpty()) {
                return "Overworld";
            }

            String dimName = dimension;

            if (dimName.contains(":")) {
                int colonIndex = dimName.lastIndexOf(":");
                if (colonIndex >= 0 && colonIndex < dimName.length() - 1) {
                    dimName = dimName.substring(colonIndex + 1);
                }
            }

            dimName = dimName.replace("_", " ");

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
            if (!isMouseOverRow(mouseX, mouseY)) {
                return false;
            }

            int scrollbarWidth = BuildScapeConfigScreen.scaleSize(8);
            int scrollbarPadding = BuildScapeConfigScreen.scaleSize(4);
            int contentEndX = rowX + rowWidth - scrollbarWidth - scrollbarPadding;

            if (mouseX >= contentEndX) {
                return false;
            }

            if (selectCheckbox != null && Services.PLATFORM.widgetMouseClicked(selectCheckbox, mouseX, mouseY, button)) {
                return true;
            }

            if (button == 0) {
                int idX = WidgetLayoutHelper.getX(idField);
                int idY = WidgetLayoutHelper.getY(idField);
                int idW = idField.getWidth();
                int idH = WidgetLayoutHelper.getHeight(idField);
                if (mouseX >= idX && mouseX < idX + idW &&
                        mouseY >= idY && mouseY < idY + idH) {
                    openPillarDetailTab(id);
                    return true;
                }
            }

            boolean clickedOnField = false;
            for (ColorSwatchButton swatch : colorSwatches) {
                clickedOnField |= swatch.mouseClicked(mouseX, mouseY, button);
            }
            clickedOnField |= Services.PLATFORM.widgetMouseClicked(dimensionField, mouseX, mouseY, button);
            clickedOnField |= Services.PLATFORM.widgetMouseClicked(xField, mouseX, mouseY, button);
            clickedOnField |= Services.PLATFORM.widgetMouseClicked(yField, mouseX, mouseY, button);
            clickedOnField |= Services.PLATFORM.widgetMouseClicked(zField, mouseX, mouseY, button);

            if (clickedOnField) {
                return true;
            }

            int[] columns = PillarIdsConfigTab.this.computeColumns(rowWidth);
            int columnGap = columns[7];

            int checkboxEndX = rowX + columns[0] + columns[1];
            int idStartX = checkboxEndX + columnGap;
            int idEndX = idStartX + columns[2];
            int itemStartX = idEndX + columnGap;
            int itemEndX = itemStartX + columns[3];
            int colorStartX = itemEndX + columnGap;
            int colorEndX = colorStartX + columns[4];
            int dimStartX = colorEndX + columnGap;
            int dimEndX = dimStartX + columns[5];
            int coordsStartX = dimEndX + columnGap;
            int coordsEndX = coordsStartX + columns[6];

            boolean clickedOnValidColumn =
                    (mouseX >= idStartX && mouseX < idEndX) ||
                            (mouseX >= itemStartX && mouseX < itemEndX) ||
                            (mouseX >= colorStartX && mouseX < colorEndX) ||
                            (mouseX >= dimStartX && mouseX < dimEndX) ||
                            (mouseX >= coordsStartX && mouseX < coordsEndX);

            if (!clickedOnValidColumn) {
                return false;
            }

            if (mouseX >= idStartX && mouseX < idEndX && button == 0) {
                openPillarDetailTab(id);
                return true;
            }

            if (button == 0) {
                long currentTime = System.currentTimeMillis();

                if (lastClickTime > 0 &&
                        currentTime - lastClickTime < DOUBLE_CLICK_TIME_MS &&
                        lastClickX >= 0 && lastClickY >= 0 &&
                        Math.abs(mouseX - lastClickX) < DOUBLE_CLICK_DISTANCE &&
                        Math.abs(mouseY - lastClickY) < DOUBLE_CLICK_DISTANCE) {
                    handleDoubleClick();
                    lastClickTime = 0;
                    lastClickX = -1;
                    lastClickY = -1;
                    return true;
                }

                lastClickTime = currentTime;
                lastClickX = mouseX;
                lastClickY = mouseY;
            }

            return true;
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

            PillarIdManager.PillarData data = toData(new ArrayList<>());
            if (data == null) {
                return;
            }

            BlockPos pillarPos = new BlockPos(data.x, data.y, data.z);
            String dimension = data.dimension;

            String currentDimension = Services.PLATFORM.getDimensionId(level).toString();
            if (!dimension.equals(currentDimension)) {
                return;
            }

            PillarMarkerManager.get().markPillar(id, pillarPos, dimension);

            double dx = pillarPos.getX() - player.getX();
            double dz = pillarPos.getZ() - player.getZ();
            float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;

            player.setYRot(yaw);
            player.setXRot(0.0f);
            player.yRotO = yaw;
            player.xRotO = 0.0f;

            if (parent != null) {
                Services.PLATFORM.openScreen(null);
            }
        }

        private boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return Services.PLATFORM.widgetKeyPressed(dimensionField, keyCode, scanCode, modifiers)
                    || Services.PLATFORM.widgetKeyPressed(xField, keyCode, scanCode, modifiers)
                    || Services.PLATFORM.widgetKeyPressed(yField, keyCode, scanCode, modifiers)
                    || Services.PLATFORM.widgetKeyPressed(zField, keyCode, scanCode, modifiers);
        }

        private boolean charTyped(char codePoint, int modifiers) {
            return Services.PLATFORM.widgetCharTyped(dimensionField, codePoint, modifiers)
                    || Services.PLATFORM.widgetCharTyped(xField, codePoint, modifiers)
                    || Services.PLATFORM.widgetCharTyped(yField, codePoint, modifiers)
                    || Services.PLATFORM.widgetCharTyped(zField, codePoint, modifiers);
        }
    }

    private void spawnPillarMarkerParticles(Level level, BlockPos pos) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        java.util.concurrent.ThreadLocalRandom random = java.util.concurrent.ThreadLocalRandom.current();
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;


        for (int i = 0; i < 20; i++) {
            double x = centerX - 0.5 + random.nextDouble();
            double y = pos.getY() + 0.1;
            double z = centerZ - 0.5 + random.nextDouble();
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        for (int i = 0; i < 20; i++) {
            double x = centerX - 0.5 + random.nextDouble();
            double y = pos.getY() + 0.9;
            double z = centerZ - 0.5 + random.nextDouble();
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }

        for (int i = 0; i < 15; i++) {
            double y = pos.getY() + 0.1 + (random.nextDouble() * 0.8);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, y, centerZ - 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, y, centerZ - 0.5, 0, 0, 0);
        }

        for (int i = 0; i < 15; i++) {
            double y = pos.getY() + 0.1 + (random.nextDouble() * 0.8);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, y, centerZ + 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, y, centerZ + 0.5, 0, 0, 0);
        }

        for (int i = 0; i < 10; i++) {
            double x = centerX - 0.5 + (random.nextDouble());
            double z = centerZ - 0.5 + (random.nextDouble());
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.1, centerZ - 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.1, centerZ + 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, pos.getY() + 0.1, z, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, pos.getY() + 0.1, z, 0, 0, 0);
        }

        for (int i = 0; i < 10; i++) {
            double x = centerX - 0.5 + (random.nextDouble());
            double z = centerZ - 0.5 + (random.nextDouble());
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.9, centerZ - 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.9, centerZ + 0.5, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX - 0.5, pos.getY() + 0.9, z, 0, 0, 0);
            level.addParticle(ParticleTypes.END_ROD, centerX + 0.5, pos.getY() + 0.9, z, 0, 0, 0);
        }

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

    private void renderDimensionIcon(Object poseStackOrGraphics, String dimension, int x, int y, int size) {
        int color = 0xFFFFFFFF;

        if (dimension != null) {
            if (dimension.contains("overworld") || dimension.contains("minecraft:overworld")) {
                color = 0xFF00FF00;
            } else if (dimension.contains("nether") || dimension.contains("minecraft:the_nether")) {
                color = 0xFFFF0000;
            } else if (dimension.contains("end") || dimension.contains("minecraft:the_end")) {
                color = 0xFF8000FF;
            }
        }

        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + size, y + size, color);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + size, y + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + size - 1, x + size, y + size, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + size, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, x + size - 1, y, x + size, y + size, 0xFF000000);
    }

    @Override
    public String getTabName() {
        return "PillarIds";
    }
}
