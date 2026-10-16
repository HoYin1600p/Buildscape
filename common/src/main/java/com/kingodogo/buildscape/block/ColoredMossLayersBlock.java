package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;
public class ColoredMossLayersBlock extends MossLayersBlock {

    public ColoredMossLayersBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public ColoredMossLayersBlock(BlockBehaviour.Properties properties, Supplier<Block> fullBlockSupplier) {
        super(properties, fullBlockSupplier);
    }
}
