package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VerticalSlabBlock extends SlabBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    private static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    @SuppressWarnings("unused")
    private final Block baseBlock;

    public VerticalSlabBlock(Block baseBlock, BlockBehaviour.Properties properties) {
        super(properties);
        this.baseBlock = baseBlock;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Z)
                .setValue(TYPE, SlabType.BOTTOM)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    private boolean isGlassLike() {
        return this.baseBlock instanceof AbstractGlassBlock && !this.isTintedGlassLike();
    }

    private boolean isTintedGlassLike() {
        return this.baseBlock == Blocks.TINTED_GLASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        SlabType type = state.getValue(TYPE);
        if (type == SlabType.DOUBLE) {
            return super.getShape(state, level, pos, context);
        }

        Direction.Axis axis = state.getValue(AXIS);
        if (axis == Direction.Axis.X) {
            return type == SlabType.TOP ? EAST : WEST;
        }
        return type == SlabType.TOP ? SOUTH : NORTH;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        if (state.is(this)) {
            return state.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, Boolean.FALSE);
        }

        FluidState fluid = context.getLevel().getFluidState(pos);
        Direction direction = this.getDirectionForPlacement(context);

        return this.defaultBlockState()
                .setValue(AXIS, direction.getAxis())
                .setValue(TYPE, this.typeFromDirection(direction))
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        SlabType type = state.getValue(TYPE);
        if (type == SlabType.DOUBLE || !stack.is(this.asItem())) {
            return false;
        }

        Direction slabDirection = this.directionFromState(state);
        Direction clickedFace = context.getClickedFace();
        if (context.replacingClickedOnBlock()) {
            return clickedFace == slabDirection && this.getDirectionForPlacement(context) == slabDirection;
        }
        return clickedFace.getAxis() != slabDirection.getAxis();
    }

    private Direction getDirectionForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        if (direction.getAxis().isHorizontal()) {
            return direction;
        }

        BlockPos pos = context.getClickedPos();
        Vec3 hit = context.getClickLocation()
                .subtract(pos.getX(), pos.getY(), pos.getZ())
                .subtract(0.5D, 0.0D, 0.5D);
        double angle = Math.atan2(hit.x, hit.z) * -180.0D / Math.PI;
        return Direction.fromYRot(angle).getOpposite();
    }

    private Direction directionFromState(BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        SlabType type = state.getValue(TYPE);
        Direction.AxisDirection axisDirection = type == SlabType.TOP
                ? Direction.AxisDirection.NEGATIVE
                : Direction.AxisDirection.POSITIVE;
        return Direction.fromAxisAndDirection(axis, axisDirection);
    }

    private SlabType typeFromDirection(Direction direction) {
        return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE
                ? SlabType.BOTTOM
                : SlabType.TOP;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return !this.isTintedGlassLike() && (this.isGlassLike() || super.propagatesSkylightDown(state, level, pos));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (this.isTintedGlassLike()) {
            return 15;
        }

        return super.getLightBlock(state, level, pos);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !this.isGlassLike() && super.useShapeForLightOcclusion(state);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return (this.isGlassLike() && adjacentBlockState.is(this)) || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }
}
