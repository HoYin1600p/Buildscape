package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;

import java.util.Map;
import java.util.HashMap;

/**
 * Armor Display Panel (Panel 6)
 * 
 * Armor model similar to Minecraft inventory view.
 * Resolves equipped cosmetic IDs to armor ItemStack via CosmeticRegistry.
 * Reflects equipped cosmetic armor.
 * 
 * Dimensions: 21% width × 18% height
 * Position: (45%, 82%)
 */
public class ArmorDisplayPanel extends BasePanel {
    private static final int PADDING = 5;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = 2;
    private static final int REMOVE_BUTTON_SIZE = 6; // Size of the X button
    private static final int REMOVE_BUTTON_OFFSET = 1; // Offset from top-right corner
    
    private final CosmeticRegistry cosmeticRegistry = CosmeticRegistry.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    
    
    // Armor slot positions (similar to inventory)
    private static final String[] ARMOR_SLOTS = {"head", "chest", "legs", "feet"};
    
    /**
     * Get the armor slot index for a cosmetic item.
     * Returns -1 if not an armor item.
     */
    private int getArmorSlotIndex(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }
        
        Item item = stack.getItem();
        
        // Check for elytra (goes in chest slot)
        if (item instanceof ElytraItem) {
            return 1; // Chest slot
        }
        
        // Check for armor items
        if (item instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) item;
            switch (armor.getSlot()) {
                case HEAD:
                    return 0; // Head slot
                case CHEST:
                    return 1; // Chest slot
                case LEGS:
                    return 2; // Legs slot
                case FEET:
                    return 3; // Feet slot
                default:
                    return -1;
            }
        }
        
        return -1;
    }
    
    @Override
    public void init() {
        // Initialize armor display
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Enable scissor clipping
        Minecraft mc = Minecraft.getInstance();
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        
        int scissorX = (int)(startX * guiScale);
        int scissorY = (int)(windowHeight - (startY + height) * guiScale);
        int scissorWidth = (int)(width * guiScale);
        int scissorHeight = (int)(height * guiScale);
        
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        
        // Render background
        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);
        
        // Render title
        String title = "Armor";
        mc.font.draw(poseStack, title, startX + PADDING, startY + PADDING, 0xFFFFFF);
        
        // Get equipped cosmetics by slot
        Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();
        
        // Map cosmetic armor to their slot indices
        Map<Integer, ItemStack> cosmeticArmorBySlot = new HashMap<>();
        for (Map.Entry<Integer, String> entry : equippedBySlot.entrySet()) {
            int slotIndex = entry.getKey();
            String cosmeticId = entry.getValue();
            ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
            if (stack != null && !stack.isEmpty()) {
                // Verify the cosmetic matches the slot
                int expectedSlot = getArmorSlotIndex(stack);
                if (expectedSlot == slotIndex || expectedSlot >= 0) {
                    cosmeticArmorBySlot.put(slotIndex, stack);
                }
            }
        }
        
        // Render vanilla armor slots horizontally in a line
        int y = startY + PADDING + 12;
        int x = startX + PADDING;
        
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            int slotX = x + i * (SLOT_SIZE + SLOT_SPACING);
            int slotY = y; // Keep Y constant for horizontal layout
            
            // Check if mouse is hovering over this slot (for drop indication)
            boolean isHovered = mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                              mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
            
            // Render vanilla armor slot background (always visible)
            int slotBgColor = isHovered ? 0xAA808080 : 0xFF808080; // Lighter when hovered
            GuiComponent.fill(poseStack, slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0x80000000);
            GuiComponent.fill(poseStack, slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, slotBgColor);
            GuiComponent.fill(poseStack, slotX + 1, slotY + 1, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, 0xFF000000);
            
            // Render cosmetic armor on top of vanilla slot if equipped
            ItemStack cosmeticArmor = cosmeticArmorBySlot.get(i);
            if (cosmeticArmor != null && !cosmeticArmor.isEmpty()) {
                // Render cosmetic armor item on top of vanilla slot
                mc.getItemRenderer().renderGuiItem(cosmeticArmor, slotX + 1, slotY + 1);
                mc.getItemRenderer().renderGuiItemDecorations(mc.font, cosmeticArmor, slotX + 1, slotY + 1);
                
                // Render remove button (X mark) in top-right corner
                int removeButtonX = slotX + SLOT_SIZE - REMOVE_BUTTON_SIZE - REMOVE_BUTTON_OFFSET;
                int removeButtonY = slotY + REMOVE_BUTTON_OFFSET;
                
                // Check if mouse is hovering over remove button
                boolean isHoveringRemove = mouseX >= removeButtonX && mouseX < removeButtonX + REMOVE_BUTTON_SIZE &&
                                         mouseY >= removeButtonY && mouseY < removeButtonY + REMOVE_BUTTON_SIZE;
                
                // Render remove button background (semi-transparent red when hovered)
                int removeBgColor = isHoveringRemove ? 0xCCFF0000 : 0x80000000;
                GuiComponent.fill(poseStack, removeButtonX, removeButtonY, 
                    removeButtonX + REMOVE_BUTTON_SIZE, removeButtonY + REMOVE_BUTTON_SIZE, removeBgColor);
                
                // Render X mark (simple diagonal lines)
                int xColor = isHoveringRemove ? 0xFFFFFFFF : 0xFFCCCCCC;
                // Draw X: two diagonal lines
                // Top-left to bottom-right diagonal
                GuiComponent.fill(poseStack, 
                    removeButtonX + 1, removeButtonY + 1,
                    removeButtonX + REMOVE_BUTTON_SIZE - 1, removeButtonY + 2, xColor);
                GuiComponent.fill(poseStack, 
                    removeButtonX + 1, removeButtonY + REMOVE_BUTTON_SIZE - 2,
                    removeButtonX + REMOVE_BUTTON_SIZE - 1, removeButtonY + REMOVE_BUTTON_SIZE - 1, xColor);
                // Top-right to bottom-left diagonal
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
    
    /**
     * Handle mouse release to accept dropped cosmetics.
     * This is called from the tab when a drag is released.
     */
    public boolean handleDrop(double mouseX, double mouseY, String draggedCosmeticId) {
        if (draggedCosmeticId == null || draggedCosmeticId.isEmpty()) {
            return false;
        }
        
        // First check if mouse is anywhere within Panel 6
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        
        // Check if the cosmetic is armor
        ItemStack stack = cosmeticRegistry.resolveToItemStack(draggedCosmeticId);
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        
        int slotIndex = getArmorSlotIndex(stack);
        if (slotIndex < 0 || slotIndex >= ARMOR_SLOTS.length) {
            return false; // Not armor or invalid slot
        }
        
        // Check if dropped on the specific armor slot
        int y = startY + PADDING + 12;
        int x = startX + PADDING;
        
        int slotX = x + slotIndex * (SLOT_SIZE + SLOT_SPACING);
        int slotY = y;
        
        // Allow drop if mouse is on the slot OR anywhere in the panel (auto-place in correct slot)
        boolean onSlot = mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                        mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
        
        if (onSlot || isInside(mouseX, mouseY)) {
            // Equip the cosmetic to its correct slot
            state.equipCosmeticToSlot(slotIndex, draggedCosmeticId);
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if a cosmetic can be dropped on a specific slot.
     */
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
        if (button != 0) return false; // Only handle left click

        // Get equipped cosmetics by slot
        Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();
        
        // Calculate slot positions
        int y = startY + PADDING + 12;
        int x = startX + PADDING;
        
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            int slotX = x + i * (SLOT_SIZE + SLOT_SPACING);
            int slotY = y;
            
            // Check if there's a cosmetic in this slot
            String cosmeticId = equippedBySlot.get(i);
            if (cosmeticId != null && !cosmeticId.isEmpty()) {
                // Calculate remove button position
                int removeButtonX = slotX + SLOT_SIZE - REMOVE_BUTTON_SIZE - REMOVE_BUTTON_OFFSET;
                int removeButtonY = slotY + REMOVE_BUTTON_OFFSET;
                
                // Check if click is on remove button
                if (mouseX >= removeButtonX && mouseX < removeButtonX + REMOVE_BUTTON_SIZE &&
                    mouseY >= removeButtonY && mouseY < removeButtonY + REMOVE_BUTTON_SIZE) {
                    // Remove cosmetic from this slot
                    state.unequipCosmeticFromSlot(i);
                    return true;
                }
            }
        }
        
        return false;
    }
}

