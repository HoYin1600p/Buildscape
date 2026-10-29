package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ShulkerTooltipComponent implements ClientTooltipComponent {
    private final ShulkerBoxTooltipData data;
    public ShulkerTooltipComponent(ShulkerBoxTooltipData data) { this.data = data; }
    @Override public int getHeight() { return data.getHeight(); }
    @Override public int getWidth(Font font) { return data.getWidth(font); }

    private void tile(GuiGraphics graphics, int x, int y, int u, int v, int width, int height) {
        graphics.blit(ResourceLocation.parse(ShulkerBoxTooltipData.TEXTURE.toString()), x, y, u, v, width, height, 32, 32);
    }

    @Override public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int width = getWidth(font), height = getHeight();
        float[] tint = ShulkerBoxTooltipData.getTint(data.getColor());
        graphics.setColor(tint[0], tint[1], tint[2], 1.0F);
        try {
            tile(graphics, x, y, 0, 0, 7, 7);
            tile(graphics, x + width - 7, y, 25, 0, 7, 7);
            tile(graphics, x, y + height - 7, 0, 25, 7, 7);
            tile(graphics, x + width - 7, y + height - 7, 25, 25, 7, 7);
            for (int col = 0; col < 9; col++) {
                tile(graphics, x + 7 + col * 18, y, 7, 0, 18, 7);
                tile(graphics, x + 7 + col * 18, y + height - 7, 7, 25, 18, 7);
            }
            for (int row = 0; row < 3; row++) {
                tile(graphics, x, y + 7 + row * 18, 0, 7, 7, 18);
                tile(graphics, x + width - 7, y + 7 + row * 18, 25, 7, 7, 18);
                for (int col = 0; col < 9; col++) tile(graphics, x + 7 + col * 18, y + 7 + row * 18, 7, 7, 18, 18);
            }
        } finally { graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F); }
        for (int slot = 0; slot < 27; slot++) {
            ItemStack real = data.getRealStacks().get(slot), filter = data.getFilterStacks().get(slot);
            ItemStack stack = real.isEmpty() ? filter : real;
            if (stack.isEmpty()) continue;
            int itemX = x + 8 + slot % 9 * 18, itemY = y + 8 + slot / 9 * 18;
            graphics.renderItem(stack, itemX, itemY);
            if (!real.isEmpty()) graphics.renderItemDecorations(font, stack, itemX, itemY);
        }
    }
}
