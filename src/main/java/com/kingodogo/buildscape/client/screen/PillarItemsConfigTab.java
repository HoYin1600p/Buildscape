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

        // Use dimensions from parent screen directly to ensure consistency

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
        int bottomGap = (int) (screenHeight * 0.005);
        int middleGap = (int) (screenHeight * 0.005);

        int availableHeight = screenHeight - topGap - bottomGap;
        int sectionHeight = (availableHeight - middleGap) / 2;

        // Calculate positions
        int topY = topGap;
        int middleGapY = topY + sectionHeight;
        int bottomY = middleGapY + middleGap;

        // 0.5% internal padding for buttons relative to panel top
        int internalPaddingY = (int) (screenHeight * 0.005);

        // Top-Left: Selected items (44% of full screen width, 50% height)
        refreshExistingItems();
        int defaultExistingItemsX = leftX;
        int defaultExistingItemsY = topY;
        int defaultExistingItemsW = leftPanelWidth;
        int defaultExistingItemsH = sectionHeight;
        existingItemsWidget = new ExistingItemsWidget(
                defaultExistingItemsX, defaultExistingItemsY,
                defaultExistingItemsW, defaultExistingItemsH,
                existingItems,
                this::removeItem,
                this::isItemInConfig);
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
        int bottomPadding = BuildScapeConfigScreen.scaleSize(10);

        // Create panel container (invisible, just for positioning reference)
        net.minecraft.client.gui.components.AbstractWidget itemSelectorPanel = new net.minecraft.client.gui.components.AbstractWidget(
                leftX, bottomY, leftPanelWidth, sectionHeight,
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

        // Calculate label text width for "All items" to position search box after it
        // Calculate button group width first
        int buttonSize = 20; // Fixed 20x20 pixels as requested
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);

        // Gap from right edge: 0.5%
        int rightGap = (int) (screenWidth * 0.005);

        // Start buttons from the right edge of the panel minus gap
        // Ensure consistent anchoring
        int buttonsEndX = leftX + leftPanelWidth - rightGap;
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
        // leftPadding already defined above

        int searchBoxX = leftX + leftPadding + allItemsLabelWidth + labelSpacing;
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
        int itemSelectionWidth = leftPanelWidth;
        int itemSelectionWidgetHeight = sectionHeight - bottomPadding; // Full panel height

        itemSelectionWidget = new ItemSelectionWidget(
                defaultItemSelectionX, defaultItemSelectionY,
                itemSelectionWidth, itemSelectionWidgetHeight,
                this::onItemSelected,
                this::isItemInConfig);
        itemSelectionWidget.setSortMode(SortToggleButton.SortType.ALL_ITEMS);
        addTabWidget(itemSelectionWidget);

        // Top-Right: Presets (44% of full screen width, 50% height)
        int presetsX = rightX;
        int presetsY = topY;
        int presetsWidth = rightPanelWidth;
        int presetsHeight = sectionHeight;
        presetsWidget = new PresetsWidget(
                presetsX, presetsY,
                presetsWidth, presetsHeight,
                this::onPresetApplied);
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
        int tagsHeight = sectionHeight;

        net.minecraft.client.gui.components.AbstractWidget tagsPanel = new net.minecraft.client.gui.components.AbstractWidget(
                tagsX, tagsY, tagsWidth, tagsHeight,
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
        int tagsButtonSize = 20;
        int tagsButtonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int totalTagsButtonsWidth = (tagsButtonSize * 3) + (tagsButtonSpacing * 2);

        int tagsRightGap = (int) (screenWidth * 0.005);

        // Start buttons from the right edge of the TAGS panel minus gap
        // Identical logic to ItemSelectionWidget buttons
        int tagsButtonsEndX = tagsX + tagsWidth - tagsRightGap;
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

        int tagsLeftPadding = BuildScapeConfigScreen.scaleSize(5);
        int tagsSearchBoxX = tagsX + tagsLeftPadding + tagsLabelWidth + tagsLabelSpacing;
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
        int tagsWidgetHeight = sectionHeight - bottomPadding; // Full panel height

        tagsSelectorWidget = new TagsSelectorWidget(
                tagsX, tagsY, // Start at panel top
                tagsWidth, tagsWidgetHeight,
                this::onTagSelected);
        tagsSelectorWidget.setSortType(TagsSelectorWidget.SortType.ALL_ITEMS);
        addTabWidget(tagsSelectorWidget);

        // Register widget defaults and apply saved configs
        String tabName = getTabName();
        java.util.Map<String, com.kingodogo.buildscape.config.GuiConfigData.ElementConfig> defaults = new java.util.HashMap<>();

        // Helper function to create config with percentages
        // Percentages are calculated from absolute screen positions, not
        // content-relative
        java.util.function.Function<com.kingodogo.buildscape.config.GuiConfigData.ElementConfig, com.kingodogo.buildscape.config.GuiConfigData.ElementConfig> addPercentages = (
                config) -> {
            if (screenWidth > 0 && screenHeight > 0) {
                // Convert content-relative position to absolute screen position for percentage
                // calculation
                int absoluteX = config.x + contentX;
                int absoluteY = config.y + contentY;
                config.percentX = (double) absoluteX / screenWidth;
                config.percentY = (double) absoluteY / screenHeight;
                config.percentWidth = (double) config.width / screenWidth;
                config.percentHeight = (double) config.height / screenHeight;
            }
            return config;
        };

        defaults.put("existingItemsWidget",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        defaultExistingItemsX - contentX, defaultExistingItemsY - contentY,
                        defaultExistingItemsW, defaultExistingItemsH)));
        defaults.put("searchBox", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                searchBoxX - contentX, buttonY - contentY, searchBoxWidth,
                buttonSize)));
        defaults.put("searchBox2", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                tagsSearchBoxX - contentX, tagsButtonY - contentY, tagsSearchBoxWidth,
                tagsButtonSize)));
        defaults.put("tagsInventoryButton",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        tagsButtonsStartX - contentX, tagsButtonY - contentY, tagsButtonSize,
                        tagsButtonSize)));
        defaults.put("tagsAllButton",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        (tagsButtonsStartX + tagsButtonSize + tagsButtonSpacing) - contentX, tagsButtonY - contentY,
                        tagsButtonSize, tagsButtonSize)));
        defaults.put("tagsModOnlyButton",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        (tagsButtonsStartX + (tagsButtonSize + tagsButtonSpacing) * 2) - contentX,
                        tagsButtonY - contentY, tagsButtonSize, tagsButtonSize)));
        defaults.put("inventoryButton",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        buttonsStartX - contentX, buttonY - contentY, buttonSize,
                        buttonSize)));
        defaults.put("allItemsButton",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        (buttonsStartX + buttonSize + buttonSpacing) - contentX, buttonY - contentY, buttonSize,
                        buttonSize)));
        defaults.put("modOnlyButton",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        (buttonsStartX + (buttonSize + buttonSpacing) * 2) - contentX, buttonY - contentY,
                        buttonSize, buttonSize)));
        defaults.put("itemSelectionWidget",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        defaultItemSelectionX - contentX, defaultItemSelectionY - contentY, itemSelectionWidth,
                        itemSelectionWidgetHeight)));
        defaults.put("presetsWidget",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        presetsX - contentX, presetsY - contentY, presetsWidth, presetsHeight)));
        defaults.put("presetCreateButton",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        defaultCreateButtonX - contentX, defaultCreateButtonY - contentY,
                        BuildScapeConfigScreen.scaleSize(70), BuildScapeConfigScreen.getScaledButtonHeight())));
        defaults.put("tagsSelectorWidget",
                addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
                        tagsX - contentX, tagsY - contentY, tagsWidth, tagsWidgetHeight)));

        // Set searchBox2 to target tagsSelectorWidget by default
        com.kingodogo.buildscape.config.GuiConfigData.ElementConfig searchBox2Config = defaults.get("searchBox2");
        if (searchBox2Config.properties == null) {
            searchBox2Config.properties = new java.util.HashMap<>();
        }
        searchBox2Config.properties.put("searchTarget", "tagsSelectorWidget");

        GuiConfigHelper.registerWidgetDefaults(tabName, defaults);

        // Apply saved configs (positions are content-relative, so pass content offsets)
        java.util.Map<String, net.minecraft.client.gui.components.AbstractWidget> widgets = new java.util.HashMap<>();
        widgets.put("existingItemsWidget", existingItemsWidget);
        widgets.put("itemSelectionWidget", itemSelectionWidget);
        widgets.put("presetsWidget", presetsWidget);
        widgets.put("tagsSelectorWidget", tagsSelectorWidget);
        widgets.put("inventoryButton", inventoryButton);
        widgets.put("allItemsButton", allItemsButton);
        widgets.put("modOnlyButton", modOnlyButton);
        widgets.put("tagsInventoryButton", tagsInventoryButton);
        widgets.put("tagsAllButton", tagsAllButton);
        widgets.put("tagsModOnlyButton", tagsModOnlyButton);
        if (presetCreateButton != null) {
            widgets.put("presetCreateButton", presetCreateButton);
        }

        // Use screen dimensions already calculated at the start of init() for
        // percentage-based calculations

        GuiConfigHelper.applyAllConfigs(tabName, widgets, contentX, contentY, screenWidth, screenHeight);

        java.util.Map<String, net.minecraft.client.gui.components.EditBox> editBoxes = new java.util.HashMap<>();
        editBoxes.put("searchBox", searchBox);
        editBoxes.put("searchBox2", tagsSearchBox);
        GuiConfigHelper.applyAllEditBoxConfigs(tabName, editBoxes, contentX, contentY, screenWidth, screenHeight);

        // Update child component positions relative to their parent widgets after
        // configs are applied
        updateChildComponentPositions();

        // Apply search box target linking if configured
        com.kingodogo.buildscape.config.GuiConfigData config = GuiConfigHelper.getConfig(tabName);

        // Link searchBox to its target
        com.kingodogo.buildscape.config.GuiConfigData.ElementConfig searchBoxConfig = config
                .getElementConfig("searchBox");
        if (searchBoxConfig != null && searchBoxConfig.properties != null
                && searchBoxConfig.properties.containsKey("searchTarget")) {
            String searchTarget = (String) searchBoxConfig.properties.get("searchTarget");
            // Re-link the search box responder based on the configured target
            if ("itemSelectionWidget".equals(searchTarget) && itemSelectionWidget != null) {
                searchBox.setResponder((text) -> {
                    if (itemSelectionWidget != null) {
                        itemSelectionWidget.setFilter(text);
                    }
                });
            }
        }

        // Link searchBox2 (tags search box) to tagsSelectorWidget
        com.kingodogo.buildscape.config.GuiConfigData.ElementConfig savedSearchBox2Config = config
                .getElementConfig("searchBox2");
        if (savedSearchBox2Config != null && savedSearchBox2Config.properties != null
                && savedSearchBox2Config.properties.containsKey("searchTarget")) {
            String searchTarget = (String) savedSearchBox2Config.properties.get("searchTarget");
            if ("tagsSelectorWidget".equals(searchTarget) && tagsSelectorWidget != null) {
                tagsSearchBox.setResponder((text) -> {
                    if (tagsSelectorWidget != null) {
                        tagsSelectorWidget.setFilter(text);
                    }
                });
            }
        } else {
            // Default: link to tagsSelectorWidget if no config
            if (tagsSelectorWidget != null) {
                tagsSearchBox.setResponder((text) -> {
                    if (tagsSelectorWidget != null) {
                        tagsSelectorWidget.setFilter(text);
                    }
                });
            }
        }

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
        // Get current panel bounds (recalculate from screen dimensions)
        int screenWidth = parent.width;
        int screenHeight = parent.height;
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        // Calculate panel positions (same as in init)
        // Calculate panel positions (same as in init)
        int leftPanelWidth = parent.getContentWidth(); // Use parent method for consistency
        int rightPanelWidth = parent.getRightPanelWidth();
        int gap = (int) (screenWidth * 0.01);

        int topGap = parent.getContentY();
        int bottomGap = (int) (screenHeight * 0.005);
        int middleGap = (int) (screenHeight * 0.005);

        int availableHeight = screenHeight - topGap - bottomGap;
        int sectionHeight = (availableHeight - middleGap) / 2;

        int leftX = contentX;
        int rightX = parent.getRightPanelX();
        int topY = topGap;
        int middleGapY = topY + sectionHeight;
        int bottomY = middleGapY + middleGap;

        // Update item selector panel components (bottom-left)
        int scaledOffset = BuildScapeConfigScreen.scaleSize(5);
        int scaledButtonArea = BuildScapeConfigScreen.scaleSize(100);
        int leftPadding = BuildScapeConfigScreen.scaleSize(5);
        int bottomPadding = BuildScapeConfigScreen.scaleSize(10);

        // Position search box Y coordinate (X and width handled by
        // updateSearchBoxForLabel)
        if (searchBox != null) {
            int searchBoxY = bottomY + scaledOffset;
            searchBox.y = searchBoxY;
        }

        // Position buttons Y coordinate (X handled by updateSearchBoxForLabel)
        if (inventoryButton != null && allItemsButton != null && modOnlyButton != null) {
            int buttonY = bottomY + scaledOffset;
            inventoryButton.y = buttonY;
            allItemsButton.y = buttonY;
            modOnlyButton.y = buttonY;
        }

        // Update item selection widget position - starts at panel top
        if (itemSelectionWidget != null && searchBox != null) {
            itemSelectionWidget.x = leftX;
            itemSelectionWidget.y = bottomY; // Start at panel top
            itemSelectionWidget.setWidth(leftPanelWidth);
            int itemSelectionWidgetHeight = sectionHeight - bottomPadding; // Full panel height
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

        int presetsBottomY = topY + sectionHeight;
        int tagsSearchBoxYOffset = Math.min(scaledOffset, Math.max(scaledOffset, presetsBottomY - tagsY));

        if (tagsSearchBox != null) {
            tagsSearchBox.y = tagsY + tagsSearchBoxYOffset;
        }

        if (tagsInventoryButton != null) {
            tagsInventoryButton.y = tagsSearchBox != null ? tagsSearchBox.y : tagsY + scaledOffset;
            tagsAllButton.y = tagsInventoryButton.y;
            tagsModOnlyButton.y = tagsInventoryButton.y;
        }

        // Update tags selector widget position - starts at panel top
        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            int tagsWidgetHeight = sectionHeight - bottomPadding; // Full panel height
            tagsSelectorWidget.x = tagsX;
            tagsSelectorWidget.y = tagsY; // Start at panel top
            tagsSelectorWidget.setWidth(rightPanelWidth);
            tagsSelectorWidget.setHeight(tagsWidgetHeight);
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
        int scaledButtonArea = BuildScapeConfigScreen.scaleSize(100);

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

        int labelWidth = mc.font.width(labelText);

        // Dynamically compute maxLabelWidth to ensure searchBox is minimum 50px width
        int minSearchBoxWidth = BuildScapeConfigScreen.scaleSize(50);
        
        // Calculate the anchor buttons right start point
        int buttonSize = 20;
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int screenWidth = parent.width;
        int rightGap = (int) (screenWidth * 0.005);
        int buttonsEndX = leftX + leftPanelWidth - rightGap;
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);
        int buttonsStartX = buttonsEndX - totalButtonsWidth;

        int availableLabelSpace = (buttonsStartX - labelSpacing - minSearchBoxWidth) - (leftX + leftPadding);
        int maxLabelWidth = Math.max(BuildScapeConfigScreen.scaleSize(30), availableLabelSpace);

        int finalLabelWidth = Math.min(labelWidth, maxLabelWidth);

        int searchBoxX = leftX + leftPadding + finalLabelWidth + labelSpacing;
        int finalSearchBoxWidth = buttonsStartX - searchBoxX - labelSpacing;

        searchBox.x = searchBoxX;
        searchBox.setWidth(Math.max(50, finalSearchBoxWidth)); 

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

        int buttonSize = 20; 
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int screenWidth = parent.width;
        int rightGap = (int) (screenWidth * 0.005);
        int buttonsEndX = rightX + rightPanelWidth - rightGap;
        int totalButtonsWidth = (buttonSize * 3) + (buttonSpacing * 2);
        int buttonsStartX = buttonsEndX - totalButtonsWidth;

        net.minecraft.network.chat.Component labelText;
        com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType sortType = tagsSelectorWidget.getSortType();
        if (sortType == com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType.MOD_ONLY) {
            labelText = new TranslatableComponent("buildscape.config.mod_items", "Buildscape");
        } else {
            labelText = new TranslatableComponent("buildscape.config.tags");
        }

        int labelWidth = mc.font.width(labelText);
        int minSearchBoxWidth = BuildScapeConfigScreen.scaleSize(50);
        int availableLabelSpace = (buttonsStartX - labelSpacing - minSearchBoxWidth) - (rightX + leftPadding);
        int maxLabelWidth = Math.max(BuildScapeConfigScreen.scaleSize(30), availableLabelSpace);
        int finalLabelWidth = Math.min(labelWidth, maxLabelWidth);

        int searchBoxX = rightX + leftPadding + finalLabelWidth + labelSpacing;
        int finalSearchBoxWidth = buttonsStartX - searchBoxX - labelSpacing;

        tagsSearchBox.x = searchBoxX;
        tagsSearchBox.setWidth(Math.max(50, finalSearchBoxWidth)); 

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
            int labelPadding = BuildScapeConfigScreen.scaleSize(5);
            int labelX = existingItemsWidget.x + labelPadding;
            int labelY = existingItemsWidget.y + labelPadding;
            net.minecraft.network.chat.Component pillarItemsLabel = new TranslatableComponent(
                    "buildscape.config.pillar_items");
            Minecraft mc = Minecraft.getInstance();
            int labelWidth = mc.font.width(pillarItemsLabel);
            int labelHeight = mc.font.lineHeight;

            // Calculate text scale based on GUI scale (shrink text for high GUI scales)
            double guiScale = mc.getWindow().getGuiScale();
            float textScale = 1.0f;
            if (guiScale >= 3.0) {
                textScale = 0.75f; // Scale down for GUI scale 3-4
            } else if (guiScale >= 2.5) {
                textScale = 0.85f; // Scale down slightly for GUI scale 2.5-3
            }

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
        int labelPadding = BuildScapeConfigScreen.scaleSize(5);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5);
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();


        // Render Search Box Label (Scrolling if needed)
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

            // Calculate label position - avoid overlap
            // Actual start X is itemSelectionWidget.x + padding
            int actualLabelX = itemSelectionWidget.x + BuildScapeConfigScreen.scaleSize(5);
            int dynamicMaxLabelWidth = Math.max(10, searchBox.x - actualLabelX - labelSpacing);

            renderScrollingString(poseStack, mc.font, labelText, actualLabelX, searchBox.y + (searchBoxHeight - mc.font.lineHeight) / 2, dynamicMaxLabelWidth, 0xFFFFFFFF);
        }

        // Render "Tags" label aligned with tags search box on the same line
        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            String labelKey = "buildscape.config.tags";
            net.minecraft.network.chat.Component tagsLabel;

            com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType sortType = tagsSelectorWidget.getSortType();
            if (sortType == com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget.SortType.MOD_ONLY) {
                tagsLabel = new TranslatableComponent("buildscape.config.mod_items", "Buildscape");
            } else {
                tagsLabel = new TranslatableComponent(labelKey);
            }
            
            int tagsLabelX = tagsSelectorWidget.x + labelPadding;
            int tagsLabelY = tagsSearchBox.y + (searchBoxHeight - mc.font.lineHeight) / 2;
            int dynamicMaxTagsLabelWidth = Math.max(10, tagsSearchBox.x - tagsLabelX - labelSpacing);

            renderScrollingString(poseStack, mc.font, tagsLabel, tagsLabelX, tagsLabelY, dynamicMaxTagsLabelWidth, 0xFFFFFFFF);
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
            return true;
        }
        if (tagsSearchBox != null && tagsSearchBox.mouseClicked(mouseX, mouseY, button)) {
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
            return true;
        }
        if (itemSelectionWidget != null && itemSelectionWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (presetsWidget != null && presetsWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return tagsSelectorWidget != null && tagsSelectorWidget.mouseClicked(mouseX, mouseY, button);
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

    private void renderScrollingString(PoseStack poseStack, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component text, int x, int y, int maxWidth, int color) {
        int textWidth = font.width(text);
        if (textWidth <= maxWidth) {
            font.draw(poseStack, text, x, y, color);
        } else {
            // Scrolling logic
            long currentTime = System.currentTimeMillis();

            int scrollRange = textWidth - maxWidth + 10; // Extra buffer

            // Total cycle time: 2000ms wait + (scrollRange * 40ms) scroll + 2000ms wait
            long waitTime = 2000;
            long scrollDuration = scrollRange * 40L; // 40ms per pixel
            long totalCycle = waitTime * 2 + scrollDuration;

            long cyclePos = currentTime % totalCycle;

            double scrollX = 0;
            if (cyclePos < waitTime) {
                scrollX = 0;
            } else if (cyclePos < waitTime + scrollDuration) {
                scrollX = (double) (cyclePos - waitTime) / 40.0;
            } else {
                scrollX = scrollRange;
            }

            // Scissor test to clip text
            int scale = (int) Minecraft.getInstance().getWindow().getGuiScale();
            int scissorX = x * scale;
            int scissorY = (Minecraft.getInstance().getWindow().getHeight()) - ((y + font.lineHeight + 2) * scale);
            int scissorW = maxWidth * scale;
            int scissorH = (font.lineHeight + 4) * scale;

            if (scissorW > 0 && scissorH > 0) {
                com.mojang.blaze3d.systems.RenderSystem.enableScissor(scissorX, scissorY, scissorW, scissorH);
                font.draw(poseStack, text, (float) (x - scrollX), y, color);
                com.mojang.blaze3d.systems.RenderSystem.disableScissor();
            }
        }
    }
}
