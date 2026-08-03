package com.kingodogo.buildscape.item;

import net.minecraft.world.item.Item;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(net.minecraft.world.item.ItemStack stack, @javax.annotation.Nullable net.minecraft.world.level.Level level, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.wrench.desc1").withStyle(net.minecraft.ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
