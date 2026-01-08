package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.client.screen.widget.ColorPickerWidget;
import com.kingodogo.buildscape.client.screen.widget.ColorSwatchButton;
import com.kingodogo.buildscape.client.screen.widget.IntSliderWidget;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;

public class PillarIdDetailConfigTab extends AbstractConfigTab {
    private static final String[] PATTERNS = {"none", "default", "beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"};
    private static final int MAX_COLORS = 5;
    
    private final String pillarId;
    private PillarIdManager.PillarData pillarData;
    
    private Button backButton;
    private Button saveButton;
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
    private int currentMaxColor = 5;
    
    // Panel coordinates
    private int leftBoxX, leftBoxY, leftBoxWidth, leftBoxHeight;
    private int rightBoxX, rightBoxY, rightBoxWidth, rightBoxHeight;
    
    public PillarIdDetailConfigTab(BuildScapeConfigScreen parent, String pillarId) {
        super(parent);
        this.pillarId = pillarId;
    }
    
    @Override
    public void init() {
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
        backButton = new Button(
            0, 0,
            80, 20,
            new TextComponent("← Back"),
            (btn) -> parent.setActiveTab(new PillarIdsConfigTab(parent))
        );
        addTabWidget(backButton);
        
        // Pattern selector
        String pattern = pillarData.pattern != null ? pillarData.pattern : globalConfig.pattern;
        currentPatternIndex = findPatternIndex(pattern);
        patternSelector = new com.kingodogo.buildscape.client.screen.widget.WideButton(
            0, 0,
            100, 20,
            new TranslatableComponent("buildscape.config.particles.pattern." + pattern),
            (btn) -> cyclePattern()
        );
        patternSelector.active = true;
        addTabWidget(patternSelector);
        
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
        saveButton = new Button(
            0, 0,
            100, 20,
            new TextComponent("Save"),
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
                20, 20,
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
    }
    
    private void onMaxParticleColorChanged(int value) {
        currentMaxColor = value;
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
            if (i < colorHexFields.size()) {
                colorHexFields.get(i).setEditable(enabled);
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
        patternSelector.setMessage(new TranslatableComponent("buildscape.config.particles.pattern." + pattern));
    }
    
    private void saveConfig() {
        if (pillarData == null) return;
        
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
        
        PillarIdManager manager = PillarIdManager.get();
        manager.saveImmediate();
        
        updateBlockEntityNBT();
    }
    
    private void updateBlockEntityNBT() {
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
        } catch (Exception e) {
            System.err.println("BuildScape: Error updating block entity NBT: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();
        
        // Calculate layout like PillarParticlesConfigTab
        int screenWidth = parent.width;
        int sidebarWidth = (int)(screenWidth * 0.11);
        int gap = (int)(screenWidth * 0.01);
        int leftPanelWidth = (int)(screenWidth * 0.44);
        int rightPanelWidth = (int)(screenWidth * 0.44);
        
        int leftX = sidebarWidth + gap;
        int rightX = sidebarWidth + gap + leftPanelWidth + gap;
        
        leftBoxX = leftX;
        leftBoxY = contentY;
        leftBoxWidth = leftPanelWidth;
        leftBoxHeight = contentHeight;
        
        rightBoxX = rightX;
        rightBoxY = contentY;
        rightBoxWidth = rightPanelWidth;
        rightBoxHeight = contentHeight;
        
        int padding = 10;
        
        // Position back button - place it at the very top left of the content area
        backButton.x = contentX + 10;
        backButton.y = contentY + 10;
        
        // Draw title at top, moved down to make room for back button
        String title = "Pillar ID: " + pillarId;
        Minecraft.getInstance().font.draw(poseStack, new TextComponent(title), 
            contentX + 100, contentY + 15, 0xFFFFFF); // Moved right to avoid back button
        
        String subtitle = "Configure individual settings for this pillar";
        Minecraft.getInstance().font.draw(poseStack, new TextComponent(subtitle), 
            contentX + 100, contentY + 27, 0xAAAAAA);
        
        // LEFT PANEL: Layout config fields
        int labelWidth = 150;
        int fieldX = leftBoxX + padding + labelWidth;
        int fieldWidth = leftBoxWidth - padding * 2 - labelWidth - 5;
        int fieldHeight = 20;
        int fieldSpacing = 26;
        int startY = leftBoxY + 45; // Adjusted to align with right panel
        int currentY = startY;
        
        // Pattern
        patternSelector.x = fieldX;
        patternSelector.y = currentY;
        patternSelector.setWidth(fieldWidth);
        Minecraft.getInstance().font.draw(poseStack, "Pattern:", 
            leftBoxX + padding, currentY + 5, 0xFFFFFF);
        currentY += fieldSpacing;
        
        // Pattern Speed
        patternSpeedField.x = fieldX;
        patternSpeedField.y = currentY;
        patternSpeedField.setWidth(fieldWidth);
        Minecraft.getInstance().font.draw(poseStack, 
            new TranslatableComponent("buildscape.config.particles.pattern_speed").getString() + ":", 
            leftBoxX + padding, currentY + 5, 0xFFFFFF);
        currentY += fieldSpacing;
        
        // Pattern Spread
        patternSpreadField.x = fieldX;
        patternSpreadField.y = currentY;
        patternSpreadField.setWidth(fieldWidth);
        Minecraft.getInstance().font.draw(poseStack, 
            new TranslatableComponent("buildscape.config.particles.pattern_spread").getString() + ":", 
            leftBoxX + padding, currentY + 5, 0xFFFFFF);
        currentY += fieldSpacing;
        
        // Pattern Intensity
        patternIntensityField.x = fieldX;
        patternIntensityField.y = currentY;
        patternIntensityField.setWidth(fieldWidth);
        Minecraft.getInstance().font.draw(poseStack, 
            new TranslatableComponent("buildscape.config.particles.pattern_intensity").getString() + ":", 
            leftBoxX + padding, currentY + 5, 0xFFFFFF);
        currentY += fieldSpacing;
        
        // Max Particle Colors
        maxParticleColorSlider.x = fieldX;
        maxParticleColorSlider.y = currentY;
        maxParticleColorSlider.setWidth(fieldWidth);
        Minecraft.getInstance().font.draw(poseStack, 
            new TranslatableComponent("buildscape.config.particles.max_particle_color").getString() + ":", 
            leftBoxX + padding, currentY + 5, 0xFFFFFF);
        currentY += fieldSpacing + 10;
        
        // Save button
        if (saveButton != null) {
            saveButton.x = leftBoxX + padding;
            saveButton.y = currentY;
        }
        
        // RIGHT PANEL: Color swatches and picker (moved 10px left from edge)
        int swatchSize = 20;
        int swatchSpacing = 6; // Spacing between swatch and hex field
        int hexFieldWidth = 65; // Reduced from 80 to fit properly in window
        int rowSpacing = 25;
        int rightStartY = rightBoxY + 45; // Start below title area (adjusted for new title position)
        
        // Move everything 10px left from the edge
        int rightContentX = rightBoxX + padding - 10;
        
        // 2 columns layout with better spacing
        int swatchX1 = rightContentX;
        int hexFieldX1 = swatchX1 + swatchSize + swatchSpacing; // Add spacing before hex field
        int columnGap = 12; // Gap between columns
        int swatchX2 = hexFieldX1 + hexFieldWidth + columnGap; // Add spacing after hex field
        int hexFieldX2 = swatchX2 + swatchSize + swatchSpacing;
        
        for (int i = 0; i < MAX_COLORS; i++) {
            int row = i / 2;
            int col = i % 2;
            int swatchY = rightStartY + row * rowSpacing;
            
            ColorSwatchButton swatch = colorSwatchButtons.get(i);
            swatch.x = col == 0 ? swatchX1 : swatchX2;
            swatch.y = swatchY;
            swatch.setWidth(swatchSize);
            swatch.setHeight(swatchSize);
            
            EditBox hexField = colorHexFields.get(i);
            hexField.x = col == 0 ? hexFieldX1 : hexFieldX2;
            hexField.y = swatchY;
            hexField.setWidth(hexFieldWidth);
        }
        
        // Position color picker below swatches (also moved left)
        if (colorPicker != null && colorPicker.visible) {
            int numRows = (MAX_COLORS + 1) / 2;
            colorPicker.x = rightContentX;
            colorPicker.y = rightStartY + numRows * rowSpacing + 10;
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
        }
    }
    
    @Override
    public String getTabName() {
        return "PillarIdDetail";
    }
}
