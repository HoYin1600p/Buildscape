package com.kingodogo.buildscape.feature.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CopperRodLightningPositionTest {
    @Test
    public void summonAtRodBottomSelectsCopperBase() {
        BlockPos rod = new BlockPos(20, -59, 130);
        Vec3 bolt = new Vec3(20.5, -59.0, 130.5);

        assertEquals(rod.below(), strikePosition(bolt));
    }

    @Test
    public void summonAboveRodSelectsRod() {
        BlockPos rod = new BlockPos(20, -59, 130);
        Vec3 bolt = new Vec3(20.5, -58.0, 130.5);

        assertEquals(rod, strikePosition(bolt));
    }

    @Test
    public void channelingStrikeTargetsRodAcrossWorldHeights() {
        for (int y : new int[]{-64, -59, -1, 0, 1, 319}) {
            BlockPos rod = new BlockPos(20, y, 130);
            Vec3 bolt = Vec3.atBottomCenterOf(rod.above());

            assertEquals("strike at rod top for y=" + y, rod, strikePosition(bolt));
        }
    }

    private static BlockPos strikePosition(Vec3 bolt) {
        return new BlockPos(bolt.x, bolt.y - 1.0E-6, bolt.z);
    }
}
