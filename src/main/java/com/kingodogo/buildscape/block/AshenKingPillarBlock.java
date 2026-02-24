package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AshenKingPillarBlock extends PillarBlock {

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NS = Shapes.or(
            Block.box(0, 0, 2, 16, 9, 14),
            Block.box(1, 9, 3, 15, 12, 13)
    );
    private static final VoxelShape SHAPE_EW = Shapes.or(
            Block.box(2, 0, 0, 14, 9, 16),
            Block.box(3, 9, 1, 13, 12, 15)
    );

    public AshenKingPillarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(PART, PillarPart.SINGLE)
                        .setValue(WATERLOGGED, false)
                        .setValue(FACING, Direction.NORTH)
        );
    }


    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(PART, PillarPart.SINGLE)
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    @Override
    public VoxelShape getShape(
            BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return (facing == Direction.EAST || facing == Direction.WEST) ? SHAPE_EW : SHAPE_NS;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
