package com.kingodogo.buildscape.mixinsupport;

import com.kingodogo.buildscape.client.renderer.PipeSpillVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import java.util.List;

/** Bridges the fluid renderer's layer output to the outlet geometry consumer. */
public final class PipeSpillOutput implements FluidRenderer.Output {
    private final FluidRenderer.Output delegate;
    private final BlockPos pos;
    private final List<PipeSpillVertexConsumer.Outlet> outlets;

    private PipeSpillOutput(FluidRenderer.Output delegate, BlockPos pos,
                            List<PipeSpillVertexConsumer.Outlet> outlets) {
        this.delegate = delegate;
        this.pos = pos.immutable();
        this.outlets = outlets;
    }

    public static FluidRenderer.Output wrap(FluidRenderer.Output delegate, BlockAndTintGetter level,
                                            BlockPos pos, BlockState state, FluidState fluid) {
        List<PipeSpillVertexConsumer.Outlet> outlets = PipeSpillVertexConsumer.findOutlets(level, pos, state, fluid);
        return outlets.isEmpty() ? delegate : new PipeSpillOutput(delegate, pos, outlets);
    }

    @Override
    public VertexConsumer getBuilder(ChunkSectionLayer layer) {
        return new PipeSpillVertexConsumer(delegate.getBuilder(layer), pos, outlets);
    }
}
