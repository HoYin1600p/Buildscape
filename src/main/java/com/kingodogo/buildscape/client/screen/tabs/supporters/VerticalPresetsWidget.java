package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.client.screen.widget.CustomScrollbarRenderer;
import com.kingodogo.buildscape.client.screen.widget.ScaledTextButton;
import com.kingodogo.buildscape.config.VerticalConfig;
import com.kingodogo.buildscape.config.VerticalPresetsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.function.Consumer;

public class VerticalPresetsWidget extends AbstractWidget {
    private static final int PRESET_BUTTON_HEIGHT = 20;
    private static final int PRESET_BUTTON_SPACING = 2;
    
    private final EditBox nameEditBox;
    private final Button createButton;
    private final Button saveButton;
    private final Button deleteButton;
    private final Button applyButton;
    private final Consumer<String> onPresetApplied;
    private String selectedPresetKey = "default";
    private int scrollOffset = 0;
    private final CustomScrollbarRenderer scrollbarRenderer = new CustomScrollbarRenderer();

    public VerticalPresetsWidget(int x, int y, int width, int height, Consumer<String> onPresetApplied) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.onPresetApplied = onPresetApplied;

        int scaledSpacing = BuildScapeConfigScreen.scaleSize(5);
        int bottomAreaHeight = BuildScapeConfigScreen.scaleSize(40);
        int buttonY = y + height - bottomAreaHeight;
        int scaledButtonHeight = 20;
        int buttonWidth = (width - scaledSpacing * 5) / 4;

        nameEditBox = new EditBox(Minecraft.getInstance().font, x + 5, buttonY - 25, width - 10, 20, new TextComponent("Preset Name"));
        
        createButton = new ScaledTextButton(x + scaledSpacing, buttonY, buttonWidth, scaledButtonHeight, new TextComponent("New"), (btn) -> createNew());
        saveButton = new ScaledTextButton(x + scaledSpacing * 2 + buttonWidth, buttonY, buttonWidth, scaledButtonHeight, new TextComponent("Save"), (btn) -> save());
        deleteButton = new ScaledTextButton(x + scaledSpacing * 3 + buttonWidth * 2, buttonY, buttonWidth, scaledButtonHeight, new TextComponent("Del"), (btn) -> delete());
        applyButton = new ScaledTextButton(x + scaledSpacing * 4 + buttonWidth * 3, buttonY, buttonWidth, scaledButtonHeight, new TextComponent("Apply"), (btn) -> apply());
    }

    private void createNew() {
        VerticalPresetsConfig.get().saveUnnamedPreset(VerticalConfig.get());
        selectedPresetKey = "_unnamed";
        nameEditBox.setValue("");
    }

    private void save() {
        String name = nameEditBox.getValue();
        if (!name.isEmpty()) {
            String key = selectedPresetKey.equals("_unnamed") ? VerticalPresetsConfig.get().generatePresetKey() : selectedPresetKey;
            VerticalPresetsConfig.get().savePreset(key, name, VerticalConfig.get());
            selectedPresetKey = key;
        }
    }

    private void delete() {
        VerticalPresetsConfig.get().deletePreset(selectedPresetKey);
        selectedPresetKey = "default";
    }

    private void apply() {
        VerticalPresetsConfig.get().applyPreset(selectedPresetKey);
        if (onPresetApplied != null) onPresetApplied.accept(selectedPresetKey);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        fill(poseStack, x, y, x + width, y + height, 0x44000000);
        Minecraft.getInstance().font.draw(poseStack, new TranslatableComponent("buildscape.config.vertical.presets"), x + 5, y + 5, 0xFFFFFF);

        VerticalPresetsConfig config = VerticalPresetsConfig.get();
        List<VerticalPresetsConfig.VerticalPreset> presets = config.getPresets();
        List<String> keys = config.getPresetKeys();

        int presetY = y + 20;
        for (int i = 0; i < presets.size(); i++) {
            String key = keys.get(i);
            int rowY = presetY + i * (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING);
            boolean hovered = mouseX >= x + 5 && mouseX < x + width - 5 && mouseY >= rowY && mouseY < rowY + PRESET_BUTTON_HEIGHT;
            boolean selected = key.equals(selectedPresetKey);
            
            fill(poseStack, x + 5, rowY, x + width - 5, rowY + PRESET_BUTTON_HEIGHT, selected ? 0x60FF0000 : (hovered ? 0x40FFFFFF : 0x20FFFFFF));
            Minecraft.getInstance().font.draw(poseStack, presets.get(i).name, x + 10, rowY + 6, selected ? 0xFF0000 : 0xFFFFFF);
        }

        nameEditBox.render(poseStack, mouseX, mouseY, partialTick);
        createButton.render(poseStack, mouseX, mouseY, partialTick);
        saveButton.render(poseStack, mouseX, mouseY, partialTick);
        deleteButton.render(poseStack, mouseX, mouseY, partialTick);
        applyButton.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        
        VerticalPresetsConfig config = VerticalPresetsConfig.get();
        List<String> keys = config.getPresetKeys();
        int presetY = y + 20;
        for (int i = 0; i < keys.size(); i++) {
            int rowY = presetY + i * (PRESET_BUTTON_HEIGHT + PRESET_BUTTON_SPACING);
            if (mouseX >= x + 5 && mouseX < x + width - 5 && mouseY >= rowY && mouseY < rowY + PRESET_BUTTON_HEIGHT) {
                selectedPresetKey = keys.get(i);
                nameEditBox.setValue(config.getPreset(selectedPresetKey).name);
                return true;
            }
        }

        if (nameEditBox.mouseClicked(mouseX, mouseY, button)) return true;
        if (createButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (saveButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (deleteButton.mouseClicked(mouseX, mouseY, button)) return true;
        return applyButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) { return nameEditBox.charTyped(codePoint, modifiers); }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return nameEditBox.keyPressed(keyCode, scanCode, modifiers); }
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
