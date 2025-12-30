package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModTrapdoorBlock extends TrapDoorBlock {

    public ModTrapdoorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter reader,
            BlockPos pos
    ) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentState,
            Direction side
    ) {
        if (adjacentState.is(this)) {
            boolean stateOpen = state.getValue(TrapDoorBlock.OPEN);
            boolean adjacentOpen = adjacentState.getValue(TrapDoorBlock.OPEN);
            Half stateHalf = state.getValue(TrapDoorBlock.HALF);
            Half adjacentHalf = adjacentState.getValue(TrapDoorBlock.HALF);
            Direction stateFacing = state.getValue(TrapDoorBlock.FACING);
            Direction adjacentFacing = adjacentState.getValue(TrapDoorBlock.FACING);

            if (stateOpen == adjacentOpen && stateHalf == adjacentHalf) {
                if (!stateOpen) {
                    if (stateFacing == adjacentFacing) {
                        return true;
                    }
                } else {
                    if (stateFacing == side || adjacentFacing == side.getOpposite()) {
                        return true;
                    }
                }
            }
        }
        return super.skipRendering(state, adjacentState, side);
    }
}
