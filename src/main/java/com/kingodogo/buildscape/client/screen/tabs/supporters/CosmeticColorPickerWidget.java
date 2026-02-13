package com.kingodogo.buildscape.client.screen.tabs.supporters;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;

public class CosmeticColorPickerWidget extends AbstractWidget {
    private static final int GRADIENT_SIZE = 80;
    private static final int HUE_SLIDER_WIDTH = 20;
    private static final int HUE_SLIDER_HEIGHT = 80;
    private static final int SLIDER_HEIGHT = 12;
    private static final int SLIDER_WIDTH = 80;
    private static final int SLIDER_SPACING = 18;
    private static final int TEXT_FIELD_HEIGHT = 20;
    private static final int VALUE_TEXT_WIDTH = 40;

    private int currentColor;
    private final Consumer<String> onColorChanged;
    private boolean draggingHue = false;
    private boolean draggingGradient = false;
    private boolean draggingR = false, draggingG = false, draggingB = false;
    private boolean draggingH = false, draggingS = false, draggingBrightness = false;
    private float hue = 0.0f;
    private float saturation = 1.0f;
    private float brightness = 1.0f;
    private int r = 255, g = 255, b = 255;
    private boolean enabled = true;
    private float currentScale = 1.0f;

    public EditBox rField, gField, bField;
    public EditBox hField, sField, brightnessField;
    public EditBox hexField;
    private boolean updatingFields = false;

    public CosmeticColorPickerWidget(int x, int y, int width, int height, int initialColor, Consumer<String> onColorChanged) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.currentColor = initialColor;
        this.onColorChanged = onColorChanged;
        rgbToHsb(initialColor);
        updateRgbFromColor();
        createValueFields();
    }

    public void setColor(int rgb) {
        this.currentColor = rgb;
        rgbToHsb(rgb);
        updateRgbFromColor();
        updateFieldValues();
    }

    private void createValueFields() {
        Minecraft mc = Minecraft.getInstance();
        int fieldWidth = 40;
        int fieldHeight = 12;

        rField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        rField.setMaxLength(3);
        rField.setFilter(s -> s.matches("[0-9]*"));
        rField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    int newR = Integer.parseInt(value);
                    if (newR >= 0 && newR <= 255) {
                        r = newR;
                        updateColorFromRgb();
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        gField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        gField.setMaxLength(3);
        gField.setFilter(s -> s.matches("[0-9]*"));
        gField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    int newG = Integer.parseInt(value);
                    if (newG >= 0 && newG <= 255) {
                        g = newG;
                        updateColorFromRgb();
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        bField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        bField.setMaxLength(3);
        bField.setFilter(s -> s.matches("[0-9]*"));
        bField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    int newB = Integer.parseInt(value);
                    if (newB >= 0 && newB <= 255) {
                        b = newB;
                        updateColorFromRgb();
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        hField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        hField.setMaxLength(6);
        hField.setFilter(s -> s.matches("[0-9.]*"));
        hField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    float newH = Float.parseFloat(value);
                    if (newH >= 0 && newH <= 360) {
                        hue = newH;
                        updateColorFromHsb();
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        sField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        sField.setMaxLength(5);
        sField.setFilter(s -> s.matches("[0-9.]*"));
        sField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    float newS = Float.parseFloat(value);
                    if (newS >= 0 && newS <= 100) {
                        saturation = newS / 100.0f;
                        updateColorFromHsb();
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        brightnessField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        brightnessField.setMaxLength(5);
        brightnessField.setFilter(s -> s.matches("[0-9.]*"));
        brightnessField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    float newB = Float.parseFloat(value);
                    if (newB >= 0 && newB <= 100) {
                        brightness = newB / 100.0f;
                        updateColorFromHsb();
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
        
        // Hex Field
        hexField = new EditBox(mc.font, 0, 0, 60, fieldHeight, new TextComponent(""));
        hexField.setMaxLength(7); // #RRGGBB
        hexField.setFilter(s -> s.matches("[0-9a-fA-F#]*"));
        hexField.setResponder(value -> {
             if (!updatingFields && !value.isEmpty()) {
                 try {
                     String hex = value.startsWith("#") ? value.substring(1) : value;
                     if (hex.length() == 6) {
                         int color = Integer.parseInt(hex, 16);
                         this.currentColor = color;
                         rgbToHsb(color);
                         // Don't call updateColorFromRgb/Hsb fully to avoid recursion loop or text reset unless valid
                         r = (color >> 16) & 0xFF;
                         g = (color >> 8) & 0xFF;
                         b = color & 0xFF;
                         
                         // Manually update other fields but keep hex as is (or normalize format)
                         updatingFields = true;
                         if (rField != null) rField.setValue(String.valueOf(r));
                         if (gField != null) gField.setValue(String.valueOf(g));
                         if (bField != null) bField.setValue(String.valueOf(b));
                         if (hField != null) hField.setValue(String.format("%.1f", hue));
                         if (sField != null) sField.setValue(String.format("%.1f", saturation * 100.0f));
                         if (brightnessField != null) brightnessField.setValue(String.format("%.1f", brightness * 100.0f));
                         updatingFields = false;
                         
                         if (onColorChanged != null) {
                             onColorChanged.accept("#" + hex.toUpperCase());
                         }
                     }
                 } catch (NumberFormatException e) {
                     // Ignore invalid hex
                 }
             }
        });

        updateFieldValues();
    }

    private void updateFieldValues() {
        updatingFields = true;
        if (rField != null)
            rField.setValue(String.valueOf(r));
        if (gField != null)
            gField.setValue(String.valueOf(g));
        if (bField != null)
            bField.setValue(String.valueOf(b));
        if (hField != null)
            hField.setValue(String.format("%.1f", hue));
        if (sField != null)
            sField.setValue(String.format("%.1f", saturation * 100.0f));
        if (brightnessField != null)
            brightnessField.setValue(String.format("%.1f", brightness * 100.0f));
        if (hexField != null)
            hexField.setValue(String.format("#%06X", currentColor));
        updatingFields = false;
    }

    public int getColor() {
        return currentColor;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public float getCurrentScale() {
        return currentScale;
    }

    private void updateRgbFromColor() {
        r = (currentColor >> 16) & 0xFF;
        g = (currentColor >> 8) & 0xFF;
        b = currentColor & 0xFF;
    }

    private void rgbToHsb(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);
        this.hue = hsb[0] * 360.0f;
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    private int hsbToRgb(float h, float s, float b) {
        java.awt.Color color = java.awt.Color.getHSBColor(h / 360.0f, s, b);
        return (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        float totalUnscaledWidth = GRADIENT_SIZE + 5 + HUE_SLIDER_WIDTH + 10 + 15 + SLIDER_WIDTH + VALUE_TEXT_WIDTH
                + 10;
        float totalUnscaledHeight = GRADIENT_SIZE + 5 + 20 + 10;

        float scaleX = (float) width / totalUnscaledWidth;
        float scaleY = (float) height / totalUnscaledHeight;
        currentScale = Math.min(scaleX, scaleY);
        currentScale = Math.min(1.0f, currentScale);
        float scale = currentScale;

        int scaledGradientSize = (int) (GRADIENT_SIZE * scale);
        int scaledHueSliderWidth = (int) (HUE_SLIDER_WIDTH * scale);
        int scaledHueSliderHeight = (int) (HUE_SLIDER_HEIGHT * scale);
        int scaledSliderWidth = (int) (SLIDER_WIDTH * scale);
        int scaledSliderHeight = (int) (SLIDER_HEIGHT * scale);
        int scaledSliderSpacing = (int) (SLIDER_SPACING * scale);
        int scaledValueTextWidth = (int) (VALUE_TEXT_WIDTH * scale);
        int scaledGap1 = (int) (5 * scale);
        // Increased gap to 8 for better text spacing
        int scaledGap2 = (int) (8 * scale); 
        int scaledLabelWidth = (int) (15 * scale);
        int scaledPadding = (int) (5 * scale);

        int gradientX = x + scaledPadding;
        int gradientY = y + scaledPadding;
        int hueSliderX = gradientX + scaledGradientSize + scaledGap1;
        int hueSliderY = gradientY;

        for (int py = 0; py < scaledGradientSize; py++) {
            for (int px = 0; px < scaledGradientSize; px++) {
                float s = px / (float) scaledGradientSize;
                float b = 1.0f - (py / (float) scaledGradientSize);
                int color = hsbToRgb(hue, s, b);
                fill(poseStack, gradientX + px, gradientY + py, gradientX + px + 1, gradientY + py + 1,
                        0xFF000000 | color);
            }
        }

        fill(poseStack, gradientX - 1, gradientY - 1, gradientX + scaledGradientSize + 1, gradientY, 0xFF000000);
        fill(poseStack, gradientX - 1, gradientY + scaledGradientSize, gradientX + scaledGradientSize + 1,
                gradientY + scaledGradientSize + 1, 0xFF000000);
        fill(poseStack, gradientX - 1, gradientY - 1, gradientX, gradientY + scaledGradientSize + 1, 0xFF000000);
        fill(poseStack, gradientX + scaledGradientSize, gradientY - 1, gradientX + scaledGradientSize + 1,
                gradientY + scaledGradientSize + 1, 0xFF000000);

        for (int py = 0; py < scaledHueSliderHeight; py++) {
            float h = (py / (float) scaledHueSliderHeight) * 360.0f;
            int color = hsbToRgb(h, 1.0f, 1.0f);
            fill(poseStack, hueSliderX, hueSliderY + py, hueSliderX + scaledHueSliderWidth, hueSliderY + py + 1,
                    0xFF000000 | color);
        }

        fill(poseStack, hueSliderX - 1, hueSliderY - 1, hueSliderX + scaledHueSliderWidth + 1, hueSliderY, 0xFF000000);
        fill(poseStack, hueSliderX - 1, hueSliderY + scaledHueSliderHeight, hueSliderX + scaledHueSliderWidth + 1,
                hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);
        fill(poseStack, hueSliderX - 1, hueSliderY - 1, hueSliderX, hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);
        fill(poseStack, hueSliderX + scaledHueSliderWidth, hueSliderY - 1, hueSliderX + scaledHueSliderWidth + 1,
                hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);

        int indicatorX = gradientX + (int) (saturation * scaledGradientSize);
        int indicatorY = gradientY + (int) ((1.0f - brightness) * scaledGradientSize);
        fill(poseStack, indicatorX - 2, indicatorY - 2, indicatorX + 2, indicatorY + 2, 0xFFFFFFFF);
        fill(poseStack, indicatorX - 1, indicatorY - 1, indicatorX + 1, indicatorY + 1, 0xFF000000);

        int hueIndicatorY = hueSliderY + (int) ((hue / 360.0f) * scaledHueSliderHeight);
        fill(poseStack, hueSliderX - 3, hueIndicatorY - 2, hueSliderX, hueIndicatorY + 2, 0xFFFFFFFF);
        fill(poseStack, hueSliderX + scaledHueSliderWidth, hueIndicatorY - 2, hueSliderX + scaledHueSliderWidth + 3,
                hueIndicatorY + 2, 0xFFFFFFFF);

        // --- Custom Render Area for Preview and Hex Input ---
        int previewY = gradientY + scaledGradientSize + scaledGap1;
        int previewX = gradientX;
        // Make preview height consistent with scale, maybe slightly larger for better input area
        int previewHeight = (int) (20 * scale); 
        int totalPreviewWidth = scaledGradientSize + scaledHueSliderWidth + scaledGap1;
        
        // Split area: Left is Square Color Box, Right is Hex
        int colorBoxWidth = previewHeight; // Square
        int hexBoxX = previewX + colorBoxWidth + scaledGap1;
        int hexBoxWidth = totalPreviewWidth - colorBoxWidth - scaledGap1;
        
        // Render Color Box
        fill(poseStack, previewX, previewY, previewX + colorBoxWidth, previewY + previewHeight,
                0xFF000000 | currentColor);
        fill(poseStack, previewX - 1, previewY - 1, previewX + colorBoxWidth + 1, previewY, 0xFF000000);
        fill(poseStack, previewX - 1, previewY + previewHeight, previewX + colorBoxWidth + 1,
                previewY + previewHeight + 1, 0xFF000000);
        fill(poseStack, previewX - 1, previewY - 1, previewX, previewY + previewHeight + 1, 0xFF000000);
        fill(poseStack, previewX + colorBoxWidth, previewY - 1, previewX + colorBoxWidth + 1,
                previewY + previewHeight + 1, 0xFF000000);

        // Render Hex Field
        if (hexField != null) {
            // Calculate virtual bounds (unscaled)
            int virtualX = (int) (hexBoxX / scale);
            // Center vertically.
            // Pushing down by 4 virtual pixels
            int virtualY = (int) (previewY / scale) + 4; 
            int virtualWidth = (int) (hexBoxWidth / scale);
            int virtualHeight = (int) (previewHeight / scale);
            
            hexField.x = virtualX;
            hexField.y = virtualY;
            hexField.setWidth(virtualWidth);
            // hexField.setHeight(virtualHeight); // Protected access
            
            poseStack.pushPose();
            poseStack.scale(scale, scale, 1.0f);
            hexField.render(poseStack, (int)(mouseX / scale), (int)(mouseY / scale), partialTick);
            poseStack.popPose();
        }
        
        // --- End Custom Render ---

        int rgbStartX = hueSliderX + scaledHueSliderWidth + scaledGap2;
        int rgbStartY = gradientY;
        renderSliderScaled(poseStack, mc, rgbStartX, rgbStartY, "R", r, 0, 255, 0xFF0000, draggingR, scale, false,
                mouseX, mouseY, partialTick);
        renderSliderScaled(poseStack, mc, rgbStartX, rgbStartY + scaledSliderSpacing, "G", g, 0, 255, 0x00FF00,
                draggingG, scale, false, mouseX, mouseY, partialTick);
        renderSliderScaled(poseStack, mc, rgbStartX, rgbStartY + scaledSliderSpacing * 2, "B", b, 0, 255, 0x0000FF,
                draggingB, scale, false, mouseX, mouseY, partialTick);

        int hsbStartY = rgbStartY + scaledSliderSpacing * 3 + 5;
        renderSliderScaled(poseStack, mc, rgbStartX, hsbStartY, "H", (int) hue, 0, 360, -1, draggingH, scale, true,
                mouseX, mouseY, partialTick);
        renderSliderScaled(poseStack, mc, rgbStartX, hsbStartY + scaledSliderSpacing, "S", (int) (saturation * 100), 0,
                100, -1, draggingS, scale, true, mouseX, mouseY, partialTick);
        renderSliderScaled(poseStack, mc, rgbStartX, hsbStartY + scaledSliderSpacing * 2, "B", (int) (brightness * 100),
                0, 100, -1, draggingBrightness, scale, true, mouseX, mouseY, partialTick);

        if (!enabled) {
            fill(poseStack, x, y, x + width, y + height, 0x80000000);
        }
    }

    private void renderSliderScaled(PoseStack poseStack, Minecraft mc, int x, int y, String label, int value, int min,
            int max, int gradientColor, boolean isDragging, float scale, boolean isHsb, int mouseX, int mouseY,
            float partialTick) {
        int scaledSliderWidth = (int) (SLIDER_WIDTH * scale);
        int scaledSliderHeight = (int) (SLIDER_HEIGHT * scale);

        poseStack.pushPose();
        if (scale < 1.0f) {
            poseStack.translate(x, y + 2, 0);
            poseStack.scale(scale, scale, 1.0f);
            mc.font.draw(poseStack, label, 0, 0, 0xFFFFFF);
        } else {
            mc.font.draw(poseStack, label, x, y + 2, 0xFFFFFF);
        }
        poseStack.popPose();

        int sliderX = x + 15;
        int sliderY = y;

        if (gradientColor == -1) {
            if (label.equals("H")) {
                for (int px = 0; px < scaledSliderWidth; px++) {
                    float h = (px / (float) scaledSliderWidth) * 360.0f;
                    int color = hsbToRgb(h, 1.0f, 1.0f);
                    fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + scaledSliderHeight,
                            0xFF000000 | color);
                }
            } else {
                int baseColor = label.equals("S") ? hsbToRgb(hue, 1.0f, brightness) : hsbToRgb(hue, saturation, 1.0f);
                for (int px = 0; px < scaledSliderWidth; px++) {
                    float ratio = px / (float) scaledSliderWidth;
                    int r = (baseColor >> 16) & 0xFF;
                    int g = (baseColor >> 8) & 0xFF;
                    int bl = baseColor & 0xFF;
                    if (label.equals("S")) {
                        r = (int) (128 + (r - 128) * ratio);
                        g = (int) (128 + (g - 128) * ratio);
                        bl = (int) (128 + (bl - 128) * ratio);
                    } else {
                        r = (int) (r * ratio);
                        g = (int) (g * ratio);
                        bl = (int) (bl * ratio);
                    }
                    int color = (r << 16) | (g << 8) | bl;
                    fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + scaledSliderHeight,
                            0xFF000000 | color);
                }
            }
        } else {
            for (int px = 0; px < scaledSliderWidth; px++) {
                float ratio = px / (float) scaledSliderWidth;
                int color = 0;
                if (label.equals("R")) {
                    color = ((int) (ratio * 255) << 16) | ((g << 8) | b);
                } else if (label.equals("G")) {
                    color = (r << 16) | ((int) (ratio * 255) << 8) | b;
                } else {
                    color = (r << 16) | (g << 8) | (int) (ratio * 255);
                }
                fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + scaledSliderHeight,
                        0xFF000000 | color);
            }
        }

        fill(poseStack, sliderX - 1, sliderY - 1, sliderX + scaledSliderWidth + 1, sliderY, 0xFF000000);
        fill(poseStack, sliderX - 1, sliderY + scaledSliderHeight, sliderX + scaledSliderWidth + 1,
                sliderY + scaledSliderHeight + 1, 0xFF000000);
        fill(poseStack, sliderX - 1, sliderY - 1, sliderX, sliderY + scaledSliderHeight + 1, 0xFF000000);
        fill(poseStack, sliderX + scaledSliderWidth, sliderY - 1, sliderX + scaledSliderWidth + 1,
                sliderY + scaledSliderHeight + 1, 0xFF000000);

        float ratio = (value - min) / (float) (max - min);
        int indicatorX = sliderX + (int) (ratio * scaledSliderWidth);
        fill(poseStack, indicatorX - 1, sliderY - 2, indicatorX + 1, sliderY + scaledSliderHeight + 2, 0xFFFFFFFF);
        fill(poseStack, indicatorX, sliderY - 1, indicatorX, sliderY + scaledSliderHeight + 1, 0xFF000000);

        int fieldX = sliderX + scaledSliderWidth + 3;
        int fieldY = sliderY;

        String valueStr = isHsb && !label.equals("H") ? String.format("%.1f", (float) value) : String.valueOf(value);
        int textWidth = mc.font.width(valueStr);

        int scaledFieldWidth = (int) ((textWidth + 6) * scale);
        int scaledFieldHeight = (int) (SLIDER_HEIGHT * scale) + 1;

        // Render manual EditBox background/text if not real editbox (the original code did this, but also created real EditBoxes that were rendered?? No, original code renderSliderScaled draws a FAKE field)
        // Wait, lines 407-415 in original draw the field.
        // But createValueFields created REAL EditBoxes.
        // And renderButton didn't call render() on them.
        // So they were invisible interaction layers? Or I missed where they are rendered.
        // Original code: mouseClicked calls rField.mouseClicked.
        // But renderButton does NOT render rField. it renders a fake box at lines 407-415.
        // This implies the real EditBoxes are hidden and only used for input?
        // OR, they are overlayed?
        // If they are not rendered, you can't see what you type unless the fake box updates.
        // The fake box updates from `value`. 
        // When you type in `rField`, it updates `r`, which updates `value` in render.
        // So the user types blindly? No, EditBox usually renders cursor.
        // If render isn't called, standard EditBox features (cursor, selection) won't show.
        // This suggests the original implementation might be slightly broken or relies on something I don't see.
        // But `CosmeticsDisplayPanel` wants "enter hex code directly".
        // I should render the `hexField` PROPERLY.
        
        fill(poseStack, fieldX, fieldY, fieldX + scaledFieldWidth, fieldY + scaledFieldHeight, 0xFF000000);
        fill(poseStack, fieldX + 1, fieldY + 1, fieldX + scaledFieldWidth - 1, fieldY + scaledFieldHeight - 1,
                0x80FFFFFF);

        poseStack.pushPose();
        poseStack.translate(fieldX + 2, fieldY + 2, 0);
        poseStack.scale(scale, scale, 1.0f);
        mc.font.draw(poseStack, valueStr, -1, 0, 0xFF000000);
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hexField != null) {
            // Transform mouse to virtual scaled coordinates for hexField
            if (hexField.mouseClicked(mouseX / currentScale, mouseY / currentScale, button)) {
                return true;
            }
        }
        
        if (rField != null && rField.mouseClicked(mouseX, mouseY, button))
            return true;
        if (gField != null && gField.mouseClicked(mouseX, mouseY, button))
            return true;
        if (bField != null && bField.mouseClicked(mouseX, mouseY, button))
            return true;
        if (hField != null && hField.mouseClicked(mouseX, mouseY, button))
            return true;
        if (sField != null && sField.mouseClicked(mouseX, mouseY, button))
            return true;
        if (brightnessField != null && brightnessField.mouseClicked(mouseX, mouseY, button))
            return true;

        if (!enabled || button != 0) {
            clearAllDragging();
            return false;
        }

        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return false;
        }
        
        // Use cached scale for consistency
        float scale = currentScale;

        int scaledGradientSize = (int) (GRADIENT_SIZE * scale);
        int scaledHueSliderWidth = (int) (HUE_SLIDER_WIDTH * scale);
        int scaledHueSliderHeight = (int) (HUE_SLIDER_HEIGHT * scale);
        int scaledSliderWidth = (int) (SLIDER_WIDTH * scale);
        int scaledSliderSpacing = (int) (SLIDER_SPACING * scale);
        int scaledGap1 = (int) (5 * scale);
        // Match gap from renderButton
        int scaledGap2 = (int) (8 * scale);
        int scaledPadding = (int) (5 * scale);

        int gradientX = x + scaledPadding;
        int gradientY = y + scaledPadding;
        int hueSliderX = gradientX + scaledGradientSize + scaledGap1;
        int hueSliderY = gradientY;
        int rgbStartX = hueSliderX + scaledHueSliderWidth + scaledGap2;
        int rgbStartY = gradientY;
        int hsbStartY = rgbStartY + scaledSliderSpacing * 3 + 5; // Fixed 5 offset matches render logic

        if (mouseX >= gradientX - 2 && mouseX < gradientX + scaledGradientSize + 2 &&
                mouseY >= gradientY - 2 && mouseY < gradientY + scaledGradientSize + 2) {
            draggingGradient = true;
            clearOtherDragging();
            double clampedX = Math.max(gradientX, Math.min(gradientX + scaledGradientSize - 1, mouseX));
            double clampedY = Math.max(gradientY, Math.min(gradientY + scaledGradientSize - 1, mouseY));
            saturation = (float) Math.max(0, Math.min(1, (clampedX - gradientX) / (double) scaledGradientSize));
            brightness = (float) Math.max(0, Math.min(1, 1.0 - (clampedY - gradientY) / (double) scaledGradientSize));
            updateColorFromHsb();
            return true;
        }

        if (mouseX >= hueSliderX - 2 && mouseX < hueSliderX + scaledHueSliderWidth + 2 &&
                mouseY >= hueSliderY - 2 && mouseY < hueSliderY + scaledHueSliderHeight + 2) {
            draggingGradient = false;
            draggingR = draggingG = draggingB = false;
            draggingH = draggingS = draggingBrightness = false;
            draggingHue = true;
            double clampedY = Math.max(hueSliderY, Math.min(hueSliderY + scaledHueSliderHeight - 1, mouseY));
            hue = (float) Math.max(0,
                    Math.min(360, ((clampedY - hueSliderY) / (double) scaledHueSliderHeight) * 360.0));
            updateColorFromHsb();
            return true;
        }

        int sliderX = rgbStartX + 15;
        if (mouseX >= sliderX - 2 && mouseX < sliderX + scaledSliderWidth + 2) {
            double clampedX = Math.max(sliderX, Math.min(sliderX + scaledSliderWidth - 1, mouseX));
            float ratio = (float) Math.max(0, Math.min(1, (clampedX - sliderX) / (double) scaledSliderWidth));

            if (mouseY >= rgbStartY - 2 && mouseY < rgbStartY + scaledSliderSpacing + 2) {
                draggingGradient = false;
                draggingHue = false;
                draggingG = draggingB = false;
                draggingH = draggingS = draggingBrightness = false;
                draggingR = true;
                r = (int) (ratio * 255);
                updateColorFromRgb();
                return true;
            } else if (mouseY >= rgbStartY + scaledSliderSpacing - 2
                    && mouseY < rgbStartY + scaledSliderSpacing * 2 + 2) {
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingB = false;
                draggingH = draggingS = draggingBrightness = false;
                draggingG = true;
                g = (int) (ratio * 255);
                updateColorFromRgb();
                return true;
            } else if (mouseY >= rgbStartY + scaledSliderSpacing * 2 - 2
                    && mouseY < rgbStartY + scaledSliderSpacing * 3 + 2) {
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = false;
                draggingH = draggingS = draggingBrightness = false;
                draggingB = true;
                b = (int) (ratio * 255);
                updateColorFromRgb();
                return true;
            }
        }

        if (mouseX >= sliderX - 2 && mouseX < sliderX + scaledSliderWidth + 2) {
            double clampedX = Math.max(sliderX, Math.min(sliderX + scaledSliderWidth - 1, mouseX));
            float ratio = (float) Math.max(0, Math.min(1, (clampedX - sliderX) / (double) scaledSliderWidth));

            if (mouseY >= hsbStartY - 2 && mouseY < hsbStartY + scaledSliderSpacing + 2) {
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = draggingB = false;
                draggingS = draggingBrightness = false;
                draggingH = true;
                hue = ratio * 360.0f;
                updateColorFromHsb();
                return true;
            } else if (mouseY >= hsbStartY + scaledSliderSpacing - 2
                    && mouseY < hsbStartY + scaledSliderSpacing * 2 + 2) {
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = draggingB = false;
                draggingH = draggingBrightness = false;
                draggingS = true;
                saturation = ratio;
                updateColorFromHsb();
                return true;
            } else if (mouseY >= hsbStartY + scaledSliderSpacing * 2 - 2
                    && mouseY < hsbStartY + scaledSliderSpacing * 3 + 2) {
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = draggingB = false;
                draggingH = draggingS = false;
                draggingBrightness = true;
                brightness = ratio;
                updateColorFromHsb();
                return true;
            }
        }

        clearAllDragging();
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        boolean isDragging = draggingGradient || draggingHue || draggingR || draggingG || draggingB ||
                draggingH || draggingS || draggingBrightness;

        if (!enabled && !isDragging) {
            clearAllDragging();
            return false;
        }
        
        if (isDragging) {
            float scale = currentScale;

            int scaledGradientSize = (int) (GRADIENT_SIZE * scale);
            int scaledHueSliderWidth = (int) (HUE_SLIDER_WIDTH * scale);
            int scaledHueSliderHeight = (int) (HUE_SLIDER_HEIGHT * scale);
            int scaledSliderWidth = (int) (SLIDER_WIDTH * scale);
            int scaledSliderSpacing = (int) (SLIDER_SPACING * scale);
            int scaledGap1 = (int) (5 * scale);
            // Match gap
            int scaledGap2 = (int) (8 * scale);
            int scaledPadding = (int) (5 * scale);

            int gradientX = x + scaledPadding;
            int gradientY = y + scaledPadding;
            int hueSliderX = gradientX + scaledGradientSize + scaledGap1;
            int hueSliderY = gradientY;
            int rgbStartX = hueSliderX + scaledHueSliderWidth + scaledGap2;
            int sliderX = rgbStartX + (int) (15 * scale);

            if (draggingGradient) {
                double clampedX = Math.max(gradientX, Math.min(gradientX + scaledGradientSize - 1, mouseX));
                double clampedY = Math.max(gradientY, Math.min(gradientY + scaledGradientSize - 1, mouseY));
                saturation = (float) Math.max(0, Math.min(1, (clampedX - gradientX) / (double) scaledGradientSize));
                brightness = (float) Math.max(0, Math.min(1, 1.0 - (clampedY - gradientY) / (double) scaledGradientSize));
                updateColorFromHsb();
                return true;
            }

            if (draggingHue) {
                double clampedY = Math.max(hueSliderY, Math.min(hueSliderY + scaledHueSliderHeight - 1, mouseY));
                hue = (float) Math.max(0,
                        Math.min(360, ((clampedY - hueSliderY) / (double) scaledHueSliderHeight) * 360.0));
                updateColorFromHsb();
                return true;
            }

            if (draggingR || draggingG || draggingB || draggingH || draggingS || draggingBrightness) {
                double clampedX = Math.max(sliderX, Math.min(sliderX + scaledSliderWidth - 1, mouseX));
                float ratio = (float) Math.max(0, Math.min(1, (clampedX - sliderX) / (double) scaledSliderWidth));

                if (draggingR) {
                    r = (int) (ratio * 255);
                    updateColorFromRgb();
                    return true;
                }

                if (draggingG) {
                    g = (int) (ratio * 255);
                    updateColorFromRgb();
                    return true;
                }

                if (draggingB) {
                    b = (int) (ratio * 255);
                    updateColorFromRgb();
                    return true;
                }

                if (draggingH) {
                    hue = ratio * 360.0f;
                    updateColorFromHsb();
                    return true;
                }

                if (draggingS) {
                    saturation = ratio;
                    updateColorFromHsb();
                    return true;
                }

                if (draggingBrightness) {
                    brightness = ratio;
                    updateColorFromHsb();
                    return true;
                }
            }
            return true;
        }

        // Only allow text selection if NOT dragging a color/slider
        if (hexField != null && hexField.mouseDragged(mouseX / currentScale, mouseY / currentScale, button, dragX / currentScale, dragY / currentScale)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean wasDragging = draggingGradient || draggingHue || draggingR || draggingG || draggingB ||
                draggingH || draggingS || draggingBrightness;
        clearAllDragging();
        if (hexField != null) hexField.mouseReleased(mouseX, mouseY, button);
        return wasDragging;
    }

    private void clearAllDragging() {
        draggingGradient = false;
        draggingHue = false;
        draggingR = draggingG = draggingB = false;
        draggingH = draggingS = draggingBrightness = false;
    }

    private void clearOtherDragging() {
        if (!draggingGradient)
            draggingGradient = false;
        if (!draggingHue)
            draggingHue = false;
        if (!draggingR)
            draggingR = false;
        if (!draggingG)
            draggingG = false;
        if (!draggingB)
            draggingB = false;
        if (!draggingH)
            draggingH = false;
        if (!draggingS)
            draggingS = false;
        if (!draggingBrightness)
            draggingBrightness = false;
    }

    private void updateColorFromHsb() {
        currentColor = hsbToRgb(hue, saturation, brightness);
        updateRgbFromColor();
        updateFieldValues();
        if (onColorChanged != null) {
            String hexColor = String.format("#%06X", currentColor);
            onColorChanged.accept(hexColor);
        }
    }

    private void updateColorFromRgb() {
        currentColor = (r << 16) | (g << 8) | b;
        rgbToHsb(currentColor);
        updateFieldValues();
        if (onColorChanged != null) {
            String hexColor = String.format("#%06X", currentColor);
            onColorChanged.accept(hexColor);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // No narration implemented
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (hexField != null && hexField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (rField != null && rField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (gField != null && gField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (bField != null && bField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (hField != null && hField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (sField != null && sField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (brightnessField != null && brightnessField.keyPressed(keyCode, scanCode, modifiers)) return true;
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (hexField != null && hexField.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (rField != null && rField.charTyped(codePoint, modifiers)) return true;
        if (gField != null && gField.charTyped(codePoint, modifiers)) return true;
        if (bField != null && bField.charTyped(codePoint, modifiers)) return true;
        if (hField != null && hField.charTyped(codePoint, modifiers)) return true;
        if (sField != null && sField.charTyped(codePoint, modifiers)) return true;
        if (brightnessField != null && brightnessField.charTyped(codePoint, modifiers)) return true;
        
        return super.charTyped(codePoint, modifiers);
    }
}
