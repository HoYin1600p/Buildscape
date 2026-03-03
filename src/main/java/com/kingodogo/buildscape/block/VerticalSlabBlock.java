package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class VerticalSlabBlock extends SlabBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    // We reuse SlabType. TOP/BOTTOM are somewhat meaningless names here but:
    // BOTTOM = Single Slab
    // DOUBLE = Double Slab (Full Block)
    // TOP = Unused or treated same as BOTTOM (Single) for now, or maybe we don't use it.
    // Actually, to keep it simple and compatible with the rendering logic which expects TYPE:
    public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;

    protected static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_SHAPE = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);

    private final Block parentBlock;

    public VerticalSlabBlock(Properties properties, Block parentBlock) {
        super(properties);
        this.parentBlock = parentBlock;
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, false));
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(TYPE) == SlabType.DOUBLE) {
            return Shapes.block();
        }
        switch (state.getValue(FACING)) {
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return NORTH_SHAPE;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState existingState = context.getLevel().getBlockState(pos);
        if (existingState.is(this)) {
            if (context.replacingClickedOnBlock()) {
                return existingState.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false);
            }
            return null;
        }

        FluidState fluidState = context.getLevel().getFluidState(pos);
        Direction facing = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, facing).setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public float getDestroyProgress(BlockState state, net.minecraft.world.entity.player.Player player, BlockGetter level, BlockPos pos) {
        // Delegate to parent block to automatically handle correct tool speed and tags
        return parentBlock.defaultBlockState().getDestroyProgress(player, level, pos);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        return parentBlock.defaultBlockState().canHarvestBlock(level, pos, player);
    }



    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        ItemStack itemstack = useContext.getItemInHand();
        SlabType slabtype = state.getValue(TYPE);
        if (slabtype != SlabType.DOUBLE && itemstack.getItem() == this.asItem()) {
            if (useContext.replacingClickedOnBlock()) {
                boolean flag = useContext.getClickLocation().y - (double) useContext.getClickedPos().getY() > 0.5D;
                Direction direction = useContext.getClickedFace();
                Direction facing = state.getValue(FACING);

                // Allow replacing (merging) only if clicking on the correct face
                if (facing == Direction.NORTH) {
                    // North slab occupies 0-8 Z. Empty space is 8-16 Z (South).
                    return direction == Direction.SOUTH;
                } else if (facing == Direction.SOUTH) {
                    return direction == Direction.NORTH;
                } else if (facing == Direction.WEST) {
                    return direction == Direction.EAST;
                } else if (facing == Direction.EAST) {
                    return direction == Direction.WEST;
                }

                // If not clicking the specific merge face, treat as full block (don't replace) -> places adjacent
                return false;
            } else {
                return true;
            }
        } else {
            return super.canBeReplaced(state, useContext);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
}
