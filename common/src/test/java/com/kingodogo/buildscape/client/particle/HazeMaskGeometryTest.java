package com.kingodogo.buildscape.client.particle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HazeMaskGeometryTest {
    @Test
    void wallsBlockVisibilityAndIdenticalLayoutsReuseTheMask() {
        HazeMask.clearCaches();
        HazeMask wall = HazeMask.compute(7, 7, 3, 3, (x, z) -> x == 5);
        assertTrue(wall.isVisible(4, 3));
        assertFalse(wall.isVisible(6, 3));
        assertSame(wall, HazeMask.compute(7, 7, 3, 3, (x, z) -> x == 5));
        assertNotSame(wall, HazeMask.compute(7, 7, 3, 3, (x, z) -> false));
    }

    @Test
    void openAreaProducesTwoSidedQuadAndObstaclesClipGeometry() {
        HazeMask open = HazeMask.compute(7, 7, 3, 3, (x, z) -> false);
        HazeGeometry geometry = new HazeGeometry(open.rectangles(), -1.75F, 0.5F);
        geometry.update(0, 0, 1, 0);
        assertEquals(8, geometry.vertexCount());
        HazeMask blocked = HazeMask.compute(7, 7, 3, 3, (x, z) -> x >= 4);
        HazeGeometry clipped = new HazeGeometry(blocked.rectangles(), -1.75F, 0.5F);
        clipped.update(0, 0, 1, 0);
        float[] vertices = clipped.vertices();
        for (int i = 0; i < clipped.vertexCount(); i++) assertTrue(vertices[i * 4] <= 0.25F);
    }
}
