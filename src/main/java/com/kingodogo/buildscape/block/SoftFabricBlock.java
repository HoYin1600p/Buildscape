package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SoftFabricBlock extends Block {
    public SoftFabricBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = 1.0F;

        if (tool.getItem() instanceof ShearsItem || tool.getItem() instanceof HoeItem) {
            if (tool.getItem() instanceof ShearsItem) {
                speedMultiplier = 5.0F;
            } else if (tool.getItem() instanceof net.minecraft.world.item.DiggerItem digger) {
                speedMultiplier = digger.getTier().getSpeed();
            } else {
                speedMultiplier = 2.0F;
            }

            int efficiencyLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getBlockEfficiency(player);
            if (efficiencyLevel > 0) {
                speedMultiplier += (float) (efficiencyLevel * efficiencyLevel + 1);
            }

            return speedMultiplier / destroySpeed / 30.0F;
        } else {
            // Hand/other tools: mine fast as well
            speedMultiplier = 2.5F;
            return speedMultiplier / destroySpeed / 100.0F;
        }
    }
}
