package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public abstract class HazeBushItem extends BlockItem {
    public HazeBushItem(Block block, Properties properties) {
        super(block, properties);
    }

    protected abstract boolean hasDrainedState(ItemStack stack);
    protected abstract void writeDrainedState(ItemStack stack);

    public static boolean isDrained(ItemStack stack) {
        return stack.getItem() instanceof HazeBushItem item && item.hasDrainedState(stack);
    }

    public static ItemStack markDrained(ItemStack stack) {
        if (stack.getItem() instanceof HazeBushItem item) item.writeDrainedState(stack);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        return isDrained(stack) ? ComponentHelper.translatable(getDescriptionId() + ".drained") : super.getName(stack);
    }
}
