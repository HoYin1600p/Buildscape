package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class PatternItem extends Item {

    private final String tooltipKey;

    public PatternItem(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    public String getTooltipKey() {
        return tooltipKey;
    }

    public void appendPatternTooltip(ItemStack stack, Consumer<Component> tooltip) {
        if (this.tooltipKey != null) {
            tooltip.accept(ComponentHelper.translatable(this.tooltipKey).withStyle(ChatFormatting.GRAY));
        }
        GoldenJarItem.addAdvancementTooltip(stack, tooltip);
    }
}
