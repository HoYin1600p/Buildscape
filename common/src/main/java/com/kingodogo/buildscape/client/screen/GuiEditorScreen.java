package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.GuiConfigData;
import com.kingodogo.buildscape.config.GuiConfigManager;
import com.kingodogo.buildscape.client.screen.widget.WidgetLayoutHelper;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GuiEditorScreen implements IScreenDelegate {
    public int width;
    public int height;
    protected Minecraft minecraft;
    public Font font;
    private final Component title;
    private Consumer<AbstractWidget> addWidgetConsumer;
    private BuildScapeConfigScreen configScreen;

    public BuildScapeConfigScreen getConfigScreen() {
        if (this.configScreen != null) return this.configScreen;
        if (this.sourceTab != null) return this.sourceTab.parent;
        return null;
    }
    private Consumer<GuiEventListener> removeWidgetConsumer;
    private GuiEventListener focused;

    private final Screen parentScreen;
    private final String tabName;
    private final AbstractConfigTab sourceTab;
    private final GuiConfigData config;
    private final GuiConfigManager configManager;

    private final List<VisualElement> visualElements = new ArrayList<>();
    private VisualElement selectedElement = null;
    private final List<VisualElement> selectedElements = new ArrayList<>();
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

    private boolean showTextEditor = false;
    private EditBox textContentField;
    private Button saveTextButton;
    private VisualElement editingTextElement = null;

    private EditBox opacityField;
    private EditBox backgroundColorField;
    private EditBox borderColorField;
    private EditBox borderWidthField;
    private ResizeHandle hoveredHandle = ResizeHandle.NONE;
    private boolean isDragging = false;
    private boolean isResizing = false;
    private long lastClickTime = 0;
    private VisualElement lastClickedElement = null;

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
    private final boolean toolboxExpanded = true;

    private int currentGuiScale = 2;
    private Button scale1Button;
    private Button scale2Button;
    private Button scale3Button;
    private Button scale4Button;
    private Button autoScaleButton;

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

    private int toolboxX = 0;
    private int toolboxY = 10;
    private boolean isDraggingToolbox = false;
    private double toolboxDragStartX = 0;
    private double toolboxDragStartY = 0;
    private int toolboxDragStartXPos = 0;
    private int toolboxDragStartYPos = 0;

    private static final int HANDLE_SIZE = 10;
    private static final int BORDER_THICKNESS = 2;
    private static final int HANDLE_MARGIN = 8;

    private static class VisualElement {
        String elementId;
        GuiConfigData.ElementConfig config;
        boolean selected = false;

        VisualElement(String elementId, GuiConfigData.ElementConfig config) {
            this.elementId = elementId;
            this.config = config != null ? new GuiConfigData.ElementConfig(config) : new GuiConfigData.ElementConfig(0, 0, 100, 20);
        }
    }

    private enum ResizeHandle {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT
    }

    private Button newButton(int x, int y, int width, int height, Component message, Button.OnPress onPress) {
        return Services.PLATFORM.createButton(x, y, width, height, message, onPress);
    }

    private <T extends GuiEventListener> T addRenderableWidget(T widget) {
        if (widget instanceof AbstractWidget abstractWidget && addWidgetConsumer != null) {
            addWidgetConsumer.accept(abstractWidget);
        }
        return widget;
    }

    private void fill(Object poseStackOrGraphics, int x1, int y1, int x2, int y2, int color) {
        Services.PLATFORM.fill(poseStackOrGraphics, x1, y1, x2, y2, color);
    }

    private void drawCenteredString(Object poseStackOrGraphics, Font font, Component text, int x, int y, int color) {
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, x, y, color);
    }

    private void drawCenteredString(Object poseStackOrGraphics, Font font, String text, int x, int y, int color) {
        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, text, x, y, color);
    }

    private void drawString(Object poseStackOrGraphics, Font font, String text, int x, int y, int color) {
        Services.PLATFORM.draw(poseStackOrGraphics, font, text, x, y, color);
    }

    private void setWidgetPos(AbstractWidget widget, int x, int y) {
        if (widget != null) {
            WidgetLayoutHelper.setPosition(widget, x, y);
        }
    }

    private boolean hasControlDown() {
        return Services.PLATFORM.hasControlDown();
    }

    private boolean hasShiftDown() {
        return Services.PLATFORM.hasShiftDown();
    }

    public void setFocused(GuiEventListener focused) {
        this.focused = focused;
    }

    public GuiEventListener getFocused() {
        return this.focused;
    }

    public GuiEditorScreen(Screen parent, String tabName) {
        this(parent, tabName, null);
    }

    public GuiEditorScreen(Screen parent, String tabName, AbstractConfigTab sourceTab) {
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;
        this.title = ComponentHelper.translatable("buildscape.gui.editor.title", tabName);
        this.parentScreen = parent;
        this.tabName = tabName;
        this.sourceTab = sourceTab;
        this.configManager = GuiConfigManager.get();
        this.config = configManager.loadConfig(tabName);

        int contentX = 0;
        int contentY = 0;
        BuildScapeConfigScreen cfgScreen = getConfigScreen();
        if (cfgScreen != null) {
            contentX = cfgScreen.getContentX();
            contentY = cfgScreen.getContentY();
        }

        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : config.elements.entrySet()) {
            GuiConfigData.ElementConfig elementConfig = entry.getValue();

            boolean isSidebarElement = elementConfig.properties != null &&
                Boolean.TRUE.equals(elementConfig.properties.get("isSidebarElement"));

            int screenX = isSidebarElement ? elementConfig.x : elementConfig.x + contentX;
            int screenY = isSidebarElement ? elementConfig.y : elementConfig.y + contentY;

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

        if (getConfigScreen() != null) {
            loadSidebarElements(getConfigScreen());
        }
    }

    private void loadSidebarElements(BuildScapeConfigScreen configScreen) {
        String sidebarConfigName = "Sidebar";
        GuiConfigData sidebarConfig = configManager.loadConfig(sidebarConfigName);

        if (sidebarConfig.elements.isEmpty()) {
            int sidebarX = 10;
            int sidebarY = 30;
            int buttonWidth = 180;
            int buttonHeight = 20;
            int spacing = 2;

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

            for (Map.Entry<String, GuiConfigData.ElementConfig> entry : sidebarDefaults.entrySet()) {
                if (entry.getValue().properties == null) {
                    entry.getValue().properties = new HashMap<>();
                }
                entry.getValue().properties.put("isSidebarElement", true);
            }

            GuiConfigHelper.registerWidgetDefaults(sidebarConfigName, sidebarDefaults);
            sidebarConfig = configManager.loadConfig(sidebarConfigName);
        }

        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : sidebarConfig.elements.entrySet()) {
            GuiConfigData.ElementConfig elementConfig = entry.getValue();
            if (elementConfig.properties == null) {
                elementConfig.properties = new HashMap<>();
            }
            elementConfig.properties.put("isSidebarElement", true);

            VisualElement visualElement = new VisualElement("sidebar." + entry.getKey(), elementConfig);
            visualElements.add(visualElement);
        }
    }

    @Override
    public void init(Minecraft mc, int width, int height, Consumer<AbstractWidget> addWidget, Consumer<GuiEventListener> removeWidget) {
        this.minecraft = mc;
        this.font = mc.font;
        this.width = width;
        this.height = height;
        this.addWidgetConsumer = addWidget;
        this.removeWidgetConsumer = removeWidget;
        Font font = mc.font;

        int buttonY = height - 30;
        int buttonWidth = 100;
        int buttonSpacing = 110;

        saveButton = newButton(
            width / 2 - buttonSpacing - buttonWidth / 2, buttonY,
            buttonWidth, 20,
            ComponentHelper.translatable("buildscape.gui.editor.save"),
            (button) -> saveConfig()
        );
        addRenderableWidget(saveButton);

        resetButton = newButton(
            width / 2 - buttonWidth / 2, buttonY,
            buttonWidth, 20,
            ComponentHelper.translatable("buildscape.gui.editor.reset"),
            (button) -> resetConfig()
        );
        addRenderableWidget(resetButton);

        closeButton = newButton(
            width / 2 + buttonSpacing - buttonWidth / 2, buttonY,
            buttonWidth, 20,
            ComponentHelper.translatable("gui.cancel"),
            (button) -> onClose()
        );
        addRenderableWidget(closeButton);

        toolboxX = width - 50;
        toolboxY = 10;
        int toolButtonSize = 20;
        int toolSpacing = 2;

        moveToolButton = newButton(
            toolboxX, toolboxY,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("M"),
            (button) -> setCurrentTool(EditorTool.MOVE)
        );
        addRenderableWidget(moveToolButton);

        editToolButton = newButton(
            toolboxX, toolboxY + toolButtonSize + toolSpacing,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("E"),
            (button) -> setCurrentTool(EditorTool.EDIT)
        );
        addRenderableWidget(editToolButton);

        editTextToolButton = newButton(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 2,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("T"),
            (button) -> setCurrentTool(EditorTool.EDIT_TEXT)
        );
        addRenderableWidget(editTextToolButton);

        createTextToolButton = newButton(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 3,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("N"),
            (button) -> setCurrentTool(EditorTool.CREATE_TEXT)
        );
        addRenderableWidget(createTextToolButton);

        createWidgetToolButton = newButton(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 4,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("W"),
            (button) -> {
                setCurrentTool(EditorTool.CREATE_WIDGET);
                showAddWidgetDialog = true;
            }
        );
        addRenderableWidget(createWidgetToolButton);

        createBackgroundButton = newButton(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 5,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("B"),
            (button) -> setCurrentTool(EditorTool.CREATE_BACKGROUND)
        );
        addRenderableWidget(createBackgroundButton);

        createSearchBarButton = newButton(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 6,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("S"),
            (button) -> setCurrentTool(EditorTool.CREATE_SEARCH_BAR)
        );
        addRenderableWidget(createSearchBarButton);

        createTextBoxButton = newButton(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 7,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("X"),
            (button) -> setCurrentTool(EditorTool.CREATE_TEXT_BOX)
        );
        addRenderableWidget(createTextBoxButton);

        selectGroupToolButton = newButton(
            toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 8,
            toolButtonSize, toolButtonSize,
            ComponentHelper.literal("G"),
            (button) -> setCurrentTool(EditorTool.SELECT_GROUP)
        );
        addRenderableWidget(selectGroupToolButton);

        int scaleY = toolboxY + (toolButtonSize + toolSpacing) * 9 + 5;
        int scaleX = toolboxX - 180;
        scale1Button = newButton(
            scaleX, scaleY,
            35, toolButtonSize,
            ComponentHelper.literal("1x"),
            (button) -> setGuiScale(1)
        );
        addRenderableWidget(scale1Button);

        scale2Button = newButton(
            scaleX + 37, scaleY,
            35, toolButtonSize,
            ComponentHelper.literal("2x"),
            (button) -> setGuiScale(2)
        );
        addRenderableWidget(scale2Button);

        scale3Button = newButton(
            scaleX + 74, scaleY,
            35, toolButtonSize,
            ComponentHelper.literal("3x"),
            (button) -> setGuiScale(3)
        );
        addRenderableWidget(scale3Button);

        scale4Button = newButton(
            scaleX + 111, scaleY,
            35, toolButtonSize,
            ComponentHelper.literal("4x"),
            (button) -> setGuiScale(4)
        );
        addRenderableWidget(scale4Button);

        autoScaleButton = newButton(
            scaleX + 148, scaleY,
            50, toolButtonSize,
            ComponentHelper.literal("Auto"),
            (button) -> setAutoScale()
        );
        addRenderableWidget(autoScaleButton);

        addWidgetButton = newButton(
            width - 120, height - 100,
            110, 20,
            ComponentHelper.translatable("buildscape.gui.editor.add_widget"),
            (button) -> {
                setCurrentTool(EditorTool.CREATE_WIDGET);
                showAddWidgetDialog = true;
            }
        );
        addRenderableWidget(addWidgetButton);

        updateToolButtons();

        deleteButton = newButton(
            width - 120, 35,
            110, 20,
            ComponentHelper.translatable("buildscape.gui.editor.delete"),
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

        if (sourceTab != null && visualElements.isEmpty()) {
            scanTabButton = newButton(
                width / 2 - 150, height / 2,
                300, 20,
                ComponentHelper.translatable("buildscape.gui.editor.scantab"),
                (button) -> scanCurrentTab()
            );
            addRenderableWidget(scanTabButton);
        }

        newWidgetNameField = new EditBox(
            font, width / 2 - 100, height / 2 - 20,
            200, 20,
            ComponentHelper.literal("Widget Name")
        );
        newWidgetNameField.setMaxLength(64);
        newWidgetNameField.visible = false;
        addRenderableWidget(newWidgetNameField);

        searchTargetField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            ComponentHelper.literal("Search Target Widget ID")
        );
        searchTargetField.setMaxLength(64);
        searchTargetField.visible = false;
        addRenderableWidget(searchTargetField);

        savePropertiesButton = newButton(
            width / 2 - 50, height / 2 + 35,
            100, 20,
            ComponentHelper.translatable("buildscape.gui.editor.save"),
            (button) -> saveElementProperties()
        );
        savePropertiesButton.visible = false;
        addRenderableWidget(savePropertiesButton);

        opacityField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            ComponentHelper.literal("Opacity (0-255)")
        );
        opacityField.setMaxLength(3);
        Services.PLATFORM.setEditBoxFilter(opacityField, s -> s.matches("[0-9]*"));
        opacityField.visible = false;
        addRenderableWidget(opacityField);

        backgroundColorField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            ComponentHelper.literal("Background Color (RRGGBB)")
        );
        backgroundColorField.setMaxLength(6);
        Services.PLATFORM.setEditBoxFilter(backgroundColorField, s -> s.matches("[0-9a-fA-F]*"));
        backgroundColorField.visible = false;
        addRenderableWidget(backgroundColorField);

        borderColorField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            ComponentHelper.literal("Border Color (RRGGBB)")
        );
        borderColorField.setMaxLength(6);
        Services.PLATFORM.setEditBoxFilter(borderColorField, s -> s.matches("[0-9a-fA-F]*"));
        borderColorField.visible = false;
        addRenderableWidget(borderColorField);

        borderWidthField = new EditBox(
            font, width / 2 - 100, height / 2 + 10,
            200, 20,
            ComponentHelper.literal("Border Width")
        );
        borderWidthField.setMaxLength(3);
        Services.PLATFORM.setEditBoxFilter(borderWidthField, s -> s.matches("[0-9]*"));
        borderWidthField.visible = false;
        addRenderableWidget(borderWidthField);

        textContentField = new EditBox(
            font, width / 2 - 150, height / 2 - 10,
            300, 20,
            ComponentHelper.literal("Text Content")
        );
        textContentField.setMaxLength(256);
        textContentField.visible = false;
        addRenderableWidget(textContentField);

        saveTextButton = newButton(
            width / 2 - 50, height / 2 + 15,
            100, 20,
            ComponentHelper.translatable("buildscape.gui.editor.save"),
            (button) -> saveTextContent()
        );
        saveTextButton.visible = false;
        addRenderableWidget(saveTextButton);
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        fill(poseStackOrGraphics, 0, 0, width, height, 0x80000000);

        float scaleFactor = currentGuiScale / 2.0f;
        int scaledMouseX = mouseX;
        int scaledMouseY = mouseY;

        if (scaleFactor != 1.0f) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scaleFactor, scaleFactor);
            scaledMouseX = (int)(mouseX / scaleFactor);
            scaledMouseY = (int)(mouseY / scaleFactor);
        }

        if (getConfigScreen() != null) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            fill(poseStackOrGraphics, 0, 0, width, height, 0x80000000);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.translateGuiPose(poseStackOrGraphics, 0, 0, -100);
            sourceTab.render(poseStackOrGraphics, scaledMouseX, scaledMouseY, partialTick);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        }

        drawCenteredString(poseStackOrGraphics, font, title, width / 2, 10, 0xFFFFFF);

        String instructions = ComponentHelper.translatable("buildscape.gui.editor.instructions_visual").getString();
        drawString(poseStackOrGraphics, font, instructions, 10, 25, 0xCCCCCC);

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
            drawString(poseStackOrGraphics, font, hint, 10, 40, 0xAAAAAA);
            drawString(poseStackOrGraphics, font, toolHint, 10, 55, 0x888888);
        } else {
            String hint = "Right-click empty space to create new widget | " + toolHint;
            if (currentTool == EditorTool.EDIT_TEXT) {
                hint += " | Click any element to edit its text";
            }
            drawString(poseStackOrGraphics, font, hint, 10, 40, 0xAAAAAA);
        }

        if (toolboxExpanded) {
            int toolboxW = 22;
            int toolboxH = 132;
            fill(poseStackOrGraphics, toolboxX - 1, toolboxY - 1, toolboxX + toolboxW, toolboxY + toolboxH, 0xCC000000);
            fill(poseStackOrGraphics, toolboxX - 1, toolboxY - 1, toolboxX + toolboxW, toolboxY, 0xFFFFFFFF);
            fill(poseStackOrGraphics, toolboxX - 1, toolboxY + toolboxH - 1, toolboxX + toolboxW, toolboxY + toolboxH, 0xFFFFFFFF);
            fill(poseStackOrGraphics, toolboxX - 1, toolboxY - 1, toolboxX, toolboxY + toolboxH, 0xFFFFFFFF);
            fill(poseStackOrGraphics, toolboxX + toolboxW - 1, toolboxY - 1, toolboxX + toolboxW, toolboxY + toolboxH, 0xFFFFFFFF);
        }

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
                fill(poseStackOrGraphics, tooltipX - 4, tooltipY - 2, tooltipX + tooltipWidth + 4, tooltipY + 10, 0xDD000000);
                drawString(poseStackOrGraphics, font, tooltip, tooltipX, tooltipY, 0xFFFFFF);
            }
        }

        renderLayersPanel(poseStackOrGraphics, mouseX, mouseY);

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
            renderVisualElement(poseStackOrGraphics, element, scaledMouseX, scaledMouseY);
        }

        hoveredHandle = ResizeHandle.NONE;
        if (selectedElement != null) {
            hoveredHandle = getResizeHandle(selectedElement, scaledMouseX, scaledMouseY);
        }

        if (showAddWidgetDialog) {
            renderAddWidgetDialog(poseStackOrGraphics, scaledMouseX, scaledMouseY);
        }

        if (showPropertiesDialog && selectedElement != null) {
            renderPropertiesDialog(poseStackOrGraphics, scaledMouseX, scaledMouseY);
        }

        if (showTextEditor && editingTextElement != null) {
            renderTextEditorDialog(poseStackOrGraphics, scaledMouseX, scaledMouseY);
        }

        if (scaleFactor != 1.0f) {
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        }

        if (visualElements.isEmpty() && (sourceTab == null || scanTabButton == null)) {
            drawCenteredString(poseStackOrGraphics, font,
                ComponentHelper.translatable("buildscape.gui.editor.no_elements").getString(),
                width / 2, height / 2 - 20, 0xCCCCCC);
        }

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
        fill(poseStackOrGraphics, width - textWidth - 10, height - 50, width, height - 30, 0x80000000);
        drawString(poseStackOrGraphics, font, coordText, width - textWidth - 8, height - 48, 0xFFFFFF);

        if ((isDragging || isResizing) && selectedElement != null) {
            renderAlignmentGuides(poseStackOrGraphics, selectedElement);
        }
    }

    private void renderAlignmentGuides(Object poseStackOrGraphics, VisualElement element) {
        int x = element.config.x;
        int y = element.config.y;
        int w = element.config.width;
        int h = element.config.height;

        for (VisualElement other : visualElements) {
            if (other == element) continue;
            if (!isElementVisible(other)) continue;

            int ox = other.config.x;
            int oy = other.config.y;
            int ow = other.config.width;
            int oh = other.config.height;

            if (Math.abs(x - ox) < 5 || Math.abs(x - (ox + ow)) < 5 ||
                Math.abs((x + w) - ox) < 5 || Math.abs((x + w) - (ox + ow)) < 5) {
                fill(poseStackOrGraphics, x, 0, x + 1, height, 0x4000FF00);
            }

            if (Math.abs(y - oy) < 5 || Math.abs(y - (oy + oh)) < 5 ||
                Math.abs((y + h) - oy) < 5 || Math.abs((y + h) - (oy + oh)) < 5) {
                fill(poseStackOrGraphics, 0, y, width, y + 1, 0x4000FF00);
            }
        }
    }

    private void renderVisualElement(Object poseStackOrGraphics, VisualElement element, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        int w = getElementWidthForScale(element, currentGuiScale);
        int h = getElementHeightForScale(element, currentGuiScale);
        int x = element.config.x;
        int y = element.config.y;

        boolean isTextElement = element.config.properties != null &&
            Boolean.TRUE.equals(element.config.properties.get("isTextElement"));
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

        boolean mouseOver = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        ResizeHandle handle = element.selected ? getResizeHandle(element, mouseX, mouseY) : ResizeHandle.NONE;
        if (handle != ResizeHandle.NONE) {
            mouseOver = false;
        }

        int borderColor = element.selected ? 0xFFFFFF00 : (mouseOver ? 0xFF00FFFF : 0xFFFFFFFF);
        int fillColor = element.selected ? 0x50FFFFFF : 0x20FFFFFF;

        if ((isDragging && draggingElementId != null && draggingElementId.equals(element.elementId)) ||
            (isResizing && resizingElementId != null && resizingElementId.equals(element.elementId))) {
            fillColor = 0x60FFFFFF;
            borderColor = 0xFFFF00FF;
        }

        boolean isBackground = element.config.properties != null &&
            Boolean.TRUE.equals(element.config.properties.get("isBackground"));
        boolean isSearchBar = element.config.properties != null &&
            Boolean.TRUE.equals(element.config.properties.get("isSearchBar"));
        boolean isTextBox = element.config.properties != null &&
            Boolean.TRUE.equals(element.config.properties.get("isTextBox"));

        if (isBackground || isSearchBar || isTextBox) {
            int bgOpacity = 192;
            int bgColor = 0x101010;
            int elementBorderColor = 0xFFFFFF;
            int borderWidth = 1;

            if (element.config.properties != null) {
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

                if (element.config.properties.containsKey("backgroundColor")) {
                    String colorStr = (String) element.config.properties.get("backgroundColor");
                    if (colorStr != null && !colorStr.isEmpty()) {
                        try {
                            bgColor = Integer.parseInt(colorStr, 16);
                        } catch (NumberFormatException e) {}
                    }
                }

                if (element.config.properties.containsKey("borderColor")) {
                    String colorStr = (String) element.config.properties.get("borderColor");
                    if (colorStr != null && !colorStr.isEmpty()) {
                        try {
                            elementBorderColor = Integer.parseInt(colorStr, 16);
                        } catch (NumberFormatException e) {}
                    }
                }

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

            int finalBgColor = (bgOpacity << 24) | bgColor;
            fill(poseStackOrGraphics, x, y, x + w, y + h, finalBgColor);

            if (borderWidth > 0) {
                int finalBorderColor = (0xFF << 24) | elementBorderColor;
                fill(poseStackOrGraphics, x, y, x + w, y + borderWidth, finalBorderColor);
                fill(poseStackOrGraphics, x, y + h - borderWidth, x + w, y + h, finalBorderColor);
                fill(poseStackOrGraphics, x, y, x + borderWidth, y + h, finalBorderColor);
                fill(poseStackOrGraphics, x + w - borderWidth, y, x + w, y + h, finalBorderColor);
            }
        } else {
            fill(poseStackOrGraphics, x, y, x + w, y + h, fillColor);
        }

        if (isTextElement && element.config.properties != null) {
            String text = (String) element.config.properties.get("text");
            if (text != null && !text.isEmpty()) {
                String processedText = text.replaceAll("&([0-9a-fk-or])", "§$1");
                drawString(poseStackOrGraphics, font, processedText, x + 2, y + 2, 0xFFFFFF);
            }
        }

        if (isTextBox && element.config.properties != null) {
            String text = (String) element.config.properties.get("text");
            if (text != null && !text.isEmpty()) {
                String processedText = text.replaceAll("&([0-9a-fk-or])", "§$1");
                drawString(poseStackOrGraphics, font, processedText, x + 5, y + h / 2 - 4, 0xFFFFFF);
            }
        }

        if (isSearchBar && element.config.properties != null) {
            String placeholder = (String) element.config.properties.get("placeholder");
            if (placeholder == null || placeholder.isEmpty()) {
                placeholder = "Search...";
            }
            int textColor = element.selected ? 0xFFFFFF : 0x888888;
            drawString(poseStackOrGraphics, font, placeholder, x + 5, y + h / 2 - 4, textColor);
        }

        fill(poseStackOrGraphics, x, y, x + w, y + BORDER_THICKNESS, borderColor);
        fill(poseStackOrGraphics, x, y + h - BORDER_THICKNESS, x + w, y + h, borderColor);
        fill(poseStackOrGraphics, x, y, x + BORDER_THICKNESS, y + h, borderColor);
        fill(poseStackOrGraphics, x + w - BORDER_THICKNESS, y, x + w, y + h, borderColor);

        int labelY = y - 10;
        if (labelY < 0) labelY = y + h + 2;
        fill(poseStackOrGraphics, x, labelY - 8, x + font.width(element.elementId) + 4, labelY + 8, 0xCC000000);
        drawString(poseStackOrGraphics, font, element.elementId, x + 2, labelY - 4, 0xFFFFFF);

        if (element.selected) {
            renderResizeHandles(poseStackOrGraphics, element, handle == ResizeHandle.NONE ? hoveredHandle : handle);
        }

        String sizeText = w + "x" + h;
        int textWidth = font.width(sizeText);
        fill(poseStackOrGraphics, x + w - textWidth - 4, y + h - 12, x + w, y + h, 0xCC000000);
        drawString(poseStackOrGraphics, font, sizeText, x + w - textWidth - 2, y + h - 10, 0xCCCCCC);
    }

    private void renderResizeHandles(Object poseStackOrGraphics, VisualElement element, ResizeHandle hoveredHandle) {
        int x = element.config.x;
        int y = element.config.y;
        int w = element.config.width;
        int h = element.config.height;
        int hs = HANDLE_SIZE / 2;

        renderHandle(poseStackOrGraphics, x - hs, y - hs, hoveredHandle == ResizeHandle.TOP_LEFT);
        renderHandle(poseStackOrGraphics, x + w - hs, y - hs, hoveredHandle == ResizeHandle.TOP_RIGHT);
        renderHandle(poseStackOrGraphics, x - hs, y + h - hs, hoveredHandle == ResizeHandle.BOTTOM_LEFT);
        renderHandle(poseStackOrGraphics, x + w - hs, y + h - hs, hoveredHandle == ResizeHandle.BOTTOM_RIGHT);

        renderHandle(poseStackOrGraphics, x + w / 2 - hs, y - hs, hoveredHandle == ResizeHandle.TOP);
        renderHandle(poseStackOrGraphics, x + w / 2 - hs, y + h - hs, hoveredHandle == ResizeHandle.BOTTOM);
        renderHandle(poseStackOrGraphics, x - hs, y + h / 2 - hs, hoveredHandle == ResizeHandle.LEFT);
        renderHandle(poseStackOrGraphics, x + w - hs, y + h / 2 - hs, hoveredHandle == ResizeHandle.RIGHT);
    }

    private void renderHandle(Object poseStackOrGraphics, int x, int y, boolean hovered) {
        int outerSize = HANDLE_SIZE + 2;
        int color = hovered ? 0xFFFFFFFF : 0xFF00FFFF;

        if (hovered) {
            fill(poseStackOrGraphics, x - 1, y - 1, x + outerSize + 1, y + outerSize + 1, 0x80FFFFFF);
        }

        fill(poseStackOrGraphics, x, y, x + outerSize, y + outerSize, color);
        fill(poseStackOrGraphics, x + 2, y + 2, x + outerSize - 2, y + outerSize - 2, 0xFF000000);
    }

    private ResizeHandle getResizeHandle(VisualElement element, int mouseX, int mouseY) {
        if (!element.selected) return ResizeHandle.NONE;

        int x = element.config.x;
        int y = element.config.y;
        int w = element.config.width;
        int h = element.config.height;
        int hs = HANDLE_SIZE / 2;
        int margin = HANDLE_MARGIN;

        if (mouseX >= x - hs - margin && mouseX < x + hs + margin &&
            mouseY >= y - hs - margin && mouseY < y + hs + margin) return ResizeHandle.TOP_LEFT;
        if (mouseX >= x + w - hs - margin && mouseX < x + w + hs + margin &&
            mouseY >= y - hs - margin && mouseY < y + hs + margin) return ResizeHandle.TOP_RIGHT;
        if (mouseX >= x - hs - margin && mouseX < x + hs + margin &&
            mouseY >= y + h - hs - margin && mouseY < y + h + hs + margin) return ResizeHandle.BOTTOM_LEFT;
        if (mouseX >= x + w - hs - margin && mouseX < x + w + hs + margin &&
            mouseY >= y + h - hs - margin && mouseY < y + h + hs + margin) return ResizeHandle.BOTTOM_RIGHT;

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

    private void renderAddWidgetDialog(Object poseStackOrGraphics, int mouseX, int mouseY) {
        int dialogX = width / 2 - 120;
        int dialogY = height / 2 - 40;
        int dialogW = 240;
        int dialogH = 80;

        fill(poseStackOrGraphics, dialogX, dialogY, dialogX + dialogW, dialogY + dialogH, 0xDD000000);
        fill(poseStackOrGraphics, dialogX, dialogY, dialogX + dialogW, dialogY + 1, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX, dialogY + dialogH - 1, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX, dialogY + 1, dialogX + 1, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX + dialogW - 1, dialogY, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);

        drawCenteredString(poseStackOrGraphics, font,
            ComponentHelper.translatable("buildscape.gui.editor.add_widget_title").getString(),
            width / 2, dialogY + 5, 0xFFFFFF);

        drawCenteredString(poseStackOrGraphics, font,
            ComponentHelper.translatable("buildscape.gui.editor.enter_name").getString(),
            width / 2, dialogY + 25, 0xCCCCCC);

        newWidgetNameField.visible = true;
        setWidgetPos(newWidgetNameField, width / 2 - 100, dialogY + 35);
    }

    private void renderPropertiesDialog(Object poseStackOrGraphics, int mouseX, int mouseY) {
        if (selectedElement == null) return;

        int dialogX = width / 2 - 150;
        int dialogY = height / 2 - 80;
        int dialogW = 300;
        int dialogH = 160;

        fill(poseStackOrGraphics, dialogX, dialogY, dialogX + dialogW, dialogY + dialogH, 0xDD000000);
        fill(poseStackOrGraphics, dialogX, dialogY, dialogX + dialogW, dialogY + 1, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX, dialogY + dialogH - 1, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX, dialogY + 1, dialogX + 1, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX + dialogW - 1, dialogY, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);

        drawCenteredString(poseStackOrGraphics, font,
            "Edit Properties: " + selectedElement.elementId,
            width / 2, dialogY + 5, 0xFFFFFF);

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
            drawString(poseStackOrGraphics, font, "Search Target Widget ID:", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            searchTargetField.visible = true;
            setWidgetPos(searchTargetField, dialogX + 10, dialogY + yOffset + 15);
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
            drawString(poseStackOrGraphics, font, "Opacity (0-255):", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            opacityField.visible = true;
            setWidgetPos(opacityField, dialogX + 10, dialogY + yOffset + 15);
            opacityField.setWidth(dialogW - 20);

            if (selectedElement.config.properties != null &&
                selectedElement.config.properties.containsKey("opacity")) {
                Object opacityObj = selectedElement.config.properties.get("opacity");
                opacityField.setValue(opacityObj != null ? opacityObj.toString() : "192");
            } else {
                opacityField.setValue("192");
            }
            yOffset += 45;

            drawString(poseStackOrGraphics, font, "Background Color (RRGGBB):", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            backgroundColorField.visible = true;
            setWidgetPos(backgroundColorField, dialogX + 10, dialogY + yOffset + 15);
            backgroundColorField.setWidth(dialogW - 20);

            if (selectedElement.config.properties != null &&
                selectedElement.config.properties.containsKey("backgroundColor")) {
                String color = (String) selectedElement.config.properties.get("backgroundColor");
                backgroundColorField.setValue(color != null ? color : "101010");
            } else {
                backgroundColorField.setValue("101010");
            }
            yOffset += 45;

            drawString(poseStackOrGraphics, font, "Border Color (RRGGBB):", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            borderColorField.visible = true;
            setWidgetPos(borderColorField, dialogX + 10, dialogY + yOffset + 15);
            borderColorField.setWidth(dialogW - 20);

            if (selectedElement.config.properties != null &&
                selectedElement.config.properties.containsKey("borderColor")) {
                String color = (String) selectedElement.config.properties.get("borderColor");
                borderColorField.setValue(color != null ? color : "FFFFFF");
            } else {
                borderColorField.setValue("FFFFFF");
            }
            yOffset += 45;

            drawString(poseStackOrGraphics, font, "Border Width:", dialogX + 10, dialogY + yOffset, 0xCCCCCC);
            borderWidthField.visible = true;
            setWidgetPos(borderWidthField, dialogX + 10, dialogY + yOffset + 15);
            borderWidthField.setWidth(dialogW - 20);

            if (selectedElement.config.properties != null &&
                selectedElement.config.properties.containsKey("borderWidth")) {
                Object widthObj = selectedElement.config.properties.get("borderWidth");
                borderWidthField.setValue(widthObj != null ? widthObj.toString() : "1");
            } else {
                borderWidthField.setValue("1");
            }
            yOffset += 45;

            dialogH = Math.max(160, yOffset + 40);
        } else {
            opacityField.visible = false;
            backgroundColorField.visible = false;
            borderColorField.visible = false;
            borderWidthField.visible = false;
        }

        savePropertiesButton.visible = hasCustomProperties;
        setWidgetPos(savePropertiesButton, dialogX + dialogW / 2 - 50, dialogY + dialogH - 30);
    }
    private void saveElementProperties() {
        if (selectedElement == null) return;

        if (selectedElement.config.properties == null) {
            selectedElement.config.properties = new HashMap<>();
        }

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

        boolean isBackground = Boolean.TRUE.equals(selectedElement.config.properties.get("isBackground"));
        boolean isSearchBar = Boolean.TRUE.equals(selectedElement.config.properties.get("isSearchBar"));
        boolean isTextBox = Boolean.TRUE.equals(selectedElement.config.properties.get("isTextBox"));

        if (isBackground || isSearchBar || isTextBox) {
            if (opacityField != null && !opacityField.getValue().isEmpty()) {
                try {
                    int opacity = Integer.parseInt(opacityField.getValue());
                    opacity = Math.max(0, Math.min(255, opacity));
                    selectedElement.config.properties.put("opacity", String.valueOf(opacity));
                } catch (NumberFormatException e) {}
            }

            if (backgroundColorField != null && !backgroundColorField.getValue().isEmpty()) {
                selectedElement.config.properties.put("backgroundColor", backgroundColorField.getValue());
            }

            if (borderColorField != null && !borderColorField.getValue().isEmpty()) {
                selectedElement.config.properties.put("borderColor", borderColorField.getValue());
            }

            if (borderWidthField != null && !borderWidthField.getValue().isEmpty()) {
                try {
                    int width = Integer.parseInt(borderWidthField.getValue());
                    width = Math.max(0, Math.min(10, width));
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
        editingTextElement = element;
        showTextEditor = true;

        String currentText = "";
        if (element.config.properties != null && element.config.properties.containsKey("text")) {
            Object textObj = element.config.properties.get("text");
            if (textObj != null) {
                currentText = textObj.toString();
            }
        }

        if (currentText.isEmpty() && element.config.properties != null &&
            element.config.properties.containsKey("textKey")) {
            String textKey = (String) element.config.properties.get("textKey");
            if (textKey != null && !textKey.isEmpty()) {
                try {
                    currentText = ComponentHelper.translatable(textKey).getString();
                } catch (Exception e) {
                    currentText = textKey;
                }
            }
        }

        textContentField.setValue(currentText);
        textContentField.visible = true;
        saveTextButton.visible = true;

        setWidgetPos(textContentField, width / 2 - 150, height / 2 - 10);
        textContentField.setWidth(300);

        setWidgetPos(saveTextButton, width / 2 - 50, height / 2 + 15);

        setFocused(textContentField);
    }

    private void renderTextEditorDialog(Object poseStackOrGraphics, int mouseX, int mouseY) {
        if (editingTextElement == null) return;

        int dialogX = width / 2 - 160;
        int dialogY = height / 2 - 50;
        int dialogW = 320;
        int dialogH = 100;

        fill(poseStackOrGraphics, dialogX, dialogY, dialogX + dialogW, dialogY + dialogH, 0xDD000000);
        fill(poseStackOrGraphics, dialogX, dialogY, dialogX + dialogW, dialogY + 1, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX, dialogY + dialogH - 1, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX, dialogY, dialogX + 1, dialogY + dialogH, 0xFFFFFFFF);
        fill(poseStackOrGraphics, dialogX + dialogW - 1, dialogY, dialogX + dialogW, dialogY + dialogH, 0xFFFFFFFF);

        drawCenteredString(poseStackOrGraphics, font,
            "Edit Text: " + editingTextElement.elementId,
            width / 2, dialogY + 5, 0xFFFFFF);

        drawString(poseStackOrGraphics, font, "Text Content:", dialogX + 10, dialogY + 30, 0xCCCCCC);
        textContentField.visible = true;
        setWidgetPos(textContentField, dialogX + 10, dialogY + 45);
        textContentField.setWidth(dialogW - 20);

        drawString(poseStackOrGraphics, font,
            "Enter the text to display. Use & for color codes (e.g., &cRed, &aGreen).",
            dialogX + 10, dialogY + 70, 0xAAAAAA);

        saveTextButton.visible = true;
        setWidgetPos(saveTextButton, dialogX + dialogW / 2 - 50, dialogY + dialogH - 25);
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

        editingTextElement.config.properties.put("isTextElement", true);

        showTextEditor = false;
        textContentField.visible = false;
        saveTextButton.visible = false;
        editingTextElement = null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isCtrlPressed = hasControlDown();
        boolean isShiftPressed = hasShiftDown();
        if (showTextEditor) {
            if (button == 0) {
                if (textContentField != null && Services.PLATFORM.widgetMouseClicked(textContentField, mouseX, mouseY, button)) {
                    setFocused(textContentField);
                    return true;
                }
                if (saveTextButton != null && saveTextButton.visible &&
                    Services.PLATFORM.widgetMouseClicked(saveTextButton, mouseX, mouseY, button)) {
                    return true;
                }
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

        if (showPropertiesDialog) {
            if (button == 0) {
                if (searchTargetField != null && Services.PLATFORM.widgetMouseClicked(searchTargetField, mouseX, mouseY, button)) {
                    return true;
                }
                if (savePropertiesButton != null && savePropertiesButton.visible &&
                    Services.PLATFORM.widgetMouseClicked(savePropertiesButton, mouseX, mouseY, button)) {
                    return true;
                }
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

        if (showAddWidgetDialog) {
            if (newWidgetNameField != null && Services.PLATFORM.widgetMouseClicked(newWidgetNameField, mouseX, mouseY, button)) {
                setFocused(newWidgetNameField);
                    return true;
                }
            if (button == 0) {
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

        if (saveButton != null && Services.PLATFORM.widgetMouseClicked(saveButton, mouseX, mouseY, button)) return true;
        if (resetButton != null && Services.PLATFORM.widgetMouseClicked(resetButton, mouseX, mouseY, button)) return true;
        if (closeButton != null && Services.PLATFORM.widgetMouseClicked(closeButton, mouseX, mouseY, button)) return true;
        if (addWidgetButton != null && Services.PLATFORM.widgetMouseClicked(addWidgetButton, mouseX, mouseY, button)) return true;
        if (deleteButton != null && deleteButton.visible && Services.PLATFORM.widgetMouseClicked(deleteButton, mouseX, mouseY, button)) return true;

        int layersPanelHeight = (height - layersPanelY - 50) / 2;
        if (mouseX >= layersPanelX && mouseX < layersPanelX + LAYERS_PANEL_WIDTH &&
            mouseY >= layersPanelY && mouseY < layersPanelY + 20) {
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

        int toolboxW = 22;
        int toolboxH = 132;
        if (mouseX >= toolboxX && mouseX < toolboxX + toolboxW &&
            mouseY >= toolboxY && mouseY < toolboxY + 20) {
            if (button == 0) {
                isDraggingToolbox = true;
                toolboxDragStartX = mouseX;
                toolboxDragStartY = mouseY;
                toolboxDragStartXPos = toolboxX;
                toolboxDragStartYPos = toolboxY;
                return true;
            }
        }

        VisualElement clickedElement = null;
        ResizeHandle handle = ResizeHandle.NONE;

        for (VisualElement element : visualElements) {
            if (!isElementVisible(element)) continue;
            handle = getResizeHandle(element, (int)mouseX, (int)mouseY);
            if (handle != ResizeHandle.NONE) {
                clickedElement = element;
                break;
            }
        }

        if (button == 0 && handle != ResizeHandle.NONE && clickedElement != null) {
            for (VisualElement element : visualElements) {
                element.selected = false;
            }

            clickedElement.selected = true;
            selectedElement = clickedElement;
            deleteButton.visible = true;

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

        if (button == 1) {
            boolean clickedOnWidget = false;
            for (VisualElement element : visualElements) {
                if (!isElementVisible(element)) continue;
                if (mouseX >= element.config.x && mouseX < element.config.x + element.config.width &&
                    mouseY >= element.config.y && mouseY < element.config.y + element.config.height) {
                    clickedOnWidget = true;
                    break;
                }
            }

            if (!clickedOnWidget && mouseY < height - 50) {
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

        if (button == 0) {

            if (currentTool == EditorTool.SELECT_GROUP) {
                boolean clickedOnSidebar = false;
                for (VisualElement element : visualElements) {
                    if (!isElementVisible(element)) continue;
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
                    selectedElements.clear();
                    for (VisualElement element : visualElements) {
                        if (!isElementVisible(element)) continue;
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
                if (!isElementVisible(element)) continue;

                int x = element.config.x;
                int y = element.config.y;
                int w = getElementWidthForScale(element, currentGuiScale);
                int h = getElementHeightForScale(element, currentGuiScale);

                boolean isTextElement = element.config.properties != null &&
                    Boolean.TRUE.equals(element.config.properties.get("isTextElement"));

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

                int clickMargin = 3;
                boolean isClickable = mouseX >= x - clickMargin && mouseX < x + w + clickMargin &&
                                     mouseY >= y - clickMargin && mouseY < y + h + clickMargin;

                if (isClickable) {
                    if (isCtrlPressed) {
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
                        for (VisualElement e : visualElements) {
                        e.selected = false;
                    }
                        selectedElements.clear();

                        element.selected = true;
                    selectedElement = element;
                        selectedElements.add(element);
                    deleteButton.visible = true;

                        if (element.config.properties == null) {
                            element.config.properties = new HashMap<>();
                        }
                        element.config.properties.put("dragStartX", element.config.x);
                        element.config.properties.put("dragStartY", element.config.y);

                        long currentTime = System.currentTimeMillis();
                        if (lastClickTime > 0 && currentTime - lastClickTime < 300 &&
                            lastClickedElement == element) {
                            if (isTextElement) {
                                openTextEditor(element);
                            } else if (currentTool == EditorTool.EDIT) {
                                showPropertiesDialog = true;
                            } else if (currentTool == EditorTool.EDIT_TEXT) {
                                openTextEditor(element);
                            } else {
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

                    draggingElementId = element.elementId;
                    isDragging = true;
                    dragStartX = mouseX;
                    dragStartY = mouseY;
                    dragStartElementX = element.config.x;
                    dragStartElementY = element.config.y;

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

            if (!isCtrlPressed) {
            for (VisualElement element : visualElements) {
                element.selected = false;
            }
                selectedElements.clear();
            selectedElement = null;
            deleteButton.visible = false;
            }
        }

        if (sourceTab != null) {
            if (sourceTab.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            if (isDraggingLayersPanel) {
                double deltaX = mouseX - layersPanelDragStartX;
                double deltaY = mouseY - layersPanelDragStartY;
                layersPanelX = layersPanelDragStartPanelX + (int)deltaX;
                layersPanelY = layersPanelDragStartPanelY + (int)deltaY;
                layersPanelX = Math.max(0, Math.min(width - LAYERS_PANEL_WIDTH, layersPanelX));
                layersPanelY = Math.max(0, Math.min(height - 100, layersPanelY));
                return true;
            }

            if (isDraggingToolbox) {
                double deltaX = mouseX - toolboxDragStartX;
                double deltaY = mouseY - toolboxDragStartY;
                int newToolboxX = toolboxDragStartXPos + (int)deltaX;
                int newToolboxY = toolboxDragStartYPos + (int)deltaY;
                int toolboxW = 22;
                int toolboxH = 132;
                newToolboxX = Math.max(0, Math.min(width - toolboxW, newToolboxX));
                newToolboxY = Math.max(0, Math.min(height - toolboxH, newToolboxY));

                int deltaToolboxX = newToolboxX - toolboxX;
                int deltaToolboxY = newToolboxY - toolboxY;
                toolboxX = newToolboxX;
                toolboxY = newToolboxY;

                int toolButtonSize = 20;
                int toolSpacing = 2;
                if (moveToolButton != null) setWidgetPos(moveToolButton, toolboxX, toolboxY);
                if (editToolButton != null) setWidgetPos(editToolButton, toolboxX, toolboxY + toolButtonSize + toolSpacing);
                if (editTextToolButton != null) setWidgetPos(editTextToolButton, toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 2);
                if (createTextToolButton != null) setWidgetPos(createTextToolButton, toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 3);
                if (createWidgetToolButton != null) setWidgetPos(createWidgetToolButton, toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 4);
                if (createBackgroundButton != null) setWidgetPos(createBackgroundButton, toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 5);
                if (createSearchBarButton != null) setWidgetPos(createSearchBarButton, toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 6);
                if (createTextBoxButton != null) setWidgetPos(createTextBoxButton, toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 7);
                if (selectGroupToolButton != null) setWidgetPos(selectGroupToolButton, toolboxX, toolboxY + (toolButtonSize + toolSpacing) * 8);

                int scaleY = toolboxY + (toolButtonSize + toolSpacing) * 9 + 5;
                int scaleX = toolboxX - 180;
                if (scale1Button != null) setWidgetPos(scale1Button, scaleX, scaleY);
                if (scale2Button != null) setWidgetPos(scale2Button, scaleX + 37, scaleY);
                if (scale3Button != null) setWidgetPos(scale3Button, scaleX + 74, scaleY);
                if (scale4Button != null) setWidgetPos(scale4Button, scaleX + 111, scaleY);
                if (autoScaleButton != null) setWidgetPos(autoScaleButton, scaleX + 148, scaleY);

                return true;
            }

            if (resizingElementId != null && resizeHandle != null) {
                VisualElement element = getElementById(resizingElementId);
                if (element != null && isElementVisible(element)) {
                    resizeElement(element, mouseX, mouseY);
                    return true;
                }
            } else if (draggingElementId != null) {
                VisualElement element = getElementById(draggingElementId);
                if (element != null && isElementVisible(element)) {
                    double deltaX = mouseX - dragStartX;
                    double deltaY = mouseY - dragStartY;
                    int newX = dragStartElementX + (int)deltaX;
                    int newY = dragStartElementY + (int)deltaY;

                    element.config.x = newX;
                    element.config.y = newY;

                    if (!selectedElements.isEmpty() && selectedElements.contains(element)) {
                        for (VisualElement selected : selectedElements) {
                            if (!isElementVisible(selected)) continue;
                            if (selected != element) {
                                int startX = dragStartElementX;
                                int startY = dragStartElementY;
                                if (selected.config.properties != null) {
                                    Object storedX = selected.config.properties.get("dragStartX");
                                    Object storedY = selected.config.properties.get("dragStartY");
                                    if (storedX instanceof Number) startX = ((Number)storedX).intValue();
                                    if (storedY instanceof Number) startY = ((Number)storedY).intValue();
                                }
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
                double deltaX = mouseX - dragStartX;
                double deltaY = mouseY - dragStartY;

                for (VisualElement selected : selectedElements) {
                    if (!isElementVisible(selected)) continue;
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

        if (sourceTab != null && draggingElementId == null && resizingElementId == null && !isDragging) {
            if (sourceTab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }

        return false;
    }

    private void resizeElement(VisualElement element, double mouseX, double mouseY) {
        double deltaX = mouseX - dragStartX;
        double deltaY = mouseY - dragStartY;

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

        String widthKey = "scale" + currentGuiScale + "_width";
        String heightKey = "scale" + currentGuiScale + "_height";
        element.config.properties.put(widthKey, element.config.width);
        element.config.properties.put(heightKey, element.config.height);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isResizing && selectedElement != null) {
                saveElementSizesForCurrentScale();
            }
            draggingElementId = null;
            resizingElementId = null;
            resizeHandle = null;
            isDragging = false;
            isResizing = false;
        }

        if (sourceTab != null) {
            if (sourceTab.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showAddWidgetDialog && newWidgetNameField != null && newWidgetNameField.visible) {
            if (Services.PLATFORM.widgetKeyPressed(newWidgetNameField, keyCode, scanCode, modifiers)) {
                return true;
            }
            if (keyCode == 257) {
                createNewWidget();
                return true;
            }
        }

        if (keyCode == 256) {
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

        if (showTextEditor && keyCode == 257) {
            saveTextContent();
            return true;
        }
        if (showPropertiesDialog && keyCode == 257) {
            saveElementProperties();
            return true;
        }

        if (showTextEditor && textContentField != null && textContentField.visible) {
            if (Services.PLATFORM.widgetKeyPressed(textContentField, keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        if (showPropertiesDialog) {
            if (searchTargetField != null && searchTargetField.visible &&
                Services.PLATFORM.widgetKeyPressed(searchTargetField, keyCode, scanCode, modifiers)) {
                return true;
            }
            if (opacityField != null && opacityField.visible &&
                Services.PLATFORM.widgetKeyPressed(opacityField, keyCode, scanCode, modifiers)) {
                return true;
            }
            if (backgroundColorField != null && backgroundColorField.visible &&
                Services.PLATFORM.widgetKeyPressed(backgroundColorField, keyCode, scanCode, modifiers)) {
                return true;
            }
            if (borderColorField != null && borderColorField.visible &&
                Services.PLATFORM.widgetKeyPressed(borderColorField, keyCode, scanCode, modifiers)) {
                return true;
            }
            if (borderWidthField != null && borderWidthField.visible &&
                Services.PLATFORM.widgetKeyPressed(borderWidthField, keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        if (keyCode == 261) {
            if (!selectedElements.isEmpty()) {
                deleteSelectedElements();
                return true;
            } else if (selectedElement != null) {
            deleteSelectedElement();
            return true;
            }
        }

        if (!showAddWidgetDialog && !showTextEditor && !showPropertiesDialog) {
            if (keyCode == 77) {
                setCurrentTool(EditorTool.MOVE);
                return true;
            } else if (keyCode == 69) {
                setCurrentTool(EditorTool.EDIT);
                return true;
            } else if (keyCode == 84) {
                setCurrentTool(EditorTool.EDIT_TEXT);
                return true;
            } else if (keyCode == 78) {
                setCurrentTool(EditorTool.CREATE_TEXT);
                return true;
            } else if (keyCode == 87) {
                setCurrentTool(EditorTool.CREATE_WIDGET);
                showAddWidgetDialog = true;
                return true;
            } else if (keyCode == 66) {
                setCurrentTool(EditorTool.CREATE_BACKGROUND);
                return true;
            } else if (keyCode == 83) {
                setCurrentTool(EditorTool.CREATE_SEARCH_BAR);
                return true;
            } else if (keyCode == 88) {
                setCurrentTool(EditorTool.CREATE_TEXT_BOX);
                return true;
            } else if (keyCode == 71) {
                setCurrentTool(EditorTool.SELECT_GROUP);
                return true;
            }
        }

        if (sourceTab != null) {
            if (sourceTab.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return false;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int layersPanelHeight = (height - layersPanelY - 50) / 2;
        if (mouseX >= layersPanelX && mouseX < layersPanelX + LAYERS_PANEL_WIDTH &&
            mouseY >= layersPanelY && mouseY < layersPanelY + layersPanelHeight) {
            int maxVisible = layersPanelHeight / LAYER_ITEM_HEIGHT;
            int maxScroll = Math.max(0, visualElements.size() - maxVisible);
            layersPanelScrollOffset = (int) Math.max(0, Math.min(maxScroll, layersPanelScrollOffset - delta));
            return true;
        }

        if (sourceTab != null) {
            if (sourceTab.mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (showAddWidgetDialog && newWidgetNameField != null && newWidgetNameField.visible) {
            if (Services.PLATFORM.widgetCharTyped(newWidgetNameField, codePoint, modifiers)) {
                return true;
            }
        }

        if (showTextEditor && textContentField != null && textContentField.visible) {
            if (Services.PLATFORM.widgetCharTyped(textContentField, codePoint, modifiers)) {
                return true;
            }
        }

        if (showPropertiesDialog) {
            if (searchTargetField != null && searchTargetField.visible &&
                Services.PLATFORM.widgetCharTyped(searchTargetField, codePoint, modifiers)) {
                return true;
            }
            if (opacityField != null && opacityField.visible &&
                Services.PLATFORM.widgetCharTyped(opacityField, codePoint, modifiers)) {
                return true;
            }
            if (backgroundColorField != null && backgroundColorField.visible &&
                Services.PLATFORM.widgetCharTyped(backgroundColorField, codePoint, modifiers)) {
                return true;
            }
            if (borderColorField != null && borderColorField.visible &&
                Services.PLATFORM.widgetCharTyped(borderColorField, codePoint, modifiers)) {
                return true;
            }
            if (borderWidthField != null && borderWidthField.visible &&
                Services.PLATFORM.widgetCharTyped(borderWidthField, codePoint, modifiers)) {
                return true;
            }
        }

        if (sourceTab != null) {
            if (sourceTab.charTyped(codePoint, modifiers)) {
                return true;
            }
        }

        return false;
    }

    private void createNewWidget() {
        String name = newWidgetNameField.getValue().trim();
        if (name.isEmpty()) {
            name = "newWidget" + visualElements.size();
        }

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

        if (getElementById(name) != null) {
            int counter = 1;
            String baseName = name;
            while (getElementById(name) != null) {
                name = baseName + counter;
                counter++;
            }
        }

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
            newConfig.properties.put("opacity", "192");
            newConfig.properties.put("backgroundColor", "101010");
            newConfig.properties.put("borderColor", "FFFFFF");
            newConfig.properties.put("borderWidth", "1");
            newConfig.width = 200;
            newConfig.height = 100;
        } else if (currentTool == EditorTool.CREATE_SEARCH_BAR) {
            newConfig.properties.put("isSearchBar", true);
            newConfig.properties.put("placeholder", "Search...");
            newConfig.properties.put("opacity", "255");
            newConfig.properties.put("backgroundColor", "2C2C2C");
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

        for (VisualElement element : visualElements) {
            element.selected = false;
        }
        selectedElements.clear();
        selectedElements.add(newElement);

        visualElements.add(newElement);
        selectedElement = newElement;
        deleteButton.visible = true;

        config.setElementConfig(name, newConfig);

        if (currentTool == EditorTool.CREATE_TEXT) {
            openTextEditor(newElement);
        }
    }

    private void setCurrentTool(EditorTool tool) {
        currentTool = tool;
        updateToolButtons();
    }

    private void updateToolButtons() {
        if (moveToolButton != null) {
            moveToolButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.MOVE ? "●" : "M"));
            moveToolButton.active = currentTool != EditorTool.MOVE;
        }
        if (editToolButton != null) {
            editToolButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.EDIT ? "●" : "E"));
            editToolButton.active = currentTool != EditorTool.EDIT;
        }
        if (editTextToolButton != null) {
            editTextToolButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.EDIT_TEXT ? "●" : "T"));
            editTextToolButton.active = currentTool != EditorTool.EDIT_TEXT;
        }
        if (createTextToolButton != null) {
            createTextToolButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.CREATE_TEXT ? "●" : "N"));
            createTextToolButton.active = currentTool != EditorTool.CREATE_TEXT;
        }
        if (createWidgetToolButton != null) {
            createWidgetToolButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.CREATE_WIDGET ? "●" : "W"));
            createWidgetToolButton.active = currentTool != EditorTool.CREATE_WIDGET;
        }
        if (createBackgroundButton != null) {
            createBackgroundButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.CREATE_BACKGROUND ? "●" : "B"));
            createBackgroundButton.active = currentTool != EditorTool.CREATE_BACKGROUND;
        }
        if (createSearchBarButton != null) {
            createSearchBarButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.CREATE_SEARCH_BAR ? "●" : "S"));
            createSearchBarButton.active = currentTool != EditorTool.CREATE_SEARCH_BAR;
        }
        if (createTextBoxButton != null) {
            createTextBoxButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.CREATE_TEXT_BOX ? "●" : "X"));
            createTextBoxButton.active = currentTool != EditorTool.CREATE_TEXT_BOX;
        }
        if (selectGroupToolButton != null) {
            selectGroupToolButton.setMessage(ComponentHelper.literal(currentTool == EditorTool.SELECT_GROUP ? "●" : "G"));
            selectGroupToolButton.active = currentTool != EditorTool.SELECT_GROUP;
        }
        updateScaleButtons();
    }

    private void setGuiScale(int scale) {
        if (currentGuiScale != scale) {
            saveElementSizesForCurrentScale();
        }

        currentGuiScale = scale;

        if (getConfigScreen() != null) {
            config.screen.scale = scale;
        }

        loadElementSizesForCurrentScale();

        updateToolButtons();
        updateScaleButtons();
    }

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
        return element.config.width;
    }

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
        return element.config.height;
    }

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

    private boolean isElementVisible(VisualElement element) {
        if (element.config.properties != null && element.config.properties.containsKey("editorVisible")) {
            Object visible = element.config.properties.get("editorVisible");
            if (visible instanceof Boolean) {
                return (Boolean) visible;
            } else if (visible instanceof String) {
                return Boolean.parseBoolean((String) visible);
            }
        }
        return true;
    }

    private void toggleElementVisibility(VisualElement element) {
        if (element.config.properties == null) {
            element.config.properties = new HashMap<>();
        }
        boolean currentVisible = isElementVisible(element);
        element.config.properties.put("editorVisible", !currentVisible);

        if (!currentVisible) {
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

    private void renderLayersPanel(Object poseStackOrGraphics, int mouseX, int mouseY) {
        int panelHeight = (height - layersPanelY - 50) / 2;
        int panelX = layersPanelX;
        int panelY = layersPanelY;

        fill(poseStackOrGraphics, panelX, panelY, panelX + LAYERS_PANEL_WIDTH, panelY + panelHeight, 0xCC000000);
        fill(poseStackOrGraphics, panelX, panelY, panelX + LAYERS_PANEL_WIDTH, panelY + 1, 0xFFFFFFFF);
        fill(poseStackOrGraphics, panelX, panelY + panelHeight - 1, panelX + LAYERS_PANEL_WIDTH, panelY + panelHeight, 0xFFFFFFFF);
        fill(poseStackOrGraphics, panelX, panelY, panelX + 1, panelY + panelHeight, 0xFFFFFFFF);
        fill(poseStackOrGraphics, panelX + LAYERS_PANEL_WIDTH - 1, panelY, panelX + LAYERS_PANEL_WIDTH, panelY + panelHeight, 0xFFFFFFFF);

        String layersTitle = ComponentHelper.translatable("buildscape.gui.editor.layers").getString();
        drawString(poseStackOrGraphics, font, layersTitle, panelX + 5, panelY + 5, 0xFFFFFF);

        int maxVisible = panelHeight / LAYER_ITEM_HEIGHT;
        int startIndex = layersPanelScrollOffset;
        int endIndex = Math.min(startIndex + maxVisible, visualElements.size());

        for (int i = startIndex; i < endIndex; i++) {
            if (i >= visualElements.size()) break;
            VisualElement element = visualElements.get(i);
            int itemY = panelY + 20 + (i - startIndex) * LAYER_ITEM_HEIGHT;

            boolean isVisible = isElementVisible(element);
            boolean isSelected = element.selected;
            boolean isHovered = mouseX >= panelX && mouseX < panelX + LAYERS_PANEL_WIDTH &&
                               mouseY >= itemY && mouseY < itemY + LAYER_ITEM_HEIGHT;

            int bgColor = isHovered ? 0x40CCCCCC : (isSelected ? 0x30FFFFFF : 0x20CCCCCC);
            fill(poseStackOrGraphics, panelX + 1, itemY, panelX + LAYERS_PANEL_WIDTH - 1, itemY + LAYER_ITEM_HEIGHT, bgColor);

            int checkboxX = panelX + 5;
            int checkboxY = itemY + 4;
            fill(poseStackOrGraphics, checkboxX, checkboxY, checkboxX + LAYER_CHECKBOX_SIZE, checkboxY + LAYER_CHECKBOX_SIZE, 0x33FFFFFF);
            if (isVisible) {
                fill(poseStackOrGraphics, checkboxX + 2, checkboxY + 5, checkboxX + 5, checkboxY + 6, 0xFFFFFFFF);
                fill(poseStackOrGraphics, checkboxX + 5, checkboxY + 6, checkboxX + 10, checkboxY + 7, 0xFFFFFFFF);
                fill(poseStackOrGraphics, checkboxX + 9, checkboxY + 6, checkboxX + 10, checkboxY + 9, 0xFFFFFFFF);
            }

            String elementName = element.elementId;
            if (elementName.startsWith("sidebar.")) {
                elementName = elementName.substring(8);
            }
            int nameX = checkboxX + LAYER_CHECKBOX_SIZE + 5;
            int maxNameWidth = LAYERS_PANEL_WIDTH - (nameX - panelX) - 5;
            int nameWidth = font.width(elementName);
            if (nameWidth > maxNameWidth) {
                elementName = font.plainSubstrByWidth(elementName, maxNameWidth - font.width("...")) + "...";
            }

            int textColor = isVisible ? (isSelected ? 0xFFFFFF : 0xCCCCCC) : 0x888888;
            drawString(poseStackOrGraphics, font, elementName, nameX, itemY + 5, textColor);
        }

        if (visualElements.size() > maxVisible) {
            int scrollbarX = panelX + LAYERS_PANEL_WIDTH - 8;
            int scrollbarHeight = panelHeight - 20;
            int scrollbarY = panelY + 20;

            double maxScroll = Math.max(0, visualElements.size() - maxVisible);
            double scrollRatio = layersPanelScrollOffset / maxScroll;
            int thumbHeight = (int) (scrollbarHeight * (maxVisible / (double) visualElements.size()));
            thumbHeight = Math.max(20, thumbHeight);
            int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));

            fill(poseStackOrGraphics, scrollbarX, scrollbarY, scrollbarX + 5, scrollbarY + scrollbarHeight, 0x33CCCCCC);
            fill(poseStackOrGraphics, scrollbarX, thumbY, scrollbarX + 5, thumbY + thumbHeight, 0x40CCCCCC);
        }
    }

    private boolean handleLayersPanelClick(double mouseX, double mouseY) {
        int panelHeight = (height - layersPanelY - 50) / 2;
        int panelX = layersPanelX;
        int panelY = layersPanelY;

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

                if (mouseX >= checkboxX && mouseX < checkboxX + LAYER_CHECKBOX_SIZE &&
                    mouseY >= checkboxY && mouseY < checkboxY + LAYER_CHECKBOX_SIZE) {
                    toggleElementVisibility(element);
                    return true;
                }

                for (VisualElement e : visualElements) {
                    e.selected = false;
                }
                selectedElements.clear();

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
        Minecraft mc = Minecraft.getInstance();
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();
        double userGuiScale = mc.getWindow().getGuiScale();

        int effectiveWidth = (int)(windowWidth / userGuiScale);
        int effectiveHeight = (int)(windowHeight / userGuiScale);

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
        int contentX = 0;
        int contentY = 0;
        BuildScapeConfigScreen cfgScreen = getConfigScreen();
        if (cfgScreen != null) {
            contentX = cfgScreen.getContentX();
            contentY = cfgScreen.getContentY();
        }

        int screenWidth = this.width;
        int screenHeight = this.height;

        if (config.screen == null) {
            config.screen = new GuiConfigData.ScreenConfig();
        }
        config.screen.width = screenWidth;
        config.screen.height = screenHeight;
        config.screen.scale = (float)Minecraft.getInstance().getWindow().getGuiScale();

        Map<String, GuiConfigData.ElementConfig> sidebarElements = new HashMap<>();
        Map<String, GuiConfigData.ElementConfig> tabElements = new HashMap<>();

        for (VisualElement element : visualElements) {
            boolean isSidebarElement = element.config.properties != null &&
                Boolean.TRUE.equals(element.config.properties.get("isSidebarElement"));

            int absoluteScreenX = element.config.x;
            int absoluteScreenY = element.config.y;

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

            if (screenWidth > 0 && screenHeight > 0) {
                savedConfig.percentX = (double)absoluteScreenX / screenWidth;
                savedConfig.percentY = (double)absoluteScreenY / screenHeight;
                savedConfig.percentWidth = (double)element.config.width / screenWidth;
                savedConfig.percentHeight = (double)element.config.height / screenHeight;
            }

            if (isSidebarElement) {
                String elementId = element.elementId.startsWith("sidebar.") ?
                    element.elementId.substring(8) : element.elementId;
                sidebarElements.put(elementId, savedConfig);
            } else {
                tabElements.put(element.elementId, savedConfig);
            }
        }

        for (Map.Entry<String, GuiConfigData.ElementConfig> entry : tabElements.entrySet()) {
            config.setElementConfig(entry.getKey(), entry.getValue());
        }
        configManager.saveConfig(tabName, config);
        configManager.clearCache(tabName);

        if (!sidebarElements.isEmpty()) {
            String sidebarConfigName = "Sidebar";
            GuiConfigData sidebarConfig = configManager.loadConfig(sidebarConfigName);
            for (Map.Entry<String, GuiConfigData.ElementConfig> entry : sidebarElements.entrySet()) {
                sidebarConfig.setElementConfig(entry.getKey(), entry.getValue());
            }
            configManager.saveConfig(sidebarConfigName, sidebarConfig);
            configManager.clearCache(sidebarConfigName);
        }

        if (minecraft.player != null) {
            Services.PLATFORM.sendSystemMessage(minecraft.player,
                ComponentHelper.translatable("buildscape.gui.editor.saved", tabName)
            );
        }

        BuildScapeConfigScreen parentCfg = getConfigScreen();
        if (parentCfg != null) {
            if (parentCfg.getActiveTab() != null &&
                parentCfg.getActiveTab().getTabName().equals(tabName)) {
                parentCfg.getActiveTab().init();
            }
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

            int contentX = 0;
            int contentY = 0;
            BuildScapeConfigScreen cfgScreen = getConfigScreen();
            if (cfgScreen != null) {
                contentX = cfgScreen.getContentX();
                contentY = cfgScreen.getContentY();
            }

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
                        int wx = WidgetLayoutHelper.getX(widget);
                        int wy = WidgetLayoutHelper.getY(widget);
                        if (!widget.visible || wx < 0 || wy < 0) continue;
                        elementConfig = new GuiConfigData.ElementConfig(
                                wx - contentX, wy - contentY, widget.getWidth(), 20
                        );
                        elementConfig.height = WidgetLayoutHelper.getHeight(widget);
                    }

                    if (elementConfig != null && !discoveredWidgets.containsKey(elementId)) {
                        discoveredWidgets.put(elementId, elementConfig);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            scanTextLabels(discoveredWidgets, contentX, contentY);

            if (!discoveredWidgets.isEmpty()) {
                GuiConfigHelper.registerWidgetDefaults(tabName, discoveredWidgets);
                configManager.clearCache(tabName);
                GuiConfigData newConfig = configManager.loadConfig(tabName);

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
                    Services.PLATFORM.sendSystemMessage(minecraft.player,
                            ComponentHelper.translatable("buildscape.gui.editor.scanned", discoveredWidgets.size()));
                }
            }
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to scan tab: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void scanTextLabels(Map<String, GuiConfigData.ElementConfig> discoveredWidgets, int contentX, int contentY) {
        if (sourceTab == null) return;

        try {
            if (sourceTab instanceof PillarItemsConfigTab) {
                addTextLabel(discoveredWidgets, "pillarItemsLabel",
                    ComponentHelper.translatable("buildscape.config.pillar_items").getString(),
                    0, 10, "buildscape.config.pillar_items");

                addTextLabel(discoveredWidgets, "allItemsLabel",
                    "All items", 0, (parentScreen.height - contentY) / 2, null, true);

                addTextLabel(discoveredWidgets, "presetsLabel",
                    "Presets", (parentScreen.width - contentX) / 2 + 15, 10, null);
            } else if (sourceTab instanceof PillarParticlesConfigTab) {
                String titleText = ComponentHelper.translatable("buildscape.config.particles.title").getString();
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

                addTextLabel(discoveredWidgets, "defaultPropertiesLabel",
                    ComponentHelper.translatable("buildscape.config.particles.default_properties").getString(),
                    20, 50, "buildscape.config.particles.default_properties");

                addTextLabel(discoveredWidgets, "customPropertiesLabel",
                    ComponentHelper.translatable("buildscape.config.particles.custom_properties").getString(),
                    (parentScreen.width - contentX) / 2 + 15, 50, "buildscape.config.particles.custom_properties");

                addTextLabel(discoveredWidgets, "particleSpeedLabel",
                    ComponentHelper.translatable("buildscape.config.particles.particle_speed").getString() + " ",
                    20, 90, "buildscape.config.particles.particle_speed");
                addTextLabel(discoveredWidgets, "particleSpreadLabel",
                    ComponentHelper.translatable("buildscape.config.particles.particle_spread").getString() + " ",
                    20, 116, "buildscape.config.particles.particle_spread");
                addTextLabel(discoveredWidgets, "particleLifetimeLabel",
                    ComponentHelper.translatable("buildscape.config.particles.particle_lifetime").getString() + " ",
                    20, 142, "buildscape.config.particles.particle_lifetime");
                addTextLabel(discoveredWidgets, "particleDensityLabel",
                    ComponentHelper.translatable("buildscape.config.particles.particle_density").getString() + " ",
                    20, 168, "buildscape.config.particles.particle_density");

                addTextLabel(discoveredWidgets, "patternSpeedLabel",
                    ComponentHelper.translatable("buildscape.config.particles.pattern_speed").getString() + " ",
                    (parentScreen.width - contentX) / 2 + 15, 90, "buildscape.config.particles.pattern_speed");
                addTextLabel(discoveredWidgets, "patternSpreadLabel",
                    ComponentHelper.translatable("buildscape.config.particles.pattern_spread").getString() + " ",
                    (parentScreen.width - contentX) / 2 + 15, 116, "buildscape.config.particles.pattern_spread");
                addTextLabel(discoveredWidgets, "patternIntensityLabel",
                    ComponentHelper.translatable("buildscape.config.particles.pattern_intensity").getString() + " ",
                    (parentScreen.width - contentX) / 2 + 15, 142, "buildscape.config.particles.pattern_intensity");
            } else if (sourceTab instanceof PillarIdsConfigTab) {
                int tableX = 12;
                int tableY = 32;
                int[] columns = new int[] {
                    (int)((parentScreen.width - contentX - 24) * 0.22f),
                    (int)((parentScreen.width - contentX - 24) * 0.36f),
                    (int)((parentScreen.width - contentX - 24) * 0.16f)
                };
                int headerY = tableY + 28;

                int x = tableX;
                addTextLabel(discoveredWidgets, "pillarIdHeader",
                    ComponentHelper.translatable("buildscape.config.ids.id").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.id");
                x += columns[0] + 8;

                addTextLabel(discoveredWidgets, "colorsHeader",
                    ComponentHelper.translatable("buildscape.config.ids.colors").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.colors");
                x += columns[1] + 8;

                addTextLabel(discoveredWidgets, "dimensionHeader",
                    ComponentHelper.translatable("buildscape.config.ids.dimension").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.dimension");
                x += columns[2] + 8;

                addTextLabel(discoveredWidgets, "coordsHeader",
                    ComponentHelper.translatable("buildscape.config.ids.coords").getString(),
                    x + 6, headerY + 8, "buildscape.config.ids.coords");
            }
            if (getConfigScreen() != null) {
                int parentWidth = parentScreen != null ? parentScreen.width : this.width;
                addTextLabel(discoveredWidgets, "configScreenTitle",
                    "BuildScape Config", parentWidth / 2 - font.width("BuildScape Config") / 2, 10, null);
            }
        } catch (Exception e) {
            BuildScape.getLogger().warn("Failed to scan text labels: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void addTextLabel(Map<String, GuiConfigData.ElementConfig> discoveredWidgets,
                             String elementId, String text, int x, int y, String textKey) {
        addTextLabel(discoveredWidgets, elementId, text, x, y, textKey, false);
    }

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
        Services.PLATFORM.openScreen(parentScreen);
    }
}
