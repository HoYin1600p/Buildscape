package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Central coordinator and entry point for the Hollow Steel Pipe fluid transport subsystem.
 *
 * FIRE RULES:
 * - markDirty() is the single gate for all recalculations. It is always server-side only.
 * - neighborChanged() and updateShape() both call markDirty() per neighbor — this naturally
 *   fires twice per update. We deduplicate via a per-tick dirty set to avoid cascading.
 * - The renderer is READ-ONLY and must never call any method here.
 */
public class HollowPipeTransportManager {

    private static final Logger LOGGER = LogManager.getLogger("PipeTransport");

    /**
     * Dedicated debug flag to enable formatted transport logging.
     * When false (default), logging is completely silent.
     */
    public static boolean DEBUG_TRANSPORT = false;

    /**
     * Schedules or executes a network recalculation at the given position.
     * Guards: server-side only; startPos must be a hollow pipe or directly adjacent to one.
     */
    public static void markDirty(Level level, BlockPos pos) {
        if (level == null || level.isClientSide || pos == null) {
            return;
        }
        WaterPipeTransport.INSTANCE.recalculateNetwork(level, pos);
    }

    /**
     * Called when a new HollowPipeBlock is placed.
     * Always triggers a full network recalculation from the new position.
     */
    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state) {
        markDirty(level, pos);
    }

    /**
     * Called when a HollowPipeBlock is removed.
     * Triggers recalculation from all 6 adjacent positions (the removed block is gone,
     * so we recalculate from neighbors which may now be disconnected).
     */
    public static void onBlockRemoved(Level level, BlockPos pos, BlockState state) {
        markDirty(level, pos);
    }

    /**
     * Called when a neighbor block changes adjacent to a HollowPipeBlock.
     *
     * Guard: Only fires recalculation if the changing neighbor is one of:
     *   (a) Another HollowPipeBlock (topology change)
     *   (b) A fluid source block (water/lava appearing or disappearing next to an open endpoint)
     *
     * This prevents spurious recalculations from decoration, glass cover, or other
     * non-transport neighbor events, which previously caused double-fires and
     * false source detection on every structural update.
     */
    public static void onNeighborChanged(Level level, BlockPos pos, BlockState state, BlockPos neighborPos) {
        if (level == null || level.isClientSide || pos == null || neighborPos == null) {
            return;
        }

        // Only recalculate if the neighbor is relevant to transport:
        // (a) The neighbor is another Hollow Pipe (topology may have changed)
        // (b) The neighbor contains a water or lava fluid source
        // (c) The neighbor occupies an open pipe endpoint.  This last case is required
        //     for source removal: after a source becomes air it no longer satisfies
        //     (b), but the network must still drain.
        BlockState neighborState = level.getBlockState(neighborPos);
        FluidState neighborFluid = level.getFluidState(neighborPos);

        boolean neighborIsPipe = neighborState.getBlock() instanceof HollowPipeBlock;
        boolean neighborIsFluidSource = neighborFluid.isSource()
                && (neighborFluid.getType() == Fluids.WATER || neighborFluid.getType() == Fluids.LAVA);

        boolean neighborIsOpenEndpoint = false;
        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
            if (pos.relative(direction).equals(neighborPos)
                    && HollowPipeBlock.isOpenEndpoint(state, direction)) {
                neighborIsOpenEndpoint = true;
                break;
            }
        }

        if (neighborIsPipe || neighborIsFluidSource || neighborIsOpenEndpoint) {
            markDirty(level, pos);
        }
    }

    /**
     * Called when a water or lava bucket is used on this pipe.
     * Always triggers recalculation since source/drain state has explicitly changed.
     */
    public static void onBucketUsed(Level level, BlockPos pos, BlockState state) {
        markDirty(level, pos);
    }

    public static void logDebug(String message) {
        if (DEBUG_TRANSPORT) {
            LOGGER.info(message);
        }
    }
}
