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

    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer defaultScrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();
    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer patternScrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();
    private final com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer colorScrollbarRenderer = new com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer();

    public PillarParticlesConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    private Component getUsePatternMessage(boolean value) {
        TextComponent base = new TextComponent("");
        base.append(new TextComponent("Use Pattern : ").withStyle(style -> style.withColor(TextColor.fromRgb(0x5555FF))));
        if (value) {
            base.append(new TextComponent("True").withStyle(style -> style.withColor(TextColor.fromRgb(0x00FF00))));
        } else {
            base.append(new TextComponent("False").withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000))));
        }
        return base;
    }

    private Component getPatternMessage(String pattern) {
        TextComponent base = new TextComponent("");
        base.append(new TextComponent("Pattern : ").withStyle(style -> style.withColor(TextColor.fromRgb(0x5555FF))));

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

        currentPatternIndex = findPatternIndex(config.pattern);
        currentMaxColor = Math.max(1, Math.min(7, config.max_particle_color));

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton usePatternBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0,
                100, 20,
                getUsePatternMessage(config.use_pattern),
                (btn) -> toggleUsePattern());
        usePatternBtn.setCustomTextColors(0, 0);
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

        colorSwatchButtons = new ArrayList<>();
        colorHexFields = new ArrayList<>();
        sharedColorPicker = null;

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton patternSelectorBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0,
                100, 20,
                getPatternMessage(config.pattern),
                (btn) -> cyclePattern());
        patternSelectorBtn.setCustomTextColors(0, 0);
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
        config.saveProperties();

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

        while (config.particle_color.size() < 7) {
            config.particle_color.add("#FFFFFF");
        }

        int startY = colorBoxY + padding + 25;
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
                    net.minecraft.network.chat.TextComponent.EMPTY);
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

        String hexValue = config.particle_color.get(colorIndex);
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
        try {
            java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                    .getDeclaredField("height");
            heightField.setAccessible(true);
            heightField.setInt(editBox, height);
        } catch (Exception e) {
        }
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

        particleSpeedField.y = defaultBaseFirstFieldY - scrollOffsetInt;
        particleSpreadField.y = defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) - scrollOffsetInt;
        particleLifetimeField.y = defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) * 2 - scrollOffsetInt;
        particleDensityField.y = defaultBaseFirstFieldY + (FIELD_HEIGHT + COMPONENT_SPACING) * 3 - scrollOffsetInt;
        usePatternToggle.y = defaultBaseButtonY - scrollOffsetInt;
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

        colorsResetButton.x = colorBoxX + colorBoxWidth - 20 - 2;
        colorsResetButton.y = colorBoxY + 2;

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


        patternSelector.x = patternButtonStartX;
        patternSelector.setWidth(patternButtonWidth);

        int finalPatternFieldWidth = Math.min(patternFieldWidth, patternComponentEndX - patternFieldX);
        if (finalPatternFieldWidth < 0)
            finalPatternFieldWidth = 0;

        maxParticleColorSlider.x = patternFieldX;
        maxParticleColorSlider.setWidth(finalPatternFieldWidth);

        patternSpeedField.x = patternFieldX;
        patternSpeedField.setWidth(finalPatternFieldWidth);

        patternSpreadField.x = patternFieldX;
        patternSpreadField.setWidth(finalPatternFieldWidth);

        patternIntensityField.x = patternFieldX;
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
        while (config.particle_color.size() <= index) {
            config.particle_color.add("#FFFFFF");
        }
        config.particle_color.set(index, hexColor);
        config.saveProperties();

        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(new com.kingodogo.buildscape.network.UpdateConfigPacket(config));

        updateWorldPillars();
    }

    private void updateWorldPillars() {
        Minecraft mc = Minecraft.getInstance();
        PillarIdManager manager = PillarIdManager.get();
        if (mc.level != null && mc.player != null) {
            int renderDistance = mc.options.renderDistance;
            int range = 32;

            net.minecraft.world.level.ChunkPos center = mc.player.chunkPosition();

            for (int x = center.x - range; x <= center.x + range; x++) {
                for (int z = center.z - range; z <= center.z + range; z++) {
                    if (mc.level.hasChunk(x, z)) {
                        net.minecraft.world.level.chunk.LevelChunk chunk = mc.level.getChunk(x, z);
                        for (BlockEntity be : chunk.getBlockEntities().values()) {
                            if (be instanceof PillarBlockEntity pbe) {
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
        config.saveProperties();

        PillarIdManager manager = PillarIdManager.get();
        if (manager.hasLoaded()) {
            for (PillarIdManager.PillarData pData : manager.getAllData()) {
                boolean hasPatternOverride = pData.pattern != null && !pData.pattern.equals("default");
                boolean isCustomized = pData.hasColors() || hasPatternOverride;

                if (isCustomized && (pData.pattern == null || pData.pattern.equals("default"))) {
                    pData.pattern = oldPattern != null ? oldPattern : "ring";
                }
            }
        }

        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(new com.kingodogo.buildscape.network.UpdateConfigPacket(config));

        updateWorldPillars();

        if (usePatternToggle != null) {
            usePatternToggle.setMessage(getUsePatternMessage(true));
        }

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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
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
        GuiComponent.fill(poseStack, defaultBoxX, defaultBoxY, defaultBoxX + defaultBoxWidth, defaultBoxY + 1,
                borderColor);
        GuiComponent.fill(poseStack, defaultBoxX, defaultBoxY + defaultBoxHeight - 1, defaultBoxX + defaultBoxWidth,
                defaultBoxY + defaultBoxHeight, borderColor);
        GuiComponent.fill(poseStack, defaultBoxX, defaultBoxY, defaultBoxX + 1, defaultBoxY + defaultBoxHeight,
                borderColor);
        GuiComponent.fill(poseStack, defaultBoxX + defaultBoxWidth - 1, defaultBoxY, defaultBoxX + defaultBoxWidth,
                defaultBoxY + defaultBoxHeight, borderColor);

        int bottomOffset = Math.max(5, (int) (windowHeight * 0.01 / guiScale));

        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int textYOffset = (20 - (int)(mcInstance.font.lineHeight * textScale)) / 2 + 1;


        int scissorX = (int) (defaultBoxX * guiScale);
        int scissorY = (int) (windowHeight - (defaultBoxY + defaultBoxHeight) * guiScale + bottomOffset * guiScale);
        int scissorWidth = (int) (defaultBoxWidth * guiScale);
        int scissorHeight = (int) (defaultBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        if (scissorHeight > 0)
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        int defaultTextX = defaultBoxX + padding;

        int labelYOffset = (FIELD_HEIGHT - (int) (Minecraft.getInstance().font.lineHeight * textScale)) / 2;
        int totalContentHeightDefault = getDefaultPropertiesTotalHeight();
        int availableHeightDefault = defaultBoxHeight - UI_PADDING * 2;
        double maxScrollDefault = Math.max(0, totalContentHeightDefault - availableHeightDefault);
        boolean needsScrollbarDefault = maxScrollDefault > 0;

        int headerBottom = defaultBoxY + HEADER_CLIP;
        int panelTop = defaultBoxY;
        int panelBottom = defaultBoxY + defaultBoxHeight;

        boolean particleSpeedRowVisible = particleSpeedField.y + FIELD_HEIGHT > headerBottom
                && particleSpeedField.y < panelBottom;
        boolean particleSpreadRowVisible = particleSpreadField.y + FIELD_HEIGHT > headerBottom
                && particleSpreadField.y < panelBottom;
        boolean particleLifetimeRowVisible = particleLifetimeField.y + FIELD_HEIGHT > headerBottom
                && particleLifetimeField.y < panelBottom;
        boolean particleDensityRowVisible = particleDensityField.y + FIELD_HEIGHT > headerBottom
                && particleDensityField.y < panelBottom;

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

        usePatternToggle.visible = false;
        particleSpeedField.visible = false;
        particleSpreadField.visible = false;
        particleLifetimeField.visible = false;
        particleDensityField.visible = false;



        if (usePatternToggle.y + BUTTON_HEIGHT > headerBottom && usePatternToggle.y < panelBottom) {
            usePatternToggle.visible = true;
            usePatternToggle.render(poseStack, mouseX, mouseY, partialTick);
            usePatternToggle.visible = false;
        }
        int textPadding = BuildScapeConfigScreen.scaleSize(4);
        int fontHeight = Minecraft.getInstance().font.lineHeight;

        if (particleSpeedRowVisible) {
            particleSpeedField.visible = true;
            particleSpeedField.render(poseStack, mouseX, mouseY, partialTick);
            particleSpeedField.visible = false;
        }
        if (particleSpreadRowVisible) {
            particleSpreadField.visible = true;
            particleSpreadField.render(poseStack, mouseX, mouseY, partialTick);
            particleSpreadField.visible = false;
        }
        if (particleLifetimeRowVisible) {
            particleLifetimeField.visible = true;
            particleLifetimeField.render(poseStack, mouseX, mouseY, partialTick);
            particleLifetimeField.visible = false;
        }
        if (particleDensityRowVisible) {
            particleDensityField.visible = true;
            particleDensityField.render(poseStack, mouseX, mouseY, partialTick);
            particleDensityField.visible = false;
        }

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


        scissorX = (int) (colorBoxX * guiScale);
        scissorY = (int) (windowHeight - (colorBoxY + colorBoxHeight) * guiScale + bottomOffset * guiScale);
        scissorWidth = (int) (colorBoxWidth * guiScale);
        scissorHeight = (int) (colorBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        int colorBorderColor = 0xFF666666;
        GuiComponent.fill(poseStack, colorBoxX, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + 1, colorBorderColor);
        GuiComponent.fill(poseStack, colorBoxX, colorBoxY + colorBoxHeight - 1, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, colorBorderColor);
        GuiComponent.fill(poseStack, colorBoxX, colorBoxY, colorBoxX + 1, colorBoxY + colorBoxHeight, colorBorderColor);
        GuiComponent.fill(poseStack, colorBoxX + colorBoxWidth - 1, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, colorBorderColor);

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);



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

                String hexValue = i < config.particle_color.size() ? config.particle_color.get(i) : "#FFFFFF";
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
                swatchButton.render(poseStack, mouseX, mouseY, partialTick);
                swatchButton.visible = false;

                colorHexFields.get(i).visible = true;
                colorHexFields.get(i).render(poseStack, mouseX, mouseY, partialTick);
                colorHexFields.get(i).visible = false;
            }

            if (colorNeedsScrollbar && colorMaxScroll > 0) {
                int scrollbarX = colorBoxX + colorBoxWidth - CustomScrollbarRenderer.getScrollbarWidth() - 5;
                int scrollbarY = colorBoxY + UI_PADDING + COLOR_HEADER_SPACE;
                int scrollbarHeight = colorAvailableHeight - COLOR_HEADER_SPACE;

                double visibleRatio = (double) scrollbarHeight / (colorTotalContentHeight - COLOR_HEADER_SPACE);

                colorScrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                        colorSwatchesScrollOffset, colorMaxScroll, visibleRatio);
            }
        }


        RenderSystem.disableScissor();

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

        poseStack.popPose();
        poseStack.pushPose();
        poseStack.popPose();

        int patternBorderColor = 0xFF666666;
        GuiComponent.fill(poseStack, patternBoxX, patternBoxY, patternBoxX + patternBoxWidth, patternBoxY + 1,
                patternBorderColor);
        GuiComponent.fill(poseStack, patternBoxX, patternBoxY + patternBoxHeight - 1, patternBoxX + patternBoxWidth,
                patternBoxY + patternBoxHeight, patternBorderColor);
        GuiComponent.fill(poseStack, patternBoxX, patternBoxY, patternBoxX + 1, patternBoxY + patternBoxHeight,
                patternBorderColor);
        GuiComponent.fill(poseStack, patternBoxX + patternBoxWidth - 1, patternBoxY, patternBoxX + patternBoxWidth,
                patternBoxY + patternBoxHeight, patternBorderColor);

        scissorX = (int) (patternBoxX * guiScale);
        scissorY = (int) (windowHeight - (patternBoxY + patternBoxHeight) * guiScale + bottomOffset * guiScale);
        scissorWidth = (int) (patternBoxWidth * guiScale);
        scissorHeight = (int) (patternBoxHeight * guiScale - bottomOffset * guiScale - HEADER_CLIP * guiScale);
        if (scissorHeight > 0)
            RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

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


        boolean patternSelectorVisible = patternSelector.y + BUTTON_HEIGHT > patternHeaderBottom
                && patternSelector.y < patternPanelBottom;

        boolean maxParticlesRowVisible = maxParticleColorSlider.y + SLIDER_HEIGHT > patternHeaderBottom
                && maxParticleColorSlider.y < patternPanelBottom;

        boolean patternSpeedRowVisible = patternSpeedField.y + FIELD_HEIGHT > patternHeaderBottom
                && patternSpeedField.y < patternPanelBottom;

        boolean patternSpreadRowVisible = patternSpreadField.y + FIELD_HEIGHT > patternHeaderBottom
                && patternSpreadField.y < patternPanelBottom;

        boolean patternIntensityRowVisible = patternIntensityField.y + FIELD_HEIGHT > patternHeaderBottom
                && patternIntensityField.y < patternPanelBottom;

        int maxParticleLabelY = maxParticleColorSlider.y + patternLabelYOffset;
        if (maxParticlesRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack, "Max Particle's ", patternTextX / textScale, maxParticleLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        int patternSpeedLabelY = patternSpeedField.y + patternLabelYOffset;
        if (patternSpeedRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.pattern_speed").getString() + " ",
                    patternTextX / textScale, patternSpeedLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        int patternSpreadLabelY = patternSpreadField.y + patternLabelYOffset;
        if (patternSpreadRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.pattern_spread").getString() + " ",
                    patternTextX / textScale, patternSpreadLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        int patternIntensityLabelY = patternIntensityField.y + patternLabelYOffset;
        if (patternIntensityRowVisible) {
            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);
            Minecraft.getInstance().font.draw(poseStack,
                    new TranslatableComponent("buildscape.config.particles.pattern_intensity").getString() + " ",
                    patternTextX / textScale, patternIntensityLabelY / textScale, 0xFFFFFF);
            poseStack.popPose();
        }

        patternSelector.visible = false;
        patternSpeedField.visible = false;
        patternSpreadField.visible = false;
        patternIntensityField.visible = false;
        maxParticleColorSlider.visible = false;

        if (patternSelectorVisible) {
            patternSelector.visible = true;
            patternSelector.render(poseStack, mouseX, mouseY, partialTick);
            patternSelector.visible = false;
        }
        if (maxParticlesRowVisible) {
            maxParticleColorSlider.visible = true;
            maxParticleColorSlider.render(poseStack, mouseX, mouseY, partialTick);
            maxParticleColorSlider.visible = false;
        }


        if (patternSpeedRowVisible) {
            patternSpeedField.visible = true;
            patternSpeedField.render(poseStack, mouseX, mouseY, partialTick);
            patternSpeedField.visible = false;
        }
        if (patternSpreadRowVisible) {
            patternSpreadField.visible = true;
            patternSpreadField.render(poseStack, mouseX, mouseY, partialTick);
            patternSpreadField.visible = false;
        }
        if (patternIntensityRowVisible) {
            patternIntensityField.visible = true;
            patternIntensityField.render(poseStack, mouseX, mouseY, partialTick);
            patternIntensityField.visible = false;
        }

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

        float standardScale = BuildScapeConfigScreen.getStandardTextScale();

        poseStack.pushPose();
        poseStack.translate(defaultBoxX + 10, defaultBoxY + 5, 0);
        poseStack.scale(standardScale, standardScale, 1.0f);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.default_properties"), 0, 0, 0xFFFFFF);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(patternBoxX + 10, patternBoxY + 5, 0);
        poseStack.scale(standardScale, standardScale, 1.0f);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_properties"), 0, 0, 0xFFFFFF);
        poseStack.popPose();


        updateConfigFromFields();
    }

    @Override
    public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
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

                poseStack.pushPose();
                poseStack.translate(0, 0, 500);
                sharedColorPicker.visible = true;
                sharedColorPicker.renderButton(poseStack, mouseX, mouseY, partialTick);
                sharedColorPicker.visible = false;
                poseStack.popPose();
            }
        }
    }

    private static final double FIELD_MIN_VALUE = 0.001;

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
            config.saveProperties();

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

        if (colorHexFields != null) {
            for (EditBox hexField : colorHexFields) {
                if (hexField.mouseClicked(mouseX, mouseY, button)) {
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
            if (usePatternToggle.mouseClicked(mouseX, mouseY, button)) {
                usePatternToggle.visible = wasVisible;
                activeDraggingPicker = null;
                return true;
            }
            usePatternToggle.visible = wasVisible;
        }

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
                    if (hexField.charTyped(codePoint, modifiers)) {
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
