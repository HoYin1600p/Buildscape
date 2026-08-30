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

/**
 * Abstract extensible base class for fluid transport through Hollow Steel Pipes.
 * Treats the existing pipe topology as the absolute authority.
 */
public abstract class PipeFluidTransport {

    /**
     * The fluid type managed by this transport instance (e.g. Fluids.WATER).
     */
    public abstract Fluid getFluidType();

    /**
     * Recalculates the connected pipe network starting from the given position.
     * Returns the set of BlockPos in the discovered connected pipe component.
     */
    public abstract java.util.Set<BlockPos> recalculateNetwork(Level level, BlockPos startPos);

    /**
     * Checks if a blockstate is a Hollow Pipe.
     */
    public static boolean isHollowPipe(BlockState state) {
        return state != null && state.getBlock() instanceof HollowPipeBlock;
    }

    /**
     * Checks if two Hollow Pipe blockstates are connected in direction dir based on topology properties.
     */
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

    /**
     * Checks if there is a valid connected topology passage from fromPos in direction dir.
     */
    public static boolean isTopologyConnected(BlockGetter level, BlockPos fromPos, Direction dir) {
        if (level == null || fromPos == null || dir == null) return false;
        BlockState fromState = level.getBlockState(fromPos);
        BlockState toState = level.getBlockState(fromPos.relative(dir));
        return arePipesConnected(fromState, dir, toState);
    }

    /**
     * Returns a list of all directions in which this pipe blockstate has an active connected branch.
     */
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

    /**
     * Checks if direction dir is an open endpoint on the given pipe blockstate.
     */
    public static boolean isOpenEndpoint(BlockState state, Direction dir) {
        if (!isHollowPipe(state) || dir == null) return false;
        return HollowPipeBlock.isOpenEndpoint(state, dir);
    }
}
