package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.function.Consumer;

public class ColorPickerWidget implements ICustomWidget {
    private static final int GRADIENT_SIZE = 80;
    private static final int HUE_SLIDER_WIDTH = 20;
    private static final int HUE_SLIDER_HEIGHT = 80;
    private static final int SLIDER_HEIGHT = 12;
    private static final int SLIDER_WIDTH = 80;
    private static final int SLIDER_SPACING = 18;
    private static final int TEXT_FIELD_HEIGHT = 20;
    private static final int VALUE_TEXT_WIDTH = 40;
    private static final int HUE_TEXTURE_Y = GRADIENT_SIZE;
    private static final int SLIDER_TEXTURE_Y = HUE_TEXTURE_Y + HUE_SLIDER_HEIGHT;
    private static final int TEXTURE_HEIGHT = SLIDER_TEXTURE_Y + 6;

    private static DynamicTexture gradientTexture;
    private static CommonId gradientTextureId;
    private static float cachedHue = Float.NaN;
    private static float cachedSaturation = Float.NaN;
    private static float cachedBrightness = Float.NaN;
    private static int cachedR = -1;
    private static int cachedG = -1;
    private static int cachedB = -1;

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean active = true;
    private boolean focused = false;

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
    private boolean updatingFields = false;

    public ColorPickerWidget(int x, int y, int width, int height, int initialColor, Consumer<String> onColorChanged) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentColor = initialColor;
        this.onColorChanged = onColorChanged;
        rgbToHsb(initialColor);
        updateRgbFromColor();
        createValueFields();
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

        rField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, ComponentHelper.literal(""));
        rField.setMaxLength(3);
        Services.PLATFORM.setEditBoxFilter(rField, s -> s.matches("[0-9]*"));
        rField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    int newR = Integer.parseInt(value);
                    if (newR >= 0 && newR <= 255) {
                        r = newR;
                        updateColorFromRgb();
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        gField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, ComponentHelper.literal(""));
        gField.setMaxLength(3);
        Services.PLATFORM.setEditBoxFilter(gField, s -> s.matches("[0-9]*"));
        gField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    int newG = Integer.parseInt(value);
                    if (newG >= 0 && newG <= 255) {
                        g = newG;
                        updateColorFromRgb();
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        bField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, ComponentHelper.literal(""));
        bField.setMaxLength(3);
        Services.PLATFORM.setEditBoxFilter(bField, s -> s.matches("[0-9]*"));
        bField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    int newB = Integer.parseInt(value);
                    if (newB >= 0 && newB <= 255) {
                        b = newB;
                        updateColorFromRgb();
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        hField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, ComponentHelper.literal(""));
        hField.setMaxLength(6);
        Services.PLATFORM.setEditBoxFilter(hField, s -> s.matches("[0-9.]*"));
        hField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    float newH = Float.parseFloat(value);
                    if (newH >= 0 && newH <= 360) {
                        hue = newH;
                        updateColorFromHsb();
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        sField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, ComponentHelper.literal(""));
        sField.setMaxLength(5);
        Services.PLATFORM.setEditBoxFilter(sField, s -> s.matches("[0-9.]*"));
        sField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    float newS = Float.parseFloat(value);
                    if (newS >= 0 && newS <= 100) {
                        saturation = newS / 100.0f;
                        updateColorFromHsb();
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        brightnessField = new EditBox(mc.font, 0, 0, fieldWidth, fieldHeight, ComponentHelper.literal(""));
        brightnessField.setMaxLength(5);
        Services.PLATFORM.setEditBoxFilter(brightnessField, s -> s.matches("[0-9.]*"));
        brightnessField.setResponder(value -> {
            if (!updatingFields && !value.isEmpty()) {
                try {
                    float newB = Float.parseFloat(value);
                    if (newB >= 0 && newB <= 100) {
                        brightness = newB / 100.0f;
                        updateColorFromHsb();
                    }
                } catch (NumberFormatException ignored) {
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

    public boolean isDragging() {
        return draggingHue || draggingGradient || draggingR || draggingG || draggingB || draggingH || draggingS || draggingBrightness;
    }

    private void updateRgbFromColor() {
        r = (currentColor >> 16) & 0xFF;
        g = (currentColor >> 8) & 0xFF;
        b = currentColor & 0xFF;
    }

    private void rgbToHsb(int rgb) {
        int rVal = (rgb >> 16) & 0xFF;
        int gVal = (rgb >> 8) & 0xFF;
        int bVal = rgb & 0xFF;

        float[] hsb = java.awt.Color.RGBtoHSB(rVal, gVal, bVal, null);
        this.hue = hsb[0] * 360.0f;
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    private int hsbToRgb(float h, float s, float bVal) {
        java.awt.Color color = java.awt.Color.getHSBColor(h / 360.0f, s, bVal);
        return (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        float totalUnscaledWidth = GRADIENT_SIZE + 5 + HUE_SLIDER_WIDTH + 10 + 15 + SLIDER_WIDTH + VALUE_TEXT_WIDTH + 15;
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
        int scaledGap1 = (int) (5 * scale);
        int scaledGap2 = (int) (10 * scale);
        int scaledPadding = (int) (5 * scale);

        int gradientX = x + scaledPadding;
        int gradientY = y + scaledPadding;
        int hueSliderX = gradientX + scaledGradientSize + scaledGap1;
        int hueSliderY = gradientY;

        ensureGradientTexture();
        blitGradient(poseStackOrGraphics, gradientX, gradientY, scaledGradientSize, scaledGradientSize,
                0, 0, GRADIENT_SIZE, GRADIENT_SIZE);

        Services.PLATFORM.fill(poseStackOrGraphics, gradientX - 1, gradientY - 1, gradientX + scaledGradientSize + 1, gradientY, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, gradientX - 1, gradientY + scaledGradientSize, gradientX + scaledGradientSize + 1,
                gradientY + scaledGradientSize + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, gradientX - 1, gradientY - 1, gradientX, gradientY + scaledGradientSize + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, gradientX + scaledGradientSize, gradientY - 1, gradientX + scaledGradientSize + 1,
                gradientY + scaledGradientSize + 1, 0xFF000000);

        blitGradient(poseStackOrGraphics, hueSliderX, hueSliderY, scaledHueSliderWidth, scaledHueSliderHeight,
                0, HUE_TEXTURE_Y, 1, HUE_SLIDER_HEIGHT);

        Services.PLATFORM.fill(poseStackOrGraphics, hueSliderX - 1, hueSliderY - 1, hueSliderX + scaledHueSliderWidth + 1, hueSliderY, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, hueSliderX - 1, hueSliderY + scaledHueSliderHeight, hueSliderX + scaledHueSliderWidth + 1,
                hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, hueSliderX - 1, hueSliderY - 1, hueSliderX, hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, hueSliderX + scaledHueSliderWidth, hueSliderY - 1, hueSliderX + scaledHueSliderWidth + 1,
                hueSliderY + scaledHueSliderHeight + 1, 0xFF000000);

        int indicatorX = gradientX + (int) (saturation * scaledGradientSize);
        int indicatorY = gradientY + (int) ((1.0f - brightness) * scaledGradientSize);
        Services.PLATFORM.fill(poseStackOrGraphics, indicatorX - 2, indicatorY - 2, indicatorX + 2, indicatorY + 2, 0xFFFFFFFF);
        Services.PLATFORM.fill(poseStackOrGraphics, indicatorX - 1, indicatorY - 1, indicatorX + 1, indicatorY + 1, 0xFF000000);

        int hueIndicatorY = hueSliderY + (int) ((hue / 360.0f) * scaledHueSliderHeight);
        Services.PLATFORM.fill(poseStackOrGraphics, hueSliderX - 3, hueIndicatorY - 2, hueSliderX, hueIndicatorY + 2, 0xFFFFFFFF);
        Services.PLATFORM.fill(poseStackOrGraphics, hueSliderX + scaledHueSliderWidth, hueIndicatorY - 2, hueSliderX + scaledHueSliderWidth + 3,
                hueIndicatorY + 2, 0xFFFFFFFF);

        int previewY = gradientY + scaledGradientSize + scaledGap1;
        int previewX = gradientX;
        int previewHeight = (int) (20 * scale);
        int previewWidth = scaledGradientSize + scaledHueSliderWidth + scaledGap1;
        Services.PLATFORM.fill(poseStackOrGraphics, previewX, previewY, previewX + previewWidth, previewY + previewHeight,
                0xFF000000 | currentColor);
        Services.PLATFORM.fill(poseStackOrGraphics, previewX - 1, previewY - 1, previewX + previewWidth + 1, previewY, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, previewX - 1, previewY + previewHeight, previewX + previewWidth + 1,
                previewY + previewHeight + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, previewX - 1, previewY - 1, previewX, previewY + previewHeight + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, previewX + previewWidth, previewY - 1, previewX + previewWidth + 1,
                previewY + previewHeight + 1, 0xFF000000);

        int rgbStartX = hueSliderX + scaledHueSliderWidth + scaledGap2;
        int rgbStartY = gradientY;
        renderSliderScaled(poseStackOrGraphics, mc, rgbStartX, rgbStartY, "R", r, 0, 255, 0, draggingR, scale, false,
                mouseX, mouseY, partialTick);
        renderSliderScaled(poseStackOrGraphics, mc, rgbStartX, rgbStartY + scaledSliderSpacing, "G", g, 0, 255, 1,
                draggingG, scale, false, mouseX, mouseY, partialTick);
        renderSliderScaled(poseStackOrGraphics, mc, rgbStartX, rgbStartY + scaledSliderSpacing * 2, "B", b, 0, 255, 2,
                draggingB, scale, false, mouseX, mouseY, partialTick);

        int hsbStartY = rgbStartY + scaledSliderSpacing * 3 + (int) (5 * scale);
        renderSliderScaled(poseStackOrGraphics, mc, rgbStartX, hsbStartY, "H", (int) hue, 0, 360, 3, draggingH, scale, true,
                mouseX, mouseY, partialTick);
        renderSliderScaled(poseStackOrGraphics, mc, rgbStartX, hsbStartY + scaledSliderSpacing, "S", (int) (saturation * 100), 0,
                100, 4, draggingS, scale, true, mouseX, mouseY, partialTick);
        renderSliderScaled(poseStackOrGraphics, mc, rgbStartX, hsbStartY + scaledSliderSpacing * 2, "B", (int) (brightness * 100),
                0, 100, 5, draggingBrightness, scale, true, mouseX, mouseY, partialTick);

        if (!enabled) {
            Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + height, 0x80000000);
        }
    }

    private void renderSliderScaled(Object poseStackOrGraphics, Minecraft mc, int sliderOriginX, int sliderOriginY, String label, int value, int min,
            int max, int textureRow, boolean isDragging, float scale, boolean isHsb, int mouseX, int mouseY,
            float partialTick) {
        int scaledSliderWidth = (int) (SLIDER_WIDTH * scale);
        int scaledSliderHeight = (int) (SLIDER_HEIGHT * scale);

        if (scale < 1.0f) {
            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.translateGuiPose(poseStackOrGraphics, sliderOriginX, sliderOriginY + 2, 0);
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, label, 0, 0, 0xFFFFFF);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);
        } else {
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, label, sliderOriginX, sliderOriginY + 2, 0xFFFFFF);
        }

        int labelW = (int) (15 * scale);
        int sliderX = sliderOriginX + labelW;
        int sliderY = sliderOriginY;

        blitGradient(poseStackOrGraphics, sliderX, sliderY, scaledSliderWidth, scaledSliderHeight,
                0, SLIDER_TEXTURE_Y + textureRow, SLIDER_WIDTH, 1);

        Services.PLATFORM.fill(poseStackOrGraphics, sliderX - 1, sliderY - 1, sliderX + scaledSliderWidth + 1, sliderY, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, sliderX - 1, sliderY + scaledSliderHeight, sliderX + scaledSliderWidth + 1,
                sliderY + scaledSliderHeight + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, sliderX - 1, sliderY - 1, sliderX, sliderY + scaledSliderHeight + 1, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, sliderX + scaledSliderWidth, sliderY - 1, sliderX + scaledSliderWidth + 1,
                sliderY + scaledSliderHeight + 1, 0xFF000000);

        float ratio = (value - min) / (float) (max - min);
        int indicatorX = sliderX + (int) (ratio * scaledSliderWidth);
        Services.PLATFORM.fill(poseStackOrGraphics, indicatorX - 1, sliderY - 2, indicatorX + 1, sliderY + scaledSliderHeight + 2, 0xFFFFFFFF);
        Services.PLATFORM.fill(poseStackOrGraphics, indicatorX, sliderY - 1, indicatorX, sliderY + scaledSliderHeight + 1, 0xFF000000);

        int fieldX = sliderX + scaledSliderWidth + (int) (3 * scale);
        int fieldY = sliderY;

        String valueStr = isHsb && !label.equals("H") ? String.format("%.1f", (float) value) : String.valueOf(value);
        int textWidth = mc.font.width(valueStr);

        int scaledFieldWidth = (int) ((textWidth + 6) * scale);
        int scaledFieldHeight = (int) (SLIDER_HEIGHT * scale) + 1;

        Services.PLATFORM.fill(poseStackOrGraphics, fieldX, fieldY, fieldX + scaledFieldWidth, fieldY + scaledFieldHeight, 0xFF000000);
        Services.PLATFORM.fill(poseStackOrGraphics, fieldX + 1, fieldY + 1, fieldX + scaledFieldWidth - 1, fieldY + scaledFieldHeight - 1,
                0x80FFFFFF);

        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, fieldX + 2, fieldY + 2, 0);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);
        Services.PLATFORM.draw(poseStackOrGraphics, mc.font, valueStr, 0, 0, 0xFF000000);
        Services.PLATFORM.popGuiPose(poseStackOrGraphics);
    }

    private void ensureGradientTexture() {
        if (gradientTexture == null || gradientTexture.getPixels() == null) {
            gradientTexture = Services.PLATFORM.createDynamicTexture("buildscape_color_picker", GRADIENT_SIZE, TEXTURE_HEIGHT, true);
            gradientTextureId = Services.PLATFORM.registerDynamicTexture("buildscape_color_picker", gradientTexture);
        }

        if (Float.compare(cachedHue, hue) == 0
                && Float.compare(cachedSaturation, saturation) == 0
                && Float.compare(cachedBrightness, brightness) == 0
                && cachedR == r && cachedG == g && cachedB == b) {
            return;
        }

        NativeImage image = gradientTexture.getPixels();
        if (image == null) {
            return;
        }

        for (int py = 0; py < GRADIENT_SIZE; py++) {
            float pixelBrightness = 1.0f - (py / (float) GRADIENT_SIZE);
            for (int px = 0; px < GRADIENT_SIZE; px++) {
                float pixelSaturation = px / (float) GRADIENT_SIZE;
                setTexturePixel(image, px, py, hsbToRgb(hue, pixelSaturation, pixelBrightness));
            }
        }

        for (int py = 0; py < HUE_SLIDER_HEIGHT; py++) {
            float pixelHue = (py / (float) HUE_SLIDER_HEIGHT) * 360.0f;
            setTexturePixel(image, 0, HUE_TEXTURE_Y + py, hsbToRgb(pixelHue, 1.0f, 1.0f));
        }

        int saturationBase = hsbToRgb(hue, 1.0f, brightness);
        int brightnessBase = hsbToRgb(hue, saturation, 1.0f);
        for (int px = 0; px < SLIDER_WIDTH; px++) {
            float ratio = px / (float) SLIDER_WIDTH;
            int variable = (int) (ratio * 255);
            setTexturePixel(image, px, SLIDER_TEXTURE_Y, (variable << 16) | (g << 8) | b);
            setTexturePixel(image, px, SLIDER_TEXTURE_Y + 1, (r << 16) | (variable << 8) | b);
            setTexturePixel(image, px, SLIDER_TEXTURE_Y + 2, (r << 16) | (g << 8) | variable);
            setTexturePixel(image, px, SLIDER_TEXTURE_Y + 3, hsbToRgb(ratio * 360.0f, 1.0f, 1.0f));
            setTexturePixel(image, px, SLIDER_TEXTURE_Y + 4, interpolateSaturation(saturationBase, ratio));
            setTexturePixel(image, px, SLIDER_TEXTURE_Y + 5, scaleBrightness(brightnessBase, ratio));
        }

        gradientTexture.upload();
        cachedHue = hue;
        cachedSaturation = saturation;
        cachedBrightness = brightness;
        cachedR = r;
        cachedG = g;
        cachedB = b;
    }

    private static int interpolateSaturation(int color, float ratio) {
        int red = (int) (128 + (((color >> 16) & 0xFF) - 128) * ratio);
        int green = (int) (128 + (((color >> 8) & 0xFF) - 128) * ratio);
        int blue = (int) (128 + ((color & 0xFF) - 128) * ratio);
        return (red << 16) | (green << 8) | blue;
    }

    private static int scaleBrightness(int color, float ratio) {
        int red = (int) (((color >> 16) & 0xFF) * ratio);
        int green = (int) (((color >> 8) & 0xFF) * ratio);
        int blue = (int) ((color & 0xFF) * ratio);
        return (red << 16) | (green << 8) | blue;
    }

    private static void setTexturePixel(NativeImage image, int x, int y, int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int abgr = (255 << 24) | (blue << 16) | (green << 8) | red;
        Services.PLATFORM.setNativeImagePixel(image, x, y, abgr);
    }

    private static void blitGradient(Object poseStackOrGraphics, int x, int y, int width, int height,
            int textureX, int textureY, int textureWidth, int textureHeight) {
        Services.PLATFORM.blit(poseStackOrGraphics, gradientTextureId, x, y, width, height,
                (float) textureX, (float) textureY, textureWidth, textureHeight, GRADIENT_SIZE, TEXTURE_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (rField != null && Services.PLATFORM.widgetMouseClicked(rField, mouseX, mouseY, button))
            return true;
        if (gField != null && Services.PLATFORM.widgetMouseClicked(gField, mouseX, mouseY, button))
            return true;
        if (bField != null && Services.PLATFORM.widgetMouseClicked(bField, mouseX, mouseY, button))
            return true;
        if (hField != null && Services.PLATFORM.widgetMouseClicked(hField, mouseX, mouseY, button))
            return true;
        if (sField != null && Services.PLATFORM.widgetMouseClicked(sField, mouseX, mouseY, button))
            return true;
        if (brightnessField != null && Services.PLATFORM.widgetMouseClicked(brightnessField, mouseX, mouseY, button))
            return true;

        if (!enabled || button != 0) {
            clearAllDragging();
            return false;
        }

        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return false;
        }

        float totalUnscaledWidth = GRADIENT_SIZE + 5 + HUE_SLIDER_WIDTH + 10 + 15 + SLIDER_WIDTH + VALUE_TEXT_WIDTH
                + 10;
        float totalUnscaledHeight = GRADIENT_SIZE + 5 + 20 + 10;

        float scaleX = (float) width / totalUnscaledWidth;
        float scaleY = (float) height / totalUnscaledHeight;
        float scale = Math.min(scaleX, scaleY);
        scale = Math.min(1.0f, scale);

        int scaledGradientSize = (int) (GRADIENT_SIZE * scale);
        int scaledHueSliderWidth = (int) (HUE_SLIDER_WIDTH * scale);
        int scaledHueSliderHeight = (int) (HUE_SLIDER_HEIGHT * scale);
        int scaledSliderWidth = (int) (SLIDER_WIDTH * scale);
        int scaledSliderSpacing = (int) (SLIDER_SPACING * scale);
        int scaledGap1 = (int) (5 * scale);
        int scaledGap2 = (int) (10 * scale);
        int scaledPadding = (int) (5 * scale);

        int gradientX = x + scaledPadding;
        int gradientY = y + scaledPadding;
        int hueSliderX = gradientX + scaledGradientSize + scaledGap1;
        int hueSliderY = gradientY;
        int rgbStartX = hueSliderX + scaledHueSliderWidth + scaledGap2;
        int rgbStartY = gradientY;
        int hsbStartY = rgbStartY + scaledSliderSpacing * 3 + scaledGap1;

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

        int labelW = (int) (15 * scale);
        int sliderX = rgbStartX + labelW;
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

        if (!isDragging) {
            return false;
        }

        float totalUnscaledWidth = GRADIENT_SIZE + 5 + HUE_SLIDER_WIDTH + 10 + 15 + SLIDER_WIDTH + VALUE_TEXT_WIDTH
                + 10;
        float totalUnscaledHeight = GRADIENT_SIZE + 5 + 20 + 10;

        float scaleX = (float) width / totalUnscaledWidth;
        float scaleY = (float) height / totalUnscaledHeight;
        float scale = Math.min(scaleX, scaleY);
        scale = Math.min(1.0f, scale);

        int scaledGradientSize = (int) (GRADIENT_SIZE * scale);
        int scaledHueSliderWidth = (int) (HUE_SLIDER_WIDTH * scale);
        int scaledHueSliderHeight = (int) (HUE_SLIDER_HEIGHT * scale);
        int scaledSliderWidth = (int) (SLIDER_WIDTH * scale);
        int scaledSliderSpacing = (int) (SLIDER_SPACING * scale);
        int scaledGap1 = (int) (5 * scale);
        int scaledGap2 = (int) (10 * scale);
        int scaledPadding = (int) (5 * scale);

        int gradientX = x + scaledPadding;
        int gradientY = y + scaledPadding;
        int hueSliderX = gradientX + scaledGradientSize + scaledGap1;
        int hueSliderY = gradientY;
        int rgbStartX = hueSliderX + scaledHueSliderWidth + scaledGap2;
        int labelW = (int) (15 * scale);
        int sliderX = rgbStartX + labelW;

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

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean wasDragging = draggingGradient || draggingHue || draggingR || draggingG || draggingB ||
                draggingH || draggingS || draggingBrightness;
        clearAllDragging();
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (rField != null && Services.PLATFORM.widgetKeyPressed(rField, keyCode, scanCode, modifiers)) return true;
        if (gField != null && Services.PLATFORM.widgetKeyPressed(gField, keyCode, scanCode, modifiers)) return true;
        if (bField != null && Services.PLATFORM.widgetKeyPressed(bField, keyCode, scanCode, modifiers)) return true;
        if (hField != null && Services.PLATFORM.widgetKeyPressed(hField, keyCode, scanCode, modifiers)) return true;
        if (sField != null && Services.PLATFORM.widgetKeyPressed(sField, keyCode, scanCode, modifiers)) return true;
        if (brightnessField != null && Services.PLATFORM.widgetKeyPressed(brightnessField, keyCode, scanCode, modifiers)) return true;
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (rField != null && Services.PLATFORM.widgetCharTyped(rField, codePoint, modifiers)) return true;
        if (gField != null && Services.PLATFORM.widgetCharTyped(gField, codePoint, modifiers)) return true;
        if (bField != null && Services.PLATFORM.widgetCharTyped(bField, codePoint, modifiers)) return true;
        if (hField != null && Services.PLATFORM.widgetCharTyped(hField, codePoint, modifiers)) return true;
        if (sField != null && Services.PLATFORM.widgetCharTyped(sField, codePoint, modifiers)) return true;
        if (brightnessField != null && Services.PLATFORM.widgetCharTyped(brightnessField, codePoint, modifiers)) return true;
        return false;
    }
}
