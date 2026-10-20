package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CushionSupportTest {
    private static final double BOTTOM_CUSHION_TOP = 3.0D / 16.0D;

    @Test
    public void entityOnBottomHalfCushionIsSupportedByTheCushionNotTheBlockBelow() {
        BlockPos cushion = new BlockPos(4, -60, 7);
        BlockPos vanilla = new BlockPos(4, -61, 7);
        BlockPos result = CushionSupport.supportingPos(vanilla, 4.5D, -60 + BOTTOM_CUSHION_TOP, 7.5D, cushion::equals);
        assertEquals(cushion, result);
    }

    @Test
    public void vanillaPositionStaysWhenItIsAlreadyTheCushion() {
        BlockPos cushion = new BlockPos(0, 10, 0);
        BlockPos vanilla = new BlockPos(0, 10, 0);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 0.5D, 10.5D + 0.5D, 0.5D, cushion::equals));
    }

    @Test
    public void topHalfCushionKeepsTheVanillaLookup() {
        BlockPos cushion = new BlockPos(0, 10, 0);
        BlockPos vanilla = new BlockPos(0, 10, 0);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 0.5D, 11.0D, 0.5D, cushion::equals));
    }

    @Test
    public void flatGroundNeverProbesForCushions() {
        BlockPos vanilla = new BlockPos(2, -61, 2);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 2.5D, -60.0D, 2.5D, pos -> {
            throw new AssertionError("no probe expected when both lookups hit the same block");
        }));
    }

    @Test
    public void nonCushionBlockInTheFeetCellIsIgnored() {
        BlockPos vanilla = new BlockPos(2, -61, 2);
        assertSame(vanilla, CushionSupport.supportingPos(vanilla, 2.5D, -60 + BOTTOM_CUSHION_TOP, 2.5D, pos -> false));
    }

    @Test
    public void negativeCoordinatesFloorTowardTheCorrectColumn() {
        BlockPos cushion = new BlockPos(-3, 5, -9);
        BlockPos vanilla = new BlockPos(-3, 4, -9);
        assertEquals(cushion,
                CushionSupport.supportingPos(vanilla, -2.5D, 5 + BOTTOM_CUSHION_TOP, -8.5D, Set.of(cushion)::contains));
    }
}
