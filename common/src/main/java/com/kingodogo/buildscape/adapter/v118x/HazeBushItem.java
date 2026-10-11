package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** 1.18.2 haze bush item: the drained state lives in the stack's legacy BlockStateTag NBT. */
public final class HazeBushItem extends com.kingodogo.buildscape.item.HazeBushItem {
    public HazeBushItem(Block block, Properties properties) { super(block, properties); }

    @Override protected boolean hasDrainedState(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        if (tag.contains("BlockStateTag") && tag.getCompound("BlockStateTag").contains("has_haze")) {
            return "false".equalsIgnoreCase(tag.getCompound("BlockStateTag").getString("has_haze"));
        }
        return tag.contains("HasHaze") && !tag.getBoolean("HasHaze");
    }

    @Override protected void writeDrainedState(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag state = tag.getCompound("BlockStateTag");
        state.putString("has_haze", "false");
        tag.put("BlockStateTag", state);
        tag.putBoolean("HasHaze", false);
    }

    @Override public String getDescriptionId(ItemStack stack) {
        return hasDrainedState(stack) ? super.getDescriptionId(stack) + ".drained" : super.getDescriptionId(stack);
    }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if (hasDrainedState(stack)) {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.haze_bush.drained").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
