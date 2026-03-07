package com.kingodogo.buildscape.variantengine.block;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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

/**
 * Vertical Slab Block with merging functionality (North, South, East, West, Double).
 */
public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock, ExtShapeBlockInterface {
    public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.create("type", VerticalSlabType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, 8);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 8, 16, 16, 16);
    protected static final VoxelShape EAST_SHAPE = Block.box(8, 0, 0, 16, 16, 16);
    protected static final VoxelShape WEST_SHAPE = Block.box(0, 0, 0, 8, 16, 16);

    private final Block baseBlock;

    public VerticalSlabBlock(Properties properties, Block baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, VerticalSlabType.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public Block getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.VERTICAL_SLAB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VerticalSlabType type = state.getValue(TYPE);
        switch (type) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case DOUBLE:
                return Shapes.block();
            default:
                return Shapes.block();
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState existingState = context.getLevel().getBlockState(blockPos);
        FluidState fluidState = context.getLevel().getFluidState(blockPos);

        if (existingState.is(this)) {
            VerticalSlabType existingType = existingState.getValue(TYPE);
            if (existingType != VerticalSlabType.DOUBLE) {
                // Determine if we should merge
                Direction dir = context.getClickedFace();
                if (shouldMerge(existingType, dir)) {
                    return existingState.setValue(TYPE, VerticalSlabType.DOUBLE).setValue(WATERLOGGED, false);
                }
            }
        }

        Direction.Axis axis = context.getHorizontalDirection().getAxis();
        double d = context.getClickLocation().get(axis) - blockPos.get(axis);

        VerticalSlabType type;
        switch (axis) {
            case X:
                type = d < 0.5 ? VerticalSlabType.WEST : VerticalSlabType.EAST;
                break;
            case Z:
                type = d < 0.5 ? VerticalSlabType.NORTH : VerticalSlabType.SOUTH;
                break;
            default:
                type = VerticalSlabType.NORTH;
                break;
        }

        return this.defaultBlockState()
                .setValue(TYPE, type)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack itemstack = context.getItemInHand();
        VerticalSlabType type = state.getValue(TYPE);
        if (type != VerticalSlabType.DOUBLE && itemstack.is(this.asItem())) {
            if (context.replacingClickedOnBlock()) {
                return shouldMerge(type, context.getClickedFace());
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean shouldMerge(VerticalSlabType type, Direction face) {
        if (type == VerticalSlabType.NORTH && face == Direction.SOUTH) return true;
        if (type == VerticalSlabType.SOUTH && face == Direction.NORTH) return true;
        if (type == VerticalSlabType.EAST && face == Direction.WEST) return true;
        return type == VerticalSlabType.WEST && face == Direction.EAST;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public enum VerticalSlabType implements StringRepresentable {
        NORTH("north"), SOUTH("south"), EAST("east"), WEST("west"), DOUBLE("double");
        private final String name;

        VerticalSlabType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
