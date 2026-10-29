package com.kingodogo.buildscape.mixinsupport;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

/** Immutable tooltip snapshot; the selected client adapter owns extraction. */
public final class TooltipPreviewComponent implements ClientTooltipComponent {
    public final NonNullList<ItemStack> filters;
    public final NonNullList<ItemStack> items;
    public final int color;
    public final boolean outside;
    public final int textHeight;
    private final ImageExtractor extractor;

    public TooltipPreviewComponent(NonNullList<ItemStack> filters, NonNullList<ItemStack> items,
            int color, boolean outside, int textHeight, ImageExtractor extractor) {
        this.filters = copy(filters);
        this.items = copy(items);
        this.color = color;
        this.outside = outside;
        this.textHeight = textHeight;
        this.extractor = extractor;
    }

    private static NonNullList<ItemStack> copy(NonNullList<ItemStack> source) {
        NonNullList<ItemStack> copy = NonNullList.withSize(source.size(), ItemStack.EMPTY);
        for (int i = 0; i < source.size(); i++) copy.set(i, source.get(i).copy());
        return copy;
    }

    public int imageWidth() { return 9 * 18 + 14; }
    public int imageHeight() { return ((items.size() + 8) / 9) * 18 + 14; }
    @Override public int getWidth(Font font) { return outside ? 0 : imageWidth(); }
    @Override public int getHeight(Font font) { return outside ? 0 : imageHeight(); }
    @Override public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        extractor.extract(this, font, x, y, graphics);
    }

    @FunctionalInterface
    public interface ImageExtractor {
        void extract(TooltipPreviewComponent preview, Font font, int x, int y, GuiGraphicsExtractor graphics);
    }
}
