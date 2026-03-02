package com.kingodogo.buildscape.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class MistBlockItem extends BlockItem {
    public MistBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (this.getBlock() == com.kingodogo.buildscape.block.ModBlocks.CASCADE_BLOCK_NO_MIST.get()) {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.cascade_block_no_mist.info").withStyle(ChatFormatting.GRAY));
            tooltip.add(new TranslatableComponent("tooltip.buildscape.cascade_block_no_mist.obtain").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.cascade_block.info").withStyle(ChatFormatting.GRAY));
            tooltip.add(new TranslatableComponent("tooltip.buildscape.mist_toggle").withStyle(ChatFormatting.DARK_AQUA));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
