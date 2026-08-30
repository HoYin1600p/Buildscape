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
     *   (b) It has an open endpoint that directly borders an EXTERNAL world water SOURCE block.
     *
     * Pipe outflow is placed as flowing water, never as a source, ensuring outflows do not become false sources.
     * WATER_LEVEL > 0 is explicitly excluded: those pipes carry BFS-transported flowing water and must not be
     * counted as real sources or the distance limit would be broken.
     */
    @Override
    public boolean isWaterSource(BlockPos pos) {
        if (level == null || pos == null) return false;
        BlockState state = level.getBlockState(pos);

        // (a) Waterlogged blockstate: a water bucket was explicitly placed into this pipe.
        if (state.hasProperty(HollowPipeBlock.WATERLOGGED) && state.getValue(HollowPipeBlock.WATERLOGGED)) {
            return true;
        }

        return hasExternalWaterSource(pos);
    }

    @Override
    public int getInitialWaterFlowDistance(BlockPos pos) {
        if (level == null || pos == null) return 0;
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(HollowPipeBlock.WATERLOGGED) && state.getValue(HollowPipeBlock.WATERLOGGED)) {
            return 0;
        }
        // The intake pipe is itself vanilla's first flowing-water block.
        return hasExternalWaterSource(pos) ? 1 : 0;
    }

    @Override
    public Direction getSourceInflowDirection(BlockPos pos) {
        if (level == null || pos == null) return null;
        for (Direction dir : Direction.values()) {
            if (!isOpenEndpoint(pos, dir)) continue;
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof HollowPipeBlock) continue;
            FluidState fluid = level.getFluidState(neighborPos);
            if (fluid.isSource() && fluid.getType() == Fluids.WATER) {
                return dir;
            }
        }
        return null;
    }

    private boolean hasExternalWaterSource(BlockPos pos) {
        return getSourceInflowDirection(pos) != null;
    }
}
