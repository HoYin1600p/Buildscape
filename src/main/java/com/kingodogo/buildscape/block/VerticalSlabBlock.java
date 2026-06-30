package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
        Direction clickedFace = context.getClickedFace();
        Direction.Axis axis;
        SlabType type;

        if (clickedFace.getAxis().isHorizontal()) {
            axis = clickedFace.getAxis();
            type = clickedFace.getAxisDirection() == Direction.AxisDirection.POSITIVE
                    ? SlabType.TOP
                    : SlabType.BOTTOM;
        } else {
            Vec3 hit = context.getClickLocation();
            double x = hit.x - pos.getX();
            double z = hit.z - pos.getZ();
            double xDistance = Math.abs(x - 0.5D);
            double zDistance = Math.abs(z - 0.5D);
            if (xDistance > zDistance) {
                axis = Direction.Axis.X;
                type = x > 0.5D ? SlabType.TOP : SlabType.BOTTOM;
            } else {
                axis = Direction.Axis.Z;
                type = z > 0.5D ? SlabType.TOP : SlabType.BOTTOM;
            }
        }

        return this.defaultBlockState()
                .setValue(AXIS, axis)
                .setValue(TYPE, type)
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        SlabType type = state.getValue(TYPE);
        if (type == SlabType.DOUBLE || !stack.is(this.asItem())) {
            return false;
        }
        if (!context.replacingClickedOnBlock()) {
            return true;
        }

        Direction face = context.getClickedFace();
        Direction.Axis axis = state.getValue(AXIS);
        if (face.getAxis().isVertical()) {
            return true;
        }
        if (face.getAxis() != axis) {
            return false;
        }
        return face.getAxisDirection() == Direction.AxisDirection.POSITIVE
                ? type == SlabType.BOTTOM
                : type == SlabType.TOP;
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
