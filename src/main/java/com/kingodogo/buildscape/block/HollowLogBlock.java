package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HollowLogBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape Y_NORTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape Y_SOUTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape Y_WEST  = Block.box(0, 0, 2, 2, 16, 14);
    private static final VoxelShape Y_EAST  = Block.box(14, 0, 2, 16, 16, 14);
    private static final VoxelShape Y_SHAPE = Shapes.or(Y_NORTH, Y_SOUTH, Y_WEST, Y_EAST);

    private static final VoxelShape X_NORTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape X_SOUTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape X_DOWN  = Block.box(0, 0, 2, 16, 2, 14);
    private static final VoxelShape X_UP    = Block.box(0, 14, 2, 16, 16, 14);
    private static final VoxelShape X_SHAPE = Shapes.or(X_NORTH, X_SOUTH, X_DOWN, X_UP);
    private static final VoxelShape X_SHAPE_SNEAK = Shapes.or(X_NORTH, X_SOUTH, X_DOWN);

    private static final VoxelShape Z_WEST  = Block.box(0, 0, 0, 2, 16, 16);
    private static final VoxelShape Z_EAST  = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape Z_DOWN  = Block.box(2, 0, 0, 14, 2, 16);
    private static final VoxelShape Z_UP    = Block.box(2, 14, 0, 14, 16, 16);
    private static final VoxelShape Z_SHAPE = Shapes.or(Z_WEST, Z_EAST, Z_DOWN, Z_UP);
    private static final VoxelShape Z_SHAPE_SNEAK = Shapes.or(Z_WEST, Z_EAST, Z_DOWN);

    public HollowLogBlock(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = state.getValue(AXIS);
        return switch (axis) {
            case X -> X_SHAPE;
            case Z -> Z_SHAPE;
            default -> Y_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = state.getValue(AXIS);
        if (context instanceof EntityCollisionContext entityContext) {
            net.minecraft.world.entity.Entity entity = entityContext.getEntity();
            if (entity instanceof net.minecraft.world.entity.LivingEntity living && living.isShiftKeyDown()) {
                return switch (axis) {
                    case X -> X_SHAPE_SNEAK;
                    case Z -> Z_SHAPE_SNEAK;
                    default -> Y_SHAPE;
                };
            }
        }
        return this.getShape(state, level, pos, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }
}
