package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.*;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.config.PresetsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
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
    private net.minecraft.client.gui.components.Button presetCreateButton;
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

        int scaledOffset = BuildScapeConfigScreen.scaleSize(5);
        int scaledButtonArea = BuildScapeConfigScreen.scaleSize(100);
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int leftPadding = BuildScapeConfigScreen.scaleSize(5);

        net.minecraft.client.gui.components.AbstractWidget itemSelectorPanel = new net.minecraft.client.gui.components.AbstractWidget(
                leftX, bottomY, leftPanelWidth, bottomSectionHeight,
                net.minecraft.network.chat.TextComponent.EMPTY) {
            @Override
            public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY,
                    float partialTick) {
            }

            @Override
            public void updateNarration(
                    net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
            }
        };

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
        inventoryButton.setTooltip(java.util.Arrays.asList(
                new net.minecraft.network.chat.TextComponent("Filter By Inventory"),
                new net.minecraft.network.chat.TextComponent("Show items only from your inventory").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(inventoryButton);

        allItemsButton = new SortToggleButton(
                buttonsStartX + buttonSize + buttonSpacing, buttonY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.ALL_ITEMS,
                (type, ctrl) -> onSortModeChanged(type, ctrl));
        allItemsButton.setSelected(true);
        allItemsButton.setTooltip(java.util.Arrays.asList(
                new net.minecraft.network.chat.TextComponent("Filter By All Items"),
                new net.minecraft.network.chat.TextComponent("Show all available items").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(allItemsButton);

        modOnlyButton = new SortToggleButton(
                buttonsStartX + (buttonSize + buttonSpacing) * 2, buttonY,
                buttonSize, buttonSize,
                SortToggleButton.SortType.MOD_ONLY,
                (type, ctrl) -> onSortModeChanged(type, ctrl));
        modOnlyButton.setTooltip(java.util.Arrays.asList(
                new net.minecraft.network.chat.TextComponent("Filter By Mod"),
                new net.minecraft.network.chat.TextComponent("Click to cycle next mod").withStyle(net.minecraft.ChatFormatting.GRAY),
                new net.minecraft.network.chat.TextComponent("Ctrl Click to cycle Previous mod").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(modOnlyButton);

        Minecraft mc = Minecraft.getInstance();
        net.minecraft.network.chat.Component allItemsLabel = new TranslatableComponent("buildscape.config.all_items");
        int allItemsLabelWidth = mc.font.width(allItemsLabel);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5);
        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        int searchBoxX = leftX + 2 + (int)(allItemsLabelWidth * textScale) + labelSpacing;
        int searchBoxEndX = buttonsStartX - labelSpacing;
        int searchBoxWidth = searchBoxEndX - searchBoxX;

        searchBox = new EditBox(
                net.minecraft.client.Minecraft.getInstance().font,
                searchBoxX, buttonY,
                searchBoxWidth, buttonSize,
                new TranslatableComponent("buildscape.config.search"));
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
        int scaledSpacing = BuildScapeConfigScreen.scaleSize(10);
        int defaultCreateButtonX = presetsX + scaledSpacing;
        int defaultCreateButtonY = presetsY + presetsHeight - BuildScapeConfigScreen.scaleSize(35);

        int tagsX = rightX;
        int tagsY = bottomY;
        int tagsWidth = rightPanelWidth;

        net.minecraft.client.gui.components.AbstractWidget tagsPanel = new net.minecraft.client.gui.components.AbstractWidget(
                tagsX, tagsY, tagsWidth, bottomSectionHeight,
                net.minecraft.network.chat.TextComponent.EMPTY) {
            @Override
            public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY,
                    float partialTick) {
            }

            @Override
            public void updateNarration(
                    net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
            }
        };

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
        tagsInventoryButton.setTooltip(java.util.Arrays.asList(
                new net.minecraft.network.chat.TextComponent("Filter By Inventory"),
                new net.minecraft.network.chat.TextComponent("Show tags matching items in your inventory").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(tagsInventoryButton);

        tagsAllButton = new SortToggleButton(
                tagsButtonsStartX + tagsButtonSize + tagsButtonSpacing, tagsButtonY,
                tagsButtonSize, tagsButtonSize,
                SortToggleButton.SortType.ALL_ITEMS,
                (type, ctrl) -> onTagsSortModeChanged(type, ctrl));
        tagsAllButton.setSelected(true);
        tagsAllButton.setTooltip(java.util.Arrays.asList(
                new net.minecraft.network.chat.TextComponent("Filter By All Items"),
                new net.minecraft.network.chat.TextComponent("Show all available tags").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(tagsAllButton);

        tagsModOnlyButton = new SortToggleButton(
                tagsButtonsStartX + (tagsButtonSize + tagsButtonSpacing) * 2, tagsButtonY,
                tagsButtonSize, tagsButtonSize,
                SortToggleButton.SortType.MOD_ONLY,
                (type, ctrl) -> onTagsSortModeChanged(type, ctrl));
        tagsModOnlyButton.setTooltip(java.util.Arrays.asList(
                new net.minecraft.network.chat.TextComponent("Filter By Mod"),
                new net.minecraft.network.chat.TextComponent("Show tags only from specific mods").withStyle(net.minecraft.ChatFormatting.GRAY)
        ));
        addTabWidget(tagsModOnlyButton);

        net.minecraft.network.chat.Component tagsLabel = new TranslatableComponent("buildscape.config.tags");
        int tagsLabelWidth = mc.font.width(tagsLabel);
        int tagsLabelSpacing = BuildScapeConfigScreen.scaleSize(5);

        int tagsSearchBoxX = tagsX + 2 + (int)(tagsLabelWidth * BuildScapeConfigScreen.getStandardTextScale()) + tagsLabelSpacing;
        int tagsSearchBoxEndX = tagsButtonsStartX - tagsLabelSpacing;
        int tagsSearchBoxWidth = tagsSearchBoxEndX - tagsSearchBoxX;

        tagsSearchBox = new EditBox(
                net.minecraft.client.Minecraft.getInstance().font,
                tagsSearchBoxX, tagsButtonY,
                tagsSearchBoxWidth, tagsButtonSize,
                new TranslatableComponent("buildscape.config.search_tags"));
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
            searchBox.y = searchBoxY;
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
            try {
                java.lang.reflect.Method setHeightMethod = itemSelectionWidget.getClass().getMethod("setHeight",
                        int.class);
                setHeightMethod.invoke(itemSelectionWidget, itemSelectionWidgetHeight);
            } catch (Exception e) {
                try {
                    java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                            .getDeclaredField("height");
                    heightField.setAccessible(true);
                    heightField.setInt(itemSelectionWidget, itemSelectionWidgetHeight);
                } catch (Exception ex) {
                }
            }
        }

        int tagsX = rightX;
        int tagsY = bottomY;

        if (tagsSearchBox != null) {
            tagsSearchBox.y = tagsY + internalPaddingY;
        }

        if (tagsInventoryButton != null) {
            tagsInventoryButton.y = tagsSearchBox != null ? tagsSearchBox.y : tagsY + internalPaddingY;
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
        ForgeRegistries.ITEMS.getValues().forEach(item -> {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId != null && !itemId.getNamespace().equals("minecraft")) {
                namespaces.add(itemId.getNamespace());
            }
        });
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
        net.minecraft.network.chat.Component labelText = null;

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
                labelText = new TranslatableComponent("buildscape.config.mod_items", modName);
                break;
        }

        if (labelText == null) {
            labelText = new TranslatableComponent(labelKey);
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

        searchBox.x = searchBoxX;
        searchBox.setWidth(Math.max(minSearchBoxWidth, finalSearchBoxWidth));

        if (inventoryButton != null && allItemsButton != null && modOnlyButton != null && searchBox != null) {
            inventoryButton.x = buttonsStartX;
            inventoryButton.y = searchBox.y;
            inventoryButton.setWidth(buttonSize);
            inventoryButton.setHeight(buttonSize);

            allItemsButton.x = buttonsStartX + buttonSize + buttonSpacing;
            allItemsButton.y = searchBox.y;
            allItemsButton.setWidth(buttonSize);
            allItemsButton.setHeight(buttonSize);

            modOnlyButton.x = buttonsStartX + (buttonSize + buttonSpacing) * 2;
            modOnlyButton.y = searchBox.y;
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

        net.minecraft.network.chat.Component labelText;
        com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType sortType = tagsSelectorWidget.getSortType();
        if (sortType == com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType.MOD_ONLY) {
            labelText = new net.minecraft.network.chat.TextComponent("Buildscape Tags");
        } else if (sortType == com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType.INVENTORY) {
            labelText = new net.minecraft.network.chat.TextComponent("Inventory Tags");
        } else {
            labelText = new net.minecraft.network.chat.TextComponent("All Tags");
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

        tagsSearchBox.x = searchBoxX;
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        int spacing = 15;
        int midX = contentX + (contentWidth - spacing) / 2;
        int midY = contentY + (contentHeight - spacing) / 2;
        int leftWidth = (contentWidth - spacing) / 2;
        int topHeight = (contentHeight - spacing) / 2;
        int bottomLeftY = midY + spacing;

        if (existingItemsWidget != null) {
            existingItemsWidget.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (existingItemsWidget != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 400);
            Minecraft mc = Minecraft.getInstance();
            int labelX = existingItemsWidget.x + 2;
            int labelY = existingItemsWidget.y + 2 + BuildScapeConfigScreen.getScaledButtonHeight() / 2 - mc.font.lineHeight / 2 + 1;
            net.minecraft.network.chat.Component pillarItemsLabel = new TranslatableComponent(
                    "buildscape.config.pillar_items");
            int labelWidth = mc.font.width(pillarItemsLabel);
            int labelHeight = mc.font.lineHeight;

            float textScale = BuildScapeConfigScreen.getStandardTextScale();

            poseStack.pushPose();
            poseStack.translate(labelX, labelY, 0);
            poseStack.scale(textScale, textScale, 1.0f);
            mc.font.draw(
                    poseStack,
                    pillarItemsLabel,
                    0, 0,
                    0xFFFFFFFF
            );
            poseStack.popPose();
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(0, 0, 200);

        Minecraft mc = Minecraft.getInstance();
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        if (itemSelectionWidget != null && searchBox != null) {
            String labelKey = "buildscape.config.filtered_items";
            net.minecraft.network.chat.Component labelText = null;
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
                    labelText = new TranslatableComponent("buildscape.config.mod_items", modName);
                    break;
            }

            if (labelText == null) {
                labelText = new TranslatableComponent(labelKey);
            }

            int actualLabelX = itemSelectionWidget.x + 2;
            int textYOffset = (searchBoxHeight - (int)(mc.font.lineHeight * textScale)) / 2;
            renderScaledText(poseStack, labelText, actualLabelX, searchBox.y + textYOffset, textScale);
        }

        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            String labelKey = "buildscape.config.tags";
            net.minecraft.network.chat.Component tagsLabel;

            com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType sortType = tagsSelectorWidget.getSortType();
            if (sortType == com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType.MOD_ONLY) {
                tagsLabel = new net.minecraft.network.chat.TextComponent("Buildscape Tags");
            } else if (sortType == com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType.INVENTORY) {
                tagsLabel = new net.minecraft.network.chat.TextComponent("Inventory Tags");
            } else {
                tagsLabel = new net.minecraft.network.chat.TextComponent("All Tags");
            }

            int tagsLabelX = tagsSelectorWidget.x + 2;
            int textYOffset = (searchBoxHeight - (int)(mc.font.lineHeight * textScale)) / 2;
            renderScaledText(poseStack, tagsLabel, tagsLabelX, tagsSearchBox.y + textYOffset, textScale);
        }

        poseStack.popPose();

        if (searchBox != null) {
            searchBox.render(poseStack, mouseX, mouseY, partialTick);
        }
        if (inventoryButton != null) {
            inventoryButton.render(poseStack, mouseX, mouseY, partialTick);
        }
        if (allItemsButton != null) {
            allItemsButton.render(poseStack, mouseX, mouseY, partialTick);
        }
        if (modOnlyButton != null) {
            modOnlyButton.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (itemSelectionWidget != null) {
            itemSelectionWidget.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (presetsWidget != null) {
            presetsWidget.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (tagsSearchBox != null) {
            tagsSearchBox.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (tagsInventoryButton != null) {
            tagsInventoryButton.render(poseStack, mouseX, mouseY, partialTick);
        }
        if (tagsAllButton != null) {
            tagsAllButton.render(poseStack, mouseX, mouseY, partialTick);
        }
        if (tagsModOnlyButton != null) {
            tagsModOnlyButton.render(poseStack, mouseX, mouseY, partialTick);
        }

        if (tagsSelectorWidget != null) {
            tagsSelectorWidget.render(poseStack, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (inventoryButton != null) inventoryButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (allItemsButton != null) allItemsButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (modOnlyButton != null) modOnlyButton.renderButtonTooltip(poseStack, mouseX, mouseY);

        if (tagsInventoryButton != null) tagsInventoryButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (tagsAllButton != null) tagsAllButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (tagsModOnlyButton != null) tagsModOnlyButton.renderButtonTooltip(poseStack, mouseX, mouseY);

        if (itemSelectionWidget != null) {
            itemSelectionWidget.renderTooltip(poseStack, mouseX, mouseY);
        }

        if (existingItemsWidget != null) {
            existingItemsWidget.renderTooltip(poseStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchBox != null && searchBox.mouseClicked(mouseX, mouseY, button)) {
            parent.setFocused(searchBox);
            return true;
        }
        if (tagsSearchBox != null && tagsSearchBox.mouseClicked(mouseX, mouseY, button)) {
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
        if (searchBox != null && searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (tagsSearchBox != null && tagsSearchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return presetsWidget != null && presetsWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBox != null && searchBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (tagsSearchBox != null && tagsSearchBox.charTyped(codePoint, modifiers)) {
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

    private void renderScaledText(PoseStack poseStack, net.minecraft.network.chat.Component text, int x, int y, float scale) {
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        Minecraft.getInstance().font.drawShadow(poseStack, text, 0, 0, 0xFFFFFFFF);
        poseStack.popPose();
    }
}
