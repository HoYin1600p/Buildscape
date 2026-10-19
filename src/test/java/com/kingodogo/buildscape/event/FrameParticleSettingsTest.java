package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.feature.pillar.ItemFrameParticleHandler;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class FrameParticleSettingsTest {
    @Test
    public void overridesDefaultsAndParticleLimitsAreResolvedTogether() throws Exception {
        PillarParticleConfig cfg = new PillarParticleConfig();
        cfg.pattern = "ring";
        cfg.use_pattern = true;
        cfg.particle_density = 4;
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.pattern = "default";
        data.pattern_speed = 2.0;
        data.pattern_spread = 1.5;
        data.pattern_intensity = 3.0;
        data.max_particle_color = 2;
        Object settings = resolve(data, cfg);
        assertEquals("ring", value(settings, "pattern"));
        assertEquals(12, value(settings, "count"));
        assertEquals(6.0, (Double) value(settings, "speed"), 0.0);
        assertEquals(1.5, (Double) value(settings, "spread"), 0.0);
        assertEquals(2, value(settings, "maxColors"));

        data.use_pattern = false;
        settings = resolve(data, cfg);
        assertEquals("random", value(settings, "pattern"));
        assertEquals(4, value(settings, "count"));
        assertEquals(cfg.particle_speed, (Double) value(settings, "speed"), 0.0);

        data.use_pattern = true;
        data.pattern_intensity = 64.0;
        cfg.particle_density = 128;
        assertEquals(64, value(resolve(data, cfg), "count"));
        cfg.particle_density = 0;
        assertEquals(0, value(resolve(data, cfg), "count"));
        cfg.particle_density = 4;
        cfg.pattern = "none";
        assertEquals(0, value(resolve(data, cfg), "count"));
    }

    private static Object resolve(PillarIdManager.PillarData data, PillarParticleConfig cfg) throws Exception {
        Method method = ItemFrameParticleHandler.class.getDeclaredMethod("resolveFrameSettings",
                PillarIdManager.PillarData.class, String.class, PillarParticleConfig.class);
        method.setAccessible(true);
        return method.invoke(null, data, "default", cfg);
    }

    private static Object value(Object settings, String field) throws Exception {
        Method method = settings.getClass().getDeclaredMethod(field);
        method.setAccessible(true);
        return method.invoke(settings);
    }
}
