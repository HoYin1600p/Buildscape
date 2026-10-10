package com.kingodogo.buildscape.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class FestiveStarItem extends BlockItem {

    public FestiveStarItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static void appendFestiveStarTooltip(ItemStack stack, Consumer<Component> tooltip) {
        GoldenJarItem.addAdvancementTooltip(stack, tooltip);
    }
}
