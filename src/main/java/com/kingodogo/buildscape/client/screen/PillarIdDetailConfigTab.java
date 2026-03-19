package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.ColorPickerWidget;
import com.kingodogo.buildscape.client.screen.widget.ColorSwatchButton;
import com.kingodogo.buildscape.client.screen.widget.IntSliderWidget;
import com.kingodogo.buildscape.client.screen.widget.ScaledTextButton;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class PillarIdDetailConfigTab extends AbstractConfigTab {
    private static final String[] PATTERNS = {"none", "default", "beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"};
    private static final int MAX_COLORS = 5;
    
    private final String pillarId;
    private PillarIdManager.PillarData pillarData;

    private ScaledTextButton backButton;
    private ScaledTextButton saveButton;
    private ScaledTextButton patternSelector;
    private ScaledTextButton usePatternToggle;
    private EditBox patternSpeedField;
    private EditBox patternSpreadField;
    private EditBox patternIntensityField;
    private IntSliderWidget maxParticleColorSlider;
    private List<ColorSwatchButton> colorSwatchButtons;
    private List<EditBox> colorHexFields;
    private ColorPickerWidget colorPicker;
    private int selectedColorIndex = -1;
    private int currentPatternIndex = 0;
    private int currentMaxColor = 5;
    
    // Panel coordinates
    private int leftBoxX, leftBoxY, leftBoxWidth, leftBoxHeight;
    private int rightBoxX, rightBoxY, rightBoxWidth, rightBoxHeight;
    private int lastContentX = -1, lastContentY = -1, lastContentWidth = -1, lastContentHeight = -1;
    private int lastScreenWidth = -1;

    private boolean dirty = false;
    
    public PillarIdDetailConfigTab(BuildScapeConfigScreen parent, String pillarId) {
        super(parent);
        this.pillarId = pillarId;
    }
    
    @Override
    public void init() {
        // IMPORTANT: Request fresh pillar data from server when opening the tab
        // This ensures we always have the latest data, especially on multiplayer servers
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.getCurrentServer() != null) {
            // Multiplayer - request data from server
            com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(
                    new com.kingodogo.buildscape.network.RequestPillarIdsPacket()
            );
        }

        // Load pillar data
        PillarIdManager manager = PillarIdManager.get();
        try {
            manager.checkAndReload();
        } catch (Exception e) {
            // Ignore reload errors
        }
        
        pillarData = manager.getPillarData(pillarId);
        if (pillarData == null) {
            parent.setActiveTab(new PillarIdsConfigTab(parent));
            return;
        }
        
        // Try to sync pattern from block entity
        syncPatternFromBlockEntity(manager);
        pillarData = manager.getPillarData(pillarId);
        if (pillarData == null) {
            parent.setActiveTab(new PillarIdsConfigTab(parent));
            return;
        }
        
        PillarParticleConfig globalConfig = PillarParticleConfig.get();
        
        // Back button
        backButton = new ScaledTextButton(
            0, 0,
            60, 20,
            new TextComponent("Back"),
            (btn) -> parent.setActiveTab(new PillarIdsConfigTab(parent))
        );
        backButton.setCustomTextColors(0xFFFF55, 0xFFFFFF); // Yellow/White on hover
        addTabWidget(backButton);
        
        // Pattern selector
        String pattern = pillarData.pattern != null ? pillarData.pattern : "none"; // Default to "none" if null (global)
        // If pattern logic in this tab uses "none" to represent null/global
        if (pillarData.pattern == null || pillarData.pattern.isEmpty()) pattern = "none";
        
        currentPatternIndex = findPatternIndex(pattern);

        patternSelector = new ScaledTextButton(
            0, 0,
                120, 20,
                getPatternMessage(pattern),
            (btn) -> cyclePattern()
        );
        patternSelector.setCustomTextColors(0, 0); // Allow component colors
        patternSelector.active = true;
        addTabWidget(patternSelector);

        // Use pattern toggle
        boolean usePattern = pillarData.use_pattern != null ? pillarData.use_pattern : globalConfig.use_pattern;
        usePatternToggle = new ScaledTextButton(
                0, 0,
                120, 20,
                getUsePatternMessage(usePattern),
                (btn) -> {
                    boolean next = !(pillarData.use_pattern != null ? pillarData.use_pattern : globalConfig.use_pattern);
                    pillarData.use_pattern = next;
                    btn.setMessage(getUsePatternMessage(next));
                    dirty = true;
                }
        );
        addTabWidget(usePatternToggle);
        
        // Pattern speed
        double patternSpeed = pillarData.pattern_speed != null ? pillarData.pattern_speed : globalConfig.pattern_speed;
        patternSpeedField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, 20,
            TextComponent.EMPTY
        );
        patternSpeedField.setValue(String.valueOf(patternSpeed));
        patternSpeedField.setEditable(true);
        patternSpeedField.setBordered(true);
        patternSpeedField.setTextColor(0xFFFFFF);
        patternSpeedField.setTextColorUneditable(0xAAAAAA);
        patternSpeedField.setMaxLength(64);
        patternSpeedField.setResponder((text) -> dirty = true);
        addTabWidget(patternSpeedField);
        
        // Pattern spread
        double patternSpread = pillarData.pattern_spread != null ? pillarData.pattern_spread : globalConfig.pattern_spread;
        patternSpreadField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, 20,
            TextComponent.EMPTY
        );
        patternSpreadField.setValue(String.valueOf(patternSpread));
        patternSpreadField.setEditable(true);
        patternSpreadField.setBordered(true);
        patternSpreadField.setTextColor(0xFFFFFF);
        patternSpreadField.setTextColorUneditable(0xAAAAAA);
        patternSpreadField.setMaxLength(64);
        patternSpreadField.setResponder((text) -> dirty = true);
        addTabWidget(patternSpreadField);
        
        // Pattern intensity
        double patternIntensity = pillarData.pattern_intensity != null ? pillarData.pattern_intensity : globalConfig.pattern_intensity;
        patternIntensityField = new EditBox(
            Minecraft.getInstance().font,
            0, 0,
            120, 20,
            TextComponent.EMPTY
        );
        patternIntensityField.setValue(String.valueOf(patternIntensity));
        patternIntensityField.setEditable(true);
        patternIntensityField.setBordered(true);
        patternIntensityField.setTextColor(0xFFFFFF);
        patternIntensityField.setTextColorUneditable(0xAAAAAA);
        patternIntensityField.setMaxLength(64);
        patternIntensityField.setResponder((text) -> dirty = true);
        addTabWidget(patternIntensityField);
        
        // Max particle color slider
        int maxColor = pillarData.max_particle_color != null ? pillarData.max_particle_color : globalConfig.max_particle_color;
        currentMaxColor = Math.max(1, Math.min(MAX_COLORS, maxColor));
        maxParticleColorSlider = new IntSliderWidget(
            0, 0,
            120, 20,
            new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor),
            1, MAX_COLORS, currentMaxColor,
            (value) -> onMaxParticleColorChanged(value)
        );
        maxParticleColorSlider.active = true;
        addTabWidget(maxParticleColorSlider);
        
        // Save button
        saveButton = new ScaledTextButton(
            0, 0,
            100, 20,
                new TranslatableComponent("buildscape.config.apply"),
            (btn) -> saveConfig()
        );
        addTabWidget(saveButton);
        
        // Color swatches and hex fields
        colorSwatchButtons = new ArrayList<>();
        colorHexFields = new ArrayList<>();
        
        List<String> colors = pillarData.dyeColors != null ? new ArrayList<>(pillarData.dyeColors) : new ArrayList<>();
        while (colors.size() < MAX_COLORS) {
            colors.add("#FFFFFF");
        }
        
        for (int i = 0; i < MAX_COLORS; i++) {
            final int colorIndex = i;
            String colorCode = i < colors.size() ? colors.get(i) : "#FFFFFF";
            int color = 0xFFFFFF;
            try {
                if (colorCode.startsWith("#") && colorCode.length() == 7) {
                    color = Integer.parseInt(colorCode.substring(1), 16);
                }
            } catch (NumberFormatException e) {
                // Use default white
            }
            
            ColorSwatchButton swatch = new ColorSwatchButton(
                0, 0,
                BuildScapeConfigScreen.getScaledEditBoxHeight(), 
                BuildScapeConfigScreen.getScaledEditBoxHeight(),
                color,
                (btn) -> onColorSwatchClicked(colorIndex)
            );
            colorSwatchButtons.add(swatch);
            addTabWidget(swatch);
            
            EditBox hexField = new EditBox(
                Minecraft.getInstance().font,
                0, 0,
                80, 20,
                TextComponent.EMPTY
            );
            hexField.setValue(colorCode);
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
                        onColorChanged(colorIndex, hexText);
                        updateSwatchButtonColor(colorIndex, newColor);
                        if (selectedColorIndex == colorIndex && colorPicker != null) {
                            colorPicker.setColor(newColor);
                        }
                        if (!text.equals(hexText)) {
                            hexField.setValue(hexText);
                        }
                        dirty = true;
                    }
                } catch (NumberFormatException e) {
                    // Invalid hex, ignore
                }
            });
            colorHexFields.add(hexField);
            addTabWidget(hexField);
        }
        
        // Color picker - reduced size to fit in window
        colorPicker = new ColorPickerWidget(
            0, 0,
            190, 190,
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
        colorPicker.setEnabled(true);
        addTabWidget(colorPicker);
        
        updateSwatchesEnabledState();
        
        // Initial layout
        relayout(parent.getContentX(), parent.getContentY(), parent.getContentWidth(), parent.getContentHeight());
    }
    
    private void relayout(int contentX, int contentY, int contentWidth, int contentHeight) {
        Minecraft mc = Minecraft.getInstance();
        int padding = BuildScapeConfigScreen.scaleSize(10);
        
        // Use dimensions from parent screen directly to ensure consistency
        int middleX = parent.getContentX();
        int middlePanelWidth = parent.getContentWidth();
        int rightX = parent.getRightPanelX();
        int rightPanelWidth = parent.getRightPanelWidth();
        
        // Button area at top for back button
        int buttonAreaHeight = BuildScapeConfigScreen.getScaledButtonHeight() + padding;
        
        // Position back button
        backButton.x = contentX + padding;
        backButton.y = contentY + BuildScapeConfigScreen.scaleSize(3);
        backButton.setWidth(BuildScapeConfigScreen.scaleSize(60));
        backButton.setHeight(BuildScapeConfigScreen.getScaledButtonHeight());

        // LEFT PANEL: Settings
        leftBoxX = middleX;
        leftBoxY = contentY + buttonAreaHeight;
        leftBoxWidth = middlePanelWidth;
        leftBoxHeight = contentHeight - buttonAreaHeight;

        // RIGHT PANEL: Colors
        rightBoxX = rightX;
        rightBoxY = contentY + buttonAreaHeight;
        rightBoxWidth = rightPanelWidth;
        rightBoxHeight = contentHeight - buttonAreaHeight;

        // Left Panel Layout: Config fields
        int gap = BuildScapeConfigScreen.scaleSize(6);
        int fieldHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int fieldSpacing = fieldHeight + gap;
        int currentY = leftBoxY + padding + BuildScapeConfigScreen.scaleSize(15); // Extra space for title
        
        // Calculate label and field widths
        int labelWidth = BuildScapeConfigScreen.scaleSize(140);
        int fieldGap = BuildScapeConfigScreen.scaleSize(8);
        int fieldX = leftBoxX + padding + labelWidth + fieldGap;
        int fieldWidth = leftBoxWidth - padding * 2 - labelWidth - fieldGap;

        patternSelector.x = fieldX;
        patternSelector.y = currentY;
        patternSelector.setWidth(fieldWidth);
        patternSelector.setHeight(fieldHeight);
        currentY += fieldSpacing;

        usePatternToggle.x = fieldX;
        usePatternToggle.y = currentY;
        usePatternToggle.setWidth(fieldWidth);
        usePatternToggle.setHeight(fieldHeight);
        currentY += fieldSpacing;

        patternSpeedField.x = fieldX;
        patternSpeedField.y = currentY;
        patternSpeedField.setWidth(fieldWidth);
        setEditBoxHeight(patternSpeedField, fieldHeight);
        currentY += fieldSpacing;

        patternSpreadField.x = fieldX;
        patternSpreadField.y = currentY;
        patternSpreadField.setWidth(fieldWidth);
        setEditBoxHeight(patternSpreadField, fieldHeight);
        currentY += fieldSpacing;

        patternIntensityField.x = fieldX;
        patternIntensityField.y = currentY;
        patternIntensityField.setWidth(fieldWidth);
        setEditBoxHeight(patternIntensityField, fieldHeight);
        currentY += fieldSpacing;

        maxParticleColorSlider.x = fieldX;
        maxParticleColorSlider.y = currentY;
        maxParticleColorSlider.setWidth(fieldWidth);
        maxParticleColorSlider.setHeight(fieldHeight);
        
        // Save button
        if (saveButton != null) {
            saveButton.x = leftBoxX + padding;
            saveButton.y = leftBoxY + leftBoxHeight - padding - BuildScapeConfigScreen.getScaledButtonHeight();
            saveButton.setWidth(leftBoxWidth - padding * 2);
            saveButton.setHeight(BuildScapeConfigScreen.getScaledButtonHeight());
        }

        // Right Panel Layout: Colors (2 column layout like PillarParticlesConfigTab)
        int swatchSize = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int hexFieldWidth = BuildScapeConfigScreen.scaleSize(85);
        int hexFieldHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int colorRowSpacing = BuildScapeConfigScreen.scaleSize(6);
        int startY = rightBoxY + padding + BuildScapeConfigScreen.scaleSize(18);
        
        // Column spacing
        int availableWidth = rightBoxWidth - padding * 2;
        int columnSpacing = BuildScapeConfigScreen.scaleSize(10);
        int columnWidth = (availableWidth - columnSpacing) / 2;
        
        int leftSwatchX = rightBoxX + padding;
        int fieldSwatchGap = BuildScapeConfigScreen.scaleSize(6);
        int leftHexFieldX = leftSwatchX + swatchSize + fieldSwatchGap;
        
        int rightSwatchX = rightBoxX + padding + columnWidth + columnSpacing;
        int rightHexFieldX = rightSwatchX + swatchSize + fieldSwatchGap;

        for (int i = 0; i < MAX_COLORS; i++) {
            int row = i / 2;
            int col = i % 2;
            int swatchY = startY + row * (swatchSize + colorRowSpacing);
            
            ColorSwatchButton swatch = colorSwatchButtons.get(i);
            swatch.x = (col == 0) ? leftSwatchX : rightSwatchX;
            swatch.y = swatchY;
            swatch.setWidth(swatchSize);
            swatch.setHeight(swatchSize);

            EditBox hexField = colorHexFields.get(i);
            hexField.x = (col == 0) ? leftHexFieldX : rightHexFieldX;
            hexField.y = swatchY + (swatchSize - hexFieldHeight) / 2;
            hexField.setWidth(Math.min(hexFieldWidth, columnWidth - swatchSize - BuildScapeConfigScreen.scaleSize(8)));
            setEditBoxHeight(hexField, hexFieldHeight);
        }

        // Position color picker below swatches
        if (colorPicker != null) {
            int swatchesEndY = startY + ((MAX_COLORS + 1) / 2) * (swatchSize + colorRowSpacing);
            int pickerX = rightBoxX + padding;
            int pickerY = swatchesEndY + BuildScapeConfigScreen.scaleSize(10);
            int pickerWidth = rightBoxWidth - padding * 2;
            int pickerHeight = rightBoxY + rightBoxHeight - padding - pickerY;
            
            // Limit picker size to reasonable proportions
            int idealWidth = BuildScapeConfigScreen.scaleSize(260);
            int idealHeight = BuildScapeConfigScreen.scaleSize(200);
            
            colorPicker.setWidth(Math.min(pickerWidth, idealWidth));
            colorPicker.setHeight(Math.min(pickerHeight, idealHeight));
            colorPicker.x = rightBoxX + (rightBoxWidth - colorPicker.getWidth()) / 2;
            colorPicker.y = pickerY;
        }
    }
    
    private void onMaxParticleColorChanged(int value) {
        currentMaxColor = value;
        dirty = true;
        updateSwatchesEnabledState();
        if (maxParticleColorSlider != null) {
            maxParticleColorSlider.setMessage(new TranslatableComponent("buildscape.config.particles.max_particle_color", currentMaxColor));
        }
        
        if (selectedColorIndex >= currentMaxColor) {
            selectedColorIndex = -1;
            if (colorPicker != null) {
                colorPicker.visible = false;
            }
            for (ColorSwatchButton swatch : colorSwatchButtons) {
                swatch.setSelected(false);
            }
        }
    }
    
    private void updateSwatchesEnabledState() {
        for (int i = 0; i < colorSwatchButtons.size(); i++) {
            boolean enabled = i < currentMaxColor;
            colorSwatchButtons.get(i).active = enabled;
            colorSwatchButtons.get(i).visible = enabled;
            if (i < colorHexFields.size()) {
                colorHexFields.get(i).setEditable(enabled);
                colorHexFields.get(i).visible = enabled;
            }
        }
    }
    
    private void onColorSwatchClicked(int colorIndex) {
        if (colorIndex >= currentMaxColor) {
            return;
        }
        
        selectedColorIndex = colorIndex;
        
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
        
        if (colorPicker != null) {
            colorPicker.setColor(color);
            colorPicker.visible = true;
            colorPicker.setEnabled(true);
        }
        
        for (int i = 0; i < colorSwatchButtons.size(); i++) {
            colorSwatchButtons.get(i).setSelected(i == colorIndex);
        }
    }
    
    private void onColorChanged(int colorIndex, String hexColor) {
        if (pillarData.dyeColors == null) {
            pillarData.dyeColors = new ArrayList<>();
        }
        
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
        patternSelector.setMessage(getPatternMessage(pattern));
        dirty = true;
    }
    
    private void saveConfig() {
        if (pillarData == null) return;
        
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.playSound(
                net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL,
                1.0f,
                1.0f
            );
        }
        
        PillarParticleConfig globalConfig = PillarParticleConfig.get();
        
        pillarData.pattern = PATTERNS[currentPatternIndex];
        
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
        
        pillarData.max_particle_color = currentMaxColor;
        
        if (pillarData.dyeColors != null) {
            while (pillarData.dyeColors.size() > currentMaxColor) {
                pillarData.dyeColors.remove(pillarData.dyeColors.size() - 1);
            }
            while (!pillarData.dyeColors.isEmpty() && 
                   (pillarData.dyeColors.get(pillarData.dyeColors.size() - 1).equals("#FFFFFF") ||
                    pillarData.dyeColors.get(pillarData.dyeColors.size() - 1).equals("#ffffff"))) {
                pillarData.dyeColors.remove(pillarData.dyeColors.size() - 1);
            }
        }
        
        pillarData.modifiedTime = System.currentTimeMillis();

        // Send packet to server to update pillar data
        com.kingodogo.buildscape.network.UpdatePillarDataPacket packet =
                new com.kingodogo.buildscape.network.UpdatePillarDataPacket(
                        pillarData.id,
                        pillarData.pattern,
                        pillarData.use_pattern,
                        pillarData.pattern_speed,
                        pillarData.pattern_spread,
                        pillarData.pattern_intensity,
                        pillarData.max_particle_color,
                        pillarData.dyeColors
                );

        com.kingodogo.buildscape.network.ModMessages.INSTANCE.sendToServer(packet);

        // Also update local manager for single-player
        PillarIdManager manager = PillarIdManager.get();
        manager.saveImmediate();
        this.dirty = false;
    }

    @Deprecated
    private void updateBlockEntityNBT_OLD() {
        if (pillarData == null) return;

        try {
            net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isRunning()) {
                return;
            }

            // Find the level for this pillar's dimension
            for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                if (level == null) continue;

                String dimensionKey = PillarIdManager.getDimensionKey(level);
                if (!dimensionKey.equals(pillarData.dimension)) {
                    continue;
                }

                net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(pillarData.x, pillarData.y, pillarData.z);

                if (!level.isLoaded(pos)) {
                    continue;
                }

                net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
                if (!(be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE)) {
                    continue;
                }

                // Find the bottom of the stack
                net.minecraft.core.BlockPos bottomPos = pillarBE.findStackBottom();
                net.minecraft.world.level.block.entity.BlockEntity bottomBE = level.getBlockEntity(bottomPos);

                if (!(bottomBE instanceof com.kingodogo.buildscape.block.PillarBlockEntity bottomPillarBE)) {
                    continue;
                }

                // Update NBT with settings from manager
                boolean needsUpdate = false;

                // Update pattern
                if (pillarData.pattern != null && !pillarData.pattern.isEmpty()) {
                    if (bottomPillarBE.getParticlePattern() == null ||
                        !bottomPillarBE.getParticlePattern().equals(pillarData.pattern)) {
                        bottomPillarBE.setParticlePattern(pillarData.pattern);
                        needsUpdate = true;
                    }
                }

                // Update pattern speed
                if (pillarData.pattern_speed != null) {
                    if (bottomPillarBE.getPatternSpeed() == null ||
                        !bottomPillarBE.getPatternSpeed().equals(pillarData.pattern_speed)) {
                        bottomPillarBE.setPatternSpeed(pillarData.pattern_speed);
                        needsUpdate = true;
                    }
                }

                // Update pattern spread
                if (pillarData.pattern_spread != null) {
                    if (bottomPillarBE.getPatternSpread() == null ||
                        !bottomPillarBE.getPatternSpread().equals(pillarData.pattern_spread)) {
                        bottomPillarBE.setPatternSpread(pillarData.pattern_spread);
                        needsUpdate = true;
                    }
                }

                // Update pattern intensity
                if (pillarData.pattern_intensity != null) {
                    if (bottomPillarBE.getPatternIntensity() == null ||
                        !bottomPillarBE.getPatternIntensity().equals(pillarData.pattern_intensity)) {
                        bottomPillarBE.setPatternIntensity(pillarData.pattern_intensity);
                        needsUpdate = true;
                    }
                }

                // Update max particle color
                if (pillarData.max_particle_color != null) {
                    if (bottomPillarBE.getMaxParticleColor() == null ||
                        !bottomPillarBE.getMaxParticleColor().equals(pillarData.max_particle_color)) {
                        bottomPillarBE.setMaxParticleColor(pillarData.max_particle_color);
                        needsUpdate = true;
                    }
                }
                
                // Update colors
                if (pillarData.dyeColors != null && !pillarData.dyeColors.isEmpty()) {
                    java.util.List<String> nbtColors = bottomPillarBE.getParticleColors();
                    boolean colorsChanged = false;
                    
                    if (nbtColors == null || nbtColors.size() != pillarData.dyeColors.size()) {
                        colorsChanged = true;
                    } else {
                        for (int i = 0; i < pillarData.dyeColors.size(); i++) {
                            String managerColor = pillarData.dyeColors.get(i);
                            String nbtColor = i < nbtColors.size() ? nbtColors.get(i) : null;
                            if (nbtColor == null || !nbtColor.equals(managerColor)) {
                                colorsChanged = true;
                                break;
                            }
                        }
                    }
                    
                    if (colorsChanged) {
                        // Clear and set colors
                        if (bottomPillarBE.getParticleColors() != null) {
                            bottomPillarBE.getParticleColors().clear();
                        }
                        for (String color : pillarData.dyeColors) {
                            if (color != null && !color.isEmpty()) {
                                bottomPillarBE.addParticleColor(color);
                            }
                        }
                        needsUpdate = true;
                    }
                }
                
                if (needsUpdate) {
                    bottomPillarBE.setChanged();
                    level.sendBlockUpdated(
                        bottomPos,
                        level.getBlockState(bottomPos),
                        level.getBlockState(bottomPos),
                        3
                    );
                }
                
                break; // Found the pillar, done
            }
        } catch (Exception e) {}
    }
    
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
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
        if (colorPicker != null && colorPicker.visible) {
            if (colorPicker.rField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.gField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.bField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.hField != null && colorPicker.hField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.sField != null && colorPicker.sField.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (colorPicker.brightnessField != null && colorPicker.brightnessField.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        
        for (EditBox hexField : colorHexFields) {
            if (hexField.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        
        if (patternSpeedField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (patternSpreadField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (patternIntensityField.keyPressed(keyCode, scanCode, modifiers)) return true;
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (colorPicker != null && colorPicker.visible) {
            if (colorPicker.rField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.gField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.bField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.hField != null && colorPicker.hField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.sField != null && colorPicker.sField.charTyped(codePoint, modifiers)) return true;
            if (colorPicker.brightnessField != null && colorPicker.brightnessField.charTyped(codePoint, modifiers)) return true;
        }
        
        for (EditBox hexField : colorHexFields) {
            if (hexField.charTyped(codePoint, modifiers)) return true;
        }
        
        if (patternSpeedField.charTyped(codePoint, modifiers)) return true;
        if (patternSpreadField.charTyped(codePoint, modifiers)) return true;
        if (patternIntensityField.charTyped(codePoint, modifiers)) return true;
        
        return super.charTyped(codePoint, modifiers);
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        // Check if relayout needed
        int screenWidth = parent.width;
        if (contentX != lastContentX || contentY != lastContentY || 
            contentWidth != lastContentWidth || contentHeight != lastContentHeight ||
            screenWidth != lastScreenWidth) {
            
            relayout(contentX, contentY, contentWidth, contentHeight);
            lastContentX = contentX;
            lastContentY = contentY;
            lastContentWidth = contentWidth;
            lastContentHeight = contentHeight;
            lastScreenWidth = screenWidth;
        }

        Minecraft mc = Minecraft.getInstance();
        int borderColor = 0xFF666666;
        int padding = BuildScapeConfigScreen.scaleSize(10);

        float textScale = BuildScapeConfigScreen.getStandardTextScale();

        // Header info (Pillar ID)
        int titleX = backButton.x + backButton.getWidth() + padding;
        int titleY = backButton.y;
        
        poseStack.pushPose();
        poseStack.scale(textScale, textScale, 1.0f);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.id").getString() + ": " + pillarId,
                titleX / textScale, titleY / textScale, 0xFFFFFF);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.detail.subtitle"),
                titleX / textScale, (titleY + (int) (mc.font.lineHeight * textScale) + 2) / textScale, 0xAAAAAA);
        poseStack.popPose();

        // LEFT PANEL: Borders
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX, leftBoxY, leftBoxX + leftBoxWidth, leftBoxY + 1, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX, leftBoxY + leftBoxHeight - 1, leftBoxX + leftBoxWidth, leftBoxY + leftBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX, leftBoxY, leftBoxX + 1, leftBoxY + leftBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX + leftBoxWidth - 1, leftBoxY, leftBoxX + leftBoxWidth, leftBoxY + leftBoxHeight, borderColor);

        poseStack.pushPose();
        poseStack.scale(textScale, textScale, 1.0f);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_properties"),
                (leftBoxX + BuildScapeConfigScreen.scaleSize(10)) / textScale, (leftBoxY + BuildScapeConfigScreen.scaleSize(5)) / textScale, 0xFFFFFF);
        poseStack.popPose();

        // Labels for fields in Left Panel - ensure they are drawn ONLY once
        int labelYOffset = (BuildScapeConfigScreen.getScaledEditBoxHeight() - mc.font.lineHeight) / 2;
        int textX = leftBoxX + padding;

        poseStack.pushPose();
        poseStack.scale(textScale, textScale, 1.0f);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.detail.pattern").getString() + ":",
                textX / textScale, (patternSelector.y + labelYOffset) / textScale, 0xFFFFFF);
        mc.font.draw(poseStack, "Use Pattern:",
                textX / textScale, (usePatternToggle.y + labelYOffset) / textScale, 0xFFFFFF);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_speed").getString() + ":",
                textX / textScale, (patternSpeedField.y + labelYOffset) / textScale, 0xFFFFFF);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_spread").getString() + ":",
                textX / textScale, (patternSpreadField.y + labelYOffset) / textScale, 0xFFFFFF);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.pattern_intensity").getString() + ":",
                textX / textScale, (patternIntensityField.y + labelYOffset) / textScale, 0xFFFFFF);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.detail.max_colors").getString() + ":",
                textX / textScale, (maxParticleColorSlider.y + labelYOffset) / textScale, 0xFFFFFF);
        poseStack.popPose();

        // RIGHT PANEL: Borders
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX, rightBoxY, rightBoxX + rightBoxWidth, rightBoxY + 1, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX, rightBoxY + rightBoxHeight - 1, rightBoxX + rightBoxWidth, rightBoxY + rightBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX, rightBoxY, rightBoxX + 1, rightBoxY + rightBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX + rightBoxWidth - 1, rightBoxY, rightBoxX + rightBoxWidth, rightBoxY + rightBoxHeight, borderColor);

        poseStack.pushPose();
        poseStack.scale(textScale, textScale, 1.0f);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.particles.particle_colors").getString(),
                (rightBoxX + BuildScapeConfigScreen.scaleSize(10)) / textScale, (rightBoxY + BuildScapeConfigScreen.scaleSize(5)) / textScale, 0xFFFFFF);
        poseStack.popPose();

        // Status text
        if (dirty) {
            int statusY = saveButton != null ? saveButton.y - mc.font.lineHeight - BuildScapeConfigScreen.scaleSize(4)
                    : contentY + contentHeight - BuildScapeConfigScreen.scaleSize(14);
            mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.ids.unsaved"),
                    leftBoxX + padding, statusY, 0xFFFFFF);
        }
    }
    
    @Override
    public String getTabName() {
        return "PillarIdDetail";
    }

    // Helper to get consistent pattern message styles
    private net.minecraft.network.chat.Component getPatternMessage(String pattern) {
        String key = "buildscape.config.particles.pattern." + pattern;
        if ("none".equals(pattern)) {
            return new TranslatableComponent(key).withStyle(net.minecraft.ChatFormatting.GRAY);
        } else if ("default".equals(pattern)) {
            return new TranslatableComponent(key).withStyle(net.minecraft.ChatFormatting.WHITE);
        } else {
            return new TranslatableComponent(key).withStyle(net.minecraft.ChatFormatting.GOLD);
        }
    }

    private net.minecraft.network.chat.Component getUsePatternMessage(boolean use) {
        String state = use ? "ON" : "OFF";
        net.minecraft.ChatFormatting color = use ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED;
        return new TextComponent(state).withStyle(color);
    }

    private void syncPatternFromBlockEntity(PillarIdManager manager) {
        if (pillarData == null) return;

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        String currentDimension = mc.level.dimension().location().toString();
        if (!currentDimension.equals(pillarData.dimension)) {
            return;
        }

        net.minecraft.core.BlockPos pillarPos = new net.minecraft.core.BlockPos(
            pillarData.x, pillarData.y, pillarData.z
        );

        if (!mc.level.isLoaded(pillarPos)) {
            return;
        }

        net.minecraft.world.level.block.entity.BlockEntity be = mc.level.getBlockEntity(pillarPos);
        if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {
            String blockEntityPattern = pillarBE.getParticlePattern();

            if (blockEntityPattern != null && !blockEntityPattern.isEmpty()) {
                if (pillarData.pattern == null || !pillarData.pattern.equals(blockEntityPattern)) {
                    pillarData.pattern = blockEntityPattern;
                    pillarData.modifiedTime = System.currentTimeMillis();
                    manager.saveImmediate();
                }
            }
        } else {
            // Try to find item frame entities at this position
            double range = 1.0;
            java.util.List<net.minecraft.world.entity.Entity> entities = mc.level.getEntities(
                    null,
                    new net.minecraft.world.phys.AABB(pillarPos).inflate(range)
            );
            for (net.minecraft.world.entity.Entity entity : entities) {
                String frameId = null;
                String pattern = null;

                if (entity instanceof net.minecraft.world.entity.decoration.ItemFrame frame) {
                    frameId = frame.getPersistentData().getString("BuildScapeFrameId");
                    pattern = frame.getPersistentData().getString("BuildScapeParticlePattern");
                } else if (entity instanceof com.kingodogo.buildscape.entity.ColoredItemFrameEntity frame) {
                    frameId = frame.getPersistentData().getString("BuildScapeFrameId");
                    pattern = frame.getPersistentData().getString("BuildScapeParticlePattern");
                }

                if (pillarId.equals(frameId) && pattern != null && !pattern.isEmpty()) {
                    if (!java.util.Objects.equals(pillarData.pattern, pattern)) {
                        pillarData.pattern = pattern;
                        pillarData.modifiedTime = System.currentTimeMillis();
                        manager.saveImmediate();
                    }
                    break;
                }
            }
        }
    }
}
