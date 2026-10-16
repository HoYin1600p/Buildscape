package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.block.CascadeBlockNoMist;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class MistBlockItem extends BlockItem {

    public MistBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public void appendMistBlockTooltip(Consumer<Component> tooltip) {
        if (this.getBlock() instanceof CascadeBlockNoMist) {
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.cascade_block_no_mist.info").withStyle(ChatFormatting.GRAY));
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.cascade_block.tune").withStyle(ChatFormatting.GRAY));
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.cascade_block_no_mist.obtain").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.cascade_block.info").withStyle(ChatFormatting.GRAY));
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.cascade_block.tune").withStyle(ChatFormatting.GRAY));
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.mist_toggle").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
