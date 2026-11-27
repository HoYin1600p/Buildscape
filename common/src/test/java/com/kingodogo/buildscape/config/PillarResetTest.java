package com.kingodogo.buildscape.config;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PillarResetTest {
    @Test
    void resetPreservesIdentityAndLocationWhileClearingOverrides() {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData("pillar-1", "minecraft:overworld", new BlockPos(12, 64, -8));
        data.dyeColors = new ArrayList<>(Arrays.asList("#112233", "#445566"));
        data.use_pattern = false;
        data.usePattern = false;
        data.pattern = "burst";
        data.pattern_speed = 0.4;
        data.patternSpeed = 0.4;
        data.pattern_spread = 0.3;
        data.patternSpread = 0.3;
        data.pattern_intensity = 4.0;
        data.patternIntensity = 4.0;
        data.max_particle_color = 2;
        data.maxParticleColor = 2;

        PillarIdManager.clearPillarOverrides(data);

        assertEquals("pillar-1", data.id);
        assertEquals("minecraft:overworld", data.dimension);
        assertEquals(new BlockPos(12, 64, -8), data.getBlockPos());
        assertTrue(data.dyeColors.isEmpty());
        assertNull(data.use_pattern);
        assertNull(data.usePattern);
        assertNull(data.pattern);
        assertNull(data.pattern_speed);
        assertNull(data.patternSpeed);
        assertNull(data.pattern_spread);
        assertNull(data.patternSpread);
        assertNull(data.pattern_intensity);
        assertNull(data.patternIntensity);
        assertNull(data.max_particle_color);
        assertNull(data.maxParticleColor);
    }
}
