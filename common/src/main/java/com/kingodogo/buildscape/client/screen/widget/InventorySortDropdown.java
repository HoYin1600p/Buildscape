package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class InventorySortDropdown implements ICustomWidget {
    public enum SortMode {
        INVENTORY(ComponentHelper.translatable("buildscape.config.sort.inventory")),
        ALL(ComponentHelper.translatable("buildscape.config.sort.all")),
        MOD_ONLY(ComponentHelper.translatable("buildscape.config.sort.mod_only"));

        private final Component displayName;

        SortMode(Component displayName) {
            this.displayName = displayName;
        }

        public Component getDisplayName() {
            return displayName;
        }
    }

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean active = true;
    private boolean focused = false;

    private SortMode currentMode = SortMode.INVENTORY;
    private boolean dropdownOpen = false;
    private final Consumer<SortMode> onModeChanged;
    private final int dropdownHeight = 60;

    public InventorySortDropdown(int x, int y, int width, int height, Consumer<SortMode> onModeChanged) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.onModeChanged = onModeChanged;
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

    public SortMode getCurrentMode() {
        return currentMode;
    }

    public void setMode(SortMode mode) {
        if (this.currentMode != mode) {
            this.currentMode = mode;
            if (onModeChanged != null) {
                onModeChanged.accept(mode);
            }
        }
        dropdownOpen = false;
    }

    public Component getMessage() {
        return ComponentHelper.translatable("buildscape.config.select_inventory");
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        Minecraft mc = Minecraft.getInstance();
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + height, 0x80000000);
        int borderColor = (active && (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height)) ? 0xFFFFFFFF : 0xFF666666;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + width, y + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y + height - 1, x + width, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + 1, y + height, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, x + width - 1, y, x + width, y + height, borderColor);

        Services.PLATFORM.draw(
            poseStackOrGraphics,
            mc.font,
            getMessage().getString(),
            x + width / 2 - mc.font.width(getMessage().getString()) / 2,
            y + (height - 8) / 2,
            0xFFFFFF
        );

        int arrowX = x + width - 15;
        int arrowY = y + height / 2;
        if (dropdownOpen) {
            Services.PLATFORM.fill(poseStackOrGraphics, arrowX, arrowY - 2, arrowX + 5, arrowY, 0xFFFFFF);
            Services.PLATFORM.fill(poseStackOrGraphics, arrowX + 2, arrowY - 4, arrowX + 3, arrowY - 2, 0xFFFFFF);
        } else {
            Services.PLATFORM.fill(poseStackOrGraphics, arrowX, arrowY, arrowX + 5, arrowY + 2, 0xFFFFFF);
            Services.PLATFORM.fill(poseStackOrGraphics, arrowX + 2, arrowY + 2, arrowX + 3, arrowY + 4, 0xFFFFFF);
        }

        if (dropdownOpen) {
            int dropdownY = y + height;
            int optionHeight = 20;

            for (int i = 0; i < SortMode.values().length; i++) {
                SortMode mode = SortMode.values()[i];
                int optionY = dropdownY + i * optionHeight;
                boolean hovered = mouseX >= x && mouseX < x + width &&
                                mouseY >= optionY && mouseY < optionY + optionHeight;
                boolean selected = mode == currentMode;

                Services.PLATFORM.fill(poseStackOrGraphics, x, optionY, x + width, optionY + optionHeight, hovered ? 0xCC333333 : 0xCC111111);

                String icon = "";
                switch (mode) {
                    case INVENTORY:
                        icon = "📦";
                        break;
                    case ALL:
                        icon = "A";
                        break;
                    case MOD_ONLY:
                        icon = "M";
                        break;
                }

                Services.PLATFORM.draw(
                    poseStackOrGraphics,
                    mc.font,
                    icon + " " + mode.getDisplayName().getString(),
                    x + 5,
                    optionY + (optionHeight - 8) / 2,
                    selected ? 0xFFFFFF : 0xCCCCCC
                );
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) {
            return false;
        }

        if (button == 0) {
            boolean clicked = mouseX >= (double)this.x && mouseY >= (double)this.y &&
                            mouseX < (double)(this.x + this.width) &&
                            mouseY < (double)(this.y + this.height);

            if (clicked) {
                Services.PLATFORM.playButtonClick();
                this.dropdownOpen = !this.dropdownOpen;
                return true;
            }

            if (dropdownOpen) {
                int dropdownY = y + height;
                int optionHeight = 20;

                for (int i = 0; i < SortMode.values().length; i++) {
                    SortMode mode = SortMode.values()[i];
                    int optionY = dropdownY + i * optionHeight;

                    if (mouseX >= x && mouseX < x + width &&
                        mouseY >= optionY && mouseY < optionY + optionHeight) {
                        Services.PLATFORM.playButtonClick();
                        setMode(mode);
                        return true;
                    }
                }

                dropdownOpen = false;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
}
