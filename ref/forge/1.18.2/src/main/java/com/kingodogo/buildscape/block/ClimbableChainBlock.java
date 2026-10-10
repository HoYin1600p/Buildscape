package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClimbableChainBlock
        extends RotatedPillarBlock
        implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE_Y = Block.box(
            6.5D,
            0.0D,
            6.5D,
            9.5D,
            16.0D,
            9.5D
    );
    private static final VoxelShape SHAPE_X = Block.box(
            0.0D,
            6.5D,
            6.5D,
            16.0D,
            9.5D,
            9.5D
    );
    private static final VoxelShape SHAPE_Z = Block.box(
            6.5D,
            6.5D,
            0.0D,
            9.5D,
            9.5D,
            16.0D
    );

    private static final VoxelShape COLLISION_SHAPE_Y = Block.box(
            7.0D,
            0.0D,
            7.0D,
            9.0D,
            16.0D,
            9.0D
    );
    private static final VoxelShape COLLISION_SHAPE_X = Block.box(
            0.0D,
            6.5D,
            6.5D,
            16.0D,
            9.5D,
            9.5D
    );
    private static final VoxelShape COLLISION_SHAPE_Z = Block.box(
            6.5D,
            6.5D,
            0.0D,
            9.5D,
            9.5D,
            16.0D
    );

    public ClimbableChainBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context
                .getLevel()
                .getFluidState(context.getClickedPos());
        BlockState state = super.getStateForPlacement(context);
        return state != null
                ? state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                : null;
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        Direction.Axis axis = state.getValue(AXIS);
        switch (axis) {
            case X:
                return SHAPE_X;
            case Z:
                return SHAPE_Z;
            case Y:
            default:
                return SHAPE_Y;
        }
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        if (
                context instanceof
                        net.minecraft.world.phys.shapes.EntityCollisionContext entityContext
        ) {
            net.minecraft.world.entity.Entity entity = entityContext.getEntity();
            if (
                    entity != null &&
                            !(entity instanceof net.minecraft.world.entity.player.Player)
            ) {
                return Shapes.empty();
            }
        }

        Direction.Axis axis = state.getValue(AXIS);
        switch (axis) {
            case X:
                return COLLISION_SHAPE_X;
            case Z:
                return COLLISION_SHAPE_Z;
            case Y:
            default:
                return COLLISION_SHAPE_Y;
        }
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
    public boolean isLadder(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            LivingEntity entity
    ) {
        return (
                entity instanceof net.minecraft.world.entity.player.Player &&
                        state.getValue(AXIS) == Direction.Axis.Y
        );
    }

    @Override
    public boolean isPathfindable(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            net.minecraft.world.level.pathfinder.PathComputationType type
    ) {
        return true;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
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
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            Direction side
    ) {
        return (
                adjacentBlockState.is(this) ||
                        super.skipRendering(state, adjacentBlockState, side)
        );
    }
}
