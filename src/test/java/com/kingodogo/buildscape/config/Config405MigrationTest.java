package com.kingodogo.buildscape.config;

import com.google.gson.JsonObject;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class Config405MigrationTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    private static String fixture(String name) throws Exception {
        try (InputStream input = Config405MigrationTest.class.getResourceAsStream("/config405/" + name)) {
            assertNotNull(name, input);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void invoke(PillarParticleConfig config, String name) throws Exception {
        Method method = PillarParticleConfig.class.getDeclaredMethod(name);
        method.setAccessible(true);
        method.invoke(config);
    }

    @Test
    public void exactOldItemsParseWithoutOverwritingOnLoad() throws Exception {
        for (boolean vault : new boolean[]{false, true}) {
            Path directory = temporary.newFolder().toPath();
            Path file = directory.resolve("pillar-items.json");
            String original = fixture("old-pillar-items-" + (vault ? "vault" : "vanilla") + ".json");
            Set<String> oldItems = PillarParticleConfig.parseItems(original);
            assertTrue(oldItems.contains("minecraft:diamond"));
            assertTrue(oldItems.contains("buildscape:ancient_ashen_scroll"));
            assertEquals(vault, oldItems.contains("the_vault:echo_pog"));
            Files.writeString(file, original);
            PillarParticleConfig config = new PillarParticleConfig(directory);
            config.loadItemsInternal(vault);
            assertEquals(oldItems, config.items);
            assertEquals("Existing file must remain untouched and not rewritten on launch", original, ConfigLayoutIO.readText(file));

            config.items.remove("minecraft:diamond");
            assertTrue(config.saveItemsToDisk(vault));
            String saved = ConfigLayoutIO.readText(file);
            PillarParticleConfig reloaded = new PillarParticleConfig(directory);
            reloaded.loadItemsInternal(vault);
            assertFalse(reloaded.items.contains("minecraft:diamond"));
            assertEquals(saved, ConfigLayoutIO.readText(file));
        }
    }

    @Test
    public void handRemovedOldItemsStayRemovedAndFileIsUnmodifiedOnLoad() throws Exception {
        for (boolean vault : new boolean[]{false, true}) {
            Path directory = temporary.newFolder().toPath();
            Path file = directory.resolve("pillar-items.json");
            String original = fixture("old-pillar-items-" + (vault ? "vault" : "vanilla") + ".json")
                    .replaceAll("(?m)^\\s*\"minecraft:diamond\",\\R", "")
                    .replaceAll("(?m)^\\s*\"the_vault:echo_pog\",\\R", "");
            Files.writeString(file, original);
            PillarParticleConfig config = new PillarParticleConfig(directory);
            config.loadItemsInternal(vault);
            assertFalse(config.items.contains("minecraft:diamond"));
            assertFalse(config.items.contains("the_vault:echo_pog"));
            assertEquals("User modified file must not be rewritten on load", original, ConfigLayoutIO.readText(file));
        }
    }

    @Test
    public void exactOldPropertiesKeepValuesAndCommentsOnTheirSettings() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-properties.json");
        String original = fixture("old-pillar-properties.json");
        Files.writeString(file, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        invoke(config, "loadPropertiesInternal");
        assertTrue(config.use_pattern);
        assertEquals(3, config.max_particle_color);
        assertEquals(6, config.particle_color.size());
        assertTrue(config.savePropertiesToDisk());
        String saved = ConfigLayoutIO.readText(file);
        assertEquals(ConfigLayoutIO.readProperties(original), ConfigLayoutIO.readProperties(saved));
        for (String line : original.split("\\R")) {
            int comment = line.indexOf("//");
            if (comment >= 0) assertTrue(line, saved.contains(line.substring(comment)));
        }
        assertBeforeKey(saved, "// Base speed multiplier for particles", "particle_speed");
        assertBeforeKey(saved, "// How far particles spread from center", "particle_spread");
        assertBeforeKey(saved, "// Number of particles spawned per tick", "particle_density");
        assertBeforeKey(saved, "// Pattern-specific parameters (only used when use_pattern = true)", "pattern_speed");
        assertBeforeKey(saved, "// DYING PILLARS:", "particle_color");
        assertTrue(config.savePropertiesToDisk());
        assertEquals(saved, ConfigLayoutIO.readText(file));
    }

    @Test
    public void exactOldClientConfigLoadsAndKeepsItsValues() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path file = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        String original = fixture("old-buildscape.cfg");
        Files.writeString(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertEquals(original, ConfigLayoutIO.readText(file));
        config.save();
        String saved = ConfigLayoutIO.readText(file);
        assertEquals(ConfigLayoutIO.readEntries(original), ConfigLayoutIO.readEntries(saved));
        assertTrue(saved.contains("# Edit this file to customise BuildScape behaviour."));
        config.save();
        assertEquals(saved, ConfigLayoutIO.readText(file));
    }

    @Test
    public void replacementDecodedFilesLoadValuesAndNeverRewriteBytes() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path client = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(client.getParent());
        byte[] clientBytes = badBytes("# note ", "\nHideBuildscapeConfig = true\nMaxPipeNetworkSize = 27\n");
        Files.write(client, clientBytes);
        BuildscapeClientConfig clientConfig = new BuildscapeClientConfig(root);
        assertTrue(clientConfig.isConfigButtonHidden());
        assertEquals(27, clientConfig.getMaxPipeNetworkSize());
        clientConfig.save();
        assertArrayEquals(clientBytes, Files.readAllBytes(client));

        Path directory = root.resolve("pillar");
        Files.createDirectories(directory);
        Path properties = directory.resolve("pillar-properties.json");
        byte[] propertiesBytes = badBytes("{// note ", "\n\"pattern\":\"spiral\",\"particle_speed\":0.037}");
        Files.write(properties, propertiesBytes);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        invoke(config, "loadPropertiesInternal");
        assertEquals("spiral", config.pattern);
        assertEquals(0.037, config.particle_speed, 0.000001);
        assertFalse(config.savePropertiesToDisk());
        assertTrue(config.getSaveFailureReason().contains("read-only"));
        assertArrayEquals(propertiesBytes, Files.readAllBytes(properties));

        Path items = directory.resolve("pillar-items.json");
        byte[] itemsBytes = badBytes("{// note ", "\n\"items\":[\"minecraft:stick\"]}");
        Files.write(items, itemsBytes);
        config.loadItemsInternal(false);
        assertTrue(config.items.contains("minecraft:stick"));
        assertFalse(config.items.contains("minecraft:diamond"));
        assertFalse(config.saveItemsToDisk(false));
        assertArrayEquals(itemsBytes, Files.readAllBytes(items));
        assertFalse(Files.exists(items.resolveSibling("pillar-items.json.pre-4.0.5.bak")));

        Path presets = directory.resolve("pillar-presets.json");
        byte[] presetsBytes = badBytes("{\"default\":{\"name\":\"Default\",\"items\":[]},\"custom\":{\"name\":\"",
                "\",\"items\":[\"minecraft:stick\"]}}");
        Files.write(presets, presetsBytes);
        PresetsConfig presetsConfig = new PresetsConfig(directory);
        presetsConfig.load();
        assertEquals(Set.of("minecraft:stick"), presetsConfig.getPreset("custom").items);
        presetsConfig.save();
        assertArrayEquals(presetsBytes, Files.readAllBytes(presets));
    }

    private static byte[] badBytes(String prefix, String suffix) throws Exception {
        byte[] bad = {(byte) 0x81};
        assumeTrue("Default charset has no replacement for the fixture byte",
                ConfigLayoutIO.hasReplacementCharacters(new String(bad, Charset.defaultCharset())));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bytes.write(prefix.getBytes(Charset.defaultCharset()));
        bytes.write(bad);
        bytes.write(suffix.getBytes(Charset.defaultCharset()));
        return bytes.toByteArray();
    }

    @Test
    public void lenientOnlyItemsAreReadOnlyButTrailingCommasAreWritable() throws Exception {
        for (String original : new String[]{"{items:['minecraft:stick'],}",
                "{\"items\":[\"minecraft:stick\"],\"custom\":1,\"custom\":2}",
                "{\"items\":[\"minecraft:stick\",7]}"}) {
            Path directory = temporary.newFolder().toPath();
            Path file = directory.resolve("pillar-items.json");
            Files.writeString(file, original);
            PillarParticleConfig config = new PillarParticleConfig(directory);
            config.loadItemsInternal(false);
            assertTrue(config.items.contains("minecraft:stick"));
            assertFalse(config.saveItemsToDisk(false));
            assertEquals(original, ConfigLayoutIO.readText(file));
        }
        assertEquals(Set.of("example:literal,}"), PillarParticleConfig.parseItems(
                "{\"items\":[\"example:literal,}\",/* trailing array comma */],/* object comma */}"));
    }

    @Test
    public void existingFileCanBeSavedLater() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-items.json");
        String original = fixture("old-pillar-items-vanilla.json");
        Files.writeString(file, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        assertEquals(original, ConfigLayoutIO.readText(file));
        config.items.add("example:new_item");
        assertTrue(config.saveItemsToDisk(false));
        assertTrue(ConfigLayoutIO.readText(file).contains("example:new_item"));
    }

    @Test
    public void legacyPartsMigrateIndependentlyAndBackupPreventsResurrection() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path directory = root.resolve("pillar");
        Path legacy = root.resolve("pillar-particles.json");
        String original = "{\"items\":null,\"pattern\":\"spiral\",\"particle_speed\":0.037}";
        Files.writeString(legacy, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        invoke(config, "migrateOldConfig");
        Path properties = directory.resolve("pillar-properties.json");
        assertEquals("spiral", ConfigLayoutIO.readProperties(ConfigLayoutIO.readText(properties)).get("pattern").getAsString());
        assertFalse(Files.exists(directory.resolve("pillar-items.json")));
        assertEquals(original, ConfigLayoutIO.readText(legacy));
        assertEquals(original, ConfigLayoutIO.readText(root.resolve("pillar-particles.json.backup")));
        Files.delete(properties);
        invoke(config, "migrateOldConfig");
        assertFalse(Files.exists(properties));
    }

    @Test
    public void replacementDecodedCombinedLegacyConfigMigratesWithoutChangingBytesOrValues() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path directory = root.resolve("pillar");
        Path legacy = root.resolve("pillar-particles.json");
        byte[] original = badBytes("{// old note ",
                "\n\"items\":[\"minecraft:stick\"],\"pattern\":\"spiral\",\"particle_speed\":0.037}");
        Files.write(legacy, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        invoke(config, "migrateOldConfig");
        invoke(config, "loadPropertiesInternal");
        config.loadItemsInternal(false);
        assertEquals("spiral", config.pattern);
        assertEquals(0.037, config.particle_speed, 0.000001);
        assertTrue(config.items.contains("minecraft:stick"));
        assertFalse(config.items.contains("minecraft:diamond"));
        assertFalse(config.savePropertiesToDisk());
        assertFalse(config.saveItemsToDisk(false));
        assertArrayEquals(original, Files.readAllBytes(directory.resolve("pillar-properties.json")));
        assertArrayEquals(original, Files.readAllBytes(directory.resolve("pillar-items.json")));
        assertArrayEquals(original, Files.readAllBytes(root.resolve("pillar-particles.json.backup")));
        assertArrayEquals(original, Files.readAllBytes(legacy));
    }

    @Test
    public void deletedFilesCanBeSavedAgainWithoutRestart() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path client = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(client.getParent());
        Files.writeString(client, fixture("old-buildscape.cfg"));
        BuildscapeClientConfig clientConfig = new BuildscapeClientConfig(root);
        Files.delete(client);
        clientConfig.save();
        assertEquals(64, Integer.parseInt(ConfigLayoutIO.readEntries(ConfigLayoutIO.readText(client)).get("MaxPipeNetworkSize")));

        Path directory = root.resolve("pillar");
        Files.createDirectories(directory);
        Path properties = directory.resolve("pillar-properties.json");
        Files.writeString(properties, fixture("old-pillar-properties.json"));
        PillarParticleConfig config = new PillarParticleConfig(directory);
        invoke(config, "loadPropertiesInternal");
        Files.delete(properties);
        config.pattern = "spiral";
        assertTrue(config.savePropertiesToDisk());
        assertEquals("spiral", ConfigLayoutIO.readProperties(ConfigLayoutIO.readText(properties)).get("pattern").getAsString());

        Path items = directory.resolve("pillar-items.json");
        Files.writeString(items, "{\"items\":[\"minecraft:stick\"],\"defaults_version\":410}");
        config.loadItemsInternal(false);
        Files.delete(items);
        assertTrue(config.saveItemsToDisk(false));
        assertEquals(Set.of("minecraft:stick"), PillarParticleConfig.parseItems(ConfigLayoutIO.readText(items)));
    }

    private static void assertBeforeKey(String text, String comment, String key) {
        int commentPosition = text.indexOf(comment);
        assertTrue(comment, commentPosition >= 0);
        int keyPosition = text.indexOf("\"" + key + "\":", commentPosition);
        assertTrue(key, keyPosition >= 0);
        // No different setting may intervene between the old comment and its setting.
        String between = text.substring(commentPosition, keyPosition);
        for (String line : between.split("\\R")) assertFalse(between, line.trim().startsWith("\""));
    }
}
