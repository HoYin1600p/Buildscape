package com.kingodogo.buildscape.item;

import net.minecraft.world.item.Item;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class BigOrnamentTemplateItem extends Item {

    public BigOrnamentTemplateItem(Properties properties) {
        super(properties);
    }

    public void appendBuildscapeTooltip(Consumer<Component> tooltip) {
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.big_ornament_template").withStyle(ChatFormatting.GRAY));
    }
}
