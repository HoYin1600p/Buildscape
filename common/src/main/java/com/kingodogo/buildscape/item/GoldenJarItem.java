package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class GoldenJarItem extends GlassJarItem {

    public GoldenJarItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static void addAdvancementTooltip(ItemStack stack, Consumer<Component> tooltip) {
        CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
        String obtainedBy = (tag != null && tag.contains("ObtainedBy")) ? Services.PLATFORM.getTagString(tag, "ObtainedBy", null) : null;
        String obtainedOn = (tag != null && tag.contains("ObtainedOn")) ? Services.PLATFORM.getTagString(tag, "ObtainedOn", null) : null;

        if (obtainedBy != null && !obtainedBy.isEmpty()) {
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.trophy.obtained_by_prefix")
                    .withStyle(ChatFormatting.GRAY)
                    .append(ComponentHelper.literal(" " + obtainedBy).withStyle(ChatFormatting.AQUA)));
        }

        if (obtainedOn != null && !obtainedOn.isEmpty()) {
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.trophy.obtained_on_prefix")
                    .withStyle(ChatFormatting.GRAY)
                    .append(ComponentHelper.literal(" " + obtainedOn).withStyle(ChatFormatting.WHITE)));
        }
    }
}
