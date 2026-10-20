package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigReadOnlyRegressionTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void missingDefaultsAreAppendedOnceWithoutChangingAnyExistingBytes() throws Exception {
        for (String newline : new String[]{"\n", "\r\n", "\r"}) {
            for (boolean trailingNewline : new boolean[]{false, true}) {
                Path root = temporary.newFolder().toPath();
                Path file = root.resolve("buildscape/buildscape.cfg");
                Files.createDirectories(file.getParent());
                String original = String.join(newline, "# own comment", "HideBuildscapeConfig = true",
                        "OptimizeBuildscapeModelLoading = false", "OptimizeBuildscapeModelMaterials = true",
                        "OptimizeBuildscapeModelBaking = false", "OptimizeBuildscapeBlockStateCache = true",
                        "isCakeStack = false", "isWaterbottleStack = false", "FooBar=1")
                        + (trailingNewline ? newline : "");
                byte[] bytes = original.getBytes(StandardCharsets.US_ASCII);
                Files.write(file, bytes);
                BuildscapeClientConfig config = new BuildscapeClientConfig(root);
                byte[] completed = Files.readAllBytes(file);
                assertArrayEquals(bytes, Arrays.copyOf(completed, bytes.length));
                assertEquals(original + (trailingNewline ? "" : newline)
                        + "# Maximum number of pipes in one network." + newline
                        + "MaxPipeNetworkSize = 64" + newline
                        + "# Number of custom item presets that can be saved besides Default (1-32, default 4)." + newline
                        + "MaxCustomPresets = 4" + newline, ConfigLayoutIO.readText(file));
                assertEquals(64, config.getMaxPipeNetworkSize());
                assertFalse(config.isCakeStackingEnabled());
                assertFalse(config.isWaterBottleStackingEnabled());
                assertArrayEquals(bytes, Files.readAllBytes(file.resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
                FileTime stamp = FileTime.fromMillis(1_600_000_000_000L);
                Files.setLastModifiedTime(file, stamp);
                new BuildscapeClientConfig(root);
                assertArrayEquals(completed, Files.readAllBytes(file));
                assertEquals(stamp, Files.getLastModifiedTime(file));
            }
        }
    }

    @Test
    public void garbageAndDuplicateKeysAreSalvagedWithoutAppending() throws Exception {
        for (String original : new String[]{"# keep\nHideBuildscapeConfig=true\ngarbage\n",
                "HideBuildscapeConfig=false\nHideBuildscapeConfig=true\n"}) {
            Path root = temporary.newFolder().toPath();
            Path file = root.resolve("buildscape/buildscape.cfg");
            Files.createDirectories(file.getParent());
            Files.writeString(file, original);
            BuildscapeClientConfig config = new BuildscapeClientConfig(root);
            assertTrue(config.isConfigButtonHidden());
            assertEquals(64, config.getMaxPipeNetworkSize());
            config.save();
            assertEquals(original, Files.readString(file));
            assertFalse(Files.exists(file.resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
        }
    }

    @Test
    public void invalidUtf8ClientConfigLoadsWithoutAppendingOrSaving() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path file = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        byte[] original = badBytes("# note @\nHideBuildscapeConfig=true\nMaxPipeNetworkSize=27\n");
        Files.write(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertTrue(config.isConfigButtonHidden());
        assertEquals(27, config.getMaxPipeNetworkSize());
        config.save();
        assertUnchangedWithBackup(file, original);
    }

    @Test
    public void invalidUtf8PropertiesLoadValuesButRejectEditsAndKeepTheirBackup() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-properties.json");
        byte[] original = badBytes("{// note @\n\"pattern\":\"spiral\",\"particle_density\":7}");
        Files.write(file, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        Method load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
        assertEquals("spiral", config.pattern);
        assertEquals(7, config.particle_density);
        assertFalse(config.canEditProperties());
        config.particle_density = 8;
        assertFalse(config.savePropertiesToDisk());
        assertUnchangedWithBackup(file, original);
        load.invoke(config);
        assertFalse(config.savePropertiesToDisk());
        assertUnchangedWithBackup(file, original);
    }

    @Test
    public void invalidUtf8ItemsLoadValuesButRefuseAddsRemovalsAndSaves() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-items.json");
        byte[] original = badBytes("{// note @\n\"items\":[\"minecraft:stick\"]}");
        Files.write(file, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        assertTrue(config.items.contains("minecraft:stick"));
        assertFalse(config.canEditItems());
        Set<String> loaded = Set.copyOf(config.items);
        assertFalse(config.addItem("minecraft:diamond"));
        assertFalse(config.removeItem("minecraft:stick"));
        assertEquals(loaded, config.items);
        assertFalse(config.saveItemsToDisk(false));
        config.loadItemsInternal(false);
        assertUnchangedWithBackup(file, original);
    }

    @Test
    public void invalidUtf8PresetsLoadValuesButRefuseSavesAndDeletes() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-presets.json");
        byte[] original = badBytes("{\"default\":{\"name\":\"Defa@ult\",\"items\":[]},"
                + "\"custom\":{\"name\":\"Custom\",\"items\":[\"minecraft:stick\"]}}");
        Files.write(file, original);
        PresetsConfig config = new PresetsConfig(directory);
        config.load();
        assertEquals(Set.of("minecraft:stick"), config.getPreset("custom").items);
        assertFalse(config.canEdit());
        assertFalse(config.savePreset("custom", "Replacement", Set.of("minecraft:diamond")));
        assertEquals("unreadable", config.getLastEditFailure());
        assertFalse(config.deletePreset("custom"));
        assertEquals("unreadable", config.getLastEditFailure());
        config.save();
        config.load();
        assertUnchangedWithBackup(file, original);
    }

    @Test
    public void lowLevelWriterProtectsBytesRejectedByBothDecoders() throws Exception {
        Path file = temporary.newFolder().toPath().resolve("config.json");
        byte[] original = badBytes("{// @\n\"value\":1}");
        Files.write(file, original);
        assertThrows(IOException.class, () -> ConfigLayoutIO.write(file, "{}", ConfigLayoutIO.readText(file), true));
        assertUnchangedWithBackup(file, original);
    }

    private static byte[] badBytes(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '@') {
                bytes[i] = (byte) 0x81;
            }
        }
        return bytes;
    }

    private static void assertUnchangedWithBackup(Path file, byte[] original) throws Exception {
        Path backup = file.resolveSibling(file.getFileName() + ".bak");
        assertArrayEquals(original, Files.readAllBytes(file));
        assertArrayEquals(original, Files.readAllBytes(backup));
        FileTime stamp = FileTime.fromMillis(1_600_000_000_000L);
        Files.setLastModifiedTime(backup, stamp);
        assertTrue(ConfigLayoutIO.protectInvalidEncoding(file));
        assertEquals(stamp, Files.getLastModifiedTime(backup));
        assertArrayEquals(original, Files.readAllBytes(backup));
    }
}
