package com.kingodogo.buildscape.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoldenJarItem extends GlassJarItem {

    public GoldenJarItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        addAdvancementTooltip(stack, tooltip);
    }

    public static void addAdvancementTooltip(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = stack.getTag();
        String obtainedBy = (tag != null && tag.contains("ObtainedBy")) ? tag.getString("ObtainedBy") : null;
        String obtainedOn = (tag != null && tag.contains("ObtainedOn")) ? tag.getString("ObtainedOn") : null;

        if (obtainedBy != null && !obtainedBy.isEmpty()) {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.trophy.obtained_by_prefix")
                    .withStyle(ChatFormatting.GRAY)
                    .append(new TextComponent(" " + obtainedBy).withStyle(ChatFormatting.AQUA)));
        }

        if (obtainedOn != null && !obtainedOn.isEmpty()) {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.trophy.obtained_on_prefix")
                    .withStyle(ChatFormatting.GRAY)
                    .append(new TextComponent(" " + obtainedOn).withStyle(ChatFormatting.WHITE)));
        }
    }
}
