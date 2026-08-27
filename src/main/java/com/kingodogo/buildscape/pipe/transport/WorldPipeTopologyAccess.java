package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Real-world adapter implementing PipeTopologyAccess by querying Minecraft's Level / BlockGetter
 * and existing Hollow Steel Pipe blockstates.
 *
 * IMPORTANT DESIGN RULE:
 * isWaterSource() must ONLY detect EXTERNAL, EXPLICIT water sources:
 *   (a) WATERLOGGED=true blockstate (water bucket placed directly in this pipe)
 *   (b) Open endpoint touching a WORLD water SOURCE block
 *
 * It must NEVER read back from fluidType, pipeFlowState, or any other transport state.
 * Reading back from transport state would create a circular dependency:
 *   simulation writes state → state is re-read as source → all pipes become sources.
 */
public class WorldPipeTopologyAccess implements PipeTopologyAccess {

    private final BlockGetter level;

    public WorldPipeTopologyAccess(BlockGetter level) {
        this.level = level;
    }

    @Override
    public boolean isHollowPipe(BlockPos pos) {
        if (level == null || pos == null) return false;
        BlockState state = level.getBlockState(pos);
        return PipeFluidTransport.isHollowPipe(state);
    }

    @Override
    public boolean isConnected(BlockPos pos, Direction dir) {
        if (level == null || pos == null || dir == null) return false;
        return PipeFluidTransport.isTopologyConnected(level, pos, dir);
    }

    @Override
    public boolean isOpenEndpoint(BlockPos pos, Direction dir) {
        if (level == null || pos == null || dir == null) return false;
        BlockState state = level.getBlockState(pos);
        return PipeFluidTransport.isOpenEndpoint(state, dir);
    }

    @Override
    public BubbleColumnState getBubbleColumnBase(BlockPos pos) {
        return BubbleColumnHandler.detectBubbleColumnBase(level, pos);
    }

    /**
     * Checks if this pipe position is an AUTHORITATIVE WATER SOURCE.
     *
     * A pipe is a source if and only if:
     *   (a) Its blockstate has WATERLOGGED=true (water bucket was placed directly into this pipe), OR
     *   (b) It has an open endpoint that directly borders a world water SOURCE block.
     *
     * This method must NEVER read from:
     *   - be.getFluidType() (old fluid logging — causes circular source propagation)
     *   - be.getPipeFlowState() (transport state output — creates circular dependency)
     *   - getContainedFluid() (aggregates fluidType which is the root-cause bug)
     */
    @Override
    public boolean isWaterSource(BlockPos pos) {
        if (level == null || pos == null) return false;
        BlockState state = level.getBlockState(pos);

        // (a) Waterlogged blockstate: a water bucket was explicitly placed into this pipe.
        // This is the ONLY internal state that makes a pipe a legitimate source.
        if (state.hasProperty(HollowPipeBlock.WATERLOGGED) && state.getValue(HollowPipeBlock.WATERLOGGED)) {
            return true;
        }

        // (b) Open endpoint bordering an EXTERNAL world water SOURCE block.
        // The world water source feeds water into the pipe network through the open passage.
        // Pipe outflow is placed as flowing water, never as a source, and therefore
        // cannot satisfy the source check below.
        for (Direction dir : Direction.values()) {
            if (isOpenEndpoint(pos, dir)) {
                BlockPos neighborPos = pos.relative(dir);
                FluidState fs = level.getFluidState(neighborPos);
                if (fs.isSource() && fs.getType() == Fluids.WATER) {
                    return true;
                }
            }
        }

        return false;
    }
}
