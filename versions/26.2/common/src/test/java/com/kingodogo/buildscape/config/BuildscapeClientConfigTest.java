package com.kingodogo.buildscape.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BuildscapeClientConfigTest {
    @TempDir Path configDirectory;

    @Test
    void loadsAndSavesReferenceFormatWithoutLosingSettings() throws Exception {
        Path file = configDirectory.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "# Existing settings\nMaxPipeNetworkSize = 137\nHideBuildscapeConfig = true\n");

        BuildscapeClientConfig config = new BuildscapeClientConfig(configDirectory);
        assertEquals(137, config.getMaxPipeNetworkSize());
        assertTrue(config.isConfigButtonHidden());
        config.setMaxPipeNetworkSize(211);
        config.save();

        BuildscapeClientConfig reloaded = new BuildscapeClientConfig(configDirectory);
        assertEquals(211, reloaded.getMaxPipeNetworkSize());
        assertTrue(reloaded.isConfigButtonHidden());
        assertTrue(reloaded.isModelMaterialCacheEnabled());
        String saved = Files.readString(file);
        assertTrue(saved.contains("MaxPipeNetworkSize = 211"));
        assertTrue(saved.contains("OptimizeBuildscapeModelMaterials = true"));
    }

    @Test
    void migratesLegacyFileWithinSelectedConfigDirectory() throws Exception {
        Files.writeString(configDirectory.resolve("buildscape.cfg"), "MaxPipeNetworkSize = 99\n");
        BuildscapeClientConfig config = new BuildscapeClientConfig(configDirectory);
        assertEquals(99, config.getMaxPipeNetworkSize());
        assertTrue(Files.exists(configDirectory.resolve("buildscape/buildscape.cfg")));
        assertEquals(99, new BuildscapeClientConfig(configDirectory).getMaxPipeNetworkSize());
    }

    @Test
    void malformedAndNonpositiveLimitsKeepReferenceDefaultsAndMinimum() throws Exception {
        Path file = configDirectory.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "MaxPipeNetworkSize = invalid\n");
        assertEquals(64, new BuildscapeClientConfig(configDirectory).getMaxPipeNetworkSize());
        Files.writeString(file, "MaxPipeNetworkSize = -7\n");
        assertEquals(1, new BuildscapeClientConfig(configDirectory).getMaxPipeNetworkSize());
    }
}
