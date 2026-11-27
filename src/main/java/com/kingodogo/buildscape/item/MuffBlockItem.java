package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class MuffBlockItem extends BlockItem {

    public MuffBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static void appendMuffBlockTooltip(Consumer<Component> tooltip) {
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.muff_block.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.muff_block.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.muff_block.desc3").withStyle(ChatFormatting.AQUA));
    }
}
