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
        if (this.baseBlock == null || !com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isGlass(this.baseBlock)) {
            return null;
        }
        int color = state.getMapColor(level, pos).col;
        if (color == 0 || color == 16777215) {
            return null;
        }
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, net.minecraft.core.Direction side) {
        if (this.baseBlock != null && com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector.isGlass(this.baseBlock)) {
            net.minecraft.world.level.block.Block adjBlock = adjacentBlockState.getBlock();
            net.minecraft.world.level.block.Block pureAdjBlock = adjBlock;
            
            if (adjBlock instanceof ModSlabBlock other) {
                pureAdjBlock = other.getBaseBlock();
            } else if (adjBlock instanceof ModStairBlock otherStair) {
                pureAdjBlock = otherStair.getBaseBlock();
            } else if (adjBlock instanceof com.kingodogo.buildscape.variantengine.block.ExtShapeBlockInterface ext) {
                pureAdjBlock = ext.getBaseBlock();
            }

            if (pureAdjBlock != null && this.baseBlock != null && 
                pureAdjBlock.getRegistryName() != null && this.baseBlock.getRegistryName() != null &&
                pureAdjBlock.getRegistryName().equals(this.baseBlock.getRegistryName())) {
                return true;
            }
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }
}
