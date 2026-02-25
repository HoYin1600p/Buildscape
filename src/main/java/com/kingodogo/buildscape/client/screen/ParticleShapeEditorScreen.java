package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.particle.ParticleShapeLibrary;
import com.kingodogo.buildscape.particle.ParticleShapeReloader;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;

/**
 * In-game particle wing shape editor and preview
 * Allows creating and testing custom wing shapes without restart
 */
public class ParticleShapeEditorScreen extends Screen {

    private EditBox wingLengthInput;
    private EditBox wingHeightInput;
    private EditBox maxWidthInput;
    private EditBox lengthStepInput;
    private EditBox shapeNameInput;
    private String selectedPattern = "curved";
    private String previewShapeId = "custom_preview";
    private int currentPage = 0;

    private static final int BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 100;
    private static final int PREVIEW_WIDTH = 150;
    private static final int PREVIEW_HEIGHT = 100;

    public ParticleShapeEditorScreen() {
        super(new TextComponent("Particle Shape Editor"));
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();

        int x = 10;
        int y = 10;
        int spacing = 25;

        // Title
        this.addRenderableWidget(new Button(this.width / 2 - 100, y, 200, 20,
                new TextComponent("Wing Shape Editor"),
                (btn) -> {}) {
            @Override
            public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                this.active = false;
                super.render(poseStack, mouseX, mouseY, partialTick);
            }
        });
        y += spacing;

        // Shape Name Input
        shapeNameInput = new EditBox(this.font, x, y, INPUT_WIDTH, 18, new TextComponent("Shape Name"));
        shapeNameInput.setValue("custom_shape");
        this.addRenderableWidget(shapeNameInput);
        y += spacing;

        // Wing Length
        wingLengthInput = createLabeledInput(x, y, "Wing Length:", 1.7);
        y += spacing;

        // Wing Height
        wingHeightInput = createLabeledInput(x, y, "Wing Height:", 1.6);
        y += spacing;

        // Max Width
        maxWidthInput = createLabeledInput(x, y, "Max Width:", 0.65);
        y += spacing;

        // Length Step
        lengthStepInput = createLabeledInput(x, y, "Length Step:", 0.12);
        y += spacing;

        // Pattern Selection
        y += 10;
        this.addRenderableWidget(new Button(x, y, 80, BUTTON_HEIGHT,
                new TextComponent("Pattern: " + selectedPattern),
                (btn) -> cyclePattern()) {
            @Override
            public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                this.setMessage(new TextComponent("Pattern: " + selectedPattern));
                super.render(poseStack, mouseX, mouseY, partialTick);
            }
        });
        y += spacing;

        // Preview Button
        this.addRenderableWidget(new Button(x, y, 80, BUTTON_HEIGHT,
                new TextComponent("Preview"), (btn) -> updatePreview()));
        y += spacing;

        // Save Button
        this.addRenderableWidget(new Button(x, y, 80, BUTTON_HEIGHT,
                new TextComponent("Save Shape"), (btn) -> saveShape()));
        y += spacing;

        // Load Preset Buttons
        y += 10;
        this.addRenderableWidget(new Button(x, y, 80, BUTTON_HEIGHT,
                new TextComponent("Load: Curved"), (btn) -> loadPreset("curved")));
        y += spacing;

        this.addRenderableWidget(new Button(x, y, 80, BUTTON_HEIGHT,
                new TextComponent("Load: Heart"), (btn) -> loadPreset("heart")));
        y += spacing;

        this.addRenderableWidget(new Button(x, y, 80, BUTTON_HEIGHT,
                new TextComponent("Load: Sparkle"), (btn) -> loadPreset("sparkle")));
        y += spacing;

        this.addRenderableWidget(new Button(x, y, 80, BUTTON_HEIGHT,
                new TextComponent("Load: Cake"), (btn) -> loadPreset("cake")));
        y += spacing;

        // Close Button
        y = this.height - 30;
        this.addRenderableWidget(new Button(this.width / 2 - 50, y, 100, BUTTON_HEIGHT,
                new TextComponent("Close"), (btn) -> this.onClose()));
    }

    private EditBox createLabeledInput(int x, int y, String label, double defaultValue) {
        EditBox input = new EditBox(this.font, x + 120, y, INPUT_WIDTH, 18, new TextComponent(label));
        input.setValue(String.format("%.2f", defaultValue));
        this.addRenderableWidget(input);
        return input;
    }

    private void cyclePattern() {
        String[] patterns = {"curved", "heart", "sparkle", "cake"};
        int currentIndex = java.util.Arrays.asList(patterns).indexOf(selectedPattern);
        selectedPattern = patterns[(currentIndex + 1) % patterns.length];
    }

    private void updatePreview() {
        try {
            double wingLength = Double.parseDouble(wingLengthInput.getValue());
            double wingHeight = Double.parseDouble(wingHeightInput.getValue());
            double maxWidth = Double.parseDouble(maxWidthInput.getValue());
            double lengthStep = Double.parseDouble(lengthStepInput.getValue());

            // Create preview configuration
            ParticleShapeLibrary.WingShapeConfig previewConfig =
                    new ParticleShapeLibrary.WingShapeConfig(
                            previewShapeId,
                            wingLength, wingHeight, maxWidth, lengthStep,
                            selectedPattern
                    );

            // Register for preview
            ParticleShapeLibrary.registerShape(previewShapeId, previewConfig);
            ParticleShapeReloader.reloadAllShapes();

        } catch (NumberFormatException e) {
        }
    }

    private void saveShape() {
        try {
            String shapeName = shapeNameInput.getValue();
            if (shapeName.isEmpty()) {
                return;
            }

            double wingLength = Double.parseDouble(wingLengthInput.getValue());
            double wingHeight = Double.parseDouble(wingHeightInput.getValue());
            double maxWidth = Double.parseDouble(maxWidthInput.getValue());
            double lengthStep = Double.parseDouble(lengthStepInput.getValue());

            // Create and register the shape
            ParticleShapeLibrary.WingShapeConfig shapeConfig =
                    new ParticleShapeLibrary.WingShapeConfig(
                            shapeName,
                            wingLength, wingHeight, maxWidth, lengthStep,
                            selectedPattern
                    );

            ParticleShapeLibrary.registerShape(shapeName, shapeConfig);
            ParticleShapeReloader.reloadAllShapes();

        } catch (NumberFormatException e) {
        }
    }

    private void loadPreset(String presetName) {
        ParticleShapeLibrary.WingShapeConfig preset = ParticleShapeLibrary.getShape(presetName);
        wingLengthInput.setValue(String.format("%.2f", preset.wingLength));
        wingHeightInput.setValue(String.format("%.2f", preset.wingHeight));
        maxWidthInput.setValue(String.format("%.2f", preset.maxWidth));
        lengthStepInput.setValue(String.format("%.4f", preset.lengthStep));
        selectedPattern = preset.pattern;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        // Draw help text
        int helpY = this.height - 80;
        this.font.draw(poseStack, "Create custom wing shapes for players!", 10, helpY, 0xFFFFFF);
        this.font.draw(poseStack, "Adjust parameters, preview, and save.", 10, helpY + 12, 0xFFFFFF);
        this.font.draw(poseStack, "No restart needed - hot-reload enabled!", 10, helpY + 24, 0x00FF00);

        // Draw shape stats
        int statsY = this.height - 50;
        String stats = ParticleShapeLibrary.getShapeStats(previewShapeId);
        this.font.draw(poseStack, stats, 10, statsY, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        // Clear preview when closing
        ParticleShapeLibrary.clearCache();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
