package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.Block;
public interface IBlockFactory {
    Block createBlock(BlockDefinition def);
}
