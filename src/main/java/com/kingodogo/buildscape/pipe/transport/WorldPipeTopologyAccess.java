package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

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
        if (state.getBlock() instanceof HollowLogBlock) {
            return HollowLogBlock.isOpenEnd(state, dir) && !isConnected(pos, dir);
        }
        return PipeFluidTransport.isOpenEndpoint(state, dir);
    }

    @Override
    public BubbleColumnState getBubbleColumnBase(BlockPos pos) {
        return BubbleColumnHandler.detectBubbleColumnBase(level, pos);
    }

    @Override
    public String getSourceFluidId(BlockPos pos) {
        if (level == null || pos == null) return null;
        BlockState state = level.getBlockState(pos);
        Fluid contained = HollowPipeBlock.getSourceFluid(state, level.getBlockEntity(pos));
        if (isTransportFluid(contained)) return fluidId(contained);
        Fluid external = getExternalSourceFluid(pos);
        return external == Fluids.EMPTY ? null : fluidId(external);
    }

    @Override
    public int getInitialWaterFlowDistance(BlockPos pos) {
        if (level == null || pos == null) return 0;
        BlockState state = level.getBlockState(pos);
        if (isTransportFluid(HollowPipeBlock.getSourceFluid(state, level.getBlockEntity(pos)))) {
            return 0;
        }
        return getExternalSourceFluid(pos) != Fluids.EMPTY ? 1 : 0;
    }

    @Override
    public Direction getSourceInflowDirection(BlockPos pos) {
        if (level == null || pos == null) return null;
        String sourceFluidId = getSourceFluidId(pos);
        if (sourceFluidId == null) return null;
        BlockState selfState = level.getBlockState(pos);
        if (isTransportFluid(HollowPipeBlock.getSourceFluid(selfState, level.getBlockEntity(pos)))) {
            return null;
        }
        for (Direction dir : Direction.values()) {
            if (!isOpenEndpoint(pos, dir)) continue;
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (PipeFluidTransport.isHollowPipe(neighborState)) continue;
            FluidState fluid = level.getFluidState(neighborPos);
            if (fluid.isSource() && sourceFluidId.equals(fluidId(fluid.getType()))) {
                return dir;
            }
        }
        return null;
    }

    private Fluid getExternalSourceFluid(BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (!isOpenEndpoint(pos, dir)) continue;
            BlockState neighborState = level.getBlockState(pos.relative(dir));
            if (PipeFluidTransport.isHollowPipe(neighborState)) continue;
            FluidState fluid = neighborState.getFluidState();
            if (fluid.isSource() && isTransportFluid(fluid.getType())) return fluid.getType();
        }
        return Fluids.EMPTY;
    }

    public static boolean isTransportFluid(@Nullable Fluid fluid) {
        return fluid != null && (fluid.isSame(Fluids.WATER) || fluid.isSame(Fluids.LAVA)
                || fluid.isSame(ModFluids.EXPERIENCE_STILL.get()));
    }

    public static String fluidId(Fluid fluid) {
        Fluid canonical = fluid.isSame(Fluids.WATER) ? Fluids.WATER
                : fluid.isSame(Fluids.LAVA) ? Fluids.LAVA
                : fluid.isSame(ModFluids.EXPERIENCE_STILL.get()) ? ModFluids.EXPERIENCE_STILL.get() : fluid;
        ResourceLocation key = ForgeRegistries.FLUIDS.getKey(canonical);
        return key == null ? "" : key.toString();
    }

    public static Fluid fluidById(String fluidId) {
        ResourceLocation key = ResourceLocation.tryParse(fluidId);
        Fluid fluid = key == null ? null : ForgeRegistries.FLUIDS.getValue(key);
        return fluid == null ? Fluids.EMPTY : fluid;
    }
}
