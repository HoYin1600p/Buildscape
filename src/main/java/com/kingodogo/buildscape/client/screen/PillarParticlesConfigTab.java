package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.client.screen.widget.*;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class PillarParticlesConfigTab extends AbstractConfigTab {

    private static final String[] PATTERNS = {"beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"};

    // Consolidated constants for consistent layout
    private static final int UI_PADDING = 10;
    private static final int TITLE_HEIGHT = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int FIELD_HEIGHT = 14;
    private static final int SLIDER_HEIGHT = 14;
    private static final int COMPONENT_SPACING = 4; // Increased for better clarity
    private static final int BTN_TO_FIELD_SPACING = 8; // More gap between button and fields
    private static final int SCROLLBAR_WIDTH = 8;
    private static final int SCROLLBAR_RIGHT_MARGIN = 5;
    private static final int COMPONENT_SCROLLBAR_GAP = 10;
    
    // Spacing for Color Swatches
    private static final int COLOR_SWATCH_SIZE = 14; // Small and neat
    private static final int COLOR_ROW_SPACING = 6; // Matching user's request for gap
    private static final int COLOR_HEADER_SPACE = 16;
    private static final int HEADER_CLIP = 16; // 5px top + ~9px font + 2px safety gap

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
    private Button colorsResetButton;

    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer defaultScrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();
    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer patternScrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();
    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer colorScrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();

    public PillarParticlesConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    private Component getUsePatternMessage(boolean value) {
        TextComponent base = new TextComponent("");
        base.append(new TextComponent("Use Pattern : ").withStyle(style -> style.withColor(TextColor.fromRgb(0x5555FF)))); // Blue
        if (value) {
            base.append(new TextComponent("True").withStyle(style -> style.withColor(TextColor.fromRgb(0x00FF00)))); // Green
        } else {
            base.append(new TextComponent("False").withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000)))); // Red
        }
        return base;
    }

    private Component getPatternMessage(String pattern) {
        TextComponent base = new TextComponent("");
        base.append(new TextComponent("Pattern : ").withStyle(style -> style.withColor(TextColor.fromRgb(0x5555FF)))); // Blue

        int color = 0xFFFFFF; // Default white
        switch (pattern) {
            case "beam":
                color = 0x00FFFF;
                break; // Cyan
            case "spiral":
                color = 0xFF00FF;
                break; // Magenta
            case "fountain":
                color = 0x00FF00;
                break; // Green
            case "pulse":
                color = 0xFF0000;
                break; // Red
            case "ring":
                color = 0xFFAA00;
                break; // Gold
            case "burst":
                color = 0xFF5555;
                break; // Light Red
            case "snowflake":
                color = 0xA0FFFF;
                break; // Light Cyan
            default:
                color = 0xAAAAAA;
                break;
        }

        // Translate pattern name properly if localized, or just capitalize
        String displayName = pattern.substring(0, 1).toUpperCase() + pattern.substring(1);
        final int finalColor = color;
        try {
            base.append(new TranslatableComponent("buildscape.config.particles.pattern." + pattern).withStyle(style -> style.withColor(TextColor.fromRgb(finalColor))));
        } catch (Exception e) {
            base.append(new TextComponent(displayName).withStyle(style -> style.withColor(TextColor.fromRgb(finalColor))));
        }
        return base;
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
        // Widgets are created once; layout applied via relayout()
        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton usePatternBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0,
                100, 20,
                getUsePatternMessage(config.use_pattern),
                (btn) -> toggleUsePattern());
        // Green text for "Use Pattern true" (cool looking) - handled by TextComponent colors now
        usePatternBtn.setCustomTextColors(0, 0); // Ensure no override so component colors show
        usePatternToggle = usePatternBtn;
        addTabWidget(usePatternToggle);

        int fieldHeight = 14;

        particleSpeedField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                new TranslatableComponent("buildscape.config.particles.particle_speed"));
        particleSpeedField.setValue(String.valueOf(config.particle_speed));
        particleSpeedField.setEditable(!config.use_pattern);
        particleSpeedField.setBordered(true);
        particleSpeedField.setTextColor(0xFFFFFF);
        particleSpeedField.setTextColorUneditable(0xAAAAAA);
        particleSpeedField.setTextColorUneditable(0xAAAAAA);
        particleSpeedField.setMaxLength(64);
        particleSpeedField.setFilter(s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleSpeedField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleSpeedField);

        particleSpreadField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                new TranslatableComponent("buildscape.config.particles.particle_spread"));
        particleSpreadField.setValue(String.valueOf(config.particle_spread));
        particleSpreadField.setEditable(!config.use_pattern);
        particleSpreadField.setBordered(true);
        particleSpreadField.setTextColor(0xFFFFFF);
        particleSpreadField.setTextColorUneditable(0xAAAAAA);
        particleSpreadField.setTextColorUneditable(0xAAAAAA);
        particleSpreadField.setMaxLength(64);
        particleSpreadField.setFilter(s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleSpreadField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleSpreadField);

        particleLifetimeField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                new TranslatableComponent("buildscape.config.particles.particle_lifetime"));
        particleLifetimeField.setValue(String.valueOf(config.particle_lifetime));
        particleLifetimeField.setEditable(!config.use_pattern);
        particleLifetimeField.setBordered(true);
        particleLifetimeField.setTextColor(0xFFFFFF);
        particleLifetimeField.setTextColorUneditable(0xAAAAAA);
        particleLifetimeField.setTextColorUneditable(0xAAAAAA);
        particleLifetimeField.setMaxLength(64);
        particleLifetimeField.setFilter(s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleLifetimeField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleLifetimeField);

        particleDensityField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                new TranslatableComponent("buildscape.config.particles.particle_density"));
        particleDensityField.setValue(String.valueOf(config.particle_density));
        particleDensityField.setEditable(!config.use_pattern);
        particleDensityField.setBordered(true);
        particleDensityField.setTextColor(0xFFFFFF);
        particleDensityField.setTextColorUneditable(0xAAAAAA);
        particleDensityField.setTextColorUneditable(0xAAAAAA);
        particleDensityField.setMaxLength(64);
        particleDensityField.setFilter(s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleDensityField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleDensityField);

        // Color swatches and single shared color picker
        colorSwatchButtons = new ArrayList<>();
        colorHexFields = new ArrayList<>();
        sharedColorPicker = null; // Will be created in relayout

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton patternSelectorBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0,
                100, 20,
                getPatternMessage(config.pattern),
                (btn) -> cyclePattern());
        patternSelectorBtn.setCustomTextColors(0, 0); // Allow component colors
        patternSelector = patternSelectorBtn;
        patternSelector.active = config.use_pattern;
        addTabWidget(patternSelector);

        patternSpeedField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                new TranslatableComponent("buildscape.config.particles.pattern_speed"));
        patternSpeedField.setValue(String.valueOf(config.pattern_speed));
        patternSpeedField.setEditable(config.use_pattern);
        patternSpeedField.setBordered(true);
        patternSpeedField.setTextColor(0xFFFFFF);
        patternSpeedField.setTextColorUneditable(0xAAAAAA);
        patternSpeedField.setTextColorUneditable(0xAAAAAA);
        patternSpeedField.setMaxLength(64);
        patternSpeedField.setFilter(s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        patternSpeedField.setResponder(s -> updateConfigFromFields());
        addTabWidget(patternSpeedField);

        patternSpreadField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                new TranslatableComponent("buildscape.config.particles.pattern_spread"));
        patternSpreadField.setValue(String.valueOf(config.pattern_spread));
        patternSpreadField.setEditable(config.use_pattern);
        patternSpreadField.setBordered(true);
        patternSpreadField.setTextColor(0xFFFFFF);
        patternSpreadField.setTextColorUneditable(0xAAAAAA);
        patternSpreadField.setTextColorUneditable(0xAAAAAA);
        patternSpreadField.setMaxLength(64);
        patternSpreadField.setFilter(s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        patternSpreadField.setResponder(s -> updateConfigFromFields());
        addTabWidget(patternSpreadField);

        patternIntensityField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                new TranslatableComponent("buildscape.config.particles.pattern_intensity"));
        patternIntensityField.setValue(String.valueOf(config.pattern_intensity));
        patternIntensityField.setEditable(config.use_pattern);
        patternIntensityField.setBordered(true);
        patternIntensityField.setTextColor(0xFFFFFF);
        patternIntensityField.setTextColorUneditable(0xAAAAAA);
        patternIntensityField.setTextColorUneditable(0xAAAAAA);
        patternIntensityField.setMaxLength(64);
        patternIntensityField.setFilter(s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        patternIntensityField.setResponder(s -> updateConfigFromFields());
        addTabWidget(patternIntensityField);

        colorsResetButton = new FlatIconButton(0, 0, 20, 20, new TextComponent("\u27F2"), (btn) -> {
            boolean shift = Screen.hasShiftDown();
            boolean ctrl = Screen.hasControlDown();

            if (shift) {
                resetPropertiesToDefault();
                resetColorsToDefault();
            } else if (ctrl) {
                resetPropertiesToDefault();
            } else {
                resetColorsToDefault();
            }
            // Ensure server is synchronized with the new global defaults
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                    new com.kingodogo.buildscape.network.UpdateConfigPacket(PillarParticleConfig.get())
            );
            updateWorldPillars();
        });
        addTabWidget(colorsResetButton);

        maxParticleColorSlider = new IntSliderWidget(
                0, 0,
                120, 14,
                new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor),
                1, 7, currentMaxColor,
                (value) -> onMaxParticleColorChanged(value));
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

    private void resetPropertiesToDefault() {
        PillarParticleConfig config = PillarParticleConfig.get();
        config.particle_speed = 0.02;
        config.particle_spread = 0.1;
        config.particle_lifetime = 20;
        config.particle_density = 2;
        config.use_pattern = true;
        config.pattern = "ring";
        config.pattern_speed = 0.05;
        config.pattern_spread = 0.05;
        config.pattern_intensity = 1.0;
        config.saveProperties();

        // Refresh UI state
        currentPatternIndex = findPatternIndex("ring");
        if (usePatternToggle != null) {
            usePatternToggle.setMessage(getUsePatternMessage(true));
        }
        if (patternSelector != null) {
            patternSelector.setMessage(getPatternMessage("ring"));
        }

        particleSpeedField.setValue("0.02");
        particleSpreadField.setValue("0.1");
        particleLifetimeField.setValue("20");
        particleDensityField.setValue("2");

        patternSpeedField.setValue("0.05");
        patternSpreadField.setValue("0.05");
        patternIntensityField.setValue("1.0");

        updateDefaultPropertiesPositions();
        updatePatternPropertiesPositions();
    }

    private void resetColorsToDefault() {
        PillarParticleConfig config = PillarParticleConfig.get();
        config.particle_color.clear();
        config.particle_color.add("#FFB81C");
        config.particle_color.add("#FFFFFF");
        config.particle_color.add("#FFFF00");
        config.max_particle_color = 3;
        config.saveProperties();

        // Update local state and widgets
        currentMaxColor = 3;
        if (maxParticleColorSlider != null) {
            maxParticleColorSlider.setValue(3);
        }
        // Re-create/Update swatches
        createColorSwatchesAndPicker(config);
        updateColorSwatchesPositions(); // Ensure positions are updated after re-creation
        updateSwatchesEnabledState(); // Ensure enabled state is correct
    }

    private void createColorSwatchesAndPicker(PillarParticleConfig config) {
        int padding = BuildScapeConfigScreen.scaleSize(10);
        int swatchSize = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int swatchSpacing = BuildScapeConfigScreen.scaleSize(5);
        int hexFieldWidth = BuildScapeConfigScreen.scaleSize(80);
        int hexFieldHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int rowSpacing = BuildScapeConfigScreen.scaleSize(25);

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
                    (btn) -> onColorSwatchClicked(colorIndex));
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
                    net.minecraft.network.chat.TextComponent.EMPTY);
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

                        // Prevent feedback loop: don't update picker if the change came FROM the picker
                        if (selectedColorIndex == colorIndex && sharedColorPicker != null && !sharedColorPicker.isDragging()) {
                            sharedColorPicker.setColor(newColor);
                        }
                        
                        onColorChanged(colorIndex, hexText);
                        // Update swatch button color visually
                        updateSwatchButtonColor(colorIndex, newColor);

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
        // Size will be recalculated during render, but set initial size for RGB/HSB
        // sliders
        int pickerX = colorBoxX + padding + swatchSize + hexFieldWidth + swatchSpacing * 2;
        // Moved down by 3 pixels as per request
        int pickerY = colorBoxY + padding + 25 + 3;
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
                });
        sharedColorPicker.setEnabled(config.use_pattern);
        sharedColorPicker.visible = false; // Initially hidden
        addTabWidget(sharedColorPicker);
    }

    private void onColorSwatchClicked(int colorIndex) {
        // Only allow clicking if swatch is enabled (within max range and use_pattern is
        // true)
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

    /**
     * Sets the height of an EditBox via reflection since 1.18.2 EditBox
     * doesn't have a public setHeight method.
     */
    private static void setEditBoxHeight(EditBox editBox, int height) {
        try {
            java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                    .getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(editBox, height);
        } catch (Exception e) {
            // Fallback - ignore
        }
    }

    private int getColorSwatchesTotalHeight() {
        // Space for reset button + Swatches in 2 columns
        int numSwatches = 7;
        int numRows = (numSwatches + 1) / 2; // 4 rows
        return COLOR_HEADER_SPACE + (numRows * (COLOR_SWATCH_SIZE + COLOR_ROW_SPACING));
    }

    private void updateColorSwatchesPositions() {
        int colorTotalContentHeight = getColorSwatchesTotalHeight();
        int colorAvailableHeight = colorBoxHeight - UI_PADDING * 2;
        double maxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);
        colorSwatchesScrollOffset = Math.max(0, Math.min(maxScroll, colorSwatchesScrollOffset));
        int scrollOffsetInt = (int) colorSwatchesScrollOffset;

        int availableWidth = colorBoxWidth - UI_PADDING * 2;
        int colSpacing = 12; // More space between columns
        int colWidth = (availableWidth - colSpacing) / 2;
        
        int leftX = colorBoxX + UI_PADDING;
        int rightX = leftX + colWidth + colSpacing;
        int hexGap = 3;
        int hexWidth = colWidth - COLOR_SWATCH_SIZE - hexGap;

        if (colorSwatchButtons != null && colorHexFields != null) {
            for (int i = 0; i < Math.min(colorSwatchButtons.size(), colorHexFields.size()); i++) {
                int col = i % 2;
                int row = i / 2;
                int startX = (col == 0) ? leftX : rightX;
                
                int y = colorBoxY + UI_PADDING + COLOR_HEADER_SPACE + row * (COLOR_SWATCH_SIZE + COLOR_ROW_SPACING) - scrollOffsetInt;

                ColorSwatchButton btn = colorSwatchButtons.get(i);
                btn.x = startX;
                btn.y = y;
                btn.setWidth(COLOR_SWATCH_SIZE);

                EditBox hex = colorHexFields.get(i);
                hex.x = startX + COLOR_SWATCH_SIZE + hexGap;
                hex.y = y + (COLOR_SWATCH_SIZE - FIELD_HEIGHT) / 2;
                hex.setWidth(hexWidth);
            }
        }
    }

    // Box positions and sizes (stored for consistent rendering)
    // Middle panel (44% width): Top 50% (Default Properties), Bottom 50% (Pattern
    // Properties)
    private int defaultBoxX, defaultBoxY, defaultBoxWidth, defaultBoxHeight;
    private int patternBoxX, patternBoxY, patternBoxWidth, patternBoxHeight;
    // Right panel (44% width): Top 50% (Color Swatches), Bottom 50% (Color Selector
    // and Max Particles)
    private int colorBoxX, colorBoxY, colorBoxWidth, colorBoxHeight;

    // Track last layout dimensions to avoid unnecessary relayouts
    private int lastContentX = -1, lastContentY = -1, lastContentWidth = -1, lastContentHeight = -1;
    private int lastScreenWidth = -1;

    // Scrolling for panels
    private double defaultPropertiesScrollOffset = 0;
    private double colorSwatchesScrollOffset = 0;

    private double patternPropertiesScrollOffset = 0;

    // Base positions for default properties (without scroll offset)
    private int defaultBaseButtonY = 0;
    private int defaultBaseFirstFieldY = 0;

    // Base positions for pattern properties (without scroll offset)
    private int patternBaseButtonY = 0;
    private int patternBaseFirstFieldY = 0;

    // Update widget positions with scroll offset applied
    private void updateDefaultPropertiesPositions() {
        int totalContentHeight = getDefaultPropertiesTotalHeight();
        int availableHeight = defaultBoxHeight - UI_PADDING * 2;
        double maxScroll = Math.max(0, totalContentHeight - availableHeight);

        defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, defaultPropertiesScrollOffset));
        int scrollOffsetInt = (int) defaultPropertiesScrollOffset;

        particleSpeedField.y = defaultBaseFirstFieldY - scrollOffsetInt;
        particleSpreadField.y = defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) - scrollOffsetInt;
        particleLifetimeField.y = defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) * 2 - scrollOffsetInt;
        particleDensityField.y = defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) * 3 - scrollOffsetInt;
        usePatternToggle.y = defaultBaseButtonY - scrollOffsetInt;
    }

    private int getDefaultPropertiesTotalHeight() {
        // Space for header clip + button + gap + fields
        return HEADER_CLIP + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING + (4 * FIELD_HEIGHT) + (3 * COMPONENT_SPACING);
    }

    private int getPatternPropertiesTotalHeight() {
        // Space for header clip + button + gap + 4 items (Slider + 3 Fields)
        return HEADER_CLIP + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING + (4 * FIELD_HEIGHT) + (3 * COMPONENT_SPACING);
    }

    // Update pattern properties widget positions with scroll offset applied
    private void updatePatternPropertiesPositions() {
        int patternAvailableHeight = patternBoxHeight - UI_PADDING * 2;
        int patternTotalContentHeight = getPatternPropertiesTotalHeight();
        double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);

        patternPropertiesScrollOffset = Math.max(0, Math.min(patternMaxScroll, patternPropertiesScrollOffset));
        int scrollOffsetInt = (int) patternPropertiesScrollOffset;

        patternSelector.y = patternBaseButtonY - scrollOffsetInt;
        int currentY = patternBaseFirstFieldY - scrollOffsetInt;

        maxParticleColorSlider.y = currentY;
        currentY += FIELD_HEIGHT + COMPONENT_SPACING;

        patternSpeedField.y = currentY;
        currentY += FIELD_HEIGHT + COMPONENT_SPACING;

        patternSpreadField.y = currentY;
        currentY += FIELD_HEIGHT + COMPONENT_SPACING;

        patternIntensityField.y = currentY;
    }

    /**
     * Recomputes positions/sizes for all widgets based on current content area and
     * GUI scale.
     * Layout: 11% sidebar + 44% middle + 1% gap + 44% right (all from full screen
     * width)
     * Middle panel: Top 50% (Default Properties), Bottom 50% (Pattern Properties)
     * Right panel: Top 50% (Color Pickers), Bottom 50% (reserved)
     */
    private void relayout(int contentX, int contentY, int contentWidth, int contentHeight) {
        int padding = 10; // Internal padding within boxes

        int screenHeight = parent.height;
        int middleGap = parent.getVerticalPanelGap(); // 0.5% consistent gap between panels
        int fullContentHeight = contentHeight;

        // Split for left side (Two panels with middle gap)
        int sectionHeight = (fullContentHeight - middleGap) / 2;

        int topY = contentY;

        // Middle panel - Top 50%: Default Properties
        defaultBoxX = parent.getContentX();
        defaultBoxY = topY;
        defaultBoxWidth = parent.getContentWidth();
        defaultBoxHeight = sectionHeight;

        // Middle panel - Bottom 50%: Pattern Properties
        patternBoxX = parent.getContentX();
        patternBoxY = topY + sectionHeight + middleGap;
        patternBoxWidth = parent.getContentWidth();
        patternBoxHeight = fullContentHeight - (sectionHeight + middleGap); // Ensure bottoms perfectly align flush

        // Right panel - Matches combined height of both middle panels + gap
        colorBoxX = parent.getRightPanelX();
        colorBoxY = topY;
        colorBoxWidth = parent.getRightPanelWidth();
        colorBoxHeight = defaultBoxHeight + middleGap + patternBoxHeight; 

        colorsResetButton.x = colorBoxX + colorBoxWidth - 20 - 2;
        colorsResetButton.y = colorBoxY + 2;
        // Right panel - Bottom section no longer used (color picker is now in top
        // panel)

        // Layout Middle Top: Default Properties (within defaultBox bounds)
        // Calculate all positions dynamically based on panel dimensions to ensure
        // everything fits
        int defaultTextX = defaultBoxX + padding;
        int labelWidth = 115; // Decreased to make value boxes wider
        int fieldX = defaultTextX + labelWidth - 3; // Start fields earlier (overlap slightly with label end for tighter
                                                    // layout)

        // Calculate vertical layout - use fixed spacing, enable scrolling if needed
        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int fieldHeight = 14;
        int titleHeight = 20; // Space for "Default Properties" title
        int buttonHeight = 20;
        int numFields = 4; // Particle Speed, Spread, Lifetime, Density
        int fieldSpacing = 2;

        // Calculate total content height needed
        int totalContentHeightDefault = getDefaultPropertiesTotalHeight();
        int defaultPanelAvailableHeight = defaultBoxHeight - padding * 2;

        // Always reserve space for scrollbar to prevent fields from overlapping it
        boolean needsScrollbarDefault = totalContentHeightDefault > defaultPanelAvailableHeight;

        // Calculate end position: if scrollbar exists, end before scrollbar with
        // offset, otherwise use full width
        int componentEndX;
        if (needsScrollbarDefault) {
            // Components end before the scrollbar with offset (scrollbar starts at panel
            // edge - scrollbarWidth)
            componentEndX = defaultBoxX + defaultBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN - COMPONENT_SCROLLBAR_GAP;
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
        if (buttonWidth < 1)
            buttonWidth = 1; // Minimum button width

        // Calculate field width - fields MUST end exactly where button ends (at
        // componentEndX)
        // Button ends at: buttonStartX + buttonWidth = componentEndX
        // Fields should end at: componentEndX (same as button)
        int fieldWidth = componentEndX - fieldX;
        // CRITICAL: Ensure fieldWidth never exceeds what it should be
        if (fieldWidth < 0)
            fieldWidth = 0;
        // Ensure field + width never exceeds componentEndX
        if (fieldX + fieldWidth > componentEndX) {
            fieldWidth = componentEndX - fieldX;
            if (fieldWidth < 0)
                fieldWidth = 0;
        }

        // Final verification: both button and fields end at componentEndX
        // Button end: buttonStartX + buttonWidth = componentEndX ✓
        // Field end: fieldX + fieldWidth = componentEndX ✓

        // Store base positions (without scroll offset)
        // Position buttons exactly at the header clip for a tighter look
        defaultBaseButtonY = defaultBoxY + HEADER_CLIP; 
        defaultBaseFirstFieldY = defaultBaseButtonY + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING;

        // Set widget X positions and widths (these don't change with scrolling)
        // FINAL SAFETY CHECK: Ensure fieldWidth never exceeds componentEndX
        int finalFieldWidth = Math.min(fieldWidth, componentEndX - fieldX);
        if (finalFieldWidth < 0)
            finalFieldWidth = 0;

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
        int patternLabelWidth = 115; // Decreased to make value boxes wider
        // Use a dynamic gap based on screen height to separate text and values (requested 0.2%)
        int dynamicGap = (int) (screenHeight * 0.002);
        int patternFieldX = patternTextX + patternLabelWidth + dynamicGap;

        // Define pattern properties constants first
        int patternFieldSpacing = 2; // Reduced spacing between fields (matching user's changes)
        int patternTitleHeight = 20;
        int patternButtonHeight = 20;
        int patternButtonToFieldSpacing = 5 + dynamicGap; // Reduced spacing between button and first field (matching user's changes) + dynamic gap

        // Calculate field width based on available space in panel - ensure it doesn't
        // exceed panel
        // Always reserve space for scrollbar to prevent fields from overlapping it
        int patternScrollbarWidth = 13; // 8px width + 5px padding
        int patternScrollbarOffset = 10; // Increased Gap between components and scrollbar (10 pixels)

        // Calculate if scrollbar is needed for pattern properties
        int patternTotalContentHeight = getPatternPropertiesTotalHeight();
        int patternAvailableHeight = patternBoxHeight - padding * 2;
        boolean needsPatternScrollbar = patternTotalContentHeight > patternAvailableHeight;

        // Calculate end position: if scrollbar exists, end before scrollbar with
        // offset, otherwise use full width
        int patternComponentEndX;
        if (needsPatternScrollbar) {
            // Components end before the scrollbar with offset
            patternComponentEndX = patternBoxX + patternBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN - COMPONENT_SCROLLBAR_GAP;
        } else {
            // No scrollbar, use full width minus padding
            patternComponentEndX = patternBoxX + patternBoxWidth - padding;
        }

        // Pattern selector button should start at label start and end at component end
        int patternButtonStartX = patternTextX;
        // Calculate button width - button ends exactly at patternComponentEndX
        int patternButtonWidth = patternComponentEndX - patternButtonStartX;
        if (patternButtonWidth < 1)
            patternButtonWidth = 1; // Minimum button width

        // Pattern Properties - position button exactly at header clip
        patternBaseButtonY = patternBoxY + HEADER_CLIP;
        patternBaseFirstFieldY = patternBaseButtonY + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING;

        // Calculate field width - fields MUST end exactly where button ends (at
        // patternComponentEndX)
        // Button ends at: patternButtonStartX + patternButtonWidth =
        // patternComponentEndX
        // Fields should end at: patternComponentEndX (same as button)
        int patternFieldWidth = patternComponentEndX - patternFieldX;
        // CRITICAL: Ensure patternFieldWidth never exceeds what it should be
        if (patternFieldWidth < 0)
            patternFieldWidth = 0;
        // Ensure field + width never exceeds patternComponentEndX
        if (patternFieldX + patternFieldWidth > patternComponentEndX) {
            patternFieldWidth = patternComponentEndX - patternFieldX;
            if (patternFieldWidth < 0)
                patternFieldWidth = 0;
        }

        // Final verification: both button and fields end at patternComponentEndX
        // Button end: patternButtonStartX + patternButtonWidth = patternComponentEndX ✓
        // Field end: patternFieldX + patternFieldWidth = patternComponentEndX ✓

        patternSelector.x = patternButtonStartX;
        patternSelector.setWidth(patternButtonWidth);

        // FINAL SAFETY CHECK: Ensure patternFieldWidth never exceeds
        // patternComponentEndX
        int finalPatternFieldWidth = Math.min(patternFieldWidth, patternComponentEndX - patternFieldX);
        if (finalPatternFieldWidth < 0)
            finalPatternFieldWidth = 0;

        // Order: Pattern Selector, Max Particles (second), Pattern Speed, Pattern
        // Spread, Pattern Intensity
        // Positions are updated in updatePatternPropertiesPositions below
        maxParticleColorSlider.x = patternFieldX;
        maxParticleColorSlider.setWidth(finalPatternFieldWidth);

        patternSpeedField.x = patternFieldX;
        patternSpeedField.setWidth(finalPatternFieldWidth);

        patternSpreadField.x = patternFieldX;
        patternSpreadField.setWidth(finalPatternFieldWidth);

        patternIntensityField.x = patternFieldX;
        patternIntensityField.setWidth(finalPatternFieldWidth);

        // Calculate total content height for pattern box to determine if scrolling is
        // needed
        // Note: patternTotalContentHeight and patternAvailableHeight are already
        // calculated above in relayout
        // Recalculate with slider included for render method

        // Start of pattern properties position update - already set above
        updatePatternPropertiesPositions();

        // Layout Right Panel: Color Selector (sharedColorPicker) - now in top right
        // panel
        // Position shared color picker in the colorBox (top right panel, now full
        // height)
        // Always position it, even if not visible, so it's ready when a swatch is
        // clicked
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

        // Sync color changes to server
        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(new com.kingodogo.buildscape.network.UpdateConfigPacket(config));
        
        updateWorldPillars();
    }

    private void updateWorldPillars() {
        Minecraft mc = Minecraft.getInstance();
        PillarIdManager manager = PillarIdManager.get();
        if (mc.level != null && mc.player != null) {
            int renderDistance = mc.options.renderDistance; // Try field access first, widespread in 1.16-1.18
            // If it's 1.19+, it might be renderDistance().get(). But usually 'renderDistance' works or we can guess 32.
            // Safe fallback:
            int range = 32;

            net.minecraft.world.level.ChunkPos center = mc.player.chunkPosition();

            for (int x = center.x - range; x <= center.x + range; x++) {
                for (int z = center.z - range; z <= center.z + range; z++) {
                    if (mc.level.hasChunk(x, z)) {
                        net.minecraft.world.level.chunk.LevelChunk chunk = mc.level.getChunk(x, z);
                        for (BlockEntity be : chunk.getBlockEntities().values()) {
                            if (be instanceof PillarBlockEntity pbe) {
                                // Important: re-sync from manager so that the newly 'locked' 
                                // patterns (for customized pillars) are picked up by the BE immediately.
                                String pid = pbe.getPillarId();
                                if (pid != null) {
                                    PillarIdManager.PillarData data = manager.getPillarData(pid);
                                    if (data != null) {
                                        pbe.syncFromData(data);
                                    }
                                }
                                pbe.resetParticleTick();
                            }
                        }
                    }
                }
            }
        }
    }

    private void toggleUsePattern() {
        PillarParticleConfig config = PillarParticleConfig.get();
        config.use_pattern = !config.use_pattern;
        config.saveProperties();
        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(new com.kingodogo.buildscape.network.UpdateConfigPacket(config));
        updateWorldPillars();

        // Update UI - show "Use Pattern True" or "Use Pattern False" with correct styling
        usePatternToggle.setMessage(getUsePatternMessage(config.use_pattern));
        particleSpeedField.setEditable(!config.use_pattern);
        particleSpreadField.setEditable(!config.use_pattern);
        particleLifetimeField.setEditable(!config.use_pattern);
        particleDensityField.setEditable(!config.use_pattern);
        patternSelector.active = config.use_pattern;
        patternSpeedField.setEditable(config.use_pattern);
        patternSpreadField.setEditable(config.use_pattern);
        patternIntensityField.setEditable(config.use_pattern);

        // Disable/enable color swatches and max particle color slider based on
        // use_pattern
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
        String oldPattern = config.pattern; // Capture old pattern for transition
        config.pattern = pattern;
        config.use_pattern = true; // Instantly enable use_pattern to make sure the cycle takes effect visually
        config.saveProperties();

        // Transition: On the client, proactively lock patterns for any customized pillars
        // so the UI feedback is instant and doesn't flicker to the global pattern.
        PillarIdManager manager = PillarIdManager.get();
        if (manager.hasLoaded()) {
            for (PillarIdManager.PillarData pData : manager.getAllData()) {
                // Robust check for modification: has colors or has hardcoded pattern settings
                boolean hasPatternOverride = pData.pattern != null && !pData.pattern.equals("default");
                boolean isCustomized = pData.hasColors() || hasPatternOverride;

                if (isCustomized && (pData.pattern == null || pData.pattern.equals("default"))) {
                    pData.pattern = oldPattern != null ? oldPattern : "ring"; // Lock to the pattern it was using BEFORE the change
                }
            }
        }

        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(new com.kingodogo.buildscape.network.UpdateConfigPacket(config));

        updateWorldPillars();

        // Update Use Pattern display to reflect it was forced on
        if (usePatternToggle != null) {
            usePatternToggle.setMessage(getUsePatternMessage(true));
        }

        // Use helper method to get styled message
        patternSelector.setMessage(getPatternMessage(pattern));
    }

    private void onMaxParticleColorChanged(int value) {
        currentMaxColor = value;

        PillarParticleConfig config = PillarParticleConfig.get();
        config.max_particle_color = currentMaxColor;
        config.saveProperties();
        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(new com.kingodogo.buildscape.network.UpdateConfigPacket(config));
        updateWorldPillars();

        maxParticleColorSlider.setMessage(
                new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor));

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

        // Also update hex fields - they should be editable when not locked by max
        // particles
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
        // GuiComponent.fill(poseStack, contentX, contentY, contentX + contentWidth,
        // contentY + contentHeight, 0xC0220B0B);

        // Header title removed as requested

        Minecraft mcInstance = Minecraft.getInstance();

        PillarParticleConfig config = PillarParticleConfig.get();
        int padding = 10; // Internal padding within boxes

        // Middle Top: Default Properties - Render with scissor test to clip to panel
        // bounds
        // Scissor coordinates need to account for GUI scale (window pixels, not GUI
        // pixels)
        // Scissor uses window coordinates: X from left, Y from bottom
        double guiScale = mcInstance.getWindow().getGuiScale();
        int windowHeight = mcInstance.getWindow().getHeight();

        // Draw border for Default Properties panel (Always render)
        int borderColor = 0xFF666666;
        GuiComponent.fill(poseStack, defaultBoxX, defaultBoxY, defaultBoxX + defaultBoxWidth, defaultBoxY + 1,
                borderColor); // Top
        GuiComponent.fill(poseStack, defaultBoxX, defaultBoxY + defaultBoxHeight - 1, defaultBoxX + defaultBoxWidth,
                defaultBoxY + defaultBoxHeight, borderColor); // Bottom
        GuiComponent.fill(poseStack, defaultBoxX, defaultBoxY, defaultBoxX + 1, defaultBoxY + defaultBoxHeight,
                borderColor); // Left
        GuiComponent.fill(poseStack, defaultBoxX + defaultBoxWidth - 1, defaultBoxY, defaultBoxX + defaultBoxWidth,
                defaultBoxY + defaultBoxHeight, borderColor); // Right

        // Calculate a bottom offset to prevent content from touching the border (user
        // requested ~1%)
        int bottomOffset = Math.max(5, (int) (windowHeight * 0.01 / guiScale));

        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int textYOffset = (20 - (int)(mcInstance.font.lineHeight * textScale)) / 2 + 1; // Center inside top border padding


        int scissorX = (int) (defaultBoxX * guiScale);
        // Raise the bottom of the scissor box by bottomOffset
        int scissorY = (int) (windowHeight - (defaultBoxY + defaultBoxHeight) * guiScale + bottomOffset * guiScale);
        int scissorWidth = (int) (defaultBoxWidth * guiScale);
        // Clip the top area (header) to prevent scrolling overlap
        int scissorHeight = (int) (defaultBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        if (scissorHeight > 0)
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        // Render labels and fields for default properties - use actual widget positions
        // (already have scroll offset applied)
        int defaultTextX = defaultBoxX + padding;

        int labelYOffset = (FIELD_HEIGHT - (int) (Minecraft.getInstance().font.lineHeight * textScale)) / 2;
        // Calculate total content height and scroll range
        int totalContentHeightDefault = getDefaultPropertiesTotalHeight();
        int availableHeightDefault = defaultBoxHeight - UI_PADDING * 2;
        double maxScrollDefault = Math.max(0, totalContentHeightDefault - availableHeightDefault);
        boolean needsScrollbarDefault = maxScrollDefault > 0;

        // Define header area - nothing should render above this
        int headerBottom = defaultBoxY + HEADER_CLIP; 
        int panelTop = defaultBoxY;
        int panelBottom = defaultBoxY + defaultBoxHeight;

        // Sync component visibility - use actual FIELD_HEIGHT for accurate clipping
        boolean particleSpeedRowVisible = particleSpeedField.y + FIELD_HEIGHT > headerBottom
                && particleSpeedField.y < panelBottom;
        boolean particleSpreadRowVisible = particleSpreadField.y + FIELD_HEIGHT > headerBottom
                && particleSpreadField.y < panelBottom;
        boolean particleLifetimeRowVisible = particleLifetimeField.y + FIELD_HEIGHT > headerBottom
                && particleLifetimeField.y < panelBottom;
        boolean particleDensityRowVisible = particleDensityField.y + FIELD_HEIGHT > headerBottom
                && particleDensityField.y < panelBottom;

        // Render labels and widgets aligned correctly - widgets must be rendered here
        // within scissor test
        // Only render labels if they're below the header
        int particleSpeedLabelY = particleSpeedField.y + labelYOffset;
        if (particleSpeedRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.particle_speed").getString() + " ",
                    defaultTextX / textScale, particleSpeedLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        int particleSpreadLabelY = particleSpreadField.y + labelYOffset;
        if (particleSpreadRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.particle_spread").getString() + " ",
                    defaultTextX / textScale, particleSpreadLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        int particleLifetimeLabelY = particleLifetimeField.y + labelYOffset;
        if (particleLifetimeRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.particle_lifetime").getString() + " ",
                    defaultTextX / textScale, particleLifetimeLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        int particleDensityLabelY = particleDensityField.y + labelYOffset;
        if (particleDensityRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.particle_density").getString() + " ",
                    defaultTextX / textScale, particleDensityLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        // Permanently hide widgets from parent - we render them manually here with
        // scissor
        usePatternToggle.visible = false;
        particleSpeedField.visible = false;
        particleSpreadField.visible = false;
        particleLifetimeField.visible = false;
        particleDensityField.visible = false;

        // Scissor region variables already defined above for visibility checks

        // Hide standard button rendering by not calling super.render or manual fills
        // But we DO need to handle tooltips if we had them. Here we just draw the text.

        // Render button only if below header
        // Render button only if some part of it is in visible area
        if (usePatternToggle.y + BUTTON_HEIGHT > headerBottom && usePatternToggle.y < panelBottom) {
            usePatternToggle.visible = true; // Make visible for rendering
            usePatternToggle.render(poseStack, mouseX, mouseY, partialTick);
            usePatternToggle.visible = false; // Hide again to prevent parent from rendering
        }
        // Render text fields only if their row is visible
        int textPadding = BuildScapeConfigScreen.scaleSize(4);
        int fontHeight = Minecraft.getInstance().font.lineHeight;

        if (particleSpeedRowVisible) {
            particleSpeedField.visible = true; // Make visible for rendering
            particleSpeedField.render(poseStack, mouseX, mouseY, partialTick);
            particleSpeedField.visible = false;
        }
        if (particleSpreadRowVisible) {
            particleSpreadField.visible = true; // Make visible for rendering
            particleSpreadField.render(poseStack, mouseX, mouseY, partialTick);
            particleSpreadField.visible = false;
        }
        if (particleLifetimeRowVisible) {
            particleLifetimeField.visible = true; // Make visible for rendering
            particleLifetimeField.render(poseStack, mouseX, mouseY, partialTick);
            particleLifetimeField.visible = false;
        }
        if (particleDensityRowVisible) {
            particleDensityField.visible = true; // Make visible for rendering
            particleDensityField.render(poseStack, mouseX, mouseY, partialTick);
            particleDensityField.visible = false;
        }

        // Render scrollbar if needed (before disabling scissor so it gets clipped)
        if (needsScrollbarDefault && maxScrollDefault > 0) {
            int scrollbarX = defaultBoxX + defaultBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
            int scrollbarY = defaultBoxY + HEADER_CLIP;
            int scrollbarHeight = defaultBoxHeight - HEADER_CLIP - UI_PADDING;

            double scrollableAreaHeight = totalContentHeightDefault - HEADER_CLIP;
            double visibleAreaHeight = defaultBoxHeight - HEADER_CLIP - UI_PADDING;
            double visibleRatio = Math.min(1.0, visibleAreaHeight / scrollableAreaHeight);
            
            defaultScrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    defaultPropertiesScrollOffset, maxScrollDefault, visibleRatio);
        }

        RenderSystem.disableScissor();

        // Right Top: Color Swatches and Shared Picker - Render with scissor test to
        // clip to panel bounds
        // Draw border for Color Swatches panel (debug mode)
        // Right Top: Color Swatches and Shared Picker - Render with scissor test to
        // clip to panel bounds
        // Draw border for Color Swatches panel (debug mode)
        // Debug border removed as permanent border is drawn below

        scissorX = (int) (colorBoxX * guiScale);
        scissorY = (int) (windowHeight - (colorBoxY + colorBoxHeight) * guiScale + bottomOffset * guiScale);
        scissorWidth = (int) (colorBoxWidth * guiScale);
        // Clip top area for header
        scissorHeight = (int) (colorBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        // Sync border logic with middle panels: draw inside the box dimensions
        int colorBorderColor = 0xFF666666;
        GuiComponent.fill(poseStack, colorBoxX, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + 1, colorBorderColor); // Top
        GuiComponent.fill(poseStack, colorBoxX, colorBoxY + colorBoxHeight - 1, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, colorBorderColor); // Bottom
        GuiComponent.fill(poseStack, colorBoxX, colorBoxY, colorBoxX + 1, colorBoxY + colorBoxHeight, colorBorderColor); // Left
        GuiComponent.fill(poseStack, colorBoxX + colorBoxWidth - 1, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, colorBorderColor); // Right

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        // Debug: Draw panel background to verify panel is visible
        // GuiComponent.fill(poseStack, colorBoxX, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight,
        //         0x40000000);

        // Removed "Custom Properties" title text as requested

        // Ensure swatches are created if they don't exist
        // Ensure swatches are created if they don't exist
        if (colorSwatchButtons == null || colorHexFields == null || colorSwatchButtons.isEmpty()
                || colorHexFields.isEmpty()) {
            createColorSwatchesAndPicker(config);
        }

        // Update color swatches with current colors and selection state
        // Update color swatches with current colors and selection state
        if (colorSwatchButtons != null && colorHexFields != null && colorSwatchButtons.size() > 0
                && colorHexFields.size() > 0) {
            // Update enabled state first (based on max value)
            updateSwatchesEnabledState();

            // Calculate scroll info for color swatches (2 columns layout)
            int colorTotalContentHeight = getColorSwatchesTotalHeight();
            int colorAvailableHeight = colorBoxHeight - UI_PADDING * 2;
            double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);
            boolean colorNeedsScrollbar = colorMaxScroll > 0;

            // Render colors reset button (moved to after scissor)
            colorsResetButton.visible = true; // For click handling

            // Ensure positions are updated (this is critical - must be called after
            // colorBox coordinates are set)
            // Color positions now include header offset, managed in
            // updateColorSwatchesPositions
            updateColorSwatchesPositions();

            // Permanently hide widgets from parent - we render them manually here with
            // scissor
            for (int i = 0; i < colorSwatchButtons.size(); i++) {
                colorSwatchButtons.get(i).visible = false;
            }
            for (int i = 0; i < colorHexFields.size(); i++) {
                colorHexFields.get(i).visible = false;
            }

            // Render swatches and hex fields - always render them, scissor test will clip
            // them
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
                int scrollbarX = colorBoxX + colorBoxWidth - CustomScrollbarRenderer.getScrollbarWidth() - 5; // 5px
                                                                                                              // from
                                                                                                              // edge
                int scrollbarY = colorBoxY + UI_PADDING + COLOR_HEADER_SPACE;
                int scrollbarHeight = colorAvailableHeight - COLOR_HEADER_SPACE;

                double visibleRatio = (double) scrollbarHeight / (colorTotalContentHeight - COLOR_HEADER_SPACE);

                colorScrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                        colorSwatchesScrollOffset, colorMaxScroll, visibleRatio);
            }
        }

        // Render shared color picker if a swatch is selected - hide from parent and
        // render manually
        // Render shared color picker if a swatch is selected - hide from parent and
        // render manually

        RenderSystem.disableScissor();

        // Render colors reset button AFTER scissor (Text style)
        // Render colors reset button AFTER scissor
        if (colorsResetButton.visible) {
            colorsResetButton.render(poseStack, mouseX, mouseY, partialTick);

            if (colorsResetButton.isMouseOver(mouseX, mouseY)) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(new TranslatableComponent("buildscape.config.particles.reset_tooltip"));
                tooltip.add(new TextComponent("Click: Reset Colors"));
                tooltip.add(new TextComponent("Ctrl+Click: Reset Default Properties"));
                tooltip.add(new TextComponent("Shift+Click: Reset All"));
                parent.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
            }
        }

        // Remove old manual rendering code
        poseStack.popPose();
        poseStack.pushPose(); // Balance the popPose call that follows for "Middle Bottom" comment separation if needed, or remove completely. 
        // Actually, just let the flow continue.
        poseStack.popPose();

        // Middle Bottom: Pattern Properties - Render with scissor test to clip to panel
        // bounds
        // Draw border for Pattern Properties panel (debug mode)
        // Middle Bottom: Pattern Properties - Render with scissor test to clip to panel
        // bounds
        // Draw border for Pattern Properties panel (debug mode)
        // Draw border for Pattern Properties panel (Always render)
        int patternBorderColor = 0xFF666666;
        GuiComponent.fill(poseStack, patternBoxX, patternBoxY, patternBoxX + patternBoxWidth, patternBoxY + 1,
                patternBorderColor); // Top
        GuiComponent.fill(poseStack, patternBoxX, patternBoxY + patternBoxHeight - 1, patternBoxX + patternBoxWidth,
                patternBoxY + patternBoxHeight, patternBorderColor); // Bottom
        GuiComponent.fill(poseStack, patternBoxX, patternBoxY, patternBoxX + 1, patternBoxY + patternBoxHeight,
                patternBorderColor); // Left
        GuiComponent.fill(poseStack, patternBoxX + patternBoxWidth - 1, patternBoxY, patternBoxX + patternBoxWidth,
                patternBoxY + patternBoxHeight, patternBorderColor); // Right

        scissorX = (int) (patternBoxX * guiScale);
        // Raise the bottom of the scissor box by bottomOffset
        scissorY = (int) (windowHeight - (patternBoxY + patternBoxHeight) * guiScale + bottomOffset * guiScale);
        scissorWidth = (int) (patternBoxWidth * guiScale);
        scissorHeight = (int) (patternBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        if (scissorHeight > 0)
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        float textScale_pattern = BuildScapeConfigScreen.getStandardTextScale();


        int patternTextX = patternBoxX + padding;

        int patternLabelYOffset = (FIELD_HEIGHT - (int) (Minecraft.getInstance().font.lineHeight * textScale)) / 2;

        // Calculate scroll info
        int patternTotalContentHeightRender = getPatternPropertiesTotalHeight();
        int patternAvailableHeightRender = patternBoxHeight - UI_PADDING * 2;
        double patternMaxScrollRender = Math.max(0, patternTotalContentHeightRender - patternAvailableHeightRender);
        boolean patternNeedsScrollbarRender = patternMaxScrollRender > 0;

        // Define header area - nothing should render above this
        int patternHeaderBottom = patternBoxY + HEADER_CLIP;
        int patternPanelTop = patternBoxY;
        int patternPanelBottom = patternBoxY + patternBoxHeight;

        // Pattern selector button label - only render if below header
        // Use actual widget position which already has scroll offset applied
        // Don't render text behind the button - the button itself will display the
        // pattern name
        // The button text is handled by the button's render method, so we don't need to
        // render it here

        // Sync component visibility - use actual FIELD_HEIGHT for accurate clipping
        // Row 1: Pattern Selector
        boolean patternSelectorVisible = patternSelector.y + BUTTON_HEIGHT > patternHeaderBottom
                && patternSelector.y < patternPanelBottom;

        // Row 2: Max Particles (Label + Slider)
        // Use the slider's Y as reference for the whole row
        boolean maxParticlesRowVisible = maxParticleColorSlider.y + SLIDER_HEIGHT > patternHeaderBottom
                && maxParticleColorSlider.y < patternPanelBottom;

        // Row 3: Pattern Speed (Label + Field)
        boolean patternSpeedRowVisible = patternSpeedField.y + FIELD_HEIGHT > patternHeaderBottom
                && patternSpeedField.y < patternPanelBottom;

        // Row 4: Pattern Spread (Label + Field)
        boolean patternSpreadRowVisible = patternSpreadField.y + FIELD_HEIGHT > patternHeaderBottom
                && patternSpreadField.y < patternPanelBottom;

        // Row 5: Pattern Intensity (Label + Field)
        boolean patternIntensityRowVisible = patternIntensityField.y + FIELD_HEIGHT > patternHeaderBottom
                && patternIntensityField.y < patternPanelBottom;

        // Max Particles label
        int maxParticleLabelY = maxParticleColorSlider.y + patternLabelYOffset;
        if (maxParticlesRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack, "Max Particle's ", patternTextX / textScale, maxParticleLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        // Pattern Speed label
        int patternSpeedLabelY = patternSpeedField.y + patternLabelYOffset;
        if (patternSpeedRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.pattern_speed").getString() + " ",
                    patternTextX / textScale, patternSpeedLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        // Pattern Spread label
        int patternSpreadLabelY = patternSpreadField.y + patternLabelYOffset;
        if (patternSpreadRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.pattern_spread").getString() + " ",
                    patternTextX / textScale, patternSpreadLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        // Pattern Intensity label
        int patternIntensityLabelY = patternIntensityField.y + patternLabelYOffset;
        if (patternIntensityRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.pattern_intensity").getString() + " ",
                    patternTextX / textScale, patternIntensityLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        // Permanently hide widgets from parent - we render them manually here with
        // scissor
        patternSelector.visible = false;
        patternSpeedField.visible = false;
        patternSpreadField.visible = false;
        patternIntensityField.visible = false;
        maxParticleColorSlider.visible = false;

        // Render widgets for pattern properties - render within scissor test
        // only if their row is marked visible
        if (patternSelectorVisible) {
            patternSelector.visible = true; // Make visible for rendering
            patternSelector.render(poseStack, mouseX, mouseY, partialTick);
            patternSelector.visible = false; // Hide again to prevent parent from rendering
        }
        if (maxParticlesRowVisible) {
            maxParticleColorSlider.visible = true; // Make visible for rendering
            maxParticleColorSlider.render(poseStack, mouseX, mouseY, partialTick);
            maxParticleColorSlider.visible = false; // Hide again to prevent parent from rendering
        }


        if (patternSpeedRowVisible) {
            patternSpeedField.visible = true; // Make visible for rendering
            patternSpeedField.render(poseStack, mouseX, mouseY, partialTick);
            patternSpeedField.visible = false;
        }
        if (patternSpreadRowVisible) {
            patternSpreadField.visible = true; // Make visible for rendering
            patternSpreadField.render(poseStack, mouseX, mouseY, partialTick);
            patternSpreadField.visible = false;
        }
        if (patternIntensityRowVisible) {
            patternIntensityField.visible = true; // Make visible for rendering
            patternIntensityField.render(poseStack, mouseX, mouseY, partialTick);
            patternIntensityField.visible = false;
        }

        // Render scrollbar if needed
        if (patternNeedsScrollbarRender && patternMaxScrollRender > 0) {
            int scrollbarX = patternBoxX + patternBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
            int scrollbarY = patternBoxY + HEADER_CLIP;
            int scrollbarHeight = patternBoxHeight - HEADER_CLIP - UI_PADDING;

            double patternScrollableAreaHeight = patternTotalContentHeightRender - HEADER_CLIP;
            double patternVisibleAreaHeight = patternBoxHeight - HEADER_CLIP - UI_PADDING;
            double visibleRatio = Math.min(1.0, patternVisibleAreaHeight / patternScrollableAreaHeight);

            patternScrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    patternPropertiesScrollOffset, patternMaxScrollRender, visibleRatio);
        }

        RenderSystem.disableScissor();

        // Render Panel Titles AFTER all scissors are disabled to ensure they are visible
        float standardScale = BuildScapeConfigScreen.getStandardTextScale();
        
        // Default Properties Title
        poseStack.pushPose();
        poseStack.translate(defaultBoxX + 10, defaultBoxY + 5, 0);
        poseStack.scale(standardScale, standardScale, 1.0f);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.default_properties"), 0, 0, 0xFFFFFF);
        poseStack.popPose();

        // Pattern Properties Title
        poseStack.pushPose();
        poseStack.translate(patternBoxX + 10, patternBoxY + 5, 0);
        poseStack.scale(standardScale, standardScale, 1.0f);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_properties"), 0, 0, 0xFFFFFF);
        poseStack.popPose();

        // Color Swatches Title (Optional, check if needed. Keeping it clean as requested before)
        // If the user wants it, I can add it here.

        // Update config from current fields
        updateConfigFromFields();
    }

    @Override
    public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (sharedColorPicker != null) {
            // Always hide from parent to prevent duplicate rendering
            sharedColorPicker.visible = false;

            // Render manually if a swatch is selected - always render when
            // selectedColorIndex is valid
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
                // Moved down by an additional 15 pixels as per request (was 20 offset in availableY)
                int pickerY = availableY + 15; // Below swatches with extra gap

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

                // Always render the picker - no scissor test here so it floats on top
                poseStack.pushPose();
                poseStack.translate(0, 0, 500);
                sharedColorPicker.visible = true;
                sharedColorPicker.renderButton(poseStack, mouseX, mouseY, partialTick);
                sharedColorPicker.visible = false;
                poseStack.popPose();
            }
        }
    }

    /** Minimum allowed value for all numeric double/float config fields to prevent crashes. */
    private static final double FIELD_MIN_VALUE = 0.001;

    private void updateConfigFromFields() {
        PillarParticleConfig config = PillarParticleConfig.get();
        boolean changed = false;

        // Update default properties
        if (!config.use_pattern) {
            try {
                double speed = Math.max(FIELD_MIN_VALUE, Double.parseDouble(particleSpeedField.getValue()));
                if (config.particle_speed != speed) {
                    config.particle_speed = speed;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }

            try {
                double spread = Math.max(FIELD_MIN_VALUE, Double.parseDouble(particleSpreadField.getValue()));
                if (config.particle_spread != spread) {
                    config.particle_spread = spread;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }

            try {
                int lifetime = Math.max(1, Integer.parseInt(particleLifetimeField.getValue()));
                if (config.particle_lifetime != lifetime) {
                    config.particle_lifetime = lifetime;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }

            try {
                int density = Math.max(1, Integer.parseInt(particleDensityField.getValue()));
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
                double speed = Math.max(FIELD_MIN_VALUE, Double.parseDouble(patternSpeedField.getValue()));
                if (config.pattern_speed != speed) {
                    config.pattern_speed = speed;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }

            try {
                double spread = Math.max(FIELD_MIN_VALUE, Double.parseDouble(patternSpreadField.getValue()));
                if (config.pattern_spread != spread) {
                    config.pattern_spread = spread;
                    changed = true;
                }
            } catch (NumberFormatException e) {
                // Invalid value, ignore
            }

            try {
                double intensity = Math.max(FIELD_MIN_VALUE, Double.parseDouble(patternIntensityField.getValue()));
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

            // Sync field changes to server (speed, spread, intensity, etc.)
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(new com.kingodogo.buildscape.network.UpdateConfigPacket(config));

            updateWorldPillars();
        }
    }

    @Override
    public void onClose() {
        updateConfigFromFields();
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle scrollbar clicks for Default Properties panel
        int totalContentHeight = getDefaultPropertiesTotalHeight();
        // Handle Default Properties scrollbar dragging
        int totalContentHeightDefault = getDefaultPropertiesTotalHeight();
        int availableHeightDefault = defaultBoxHeight - UI_PADDING * 2;
        double maxScrollDefault = Math.max(0, totalContentHeightDefault - availableHeightDefault);

        if (maxScrollDefault > 0 && button == 0) {
            int scrollbarX = defaultBoxX + defaultBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
            int scrollbarY = defaultBoxY + HEADER_CLIP;
            int scrollbarHeight = defaultBoxHeight - HEADER_CLIP - UI_PADDING;
            double visibleRatio = availableHeightDefault / (double) totalContentHeightDefault;

            // Pass content bounds as defaultBox for drag-to-scroll support
            double newOffset = defaultScrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    defaultBoxX, defaultBoxY, defaultBoxWidth, defaultBoxHeight,
                    defaultPropertiesScrollOffset, maxScrollDefault, visibleRatio);

            if (newOffset >= 0) {
                defaultPropertiesScrollOffset = newOffset;
                updateDefaultPropertiesPositions();
                return true;
            }
        }

        // Handle Pattern Properties scrollbar dragging
        int patternTotalContentHeight = getPatternPropertiesTotalHeight();
        int patternAvailableHeight = patternBoxHeight - UI_PADDING * 2;
        double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);

        if (patternMaxScroll > 0 && button == 0) {
            int scrollbarX = patternBoxX + patternBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
            int scrollbarY = patternBoxY + HEADER_CLIP;
            int scrollbarHeight = patternBoxHeight - HEADER_CLIP - UI_PADDING;

            if (scrollbarHeight < 10) scrollbarHeight = 10;
            double visibleRatio = patternAvailableHeight / (double) patternTotalContentHeight;

            double newOffset = patternScrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    patternBoxX, patternBoxY, patternBoxWidth, patternBoxHeight,
                    patternPropertiesScrollOffset, patternMaxScroll, visibleRatio);

            if (newOffset >= 0) {
                patternPropertiesScrollOffset = newOffset;
                updatePatternPropertiesPositions();
                return true;
            }
        }

        // Handle scrollbar clicks for Color Swatches panel
        int colorPadding = 10;
        int colorAvailableHeight = colorBoxHeight - colorPadding * 2;
        int swatchSize = 20;
        int rowSpacing = 4;
        int numSwatches = 7;
        int numRows = (numSwatches + 1) / 2;
        int colorTotalContentHeight = (numRows * swatchSize) + ((numRows - 1) * rowSpacing);
        double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);

        if (colorMaxScroll > 0 && button == 0) {
            int scrollbarX = colorBoxX + colorBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
            int scrollbarY = colorBoxY + UI_PADDING + COLOR_HEADER_SPACE;
            int scrollbarHeight = colorBoxHeight - UI_PADDING * 2 - COLOR_HEADER_SPACE;

            double visibleRatio = colorAvailableHeight / (double) colorTotalContentHeight;

            double newOffset = colorScrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    colorBoxX, colorBoxY, colorBoxWidth, colorBoxHeight,
                    colorSwatchesScrollOffset, colorMaxScroll, visibleRatio);

            if (newOffset >= 0) {
                colorSwatchesScrollOffset = newOffset;
                updateColorSwatchesPositions();
                return true;
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

        // Handle shared color picker clicks (for dragging) - call directly like legacy
        // version
        // NO selectedColorIndex check - just call it if it exists
        if (sharedColorPicker != null) {
            // Make visible temporarily for event handling
            boolean wasVisible = sharedColorPicker.visible;
            sharedColorPicker.visible = true;
            if (sharedColorPicker.mouseClicked(mouseX, mouseY, button)) {
                sharedColorPicker.visible = wasVisible;
                activeDraggingPicker = sharedColorPicker; // Track which picker started dragging - CRITICAL for dragging
                                                          // to work
                return true;
            }
            sharedColorPicker.visible = wasVisible;
        }

        // Handle usePatternToggle button clicks - temporarily make visible for mouse
        // event
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

        // Handle patternSelector button clicks - temporarily make visible for mouse
        // event
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
            if (mouseX >= maxParticleColorSlider.x
                    && mouseX <= maxParticleColorSlider.x + maxParticleColorSlider.getWidth() &&
                    mouseY >= maxParticleColorSlider.y
                    && mouseY <= maxParticleColorSlider.y + maxParticleColorSlider.getHeight()) {
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
        // PRIORITY: Handle shared color picker dragging FIRST - call directly like
        // legacy version
        // NO visibility checks, NO selectedColorIndex checks - just call it if it
        // exists
        // The picker itself will check if it's being dragged and return true/false
        // CRITICAL: Always call mouseDragged if picker exists - it will handle its own
        // state
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

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        int windowHeight = Minecraft.getInstance().getWindow().getHeight();
        int bottomOffset = Math.max(5, (int) (windowHeight * 0.01 / guiScale));

        if (defaultScrollbarRenderer.isDragging()) {
            int totalContentHeight = getDefaultPropertiesTotalHeight();
            int availableHeight = defaultBoxHeight - UI_PADDING * 2;
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);

            if (maxScroll > 0) {
                int scrollbarY = defaultBoxY + HEADER_CLIP;
                int scrollbarHeight = defaultBoxHeight - HEADER_CLIP - UI_PADDING;
                double visibleRatio = availableHeight / (double) totalContentHeight;

                double newOffset = defaultScrollbarRenderer.handleMouseDrag(mouseY, scrollbarY, scrollbarHeight,
                        maxScroll, visibleRatio, 1.0);
                if (newOffset >= 0) {
                    defaultPropertiesScrollOffset = newOffset;
                    updateDefaultPropertiesPositions();
                    return true;
                }
            }
        }

        if (patternScrollbarRenderer.isDragging()) {
            int patternTotalContentHeight = getPatternPropertiesTotalHeight();
            int patternAvailableHeight = patternBoxHeight - UI_PADDING * 2;
            double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);

            if (patternMaxScroll > 0) {
                int scrollbarY = patternBoxY + HEADER_CLIP;
                int scrollbarHeight = patternBoxHeight - HEADER_CLIP - UI_PADDING; 
                double visibleRatio = patternAvailableHeight / (double) patternTotalContentHeight;

                double newOffset = patternScrollbarRenderer.handleMouseDrag(mouseY, scrollbarY, scrollbarHeight,
                        patternMaxScroll, visibleRatio, 1.0);
                if (newOffset >= 0) {
                    patternPropertiesScrollOffset = newOffset;
                    updatePatternPropertiesPositions();
                    return true;
                }
            }
        }

        if (colorScrollbarRenderer.isDragging()) {
            int colorTotalContentHeight = getColorSwatchesTotalHeight();
            int colorAvailableHeight = colorBoxHeight - UI_PADDING * 2;
            double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);

            if (colorMaxScroll > 0) {
                int scrollbarY = colorBoxY + UI_PADDING + COLOR_HEADER_SPACE;
                int scrollbarHeight = colorAvailableHeight - COLOR_HEADER_SPACE;
                double visibleRatio = colorAvailableHeight / (double) colorTotalContentHeight;

                double newOffset = colorScrollbarRenderer.handleMouseDrag(mouseY, scrollbarY, scrollbarHeight,
                        colorMaxScroll, visibleRatio, 1.0);
                if (newOffset >= 0) {
                    colorSwatchesScrollOffset = newOffset;
                    updateColorSwatchesPositions();
                    return true;
                }
            }
        }

        // Handle slider dragging - only if we started dragging it (clicked on it first)
        if (isDraggingSlider && maxParticleColorSlider != null && maxParticleColorSlider.active) {
            // Temporarily make visible for mouse event
            boolean wasVisible = maxParticleColorSlider.visible;
            maxParticleColorSlider.visible = true;
            // AbstractSliderButton handles dragging internally, but we need to forward the
            // event
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
        if (defaultScrollbarRenderer.handleMouseRelease(button))
            return true;
        if (patternScrollbarRenderer.handleMouseRelease(button))
            return true;
        if (colorScrollbarRenderer.handleMouseRelease(button))
            return true;

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
        // Handle color picker RGB/HSB field key presses - temporarily make visible if
        // focused
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
            int totalContentHeight = getDefaultPropertiesTotalHeight();
            int availableHeight = defaultBoxHeight - UI_PADDING * 2;
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);

            if (maxScroll > 0) {
                defaultPropertiesScrollOffset -= delta * 10; // Scroll speed
                defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, defaultPropertiesScrollOffset));
                updateDefaultPropertiesPositions();
                return true;
            }
        }

        // Handle scrolling for Pattern Properties panel
        if (mouseX >= patternBoxX && mouseX <= patternBoxX + patternBoxWidth &&
                mouseY >= patternBoxY && mouseY <= patternBoxY + patternBoxHeight) {
            int patternTotalContentHeight = getPatternPropertiesTotalHeight();
            int patternAvailableHeight = patternBoxHeight - UI_PADDING * 2;
            double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);

            if (patternMaxScroll > 0) {
                patternPropertiesScrollOffset -= delta * 10; // Scroll speed
                patternPropertiesScrollOffset = Math.max(0, Math.min(patternMaxScroll, patternPropertiesScrollOffset));
                updatePatternPropertiesPositions();
                return true;
            }
        }

        // Handle scrolling for Color Swatches panel
        if (mouseX >= colorBoxX && mouseX <= colorBoxX + colorBoxWidth &&
                mouseY >= colorBoxY && mouseY <= colorBoxY + colorBoxHeight) {
            int colorTotalContentHeight = getColorSwatchesTotalHeight();
            int colorAvailableHeight = colorBoxHeight - UI_PADDING * 2;
            double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);

            if (colorMaxScroll > 0) {
                colorSwatchesScrollOffset -= delta * 10; // Scroll speed
                colorSwatchesScrollOffset = Math.max(0, Math.min(colorMaxScroll, colorSwatchesScrollOffset));
                updateColorSwatchesPositions();
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


    @Override
    public String getTabName() {
        return "PillarParticles";
    }
}
