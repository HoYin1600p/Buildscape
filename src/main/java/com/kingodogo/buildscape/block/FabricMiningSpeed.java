package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

final class FabricMiningSpeed {
    private FabricMiningSpeed() {
    }

    static float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float hardness = state.getDestroySpeed(level, pos);
        if (hardness == -1.0F) return 0.0F;
        ItemStack tool = player.getMainHandItem();
        boolean fabricTool = tool.getItem() instanceof ShearsItem || tool.getItem() instanceof HoeItem;
        float fabricSpeed = 2.5F;
        if (tool.getItem() instanceof ShearsItem) {
            fabricSpeed = 5.0F;
        } else if (tool.getItem() instanceof HoeItem hoe) {
            fabricSpeed = hoe.getTier().getSpeed();
        }
        float toolSpeed = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        int efficiency = EnchantmentHelper.getBlockEfficiency(player);
        float bonus = efficiency > 0 && !tool.isEmpty() ? efficiency * efficiency + 1.0F : 0.0F;
        if (fabricTool) fabricSpeed += bonus;
        if (toolSpeed > 1.0F) toolSpeed += bonus;
        float speed = player.getDigSpeed(state, pos) * fabricSpeed / Math.max(1.0F, toolSpeed);
        return speed / hardness / (fabricTool ? 30.0F : 100.0F);
    }
}
