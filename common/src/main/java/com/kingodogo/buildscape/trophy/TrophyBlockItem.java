package com.kingodogo.buildscape.trophy;

import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.function.Consumer;
public class TrophyBlockItem extends BlockItem {
    private final TrophyDefinition definition;

    public TrophyBlockItem(TrophyBlock block, TrophyDefinition definition, Properties properties) {
        super(block, properties.rarity(definition.getRarity()));
        this.definition = definition;
    }

    public TrophyDefinition getDefinition() {
        return definition;
    }

    @Override
    public Component getName(ItemStack stack) {
        return ComponentHelper.translatable("block.buildscape." + definition.getId());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack);
    }

    public void addHoverText(ItemStack stack, Consumer<Component> tooltip) {
        CompoundTag tag = com.kingodogo.buildscape.platform.Services.PLATFORM.getCustomData(stack, false);
        String obtainedBy = (tag != null && tag.contains("ObtainedBy")) ? com.kingodogo.buildscape.platform.Services.PLATFORM.getTagString(tag, "ObtainedBy", null) : null;
        String obtainedOn = (tag != null && tag.contains("ObtainedOn")) ? com.kingodogo.buildscape.platform.Services.PLATFORM.getTagString(tag, "ObtainedOn", null) : null;

        if (obtainedBy != null && !obtainedBy.isEmpty()) {
            tooltip.accept(com.kingodogo.buildscape.util.ComponentHelper.translatable("tooltip.buildscape.trophy.obtained_by_prefix")
                    .withStyle(ChatFormatting.GRAY)
                    .append(com.kingodogo.buildscape.util.ComponentHelper.literal(" " + obtainedBy).withStyle(ChatFormatting.AQUA)));
        }

        if (obtainedOn != null && !obtainedOn.isEmpty()) {
            tooltip.accept(com.kingodogo.buildscape.util.ComponentHelper.translatable("tooltip.buildscape.trophy.obtained_on_prefix")
                    .withStyle(ChatFormatting.GRAY)
                    .append(com.kingodogo.buildscape.util.ComponentHelper.literal(" " + obtainedOn).withStyle(ChatFormatting.WHITE)));
        }

        if (definition.getCustomDescription() != null) {
            tooltip.accept(com.kingodogo.buildscape.util.ComponentHelper.literal(definition.getCustomDescription()).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        }
    }
}
