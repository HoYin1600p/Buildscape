package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ArmorDisplayPanel extends BasePanel {
    private static final int PADDING = 5;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = 2;
    private static final int REMOVE_BUTTON_SIZE = 6;
    private static final int REMOVE_BUTTON_OFFSET = 1;

    private final CosmeticRegistry cosmeticRegistry = CosmeticRegistry.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    

    private static final String[] ARMOR_SLOTS = {"head", "chest", "legs", "feet"};

    private int getArmorSlotIndex(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }
        
        Item item = stack.getItem();

        if (item instanceof ElytraItem) {
            return 1;
        }

        if (item instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) item;
            switch (armor.getSlot()) {
                case HEAD:
                    return 0;
                case CHEST:
                    return 1;
                case LEGS:
                    return 2;
                case FEET:
                    return 3;
                default:
                    return -1;
            }
        }
        
        return -1;
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        
        int scissorX = (int)(startX * guiScale);
        int scissorY = (int)(windowHeight - (startY + height) * guiScale);
        int scissorWidth = (int)(width * guiScale);
        int scissorHeight = (int)(height * guiScale);
        
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);

        String title = "Armor";
        mc.font.draw(poseStack, title, startX + PADDING, startY + PADDING, 0xFFFFFF);

        Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();

        Map<Integer, ItemStack> cosmeticArmorBySlot = new HashMap<>();
        for (Map.Entry<Integer, String> entry : equippedBySlot.entrySet()) {
            int slotIndex = entry.getKey();
            String cosmeticId = entry.getValue();
            ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
            if (stack != null && !stack.isEmpty()) {
                int expectedSlot = getArmorSlotIndex(stack);
                if (expectedSlot == slotIndex || expectedSlot >= 0) {
                    cosmeticArmorBySlot.put(slotIndex, stack);
                }
            }
        }

        int y = startY + PADDING + 12;
        int x = startX + PADDING;
        
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            int slotX = x + i * (SLOT_SIZE + SLOT_SPACING);
            int slotY = y;

            boolean isHovered = mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                              mouseY >= slotY && mouseY < slotY + SLOT_SIZE;

            int slotBgColor = isHovered ? 0xAA808080 : 0xFF808080;
            GuiComponent.fill(poseStack, slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0x80000000);
            GuiComponent.fill(poseStack, slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, slotBgColor);
            GuiComponent.fill(poseStack, slotX + 1, slotY + 1, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, 0xFF000000);

            ItemStack cosmeticArmor = cosmeticArmorBySlot.get(i);
            if (cosmeticArmor != null && !cosmeticArmor.isEmpty()) {
                mc.getItemRenderer().renderGuiItem(cosmeticArmor, slotX + 1, slotY + 1);
                mc.getItemRenderer().renderGuiItemDecorations(mc.font, cosmeticArmor, slotX + 1, slotY + 1);

                int removeButtonX = slotX + SLOT_SIZE - REMOVE_BUTTON_SIZE - REMOVE_BUTTON_OFFSET;
                int removeButtonY = slotY + REMOVE_BUTTON_OFFSET;

                boolean isHoveringRemove = mouseX >= removeButtonX && mouseX < removeButtonX + REMOVE_BUTTON_SIZE &&
                                         mouseY >= removeButtonY && mouseY < removeButtonY + REMOVE_BUTTON_SIZE;

                int removeBgColor = isHoveringRemove ? 0xCCFF0000 : 0x80000000;
                GuiComponent.fill(poseStack, removeButtonX, removeButtonY, 
                    removeButtonX + REMOVE_BUTTON_SIZE, removeButtonY + REMOVE_BUTTON_SIZE, removeBgColor);

                int xColor = isHoveringRemove ? 0xFFFFFFFF : 0xFFCCCCCC;
                GuiComponent.fill(poseStack,
                    removeButtonX + 1, removeButtonY + 1,
                    removeButtonX + REMOVE_BUTTON_SIZE - 1, removeButtonY + 2, xColor);
                GuiComponent.fill(poseStack, 
                    removeButtonX + 1, removeButtonY + REMOVE_BUTTON_SIZE - 2,
                    removeButtonX + REMOVE_BUTTON_SIZE - 1, removeButtonY + REMOVE_BUTTON_SIZE - 1, xColor);
                GuiComponent.fill(poseStack,
                    removeButtonX + REMOVE_BUTTON_SIZE - 2, removeButtonY + 1,
                    removeButtonX + REMOVE_BUTTON_SIZE - 1, removeButtonY + REMOVE_BUTTON_SIZE - 1, xColor);
                GuiComponent.fill(poseStack, 
                    removeButtonX + 1, removeButtonY + 1,
                    removeButtonX + 2, removeButtonY + REMOVE_BUTTON_SIZE - 1, xColor);
            }
        }
        
        RenderSystem.disableScissor();
    }

    public boolean handleDrop(double mouseX, double mouseY, String draggedCosmeticId) {
        if (draggedCosmeticId == null || draggedCosmeticId.isEmpty()) {
            return false;
        }

        if (!isInside(mouseX, mouseY)) {
            return false;
        }

        ItemStack stack = cosmeticRegistry.resolveToItemStack(draggedCosmeticId);
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        
        int slotIndex = getArmorSlotIndex(stack);
        if (slotIndex < 0 || slotIndex >= ARMOR_SLOTS.length) {
            return false;
        }

        int y = startY + PADDING + 12;
        int x = startX + PADDING;
        
        int slotX = x + slotIndex * (SLOT_SIZE + SLOT_SPACING);
        int slotY = y;

        boolean onSlot = mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                        mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
        
        if (onSlot || isInside(mouseX, mouseY)) {
            state.equipCosmeticToSlot(slotIndex, draggedCosmeticId);
            return true;
        }
        
        return false;
    }

    public boolean canDropOnSlot(int slotIndex, String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return false;
        }
        
        ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        
        int expectedSlot = getArmorSlotIndex(stack);
        return expectedSlot == slotIndex;
    }
    
    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();

        int y = startY + PADDING + 12;
        int x = startX + PADDING;
        
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            int slotX = x + i * (SLOT_SIZE + SLOT_SPACING);
            int slotY = y;

            String cosmeticId = equippedBySlot.get(i);
            if (cosmeticId != null && !cosmeticId.isEmpty()) {
                int removeButtonX = slotX + SLOT_SIZE - REMOVE_BUTTON_SIZE - REMOVE_BUTTON_OFFSET;
                int removeButtonY = slotY + REMOVE_BUTTON_OFFSET;

                if (mouseX >= removeButtonX && mouseX < removeButtonX + REMOVE_BUTTON_SIZE &&
                    mouseY >= removeButtonY && mouseY < removeButtonY + REMOVE_BUTTON_SIZE) {
                    state.unequipCosmeticFromSlot(i);
                    return true;
                }
            }
        }
        
        return false;
    }
}

