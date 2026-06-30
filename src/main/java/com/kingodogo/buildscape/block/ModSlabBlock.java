package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ModSlabBlock extends SlabBlock {

    private final Block baseBlock;

    public ModSlabBlock(
            Block baseBlock,
            BlockBehaviour.Properties properties
    ) {
        super(properties);
        this.baseBlock = baseBlock;
    }

    // Secondary constructor for non-glass slabs using only properties
    public ModSlabBlock(
            BlockBehaviour.Properties properties
    ) {
        super(properties);
        this.baseBlock = null;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.core.BlockPos beaconPos) {
        return null;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, net.minecraft.core.Direction side) {
        return super.skipRendering(state, adjacentBlockState, side);
    }
}
