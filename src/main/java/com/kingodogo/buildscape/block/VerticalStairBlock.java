package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VerticalStairBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D),
            Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D)
    );
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D)
    );
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D)
    );
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
            Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D)
    );

    private final Block baseBlock;

    public VerticalStairBlock(Block baseBlock, BlockBehaviour.Properties properties) {
        super(properties);
        this.baseBlock = baseBlock;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    private boolean isGlassLike() {
        if (this.baseBlock == null) return false;
        String name = this.baseBlock.getClass().getSimpleName();
        return (name.contains("Glass") || name.contains("Transparent")) && this.baseBlock != Blocks.TINTED_GLASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public static VoxelShape getShapeForFacing(Direction facing) {
        return switch (facing) {
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    public static Direction getPlacementFacing(BlockPos pos, Direction clickedFace, net.minecraft.world.phys.Vec3 clickLocation) {
        double dx = clickLocation.x - pos.getX() - 0.5;
        double dz = clickLocation.z - pos.getZ() - 0.5;
        if (clickedFace.getAxis().isVertical()) {
            if (dx < 0) {
                return dz < 0 ? Direction.NORTH : Direction.WEST;
            }
            return dz < 0 ? Direction.EAST : Direction.SOUTH;
        }
        return switch (clickedFace) {
            case NORTH -> dx < 0 ? Direction.WEST : Direction.SOUTH;
            case SOUTH -> dx < 0 ? Direction.NORTH : Direction.EAST;
            case WEST -> dz < 0 ? Direction.EAST : Direction.SOUTH;
            case EAST -> dz < 0 ? Direction.NORTH : Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShapeForFacing(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        Direction facing = getPlacementFacing(context.getClickedPos(), context.getClickedFace(), context.getClickLocation());
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.isGlassLike() ? Shapes.empty() : super.getVisualShape(state, level, pos, context);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return this.isGlassLike() ? 1.0F : super.getShadeBrightness(state, level, pos);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !this.isGlassLike() && super.useShapeForLightOcclusion(state);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return (this.isGlassLike() && (adjacentBlockState.is(this) || (this.baseBlock != null && adjacentBlockState.is(this.baseBlock))))
                || super.skipRendering(state, adjacentBlockState, side);
    }
}
