package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.PipeWaterSurface;
import net.minecraft.core.Direction;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NativeDisplayExtractionTest {
    @Test void pillarDisplayHeightKeepsMobFeetAndItemsAtTheirReferenceOffsets() {
        assertEquals(1.4625F, PillarBlockEntityRenderer.hoverHeight(false, false));
        assertEquals(1.125F, PillarBlockEntityRenderer.hoverHeight(false, true));
        assertEquals(1, PillarBlockEntityRenderer.hoverHeight(true, false));
        assertEquals(0.875F, PillarBlockEntityRenderer.hoverHeight(true, true));
    }

    @Test void mobDisplaysSpinOnlyWhenRequestedWhileItemsUseTheFasterReferenceRotation() {
        assertEquals(0, PillarBlockEntityRenderer.rotationSpeed(true, false));
        assertEquals(22.5F, PillarBlockEntityRenderer.rotationSpeed(true, true));
        assertEquals(90, PillarBlockEntityRenderer.rotationSpeed(false, false));
        assertEquals(90, PillarBlockEntityRenderer.rotationSpeed(false, true));
    }

    @Test void fluidFlowDirectionsAreCopiedAndNeighborVisibilityIsDetachedFromTheWorld() {
        var flow = EnumSet.of(Direction.EAST, Direction.UP);
        var heights = new PipeWaterSurface.Heights(0.8F, 0.4F);
        var state = new HollowLogBlockEntityRenderer.FluidDisplay(null, false, null, 0xFF3F76E4,
                Direction.Axis.X, Direction.WEST, flow, heights, false, true,
                (1 << Direction.EAST.ordinal()) | (1 << Direction.DOWN.ordinal()), true, false);
        flow.clear();
        assertEquals(Set.of(Direction.EAST, Direction.UP), state.outlets());
        assertThrows(UnsupportedOperationException.class, () -> state.outlets().clear());
        assertTrue(state.hasNeighborFluid(Direction.EAST));
        assertTrue(state.hasNeighborFluid(Direction.DOWN));
        assertFalse(state.hasNeighborFluid(Direction.WEST));
        assertFalse(state.hasNeighborFluid(Direction.UP));
        assertEquals(0.6F, state.heights().center(), 0.000001F);
        assertEquals(0xFF3F76E4, state.color());
        assertTrue(state.glassNeg());
        assertFalse(state.glassPos());
    }

    @Test void colouredFrameTextureExtractionPreservesTheReferenceFallback() {
        assertEquals(ColoredItemFrameEntityRenderer.textureFor("white"), ColoredItemFrameEntityRenderer.textureFor(null));
        assertEquals(ColoredItemFrameEntityRenderer.textureFor("white"), ColoredItemFrameEntityRenderer.textureFor(""));
        assertEquals("buildscape:textures/entity/blue_item_frame.png", ColoredItemFrameEntityRenderer.textureFor("blue").toString());
    }

    @Test void glassCoversKeepTheReferenceInsetAndThicknessOnEveryAxis() {
        for (Direction.Axis axis : Direction.Axis.values()) {
            for (boolean positive : new boolean[] {false, true}) {
                PoseStack pose = new PoseStack();
                HollowLogBlockEntityRenderer.positionGlassCover(pose, axis, positive);
                Vector3f minimum = pose.last().pose().transformPosition(new Vector3f());
                Vector3f maximum = pose.last().pose().transformPosition(new Vector3f(1, 1, 1));
                float inset = positive ? 0.921875F : 0.015625F;
                int thinAxis = axis == Direction.Axis.X ? 0 : axis == Direction.Axis.Y ? 1 : 2;
                for (int i = 0; i < 3; i++) {
                    assertEquals(i == thinAxis ? inset : 0.125F, minimum.get(i), 0.000001F);
                    assertEquals(i == thinAxis ? 0.0625F : 0.75F, maximum.get(i) - minimum.get(i), 0.000001F);
                }
            }
        }
    }
}
