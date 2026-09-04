package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import net.minecraft.world.level.block.state.BlockState;

/** Shared channel heights for the pipe surface and its outlet spill. Author: HoYin1600p. */
public final class PipeWaterSurface {
    private PipeWaterSurface() {}

    public record Heights(float inlet, float outlet) {
        public float center() {
            return (inlet + outlet) * 0.5F;
        }
    }

    public static Heights flowing(BlockState state, PipeFlowState flow) {
        if (state.getValue(HollowPipeBlock.WATERLOGGED)) {
            float source = HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
            return new Heights(source, flow.getFlowDirections().isEmpty()
                    ? source : (source + flowHeight(1)) * 0.5F);
        }
        float floor = state.getValue(HollowPipeBlock.DOWN) ? 0.0F : 0.125F;
        int distance = Math.min(7, Math.max(1, flow.getDistance()));
        float current = flowHeight(distance);
        return new Heights(Math.max(floor, (flowHeight(distance - 1) + current) * 0.5F),
                Math.max(floor, (current + Math.max(floor, flowHeight(distance + 1))) * 0.5F));
    }

    private static float flowHeight(int distance) {
        return distance <= 0 ? HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT
                : Math.max(0.125F, Math.max(1, 8 - Math.min(7, distance)) / 9.0F);
    }
}
