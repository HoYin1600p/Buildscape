package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;
public class ModBlock extends Block {
    @SuppressWarnings("unused")
    private final Supplier<?> dropItem;
    public ModBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.dropItem = null;
    }
    public ModBlock(BlockBehaviour.Properties properties, Supplier<?> dropItem) {
        super(properties);
        this.dropItem = dropItem;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel = com.kingodogo.buildscape.platform.Services.PLATFORM.getEfficiencyLevel(player.getMainHandItem());
        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0 ? efficiencyLevel * efficiencyLevel + 1 : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state) ? 30.0F : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
