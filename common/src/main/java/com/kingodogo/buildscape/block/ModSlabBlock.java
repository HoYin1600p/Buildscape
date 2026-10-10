package com.kingodogo.buildscape.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
public class ModSlabBlock extends SlabBlock {
    private final Block baseBlock;
    public ModSlabBlock(Block baseBlock, BlockBehaviour.Properties properties) {
        super(properties);
        this.baseBlock = baseBlock;
    }
    public ModSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.baseBlock = null;
    }
    public Block getBaseBlock() {
        return this.baseBlock;
    }
    private boolean isGlassLike() {
        return com.kingodogo.buildscape.platform.Services.PLATFORM.isGlassBlock(this.baseBlock) && !this.isTintedGlassLike();
    }
    private boolean isTintedGlassLike() {
        return this.baseBlock == Blocks.TINTED_GLASS;
    }

    public boolean isRegularGlassLike() { return isGlassLike(); }
    public boolean isTintedBase() { return isTintedGlassLike(); }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !this.isGlassLike() && super.useShapeForLightOcclusion(state);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return (this.isGlassLike() && adjacentBlockState.is(this)) || super.skipRendering(state, adjacentBlockState, side);
    }
}
