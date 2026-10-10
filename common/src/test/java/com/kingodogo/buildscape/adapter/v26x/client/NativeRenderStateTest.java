package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.client.renderer.ShelfRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NativeRenderStateTest {
    @Test void stockingBlockPlacementsStayJustInFrontOfTheirSupportSurface() {
        assertEquals(new StockingRenderGeometry.Placement(0.5, 0.5, 0.999, 180, 0),
                StockingRenderGeometry.blockPlacement(Direction.NORTH));
        assertEquals(new StockingRenderGeometry.Placement(0.5, 0.5, 0.001, 0, 0),
                StockingRenderGeometry.blockPlacement(Direction.SOUTH));
        assertEquals(new StockingRenderGeometry.Placement(0.999, 0.5, 0.5, 90, 0),
                StockingRenderGeometry.blockPlacement(Direction.WEST));
        assertEquals(new StockingRenderGeometry.Placement(0.001, 0.5, 0.5, 270, 0),
                StockingRenderGeometry.blockPlacement(Direction.EAST));
        assertEquals(new StockingRenderGeometry.Placement(0.5, 0.5 / 16, 0.5, 180, -90),
                StockingRenderGeometry.blockPlacement(Direction.UP));
        assertEquals(new StockingRenderGeometry.Placement(0.5, 15.5 / 16, 0.5, 180, 90),
                StockingRenderGeometry.blockPlacement(Direction.DOWN));
    }

    @Test void hangingStockingsUseTheReferenceEntityFacingRatherThanTheBlockFacing() {
        assertEquals(0, StockingRenderGeometry.entityPlacement(Direction.NORTH).yaw());
        assertEquals(180, StockingRenderGeometry.entityPlacement(Direction.SOUTH).yaw());
        assertEquals(90, StockingRenderGeometry.entityPlacement(Direction.WEST).yaw());
        assertEquals(270, StockingRenderGeometry.entityPlacement(Direction.EAST).yaw());
        assertEquals(-90, StockingRenderGeometry.entityPlacement(Direction.UP).pitch());
        assertEquals(90, StockingRenderGeometry.entityPlacement(Direction.DOWN).pitch());
        for (Direction facing : Direction.values()) {
            var placement = StockingRenderGeometry.entityPlacement(facing);
            assertEquals(0, placement.x());
            assertEquals(0, placement.y());
            assertEquals(0, placement.z());
        }
    }

    @Test void stockingColorExtractionPreservesTheDefaultAndDyedTextureNames() {
        assertEquals(StockingRenderGeometry.textureFor(null), StockingRenderGeometry.textureFor("festive"));
        assertEquals("buildscape:textures/entity/festive_stocking.png",
                StockingRenderGeometry.textureFor(null).toString());
        assertEquals("buildscape:textures/entity/red_festive_stocking.png",
                StockingRenderGeometry.textureFor("red").toString());
    }

    @Test void potWobbleHasTheReferenceDurationAmplitudeAndPartialTickPhase() {
        assertEquals(-6, DecoratedPotRenderer.wobbleAngle(102, 100, true, 0.5F), 0.00001F);
        assertEquals(2, DecoratedPotRenderer.wobbleAngle(107, 100, true, 0.5F), 0.00001F);
        assertEquals(0, DecoratedPotRenderer.wobbleAngle(100, 100, true, 0));
        assertEquals(0, DecoratedPotRenderer.wobbleAngle(110, 100, true, 0));
        assertEquals(0, DecoratedPotRenderer.wobbleAngle(120, 100, true, 0));
        assertEquals(0, DecoratedPotRenderer.wobbleAngle(99, 100, true, 0.5F));
        assertEquals(0, DecoratedPotRenderer.wobbleAngle(102, 0, true, 0.5F));
        assertEquals(0, DecoratedPotRenderer.wobbleAngle(102, 100, false, 0.5F));
        assertEquals(-6, DecoratedPotRenderer.wobbleAngle(10000000002L, 10000000000L, true, 0.5F), 0.00001F);
    }

    @Test void fallingIcicleSamplesTheTopOfItsBoundsAndFloorsNegativeCoordinates() {
        assertEquals(new BlockPos(-1, 65, -3), FallingIcicleEntityRenderer.samplePosition(-0.1, 65.9, -2.1));
        assertEquals(new BlockPos(2, 66, 4), FallingIcicleEntityRenderer.samplePosition(2, 66, 4));
    }

    @Test void shelfBoundsFollowTheSameRotationCompositionAsTheBedPose() {
        var bed = new ShelfRenderer.OnShelf(90, 180, 0, 0, 0.9375F, false);
        double[] bounds = bed.transformedBounds(new AABB(0, 0, 0, 1, 2, 3));
        assertArrayEquals(new double[] {-0.9375, 0, 0, 0, 2.8125, 1.875}, bounds, 0.000001);
    }

    @Test void shelfAlignmentEitherRestsTheBottomOrCentresTheItem() {
        double[] bounds = {-0.2, -0.3, 0.2, 0.6, 0.9, 1};
        var bottom = ShelfBlockRenderer.offset(bounds, true, true);
        var centred = ShelfBlockRenderer.offset(bounds, false, true);
        assertEquals(-0.2, bottom.x(), 0.000001);
        assertEquals(0.3, bottom.y(), 0.000001);
        assertEquals(-0.6, bottom.z(), 0.000001);
        assertEquals(-0.3, centred.y(), 0.000001);
        assertEquals(bottom.x(), centred.x());
        assertEquals(bottom.z(), centred.z());
        var noRecentering = ShelfBlockRenderer.offset(bounds, true, false);
        assertEquals(0, noRecentering.x());
        assertEquals(0, noRecentering.z());
    }
}
