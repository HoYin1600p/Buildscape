package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;
public class ModWallBlock extends WallBlock {
    @SuppressWarnings("unused")
    private final Supplier<?> dropItem;
    public ModWallBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.dropItem = null;
    }
    public ModWallBlock(BlockBehaviour.Properties properties, Supplier<?> dropItem) {
        super(properties);
        this.dropItem = dropItem;
    }
}
