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

        createButton = new Button(
                x + scaledSpacing, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.create"),
                (btn) -> createNewPreset());

        saveButton = new Button(
                x + scaledSpacing * 2 + buttonWidth, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.save"),
                (btn) -> saveCurrentPreset());

        deleteButton = new Button(
                x + scaledSpacing * 3 + buttonWidth * 2, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.delete"),
                (btn) -> deleteSelectedPreset());

        applyButton = new Button(
                x + scaledSpacing * 4 + buttonWidth * 3, buttonY,
                buttonWidth, scaledButtonHeight,
                new TranslatableComponent("buildscape.config.preset.apply"),
                (btn) -> applySelectedPreset());
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

    public String getSelectedPresetKey() {
        return selectedPresetKey;
    }

    public Button getCreateButton() {
        return createButton;
    }

    public void updateChildPositions() {
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonY = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40);
        int scaledButtonHeight = 20; // Fixed 20px for vanilla button assets
        int buttonWidth = (width - scaledSpacing * 5) / 4;

        if (nameEditBox != null) {
            int editBoxHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight();
            int editBoxY = buttonY - editBoxHeight - 2;
            int editBoxWidth = width - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
            nameEditBox.x = x + com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(5);
            nameEditBox.y = editBoxY;
            nameEditBox.setWidth(editBoxWidth);
        }

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

            Minecraft.getInstance().font.draw(
                    poseStack,
                    displayName,
                    x + 10, buttonY + 6,
                    0xFFFFFF);
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
