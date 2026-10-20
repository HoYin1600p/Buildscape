package com.kingodogo.buildscape.block.decor;

import net.minecraft.core.BlockPos;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CushionSupportTest {
    private static final double BOTTOM_CUSHION_TOP = 3.0 / 16.0;

    @Test
    public void entityOnBottomHalfCushionIsSupportedByTheCushionNotTheBlockBelow() {
        BlockPos cushion = new BlockPos(4, -60, 7);
        BlockPos vanilla = new BlockPos(4, -61, 7);
        BlockPos result = CushionSupport.supportingPos(vanilla, 4.5, -60 + BOTTOM_CUSHION_TOP, 7.5, cushion::equals);
        assertEquals(cushion, result);
    }

    @Test
    public void vanillaPositionStaysWhenItIsAlreadyTheCushion() {
        BlockPos cushion = new BlockPos(0, 10, 0);
        BlockPos vanilla = new BlockPos(0, 10, 0);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 0.5, 10.5 + 0.5, 0.5, cushion::equals));
    }

    @Test
    public void topHalfCushionKeepsTheVanillaLookup() {
        BlockPos cushion = new BlockPos(0, 10, 0);
        BlockPos vanilla = new BlockPos(0, 10, 0);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 0.5, 11.0, 0.5, cushion::equals));
    }

    @Test
    public void flatGroundNeverProbesForCushions() {
        BlockPos vanilla = new BlockPos(2, -61, 2);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 2.5, -60.0, 2.5, pos -> {
            throw new AssertionError("no probe expected when both lookups hit the same block");
        }));
    }

    @Test
    public void nonCushionBlockInTheFeetCellIsIgnored() {
        BlockPos vanilla = new BlockPos(2, -61, 2);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 2.5, -60 + BOTTOM_CUSHION_TOP, 2.5, pos -> false));
    }

    @Test
    public void negativeCoordinatesFloorTowardTheCorrectColumn() {
        BlockPos cushion = new BlockPos(-3, 5, -9);
        BlockPos vanilla = new BlockPos(-3, 4, -9);
        assertEquals(cushion,
                CushionSupport.supportingPos(vanilla, -2.5, 5 + BOTTOM_CUSHION_TOP, -8.5, Set.of(cushion)::contains));
    }
}
