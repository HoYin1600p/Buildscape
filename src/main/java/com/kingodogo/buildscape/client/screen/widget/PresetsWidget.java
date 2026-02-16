package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.config.PresetsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.function.Consumer;

public class PresetsWidget extends AbstractWidget {
    private static final int PRESET_BUTTON_HEIGHT = 20;
    private static final int PRESET_BUTTON_SPACING = 2;
    private static final int MAX_VISIBLE_PRESETS = 6;

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
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    private String appliedPresetKey = null;

    public PresetsWidget(int x, int y, int width, int height, Consumer<String> onPresetApplied) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.onPresetApplied = onPresetApplied;

        loadPresets();

        PresetsConfig config = PresetsConfig.get();
        if (selectedPresetKey == null) {
            if (config.hasUnnamedPreset()) {
                selectedPresetKey = "_unnamed";
            } else {
                selectedPresetKey = "default";
            }
        }

        // Initialize applied preset key
        this.appliedPresetKey = config.getLastAppliedPreset();
        if (config.hasUnnamedPreset()) {
            // If unamed preset exists, it might be the applied one if user just edited properties
            // But strictly speaking, applied key is what was last applied.
        }

        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonY = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40); // moved
                                                                                                                // up
        int scaledButtonHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledButtonHeight();
        int buttonWidth = (width - scaledSpacing * 5) / 4;

        int editBoxHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight();
        int editBoxY = buttonY - editBoxHeight - 2;
        int editBoxWidth = width - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        nameEditBox = new EditBox(
                Minecraft.getInstance().font,
                x + com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(5), editBoxY,
                editBoxWidth, com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight(),
                new TranslatableComponent("buildscape.config.preset.name"));
        nameEditBox.setMaxLength(32);

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

        if (selectedPresetKey != null && selectedPresetKey.equals("default")) {
            nameEditBox.setEditable(false);
        }

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton createBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                x + scaledSpacing, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.create"),
                (btn) -> createNewPreset());
        createBtn.setCustomTextColors(0x00FF00, 0x55FF55);
        createButton = createBtn;

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton saveBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                x + scaledSpacing * 2 + buttonWidth, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.save"),
                (btn) -> saveCurrentPreset());
        saveBtn.setCustomTextColors(0x00FFFF, 0x55FFFF);
        saveButton = saveBtn;

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton deleteBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                x + scaledSpacing * 3 + buttonWidth * 2, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.delete"),
                (btn) -> deleteSelectedPreset());
        deleteBtn.setCustomTextColors(0xFF0000, 0xFF5555);
        deleteButton = deleteBtn;

        com.kingodogo.buildscape.client.screen.widget.ScaledTextButton applyBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                x + scaledSpacing * 4 + buttonWidth * 3, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.apply"),
                (btn) -> applySelectedPreset());
        applyBtn.setCustomTextColors(0xFFAA00, 0xFFFF55);
        applyButton = applyBtn;
    }

    private void loadPresets() {
        PresetsConfig config = PresetsConfig.get();
        presets = config.getPresets();
        presetKeys = config.getPresetKeys();
    }

    private void createNewPreset() {
        nameEditBox.setValue("");
        nameEditBox.setEditable(true);
        selectedPresetKey = "_unnamed";

        PresetsConfig config = PresetsConfig.get();
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        java.util.Set<String> emptyItems = new java.util.HashSet<>();
        config.saveUnnamedPreset(emptyItems);

        itemConfig.items.clear();
        itemConfig.saveItems();

        loadPresets();

        if (onPresetApplied != null) {
            onPresetApplied.accept("_unnamed");
        }
        // Applying explicitly sets applied key? Or assume create implies user is working on it.
        // Usually creating clears items so effectively we applied empty.
        appliedPresetKey = "_unnamed"; 
    }

    private void saveCurrentPreset() {
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
        if (selectedPresetKey != null && !selectedPresetKey.equals("default")
                && !selectedPresetKey.equals("_unnamed")) {
            key = selectedPresetKey;
        } else {
            List<String> existingKeys = config.getPresetKeys();
            if (existingKeys.size() >= 5) {
                return;
            }
            key = config.generatePresetKey();
        }

        if (config.savePreset(key, name, itemConfig.items)) {
            selectedPresetKey = key;
            // If we saved the unnamed preset (which was applied), update applied key to new key
            if ("_unnamed".equals(appliedPresetKey)) {
                appliedPresetKey = key;
            }
            
            config.clearUnnamedPreset();
            loadPresets();
            nameEditBox.setValue(name);
            nameEditBox.setEditable(true);
        }
    }

    private void deleteSelectedPreset() {
        if (selectedPresetKey != null && !selectedPresetKey.equals("default")
                && !selectedPresetKey.equals("_unnamed")) {
            PresetsConfig config = PresetsConfig.get();
            if (config.deletePreset(selectedPresetKey)) {
                if (selectedPresetKey.equals(appliedPresetKey)) {
                    appliedPresetKey = "default"; // Fallback
                    // Or keep null? Default is safer.
                }
                selectedPresetKey = "default";
                PresetsConfig.Preset defaultPreset = config.getPreset("default");
                if (defaultPreset != null) {
                    nameEditBox.setValue(defaultPreset.name);
                } else {
                    nameEditBox.setValue("");
                }
                nameEditBox.setEditable(false);
                loadPresets();
            }
        }
    }

    private void applySelectedPreset() {
        if (selectedPresetKey != null) {
            PresetsConfig config = PresetsConfig.get();
            config.applyPreset(selectedPresetKey);
            appliedPresetKey = selectedPresetKey;
            
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

        nameEditBox.setEditable(key == null || !key.equals("default"));
    }

    // Allow external update of applied key ensuring sync
    public void setAppliedPreset(String key) {
        this.appliedPresetKey = key;
    }

    public String getSelectedPresetKey() {
        return selectedPresetKey;
    }

    public Button getCreateButton() {
        return createButton;
    }

    public void updateChildPositions() {
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int scaledButtonHeight = 20; // Fixed 20px for vanilla button assets

        // Calculate button width to fit 4 buttons with spacing
        // But maybe we want them centered with a fixed width?
        // Existing logic: int buttonWidth = (width - scaledSpacing * 5) / 4;
        // Let's keep the width calculation but center the whole group if needed, 
        // or just ensure they fill the space nicely. The previous logic filled the width.
        // If the user wants them centered, maybe they mean the text inside? 
        // "inside there respecitive boxes" -> sounds like text inside button.
        // "centre algin the other buttons as well like create save and such" -> sounds like buttons themselves.
        // The screenshot shows "Create Save Delete Apply" left aligned?
        // No, the screenshot shows them spread out.
        // Wait, current logic: `x + scaledSpacing`, `x + scaledSpacing * 2 + buttonWidth`...
        // This spreads them out.
        // Let's ensuring the *vertical* alignment is centered in the bottom area.

        int bottomAreaHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40);
        int bottomAreaY = y + height - bottomAreaHeight;
        // Center buttons vertically in the bottom area
        int buttonY = bottomAreaY + (bottomAreaHeight - scaledButtonHeight) / 2;

        // Horizontal centering of the group:
        int buttonWidth = (width - scaledSpacing * 5) / 4;
        // Calculate total width of the group
        int totalGroupWidth = (buttonWidth * 4) + (scaledSpacing * 3);
        int startX = x + (width - totalGroupWidth) / 2;

        if (nameEditBox != null) {
            int editBoxHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight();
            int editBoxY = buttonY - editBoxHeight - 5; // moved up a bit more
            int editBoxWidth = width - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
            nameEditBox.x = x + (width - editBoxWidth) / 2; // Center edit box too
            nameEditBox.y = editBoxY;
            nameEditBox.setWidth(editBoxWidth);
        }

        if (createButton != null) {
            createButton.x = startX;
            createButton.y = buttonY;
            createButton.setWidth(buttonWidth);
        }

        if (saveButton != null) {
            saveButton.x = startX + buttonWidth + scaledSpacing;
            saveButton.y = buttonY;
            saveButton.setWidth(buttonWidth);
        }

        if (deleteButton != null) {
            deleteButton.x = startX + (buttonWidth + scaledSpacing) * 2;
            deleteButton.y = buttonY;
            deleteButton.setWidth(buttonWidth);
        }

        if (applyButton != null) {
            applyButton.x = startX + (buttonWidth + scaledSpacing) * 3;
            applyButton.y = buttonY;
            applyButton.setWidth(buttonWidth);
        }
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        // Draw border around panel (debug mode)
        if (com.kingodogo.buildscape.client.screen.DebugRenderConfig.RENDER_PANEL_BORDERS) {
            int borderColor = com.kingodogo.buildscape.client.screen.DebugRenderConfig.PANEL_BORDER_COLOR;
            fill(poseStack, x, y, x + width, y + 1, borderColor); // Top
            fill(poseStack, x, y + height - 1, x + width, y + height, borderColor); // Bottom
            fill(poseStack, x, y, x + 1, y + height, borderColor); // Left
            fill(poseStack, x + width - 1, y, x + width, y + height, borderColor); // Right
        }

        Minecraft.getInstance().font.draw(
                poseStack,
                new TranslatableComponent("buildscape.config.presets"),
                x + 5, y + 5,
                0xFFFFFF);

        int presetY = y + 20;
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40); // Lift
                                                                                                                   // buttons
                                                                                                                   // up
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5;
        int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));
        int visibleCount = Math.min(presets.size() - scrollOffset, maxVisiblePresets);

        for (int i = 0; i < visibleCount; i++) {
            int index = scrollOffset + i;
            if (index >= presets.size())
                break;

            PresetsConfig.Preset preset = presets.get(index);
            String presetKey = presetKeys.get(index);
            boolean isSelected = presetKey.equals(selectedPresetKey);
            boolean isApplied = presetKey.equals(appliedPresetKey);

            int buttonY = presetY + i * (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING);

            if (buttonY + PRESET_BUTTON_HEIGHT >= editBoxTop) {
                break;
            }
            // Hover check: x + 5 to width - 16 (scrollbar area)
            boolean isHovered = mouseX >= x + 5 && mouseX < x + width - 16
                    &&
                    mouseY >= buttonY && mouseY < buttonY + PRESET_BUTTON_HEIGHT;
            int bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;

            fill(poseStack, x + 5, buttonY, x + width - 16,
                    buttonY + PRESET_BUTTON_HEIGHT, bgColor);

            String displayName = preset.name;
            if (presetKey.equals("_unnamed")) {
                displayName = "(Unsaved Changes)";
            } else if (displayName.isEmpty()) {
                displayName = "(Unnamed)";
            }

            int availableWidth = width - 20;
            int textWidth = Minecraft.getInstance().font.width(displayName);
            if (textWidth > availableWidth) {
                String truncated = Minecraft.getInstance().font.plainSubstrByWidth(displayName,
                        availableWidth - Minecraft.getInstance().font.width("..."));
                displayName = truncated + "...";
            }

            // Text Color Logic:
            // Selected -> Light Red (0xFFFF5555)
            // Applied (and not selected) -> Green (0xFF55FF55)
            // Else -> White (0xFFFFFF)

            int textColor = 0xFFFFFF;
            if (isSelected) {
                textColor = 0xFFFF5555; // Light Red
            } else if (isApplied) {
                textColor = 0xFF55FF55; // Light Green
            }

            Minecraft.getInstance().font.draw(
                    poseStack,
                    displayName,
                    x + 10, buttonY + 6,
                    textColor);
        }

        if (presets.size() > maxVisiblePresets) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarHeight = editBoxTop - presetY - 5;
            int scrollbarY = presetY;

            double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
            double visibleRatio = maxVisiblePresets / (double) presets.size();
            scrollbarRenderer.renderScrollbar(poseStack, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, maxScroll, visibleRatio);
        }

        nameEditBox.render(poseStack, mouseX, mouseY, partialTick);
        createButton.render(poseStack, mouseX, mouseY, partialTick);
        saveButton.render(poseStack, mouseX, mouseY, partialTick);
        deleteButton.render(poseStack, mouseX, mouseY, partialTick);
        applyButton.render(poseStack, mouseX, mouseY, partialTick);

        deleteButton.active = selectedPresetKey != null && !selectedPresetKey.equals("default");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int presetY = y + 20;
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40); // Lift
                                                                                                                   // buttons
                                                                                                                   // up
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5;
        int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));

        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        if (maxScroll > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4; // 4px form edge
            int scrollbarY = presetY;
            int scrollbarHeight = editBoxTop - presetY - 5;
            int contentX = x + 5;
            int contentY = presetY;
            int contentWidth = width - 21; // width - 16 - 5
            int contentHeight = scrollbarHeight;

            double visibleRatio = maxVisiblePresets / (double) presets.size();
            double newOffset = scrollbarRenderer.handleMouseClick(mouseX, mouseY, button,
                    scrollbarX, scrollbarY, scrollbarHeight,
                    contentX, contentY, contentWidth, contentHeight,
                    scrollOffset, maxScroll, visibleRatio);

            if (newOffset >= 0) {
                scrollOffset = (int) newOffset;
                return true;
            }
        }

        int visibleCount = Math.min(presets.size() - scrollOffset, maxVisiblePresets);
        for (int i = 0; i < visibleCount; i++) {
            int index = scrollOffset + i;
            if (index >= presets.size())
                break;

            int buttonY = presetY + i * (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING);
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

        int presetY = y + 20;
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40); // Lift
                                                                                                                   // buttons
                                                                                                                   // up
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5;
        int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));
        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbarRenderer.isDragging() && button == 0) {
            int presetY = y + 20;
            int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40); // Lift
                                                                                                                       // buttons
                                                                                                                       // up
            int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
            int availableHeight = editBoxTop - presetY - 5;
            int maxVisiblePresets = Math.max(1, availableHeight / (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING));
            double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);

            if (maxScroll > 0) {
                int scrollbarY = presetY;
                int scrollbarHeight = editBoxTop - presetY - 5;
                double visibleRatio = maxVisiblePresets / (double) presets.size();

                double newOffset = scrollbarRenderer.handleMouseDrag(mouseY, scrollbarY, scrollbarHeight,
                        maxScroll, visibleRatio, 1.0);

                if (newOffset >= 0) {
                    scrollOffset = (int) newOffset;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scrollbarRenderer.handleMouseRelease(button);
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
    }
}
