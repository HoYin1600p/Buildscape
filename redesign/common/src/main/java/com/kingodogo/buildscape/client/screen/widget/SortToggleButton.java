package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SortToggleButton implements ICustomWidget {
    public enum SortType {
        INVENTORY,
        ALL_ITEMS,
        MOD_ONLY
    }

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean active = true;
    private boolean focused = false;

    private final SortType sortType;
    private boolean selected = false;
    private final BiConsumer<SortType, Boolean> onToggle;
    private List<Component> tooltip;

    public SortToggleButton(int x, int y, int width, int height, SortType sortType, BiConsumer<SortType, Boolean> onToggle) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sortType = sortType;
        this.onToggle = onToggle;
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

    public void setTooltip(List<Component> tooltipLines) {
        this.tooltip = new ArrayList<>(tooltipLines);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public SortType getSortType() {
        return sortType;
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        int bgColor = selected ? 0xFF1E1E1E : (hovered ? 0xFF2A2A2A : 0xFF151515);
        int borderColor = selected ? 0xFF00FF00 : (hovered ? 0xFF888888 : 0xFF444444);
        int textColor = selected ? 0xFF00FF00 : 0xFFFFFFFF;

        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + 1, y + 1, x + width - 1, y + height - 1, bgColor);

        int centerX = x + width / 2;
        int centerY = y + (height - 8) / 2;

        if (sortType == SortType.INVENTORY) {
            ItemStack stack = new ItemStack(Items.CHEST);
            Services.PLATFORM.renderGuiItem(poseStackOrGraphics, stack, centerX - 8, centerY - 4);
        } else {
            String label = "";
            switch (sortType) {
                case ALL_ITEMS:
                    label = "A";
                    break;
                case MOD_ONLY:
                    label = "M";
                    break;
            }
            Services.PLATFORM.drawCenteredString(poseStackOrGraphics, mc.font, label, centerX, centerY, textColor);
        }
    }

    public void renderButtonTooltip(Object poseStackOrGraphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (hovered && tooltip != null && !tooltip.isEmpty()) {
            Services.PLATFORM.renderComponentTooltip(poseStackOrGraphics, mc.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && button == 0) {
            boolean clicked = mouseX >= (double)this.x && mouseY >= (double)this.y &&
                    mouseX < (double) (this.x + this.width) &&
                    mouseY < (double) (this.y + this.height);

            if (clicked) {
                Services.PLATFORM.playButtonClick();
                boolean isCtrlDown = Services.PLATFORM.hasControlDown();
                if (onToggle != null) {
                    onToggle.accept(sortType, isCtrlDown);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
}
