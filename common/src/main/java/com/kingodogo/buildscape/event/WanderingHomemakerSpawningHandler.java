package com.kingodogo.buildscape.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class WanderingHomemakerSpawningHandler {
    private WanderingHomemakerSpawningHandler() {}

    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state, Player player) {
        WanderingHomemakerSpawningLogic.onBlockPlaced(level, pos, state, player);
    }
}
