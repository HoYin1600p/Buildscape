package com.kingodogo.buildscape.event;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExperienceSwimBoostTest {
    @Test
    void scalesHorizontalAndVerticalMotionIndependently() {
        Vec3 boosted = ModCommonEvents.boostExperienceSwimming(new Vec3(2.0D, 0.5D, -3.0D));
        assertEquals(2.35D, boosted.x, 1.0E-12D);
        assertEquals(0.53D, boosted.y, 1.0E-12D);
        assertEquals(-3.525D, boosted.z, 1.0E-12D);
    }

    @Test
    void preservesRestAndTheDirectionOfFallingMotion() {
        assertEquals(Vec3.ZERO, ModCommonEvents.boostExperienceSwimming(Vec3.ZERO));
        assertEquals(-1.06D, ModCommonEvents.boostExperienceSwimming(new Vec3(0, -1, 0)).y, 1.0E-12D);
    }
}
