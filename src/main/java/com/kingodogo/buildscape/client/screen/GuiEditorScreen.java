package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.GuiConfigData;
import com.kingodogo.buildscape.config.GuiConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visual drag-and-drop GUI editor screen for editing GUI layouts.
 * Allows dragging, resizing, and creating GUI elements visually.
 */
public class GuiEditorScreen extends Screen {
    private final Screen parentScreen;
    private final String tabName;
    private final AbstractConfigTab sourceTab;
    private final GuiConfigData config;
    private final GuiConfigManager configManager;
    
    // Visual editing mode
    private final List<VisualElement> visualElements = new ArrayList<>();
    private VisualElement selectedElement = null;
    private final List<VisualElement> selectedElements = new ArrayList<>(); // Multi-select support
    private String draggingElementId = null;
    private String resizingElementId = null;
    private ResizeHandle resizeHandle = null;
    private double dragStartX = 0;
    private double dragStartY = 0;
    private int dragStartElementX = 0;
    private int dragStartElementY = 0;
    private int dragStartElementWidth = 0;
    private int dragStartElementHeight = 0;
    
    private Button saveButton;
    private Button resetButton;
    private Button closeButton;
    private Button scanTabButton;
    private Button addWidgetButton;
    private Button deleteButton;
    private EditBox newWidgetNameField;
    private boolean showAddWidgetDialog = false;
    private boolean showPropertiesDialog = false;
    private EditBox searchTargetField;
    private Button savePropertiesButton;
    
    // Text editing
    private boolean showTextEditor = false;
    private EditBox textContentField;
    private Button saveTextButton;
    private VisualElement editingTextElement = null;
    
    // Properties editing fields
    private EditBox opacityField;
    private EditBox backgroundColorField;
    private EditBox borderColorField;
    private EditBox borderWidthField;
    private ResizeHandle hoveredHandle = ResizeHandle.NONE; // Track which handle is hovered
    private boolean isDragging = false; // Track if currently dragging
    private boolean isResizing = false; // Track if currently resizing
    private long lastClickTime = 0; // For double-click detection
    private VisualElement lastClickedElement = null; // For double-click detection
    
    // Toolbox system
    private enum EditorTool {
        MOVE("Move", "Drag elements to reposition", 'M'),
        EDIT("Edit", "Edit element properties", 'E'),
        EDIT_TEXT("Edit Text", "Edit text content", 'T'),
        CREATE_TEXT("Create Text", "Create new text label", 'N'),
        CREATE_WIDGET("Create Widget", "Create new widget", 'W'),
        CREATE_BACKGROUND("Create Background", "Create background rectangle with opacity", 'B'),
        CREATE_SEARCH_BAR("Create Search Bar", "Create search box widget", 'S'),
        CREATE_TEXT_BOX("Create Text Box", "Create rectangle with text inside", 'X'),
        SELECT_GROUP("Select Group", "Select multiple elements", 'G');
        
        final String displayName;
        final String description;
        final char key;
        
        EditorTool(String displayName, String description, char key) {
            this.displayName = displayName;
            this.description = description;
            this.key = key;
        }
    }
    private EditorTool currentTool = EditorTool.MOVE;
    private Button moveToolButton;
    private Button editToolButton;
    private Button editTextToolButton;
    private Button createTextToolButton;
    private Button createWidgetToolButton;
    private Button createBackgroundButton;
    private Button createSearchBarButton;
    private Button createTextBoxButton;
    private Button selectGroupToolButton;
    private final boolean toolboxExpanded = true; // Toolbox panel visibility
    
    // GUI scale support
    private int currentGuiScale = 2; // Default scale
    private Button scale1Button;
    private Button scale2Button;
    private Button scale3Button;
    private Button scale4Button;
    private Button autoScaleButton;
    
    // Layers panel (moveable)
    private static final int LAYERS_PANEL_WIDTH = 200;
    private int layersPanelX = 10;
    private int layersPanelY = 70;
    private int layersPanelScrollOffset = 0;
    private static final int LAYER_ITEM_HEIGHT = 20;
    private static final int LAYER_CHECKBOX_SIZE = 12;
    private boolean isDraggingLayersPanel = false;
    private double layersPanelDragStartX = 0;
    private double layersPanelDragStartY = 0;
    private int layersPanelDragStartPanelX = 0;
    private int layersPanelDragStartPanelY = 0;
    
    // Toolbox (moveable)
    private int toolboxX = 0; // Will be set in init()
    private int toolboxY = 10;
    private boolean isDraggingToolbox = false;
    private double toolboxDragStartX = 0;
    private double toolboxDragStartY = 0;
    private int toolboxDragStartXPos = 0;
    private int toolboxDragStartYPos = 0;
    
    private static final int HANDLE_SIZE = 10; // Increased for easier clicking
    private static final int BORDER_THICKNESS = 2;
    private static final int HANDLE_MARGIN = 8; // Margin for easier clicking
    
    /**
     * Visual representation of a GUI element
     */
    private static class VisualElement {
        String elementId;
        GuiConfigData.ElementConfig config;
        boolean selected = false;
        
        VisualElement(String elementId, GuiConfigData.ElementConfig config) {
            this.elementId = elementId;
            this.config = config != null ? new GuiConfigData.ElementConfig(config) : new GuiConfigData.ElementConfig(0, 0, 100, 20);
        }
    }
    
    /**
     * Resize handle positions
     */
    private enum ResizeHandle {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT
    }
    
    public GuiEditorScreen(Screen parent, String tabName) {
        this(parent, tabName, null);
    }
    
    public GuiEditorScreen(Screen parent, String tabName, AbstractConfigTab sourceTab) {
        super(new TranslatableComponent("buildscape.gui.editor.title", tabName));
        this.parentScreen = parent;
        this.tabName = tabName;
        this.sourceTab = sourceTab;
        this.configManager = GuiConfigManager.get();
        this.config = configManager.loadConfig(tabName);
        
        // Convert config elements to visual elements
        // Positions in config are relative to content area, but we need to convert to screen coordinates for editing
        int contentX = 0;
        int contentY = 0;
        if (parent instanceof BuildScapeConfigScreen configScreen) {
            contentX = configScreen.getContentX();
            contentY = configScreen.getContentY();
        }
        
        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : config.elements.entrySet()) {
            GuiConfigData.ElementConfig elementConfig = entry.getValue();
            
            // Check if this is a sidebar element (stored as screen-relative) or tab element (content-relative)
            boolean isSidebarElement = elementConfig.properties != null && 
                Boolean.TRUE.equals(elementConfig.properties.get("isSidebarElement"));
            
            // Convert from content-relative to screen coordinates for editing
            // Sidebar elements are already screen-relative, tab elements need offset added
            int screenX = isSidebarElement ? elementConfig.x : elementConfig.x + contentX;
            int screenY = isSidebarElement ? elementConfig.y : elementConfig.y + contentY;
            
            // For text elements, ensure proper size
            boolean isTextElement = elementConfig.properties != null && 
                Boolean.TRUE.equals(elementConfig.properties.get("isTextElement"));
            int elementWidth = elementConfig.width;
            int elementHeight = elementConfig.height;
            
            if (isTextElement && elementConfig.properties != null) {
                String text = (String) elementConfig.properties.get("text");
                if (text != null && !text.isEmpty()) {
                    int textWidth = font.width(text);
                    if (elementWidth < textWidth + 10) {
                        elementWidth = textWidth + 10;
                    }
                    if (elementHeight < 12) {
                        elementHeight = 12;
                    }
                }
            }
            
            GuiConfigData.ElementConfig screenConfig = new GuiConfigData.ElementConfig(
                screenX,
                screenY,
                elementWidth,
                elementHeight,
                elementConfig.scale
            );
            screenConfig.visible = elementConfig.visible;
            screenConfig.properties = elementConfig.properties != null ? 
                new HashMap<>(elementConfig.properties) : new HashMap<>();
            visualElements.add(new VisualElement(entry.getKey(), screenConfig));
        }
        
        // Also load sidebar elements if editing the main screen
        if (parent instanceof BuildScapeConfigScreen) {
            loadSidebarElements((BuildScapeConfigScreen) parent);
        }
    }
    
    /**
     * Load sidebar elements from the BuildScapeConfigScreen into the editor
     */
    private void loadSidebarElements(BuildScapeConfigScreen configScreen) {
        String sidebarConfigName = "Sidebar";
        GuiConfigData sidebarConfig = configManager.loadConfig(sidebarConfigName);
        
        // Register sidebar buttons if they don't exist
        if (sidebarConfig.elements.isEmpty()) {
            // Get current positions from the screen
            int sidebarX = 10;
            int sidebarY = 30;
            int buttonWidth = 180; // Default: SIDEBAR_WIDTH - 20
            int buttonHeight = 20; // Default: CATEGORY_BUTTON_HEIGHT
            int spacing = 2; // Default: CATEGORY_BUTTON_SPACING
            
            // Use reflection to get constants
            try {
                java.lang.reflect.Field sidebarWidthField = BuildScapeConfigScreen.class.getDeclaredField("SIDEBAR_WIDTH");
                sidebarWidthField.setAccessible(true);
                int sidebarWidth = sidebarWidthField.getInt(null);
                buttonWidth = sidebarWidth - 20;
                
                java.lang.reflect.Field buttonHeightField = BuildScapeConfigScreen.class.getDeclaredField("CATEGORY_BUTTON_HEIGHT");
                buttonHeightField.setAccessible(true);
                buttonHeight = buttonHeightField.getInt(null);
                
                java.lang.reflect.Field spacingField = BuildScapeConfigScreen.class.getDeclaredField("CATEGORY_BUTTON_SPACING");
                spacingField.setAccessible(true);
                spacing = spacingField.getInt(null);
            } catch (Exception e) {
                // Use defaults if reflection fails
            }
            
            Map<String, GuiConfigData.ElementConfig> sidebarDefaults = new HashMap<>();
            sidebarDefaults.put("pillarItemsButton", new GuiConfigData.ElementConfig(
                sidebarX, sidebarY, buttonWidth, buttonHeight));
            sidebarDefaults.put("pillarParticlesButton", new GuiConfigData.ElementConfig(
                sidebarX, sidebarY + buttonHeight + spacing, buttonWidth, buttonHeight));
            sidebarDefaults.put("pillarIdsButton", new GuiConfigData.ElementConfig(
                sidebarX, sidebarY + (buttonHeight + spacing) * 2, buttonWidth, buttonHeight));
            sidebarDefaults.put("editGuiButton", new GuiConfigData.ElementConfig(
                sidebarX, configScreen.height - 60, buttonWidth, 20));
            sidebarDefaults.put("kofiButton", new GuiConfigData.ElementConfig(
                sidebarX, configScreen.height - 30, buttonWidth, 20));
            
            // Mark all as sidebar elements
            for (Map.Entry<String, GuiConfigData.ElementConfig> entry : sidebarDefaults.entrySet()) {
                if (entry.getValue().properties == null) {
                    entry.getValue().properties = new HashMap<>();
                }
                entry.getValue().properties.put("isSidebarElement", true);
            }
            
            GuiConfigHelper.registerWidgetDefaults(sidebarConfigName, sidebarDefaults);
            sidebarConfig = configManager.loadConfig(sidebarConfigName);
        }
        
        // Add sidebar elements to visual editor (they're already screen-relative)
        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : sidebarConfig.elements.entrySet()) {
            GuiConfigData.ElementConfig elementConfig = entry.getValue();
            // Ensure sidebar flag is set
            if (elementConfig.properties == null) {
                elementConfig.properties = new HashMap<>();
            }
            elementConfig.properties.put("isSidebarElement", true);
            
            VisualElement visualElement = new VisualElement("sidebar." + entry.getKey(), elementConfig);
            visualElements.add(visualElement);
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Bottom buttons
        int buttonY = height - 30;
        int buttonWidth = 100;
        int buttonSpacing = 110;
        
        saveButton = new Button(
            width / 2 - buttonSpacing - buttonWidth / 2, buttonY,
            buttonWidth, 20,
            new TranslatableComponent("buildscape.gui.editor.save"),
            (button) -> saveConfig()
        );
        addRenderableWidget(saveButton);
        
        resetButton = new Button(
            width / 2 - buttonWidth / 2, buttonY,
            buttonWidth, 20,
            new TranslatableComponent("buildscape.gui.editor.reset"),
            (button) -> resetConfig()
        );
        addRenderableWidget(resetButton);
        
        closeButton = new Button(
            width / 2 + buttonSpacing - buttonWidth / 2, buttonY,
            buttonWidth, 20,
            new TranslatableComponent("gui.cancel"),
            (button) -> onClose()
        );
        addRenderableWidget(closeButton);
        
        // Compact Toolbox Panel (top right) - single letter buttons (moveable)
        toolboxX = width - 50; // Initialize default position
        toolboxY = 10;
        int toolButtonSize = 20; // Square buttons
        int toolSpacing = 2;
        
        // Create compact single-letter tool buttons in a vertical panel
        moveToolButton = new Button(
            toolboxX, toolboxY,
            toolButtonSize, toolButtonSize,
            new TextComponent("M"),
            (button) -> setCurrentTool(EditorTool.MOVE)
        );
        addRenderableWidget(moveToolButton);
        
        editToolButton = new Button(
            toolboxX, toolboxY + toolButtonSize + toolSpacing,
            toolButtonSize, toolButtonSize,
            new TextComponent("E"),
            (button) -> setCurrentTool(EditorTool.EDIT)
        );
        addRenderableWidget(editToolButton);
        
        editTextToolButton = new Button(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 2,
            toolButtonSize, toolButtonSize,
            new TextComponent("T"),
            (button) -> setCurrentTool(EditorTool.EDIT_TEXT)
        );
        addRenderableWidget(editTextToolButton);
        
        createTextToolButton = new Button(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 3,
            toolButtonSize, toolButtonSize,
            new TextComponent("N"),
            (button) -> setCurrentTool(EditorTool.CREATE_TEXT)
        );
        addRenderableWidget(createTextToolButton);
        
        createWidgetToolButton = new Button(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 4,
            toolButtonSize, toolButtonSize,
            new TextComponent("W"),
            (button) -> {
                setCurrentTool(EditorTool.CREATE_WIDGET);
                showAddWidgetDialog = true;
            }
        );
        addRenderableWidget(createWidgetToolButton);
        
        // Create Background button
        createBackgroundButton = new Button(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 5,
            toolButtonSize, toolButtonSize,
            new TextComponent("B"),
            (button) -> setCurrentTool(EditorTool.CREATE_BACKGROUND)
        );
        addRenderableWidget(createBackgroundButton);
        
        // Create Search Bar button
        createSearchBarButton = new Button(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 6,
            toolButtonSize, toolButtonSize,
            new TextComponent("S"),
            (button) -> setCurrentTool(EditorTool.CREATE_SEARCH_BAR)
        );
        addRenderableWidget(createSearchBarButton);
        
        // Create Text Box button
        createTextBoxButton = new Button(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 7,
            toolButtonSize, toolButtonSize,
            new TextComponent("X"),
            (button) -> setCurrentTool(EditorTool.CREATE_TEXT_BOX)
        );
        addRenderableWidget(createTextBoxButton);
        
        selectGroupToolButton = new Button(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 8,
            toolButtonSize, toolButtonSize,
            new TextComponent("G"),
            (button) -> setCurrentTool(EditorTool.SELECT_GROUP)
        );
        addRenderableWidget(selectGroupToolButton);
        
        // GUI Scale buttons (below toolbox, horizontal)
        int scaleY = toolboxY + (toolButtonSize + toolSpacing) * 9 + 5;
        int scaleX = toolboxX - 180; // Position to the left of tool buttons
        scale1Button = new Button(
            scaleX, scaleY,
            35, toolButtonSize,
            new TextComponent("1x"),
            (button) -> setGuiScale(1)
        );
        addRenderableWidget(scale1Button);
        
        scale2Button = new Button(
            scaleX + 37, scaleY,
            35, toolButtonSize,
            new TextComponent("2x"),
            (button) -> setGuiScale(2)
        );
        addRenderableWidget(scale2Button);
        
        scale3Button = new Button(
            scaleX + 74, scaleY,
            35, toolButtonSize,
            new TextComponent("3x"),
            (button) -> setGuiScale(3)
        );
        addRenderableWidget(scale3Button);
        
        scale4Button = new Button(
            scaleX + 111, scaleY,
            35, toolButtonSize,
            new TextComponent("4x"),
            (button) -> setGuiScale(4)
        );
        addRenderableWidget(scale4Button);
        
        autoScaleButton = new Button(
            scaleX + 148, scaleY,
            50, toolButtonSize,
            new TextComponent("Auto"),
            (button) -> setAutoScale()
        );
        addRenderableWidget(autoScaleButton);
        
        // Add widget button (legacy, now handled by toolbox)
        addWidgetButton = new Button(
            width - 120, height - 100,
            110, 20,
            new TranslatableComponent("buildscape.gui.editor.add_widget"),
            (button) -> {
                setCurrentTool(EditorTool.CREATE_WIDGET);
                showAddWidgetDialog = true;
            }
        );
        addRenderableWidget(addWidgetButton);
        
        updateToolButtons();
        
        // Delete button (when element selected)
        deleteButton = new Button(
            width - 120, 35,
            110, 20,
            new TranslatableComponent("buildscape.gui.editor.delete"),
            (button) -> {
                if (!selectedElements.isEmpty()) {
                    deleteSelectedElements();
                } else if (selectedElement != null) {
                    deleteSelectedElement();
                }
            }
        );
        deleteButton.visible = false;
        addRenderableWidget(deleteButton);
        
        // Scan button (if no elements and we have source tab)
        if (sourceTab != null && visualElements.isEmpty()) {
            scanTabButton = new Button(
                width / 2 - 150, height / 2,
                300, 20,
                new TranslatableComponent("buildscape.gui.editor.scantab"),
                (button) -> scanCurrentTab()
            );
            addRenderableWidget(scanTabButton);
        }
        
        // New widget name field (for add widget dialog)
        newWidgetNameField = new EditBox(
            font, width / 2 - 100, height / 2 - 20,
            200, 20,
            new TextComponent("Widget Name")
        );
        newWidgetNameField.setMaxLength(64);
        newWidgetNameField.visible = false;
        addRenderableWidget(newWidgetNameField);
        
        // Properties dialog fields
        searchTargetField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            new TextComponent("Search Target Widget ID")
        );
        searchTargetField.setMaxLength(64);
        searchTargetField.visible = false;
        addRenderableWidget(searchTargetField);
        
        savePropertiesButton = new Button(
            width / 2 - 50, height / 2 + 35,
            100, 20,
            new TranslatableComponent("buildscape.gui.editor.save"),
            (button) -> saveElementProperties()
        );
        savePropertiesButton.visible = false;
        addRenderableWidget(savePropertiesButton);
        
        // Properties editing fields
        opacityField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            new TextComponent("Opacity (0-255)")
        );
        opacityField.setMaxLength(3);
        opacityField.setFilter(s -> s.matches("[0-9]*"));
        opacityField.visible = false;
        addRenderableWidget(opacityField);
        
        backgroundColorField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            new TextComponent("Background Color (RRGGBB)")
        );
        backgroundColorField.setMaxLength(6);
        backgroundColorField.setFilter(s -> s.matches("[0-9a-fA-F]*"));
        backgroundColorField.visible = false;
        addRenderableWidget(backgroundColorField);
        
        borderColorField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            new TextComponent("Border Color (RRGGBB)")
        );
        borderColorField.setMaxLength(6);
        borderColorField.setFilter(s -> s.matches("[0-9a-fA-F]*"));
        borderColorField.visible = false;
        addRenderableWidget(borderColorField);
        
        borderWidthField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            new TextComponent("Border Width")
        );
        borderWidthField.setMaxLength(3);
        borderWidthField.setFilter(s -> s.matches("[0-9]*"));
        borderWidthField.visible = false;
        addRenderableWidget(borderWidthField);
        
        // Text editor fields
        textContentField = new EditBox(
            font, width / 2 - 150, height / 2 - 10,
            300, 20,
            new TextComponent("Text Content")
        );
        textContentField.setMaxLength(256);
        textContentField.visible = false;
        addRenderableWidget(textContentField);
        
        saveTextButton = new Button(
            width / 2 - 50, height / 2 + 15,
            100, 20,
            new TranslatableComponent("buildscape.gui.editor.save"),
            (button) -> saveTextContent()
        );
        saveTextButton.visible = false;
        addRenderableWidget(saveTextButton);
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        
        // Apply GUI scale transformation if needed
        float scaleFactor = currentGuiScale / 2.0f; // Normalize to default scale of 2
        int scaledMouseX = mouseX;
        int scaledMouseY = mouseY;
        
        if (scaleFactor != 1.0f) {
            poseStack.pushPose();
            poseStack.scale(scaleFactor, scaleFactor, 1.0f);
            scaledMouseX = (int)(mouseX / scaleFactor);
            scaledMouseY = (int)(mouseY / scaleFactor);
        }
        
        // Render the actual tab in the background (dimmed) so user can see what they're editing
        if (sourceTab != null && parentScreen instanceof BuildScapeConfigScreen configScreen) {
            poseStack.pushPose();
            // Dim the background tab
            fill(poseStack, 0, 0, width, height, 0x80000000);
            poseStack.popPose();
            
            // Render the tab at reduced opacity
            poseStack.pushPose();
            poseStack.translate(0, 0, -100); // Render behind editor UI
            sourceTab.render(poseStack, scaledMouseX, scaledMouseY, partialTick);
            poseStack.popPose();
        }
        
        // Title
        drawCenteredString(poseStack, font, title, width / 2, 10, 0xFFFFFF);
        
        // Instructions
        String instructions = new TranslatableComponent("buildscape.gui.editor.instructions_visual").getString();
        drawString(poseStack, font, instructions, 10, 25, 0xCCCCCC);
        
        // Additional hints with current tool info
        String toolHint = "Tool: " + currentTool.displayName + " - " + currentTool.description;
        if (selectedElement != null) {
            boolean isTextElement = selectedElement.config.properties != null && 
                Boolean.TRUE.equals(selectedElement.config.properties.get("isTextElement"));
            String hint = "Selected: " + selectedElement.elementId + 
                " | Drag to move | Drag handles to resize | ";
            if (isTextElement || currentTool == EditorTool.EDIT_TEXT) {
                hint += "Double-click to edit text | ";
            } else {
                hint += "Double-click for properties | ";
            }
            hint += "Delete key to remove";
            drawString(poseStack, font, hint, 10, 40, 0xAAAAAA);
            drawString(poseStack, font, toolHint, 10, 55, 0x888888);
        } else {
            String hint = "Right-click empty space to create new widget | " + toolHint;
            if (currentTool == EditorTool.EDIT_TEXT) {
                hint += " | Click any element to edit its text";
            }
            drawString(poseStack, font, hint, 10, 40, 0xAAAAAA);
        }
        
        // Render compact toolbox panel background
        if (toolboxExpanded) {
            int toolboxW = 22;
            int toolboxH = 132; // 6 buttons * 22px each
            fill(poseStack, toolboxX - 1, toolboxY - 1, toolboxX + toolboxW, toolboxY + toolboxH, 0xCC000000);
            fill(poseStack, toolboxX - 1, toolboxY - 1, toolboxX + toolboxW, toolboxY, 0xFFFFFFFF);
            fill(poseStack, toolboxX - 1, toolboxY + toolboxH - 1, toolboxX + toolboxW, toolboxY + toolboxH, 0xFFFFFFFF);
            fill(poseStack, toolboxX - 1, toolboxY - 1, toolboxX, toolboxY + toolboxH, 0xFFFFFFFF);
            fill(poseStack, toolboxX + toolboxW - 1, toolboxY - 1, toolboxX + toolboxW, toolboxY + toolboxH, 0xFFFFFFFF);
        }
        
        // Render tooltips for toolbox buttons on hover
        int toolButtonSize = 20;
        int toolSpacing = 2;
        int numTools = EditorTool.values().length;
        
        if (mouseX >= toolboxX && mouseX < toolboxX + toolButtonSize && 
            mouseY >= toolboxY && mouseY < toolboxY + (toolButtonSize + toolSpacing) * numTools) {
            int toolIndex = (mouseY - toolboxY) / (toolButtonSize + toolSpacing);
            if (toolIndex >= 0 && toolIndex < numTools) {
                EditorTool hoveredTool = EditorTool.values()[toolIndex];
                String tooltip = hoveredTool.displayName + " - " + hoveredTool.description;
                int tooltipWidth = font.width(tooltip);
                int tooltipX = toolboxX - tooltipWidth - 10;
                int tooltipY = toolboxY + toolIndex * (toolButtonSize + toolSpacing);
                fill(poseStack, tooltipX - 4, tooltipY - 2, tooltipX + tooltipWidth + 4, tooltipY + 10, 0xDD000000);
                drawString(poseStack, font, tooltip, tooltipX, tooltipY, 0xFFFFFF);
            }
        }
        
        // Render layers panel on the left
        renderLayersPanel(poseStack, mouseX, mouseY);
        
        // Render visual elements (render selected last so it's on top)
        // Skip hidden elements
        List<VisualElement> sortedElements = new ArrayList<>();
        for (VisualElement element : visualElements) {
            if (isElementVisible(element)) {
                sortedElements.add(element);
            }
        }
        sortedElements.sort((a, b) -> {
            if (a.selected && !b.selected) return 1;
            if (!a.selected && b.selected) return -1;
            return 0;
        });
        
        for (VisualElement element : sortedElements) {
            renderVisualElement(poseStack, element, scaledMouseX, scaledMouseY);
        }
        
        // Update hovered handle
        hoveredHandle = ResizeHandle.NONE;
        if (selectedElement != null) {
            hoveredHandle = getResizeHandle(selectedElement, scaledMouseX, scaledMouseY);
        }
        
        // Render add widget dialog
        if (showAddWidgetDialog) {
            renderAddWidgetDialog(poseStack, scaledMouseX, scaledMouseY);
        }
        
        // Render properties dialog
        if (showPropertiesDialog && selectedElement != null) {
            renderPropertiesDialog(poseStack, scaledMouseX, scaledMouseY);
        }
        
        // Render text editor dialog
        if (showTextEditor && editingTextElement != null) {
            renderTextEditorDialog(poseStack, scaledMouseX, scaledMouseY);
        }
        
        // Pop scale transformation if applied
        if (scaleFactor != 1.0f) {
            poseStack.popPose();
        }
        
        // Show message if no elements
        if (visualElements.isEmpty() && (sourceTab == null || scanTabButton == null)) {
            drawCenteredString(poseStack, font, 
                new TranslatableComponent("buildscape.gui.editor.no_elements").getString(), 
                width / 2, height / 2 - 20, 0xCCCCCC);
        }
        
        // Show mouse coordinates and current action
        String coordText = String.format("Mouse: %d, %d | Scale: %dx", scaledMouseX, scaledMouseY, currentGuiScale);
        if (selectedElement != null) {
            coordText += String.format(" | Element: %d, %d (%dx%d)", 
                selectedElement.config.x, selectedElement.config.y,
                selectedElement.config.width, selectedElement.config.height);
        }
        if (!selectedElements.isEmpty()) {
            coordText += String.format(" | Selected: %d", selectedElements.size());
        }
        if (isDragging) {
            coordText += " | DRAGGING";
        } else if (isResizing) {
            coordText += " | RESIZING";
        }
        int textWidth = font.width(coordText);
        fill(poseStack, width - textWidth - 10, height - 50, width, height - 30, 0x80000000);
        drawString(poseStack, font, coordText, width - textWidth - 8, height - 48, 0xFFFFFF);
        
        // Render alignment guides when dragging/resizing
        if ((isDragging || isResizing) && selectedElement != null) {
            renderAlignmentGuides(poseStack, selectedElement);
        }
        
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
    
    private void renderAlignmentGuides(PoseStack poseStack, VisualElement element) {
        int x = element.config.x;
        int y = element.config.y;
        int w = element.config.width;
        int h = element.config.height;
        
        // Draw alignment lines to other elements
        for (VisualElement other : visualElements) {
            if (other == element) continue;
            if (!isElementVisible(other)) continue; // Skip hidden elements
            
            int ox = other.config.x;
            int oy = other.config.y;
            int ow = other.config.width;
            int oh = other.config.height;
            
            // Vertical alignment guides
            if (Math.abs(x - ox) < 5 || Math.abs(x - (ox + ow)) < 5 || 
                Math.abs((x + w) - ox) < 5 || Math.abs((x + w) - (ox + ow)) < 5) {
                fill(poseStack, x, 0, x + 1, height, 0x4000FF00);
            }
            
            // Horizontal alignment guides
            if (Math.abs(y - oy) < 5 || Math.abs(y - (oy + oh)) < 5 ||
                Math.abs((y + h) - oy) < 5 || Math.abs((y + h) - (oy + oh)) < 5) {
                fill(poseStack, 0, y, width, y + 1, 0x4000FF00);
            }
        }
    }
    
    private void renderVisualElement(PoseStack poseStack, VisualElement element, int mouseX, int mouseY) {
        // Get size for current GUI scale (supports per-scale sizing)
        int w = getElementWidthForScale(element, currentGuiScale);
        int h = getElementHeightForScale(element, currentGuiScale);
        int x = element.config.x;
        int y = element.config.y;
        
        // For text elements, adjust size based on text width if needed
        boolean isTextElement = element.config.properties != null && 
            Boolean.TRUE.equals(element.config.properties.get("isTextElement"));
        if (isTextElement && element.config.properties != null) {
            String text = (String) element.config.properties.get("text");
            if (text != null && !text.isEmpty()) {
                int textWidth = font.width(text);
                // Ensure width is at least text width + padding
                if (w < textWidth + 10) {
                    w = textWidth + 10;
                }
                // Text elements have a minimum height
                if (h < 12) {
                    h = 12;
                }
            }
        }
        
        // Determine if mouse is over this element (but not over a handle)
        boolean mouseOver = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        ResizeHandle handle = element.selected ? getResizeHandle(element, mouseX, mouseY) : ResizeHandle.NONE;
        if (handle != ResizeHandle.NONE) {
            mouseOver = false; // Don't highlight border if hovering handle
        }
        
        // Border color: selected = yellow, mouse over = cyan, normal = white
        int borderColor = element.selected ? 0xFFFFFF00 : (mouseOver ? 0xFF00FFFF : 0xFFFFFFFF);
        int fillColor = element.selected ? 0x50FFFFFF : 0x20FFFFFF;
        
        // Make dragging/resizing elements more visible
        if ((isDragging && draggingElementId != null && draggingElementId.equals(element.elementId)) ||
            (isResizing && resizingElementId != null && resizingElementId.equals(element.elementId))) {
            fillColor = 0x60FFFFFF;
            borderColor = 0xFFFF00FF; // Magenta when actively dragging/resizing
        }
        
        // Check if this is a background element
        boolean isBackground = element.config.properties != null && 
            Boolean.TRUE.equals(element.config.properties.get("isBackground"));
        boolean isSearchBar = element.config.properties != null && 
            Boolean.TRUE.equals(element.config.properties.get("isSearchBar"));
        boolean isTextBox = element.config.properties != null && 
            Boolean.TRUE.equals(element.config.properties.get("isTextBox"));
        
        // Render background fill with opacity if it's a background element
        if (isBackground || isSearchBar || isTextBox) {
            int bgOpacity = 192; // Default opacity (0xC0)
            int bgColor = 0x101010; // Default dark gray
            int elementBorderColor = 0xFFFFFF; // Default white border (renamed to avoid conflict)
            int borderWidth = 1;
            
            if (element.config.properties != null) {
                // Get opacity (0-255)
                if (element.config.properties.containsKey("opacity")) {
                    Object opacityObj = element.config.properties.get("opacity");
                    if (opacityObj instanceof Number) {
                        bgOpacity = ((Number) opacityObj).intValue();
                    } else if (opacityObj instanceof String) {
                        try {
                            bgOpacity = Integer.parseInt((String) opacityObj);
                        } catch (NumberFormatException e) {}
                    }
                }
                
                // Get background color (RRGGBB hex)
                if (element.config.properties.containsKey("backgroundColor")) {
                    String colorStr = (String) element.config.properties.get("backgroundColor");
                    if (colorStr != null && !colorStr.isEmpty()) {
                        try {
                            bgColor = Integer.parseInt(colorStr, 16);
                        } catch (NumberFormatException e) {}
                    }
                }
                
                // Get border color
                if (element.config.properties.containsKey("borderColor")) {
                    String colorStr = (String) element.config.properties.get("borderColor");
                    if (colorStr != null && !colorStr.isEmpty()) {
                        try {
                            elementBorderColor = Integer.parseInt(colorStr, 16);
                        } catch (NumberFormatException e) {}
                    }
                }
                
                // Get border width
                if (element.config.properties.containsKey("borderWidth")) {
                    Object widthObj = element.config.properties.get("borderWidth");
                    if (widthObj instanceof Number) {
                        borderWidth = ((Number) widthObj).intValue();
                    } else if (widthObj instanceof String) {
                        try {
                            borderWidth = Integer.parseInt((String) widthObj);
                        } catch (NumberFormatException e) {}
                    }
                }
            }
            
            // Combine opacity with color (ARGB format)
            int finalBgColor = (bgOpacity << 24) | bgColor;
            fill(poseStack, x, y, x + w, y + h, finalBgColor);
            
            // Render border if width > 0
            if (borderWidth > 0) {
                int finalBorderColor = (0xFF << 24) | elementBorderColor;
                // Top
                fill(poseStack, x, y, x + w, y + borderWidth, finalBorderColor);
                // Bottom
                fill(poseStack, x, y + h - borderWidth, x + w, y + h, finalBorderColor);
                // Left
                fill(poseStack, x, y, x + borderWidth, y + h, finalBorderColor);
                // Right
                fill(poseStack, x + w - borderWidth, y, x + w, y + h, finalBorderColor);
            }
        } else {
            // Regular fill for non-background elements
        fill(poseStack, x, y, x + w, y + h, fillColor);
        }
        
        // For text elements, render the text inside
        if (isTextElement && element.config.properties != null) {
            String text = (String) element.config.properties.get("text");
            if (text != null && !text.isEmpty()) {
                // Render text with color codes support
                String processedText = text.replaceAll("&([0-9a-fk-or])", "§$1");
                font.draw(poseStack, processedText, x + 2, y + 2, 0xFFFFFF);
            }
        }
        
        // For text boxes, render text inside
        if (isTextBox && element.config.properties != null) {
            String text = (String) element.config.properties.get("text");
            if (text != null && !text.isEmpty()) {
                String processedText = text.replaceAll("&([0-9a-fk-or])", "§$1");
                font.draw(poseStack, processedText, x + 5, y + h / 2 - 4, 0xFFFFFF);
            }
        }
        
        // For search bars, render placeholder text
        if (isSearchBar && element.config.properties != null) {
            String placeholder = (String) element.config.properties.get("placeholder");
            if (placeholder == null || placeholder.isEmpty()) {
                placeholder = "Search...";
            }
            int textColor = element.selected ? 0xFFFFFF : 0x888888;
            font.draw(poseStack, placeholder, x + 5, y + h / 2 - 4, textColor);
        }
        
        // Border
        // Top
        fill(poseStack, x, y, x + w, y + BORDER_THICKNESS, borderColor);
        // Bottom
        fill(poseStack, x, y + h - BORDER_THICKNESS, x + w, y + h, borderColor);
        // Left
        fill(poseStack, x, y, x + BORDER_THICKNESS, y + h, borderColor);
        // Right
        fill(poseStack, x + w - BORDER_THICKNESS, y, x + w, y + h, borderColor);
        
        // Element ID label
        int labelY = y - 10;
        if (labelY < 0) labelY = y + h + 2;
        fill(poseStack, x, labelY - 8, x + font.width(element.elementId) + 4, labelY + 8, 0xCC000000);
        font.draw(poseStack, element.elementId, x + 2, labelY - 4, 0xFFFFFF);
        
        // Resize handles (only when selected)
        if (element.selected) {
            renderResizeHandles(poseStack, element, handle == ResizeHandle.NONE ? hoveredHandle : handle);
        }
        
        // Size label (bottom right corner)
        String sizeText = w + "x" + h;
        int textWidth = font.width(sizeText);
        fill(poseStack, x + w - textWidth - 4, y + h - 12, x + w, y + h, 0xCC000000);
        font.draw(poseStack, sizeText, x + w - textWidth - 2, y + h - 10, 0xCCCCCC);
    }
    
    private void renderResizeHandles(PoseStack poseStack, VisualElement element, ResizeHandle hoveredHandle) {
        int x = element.config.x;
        int y = element.config.y;
        int w = element.config.width;
        int h = element.config.height;
        int hs = HANDLE_SIZE / 2;
        
        // Corner handles
        renderHandle(poseStack, x - hs, y - hs, hoveredHandle == ResizeHandle.TOP_LEFT);
        renderHandle(poseStack, x + w - hs, y - hs, hoveredHandle == ResizeHandle.TOP_RIGHT);
        renderHandle(poseStack, x - hs, y + h - hs, hoveredHandle == ResizeHandle.BOTTOM_LEFT);
        renderHandle(poseStack, x + w - hs, y + h - hs, hoveredHandle == ResizeHandle.BOTTOM_RIGHT);
        
        // Edge handles
        renderHandle(poseStack, x + w / 2 - hs, y - hs, hoveredHandle == ResizeHandle.TOP);
        renderHandle(poseStack, x + w / 2 - hs, y + h - hs, hoveredHandle == ResizeHandle.BOTTOM);
        renderHandle(poseStack, x - hs, y + h / 2 - hs, hoveredHandle == ResizeHandle.LEFT);
        renderHandle(poseStack, x + w - hs, y + h / 2 - hs, hoveredHandle == ResizeHandle.RIGHT);
    }
    
    private void renderHandle(PoseStack poseStack, int x, int y, boolean hovered) {
        // Larger, more visible handles
        int outerSize = HANDLE_SIZE + 2;
        int color = hovered ? 0xFFFFFFFF : 0xFF00FFFF;
        
        // Outer glow when hovered
        if (hovered) {
            fill(poseStack, x - 1, y - 1, x + outerSize + 1, y + outerSize + 1, 0x80FFFFFF);
        }
        
        // Main handle
        fill(poseStack, x, y, x + outerSize, y + outerSize, color);
        fill(poseStack, x + 2, y + 2, x + outerSize - 2, y + outerSize - 2, 0xFF000000);
    }
    
    private ResizeHandle getResizeHandle(VisualElement element, int mouseX, int mouseY) {
        if (!element.selected) return ResizeHandle.NONE;
        
        int x = element.config.x;
        int y = element.config.y;
        int w = element.config.width;
        int h = element.config.height;
        int hs = HANDLE_SIZE / 2;
        int margin = HANDLE_MARGIN; // Larger margin for easier clicking
        
        // Check corners first (they have priority)
        if (mouseX >= x - hs - margin && mouseX < x + hs + margin &&
            mouseY >= y - hs - margin && mouseY < y + hs + margin) return ResizeHandle.TOP_LEFT;
        if (mouseX >= x + w - hs - margin && mouseX < x + w + hs + margin &&
            mouseY >= y - hs - margin && mouseY < y + hs + margin) return ResizeHandle.TOP_RIGHT;
        if (mouseX >= x - hs - margin && mouseX < x + hs + margin &&
            mouseY >= y + h - hs - margin && mouseY < y + h + hs + margin) return ResizeHandle.BOTTOM_LEFT;
        if (mouseX >= x + w - hs - margin && mouseX < x + w + hs + margin &&
            mouseY >= y + h - hs - margin && mouseY < y + h + hs + margin) return ResizeHandle.BOTTOM_RIGHT;
        
        // Check edges
        if (mouseX >= x + w / 2 - hs - margin && mouseX < x + w / 2 + hs + margin &&
            mouseY >= y - hs - margin && mouseY < y + hs + margin) return ResizeHandle.TOP;
        if (mouseX >= x + w / 2 - hs - margin && mouseX < x + w / 2 + hs + margin &&
            mouseY >= y + h - hs - margin && mouseY < y + h + hs + margin) return ResizeHandle.BOTTOM;
        if (mouseX >= x - hs - margin && mouseX < x + hs + margin &&
            mouseY >= y + h / 2 - hs - margin && mouseY < y + h / 2 + hs + margin) return ResizeHandle.LEFT;
        if (mouseX >= x + w - hs - margin && mouseX < x + w + hs + margin &&
            mouseY >= y + h / 2 - hs - margin && mouseY < y + h / 2 + hs + margin) return ResizeHandle.RIGHT;
        
        return ResizeHandle.NONE;
    }
    
    private void renderAddWidgetDialog(PoseStack poseStack, int mouseX, int mouseY) {
        int dialogX = width / 2 - 120;
        int dialogY = height / 2 - 40;
        int dialogW = 240;
        int dialogH = 80;
        
        // Background
        fill(poseStack, dialogX, dialogY, dialogX + dialogW, dialogY + dialogH, 0xDD000000);
        fill(poseStack, dialogX, dialogY, dialogX + dialogW, dialogY + 1, 0xFFFFFFFF);
        fill(poseStack, dialogX, dialogY + dialogH - 1, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStack, dialogX, dialogY, dialogX + 1, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStack, dialogX + dialogW - 1, dialogY, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        
        // Title
        drawCenteredString(poseStack, font, 
            new TranslatableComponent("buildscape.gui.editor.add_widget_title").getString(),
            width / 2, dialogY + 5, 0xFFFFFF);
        
        // Instructions
        drawCenteredString(poseStack, font,
            new TranslatableComponent("buildscape.gui.editor.enter_name").getString(),
            width / 2, dialogY + 25, 0xCCCCCC);
        
        // Show name field
        newWidgetNameField.visible = true;
        newWidgetNameField.y = dialogY + 35;
    }
    
    private void renderPropertiesDialog(PoseStack poseStack, int mouseX, int mouseY) {
        if (selectedElement == null) return;
        
        int dialogX = width / 2 - 150;
        int dialogY = height / 2 - 80;
        int dialogW = 300;
        int dialogH = 160;
        
        // Background
        fill(poseStack, dialogX, dialogY, dialogX + dialogW, dialogY + dialogH, 0xDD000000);
        fill(poseStack, dialogX, dialogY, dialogX + dialogW, dialogY + 1, 0xFFFFFFFF);
        fill(poseStack, dialogX, dialogY + dialogH - 1, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStack, dialogX, dialogY, dialogX + 1, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStack, dialogX + dialogW - 1, dialogY, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        
        // Title
        drawCenteredString(poseStack, font, 
            "Edit Properties: " + selectedElement.elementId,
            width / 2, dialogY + 5, 0xFFFFFF);
        
        // Check element type
        boolean isSearchBox = selectedElement.elementId.contains("searchBox") || 
            selectedElement.elementId.contains("SearchBox");
        boolean isBackground = selectedElement.config.properties != null && 
            Boolean.TRUE.equals(selectedElement.config.properties.get("isBackground"));
        boolean isSearchBar = selectedElement.config.properties != null && 
            Boolean.TRUE.equals(selectedElement.config.properties.get("isSearchBar"));
        boolean isTextBox = selectedElement.config.properties != null && 
            Boolean.TRUE.equals(selectedElement.config.properties.get("isTextBox"));
        boolean hasCustomProperties = isBackground || isSearchBar || isTextBox || isSearchBox;
        
        int yOffset = 30;
        
        if (isSearchBox) {
            // Search target field
            drawString(poseStack, font, "Search Target Widget ID:", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            searchTargetField.visible = true;
            searchTargetField.x = dialogX + 10;
            searchTargetField.y = dialogY + yOffset + 15;
            searchTargetField.setWidth(dialogW - 20);
            
            if (selectedElement.config.properties != null && 
                selectedElement.config.properties.containsKey("searchTarget")) {
                String currentTarget = (String) selectedElement.config.properties.get("searchTarget");
                if (!searchTargetField.getValue().equals(currentTarget)) {
                    searchTargetField.setValue(currentTarget);
                }
            } else {
                if (searchTargetField.getValue().isEmpty()) {
                    searchTargetField.setValue("itemSelectionWidget");
                }
            }
            yOffset += 50;
        } else {
            searchTargetField.visible = false;
        }
        
        if (isBackground || isSearchBar || isTextBox) {
            // Opacity field
            drawString(poseStack, font, "Opacity (0-255):", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            opacityField.visible = true;
            opacityField.x = dialogX + 10;
            opacityField.y = dialogY + yOffset + 15;
            opacityField.setWidth(dialogW - 20);
            
            if (selectedElement.config.properties != null && 
                selectedElement.config.properties.containsKey("opacity")) {
                Object opacityObj = selectedElement.config.properties.get("opacity");
                opacityField.setValue(opacityObj != null ? opacityObj.toString() : "192");
            } else {
                opacityField.setValue("192");
            }
            yOffset += 45;
            
            // Background color field
            drawString(poseStack, font, "Background Color (RRGGBB):", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            backgroundColorField.visible = true;
            backgroundColorField.x = dialogX + 10;
            backgroundColorField.y = dialogY + yOffset + 15;
            backgroundColorField.setWidth(dialogW - 20);
            
            if (selectedElement.config.properties != null && 
                selectedElement.config.properties.containsKey("backgroundColor")) {
                String color = (String) selectedElement.config.properties.get("backgroundColor");
                backgroundColorField.setValue(color != null ? color : "101010");
            } else {
                backgroundColorField.setValue("101010");
            }
            yOffset += 45;
            
            // Border color field
            drawString(poseStack, font, "Border Color (RRGGBB):", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            borderColorField.visible = true;
            borderColorField.x = dialogX + 10;
            borderColorField.y = dialogY + yOffset + 15;
            borderColorField.setWidth(dialogW - 20);
            
            if (selectedElement.config.properties != null && 
                selectedElement.config.properties.containsKey("borderColor")) {
                String color = (String) selectedElement.config.properties.get("borderColor");
                borderColorField.setValue(color != null ? color : "FFFFFF");
            } else {
                borderColorField.setValue("FFFFFF");
            }
            yOffset += 45;
            
            // Border width field
            drawString(poseStack, font, "Border Width:", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            borderWidthField.visible = true;
            borderWidthField.x = dialogX + 10;
            borderWidthField.y = dialogY + yOffset + 15;
            borderWidthField.setWidth(dialogW - 20);
            
            if (selectedElement.config.properties != null && 
                selectedElement.config.properties.containsKey("borderWidth")) {
                Object widthObj = selectedElement.config.properties.get("borderWidth");
                borderWidthField.setValue(widthObj != null ? widthObj.toString() : "1");
            } else {
                borderWidthField.setValue("1");
            }
            yOffset += 45;
            
            // Adjust dialog height
            dialogH = Math.max(160, yOffset + 40);
        } else {
            opacityField.visible = false;
            backgroundColorField.visible = false;
            borderColorField.visible = false;
            borderWidthField.visible = false;
        }
        
        // Save button
        savePropertiesButton.visible = hasCustomProperties;
        savePropertiesButton.x = dialogX + dialogW / 2 - 50;
        savePropertiesButton.y = dialogY + dialogH - 30;
    }
    
    private void saveElementProperties() {
        if (selectedElement == null) return;
        
        if (selectedElement.config.properties == null) {
            selectedElement.config.properties = new HashMap<>();
        }
        
        // Save search target if it's a search box
        boolean isSearchBox = selectedElement.elementId.contains("searchBox") || 
            selectedElement.elementId.contains("SearchBox");
        if (isSearchBox && searchTargetField != null) {
            String target = searchTargetField.getValue().trim();
            if (!target.isEmpty()) {
                selectedElement.config.properties.put("searchTarget", target);
            } else {
                selectedElement.config.properties.remove("searchTarget");
            }
        }
        
        // Save background properties
        boolean isBackground = Boolean.TRUE.equals(selectedElement.config.properties.get("isBackground"));
        boolean isSearchBar = Boolean.TRUE.equals(selectedElement.config.properties.get("isSearchBar"));
        boolean isTextBox = Boolean.TRUE.equals(selectedElement.config.properties.get("isTextBox"));
        
        if (isBackground || isSearchBar || isTextBox) {
            // Save opacity
            if (opacityField != null && !opacityField.getValue().isEmpty()) {
                try {
                    int opacity = Integer.parseInt(opacityField.getValue());
                    opacity = Math.max(0, Math.min(255, opacity)); // Clamp to 0-255
                    selectedElement.config.properties.put("opacity", String.valueOf(opacity));
                } catch (NumberFormatException e) {}
            }
            
            // Save background color
            if (backgroundColorField != null && !backgroundColorField.getValue().isEmpty()) {
                selectedElement.config.properties.put("backgroundColor", backgroundColorField.getValue());
            }
            
            // Save border color
            if (borderColorField != null && !borderColorField.getValue().isEmpty()) {
                selectedElement.config.properties.put("borderColor", borderColorField.getValue());
            }
            
            // Save border width
            if (borderWidthField != null && !borderWidthField.getValue().isEmpty()) {
                try {
                    int width = Integer.parseInt(borderWidthField.getValue());
                    width = Math.max(0, Math.min(10, width)); // Clamp to 0-10
                    selectedElement.config.properties.put("borderWidth", String.valueOf(width));
                } catch (NumberFormatException e) {}
            }
        }
        
        showPropertiesDialog = false;
        searchTargetField.visible = false;
        opacityField.visible = false;
        backgroundColorField.visible = false;
        borderColorField.visible = false;
        borderWidthField.visible = false;
        savePropertiesButton.visible = false;
    }
    
    private void openTextEditor(VisualElement element) {
        // Open text editing dialog for text elements
        editingTextElement = element;
        showTextEditor = true;
        
        // Load current text content
        String currentText = "";
        if (element.config.properties != null && element.config.properties.containsKey("text")) {
            Object textObj = element.config.properties.get("text");
            if (textObj != null) {
                currentText = textObj.toString();
            }
        }
        
        // If no text but has textKey, try to get from translation
        if (currentText.isEmpty() && element.config.properties != null && 
            element.config.properties.containsKey("textKey")) {
            String textKey = (String) element.config.properties.get("textKey");
            if (textKey != null && !textKey.isEmpty()) {
                try {
                    currentText = new TranslatableComponent(textKey).getString();
                } catch (Exception e) {
                    // Use key as fallback
                    currentText = textKey;
                }
            }
        }
        
        textContentField.setValue(currentText);
        textContentField.visible = true;
        saveTextButton.visible = true;
        
        // Position the dialog
        textContentField.x = width / 2 - 150;
        textContentField.y = height / 2 - 10;
        textContentField.setWidth(300);
        
        saveTextButton.x = width / 2 - 50;
        saveTextButton.y = height / 2 + 15;
        
        // Use Screen's setFocused method to focus the EditBox
        setFocused(textContentField);
    }
    
    private void renderTextEditorDialog(PoseStack poseStack, int mouseX, int mouseY) {
        if (editingTextElement == null) return;
        
        int dialogX = width / 2 - 160;
        int dialogY = height / 2 - 50;
        int dialogW = 320;
        int dialogH = 100;
        
        // Background
        fill(poseStack, dialogX, dialogY, dialogX + dialogW, dialogY + dialogH, 0xDD000000);
        fill(poseStack, dialogX, dialogY, dialogX + dialogW, dialogY + 1, 0xFFFFFFFF);
        fill(poseStack, dialogX, dialogY + dialogH - 1, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStack, dialogX, dialogY, dialogX + 1, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStack, dialogX + dialogW - 1, dialogY, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        
        // Title
        drawCenteredString(poseStack, font, 
            "Edit Text: " + editingTextElement.elementId,
            width / 2, dialogY + 5, 0xFFFFFF);
        
        // Text content field
        drawString(poseStack, font, "Text Content:", dialogX + 10, dialogY + 30, 0xCCCCCC);
        textContentField.visible = true;
        textContentField.x = dialogX + 10;
        textContentField.y = dialogY + 45;
        textContentField.setWidth(dialogW - 20);
        
        // Instructions
        drawString(poseStack, font, 
            "Enter the text to display. Use & for color codes (e.g., &cRed, &aGreen).",
            dialogX + 10, dialogY + 70, 0xAAAAAA);
        
        // Save button
        saveTextButton.visible = true;
        saveTextButton.x = dialogX + dialogW / 2 - 50;
        saveTextButton.y = dialogY + dialogH - 25;
    }
    
    private void saveTextContent() {
        if (editingTextElement == null) return;
        
        if (editingTextElement.config.properties == null) {
            editingTextElement.config.properties = new HashMap<>();
        }
        
        String text = textContentField.getValue().trim();
        if (!text.isEmpty()) {
            editingTextElement.config.properties.put("text", text);
        } else {
            editingTextElement.config.properties.remove("text");
        }
        
        // Mark as text element
        editingTextElement.config.properties.put("isTextElement", true);
        
        showTextEditor = false;
        textContentField.visible = false;
        saveTextButton.visible = false;
        editingTextElement = null;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check for Ctrl/Shift using GLFW key states
        boolean isCtrlPressed = false;
        boolean isShiftPressed = false;
        try {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            // Use GLFW to check key states
            isCtrlPressed = org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL) == org.lwjgl.glfw.GLFW.GLFW_PRESS ||
                           org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            isShiftPressed = org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS ||
                            org.lwjgl.glfw.GLFW.glfwGetKey(windowHandle, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        } catch (Exception e) {
            // Fallback: assume no modifiers
        }
        // Handle text editor dialog
        if (showTextEditor) {
            if (button == 0) { // Left click
                if (textContentField != null && textContentField.mouseClicked(mouseX, mouseY, button)) {
                    // Set focus to the field (use Screen's setFocused method)
                    setFocused(textContentField);
                    return true;
                }
                if (saveTextButton != null && saveTextButton.visible && 
                    saveTextButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                // Click outside dialog = close it
                int dialogX = width / 2 - 160;
                int dialogY = height / 2 - 50;
                if (mouseX < dialogX || mouseX > dialogX + 320 ||
                    mouseY < dialogY || mouseY > dialogY + 100) {
                    showTextEditor = false;
                    if (textContentField != null) textContentField.visible = false;
                    if (saveTextButton != null) saveTextButton.visible = false;
                    editingTextElement = null;
                }
                return true;
            }
        }
        
        // Handle properties dialog
        if (showPropertiesDialog) {
            if (button == 0) { // Left click
                if (searchTargetField != null && searchTargetField.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                if (savePropertiesButton != null && savePropertiesButton.visible && 
                    savePropertiesButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                // Click outside dialog = close it
                int dialogX = width / 2 - 150;
                int dialogY = height / 2 - 80;
                if (mouseX < dialogX || mouseX > dialogX + 300 ||
                    mouseY < dialogY || mouseY > dialogY + 160) {
                    showPropertiesDialog = false;
                    if (searchTargetField != null) searchTargetField.visible = false;
                    if (savePropertiesButton != null) savePropertiesButton.visible = false;
                }
                return true;
            }
        }
        
        // Handle add widget dialog
        if (showAddWidgetDialog) {
            // Always allow the EditBox to receive clicks
            if (newWidgetNameField != null && newWidgetNameField.mouseClicked(mouseX, mouseY, button)) {
                // Set focus to the field (use Screen's setFocused method)
                setFocused(newWidgetNameField);
                    return true;
                }
            if (button == 0) { // Left click
                // Click outside dialog = close it
                int dialogX = width / 2 - 120;
                int dialogY = height / 2 - 40;
                if (mouseX < dialogX || mouseX > dialogX + 240 ||
                    mouseY < dialogY || mouseY > dialogY + 80) {
                    showAddWidgetDialog = false;
                    newWidgetNameField.visible = false;
                }
                return true;
            }
        }
        
        // Check if clicking on UI buttons first (they have priority)
        if (saveButton != null && saveButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (resetButton != null && resetButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (closeButton != null && closeButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (addWidgetButton != null && addWidgetButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (deleteButton != null && deleteButton.visible && deleteButton.mouseClicked(mouseX, mouseY, button)) return true;
        
        // Check if clicking on layers panel (check for dragging the panel itself)
        int layersPanelHeight = (height - layersPanelY - 50) / 2; // Half height
        if (mouseX >= layersPanelX && mouseX < layersPanelX + LAYERS_PANEL_WIDTH &&
            mouseY >= layersPanelY && mouseY < layersPanelY + 20) {
            // Clicking on title bar - start dragging
            if (button == 0) {
                isDraggingLayersPanel = true;
                layersPanelDragStartX = mouseX;
                layersPanelDragStartY = mouseY;
                layersPanelDragStartPanelX = layersPanelX;
                layersPanelDragStartPanelY = layersPanelY;
                return true;
            }
        } else if (mouseX >= layersPanelX && mouseX < layersPanelX + LAYERS_PANEL_WIDTH &&
                   mouseY >= layersPanelY && mouseY < layersPanelY + layersPanelHeight) {
            if (handleLayersPanelClick(mouseX, mouseY)) {
                return true;
            }
        }
        
        // Check if clicking on toolbox title area (for dragging)
        int toolboxW = 22;
        int toolboxH = 132;
        if (mouseX >= toolboxX && mouseX < toolboxX + toolboxW &&
            mouseY >= toolboxY && mouseY < toolboxY + 20) {
            // Clicking on title area - start dragging
            if (button == 0) {
                isDraggingToolbox = true;
                toolboxDragStartX = mouseX;
                toolboxDragStartY = mouseY;
                toolboxDragStartXPos = toolboxX;
                toolboxDragStartYPos = toolboxY;
                return true;
            }
        }
        
        // Check if clicking on a resize handle (highest priority for editor interaction)
        VisualElement clickedElement = null;
        ResizeHandle handle = ResizeHandle.NONE;
        
        for (VisualElement element : visualElements) {
            // Skip hidden elements
            if (!isElementVisible(element)) continue;
            handle = getResizeHandle(element, (int)mouseX, (int)mouseY);
            if (handle != ResizeHandle.NONE) {
                clickedElement = element;
                break;
            }
        }
        
        // If clicking on a resize handle, start resizing
        if (button == 0 && handle != ResizeHandle.NONE && clickedElement != null) {
            // Deselect all
            for (VisualElement element : visualElements) {
                element.selected = false;
            }
            
            // Select clicked element
            clickedElement.selected = true;
            selectedElement = clickedElement;
            deleteButton.visible = true;
            
            // Start resizing
            resizingElementId = clickedElement.elementId;
            resizeHandle = handle;
            isResizing = true;
            dragStartX = mouseX;
            dragStartY = mouseY;
            dragStartElementX = clickedElement.config.x;
            dragStartElementY = clickedElement.config.y;
            dragStartElementWidth = clickedElement.config.width;
            dragStartElementHeight = clickedElement.config.height;
            return true;
        }
        
        // Right-click on empty space to create new element based on current tool
        if (button == 1) { // Right click
            // Check if clicking on a widget or button (skip hidden elements)
            boolean clickedOnWidget = false;
            for (VisualElement element : visualElements) {
                if (!isElementVisible(element)) continue; // Skip hidden elements
                if (mouseX >= element.config.x && mouseX < element.config.x + element.config.width &&
                    mouseY >= element.config.y && mouseY < element.config.y + element.config.height) {
                    clickedOnWidget = true;
                    break;
                }
            }
            
            if (!clickedOnWidget && mouseY < height - 50) { // Not in button area
                // Create element based on current tool
                if (currentTool == EditorTool.CREATE_BACKGROUND || 
                    currentTool == EditorTool.CREATE_SEARCH_BAR || 
                    currentTool == EditorTool.CREATE_TEXT_BOX ||
                    currentTool == EditorTool.CREATE_TEXT ||
                    currentTool == EditorTool.CREATE_WIDGET) {
                createNewWidgetAt((int)mouseX, (int)mouseY);
                return true;
                }
            }
        }
        
        // Check if clicking on widget border/selection area (for dragging)
        if (button == 0) {
            
            // Check for sidebar group selection
            if (currentTool == EditorTool.SELECT_GROUP) {
                boolean clickedOnSidebar = false;
                for (VisualElement element : visualElements) {
                    if (!isElementVisible(element)) continue; // Skip hidden elements
                    if (element.elementId.startsWith("sidebar.")) {
                        int x = element.config.x;
                        int y = element.config.y;
                        int w = getElementWidthForScale(element, currentGuiScale);
                        int h = getElementHeightForScale(element, currentGuiScale);
                        if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                            clickedOnSidebar = true;
                            break;
                        }
                    }
                }
                
                if (clickedOnSidebar) {
                    // Select all sidebar elements (only visible ones)
                    selectedElements.clear();
                    for (VisualElement element : visualElements) {
                        if (!isElementVisible(element)) continue; // Skip hidden elements
                        if (element.elementId.startsWith("sidebar.")) {
                            element.selected = true;
                            selectedElements.add(element);
                        } else {
                            element.selected = false;
                        }
                    }
                    selectedElement = selectedElements.isEmpty() ? null : selectedElements.get(0);
                    deleteButton.visible = !selectedElements.isEmpty();
                    return true;
                }
            }
            
            for (VisualElement element : visualElements) {
                // Skip hidden elements - they should not be selectable or moveable
                if (!isElementVisible(element)) continue;
                
                // Check if clicking on this element (use scale-aware sizes)
                int x = element.config.x;
                int y = element.config.y;
                int w = getElementWidthForScale(element, currentGuiScale);
                int h = getElementHeightForScale(element, currentGuiScale);
                
                // For text elements, make the entire area clickable (not just border)
                boolean isTextElement = element.config.properties != null && 
                    Boolean.TRUE.equals(element.config.properties.get("isTextElement"));
                
                // Adjust size for text elements
                if (isTextElement && element.config.properties != null) {
                    String text = (String) element.config.properties.get("text");
                    if (text != null && !text.isEmpty()) {
                        int textWidth = font.width(text);
                        if (w < textWidth + 10) {
                            w = textWidth + 10;
                        }
                        if (h < 12) {
                            h = 12;
                        }
                    }
                }
                
                // Make all elements fully clickable - click anywhere within bounds
                // Also allow clicking slightly outside for easier selection
                int clickMargin = 3; // Allow clicking 3 pixels outside the element
                boolean isClickable = mouseX >= x - clickMargin && mouseX < x + w + clickMargin &&
                                     mouseY >= y - clickMargin && mouseY < y + h + clickMargin;
                
                if (isClickable) {
                    // Handle multi-select with Ctrl
                    if (isCtrlPressed) {
                        // Toggle selection
                        if (element.selected) {
                            element.selected = false;
                            selectedElements.remove(element);
                            if (selectedElement == element) {
                                selectedElement = selectedElements.isEmpty() ? null : selectedElements.get(0);
                            }
                        } else {
                            element.selected = true;
                            if (!selectedElements.contains(element)) {
                                selectedElements.add(element);
                            }
                            selectedElement = element;
                        }
                        deleteButton.visible = !selectedElements.isEmpty() || selectedElement != null;
                    } else {
                        // Single select
                    // Deselect all
                    for (VisualElement e : visualElements) {
                        e.selected = false;
                    }
                        selectedElements.clear();
                    
                    // Select clicked element
                    element.selected = true;
                    selectedElement = element;
                        selectedElements.add(element);
                    deleteButton.visible = true;
                        
                        // Store drag start positions for multi-select
                        if (element.config.properties == null) {
                            element.config.properties = new HashMap<>();
                        }
                        element.config.properties.put("dragStartX", element.config.x);
                        element.config.properties.put("dragStartY", element.config.y);
                        
                        // Double-click to open properties dialog or text editor
                        long currentTime = System.currentTimeMillis();
                        if (lastClickTime > 0 && currentTime - lastClickTime < 300 && 
                            lastClickedElement == element) {
                            // Double-click detected - open properties dialog or edit text
                            if (isTextElement) {
                                // Always open text editor for text elements
                                openTextEditor(element);
                            } else if (currentTool == EditorTool.EDIT_TEXT) {
                                // If EDIT_TEXT tool is active, try to convert to text element
                                openTextEditor(element);
                            } else if (currentTool == EditorTool.EDIT) {
                                showPropertiesDialog = true;
                            } else if (currentTool == EditorTool.EDIT_TEXT) {
                                // EDIT_TEXT tool active - open text editor for any element
                                openTextEditor(element);
                            } else {
                                // Default: open text editor if it's a text element, otherwise properties
                                if (isTextElement) {
                                    openTextEditor(element);
                                } else {
                                    showPropertiesDialog = true;
                                }
                            }
                            lastClickTime = 0;
                            lastClickedElement = null;
                            return true;
                        }
                        lastClickTime = currentTime;
                        lastClickedElement = element;
                    }
                    
                    // Start dragging
                    draggingElementId = element.elementId;
                    isDragging = true;
                    dragStartX = mouseX;
                    dragStartY = mouseY;
                    dragStartElementX = element.config.x;
                    dragStartElementY = element.config.y;
                    
                    // Store drag start positions for all selected elements
                    for (VisualElement selected : selectedElements) {
                        if (selected.config.properties == null) {
                            selected.config.properties = new HashMap<>();
                        }
                        selected.config.properties.put("dragStartX", selected.config.x);
                        selected.config.properties.put("dragStartY", selected.config.y);
                    }
                    return true;
                }
            }
            
            // Clicked on empty space - deselect all (unless Ctrl is held)
            if (!isCtrlPressed) {
            for (VisualElement element : visualElements) {
                element.selected = false;
            }
                selectedElements.clear();
            selectedElement = null;
            deleteButton.visible = false;
            }
        }
        
        // Allow widgets to receive mouse clicks (pass through to source tab)
        if (sourceTab != null) {
            if (sourceTab.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            // Handle layers panel dragging
            if (isDraggingLayersPanel) {
                double deltaX = mouseX - layersPanelDragStartX;
                double deltaY = mouseY - layersPanelDragStartY;
                layersPanelX = layersPanelDragStartPanelX + (int)deltaX;
                layersPanelY = layersPanelDragStartPanelY + (int)deltaY;
                // Constrain to screen bounds
                layersPanelX = Math.max(0, Math.min(width - LAYERS_PANEL_WIDTH, layersPanelX));
                layersPanelY = Math.max(0, Math.min(height - 100, layersPanelY));
                return true;
            }
            
            // Handle toolbox dragging
            if (isDraggingToolbox) {
                double deltaX = mouseX - toolboxDragStartX;
                double deltaY = mouseY - toolboxDragStartY;
                int newToolboxX = toolboxDragStartXPos + (int)deltaX;
                int newToolboxY = toolboxDragStartYPos + (int)deltaY;
                // Constrain to screen bounds
                int toolboxW = 22;
                int toolboxH = 132;
                newToolboxX = Math.max(0, Math.min(width - toolboxW, newToolboxX));
                newToolboxY = Math.max(0, Math.min(height - toolboxH, newToolboxY));
                
                // Update toolbox position
                int deltaToolboxX = newToolboxX - toolboxX;
                int deltaToolboxY = newToolboxY - toolboxY;
                toolboxX = newToolboxX;
                toolboxY = newToolboxY;
                
                // Update all toolbox button positions
                int toolButtonSize = 20;
                int toolSpacing = 2;
                if (moveToolButton != null) moveToolButton.x = toolboxX;
                if (moveToolButton != null) moveToolButton.y = toolboxY;
                if (editToolButton != null) editToolButton.x = toolboxX;
                if (editToolButton != null) editToolButton.y = toolboxY + toolButtonSize + toolSpacing;
                if (editTextToolButton != null) editTextToolButton.x = toolboxX;
                if (editTextToolButton != null) editTextToolButton.y = toolboxY + (toolButtonSize + toolSpacing) * 2;
                if (createTextToolButton != null) createTextToolButton.x = toolboxX;
                if (createTextToolButton != null) createTextToolButton.y = toolboxY + (toolButtonSize + toolSpacing) * 3;
                if (createWidgetToolButton != null) createWidgetToolButton.x = toolboxX;
                if (createWidgetToolButton != null) createWidgetToolButton.y = toolboxY + (toolButtonSize + toolSpacing) * 4;
                if (createBackgroundButton != null) createBackgroundButton.x = toolboxX;
                if (createBackgroundButton != null) createBackgroundButton.y = toolboxY + (toolButtonSize + toolSpacing) * 5;
                if (createSearchBarButton != null) createSearchBarButton.x = toolboxX;
                if (createSearchBarButton != null) createSearchBarButton.y = toolboxY + (toolButtonSize + toolSpacing) * 6;
                if (createTextBoxButton != null) createTextBoxButton.x = toolboxX;
                if (createTextBoxButton != null) createTextBoxButton.y = toolboxY + (toolButtonSize + toolSpacing) * 7;
                if (selectGroupToolButton != null) selectGroupToolButton.x = toolboxX;
                if (selectGroupToolButton != null) selectGroupToolButton.y = toolboxY + (toolButtonSize + toolSpacing) * 8;
                
                // Update scale button positions
                int scaleY = toolboxY + (toolButtonSize + toolSpacing) * 9 + 5;
                int scaleX = toolboxX - 180;
                if (scale1Button != null) scale1Button.x = scaleX;
                if (scale1Button != null) scale1Button.y = scaleY;
                if (scale2Button != null) scale2Button.x = scaleX + 37;
                if (scale2Button != null) scale2Button.y = scaleY;
                if (scale3Button != null) scale3Button.x = scaleX + 74;
                if (scale3Button != null) scale3Button.y = scaleY;
                if (scale4Button != null) scale4Button.x = scaleX + 111;
                if (scale4Button != null) scale4Button.y = scaleY;
                if (autoScaleButton != null) autoScaleButton.x = scaleX + 148;
                if (autoScaleButton != null) autoScaleButton.y = scaleY;
                
                return true;
            }
            
            if (resizingElementId != null && resizeHandle != null) {
                VisualElement element = getElementById(resizingElementId);
                if (element != null && isElementVisible(element)) { // Only resize visible elements
                    resizeElement(element, mouseX, mouseY);
                    return true;
                }
            } else if (draggingElementId != null) {
                VisualElement element = getElementById(draggingElementId);
                if (element != null && isElementVisible(element)) { // Only move visible elements
                    double deltaX = mouseX - dragStartX;
                    double deltaY = mouseY - dragStartY;
                    int newX = dragStartElementX + (int)deltaX;
                    int newY = dragStartElementY + (int)deltaY;
                    
                    // Move the dragged element
                    element.config.x = newX;
                    element.config.y = newY;
                    
                    // If multi-selecting, move all selected elements by the same delta (only visible ones)
                    if (!selectedElements.isEmpty() && selectedElements.contains(element)) {
                        for (VisualElement selected : selectedElements) {
                            if (!isElementVisible(selected)) continue; // Skip hidden elements
                            if (selected != element) {
                                // Get stored drag start positions
                                int startX = dragStartElementX;
                                int startY = dragStartElementY;
                                if (selected.config.properties != null) {
                                    Object storedX = selected.config.properties.get("dragStartX");
                                    Object storedY = selected.config.properties.get("dragStartY");
                                    if (storedX instanceof Number) startX = ((Number)storedX).intValue();
                                    if (storedY instanceof Number) startY = ((Number)storedY).intValue();
                                }
                                // Calculate relative position and apply delta
                                int relativeX = selected.config.x - startX;
                                int relativeY = selected.config.y - startY;
                                selected.config.x = newX + relativeX;
                                selected.config.y = newY + relativeY;
                            }
                        }
                    }
                    return true;
                }
            } else if (!selectedElements.isEmpty() && isDragging) {
                // Handle dragging multiple selected elements (when dragging started with multi-select)
                double deltaX = mouseX - dragStartX;
                double deltaY = mouseY - dragStartY;
                
                for (VisualElement selected : selectedElements) {
                    if (!isElementVisible(selected)) continue; // Skip hidden elements
                    if (selected.config.properties != null) {
                        Object storedX = selected.config.properties.get("dragStartX");
                        Object storedY = selected.config.properties.get("dragStartY");
                        if (storedX instanceof Number && storedY instanceof Number) {
                            int startX = ((Number)storedX).intValue();
                            int startY = ((Number)storedY).intValue();
                            selected.config.x = startX + (int)deltaX;
                            selected.config.y = startY + (int)deltaY;
                        }
                    }
                }
                return true;
            }
        }
        
        // Pass through to widgets if not dragging/resizing editor elements
        if (sourceTab != null && draggingElementId == null && resizingElementId == null && !isDragging) {
            if (sourceTab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    private void resizeElement(VisualElement element, double mouseX, double mouseY) {
        double deltaX = mouseX - dragStartX;
        double deltaY = mouseY - dragStartY;
        
        // Ensure properties map exists
        if (element.config.properties == null) {
            element.config.properties = new HashMap<>();
        }
        
        switch (resizeHandle) {
            case TOP_LEFT:
                element.config.width = Math.max(20, dragStartElementWidth - (int)deltaX);
                element.config.height = Math.max(20, dragStartElementHeight - (int)deltaY);
                element.config.x = dragStartElementX + (dragStartElementWidth - element.config.width);
                element.config.y = dragStartElementY + (dragStartElementHeight - element.config.height);
                break;
            case TOP_RIGHT:
                element.config.width = Math.max(20, dragStartElementWidth + (int)deltaX);
                element.config.height = Math.max(20, dragStartElementHeight - (int)deltaY);
                element.config.y = dragStartElementY + (dragStartElementHeight - element.config.height);
                break;
            case BOTTOM_LEFT:
                element.config.width = Math.max(20, dragStartElementWidth - (int)deltaX);
                element.config.height = Math.max(20, dragStartElementHeight + (int)deltaY);
                element.config.x = dragStartElementX + (dragStartElementWidth - element.config.width);
                break;
            case BOTTOM_RIGHT:
                element.config.width = Math.max(20, dragStartElementWidth + (int)deltaX);
                element.config.height = Math.max(20, dragStartElementHeight + (int)deltaY);
                break;
            case TOP:
                element.config.height = Math.max(20, dragStartElementHeight - (int)deltaY);
                element.config.y = dragStartElementY + (dragStartElementHeight - element.config.height);
                break;
            case BOTTOM:
                element.config.height = Math.max(20, dragStartElementHeight + (int)deltaY);
                break;
            case LEFT:
                element.config.width = Math.max(20, dragStartElementWidth - (int)deltaX);
                element.config.x = dragStartElementX + (dragStartElementWidth - element.config.width);
                break;
            case RIGHT:
                element.config.width = Math.max(20, dragStartElementWidth + (int)deltaX);
                break;
        }
        
        // Save the new size for current scale
        String widthKey = "scale" + currentGuiScale + "_width";
        String heightKey = "scale" + currentGuiScale + "_height";
        element.config.properties.put(widthKey, element.config.width);
        element.config.properties.put(heightKey, element.config.height);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Save sizes for current scale when resizing ends
            if (isResizing && selectedElement != null) {
                saveElementSizesForCurrentScale();
            }
            draggingElementId = null;
            resizingElementId = null;
            resizeHandle = null;
            isDragging = false;
            isResizing = false;
        }
        
        // Pass through to widgets
        if (sourceTab != null) {
            if (sourceTab.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle add widget dialog input first
        if (showAddWidgetDialog && newWidgetNameField != null && newWidgetNameField.visible) {
            if (newWidgetNameField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (keyCode == 257) { // Enter
                createNewWidget();
                return true;
            }
        }
        
        if (keyCode == 256) { // ESC
            if (showAddWidgetDialog) {
                showAddWidgetDialog = false;
                newWidgetNameField.visible = false;
                return true;
            }
            if (showTextEditor) {
                showTextEditor = false;
                if (textContentField != null) textContentField.visible = false;
                if (saveTextButton != null) saveTextButton.visible = false;
                editingTextElement = null;
                return true;
            }
            if (showPropertiesDialog) {
                showPropertiesDialog = false;
                if (searchTargetField != null) searchTargetField.visible = false;
                if (savePropertiesButton != null) savePropertiesButton.visible = false;
                return true;
            }
            onClose();
            return true;
        }
        
        // Enter to save text or properties
        if (showTextEditor && keyCode == 257) { // Enter
            saveTextContent();
            return true;
        }
        if (showPropertiesDialog && keyCode == 257) { // Enter
            saveElementProperties();
            return true;
        }
        
        // Handle text editor input
        if (showTextEditor && textContentField != null && textContentField.visible) {
            if (textContentField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        // Handle properties dialog input
        if (showPropertiesDialog) {
            if (searchTargetField != null && searchTargetField.visible && 
                searchTargetField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (opacityField != null && opacityField.visible && 
                opacityField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (backgroundColorField != null && backgroundColorField.visible && 
                backgroundColorField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (borderColorField != null && borderColorField.visible && 
                borderColorField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (borderWidthField != null && borderWidthField.visible && 
                borderWidthField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        // Delete key to delete selected elements
        if (keyCode == 261) { // Delete
            if (!selectedElements.isEmpty()) {
                deleteSelectedElements();
                return true;
            } else if (selectedElement != null) {
            deleteSelectedElement();
            return true;
            }
        }
        
        // Keyboard shortcuts for tools (only when no dialogs are open)
        if (!showAddWidgetDialog && !showTextEditor && !showPropertiesDialog) {
            // M = Move, E = Edit, T = Edit Text, N = Create Text, W = Create Widget, 
            // B = Create Background, S = Create Search Bar, X = Create Text Box, G = Select Group
            if (keyCode == 77) { // M
                setCurrentTool(EditorTool.MOVE);
                return true;
            } else if (keyCode == 69) { // E
                setCurrentTool(EditorTool.EDIT);
                return true;
            } else if (keyCode == 84) { // T
                setCurrentTool(EditorTool.EDIT_TEXT);
                return true;
            } else if (keyCode == 78) { // N
                setCurrentTool(EditorTool.CREATE_TEXT);
                return true;
            } else if (keyCode == 87) { // W
                setCurrentTool(EditorTool.CREATE_WIDGET);
                showAddWidgetDialog = true;
                return true;
            } else if (keyCode == 66) { // B
                setCurrentTool(EditorTool.CREATE_BACKGROUND);
                return true;
            } else if (keyCode == 83) { // S
                setCurrentTool(EditorTool.CREATE_SEARCH_BAR);
                return true;
            } else if (keyCode == 88) { // X
                setCurrentTool(EditorTool.CREATE_TEXT_BOX);
                return true;
            } else if (keyCode == 71) { // G
                setCurrentTool(EditorTool.SELECT_GROUP);
                return true;
            }
        }
        
        // Pass through to widgets
        if (sourceTab != null) {
            if (sourceTab.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Handle layers panel scrolling
        int layersPanelHeight = (height - layersPanelY - 50) / 2; // Half height
        if (mouseX >= layersPanelX && mouseX < layersPanelX + LAYERS_PANEL_WIDTH &&
            mouseY >= layersPanelY && mouseY < layersPanelY + layersPanelHeight) {
            int maxVisible = layersPanelHeight / LAYER_ITEM_HEIGHT;
            int maxScroll = Math.max(0, visualElements.size() - maxVisible);
            layersPanelScrollOffset = (int) Math.max(0, Math.min(maxScroll, layersPanelScrollOffset - delta));
            return true;
        }
        
        // Pass through to widgets
        if (sourceTab != null) {
            if (sourceTab.mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }
        
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Handle add widget dialog input first
        if (showAddWidgetDialog && newWidgetNameField != null && newWidgetNameField.visible) {
            if (newWidgetNameField.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        
        // Handle text editor input
        if (showTextEditor && textContentField != null && textContentField.visible) {
            if (textContentField.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        
        // Handle properties dialog input
        if (showPropertiesDialog) {
            if (searchTargetField != null && searchTargetField.visible && 
                searchTargetField.charTyped(codePoint, modifiers)) {
                return true;
            }
            if (opacityField != null && opacityField.visible && 
                opacityField.charTyped(codePoint, modifiers)) {
                return true;
            }
            if (backgroundColorField != null && backgroundColorField.visible && 
                backgroundColorField.charTyped(codePoint, modifiers)) {
                return true;
            }
            if (borderColorField != null && borderColorField.visible && 
                borderColorField.charTyped(codePoint, modifiers)) {
                return true;
            }
            if (borderWidthField != null && borderWidthField.visible && 
                borderWidthField.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        
        // Pass through to widgets
        if (sourceTab != null) {
            if (sourceTab.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        
        return super.charTyped(codePoint, modifiers);
    }
    
    private void createNewWidget() {
        String name = newWidgetNameField.getValue().trim();
        if (name.isEmpty()) {
            name = "newWidget" + visualElements.size();
        }
        
        // Create at center of screen
        createNewWidgetAt(width / 2 - 50, height / 2 - 10, name);
        
        showAddWidgetDialog = false;
        newWidgetNameField.setValue("");
        newWidgetNameField.visible = false;
    }
    
    private void createNewWidgetAt(int x, int y) {
        createNewWidgetAt(x, y, null);
    }
    
    private void createNewWidgetAt(int x, int y, String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "newWidget" + visualElements.size();
        } else {
            name = name.trim();
        }
        
        // Check if name already exists
        if (getElementById(name) != null) {
            // Append number if exists
            int counter = 1;
            String baseName = name;
            while (getElementById(name) != null) {
                name = baseName + counter;
                counter++;
            }
        }
        
        // Create new element based on current tool
        GuiConfigData.ElementConfig newConfig = new GuiConfigData.ElementConfig(
            x - 50, y - 10, 100, 20
        );
        
        if (newConfig.properties == null) {
            newConfig.properties = new HashMap<>();
        }
        
        if (currentTool == EditorTool.CREATE_TEXT) {
            newConfig.properties.put("isTextElement", true);
            newConfig.properties.put("text", "New Text");
            newConfig.width = Math.max(100, font.width("New Text") + 10);
            newConfig.height = 20;
        } else if (currentTool == EditorTool.CREATE_BACKGROUND) {
            newConfig.properties.put("isBackground", true);
            newConfig.properties.put("opacity", "192"); // 0xC0
            newConfig.properties.put("backgroundColor", "101010"); // Dark gray
            newConfig.properties.put("borderColor", "FFFFFF"); // White
            newConfig.properties.put("borderWidth", "1");
            newConfig.width = 200;
            newConfig.height = 100;
        } else if (currentTool == EditorTool.CREATE_SEARCH_BAR) {
            newConfig.properties.put("isSearchBar", true);
            newConfig.properties.put("placeholder", "Search...");
            newConfig.properties.put("opacity", "255"); // Fully opaque
            newConfig.properties.put("backgroundColor", "2C2C2C"); // Dark gray
            newConfig.properties.put("borderColor", "FFFFFF");
            newConfig.properties.put("borderWidth", "1");
            newConfig.width = 200;
            newConfig.height = 20;
        } else if (currentTool == EditorTool.CREATE_TEXT_BOX) {
            newConfig.properties.put("isTextBox", true);
            newConfig.properties.put("text", "Text Box");
            newConfig.properties.put("opacity", "200");
            newConfig.properties.put("backgroundColor", "1A1A1A");
            newConfig.properties.put("borderColor", "FFFFFF");
            newConfig.properties.put("borderWidth", "1");
            newConfig.width = 150;
            newConfig.height = 30;
        }
        
        VisualElement newElement = new VisualElement(name, newConfig);
        newElement.selected = true;
        
        // Deselect others
        for (VisualElement element : visualElements) {
            element.selected = false;
        }
        selectedElements.clear();
        selectedElements.add(newElement);
        
        visualElements.add(newElement);
        selectedElement = newElement;
        deleteButton.visible = true;
        
        // Add to config
        config.setElementConfig(name, newConfig);
        
        // If it's a text element, open editor immediately
        if (currentTool == EditorTool.CREATE_TEXT) {
            openTextEditor(newElement);
        }
    }
    
    private void setCurrentTool(EditorTool tool) {
        currentTool = tool;
        updateToolButtons();
    }
    
    private void updateToolButtons() {
        // Update all tool buttons - compact single letter with active indicator
        if (moveToolButton != null) {
            moveToolButton.setMessage(new TextComponent(currentTool == EditorTool.MOVE ? "●" : "M"));
            moveToolButton.active = currentTool != EditorTool.MOVE; // Highlight active tool
        }
        if (editToolButton != null) {
            editToolButton.setMessage(new TextComponent(currentTool == EditorTool.EDIT ? "●" : "E"));
            editToolButton.active = currentTool != EditorTool.EDIT;
        }
        if (editTextToolButton != null) {
            editTextToolButton.setMessage(new TextComponent(currentTool == EditorTool.EDIT_TEXT ? "●" : "T"));
            editTextToolButton.active = currentTool != EditorTool.EDIT_TEXT;
        }
        if (createTextToolButton != null) {
            createTextToolButton.setMessage(new TextComponent(currentTool == EditorTool.CREATE_TEXT ? "●" : "N"));
            createTextToolButton.active = currentTool != EditorTool.CREATE_TEXT;
        }
        if (createWidgetToolButton != null) {
            createWidgetToolButton.setMessage(new TextComponent(currentTool == EditorTool.CREATE_WIDGET ? "●" : "W"));
            createWidgetToolButton.active = currentTool != EditorTool.CREATE_WIDGET;
        }
        if (createBackgroundButton != null) {
            createBackgroundButton.setMessage(new TextComponent(currentTool == EditorTool.CREATE_BACKGROUND ? "●" : "B"));
            createBackgroundButton.active = currentTool != EditorTool.CREATE_BACKGROUND;
        }
        if (createSearchBarButton != null) {
            createSearchBarButton.setMessage(new TextComponent(currentTool == EditorTool.CREATE_SEARCH_BAR ? "●" : "S"));
            createSearchBarButton.active = currentTool != EditorTool.CREATE_SEARCH_BAR;
        }
        if (createTextBoxButton != null) {
            createTextBoxButton.setMessage(new TextComponent(currentTool == EditorTool.CREATE_TEXT_BOX ? "●" : "X"));
            createTextBoxButton.active = currentTool != EditorTool.CREATE_TEXT_BOX;
        }
        if (selectGroupToolButton != null) {
            selectGroupToolButton.setMessage(new TextComponent(currentTool == EditorTool.SELECT_GROUP ? "●" : "G"));
            selectGroupToolButton.active = currentTool != EditorTool.SELECT_GROUP;
        }
        updateScaleButtons();
    }
    
    private void setGuiScale(int scale) {
        // Save current element sizes before switching scales
        if (currentGuiScale != scale) {
            saveElementSizesForCurrentScale();
        }
        
        currentGuiScale = scale;
        
        // Store scale in config for persistence
        if (parentScreen instanceof BuildScapeConfigScreen configScreen) {
            config.screen.scale = scale;
        }
        
        // Load element sizes for the new scale
        loadElementSizesForCurrentScale();
        
        updateToolButtons();
        updateScaleButtons();
    }
    
    /**
     * Get element width for a specific GUI scale (supports per-scale sizing)
     */
    private int getElementWidthForScale(VisualElement element, int scale) {
        if (element.config.properties != null) {
            String scaleKey = "scale" + scale + "_width";
            if (element.config.properties.containsKey(scaleKey)) {
                Object widthObj = element.config.properties.get(scaleKey);
                if (widthObj instanceof Number) {
                    return ((Number) widthObj).intValue();
                } else if (widthObj instanceof String) {
                    try {
                        return Integer.parseInt((String) widthObj);
                    } catch (NumberFormatException e) {}
                }
            }
        }
        // Fall back to default width
        return element.config.width;
    }
    
    /**
     * Get element height for a specific GUI scale (supports per-scale sizing)
     */
    private int getElementHeightForScale(VisualElement element, int scale) {
        if (element.config.properties != null) {
            String scaleKey = "scale" + scale + "_height";
            if (element.config.properties.containsKey(scaleKey)) {
                Object heightObj = element.config.properties.get(scaleKey);
                if (heightObj instanceof Number) {
                    return ((Number) heightObj).intValue();
                } else if (heightObj instanceof String) {
                    try {
                        return Integer.parseInt((String) heightObj);
                    } catch (NumberFormatException e) {}
                }
            }
        }
        // Fall back to default height
        return element.config.height;
    }
    
    /**
     * Save current element sizes for the current GUI scale
     */
    private void saveElementSizesForCurrentScale() {
        for (VisualElement element : visualElements) {
            if (element.config.properties == null) {
                element.config.properties = new HashMap<>();
            }
            String widthKey = "scale" + currentGuiScale + "_width";
            String heightKey = "scale" + currentGuiScale + "_height";
            element.config.properties.put(widthKey, element.config.width);
            element.config.properties.put(heightKey, element.config.height);
        }
    }
    
    /**
     * Load element sizes for the current GUI scale
     */
    private void loadElementSizesForCurrentScale() {
        for (VisualElement element : visualElements) {
            int scaleWidth = getElementWidthForScale(element, currentGuiScale);
            int scaleHeight = getElementHeightForScale(element, currentGuiScale);
            element.config.width = scaleWidth;
            element.config.height = scaleHeight;
        }
    }
    
    private void updateScaleButtons() {
        scale1Button.active = currentGuiScale != 1;
        scale2Button.active = currentGuiScale != 2;
        scale3Button.active = currentGuiScale != 3;
        scale4Button.active = currentGuiScale != 4;
    }
    
    /**
     * Check if an element is visible (not hidden)
     */
    private boolean isElementVisible(VisualElement element) {
        if (element.config.properties != null && element.config.properties.containsKey("editorVisible")) {
            Object visible = element.config.properties.get("editorVisible");
            if (visible instanceof Boolean) {
                return (Boolean) visible;
            } else if (visible instanceof String) {
                return Boolean.parseBoolean((String) visible);
            }
        }
        return true; // Default to visible
    }
    
    /**
     * Toggle element visibility
     */
    private void toggleElementVisibility(VisualElement element) {
        if (element.config.properties == null) {
            element.config.properties = new HashMap<>();
        }
        boolean currentVisible = isElementVisible(element);
        element.config.properties.put("editorVisible", !currentVisible);
        
        // If hiding, deselect the element
        if (!!currentVisible) { // If we're hiding it
            element.selected = false;
            if (selectedElement == element) {
                selectedElement = null;
            }
            selectedElements.remove(element);
            if (selectedElements.isEmpty()) {
                deleteButton.visible = false;
            }
        }
    }
    
    /**
     * Render the layers panel on the left side
     */
    private void renderLayersPanel(PoseStack poseStack, int mouseX, int mouseY) {
        int panelHeight = (height - layersPanelY - 50) / 2; // Half the current height
        int panelX = layersPanelX;
        int panelY = layersPanelY;
        
        // Panel background
        fill(poseStack, panelX, panelY, panelX + LAYERS_PANEL_WIDTH, panelY + panelHeight, 0xCC000000);
        fill(poseStack, panelX, panelY, panelX + LAYERS_PANEL_WIDTH, panelY + 1, 0xFFFFFFFF);
        fill(poseStack, panelX, panelY + panelHeight - 1, panelX + LAYERS_PANEL_WIDTH, panelY + panelHeight, 0xFFFFFFFF);
        fill(poseStack, panelX, panelY, panelX + 1, panelY + panelHeight, 0xFFFFFFFF);
        fill(poseStack, panelX + LAYERS_PANEL_WIDTH - 1, panelY, panelX + LAYERS_PANEL_WIDTH, panelY + panelHeight, 0xFFFFFFFF);
        
        // Title (with translation)
        String layersTitle = new TranslatableComponent("buildscape.gui.editor.layers").getString();
        drawString(poseStack, font, layersTitle, panelX + 5, panelY + 5, 0xFFFFFF);
        
        // Calculate visible range
        int maxVisible = panelHeight / LAYER_ITEM_HEIGHT;
        int startIndex = layersPanelScrollOffset;
        int endIndex = Math.min(startIndex + maxVisible, visualElements.size());
        
        // Render layer items
        for (int i = startIndex; i < endIndex; i++) {
            if (i >= visualElements.size()) break;
            VisualElement element = visualElements.get(i);
            int itemY = panelY + 20 + (i - startIndex) * LAYER_ITEM_HEIGHT;
            
            boolean isVisible = isElementVisible(element);
            boolean isSelected = element.selected;
            boolean isHovered = mouseX >= panelX && mouseX < panelX + LAYERS_PANEL_WIDTH &&
                               mouseY >= itemY && mouseY < itemY + LAYER_ITEM_HEIGHT;
            
            // Item background
            int bgColor = isHovered ? 0x40CCCCCC : (isSelected ? 0x30FFFFFF : 0x20CCCCCC);
            fill(poseStack, panelX + 1, itemY, panelX + LAYERS_PANEL_WIDTH - 1, itemY + LAYER_ITEM_HEIGHT, bgColor);
            
            // Checkbox
            int checkboxX = panelX + 5;
            int checkboxY = itemY + 4;
            fill(poseStack, checkboxX, checkboxY, checkboxX + LAYER_CHECKBOX_SIZE, checkboxY + LAYER_CHECKBOX_SIZE, 0x33FFFFFF);
            if (isVisible) {
                // Draw checkmark
                fill(poseStack, checkboxX + 2, checkboxY + 5, checkboxX + 5, checkboxY + 6, 0xFFFFFFFF);
                fill(poseStack, checkboxX + 5, checkboxY + 6, checkboxX + 10, checkboxY + 7, 0xFFFFFFFF);
                fill(poseStack, checkboxX + 9, checkboxY + 6, checkboxX + 10, checkboxY + 9, 0xFFFFFFFF);
            }
            
            // Element name (truncate if too long)
            String elementName = element.elementId;
            if (elementName.startsWith("sidebar.")) {
                elementName = elementName.substring(8); // Remove "sidebar." prefix
            }
            int nameX = checkboxX + LAYER_CHECKBOX_SIZE + 5;
            int maxNameWidth = LAYERS_PANEL_WIDTH - (nameX - panelX) - 5;
            int nameWidth = font.width(elementName);
            if (nameWidth > maxNameWidth) {
                elementName = font.plainSubstrByWidth(elementName, maxNameWidth - font.width("...")) + "...";
            }
            
            int textColor = isVisible ? (isSelected ? 0xFFFFFF : 0xCCCCCC) : 0x888888;
            drawString(poseStack, font, elementName, nameX, itemY + 5, textColor);
        }
        
        // Scrollbar if needed
        if (visualElements.size() > maxVisible) {
            int scrollbarX = panelX + LAYERS_PANEL_WIDTH - 8;
            int scrollbarHeight = panelHeight - 20;
            int scrollbarY = panelY + 20;
            
            double maxScroll = Math.max(0, visualElements.size() - maxVisible);
            double scrollRatio = layersPanelScrollOffset / maxScroll;
            int thumbHeight = (int) (scrollbarHeight * (maxVisible / (double) visualElements.size()));
            thumbHeight = Math.max(20, thumbHeight);
            int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));
            
            fill(poseStack, scrollbarX, scrollbarY, scrollbarX + 5, scrollbarY + scrollbarHeight, 0x33CCCCCC);
            fill(poseStack, scrollbarX, thumbY, scrollbarX + 5, thumbY + thumbHeight, 0x40CCCCCC);
        }
    }
    
    /**
     * Handle clicks on the layers panel
     */
    private boolean handleLayersPanelClick(double mouseX, double mouseY) {
        int panelHeight = (height - layersPanelY - 50) / 2; // Half the current height
        int panelX = layersPanelX;
        int panelY = layersPanelY;
        
        // Check if clicking on a layer item
        int maxVisible = panelHeight / LAYER_ITEM_HEIGHT;
        int startIndex = layersPanelScrollOffset;
        int endIndex = Math.min(startIndex + maxVisible, visualElements.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            if (i >= visualElements.size()) break;
            VisualElement element = visualElements.get(i);
            int itemY = panelY + 20 + (i - startIndex) * LAYER_ITEM_HEIGHT;
            
            if (mouseY >= itemY && mouseY < itemY + LAYER_ITEM_HEIGHT) {
                int checkboxX = panelX + 5;
                int checkboxY = itemY + 4;
                
                // Check if clicking on checkbox
                if (mouseX >= checkboxX && mouseX < checkboxX + LAYER_CHECKBOX_SIZE &&
                    mouseY >= checkboxY && mouseY < checkboxY + LAYER_CHECKBOX_SIZE) {
                    toggleElementVisibility(element);
                    return true;
                }
                
                // Clicking on the item selects it
                // Deselect all
                for (VisualElement e : visualElements) {
                    e.selected = false;
                }
                selectedElements.clear();
                
                // Select clicked element
                element.selected = true;
                selectedElement = element;
                selectedElements.add(element);
                deleteButton.visible = true;
                return true;
            }
        }
        
        return false;
    }
    
    private void setAutoScale() {
        // Auto-detect optimal scale based on window size and user's GUI scale
        Minecraft mc = Minecraft.getInstance();
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();
        double userGuiScale = mc.getWindow().getGuiScale();
        
        // Calculate effective resolution (accounting for user's GUI scale)
        int effectiveWidth = (int)(windowWidth / userGuiScale);
        int effectiveHeight = (int)(windowHeight / userGuiScale);
        
        // Determine scale based on effective resolution
        if (effectiveWidth >= 1920 && effectiveHeight >= 1080) {
            currentGuiScale = 4;
        } else if (effectiveWidth >= 1280 && effectiveHeight >= 720) {
            currentGuiScale = 3;
        } else if (effectiveWidth >= 854 && effectiveHeight >= 480) {
            currentGuiScale = 2;
        } else {
            currentGuiScale = 1;
        }
        
        setGuiScale(currentGuiScale);
        updateToolButtons();
        updateScaleButtons();
    }
    
    private void deleteSelectedElement() {
        if (selectedElement != null) {
            visualElements.remove(selectedElement);
            config.removeElementConfig(selectedElement.elementId);
            selectedElement = null;
            deleteButton.visible = false;
        }
    }
    
    private void deleteSelectedElements() {
        for (VisualElement element : selectedElements) {
            visualElements.remove(element);
            config.removeElementConfig(element.elementId);
        }
        selectedElements.clear();
        selectedElement = null;
        deleteButton.visible = false;
    }
    
    private VisualElement getElementById(String id) {
        for (VisualElement element : visualElements) {
            if (element.elementId.equals(id)) {
                return element;
            }
        }
        return null;
    }
    
    private void saveConfig() {
        // Get content area offset
        int contentX = 0;
        int contentY = 0;
        if (parentScreen instanceof BuildScapeConfigScreen configScreen) {
            contentX = configScreen.getContentX();
            contentY = configScreen.getContentY();
        }
        
        // Get current screen dimensions (GUI-scaled)
        int screenWidth = this.width;
        int screenHeight = this.height;
        
        // Update screen config with current dimensions for proper scaling
        if (config.screen == null) {
            config.screen = new GuiConfigData.ScreenConfig();
        }
        config.screen.width = screenWidth;
        config.screen.height = screenHeight;
        config.screen.scale = (float)Minecraft.getInstance().getWindow().getGuiScale();
        
        // Separate sidebar and tab elements
        Map<String, GuiConfigData.ElementConfig> sidebarElements = new HashMap<>();
        Map<String, GuiConfigData.ElementConfig> tabElements = new HashMap<>();
        
        // Update config from visual elements
        // Convert from screen coordinates back to content-relative coordinates
        for (VisualElement element : visualElements) {
            boolean isSidebarElement = element.config.properties != null && 
                Boolean.TRUE.equals(element.config.properties.get("isSidebarElement"));
            
            // Calculate absolute screen position for percentage calculation
            int absoluteScreenX = element.config.x;
            int absoluteScreenY = element.config.y;
            
            // For tab elements, convert from screen coordinates to content-relative
            // For sidebar elements, positions are already screen-relative
            int contentRelativeX = isSidebarElement ? element.config.x : element.config.x - contentX;
            int contentRelativeY = isSidebarElement ? element.config.y : element.config.y - contentY;
            
            GuiConfigData.ElementConfig savedConfig = new GuiConfigData.ElementConfig(
                contentRelativeX,
                contentRelativeY,
                element.config.width,
                element.config.height,
                element.config.scale
            );
            savedConfig.visible = element.config.visible;
            savedConfig.properties = element.config.properties != null ? 
                new HashMap<>(element.config.properties) : new HashMap<>();
            
            // Calculate and save percentages based on current screen dimensions
            // This ensures positions scale correctly across different screen sizes and GUI scales
            if (screenWidth > 0 && screenHeight > 0) {
                savedConfig.percentX = (double)absoluteScreenX / screenWidth;
                savedConfig.percentY = (double)absoluteScreenY / screenHeight;
                savedConfig.percentWidth = (double)element.config.width / screenWidth;
                savedConfig.percentHeight = (double)element.config.height / screenHeight;
            }
            
            if (isSidebarElement) {
                // Remove "sidebar." prefix for saving
                String elementId = element.elementId.startsWith("sidebar.") ? 
                    element.elementId.substring(8) : element.elementId;
                sidebarElements.put(elementId, savedConfig);
            } else {
                tabElements.put(element.elementId, savedConfig);
            }
        }
        
        // Save tab config
        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : tabElements.entrySet()) {
            config.setElementConfig(entry.getKey(), entry.getValue());
        }
        configManager.saveConfig(tabName, config);
        configManager.clearCache(tabName);
        
        // Save sidebar config
        if (!sidebarElements.isEmpty()) {
            String sidebarConfigName = "Sidebar";
            GuiConfigData sidebarConfig = configManager.loadConfig(sidebarConfigName);
            for (Map.Entry<String, GuiConfigData.ElementConfig> entry : sidebarElements.entrySet()) {
                sidebarConfig.setElementConfig(entry.getKey(), entry.getValue());
            }
            configManager.saveConfig(sidebarConfigName, sidebarConfig);
            configManager.clearCache(sidebarConfigName);
        }
        
        // Show success message
        if (minecraft.player != null) {
            minecraft.player.sendMessage(
                new TranslatableComponent("buildscape.gui.editor.saved", tabName),
                java.util.UUID.randomUUID()
            );
        }
        
        // Refresh parent screen if it's a BuildScapeConfigScreen
        if (parentScreen instanceof BuildScapeConfigScreen configScreen) {
            // Reinitialize the active tab to apply new positions
            if (configScreen.getActiveTab() != null && 
                configScreen.getActiveTab().getTabName().equals(tabName)) {
                configScreen.getActiveTab().init();
            }
            // Reinitialize sidebar buttons
            configScreen.init();
        }
        
        onClose();
    }
    
    private void resetConfig() {
        config.elements.clear();
        configManager.clearCache(tabName);
        GuiConfigData freshConfig = configManager.loadConfig(tabName);
        config.elements.putAll(freshConfig.elements);
        
        visualElements.clear();
        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : config.elements.entrySet()) {
            visualElements.add(new VisualElement(entry.getKey(), entry.getValue()));
        }
        
        selectedElement = null;
        deleteButton.visible = false;
    }
    
    private void scanCurrentTab() {
        if (sourceTab == null) {
            return;
        }
        
        try {
            java.lang.reflect.Field[] fields = sourceTab.getClass().getDeclaredFields();
            Map<String, GuiConfigData.ElementConfig> discoveredWidgets = new HashMap<>();
            
            // Get content area for position conversion
            int contentX = 0;
            int contentY = 0;
            if (parentScreen instanceof BuildScapeConfigScreen configScreen) {
                contentX = configScreen.getContentX();
                contentY = configScreen.getContentY();
            }
            
            // Scan all fields including parent classes
            Class<?> currentClass = sourceTab.getClass();
            while (currentClass != null && currentClass != Object.class) {
                java.lang.reflect.Field[] classFields = currentClass.getDeclaredFields();
                for (java.lang.reflect.Field field : classFields) {
                field.setAccessible(true);
                Object value = field.get(sourceTab);
                
                if (value == null) continue;
                
                String elementId = field.getName();
                GuiConfigData.ElementConfig elementConfig = null;
                
                if (value instanceof net.minecraft.client.gui.components.AbstractWidget widget) {
                    // Skip if widget is not visible or has invalid position
                        if (!widget.visible || widget.x < 0 || widget.y < 0) continue;
                        // Convert to content-relative coordinates
                    elementConfig = new GuiConfigData.ElementConfig(
                            widget.x - contentX, widget.y - contentY, widget.getWidth(), 20
                    );
                    try {
                        java.lang.reflect.Field heightField = 
                            net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                        heightField.setAccessible(true);
                        elementConfig.height = heightField.getInt(widget);
                    } catch (Exception e) {}
                } else if (value instanceof EditBox editBox) {
                    // Skip if edit box is not visible or has invalid position
                        if (!editBox.visible || editBox.x < 0 || editBox.y < 0) continue;
                        // Convert to content-relative coordinates
                    elementConfig = new GuiConfigData.ElementConfig(
                            editBox.x - contentX, editBox.y - contentY, editBox.getWidth(), 20
                    );
                    try {
                        java.lang.reflect.Field heightField = EditBox.class.getDeclaredField("height");
                        heightField.setAccessible(true);
                        elementConfig.height = heightField.getInt(editBox);
                    } catch (Exception e) {}
                    } else if (value instanceof Button button) {
                        // Also check Button specifically (it extends AbstractWidget but might be missed)
                    if (!button.visible || button.x < 0 || button.y < 0) continue;
                        elementConfig = new GuiConfigData.ElementConfig(
                            button.x - contentX, button.y - contentY, button.getWidth(), 20
                        );
                        try {
                            java.lang.reflect.Field heightField = 
                                net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                            heightField.setAccessible(true);
                            elementConfig.height = heightField.getInt(button);
                        } catch (Exception e) {}
                    }
                    
                    if (elementConfig != null && !discoveredWidgets.containsKey(elementId)) {
                    discoveredWidgets.put(elementId, elementConfig);
                }
                }
                currentClass = currentClass.getSuperclass();
            }
            
            // Also scan renderable widgets from Screen's children list
            try {
                java.lang.reflect.Field childrenField = Screen.class.getDeclaredField("children");
                childrenField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<net.minecraft.client.gui.components.events.GuiEventListener> children = 
                    (List<net.minecraft.client.gui.components.events.GuiEventListener>) childrenField.get(sourceTab);
                if (children != null) {
                    int index = 0;
                    for (net.minecraft.client.gui.components.events.GuiEventListener child : children) {
                        if (child instanceof net.minecraft.client.gui.components.AbstractWidget widget) {
                            if (widget.visible && widget.x >= 0 && widget.y >= 0) {
                                String elementId = "widget_" + index++;
                                if (!discoveredWidgets.containsKey(elementId)) {
                                    GuiConfigData.ElementConfig elementConfig = new GuiConfigData.ElementConfig(
                                        widget.x - contentX, widget.y - contentY, widget.getWidth(), 20
                                    );
                                    try {
                                        java.lang.reflect.Field heightField = 
                                            net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                                        heightField.setAccessible(true);
                                        elementConfig.height = heightField.getInt(widget);
                                    } catch (Exception e) {}
                                    discoveredWidgets.put(elementId, elementConfig);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Reflection might fail, that's okay
            }
            
            // Also scan for text labels by checking render method
            scanTextLabels(discoveredWidgets, contentX, contentY);
            
            if (!discoveredWidgets.isEmpty()) {
                com.kingodogo.buildscape.client.screen.GuiConfigHelper.registerWidgetDefaults(tabName, discoveredWidgets);
                configManager.clearCache(tabName);
                GuiConfigData newConfig = configManager.loadConfig(tabName);
                
                // Convert to screen coordinates for editor
                visualElements.clear();
                for (Map.Entry<String, GuiConfigData.ElementConfig> entry : newConfig.elements.entrySet()) {
                    GuiConfigData.ElementConfig elementConfig = entry.getValue();
                    boolean isSidebarElement = elementConfig.properties != null && 
                        Boolean.TRUE.equals(elementConfig.properties.get("isSidebarElement"));
                    
                    int screenX = isSidebarElement ? elementConfig.x : elementConfig.x + contentX;
                    int screenY = isSidebarElement ? elementConfig.y : elementConfig.y + contentY;
                    
                    GuiConfigData.ElementConfig screenConfig = new GuiConfigData.ElementConfig(
                        screenX, screenY, elementConfig.width, elementConfig.height, elementConfig.scale
                    );
                    screenConfig.visible = elementConfig.visible;
                    screenConfig.properties = elementConfig.properties != null ? 
                        new HashMap<>(elementConfig.properties) : new HashMap<>();
                    visualElements.add(new VisualElement(entry.getKey(), screenConfig));
                }
                
                if (minecraft.player != null) {
                    minecraft.player.sendMessage(
                        new TranslatableComponent("buildscape.gui.editor.scanned", discoveredWidgets.size()),
                        java.util.UUID.randomUUID()
                    );
                }
            }
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to scan tab: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Scan for text labels in the tab's render method
     * This method attempts to detect all text labels by analyzing the tab's structure
     */
    private void scanTextLabels(Map<String, GuiConfigData.ElementConfig> discoveredWidgets, int contentX, int contentY) {
        if (sourceTab == null) return;
        
        try {
            // PillarItemsConfigTab text labels
            if (sourceTab instanceof PillarItemsConfigTab) {
                addTextLabel(discoveredWidgets, "pillarItemsLabel", 
                    new TranslatableComponent("buildscape.config.pillar_items").getString(),
                    0, 10, "buildscape.config.pillar_items");
                
                // Dynamic label (All items / Inventory items / Mod items)
                addTextLabel(discoveredWidgets, "allItemsLabel", 
                    "All items", 0, (parentScreen.height - contentY) / 2, null, true);
                
                // "Presets" label (from PresetsWidget)
                addTextLabel(discoveredWidgets, "presetsLabel", 
                    "Presets", (parentScreen.width - contentX) / 2 + 15, 10, null);
            } 
            // PillarParticlesConfigTab text labels
            else if (sourceTab instanceof PillarParticlesConfigTab) {
                // Particles title (scaled)
                String titleText = new TranslatableComponent("buildscape.config.particles.title").getString();
                int textWidth = (int)(font.width(titleText) * 2.5f);
                GuiConfigData.ElementConfig titleConfig = new GuiConfigData.ElementConfig(
                    16, 6, textWidth + 10, 20
                );
                titleConfig.properties = new HashMap<>();
                titleConfig.properties.put("isTextElement", true);
                titleConfig.properties.put("text", titleText);
                titleConfig.properties.put("textKey", "buildscape.config.particles.title");
                titleConfig.properties.put("scale", 2.5f);
                discoveredWidgets.put("particlesTitle", titleConfig);
                
                // "Default Properties" label
                addTextLabel(discoveredWidgets, "defaultPropertiesLabel",
                    new TranslatableComponent("buildscape.config.particles.default_properties").getString(),
                    20, 50, "buildscape.config.particles.default_properties");
                
                // "Custom Properties" label
                addTextLabel(discoveredWidgets, "customPropertiesLabel",
                    new TranslatableComponent("buildscape.config.particles.custom_properties").getString(),
                    (parentScreen.width - contentX) / 2 + 15, 50, "buildscape.config.particles.custom_properties");
                
                // Field labels
                addTextLabel(discoveredWidgets, "particleSpeedLabel",
                    new TranslatableComponent("buildscape.config.particles.particle_speed").getString() + " ",
                    20, 90, "buildscape.config.particles.particle_speed");
                addTextLabel(discoveredWidgets, "particleSpreadLabel",
                    new TranslatableComponent("buildscape.config.particles.particle_spread").getString() + " ",
                    20, 116, "buildscape.config.particles.particle_spread");
                addTextLabel(discoveredWidgets, "particleLifetimeLabel",
                    new TranslatableComponent("buildscape.config.particles.particle_lifetime").getString() + " ",
                    20, 142, "buildscape.config.particles.particle_lifetime");
                addTextLabel(discoveredWidgets, "particleDensityLabel",
                    new TranslatableComponent("buildscape.config.particles.particle_density").getString() + " ",
                    20, 168, "buildscape.config.particles.particle_density");
                
                addTextLabel(discoveredWidgets, "patternSpeedLabel",
                    new TranslatableComponent("buildscape.config.particles.pattern_speed").getString() + " ",
                    (parentScreen.width - contentX) / 2 + 15, 90, "buildscape.config.particles.pattern_speed");
                addTextLabel(discoveredWidgets, "patternSpreadLabel",
                    new TranslatableComponent("buildscape.config.particles.pattern_spread").getString() + " ",
                    (parentScreen.width - contentX) / 2 + 15, 116, "buildscape.config.particles.pattern_spread");
                addTextLabel(discoveredWidgets, "patternIntensityLabel",
                    new TranslatableComponent("buildscape.config.particles.pattern_intensity").getString() + " ",
                    (parentScreen.width - contentX) / 2 + 15, 142, "buildscape.config.particles.pattern_intensity");
            }
            // PillarIdsConfigTab text labels
            else if (sourceTab instanceof PillarIdsConfigTab) {
                // Column headers - these are rendered in drawHeader method
                // Get the actual positions from the table structure
                int tableX = 12; // TABLE_MARGIN
                int tableY = 32; // contentY + 32
                int[] columns = new int[] { 
                    (int)((parentScreen.width - contentX - 24) * 0.22f),
                    (int)((parentScreen.width - contentX - 24) * 0.36f),
                    (int)((parentScreen.width - contentX - 24) * 0.16f)
                };
                int headerY = tableY + 28; // tableY + HEADER_HEIGHT
                
                int x = tableX;
                addTextLabel(discoveredWidgets, "pillarIdHeader",
                    new TranslatableComponent("buildscape.config.ids.id").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.id");
                x += columns[0] + 8; // COLUMN_GAP
                
                addTextLabel(discoveredWidgets, "colorsHeader",
                    new TranslatableComponent("buildscape.config.ids.colors").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.colors");
                x += columns[1] + 8;
                
                addTextLabel(discoveredWidgets, "dimensionHeader",
                    new TranslatableComponent("buildscape.config.ids.dimension").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.dimension");
                x += columns[2] + 8;
                
                addTextLabel(discoveredWidgets, "coordsHeader",
                    new TranslatableComponent("buildscape.config.ids.coords").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.coords");
            }
            // BuildScapeConfigScreen title
            if (parentScreen instanceof BuildScapeConfigScreen) {
                addTextLabel(discoveredWidgets, "configScreenTitle",
                    "BuildScape Config", parentScreen.width / 2 - font.width("BuildScape Config") / 2, 10, null);
            }
        } catch (Exception e) {
            BuildScape.getLogger().warn("Failed to scan text labels: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to add a text label to discovered widgets
     */
    private void addTextLabel(Map<String, GuiConfigData.ElementConfig> discoveredWidgets, 
                             String elementId, String text, int x, int y, String textKey) {
        addTextLabel(discoveredWidgets, elementId, text, x, y, textKey, false);
    }
    
    /**
     * Helper method to add a text label to discovered widgets
     */
    private void addTextLabel(Map<String, GuiConfigData.ElementConfig> discoveredWidgets, 
                             String elementId, String text, int x, int y, String textKey, boolean isDynamic) {
        if (text == null || text.isEmpty()) return;
        
        int textWidth = font.width(text);
        int textHeight = 12;
        
        GuiConfigData.ElementConfig labelConfig = new GuiConfigData.ElementConfig(
            x, y, textWidth + 10, textHeight
        );
        labelConfig.properties = new HashMap<>();
        labelConfig.properties.put("isTextElement", true);
        labelConfig.properties.put("text", text);
        if (textKey != null) {
            labelConfig.properties.put("textKey", textKey);
        }
        if (isDynamic) {
            labelConfig.properties.put("isDynamic", true);
        }
        discoveredWidgets.put(elementId, labelConfig);
    }
    
    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }
}
