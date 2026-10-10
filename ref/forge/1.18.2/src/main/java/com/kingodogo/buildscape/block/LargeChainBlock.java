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

public class LargeChainBlock
        extends RotatedPillarBlock
        implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE_Y = Block.box(
            4.0D,
            0.0D,
            4.0D,
            12.0D,
            16.0D,
            12.0D
    );
    private static final VoxelShape SHAPE_X = Block.box(
            0.0D,
            4.0D,
            4.0D,
            16.0D,
            12.0D,
            12.0D
    );
    private static final VoxelShape SHAPE_Z = Block.box(
            4.0D,
            4.0D,
            0.0D,
            12.0D,
            12.0D,
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
            4.0D,
            4.0D,
            16.0D,
            12.0D,
            12.0D
    );
    private static final VoxelShape COLLISION_SHAPE_Z = Block.box(
            4.0D,
            4.0D,
            0.0D,
            12.0D,
            12.0D,
            16.0D
    );

    public LargeChainBlock(BlockBehaviour.Properties properties) {
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
        if (state != null) {
            state = state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }

        Direction clickedFace = context.getClickedFace();

        if (clickedFace == Direction.DOWN) {
            BlockPos placePos = context.getClickedPos().above();
            BlockState aboveState = context
                    .getLevel()
                    .getBlockState(context.getClickedPos());

            if (
                    Block.isFaceFull(
                            aboveState.getCollisionShape(
                                    context.getLevel(),
                                    context.getClickedPos()
                            ),
                            Direction.DOWN
                    )
            ) {
                if (context.getLevel().getBlockState(placePos).canBeReplaced(context)) {
                    return (state != null ? state : this.defaultBlockState()).setValue(
                            AXIS,
                            Direction.Axis.Y
                    );
                }
            }
        }

        return state;
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

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction.Axis axis = state.getValue(AXIS);

        if (axis == Direction.Axis.Y) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            boolean canHangFromBelow =
                    Block.isFaceFull(
                            belowState.getCollisionShape(level, below),
                            Direction.UP
                    ) ||
                            belowState.getBlock() instanceof
                                    net.minecraft.world.level.block.ChainBlock ||
                            belowState.getBlock() instanceof ClimbableChainBlock ||
                            belowState.getBlock() instanceof LargeChainBlock;

            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            boolean canHangFromAbove =
                    Block.isFaceFull(
                            aboveState.getCollisionShape(level, above),
                            Direction.DOWN
                    ) ||
                            aboveState.getBlock() instanceof
                                    net.minecraft.world.level.block.ChainBlock ||
                            aboveState.getBlock() instanceof ClimbableChainBlock ||
                            aboveState.getBlock() instanceof LargeChainBlock;

            return canHangFromBelow || canHangFromAbove;
        } else {
            Direction dir1 = Direction.get(Direction.AxisDirection.POSITIVE, axis);
            Direction dir2 = Direction.get(Direction.AxisDirection.NEGATIVE, axis);

            BlockPos pos1 = pos.relative(dir1);
            BlockPos pos2 = pos.relative(dir2);
            BlockState state1 = level.getBlockState(pos1);
            BlockState state2 = level.getBlockState(pos2);

            boolean side1Valid =
                    Block.isFaceFull(
                            state1.getCollisionShape(level, pos1),
                            dir1.getOpposite()
                    ) ||
                            state1.getBlock() instanceof
                                    net.minecraft.world.level.block.ChainBlock ||
                            state1.getBlock() instanceof ClimbableChainBlock ||
                            state1.getBlock() instanceof LargeChainBlock;

            boolean side2Valid =
                    Block.isFaceFull(
                            state2.getCollisionShape(level, pos2),
                            dir2.getOpposite()
                    ) ||
                            state2.getBlock() instanceof
                                    net.minecraft.world.level.block.ChainBlock ||
                            state2.getBlock() instanceof ClimbableChainBlock ||
                            state2.getBlock() instanceof LargeChainBlock;

            return side1Valid || side2Valid;
        }
    }
}
