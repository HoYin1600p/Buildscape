package com.kingodogo.buildscape.client.particle;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class HazeMaskTest extends BootstrappedGameTest {

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
        assertSingleCollisionShadow(Blocks.GLASS.defaultBlockState());
    }

    @Test
    public void singleLeafBlockHidesItsCellAndItsShadow() {
        assertSingleCollisionShadow(Blocks.OAK_LEAVES.defaultBlockState());
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
        for (BlockState state : List.of(Blocks.GRASS.defaultBlockState(), Blocks.POPPY.defaultBlockState(),
                Blocks.AIR.defaultBlockState(), Blocks.SNOW.defaultBlockState())) {
            assertStateHeights(state, 1.0, false);
            SingleBlockLookup level = new SingleBlockLookup(new BlockPos(4, 0, 3), state);
            Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            HazeMask mask = HazeMask.compute(9, 7, 2, 3,
                    (x, z) -> HazeMask.blocksCell(level, pos, shapes, x, z, x + 1.0, z + 1.0, 0.02));
            assertAllVisible(mask, 9, 7);
        }
    }

    @Test
    public void realCollisionBlocksAreCheckedInsideAboveAndBelowTheirShapes() {
        for (BlockState state : List.of(Blocks.GLASS.defaultBlockState(), Blocks.OAK_LEAVES.defaultBlockState(),
                Blocks.GLASS_PANE.defaultBlockState(), Blocks.STONE.defaultBlockState())) {
            assertStateHeights(state, 1.0, true);
        }
        assertStateHeights(Blocks.OAK_SLAB.defaultBlockState(), 0.5, true);
        assertStateHeights(Blocks.OAK_FENCE.defaultBlockState(), 1.5, true);
    }

    @Test
    public void slabTopAndTallFenceUseStrictHeightOverlap() {
        BlockPos pos = new BlockPos(-3, 7, 11);
        SingleBlockLookup slab = new SingleBlockLookup(pos, Blocks.OAK_SLAB.defaultBlockState());
        assertFalse(blocksCellAt(slab, 7.5));
        assertFalse(blocksCellAt(slab, 7.7));
        SingleBlockLookup fence = new SingleBlockLookup(pos, Blocks.OAK_FENCE.defaultBlockState());
        assertTrue(blocksCellAt(fence, 8.2));
        assertFalse(blocksCellAt(fence, 8.5));
        assertFalse(blocksCellAt(fence, 7.0));
    }

    @Test
    public void collisionBoxesMustOverlapTheCellHorizontally() {
        SingleBlockLookup pane = new SingleBlockLookup(new BlockPos(-3, 7, 11),
                Blocks.GLASS_PANE.defaultBlockState());
        Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        assertFalse(HazeMask.blocksCell(pane, pos, shapes, -3.0, 11.0, -2.75, 11.25, 7.2));
        assertFalse(HazeMask.blocksCell(pane, pos, shapes, -3.0, 11.0, -2.5625, 11.4375, 7.2));
        assertTrue(HazeMask.blocksCell(pane, pos, shapes, -2.75, 11.25, -2.25, 11.75, 7.2));
    }

    @Test
    public void cellLookupUsesOnlyTwoLayersAndCachesEmptyShapes() {
        SingleBlockLookup level = new SingleBlockLookup(new BlockPos(-3, 7, 11), Blocks.AIR.defaultBlockState());
        Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        assertFalse(HazeMask.blocksCell(level, pos, shapes, -2.75, 11.25, -2.25, 11.75, 7.2));
        assertEquals(2, level.lookups);
        assertTrue(shapes.containsKey(BlockPos.asLong(-3, 6, 11)));
        assertTrue(shapes.containsKey(BlockPos.asLong(-3, 7, 11)));
        assertFalse(HazeMask.blocksCell(level, pos, shapes, -2.75, 11.25, -2.25, 11.75, 7.2));
        assertEquals(2, level.lookups);
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

    private static void assertSingleCollisionShadow(BlockState state) {
        SingleBlockLookup level = new SingleBlockLookup(new BlockPos(4, 0, 3), state);
        Long2ObjectMap<List<AABB>> shapes = new Long2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        HazeMask mask = HazeMask.compute(9, 7, 2, 3,
                (x, z) -> HazeMask.blocksCell(level, pos, shapes, x, z, x + 1.0, z + 1.0, 0.2));
        assertTrue(mask.isVisible(2, 3));
        assertTrue(mask.isVisible(3, 3));
        assertFalse(mask.isVisible(4, 3));
        assertFalse(mask.isVisible(5, 3));
        assertFalse(mask.isVisible(8, 3));
        assertTrue(mask.isVisible(6, 0));
    }

    private static void assertStateHeights(BlockState state, double top, boolean blocksInside) {
        SingleBlockLookup level = new SingleBlockLookup(new BlockPos(-3, 7, 11), state);
        assertEquals(state + " inside", blocksInside, blocksCellAt(level, 7.02));
        assertFalse(state + " above", blocksCellAt(level, 7.0 + top + 0.2));
        assertFalse(state + " below", blocksCellAt(level, 6.8));
    }

    private static boolean blocksCellAt(SingleBlockLookup level, double hazeY) {
        return HazeMask.blocksCell(level, new BlockPos.MutableBlockPos(), new Long2ObjectOpenHashMap<>(),
                -2.75, 11.25, -2.25, 11.75, hazeY);
    }

    private static final class SingleBlockLookup implements BlockGetter {
        private final BlockPos blockPos;
        private final BlockState state;
        private int lookups;

        private SingleBlockLookup(BlockPos blockPos, BlockState state) {
            this.blockPos = blockPos;
            this.state = state;
        }

        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return null;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            this.lookups++;
            return pos.equals(this.blockPos) ? this.state : Blocks.AIR.defaultBlockState();
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return getBlockState(pos).getFluidState();
        }

        @Override
        public int getHeight() {
            return 256;
        }

        @Override
        public int getMinBuildHeight() {
            return 0;
        }
    }

    private static void assertAllVisible(HazeMask mask, int width, int height) {
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                assertTrue("cell " + x + ", " + z, mask.isVisible(x, z));
            }
        }
    }
}
