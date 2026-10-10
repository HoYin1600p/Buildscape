package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.registries.ForgeRegistries;

public class PipeBlock extends RotatedPillarBlock {
    public static final BooleanProperty CONNECTED_NEG = BooleanProperty.create("connected_neg");
    public static final BooleanProperty CONNECTED_POS = BooleanProperty.create("connected_pos");

    public PipeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(CONNECTED_NEG, false)
                .setValue(CONNECTED_POS, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED_NEG, CONNECTED_POS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return updateConnections(context.getLevel(), context.getClickedPos(), state);
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        return updateConnections(level, pos, updated);
    }

    public BlockState updateConnections(BlockGetter level, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        Direction negDir;
        Direction posDir;
        switch (axis) {
            case X:
                negDir = Direction.WEST;
                posDir = Direction.EAST;
                break;
            case Z:
                negDir = Direction.NORTH;
                posDir = Direction.SOUTH;
                break;
            default:
                negDir = Direction.DOWN;
                posDir = Direction.UP;
                break;
        }

        BlockState negState = level.getBlockState(pos.relative(negDir));
        BlockState posState = level.getBlockState(pos.relative(posDir));

        boolean connectNeg = canConnect(state, negState);
        boolean connectPos = canConnect(state, posState);

        return state.setValue(CONNECTED_NEG, connectNeg).setValue(CONNECTED_POS, connectPos);
    }

    protected boolean canConnect(BlockState state, BlockState neighbor) {
        if (neighbor.getBlock() instanceof PipeBlock) {
            return neighbor.getValue(AXIS) == state.getValue(AXIS);
        }
        if (neighbor.getBlock() instanceof HollowPipeBlock) {
            return true;
        }
        return false;
    }

    @Override
    public void setPlacedBy(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            notifyNeighbors(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, net.minecraft.world.level.Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            if (!level.isClientSide) {
                notifyNeighbors(level, pos);
            }
        } else {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    private void notifyNeighbors(net.minecraft.world.level.Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof HollowPipeBlock hollowPipe) {
                BlockState updated = hollowPipe.updateConnections(level, neighborPos, neighborState);
                if (updated != neighborState) {
                    level.setBlock(neighborPos, updated, 3);
                }
            } else if (neighborState.getBlock() instanceof PipeBlock pipeBlock) {
                BlockState updated = pipeBlock.updateConnections(level, neighborPos, neighborState);
                if (updated != neighborState) {
                    level.setBlock(neighborPos, updated, 3);
                }
            }
        }
    }
}
