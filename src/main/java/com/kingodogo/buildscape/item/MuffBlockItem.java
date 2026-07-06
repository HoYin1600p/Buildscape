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

public class MuffBlockItem extends BlockItem {
    public MuffBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.buildscape.muff_block.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("tooltip.buildscape.muff_block.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("tooltip.buildscape.muff_block.desc3").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
