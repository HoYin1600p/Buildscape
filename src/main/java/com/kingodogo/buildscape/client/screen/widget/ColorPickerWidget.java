package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;

public class ColorPickerWidget extends AbstractWidget {
    private static final int GRADIENT_SIZE = 80;
    private static final int HUE_SLIDER_WIDTH = 20;
    private static final int HUE_SLIDER_HEIGHT = 80;
    private static final int SLIDER_HEIGHT = 12;
    private static final int SLIDER_WIDTH = 80; // Reduced to make room for value text
    private static final int SLIDER_SPACING = 18;
    private static final int TEXT_FIELD_HEIGHT = 20;
    private static final int VALUE_TEXT_WIDTH = 40; // Space for value text
    
    private int currentColor; // RGB color (0xRRGGBB)
    private Consumer<String> onColorChanged;
    private boolean draggingHue = false;
    private boolean draggingGradient = false;
    private boolean draggingR = false, draggingG = false, draggingB = false;
    private boolean draggingH = false, draggingS = false, draggingBrightness = false;
    private float hue = 0.0f; // 0-360
    private float saturation = 1.0f; // 0-1
    private float brightness = 1.0f; // 0-1
    private int r = 255, g = 255, b = 255; // RGB values 0-255
    private boolean enabled = true; // Whether the picker is enabled
    private float currentScale = 1.0f; // Current scale factor for rendering
    
    // Editable text fields for RGB and HSB values
    public EditBox rField, gField, bField; // Made public for parent access
    public EditBox hField, sField, brightnessField; // Made public for parent access
    private boolean updatingFields = false; // Prevent recursive updates
    
    public ColorPickerWidget(int x, int y, int width, int height, int initialColor, Consumer<String> onColorChanged) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
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
        
        // RGB fields - will be positioned in render method
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
                    // Ignore invalid input
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
                    // Ignore invalid input
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
                    // Ignore invalid input
                }
            }
        });
        
        // HSB fields
        hField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        hField.setMaxLength(6); // Allow "360.0"
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
                    // Ignore invalid input
                }
            }
        });
        
        sField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        sField.setMaxLength(5); // Allow "100.0"
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
                    // Ignore invalid input
                }
            }
        });
        
        brightnessField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, new TextComponent(""));
        brightnessField.setMaxLength(5); // Allow "100.0"
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
                    // Ignore invalid input
                }
            }
        });
        
        // Set initial values
        updateFieldValues();
    }
    
    private void updateFieldValues() {
        updatingFields = true;
        if (rField != null) rField.setValue(String.valueOf(r));
        if (gField != null) gField.setValue(String.valueOf(g));
        if (bField != null) bField.setValue(String.valueOf(b));
        if (hField != null) hField.setValue(String.format("%.1f", hue));
        if (sField != null) sField.setValue(String.format("%.1f", saturation * 100.0f));
        if (brightnessField != null) brightnessField.setValue(String.format("%.1f", brightness * 100.0f));
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
        
        // Calculate scale factor based on available size vs ideal size
        // Ideal size: 250x220, but we'll scale down if needed
        // Set minimum scale to 0.75 so it doesn't become too small
        float scaleX = Math.min(1.0f, Math.max(0.75f, (width - 20) / 250.0f)); // Account for padding, min 0.75
        float scaleY = Math.min(1.0f, Math.max(0.75f, (height - 20) / 220.0f)); // Min 0.75
        currentScale = Math.min(scaleX, scaleY); // Use smaller scale to maintain aspect ratio
        float scale = currentScale;
        
        // Scale components
        int scaledGradientSize = (int)(GRADIENT_SIZE * scale);
        int scaledHueSliderWidth = (int)(HUE_SLIDER_WIDTH * scale);
        int scaledHueSliderHeight = (int)(HUE_SLIDER_HEIGHT * scale);
        int scaledSliderWidth = (int)(SLIDER_WIDTH * scale);
        int scaledSliderHeight = (int)(SLIDER_HEIGHT * scale);
        int scaledSliderSpacing = (int)(SLIDER_SPACING * scale);
        
        // Center the picker if it's smaller than ideal
        int offsetX = (int)((width - (scaledGradientSize + 5 + scaledHueSliderWidth + 10 + 15 + scaledSliderWidth + VALUE_TEXT_WIDTH)) / 2);
        offsetX = Math.max(5, offsetX); // Minimum padding
        
        int gradientX = x + offsetX;
        int gradientY = y + 5;
        int hueSliderX = gradientX + scaledGradientSize + 5;
        int hueSliderY = gradientY;
        
        // Render gradient (saturation/brightness) - use scaled size
        for (int py = 0; py < scaledGradientSize; py++) {
            for (int px = 0; px < scaledGradientSize; px++) {
                float s = px / (float) scaledGradientSize;
                float b = 1.0f - (py / (float) scaledGradientSize);
                int color = hsbToRgb(hue, s, b);
                fill(poseStack, gradientX + px, gradientY + py, gradientX + px + 1, gradientY + py + 1, 0xFF000000 | color);
            }
        }
        
        // Render gradient border
        fill(poseStack, gradientX - 1, gradientY - 1, gradientX + scaledGradientSize + 1, gradientY, 0xFF000000);
        fill(poseStack, gradientX - 1, gradientY + scaledGradientSize, gradientX + scaledGradientSize + 1, gradientY + scaledGradientSize + 1, 0xFF000000);
        fill(poseStack, gradientX - 1, gradientY - 1, gradientX, gradientY + scaledGradientSize + 1, 0xFF000000);
        fill(poseStack, gradientX + scaledGradientSize, gradientY - 1, gradientX + scaledGradientSize + 1, gradientY + scaledGradientSize + 1, 0xFF000000);
        
        // Render hue slider (rainbow) - use scaled size
        for (int py = 0; py < scaledHueSliderHeight; py++) {
            float h = (py / (float) scaledHueSliderHeight) * 360.0f;
            int color = hsbToRgb(h, 1.0f, 1.0f);
            fill(poseStack, hueSliderX, hueSliderY + py, hueSliderX + scaledHueSliderWidth, hueSliderY + py + 1, 0xFF000000 | color);
        }
        
        // Render hue slider border
        fill(poseStack, hueSliderX - 1, hueSliderY - 1, hueSliderX + scaledHueSliderWidth + 1, hueSliderY, 0xFF000000);
        fill(poseStack, hueSliderX - 1, hueSliderY + scaledHueSliderHeight, hueSliderX + scaledHueSliderWidth + 1, hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);
        fill(poseStack, hueSliderX - 1, hueSliderY - 1, hueSliderX, hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);
        fill(poseStack, hueSliderX + scaledHueSliderWidth, hueSliderY - 1, hueSliderX + scaledHueSliderWidth + 1, hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);
        
        // Render indicator on gradient
        int indicatorX = gradientX + (int) (saturation * scaledGradientSize);
        int indicatorY = gradientY + (int) ((1.0f - brightness) * scaledGradientSize);
        fill(poseStack, indicatorX - 2, indicatorY - 2, indicatorX + 2, indicatorY + 2, 0xFFFFFFFF);
        fill(poseStack, indicatorX - 1, indicatorY - 1, indicatorX + 1, indicatorY + 1, 0xFF000000);
        
        // Render indicator on hue slider
        int hueIndicatorY = hueSliderY + (int) ((hue / 360.0f) * scaledHueSliderHeight);
        fill(poseStack, hueSliderX - 3, hueIndicatorY - 2, hueSliderX, hueIndicatorY + 2, 0xFFFFFFFF);
        fill(poseStack, hueSliderX + scaledHueSliderWidth, hueIndicatorY - 2, hueSliderX + scaledHueSliderWidth + 3, hueIndicatorY + 2, 0xFFFFFFFF);
        
        // Color preview display (below gradient)
        int previewY = gradientY + scaledGradientSize + 5;
        int previewX = gradientX;
        int previewWidth = scaledGradientSize + scaledHueSliderWidth + 5;
        fill(poseStack, previewX, previewY, previewX + previewWidth, previewY + 20, 0xFF000000 | currentColor);
        fill(poseStack, previewX - 1, previewY - 1, previewX + previewWidth + 1, previewY, 0xFF000000);
        fill(poseStack, previewX - 1, previewY + 20, previewX + previewWidth + 1, previewY + 21, 0xFF000000);
        fill(poseStack, previewX - 1, previewY - 1, previewX, previewY + 21, 0xFF000000);
        fill(poseStack, previewX + previewWidth, previewY - 1, previewX + previewWidth + 1, previewY + 21, 0xFF000000);
        
        // RGB Sliders (to the right of gradient/hue) - use scaled spacing
        int rgbStartX = hueSliderX + scaledHueSliderWidth + 10;
        int rgbStartY = gradientY;
        renderSliderScaled(poseStack, mc, rgbStartX, rgbStartY, "R", r, 0, 255, 0xFF0000, draggingR, scale, false, mouseX, mouseY, partialTick);
        renderSliderScaled(poseStack, mc, rgbStartX, rgbStartY + scaledSliderSpacing, "G", g, 0, 255, 0x00FF00, draggingG, scale, false, mouseX, mouseY, partialTick);
        renderSliderScaled(poseStack, mc, rgbStartX, rgbStartY + scaledSliderSpacing * 2, "B", b, 0, 255, 0x0000FF, draggingB, scale, false, mouseX, mouseY, partialTick);
        
        // HSB Sliders (below RGB) - use scaled spacing
        int hsbStartY = rgbStartY + scaledSliderSpacing * 3 + 5;
        renderSliderScaled(poseStack, mc, rgbStartX, hsbStartY, "H", (int)hue, 0, 360, -1, draggingH, scale, true, mouseX, mouseY, partialTick); // -1 for rainbow gradient
        renderSliderScaled(poseStack, mc, rgbStartX, hsbStartY + scaledSliderSpacing, "S", (int)(saturation * 100), 0, 100, -1, draggingS, scale, true, mouseX, mouseY, partialTick);
        renderSliderScaled(poseStack, mc, rgbStartX, hsbStartY + scaledSliderSpacing * 2, "B", (int)(brightness * 100), 0, 100, -1, draggingBrightness, scale, true, mouseX, mouseY, partialTick);
        
        // Render disabled overlay if disabled
        if (!enabled) {
            fill(poseStack, x, y, x + width, y + height, 0x80000000);
        }
    }
    
    private void renderSliderScaled(PoseStack poseStack, Minecraft mc, int x, int y, String label, int value, int min, int max, int gradientColor, boolean isDragging, float scale, boolean isHsb, int mouseX, int mouseY, float partialTick) {
        // Use scaled dimensions
        int scaledSliderWidth = (int)(SLIDER_WIDTH * scale);
        int scaledSliderHeight = (int)(SLIDER_HEIGHT * scale);
        
        // Render label - scale text if needed
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
        
        // Render gradient bar
        if (gradientColor == -1) {
            // Rainbow gradient for H
            if (label.equals("H")) {
                for (int px = 0; px < scaledSliderWidth; px++) {
                    float h = (px / (float) scaledSliderWidth) * 360.0f;
                    int color = hsbToRgb(h, 1.0f, 1.0f);
                    fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + scaledSliderHeight, 0xFF000000 | color);
                }
            } else {
                // Gray to color gradient for S and B
                int baseColor = label.equals("S") ? hsbToRgb(hue, 1.0f, brightness) : hsbToRgb(hue, saturation, 1.0f);
                for (int px = 0; px < scaledSliderWidth; px++) {
                    float ratio = px / (float) scaledSliderWidth;
                    int r = (baseColor >> 16) & 0xFF;
                    int g = (baseColor >> 8) & 0xFF;
                    int bl = baseColor & 0xFF;
                    if (label.equals("S")) {
                        // Saturation: gray to full color
                        r = (int)(128 + (r - 128) * ratio);
                        g = (int)(128 + (g - 128) * ratio);
                        bl = (int)(128 + (bl - 128) * ratio);
                    } else {
                        // Brightness: black to full color
                        r = (int)(r * ratio);
                        g = (int)(g * ratio);
                        bl = (int)(bl * ratio);
                    }
                    int color = (r << 16) | (g << 8) | bl;
                    fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + scaledSliderHeight, 0xFF000000 | color);
                }
            }
        } else {
            // RGB gradient
            for (int px = 0; px < scaledSliderWidth; px++) {
                float ratio = px / (float) scaledSliderWidth;
                int color = 0;
                if (label.equals("R")) {
                    color = ((int)(ratio * 255) << 16) | ((g << 8) | b);
                } else if (label.equals("G")) {
                    color = (r << 16) | ((int)(ratio * 255) << 8) | b;
                } else {
                    color = (r << 16) | (g << 8) | (int)(ratio * 255);
                }
                fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + scaledSliderHeight, 0xFF000000 | color);
            }
        }
        
        // Render slider border
        fill(poseStack, sliderX - 1, sliderY - 1, sliderX + scaledSliderWidth + 1, sliderY, 0xFF000000);
        fill(poseStack, sliderX - 1, sliderY + scaledSliderHeight, sliderX + scaledSliderWidth + 1, sliderY + scaledSliderHeight + 1, 0xFF000000);
        fill(poseStack, sliderX - 1, sliderY - 1, sliderX, sliderY + scaledSliderHeight + 1, 0xFF000000);
        fill(poseStack, sliderX + scaledSliderWidth, sliderY - 1, sliderX + scaledSliderWidth + 1, sliderY + scaledSliderHeight + 1, 0xFF000000);
        
        // Render indicator
        float ratio = (value - min) / (float) (max - min);
        int indicatorX = sliderX + (int)(ratio * scaledSliderWidth);
        fill(poseStack, indicatorX - 1, sliderY - 2, indicatorX + 1, sliderY + scaledSliderHeight + 2, 0xFFFFFFFF);
        fill(poseStack, indicatorX, sliderY - 1, indicatorX, sliderY + scaledSliderHeight + 1, 0xFF000000);
        
        // Position and render editable field instead of text
        int fieldX = sliderX + scaledSliderWidth + 3; // Reduced spacing
        int fieldY = sliderY;
        int scaledFieldWidth = (int)(VALUE_TEXT_WIDTH * scale);
        int scaledFieldHeight = (int)(SLIDER_HEIGHT * scale);
        
        EditBox field = null;
        if (label.equals("R")) field = rField;
        else if (label.equals("G")) field = gField;
        else if (label.equals("B") && !isHsb) field = bField; // RGB B
        else if (label.equals("H")) field = hField;
        else if (label.equals("S")) field = sField;
        else if (label.equals("B") && isHsb) field = brightnessField; // HSB B
        
        if (field != null) {
            // Ensure field doesn't go outside widget bounds
            int maxFieldX = x + width - 5; // Leave 5px padding from right edge
            int actualFieldX = Math.min(fieldX, maxFieldX - Math.max(30, scaledFieldWidth));
            field.x = actualFieldX;
            field.y = fieldY;
            field.setWidth(Math.max(30, Math.min(scaledFieldWidth, maxFieldX - actualFieldX)));
            field.setHeight(scaledFieldHeight);
            field.visible = visible && enabled;
            field.setEditable(enabled); // Make sure field is editable when picker is enabled
            field.render(poseStack, (int)mouseX, (int)mouseY, partialTick);
        }
    }
    
    private void renderSlider(PoseStack poseStack, Minecraft mc, int x, int y, String label, int value, int min, int max, int gradientColor, boolean isDragging) {
        // Render label
        mc.font.draw(poseStack, label, x, y + 2, 0xFFFFFF);
        
        int sliderX = x + 15;
        int sliderY = y;
        
        // Render gradient bar
        if (gradientColor == -1) {
            // Rainbow gradient for H
            if (label.equals("H")) {
                for (int px = 0; px < SLIDER_WIDTH; px++) {
                    float h = (px / (float) SLIDER_WIDTH) * 360.0f;
                    int color = hsbToRgb(h, 1.0f, 1.0f);
                    fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + SLIDER_HEIGHT, 0xFF000000 | color);
                }
            } else {
                // Gray to color gradient for S and B
                int baseColor = label.equals("S") ? hsbToRgb(hue, 1.0f, brightness) : hsbToRgb(hue, saturation, 1.0f);
                for (int px = 0; px < SLIDER_WIDTH; px++) {
                    float ratio = px / (float) SLIDER_WIDTH;
                    int r = (baseColor >> 16) & 0xFF;
                    int g = (baseColor >> 8) & 0xFF;
                    int bl = baseColor & 0xFF;
                    if (label.equals("S")) {
                        // Saturation: gray to full color
                        r = (int)(128 + (r - 128) * ratio);
                        g = (int)(128 + (g - 128) * ratio);
                        bl = (int)(128 + (bl - 128) * ratio);
                    } else {
                        // Brightness: black to full color
                        r = (int)(r * ratio);
                        g = (int)(g * ratio);
                        bl = (int)(bl * ratio);
                    }
                    int color = (r << 16) | (g << 8) | bl;
                    fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + SLIDER_HEIGHT, 0xFF000000 | color);
                }
            }
        } else {
            // RGB gradient
            for (int px = 0; px < SLIDER_WIDTH; px++) {
                float ratio = px / (float) SLIDER_WIDTH;
                int color = 0;
                if (label.equals("R")) {
                    color = ((int)(ratio * 255) << 16) | ((g << 8) | b);
                } else if (label.equals("G")) {
                    color = (r << 16) | ((int)(ratio * 255) << 8) | b;
                } else {
                    color = (r << 16) | (g << 8) | (int)(ratio * 255);
                }
                fill(poseStack, sliderX + px, sliderY, sliderX + px + 1, sliderY + SLIDER_HEIGHT, 0xFF000000 | color);
            }
        }
        
        // Render slider border
        fill(poseStack, sliderX - 1, sliderY - 1, sliderX + SLIDER_WIDTH + 1, sliderY, 0xFF000000);
        fill(poseStack, sliderX - 1, sliderY + SLIDER_HEIGHT, sliderX + SLIDER_WIDTH + 1, sliderY + SLIDER_HEIGHT + 1, 0xFF000000);
        fill(poseStack, sliderX - 1, sliderY - 1, sliderX, sliderY + SLIDER_HEIGHT + 1, 0xFF000000);
        fill(poseStack, sliderX + SLIDER_WIDTH, sliderY - 1, sliderX + SLIDER_WIDTH + 1, sliderY + SLIDER_HEIGHT + 1, 0xFF000000);
        
        // Render indicator
        float ratio = (value - min) / (float) (max - min);
        int indicatorX = sliderX + (int)(ratio * SLIDER_WIDTH);
        fill(poseStack, indicatorX - 1, sliderY - 2, indicatorX + 1, sliderY + SLIDER_HEIGHT + 2, 0xFFFFFFFF);
        fill(poseStack, indicatorX, sliderY - 1, indicatorX, sliderY + SLIDER_HEIGHT + 1, 0xFF000000);
        
        // Render value text - format as integer for RGB, one decimal for HSB
        String valueText;
        if (label.equals("R") || label.equals("G") || label.equals("B")) {
            valueText = String.valueOf(value); // Integer for RGB
        } else {
            valueText = String.format("%.1f", (float)value); // One decimal for HSB
        }
        int textX = sliderX + SLIDER_WIDTH + 3; // Reduced spacing
        // Ensure text doesn't overflow - truncate if needed
        int maxTextWidth = VALUE_TEXT_WIDTH - 3;
        int textWidth = mc.font.width(valueText);
        if (textWidth > maxTextWidth) {
            // Use smaller font or truncate
            valueText = String.valueOf(value); // Fallback to integer
        }
        mc.font.draw(poseStack, valueText, textX, sliderY + 2, 0xFFFFFF);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // First check editable fields - they need to handle clicks even if widget is disabled
        if (rField != null && rField.mouseClicked(mouseX, mouseY, button)) return true;
        if (gField != null && gField.mouseClicked(mouseX, mouseY, button)) return true;
        if (bField != null && bField.mouseClicked(mouseX, mouseY, button)) return true;
        if (hField != null && hField.mouseClicked(mouseX, mouseY, button)) return true;
        if (sField != null && sField.mouseClicked(mouseX, mouseY, button)) return true;
        if (brightnessField != null && brightnessField.mouseClicked(mouseX, mouseY, button)) return true;
        
        if (!enabled || button != 0) { // Only respond to left mouse button
            clearAllDragging();
            return false;
        }
        
        // First check if mouse is within widget bounds at all
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return false;
        }
        
        // Calculate scale and positions (same as render)
        // Set minimum scale to 0.75 so it doesn't become too small
        float scaleX = Math.min(1.0f, Math.max(0.75f, (width - 20) / 250.0f)); // Account for padding, min 0.75
        float scaleY = Math.min(1.0f, Math.max(0.75f, (height - 20) / 220.0f)); // Min 0.75
        float scale = Math.min(scaleX, scaleY); // Use smaller scale to maintain aspect ratio
        
        int scaledGradientSize = (int)(GRADIENT_SIZE * scale);
        int scaledHueSliderWidth = (int)(HUE_SLIDER_WIDTH * scale);
        int scaledHueSliderHeight = (int)(HUE_SLIDER_HEIGHT * scale);
        int scaledSliderWidth = (int)(SLIDER_WIDTH * scale);
        int scaledSliderSpacing = (int)(SLIDER_SPACING * scale);
        
        int offsetX = (int)((width - (scaledGradientSize + 5 + scaledHueSliderWidth + 10 + 15 + scaledSliderWidth + VALUE_TEXT_WIDTH)) / 2);
        offsetX = Math.max(5, offsetX);
        
        int gradientX = x + offsetX;
        int gradientY = y + 5;
        int hueSliderX = gradientX + scaledGradientSize + 5;
        int hueSliderY = gradientY;
        int rgbStartX = hueSliderX + scaledHueSliderWidth + 10;
        int rgbStartY = gradientY;
        int hsbStartY = rgbStartY + scaledSliderSpacing * 3 + 5;
        
        // Check gradient - allow clicking even slightly outside for better UX
        if (mouseX >= gradientX - 2 && mouseX < gradientX + scaledGradientSize + 2 &&
            mouseY >= gradientY - 2 && mouseY < gradientY + scaledGradientSize + 2) {
            // Start dragging - don't update color yet, wait for drag
            draggingGradient = true;
            clearOtherDragging();
            // Update color on initial click
            double clampedX = Math.max(gradientX, Math.min(gradientX + scaledGradientSize - 1, mouseX));
            double clampedY = Math.max(gradientY, Math.min(gradientY + scaledGradientSize - 1, mouseY));
            saturation = (float) Math.max(0, Math.min(1, (clampedX - gradientX) / (double) scaledGradientSize));
            brightness = (float) Math.max(0, Math.min(1, 1.0 - (clampedY - gradientY) / (double) scaledGradientSize));
            updateColorFromHsb();
            return true;
        }
        
        // Check hue slider - allow clicking even slightly outside for better UX
        if (mouseX >= hueSliderX - 2 && mouseX < hueSliderX + scaledHueSliderWidth + 2 &&
            mouseY >= hueSliderY - 2 && mouseY < hueSliderY + scaledHueSliderHeight + 2) {
            // Clear other dragging flags FIRST, then set this one
            draggingGradient = false;
            draggingR = draggingG = draggingB = false;
            draggingH = draggingS = draggingBrightness = false;
            // Now set this dragging flag
            draggingHue = true; // Use draggingHue for vertical hue slider
            // Update color on initial click
            double clampedY = Math.max(hueSliderY, Math.min(hueSliderY + scaledHueSliderHeight - 1, mouseY));
            hue = (float) Math.max(0, Math.min(360, ((clampedY - hueSliderY) / (double) scaledHueSliderHeight) * 360.0));
            updateColorFromHsb();
            return true;
        }
        
        // Check RGB sliders - allow clicking slightly outside for better UX
        int sliderX = rgbStartX + 15;
        if (mouseX >= sliderX - 2 && mouseX < sliderX + scaledSliderWidth + 2) {
            // Clamp X to slider bounds
            double clampedX = Math.max(sliderX, Math.min(sliderX + scaledSliderWidth - 1, mouseX));
            float ratio = (float) Math.max(0, Math.min(1, (clampedX - sliderX) / (double) scaledSliderWidth));
            
            if (mouseY >= rgbStartY - 2 && mouseY < rgbStartY + scaledSliderSpacing + 2) {
                // Clear other dragging flags FIRST, then set this one
                draggingGradient = false;
                draggingHue = false;
                draggingG = draggingB = false;
                draggingH = draggingS = draggingBrightness = false;
                // Now set this dragging flag
                draggingR = true;
                // Update color on initial click
                r = (int)(ratio * 255);
                updateColorFromRgb();
                return true;
            } else if (mouseY >= rgbStartY + scaledSliderSpacing - 2 && mouseY < rgbStartY + scaledSliderSpacing * 2 + 2) {
                // Clear other dragging flags FIRST, then set this one
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingB = false;
                draggingH = draggingS = draggingBrightness = false;
                // Now set this dragging flag
                draggingG = true;
                // Update color on initial click
                g = (int)(ratio * 255);
                updateColorFromRgb();
                return true;
            } else if (mouseY >= rgbStartY + scaledSliderSpacing * 2 - 2 && mouseY < rgbStartY + scaledSliderSpacing * 3 + 2) {
                // Clear other dragging flags FIRST, then set this one
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = false;
                draggingH = draggingS = draggingBrightness = false;
                // Now set this dragging flag
                draggingB = true;
                // Update color on initial click
                b = (int)(ratio * 255);
                updateColorFromRgb();
                return true;
            }
        }
        
        // Check HSB sliders - allow clicking slightly outside for better UX
        if (mouseX >= sliderX - 2 && mouseX < sliderX + scaledSliderWidth + 2) {
            // Clamp X to slider bounds
            double clampedX = Math.max(sliderX, Math.min(sliderX + scaledSliderWidth - 1, mouseX));
            float ratio = (float) Math.max(0, Math.min(1, (clampedX - sliderX) / (double) scaledSliderWidth));
            
            if (mouseY >= hsbStartY - 2 && mouseY < hsbStartY + scaledSliderSpacing + 2) {
                // Clear other dragging flags FIRST, then set this one
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = draggingB = false;
                draggingS = draggingBrightness = false;
                // Now set this dragging flag
                draggingH = true;
                // Update color on initial click
                hue = ratio * 360.0f;
                updateColorFromHsb();
                return true;
            } else if (mouseY >= hsbStartY + scaledSliderSpacing - 2 && mouseY < hsbStartY + scaledSliderSpacing * 2 + 2) {
                // Clear other dragging flags FIRST, then set this one
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = draggingB = false;
                draggingH = draggingBrightness = false;
                // Now set this dragging flag
                draggingS = true;
                // Update color on initial click
                saturation = ratio;
                updateColorFromHsb();
                return true;
            } else if (mouseY >= hsbStartY + scaledSliderSpacing * 2 - 2 && mouseY < hsbStartY + scaledSliderSpacing * 3 + 2) {
                // Clear other dragging flags FIRST, then set this one
                draggingGradient = false;
                draggingHue = false;
                draggingR = draggingG = draggingB = false;
                draggingH = draggingS = false;
                // Now set this dragging flag
                draggingBrightness = true;
                // Update color on initial click
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
        // CRITICAL: Check if we're dragging FIRST - if we are, continue dragging regardless of enabled state
        // This ensures dragging continues even if something tries to disable the widget
        boolean isDragging = draggingGradient || draggingHue || draggingR || draggingG || draggingB || 
                            draggingH || draggingS || draggingBrightness;
        
        if (!enabled && !isDragging) {
            clearAllDragging();
            return false;
        }
        
        // If we're not dragging anything, return false immediately
        if (!isDragging) {
            return false;
        }
        
        // If we're already dragging, continue dragging regardless of button or enabled state
        // This allows dragging to continue smoothly even if button state changes
        
        // Calculate scale and positions (same as render and mouseClicked)
        // Set minimum scale to 0.75 so it doesn't become too small
        float scaleX = Math.min(1.0f, Math.max(0.75f, (width - 20) / 250.0f)); // Account for padding, min 0.75
        float scaleY = Math.min(1.0f, Math.max(0.75f, (height - 20) / 220.0f)); // Min 0.75
        float scale = Math.min(scaleX, scaleY); // Use smaller scale to maintain aspect ratio
        
        int scaledGradientSize = (int)(GRADIENT_SIZE * scale);
        int scaledHueSliderWidth = (int)(HUE_SLIDER_WIDTH * scale);
        int scaledHueSliderHeight = (int)(HUE_SLIDER_HEIGHT * scale);
        int scaledSliderWidth = (int)(SLIDER_WIDTH * scale);
        int scaledSliderSpacing = (int)(SLIDER_SPACING * scale);
        
        int offsetX = (int)((width - (scaledGradientSize + 5 + scaledHueSliderWidth + 10 + 15 + scaledSliderWidth + VALUE_TEXT_WIDTH)) / 2);
        offsetX = Math.max(5, offsetX);
        
        int gradientX = x + offsetX;
        int gradientY = y + 5;
        int hueSliderX = gradientX + scaledGradientSize + 5;
        int hueSliderY = gradientY;
        int rgbStartX = hueSliderX + scaledHueSliderWidth + 10;
        int sliderX = rgbStartX + 15;
        
        // Allow dragging even when mouse is outside widget bounds - clamp to bounds
        if (draggingGradient) {
            // Clamp mouse position to gradient bounds for smooth dragging
            double clampedX = Math.max(gradientX, Math.min(gradientX + scaledGradientSize - 1, mouseX));
            double clampedY = Math.max(gradientY, Math.min(gradientY + scaledGradientSize - 1, mouseY));
            saturation = (float) Math.max(0, Math.min(1, (clampedX - gradientX) / (double) scaledGradientSize));
            brightness = (float) Math.max(0, Math.min(1, 1.0 - (clampedY - gradientY) / (double) scaledGradientSize));
            updateColorFromHsb();
            return true;
        }
        
        if (draggingHue) {
            // Clamp mouse position to hue slider bounds for smooth dragging
            double clampedY = Math.max(hueSliderY, Math.min(hueSliderY + scaledHueSliderHeight - 1, mouseY));
            hue = (float) Math.max(0, Math.min(360, ((clampedY - hueSliderY) / (double) scaledHueSliderHeight) * 360.0));
            updateColorFromHsb();
            return true;
        }
        
        if (draggingR || draggingG || draggingB || draggingH || draggingS || draggingBrightness) {
            // Clamp mouse position to slider bounds for smooth dragging
            double clampedX = Math.max(sliderX, Math.min(sliderX + scaledSliderWidth - 1, mouseX));
            float ratio = (float) Math.max(0, Math.min(1, (clampedX - sliderX) / (double) scaledSliderWidth));
            
            if (draggingR) {
                r = (int)(ratio * 255);
                updateColorFromRgb();
                return true;
            }
            
            if (draggingG) {
                g = (int)(ratio * 255);
                updateColorFromRgb();
                return true;
            }
            
            if (draggingB) {
                b = (int)(ratio * 255);
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
        
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Only clear dragging if we were actually dragging something
        boolean wasDragging = draggingGradient || draggingHue || draggingR || draggingG || draggingB || 
                             draggingH || draggingS || draggingBrightness;
        clearAllDragging();
        return wasDragging; // Return true if we were dragging, so parent knows to handle it
    }
    
    private void clearAllDragging() {
        // Only clear if we're not actively being dragged (defensive check)
        // This prevents accidental clearing during drag operations
        draggingGradient = false;
        draggingHue = false;
        draggingR = draggingG = draggingB = false;
        draggingH = draggingS = draggingBrightness = false;
    }
    
    private void clearOtherDragging() {
        // Clear all OTHER dragging flags, but keep the current one
        // This is used when starting a new drag to ensure only one thing is dragged at a time
        // IMPORTANT: Only clear flags that are NOT currently set
        if (!draggingGradient) draggingGradient = false;
        if (!draggingHue) draggingHue = false;
        if (!draggingR) draggingR = false;
        if (!draggingG) draggingG = false;
        if (!draggingB) draggingB = false;
        if (!draggingH) draggingH = false;
        if (!draggingS) draggingS = false;
        if (!draggingBrightness) draggingBrightness = false;
    }
    
    private void updateColorFromHsb() {
        currentColor = hsbToRgb(hue, saturation, brightness);
        updateRgbFromColor();
        updateFieldValues(); // Update all fields when color changes
        if (onColorChanged != null) {
            String hexColor = String.format("#%06X", currentColor);
            onColorChanged.accept(hexColor);
        }
    }
    
    private void updateColorFromRgb() {
        currentColor = (r << 16) | (g << 8) | b;
        rgbToHsb(currentColor);
        updateFieldValues(); // Update all fields when color changes
        if (onColorChanged != null) {
            String hexColor = String.format("#%06X", currentColor);
            onColorChanged.accept(hexColor);
        }
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // No narration needed
    }
}

