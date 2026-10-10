package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.level.block.Block;
public final class BlockFactory {

    private BlockFactory() {}
    public static Block createBlock(BlockDefinition def) {
        return Services.BLOCK_FACTORY.createBlock(def);
    }
}
