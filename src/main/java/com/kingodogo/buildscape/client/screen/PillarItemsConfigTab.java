package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.ExistingItemsWidget;
import com.kingodogo.buildscape.client.screen.widget.ItemSelectionWidget;
import com.kingodogo.buildscape.client.screen.widget.PresetsWidget;
import com.kingodogo.buildscape.client.screen.widget.SortToggleButton;
import com.kingodogo.buildscape.client.screen.widget.TagsSelectorWidget;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.config.PresetsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
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
        
        // Layout: 11% sidebar + 44% left content + 1% gap + 44% right content (all from full screen width)
        // Each section takes 50% of content height
        
        // Calculate panel widths from full screen width (not content width)
        int leftPanelWidth = (int)(screenWidth * 0.44); // 44% of full screen
        int rightPanelWidth = (int)(screenWidth * 0.44); // 44% of full screen
        int gap = (int)(screenWidth * 0.01); // 1% of full screen
        
        // Each section takes 50% of content height
        double sectionHeightPercent = 0.50; // 50% of content height
        int sectionHeight = (int)(contentHeight * sectionHeightPercent);
        
        // Calculate positions (left panel starts at contentX, right panel after gap)
        int leftX = contentX;
        int rightX = contentX + leftPanelWidth + gap;
        int topY = contentY;
        int bottomY = contentY + sectionHeight;
        
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
            this::isItemInConfig
        );
        addTabWidget(existingItemsWidget);
        
        // Bottom-Left: Item selector panel (44% width, 50% height)
        // Create a container "panel" widget to hold all components together
        loadAvailableModNamespaces();
        
        // Create a dummy container widget to represent the panel bounds
        // This ensures all child components scale together
        int scaledOffset = BuildScapeConfigScreen.scaleSize(20);
        int scaledButtonArea = BuildScapeConfigScreen.scaleSize(100); // Space for 3 buttons
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int leftPadding = BuildScapeConfigScreen.scaleSize(5);
        int bottomPadding = BuildScapeConfigScreen.scaleSize(10);
        
        // Create panel container (invisible, just for positioning reference)
        net.minecraft.client.gui.components.AbstractWidget itemSelectorPanel = new net.minecraft.client.gui.components.AbstractWidget(
            leftX, bottomY, leftPanelWidth, sectionHeight, 
            net.minecraft.network.chat.TextComponent.EMPTY) {
            @Override
            public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                // Invisible container
            }
            @Override
            public void updateNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {}
        };
        
        // Calculate label text width for "All items" to position search box after it
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.network.chat.Component allItemsLabel = new TranslatableComponent("buildscape.config.all_items");
        int allItemsLabelWidth = mc.font.width(allItemsLabel);
        int labelSpacing = BuildScapeConfigScreen.scaleSize(5); // Space between label and search box
        
        // Create search box - positioned after label text
        int defaultSearchBoxY = bottomY + scaledOffset;
        int defaultSearchBoxX = leftX + leftPadding + allItemsLabelWidth + labelSpacing;
        // Search box width = panel width - label width - label spacing - button area - padding
        int searchBoxWidth = leftPanelWidth - allItemsLabelWidth - labelSpacing - scaledButtonArea - leftPadding * 2;
        
        searchBox = new EditBox(
            net.minecraft.client.Minecraft.getInstance().font,
            defaultSearchBoxX, defaultSearchBoxY,
            searchBoxWidth, searchBoxHeight,
            new TranslatableComponent("buildscape.config.search")
        );
        searchBox.setMaxLength(256);
        searchBox.setResponder((text) -> {
            if (itemSelectionWidget != null) {
                itemSelectionWidget.setFilter(text);
            }
        });
        addTabWidget(searchBox);
        
        // Create toggle buttons - positioned after search box
        int buttonSize = BuildScapeConfigScreen.scaleSize(20);
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int buttonsStartX = defaultSearchBoxX + searchBoxWidth + BuildScapeConfigScreen.scaleSize(10);
        int defaultInventoryButtonX = buttonsStartX;
        int defaultInventoryButtonY = defaultSearchBoxY;
        int searchBoxY = defaultSearchBoxY; // For defaults registration
        
        inventoryButton = new SortToggleButton(
            defaultInventoryButtonX, defaultInventoryButtonY,
            buttonSize, buttonSize,
            SortToggleButton.SortType.INVENTORY,
            (type) -> onSortModeChanged(type)
        );
        addTabWidget(inventoryButton);
        
        allItemsButton = new SortToggleButton(
            buttonsStartX + buttonSize + buttonSpacing, defaultSearchBoxY,
            buttonSize, buttonSize,
            SortToggleButton.SortType.ALL_ITEMS,
            (type) -> onSortModeChanged(type)
        );
        allItemsButton.setSelected(true);
        addTabWidget(allItemsButton);
        
        modOnlyButton = new SortToggleButton(
            buttonsStartX + (buttonSize + buttonSpacing) * 2, defaultSearchBoxY,
            buttonSize, buttonSize,
            SortToggleButton.SortType.MOD_ONLY,
            (type) -> onSortModeChanged(type)
        );
        addTabWidget(modOnlyButton);
        
        // Create item selection widget below search box
        int itemSelectionY = bottomY + scaledOffset + searchBoxHeight + BuildScapeConfigScreen.scaleSize(5);
        int itemSelectionHeight = sectionHeight - (itemSelectionY - bottomY) - bottomPadding;
        int defaultItemSelectionX = leftX;
        int defaultItemSelectionY = itemSelectionY;
        int itemSelectionWidth = leftPanelWidth;
        int itemSelectionWidgetHeight = itemSelectionHeight;
        
        itemSelectionWidget = new ItemSelectionWidget(
            defaultItemSelectionX, defaultItemSelectionY,
            itemSelectionWidth, itemSelectionWidgetHeight,
            this::onItemSelected,
            this::isItemInConfig
        );
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
            this::onPresetApplied
        );
        addTabWidget(presetsWidget);
        
        // Get create button from presets widget for GUI config
        presetCreateButton = presetsWidget.getCreateButton();
        // Match PresetsWidget calculation: x + scaledSpacing, y + height - scaleSize(35)
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
            public void renderButton(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                // Invisible container
            }
            @Override
            public void updateNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {}
        };
        
        // Calculate label text width for "Tags" to position search box after it
        net.minecraft.network.chat.Component tagsLabel = new TranslatableComponent("buildscape.config.tags");
        int tagsLabelWidth = mc.font.width(tagsLabel);
        int tagsLabelSpacing = BuildScapeConfigScreen.scaleSize(5); // Space between label and search box
        
        // Create tags search box - positioned after label text, aligned with "All items" search box
        int defaultTagsSearchBoxY = tagsY + scaledOffset; // Same Y as "All items" search box
        int defaultTagsSearchBoxX = tagsX + leftPadding + tagsLabelWidth + tagsLabelSpacing;
        // Search box width = panel width - label width - label spacing - button area - padding
        int tagsSearchBoxWidth = tagsWidth - tagsLabelWidth - tagsLabelSpacing - scaledButtonArea - leftPadding * 2;
        int tagsSearchBoxY = defaultTagsSearchBoxY; // For defaults registration
        
        tagsSearchBox = new EditBox(
            net.minecraft.client.Minecraft.getInstance().font,
            defaultTagsSearchBoxX, defaultTagsSearchBoxY,
            tagsSearchBoxWidth, searchBoxHeight,
            new TranslatableComponent("buildscape.config.search_tags")
        );
        tagsSearchBox.setMaxLength(256);
        tagsSearchBox.setResponder((text) -> {
            if (tagsSelectorWidget != null) {
                tagsSelectorWidget.setFilter(text);
            }
        });
        addTabWidget(tagsSearchBox);
        
        // Create tags sort buttons - positioned after tags search box
        int tagsButtonSize = BuildScapeConfigScreen.scaleSize(20);
        int tagsButtonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int tagsButtonsStartX = defaultTagsSearchBoxX + tagsSearchBoxWidth + BuildScapeConfigScreen.scaleSize(10);
        int defaultTagsInventoryButtonX = tagsButtonsStartX;
        int defaultTagsInventoryButtonY = defaultTagsSearchBoxY;
        
        tagsInventoryButton = new SortToggleButton(
            defaultTagsInventoryButtonX, defaultTagsInventoryButtonY,
            tagsButtonSize, tagsButtonSize,
            SortToggleButton.SortType.INVENTORY,
            (type) -> onTagsSortModeChanged(type)
        );
        addTabWidget(tagsInventoryButton);
        
        tagsAllButton = new SortToggleButton(
            tagsButtonsStartX + tagsButtonSize + tagsButtonSpacing, defaultTagsSearchBoxY,
            tagsButtonSize, tagsButtonSize,
            SortToggleButton.SortType.ALL_ITEMS,
            (type) -> onTagsSortModeChanged(type)
        );
        tagsAllButton.setSelected(true);
        addTabWidget(tagsAllButton);
        
        tagsModOnlyButton = new SortToggleButton(
            tagsButtonsStartX + (tagsButtonSize + tagsButtonSpacing) * 2, defaultTagsSearchBoxY,
            tagsButtonSize, tagsButtonSize,
            SortToggleButton.SortType.MOD_ONLY,
            (type) -> onTagsSortModeChanged(type)
        );
        addTabWidget(tagsModOnlyButton);
        
        // Create tags selector widget below search box
        int tagsWidgetY = tagsY + scaledOffset + searchBoxHeight + BuildScapeConfigScreen.scaleSize(5);
        int tagsWidgetHeight = sectionHeight - (tagsWidgetY - tagsY) - bottomPadding;
        
        tagsSelectorWidget = new TagsSelectorWidget(
            tagsX, tagsWidgetY,
            tagsWidth, tagsWidgetHeight,
            this::onTagSelected
        );
        tagsSelectorWidget.setSortType(TagsSelectorWidget.SortType.ALL_ITEMS);
        addTabWidget(tagsSelectorWidget);
        
        // Register widget defaults and apply saved configs
        String tabName = getTabName();
        java.util.Map<String, com.kingodogo.buildscape.config.GuiConfigData.ElementConfig> defaults = new java.util.HashMap<>();
        
        // Helper function to create config with percentages
        // Percentages are calculated from absolute screen positions, not content-relative
        java.util.function.Function<com.kingodogo.buildscape.config.GuiConfigData.ElementConfig, com.kingodogo.buildscape.config.GuiConfigData.ElementConfig> addPercentages = (config) -> {
            if (screenWidth > 0 && screenHeight > 0) {
                // Convert content-relative position to absolute screen position for percentage calculation
                int absoluteX = config.x + contentX;
                int absoluteY = config.y + contentY;
                config.percentX = (double)absoluteX / screenWidth;
                config.percentY = (double)absoluteY / screenHeight;
                config.percentWidth = (double)config.width / screenWidth;
                config.percentHeight = (double)config.height / screenHeight;
            }
            return config;
        };
        
        defaults.put("existingItemsWidget", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            defaultExistingItemsX - contentX, defaultExistingItemsY - contentY, 
            defaultExistingItemsW, defaultExistingItemsH)));
        defaults.put("searchBox", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            defaultSearchBoxX - contentX, defaultSearchBoxY - contentY, searchBoxWidth, BuildScapeConfigScreen.getScaledEditBoxHeight())));
        defaults.put("searchBox2", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            defaultTagsSearchBoxX - contentX, defaultTagsSearchBoxY - contentY, tagsSearchBoxWidth, BuildScapeConfigScreen.getScaledEditBoxHeight())));
        defaults.put("tagsInventoryButton", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            defaultTagsInventoryButtonX - contentX, defaultTagsInventoryButtonY - contentY, tagsButtonSize, tagsButtonSize)));
        defaults.put("tagsAllButton", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            (tagsButtonsStartX + tagsButtonSize + tagsButtonSpacing) - contentX, tagsSearchBoxY - contentY, tagsButtonSize, tagsButtonSize)));
        defaults.put("tagsModOnlyButton", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            (tagsButtonsStartX + (tagsButtonSize + tagsButtonSpacing) * 2) - contentX, tagsSearchBoxY - contentY, tagsButtonSize, tagsButtonSize)));
        defaults.put("inventoryButton", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            defaultInventoryButtonX - contentX, defaultInventoryButtonY - contentY, buttonSize, buttonSize)));
        defaults.put("allItemsButton", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            (buttonsStartX + buttonSize + buttonSpacing) - contentX, searchBoxY - contentY, buttonSize, buttonSize)));
        defaults.put("modOnlyButton", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            (buttonsStartX + (buttonSize + buttonSpacing) * 2) - contentX, searchBoxY - contentY, buttonSize, buttonSize)));
        defaults.put("itemSelectionWidget", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            defaultItemSelectionX - contentX, defaultItemSelectionY - contentY, itemSelectionWidth, itemSelectionWidgetHeight)));
        defaults.put("presetsWidget", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            presetsX - contentX, presetsY - contentY, presetsWidth, presetsHeight)));
        defaults.put("presetCreateButton", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            defaultCreateButtonX - contentX, defaultCreateButtonY - contentY, 
            BuildScapeConfigScreen.scaleSize(70), BuildScapeConfigScreen.getScaledButtonHeight())));
        defaults.put("tagsSelectorWidget", addPercentages.apply(new com.kingodogo.buildscape.config.GuiConfigData.ElementConfig(
            tagsX - contentX, tagsWidgetY - contentY, tagsWidth, tagsWidgetHeight)));
        
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
        
        // Use screen dimensions already calculated at the start of init() for percentage-based calculations
        
        GuiConfigHelper.applyAllConfigs(tabName, widgets, contentX, contentY, screenWidth, screenHeight);
        
        java.util.Map<String, net.minecraft.client.gui.components.EditBox> editBoxes = new java.util.HashMap<>();
        editBoxes.put("searchBox", searchBox);
        editBoxes.put("searchBox2", tagsSearchBox);
        GuiConfigHelper.applyAllEditBoxConfigs(tabName, editBoxes, contentX, contentY, screenWidth, screenHeight);
        
        // Update child component positions relative to their parent widgets after configs are applied
        updateChildComponentPositions();
        
        // Apply search box target linking if configured
        com.kingodogo.buildscape.config.GuiConfigData config = GuiConfigHelper.getConfig(tabName);
        
        // Link searchBox to its target
        com.kingodogo.buildscape.config.GuiConfigData.ElementConfig searchBoxConfig = config.getElementConfig("searchBox");
        if (searchBoxConfig != null && searchBoxConfig.properties != null && searchBoxConfig.properties.containsKey("searchTarget")) {
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
        com.kingodogo.buildscape.config.GuiConfigData.ElementConfig savedSearchBox2Config = config.getElementConfig("searchBox2");
        if (savedSearchBox2Config != null && savedSearchBox2Config.properties != null && savedSearchBox2Config.properties.containsKey("searchTarget")) {
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
     * Update positions of child components (search boxes, buttons) relative to their panel bounds.
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
        int leftPanelWidth = (int)(screenWidth * 0.44);
        int rightPanelWidth = (int)(screenWidth * 0.44);
        int gap = (int)(screenWidth * 0.01);
        int sectionHeight = (int)(contentHeight * 0.50);
        
        int leftX = contentX;
        int rightX = contentX + leftPanelWidth + gap;
        int topY = contentY;
        int bottomY = contentY + sectionHeight;
        
        // Update item selector panel components (bottom-left)
        int scaledOffset = BuildScapeConfigScreen.scaleSize(20);
        int scaledButtonArea = BuildScapeConfigScreen.scaleSize(100);
        int leftPadding = BuildScapeConfigScreen.scaleSize(5);
        int buttonSize = BuildScapeConfigScreen.scaleSize(20);
        int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
        int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int bottomPadding = BuildScapeConfigScreen.scaleSize(10);
        
        // Position search box Y coordinate (X and width handled by updateSearchBoxForLabel)
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
        
        // Update item selection widget position below search box
        if (itemSelectionWidget != null && searchBox != null) {
            int itemSelectionY = bottomY + scaledOffset + searchBoxHeight + BuildScapeConfigScreen.scaleSize(5);
            int itemSelectionHeight = sectionHeight - (itemSelectionY - bottomY) - bottomPadding;
            itemSelectionWidget.x = leftX;
            itemSelectionWidget.y = itemSelectionY;
            itemSelectionWidget.setWidth(leftPanelWidth);
            try {
                java.lang.reflect.Method setHeightMethod = itemSelectionWidget.getClass().getMethod("setHeight", int.class);
                setHeightMethod.invoke(itemSelectionWidget, itemSelectionHeight);
            } catch (Exception e) {
                try {
                    java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                    heightField.setAccessible(true);
                    heightField.setInt(itemSelectionWidget, itemSelectionHeight);
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }
        
        // Update tags panel components (bottom-right)
        int tagsX = rightX;
        int tagsY = bottomY;
        
        // Calculate label text width for "Tags" to position search box after it
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.network.chat.Component tagsLabel = new TranslatableComponent("buildscape.config.tags");
        int tagsLabelWidth = mc.font.width(tagsLabel);
        int tagsLabelSpacing = BuildScapeConfigScreen.scaleSize(5);
        
        // Position tags search box after label - align with bottom of presets scrollbar on higher GUI scales
        // Calculate where the presets panel ends (presetsY + presetsHeight from init())
        int presetsBottomY = topY + sectionHeight;
        int tagsSearchBoxYOffset = Math.min(scaledOffset, Math.max(scaledOffset, presetsBottomY - tagsY));
        
        if (tagsSearchBox != null) {
            int tagsSearchBoxY = tagsY + tagsSearchBoxYOffset; // Align with end of presets scrollbar on higher scales
            int tagsSearchBoxX = tagsX + leftPadding + tagsLabelWidth + tagsLabelSpacing;
            int tagsSearchBoxWidth = rightPanelWidth - tagsLabelWidth - tagsLabelSpacing - scaledButtonArea - leftPadding * 2;
            tagsSearchBox.x = tagsSearchBoxX;
            tagsSearchBox.y = tagsSearchBoxY;
            tagsSearchBox.setWidth(tagsSearchBoxWidth);
        }
        
        // Position tags buttons after tags search box
        if (tagsInventoryButton != null && tagsAllButton != null && tagsModOnlyButton != null) {
            int buttonY = tagsSearchBox != null ? tagsSearchBox.y : tagsY + scaledOffset;
            // Buttons start after tags search box (which is after label)
            int buttonsStartX = (tagsSearchBox != null ? tagsSearchBox.x + tagsSearchBox.getWidth() : tagsX + leftPadding) + BuildScapeConfigScreen.scaleSize(10);
            int tagsButtonSize = BuildScapeConfigScreen.scaleSize(20);
            int tagsButtonSpacing = BuildScapeConfigScreen.scaleSize(5);
            
            tagsInventoryButton.x = buttonsStartX;
            tagsInventoryButton.y = buttonY;
            tagsInventoryButton.setWidth(tagsButtonSize);
            
            tagsAllButton.x = buttonsStartX + tagsButtonSize + tagsButtonSpacing;
            tagsAllButton.y = buttonY;
            tagsAllButton.setWidth(tagsButtonSize);
            
            tagsModOnlyButton.x = buttonsStartX + (tagsButtonSize + tagsButtonSpacing) * 2;
            tagsModOnlyButton.y = buttonY;
            tagsModOnlyButton.setWidth(tagsButtonSize);
        }
        
        // Update tags selector widget position below search box
        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            int tagsWidgetY = tagsSearchBox.y + searchBoxHeight + BuildScapeConfigScreen.scaleSize(5);
            int tagsWidgetHeight = sectionHeight - (tagsWidgetY - tagsY) - bottomPadding;
            tagsSelectorWidget.x = tagsX;
            tagsSelectorWidget.y = tagsWidgetY;
            tagsSelectorWidget.setWidth(rightPanelWidth);
            try {
                java.lang.reflect.Method setHeightMethod = tagsSelectorWidget.getClass().getMethod("setHeight", int.class);
                setHeightMethod.invoke(tagsSelectorWidget, tagsWidgetHeight);
            } catch (Exception e) {
                try {
                    java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                    heightField.setAccessible(true);
                    heightField.setInt(tagsSelectorWidget, tagsWidgetHeight);
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }
        
        // Update PresetsWidget internal button positions (including Create button)
        if (presetsWidget != null) {
            presetsWidget.updateChildPositions();
        }
        
        // Update search box position/width based on current label text
        updateSearchBoxForLabel();
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
    
    private void onSortModeChanged(SortToggleButton.SortType type) {
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
                // Cycle to next mod if clicking again
                if (itemSelectionWidget != null && 
                    itemSelectionWidget.getSortMode() == SortToggleButton.SortType.MOD_ONLY) {
                    cycleToNextMod();
                } else {
                    // Set to first mod
                    if (!availableModNamespaces.isEmpty()) {
                        currentModIndex = 0;
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
     * This ensures the search box auto-resizes when the label changes (e.g., "All items" -> "Inventory Items" -> "Mod Items").
     */
    private void updateSearchBoxForLabel() {
        if (itemSelectionWidget == null || searchBox == null) return;
        
        Minecraft mc = Minecraft.getInstance();
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
        
        // Calculate label width and update search box position/width
        int labelWidth = mc.font.width(labelText);
        int leftX = itemSelectionWidget.x;
        int leftPanelWidth = itemSelectionWidget.getWidth();
        
        int searchBoxX = leftX + leftPadding + labelWidth + labelSpacing;
        int searchBoxWidth = leftPanelWidth - labelWidth - labelSpacing - scaledButtonArea - leftPadding * 2;
        
        searchBox.x = searchBoxX;
        searchBox.setWidth(Math.max(50, searchBoxWidth)); // Ensure minimum width
        
        // Update button positions
        if (inventoryButton != null && allItemsButton != null && modOnlyButton != null) {
            int buttonSize = BuildScapeConfigScreen.scaleSize(20);
            int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
            int buttonsStartX = searchBox.x + searchBox.getWidth() + BuildScapeConfigScreen.scaleSize(10);
            
            inventoryButton.x = buttonsStartX;
            allItemsButton.x = buttonsStartX + buttonSize + buttonSpacing;
            modOnlyButton.x = buttonsStartX + (buttonSize + buttonSpacing) * 2;
        }
    }
    
    private void onTagsSortModeChanged(SortToggleButton.SortType type) {
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
        if (availableModNamespaces.isEmpty()) return;
        currentModIndex = (currentModIndex + 1) % availableModNamespaces.size();
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
                // Show confirmation message
                if (net.minecraft.client.Minecraft.getInstance().player != null) {
                    net.minecraft.client.Minecraft.getInstance().player.sendMessage(
                        new TranslatableComponent("buildscape.config.item_removed", itemId),
                        java.util.UUID.randomUUID()
                    );
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
                // Show confirmation message
                if (net.minecraft.client.Minecraft.getInstance().player != null) {
                    net.minecraft.client.Minecraft.getInstance().player.sendMessage(
                        new TranslatableComponent("buildscape.config.item_added", itemId),
                        java.util.UUID.randomUUID()
                    );
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
        }
        
        presetsConfig.saveUnnamedPreset(config.items);
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
            // Show confirmation message
            if (net.minecraft.client.Minecraft.getInstance().player != null) {
                net.minecraft.client.Minecraft.getInstance().player.sendMessage(
                    new TranslatableComponent("buildscape.config.item_removed", itemId),
                    java.util.UUID.randomUUID()
                );
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
        // Render AFTER widget to ensure it's visible on top with background for visibility
        if (existingItemsWidget != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 400); // Bring label to front with highest z-level
            int labelPadding = BuildScapeConfigScreen.scaleSize(5);
            int labelX = existingItemsWidget.x + labelPadding;
            int labelY = existingItemsWidget.y + labelPadding;
            net.minecraft.network.chat.Component pillarItemsLabel = new TranslatableComponent("buildscape.config.pillar_items");
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
            
            // Render with white color and scaling for high GUI scales (no background needed)
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
        
        // Render "All items" label aligned with search box on the same line
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
                    // Capitalize first letter
                    if (modName != null && !modName.isEmpty()) {
                        modName = modName.substring(0, 1).toUpperCase() + modName.substring(1);
                    }
                    labelText = new TranslatableComponent("buildscape.config.mod_items", modName);
                    break;
        }
        
        if (labelText == null) {
            labelText = new TranslatableComponent(labelKey);
        }
        
            // Position label on the same Y line as search box, aligned to left edge
            int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
            int labelX = itemSelectionWidget.x + labelPadding;
            int labelY = searchBox.y + (searchBoxHeight - mc.font.lineHeight) / 2; // Vertically center with search box
            
            mc.font.draw(poseStack, labelText, labelX, labelY, 0xFFFFFFFF);
            }
            
        // Render "Tags" label aligned with tags search box on the same line
        if (tagsSelectorWidget != null && tagsSearchBox != null) {
            net.minecraft.network.chat.Component tagsLabel = new TranslatableComponent("buildscape.config.tags");
            int searchBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
            
            // Position label on the same Y line as tags search box, aligned to left edge
            int tagsLabelX = tagsSelectorWidget.x + labelPadding;
            int tagsLabelY = tagsSearchBox.y + (searchBoxHeight - mc.font.lineHeight) / 2; // Vertically center with search box
            
            mc.font.draw(poseStack, tagsLabel, tagsLabelX, tagsLabelY, 0xFFFFFFFF);
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
        
        // Render tooltips AFTER all widgets to ensure they're on top
        // Disable any scissor tests that might clip tooltips
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();
        
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
        if (tagsSelectorWidget != null && tagsSelectorWidget.mouseClicked(mouseX, mouseY, button)) {
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
        if (tagsSelectorWidget != null && tagsSelectorWidget.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return false;
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
        if (presetsWidget != null && presetsWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return false;
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
        if (presetsWidget != null && presetsWidget.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox != null && searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (tagsSearchBox != null && tagsSearchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (presetsWidget != null && presetsWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBox != null && searchBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (tagsSearchBox != null && tagsSearchBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (presetsWidget != null && presetsWidget.charTyped(codePoint, modifiers)) {
            return true;
        }
        return false;
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
}
