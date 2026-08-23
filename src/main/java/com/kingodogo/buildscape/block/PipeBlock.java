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

    private BlockState updateConnections(BlockGetter level, BlockPos pos, BlockState state) {
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
        if (neighbor.getBlock() instanceof PipeBlock || neighbor.getBlock() instanceof HollowPipeBlock) {
            return neighbor.getValue(AXIS) == state.getValue(AXIS);
        }
        return false;
    }
}
