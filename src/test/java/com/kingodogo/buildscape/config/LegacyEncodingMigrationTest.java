package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Set;

import static org.junit.Assert.*;

public class LegacyEncodingMigrationTest {
    private static final Charset LEGACY = Charset.forName("windows-1252");
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void legacyPropertiesKeepTextAndOriginalBytesAndRemainEditable() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-properties.json");
        String text = "{// Caf\u00e9\n\"pattern\":\"Caf\u00e9\",\"particle_density\":7}";
        byte[] original = text.getBytes(LEGACY);
        Files.write(file, original);
        assertEquals(text, ConfigLayoutIO.readText(file, LEGACY));
        PillarParticleConfig config = new PillarParticleConfig(directory);
        Method load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
        assertEquals(text, Files.readString(file, StandardCharsets.UTF_8));
        assertTrue(ConfigLayoutIO.readText(file).contains("Caf\u00e9"));
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("pillar-properties.json.bak")));
        assertTrue(config.canEditProperties());
        assertEquals("Caf\u00e9", config.pattern);
        assertEquals(7, config.particle_density);
        config.particle_density = 8;
        assertTrue(config.savePropertiesToDisk());
        PillarParticleConfig reloaded = new PillarParticleConfig(directory);
        load.invoke(reloaded);
        assertEquals("Caf\u00e9", reloaded.pattern);
        assertEquals(8, reloaded.particle_density);
        assertTrue(reloaded.canEditProperties());
        assertTrue(Files.readString(file, StandardCharsets.UTF_8).contains("Caf\u00e9"));
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("pillar-properties.json.bak")));
    }

    @Test
    public void legacyPresetsKeepNamesAndOriginalBytesAndRemainEditable() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-presets.json");
        String text = "{\"default\":{\"name\":\"Default\",\"items\":[]},"
                + "\"custom\":{\"name\":\"Caf\u00e9\",\"items\":[\"minecraft:stick\"]}}";
        byte[] original = text.getBytes(LEGACY);
        Files.write(file, original);
        assertEquals(text, ConfigLayoutIO.readText(file, LEGACY));
        PresetsConfig config = new PresetsConfig(directory);
        config.load();
        assertEquals("Caf\u00e9", config.getPreset("custom").name);
        assertEquals(text, Files.readString(file, StandardCharsets.UTF_8));
        assertTrue(config.canEdit());
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("pillar-presets.json.bak")));
        assertTrue(config.savePreset("custom", "Caf\u00e9 edited", Set.of("minecraft:diamond")));
        PresetsConfig reloaded = new PresetsConfig(directory);
        reloaded.load();
        assertTrue(reloaded.canEdit());
        assertEquals("Caf\u00e9 edited", reloaded.getPreset("custom").name);
        assertEquals(Set.of("minecraft:diamond"), reloaded.getPreset("custom").items);
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("pillar-presets.json.bak")));
    }

    @Test
    public void validUtf8IsNotRewrittenOrBackedUp() throws Exception {
        Path file = temporary.newFolder().toPath().resolve("config.json");
        String text = "{\"name\":\"Caf\u00e9 \u6c34\"}";
        Files.writeString(file, text, StandardCharsets.UTF_8);
        FileTime stamp = FileTime.fromMillis(1_600_000_000_000L);
        Files.setLastModifiedTime(file, stamp);
        assertEquals(text, ConfigLayoutIO.readText(file));
        assertFalse(ConfigLayoutIO.protectInvalidEncoding(file));
        assertEquals(stamp, Files.getLastModifiedTime(file));
        assertFalse(Files.exists(file.resolveSibling("config.json.bak")));
    }

    @Test
    public void bytesInvalidInBothEncodingsStayProtectedAndKeepOneBackup() throws Exception {
        Path file = temporary.newFolder().toPath().resolve("config.json");
        byte[] original = {(byte) 0x81};
        Files.write(file, original);
        assertTrue(ConfigLayoutIO.protectInvalidEncoding(file, LEGACY));
        Path backup = file.resolveSibling("config.json.bak");
        assertArrayEquals(original, Files.readAllBytes(file));
        assertArrayEquals(original, Files.readAllBytes(backup));
        FileTime stamp = FileTime.fromMillis(1_600_000_000_000L);
        Files.setLastModifiedTime(backup, stamp);
        assertTrue(ConfigLayoutIO.protectInvalidEncoding(file, LEGACY));
        assertEquals(stamp, Files.getLastModifiedTime(backup));
        assertThrows(java.io.IOException.class,
                () -> ConfigLayoutIO.write(file, "{}", ConfigLayoutIO.readText(file, LEGACY), false, LEGACY));
        assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void readOnlyLegacyFileStillLoadsAndCannotBeSaved() throws Exception {
        Path file = temporary.newFolder().toPath().resolve("config.json");
        String text = "{\"name\":\"Caf\u00e9\"}";
        byte[] original = text.getBytes(LEGACY);
        Files.write(file, original);
        assertTrue(file.toFile().setReadOnly());
        try {
            org.junit.Assume.assumeFalse(Files.isWritable(file));
            assertEquals(text, ConfigLayoutIO.readText(file, LEGACY));
            assertArrayEquals(original, Files.readAllBytes(file));
            assertTrue(ConfigLayoutIO.protectInvalidEncoding(file));
            assertThrows(java.io.IOException.class, () -> ConfigLayoutIO.write(file, "{}", text, false));
            assertArrayEquals(original, Files.readAllBytes(file));
        } finally {
            assertTrue(file.toFile().setWritable(true));
        }
    }

    @Test
    public void failedBackupProtectsReadableValuesEvenIfTheObstacleIsLaterRemoved() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-properties.json");
        String text = "{\"pattern\":\"Caf\u00e9\",\"particle_density\":7}";
        byte[] original = text.getBytes(LEGACY);
        Files.write(file, original);
        Path backup = Files.createDirectory(directory.resolve("pillar-properties.json.bak"));
        assertEquals(text, ConfigLayoutIO.readText(file, LEGACY));
        PillarParticleConfig config = new PillarParticleConfig(directory);
        Method load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
        assertEquals("Caf\u00e9", config.pattern);
        assertEquals(7, config.particle_density);
        assertFalse(config.canEditProperties());
        Files.delete(backup);
        assertTrue(ConfigLayoutIO.protectInvalidEncoding(file));
        assertThrows(java.io.IOException.class, () -> ConfigLayoutIO.append(file, "\n", text));
        assertFalse(config.savePropertiesToDisk());
        assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void backupUsesTheOriginalSnapshotEvenIfAnotherReaderAlreadyRewroteTheFile() throws Exception {
        Path file = temporary.newFolder().toPath().resolve("config.json");
        String text = "{\"name\":\"Caf\u00e9\"}";
        byte[] original = text.getBytes(LEGACY);
        Files.writeString(file, text, StandardCharsets.UTF_8);
        assertEquals(text, ConfigLayoutIO.readText(file, original, LEGACY));
        Path backup = file.resolveSibling("config.json.bak");
        assertArrayEquals(original, Files.readAllBytes(backup));
        assertEquals(text, ConfigLayoutIO.readText(file, original, LEGACY));
        assertArrayEquals(original, Files.readAllBytes(backup));
        assertEquals(text, Files.readString(file, StandardCharsets.UTF_8));
    }
}
