package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.Block;
public interface IBlockFactory {
    String versionCluster();
    Block createBlock(BlockDefinition def);
}
