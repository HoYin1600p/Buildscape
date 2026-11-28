package com.kingodogo.buildscape.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
class ParticleConfigPortTest {
    @TempDir Path directory;

    private PillarParticleConfig load(String text) throws Exception {
        Files.writeString(directory.resolve("pillar-properties.json"), text);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        var load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
        return config;
    }

    @Test
    void zeroDensityDisablesParticlesAndNegativeDensityClampsToZero() throws Exception {
        assertEquals(0, load("{\"particle_density\":0}").particle_density);
        assertEquals(0, load("{\"particle_density\":-1}").particle_density);
        assertEquals(6, load("{\"particle_density\":6}").particle_density);
    }

    @Test
    void legacySingleColorRemainsUsableAndEditable() throws Exception {
        PillarParticleConfig config = load("{\"particle_color\":\"#aabbcc\",\"particle_density\":6}");
        assertEquals(java.util.List.of("#AABBCC"), config.particle_color);
        assertEquals(6, config.particle_density);
        config.particle_density = 7;
        config.savePropertiesToDisk();
        assertEquals(7, ConfigLayoutIO.readProperties(
                Files.readString(directory.resolve("pillar-properties.json"))).get("particle_density").getAsInt());
    }

    @Test
    void paletteWithoutExplicitLimitUsesAllItsColors() throws Exception {
        PillarParticleConfig config = load("{\"particle_color\":[\"#FF0000\",\"#00FF00\",\"#0000FF\",\"#FFFFFF\"]}");
        assertEquals(4, config.max_particle_color);
    }
}
