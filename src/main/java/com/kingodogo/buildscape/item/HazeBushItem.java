package com.kingodogo.buildscape.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

import com.kingodogo.buildscape.core.NbtKeys;

public class HazeBushItem extends BlockItem {

    public HazeBushItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean isDrained(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                if (tag.contains(NbtKeys.BLOCK_STATE_TAG) && tag.getCompound(NbtKeys.BLOCK_STATE_TAG).contains(NbtKeys.HAS_HAZE_PROPERTY)) {
                    return "false".equalsIgnoreCase(tag.getCompound(NbtKeys.BLOCK_STATE_TAG).getString(NbtKeys.HAS_HAZE_PROPERTY));
                }
                if (tag.contains(NbtKeys.HAS_HAZE)) {
                    return !tag.getBoolean(NbtKeys.HAS_HAZE);
                }
            }
        }
        return false;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (isDrained(stack)) {
            return super.getDescriptionId(stack) + ".drained";
        }
        return super.getDescriptionId(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isDrained(stack)) {
            return new TranslatableComponent(this.getDescriptionId(stack));
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (isDrained(stack)) {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.haze_bush.drained").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
