package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

public abstract class PipeFluidTransport {

    public abstract Fluid getFluidType();

    public abstract java.util.Set<BlockPos> recalculateNetwork(Level level, BlockPos startPos);

    public static boolean isHollowPipe(BlockState state) {
        return state != null && state.getBlock() instanceof HollowPipeBlock;
    }

    public static boolean arePipesConnected(BlockState stateFrom, Direction dir, BlockState stateTo) {
        if (!isHollowPipe(stateFrom) || !isHollowPipe(stateTo) || dir == null) {
            return false;
        }
        BooleanProperty propFrom = HollowPipeBlock.getPropertyForDirection(dir);
        BooleanProperty propTo = HollowPipeBlock.getPropertyForDirection(dir.getOpposite());

        boolean fromConnected = stateFrom.hasProperty(propFrom) && stateFrom.getValue(propFrom);
        boolean toConnected = stateTo.hasProperty(propTo) && stateTo.getValue(propTo);

        return fromConnected && toConnected;
    }

    public static boolean isTopologyConnected(BlockGetter level, BlockPos fromPos, Direction dir) {
        if (level == null || fromPos == null || dir == null) return false;
        BlockState fromState = level.getBlockState(fromPos);
        BlockState toState = level.getBlockState(fromPos.relative(dir));
        return arePipesConnected(fromState, dir, toState);
    }

    public static List<Direction> getConnectedDirections(BlockState state) {
        List<Direction> dirs = new ArrayList<>(6);
        if (!isHollowPipe(state)) return dirs;

        for (Direction dir : Direction.values()) {
            BooleanProperty prop = HollowPipeBlock.getPropertyForDirection(dir);
            if (state.hasProperty(prop) && state.getValue(prop)) {
                dirs.add(dir);
            }
        }
        return dirs;
    }

    public static boolean isOpenEndpoint(BlockState state, Direction dir) {
        if (!isHollowPipe(state) || dir == null) return false;
        return HollowPipeBlock.isOpenEndpoint(state, dir);
    }
}
