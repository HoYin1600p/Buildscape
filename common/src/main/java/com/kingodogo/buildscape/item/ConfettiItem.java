package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ConfettiItem extends Item {

    public ConfettiItem(Properties properties) {
        super(properties);
    }

    public static void appendConfettiTooltip(ItemStack stack, Consumer<Component> tooltip) {
        int burstLevel = 1;
        CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
        if (tag != null && tag.contains("BurstLevel")) {
            burstLevel = Math.min(5, Math.max(1, Services.PLATFORM.getTagInt(tag, "BurstLevel", 1)));
        }
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.confetti.burst_level", burstLevel).withStyle(ChatFormatting.GRAY));
    }
}
