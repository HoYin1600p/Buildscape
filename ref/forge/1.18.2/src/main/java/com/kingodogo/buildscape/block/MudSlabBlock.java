package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MudSlabBlock extends SlabBlock {

    protected static final VoxelShape BOTTOM_SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            8.0D,
            16.0D
    );
    protected static final VoxelShape TOP_SHAPE = Block.box(
            0.0D,
            8.0D,
            0.0D,
            16.0D,
            15.0D,
            16.0D
    );
    protected static final VoxelShape DOUBLE_SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            15.0D,
            16.0D
    );

    public MudSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        SlabType type = state.getValue(TYPE);
        if (type == SlabType.TOP) {
            return TOP_SHAPE;
        } else if (type == SlabType.DOUBLE) {
            return DOUBLE_SHAPE;
        } else {
            return BOTTOM_SHAPE;
        }
    }
}
