package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.AbstractGlassBlock;
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

    private boolean isGlassLike() {
        return this.baseBlock instanceof AbstractGlassBlock;
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.core.BlockPos beaconPos) {
        return null;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos) {
        return this.isGlassLike() || super.propagatesSkylightDown(state, level, pos);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !this.isGlassLike() && super.useShapeForLightOcclusion(state);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, net.minecraft.core.Direction side) {
        return (this.isGlassLike() && adjacentBlockState.is(this)) || super.skipRendering(state, adjacentBlockState, side);
    }
}
