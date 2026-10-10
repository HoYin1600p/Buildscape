package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class IceCrystalBlock extends AmethystClusterBlock {
    public IceCrystalBlock(int size, int offset, BlockBehaviour.Properties properties) {
        super(size, offset, properties);
    }

    public IceCrystalBlock(BlockBehaviour.Properties properties) {
        super(7, 3, properties);
    }
}
