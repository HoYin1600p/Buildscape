package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.*;
import com.kingodogo.buildscape.client.screen.ParticleColorSlots;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;

public class PillarParticlesConfigTab extends AbstractConfigTab {

    private static final String[] PATTERNS = {"beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"};

    private static final int UI_PADDING = 10;
    private static final int TITLE_HEIGHT = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int FIELD_HEIGHT = 14;
    private static final int SLIDER_HEIGHT = 14;
    private static final int COMPONENT_SPACING = 4;
    private static final int BTN_TO_FIELD_SPACING = 8;
    private static final int SCROLLBAR_WIDTH = 8;
    private static final int SCROLLBAR_RIGHT_MARGIN = 5;
    private static final int COMPONENT_SCROLLBAR_GAP = 10;

    private static final int COLOR_SWATCH_SIZE = 14;
    private static final int COLOR_ROW_SPACING = 6;
    private static final int COLOR_HEADER_SPACE = 16;
    private static final int HEADER_CLIP = 16;

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
    private ColorPickerWidget sharedColorPicker;
    private List<ColorSwatchButton> colorSwatchButtons;
    private List<EditBox> colorHexFields;
    private int currentPatternIndex = 0;
    private int currentMaxColor = 7;
    private int selectedColorIndex = -1;
    private ColorPickerWidget activeDraggingPicker = null;
    private boolean isDraggingSlider = false;
    private Button colorsResetButton;

    private final CustomScrollbarRenderer defaultScrollbarRenderer = new CustomScrollbarRenderer();
    private final CustomScrollbarRenderer patternScrollbarRenderer = new CustomScrollbarRenderer();
    private final CustomScrollbarRenderer colorScrollbarRenderer = new CustomScrollbarRenderer();

    public PillarParticlesConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    private Component getUsePatternMessage(boolean value) {
        MutableComponent base = ComponentHelper.literal("");
        base.append(ComponentHelper.literal("Use Pattern : ").withStyle(style -> style.withColor(TextColor.fromRgb(0x5555FF))));
        if (value) {
            base.append(ComponentHelper.literal("True").withStyle(style -> style.withColor(TextColor.fromRgb(0x00FF00))));
        } else {
            base.append(ComponentHelper.literal("False").withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000))));
        }
        return base;
    }

    private Component getPatternMessage(String pattern) {
        MutableComponent base = ComponentHelper.literal("");
        base.append(ComponentHelper.literal("Pattern : ").withStyle(style -> style.withColor(TextColor.fromRgb(0x5555FF))));

        int color = 0xFFFFFF;
        switch (pattern) {
            case "beam":
                color = 0x00FFFF;
                break;
            case "spiral":
                color = 0xFF00FF;
                break;
            case "fountain":
                color = 0x00FF00;
                break;
            case "pulse":
                color = 0xFF0000;
                break;
            case "ring":
                color = 0xFFAA00;
                break;
            case "burst":
                color = 0xFF5555;
                break;
            case "snowflake":
                color = 0xA0FFFF;
                break;
            default:
                color = 0xAAAAAA;
                break;
        }

        String displayName = pattern.substring(0, 1).toUpperCase() + pattern.substring(1);
        final int finalColor = color;
        try {
            base.append(ComponentHelper.translatable("buildscape.config.particles.pattern." + pattern).withStyle(style -> style.withColor(TextColor.fromRgb(finalColor))));
        } catch (Exception e) {
            base.append(ComponentHelper.literal(displayName).withStyle(style -> style.withColor(TextColor.fromRgb(finalColor))));
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

        currentPatternIndex = findPatternIndex(config.pattern);
        currentMaxColor = Math.max(1, Math.min(7, config.max_particle_color));

        ScaledTextButton usePatternBtn = new ScaledTextButton(
                0, 0,
                100, 20,
                getUsePatternMessage(config.use_pattern),
                (btn) -> toggleUsePattern());
        usePatternBtn.setCustomTextColors(0, 0);
        usePatternToggle = usePatternBtn.getButton();
        addTabWidget(usePatternToggle);

        int fieldHeight = 14;

        particleSpeedField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                ComponentHelper.translatable("buildscape.config.particles.particle_speed"));
        particleSpeedField.setValue(String.valueOf(config.particle_speed));
        particleSpeedField.setEditable(!config.use_pattern);
        particleSpeedField.setBordered(true);
        particleSpeedField.setTextColor(0xFFFFFF);
        particleSpeedField.setTextColorUneditable(0xAAAAAA);
        particleSpeedField.setTextColorUneditable(0xAAAAAA);
        particleSpeedField.setMaxLength(64);
        Services.PLATFORM.setEditBoxFilter(particleSpeedField, s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleSpeedField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleSpeedField);

        particleSpreadField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                ComponentHelper.translatable("buildscape.config.particles.particle_spread"));
        particleSpreadField.setValue(String.valueOf(config.particle_spread));
        particleSpreadField.setEditable(!config.use_pattern);
        particleSpreadField.setBordered(true);
        particleSpreadField.setTextColor(0xFFFFFF);
        particleSpreadField.setTextColorUneditable(0xAAAAAA);
        particleSpreadField.setTextColorUneditable(0xAAAAAA);
        particleSpreadField.setMaxLength(64);
        Services.PLATFORM.setEditBoxFilter(particleSpreadField, s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleSpreadField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleSpreadField);

        particleLifetimeField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                ComponentHelper.translatable("buildscape.config.particles.particle_lifetime"));
        particleLifetimeField.setValue(String.valueOf(config.particle_lifetime));
        particleLifetimeField.setEditable(!config.use_pattern);
        particleLifetimeField.setBordered(true);
        particleLifetimeField.setTextColor(0xFFFFFF);
        particleLifetimeField.setTextColorUneditable(0xAAAAAA);
        particleLifetimeField.setTextColorUneditable(0xAAAAAA);
        particleLifetimeField.setMaxLength(64);
        Services.PLATFORM.setEditBoxFilter(particleLifetimeField, s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleLifetimeField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleLifetimeField);

        particleDensityField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                ComponentHelper.translatable("buildscape.config.particles.particle_density"));
        particleDensityField.setValue(String.valueOf(config.particle_density));
        particleDensityField.setEditable(!config.use_pattern);
        particleDensityField.setBordered(true);
        particleDensityField.setTextColor(0xFFFFFF);
        particleDensityField.setTextColorUneditable(0xAAAAAA);
        particleDensityField.setTextColorUneditable(0xAAAAAA);
        particleDensityField.setMaxLength(64);
        Services.PLATFORM.setEditBoxFilter(particleDensityField, s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        particleDensityField.setResponder(s -> updateConfigFromFields());
        addTabWidget(particleDensityField);

        colorSwatchButtons = new ArrayList<>();
        colorHexFields = new ArrayList<>();
        sharedColorPicker = null;

        ScaledTextButton patternSelectorBtn = new ScaledTextButton(
                0, 0,
                100, 20,
                getPatternMessage(config.pattern),
                (btn) -> cyclePattern());
        patternSelectorBtn.setCustomTextColors(0, 0);
        patternSelector = patternSelectorBtn.getButton();
        patternSelector.active = config.use_pattern;
        addTabWidget(patternSelector);

        patternSpeedField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                ComponentHelper.translatable("buildscape.config.particles.pattern_speed"));
        patternSpeedField.setValue(String.valueOf(config.pattern_speed));
        patternSpeedField.setEditable(config.use_pattern);
        patternSpeedField.setBordered(true);
        patternSpeedField.setTextColor(0xFFFFFF);
        patternSpeedField.setTextColorUneditable(0xAAAAAA);
        patternSpeedField.setTextColorUneditable(0xAAAAAA);
        patternSpeedField.setMaxLength(64);
        Services.PLATFORM.setEditBoxFilter(patternSpeedField, s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        patternSpeedField.setResponder(s -> updateConfigFromFields());
        addTabWidget(patternSpeedField);

        patternSpreadField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                ComponentHelper.translatable("buildscape.config.particles.pattern_spread"));
        patternSpreadField.setValue(String.valueOf(config.pattern_spread));
        patternSpreadField.setEditable(config.use_pattern);
        patternSpreadField.setBordered(true);
        patternSpreadField.setTextColor(0xFFFFFF);
        patternSpreadField.setTextColorUneditable(0xAAAAAA);
        patternSpreadField.setTextColorUneditable(0xAAAAAA);
        patternSpreadField.setMaxLength(64);
        Services.PLATFORM.setEditBoxFilter(patternSpreadField, s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        patternSpreadField.setResponder(s -> updateConfigFromFields());
        addTabWidget(patternSpreadField);

        patternIntensityField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                120, fieldHeight,
                ComponentHelper.translatable("buildscape.config.particles.pattern_intensity"));
        patternIntensityField.setValue(String.valueOf(config.pattern_intensity));
        patternIntensityField.setEditable(config.use_pattern);
        patternIntensityField.setBordered(true);
        patternIntensityField.setTextColor(0xFFFFFF);
        patternIntensityField.setTextColorUneditable(0xAAAAAA);
        patternIntensityField.setTextColorUneditable(0xAAAAAA);
        patternIntensityField.setMaxLength(64);
        Services.PLATFORM.setEditBoxFilter(patternIntensityField, s -> s.matches("[0-9]*\\.?[0-9]{0,3}"));
        patternIntensityField.setResponder(s -> updateConfigFromFields());
        addTabWidget(patternIntensityField);

        colorsResetButton = new FlatIconButton(0, 0, 20, 20, ComponentHelper.literal("\u27F2"), (btn) -> {
            boolean shift = Services.PLATFORM.hasShiftDown();
            boolean ctrl = Services.PLATFORM.hasControlDown();

            if (shift) {
                resetPropertiesToDefault();
                resetColorsToDefault();
            } else if (ctrl) {
                resetPropertiesToDefault();
            } else {
                resetColorsToDefault();
            }
        }).getButton();
        addTabWidget(colorsResetButton);

        maxParticleColorSlider = new IntSliderWidget(
                0, 0,
                120, 14,
                ComponentHelper.translatable("buildscape.config.particles.max_particle_color", currentMaxColor),
                1, 7, currentMaxColor,
                (value) -> onMaxParticleColorChanged(value));
        maxParticleColorSlider.active = config.use_pattern;
        addTabWidget(maxParticleColorSlider);

        relayout(contentX, contentY, contentWidth, contentHeight);

        updateSwatchesEnabledState();

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
        requestConfigSave();

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
        requestConfigSave();

        currentMaxColor = 3;
        if (maxParticleColorSlider != null) {
            maxParticleColorSlider.setValue(3);
        }
        createColorSwatchesAndPicker(config);
        updateColorSwatchesPositions();
        updateSwatchesEnabledState();
    }

    private void createColorSwatchesAndPicker(PillarParticleConfig config) {
        int padding = BuildScapeConfigScreen.scaleSize(10);
        int swatchSize = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int swatchSpacing = BuildScapeConfigScreen.scaleSize(5);
        int hexFieldWidth = BuildScapeConfigScreen.scaleSize(80);
        int hexFieldHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int rowSpacing = BuildScapeConfigScreen.scaleSize(25);

        if (colorSwatchButtons != null) {
            colorSwatchButtons.clear();
        }
        if (colorHexFields != null) {
            colorHexFields.clear();
        }

        if (colorSwatchButtons == null) {
            colorSwatchButtons = new ArrayList<>();
        }
        if (colorHexFields == null) {
            colorHexFields = new ArrayList<>();
        }

        List<String> displayColors = new ArrayList<>(config.particle_color);
        ParticleColorSlots.ensureCapacity(displayColors, 7);

        int startY = colorBoxY + padding + 25;
        int swatchX = colorBoxX + padding;
        int hexFieldX = swatchX + swatchSize + swatchSpacing;

        for (int i = 0; i < 7; i++) {
            final int colorIndex = i;
            String hexValue = displayColors.get(i);
            int color = 0xFFFFFF;
            try {
                if (hexValue.startsWith("#") && hexValue.length() == 7) {
                    color = Integer.parseInt(hexValue.substring(1), 16);
                }
            } catch (NumberFormatException e) {
            }

            int swatchY = startY + i * rowSpacing;

            ColorSwatchButton swatchButton = new ColorSwatchButton(
                    swatchX, swatchY,
                    swatchSize, swatchSize,
                    color,
                    (btn) -> onColorSwatchClicked(colorIndex));
            colorSwatchButtons.add(swatchButton);
            addTabWidget(swatchButton);

            int hexFieldY = swatchY;
            if (hexFieldHeight != swatchSize) {
                hexFieldY = swatchY + (swatchSize - hexFieldHeight) / 2;
            }

            EditBox hexField = new EditBox(
                    Minecraft.getInstance().font,
                    hexFieldX, hexFieldY,
                    hexFieldWidth, hexFieldHeight,
                    ComponentHelper.empty());
            hexField.setValue(hexValue);
            hexField.setBordered(true);
            hexField.setTextColor(0xFFFFFF);
            hexField.setMaxLength(7);
            hexField.setResponder((text) -> {
                try {
                    String hexText = text;
                    if (!hexText.startsWith("#")) {
                        hexText = "#" + hexText;
                    }

                    if (hexText.length() == 7 && hexText.matches("#[0-9A-Fa-f]{6}")) {
                        int newColor = Integer.parseInt(hexText.substring(1), 16);

                        if (selectedColorIndex == colorIndex && sharedColorPicker != null && !sharedColorPicker.isDragging()) {
                            sharedColorPicker.setColor(newColor);
                        }

                        onColorChanged(colorIndex, hexText);
                        updateSwatchButtonColor(colorIndex, newColor);

                        if (!text.equals(hexText)) {
                            hexField.setValue(hexText);
                        }
                    }
                } catch (NumberFormatException e) {
                }
            });
            colorHexFields.add(hexField);
            addTabWidget(hexField);
        }

        int pickerX = colorBoxX + padding + swatchSize + hexFieldWidth + swatchSpacing * 2;
        int pickerY = colorBoxY + padding + 25 + 3;
        int pickerWidth = 260;
        int pickerHeight = 220;

        sharedColorPicker = new ColorPickerWidget(
                pickerX, pickerY,
                pickerWidth, pickerHeight,
                0xFFFFFF,
                (hexColor) -> {
                    if (selectedColorIndex >= 0 && selectedColorIndex < 7) {
                        onColorChanged(selectedColorIndex, hexColor);
                        if (selectedColorIndex < colorHexFields.size()) {
                            colorHexFields.get(selectedColorIndex).setValue(hexColor);
                        }
                        try {
                            if (hexColor.startsWith("#") && hexColor.length() == 7) {
                                int newColor = Integer.parseInt(hexColor.substring(1), 16);
                                updateSwatchButtonColor(selectedColorIndex, newColor);
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                });
        sharedColorPicker.setEnabled(config.use_pattern);
        sharedColorPicker.visible = false;
        addTabWidget(sharedColorPicker);
    }

    private void onColorSwatchClicked(int colorIndex) {
        if (colorIndex >= currentMaxColor) {
            return;
        }

        PillarParticleConfig config = PillarParticleConfig.get();
        if (!config.use_pattern) {
            return;
        }

        selectedColorIndex = colorIndex;

        String hexValue = ParticleColorSlots.colorAt(config.particle_color, colorIndex);
        int color = 0xFFFFFF;
        try {
            if (hexValue.startsWith("#") && hexValue.length() == 7) {
                color = Integer.parseInt(hexValue.substring(1), 16);
            }
        } catch (NumberFormatException e) {
        }

        if (sharedColorPicker != null) {
            sharedColorPicker.setColor(color);

            sharedColorPicker.visible = true;
            sharedColorPicker.setEnabled(config.use_pattern);
        }
    }

    private void updateSwatchButtonColor(int index, int color) {
        if (colorSwatchButtons != null && index >= 0 && index < colorSwatchButtons.size()) {
            colorSwatchButtons.get(index).setColor(color);
        }
    }

    private int colorBaseStartY = 0;

    private static void setEditBoxHeight(EditBox editBox, int height) {
        com.kingodogo.buildscape.client.screen.widget.WidgetLayoutHelper.setWidgetHeight(editBox, height);
    }

    private int getColorSwatchesTotalHeight() {
        int numSwatches = 7;
        int numRows = (numSwatches + 1) / 2;
        return COLOR_HEADER_SPACE + (numRows * (COLOR_SWATCH_SIZE + COLOR_ROW_SPACING));
    }

    private void updateColorSwatchesPositions() {
        int colorTotalContentHeight = getColorSwatchesTotalHeight();
        int colorAvailableHeight = colorBoxHeight - UI_PADDING * 2;
        double maxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);
        colorSwatchesScrollOffset = Math.max(0, Math.min(maxScroll, colorSwatchesScrollOffset));
        int scrollOffsetInt = (int) colorSwatchesScrollOffset;

        int availableWidth = colorBoxWidth - UI_PADDING * 2;
        int colSpacing = 12;
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
                btn.setPosition(startX, y);
                btn.setWidth(COLOR_SWATCH_SIZE);

                EditBox hex = colorHexFields.get(i);
                WidgetLayoutHelper.setPosition(hex, startX + COLOR_SWATCH_SIZE + hexGap, y + (COLOR_SWATCH_SIZE - FIELD_HEIGHT) / 2);
                hex.setWidth(hexWidth);
            }
        }
    }

    private int defaultBoxX, defaultBoxY, defaultBoxWidth, defaultBoxHeight;
    private int patternBoxX, patternBoxY, patternBoxWidth, patternBoxHeight;
    private int colorBoxX, colorBoxY, colorBoxWidth, colorBoxHeight;

    private int lastContentX = -1, lastContentY = -1, lastContentWidth = -1, lastContentHeight = -1;
    private int lastScreenWidth = -1;

    private double defaultPropertiesScrollOffset = 0;
    private double colorSwatchesScrollOffset = 0;

    private double patternPropertiesScrollOffset = 0;

    private int defaultBaseButtonY = 0;
    private int defaultBaseFirstFieldY = 0;

    private int patternBaseButtonY = 0;
    private int patternBaseFirstFieldY = 0;

    private void updateDefaultPropertiesPositions() {
        int totalContentHeight = getDefaultPropertiesTotalHeight();
        int availableHeight = defaultBoxHeight - UI_PADDING * 2;
        double maxScroll = Math.max(0, totalContentHeight - availableHeight);

        defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, defaultPropertiesScrollOffset));
        int scrollOffsetInt = (int) defaultPropertiesScrollOffset;

        WidgetLayoutHelper.setY(particleSpeedField, defaultBaseFirstFieldY - scrollOffsetInt);
        WidgetLayoutHelper.setY(particleSpreadField, defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) - scrollOffsetInt);
        WidgetLayoutHelper.setY(particleLifetimeField, defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) * 2 - scrollOffsetInt);
        WidgetLayoutHelper.setY(particleDensityField, defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) * 3 - scrollOffsetInt);
        WidgetLayoutHelper.setY(usePatternToggle, defaultBaseButtonY - scrollOffsetInt);
    }

    private int getDefaultPropertiesTotalHeight() {
        return HEADER_CLIP + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING + (4 * FIELD_HEIGHT) + (3 * COMPONENT_SPACING);
    }

    private int getPatternPropertiesTotalHeight() {
        return HEADER_CLIP + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING + (4 * FIELD_HEIGHT) + (3 * COMPONENT_SPACING);
    }

    private void updatePatternPropertiesPositions() {
        int patternAvailableHeight = patternBoxHeight - UI_PADDING * 2;
        int patternTotalContentHeight = getPatternPropertiesTotalHeight();
        double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);

        patternPropertiesScrollOffset = Math.max(0, Math.min(patternMaxScroll, patternPropertiesScrollOffset));
        int scrollOffsetInt = (int) patternPropertiesScrollOffset;

        WidgetLayoutHelper.setY(patternSelector, patternBaseButtonY - scrollOffsetInt);
        int currentY = patternBaseFirstFieldY - scrollOffsetInt;

        maxParticleColorSlider.y = currentY;
        currentY += FIELD_HEIGHT + COMPONENT_SPACING;

        WidgetLayoutHelper.setY(patternSpeedField, currentY);
        currentY += FIELD_HEIGHT + COMPONENT_SPACING;

        WidgetLayoutHelper.setY(patternSpreadField, currentY);
        currentY += FIELD_HEIGHT + COMPONENT_SPACING;

        WidgetLayoutHelper.setY(patternIntensityField, currentY);
    }

    private void relayout(int contentX, int contentY, int contentWidth, int contentHeight) {
        int padding = 10;

        int screenHeight = parent.height;
        int middleGap = parent.getVerticalPanelGap();
        int fullContentHeight = contentHeight;

        int sectionHeight = (fullContentHeight - middleGap) / 2;

        int topY = contentY;

        defaultBoxX = parent.getContentX();
        defaultBoxY = topY;
        defaultBoxWidth = parent.getContentWidth();
        defaultBoxHeight = sectionHeight;

        patternBoxX = parent.getContentX();
        patternBoxY = topY + sectionHeight + middleGap;
        patternBoxWidth = parent.getContentWidth();
        patternBoxHeight = fullContentHeight - (sectionHeight + middleGap);

        colorBoxX = parent.getRightPanelX();
        colorBoxY = topY;
        colorBoxWidth = parent.getRightPanelWidth();
        colorBoxHeight = defaultBoxHeight + middleGap + patternBoxHeight;

        WidgetLayoutHelper.setPosition(colorsResetButton, colorBoxX + colorBoxWidth - 20 - 2, colorBoxY + 2);

        int defaultTextX = defaultBoxX + padding;
        int labelWidth = 115;
        int fieldX = defaultTextX + labelWidth - 3;

        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int fieldHeight = 14;
        int titleHeight = 20;
        int buttonHeight = 20;
        int numFields = 4;
        int fieldSpacing = 2;

        int totalContentHeightDefault = getDefaultPropertiesTotalHeight();
        int defaultPanelAvailableHeight = defaultBoxHeight - padding * 2;

        boolean needsScrollbarDefault = totalContentHeightDefault > defaultPanelAvailableHeight;

        int componentEndX;
        if (needsScrollbarDefault) {
            componentEndX = defaultBoxX + defaultBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN - COMPONENT_SCROLLBAR_GAP;
        } else {
            componentEndX = defaultBoxX + defaultBoxWidth - padding;
        }


        int buttonStartX = defaultTextX;
        int buttonWidth = componentEndX - buttonStartX;
        if (buttonWidth < 1)
            buttonWidth = 1;

        int fieldWidth = componentEndX - fieldX;
        if (fieldWidth < 0)
            fieldWidth = 0;
        if (fieldX + fieldWidth > componentEndX) {
            fieldWidth = componentEndX - fieldX;
            if (fieldWidth < 0)
                fieldWidth = 0;
        }


        defaultBaseButtonY = defaultBoxY + HEADER_CLIP;
        defaultBaseFirstFieldY = defaultBaseButtonY + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING;

        int finalFieldWidth = Math.min(fieldWidth, componentEndX - fieldX);
        if (finalFieldWidth < 0)
            finalFieldWidth = 0;

        WidgetLayoutHelper.setX(usePatternToggle, buttonStartX);
        usePatternToggle.setWidth(buttonWidth);

        WidgetLayoutHelper.setX(particleSpeedField, fieldX);
        particleSpeedField.setWidth(finalFieldWidth);

        WidgetLayoutHelper.setX(particleSpreadField, fieldX);
        particleSpreadField.setWidth(finalFieldWidth);

        WidgetLayoutHelper.setX(particleLifetimeField, fieldX);
        particleLifetimeField.setWidth(finalFieldWidth);

        WidgetLayoutHelper.setX(particleDensityField, fieldX);
        particleDensityField.setWidth(finalFieldWidth);

        updateDefaultPropertiesPositions();

        if (colorSwatchButtons == null || colorSwatchButtons.isEmpty()) {
            createColorSwatchesAndPicker(PillarParticleConfig.get());
        }
        updateColorSwatchesPositions();

        int patternTextX = patternBoxX + padding;
        int patternLabelWidth = 115;
        int dynamicGap = (int) (screenHeight * 0.002);
        int patternFieldX = patternTextX + patternLabelWidth + dynamicGap;

        int patternFieldSpacing = 2;
        int patternTitleHeight = 20;
        int patternButtonHeight = 20;
        int patternButtonToFieldSpacing = 5 + dynamicGap;

        int patternScrollbarWidth = 13;
        int patternScrollbarOffset = 10;

        int patternTotalContentHeight = getPatternPropertiesTotalHeight();
        int patternAvailableHeight = patternBoxHeight - padding * 2;
        boolean needsPatternScrollbar = patternTotalContentHeight > patternAvailableHeight;

        int patternComponentEndX;
        if (needsPatternScrollbar) {
            patternComponentEndX = patternBoxX + patternBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN - COMPONENT_SCROLLBAR_GAP;
        } else {
            patternComponentEndX = patternBoxX + patternBoxWidth - padding;
        }

        int patternButtonStartX = patternTextX;
        int patternButtonWidth = patternComponentEndX - patternButtonStartX;
        if (patternButtonWidth < 1)
            patternButtonWidth = 1;

        patternBaseButtonY = patternBoxY + HEADER_CLIP;
        patternBaseFirstFieldY = patternBaseButtonY + BUTTON_HEIGHT + BTN_TO_FIELD_SPACING;

        int patternFieldWidth = patternComponentEndX - patternFieldX;
        if (patternFieldWidth < 0)
            patternFieldWidth = 0;
        if (patternFieldX + patternFieldWidth > patternComponentEndX) {
            patternFieldWidth = patternComponentEndX - patternFieldX;
            if (patternFieldWidth < 0)
                patternFieldWidth = 0;
        }


        WidgetLayoutHelper.setX(patternSelector, patternButtonStartX);
        patternSelector.setWidth(patternButtonWidth);

        int finalPatternFieldWidth = Math.min(patternFieldWidth, patternComponentEndX - patternFieldX);
        if (finalPatternFieldWidth < 0)
            finalPatternFieldWidth = 0;

        maxParticleColorSlider.x = patternFieldX;
        maxParticleColorSlider.setWidth(finalPatternFieldWidth);

        WidgetLayoutHelper.setX(patternSpeedField, patternFieldX);
        patternSpeedField.setWidth(finalPatternFieldWidth);

        WidgetLayoutHelper.setX(patternSpreadField, patternFieldX);
        patternSpreadField.setWidth(finalPatternFieldWidth);

        WidgetLayoutHelper.setX(patternIntensityField, patternFieldX);
        patternIntensityField.setWidth(finalPatternFieldWidth);


        updatePatternPropertiesPositions();

        if (sharedColorPicker != null) {
            int pickerPadding = 10;
            int pickerSize = Math.min(100, colorBoxWidth - pickerPadding * 2);
            int swatchAreaHeight = 7 * (20 + 4) + 5;
            int pickerX = colorBoxX + colorBoxWidth - pickerPadding - pickerSize;
            int pickerY = colorBoxY + padding + swatchAreaHeight;

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
        }
    }

    private void onColorChanged(int index, String hexColor) {
        PillarParticleConfig config = PillarParticleConfig.get();
        ParticleColorSlots.ensureCapacity(config.particle_color, index + 1);
        config.particle_color.set(index, hexColor);
        requestConfigSave();
    }

    private void toggleUsePattern() {
        PillarParticleConfig config = PillarParticleConfig.get();
        config.use_pattern = !config.use_pattern;
        requestConfigSave();

        usePatternToggle.setMessage(getUsePatternMessage(config.use_pattern));
        particleSpeedField.setEditable(!config.use_pattern);
        particleSpreadField.setEditable(!config.use_pattern);
        particleLifetimeField.setEditable(!config.use_pattern);
        particleDensityField.setEditable(!config.use_pattern);
        patternSelector.active = config.use_pattern;
        patternSpeedField.setEditable(config.use_pattern);
        patternSpreadField.setEditable(config.use_pattern);
        patternIntensityField.setEditable(config.use_pattern);

        boolean colorControlsEnabled = config.use_pattern;
        if (maxParticleColorSlider != null) {
            maxParticleColorSlider.active = colorControlsEnabled;
        }
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
        String oldPattern = config.pattern;
        config.pattern = pattern;
        config.use_pattern = true;
        requestConfigSave();

        PillarIdManager manager = PillarIdManager.getClient();
        if (manager.hasLoaded()) {
            for (PillarIdManager.PillarData pData : manager.getAllData()) {
                boolean hasPatternOverride = pData.pattern != null && !pData.pattern.equals("default");
                boolean isCustomized = pData.hasColors() || hasPatternOverride;

                if (isCustomized && (pData.pattern == null || pData.pattern.equals("default"))) {
                    pData.pattern = oldPattern != null ? oldPattern : "ring";
                }
            }
        }

        if (usePatternToggle != null) {
            usePatternToggle.setMessage(getUsePatternMessage(true));
        }

        patternSelector.setMessage(getPatternMessage(pattern));
    }

    private void onMaxParticleColorChanged(int value) {
        currentMaxColor = value;

        PillarParticleConfig config = PillarParticleConfig.get();
        config.max_particle_color = currentMaxColor;
        ParticleColorSlots.ensureCapacity(config.particle_color, currentMaxColor);
        requestConfigSave();

        maxParticleColorSlider.setMessage(
                ComponentHelper.translatable("buildscape.config.particles.max_particle_color", currentMaxColor));

        updateSwatchesEnabledState();

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
                boolean enabled = usePatternEnabled && (i < currentMaxColor);
                colorSwatchButtons.get(i).active = enabled;
            }
        }

        if (colorHexFields != null) {
            for (int i = 0; i < colorHexFields.size(); i++) {
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
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        flushConfigSave(false);
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

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



        Minecraft mcInstance = Minecraft.getInstance();
        PillarParticleConfig config = PillarParticleConfig.get();
        int padding = 10;

        double guiScale = mcInstance.getWindow().getGuiScale();
        int windowHeight = mcInstance.getWindow().getHeight();

        int borderColor = 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, defaultBoxX, defaultBoxY, defaultBoxX + defaultBoxWidth, defaultBoxY + 1,
                borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, defaultBoxX, defaultBoxY + defaultBoxHeight - 1, defaultBoxX + defaultBoxWidth,
                defaultBoxY + defaultBoxHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, defaultBoxX, defaultBoxY, defaultBoxX + 1, defaultBoxY + defaultBoxHeight,
                borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, defaultBoxX + defaultBoxWidth - 1, defaultBoxY, defaultBoxX + defaultBoxWidth,
                defaultBoxY + defaultBoxHeight, borderColor);

        int bottomOffset = Math.max(5, (int) (windowHeight * 0.01 / guiScale));

        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int textYOffset = (20 - (int)(mcInstance.font.lineHeight * textScale)) / 2 + 1;


        int scissorX = (int) (defaultBoxX * guiScale);
        int scissorY = (int) (windowHeight - (defaultBoxY + defaultBoxHeight) * guiScale + bottomOffset * guiScale);
        int scissorWidth = (int) (defaultBoxWidth * guiScale);
        int scissorHeight = (int) (defaultBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        boolean defaultClip = scissorHeight > 0 && scissorWidth > 0;
        if (defaultClip)
            Services.PLATFORM.enableScissor(poseStackOrGraphics, scissorX, scissorY, scissorWidth, scissorHeight);
        try {
            int defaultTextX = defaultBoxX + padding;

            int labelYOffset = (FIELD_HEIGHT - (int) (Minecraft.getInstance().font.lineHeight * textScale)) / 2;
            int totalContentHeightDefault = getDefaultPropertiesTotalHeight();
            int availableHeightDefault = defaultBoxHeight - UI_PADDING * 2;
            double maxScrollDefault = Math.max(0, totalContentHeightDefault - availableHeightDefault);
            boolean needsScrollbarDefault = maxScrollDefault > 0;

            int headerBottom = defaultBoxY + HEADER_CLIP;
            int panelTop = defaultBoxY;
            int panelBottom = defaultBoxY + defaultBoxHeight;

            boolean particleSpeedRowVisible = WidgetLayoutHelper.getY(particleSpeedField) + FIELD_HEIGHT > headerBottom
                    && WidgetLayoutHelper.getY(particleSpeedField) < panelBottom;
            boolean particleSpreadRowVisible = WidgetLayoutHelper.getY(particleSpreadField) + FIELD_HEIGHT > headerBottom
                    && WidgetLayoutHelper.getY(particleSpreadField) < panelBottom;
            boolean particleLifetimeRowVisible = WidgetLayoutHelper.getY(particleLifetimeField) + FIELD_HEIGHT > headerBottom
                    && WidgetLayoutHelper.getY(particleLifetimeField) < panelBottom;
            boolean particleDensityRowVisible = WidgetLayoutHelper.getY(particleDensityField) + FIELD_HEIGHT > headerBottom
                    && WidgetLayoutHelper.getY(particleDensityField) < panelBottom;

            int particleSpeedLabelY = WidgetLayoutHelper.getY(particleSpeedField) + labelYOffset;
            if (particleSpeedRowVisible) {
                drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.particle_speed").getString() + " ", defaultTextX, particleSpeedLabelY, textScale);
            }

            int particleSpreadLabelY = WidgetLayoutHelper.getY(particleSpreadField) + labelYOffset;
            if (particleSpreadRowVisible) {
                drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.particle_spread").getString() + " ", defaultTextX, particleSpreadLabelY, textScale);
            }

            int particleLifetimeLabelY = WidgetLayoutHelper.getY(particleLifetimeField) + labelYOffset;
            if (particleLifetimeRowVisible) {
                drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.particle_lifetime").getString() + " ", defaultTextX, particleLifetimeLabelY, textScale);
            }

            int particleDensityLabelY = WidgetLayoutHelper.getY(particleDensityField) + labelYOffset;
            if (particleDensityRowVisible) {
                drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.particle_density").getString() + " ", defaultTextX, particleDensityLabelY, textScale);
            }

            usePatternToggle.visible = false;
            particleSpeedField.visible = false;
            particleSpreadField.visible = false;
            particleLifetimeField.visible = false;
            particleDensityField.visible = false;



            if (WidgetLayoutHelper.getY(usePatternToggle) + BUTTON_HEIGHT > headerBottom && WidgetLayoutHelper.getY(usePatternToggle) < panelBottom) {
                usePatternToggle.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, usePatternToggle, mouseX, mouseY, partialTick);
                usePatternToggle.visible = false;
            }
            int textPadding = BuildScapeConfigScreen.scaleSize(4);
            int fontHeight = Minecraft.getInstance().font.lineHeight;

            if (particleSpeedRowVisible) {
                particleSpeedField.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, particleSpeedField, mouseX, mouseY, partialTick);
                particleSpeedField.visible = false;
            }
            if (particleSpreadRowVisible) {
                particleSpreadField.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, particleSpreadField, mouseX, mouseY, partialTick);
                particleSpreadField.visible = false;
            }
            if (particleLifetimeRowVisible) {
                particleLifetimeField.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, particleLifetimeField, mouseX, mouseY, partialTick);
                particleLifetimeField.visible = false;
            }
            if (particleDensityRowVisible) {
                particleDensityField.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, particleDensityField, mouseX, mouseY, partialTick);
                particleDensityField.visible = false;
            }

            if (needsScrollbarDefault && maxScrollDefault > 0) {
                int scrollbarX = defaultBoxX + defaultBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
                int scrollbarY = defaultBoxY + HEADER_CLIP;
                int scrollbarHeight = defaultBoxHeight - HEADER_CLIP - UI_PADDING;

                double scrollableAreaHeight = totalContentHeightDefault - HEADER_CLIP;
                double visibleAreaHeight = defaultBoxHeight - HEADER_CLIP - UI_PADDING;
                double visibleRatio = Math.min(1.0, visibleAreaHeight / scrollableAreaHeight);

                defaultScrollbarRenderer.renderScrollbar(poseStackOrGraphics, scrollbarX, scrollbarY, scrollbarHeight,
                        defaultPropertiesScrollOffset, maxScrollDefault, visibleRatio);
            }

        } finally {
            if (defaultClip) Services.PLATFORM.disableScissor(poseStackOrGraphics);
        }


        scissorX = (int) (colorBoxX * guiScale);
        scissorY = (int) (windowHeight - (colorBoxY + colorBoxHeight) * guiScale + bottomOffset * guiScale);
        scissorWidth = (int) (colorBoxWidth * guiScale);
        scissorHeight = (int) (colorBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        int colorBorderColor = 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, colorBoxX, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + 1, colorBorderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, colorBoxX, colorBoxY + colorBoxHeight - 1, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, colorBorderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, colorBoxX, colorBoxY, colorBoxX + 1, colorBoxY + colorBoxHeight, colorBorderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, colorBoxX + colorBoxWidth - 1, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, colorBorderColor);

        Services.PLATFORM.enableScissor(poseStackOrGraphics, scissorX, scissorY, scissorWidth, scissorHeight);
        try {


            if (colorSwatchButtons == null || colorHexFields == null || colorSwatchButtons.isEmpty()
                    || colorHexFields.isEmpty()) {
                createColorSwatchesAndPicker(config);
            }

            if (colorSwatchButtons != null && colorHexFields != null && colorSwatchButtons.size() > 0
                    && colorHexFields.size() > 0) {
                updateSwatchesEnabledState();

                int colorTotalContentHeight = getColorSwatchesTotalHeight();
                int colorAvailableHeight = colorBoxHeight - UI_PADDING * 2;
                double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);
                boolean colorNeedsScrollbar = colorMaxScroll > 0;

                colorsResetButton.visible = true;

                updateColorSwatchesPositions();

                for (int i = 0; i < colorSwatchButtons.size(); i++) {
                    colorSwatchButtons.get(i).visible = false;
                }
                for (int i = 0; i < colorHexFields.size(); i++) {
                    colorHexFields.get(i).visible = false;
                }

                for (int i = 0; i < colorSwatchButtons.size() && i < colorHexFields.size(); i++) {
                    ColorSwatchButton swatchButton = colorSwatchButtons.get(i);

                    String hexValue = ParticleColorSlots.colorAt(config.particle_color, i);
                    int color = 0xFFFFFF;
                    try {
                        if (hexValue.startsWith("#") && hexValue.length() == 7) {
                            color = Integer.parseInt(hexValue.substring(1), 16);
                        }
                    } catch (NumberFormatException e) {
                    }

                    swatchButton.setColor(color);
                    swatchButton.setSelected(selectedColorIndex == i);

                    swatchButton.visible = true;
                    swatchButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
                    swatchButton.visible = false;

                    colorHexFields.get(i).visible = true;
                    Services.PLATFORM.renderWidget(poseStackOrGraphics, colorHexFields.get(i), mouseX, mouseY, partialTick);
                    colorHexFields.get(i).visible = false;
                }

                if (colorNeedsScrollbar && colorMaxScroll > 0) {
                    int scrollbarX = colorBoxX + colorBoxWidth - CustomScrollbarRenderer.getScrollbarWidth() - 5;
                    int scrollbarY = colorBoxY + UI_PADDING + COLOR_HEADER_SPACE;
                    int scrollbarHeight = colorAvailableHeight - COLOR_HEADER_SPACE;

                    double visibleRatio = (double) scrollbarHeight / (colorTotalContentHeight - COLOR_HEADER_SPACE);

                    colorScrollbarRenderer.renderScrollbar(poseStackOrGraphics, scrollbarX, scrollbarY, scrollbarHeight,
                            colorSwatchesScrollOffset, colorMaxScroll, visibleRatio);
                }
            }


        } finally {
            Services.PLATFORM.disableScissor(poseStackOrGraphics);
        }

        if (colorsResetButton.visible) {
            Services.PLATFORM.renderWidget(poseStackOrGraphics, colorsResetButton, mouseX, mouseY, partialTick);

            if (colorsResetButton.isMouseOver(mouseX, mouseY)) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(ComponentHelper.translatable("buildscape.config.particles.reset_tooltip"));
                tooltip.add(ComponentHelper.literal("Click: Reset Colors"));
                tooltip.add(ComponentHelper.literal("Ctrl+Click: Reset Default Properties"));
                tooltip.add(ComponentHelper.literal("Shift+Click: Reset All"));
                Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            }
        }

        int patternBorderColor = 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, patternBoxX, patternBoxY, patternBoxX + patternBoxWidth, patternBoxY + 1,
                patternBorderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, patternBoxX, patternBoxY + patternBoxHeight - 1, patternBoxX + patternBoxWidth,
                patternBoxY + patternBoxHeight, patternBorderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, patternBoxX, patternBoxY, patternBoxX + 1, patternBoxY + patternBoxHeight,
                patternBorderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, patternBoxX + patternBoxWidth - 1, patternBoxY, patternBoxX + patternBoxWidth,
                patternBoxY + patternBoxHeight, patternBorderColor);

        scissorX = (int) (patternBoxX * guiScale);
        scissorY = (int) (windowHeight - (patternBoxY + patternBoxHeight) * guiScale + bottomOffset * guiScale);
        scissorWidth = (int) (patternBoxWidth * guiScale);
        scissorHeight = (int) (patternBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        boolean patternClip = scissorHeight > 0 && scissorWidth > 0;
        if (patternClip)
            Services.PLATFORM.enableScissor(poseStackOrGraphics, scissorX, scissorY, scissorWidth, scissorHeight);
        try {
            float textScale_pattern = BuildScapeConfigScreen.getStandardTextScale();


            int patternTextX = patternBoxX + padding;

            int patternLabelYOffset = (FIELD_HEIGHT - (int) (Minecraft.getInstance().font.lineHeight * textScale)) / 2;

            int patternTotalContentHeightRender = getPatternPropertiesTotalHeight();
            int patternAvailableHeightRender = patternBoxHeight - UI_PADDING * 2;
            double patternMaxScrollRender = Math.max(0, patternTotalContentHeightRender - patternAvailableHeightRender);
            boolean patternNeedsScrollbarRender = patternMaxScrollRender > 0;

            int patternHeaderBottom = patternBoxY + HEADER_CLIP;
            int patternPanelTop = patternBoxY;
            int patternPanelBottom = patternBoxY + patternBoxHeight;


            boolean patternSelectorVisible = WidgetLayoutHelper.getY(patternSelector) + BUTTON_HEIGHT > patternHeaderBottom
                    && WidgetLayoutHelper.getY(patternSelector) < patternPanelBottom;

            boolean maxParticlesRowVisible = maxParticleColorSlider.y + SLIDER_HEIGHT > patternHeaderBottom
                    && maxParticleColorSlider.y < patternPanelBottom;

            boolean patternSpeedRowVisible = WidgetLayoutHelper.getY(patternSpeedField) + FIELD_HEIGHT > patternHeaderBottom
                    && WidgetLayoutHelper.getY(patternSpeedField) < patternPanelBottom;

            boolean patternSpreadRowVisible = WidgetLayoutHelper.getY(patternSpreadField) + FIELD_HEIGHT > patternHeaderBottom
                    && WidgetLayoutHelper.getY(patternSpreadField) < patternPanelBottom;

            boolean patternIntensityRowVisible = WidgetLayoutHelper.getY(patternIntensityField) + FIELD_HEIGHT > patternHeaderBottom
                    && WidgetLayoutHelper.getY(patternIntensityField) < patternPanelBottom;

            int maxParticleLabelY = maxParticleColorSlider.y + patternLabelYOffset;
            if (maxParticlesRowVisible) {
                drawScaledText(poseStackOrGraphics, "Max Particle's ", patternTextX, maxParticleLabelY, textScale);
            }

            int patternSpeedLabelY = WidgetLayoutHelper.getY(patternSpeedField) + patternLabelYOffset;
            if (patternSpeedRowVisible) {
                drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.pattern_speed").getString() + " ", patternTextX, patternSpeedLabelY, textScale);
            }

            int patternSpreadLabelY = WidgetLayoutHelper.getY(patternSpreadField) + patternLabelYOffset;
            if (patternSpreadRowVisible) {
                drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.pattern_spread").getString() + " ", patternTextX, patternSpreadLabelY, textScale);
            }

            int patternIntensityLabelY = WidgetLayoutHelper.getY(patternIntensityField) + patternLabelYOffset;
            if (patternIntensityRowVisible) {
                drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.pattern_intensity").getString() + " ", patternTextX, patternIntensityLabelY, textScale);
            }

            patternSelector.visible = false;
            patternSpeedField.visible = false;
            patternSpreadField.visible = false;
            patternIntensityField.visible = false;
            maxParticleColorSlider.visible = false;

            if (patternSelectorVisible) {
                patternSelector.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, patternSelector, mouseX, mouseY, partialTick);
                patternSelector.visible = false;
            }
            if (maxParticlesRowVisible) {
                maxParticleColorSlider.visible = true;
                maxParticleColorSlider.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
                maxParticleColorSlider.visible = false;
            }


            if (patternSpeedRowVisible) {
                patternSpeedField.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, patternSpeedField, mouseX, mouseY, partialTick);
                patternSpeedField.visible = false;
            }
            if (patternSpreadRowVisible) {
                patternSpreadField.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, patternSpreadField, mouseX, mouseY, partialTick);
                patternSpreadField.visible = false;
            }
            if (patternIntensityRowVisible) {
                patternIntensityField.visible = true;
                Services.PLATFORM.renderWidget(poseStackOrGraphics, patternIntensityField, mouseX, mouseY, partialTick);
                patternIntensityField.visible = false;
            }

            if (patternNeedsScrollbarRender && patternMaxScrollRender > 0) {
                int scrollbarX = patternBoxX + patternBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
                int scrollbarY = patternBoxY + HEADER_CLIP;
                int scrollbarHeight = patternBoxHeight - HEADER_CLIP - UI_PADDING;

                double patternScrollableAreaHeight = patternTotalContentHeightRender - HEADER_CLIP;
                double patternVisibleAreaHeight = patternBoxHeight - HEADER_CLIP - UI_PADDING;
                double visibleRatio = Math.min(1.0, patternVisibleAreaHeight / patternScrollableAreaHeight);

                patternScrollbarRenderer.renderScrollbar(poseStackOrGraphics, scrollbarX, scrollbarY, scrollbarHeight,
                        patternPropertiesScrollOffset, patternMaxScrollRender, visibleRatio);
            }

        } finally {
            if (patternClip) Services.PLATFORM.disableScissor(poseStackOrGraphics);
        }

        float standardScale = BuildScapeConfigScreen.getStandardTextScale();

        drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.default_properties").getString(), defaultBoxX + 10, defaultBoxY + 5, standardScale);
        drawScaledText(poseStackOrGraphics, ComponentHelper.translatable("buildscape.config.particles.pattern_properties").getString(), patternBoxX + 10, patternBoxY + 5, standardScale);
    }

    @Override
    public void renderTooltips(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (sharedColorPicker != null) {
            sharedColorPicker.visible = false;

            if (selectedColorIndex >= 0 && selectedColorIndex < 7) {
                int pickerPadding = 10;
                int swatchSize = 20;
                int rowSpacing = 4;
                int numSwatches = 7;
                int numRows = (numSwatches + 1) / 2;
                int swatchAreaHeight = (numRows * swatchSize) + ((numRows - 1) * rowSpacing);
                int colorPadding = 10;

                int availableY = colorBoxY + colorPadding + swatchAreaHeight + 20;
                int pickerAvailableHeight = colorBoxY + colorBoxHeight - pickerPadding - availableY;
                int pickerAvailableWidth = colorBoxWidth - pickerPadding * 2;

                int idealWidth = 250;
                int idealHeight = 220;

                int pickerWidth = Math.min(idealWidth, pickerAvailableWidth);
                int pickerHeight = Math.min(idealHeight, pickerAvailableHeight);

                int pickerX = colorBoxX + (colorBoxWidth - pickerWidth) / 2;
                int pickerY = availableY + 15;

                if (pickerX < colorBoxX + pickerPadding) {
                    pickerX = colorBoxX + pickerPadding;
                    pickerWidth = Math.min(pickerWidth, colorBoxX + colorBoxWidth - pickerPadding - pickerX);
                }
                if (pickerY + pickerHeight > colorBoxY + colorBoxHeight - pickerPadding) {
                    pickerHeight = colorBoxY + colorBoxHeight - pickerPadding - pickerY;
                }

                sharedColorPicker.x = pickerX;
                sharedColorPicker.y = pickerY;
                sharedColorPicker.setWidth(pickerWidth);
                sharedColorPicker.setHeight(pickerHeight);

                Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
                Services.PLATFORM.translateGuiPose(poseStackOrGraphics, 0, 0, 500);
                sharedColorPicker.visible = true;
                sharedColorPicker.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
                sharedColorPicker.visible = false;
                Services.PLATFORM.popGuiPose(poseStackOrGraphics);
            }
        }
    }

    private static final double FIELD_MIN_VALUE = 0.001;

    private static void drawScaledText(Object context, String text, int x, int y, float scale) {
        Services.PLATFORM.pushGuiPose(context);
        Services.PLATFORM.scaleGuiPose(context, scale, scale);
        Services.PLATFORM.draw(context, Minecraft.getInstance().font, text, x / scale, y / scale, 0xFFFFFF);
        Services.PLATFORM.popGuiPose(context);
    }

    private static final long SAVE_DEBOUNCE_MS = 400L;
    private boolean configSavePending = false;
    private long lastConfigChangeMs = 0L;

    private void requestConfigSave() {
        configSavePending = true;
        lastConfigChangeMs = System.currentTimeMillis();
    }

    private void flushConfigSave(boolean force) {
        if (!configSavePending) {
            return;
        }
        if (!force && System.currentTimeMillis() - lastConfigChangeMs < SAVE_DEBOUNCE_MS) {
            return;
        }
        configSavePending = false;
        PillarParticleConfig.get().saveProperties();
    }

    private void updateConfigFromFields() {
        PillarParticleConfig config = PillarParticleConfig.get();
        boolean changed = false;

        if (!config.use_pattern) {
            try {
                double speed = Math.max(FIELD_MIN_VALUE, Double.parseDouble(particleSpeedField.getValue()));
                if (config.particle_speed != speed) {
                    config.particle_speed = speed;
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }

            try {
                double spread = Math.max(FIELD_MIN_VALUE, Double.parseDouble(particleSpreadField.getValue()));
                if (config.particle_spread != spread) {
                    config.particle_spread = spread;
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }

            try {
                int lifetime = Math.max(1, Integer.parseInt(particleLifetimeField.getValue()));
                if (config.particle_lifetime != lifetime) {
                    config.particle_lifetime = lifetime;
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }

            try {
                int density = Math.max(1, Integer.parseInt(particleDensityField.getValue()));
                if (config.particle_density != density) {
                    config.particle_density = density;
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }
        }

        if (config.use_pattern) {
            try {
                double speed = Math.max(FIELD_MIN_VALUE, Double.parseDouble(patternSpeedField.getValue()));
                if (config.pattern_speed != speed) {
                    config.pattern_speed = speed;
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }

            try {
                double spread = Math.max(FIELD_MIN_VALUE, Double.parseDouble(patternSpreadField.getValue()));
                if (config.pattern_spread != spread) {
                    config.pattern_spread = spread;
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }

            try {
                double intensity = Math.max(FIELD_MIN_VALUE, Double.parseDouble(patternIntensityField.getValue()));
                if (config.pattern_intensity != intensity) {
                    config.pattern_intensity = intensity;
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }
        }

        if (changed) {
            requestConfigSave();
        }
    }

    @Override
    public void onClose() {
        updateConfigFromFields();
        flushConfigSave(true);
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int totalContentHeight = getDefaultPropertiesTotalHeight();
        int totalContentHeightDefault = getDefaultPropertiesTotalHeight();
        int availableHeightDefault = defaultBoxHeight - UI_PADDING * 2;
        double maxScrollDefault = Math.max(0, totalContentHeightDefault - availableHeightDefault);

        if (maxScrollDefault > 0 && button == 0) {
            int scrollbarX = defaultBoxX + defaultBoxWidth - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_MARGIN;
            int scrollbarY = defaultBoxY + HEADER_CLIP;
            int scrollbarHeight = defaultBoxHeight - HEADER_CLIP - UI_PADDING;
            double visibleRatio = availableHeightDefault / (double) totalContentHeightDefault;

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

        if (colorSwatchButtons != null) {
            for (int i = 0; i < colorSwatchButtons.size(); i++) {
                ColorSwatchButton swatch = colorSwatchButtons.get(i);
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

        if (colorHexFields != null) {
            for (EditBox hexField : colorHexFields) {
                if (Services.PLATFORM.widgetMouseClicked(hexField, mouseX, mouseY, button)) {
                    activeDraggingPicker = null;
                    return true;
                }
            }
        }

        if (sharedColorPicker != null) {
            boolean wasVisible = sharedColorPicker.visible;
            sharedColorPicker.visible = true;
            if (sharedColorPicker.mouseClicked(mouseX, mouseY, button)) {
                sharedColorPicker.visible = wasVisible;
                activeDraggingPicker = sharedColorPicker;
                return true;
            }
            sharedColorPicker.visible = wasVisible;
        }


        if (usePatternToggle != null) {
            boolean wasVisible = usePatternToggle.visible;
            usePatternToggle.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(usePatternToggle, mouseX, mouseY, button)) {
                usePatternToggle.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            usePatternToggle.visible = wasVisible;
        }

        if (patternSelector != null && patternSelector.active) {
            boolean wasVisible = patternSelector.visible;
            patternSelector.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(patternSelector, mouseX, mouseY, button)) {
                patternSelector.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            patternSelector.visible = wasVisible;
        }

        if (particleSpeedField != null) {
            boolean wasVisible = particleSpeedField.visible;
            particleSpeedField.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(particleSpeedField, mouseX, mouseY, button)) {
                particleSpeedField.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            particleSpeedField.visible = wasVisible;
        }
        if (particleSpreadField != null) {
            boolean wasVisible = particleSpreadField.visible;
            particleSpreadField.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(particleSpreadField, mouseX, mouseY, button)) {
                particleSpreadField.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            particleSpreadField.visible = wasVisible;
        }
        if (particleLifetimeField != null) {
            boolean wasVisible = particleLifetimeField.visible;
            particleLifetimeField.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(particleLifetimeField, mouseX, mouseY, button)) {
                particleLifetimeField.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            particleLifetimeField.visible = wasVisible;
        }
        if (particleDensityField != null) {
            boolean wasVisible = particleDensityField.visible;
            particleDensityField.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(particleDensityField, mouseX, mouseY, button)) {
                particleDensityField.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            particleDensityField.visible = wasVisible;
        }
        if (patternSpeedField != null) {
            boolean wasVisible = patternSpeedField.visible;
            patternSpeedField.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(patternSpeedField, mouseX, mouseY, button)) {
                patternSpeedField.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            patternSpeedField.visible = wasVisible;
        }
        if (patternSpreadField != null) {
            boolean wasVisible = patternSpreadField.visible;
            patternSpreadField.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(patternSpreadField, mouseX, mouseY, button)) {
                patternSpreadField.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            patternSpreadField.visible = wasVisible;
        }
        if (patternIntensityField != null) {
            boolean wasVisible = patternIntensityField.visible;
            patternIntensityField.visible = true;
            if (Services.PLATFORM.widgetMouseClicked(patternIntensityField, mouseX, mouseY, button)) {
                patternIntensityField.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            patternIntensityField.visible = wasVisible;
        }

        if (maxParticleColorSlider != null && maxParticleColorSlider.active) {
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

        activeDraggingPicker = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (sharedColorPicker != null) {
            boolean wasVisible = sharedColorPicker.visible;
            sharedColorPicker.visible = true;
            boolean result = sharedColorPicker.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            sharedColorPicker.visible = wasVisible;
            if (result) {
                activeDraggingPicker = sharedColorPicker;
                return true;
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

        if (isDraggingSlider && maxParticleColorSlider != null && maxParticleColorSlider.active) {
            boolean wasVisible = maxParticleColorSlider.visible;
            maxParticleColorSlider.visible = true;
            if (maxParticleColorSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                maxParticleColorSlider.visible = wasVisible;
                return true;
            }
            maxParticleColorSlider.visible = wasVisible;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (defaultScrollbarRenderer.handleMouseRelease(button))
            return true;
        if (patternScrollbarRenderer.handleMouseRelease(button))
            return true;
        if (colorScrollbarRenderer.handleMouseRelease(button))
            return true;

        if (activeDraggingPicker != null) {
            activeDraggingPicker.mouseReleased(mouseX, mouseY, button);
            activeDraggingPicker = null;
            isDraggingSlider = false;
            return true;
        }

        if (isDraggingSlider && maxParticleColorSlider != null && maxParticleColorSlider.active) {
            boolean wasVisible = maxParticleColorSlider.visible;
            maxParticleColorSlider.visible = true;
            if (maxParticleColorSlider.mouseReleased(mouseX, mouseY, button)) {
                maxParticleColorSlider.visible = wasVisible;
                isDraggingSlider = false;
                return true;
            }
            maxParticleColorSlider.visible = wasVisible;
        }

        if (sharedColorPicker != null) {
            boolean wasVisible = sharedColorPicker.visible;
            sharedColorPicker.visible = true;
            if (sharedColorPicker.mouseReleased(mouseX, mouseY, button)) {
                sharedColorPicker.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            sharedColorPicker.visible = wasVisible;
        }

        if (activeDraggingPicker != null) {
            activeDraggingPicker = null;
        }
        isDraggingSlider = false;
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (sharedColorPicker != null) {
            if (sharedColorPicker.rField != null && sharedColorPicker.rField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (Services.PLATFORM.widgetKeyPressed(sharedColorPicker.rField, keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.gField != null && sharedColorPicker.gField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (Services.PLATFORM.widgetKeyPressed(sharedColorPicker.gField, keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.bField != null && sharedColorPicker.bField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (Services.PLATFORM.widgetKeyPressed(sharedColorPicker.bField, keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.hField != null && sharedColorPicker.hField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (Services.PLATFORM.widgetKeyPressed(sharedColorPicker.hField, keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.sField != null && sharedColorPicker.sField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (Services.PLATFORM.widgetKeyPressed(sharedColorPicker.sField, keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
            if (sharedColorPicker.brightnessField != null && sharedColorPicker.brightnessField.isFocused()) {
                boolean wasVisible = sharedColorPicker.visible;
                sharedColorPicker.visible = true;
                if (Services.PLATFORM.widgetKeyPressed(sharedColorPicker.brightnessField, keyCode, scanCode, modifiers)) {
                    sharedColorPicker.visible = wasVisible;
                    return true;
                }
                sharedColorPicker.visible = wasVisible;
            }
        }

        if (colorHexFields != null) {
            for (EditBox hexField : colorHexFields) {
                if (hexField.isFocused()) {
                    boolean wasVisible = hexField.visible;
                    hexField.visible = true;
                    if (Services.PLATFORM.widgetKeyPressed(hexField, keyCode, scanCode, modifiers)) {
                        hexField.visible = wasVisible;
                        return true;
                    }
                    hexField.visible = wasVisible;
                }
            }
        }

        if (particleSpeedField != null && particleSpeedField.isFocused()) {
            boolean wasVisible = particleSpeedField.visible;
            particleSpeedField.visible = true;
            if (Services.PLATFORM.widgetKeyPressed(particleSpeedField, keyCode, scanCode, modifiers)) {
                particleSpeedField.visible = wasVisible;
                return true;
            }
            particleSpeedField.visible = wasVisible;
        }
        if (particleSpreadField != null && particleSpreadField.isFocused()) {
            boolean wasVisible = particleSpreadField.visible;
            particleSpreadField.visible = true;
            if (Services.PLATFORM.widgetKeyPressed(particleSpreadField, keyCode, scanCode, modifiers)) {
                particleSpreadField.visible = wasVisible;
                return true;
            }
            particleSpreadField.visible = wasVisible;
        }
        if (particleLifetimeField != null && particleLifetimeField.isFocused()) {
            boolean wasVisible = particleLifetimeField.visible;
            particleLifetimeField.visible = true;
            if (Services.PLATFORM.widgetKeyPressed(particleLifetimeField, keyCode, scanCode, modifiers)) {
                particleLifetimeField.visible = wasVisible;
                return true;
            }
            particleLifetimeField.visible = wasVisible;
        }
        if (particleDensityField != null && particleDensityField.isFocused()) {
            boolean wasVisible = particleDensityField.visible;
            particleDensityField.visible = true;
            if (Services.PLATFORM.widgetKeyPressed(particleDensityField, keyCode, scanCode, modifiers)) {
                particleDensityField.visible = wasVisible;
                return true;
            }
            particleDensityField.visible = wasVisible;
        }
        if (patternSpeedField != null && patternSpeedField.isFocused()) {
            boolean wasVisible = patternSpeedField.visible;
            patternSpeedField.visible = true;
            if (Services.PLATFORM.widgetKeyPressed(patternSpeedField, keyCode, scanCode, modifiers)) {
                patternSpeedField.visible = wasVisible;
                return true;
            }
            patternSpeedField.visible = wasVisible;
        }
        if (patternSpreadField != null && patternSpreadField.isFocused()) {
            boolean wasVisible = patternSpreadField.visible;
            patternSpreadField.visible = true;
            if (Services.PLATFORM.widgetKeyPressed(patternSpreadField, keyCode, scanCode, modifiers)) {
                patternSpreadField.visible = wasVisible;
                return true;
            }
            patternSpreadField.visible = wasVisible;
        }
        if (patternIntensityField != null && patternIntensityField.isFocused()) {
            boolean wasVisible = patternIntensityField.visible;
            patternIntensityField.visible = true;
            if (Services.PLATFORM.widgetKeyPressed(patternIntensityField, keyCode, scanCode, modifiers)) {
                patternIntensityField.visible = wasVisible;
                return true;
            }
            patternIntensityField.visible = wasVisible;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= defaultBoxX && mouseX <= defaultBoxX + defaultBoxWidth &&
                mouseY >= defaultBoxY && mouseY <= defaultBoxY + defaultBoxHeight) {
            int totalContentHeight = getDefaultPropertiesTotalHeight();
            int availableHeight = defaultBoxHeight - UI_PADDING * 2;
            double maxScroll = Math.max(0, totalContentHeight - availableHeight);

            if (maxScroll > 0) {
                defaultPropertiesScrollOffset -= delta * 10;
                defaultPropertiesScrollOffset = Math.max(0, Math.min(maxScroll, defaultPropertiesScrollOffset));
                updateDefaultPropertiesPositions();
                return true;
            }
        }

        if (mouseX >= patternBoxX && mouseX <= patternBoxX + patternBoxWidth &&
                mouseY >= patternBoxY && mouseY <= patternBoxY + patternBoxHeight) {
            int patternTotalContentHeight = getPatternPropertiesTotalHeight();
            int patternAvailableHeight = patternBoxHeight - UI_PADDING * 2;
            double patternMaxScroll = Math.max(0, patternTotalContentHeight - patternAvailableHeight);

            if (patternMaxScroll > 0) {
                patternPropertiesScrollOffset -= delta * 10;
                patternPropertiesScrollOffset = Math.max(0, Math.min(patternMaxScroll, patternPropertiesScrollOffset));
                updatePatternPropertiesPositions();
                return true;
            }
        }

        if (mouseX >= colorBoxX && mouseX <= colorBoxX + colorBoxWidth &&
                mouseY >= colorBoxY && mouseY <= colorBoxY + colorBoxHeight) {
            int colorTotalContentHeight = getColorSwatchesTotalHeight();
            int colorAvailableHeight = colorBoxHeight - UI_PADDING * 2;
            double colorMaxScroll = Math.max(0, colorTotalContentHeight - colorAvailableHeight);

            if (colorMaxScroll > 0) {
                colorSwatchesScrollOffset -= delta * 10;
                colorSwatchesScrollOffset = Math.max(0, Math.min(colorMaxScroll, colorSwatchesScrollOffset));
                updateColorSwatchesPositions();
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (colorHexFields != null) {
            for (EditBox hexField : colorHexFields) {
                if (hexField.isFocused()) {
                    boolean wasVisible = hexField.visible;
                    hexField.visible = true;
                    if (Services.PLATFORM.widgetCharTyped(hexField, codePoint, modifiers)) {
                        hexField.visible = wasVisible;
                        return true;
                    }
                    hexField.visible = wasVisible;
                }
            }
        }

        if (particleSpeedField != null && particleSpeedField.isFocused()) {
            boolean wasVisible = particleSpeedField.visible;
            particleSpeedField.visible = true;
            if (Services.PLATFORM.widgetCharTyped(particleSpeedField, codePoint, modifiers)) {
                particleSpeedField.visible = wasVisible;
                return true;
            }
            particleSpeedField.visible = wasVisible;
        }
        if (particleSpreadField != null && particleSpreadField.isFocused()) {
            boolean wasVisible = particleSpreadField.visible;
            particleSpreadField.visible = true;
            if (Services.PLATFORM.widgetCharTyped(particleSpreadField, codePoint, modifiers)) {
                particleSpreadField.visible = wasVisible;
                return true;
            }
            particleSpreadField.visible = wasVisible;
        }
        if (particleLifetimeField != null && particleLifetimeField.isFocused()) {
            boolean wasVisible = particleLifetimeField.visible;
            particleLifetimeField.visible = true;
            if (Services.PLATFORM.widgetCharTyped(particleLifetimeField, codePoint, modifiers)) {
                particleLifetimeField.visible = wasVisible;
                return true;
            }
            particleLifetimeField.visible = wasVisible;
        }
        if (particleDensityField != null && particleDensityField.isFocused()) {
            boolean wasVisible = particleDensityField.visible;
            particleDensityField.visible = true;
            if (Services.PLATFORM.widgetCharTyped(particleDensityField, codePoint, modifiers)) {
                particleDensityField.visible = wasVisible;
                return true;
            }
            particleDensityField.visible = wasVisible;
        }
        if (patternSpeedField != null && patternSpeedField.isFocused()) {
            boolean wasVisible = patternSpeedField.visible;
            patternSpeedField.visible = true;
            if (Services.PLATFORM.widgetCharTyped(patternSpeedField, codePoint, modifiers)) {
                patternSpeedField.visible = wasVisible;
                return true;
            }
            patternSpeedField.visible = wasVisible;
        }
        if (patternSpreadField != null && patternSpreadField.isFocused()) {
            boolean wasVisible = patternSpreadField.visible;
            patternSpreadField.visible = true;
            if (Services.PLATFORM.widgetCharTyped(patternSpreadField, codePoint, modifiers)) {
                patternSpreadField.visible = wasVisible;
                return true;
            }
            patternSpreadField.visible = wasVisible;
        }
        if (patternIntensityField != null && patternIntensityField.isFocused()) {
            boolean wasVisible = patternIntensityField.visible;
            patternIntensityField.visible = true;
            if (Services.PLATFORM.widgetCharTyped(patternIntensityField, codePoint, modifiers)) {
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
