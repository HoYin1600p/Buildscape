package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.config.PresetsConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.sounds.SoundEvents;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class PresetsWidget implements ICustomWidget {
    private static final int PRESET_BUTTON_SPACING = 2;
    private static final int MAX_VISIBLE_PRESETS = 6;

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean active = true;
    private boolean focused = false;

    private int getPresetButtonHeight() {
        float scale = BuildScapeConfigScreen.getStandardTextScale();
        return (int)(16 * scale);
    }

    private List<PresetsConfig.Preset> presets;
    private List<String> presetKeys;
    private String selectedPresetKey = null;
    private final EditBox nameEditBox;
    private final ScaledTextButton createButton;
    private final ScaledTextButton saveButton;
    private final ScaledTextButton deleteButton;
    private final ScaledTextButton applyButton;
    private final Consumer<String> onPresetApplied;
    private int scrollOffset = 0;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();
    private int headerAreaHeight = 20;

    public void setHeaderAreaHeight(int height) {
        this.headerAreaHeight = height;
    }

    private String appliedPresetKey = null;

    private boolean showCreateOptions = false;
    private final ScaledTextButton createDefaultBtn;
    private final ScaledTextButton createEmptyBtn;

    public PresetsWidget(int x, int y, int width, int height, Consumer<String> onPresetApplied) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

        int scaledSpacing = BuildScapeConfigScreen.scaleSize(10);
        int buttonY = y + height - BuildScapeConfigScreen.scaleSize(40);
        int scaledButtonHeight = BuildScapeConfigScreen.getScaledButtonHeight();
        int buttonWidth = (width - scaledSpacing * 5) / 4;

        int editBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
        int editBoxY = buttonY - editBoxHeight - 2;
        int editBoxWidth = width - BuildScapeConfigScreen.scaleSize(10);
        nameEditBox = new EditBox(
                Minecraft.getInstance().font,
                x + BuildScapeConfigScreen.scaleSize(5), editBoxY,
                editBoxWidth, BuildScapeConfigScreen.getScaledEditBoxHeight(),
                ComponentHelper.translatable("buildscape.config.preset.name"));
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

        ScaledTextButton createBtn = new ScaledTextButton(
                x + scaledSpacing, buttonY,
                buttonWidth, scaledButtonHeight,
                ComponentHelper.translatable("buildscape.config.preset.create"),
                (btn) -> {
                    showCreateOptions = !showCreateOptions;
                });
        createBtn.setCustomTextColors(0x00FF00, 0x55FF55);
        createButton = createBtn;

        createDefaultBtn = new ScaledTextButton(
                0, 0,
                buttonWidth, scaledButtonHeight,
                ComponentHelper.literal("Default Items"),
                (btn) -> {
                    showCreateOptions = false;
                    createNewPreset(false);
                });
        createDefaultBtn.setCustomTextColors(0x00FF00, 0x55FF55);
        createDefaultBtn.visible = false;

        createEmptyBtn = new ScaledTextButton(
                0, 0,
                buttonWidth, scaledButtonHeight,
                ComponentHelper.literal("Empty"),
                (btn) -> {
                    showCreateOptions = false;
                    createNewPreset(true);
                });
        createEmptyBtn.setCustomTextColors(0x00FF00, 0x55FF55);
        createEmptyBtn.visible = false;

        ScaledTextButton saveBtn = new ScaledTextButton(
                x + scaledSpacing * 2 + buttonWidth, buttonY,
                buttonWidth, scaledButtonHeight,
                ComponentHelper.translatable("buildscape.config.preset.save"),
                (btn) -> saveCurrentPreset());
        saveBtn.setCustomTextColors(0x00FFFF, 0x55FFFF);
        saveButton = saveBtn;

        ScaledTextButton deleteBtn = new ScaledTextButton(
                x + scaledSpacing * 3 + buttonWidth * 2, buttonY,
                buttonWidth, scaledButtonHeight,
                ComponentHelper.translatable("buildscape.config.preset.delete"),
                (btn) -> deleteSelectedPreset());
        deleteBtn.setCustomTextColors(0xFF0000, 0xFF5555);
        deleteButton = deleteBtn;

        ScaledTextButton applyBtn = new ScaledTextButton(
                x + scaledSpacing * 4 + buttonWidth * 3, buttonY,
                buttonWidth, scaledButtonHeight,
                ComponentHelper.translatable("buildscape.config.preset.apply"),
                (btn) -> applySelectedPreset());
        applyBtn.setCustomTextColors(0xFFAA00, 0xFFFF55);
        applyButton = applyBtn;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
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
        Set<String> newItems;
        if (empty) {
            newItems = new HashSet<>();
        } else {
            PresetsConfig.Preset defaultPreset = config.getPreset("default");
            if (defaultPreset != null) {
                newItems = new HashSet<>(defaultPreset.items);
            } else {
                newItems = new HashSet<>();
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

            Services.PLATFORM.playNoteBlockBell();
        }
    }

    private void deleteSelectedPreset() {
        if (selectedPresetKey != null && !selectedPresetKey.equals("default")) {
            PresetsConfig config = PresetsConfig.get();
            if (selectedPresetKey.equals("_unnamed")) {
                config.clearUnnamedPreset();
                String revertKey = appliedPresetKey != null && !appliedPresetKey.equals("_unnamed") ? appliedPresetKey : "default";
                config.applyPreset(revertKey);

                Services.PLATFORM.playNoteBlockDidgeridoo();

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
                Services.PLATFORM.playNoteBlockDidgeridoo();
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

    public ScaledTextButton getCreateButton() {
        return createButton;
    }

    public void refreshPresets() {
        loadPresets();
        int presetY = y + headerAreaHeight;
        int bottomAreaHeight = BuildScapeConfigScreen.scaleSize(40);
        int buttonYPos = y + height - bottomAreaHeight;
        int editBoxTop = nameEditBox != null ? WidgetLayoutHelper.getY(nameEditBox) : buttonYPos;
        int availableHeight = editBoxTop - presetY - 5;
        int maxVisiblePresets = Math.max(1, availableHeight / (getPresetButtonHeight() + PRESET_BUTTON_SPACING));

        double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
        scrollOffset = (int) Math.max(0, Math.min(scrollOffset, maxScroll));
    }

    public void updateChildPositions() {
        int scaledSpacing = BuildScapeConfigScreen.scaleSize(10);
        int scaledButtonHeight = 20;

        int bottomAreaHeight = BuildScapeConfigScreen.scaleSize(22);
        int bottomAreaY = y + height - bottomAreaHeight;
        int buttonY = bottomAreaY + (bottomAreaHeight - scaledButtonHeight) / 2;

        int buttonWidth = (width - scaledSpacing * 5) / 4;
        int totalGroupWidth = (buttonWidth * 4) + (scaledSpacing * 3);
        int startX = x + (width - totalGroupWidth) / 2;

        if (nameEditBox != null) {
            int editBoxHeight = BuildScapeConfigScreen.getScaledEditBoxHeight();
            int editBoxY = buttonY - editBoxHeight - 5;
            int editBoxWidth = width - BuildScapeConfigScreen.scaleSize(10);
            WidgetLayoutHelper.setPosition(nameEditBox, x + (width - editBoxWidth) / 2, editBoxY);
            nameEditBox.setWidth(editBoxWidth);
        }

        if (createButton != null) {
            createButton.setPosition(startX, buttonY);
            createButton.setWidth(buttonWidth);

            if (createDefaultBtn != null) {
                createDefaultBtn.setPosition(startX, buttonY - scaledButtonHeight - 2);
                createDefaultBtn.setWidth(buttonWidth);
            }
            if (createEmptyBtn != null) {
                createEmptyBtn.setPosition(startX, buttonY - (scaledButtonHeight * 2) - 4);
                createEmptyBtn.setWidth(buttonWidth);
            }
        }

        if (saveButton != null) {
            saveButton.setPosition(startX + buttonWidth + scaledSpacing, buttonY);
            saveButton.setWidth(buttonWidth);
        }

        if (deleteButton != null) {
            deleteButton.setPosition(startX + (buttonWidth + scaledSpacing) * 2, buttonY);
            deleteButton.setWidth(buttonWidth);
        }

        if (applyButton != null) {
            applyButton.setPosition(startX + (buttonWidth + scaledSpacing) * 3, buttonY);
            applyButton.setWidth(buttonWidth);
        }
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        int borderColor = 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + height - 1, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + width - 1, y, x + width, y + height, borderColor);

        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, textScale, textScale);
        Services.PLATFORM.draw(
                poseStackOrGraphics,
                Minecraft.getInstance().font,
                ComponentHelper.translatable("buildscape.config.presets").getString(),
                (x + 5) / textScale, (y + 5) / textScale,
                0xFFFFFF);
        Services.PLATFORM.popGuiPose(poseStackOrGraphics);

        Services.PLATFORM.fill(poseStackOrGraphics, x, y + headerAreaHeight + 1, x + width, y + headerAreaHeight + 2, borderColor);

        int presetY = y + headerAreaHeight + 5;
        int buttonYPos = y + height - BuildScapeConfigScreen.scaleSize(22);
        int editBoxTop = nameEditBox != null ? WidgetLayoutHelper.getY(nameEditBox) : buttonYPos;
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

            Services.PLATFORM.fill(poseStackOrGraphics, x + 5, buttonY, x + width - 16,
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

            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, textScale, textScale);

            int drawY = buttonY + (buttonHeight - (int)(9 * textScale)) / 2;
            Services.PLATFORM.draw(
                    poseStackOrGraphics,
                    Minecraft.getInstance().font,
                    displayName,
                    (x + 10) / textScale, drawY / textScale,
                    textColor);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        }

        if (presets.size() > maxVisiblePresets) {
            int scrollbarX = x + width - CustomScrollbarRenderer.getScrollbarWidth() - 4;
            int scrollbarHeight = editBoxTop - presetY - 5;
            int scrollbarY = presetY;

            double maxScroll = Math.max(0, presets.size() - maxVisiblePresets);
            double visibleRatio = maxVisiblePresets / (double) presets.size();
            scrollbarRenderer.renderScrollbar(poseStackOrGraphics, scrollbarX, scrollbarY, scrollbarHeight,
                    scrollOffset, maxScroll, visibleRatio);
        }

        Services.PLATFORM.renderWidget(poseStackOrGraphics, nameEditBox, mouseX, mouseY, partialTick);
        createButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        saveButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        deleteButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        applyButton.render(poseStackOrGraphics, mouseX, mouseY, partialTick);

        createDefaultBtn.visible = showCreateOptions;
        createEmptyBtn.visible = showCreateOptions;

        if (showCreateOptions) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.translateGuiPose(poseStackOrGraphics, 0, 0, 500);
            int bgX = createDefaultBtn.getX() - 2;
            int bgY = createEmptyBtn.getY() - 2;
            int bgW = createDefaultBtn.getWidth() + 4;
            int bgH = (createDefaultBtn.getHeight() * 2) + 6;
            Services.PLATFORM.fill(poseStackOrGraphics, bgX, bgY, bgX + bgW, bgY + bgH, 0xD0000000);

            createEmptyBtn.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
            createDefaultBtn.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        }

        deleteButton.active = selectedPresetKey != null && !selectedPresetKey.equals("default");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int presetY = y + headerAreaHeight + 5;
        int buttonYPos = y + height - BuildScapeConfigScreen.scaleSize(22);
        int editBoxTop = nameEditBox != null ? WidgetLayoutHelper.getY(nameEditBox) : buttonYPos;
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

        if (Services.PLATFORM.widgetMouseClicked(nameEditBox, mouseX, mouseY, button)) {
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
        int buttonYPos = y + height - BuildScapeConfigScreen.scaleSize(22);
        int editBoxTop = nameEditBox != null ? WidgetLayoutHelper.getY(nameEditBox) : buttonYPos;
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
            int buttonYPos = y + height - BuildScapeConfigScreen.scaleSize(22);
            int editBoxTop = nameEditBox != null ? WidgetLayoutHelper.getY(nameEditBox) : buttonYPos;
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
        return Services.PLATFORM.widgetKeyPressed(nameEditBox, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return Services.PLATFORM.widgetCharTyped(nameEditBox, codePoint, modifiers);
    }
}
