package com.kingodogo.buildscape.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

class CushionSurfaceTest {

    @Test
    void findsTheTopOfTheShapeAtTheClickedColumn() {
        VoxelShape shape = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);

        assertEquals(0.5, CushionSurface.topAt(shape, 0.5, 0.5), 0.00001);
    }

    @Test
    void selectsTheHigherStepAtTheClickedColumn() {
        VoxelShape stair = Shapes.or(
                Shapes.box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                Shapes.box(0.5, 0.5, 0.0, 1.0, 1.0, 1.0));

        assertEquals(0.5, CushionSurface.topAt(stair, 0.25, 0.5), 0.00001);
        assertEquals(1.0, CushionSurface.topAt(stair, 0.75, 0.5), 0.00001);
    }

    @Test
    void rejectsColumnsWithoutAHorizontalSurface() {
        VoxelShape post = Shapes.box(0.0, 0.0, 0.0, 0.25, 1.0, 0.25);

        assertTrue(Double.isNaN(CushionSurface.topAt(post, 0.5, 0.5)));
    }
}
