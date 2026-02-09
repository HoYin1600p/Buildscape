package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.config.PresetsConfig;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;

public class PresetsWidget extends AbstractWidget {
    private static final int PRESET_BUTTON_HEIGHT = 20;
    private static final int PRESET_BUTTON_SPACING = 2;
    private static final int MAX_VISIBLE_PRESETS = 6; // Default + 5 custom
    
    private List<PresetsConfig.Preset> presets;
    private List<String> presetKeys;
    private String selectedPresetKey = null;
    private final EditBox nameEditBox;
    private final Button createButton;
    private final Button saveButton;
    private final Button deleteButton;
    private final Button applyButton;
    private final Consumer<String> onPresetApplied;
    private int scrollOffset = 0;
    private boolean isDraggingScrollbar = false;
    private double scrollbarDragStartY = 0;
    private double scrollbarDragStartOffset = 0;
    
    public PresetsWidget(int x, int y, int width, int height, Consumer<String> onPresetApplied) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onPresetApplied = onPresetApplied;
        
        loadPresets();
        
        // Auto-select default preset on first load (if no unnamed preset exists)
        PresetsConfig config = PresetsConfig.get();
        if (selectedPresetKey == null) {
            if (config.hasUnnamedPreset()) {
                selectedPresetKey = "_unnamed";
            } else {
                selectedPresetKey = "default";
            }
        }
        
        // Calculate button layout - all buttons should be aligned in a row
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonY = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(35);
        int scaledButtonHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledButtonHeight();
        // 4 buttons: Create, Save, Delete, Apply - divide width equally
        int buttonWidth = (width - scaledSpacing * 5) / 4; // 4 buttons with spacing between them
        
        // Name edit box (flush on top of buttons - moved up 2 pixels)
        int editBoxHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight();
        int editBoxY = buttonY - editBoxHeight - 2; // Position flush above buttons, moved up 2 pixels
        int editBoxWidth = width - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        nameEditBox = new EditBox(
            Minecraft.getInstance().font,
            x + com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(5), editBoxY,
            editBoxWidth, com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight(),
            new TranslatableComponent("buildscape.config.preset.name")
        );
        nameEditBox.setMaxLength(32);
        
        // Set name edit box value based on selected preset
        if (selectedPresetKey != null) {
            if (selectedPresetKey.equals("_unnamed")) {
                nameEditBox.setValue("");
            } else {
                PresetsConfig.Preset preset = config.getPreset(selectedPresetKey);
                if (preset != null) {
                    nameEditBox.setValue(preset.name);
                }
            }
        }
        
        // Disable name edit box if default preset is selected
        if (selectedPresetKey != null && selectedPresetKey.equals("default")) {
            nameEditBox.setEditable(false);
        }
        
        // Create button (aligned with other buttons)
        createButton = new Button(
            x + scaledSpacing, buttonY,
            buttonWidth, scaledButtonHeight,
            new TranslatableComponent("buildscape.config.preset.create"),
            (btn) -> createNewPreset()
        );
        
        // Save button (aligned with Create button)
        saveButton = new Button(
            x + scaledSpacing * 2 + buttonWidth, buttonY,
            buttonWidth, scaledButtonHeight,
            new TranslatableComponent("buildscape.config.preset.save"),
            (btn) -> saveCurrentPreset()
        );
        
        // Delete button
        deleteButton = new Button(
            x + scaledSpacing * 3 + buttonWidth * 2, buttonY,
            buttonWidth, scaledButtonHeight,
            new TranslatableComponent("buildscape.config.preset.delete"),
            (btn) -> deleteSelectedPreset()
        );
        
        // Apply button
        applyButton = new Button(
            x + scaledSpacing * 4 + buttonWidth * 3, buttonY,
            buttonWidth, scaledButtonHeight,
            new TranslatableComponent("buildscape.config.preset.apply"),
            (btn) -> applySelectedPreset()
        );
    }
    
    private void loadPresets() {
        PresetsConfig config = PresetsConfig.get();
        presets = config.getPresets();
        presetKeys = config.getPresetKeys();
    }
    
    private void createNewPreset() {
        // Clear the name field and create a new unnamed preset
        nameEditBox.setValue("");
        nameEditBox.setEditable(true);
        selectedPresetKey = "_unnamed";
        
        // Create a new unnamed preset with EMPTY configuration (no items selected)
        PresetsConfig config = PresetsConfig.get();
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        // Save empty set to create an empty preset
        java.util.Set<String> emptyItems = new java.util.HashSet<>();
        config.saveUnnamedPreset(emptyItems);
        
        // Clear the current config to show empty preset
        itemConfig.items.clear();
        itemConfig.saveItems(); // Use saveItems() method to save the items
        
        // Refresh the preset list
        loadPresets();
        
        // Notify parent that preset was applied (to refresh UI and clear existing items display)
        if (onPresetApplied != null) {
            onPresetApplied.accept("_unnamed");
        }
    }
    
    private void saveCurrentPreset() {
        // Can't save default preset
        if (selectedPresetKey != null && selectedPresetKey.equals("default")) {
            return;
        }
        
        String name = nameEditBox.getValue().trim();
        if (name.isEmpty()) {
            return;
        }
        
        PresetsConfig config = PresetsConfig.get();
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        
        String key;
        if (selectedPresetKey != null && !selectedPresetKey.equals("default") && !selectedPresetKey.equals("_unnamed")) {
            // Editing existing preset
            key = selectedPresetKey;
        } else {
            // Creating new preset - check if we're at max
            List<String> existingKeys = config.getPresetKeys();
            if (existingKeys.size() >= 5) { // MAX_PRESETS = 5
                // Can't create new preset, at max
                return;
            }
            key = config.generatePresetKey();
        }
        
        if (config.savePreset(key, name, itemConfig.items)) {
            selectedPresetKey = key;
            config.clearUnnamedPreset(); // Clear unnamed preset when saving
            loadPresets();
            nameEditBox.setValue(name);
            nameEditBox.setEditable(true); // Re-enable editing
        }
    }
    
    private void deleteSelectedPreset() {
        // Can't delete default or unnamed preset
        if (selectedPresetKey != null && !selectedPresetKey.equals("default") && !selectedPresetKey.equals("_unnamed")) {
            PresetsConfig config = PresetsConfig.get();
            if (config.deletePreset(selectedPresetKey)) {
                selectedPresetKey = "default"; // Select default after deletion
                PresetsConfig.Preset defaultPreset = config.getPreset("default");
                if (defaultPreset != null) {
                    nameEditBox.setValue(defaultPreset.name);
                } else {
                    nameEditBox.setValue("");
                }
                nameEditBox.setEditable(false); // Disable editing for default
                loadPresets();
            }
        }
    }
    
    private void applySelectedPreset() {
        if (selectedPresetKey != null) {
            PresetsConfig config = PresetsConfig.get();
            config.applyPreset(selectedPresetKey);
            if (onPresetApplied != null) {
                onPresetApplied.accept(selectedPresetKey);
            }
        }
    }
    
    public void setSelectedPreset(String key) {
        selectedPresetKey = key;
        PresetsConfig config = PresetsConfig.get();
        PresetsConfig.Preset preset = config.getPreset(key);
        if (preset != null) {
            nameEditBox.setValue(preset.name);
        } else {
            nameEditBox.setValue("");
        }
        
        // Disable editing for default preset
        nameEditBox.setEditable(key == null || !key.equals("default"));
    }
    
    public String getSelectedPresetKey() {
        return selectedPresetKey;
    }
    
    public Button getCreateButton() {
        return createButton;
    }
    
    /**
     * Update positions of all internal buttons and edit box relative to widget bounds.
     * This should be called when the widget is resized or moved.
     */
    public void updateChildPositions() {
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonY = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(35);
        int scaledButtonHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledButtonHeight();
        int buttonWidth = (width - scaledSpacing * 5) / 4; // 4 buttons with spacing between them
        
        // Update name edit box position (flush on top of buttons, moved up 2 pixels)
        if (nameEditBox != null) {
            int editBoxHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight();
            int editBoxY = buttonY - editBoxHeight - 2; // Position flush above buttons, moved up 2 pixels
            int editBoxWidth = width - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
            nameEditBox.x = x + com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(5);
            nameEditBox.y = editBoxY;
            nameEditBox.setWidth(editBoxWidth);
        }
        
        // Update button positions
        if (createButton != null) {
            createButton.x = x + scaledSpacing;
            createButton.y = buttonY;
            createButton.setWidth(buttonWidth);
        }
        
        if (saveButton != null) {
            saveButton.x = x + scaledSpacing * 2 + buttonWidth;
            saveButton.y = buttonY;
            saveButton.setWidth(buttonWidth);
        }
        
        if (deleteButton != null) {
            deleteButton.x = x + scaledSpacing * 3 + buttonWidth * 2;
            deleteButton.y = buttonY;
            deleteButton.setWidth(buttonWidth);
        }
        
        if (applyButton != null) {
            applyButton.x = x + scaledSpacing * 4 + buttonWidth * 3;
            applyButton.y = buttonY;
            applyButton.setWidth(buttonWidth);
        }
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Background removed - too large
        
        // Render title
        Minecraft.getInstance().font.draw(
            poseStack,
            new TranslatableComponent("buildscape.config.presets"),
            x + 5, y + 5,
            0xFFFFFF
        );
        
        // Render preset buttons - calculate available space to avoid going behind text box
        int presetY = y + 20;
        // Calculate button Y position (same as in constructor)
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(35);
        // Calculate where text box starts to avoid rendering behind it
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5; // Leave 5px gap before text box
        int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));
        int visibleCount = Math.min(presets.size() - scrollOffset, maxVisiblePresets);
        
        for (int i = 0; i < visibleCount; i++) {
            int index = scrollOffset + i;
            if (index >= presets.size()) break;
            
            PresetsConfig.Preset preset = presets.get(index);
            String presetKey = presetKeys.get(index);
            boolean isSelected = presetKey.equals(selectedPresetKey);
            
            int buttonY = presetY + i * (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING);
            
            // Don't render if button would go behind text box
            if (buttonY + PRESET_BUTTON_HEIGHT >= editBoxTop) {
                break;
            }
            // Render background - low opacity gray
            boolean isHovered = mouseX >= x + 5 && mouseX < x + width - 5 && 
                               mouseY >= buttonY && mouseY < buttonY + PRESET_BUTTON_HEIGHT;
            int bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;
            
            fill(poseStack, x + 5, buttonY, x + width - 5, buttonY + PRESET_BUTTON_HEIGHT, bgColor);
            
            // Draw preset name (truncate based on available width)
            String displayName = preset.name;
            if (presetKey.equals("_unnamed")) {
                displayName = "(Unsaved Changes)";
            } else if (displayName.isEmpty()) {
                displayName = "(Unnamed)";
            }
            
            // Calculate available width for text (button width - margins)
            int availableWidth = width - 20; // 10px margin on each side
            int textWidth = Minecraft.getInstance().font.width(displayName);
            if (textWidth > availableWidth) {
                // Truncate text to fit available width
                String truncated = Minecraft.getInstance().font.plainSubstrByWidth(displayName, availableWidth - Minecraft.getInstance().font.width("..."));
                displayName = truncated + "...";
            }
            
            Minecraft.getInstance().font.draw(
                poseStack,
                displayName,
                x + 10, buttonY + 6,
                0xFFFFFF
            );
        }
        
        // Render scrollbar if needed (based on actual visible count, not MAX_VISIBLE_PRESETS)
        // Reuse variables already calculated above
        if (presets.size() > maxVisiblePresets) {
            int scrollbarX = x + width - 10;
            int scrollbarHeight = editBoxTop - presetY - 5;
            int scrollbarY = presetY;
            
            double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
            double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
            int thumbHeight = (int) (scrollbarHeight * (maxVisiblePresets / (double) presets.size()));
            thumbHeight = Math.max(20, thumbHeight);
            int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));
            
            fill(poseStack, scrollbarX, scrollbarY, scrollbarX + 5, scrollbarY + scrollbarHeight, 0x33CCCCCC);
            fill(poseStack, scrollbarX, thumbY, scrollbarX + 5, thumbY + thumbHeight, 0x40CCCCCC);
        }
        
        // Render edit box and buttons
        nameEditBox.render(poseStack, mouseX, mouseY, partialTick);
        createButton.render(poseStack, mouseX, mouseY, partialTick);
        saveButton.render(poseStack, mouseX, mouseY, partialTick);
        deleteButton.render(poseStack, mouseX, mouseY, partialTick);
        applyButton.render(poseStack, mouseX, mouseY, partialTick);
        
        // Disable delete button for default preset
        deleteButton.active = selectedPresetKey != null && !selectedPresetKey.equals("default");
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        
        // Check preset buttons - use same calculation as rendering
        int presetY = y + 20;
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(35);
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5;
        int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));
        
        // Check if clicking on scrollbar (like creative inventory)
        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        if (maxScroll > 0) {
            int scrollbarX = x + width - 10;
            int scrollbarY = presetY;
            int scrollbarHeight = editBoxTop - presetY - 5;
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + 5 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                // Clicked on scrollbar - start dragging
                isDraggingScrollbar = true;
                scrollbarDragStartY = mouseY;
                scrollbarDragStartOffset = scrollOffset;
                
                // Calculate thumb position and check if clicking on thumb or track
                int thumbHeight = (int) (scrollbarHeight * (maxVisiblePresets / (double) presets.size()));
                thumbHeight = Math.max(20, thumbHeight);
                double scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0;
                int thumbY = scrollbarY + (int) (scrollRatio * (scrollbarHeight - thumbHeight));
                
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    // Clicked on thumb - drag from current position
                    return true;
                } else {
                    // Clicked on track - jump to that position and allow dragging from there
                    double clickRatio = (mouseY - scrollbarY) / (double) scrollbarHeight;
                    scrollOffset = (int) Math.max(0, Math.min(maxScroll, clickRatio * maxScroll));
                    scrollbarDragStartOffset = scrollOffset;
                    scrollbarDragStartY = mouseY;
                    return true;
                }
            }
        }
        
        int visibleCount = Math.min(presets.size() - scrollOffset, maxVisiblePresets);
        for (int i = 0; i < visibleCount; i++) {
            int index = scrollOffset + i;
            if (index >= presets.size()) break;
            
            int buttonY = presetY + i * (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING);
            // Don't check clicks if button would be behind text box
            if (buttonY + PRESET_BUTTON_HEIGHT >= editBoxTop) {
                break;
            }
            if (mouseX >= x + 5 && mouseX < x + width - 5 &&
                mouseY >= buttonY && mouseY < buttonY + PRESET_BUTTON_HEIGHT) {
                String presetKey = presetKeys.get(index);
                setSelectedPreset(presetKey);
                return true;
            }
        }
        
        // Check edit box and buttons
        if (nameEditBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (createButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (saveButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (deleteButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return applyButton.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        
        // Calculate max scroll based on available space
        int presetY = y + 20;
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(35);
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5;
        int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));
        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta));
        return true;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Handle scrollbar dragging (like creative inventory) - works even if mouse leaves widget
        if (isDraggingScrollbar && button == 0) {
            int presetY = y + 20;
            int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(35);
            int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
            int availableHeight = editBoxTop - presetY - 5;
            int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));
            double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
            
            if (maxScroll > 0) {
                int scrollbarY = presetY;
                int scrollbarHeight = editBoxTop - presetY - 5;
                
                // Calculate thumb height for accurate dragging
                int thumbHeight = (int) (scrollbarHeight * (maxVisiblePresets / (double) presets.size()));
                thumbHeight = Math.max(20, thumbHeight);
                
                // Calculate the usable scrollbar track height (total height minus thumb height)
                int usableTrackHeight = scrollbarHeight - thumbHeight;
                
                // Map mouse Y position to scroll position
                // Clamp mouse Y to scrollbar bounds for smooth dragging
                double clampedMouseY = Math.max(scrollbarY, Math.min(scrollbarY + scrollbarHeight, mouseY));
                double mouseYRelative = clampedMouseY - scrollbarY;
                
                // Calculate scroll ratio (0.0 to 1.0) based on where the thumb center should be
                double thumbCenterRatio = usableTrackHeight > 0 ? 
                    Math.max(0, Math.min(1, (mouseYRelative - thumbHeight / 2.0) / usableTrackHeight)) : 0;
                
                // Convert ratio to scroll offset
                scrollOffset = (int) (thumbCenterRatio * maxScroll);
                
                // Clamp to valid range
                scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset));
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDraggingScrollbar && button == 0) {
            isDraggingScrollbar = false;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return nameEditBox.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return nameEditBox.charTyped(codePoint, modifiers);
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // Not needed
    }
}

