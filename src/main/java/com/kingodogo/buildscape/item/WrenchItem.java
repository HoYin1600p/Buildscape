package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    public void appendBuildscapeTooltip(Consumer<Component> tooltip) {
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.wrench.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.accept(com.kingodogo.buildscape.platform.Services.PLATFORM.translatable("tooltip.buildscape.wrench.desc2").withStyle(ChatFormatting.GRAY));
    }

    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return level.getBlockState(pos).getBlock() instanceof HollowPipeBlock;
    }
}
