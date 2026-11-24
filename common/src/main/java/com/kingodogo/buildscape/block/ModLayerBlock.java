package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ModLayerBlock extends SnowLayerBlock {
    public ModLayerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        if (!belowState.is(Blocks.ICE) && !belowState.is(Blocks.PACKED_ICE) && !belowState.is(Blocks.BARRIER)) {
            if (!belowState.is(Blocks.HONEY_BLOCK) && !belowState.is(Blocks.SOUL_SAND)) {
                return Block.isFaceFull(belowState.getCollisionShape(level, pos.below()), Direction.UP)
                        || (belowState.hasProperty(LAYERS) && belowState.getValue(LAYERS) == 8);
            } else {
                return true;
            }
        }
        return false;
    }
}
