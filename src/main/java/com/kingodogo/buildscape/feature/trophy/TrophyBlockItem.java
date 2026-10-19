package com.kingodogo.buildscape.feature.trophy;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.kingodogo.buildscape.core.NbtKeys;

public class TrophyBlockItem extends BlockItem {
    private final TrophyDefinition definition;

    public TrophyBlockItem(TrophyBlock block, TrophyDefinition definition, Properties properties) {
        super(block, properties);
        this.definition = definition;
    }

    public TrophyDefinition getDefinition() {
        return definition;
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TranslatableComponent("block.buildscape." + definition.getId());
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return definition.getRarity();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();
        String obtainedBy = (tag != null && tag.contains(NbtKeys.OBTAINED_BY)) ? tag.getString(NbtKeys.OBTAINED_BY) : null;
        String obtainedOn = (tag != null && tag.contains(NbtKeys.OBTAINED_ON)) ? tag.getString(NbtKeys.OBTAINED_ON) : null;

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

        if (definition.getCustomDescription() != null) {
            tooltip.add(new TextComponent(definition.getCustomDescription()).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        }
    }
}
