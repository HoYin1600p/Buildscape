package com.kingodogo.buildscape.client.particle;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HazeMaskCacheTest {
    @Before
    public void clearCaches() {
        HazeMask.clearCaches();
    }

    @Test
    public void identicalLayoutsShareMaskButStillCheckLiveOccupancy() {
        AtomicInteger queries = new AtomicInteger();
        HazeMask first = HazeMask.compute(7, 7, 3, 3, (x, z) -> {
            queries.incrementAndGet();
            return x == 5;
        });
        HazeMask second = HazeMask.compute(7, 7, 3, 3, (x, z) -> {
            queries.incrementAndGet();
            return x == 5;
        });
        assertSame(first, second);
        assertEquals(98, queries.get());
        assertFalse(second.isVisible(6, 3));
    }

    @Test
    public void changedBlocksUseNewMaskAndRestoredLayoutCanReuseOldMask() {
        HazeMask open = HazeMask.compute(7, 7, 3, 3, (x, z) -> false);
        HazeMask wall = HazeMask.compute(7, 7, 3, 3, (x, z) -> x == 5);
        assertNotSame(open, wall);
        assertTrue(open.isVisible(6, 3));
        assertFalse(wall.isVisible(6, 3));
        assertSame(open, HazeMask.compute(7, 7, 3, 3, (x, z) -> false));
    }

    @Test
    public void dimensionsAndFloodFillOriginArePartOfKey() {
        HazeMask left = HazeMask.compute(7, 7, 1, 3, (x, z) -> x == 3);
        HazeMask right = HazeMask.compute(7, 7, 5, 3, (x, z) -> x == 3);
        assertNotSame(left, right);
        assertTrue(left.isVisible(1, 3));
        assertFalse(right.isVisible(1, 3));
        assertNotSame(HazeMask.compute(4, 6, 1, 1, (x, z) -> false),
                HazeMask.compute(6, 4, 1, 1, (x, z) -> false));
    }

    @Test
    public void shapeDecompositionIsSharedByIdentityAndClearedWithWorld() {
        VoxelShape shape = Shapes.box(0, 0, 0, 1, 0.5, 1);
        List<AABB> boxes = HazeMask.collisionBoxes(shape);
        assertSame(boxes, HazeMask.collisionBoxes(shape));
        assertEquals(List.of(new AABB(0, 0, 0, 1, 0.5, 1)), boxes);
        assertTrue(HazeMask.collisionBoxes(Shapes.empty()).isEmpty());
        assertNotSame(boxes, HazeMask.collisionBoxes(Shapes.block()));
        HazeMask mask = HazeMask.compute(7, 7, 3, 3, (x, z) -> false);
        HazeMask.clearCaches();
        assertNotSame(boxes, HazeMask.collisionBoxes(shape));
        assertNotSame(mask, HazeMask.compute(7, 7, 3, 3, (x, z) -> false));
    }
}
