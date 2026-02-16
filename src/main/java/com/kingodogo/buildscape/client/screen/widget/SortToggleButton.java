package com.kingodogo.buildscape.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.world.item.Item;


public class SortToggleButton extends AbstractWidget {
    public enum SortType {
        INVENTORY,
        ALL_ITEMS,
        MOD_ONLY
    }
    
    private final SortType sortType;
    private boolean selected = false;
    private final java.util.function.BiConsumer<SortType, Boolean> onToggle;
    private Item iconItem;
    private java.util.List<net.minecraft.util.FormattedCharSequence> tooltip;

    public SortToggleButton(int x, int y, int width, int height, SortType sortType, java.util.function.BiConsumer<SortType, Boolean> onToggle) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.sortType = sortType;
        this.onToggle = onToggle;
        // Icons now rendered as text/shapes
    }

    public void setTooltip(java.util.List<net.minecraft.network.chat.Component> tooltipLines) {
        this.tooltip = new java.util.ArrayList<>();
        for (net.minecraft.network.chat.Component line : tooltipLines) {
            this.tooltip.add(line.getVisualOrderText());
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
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
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        // Modern Flat Design
        // Background: Dark Grey
        // Selected: Green Accent
        // Border: 1px constant

        int bgColor = selected ? 0xFF1E1E1E : (hovered ? 0xFF2A2A2A : 0xFF151515);
        int borderColor = selected ? 0xFF00FF00 : (hovered ? 0xFF888888 : 0xFF444444); // Green when selected
        int textColor = selected ? 0xFF00FF00 : 0xFFFFFFFF;

        // Draw background
        fill(poseStack, x, y, x + width, y + height, borderColor); // Border
        fill(poseStack, x + 1, y + 1, x + width - 1, y + height - 1, bgColor); // Background
        
        int centerX = x + width / 2;
        int centerY = y + (height - 8) / 2; // Center text vertically (font height 8)

        if (sortType == SortType.INVENTORY) {
            // Render Chest Icon
            net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.CHEST);
            // Item is 16x16. CenterY is calculated for text (height 8).
            // Button Center Y = y + height/2.
            // Item Y = Button Center Y - 8.
            // Text CenterY = y + (height - 8) / 2 = Button Center Y - 4.
            // So Item Y = Text CenterY - 4.
            mc.getItemRenderer().renderGuiItem(stack, centerX - 8, centerY - 4);
        } else {
            String label = "";
            switch (sortType) {
                case ALL_ITEMS:
                    label = "A";
                    break; // A for All
                case MOD_ONLY:
                    label = "M";
                    break; // M for Mod
            }
            drawCenteredString(poseStack, mc.font, label, centerX, centerY, textColor);
        }

        // Optional: Add glow or underline if selected?
        // kept simple as requested "modern panel like"
    }

    public void renderButtonTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (hovered && tooltip != null && mc.screen != null) {
            mc.screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
        }
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        // This is called from mouseClicked, but we want modifiers, so we'll handle it there instead if possible
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (isValidClickButton(button)) {
                boolean clicked = mouseX >= (double)this.x && mouseY >= (double)this.y &&
                        mouseX < (double) (this.x + this.width) &&
                        mouseY < (double) (this.y + this.height);
                
                if (clicked) {
                    this.playDownSound(net.minecraft.client.Minecraft.getInstance().getSoundManager());
                    boolean isCtrlDown = net.minecraft.client.gui.screens.Screen.hasControlDown();
                    if (onToggle != null) {
                        onToggle.accept(sortType, isCtrlDown);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }
}

