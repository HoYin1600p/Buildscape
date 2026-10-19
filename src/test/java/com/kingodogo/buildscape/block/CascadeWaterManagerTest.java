package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CascadeWaterManagerTest {
    @Test
    public void ticketMatchesVanillaWaterSearchIncludingCornersAndHeight() {
        for (BlockPos source : new BlockPos[]{new BlockPos(0, 70, 0), new BlockPos(-16, -60, 15)}) {
            AABB area = CascadeWaterManager.hydrationArea(source);
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    for (int y = -2; y <= 2; y++) {
                        boolean expected = Math.abs(x) <= 4 && Math.abs(z) <= 4 && (y == -1 || y == 0);
                        assertEquals("offset " + x + "," + y + "," + z, expected,
                                area.contains(Vec3.atCenterOf(source.offset(x, y, z))));
                    }
                }
            }
        }
    }
}
