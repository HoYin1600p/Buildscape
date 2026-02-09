package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.ColorPickerWidget;
import com.kingodogo.buildscape.client.screen.widget.ColorSwatchButton;
import com.kingodogo.buildscape.client.screen.widget.IntSliderWidget;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;

public class PillarParticlesConfigTab extends AbstractConfigTab {
    private static final String[] PATTERNS = {"default", "beam", "spiral", "fountain", "pulse", "ring", "burst"};
    
    private Button usePatternToggle;
    private Button patternSelector;
    private EditBox particleSpeedField;
    private EditBox particleSpreadField;
    private EditBox particleLifetimeField;
    private EditBox particleDensityField;
    private EditBox patternSpeedField;
    private EditBox patternSpreadField;
    private EditBox patternIntensityField;
    private IntSliderWidget maxParticleColorSlider;
    private ColorPickerWidget sharedColorPicker; // Single color picker widget (only one visible at a time)
    private List<ColorSwatchButton> colorSwatchButtons; // 7 color swatch buttons
    private List<EditBox> colorHexFields; // Hex code edit boxes next to color swatches
    private int currentPatternIndex = 0;
    private int currentMaxColor = 7; // Always 7 swatches
    private int selectedColorIndex = -1; // Which color swatch is currently selected (-1 = none)
    private ColorPickerWidget activeDraggingPicker = null; // Track which picker is being dragged
    private boolean isDraggingSlider = false; // Track if slider is being dragged
    
    public PillarParticlesConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }
    
    @Override
    public void init() {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();
        
        PillarParticleConfig config = PillarParticleConfig.get();
        
        // Load current values
        currentPatternIndex = findPatternIndex(config.pattern);
        currentMaxColor = Math.max(1, Math.min(7, config.max_particle_color));
        
        // Widgets are created once; layout applied via relayout()
        String initialButtonText = "Use Pattern " + (config.use_pattern ? "True" : "False");
        usePatternToggle = new com.kingodogo.buildscape.client.screen.widget.WideButton(
            0, 0,
            100, 20,
            new TextComponent(initialButtonText),
            (btn) -> toggleUsePattern()
        );
        addTabWidget(usePatternToggle);
        
        int fieldHeight = 20;
        int fieldSpacing = 25;
        
        particleSpeedField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, fieldHeight,
            new TranslatableComponent("buildscape.config.particles.particle_speed")
        );
        particleSpeedField.setValue(String.valueOf(config.particle_speed));
        particleSpeedField.setEditable(!config.use_pattern);
        particleSpeedField.setBordered(true);
        particleSpeedField.setTextColor(0xFFFFFF);
        particleSpeedField.setTextColorUneditable(0xAAAAAA);
        particleSpeedField.setMaxLength(64);
        addTabWidget(particleSpeedField);
        
        particleSpreadField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, fieldHeight,
            new TranslatableComponent("buildscape.config.particles.particle_spread")
        );
        particleSpreadField.setValue(String.valueOf(config.particle_spread));
        particleSpreadField.setEditable(!config.use_pattern);
        particleSpreadField.setBordered(true);
        particleSpreadField.setTextColor(0xFFFFFF);
        particleSpreadField.setTextColorUneditable(0xAAAAAA);
        particleSpreadField.setMaxLength(64);
        addTabWidget(particleSpreadField);
        
        particleLifetimeField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, fieldHeight,
            new TranslatableComponent("buildscape.config.particles.particle_lifetime")
        );
        particleLifetimeField.setValue(String.valueOf(config.particle_lifetime));
        particleLifetimeField.setEditable(!config.use_pattern);
        particleLifetimeField.setBordered(true);
        particleLifetimeField.setTextColor(0xFFFFFF);
        particleLifetimeField.setTextColorUneditable(0xAAAAAA);
        particleLifetimeField.setMaxLength(64);
        addTabWidget(particleLifetimeField);
        
        particleDensityField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, fieldHeight,
            new TranslatableComponent("buildscape.config.particles.particle_density")
        );
        particleDensityField.setValue(String.valueOf(config.particle_density));
        particleDensityField.setEditable(!config.use_pattern);
        particleDensityField.setBordered(true);
        particleDensityField.setTextColor(0xFFFFFF);
        particleDensityField.setTextColorUneditable(0xAAAAAA);
        particleDensityField.setMaxLength(64);
        addTabWidget(particleDensityField);
        
        // Color swatches and single shared color picker
        colorSwatchButtons = new ArrayList<>();
        colorHexFields = new ArrayList<>();
        sharedColorPicker = null; // Will be created in relayout
        
        patternSelector = new com.kingodogo.buildscape.client.screen.widget.WideButton(
            0, 0,
            100, 20,
            new TranslatableComponent("buildscape.config.particles.pattern." + config.pattern),
            (btn) -> cyclePattern()
        );
        patternSelector.active = config.use_pattern;
        addTabWidget(patternSelector);
        
        patternSpeedField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, fieldHeight,
            new TranslatableComponent("buildscape.config.particles.pattern_speed")
        );
        patternSpeedField.setValue(String.valueOf(config.pattern_speed));
        patternSpeedField.setEditable(config.use_pattern);
        patternSpeedField.setBordered(true);
        patternSpeedField.setTextColor(0xFFFFFF);
        patternSpeedField.setTextColorUneditable(0xAAAAAA);
        patternSpeedField.setMaxLength(64);
        addTabWidget(patternSpeedField);
        
        patternSpreadField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, fieldHeight,
            new TranslatableComponent("buildscape.config.particles.pattern_spread")
        );
        patternSpreadField.setValue(String.valueOf(config.pattern_spread));
        patternSpreadField.setEditable(config.use_pattern);
        patternSpreadField.setBordered(true);
        patternSpreadField.setTextColor(0xFFFFFF);
        patternSpreadField.setTextColorUneditable(0xAAAAAA);
        patternSpreadField.setMaxLength(64);
        addTabWidget(patternSpreadField);
        
        patternIntensityField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, fieldHeight,
            new TranslatableComponent("buildscape.config.particles.pattern_intensity")
        );
        patternIntensityField.setValue(String.valueOf(config.pattern_intensity));
        patternIntensityField.setEditable(config.use_pattern);
        patternIntensityField.setBordered(true);
        patternIntensityField.setTextColor(0xFFFFFF);
        patternIntensityField.setTextColorUneditable(0xAAAAAA);
        patternIntensityField.setMaxLength(64);
        addTabWidget(patternIntensityField);
        
        maxParticleColorSlider = new IntSliderWidget(
            0, 0,
            120, 20,
            new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor),
            1, 7, currentMaxColor,
            (value) -> onMaxParticleColorChanged(value)
        );
        maxParticleColorSlider.active = config.use_pattern; // Disable if use_pattern is false
        addTabWidget(maxParticleColorSlider);
        
        // Initial layout - this will create color swatches and shared picker
        relayout(contentX, contentY, contentWidth, contentHeight);
        
        // Update swatches enabled state based on max value
        updateSwatchesEnabledState();
        
        // Update last dimensions to prevent immediate relayout
        lastContentX = contentX;
        lastContentY = contentY;
        lastContentWidth = contentWidth;
        lastContentHeight = contentHeight;
    }
    
    private void createColorSwatchesAndPicker(PillarParticleConfig config) {
        int padding = 10;
        int swatchSize = 20;
        int swatchSpacing = 5;
        int hexFieldWidth = 80;
        int hexFieldHeight = 20;
        int rowSpacing = 25;
        
        // Clear existing widgets
        if (colorSwatchButtons != null) {
            colorSwatchButtons.clear();
        }
        if (colorHexFields != null) {
            colorHexFields.clear();
        }
        
        // Reinitialize lists if null
        if (colorSwatchButtons == null) {
            colorSwatchButtons = new ArrayList<>();
        }
        if (colorHexFields == null) {
            colorHexFields = new ArrayList<>();
        }
        
        // Ensure config has 7 colors
        while (config.particle_color.size() < 7) {
            config.particle_color.add("#FFFFFF");
        }
        
        // Create 7 color swatches with hex fields in right top panel
        int startY = colorBoxY + padding + 25; // Below title
        int swatchX = colorBoxX + padding;
        int hexFieldX = swatchX + swatchSize + swatchSpacing;
        
        for (int i = 0; i < 7; i++) {
            final int colorIndex = i;
            String hexValue = config.particle_color.get(i);
            int color = 0xFFFFFF;
                try {
                if (hexValue.startsWith("#") && hexValue.length() == 7) {
                    color = Integer.parseInt(hexValue.substring(1), 16);
                    }
                } catch (NumberFormatException e) {
                    // Use default white
            }
            
            int swatchY = startY + i * rowSpacing;
            
            // Create color swatch button
            ColorSwatchButton swatchButton = new ColorSwatchButton(
                swatchX, swatchY,
                swatchSize, swatchSize,
                color,
                (btn) -> onColorSwatchClicked(colorIndex)
            );
            colorSwatchButtons.add(swatchButton);
            addTabWidget(swatchButton);
            
            // Create hex field next to swatch (side by side, same Y position)
            // Align hex field vertically with swatch (center it if heights differ)
            int hexFieldY = swatchY; // Same Y position for side-by-side alignment
            if (hexFieldHeight != swatchSize) {
                // Center vertically if heights differ
                hexFieldY = swatchY + (swatchSize - hexFieldHeight) / 2;
            }
            
            EditBox hexField = new EditBox(
                Minecraft.getInstance().font,
                hexFieldX, hexFieldY,
                hexFieldWidth, hexFieldHeight,
                net.minecraft.network.chat.TextComponent.EMPTY
            );
            hexField.setValue(hexValue);
            hexField.setBordered(true);
            hexField.setTextColor(0xFFFFFF);
            hexField.setMaxLength(7); // #RRGGBB
            hexField.setResponder((text) -> {
                // Update color when hex is edited
                try {
                    // Handle hex with or without # prefix
                    String hexText = text;
                    if (!hexText.startsWith("#")) {
                        hexText = "#" + hexText;
                    }
                    
                    // Only process if we have a valid hex color (6 hex digits after #)
                    if (hexText.length() == 7 && hexText.matches("#[0-9A-Fa-f]{6}")) {
                        int newColor = Integer.parseInt(hexText.substring(1), 16);
                        onColorChanged(colorIndex, hexText);
                        // Update swatch button color visually
                        updateSwatchButtonColor(colorIndex, newColor);
                        // Update shared picker if this color is selected
                        if (selectedColorIndex == colorIndex && sharedColorPicker != null) {
                            sharedColorPicker.setColor(newColor);
                        }
                        // Update hex field value to ensure it has # prefix
                        if (!text.equals(hexText)) {
                            hexField.setValue(hexText);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Invalid hex, ignore
                }
            });
            colorHexFields.add(hexField);
            addTabWidget(hexField);
        }
        
        // Create shared color picker (initially hidden, shown when swatch is clicked)
        // Size will be recalculated during render, but set initial size for RGB/HSB sliders
        int pickerX = colorBoxX + padding + swatchSize + hexFieldWidth + swatchSpacing * 2;
        int pickerY = colorBoxY + padding + 25;
        int pickerWidth = 260; // Width needed for gradient + hue + RGB/HSB sliders
        int pickerHeight = 220; // Height needed for gradient + preview + RGB/HSB sliders
        
        sharedColorPicker = new ColorPickerWidget(
            pickerX, pickerY,
            pickerWidth, pickerHeight,
            0xFFFFFF,
            (hexColor) -> {
                if (selectedColorIndex >= 0 && selectedColorIndex < 7) {
                    onColorChanged(selectedColorIndex, hexColor);
                    // Update hex field
                    if (selectedColorIndex < colorHexFields.size()) {
                        colorHexFields.get(selectedColorIndex).setValue(hexColor);
                    }
                    // Update swatch button color
                    try {
                        if (hexColor.startsWith("#") && hexColor.length() == 7) {
                            int newColor = Integer.parseInt(hexColor.substring(1), 16);
                            updateSwatchButtonColor(selectedColorIndex, newColor);
                        }
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }
        );
        sharedColorPicker.setEnabled(config.use_pattern);
        sharedColorPicker.visible = false; // Initially hidden
        addTabWidget(sharedColorPicker);
    }
    
    private void onColorSwatchClicked(int colorIndex) {
        // Only allow clicking if swatch is enabled (within max range and use_pattern is true)
        if (colorIndex >= currentMaxColor) {
            return; // Swatch is locked, don't allow clicking
        }
        
        PillarParticleConfig config = PillarParticleConfig.get();
        if (!config.use_pattern) {
            return; // Pattern mode not enabled
        }
        
        selectedColorIndex = colorIndex;
        
        // Get current color for this index
        String hexValue = config.particle_color.get(colorIndex);
        int color = 0xFFFFFF;
        try {
            if (hexValue.startsWith("#") && hexValue.length() == 7) {
                color = Integer.parseInt(hexValue.substring(1), 16);
            }
        } catch (NumberFormatException e) {
            // Use default white
        }
        
        // Update shared color picker with this color and show it
        if (sharedColorPicker != null) {
            sharedColorPicker.setColor(color);
            sharedColorPicker.visible = true;
            sharedColorPicker.setEnabled(config.use_pattern);
        }
    }
    
    private void updateSwatchButtonColor(int index, int color) {
        // Update swatch button color
        if (colorSwatchButtons != null && index >= 0 && index < colorSwatchButtons.size()) {
            colorSwatchButtons.get(index).setColor(color);
        }
    }
    
    // Base positions for color swatches (without scroll offset)
    private int colorBaseStartY = 0;
    
    private void updateColorSwatchesPositions() {
        // Use panel-relative positioning to ensure components stay within bounds
        int padding = 10;
        int titleHeight = 20;
        int swatchSize = 20;
        int swatchSpacing = 4;
        int hexFieldWidth = 80;
        int hexFieldHeight = 20;
        int rowSpacing = 4; // Reduced spacing between rows (was 25)
        
        // Calculate positions relative to colorBox panel bounds
        // Layout: 2 columns of swatches (2 per row)
        colorBaseStartY = colorBoxY + padding; // Start from top with padding (no title anymore)
        
        // Calculate column widths - split available width in half (minus spacing)
        int availableWidth = colorBoxWidth - padding * 2;
        int columnSpacing = 10; // Space between the two columns
        int columnWidth = (availableWidth - columnSpacing) / 2;
        
        // Left column
        int leftSwatchX = colorBoxX + padding;
        int leftHexFieldX = leftSwatchX + swatchSize + swatchSpacing;
        
        // Right column
        int rightSwatchX = colorBoxX + padding + columnWidth + columnSpacing;
        int rightHexFieldX = rightSwatchX + swatchSize + swatchSpacing;
        
        // Calculate total content height needed (4 rows: 2 swatches per row, last row has 1 swatch)
        int numSwatches = 7;
        int numRows = (numSwatches + 1) / 2; // 4 rows (3 full rows + 1 with 1 swatch)
        int totalContentHeight = (numRows * swatchSize) + ((numRows - 1) * rowSpacing);
        int availableHeight = colorBoxHeight - padding * 2;
        
        // Calculate scrollbar width if scrolling is needed
        boolean needsColorScrollbar = totalContentHeight > availableHeight;
        int scrollbarWidth = needsColorScrollbar ? 10 : 0;
        int scrollbarOffset = 3; // Gap between scrollbar and panel edge (2-3 pixels)
        int gapBetweenComponentAndScrollbar = 10; // LARGE gap between components and scrollbar to ensure NO overlap
        
        // Clamp scroll offset to valid range
        double maxScroll = Math.max(0, totalContentHeight - availableHeight);
        colorSwatchesScrollOffset = Math.max(0, Math.min(maxScroll, colorSwatchesScrollOffset));
        
        int scrollOffsetInt = (int)colorSwatchesScrollOffset;
        
        // Calculate max X position for components (account for scrollbar if needed)
        // Components must end before scrollbar starts
        // Scrollbar starts at: colorBoxX + colorBoxWidth - scrollbarWidth - scrollbarOffset
        // Components should end at: scrollbar start - gapBetweenComponentAndScrollbar
        int maxComponentX = needsColorScrollbar ?
            (colorBoxX + colorBoxWidth - scrollbarWidth - scrollbarOffset - gapBetweenComponentAndScrollbar) :
            (colorBoxX + colorBoxWidth - padding);
        
        // Reposition swatches and hex fields with scroll offset applied
        // Layout: 2 columns, 2 swatches per row (except last row which has 1)
        if (colorSwatchButtons != null && colorHexFields != null) {
            for (int i = 0; i < Math.min(colorSwatchButtons.size(), colorHexFields.size()); i++) {
                // Determine which column (0 = left, 1 = right)
                int column = i % 2;
                int row = i / 2;
                
                // Calculate Y position based on row
                int baseSwatchY = colorBaseStartY + row * (swatchSize + rowSpacing);
                int swatchY = baseSwatchY - scrollOffsetInt;
                
                // Set X position based on column
                int swatchX = (column == 0) ? leftSwatchX : rightSwatchX;
                int hexFieldX = (column == 0) ? leftHexFieldX : rightHexFieldX;
                
                // Ensure swatch doesn't go beyond maxComponentX
                if (swatchX + swatchSize > maxComponentX) {
                    swatchX = Math.max(leftSwatchX, maxComponentX - swatchSize);
                }
                
                colorSwatchButtons.get(i).x = swatchX;
                colorSwatchButtons.get(i).y = swatchY;
                
                // Align hex field vertically with swatch (center it if heights differ)
                int hexFieldY = swatchY; // Same Y position for side-by-side alignment
                if (hexFieldHeight != swatchSize) {
                    // Center vertically if heights differ
                    hexFieldY = swatchY + (swatchSize - hexFieldHeight) / 2;
                }
                colorHexFields.get(i).x = hexFieldX;
                colorHexFields.get(i).y = hexFieldY;
                
                // Ensure hex field width fits within column (account for scrollbar if needed)
                int maxHexWidth = (column == 0) ? 
                    (leftSwatchX + columnWidth - hexFieldX - (needsColorScrollbar ? scrollbarWidth + scrollbarOffset : 0)) :
                    (maxComponentX - hexFieldX);
                
                // Ensure hex field doesn't exceed maxComponentX
                if (hexFieldX + hexFieldWidth > maxComponentX) {
                    int adjustedWidth = maxComponentX - hexFieldX;
                    colorHexFields.get(i).setWidth(Math.max(60, adjustedWidth));
                } else if (maxHexWidth < hexFieldWidth) {
                    colorHexFields.get(i).setWidth(Math.max(60, maxHexWidth));
                } else {
                    colorHexFields.get(i).setWidth(hexFieldWidth);
                }
            }
        }
        
        // Color picker is now positioned in the bottom right panel, not here
    }
    
    // Box positions and sizes (stored for consistent rendering)
    // Middle panel (44% width): Top 50% (Default Properties), Bottom 50% (Pattern Properties)
    private int defaultBoxX, defaultBoxY, defaultBoxWidth, defaultBoxHeight;
    private int patternBoxX, patternBoxY, patternBoxWidth, patternBoxHeight;
    // Right panel (44% width): Top 50% (Color Swatches), Bottom 50% (Color Selector and Max Particles)
    private int colorBoxX, colorBoxY, colorBoxWidth, colorBoxHeight;
    private int rightBottomBoxX, rightBottomBoxY, rightBottomBoxWidth, rightBottomBoxHeight;
    
    // Track last layout dimensions to avoid unnecessary relayouts
    private int lastContentX = -1, lastContentY = -1, lastContentWidth = -1, lastContentHeight = -1;
    private int lastScreenWidth = -1;
    
    // Scrolling for panels
    private double defaultPropertiesScrollOffset = 0;
    private double colorSwatchesScrollOffset = 0;
    private boolean isDraggingColorSwatchesScrollbar = false;
    private int colorSwatchesScrollbarDragStartY = 0;
    private double colorSwatchesScrollbarDragStartOffset = 0;
    private double patternPropertiesScrollOffset = 0;
    private boolean isDraggingDefaultScrollbar = false;
    private final boolean isDraggingColorScrollbar = false;
    private boolean isDraggingPatternScrollbar = false;
    private double scrollbarDragStartY = 0;
    private double scrollbarDragStartOffset = 0;
    
    // Base positions for default properties (without scroll offset)
    private int defaultBaseButtonY = 0;
    private int defaultBaseFirstFieldY = 0;
    
    // Base positions for pattern properties (without scroll offset)
    private int patternBaseButtonY = 0;
    private int patternBaseFirstFieldY = 0;
    
    // Update widget positions with scroll offset applied
    private void updateDefaultPropertiesPositions() {
        int padding = 10;
        int titleHeight = 20;
        int buttonHeight = 20;
        int fieldHeight = 20;
        int fieldSpacing = 4; // Reduced spacing between fields
        
        defaultBaseButtonY = defaultBoxY + padding + titleHeight;
        int buttonToFieldSpacing = 15; // Reduced spacing between button and first field
        defaultBaseFirstFieldY = defaultBaseButtonY + buttonHeight + buttonToFieldSpacing;
        
        // Calculate max scroll - ensure nothing scrolls above the header
        int headerBottom = defaultBoxY + titleHeight + 5;
        int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + (4 * fieldHeight) + (3 * fieldSpacing);
        int availableHeight = defaultBoxHeight - padding * 2;
        double maxScroll = Math.max(0, totalContentHeight - availableHeight);
        
        // Clamp scroll offset to prevent scrolling above header
        // The button should never go above headerBottom
        int minScrollForHeader = Math.max(0, defaultBaseButtonY - headerBottom);
        defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, defaultPropertiesScrollOffset));
        
        int scrollOffsetInt = (int)defaultPropertiesScrollOffset;
        
        // Apply scroll offset to positions - reduce spacing between fields
        usePatternToggle.y = defaultBaseButtonY - scrollOffsetInt;
        particleSpeedField.y = defaultBaseFirstFieldY - scrollOffsetInt;
        particleSpreadField.y = defaultBaseFirstFieldY + fieldHeight + fieldSpacing - scrollOffsetInt;
        particleLifetimeField.y = defaultBaseFirstFieldY + (fieldHeight + fieldSpacing) * 2 - scrollOffsetInt;
        particleDensityField.y = defaultBaseFirstFieldY + (fieldHeight + fieldSpacing) * 3 - scrollOffsetInt;
    }
    
    // Update pattern properties widget positions with scroll offset applied
    private void updatePatternPropertiesPositions() {
        int padding = 10;
        int titleHeight = 20;
        int buttonHeight = 20;
        int fieldHeight = 20;
        int fieldSpacing = 4; // Reduced spacing between fields (matching user's changes)
        int buttonToFieldSpacing = 5; // Reduced spacing between button and first field (matching user's changes)
        
        patternBaseButtonY = patternBoxY + padding + titleHeight;
        patternBaseFirstFieldY = patternBaseButtonY + buttonHeight + buttonToFieldSpacing;
        
        // Calculate max scroll - ensure nothing scrolls above the header
        int headerBottom = patternBoxY + titleHeight + 5;
        int sliderHeight = 20;
        int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + (3 * fieldHeight) + (2 * fieldSpacing) + sliderHeight + fieldSpacing; // 3 fields + slider
        int availableHeight = patternBoxHeight - padding * 2;
        double maxScroll = Math.max(0, totalContentHeight - availableHeight);
        
        // Clamp scroll offset to prevent scrolling above header
        // The button should never go above headerBottom - same logic as Default Properties
        int minScrollForHeader = Math.max(0, patternBaseButtonY - headerBottom);
        patternPropertiesScrollOffset = Math.max(minScrollForHeader, Math.min(maxScroll, patternPropertiesScrollOffset));
        
        int scrollOffsetInt = (int)patternPropertiesScrollOffset;
        
        // Apply scroll offset to positions
        // Order: Pattern Selector, Max Particles, Pattern Speed, Pattern Spread, Pattern Intensity
        patternSelector.y = patternBaseButtonY - scrollOffsetInt;
        maxParticleColorSlider.y = patternBaseFirstFieldY - scrollOffsetInt;
        patternSpeedField.y = patternBaseFirstFieldY + fieldHeight + fieldSpacing - scrollOffsetInt;
        patternSpreadField.y = patternBaseFirstFieldY + (fieldHeight + fieldSpacing) * 2 - scrollOffsetInt;
        patternIntensityField.y = patternBaseFirstFieldY + (fieldHeight + fieldSpacing) * 3 - scrollOffsetInt;
    }
    
    /**
     * Recomputes positions/sizes for all widgets based on current content area and GUI scale.
     * Layout: 11% sidebar + 44% middle + 1% gap + 44% right (all from full screen width)
     * Middle panel: Top 50% (Default Properties), Bottom 50% (Pattern Properties)
     * Right panel: Top 50% (Color Pickers), Bottom 50% (reserved)
     */
    private void relayout(int contentX, int contentY, int contentWidth, int contentHeight) {
        int padding = 10; // Internal padding within boxes
        
        // Get screen dimensions for percentage-based calculations
        int screenWidth = parent.width;
        
        // Layout: [11% sidebar][1% gap][44% middle][1% gap][44% right]
        // Calculate panel widths from full screen width (not content width)
        int sidebarWidth = (int)(screenWidth * 0.11); // 11% of full screen
        int gap = (int)(screenWidth * 0.01); // 1% of full screen
        int middlePanelWidth = (int)(screenWidth * 0.44); // 44% of full screen
        int rightPanelWidth = (int)(screenWidth * 0.44); // 44% of full screen
        
        // Each section takes 50% of content height
        double sectionHeightPercent = 0.50; // 50% of content height
        int sectionHeight = (int)(contentHeight * sectionHeightPercent);
        
        // Calculate positions - middle panel starts after sidebar + gap
        // contentX is already the sidebar width, so we add the gap
        int middleX = sidebarWidth + gap; // 11% + 1% = start of middle panel
        int rightX = sidebarWidth + gap + middlePanelWidth + gap; // 11% + 1% + 44% + 1% = start of right panel
        int topY = contentY;
        int bottomY = contentY + sectionHeight;
        
        // Middle panel - Top 50%: Default Properties
        defaultBoxX = middleX;
        defaultBoxY = topY;
        defaultBoxWidth = middlePanelWidth;
        defaultBoxHeight = sectionHeight;
        
        // Middle panel - Bottom 50%: Pattern Properties
        patternBoxX = middleX;
        patternBoxY = bottomY;
        patternBoxWidth = middlePanelWidth;
        patternBoxHeight = sectionHeight;
        
        // Right panel - 100% height: Color Swatches and Color Picker
        colorBoxX = rightX;
        colorBoxY = contentY;
        colorBoxWidth = rightPanelWidth;
        colorBoxHeight = contentHeight; // Full height instead of 50%
        
        // Right panel - Bottom section no longer used (color picker is now in top panel)
        rightBottomBoxX = rightX;
        rightBottomBoxY = bottomY;
        rightBottomBoxWidth = rightPanelWidth;
        rightBottomBoxHeight = 0; // Not used anymore
        
        // Layout Middle Top: Default Properties (within defaultBox bounds)
        // Calculate all positions dynamically based on panel dimensions to ensure everything fits
        int defaultTextX = defaultBoxX + padding;
        int labelWidth = 140;
        int fieldX = defaultTextX + labelWidth - 3; // Start fields earlier (overlap slightly with label end for tighter layout)
        
        // Calculate vertical layout - use fixed spacing, enable scrolling if needed
        int fieldHeight = 20;
        int titleHeight = 20; // Space for "Default Properties" title
        int buttonHeight = 20;
        int numFields = 4; // Particle Speed, Spread, Lifetime, Density
        int fieldSpacing = 4; // Reduced spacing between fields (was 26)
        
        // Calculate total content height needed
        int buttonToFieldSpacing = 5; // Spacing between button and first field
        int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + (numFields * fieldHeight) + ((numFields - 1) * fieldSpacing);
        int availableHeight = defaultBoxHeight - padding * 2;
        
        // Always reserve space for scrollbar to prevent fields from overlapping it
        // Use a fixed scrollbar width to ensure consistent layout
        int scrollbarWidth = 10;
        int scrollbarOffset = 5; // Gap between components and scrollbar (5 pixels)
        boolean needsScrollbar = totalContentHeight > availableHeight;
        
        // Calculate end position: if scrollbar exists, end before scrollbar with offset, otherwise use full width
        int componentEndX;
        if (needsScrollbar) {
            // Components end before the scrollbar with offset (scrollbar starts at panel edge - scrollbarWidth)
            componentEndX = defaultBoxX + defaultBoxWidth - scrollbarWidth - scrollbarOffset;
        } else {
            // No scrollbar, use full width minus padding
            componentEndX = defaultBoxX + defaultBoxWidth - padding;
        }
        
        // CRITICAL: Do NOT override componentEndX - it must respect scrollbar position!
        // The fields will be narrower if needed, but they MUST end before the scrollbar
        
        // Position button - extend from label start to component end
        int buttonStartX = defaultTextX;
        // Calculate button width - button ends exactly at componentEndX
        int buttonWidth = componentEndX - buttonStartX;
        if (buttonWidth < 1) buttonWidth = 1; // Minimum button width
        
        // Calculate field width - fields MUST end exactly where button ends (at componentEndX)
        // Button ends at: buttonStartX + buttonWidth = componentEndX
        // Fields should end at: componentEndX (same as button)
        int fieldWidth = componentEndX - fieldX;
        // CRITICAL: Ensure fieldWidth never exceeds what it should be
        if (fieldWidth < 0) fieldWidth = 0;
        // Ensure field + width never exceeds componentEndX
        if (fieldX + fieldWidth > componentEndX) {
            fieldWidth = componentEndX - fieldX;
            if (fieldWidth < 0) fieldWidth = 0;
        }
        
        // Final verification: both button and fields end at componentEndX
        // Button end: buttonStartX + buttonWidth = componentEndX ✓
        // Field end: fieldX + fieldWidth = componentEndX ✓
        
        // Calculate starting Y position (no centering if scrolling is needed)
        int contentStartY = defaultBoxY + padding + titleHeight;
        int startY = contentStartY;
        
        // Store base positions (without scroll offset) - these will be used to calculate scroll positions
        defaultBaseButtonY = defaultBoxY + padding + titleHeight;
        defaultBaseFirstFieldY = defaultBaseButtonY + buttonHeight + buttonToFieldSpacing;
        
        // Set widget X positions and widths (these don't change with scrolling)
        // FINAL SAFETY CHECK: Ensure fieldWidth never exceeds componentEndX
        int finalFieldWidth = Math.min(fieldWidth, componentEndX - fieldX);
        if (finalFieldWidth < 0) finalFieldWidth = 0;
        
        usePatternToggle.x = buttonStartX;
        usePatternToggle.setWidth(buttonWidth);
        
        particleSpeedField.x = fieldX;
        particleSpeedField.setWidth(finalFieldWidth);
        
        particleSpreadField.x = fieldX;
        particleSpreadField.setWidth(finalFieldWidth);
        
        particleLifetimeField.x = fieldX;
        particleLifetimeField.setWidth(finalFieldWidth);
        
        particleDensityField.x = fieldX;
        particleDensityField.setWidth(finalFieldWidth);
        
        // Update Y positions with scroll offset
        updateDefaultPropertiesPositions();
        
        // Layout Right Top: Color Swatches and Shared Picker
        // Always update positions to ensure they stay within panel bounds
        if (colorSwatchButtons == null || colorSwatchButtons.isEmpty()) {
            createColorSwatchesAndPicker(PillarParticleConfig.get());
        }
        // Always reposition to ensure they scale with panel
        updateColorSwatchesPositions();
        
        // Layout Middle Bottom: Pattern Properties (within patternBox bounds)
        int patternTextX = patternBoxX + padding;
        int patternLabelWidth = 140;
        int patternFieldX = patternTextX + patternLabelWidth - 3; // Start fields earlier (overlap slightly with label end for tighter layout)
        
        // Define pattern properties constants first
        int patternFieldSpacing = 2; // Reduced spacing between fields (matching user's changes)
        int patternTitleHeight = 20;
        int patternButtonHeight = 20;
        int patternButtonToFieldSpacing = 5; // Reduced spacing between button and first field (matching user's changes)
        
        // Calculate field width based on available space in panel - ensure it doesn't exceed panel
        // Always reserve space for scrollbar to prevent fields from overlapping it
        int patternScrollbarWidth = 10;
        int patternScrollbarOffset = 5; // Gap between components and scrollbar (5 pixels)
        
        // Calculate if scrollbar is needed for pattern properties
        int patternTotalContentHeight = patternTitleHeight + patternButtonHeight + patternButtonToFieldSpacing + 
            (4 * 20) + (3 * patternFieldSpacing); // 4 fields + spacing
        int patternAvailableHeight = patternBoxHeight - padding * 2;
        boolean needsPatternScrollbar = patternTotalContentHeight > patternAvailableHeight;
        
        // Calculate end position: if scrollbar exists, end before scrollbar with offset, otherwise use full width
        int patternComponentEndX;
        if (needsPatternScrollbar) {
            // Components end before the scrollbar with offset (scrollbar starts at panel edge - scrollbarWidth)
            patternComponentEndX = patternBoxX + patternBoxWidth - patternScrollbarWidth - patternScrollbarOffset;
        } else {
            // No scrollbar, use full width minus padding
            patternComponentEndX = patternBoxX + patternBoxWidth - padding;
        }
        
        // CRITICAL: Do NOT override patternComponentEndX - it must respect scrollbar position!
        // The fields will be narrower if needed, but they MUST end before the scrollbar
        
        // Pattern selector button should start at label start and end at component end
        int patternButtonStartX = patternTextX;
        // Calculate button width - button ends exactly at patternComponentEndX
        int patternButtonWidth = patternComponentEndX - patternButtonStartX;
        if (patternButtonWidth < 1) patternButtonWidth = 1; // Minimum button width
        
        // Pattern Properties - use constants defined above
        int patternFieldY = patternBoxY + padding + patternTitleHeight + patternButtonHeight + patternButtonToFieldSpacing;
        
        // Calculate field width - fields MUST end exactly where button ends (at patternComponentEndX)
        // Button ends at: patternButtonStartX + patternButtonWidth = patternComponentEndX
        // Fields should end at: patternComponentEndX (same as button)
        int patternFieldWidth = patternComponentEndX - patternFieldX;
        // CRITICAL: Ensure patternFieldWidth never exceeds what it should be
        if (patternFieldWidth < 0) patternFieldWidth = 0;
        // Ensure field + width never exceeds patternComponentEndX
        if (patternFieldX + patternFieldWidth > patternComponentEndX) {
            patternFieldWidth = patternComponentEndX - patternFieldX;
            if (patternFieldWidth < 0) patternFieldWidth = 0;
        }
        
        // Final verification: both button and fields end at patternComponentEndX
        // Button end: patternButtonStartX + patternButtonWidth = patternComponentEndX ✓
        // Field end: patternFieldX + patternFieldWidth = patternComponentEndX ✓
        
        patternSelector.x = patternButtonStartX;
        patternSelector.y = patternBoxY + padding + patternTitleHeight;
        patternSelector.setWidth(patternButtonWidth);
        
        // FINAL SAFETY CHECK: Ensure patternFieldWidth never exceeds patternComponentEndX
        int finalPatternFieldWidth = Math.min(patternFieldWidth, patternComponentEndX - patternFieldX);
        if (finalPatternFieldWidth < 0) finalPatternFieldWidth = 0;
        
        // Order: Pattern Selector, Max Particles (second), Pattern Speed, Pattern Spread, Pattern Intensity
        // Max Particles slider in pattern box (second, after pattern selector button)
        maxParticleColorSlider.x = patternFieldX;
        maxParticleColorSlider.y = patternFieldY;
        maxParticleColorSlider.setWidth(finalPatternFieldWidth);
        
        // Pattern Speed field (third)
        patternSpeedField.x = patternFieldX;
        patternSpeedField.y = patternFieldY + 20 + patternFieldSpacing;
        patternSpeedField.setWidth(finalPatternFieldWidth);
        
        patternSpreadField.x = patternFieldX;
        patternSpreadField.y = patternFieldY + (20 + patternFieldSpacing) * 2;
        patternSpreadField.setWidth(finalPatternFieldWidth);
        
        patternIntensityField.x = patternFieldX;
        patternIntensityField.y = patternFieldY + (20 + patternFieldSpacing) * 3;
        patternIntensityField.setWidth(finalPatternFieldWidth);
        
        // Calculate total content height for pattern box to determine if scrolling is needed
        // Note: patternTotalContentHeight and patternAvailableHeight are already calculated above in relayout
        // Recalculate with slider included for render method
        int patternFieldHeight = 20;
        int patternSliderHeight = 20;
        int patternNumFields = 3; // Pattern Speed, Spread, Intensity
        int patternTotalContentHeightWithSlider = patternTitleHeight + patternButtonHeight + patternButtonToFieldSpacing + 
            (patternNumFields * patternFieldHeight) + ((patternNumFields - 1) * patternFieldSpacing) + 
            patternSliderHeight + patternFieldSpacing;
        // Use the already calculated patternAvailableHeight from relayout
        double patternMaxScroll = Math.max(0, patternTotalContentHeightWithSlider - patternAvailableHeight);
        
        // Store base positions for pattern properties (without scroll offset)
        patternBaseButtonY = patternBoxY + padding + patternTitleHeight;
        patternBaseFirstFieldY = patternBaseButtonY + patternButtonHeight + patternButtonToFieldSpacing;
        
        // Update pattern properties positions with scroll offset
        updatePatternPropertiesPositions();
        
        // Layout Right Panel: Color Selector (sharedColorPicker) - now in top right panel
        // Position shared color picker in the colorBox (top right panel, now full height)
        // Always position it, even if not visible, so it's ready when a swatch is clicked
        if (sharedColorPicker != null) {
            int pickerPadding = 10;
            int pickerSize = Math.min(100, colorBoxWidth - pickerPadding * 2);
            // Position picker to the right of swatches, below them
            int swatchAreaHeight = 7 * (20 + 4) + 5; // 7 swatches with rowSpacing of 4, plus padding
            int pickerX = colorBoxX + colorBoxWidth - pickerPadding - pickerSize; // Right side of panel
            int pickerY = colorBoxY + padding + swatchAreaHeight; // Below swatches
            
            // Ensure picker doesn't overflow panel
            if (pickerX + pickerSize > colorBoxX + colorBoxWidth - pickerPadding) {
                pickerX = colorBoxX + colorBoxWidth - pickerPadding - pickerSize;
            }
            if (pickerY + pickerSize > colorBoxY + colorBoxHeight - pickerPadding) {
                pickerY = colorBoxY + colorBoxHeight - pickerPadding - pickerSize;
            }
            sharedColorPicker.x = pickerX;
            sharedColorPicker.y = pickerY;
            sharedColorPicker.setWidth(pickerSize);
            sharedColorPicker.setHeight(pickerSize);
            // Don't set visible here - it's controlled by onColorSwatchClicked
        }
    }
    
    
    private void onColorChanged(int index, String hexColor) {
        PillarParticleConfig config = PillarParticleConfig.get();
        // Ensure list is large enough
        while (config.particle_color.size() <= index) {
            config.particle_color.add("#FFFFFF");
        }
        config.particle_color.set(index, hexColor);
        config.saveProperties();
    }
    
    private void toggleUsePattern() {
        PillarParticleConfig config = PillarParticleConfig.get();
        config.use_pattern = !config.use_pattern;
        config.saveProperties();
        
        // Update UI - show "Use Pattern True" or "Use Pattern False"
        String buttonText = "Use Pattern " + (config.use_pattern ? "True" : "False");
        usePatternToggle.setMessage(new TextComponent(buttonText));
        particleSpeedField.setEditable(!config.use_pattern);
        particleSpreadField.setEditable(!config.use_pattern);
        particleLifetimeField.setEditable(!config.use_pattern);
        particleDensityField.setEditable(!config.use_pattern);
        patternSelector.active = config.use_pattern;
        patternSpeedField.setEditable(config.use_pattern);
        patternSpreadField.setEditable(config.use_pattern);
        patternIntensityField.setEditable(config.use_pattern);
        
        // Disable/enable color swatches and max particle color slider based on use_pattern
        boolean colorControlsEnabled = config.use_pattern;
        if (maxParticleColorSlider != null) {
            maxParticleColorSlider.active = colorControlsEnabled;
        }
        // Update swatches enabled state (considers both use_pattern and max value)
        updateSwatchesEnabledState();
        if (sharedColorPicker != null) {
            sharedColorPicker.setEnabled(colorControlsEnabled);
            if (!colorControlsEnabled) {
                sharedColorPicker.visible = false;
                selectedColorIndex = -1;
            }
        }
    }
    
    private void cyclePattern() {
        currentPatternIndex = (currentPatternIndex + 1) % PATTERNS.length;
        String pattern = PATTERNS[currentPatternIndex];
        
        PillarParticleConfig config = PillarParticleConfig.get();
        config.pattern = pattern;
        config.saveProperties();
        
        patternSelector.setMessage(new TranslatableComponent("buildscape.config.particles.pattern." + pattern));
    }
    
    private void onMaxParticleColorChanged(int value) {
        currentMaxColor = value;
        
        PillarParticleConfig config = PillarParticleConfig.get();
        config.max_particle_color = currentMaxColor;
        config.saveProperties();
        
        maxParticleColorSlider.setMessage(new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor));
        
        // Enable/disable swatches based on max value
        // Only swatches up to the max value should be clickable
        updateSwatchesEnabledState();
        
        // If currently selected swatch is beyond max, deselect it
        if (selectedColorIndex >= currentMaxColor) {
            selectedColorIndex = -1;
            if (sharedColorPicker != null) {
                sharedColorPicker.visible = false;
            }
        }
    }
    
    private void updateSwatchesEnabledState() {
        PillarParticleConfig config = PillarParticleConfig.get();
        boolean usePatternEnabled = config.use_pattern;
        
        if (colorSwatchButtons != null) {
            for (int i = 0; i < colorSwatchButtons.size(); i++) {
                // Swatch is enabled only if:
                // 1. use_pattern is true (pattern mode enabled)
                // 2. Index is less than currentMaxColor (within allowed range)
                boolean enabled = usePatternEnabled && (i < currentMaxColor);
                colorSwatchButtons.get(i).active = enabled;
            }
        }
        
        // Also update hex fields - they should be editable when not locked by max particles
        if (colorHexFields != null) {
            for (int i = 0; i < colorHexFields.size(); i++) {
                // Hex field is editable if:
                // 1. use_pattern is true (pattern mode enabled)
                // 2. Index is less than currentMaxColor (within allowed range)
                boolean editable = usePatternEnabled && (i < currentMaxColor);
                colorHexFields.get(i).setEditable(editable);
            }
        }
    }
    
    private int findPatternIndex(String pattern) {
        for (int i = 0; i < PATTERNS.length; i++) {
            if (PATTERNS[i].equals(pattern)) {
                return i;
            }
        }
        return 0;
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();
        
        // Always check if relayout is needed (dimensions or screen size changed)
        int screenWidth = parent.width;
        boolean needsRelayout = (contentX != lastContentX || contentY != lastContentY || 
            contentWidth != lastContentWidth || contentHeight != lastContentHeight ||
            screenWidth != lastScreenWidth);
        
        if (needsRelayout) {
        relayout(contentX, contentY, contentWidth, contentHeight);
            lastContentX = contentX;
            lastContentY = contentY;
            lastContentWidth = contentWidth;
            lastContentHeight = contentHeight;
            lastScreenWidth = screenWidth;
        }
        
        // Overall background - removed colorful background
        // GuiComponent.fill(poseStack, contentX, contentY, contentX + contentWidth, contentY + contentHeight, 0xC0220B0B);
        
        // Header title removed as requested
        
        Minecraft mcInstance = Minecraft.getInstance();
        double currentGuiScale = mcInstance.getWindow().getGuiScale();
        PillarParticleConfig config = PillarParticleConfig.get();
        int padding = 10; // Internal padding within boxes
        
        // Middle Top: Default Properties - Render with scissor test to clip to panel bounds
        // Scissor coordinates need to account for GUI scale (window pixels, not GUI pixels)
        // Scissor uses window coordinates: X from left, Y from bottom
        double guiScale = mcInstance.getWindow().getGuiScale();
        int windowHeight = mcInstance.getWindow().getHeight();
        int scissorX = (int)(defaultBoxX * guiScale);
        int scissorY = (int)(windowHeight - (defaultBoxY + defaultBoxHeight) * guiScale);
        int scissorWidth = (int)(defaultBoxWidth * guiScale);
        int scissorHeight = (int)(defaultBoxHeight * guiScale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.default_properties"), defaultBoxX + 10, defaultBoxY + 5, 0xFFFFFF);

        // Render labels and fields for default properties - use actual widget positions (already have scroll offset applied)
        int defaultTextX = defaultBoxX + padding;
        int labelWidth = 140;
        int fieldHeight = 20;
        int labelYOffset = 6;
        int titleHeight = 20;
        int buttonHeight = 20;
        int fieldSpacing = 10; // Reduced spacing between fields
        int numFields = 4;
        
        // Calculate total content height and scroll range
        int buttonToFieldSpacing = 15; // Spacing between button and first field
        int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + (numFields * fieldHeight) + ((numFields - 1) * fieldSpacing);
        int availableHeight = defaultBoxHeight - padding * 2;
        double maxScroll = Math.max(0, totalContentHeight - availableHeight);
        boolean needsScrollbar = maxScroll > 0;
        
        // Define header area - nothing should render above this
        int headerBottom = defaultBoxY + titleHeight + 5; // Title + some padding
        
        // Render labels and widgets aligned correctly - widgets must be rendered here within scissor test
        // Only render labels if they're below the header
        int particleSpeedLabelY = particleSpeedField.y + labelYOffset;
        if (particleSpeedLabelY >= headerBottom) {
            Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.particle_speed").getString() + " ", defaultTextX, particleSpeedLabelY, 0xFFFFFF);
        }
        
        int particleSpreadLabelY = particleSpreadField.y + labelYOffset;
        if (particleSpreadLabelY >= headerBottom) {
            Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.particle_spread").getString() + " ", defaultTextX, particleSpreadLabelY, 0xFFFFFF);
        }
        
        int particleLifetimeLabelY = particleLifetimeField.y + labelYOffset;
        if (particleLifetimeLabelY >= headerBottom) {
            Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.particle_lifetime").getString() + " ", defaultTextX, particleLifetimeLabelY, 0xFFFFFF);
        }
        
        int particleDensityLabelY = particleDensityField.y + labelYOffset;
        if (particleDensityLabelY >= headerBottom) {
            Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.particle_density").getString() + " ", defaultTextX, particleDensityLabelY, 0xFFFFFF);
        }
        
        // Permanently hide widgets from parent - we render them manually here with scissor
        usePatternToggle.visible = false;
        particleSpeedField.visible = false;
        particleSpreadField.visible = false;
        particleLifetimeField.visible = false;
        particleDensityField.visible = false;
        
        // Render widgets within scissor test to ensure they're clipped properly
        // Only render if they're within the visible panel area and below the header
        int panelTop = defaultBoxY;
        int panelBottom = defaultBoxY + defaultBoxHeight;
        
        // Render button only if below header
        if (usePatternToggle.y >= headerBottom && usePatternToggle.y + 20 >= panelTop && usePatternToggle.y <= panelBottom) {
            usePatternToggle.visible = true; // Make visible for rendering
        usePatternToggle.render(poseStack, mouseX, mouseY, partialTick);
            usePatternToggle.visible = false; // Hide again to prevent parent from rendering
        }
        // Render text fields only if below header and within panel
        if (particleSpeedField.y >= headerBottom && particleSpeedField.y + 20 >= panelTop && particleSpeedField.y <= panelBottom) {
            particleSpeedField.visible = true; // Make visible for rendering
        particleSpeedField.render(poseStack, mouseX, mouseY, partialTick);
            particleSpeedField.visible = false; // Hide again to prevent parent from rendering
        }
        if (particleSpreadField.y >= headerBottom && particleSpreadField.y + 20 >= panelTop && particleSpreadField.y <= panelBottom) {
            particleSpreadField.visible = true; // Make visible for rendering
        particleSpreadField.render(poseStack, mouseX, mouseY, partialTick);
            particleSpreadField.visible = false; // Hide again to prevent parent from rendering
        }
        if (particleLifetimeField.y >= headerBottom && particleLifetimeField.y + 20 >= panelTop && particleLifetimeField.y <= panelBottom) {
            particleLifetimeField.visible = true; // Make visible for rendering
        particleLifetimeField.render(poseStack, mouseX, mouseY, partialTick);
            particleLifetimeField.visible = false; // Hide again to prevent parent from rendering
        }
        if (particleDensityField.y >= headerBottom && particleDensityField.y + 20 >= panelTop && particleDensityField.y <= panelBottom) {
            particleDensityField.visible = true; // Make visible for rendering
        particleDensityField.render(poseStack, mouseX, mouseY, partialTick);
            particleDensityField.visible = false; // Hide again to prevent parent from rendering
        }
        
        // Render scrollbar if needed
        if (needsScrollbar && maxScroll > 0) {
            int scrollbarWidth = 10;
            int scrollbarX = defaultBoxX + defaultBoxWidth - scrollbarWidth; // Scrollbar at edge
            int scrollbarY = defaultBoxY + padding + titleHeight;
            int scrollbarHeight = availableHeight;
            
            // Draw scrollbar track
            GuiComponent.fill(poseStack, scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0x80000000);
            
            // Calculate thumb
            int thumbHeight = Math.max(20, (int)(scrollbarHeight * (availableHeight / (double)totalContentHeight)));
            double scrollRatio = maxScroll > 0 ? defaultPropertiesScrollOffset / maxScroll : 0;
            int thumbY = scrollbarY + (int)(scrollRatio * (scrollbarHeight - thumbHeight));
            
            // Draw scrollbar thumb
            GuiComponent.fill(poseStack, scrollbarX, thumbY, scrollbarX + scrollbarWidth, thumbY + thumbHeight, 0xFF808080);
            GuiComponent.fill(poseStack, scrollbarX + 1, thumbY + 1, scrollbarX + scrollbarWidth - 1, thumbY + thumbHeight - 1, 0xFFC0C0C0);
        }
        
        RenderSystem.disableScissor();
        
        // Right Top: Color Swatches and Shared Picker - Render with scissor test to clip to panel bounds
        scissorX = (int)(colorBoxX * guiScale);
        scissorY = (int)(windowHeight - (colorBoxY + colorBoxHeight) * guiScale);
        scissorWidth = (int)(colorBoxWidth * guiScale);
        scissorHeight = (int)(colorBoxHeight * guiScale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        
        // Debug: Draw panel background to verify panel is visible
        GuiComponent.fill(poseStack, colorBoxX, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, 0x40000000);
        
        // Removed "Custom Properties" title text as requested
        
        // Ensure swatches are created if they don't exist
        if (colorSwatchButtons == null || colorHexFields == null || 
            colorSwatchButtons.isEmpty() || colorHexFields.isEmpty()) {
            createColorSwatchesAndPicker(config);
        }
        
        // Update color swatches with current colors and selection state
        if (colorSwatchButtons != null && colorHexFields != null && 
            colorSwatchButtons.size() > 0 && colorHexFields.size() > 0) {
            // Update enabled state first (based on max value)
            updateSwatchesEnabledState();
            
            // Calculate scroll info for color swatches (2 columns layout)
            int colorPadding = 10;
            int swatchSize = 20;
            int rowSpacing = 4; // Matching user's changes
            int numSwatches = 7;
            int numRows = (numSwatches + 1) / 2; // 4 rows (3 full rows + 1 with 1 swatch)
            int colorTotalContentHeight = (numRows * swatchSize) + ((numRows - 1) * rowSpacing); // No title anymore
            int colorAvailableHeight = colorBoxHeight - colorPadding * 2;
            double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);
            boolean colorNeedsScrollbar = colorMaxScroll > 0;
            
            // Ensure positions are updated (this is critical - must be called after colorBox coordinates are set)
            updateColorSwatchesPositions();
            
            // Permanently hide widgets from parent - we render them manually here with scissor
            for (int i = 0; i < colorSwatchButtons.size(); i++) {
                colorSwatchButtons.get(i).visible = false;
            }
            for (int i = 0; i < colorHexFields.size(); i++) {
                colorHexFields.get(i).visible = false;
            }
            
            // Render swatches and hex fields - always render them, scissor test will clip them
            for (int i = 0; i < colorSwatchButtons.size() && i < colorHexFields.size(); i++) {
                ColorSwatchButton swatchButton = colorSwatchButtons.get(i);
                
                // Get color for this swatch
                String hexValue = i < config.particle_color.size() ? config.particle_color.get(i) : "#FFFFFF";
                int color = 0xFFFFFF;
                try {
                    if (hexValue.startsWith("#") && hexValue.length() == 7) {
                        color = Integer.parseInt(hexValue.substring(1), 16);
                    }
                } catch (NumberFormatException e) {
                    // Use default white
                }
                
                // Update swatch button color and selection state
                swatchButton.setColor(color);
                swatchButton.setSelected(selectedColorIndex == i);
                
                // Always render swatches - scissor test will clip them to panel bounds
                swatchButton.visible = true;
                swatchButton.render(poseStack, mouseX, mouseY, partialTick);
                swatchButton.visible = false;
                
                // Always render hex fields - scissor test will clip them to panel bounds
                colorHexFields.get(i).visible = true;
                colorHexFields.get(i).render(poseStack, mouseX, mouseY, partialTick);
                colorHexFields.get(i).visible = false;
            }
            
            // Render scrollbar if needed
            if (colorNeedsScrollbar && colorMaxScroll > 0) {
                int scrollbarWidth = 10;
                int scrollbarOffset = 3; // Gap between components and scrollbar (2-3 pixels)
                int scrollbarX = colorBoxX + colorBoxWidth - scrollbarWidth - scrollbarOffset; // Scrollbar with offset from edge
                int scrollbarY = colorBoxY + colorPadding;
                int scrollbarHeight = colorAvailableHeight;
                
                // Draw scrollbar track
                GuiComponent.fill(poseStack, scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0x80000000);
                
                // Calculate thumb
                int thumbHeight = Math.max(20, (int)(scrollbarHeight * (colorAvailableHeight / (double)colorTotalContentHeight)));
                double scrollRatio = colorMaxScroll > 0 ? colorSwatchesScrollOffset / colorMaxScroll : 0;
                int thumbY = scrollbarY + (int)(scrollRatio * (scrollbarHeight - thumbHeight));
                
                // Draw scrollbar thumb
                GuiComponent.fill(poseStack, scrollbarX, thumbY, scrollbarX + scrollbarWidth, thumbY + thumbHeight, 0xFF808080);
                GuiComponent.fill(poseStack, scrollbarX + 1, thumbY + 1, scrollbarX + scrollbarWidth - 1, thumbY + thumbHeight - 1, 0xFFC0C0C0);
            }
        }
        
        // Render shared color picker if a swatch is selected - hide from parent and render manually
        if (sharedColorPicker != null) {
            // Always hide from parent to prevent duplicate rendering
            sharedColorPicker.visible = false;
            
            // Render manually if a swatch is selected - always render when selectedColorIndex is valid
            if (selectedColorIndex >= 0 && selectedColorIndex < 7) {
                // Recalculate position during render to ensure it's correct
                int pickerPadding = 10;
                int swatchSize = 20;
                int rowSpacing = 4;
                int numSwatches = 7;
                int numRows = (numSwatches + 1) / 2; // 4 rows (3 full rows + 1 with 1 swatch)
                int swatchAreaHeight = (numRows * swatchSize) + ((numRows - 1) * rowSpacing); // Swatch area height
                int colorPadding = 10;
                
                // Calculate available space for picker
                int availableY = colorBoxY + colorPadding + swatchAreaHeight + 20; // Start below swatches
                int pickerAvailableHeight = colorBoxY + colorBoxHeight - pickerPadding - availableY; // Remaining height
                int pickerAvailableWidth = colorBoxWidth - pickerPadding * 2; // Available width
                
                // Ideal picker size
                int idealWidth = 250;
                int idealHeight = 220;
                
                // Calculate actual picker size - shrink to fit if needed
                int pickerWidth = Math.min(idealWidth, pickerAvailableWidth);
                int pickerHeight = Math.min(idealHeight, pickerAvailableHeight);
                
                // If picker is smaller than ideal, it will scale internally
                // Position picker below the swatches, centered horizontally
                int pickerX = colorBoxX + (colorBoxWidth - pickerWidth) / 2; // Center horizontally
                int pickerY = availableY; // Below swatches
                
                // Ensure picker doesn't overflow panel
                if (pickerX < colorBoxX + pickerPadding) {
                    pickerX = colorBoxX + pickerPadding;
                    pickerWidth = Math.min(pickerWidth, colorBoxX + colorBoxWidth - pickerPadding - pickerX);
                }
                if (pickerY + pickerHeight > colorBoxY + colorBoxHeight - pickerPadding) {
                    pickerHeight = colorBoxY + colorBoxHeight - pickerPadding - pickerY;
                }
                
                // Set picker size and position
                sharedColorPicker.x = pickerX;
                sharedColorPicker.y = pickerY;
                sharedColorPicker.setWidth(pickerWidth);
                sharedColorPicker.setHeight(pickerHeight);
                
                // Always render the picker - scissor test will clip it to panel bounds
                sharedColorPicker.visible = true;
                sharedColorPicker.renderButton(poseStack, mouseX, mouseY, partialTick);
                sharedColorPicker.visible = false;
            }
        }
        RenderSystem.disableScissor();
        
        // Middle Bottom: Pattern Properties - Render with scissor test to clip to panel bounds
        scissorX = (int)(patternBoxX * guiScale);
        scissorY = (int)(windowHeight - (patternBoxY + patternBoxHeight) * guiScale);
        scissorWidth = (int)(patternBoxWidth * guiScale);
        scissorHeight = (int)(patternBoxHeight * guiScale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_properties"), patternBoxX + 10, patternBoxY + 5, 0xFFFFFF);
        
        int patternTextX = patternBoxX + padding;
        int patternLabelWidth = 140;
        int patternFieldX = patternTextX + patternLabelWidth + 2; // Reduced gap between label and field (matching relayout)
        int patternTitleHeight = 20;
        int patternButtonHeight = 20;
        int patternButtonToFieldSpacing = 5; // Reduced spacing (matching user's changes)
        int patternFieldSpacing = 2; // Reduced spacing (matching user's changes)
        int patternLabelYOffset = 6;
        int patternFieldHeight = 20;
        int patternSliderHeight = 20;
        
        // Calculate scroll info
        int patternTotalContentHeight = patternTitleHeight + patternButtonHeight + patternButtonToFieldSpacing + 
            (3 * patternFieldHeight) + (2 * patternFieldSpacing) + patternSliderHeight + patternFieldSpacing;
        int patternAvailableHeight = patternBoxHeight - padding * 2;
        double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);
        boolean patternNeedsScrollbar = patternMaxScroll > 0;
        
        // Define header area - nothing should render above this
        int patternHeaderBottom = patternBoxY + patternTitleHeight + 5;
        int patternPanelTop = patternBoxY;
        int patternPanelBottom = patternBoxY + patternBoxHeight;
        
        // Pattern selector button label - only render if below header
        // Use actual widget position which already has scroll offset applied
        // Don't render text behind the button - the button itself will display the pattern name
        // The button text is handled by the button's render method, so we don't need to render it here
        
        // Max Particles label - align with maxParticleColorSlider (second, after pattern selector)
        int maxParticleLabelY = maxParticleColorSlider.y + patternLabelYOffset;
        if (maxParticleLabelY >= patternHeaderBottom && maxParticleColorSlider.y >= patternPanelTop && maxParticleColorSlider.y <= patternPanelBottom) {
        Minecraft.getInstance().font.draw(poseStack, "Max Particle's ", patternTextX, maxParticleLabelY, 0xFFFFFF);
        }
        
        // Pattern Speed label - align with patternSpeedField (use actual widget position)
        int patternSpeedLabelY = patternSpeedField.y + patternLabelYOffset;
        if (patternSpeedLabelY >= patternHeaderBottom && patternSpeedField.y >= patternPanelTop && patternSpeedField.y <= patternPanelBottom) {
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_speed").getString() + " ", patternTextX, patternSpeedLabelY, 0xFFFFFF);
        }
        
        // Pattern Spread label - align with patternSpreadField
        int patternSpreadLabelY = patternSpreadField.y + patternLabelYOffset;
        if (patternSpreadLabelY >= patternHeaderBottom && patternSpreadField.y >= patternPanelTop && patternSpreadField.y <= patternPanelBottom) {
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_spread").getString() + " ", patternTextX, patternSpreadLabelY, 0xFFFFFF);
        }
        
        // Pattern Intensity label - align with patternIntensityField
        int patternIntensityLabelY = patternIntensityField.y + patternLabelYOffset;
        if (patternIntensityLabelY >= patternHeaderBottom && patternIntensityField.y >= patternPanelTop && patternIntensityField.y <= patternPanelBottom) {
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_intensity").getString() + " ", patternTextX, patternIntensityLabelY, 0xFFFFFF);
        }
        
        // Permanently hide widgets from parent - we render them manually here with scissor
        patternSelector.visible = false;
        patternSpeedField.visible = false;
        patternSpreadField.visible = false;
        patternIntensityField.visible = false;
        maxParticleColorSlider.visible = false;
        
        // Render widgets for pattern properties - render within scissor test
        // Order: Pattern Selector, Max Particles (second), Pattern Speed, Pattern Spread, Pattern Intensity
        // Only render if below header and within panel bounds
        if (patternSelector.y >= patternHeaderBottom && patternSelector.y >= patternPanelTop && patternSelector.y <= patternPanelBottom) {
            patternSelector.visible = true; // Make visible for rendering
        patternSelector.render(poseStack, mouseX, mouseY, partialTick);
            patternSelector.visible = false; // Hide again to prevent parent from rendering
        }
        if (maxParticleColorSlider.y >= patternHeaderBottom && maxParticleColorSlider.y >= patternPanelTop && maxParticleColorSlider.y <= patternPanelBottom) {
            maxParticleColorSlider.visible = true; // Make visible for rendering
        maxParticleColorSlider.render(poseStack, mouseX, mouseY, partialTick);
            maxParticleColorSlider.visible = false; // Hide again to prevent parent from rendering
        }
        if (patternSpeedField.y >= patternHeaderBottom && patternSpeedField.y >= patternPanelTop && patternSpeedField.y <= patternPanelBottom) {
            patternSpeedField.visible = true; // Make visible for rendering
        patternSpeedField.render(poseStack, mouseX, mouseY, partialTick);
            patternSpeedField.visible = false; // Hide again to prevent parent from rendering
        }
        if (patternSpreadField.y >= patternHeaderBottom && patternSpreadField.y >= patternPanelTop && patternSpreadField.y <= patternPanelBottom) {
            patternSpreadField.visible = true; // Make visible for rendering
        patternSpreadField.render(poseStack, mouseX, mouseY, partialTick);
            patternSpreadField.visible = false; // Hide again to prevent parent from rendering
        }
        if (patternIntensityField.y >= patternHeaderBottom && patternIntensityField.y >= patternPanelTop && patternIntensityField.y <= patternPanelBottom) {
            patternIntensityField.visible = true; // Make visible for rendering
        patternIntensityField.render(poseStack, mouseX, mouseY, partialTick);
            patternIntensityField.visible = false; // Hide again to prevent parent from rendering
        }
        
        // Render scrollbar if needed
        if (patternNeedsScrollbar && patternMaxScroll > 0) {
            int scrollbarWidth = 10;
            int scrollbarX = patternBoxX + patternBoxWidth - scrollbarWidth; // Scrollbar at edge
            int scrollbarY = patternBoxY + padding + patternTitleHeight;
            int scrollbarHeight = patternAvailableHeight;
            
            // Draw scrollbar track
            GuiComponent.fill(poseStack, scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0x80000000);
            
            // Calculate thumb
            int thumbHeight = Math.max(20, (int)(scrollbarHeight * (patternAvailableHeight / (double)patternTotalContentHeight)));
            double scrollRatio = patternMaxScroll > 0 ? patternPropertiesScrollOffset / patternMaxScroll : 0;
            int thumbY = scrollbarY + (int)(scrollRatio * (scrollbarHeight - thumbHeight));
            
            // Draw scrollbar thumb
            GuiComponent.fill(poseStack, scrollbarX, thumbY, scrollbarX + scrollbarWidth, thumbY + thumbHeight, 0xFF808080);
            GuiComponent.fill(poseStack, scrollbarX + 1, thumbY + 1, scrollbarX + scrollbarWidth - 1, thumbY + thumbHeight - 1, 0xFFC0C0C0);
        }
        
        RenderSystem.disableScissor();
        
        // Right Bottom panel is no longer used - color picker is now in the top right panel
        
        // Update config from current fields
        updateConfigFromFields();
    }

    
    private void updateConfigFromFields() {
        PillarParticleConfig config = PillarParticleConfig.get();
        boolean changed = false;
        
        // Update default properties
        if (!config.use_pattern) {
            try {
                double speed = Double.parseDouble(particleSpeedField.getValue());
                if (config.particle_speed != speed) {
                    config.particle_speed = speed;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }
            
            try {
                double spread = Double.parseDouble(particleSpreadField.getValue());
                if (config.particle_spread != spread) {
                    config.particle_spread = spread;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }
            
            try {
                int lifetime = Integer.parseInt(particleLifetimeField.getValue());
                if (config.particle_lifetime != lifetime) {
                    config.particle_lifetime = lifetime;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }
            
            try {
                int density = Integer.parseInt(particleDensityField.getValue());
                if (config.particle_density != density) {
                    config.particle_density = density;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }
        }
        
        // Update pattern properties
        if (config.use_pattern) {
            try {
                double speed = Double.parseDouble(patternSpeedField.getValue());
                if (config.pattern_speed != speed) {
                    config.pattern_speed = speed;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }
            
            try {
                double spread = Double.parseDouble(patternSpreadField.getValue());
                if (config.pattern_spread != spread) {
                    config.pattern_spread = spread;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }
            
            try {
                double intensity = Double.parseDouble(patternIntensityField.getValue());
                if (config.pattern_intensity != intensity) {
                    config.pattern_intensity = intensity;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }
        }
        
        if (changed) {
            config.saveProperties();
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle scrollbar clicks for Default Properties panel
        int padding = 10;
        int titleHeight = 20;
        int availableHeight = defaultBoxHeight - padding * 2;
        int fieldHeight = 20;
        int fieldSpacing = 10; // Reduced spacing between fields
        int buttonHeight = 20;
        int numFields = 4;
        int totalContentHeight = titleHeight + buttonHeight + fieldSpacing + (numFields * fieldHeight) + ((numFields - 1) * fieldSpacing);
        double maxScroll = Math.max(0, totalContentHeight - availableHeight);
        
        if (maxScroll > 0 && button == 0) {
            int scrollbarWidth = 10;
            int scrollbarX = defaultBoxX + defaultBoxWidth - scrollbarWidth; // Scrollbar at edge
            int scrollbarY = defaultBoxY + padding + titleHeight;
            int scrollbarHeight = availableHeight;
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                // Clicked on scrollbar - start dragging
                isDraggingDefaultScrollbar = true;
                scrollbarDragStartY = mouseY;
                scrollbarDragStartOffset = defaultPropertiesScrollOffset;
                
                // Calculate thumb position
                int thumbHeight = Math.max(20, (int)(scrollbarHeight * (availableHeight / (double)totalContentHeight)));
                double scrollRatio = maxScroll > 0 ? defaultPropertiesScrollOffset / maxScroll : 0;
                int thumbY = scrollbarY + (int)(scrollRatio * (scrollbarHeight - thumbHeight));
                
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    // Clicked on thumb - start dragging (already set above)
                    return true;
                } else {
                    // Clicked on track - jump to that position
                    double clickRatio = (mouseY - scrollbarY) / (double)scrollbarHeight;
                    defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, clickRatio * maxScroll));
                    scrollbarDragStartOffset = defaultPropertiesScrollOffset;
                    scrollbarDragStartY = mouseY;
                    updateDefaultPropertiesPositions(); // Update widget positions
                    return true;
                }
            }
        }
        
        // Handle scrollbar clicking (jump to position, start drag) for Pattern Properties panel
        if (mouseX >= patternBoxX && mouseX <= patternBoxX + patternBoxWidth &&
            mouseY >= patternBoxY && mouseY <= patternBoxY + patternBoxHeight) {
            int patternPadding = 10;
            int patternTitleHeight = 20;
            int patternAvailableHeight = patternBoxHeight - patternPadding * 2;
            int patternFieldHeight = 20;
            int patternFieldSpacing = 2; // Reduced spacing (matching user's changes)
            int patternButtonHeight = 20;
            int patternButtonToFieldSpacing = 5; // Reduced spacing (matching user's changes)
            int patternSliderHeight = 20;
            int patternTotalContentHeight = patternTitleHeight + patternButtonHeight + patternButtonToFieldSpacing + 
                (3 * patternFieldHeight) + (2 * patternFieldSpacing) + patternSliderHeight + patternFieldSpacing;
            double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);
            
            if (patternMaxScroll > 0 && button == 0) {
                int scrollbarWidth = 10;
                int scrollbarX = patternBoxX + patternBoxWidth - scrollbarWidth; // Scrollbar at edge
                int scrollbarY = patternBoxY + patternPadding + patternTitleHeight;
                int scrollbarHeight = patternAvailableHeight;
                
                if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                    // Clicked on scrollbar - start dragging
                    isDraggingPatternScrollbar = true;
                    scrollbarDragStartY = mouseY;
                    scrollbarDragStartOffset = patternPropertiesScrollOffset;
                    
                    // Calculate thumb position
                    int thumbHeight = Math.max(20, (int)(scrollbarHeight * (patternAvailableHeight / (double)patternTotalContentHeight)));
                    double scrollRatio = patternMaxScroll > 0 ? patternPropertiesScrollOffset / patternMaxScroll : 0;
                    int thumbY = scrollbarY + (int)(scrollRatio * (scrollbarHeight - thumbHeight));
                    
                    if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                        // Clicked on thumb - start dragging (already set above)
                        return true;
                    } else {
                        // Clicked on track - jump to that position
                        double clickRatio = (mouseY - scrollbarY) / (double)scrollbarHeight;
                        patternPropertiesScrollOffset = Math.max(0, Math.min(patternMaxScroll, clickRatio * patternMaxScroll));
                        scrollbarDragStartOffset = patternPropertiesScrollOffset;
                        scrollbarDragStartY = mouseY;
                        updatePatternPropertiesPositions(); // Update widget positions
                        return true;
                    }
                }
            }
        }
        
        // Handle scrollbar clicking (jump to position, start drag) for Color Swatches panel
        if (mouseX >= colorBoxX && mouseX <= colorBoxX + colorBoxWidth &&
            mouseY >= colorBoxY && mouseY <= colorBoxY + colorBoxHeight) {
            int colorPadding = 10;
            int colorTitleHeight = 0; // No title anymore
            int colorAvailableHeight = colorBoxHeight - colorPadding * 2;
            int swatchSize = 20;
            int rowSpacing = 4; // Matching user's changes
            int numSwatches = 7;
            int numRows = (numSwatches + 1) / 2; // 4 rows (3 full rows + 1 with 1 swatch)
            int colorTotalContentHeight = (numRows * swatchSize) + ((numRows - 1) * rowSpacing); // No title anymore
            double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);
            
            if (colorMaxScroll > 0) {
                int scrollbarWidth = 10;
                int scrollbarOffset = 3; // Gap between components and scrollbar (2-3 pixels)
                int scrollbarX = colorBoxX + colorBoxWidth - scrollbarWidth - scrollbarOffset; // Scrollbar with offset from edge
                int scrollbarY = colorBoxY + colorPadding;
                int scrollbarHeight = colorAvailableHeight;
                
                // Check if clicking on scrollbar
                if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth) {
                    // Calculate thumb position
                    int thumbHeight = Math.max(20, (int)(scrollbarHeight * (colorAvailableHeight / (double)colorTotalContentHeight)));
                    double scrollRatio = colorMaxScroll > 0 ? colorSwatchesScrollOffset / colorMaxScroll : 0;
                    int thumbY = scrollbarY + (int)(scrollRatio * (scrollbarHeight - thumbHeight));
                    
                    if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                        // Clicked on thumb - start dragging
                        isDraggingColorSwatchesScrollbar = true;
                        colorSwatchesScrollbarDragStartY = (int)mouseY;
                        colorSwatchesScrollbarDragStartOffset = colorSwatchesScrollOffset;
                        return true;
                    } else {
                        // Clicked on track - jump to that position
                        double clickRatio = (mouseY - scrollbarY) / (double)scrollbarHeight;
                        colorSwatchesScrollOffset = Math.max(0, Math.min(colorMaxScroll, clickRatio * colorMaxScroll));
                        colorSwatchesScrollbarDragStartOffset = colorSwatchesScrollOffset;
                        colorSwatchesScrollbarDragStartY = (int)mouseY;
                        updateColorSwatchesPositions(); // Update widget positions
                        return true;
                    }
                }
            }
        }
        
        // Handle color swatch button clicks - temporarily make visible for mouse event
        if (colorSwatchButtons != null) {
            for (int i = 0; i < colorSwatchButtons.size(); i++) {
                net.minecraft.client.gui.components.Button swatch = colorSwatchButtons.get(i);
                boolean wasVisible = swatch.visible;
                swatch.visible = true;
                if (swatch.mouseClicked(mouseX, mouseY, button)) {
                    swatch.visible = wasVisible;
                    onColorSwatchClicked(i);
                    return true;
                }
                swatch.visible = wasVisible;
            }
        }
        
        // Handle hex field clicks
            if (colorHexFields != null) {
                for (EditBox hexField : colorHexFields) {
                if (hexField.mouseClicked(mouseX, mouseY, button)) {
                        activeDraggingPicker = null; // Clear any active dragging
                        return true;
                    }
                }
            }
            
        // Handle shared color picker clicks (for dragging) - call directly like legacy version
        // NO selectedColorIndex check - just call it if it exists
        if (sharedColorPicker != null) {
            // Make visible temporarily for event handling
            boolean wasVisible = sharedColorPicker.visible;
            sharedColorPicker.visible = true;
            if (sharedColorPicker.mouseClicked(mouseX, mouseY, button)) {
                sharedColorPicker.visible = wasVisible;
                activeDraggingPicker = sharedColorPicker; // Track which picker started dragging - CRITICAL for dragging to work
                        return true;
                    }
            sharedColorPicker.visible = wasVisible;
        }
        
        // Handle usePatternToggle button clicks - temporarily make visible for mouse event
        if (usePatternToggle != null) {
            boolean wasVisible = usePatternToggle.visible;
            usePatternToggle.visible = true;
            if (usePatternToggle.mouseClicked(mouseX, mouseY, button)) {
                usePatternToggle.visible = wasVisible;
            activeDraggingPicker = null;
                return true;
            }
            usePatternToggle.visible = wasVisible;
        }
        
        // Handle patternSelector button clicks - temporarily make visible for mouse event
        if (patternSelector != null && patternSelector.active) {
            boolean wasVisible = patternSelector.visible;
            patternSelector.visible = true;
            if (patternSelector.mouseClicked(mouseX, mouseY, button)) {
                patternSelector.visible = wasVisible;
            activeDraggingPicker = null;
                return true;
            }
            patternSelector.visible = wasVisible;
        }
        
        // Handle edit box clicks - temporarily make visible for mouse event
        if (particleSpeedField != null) {
            boolean wasVisible = particleSpeedField.visible;
            particleSpeedField.visible = true;
        if (particleSpeedField.mouseClicked(mouseX, mouseY, button)) {
                particleSpeedField.visible = wasVisible;
                activeDraggingPicker = null;
            return true;
        }
            particleSpeedField.visible = wasVisible;
        }
        if (particleSpreadField != null) {
            boolean wasVisible = particleSpreadField.visible;
            particleSpreadField.visible = true;
        if (particleSpreadField.mouseClicked(mouseX, mouseY, button)) {
                particleSpreadField.visible = wasVisible;
            activeDraggingPicker = null;
            return true;
        }
            particleSpreadField.visible = wasVisible;
        }
        if (particleLifetimeField != null) {
            boolean wasVisible = particleLifetimeField.visible;
            particleLifetimeField.visible = true;
        if (particleLifetimeField.mouseClicked(mouseX, mouseY, button)) {
                particleLifetimeField.visible = wasVisible;
            activeDraggingPicker = null;
            return true;
        }
            particleLifetimeField.visible = wasVisible;
        }
        if (particleDensityField != null) {
            boolean wasVisible = particleDensityField.visible;
            particleDensityField.visible = true;
        if (particleDensityField.mouseClicked(mouseX, mouseY, button)) {
                particleDensityField.visible = wasVisible;
            activeDraggingPicker = null;
            return true;
        }
            particleDensityField.visible = wasVisible;
        }
        if (patternSpeedField != null) {
            boolean wasVisible = patternSpeedField.visible;
            patternSpeedField.visible = true;
        if (patternSpeedField.mouseClicked(mouseX, mouseY, button)) {
                patternSpeedField.visible = wasVisible;
            activeDraggingPicker = null;
            return true;
        }
            patternSpeedField.visible = wasVisible;
        }
        if (patternSpreadField != null) {
            boolean wasVisible = patternSpreadField.visible;
            patternSpreadField.visible = true;
        if (patternSpreadField.mouseClicked(mouseX, mouseY, button)) {
                patternSpreadField.visible = wasVisible;
            activeDraggingPicker = null;
            return true;
        }
            patternSpreadField.visible = wasVisible;
        }
        if (patternIntensityField != null) {
            boolean wasVisible = patternIntensityField.visible;
            patternIntensityField.visible = true;
        if (patternIntensityField.mouseClicked(mouseX, mouseY, button)) {
                patternIntensityField.visible = wasVisible;
            activeDraggingPicker = null;
            return true;
            }
            patternIntensityField.visible = wasVisible;
        }
        
        // Handle slider clicks - temporarily make visible for mouse event
        if (maxParticleColorSlider != null && maxParticleColorSlider.active) {
            // Check if mouse is over slider bounds manually (since widget might be hidden)
            if (mouseX >= maxParticleColorSlider.x && mouseX <= maxParticleColorSlider.x + maxParticleColorSlider.getWidth() &&
                mouseY >= maxParticleColorSlider.y && mouseY <= maxParticleColorSlider.y + maxParticleColorSlider.getHeight()) {
                boolean wasVisible = maxParticleColorSlider.visible;
                maxParticleColorSlider.visible = true;
            if (maxParticleColorSlider.mouseClicked(mouseX, mouseY, button)) {
                    maxParticleColorSlider.visible = wasVisible;
                    isDraggingSlider = true;
                    activeDraggingPicker = null;
                return true;
            }
                maxParticleColorSlider.visible = wasVisible;
            }
        }
        
        // If we get here, we clicked somewhere that doesn't handle it
        // Clear any active dragging state
        activeDraggingPicker = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // PRIORITY: Handle shared color picker dragging FIRST - call directly like legacy version
        // NO visibility checks, NO selectedColorIndex checks - just call it if it exists
        // The picker itself will check if it's being dragged and return true/false
        // CRITICAL: Always call mouseDragged if picker exists - it will handle its own state
        if (sharedColorPicker != null) {
            // Make visible temporarily for event handling
            boolean wasVisible = sharedColorPicker.visible;
            sharedColorPicker.visible = true;
            // ALWAYS call mouseDragged - it will return true if dragging, false otherwise
            boolean result = sharedColorPicker.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            sharedColorPicker.visible = wasVisible;
            if (result) {
                activeDraggingPicker = sharedColorPicker; // Track for reference
                return true; // Return immediately if dragging
            }
        }
        
        // Handle scrollbar dragging for Default Properties panel
        if (isDraggingDefaultScrollbar && button == 0) {
            int padding = 10;
            int titleHeight = 20;
            int availableHeight = defaultBoxHeight - padding * 2;
            int fieldHeight = 20;
            int fieldSpacing = 10; // Reduced spacing between fields
            int buttonHeight = 20;
            int numFields = 4;
            int buttonToFieldSpacing = 15;
            int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + (numFields * fieldHeight) + ((numFields - 1) * fieldSpacing);
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);
            
            if (maxScroll > 0) {
                int scrollbarWidth = 10;
                int scrollbarX = defaultBoxX + defaultBoxWidth - scrollbarWidth; // Scrollbar at edge
                int scrollbarY = defaultBoxY + padding + titleHeight;
                int scrollbarHeight = availableHeight;
                
                // Calculate thumb height
                int thumbHeight = Math.max(20, (int)(scrollbarHeight * (availableHeight / (double)totalContentHeight)));
                int usableTrackHeight = scrollbarHeight - thumbHeight;
                
                // Map mouse Y position to scroll position
                double clampedMouseY = Math.max(scrollbarY, Math.min(scrollbarY + scrollbarHeight, mouseY));
                double mouseYRelative = clampedMouseY - scrollbarY;
                double thumbCenterRatio = usableTrackHeight > 0 ? 
                    Math.max(0, Math.min(1, (mouseYRelative - thumbHeight / 2.0) / usableTrackHeight)) : 0;
                
                defaultPropertiesScrollOffset = thumbCenterRatio * maxScroll;
                defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, defaultPropertiesScrollOffset));
                updateDefaultPropertiesPositions(); // Update widget positions
                return true;
            }
        }
        
        // Handle scrollbar dragging for Pattern Properties panel
        if (isDraggingPatternScrollbar && button == 0) {
            int padding = 10;
            int titleHeight = 20;
            int availableHeight = patternBoxHeight - padding * 2;
            int fieldHeight = 20;
            int fieldSpacing = 10;
            int buttonHeight = 20;
            int buttonToFieldSpacing = 15;
            int sliderHeight = 20;
            int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + 
                (3 * fieldHeight) + (2 * fieldSpacing) + sliderHeight + fieldSpacing;
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);
            
            if (maxScroll > 0) {
                int scrollbarWidth = 10;
                int scrollbarX = patternBoxX + patternBoxWidth - scrollbarWidth; // Scrollbar at edge
                int scrollbarY = patternBoxY + padding + titleHeight;
                int scrollbarHeight = availableHeight;
                
                // Calculate thumb height
                int thumbHeight = Math.max(20, (int)(scrollbarHeight * (availableHeight / (double)totalContentHeight)));
                int usableTrackHeight = scrollbarHeight - thumbHeight;
                
                // Map mouse Y position to scroll position
                double clampedMouseY = Math.max(scrollbarY, Math.min(scrollbarY + scrollbarHeight, mouseY));
                double mouseYRelative = clampedMouseY - scrollbarY;
                double thumbCenterRatio = usableTrackHeight > 0 ? 
                    Math.max(0, Math.min(1, (mouseYRelative - thumbHeight / 2.0) / usableTrackHeight)) : 0;
                
                patternPropertiesScrollOffset = thumbCenterRatio * maxScroll;
                patternPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, patternPropertiesScrollOffset));
                updatePatternPropertiesPositions(); // Update widget positions
                    return true;
                }
            }
        
        // Handle scrollbar dragging for Color Swatches panel
        if (isDraggingColorSwatchesScrollbar && button == 0) {
            int padding = 10;
            int titleHeight = 0; // No title anymore
            int availableHeight = colorBoxHeight - padding * 2;
            int swatchSize = 20;
            int rowSpacing = 4; // Matching user's changes
            int numSwatches = 7;
            int totalContentHeight = (numSwatches * swatchSize) + ((numSwatches - 1) * rowSpacing); // No title anymore
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);
            
            if (maxScroll > 0) {
                int scrollbarWidth = 10;
                int scrollbarOffset = 3; // Gap between components and scrollbar (2-3 pixels)
                int scrollbarX = colorBoxX + colorBoxWidth - scrollbarWidth - scrollbarOffset; // Scrollbar with offset from edge
                int scrollbarY = colorBoxY + padding;
                int scrollbarHeight = availableHeight;
                
                // Calculate thumb height
                int thumbHeight = Math.max(20, (int)(scrollbarHeight * (availableHeight / (double)totalContentHeight)));
                int usableTrackHeight = scrollbarHeight - thumbHeight;
                
                // Map mouse Y position to scroll position
                double clampedMouseY = Math.max(scrollbarY, Math.min(scrollbarY + scrollbarHeight, mouseY));
                double mouseYRelative = clampedMouseY - scrollbarY;
                double thumbCenterRatio = usableTrackHeight > 0 ? 
                    Math.max(0, Math.min(1, (mouseYRelative - thumbHeight / 2.0) / usableTrackHeight)) : 0;
                
                colorSwatchesScrollOffset = thumbCenterRatio * maxScroll;
                colorSwatchesScrollOffset = Math.max(0, Math.min(maxScroll, colorSwatchesScrollOffset));
                updateColorSwatchesPositions(); // Update widget positions
                return true;
            }
        }
        
        // Handle slider dragging - only if we started dragging it (clicked on it first)
        if (isDraggingSlider && maxParticleColorSlider != null && maxParticleColorSlider.active) {
            // Temporarily make visible for mouse event
            boolean wasVisible = maxParticleColorSlider.visible;
            maxParticleColorSlider.visible = true;
            // AbstractSliderButton handles dragging internally, but we need to forward the event
            if (maxParticleColorSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                maxParticleColorSlider.visible = wasVisible;
                return true;
            }
            maxParticleColorSlider.visible = wasVisible;
        }
        
        return false;
    }
    
    // Not overriding Screen methods directly; return false when unhandled
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Handle scrollbar release
        if (isDraggingDefaultScrollbar && button == 0) {
            isDraggingDefaultScrollbar = false;
            return true;
        }
        if (isDraggingColorSwatchesScrollbar && button == 0) {
            isDraggingColorSwatchesScrollbar = false;
            return true;
        }
        if (isDraggingPatternScrollbar && button == 0) {
            isDraggingPatternScrollbar = false;
            return true;
        }
        
        // Handle color picker release first (if we were dragging one)
        if (activeDraggingPicker != null) {
            activeDraggingPicker.mouseReleased(mouseX, mouseY, button);
            activeDraggingPicker = null; // Clear dragging state
            isDraggingSlider = false; // Clear slider dragging state
            return true;
        }
        
        // Handle slider release (only if we were dragging it)
        if (isDraggingSlider && maxParticleColorSlider != null && maxParticleColorSlider.active) {
            boolean wasVisible = maxParticleColorSlider.visible;
            maxParticleColorSlider.visible = true;
            if (maxParticleColorSlider.mouseReleased(mouseX, mouseY, button)) {
                maxParticleColorSlider.visible = wasVisible;
                isDraggingSlider = false; // Clear dragging state
                return true;
            }
            maxParticleColorSlider.visible = wasVisible;
        }
        
        // Handle shared color picker release - call directly like legacy version
        if (sharedColorPicker != null) {
            // Make visible temporarily for event handling
            boolean wasVisible = sharedColorPicker.visible;
            sharedColorPicker.visible = true;
            if (sharedColorPicker.mouseReleased(mouseX, mouseY, button)) {
                sharedColorPicker.visible = wasVisible;
                activeDraggingPicker = null; // Clear dragging state
                    return true;
                }
            sharedColorPicker.visible = wasVisible;
        }
        
        // Always clear dragging state on release (if not already cleared above)
        if (activeDraggingPicker != null) {
        activeDraggingPicker = null;
        }
        isDraggingSlider = false;
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle color picker RGB/HSB field key presses - temporarily make visible if focused
        if (sharedColorPicker != null) {
            // Check all RGB/HSB fields in the picker
            if (sharedColorPicker.rField != null && sharedColorPicker.rField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (sharedColorPicker.rField.keyPressed(keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.gField != null && sharedColorPicker.gField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (sharedColorPicker.gField.keyPressed(keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.bField != null && sharedColorPicker.bField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (sharedColorPicker.bField.keyPressed(keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.hField != null && sharedColorPicker.hField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (sharedColorPicker.hField.keyPressed(keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.sField != null && sharedColorPicker.sField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (sharedColorPicker.sField.keyPressed(keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.brightnessField != null && sharedColorPicker.brightnessField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (sharedColorPicker.brightnessField.keyPressed(keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
        }
        
        // Handle hex field key presses - temporarily make visible if focused
        if (colorHexFields != null) {
            for (EditBox hexField : colorHexFields) {
                if (hexField.isFocused()) {
                    boolean wasVisible = hexField.visible;
                    hexField.visible = true;
                    if (hexField.keyPressed(keyCode, scanCode, modifiers)) {
                        hexField.visible = wasVisible;
                        return true;
                    }
                    hexField.visible = wasVisible;
                }
            }
        }
        
        // Handle edit box key presses - temporarily make visible if focused
        if (particleSpeedField != null && particleSpeedField.isFocused()) {
            boolean wasVisible = particleSpeedField.visible;
            particleSpeedField.visible = true;
            if (particleSpeedField.keyPressed(keyCode, scanCode, modifiers)) {
                particleSpeedField.visible = wasVisible;
                return true;
            }
            particleSpeedField.visible = wasVisible;
        }
        if (particleSpreadField != null && particleSpreadField.isFocused()) {
            boolean wasVisible = particleSpreadField.visible;
            particleSpreadField.visible = true;
            if (particleSpreadField.keyPressed(keyCode, scanCode, modifiers)) {
                particleSpreadField.visible = wasVisible;
                return true;
            }
            particleSpreadField.visible = wasVisible;
        }
        if (particleLifetimeField != null && particleLifetimeField.isFocused()) {
            boolean wasVisible = particleLifetimeField.visible;
            particleLifetimeField.visible = true;
            if (particleLifetimeField.keyPressed(keyCode, scanCode, modifiers)) {
                particleLifetimeField.visible = wasVisible;
                return true;
            }
            particleLifetimeField.visible = wasVisible;
        }
        if (particleDensityField != null && particleDensityField.isFocused()) {
            boolean wasVisible = particleDensityField.visible;
            particleDensityField.visible = true;
            if (particleDensityField.keyPressed(keyCode, scanCode, modifiers)) {
                particleDensityField.visible = wasVisible;
                return true;
            }
            particleDensityField.visible = wasVisible;
        }
        if (patternSpeedField != null && patternSpeedField.isFocused()) {
            boolean wasVisible = patternSpeedField.visible;
            patternSpeedField.visible = true;
            if (patternSpeedField.keyPressed(keyCode, scanCode, modifiers)) {
                patternSpeedField.visible = wasVisible;
                return true;
            }
            patternSpeedField.visible = wasVisible;
        }
        if (patternSpreadField != null && patternSpreadField.isFocused()) {
            boolean wasVisible = patternSpreadField.visible;
            patternSpreadField.visible = true;
            if (patternSpreadField.keyPressed(keyCode, scanCode, modifiers)) {
                patternSpreadField.visible = wasVisible;
                return true;
            }
            patternSpreadField.visible = wasVisible;
        }
        if (patternIntensityField != null && patternIntensityField.isFocused()) {
            boolean wasVisible = patternIntensityField.visible;
            patternIntensityField.visible = true;
            if (patternIntensityField.keyPressed(keyCode, scanCode, modifiers)) {
                patternIntensityField.visible = wasVisible;
                return true;
            }
            patternIntensityField.visible = wasVisible;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Handle scrolling for Default Properties panel
        if (mouseX >= defaultBoxX && mouseX <= defaultBoxX + defaultBoxWidth &&
            mouseY >= defaultBoxY && mouseY <= defaultBoxY + defaultBoxHeight) {
            int padding = 10;
            int titleHeight = 20;
            int availableHeight = defaultBoxHeight - padding * 2;
            int fieldHeight = 20;
            int fieldSpacing = 10; // Reduced spacing between fields
            int buttonHeight = 20;
            int numFields = 4;
            int buttonToFieldSpacing = 15;
            int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + (numFields * fieldHeight) + ((numFields - 1) * fieldSpacing);
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);
            
            if (maxScroll > 0) {
                defaultPropertiesScrollOffset -= delta * 10; // Scroll speed
                defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, defaultPropertiesScrollOffset));
                updateDefaultPropertiesPositions(); // Update widget positions
                return true;
            }
        }
        
        // Handle scrolling for Pattern Properties panel
        if (mouseX >= patternBoxX && mouseX <= patternBoxX + patternBoxWidth &&
            mouseY >= patternBoxY && mouseY <= patternBoxY + patternBoxHeight) {
            int padding = 10;
            int titleHeight = 20;
            int availableHeight = patternBoxHeight - padding * 2;
            int fieldHeight = 20;
            int fieldSpacing = 10;
            int buttonHeight = 20;
            int buttonToFieldSpacing = 15;
            int sliderHeight = 20;
            int totalContentHeight = titleHeight + buttonHeight + buttonToFieldSpacing + 
                (3 * fieldHeight) + (2 * fieldSpacing) + sliderHeight + fieldSpacing;
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);
            
            if (maxScroll > 0) {
                patternPropertiesScrollOffset -= delta * 10; // Scroll speed
                patternPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, patternPropertiesScrollOffset));
                updatePatternPropertiesPositions(); // Update widget positions
                return true;
            }
        }
        
        // Handle scrolling for Color Swatches panel
        if (mouseX >= colorBoxX && mouseX <= colorBoxX + colorBoxWidth &&
            mouseY >= colorBoxY && mouseY <= colorBoxY + colorBoxHeight) {
            int padding = 10;
            int titleHeight = 20;
            int availableHeight = colorBoxHeight - padding * 2;
            int swatchSize = 20;
            int rowSpacing = 4; // Matching user's changes
            int numSwatches = 7;
            int totalContentHeight = titleHeight + 5 + (numSwatches * swatchSize) + ((numSwatches - 1) * rowSpacing);
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);
            
            if (maxScroll > 0) {
                colorSwatchesScrollOffset -= delta * 10; // Scroll speed
                colorSwatchesScrollOffset = Math.max(0, Math.min(maxScroll, colorSwatchesScrollOffset));
                updateColorSwatchesPositions(); // Update widget positions
                return true;
            }
        }
        
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Allow typing in hex fields - temporarily make visible if focused
        if (colorHexFields != null) {
            for (EditBox hexField : colorHexFields) {
                if (hexField.isFocused()) {
                    boolean wasVisible = hexField.visible;
                    hexField.visible = true;
                    if (hexField.charTyped(codePoint, modifiers)) {
                        hexField.visible = wasVisible;
                        return true;
                    }
                    hexField.visible = wasVisible;
                }
            }
        }
        
        // Allow typing in edit boxes - temporarily make visible if focused
        if (particleSpeedField != null && particleSpeedField.isFocused()) {
            boolean wasVisible = particleSpeedField.visible;
            particleSpeedField.visible = true;
            if (particleSpeedField.charTyped(codePoint, modifiers)) {
                particleSpeedField.visible = wasVisible;
                return true;
            }
            particleSpeedField.visible = wasVisible;
        }
        if (particleSpreadField != null && particleSpreadField.isFocused()) {
            boolean wasVisible = particleSpreadField.visible;
            particleSpreadField.visible = true;
            if (particleSpreadField.charTyped(codePoint, modifiers)) {
                particleSpreadField.visible = wasVisible;
                return true;
            }
            particleSpreadField.visible = wasVisible;
        }
        if (particleLifetimeField != null && particleLifetimeField.isFocused()) {
            boolean wasVisible = particleLifetimeField.visible;
            particleLifetimeField.visible = true;
            if (particleLifetimeField.charTyped(codePoint, modifiers)) {
                particleLifetimeField.visible = wasVisible;
                return true;
            }
            particleLifetimeField.visible = wasVisible;
        }
        if (particleDensityField != null && particleDensityField.isFocused()) {
            boolean wasVisible = particleDensityField.visible;
            particleDensityField.visible = true;
            if (particleDensityField.charTyped(codePoint, modifiers)) {
                particleDensityField.visible = wasVisible;
                return true;
            }
            particleDensityField.visible = wasVisible;
        }
        if (patternSpeedField != null && patternSpeedField.isFocused()) {
            boolean wasVisible = patternSpeedField.visible;
            patternSpeedField.visible = true;
            if (patternSpeedField.charTyped(codePoint, modifiers)) {
                patternSpeedField.visible = wasVisible;
                return true;
            }
            patternSpeedField.visible = wasVisible;
        }
        if (patternSpreadField != null && patternSpreadField.isFocused()) {
            boolean wasVisible = patternSpreadField.visible;
            patternSpreadField.visible = true;
            if (patternSpreadField.charTyped(codePoint, modifiers)) {
                patternSpreadField.visible = wasVisible;
                return true;
            }
            patternSpreadField.visible = wasVisible;
        }
        if (patternIntensityField != null && patternIntensityField.isFocused()) {
            boolean wasVisible = patternIntensityField.visible;
            patternIntensityField.visible = true;
            if (patternIntensityField.charTyped(codePoint, modifiers)) {
                patternIntensityField.visible = wasVisible;
                return true;
            }
            patternIntensityField.visible = wasVisible;
        }
        return super.charTyped(codePoint, modifiers);
    }

}

