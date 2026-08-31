package com.kingodogo.buildscape.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxTooltipData implements TooltipComponent, ClientTooltipComponent {
    private final NonNullList<ItemStack> filterStacks;
    private final NonNullList<ItemStack> realStacks;
    private final int frameColor;

    public ShulkerBoxTooltipData(NonNullList<ItemStack> filterStacks, NonNullList<ItemStack> realStacks, int frameColor) {
        this.filterStacks = filterStacks;
        this.realStacks = realStacks;
        this.frameColor = frameColor;
    }

    public NonNullList<ItemStack> getFilterStacks() {
        return filterStacks;
    }

    public NonNullList<ItemStack> getRealStacks() {
        return realStacks;
    }

    @Override
    public int getHeight() {
        return 3 * 18 + 6;
    }

    @Override
    public int getWidth(Font font) {
        return 9 * 18 + 6;
    }

    @Override
    public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset) {
        Minecraft mc = Minecraft.getInstance();

        int width = getWidth(font);
        int height = getHeight();

        // 1. Render outer frame border using the Shulker Box's color
        int borderColor = 0xFF000000 | frameColor;
        GuiComponent.fill(poseStack, x, y, x + width, y + height, borderColor);
        GuiComponent.fill(poseStack, x + 1, y + 1, x + width - 1, y + height - 1, 0xFF111827);
        GuiComponent.fill(poseStack, x + 2, y + 2, x + width - 2, y + height - 2, 0xF00B0F19);

        // 2. Render 9x3 grid of slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                int slotX = x + 3 + col * 18;
                int slotY = y + 3 + row * 18;



                ItemStack filter = index < filterStacks.size() ? filterStacks.get(index) : ItemStack.EMPTY;
                ItemStack real = index < realStacks.size() ? realStacks.get(index) : ItemStack.EMPTY;

                int itemX = slotX + 1;
                int itemY = slotY + 1;

                if (!real.isEmpty()) {
                    float prevBlit = itemRenderer.blitOffset;
                    itemRenderer.blitOffset = blitOffset + 100.0F;
                    itemRenderer.renderAndDecorateItem(mc.player, real, itemX, itemY, index);
                    itemRenderer.renderGuiItemDecorations(font, real, itemX, itemY);
                    itemRenderer.blitOffset = prevBlit;
                } else if (!filter.isEmpty()) {
                    float prevBlit = itemRenderer.blitOffset;
                    itemRenderer.blitOffset = blitOffset + 100.0F;
                    itemRenderer.renderAndDecorateItem(mc.player, filter, itemX, itemY, index);
                    itemRenderer.blitOffset = prevBlit;
                }
            }
        }
    }
}
