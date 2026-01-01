package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.ColorPickerWidget;
import com.kingodogo.buildscape.client.screen.widget.ColorSwatchButton;
import com.kingodogo.buildscape.client.screen.widget.IntSliderWidget;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PillarIdDetailConfigTab extends AbstractConfigTab {
    private static final String[] PATTERNS = {"none", "default", "beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"};
    private static final int MAX_COLORS = 5;
    
    private final String pillarId;
    private PillarIdManager.PillarData pillarData;
    
    private Button backButton;
    private Button patternSelector;
    private EditBox patternSpeedField;
    private EditBox patternSpreadField;
    private EditBox patternIntensityField;
    private IntSliderWidget maxParticleColorSlider;
    private List<ColorSwatchButton> colorSwatchButtons;
    private List<EditBox> colorHexFields;
    private ColorPickerWidget colorPicker;
    private int selectedColorIndex = -1;
    private int currentPatternIndex = 0;
    private int currentMaxColor = 5; // Current max color value (1-5)
    private int colorSectionY = 0; // Store color section Y position for render method
    
    public PillarIdDetailConfigTab(BuildScapeConfigScreen parent, String pillarId) {
        super(parent);
        this.pillarId = pillarId;
    }
    
    @Override
    public void init() {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();
        
        // Load pillar data - reload manager first to get latest data
        PillarIdManager manager = PillarIdManager.get();
        try {
            manager.checkAndReload(); // Reload to get latest pattern changes
        } catch (Exception e) {
            // Ignore reload errors
        }
        
        pillarData = manager.getPillarData(pillarId);
        if (pillarData == null) {
            // If pillar doesn't exist, go back to IDs tab
            parent.setActiveTab(new PillarIdsConfigTab(parent));
            return;
        }
        
        // Try to sync pattern from block entity if we're in the same dimension
        syncPatternFromBlockEntity(manager);
        
        // Reload pillar data after sync to get updated pattern
        pillarData = manager.getPillarData(pillarId);
        if (pillarData == null) {
            // If pillar doesn't exist, go back to IDs tab
            parent.setActiveTab(new PillarIdsConfigTab(parent));
            return;
        }
        
        // Get global config for defaults
        PillarParticleConfig globalConfig = PillarParticleConfig.get();
        
        // Calculate layout using percentages - better organized
        int padding = (int)(contentWidth * 0.02f); // 2% padding
        int leftPanelWidth = (int)(contentWidth * 0.48f); // 48% for left panel (config fields and colors)
        int rightPanelWidth = (int)(contentWidth * 0.48f); // 48% for right panel (color picker)
        int panelSpacing = (int)(contentWidth * 0.02f); // 2% spacing between panels
        
        int leftPanelX = contentX + padding;
        int rightPanelX = leftPanelX + leftPanelWidth + panelSpacing;
        
        // Back button - positioned at top left
        int backButtonWidth = Math.max(80, (int)(contentWidth * 0.10f));
        backButton = new Button(
            0, 0,
            backButtonWidth, 20,
            new TextComponent("← Back"),
            (btn) -> parent.setActiveTab(new PillarIdsConfigTab(parent))
        );
        addTabWidget(backButton);
        
        // Field sizing - responsive to content width
        int fieldHeight = 20;
        int fieldSpacing = 25; // Spacing between fields
        int startY = contentY + 80; // Start below title
        
        // Calculate label width and field position for proper alignment
        int labelWidth = Math.max(130, (int)(leftPanelWidth * 0.40f)); // 40% of left panel for labels
        int labelGap = 8; // Gap between label and field
        int fieldX = leftPanelX + labelWidth + labelGap;
        int maxFieldWidth = leftPanelWidth - labelWidth - labelGap - padding; // Available width for fields
        int fieldWidth = Math.max(100, maxFieldWidth); // Ensure minimum width
        
        // Pattern selector - always active (use updated pattern after sync)
        String pattern = pillarData.pattern != null ? pillarData.pattern : globalConfig.pattern;
        currentPatternIndex = findPatternIndex(pattern);
        int patternButtonWidth = Math.min(fieldWidth, (int)(leftPanelWidth * 0.60f));
        patternSelector = new com.kingodogo.buildscape.client.screen.widget.WideButton(
            fieldX, startY,
            patternButtonWidth, 20,
            new TranslatableComponent("buildscape.config.particles.pattern." + pattern),
            (btn) -> cyclePattern()
        );
        patternSelector.active = true; // Always active
        addTabWidget(patternSelector);
        startY += fieldSpacing;
        
        // Pattern speed - always editable
        double patternSpeed = pillarData.pattern_speed != null ? pillarData.pattern_speed : globalConfig.pattern_speed;
        patternSpeedField = new EditBox(
            Minecraft.getInstance().font,
            fieldX, startY,
            fieldWidth, fieldHeight,
            TextComponent.EMPTY // No placeholder text, label will be rendered separately
        );
        patternSpeedField.setValue(String.valueOf(patternSpeed));
        patternSpeedField.setEditable(true); // Always editable
        patternSpeedField.setBordered(true);
        patternSpeedField.setTextColor(0xFFFFFF);
        patternSpeedField.setTextColorUneditable(0xAAAAAA);
        patternSpeedField.setMaxLength(64);
        addTabWidget(patternSpeedField);
        startY += fieldSpacing;
        
        // Pattern spread - always editable
        double patternSpread = pillarData.pattern_spread != null ? pillarData.pattern_spread : globalConfig.pattern_spread;
        patternSpreadField = new EditBox(
            Minecraft.getInstance().font,
            fieldX, startY,
            fieldWidth, fieldHeight,
            TextComponent.EMPTY
        );
        patternSpreadField.setValue(String.valueOf(patternSpread));
        patternSpreadField.setEditable(true); // Always editable
        patternSpreadField.setBordered(true);
        patternSpreadField.setTextColor(0xFFFFFF);
        patternSpreadField.setTextColorUneditable(0xAAAAAA);
        patternSpreadField.setMaxLength(64);
        addTabWidget(patternSpreadField);
        startY += fieldSpacing;
        
        // Pattern intensity - always editable
        double patternIntensity = pillarData.pattern_intensity != null ? pillarData.pattern_intensity : globalConfig.pattern_intensity;
        patternIntensityField = new EditBox(
            Minecraft.getInstance().font,
            fieldX, startY,
            fieldWidth, fieldHeight,
            TextComponent.EMPTY
        );
        patternIntensityField.setValue(String.valueOf(patternIntensity));
        patternIntensityField.setEditable(true); // Always editable
        patternIntensityField.setBordered(true);
        patternIntensityField.setTextColor(0xFFFFFF);
        patternIntensityField.setTextColorUneditable(0xAAAAAA);
        patternIntensityField.setMaxLength(64);
        addTabWidget(patternIntensityField);
        startY += fieldSpacing;
        
        // Max particle color slider - always active
        int maxColor = pillarData.max_particle_color != null ? pillarData.max_particle_color : globalConfig.max_particle_color;
        currentMaxColor = Math.max(1, Math.min(MAX_COLORS, maxColor));
        maxParticleColorSlider = new IntSliderWidget(
            fieldX, startY,
            fieldWidth, 20,
            new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor),
            1, MAX_COLORS, currentMaxColor,
            (value) -> onMaxParticleColorChanged(value)
        );
        maxParticleColorSlider.active = true; // Always active
        addTabWidget(maxParticleColorSlider);
        startY += fieldSpacing + 10; // Extra spacing before color section
        
        // Color swatches section - better organized
        colorSectionY = startY;
        int swatchSize = 24; // Slightly larger for better visibility
        int swatchSpacing = 6;
        // Calculate hex field width - ensure it doesn't overflow
        int swatchAreaWidth = swatchSize + swatchSpacing; // Swatch + spacing
        int hexFieldWidth = Math.max(90, Math.min(120, (int)(leftPanelWidth * 0.35f))); // Max 35% of panel, min 90px
        int swatchEndX = leftPanelX + swatchAreaWidth + hexFieldWidth;
        
        // Initialize color lists
        colorSwatchButtons = new ArrayList<>();
        colorHexFields = new ArrayList<>();
        
        // Ensure we have at least the current colors, up to MAX_COLORS
        List<String> colors = pillarData.dyeColors != null ? new ArrayList<>(pillarData.dyeColors) : new ArrayList<>();
        while (colors.size() < MAX_COLORS) {
            colors.add("#FFFFFF");
        }
        
        // Create color swatches with hex fields
        for (int i = 0; i < MAX_COLORS; i++) {
            final int colorIndex = i;
            String colorCode = i < pillarData.dyeColors.size() ? pillarData.dyeColors.get(i) : "#FFFFFF";
            int color = 0xFFFFFF;
            try {
                if (colorCode.startsWith("#") && colorCode.length() == 7) {
                    color = Integer.parseInt(colorCode.substring(1), 16);
                }
            } catch (NumberFormatException e) {
                // Use default white
            }
            
            int swatchX = leftPanelX;
            int swatchY = colorSectionY + i * (swatchSize + swatchSpacing);
            
            // Create color swatch button
            ColorSwatchButton swatch = new ColorSwatchButton(
                swatchX, swatchY,
                swatchSize, swatchSize,
                color,
                (btn) -> onColorSwatchClicked(colorIndex)
            );
            colorSwatchButtons.add(swatch);
            addTabWidget(swatch);
            
            // Create hex field next to swatch
            EditBox hexField = new EditBox(
                Minecraft.getInstance().font,
                swatchX + swatchSize + swatchSpacing, swatchY,
                hexFieldWidth, fieldHeight,
                TextComponent.EMPTY
            );
            hexField.setValue(colorCode);
            hexField.setBordered(true);
            hexField.setTextColor(0xFFFFFF);
            hexField.setMaxLength(7); // #RRGGBB
            hexField.setResponder((text) -> {
                try {
                    String hexText = text;
                    if (!hexText.startsWith("#")) {
                        hexText = "#" + hexText;
                    }
                    if (hexText.length() == 7 && hexText.matches("#[0-9A-Fa-f]{6}")) {
                        int newColor = Integer.parseInt(hexText.substring(1), 16);
                        onColorChanged(colorIndex, hexText);
                        updateSwatchButtonColor(colorIndex, newColor);
                        if (selectedColorIndex == colorIndex && colorPicker != null) {
                            colorPicker.setColor(newColor);
                        }
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
        
        // Update swatches enabled state based on max value
        updateSwatchesEnabledState();
        
        // Create color picker (initially hidden) - positioned in right panel
        int pickerWidth = Math.min(280, (int)(rightPanelWidth * 0.90f)); // 90% of right panel width
        int pickerHeight = 240;
        // Position picker in the right panel, aligned with color swatches
        int pickerX = rightPanelX + padding;
        int pickerY = colorSectionY; // Align with first color swatch
        
        colorPicker = new ColorPickerWidget(
            pickerX, pickerY,
            pickerWidth, pickerHeight,
            0xFFFFFF,
            (hexColor) -> {
                if (selectedColorIndex >= 0 && selectedColorIndex < MAX_COLORS) {
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
                        // Ignore
                    }
                }
            }
        );
        colorPicker.visible = false;
        colorPicker.setEnabled(true); // Always enabled
        addTabWidget(colorPicker);
        
        // Save button - positioned at bottom of left panel
        int saveButtonWidth = Math.max(80, (int)(leftPanelWidth * 0.25f));
        int saveButtonY = colorSectionY + MAX_COLORS * (swatchSize + swatchSpacing) + 15;
        Button saveButton = new Button(
            leftPanelX, saveButtonY,
            saveButtonWidth, 20,
            new TextComponent("Save"),
            (btn) -> saveConfig()
        );
        addTabWidget(saveButton);
    }
    
    private void onMaxParticleColorChanged(int value) {
        currentMaxColor = value;
        updateSwatchesEnabledState();
        // Update slider message
        if (maxParticleColorSlider != null) {
            maxParticleColorSlider.setMessage(new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor));
        }
        
        // If currently selected swatch is beyond max, deselect it and hide picker
        if (selectedColorIndex >= currentMaxColor) {
            selectedColorIndex = -1;
            if (colorPicker != null) {
                colorPicker.visible = false;
            }
            // Clear selection from all swatches
            for (ColorSwatchButton swatch : colorSwatchButtons) {
                swatch.setSelected(false);
            }
        }
    }
    
    private void updateSwatchesEnabledState() {
        // Enable/disable color swatches and hex fields based on max color value
        for (int i = 0; i < colorSwatchButtons.size(); i++) {
            boolean enabled = i < currentMaxColor;
            colorSwatchButtons.get(i).active = enabled;
            if (i < colorHexFields.size()) {
                colorHexFields.get(i).setEditable(enabled);
            }
        }
    }
    
    private void onColorSwatchClicked(int colorIndex) {
        // Only allow clicking if swatch is enabled (within max range)
        if (colorIndex >= currentMaxColor) {
            return; // Swatch is locked, don't allow clicking
        }
        
        selectedColorIndex = colorIndex;
        
        // Get current color for this index
        String hexValue = "#FFFFFF";
        if (colorIndex < pillarData.dyeColors.size()) {
            hexValue = pillarData.dyeColors.get(colorIndex);
        }
        
        int color = 0xFFFFFF;
        try {
            if (hexValue.startsWith("#") && hexValue.length() == 7) {
                color = Integer.parseInt(hexValue.substring(1), 16);
            }
        } catch (NumberFormatException e) {
            // Use default white
        }
        
        // Update and show color picker
        if (colorPicker != null) {
            colorPicker.setColor(color);
            colorPicker.visible = true;
            colorPicker.setEnabled(true); // Always enabled
        }
        
        // Update swatch selection
        for (int i = 0; i < colorSwatchButtons.size(); i++) {
            colorSwatchButtons.get(i).setSelected(i == colorIndex);
        }
    }
    
    private void onColorChanged(int colorIndex, String hexColor) {
        // Update pillar data colors
        if (pillarData.dyeColors == null) {
            pillarData.dyeColors = new ArrayList<>();
        }
        
        // Ensure list is large enough
        while (pillarData.dyeColors.size() <= colorIndex) {
            pillarData.dyeColors.add("#FFFFFF");
        }
        
        pillarData.dyeColors.set(colorIndex, hexColor);
        pillarData.modifiedTime = System.currentTimeMillis();
    }
    
    private void updateSwatchButtonColor(int index, int color) {
        if (colorSwatchButtons != null && index >= 0 && index < colorSwatchButtons.size()) {
            colorSwatchButtons.get(index).setColor(color);
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
    
    private void cyclePattern() {
        currentPatternIndex = (currentPatternIndex + 1) % PATTERNS.length;
        String pattern = PATTERNS[currentPatternIndex];
        patternSelector.setMessage(new TranslatableComponent("buildscape.config.particles.pattern." + pattern));
    }
    
    private void saveConfig() {
        if (pillarData == null) return;
        
        PillarParticleConfig globalConfig = PillarParticleConfig.get();
        
        // Save pattern (always enabled for pattern settings)
        pillarData.pattern = PATTERNS[currentPatternIndex];
        
        // Save pattern settings only (no particle settings)
        try {
            pillarData.pattern_speed = Double.parseDouble(patternSpeedField.getValue());
        } catch (NumberFormatException e) {
            pillarData.pattern_speed = globalConfig.pattern_speed;
        }
        
        try {
            pillarData.pattern_spread = Double.parseDouble(patternSpreadField.getValue());
        } catch (NumberFormatException e) {
            pillarData.pattern_spread = globalConfig.pattern_spread;
        }
        
        try {
            pillarData.pattern_intensity = Double.parseDouble(patternIntensityField.getValue());
        } catch (NumberFormatException e) {
            pillarData.pattern_intensity = globalConfig.pattern_intensity;
        }
        
        // Save max_particle_color
        pillarData.max_particle_color = currentMaxColor;
        
        // Clean up empty/default colors from the end (but keep at least one if list was empty)
        if (pillarData.dyeColors != null) {
            // Remove trailing white/default colors beyond max
            while (pillarData.dyeColors.size() > currentMaxColor) {
                pillarData.dyeColors.remove(pillarData.dyeColors.size() - 1);
            }
            // Remove trailing white/default colors
            while (!pillarData.dyeColors.isEmpty() && 
                   (pillarData.dyeColors.get(pillarData.dyeColors.size() - 1).equals("#FFFFFF") ||
                    pillarData.dyeColors.get(pillarData.dyeColors.size() - 1).equals("#ffffff"))) {
                pillarData.dyeColors.remove(pillarData.dyeColors.size() - 1);
            }
        }
        
        // Update modified time
        pillarData.modifiedTime = System.currentTimeMillis();
        
        // Save to manager
        PillarIdManager manager = PillarIdManager.get();
        manager.saveImmediate();
        
        // Update block entity NBT on server side
        updateBlockEntityNBT();
    }
    
    private void updateBlockEntityNBT() {
        if (pillarData == null) return;
        
        // Note: We're on the client side, so we can't directly update server-side block entities.
        // The block entity's syncColorsFromManager() method will sync from PillarIdManager
        // on the next server tick. However, it only syncs if the block entity doesn't already
        // have colors (see PillarBlockEntity.syncColorsFromManager line 97-104).
        // 
        // To force an update, we need to either:
        // 1. Clear the block entity's colors first (would require a packet)
        // 2. Modify syncColorsFromManager to always sync when manager data changes
        // 3. Add a force sync method that bypasses the early return
        
        // For now, the colors will update when:
        // - The block entity is reloaded
        // - The chunk is reloaded
        // - Or if syncColorsFromManager is modified to always check manager data
        
        // The config is saved to PillarIdManager, which is the source of truth.
        // Block entities will eventually sync from it.
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();
        
        int padding = (int)(contentWidth * 0.02f);
        int leftPanelWidth = (int)(contentWidth * 0.48f);
        int rightPanelWidth = (int)(contentWidth * 0.48f);
        int panelSpacing = (int)(contentWidth * 0.02f);
        int leftPanelX = contentX + padding;
        int rightPanelX = leftPanelX + leftPanelWidth + panelSpacing;
        
        // Position back button - aligned properly
        backButton.x = contentX + padding;
        backButton.y = contentY + 10;
        
        // Draw title showing pillar ID - properly aligned
        String title = "Pillar ID: " + pillarId;
        int titleY = contentY + 60;
        Minecraft.getInstance().font.draw(poseStack, new TextComponent(title), 
            contentX + padding, titleY, 0xFFFFFF);
        
        // Draw subtitle - below title
        String subtitle = "Configure individual settings for this pillar";
        Minecraft.getInstance().font.draw(poseStack, new TextComponent(subtitle), 
            contentX + padding, titleY + 12, 0xAAAAAA);
        
        // Calculate label width and field position (same as in init)
        int labelWidth = Math.max(130, (int)(leftPanelWidth * 0.40f));
        int labelX = leftPanelX;
        int labelYOffset = 5; // Vertical offset to center label with field
        
        // Render field labels
        if (patternSpeedField != null) {
            int labelY = patternSpeedField.y + labelYOffset;
            Minecraft.getInstance().font.draw(poseStack, 
                new TranslatableComponent("buildscape.config.particles.pattern_speed").getString() + ":", 
                labelX, labelY, 0xFFFFFF);
        }
        
        if (patternSpreadField != null) {
            int labelY = patternSpreadField.y + labelYOffset;
            Minecraft.getInstance().font.draw(poseStack, 
                new TranslatableComponent("buildscape.config.particles.pattern_spread").getString() + ":", 
                labelX, labelY, 0xFFFFFF);
        }
        
        if (patternIntensityField != null) {
            int labelY = patternIntensityField.y + labelYOffset;
            Minecraft.getInstance().font.draw(poseStack, 
                new TranslatableComponent("buildscape.config.particles.pattern_intensity").getString() + ":", 
                labelX, labelY, 0xFFFFFF);
        }
        
        if (maxParticleColorSlider != null) {
            int labelY = maxParticleColorSlider.y + labelYOffset;
            Minecraft.getInstance().font.draw(poseStack, 
                new TranslatableComponent("buildscape.config.particles.max_particle_color").getString() + ":", 
                labelX, labelY, 0xFFFFFF);
        }
        
        // Position color picker if visible - ensure it stays in right panel
        if (colorPicker != null && colorPicker.visible) {
            int pickerX = rightPanelX + padding;
            int pickerY = colorSectionY;
            
            // Ensure picker doesn't overflow right panel
            int maxPickerX = rightPanelX + rightPanelWidth - padding;
            if (pickerX + colorPicker.getWidth() > maxPickerX) {
                pickerX = maxPickerX - colorPicker.getWidth();
            }
            
            // Ensure picker doesn't overflow content height
            int maxPickerY = contentY + contentHeight - padding;
            if (pickerY + colorPicker.getHeight() > maxPickerY) {
                pickerY = maxPickerY - colorPicker.getHeight();
            }
            
            colorPicker.x = pickerX;
            colorPicker.y = pickerY;
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle color picker clicks
        if (colorPicker != null && colorPicker.visible && colorPicker.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (colorPicker != null && colorPicker.visible && colorPicker.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (colorPicker != null && colorPicker.visible && colorPicker.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Forward key events to color picker fields
        if (colorPicker != null && colorPicker.visible) {
            if (colorPicker.rField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.gField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.bField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.hField != null && colorPicker.hField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.sField != null && colorPicker.sField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.brightnessField != null && colorPicker.brightnessField.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        
        // Forward to hex fields
        for (EditBox hexField : colorHexFields) {
            if (hexField.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        
        // Forward to pattern fields
        if (patternSpeedField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (patternSpreadField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (patternIntensityField.keyPressed(keyCode, scanCode, modifiers)) return true;
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Forward char events to color picker fields
        if (colorPicker != null && colorPicker.visible) {
            if (colorPicker.rField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.gField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.bField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.hField != null && colorPicker.hField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.sField != null && colorPicker.sField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.brightnessField != null && colorPicker.brightnessField.charTyped(codePoint, modifiers)) return true;
        }
        
        // Forward to hex fields
        for (EditBox hexField : colorHexFields) {
            if (hexField.charTyped(codePoint, modifiers)) return true;
        }
        
        // Forward to pattern fields
        if (patternSpeedField.charTyped(codePoint, modifiers)) return true;
        if (patternSpreadField.charTyped(codePoint, modifiers)) return true;
        if (patternIntensityField.charTyped(codePoint, modifiers)) return true;
        
        return super.charTyped(codePoint, modifiers);
    }
    
    private void syncPatternFromBlockEntity(PillarIdManager manager) {
        if (pillarData == null) return;
        
        // Try to get the block entity from the client world
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        
        // Check if we're in the same dimension
        String currentDimension = mc.level.dimension().location().toString();
        if (!currentDimension.equals(pillarData.dimension)) {
            return; // Different dimension, can't access block entity
        }
        
        // Get the block entity at the pillar position
        net.minecraft.core.BlockPos pillarPos = new net.minecraft.core.BlockPos(
            pillarData.x, pillarData.y, pillarData.z
        );
        
        if (!mc.level.isLoaded(pillarPos)) {
            return; // Chunk not loaded
        }
        
        net.minecraft.world.level.block.entity.BlockEntity be = mc.level.getBlockEntity(pillarPos);
        if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
            // Get the current pattern from the block entity
            String blockEntityPattern = pillarBE.getParticlePattern();
            
            // If block entity has a pattern and it's different from config, update config
            if (blockEntityPattern != null && !blockEntityPattern.isEmpty()) {
                if (pillarData.pattern == null || !pillarData.pattern.equals(blockEntityPattern)) {
                    pillarData.pattern = blockEntityPattern;
                    pillarData.modifiedTime = System.currentTimeMillis();
                    manager.saveImmediate();
                }
            }
        }
    }
    
    @Override
    public String getTabName() {
        return "PillarIdDetail";
    }
}
