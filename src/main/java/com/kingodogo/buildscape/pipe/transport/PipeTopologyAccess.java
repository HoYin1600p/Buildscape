package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

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
}
