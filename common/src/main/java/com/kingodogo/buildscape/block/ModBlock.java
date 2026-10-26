package com.kingodogo.buildscape.block;

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
}
