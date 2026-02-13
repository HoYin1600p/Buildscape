package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;

public class SortToggleButton extends AbstractWidget {
    public enum SortType {
        INVENTORY,
        ALL_ITEMS,
        MOD_ONLY
    }
    
    private final SortType sortType;
    private boolean selected = false;
    private final Consumer<SortType> onToggle;
    private Item iconItem;
    
    public SortToggleButton(int x, int y, int width, int height, SortType sortType, Consumer<SortType> onToggle) {
        super(x, y, width, height, net.minecraft.network.chat.TextComponent.EMPTY);
        this.sortType = sortType;
        this.onToggle = onToggle;

        switch (sortType) {
            case INVENTORY:
                iconItem = Items.CHEST;
                break;
            case ALL_ITEMS:
                iconItem = Items.BOOK;
                break;
            case MOD_ONLY:
                iconItem = Items.BOOK;
                break;
        }
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

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        if (sortType == SortType.INVENTORY) {
            ItemStack stack = new ItemStack(iconItem);
            Minecraft mc = Minecraft.getInstance();
            itemRenderer.renderGuiItem(stack, centerX - 8, centerY - 8);
        } else if (sortType == SortType.ALL_ITEMS) {
            Minecraft.getInstance().font.draw(
                poseStack,
                "A",
                centerX - Minecraft.getInstance().font.width("A") / 2,
                centerY - 4,
                0xFFFFFF
            );
        } else if (sortType == SortType.MOD_ONLY) {
            Minecraft.getInstance().font.draw(
                poseStack,
                "M",
                centerX - Minecraft.getInstance().font.width("M") / 2,
                centerY - 4,
                0xFFFFFF
            );
        }
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        if (onToggle != null) {
            onToggle.accept(sortType);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (isValidClickButton(button)) {
                boolean clicked = mouseX >= (double)this.x && mouseY >= (double)this.y && 
                                mouseX < (double)(this.x + this.width) && 
                                mouseY < (double)(this.y + this.height);
                
                if (clicked) {
                    this.playDownSound(net.minecraft.client.Minecraft.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY);
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

