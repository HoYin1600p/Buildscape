package com.kingodogo.buildscape.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;

public class InventoryItemSelectorScreen extends Screen {
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 9;
    
    private final Screen parentScreen;
    private final PillarItemsConfigTab configTab;
    private Button backButton;
    
    public InventoryItemSelectorScreen(Screen parent, PillarItemsConfigTab configTab) {
        super(new TranslatableComponent("buildscape.config.select_inventory"));
        this.parentScreen = parent;
        this.configTab = configTab;
    }
    
    @Override
    protected void init() {
        super.init();
        
        backButton = new Button(
            width / 2 - 100, height - 30,
            200, 20,
            new TranslatableComponent("gui.back"),
            (button) -> onClose()
        );
        addRenderableWidget(backButton);
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        
        // Render title
        net.minecraft.client.gui.GuiComponent.drawCenteredString(poseStack, font, title, width / 2, 20, 0xFFFFFF);
        
        // Render instruction
        net.minecraft.client.gui.GuiComponent.drawCenteredString(poseStack, font,
            new TranslatableComponent("buildscape.config.select_inventory.instruction"),
            width / 2, 40, 0xCCCCCC);
        
        // Render inventory
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Inventory inventory = mc.player.getInventory();
            ItemRenderer itemRenderer = mc.getItemRenderer();
            
            int startX = (width - SLOTS_PER_ROW * SLOT_SIZE) / 2;
            int startY = 60;
            
            // Render hotbar (slots 0-8)
            for (int i = 0; i < 9; i++) {
                int x = startX + i * SLOT_SIZE;
                int y = startY;
                renderInventorySlot(poseStack, itemRenderer, inventory.getItem(i), x, y, mouseX, mouseY);
            }
            
            // Render main inventory (slots 9-35)
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    int slot = 9 + row * 9 + col;
                    int x = startX + col * SLOT_SIZE;
                    int y = startY + (row + 1) * SLOT_SIZE + 5;
                    renderInventorySlot(poseStack, itemRenderer, inventory.getItem(slot), x, y, mouseX, mouseY);
                }
            }
        }
        
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
    
    private void renderInventorySlot(PoseStack poseStack, ItemRenderer itemRenderer, 
                                     ItemStack stack, int x, int y, int mouseX, int mouseY) {
        // Render slot background
        boolean hovered = mouseX >= x && mouseX < x + SLOT_SIZE &&
                          mouseY >= y && mouseY < y + SLOT_SIZE;
        int bgColor = hovered ? 0xFF555555 : 0xFF333333;
        fill(poseStack, x, y, x + SLOT_SIZE, y + SLOT_SIZE, bgColor);
        
        // Render item
        if (!stack.isEmpty()) {
            itemRenderer.renderGuiItem(stack, x + 1, y + 1);
            itemRenderer.renderGuiItemDecorations(font, stack, x + 1, y + 1);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        // Check if clicking on inventory slot
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && button == 0) {
            Inventory inventory = mc.player.getInventory();
            
            int startX = (width - SLOTS_PER_ROW * SLOT_SIZE) / 2;
            int startY = 60;
            
            // Check hotbar
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
            
            // Check main inventory
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
        String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        if (configTab != null) {
            // Add item through the config tab
            configTab.onItemSelected(itemId);
        } else {
            // Fallback: directly call the config
            com.kingodogo.buildscape.config.PillarParticleConfig.addItemToConfig(itemId);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendMessage(
                    new TranslatableComponent("buildscape.config.item_added", itemId),
                    java.util.UUID.randomUUID()
                );
            }
        }
        onClose();
    }
    
    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parentScreen);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

