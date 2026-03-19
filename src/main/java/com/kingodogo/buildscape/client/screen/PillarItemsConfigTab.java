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
        // Get screen dimensions for percentage-based calculations
        int screenWidth = parent.width;
        int screenHeight = parent.height;

        // Calculate content area using percentage-based system
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        // Layout: 11% sidebar + 44% left content + 1% gap + 44% right content (all from
        // full screen width)
        // Each section takes 50% of content height

        int leftX = parent.getContentX();
        int leftPanelWidth = parent.getContentWidth();
        int rightX = parent.getRightPanelX();
        int rightPanelWidth = parent.getRightPanelWidth();

        // Vertical Layout:
        // Top Gap: 5% (handled by parent.getContentY())
        // Available Height = Screen Height - Top Gap - Bottom Gap (0.5%)
        // We have two panels separated by a middle gap (0.5%)
        // Panel Height = (Available Height - Middle Gap) / 2

        int topGap = parent.getContentY();
        int availableHeight = parent.getContentHeight();
        int middleGap = parent.getVerticalPanelGap(); // 0.5% consistent gap between panels

        int topSectionHeight = (availableHeight - middleGap) / 2;
        int bottomSectionHeight = availableHeight - middleGap - topSectionHeight;

        // Calculate positions
        int topY = topGap;
        int middleGapY = topY + topSectionHeight;
        int bottomY = middleGapY + middleGap;

        // 0.5% internal padding for buttons relative to panel top
        int internalPaddingY = (int) (screenHeight * 0.005) + 2;

        // Top-Left: Selected items (44% of full screen width, 50% height)
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
        
        // Dynamically calculate header height based on label position/height
        int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
        int headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
        existingItemsWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(existingItemsWidget);

        // Bottom-Left: Item selector panel (44% width, 50% height)
        // Create a container "panel" widget to hold all components together
        loadAvailableModNamespaces();

        // Create a dummy container widget to represent the panel bounds
        // This ensures all child components scale together
        int scaledOffset = BuildScapeConfigScreen.scaleSize(5); // Small top padding to prevent clipping
        int scaledButtonArea = BuildScapeConfigScreen.scaleSize(100); // Space for 3 buttons
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int leftPadding = BuildScapeConfigScreen.scaleSize(5);

        // Create panel container (invisible, just for positioning reference)
        net.minecraft.client.gui.components.AbstractWidget itemSelectorPanel = new net.minecraft.client.gui.components.AbstractWidget(
                leftX, bottomY, leftPanelWidth, bottomSectionHeight,
                net.minecraft.network.chat.TextComponent.EMPTY) {
            @Override
            public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY,
                    float partialTick) {
                // Invisible container
            }

            @Override
            public void updateNarration(
                    net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
            }
        };

        // Calculate button group dimensions
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);

        // Buttons end flush at panel right edge
        int buttonsEndX = leftX + leftPanelWidth;
        int buttonsStartX = buttonsEndX - totalButtonsWidth;

        int buttonY = bottomY + internalPaddingY; // Start slightly down from panel top

        // Create toggle buttons - Right aligned
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

        // Search box - positioned after label text, extending to buttons
        // Label
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.network.chat.Component allItemsLabel = new TranslatableComponent("buildscape.config.all_items");
        int allItemsLabelWidth = mc.font.width(allItemsLabel);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5);
        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        int searchBoxX = leftX + 2 + (int)(allItemsLabelWidth * textScale) + labelSpacing;
        // Search box ends before buttons with some spacing
        int searchBoxEndX = buttonsStartX - labelSpacing;
        int searchBoxWidth = searchBoxEndX - searchBoxX;

        searchBox = new EditBox(
                net.minecraft.client.Minecraft.getInstance().font,
                searchBoxX, buttonY, // Align Y with buttons
                searchBoxWidth, buttonSize, // Match height
                new TranslatableComponent("buildscape.config.search"));
        searchBox.setMaxLength(256);
        searchBox.setResponder((text) -> {
            if (itemSelectionWidget != null) {
                itemSelectionWidget.setFilter(text);
            }
        });
        addTabWidget(searchBox);

        // Create item selection widget - starts at panel top to cover entire area
        // Widget will internally handle spacing for search box via HEADER_AREA_HEIGHT
        int defaultItemSelectionX = leftX;
        int defaultItemSelectionY = bottomY; // Start at panel top
        itemSelectionWidget = new ItemSelectionWidget(
                defaultItemSelectionX, defaultItemSelectionY,
                leftPanelWidth, bottomSectionHeight,
                this::onItemSelected,
                (itemId) -> isItemInConfig(itemId) ? 1 : 0);
        itemSelectionWidget.setSortMode(SortToggleButton.SortType.ALL_ITEMS);
        
        // Dynamically calculate header height based on search box position/height to prevent overlap
        headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
        itemSelectionWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(itemSelectionWidget);

        // Top-Right: Presets (44% of full screen width, 50% height)
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

        // Get create button from presets widget for GUI config
        presetCreateButton = presetsWidget.getCreateButton();
        // Match PresetsWidget calculation: x + scaledSpacing, y + height -
        // scaleSize(35)
        int scaledSpacing = BuildScapeConfigScreen.scaleSize(10);
        int defaultCreateButtonX = presetsX + scaledSpacing;
        int defaultCreateButtonY = presetsY + presetsHeight - BuildScapeConfigScreen.scaleSize(35);

        // Bottom-Right: Tag selector panel (44% width, 50% height)
        // Create panel container for tags section
        int tagsX = rightX;
        int tagsY = bottomY;
        int tagsWidth = rightPanelWidth;

        net.minecraft.client.gui.components.AbstractWidget tagsPanel = new net.minecraft.client.gui.components.AbstractWidget(
                tagsX, tagsY, tagsWidth, bottomSectionHeight,
                net.minecraft.network.chat.TextComponent.EMPTY) {
            @Override
            public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY,
                    float partialTick) {
                // Invisible container
            }

            @Override
            public void updateNarration(
                    net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
            }
        };

        // Calculate width for tags buttons (same size/spacing)
        // Re-calculate these local variables to be safe in case code above changes
        int tagsButtonSize = BuildScapeConfigScreen.getScaledButtonHeight();
        int tagsButtonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalTagsButtonsWidth = (tagsButtonSize * 3) + (tagsButtonSpacing * 2);

        // Buttons end flush at panel right edge
        int tagsButtonsEndX = tagsX + tagsWidth;
        int tagsButtonsStartX = tagsButtonsEndX - totalTagsButtonsWidth;
        int tagsButtonY = tagsY + internalPaddingY;

        // Create tags sort buttons - Right aligned
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

        // Tags Search box
        net.minecraft.network.chat.Component tagsLabel = new TranslatableComponent("buildscape.config.tags");
        int tagsLabelWidth = mc.font.width(tagsLabel);
        int tagsLabelSpacing = BuildScapeConfigScreen.scaleSize(5);

        int tagsSearchBoxX = tagsX + 2 + (int)(tagsLabelWidth * BuildScapeConfigScreen.getStandardTextScale()) + tagsLabelSpacing;
        int tagsSearchBoxEndX = tagsButtonsStartX - tagsLabelSpacing;
        int tagsSearchBoxWidth = tagsSearchBoxEndX - tagsSearchBoxX;

        tagsSearchBox = new EditBox(
                net.minecraft.client.Minecraft.getInstance().font,
                tagsSearchBoxX, tagsButtonY, // Match buttons Y
                tagsSearchBoxWidth, tagsButtonSize, // Match buttons height
                new TranslatableComponent("buildscape.config.search_tags"));
        tagsSearchBox.setMaxLength(256);
        tagsSearchBox.setResponder((text) -> {
            if (tagsSelectorWidget != null) {
                tagsSelectorWidget.setFilter(text);
            }
        });
        addTabWidget(tagsSearchBox);

        // Create tags selector widget - starts at panel top to cover entire area
        // Widget will internally handle spacing for search box via HEADER_AREA_HEIGHT
        int tagsWidgetHeight = bottomSectionHeight; // Full panel height

        tagsSelectorWidget = new TagsSelectorWidget(
                tagsX, tagsY, // Start at panel top
                tagsWidth, tagsWidgetHeight,
                this::onTagSelected);
        tagsSelectorWidget.setSortType(TagsSelectorWidget.SortType.ALL_ITEMS);
        
        // Match tag selector header height for consistency
        tagsSelectorWidget.setHeaderAreaHeight(headerHeight);
        addTabWidget(tagsSelectorWidget);

        // Initialize widget connections
        if (itemSelectionWidget != null) {
            searchBox.setResponder((text) -> itemSelectionWidget.setFilter(text));
        }
        if (tagsSelectorWidget != null) {
            tagsSearchBox.setResponder((text) -> tagsSelectorWidget.setFilter(text));
        }

        // Update child component positions relative to their parent widgets
        updateChildComponentPositions();

        // Update selected tags from config
        updateSelectedTags();

        // Auto-apply preset on init (unnamed or last applied)
        PresetsConfig presetsConfig = PresetsConfig.get();
        presetsConfig.autoApplyOnLoad();
        refreshExistingItems();
        if (presetsWidget != null) {
            // Select the preset that was applied
            String appliedKey = presetsConfig.getLastAppliedPreset();
            if (presetsConfig.hasUnnamedPreset()) {
                appliedKey = "_unnamed";
            }
            presetsWidget.setSelectedPreset(appliedKey != null ? appliedKey : "default");
        }
    }

    /**
     * Update positions of child components (search boxes, buttons) relative to
     * their panel bounds.
     * This ensures components stay aligned when panels resize.
     */
    private void updateChildComponentPositions() {
        // Get current panel bounds using parent helper methods for consistency
        int screenWidth = parent.width;
        int screenHeight = parent.height;

        int leftX = parent.getContentX();
        int leftPanelWidth = parent.getContentWidth();
        int rightX = parent.getRightPanelX();
        int rightPanelWidth = parent.getRightPanelWidth();

        int topGap = parent.getContentY();
        int availableHeight = parent.getContentHeight();
        int middleGap = parent.getVerticalPanelGap(); // 0.5% consistent gap between panels
        
        int internalPaddingY = (int) (screenHeight * 0.005) + 2;

        int topSectionHeight = (availableHeight - middleGap) / 2;
        int bottomSectionHeight = availableHeight - middleGap - topSectionHeight;

        int topY = topGap;
        int middleGapY = topY + topSectionHeight;
        int bottomY = middleGapY + middleGap;

        // Position search box Y coordinate
        if (searchBox != null) {
            int searchBoxY = bottomY + internalPaddingY;
            searchBox.y = searchBoxY;
        }

        // Position buttons Y coordinate
        if (inventoryButton != null && allItemsButton != null && modOnlyButton != null) {
            int buttonY = bottomY + internalPaddingY;
            inventoryButton.y = buttonY;
            allItemsButton.y = buttonY;
            modOnlyButton.y = buttonY;
        }

        // Update item selection widget position - starts at panel top
        if (itemSelectionWidget != null && searchBox != null) {
            itemSelectionWidget.x = leftX;
            itemSelectionWidget.y = bottomY; // Start at panel top
            itemSelectionWidget.setWidth(leftPanelWidth);
            
            int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
            int headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
            itemSelectionWidget.setHeaderAreaHeight(headerHeight);
            
            int itemSelectionWidgetHeight = bottomSectionHeight; // Full panel height
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
                    // Ignore
                }
            }
        }

        // Update tags panel components (bottom-right)
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

        // Update tags selector widget position - starts at panel top
        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            int tagsWidgetHeight = bottomSectionHeight; // Full panel height
            tagsSelectorWidget.x = tagsX;
            tagsSelectorWidget.y = tagsY; // Start at panel top
            tagsSelectorWidget.setWidth(rightPanelWidth);
            tagsSelectorWidget.setHeight(tagsWidgetHeight);
            
            int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
            int headerHeight = internalPaddingY + buttonSize + BuildScapeConfigScreen.scaleSize(4);
            tagsSelectorWidget.setHeaderAreaHeight(headerHeight);
        }

        // Update PresetsWidget internal button positions (including Create button)
        if (presetsWidget != null) {
            presetsWidget.updateChildPositions();
        }

        // Update search box position/width based on current label text
        updateSearchBoxForLabel();
        updateTagsSearchBoxForLabel();
    }

    private void onPresetApplied(String presetKey) {
        refreshExistingItems();
        if (itemSelectionWidget != null) {
            itemSelectionWidget.refresh();
        }
        updateSelectedTags();

        // Clear unnamed preset when a preset is applied
        if (!presetKey.equals("_unnamed")) {
            PresetsConfig.get().clearUnnamedPreset();
        }
    }

    private void onTagSelected(String tagId) {
        PillarParticleConfig config = PillarParticleConfig.get();
        if (config.items.contains(tagId)) {
            // Tag is already selected, remove it
            config.removeItem(tagId);
        } else {
            // Tag is not selected, add it
            config.addItem(tagId);
        }
        saveToUnnamedPreset(); // Save changes to unnamed preset
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
        // Start with buildscape if available
        if (availableModNamespaces.contains("buildscape")) {
            currentModIndex = availableModNamespaces.indexOf("buildscape");
        }
    }

    private void onSortModeChanged(SortToggleButton.SortType type, boolean isCtrlDown) {
        // Deselect all buttons
        inventoryButton.setSelected(false);
        allItemsButton.setSelected(false);
        modOnlyButton.setSelected(false);

        // Select the clicked button
        switch (type) {
            case INVENTORY:
                inventoryButton.setSelected(true);
                break;
            case ALL_ITEMS:
                allItemsButton.setSelected(true);
                break;
            case MOD_ONLY:
                modOnlyButton.setSelected(true);
                // Cycle to next/prev mod if clicking again
                if (itemSelectionWidget != null &&
                        itemSelectionWidget.getSortMode() == SortToggleButton.SortType.MOD_ONLY) {
                    if (isCtrlDown) {
                        cycleToPreviousMod();
                    } else {
                        cycleToNextMod();
                    }
                } else {
                    // Set to first mod or buildscape if available
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

        // Update search box position/width when label changes
        updateSearchBoxForLabel();
    }

    /**
     * Updates the search box position and width based on the current label text.
     * This ensures the search box auto-resizes when the label changes (e.g., "All
     * items" -> "Inventory Items" -> "Mod Items").
     */
    private void updateSearchBoxForLabel() {
        if (itemSelectionWidget == null || searchBox == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        int leftX = parent.getContentX();
        int leftPanelWidth = parent.getContentWidth();
        int leftPadding = BuildScapeConfigScreen.scaleSize(5);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5);

        // Get current label text based on sort mode
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

        // Dynamically compute maxLabelWidth to ensure searchBox is minimum 80px width
        int minSearchBoxWidth = BuildScapeConfigScreen.scaleSize(80);
        
        int buttonSize = BuildScapeConfigScreen.getScaledButtonHeight();
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);
        // Buttons end flush at panel right edge
        int buttonsEndX = leftX + leftPanelWidth;
        int buttonsStartX = buttonsEndX - totalButtonsWidth;

        int searchBoxX = leftX + leftPadding + labelWidth + labelSpacing;
        int finalSearchBoxWidth = buttonsStartX - searchBoxX - labelSpacing;

        searchBox.x = searchBoxX;
        searchBox.setWidth(Math.max(minSearchBoxWidth, finalSearchBoxWidth)); 

        // Update button positions
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
        // Buttons end at panel right edge (no extra right gap inside panel)
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
        // Deselect all tags buttons
        tagsInventoryButton.setSelected(false);
        tagsAllButton.setSelected(false);
        tagsModOnlyButton.setSelected(false);

        // Select the clicked button
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

        // Apply sort mode to tags selector widget
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
        // Update search box position/width when mod changes (label text changes)
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
        // Update search box position/width when mod changes (label text changes)
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
        // Only toggle if explicitly clicked - don't auto-remove on load
        boolean wasInConfig = config.items.contains(itemId);

        if (wasInConfig) {
            // Item is already in config, remove it (user clicked to remove)
            if (config.removeItem(itemId)) {
                saveToUnnamedPreset(); // Save changes to unnamed preset
                refreshExistingItems();
                if (itemSelectionWidget != null) {
                    itemSelectionWidget.refresh();
                }
            }
        } else {
            // Item is not in config, add it (user clicked to add)
            if (config.addItem(itemId)) {
                saveToUnnamedPreset(); // Save changes to unnamed preset
                refreshExistingItems();
                if (itemSelectionWidget != null) {
                    itemSelectionWidget.refresh();
                }
            }
        }
    }

    private void saveToUnnamedPreset() {
        // Save current items to unnamed preset (unsaved changes)
        PillarParticleConfig config = PillarParticleConfig.get();
        PresetsConfig presetsConfig = PresetsConfig.get();

        // Don't save if default preset is selected and no changes were made
        if (presetsWidget != null) {
            String selectedKey = presetsWidget.getSelectedPresetKey();
            if (selectedKey != null && !selectedKey.equals("default") && !selectedKey.equals("_unnamed")) {
                // If a named preset is selected, switch to unnamed
                presetsWidget.setSelectedPreset("_unnamed");
            } else if (selectedKey == null || selectedKey.equals("default")) {
                // If default is selected, switch to unnamed to track changes
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
            saveToUnnamedPreset(); // Save changes to unnamed preset
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

        // Calculate quadrant boundaries
        int spacing = 15; // Match spacing from init()
        int midX = contentX + (contentWidth - spacing) / 2;
        int midY = contentY + (contentHeight - spacing) / 2;
        int leftWidth = (contentWidth - spacing) / 2;
        int topHeight = (contentHeight - spacing) / 2;
        int bottomLeftY = midY + spacing;

        // Render existing items widget (top-left quadrant)
        if (existingItemsWidget != null) {
            existingItemsWidget.render(poseStack, mouseX, mouseY, partialTick);
        }

        // Render "Pillar items" label inside existingItemsWidget area
        // Render AFTER widget to ensure it's visible on top with background for
        // visibility
        if (existingItemsWidget != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 400); // Bring label to front with highest z-level
            Minecraft mc = Minecraft.getInstance();
            int labelX = existingItemsWidget.x + 2;
            int labelY = existingItemsWidget.y + 2 + BuildScapeConfigScreen.getScaledButtonHeight() / 2 - mc.font.lineHeight / 2 + 1;
            net.minecraft.network.chat.Component pillarItemsLabel = new TranslatableComponent(
                    "buildscape.config.pillar_items");
            int labelWidth = mc.font.width(pillarItemsLabel);
            int labelHeight = mc.font.lineHeight;

            float textScale = BuildScapeConfigScreen.getStandardTextScale();

            // Render with white color and scaling for high GUI scales (no background
            // needed)
            poseStack.pushPose();
            poseStack.translate(labelX, labelY, 0);
            poseStack.scale(textScale, textScale, 1.0f);
            mc.font.draw(
                    poseStack,
                    pillarItemsLabel,
                    0, 0,
                    0xFFFFFFFF // Full opacity white
            );
            poseStack.popPose();
            poseStack.popPose();
        }

        // Render labels aligned with search boxes on the same line
        poseStack.pushPose();
        poseStack.translate(0, 0, 200); // Bring labels to front with higher z-level

        Minecraft mc = Minecraft.getInstance();
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        // Render Search Box Label
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

        // Render "Tags" label
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

        // Render search box and toggle buttons
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

        // Render item selection widget
        if (itemSelectionWidget != null) {
            itemSelectionWidget.render(poseStack, mouseX, mouseY, partialTick);
        }

        // Render presets widget (top-right)
        if (presetsWidget != null) {
            presetsWidget.render(poseStack, mouseX, mouseY, partialTick);
        }

        // Render tags search box (bottom-right, above tags selector)
        if (tagsSearchBox != null) {
            tagsSearchBox.render(poseStack, mouseX, mouseY, partialTick);
        }

        // Render tags sort buttons
        if (tagsInventoryButton != null) {
            tagsInventoryButton.render(poseStack, mouseX, mouseY, partialTick);
        }
        if (tagsAllButton != null) {
            tagsAllButton.render(poseStack, mouseX, mouseY, partialTick);
        }
        if (tagsModOnlyButton != null) {
            tagsModOnlyButton.render(poseStack, mouseX, mouseY, partialTick);
        }

        // Render tags selector widget (bottom-right)
        if (tagsSelectorWidget != null) {
            tagsSelectorWidget.render(poseStack, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Render tooltips for sort buttons
        if (inventoryButton != null) inventoryButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (allItemsButton != null) allItemsButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (modOnlyButton != null) modOnlyButton.renderButtonTooltip(poseStack, mouseX, mouseY);

        if (tagsInventoryButton != null) tagsInventoryButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (tagsAllButton != null) tagsAllButton.renderButtonTooltip(poseStack, mouseX, mouseY);
        if (tagsModOnlyButton != null) tagsModOnlyButton.renderButtonTooltip(poseStack, mouseX, mouseY);

        // Render tooltips for item selection widget
        if (itemSelectionWidget != null) {
            itemSelectionWidget.renderTooltip(poseStack, mouseX, mouseY);
        }

        // Render tooltips for existing items widget
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
        // Forward to all widgets for scrollbar dragging
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
        // Forward to all widgets for scrollbar release
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
        // Clear search boxes when leaving the tab
        if (searchBox != null) {
            searchBox.setValue("");
            if (itemSelectionWidget != null) {
                itemSelectionWidget.setFilter("");
            }
        }

        // Clear tags search box when leaving the tab
        if (tagsSearchBox != null) {
            tagsSearchBox.setValue("");
            if (tagsSelectorWidget != null) {
                tagsSelectorWidget.setFilter("");
            }
        }

        // Refresh items when tab is closed to ensure latest state
        refreshExistingItems();
        super.onClose(); // Remove tracked widgets
    }

    private void renderScaledText(PoseStack poseStack, net.minecraft.network.chat.Component text, int x, int y, float scale) {
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);
        Minecraft.getInstance().font.drawShadow(poseStack, text, 0, 0, 0xFFFFFFFF);
        poseStack.popPose();
    }
}
