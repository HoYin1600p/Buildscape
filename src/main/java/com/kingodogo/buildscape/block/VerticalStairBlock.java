package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VerticalStairBlock extends StairBlock {
    private static final VoxelShape NORTH_BOTTOM = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D),
            Block.box(0.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D));
    private static final VoxelShape NORTH_TOP = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D),
            Block.box(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D));
    private static final VoxelShape SOUTH_BOTTOM = Shapes.or(
            Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D));
    private static final VoxelShape SOUTH_TOP = Shapes.or(
            Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D));
    private static final VoxelShape WEST_BOTTOM = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D));
    private static final VoxelShape WEST_TOP = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
            Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    private static final VoxelShape EAST_BOTTOM = Shapes.or(
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 16.0D));
    private static final VoxelShape EAST_TOP = Shapes.or(
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D));

    private final Block baseBlock;

    public VerticalStairBlock(BlockState baseState, BlockBehaviour.Properties properties) {
        super(baseState, properties);
        this.baseBlock = baseState.getBlock();
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(SHAPE, StairsShape.STRAIGHT)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        Direction facing = face.getAxis().isHorizontal()
                ? face
                : context.getHorizontalDirection().getOpposite();
        double y = context.getClickLocation().y - context.getClickedPos().getY();
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(HALF, y > 0.5D ? Half.TOP : Half.BOTTOM)
                .setValue(SHAPE, StairsShape.STRAIGHT)
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(SHAPE, StairsShape.STRAIGHT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean top = state.getValue(HALF) == Half.TOP;
        return switch (state.getValue(FACING)) {
            case EAST -> top ? EAST_TOP : EAST_BOTTOM;
            case SOUTH -> top ? SOUTH_TOP : SOUTH_BOTTOM;
            case WEST -> top ? WEST_TOP : WEST_BOTTOM;
            default -> top ? NORTH_TOP : NORTH_BOTTOM;
        };
    }
}
