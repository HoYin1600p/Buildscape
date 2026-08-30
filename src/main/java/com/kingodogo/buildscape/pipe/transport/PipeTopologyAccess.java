package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

/**
 * Topology abstraction interface allowing the fluid transport simulation to query
 * connection passages and endpoints without direct coupling to Minecraft internal registry lifecycles.
 */
public interface PipeTopologyAccess {

    /**
     * Checks if a hollow pipe block exists at the given position.
     */
    boolean isHollowPipe(BlockPos pos);

    /**
     * Checks if an internal 1x1 passage is connected from pos in the given direction.
     */
    boolean isConnected(BlockPos pos, Direction dir);

    /**
     * Checks if the given direction is an open endpoint at pos.
     */
    boolean isOpenEndpoint(BlockPos pos, Direction dir);

    /**
     * Detects if an active bubble column base (Soul Sand or Magma Block) is present below pos.
     */
    BubbleColumnState getBubbleColumnBase(BlockPos pos);

    /**
     * Checks if pos is a water source (either waterlogged or intaking from external world water).
     */
    boolean isWaterSource(BlockPos pos);

    /**
     * Returns the horizontal vanilla-flow distance at which water enters this
     * pipe. A bucket-waterlogged pipe is the source itself (0); a pipe fed by
     * an adjacent world-water source is the first flowing block (1).
     *
     * The default preserves the source semantics used by lightweight topology
     * implementations and test fixtures.
     */
    default int getInitialWaterFlowDistance(BlockPos pos) {
        return 0;
    }

    /**
     * Face through which an external world-water source enters this pipe.
     * Bucket-filled pipes have no external inlet, so the default is null.
     */
    @Nullable
    default Direction getSourceInflowDirection(BlockPos pos) {
        return null;
    }
}
