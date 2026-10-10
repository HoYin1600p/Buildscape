package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class InventoryItemSelectorScreen implements IScreenDelegate {
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 9;

    private final Screen parentScreen;
    private final PillarItemsConfigTab configTab;
    private final Component title = ComponentHelper.translatable("buildscape.config.select_inventory");
    public int width;
    public int height;
    private Button backButton;

    public InventoryItemSelectorScreen(Screen parent, PillarItemsConfigTab configTab) {
        this.parentScreen = parent;
        this.configTab = configTab;
    }

    @Override
    public void init(Minecraft mc, int width, int height, Consumer<AbstractWidget> addWidget, Consumer<GuiEventListener> removeWidget) {
        this.width = width;
        this.height = height;

        backButton = Services.PLATFORM.createButton(
            width / 2 - 100, height - 30,
            200, 20,
            ComponentHelper.translatable("gui.back"),
            (button) -> onClose()
        );
        addWidget.accept(backButton);
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, 0x80000000);

        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font, title, width / 2, 20, 0xFFFFFF);

        Services.PLATFORM.drawCenteredString(poseStackOrGraphics, font,
            ComponentHelper.translatable("buildscape.config.select_inventory.instruction"),
            width / 2, 40, 0xCCCCCC);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Inventory inventory = mc.player.getInventory();

            int startX = (width - SLOTS_PER_ROW * SLOT_SIZE) / 2;
            int startY = 60;

            for (int i = 0; i < 9; i++) {
                int x = startX + i * SLOT_SIZE;
                int y = startY;
                renderInventorySlot(poseStackOrGraphics, font, inventory.getItem(i), x, y, mouseX, mouseY);
            }

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    int slot = 9 + row * 9 + col;
                    int x = startX + col * SLOT_SIZE;
                    int y = startY + (row + 1) * SLOT_SIZE + 5;
                    renderInventorySlot(poseStackOrGraphics, font, inventory.getItem(slot), x, y, mouseX, mouseY);
                }
            }
        }
    }

    private void renderInventorySlot(Object poseStackOrGraphics, Font font,
                                     ItemStack stack, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX < x + SLOT_SIZE &&
                          mouseY >= y && mouseY < y + SLOT_SIZE;
        int bgColor = hovered ? 0xFF555555 : 0xFF333333;
        Services.PLATFORM.fill(poseStackOrGraphics, x, y, x + SLOT_SIZE, y + SLOT_SIZE, bgColor);

        if (!stack.isEmpty()) {
            Services.PLATFORM.renderGuiItem(poseStackOrGraphics, stack, x + 1, y + 1);
            Services.PLATFORM.renderGuiItemDecorations(poseStackOrGraphics, font, stack, x + 1, y + 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && button == 0) {
            Inventory inventory = mc.player.getInventory();

            int startX = (width - SLOTS_PER_ROW * SLOT_SIZE) / 2;
            int startY = 60;

            for (int i = 0; i < 9; i++) {
                int x = startX + i * SLOT_SIZE;
                int y = startY;
                if (mouseX >= x && mouseX < x + SLOT_SIZE &&
                    mouseY >= y && mouseY < y + SLOT_SIZE) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty()) {
                        selectItem(stack);
                        return true;
                    }
                }
            }

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    int slot = 9 + row * 9 + col;
                    int x = startX + col * SLOT_SIZE;
                    int y = startY + (row + 1) * SLOT_SIZE + 5;
                    if (mouseX >= x && mouseX < x + SLOT_SIZE &&
                        mouseY >= y && mouseY < y + SLOT_SIZE) {
                        ItemStack stack = inventory.getItem(slot);
                        if (!stack.isEmpty()) {
                            selectItem(stack);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void selectItem(ItemStack stack) {
        String itemId = Services.PLATFORM.getItemId(stack.getItem()).toString();
        if (configTab != null) {
            configTab.onItemSelected(itemId);
        } else {
            com.kingodogo.buildscape.config.PillarParticleConfig.addItemToConfig(itemId);
        }
        onClose();
    }

    @Override
    public void onClose() {
        Services.PLATFORM.openScreen(parentScreen);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            onClose();
            return true;
        }
        return false;
    }
}
