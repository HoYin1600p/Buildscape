package com.kingodogo.buildscape.item;

import net.minecraft.world.item.Item;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class StringlightFrameItem extends Item {

    public StringlightFrameItem(Properties properties) {
        super(properties);
    }

    public void appendBuildscapeTooltip(Consumer<Component> tooltip) {
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.stringlight_frame.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.stringlight_frame.desc2").withStyle(ChatFormatting.GRAY));
    }
}
