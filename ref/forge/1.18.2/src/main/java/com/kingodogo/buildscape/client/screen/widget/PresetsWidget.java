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
    private static final int PRESET_BUTTON_SPACING = 2;
    private static final int MAX_VISIBLE_PRESETS = 6;

    private int getPresetButtonHeight() {
        float scale = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getStandardTextScale();
        return (int)(16 * scale);
    }

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
    private int headerAreaHeight = 20;

    public void setHeaderAreaHeight(int height) {
        this.headerAreaHeight = height;
    }

    private String appliedPresetKey = null;

    private boolean showCreateOptions = false;
    private final Button createDefaultBtn;
    private final Button createEmptyBtn;

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

        this.appliedPresetKey = config.getLastAppliedPreset();
        if (config.hasUnnamedPreset()) {
        }

        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonY = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40);
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
                (btn) -> {
                    showCreateOptions = !showCreateOptions;
                });
        createBtn.setCustomTextColors(0x00FF00, 0x55FF55);
        createButton = createBtn;

        createDefaultBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0,
                buttonWidth, scaledButtonHeight,
                new net.minecraft.network.chat.TextComponent("Default Items"),
                (btn) -> {
                    showCreateOptions = false;
                    createNewPreset(false);
                });
        ((com.kingodogo.buildscape.client.screen.widget.ScaledTextButton) createDefaultBtn).setCustomTextColors(0x00FF00, 0x55FF55);
        createDefaultBtn.visible = false;

        createEmptyBtn = new com.kingodogo.buildscape.client.screen.widget.ScaledTextButton(
                0, 0,
                buttonWidth, scaledButtonHeight,
                new net.minecraft.network.chat.TextComponent("Empty"),
                (btn) -> {
                    showCreateOptions = false;
                    createNewPreset(true);
                });
        ((com.kingodogo.buildscape.client.screen.widget.ScaledTextButton) createEmptyBtn).setCustomTextColors(0x00FF00, 0x55FF55);
        createEmptyBtn.visible = false;

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

    private void createNewPreset(boolean empty) {
        nameEditBox.setValue("");
        nameEditBox.setEditable(true);
        selectedPresetKey = "_unnamed";

        PresetsConfig config = PresetsConfig.get();
        PillarParticleConfig itemConfig = PillarParticleConfig.get();
        java.util.Set<String> newItems;
        if (empty) {
            newItems = new java.util.HashSet<>();
        } else {
            PresetsConfig.Preset defaultPreset = config.getPreset("default");
            if (defaultPreset != null) {
                newItems = new java.util.HashSet<>(defaultPreset.items);
            } else {
                newItems = new java.util.HashSet<>();
            }
        }
        config.saveUnnamedPreset(newItems);

        itemConfig.items.clear();
        itemConfig.items.addAll(newItems);
        itemConfig.saveItems();

        refreshPresets();

        if (onPresetApplied != null) {
            onPresetApplied.accept("_unnamed");
        }
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
            if ("_unnamed".equals(appliedPresetKey)) {
                appliedPresetKey = key;
            }

            config.clearUnnamedPreset();
            refreshPresets();
            nameEditBox.setValue(name);
            nameEditBox.setEditable(true);

            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.playSound(
                    net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL,
                    1.0f,
                    1.0f
                );
            }
        }
    }

    private void deleteSelectedPreset() {
        if (selectedPresetKey != null && !selectedPresetKey.equals("default")) {
            PresetsConfig config = PresetsConfig.get();
            if (selectedPresetKey.equals("_unnamed")) {
                config.clearUnnamedPreset();
                String revertKey = appliedPresetKey != null && !appliedPresetKey.equals("_unnamed") ? appliedPresetKey : "default";
                config.applyPreset(revertKey);

                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.playSound(
                        net.minecraft.sounds.SoundEvents.NOTE_BLOCK_DIDGERIDOO,
                        1.0f,
                        1.0f
                    );
                }

                selectedPresetKey = revertKey;
                appliedPresetKey = revertKey;

                PresetsConfig.Preset revPreset = config.getPreset(revertKey);
                if (revPreset != null) {
                    nameEditBox.setValue(revPreset.name);
                } else {
                    nameEditBox.setValue("");
                }
                nameEditBox.setEditable(false);
                refreshPresets();

                if (onPresetApplied != null) {
                    onPresetApplied.accept(revertKey);
                }
            } else if (config.deletePreset(selectedPresetKey)) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.playSound(
                        net.minecraft.sounds.SoundEvents.NOTE_BLOCK_DIDGERIDOO,
                        1.0f,
                        1.0f
                    );
                }
                if (selectedPresetKey.equals(appliedPresetKey)) {
                    appliedPresetKey = "default";
                }
                selectedPresetKey = "default";
                PresetsConfig.Preset defaultPreset = config.getPreset("default");
                if (defaultPreset != null) {
                    nameEditBox.setValue(defaultPreset.name);
                } else {
                    nameEditBox.setValue("");
                }
                nameEditBox.setEditable(false);
                refreshPresets();
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

    public void setAppliedPreset(String key) {
        this.appliedPresetKey = key;
    }

    public String getSelectedPresetKey() {
        return selectedPresetKey;
    }

    public Button getCreateButton() {
        return createButton;
    }

    public void refreshPresets() {
        loadPresets();
        int presetY = y + headerAreaHeight;
        int bottomAreaHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(40);
        int buttonYPos = y + height - bottomAreaHeight;
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5;
        int maxVisiblePresets = Math.max(1, availableHeight / (getPresetButtonHeight() + PRESET_BUTTON_SPACING));

        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        scrollOffset = (int) Math.max(0, Math.min(scrollOffset, maxScroll));
    }

    public void updateChildPositions() {
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int scaledButtonHeight = 20;


        int bottomAreaHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(22);
        int bottomAreaY = y + height - bottomAreaHeight;
        int buttonY = bottomAreaY + (bottomAreaHeight - scaledButtonHeight) / 2;

        int buttonWidth = (width - scaledSpacing * 5) / 4;
        int totalGroupWidth = (buttonWidth * 4) + (scaledSpacing * 3);
        int startX = x + (width - totalGroupWidth) / 2;

        if (nameEditBox != null) {
            int editBoxHeight = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getScaledEditBoxHeight();
            int editBoxY = buttonY - editBoxHeight - 5;
            int editBoxWidth = width - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
            nameEditBox.x = x + (width - editBoxWidth) / 2;
            nameEditBox.y = editBoxY;
            nameEditBox.setWidth(editBoxWidth);
        }

        if (createButton != null) {
            createButton.x = startX;
            createButton.y = buttonY;
            createButton.setWidth(buttonWidth);

            if (createDefaultBtn != null) {
                createDefaultBtn.x = startX;
                createDefaultBtn.y = buttonY - scaledButtonHeight - 2;
                createDefaultBtn.setWidth(buttonWidth);
            }
            if (createEmptyBtn != null) {
                createEmptyBtn.x = startX;
                createEmptyBtn.y = buttonY - (scaledButtonHeight * 2) - 4;
                createEmptyBtn.setWidth(buttonWidth);
            }
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

        int borderColor = 0xFF666666;
        fill(poseStack, x, y, x + width, y + 1, borderColor);
        fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
        fill(poseStack, x, y, x + 1, y + height, borderColor);
        fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);

        float textScale = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.getStandardTextScale();
        poseStack.pushPose();
        poseStack.scale(textScale, textScale, 1.0f);
        Minecraft.getInstance().font.draw(
                poseStack,
                new TranslatableComponent("buildscape.config.presets"),
                (x + 5) / textScale, (y + 5) / textScale,
                0xFFFFFF);
        poseStack.popPose();

        fill(poseStack, x, y + headerAreaHeight + 1, x + width, y + headerAreaHeight + 2, borderColor);

        int presetY = y + headerAreaHeight + 5;
        int scaledSpacing = com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(10);
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(22);
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - (y + headerAreaHeight);
        int buttonHeight = getPresetButtonHeight();
        int maxVisiblePresets = Math.max(1, (availableHeight - 5) / (buttonHeight + PRESET_BUTTON_SPACING));
        int visibleCount = Math.min(presets.size() - scrollOffset, maxVisiblePresets);

        for (int i = 0; i < visibleCount; i++) {
            int index = scrollOffset + i;
            if (index >= presets.size())
                break;

            PresetsConfig.Preset preset = presets.get(index);
            String presetKey = presetKeys.get(index);
            boolean isSelected = presetKey.equals(selectedPresetKey);
            boolean isApplied = presetKey.equals(appliedPresetKey);

            int buttonY = presetY + i * (buttonHeight + PRESET_BUTTON_SPACING);

            if (buttonY + buttonHeight >= editBoxTop) {
                break;
            }
            boolean isHovered = mouseX >= x + 5 && mouseX < x + width - 16
                    &&
                    mouseY >= buttonY && mouseY < buttonY + buttonHeight;
            int bgColor = isHovered ? 0x40CCCCCC : 0x33CCCCCC;

            fill(poseStack, x + 5, buttonY, x + width - 16,
                    buttonY + buttonHeight, bgColor);

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


            int textColor = 0xFFFFFF;
            if (isSelected) {
                textColor = 0xFFFF5555;
            } else if (isApplied) {
                textColor = 0xFF55FF55;
            }

            poseStack.pushPose();
            poseStack.scale(textScale, textScale, 1.0f);

            int drawY = buttonY + (buttonHeight - (int)(9 * textScale)) / 2;
            Minecraft.getInstance().font.draw(
                    poseStack,
                    displayName,
                    (x + 10) / textScale, drawY / textScale,
                    textColor);
            poseStack.popPose();
        }

        if (presets.size() > maxVisiblePresets) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
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

        createDefaultBtn.visible = showCreateOptions;
        createEmptyBtn.visible = showCreateOptions;

        if (showCreateOptions) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 500);
            int bgX = createDefaultBtn.x - 2;
            int bgY = createEmptyBtn.y - 2;
            int bgW = createDefaultBtn.getWidth() + 4;
            int bgH = (createDefaultBtn.getHeight() * 2) + 6;
            fill(poseStack, bgX, bgY, bgX + bgW, bgY + bgH, 0xD0000000);

            createEmptyBtn.render(poseStack, mouseX, mouseY, partialTick);
            createDefaultBtn.render(poseStack, mouseX, mouseY, partialTick);
            poseStack.popPose();
        }

        deleteButton.active = selectedPresetKey != null && !selectedPresetKey.equals("default");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int presetY = y + headerAreaHeight + 5;
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(22);
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - (y + headerAreaHeight);
        int buttonHeight = getPresetButtonHeight();
        int maxVisiblePresets = Math.max(1, (availableHeight - 5) / (buttonHeight + PRESET_BUTTON_SPACING));

        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        if (maxScroll > 0) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
            int scrollbarY = presetY;
            int scrollbarHeight = editBoxTop - presetY - 5;
            int contentX = x + 5;
            int contentY = presetY;
            int contentWidth = width - 21;
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

            int buttonY = presetY + i * (buttonHeight + PRESET_BUTTON_SPACING);
            if (buttonY + buttonHeight >= editBoxTop) {
                break;
            }
            if (mouseX >= x + 5 && mouseX < x + width - 5 &&
                    mouseY >= buttonY && mouseY < buttonY + buttonHeight) {
                String presetKey = presetKeys.get(index);
                setSelectedPreset(presetKey);
                return true;
            }
        }

        if (showCreateOptions) {
            if (createDefaultBtn.mouseClicked(mouseX, mouseY, button)) return true;
            if (createEmptyBtn.mouseClicked(mouseX, mouseY, button)) return true;
            showCreateOptions = false;
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

        int presetY = y + headerAreaHeight + 5;
        int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(22);
        int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
        int availableHeight = editBoxTop - (y + headerAreaHeight);
        int buttonHeight = getPresetButtonHeight();
        int maxVisiblePresets = Math.max(1, (availableHeight - 5) / (buttonHeight + PRESET_BUTTON_SPACING));
        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbarRenderer.isDragging() && button == 0) {
            int presetY = y + headerAreaHeight + 5;
            int buttonYPos = y + height - com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.scaleSize(22);
            int editBoxTop = nameEditBox != null ? nameEditBox.y : buttonYPos;
            int availableHeight = editBoxTop - (y + headerAreaHeight);
            int buttonHeight = getPresetButtonHeight();
            int maxVisiblePresets = Math.max(1, (availableHeight - 5) / (buttonHeight + PRESET_BUTTON_SPACING));
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
