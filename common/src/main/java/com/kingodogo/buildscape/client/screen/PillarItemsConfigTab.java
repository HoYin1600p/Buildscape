package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.*;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.config.PresetsConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PillarItemsConfigTab extends AbstractConfigTab {
    private static final int EXISTING_ITEMS_HEIGHT = 150;

    private EditBox searchBox;
    private EditBox tagsSearchBox;
    private ItemSelectionWidget itemSelectionWidget;
    private ExistingItemsWidget existingItemsWidget;
    private PresetsWidget presetsWidget;
    private TagsSelectorWidget tagsSelectorWidget;
    private SortToggleButton inventoryButton;
    private SortToggleButton allItemsButton;
    private SortToggleButton modOnlyButton;
    private SortToggleButton tagsInventoryButton;
    private SortToggleButton tagsAllButton;
    private SortToggleButton tagsModOnlyButton;
    private ScaledTextButton presetCreateButton;
    private List<String> existingItems;
    private List<String> availableModNamespaces;
    private int currentModIndex = 0;

    public PillarItemsConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    @Override
    public void init() {
        int screenWidth = parent.width;
        int screenHeight = parent.height;

        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        int leftX = parent.getContentX();
        int leftPanelWidth = parent.getContentWidth();
        int rightX = parent.getRightPanelX();
        int rightPanelWidth = parent.getRightPanelWidth();

        int topGap = parent.getContentY();
        int availableHeight = parent.getContentHeight();
        int middleGap = parent.getVerticalPanelGap();

        int topSectionHeight = (availableHeight - middleGap) / 2;
        int bottomSectionHeight = availableHeight - middleGap - topSectionHeight;

        int topY = topGap;
        int middleGapY = topY + topSectionHeight;
        int bottomY = middleGapY + middleGap;

        int internalPaddingY = (int) (screenHeight * 0.005) + 2;

        refreshExistingItems();
        int defaultExistingItemsX = leftX;
        int defaultExistingItemsY = topY;
        int defaultExistingItemsW = leftPanelWidth;
        int defaultExistingItemsH = topSectionHeight;
        existingItemsWidget = new ExistingItemsWidget(
                defaultExistingItemsX, defaultExistingItemsY,
                defaultExistingItemsW, defaultExistingItemsH,
                existingItems,
                this::removeItem,
                this::isItemInConfig);

        int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
        int headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
        existingItemsWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(existingItemsWidget);

        loadAvailableModNamespaces();

        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);

        int buttonsEndX = leftX + leftPanelWidth;
        int buttonsStartX = buttonsEndX - totalButtonsWidth;

        int buttonY = bottomY + internalPaddingY;

        inventoryButton = new SortToggleButton(
                buttonsStartX, buttonY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.INVENTORY,
                (type, ctrl) -> onSortModeChanged(type, ctrl));
        inventoryButton.setTooltip(Arrays.asList(
                ComponentHelper.literal("Filter By Inventory"),
                ComponentHelper.literal("Show items only from your inventory").withStyle(ChatFormatting.GRAY)
        ));
        addTabWidget(inventoryButton);

        allItemsButton = new SortToggleButton(
                buttonsStartX + buttonSize + buttonSpacing, buttonY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.ALL_ITEMS,
                (type, ctrl) -> onSortModeChanged(type, ctrl));
        allItemsButton.setSelected(true);
        allItemsButton.setTooltip(Arrays.asList(
                ComponentHelper.literal("Filter By All Items"),
                ComponentHelper.literal("Show all available items").withStyle(ChatFormatting.GRAY)
        ));
        addTabWidget(allItemsButton);

        modOnlyButton = new SortToggleButton(
                buttonsStartX + (buttonSize + buttonSpacing) * 2, buttonY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.MOD_ONLY,
                (type, ctrl) -> onSortModeChanged(type, ctrl));
        modOnlyButton.setTooltip(Arrays.asList(
                ComponentHelper.literal("Filter By Mod"),
                ComponentHelper.literal("Click to cycle next mod").withStyle(ChatFormatting.GRAY),
                ComponentHelper.literal("Ctrl Click to cycle Previous mod").withStyle(ChatFormatting.GRAY)
        ));
        addTabWidget(modOnlyButton);

        Minecraft mc = Minecraft.getInstance();
        Component allItemsLabel = ComponentHelper.translatable("buildscape.config.all_items");
        int allItemsLabelWidth = mc.font.width(allItemsLabel);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5);
        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        int searchBoxX = leftX + 2 + (int)(allItemsLabelWidth * textScale) + labelSpacing;
        int searchBoxEndX = buttonsStartX - labelSpacing;
        int searchBoxWidth = searchBoxEndX - searchBoxX;

        searchBox = new EditBox(
                Minecraft.getInstance().font,
                searchBoxX, buttonY,
                searchBoxWidth, buttonSize,
                ComponentHelper.translatable("buildscape.config.search"));
        searchBox.setMaxLength(256);
        searchBox.setResponder((text) -> {
            if (itemSelectionWidget != null) {
                itemSelectionWidget.setFilter(text);
            }
        });
        addTabWidget(searchBox);

        int defaultItemSelectionX = leftX;
        int defaultItemSelectionY = bottomY;
        itemSelectionWidget = new ItemSelectionWidget(
                defaultItemSelectionX, defaultItemSelectionY,
                leftPanelWidth, bottomSectionHeight,
                this::onItemSelected,
                (itemId) -> isItemInConfig(itemId) ? 1 : 0);
        itemSelectionWidget.setSortMode(SortToggleButton.SortType.ALL_ITEMS);

        headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
        itemSelectionWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(itemSelectionWidget);

        int presetsX = rightX;
        int presetsY = topY;
        int presetsWidth = rightPanelWidth;
        int presetsHeight = topSectionHeight;
        presetsWidget = new PresetsWidget(
                presetsX, presetsY,
                presetsWidth, presetsHeight,
                this::onPresetApplied);
        presetsWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(presetsWidget);

        presetCreateButton = presetsWidget.getCreateButton();

        int tagsX = rightX;
        int tagsY = bottomY;
        int tagsWidth = rightPanelWidth;

        int tagsButtonSize = BuildScapeConfigScreen.getScaledButtonHeight();
        int tagsButtonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalTagsButtonsWidth = (tagsButtonSize * 3) + (tagsButtonSpacing * 2);

        int tagsButtonsEndX = tagsX + tagsWidth;
        int tagsButtonsStartX = tagsButtonsEndX - totalTagsButtonsWidth;
        int tagsButtonY = tagsY + internalPaddingY;

        tagsInventoryButton = new SortToggleButton(
                tagsButtonsStartX, tagsButtonY,
                tagsButtonSize, tagsButtonSize,
                SortToggleButton.SortType.INVENTORY,
                (type, ctrl) -> onTagsSortModeChanged(type, ctrl));
        tagsInventoryButton.setTooltip(Arrays.asList(
                ComponentHelper.literal("Filter By Inventory"),
                ComponentHelper.literal("Show tags matching items in your inventory").withStyle(ChatFormatting.GRAY)
        ));
        addTabWidget(tagsInventoryButton);

        tagsAllButton = new SortToggleButton(
                tagsButtonsStartX + tagsButtonSize + tagsButtonSpacing, tagsButtonY,
                tagsButtonSize, tagsButtonSize,
                SortToggleButton.SortType.ALL_ITEMS,
                (type, ctrl) -> onTagsSortModeChanged(type, ctrl));
        tagsAllButton.setSelected(true);
        tagsAllButton.setTooltip(Arrays.asList(
                ComponentHelper.literal("Filter By All Items"),
                ComponentHelper.literal("Show all available tags").withStyle(ChatFormatting.GRAY)
        ));
        addTabWidget(tagsAllButton);

        tagsModOnlyButton = new SortToggleButton(
                tagsButtonsStartX + (tagsButtonSize + tagsButtonSpacing) * 2, tagsButtonY,
                tagsButtonSize, tagsButtonSize,
                SortToggleButton.SortType.MOD_ONLY,
                (type, ctrl) -> onTagsSortModeChanged(type, ctrl));
        tagsModOnlyButton.setTooltip(Arrays.asList(
                ComponentHelper.literal("Filter By Mod"),
                ComponentHelper.literal("Show tags only from specific mods").withStyle(ChatFormatting.GRAY)
        ));
        addTabWidget(tagsModOnlyButton);

        Component tagsLabel = ComponentHelper.translatable("buildscape.config.tags");
        int tagsLabelWidth = mc.font.width(tagsLabel);
        int tagsLabelSpacing = BuildScapeConfigScreen.scaleSize(5);

        int tagsSearchBoxX = tagsX + 2 + (int)(tagsLabelWidth * BuildScapeConfigScreen.getStandardTextScale()) + tagsLabelSpacing;
        int tagsSearchBoxEndX = tagsButtonsStartX - tagsLabelSpacing;
        int tagsSearchBoxWidth = tagsSearchBoxEndX - tagsSearchBoxX;

        tagsSearchBox = new EditBox(
                Minecraft.getInstance().font,
                tagsSearchBoxX, tagsButtonY,
                tagsSearchBoxWidth, tagsButtonSize,
                ComponentHelper.translatable("buildscape.config.search_tags"));
        tagsSearchBox.setMaxLength(256);
        tagsSearchBox.setResponder((text) -> {
            if (tagsSelectorWidget != null) {
                tagsSelectorWidget.setFilter(text);
            }
        });
        addTabWidget(tagsSearchBox);

        int tagsWidgetHeight = bottomSectionHeight;

        tagsSelectorWidget = new TagsSelectorWidget(
                tagsX, tagsY,
                tagsWidth, tagsWidgetHeight,
                this::onTagSelected);
        tagsSelectorWidget.setSortType(TagsSelectorWidget.SortType.ALL_ITEMS);

        tagsSelectorWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(tagsSelectorWidget);

        if (itemSelectionWidget != null) {
            searchBox.setResponder((text) -> itemSelectionWidget.setFilter(text));
        }
        if (tagsSelectorWidget != null) {
            tagsSearchBox.setResponder((text) -> tagsSelectorWidget.setFilter(text));
        }

        updateChildComponentPositions();
        updateSelectedTags();

        PresetsConfig presetsConfig = PresetsConfig.get();
        presetsConfig.autoApplyOnLoad();
        refreshExistingItems();
        if (presetsWidget != null) {
            String appliedKey = presetsConfig.getLastAppliedPreset();
            if (presetsConfig.hasUnnamedPreset()) {
                appliedKey = "_unnamed";
            }
            presetsWidget.setSelectedPreset(appliedKey != null ? appliedKey : "default");
        }
    }

    private void updateChildComponentPositions() {
        int screenWidth = parent.width;
        int screenHeight = parent.height;

        int leftX = parent.getContentX();
        int leftPanelWidth = parent.getContentWidth();
        int rightX = parent.getRightPanelX();
        int rightPanelWidth = parent.getRightPanelWidth();

        int topGap = parent.getContentY();
        int availableHeight = parent.getContentHeight();
        int middleGap = parent.getVerticalPanelGap();

        int internalPaddingY = (int) (screenHeight * 0.005) + 2;

        int topSectionHeight = (availableHeight - middleGap) / 2;
        int bottomSectionHeight = availableHeight - middleGap - topSectionHeight;

        int topY = topGap;
        int middleGapY = topY + topSectionHeight;
        int bottomY = middleGapY + middleGap;

        if (searchBox != null) {
            int searchBoxY = bottomY + internalPaddingY;
            WidgetLayoutHelper.setY(searchBox, searchBoxY);
        }

        if (inventoryButton != null && allItemsButton != null && modOnlyButton != null) {
            int buttonY = bottomY + internalPaddingY;
            inventoryButton.y = buttonY;
            allItemsButton.y = buttonY;
            modOnlyButton.y = buttonY;
        }

        if (itemSelectionWidget != null && searchBox != null) {
            itemSelectionWidget.x = leftX;
            itemSelectionWidget.y = bottomY;
            itemSelectionWidget.setWidth(leftPanelWidth);

            int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
            int headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
            itemSelectionWidget.setHeaderAreaHeight(headerHeight);

            int itemSelectionWidgetHeight = bottomSectionHeight;
            itemSelectionWidget.setHeight(itemSelectionWidgetHeight);
        }

        int tagsX = rightX;
        int tagsY = bottomY;

        if (tagsSearchBox != null) {
            WidgetLayoutHelper.setY(tagsSearchBox, tagsY + internalPaddingY);
        }

        if (tagsInventoryButton != null) {
            tagsInventoryButton.y = tagsSearchBox != null ? WidgetLayoutHelper.getY(tagsSearchBox) : tagsY + internalPaddingY;
            tagsAllButton.y = tagsInventoryButton.y;
            tagsModOnlyButton.y = tagsInventoryButton.y;
        }

        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            int tagsWidgetHeight = bottomSectionHeight;
            tagsSelectorWidget.x = tagsX;
            tagsSelectorWidget.y = tagsY;
            tagsSelectorWidget.setWidth(rightPanelWidth);
            tagsSelectorWidget.setHeight(tagsWidgetHeight);

            int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
            int headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
            tagsSelectorWidget.setHeaderAreaHeight(headerHeight);
        }

        if (presetsWidget != null) {
            presetsWidget.updateChildPositions();
        }

        updateSearchBoxForLabel();
        updateTagsSearchBoxForLabel();
    }

    private void onPresetApplied(String presetKey) {
        refreshExistingItems();
        if (itemSelectionWidget != null) {
            itemSelectionWidget.refresh();
        }
        updateSelectedTags();

        if (!presetKey.equals("_unnamed")) {
            PresetsConfig.get().clearUnnamedPreset();
        }
    }

    private void onTagSelected(String tagId) {
        PillarParticleConfig config = PillarParticleConfig.get();
        if (config.items.contains(tagId)) {
            config.removeItem(tagId);
        } else {
            config.addItem(tagId);
        }
        saveToUnnamedPreset();
        refreshExistingItems();
        updateSelectedTags();
    }

    private void updateSelectedTags() {
        PillarParticleConfig config = PillarParticleConfig.get();
        Set<String> selectedTags = new HashSet<>();
        for (String itemId : config.items) {
            if (itemId.startsWith("#")) {
                selectedTags.add(itemId);
            }
        }
        if (tagsSelectorWidget != null) {
            tagsSelectorWidget.setSelectedTags(selectedTags);
        }
    }

    private void loadAvailableModNamespaces() {
        Set<String> namespaces = new HashSet<>();
        for (Item item : Services.PLATFORM.getAllItems()) {
            CommonId itemId = Services.PLATFORM.getItemId(item);
            if (itemId != null && !itemId.getNamespace().equals("minecraft")) {
                namespaces.add(itemId.getNamespace());
            }
        }
        availableModNamespaces = new ArrayList<>(namespaces);
        availableModNamespaces.sort(String::compareTo);
        if (availableModNamespaces.contains("buildscape")) {
            currentModIndex = availableModNamespaces.indexOf("buildscape");
        }
    }

    private void onSortModeChanged(SortToggleButton.SortType type, boolean isCtrlDown) {
        inventoryButton.setSelected(false);
        allItemsButton.setSelected(false);
        modOnlyButton.setSelected(false);

        switch (type) {
            case INVENTORY:
                inventoryButton.setSelected(true);
                break;
            case ALL_ITEMS:
                allItemsButton.setSelected(true);
                break;
            case MOD_ONLY:
                modOnlyButton.setSelected(true);
                if (itemSelectionWidget != null &&
                        itemSelectionWidget.getSortMode() == SortToggleButton.SortType.MOD_ONLY) {
                    if (isCtrlDown) {
                        cycleToPreviousMod();
                    } else {
                        cycleToNextMod();
                    }
                } else {
                    if (!availableModNamespaces.isEmpty()) {
                        if (availableModNamespaces.contains("buildscape")) {
                            currentModIndex = availableModNamespaces.indexOf("buildscape");
                        } else {
                            currentModIndex = 0;
                        }
                        String modNamespace = availableModNamespaces.get(currentModIndex);
                        if (itemSelectionWidget != null) {
                            itemSelectionWidget.setModNamespace(modNamespace);
                        }
                    }
                }
                break;
        }

        if (itemSelectionWidget != null) {
            itemSelectionWidget.setSortMode(type);
        }

        updateSearchBoxForLabel();
    }

    private void updateSearchBoxForLabel() {
        if (itemSelectionWidget == null || searchBox == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        int leftX = parent.getContentX();
        int leftPanelWidth = parent.getContentWidth();
        int leftPadding = BuildScapeConfigScreen.scaleSize(5);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5);

        String labelKey = "buildscape.config.filtered_items";
        Component labelText = null;

        SortToggleButton.SortType sortMode = itemSelectionWidget.getSortMode();
        switch (sortMode) {
            case INVENTORY:
                labelKey = "buildscape.config.inventory_items";
                break;
            case ALL_ITEMS:
                labelKey = "buildscape.config.all_items";
                break;
            case MOD_ONLY:
                String modName = itemSelectionWidget.getCurrentModNamespace();
                if (modName != null && !modName.isEmpty()) {
                    modName = modName.substring(0, 1).toUpperCase() + modName.substring(1);
                }
                labelText = ComponentHelper.translatable("buildscape.config.mod_items", modName);
                break;
        }

        if (labelText == null) {
            labelText = ComponentHelper.translatable(labelKey);
        }

        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int labelWidth = (int) (mc.font.width(labelText) * textScale);
        int minSearchBoxWidth = BuildScapeConfigScreen.scaleSize(80);

        int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);
        int buttonsEndX = leftX + leftPanelWidth;
        int buttonsStartX = buttonsEndX - totalButtonsWidth;

        int searchBoxX = leftX + leftPadding + labelWidth + labelSpacing;
        int finalSearchBoxWidth = buttonsStartX - searchBoxX - labelSpacing;

        WidgetLayoutHelper.setX(searchBox, searchBoxX);
        searchBox.setWidth(Math.max(minSearchBoxWidth, finalSearchBoxWidth));

        if (inventoryButton != null && allItemsButton != null && modOnlyButton != null && searchBox != null) {
            int searchBoxYPos = WidgetLayoutHelper.getY(searchBox);
            inventoryButton.x = buttonsStartX;
            inventoryButton.y = searchBoxYPos;
            inventoryButton.setWidth(buttonSize);
            inventoryButton.setHeight(buttonSize);

            allItemsButton.x = buttonsStartX + buttonSize + buttonSpacing;
            allItemsButton.y = searchBoxYPos;
            allItemsButton.setWidth(buttonSize);
            allItemsButton.setHeight(buttonSize);

            modOnlyButton.x = buttonsStartX + (buttonSize + buttonSpacing) * 2;
            modOnlyButton.y = searchBoxYPos;
            modOnlyButton.setWidth(buttonSize);
            modOnlyButton.setHeight(buttonSize);
        }
    }

    private void updateTagsSearchBoxForLabel() {
        if (tagsSelectorWidget == null || tagsSearchBox == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        int rightX = parent.getRightPanelX();
        int rightPanelWidth = parent.getRightPanelWidth();

        int leftPadding = BuildScapeConfigScreen.scaleSize(5);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5);

        Component labelText;
        TagsSelectorWidget.SortType sortType = tagsSelectorWidget.getSortType();
        if (sortType == TagsSelectorWidget.SortType.MOD_ONLY) {
            labelText = ComponentHelper.literal("Buildscape Tags");
        } else if (sortType == TagsSelectorWidget.SortType.INVENTORY) {
            labelText = ComponentHelper.literal("Inventory Tags");
        } else {
            labelText = ComponentHelper.literal("All Tags");
        }

        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int labelWidth = (int) (mc.font.width(labelText) * textScale);
        int minSearchBoxWidth = BuildScapeConfigScreen.scaleSize(50);

        int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);
        int buttonsEndX = rightX + rightPanelWidth;
        int buttonsStartX = buttonsEndX - totalButtonsWidth;

        int searchBoxX = rightX + leftPadding + labelWidth + labelSpacing;
        int finalSearchBoxWidth = buttonsStartX - searchBoxX - labelSpacing;

        WidgetLayoutHelper.setX(tagsSearchBox, searchBoxX);
        tagsSearchBox.setWidth(Math.max(minSearchBoxWidth, finalSearchBoxWidth));

        if (tagsInventoryButton != null && tagsAllButton != null && tagsModOnlyButton != null) {
            tagsInventoryButton.x = buttonsStartX;
            tagsInventoryButton.setWidth(buttonSize);
            tagsInventoryButton.setHeight(buttonSize);

            tagsAllButton.x = buttonsStartX + buttonSize + buttonSpacing;
            tagsAllButton.setWidth(buttonSize);
            tagsAllButton.setHeight(buttonSize);

            tagsModOnlyButton.x = buttonsStartX + (buttonSize + buttonSpacing) * 2;
            tagsModOnlyButton.setWidth(buttonSize);
            tagsModOnlyButton.setHeight(buttonSize);
        }
    }

    private void onTagsSortModeChanged(SortToggleButton.SortType type, boolean isCtrlDown) {
        tagsInventoryButton.setSelected(false);
        tagsAllButton.setSelected(false);
        tagsModOnlyButton.setSelected(false);

        switch (type) {
            case INVENTORY:
                tagsInventoryButton.setSelected(true);
                break;
            case ALL_ITEMS:
                tagsAllButton.setSelected(true);
                break;
            case MOD_ONLY:
                tagsModOnlyButton.setSelected(true);
                break;
        }

        if (tagsSelectorWidget != null) {
            tagsSelectorWidget.setSortType(convertSortType(type));
        }
        updateTagsSearchBoxForLabel();
    }

    private TagsSelectorWidget.SortType convertSortType(SortToggleButton.SortType type) {
        switch (type) {
            case INVENTORY:
                return TagsSelectorWidget.SortType.INVENTORY;
            case ALL_ITEMS:
                return TagsSelectorWidget.SortType.ALL_ITEMS;
            case MOD_ONLY:
                return TagsSelectorWidget.SortType.MOD_ONLY;
            default:
                return TagsSelectorWidget.SortType.ALL_ITEMS;
        }
    }

    private void cycleToNextMod() {
        if (availableModNamespaces.isEmpty())
            return;
        currentModIndex = (currentModIndex + 1) % availableModNamespaces.size();
        String modNamespace = availableModNamespaces.get(currentModIndex);
        if (itemSelectionWidget != null) {
            itemSelectionWidget.setModNamespace(modNamespace);
        }
        updateSearchBoxForLabel();
    }

    private void cycleToPreviousMod() {
        if (availableModNamespaces.isEmpty())
            return;
        currentModIndex = (currentModIndex - 1 + availableModNamespaces.size()) % availableModNamespaces.size();
        String modNamespace = availableModNamespaces.get(currentModIndex);
        if (itemSelectionWidget != null) {
            itemSelectionWidget.setModNamespace(modNamespace);
        }
        updateSearchBoxForLabel();
    }

    private void refreshExistingItems() {
        PillarParticleConfig config = PillarParticleConfig.get();
        existingItems = new ArrayList<>(config.items);
        existingItems.sort(String::compareTo);
        if (existingItemsWidget != null) {
            existingItemsWidget.setItems(existingItems);
        }
    }

    public void onItemSelected(String itemId) {
        PillarParticleConfig config = PillarParticleConfig.get();
        boolean wasInConfig = config.items.contains(itemId);

        if (wasInConfig) {
            if (config.removeItem(itemId)) {
                saveToUnnamedPreset();
                refreshExistingItems();
                if (itemSelectionWidget != null) {
                    itemSelectionWidget.refresh();
                }
            }
        } else {
            if (config.addItem(itemId)) {
                saveToUnnamedPreset();
                refreshExistingItems();
                if (itemSelectionWidget != null) {
                    itemSelectionWidget.refresh();
                }
            }
        }
    }

    private void saveToUnnamedPreset() {
        PillarParticleConfig config = PillarParticleConfig.get();
        PresetsConfig presetsConfig = PresetsConfig.get();

        if (presetsWidget != null) {
            String selectedKey = presetsWidget.getSelectedPresetKey();
            if (selectedKey != null && !selectedKey.equals("default") && !selectedKey.equals("_unnamed")) {
                presetsWidget.setSelectedPreset("_unnamed");
            } else if (selectedKey == null || selectedKey.equals("default")) {
                presetsWidget.setSelectedPreset("_unnamed");
            }
            presetsWidget.setAppliedPreset("_unnamed");
        }

        presetsConfig.saveUnnamedPreset(config.items);
        if (presetsWidget != null) {
            presetsWidget.refreshPresets();
        }
    }

    private boolean isItemInConfig(String itemId) {
        PillarParticleConfig config = PillarParticleConfig.get();
        return config.items.contains(itemId);
    }

    public void removeItem(String itemId) {
        PillarParticleConfig config = PillarParticleConfig.get();
        if (config.removeItem(itemId)) {
            saveToUnnamedPreset();
            refreshExistingItems();
            if (itemSelectionWidget != null) {
                itemSelectionWidget.refresh();
            }
        }
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (existingItemsWidget != null) {
            existingItemsWidget.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }

        if (existingItemsWidget != null) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.translateGuiPose(poseStackOrGraphics, 0, 0, 400);
            Minecraft mc = Minecraft.getInstance();
            int labelX = existingItemsWidget.x + 2;
            int labelY = existingItemsWidget.y + 2 + BuildScapeConfigScreen.getScaledButtonHeight() / 2 - mc.font.lineHeight / 2 + 1;
            Component pillarItemsLabel = ComponentHelper.translatable(
                    "buildscape.config.pillar_items");

            float textScale = BuildScapeConfigScreen.getStandardTextScale();

            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.translateGuiPose(poseStackOrGraphics, labelX, labelY, 0);
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, textScale, textScale);
            Services.PLATFORM.draw(
                    poseStackOrGraphics,
                    mc.font,
                    pillarItemsLabel.getString(),
                    0, 0,
                    0xFFFFFFFF
            );
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        }

        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, 0, 0, 200);

        Minecraft mc = Minecraft.getInstance();
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        if (itemSelectionWidget != null && searchBox != null) {
            String labelKey = "buildscape.config.filtered_items";
            Component labelText = null;
            SortToggleButton.SortType sortMode = itemSelectionWidget.getSortMode();

            switch (sortMode) {
                case INVENTORY:
                    labelKey = "buildscape.config.inventory_items";
                    break;
                case ALL_ITEMS:
                    labelKey = "buildscape.config.all_items";
                    break;
                case MOD_ONLY:
                    String modName = itemSelectionWidget.getCurrentModNamespace();
                    if (modName != null && !modName.isEmpty()) {
                        modName = modName.substring(0, 1).toUpperCase() + modName.substring(1);
                    }
                    labelText = ComponentHelper.translatable("buildscape.config.mod_items", modName);
                    break;
            }

            if (labelText == null) {
                labelText = ComponentHelper.translatable(labelKey);
            }

            int actualLabelX = itemSelectionWidget.x + 2;
            int textYOffset = (searchBoxHeight - (int)(mc.font.lineHeight * textScale)) / 2;
            renderScaledText(poseStackOrGraphics, labelText, actualLabelX, WidgetLayoutHelper.getY(searchBox) + textYOffset, textScale);
        }

        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            Component tagsLabel;

            TagsSelectorWidget.SortType sortType = tagsSelectorWidget.getSortType();
            if (sortType == TagsSelectorWidget.SortType.MOD_ONLY) {
                tagsLabel = ComponentHelper.literal("Buildscape Tags");
            } else if (sortType == TagsSelectorWidget.SortType.INVENTORY) {
                tagsLabel = ComponentHelper.literal("Inventory Tags");
            } else {
                tagsLabel = ComponentHelper.literal("All Tags");
            }

            int tagsLabelX = tagsSelectorWidget.x + 2;
            int textYOffset = (searchBoxHeight - (int)(mc.font.lineHeight * textScale)) / 2;
            renderScaledText(poseStackOrGraphics, tagsLabel, tagsLabelX, WidgetLayoutHelper.getY(tagsSearchBox) + textYOffset, textScale);
        }

        Services.PLATFORM.popGuiPose(poseStackOrGraphics);

        if (searchBox != null) {
            Services.PLATFORM.renderWidget(poseStackOrGraphics, searchBox, mouseX, mouseY, partialTick);
        }
        if (inventoryButton != null) {
            inventoryButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
        if (allItemsButton != null) {
            allItemsButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
        if (modOnlyButton != null) {
            modOnlyButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }

        if (itemSelectionWidget != null) {
            itemSelectionWidget.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }

        if (presetsWidget != null) {
            presetsWidget.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }

        if (tagsSearchBox != null) {
            Services.PLATFORM.renderWidget(poseStackOrGraphics, tagsSearchBox, mouseX, mouseY, partialTick);
        }

        if (tagsInventoryButton != null) {
            tagsInventoryButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
        if (tagsAllButton != null) {
            tagsAllButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
        if (tagsModOnlyButton != null) {
            tagsModOnlyButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }

        if (tagsSelectorWidget != null) {
            tagsSelectorWidget.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderTooltips(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (inventoryButton != null) inventoryButton.renderButtonTooltip(poseStackOrGraphics, mouseX, mouseY);
        if (allItemsButton != null) allItemsButton.renderButtonTooltip(poseStackOrGraphics, mouseX, mouseY);
        if (modOnlyButton != null) modOnlyButton.renderButtonTooltip(poseStackOrGraphics, mouseX, mouseY);

        if (tagsInventoryButton != null) tagsInventoryButton.renderButtonTooltip(poseStackOrGraphics, mouseX, mouseY);
        if (tagsAllButton != null) tagsAllButton.renderButtonTooltip(poseStackOrGraphics, mouseX, mouseY);
        if (tagsModOnlyButton != null) tagsModOnlyButton.renderButtonTooltip(poseStackOrGraphics, mouseX, mouseY);

        if (itemSelectionWidget != null) {
            itemSelectionWidget.renderTooltip(poseStackOrGraphics, mouseX, mouseY);
        }

        if (existingItemsWidget != null) {
            existingItemsWidget.renderTooltip(poseStackOrGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchBox != null && Services.PLATFORM.widgetMouseClicked(searchBox, mouseX, mouseY, button)) {
            parent.setFocused(searchBox);
            return true;
        }
        if (tagsSearchBox != null && Services.PLATFORM.widgetMouseClicked(tagsSearchBox, mouseX, mouseY, button)) {
            parent.setFocused(tagsSearchBox);
            return true;
        }
        if (tagsInventoryButton != null && tagsInventoryButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (tagsAllButton != null && tagsAllButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (tagsModOnlyButton != null && tagsModOnlyButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (inventoryButton != null && inventoryButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (allItemsButton != null && allItemsButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (modOnlyButton != null && modOnlyButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (existingItemsWidget != null && existingItemsWidget.mouseClicked(mouseX, mouseY, button)) {
            parent.setFocused(existingItemsWidget);
            return true;
        }
        if (itemSelectionWidget != null && itemSelectionWidget.mouseClicked(mouseX, mouseY, button)) {
            parent.setFocused(itemSelectionWidget);
            return true;
        }
        if (presetsWidget != null && presetsWidget.mouseClicked(mouseX, mouseY, button)) {
            parent.setFocused(presetsWidget);
            return true;
        }
        if (tagsSelectorWidget != null && tagsSelectorWidget.mouseClicked(mouseX, mouseY, button)) {
            parent.setFocused(tagsSelectorWidget);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (existingItemsWidget != null && existingItemsWidget.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        if (itemSelectionWidget != null && itemSelectionWidget.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        if (presetsWidget != null && presetsWidget.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return tagsSelectorWidget != null && tagsSelectorWidget.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (existingItemsWidget != null && existingItemsWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        if (itemSelectionWidget != null && itemSelectionWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        if (tagsSelectorWidget != null && tagsSelectorWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return presetsWidget != null && presetsWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (existingItemsWidget != null && existingItemsWidget.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (itemSelectionWidget != null && itemSelectionWidget.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (tagsSelectorWidget != null && tagsSelectorWidget.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return presetsWidget != null && presetsWidget.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox != null && Services.PLATFORM.widgetKeyPressed(searchBox, keyCode, scanCode, modifiers)) {
            return true;
        }
        if (tagsSearchBox != null && Services.PLATFORM.widgetKeyPressed(tagsSearchBox, keyCode, scanCode, modifiers)) {
            return true;
        }
        return presetsWidget != null && presetsWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBox != null && Services.PLATFORM.widgetCharTyped(searchBox, codePoint, modifiers)) {
            return true;
        }
        if (tagsSearchBox != null && Services.PLATFORM.widgetCharTyped(tagsSearchBox, codePoint, modifiers)) {
            return true;
        }
        return presetsWidget != null && presetsWidget.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        if (searchBox != null) {
            searchBox.setValue("");
            if (itemSelectionWidget != null) {
                itemSelectionWidget.setFilter("");
            }
        }

        if (tagsSearchBox != null) {
            tagsSearchBox.setValue("");
            if (tagsSelectorWidget != null) {
                tagsSelectorWidget.setFilter("");
            }
        }

        refreshExistingItems();
        super.onClose();
    }

    private void renderScaledText(Object poseStackOrGraphics, Component text, int x, int y, float scale) {
        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, x, y, 0);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
        Services.PLATFORM.drawShadow(poseStackOrGraphics, Minecraft.getInstance().font, text.getString(), 0, 0, 0xFFFFFFFF);
        Services.PLATFORM.popGuiPose(poseStackOrGraphics);
    }
}
