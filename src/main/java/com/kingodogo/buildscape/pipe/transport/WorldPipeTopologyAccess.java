package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

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

    @Override
    public boolean isWaterSource(BlockPos pos) {
        if (level == null || pos == null) return false;
        BlockState state = level.getBlockState(pos);

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
