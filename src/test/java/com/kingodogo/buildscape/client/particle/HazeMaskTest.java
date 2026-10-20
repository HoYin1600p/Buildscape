package com.kingodogo.buildscape.client.particle;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class HazeMaskTest {

    @Test
    public void openFieldKeepsEveryCellAndMergesIntoOneRectangle() {
        HazeMask mask = HazeMask.compute(25, 25, 12, 12, (x, z) -> false);
        assertAllVisible(mask, 25, 25);
        assertEquals(List.of(new HazeMask.Rectangle(0, 0, 25, 25)), mask.rectangles());
    }

    @Test
    public void wallThroughMiddleHidesTheFarSide() {
        HazeMask mask = HazeMask.compute(9, 7, 2, 3, (x, z) -> x == 4);
        for (int z = 0; z < 7; z++) {
            for (int x = 0; x < 9; x++) {
                assertEquals("cell " + x + ", " + z, x < 4, mask.isVisible(x, z));
            }
        }
    }

    @Test
    public void singleGlassBlockHidesItsCellAndItsShadow() {
        assertSingleCollisionShadow(0.0, 1.0);
    }

    @Test
    public void singleLeafBlockHidesItsCellAndItsShadow() {
        // Transparency does not enter the predicate: leaves have the same collision height as glass.
        assertSingleCollisionShadow(0.0, 1.0);
    }

    @Test
    public void stepRisingThroughHazeHeightHidesItsCellsAndFarSide() {
        double hazeHeight = 0.2;
        HazeMask mask = HazeMask.compute(9, 7, 2, 3,
                (x, z) -> x >= 4 && HazeMask.blocksHeight(0.0, 1.0, hazeHeight));
        assertTrue(mask.isVisible(2, 3));
        for (int z = 0; z < 7; z++) {
            for (int x = 4; x < 9; x++) {
                assertFalse(mask.isVisible(x, z));
            }
        }
    }

    @Test
    public void overheadBlocksDoNotHideHazeBelowThem() {
        assertFalse(HazeMask.blocksHeight(1.0, 2.0, 0.2));
        assertFalse(HazeMask.blocksHeight(0.5, 1.0, 0.2));
        assertFalse(HazeMask.blocksHeight(0.2, 1.0, 0.2));
        HazeMask mask = HazeMask.compute(9, 7, 2, 3,
                (x, z) -> x >= 4 && HazeMask.blocksHeight(1.0, 2.0, 0.2));
        assertAllVisible(mask, 9, 7);
    }

    @Test
    public void groundTopExactlyAtHazeHeightDoesNotHideHaze() {
        assertFalse(HazeMask.blocksHeight(0.0, 0.5, 0.5));
        assertFalse(HazeMask.blocksHeight(0.0, 0.5, 0.6));
        HazeMask mask = HazeMask.compute(9, 7, 2, 3,
                (x, z) -> HazeMask.blocksHeight(0.0, 0.5, 0.5));
        assertAllVisible(mask, 9, 7);
    }

    @Test
    public void slabsStairsSnowAndTallCollisionUseTheirActualHeight() {
        assertFalse(HazeMask.blocksHeight(0.0, 0.5, 0.52));
        assertTrue(HazeMask.blocksHeight(0.5, 1.0, 0.75));
        assertFalse(HazeMask.blocksHeight(0.5, 1.0, 0.2));
        assertFalse(HazeMask.blocksHeight(0.0, 0.125, 0.2));
        assertTrue(HazeMask.blocksHeight(0.0, 0.375, 0.2));
        assertTrue(HazeMask.blocksHeight(-1.0, 0.5, 0.4));
        assertFalse(HazeMask.blocksHeight(0.3, 0.7, 0.2));
        assertFalse(HazeMask.blocksHeight(0.3, 0.701, 0.2));
    }

    @Test
    public void blocksWithoutCollisionHideNothing() {
        // Grass, flowers and haze bushes supply no collision boxes to the cell predicate.
        double[][] collisionBoxes = new double[0][2];
        HazeMask mask = HazeMask.compute(9, 7, 2, 3, (x, z) -> {
            for (double[] box : collisionBoxes) {
                if (HazeMask.blocksHeight(box[0], box[1], 0.2)) {
                    return true;
                }
            }
            return false;
        });
        assertAllVisible(mask, 9, 7);
    }

    @Test
    public void blockedCentreProducesNoVisibleCells() {
        HazeMask mask = HazeMask.compute(7, 7, 3, 3, (x, z) -> x == 3 && z == 3);
        assertTrue(mask.rectangles().isEmpty());
        for (int z = 0; z < 7; z++) {
            for (int x = 0; x < 7; x++) {
                assertFalse(mask.isVisible(x, z));
            }
        }
    }

    @Test
    public void diagonalGapsCannotConnectAnEnclosedCentre() {
        HazeMask mask = HazeMask.compute(7, 7, 3, 3,
                (x, z) -> Math.abs(x - 3) + Math.abs(z - 3) == 1);
        assertTrue(mask.isVisible(3, 3));
        assertFalse(mask.isVisible(2, 2));
        assertFalse(mask.isVisible(0, 0));
    }

    @Test
    public void aPartlyShadowedCellIsHiddenEvenWhenItsCentreRayIsClear() {
        // The ray to (7.5, 5.5) misses (4, 3), but rays to the cell's lower edge cross it.
        HazeMask mask = HazeMask.compute(9, 7, 2, 3, (x, z) -> x == 4 && z == 3);
        assertFalse(mask.isVisible(7, 5));
        assertTrue(mask.isVisible(6, 0));
    }

    @Test
    public void predicateIsEvaluatedExactlyOncePerCellAtTheGridCap() {
        int size = HazeMask.MAX_GRID_SIZE;
        int[][] calls = new int[size][size];
        HazeMask mask = HazeMask.compute(size, size, 0, 0, (x, z) -> {
            calls[z][x]++;
            return false;
        });
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                assertEquals(1, calls[z][x]);
            }
        }
        assertAllVisible(mask, size, size);
    }

    @Test(expected = IllegalArgumentException.class)
    public void oversizedGridIsRejected() {
        HazeMask.compute(HazeMask.MAX_GRID_SIZE + 1, 1, 0, 0, (x, z) -> false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCentreIsRejected() {
        HazeMask.compute(3, 3, 3, 0, (x, z) -> false);
    }

    @Test
    public void mergedRectanglesCoverOnlyVisibleCellsWithoutOverlap() {
        HazeMask mask = HazeMask.compute(9, 7, 2, 3, (x, z) -> x == 4 && z >= 2 && z <= 4);
        int[][] coverage = new int[7][9];
        for (HazeMask.Rectangle rectangle : mask.rectangles()) {
            for (int z = rectangle.minZ(); z < rectangle.maxZ(); z++) {
                for (int x = rectangle.minX(); x < rectangle.maxX(); x++) {
                    coverage[z][x]++;
                }
            }
        }
        for (int z = 0; z < 7; z++) {
            for (int x = 0; x < 9; x++) {
                assertEquals(mask.isVisible(x, z) ? 1 : 0, coverage[z][x]);
            }
        }
    }

    private static void assertSingleCollisionShadow(double minY, double maxY) {
        HazeMask mask = HazeMask.compute(9, 7, 2, 3,
                (x, z) -> x == 4 && z == 3 && HazeMask.blocksHeight(minY, maxY, 0.2));
        assertTrue(mask.isVisible(2, 3));
        assertTrue(mask.isVisible(3, 3));
        assertFalse(mask.isVisible(4, 3));
        assertFalse(mask.isVisible(5, 3));
        assertFalse(mask.isVisible(8, 3));
        assertTrue(mask.isVisible(6, 0));
    }

    private static void assertAllVisible(HazeMask mask, int width, int height) {
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                assertTrue("cell " + x + ", " + z, mask.isVisible(x, z));
            }
        }
    }
}
