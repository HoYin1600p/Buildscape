package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildScapeParticleResetTest {
    @Test
    void propertyResetRestoresEveryCurrentParticleDefault() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.particle_speed = 0.4;
        config.particle_spread = 0.4;
        config.particle_lifetime = 90;
        config.particle_density = 12;
        config.use_pattern = false;
        config.pattern = "burst";
        config.pattern_speed = 0.4;
        config.pattern_spread = 0.4;
        config.pattern_intensity = 4.0;

        BuildScapeConfigScreen.resetParticleProperties(config);

        assertEquals(0.02, config.particle_speed);
        assertEquals(0.1, config.particle_spread);
        assertEquals(20, config.particle_lifetime);
        assertEquals(2, config.particle_density);
        assertTrue(config.use_pattern);
        assertEquals("ring", config.pattern);
        assertEquals(0.05, config.pattern_speed);
        assertEquals(0.05, config.pattern_spread);
        assertEquals(1.0, config.pattern_intensity);
    }

    @Test
    void colorResetRestoresTheCompleteDefaultPalette() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.particle_color.clear();
        config.particle_color.add("#000000");
        config.max_particle_color = 1;

        BuildScapeConfigScreen.resetParticleColors(config);

        assertEquals(Arrays.asList(PillarParticleConfig.DEFAULT_COLORS), config.particle_color);
        assertEquals(3, config.max_particle_color);
    }
}
