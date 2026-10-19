package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import java.util.List;
import javax.annotation.Nullable;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.buildscape.wrench.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("tooltip.buildscape.wrench.desc2").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level,
                                      BlockPos pos, Player player) {
        return level.getBlockState(pos).getBlock() instanceof HollowPipeBlock;
    }
}
