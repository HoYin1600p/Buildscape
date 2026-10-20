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
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    /** A 4.0.5 client config gains the settings added since: just the preset slot count so far. */
    private static java.util.Map<String, String> withNewDefaults(java.util.Map<String, String> entries) {
        java.util.Map<String, String> expected = new java.util.LinkedHashMap<>(entries);
        expected.putIfAbsent("MaxCustomPresets", "4");
        return expected;
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
    public void exactOldItemsGainNewDefaultsWithoutReorderingOnLoad() throws Exception {
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
            assertTrue(config.items.containsAll(oldItems));
            assertTrue(config.items.containsAll(ConfigPaths.loadDefaultItems(vault)));
            com.google.gson.JsonArray before = ConfigLayoutIO.readProperties(ConfigLayoutIO.itemsJson(original))
                    .getAsJsonArray("items");
            com.google.gson.JsonArray after = ConfigLayoutIO.readProperties(ConfigLayoutIO.readText(file))
                    .getAsJsonArray("items");
            for (int i = 0; i < before.size(); i++) {
                assertEquals(before.get(i), after.get(i));
            }

            config.items.remove("minecraft:diamond");
            assertTrue(config.saveItemsToDisk(vault));
            assertEquals(original, ConfigLayoutIO.readText(file.resolveSibling("pillar-items.json.pre-4.0.5.bak")));
            String saved = ConfigLayoutIO.readText(file);
            PillarParticleConfig reloaded = new PillarParticleConfig(directory);
            reloaded.loadItemsInternal(vault);
            assertFalse(reloaded.items.contains("minecraft:diamond"));
            assertEquals(saved, ConfigLayoutIO.readText(file));
        }
    }

    @Test
    public void handRemovedOldItemsStayRemovedWhenNewDefaultsAreOffered() throws Exception {
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
            assertTrue(config.items.contains("buildscape:bone_dice"));
            String merged = ConfigLayoutIO.readText(file);
            config.loadItemsInternal(vault);
            assertEquals(merged, ConfigLayoutIO.readText(file));
        }
    }

    @Test
    public void vanilla405FileGainsAllVaultDefaultsWhenVaultIsInstalled() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-items.json");
        Files.writeString(file, fixture("old-pillar-items-vanilla.json"));
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(true);
        assertTrue(config.items.containsAll(ConfigPaths.loadDefaultItems(true)));
        assertTrue(config.items.contains("the_vault:echo_pog"));
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
            if (comment >= 0) {
                assertTrue(line, saved.contains(line.substring(comment)));
            }
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
        assertTrue(ConfigLayoutIO.readText(file).startsWith(original));
        assertEquals(withNewDefaults(ConfigLayoutIO.readEntries(original)),
                ConfigLayoutIO.readEntries(ConfigLayoutIO.readText(file)));
        config.save();
        String saved = ConfigLayoutIO.readText(file);
        assertEquals(withNewDefaults(ConfigLayoutIO.readEntries(original)), ConfigLayoutIO.readEntries(saved));
        assertTrue(saved.contains("# Edit this file to customise BuildScape behaviour."));
        config.save();
        assertEquals(saved, ConfigLayoutIO.readText(file));
    }

    @Test
    public void legacyRootClientConfigMigratesOnceAndResetWritesDefaults() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path legacy = root.resolve("buildscape.cfg");
        Path file = root.resolve("buildscape/buildscape.cfg");
        byte[] original = fixture("old-buildscape.cfg").replace("\r\n", "\n").replace("\n", "\r\n")
                .getBytes(Charset.defaultCharset());
        Files.write(legacy, original);
        new BuildscapeClientConfig(root);
        String saved = ConfigLayoutIO.readText(file);
        assertEquals(withNewDefaults(ConfigLayoutIO.readEntries(new String(original, Charset.defaultCharset()))),
                ConfigLayoutIO.readEntries(saved));
        assertTrue(saved.contains("# --- Gameplay ---"));
        assertTrue(saved.contains("# --- Performance ---"));
        assertTrue(saved.contains("# --- Interface ---"));
        assertTrue(saved.contains("# Edit this file to customise BuildScape behaviour."));
        Path backup = root.resolve("buildscape.cfg.backup");
        assertArrayEquals(original, Files.readAllBytes(backup));
        assertArrayEquals(original, Files.readAllBytes(legacy));
        assertTrue(Files.isRegularFile(root.resolve("buildscape.cfg.backup.complete")));
        new BuildscapeClientConfig(root);
        assertEquals(saved, ConfigLayoutIO.readText(file));

        Files.writeString(legacy,
                fixture("old-buildscape.cfg").replace("MaxPipeNetworkSize = 64", "MaxPipeNetworkSize = 77"));
        Files.delete(file);
        BuildscapeClientConfig reset = new BuildscapeClientConfig(root);
        assertEquals(64, reset.getMaxPipeNetworkSize());
        assertEquals("64", ConfigLayoutIO.readEntries(ConfigLayoutIO.readText(file)).get("MaxPipeNetworkSize"));
        assertArrayEquals(original, Files.readAllBytes(backup));
    }

    @Test
    public void legacyRootClientConfigKeepsEveryValueAndAttachesCommentsToReorderedSettings() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path legacy = root.resolve("buildscape.cfg");
        String original = fixture("old-buildscape.cfg").replace(" = true", " = false")
                .replace("HideBuildscapeConfig = false", "HideBuildscapeConfig = true")
                .replace("MaxPipeNetworkSize = 64", "MaxPipeNetworkSize = 77")
                + "FutureSetting = custom=value\n";
        for (String key : ConfigLayoutIO.readEntries(original).keySet()) {
            original = original.replace(key + " = ", "# note for " + key + "\n" + key + " = ");
        }
        Files.writeString(legacy, original, Charset.defaultCharset());
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        Path file = root.resolve("buildscape/buildscape.cfg");
        String saved = ConfigLayoutIO.readText(file);
        assertEquals(withNewDefaults(ConfigLayoutIO.readEntries(original)), ConfigLayoutIO.readEntries(saved));
        for (String key : ConfigLayoutIO.readEntries(original).keySet()) {
            int comment = saved.indexOf("# note for " + key + "\n");
            assertTrue(key, comment >= 0);
            int setting = saved.indexOf(key + " = ", comment);
            assertTrue(key, setting > comment);
            for (String line : saved.substring(comment, setting).split("\\R")) {
                assertTrue(key, line.isBlank() || line.trim().startsWith("#"));
            }
        }
        assertTrue(config.isConfigButtonHidden());
        assertEquals(77, config.getMaxPipeNetworkSize());
        assertFalse(config.isCakeStackingEnabled());
        assertFalse(config.isWaterBottleStackingEnabled());
        assertTrue(saved.contains("# --- Other ---"));
        assertArrayEquals(Files.readAllBytes(legacy), Files.readAllBytes(root.resolve("buildscape.cfg.backup")));
        config.save();
        assertEquals(saved, ConfigLayoutIO.readText(file));
    }

    @Test
    public void existingGroupedClientConfigWinsAndPreventsLaterLegacyImport() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path legacy = root.resolve("buildscape.cfg");
        Files.writeString(legacy, fixture("old-buildscape.cfg"));
        Path file = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        byte[] grouped = "# --- Gameplay ---\r\nMaxPipeNetworkSize = 23\r\n".getBytes(Charset.defaultCharset());
        Files.write(file, grouped);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertEquals(23, config.getMaxPipeNetworkSize());
        byte[] completed = Files.readAllBytes(file);
        assertArrayEquals(grouped, java.util.Arrays.copyOf(completed, grouped.length));
        assertEquals("true", ConfigLayoutIO.readEntries(ConfigLayoutIO.readText(file)).get("isCakeStack"));
        assertArrayEquals(Files.readAllBytes(legacy), Files.readAllBytes(root.resolve("buildscape.cfg.backup")));
        assertTrue(Files.exists(root.resolve("buildscape.cfg.backup.complete")));
        Files.delete(file);
        Files.writeString(legacy, "MaxPipeNetworkSize = 77\n");
        assertEquals(64, new BuildscapeClientConfig(root).getMaxPipeNetworkSize());
    }

    @Test
    public void failedLegacyClientBackupLeavesOriginalReadableAndCanBeRetried() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path legacy = root.resolve("buildscape.cfg");
        String original = fixture("old-buildscape.cfg").replace("MaxPipeNetworkSize = 64", "MaxPipeNetworkSize = 77");
        Files.writeString(legacy, original);
        Path backup = root.resolve("buildscape.cfg.backup");
        Files.createDirectory(backup);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertEquals(77, config.getMaxPipeNetworkSize());
        config.save();
        assertFalse(Files.exists(root.resolve("buildscape/buildscape.cfg")));
        assertFalse(Files.exists(root.resolve("buildscape.cfg.backup.complete")));
        assertEquals(original, ConfigLayoutIO.readText(legacy));
        Files.delete(backup);
        assertEquals(77, new BuildscapeClientConfig(root).getMaxPipeNetworkSize());
        assertTrue(Files.exists(root.resolve("buildscape.cfg.backup.complete")));
        assertArrayEquals(Files.readAllBytes(legacy), Files.readAllBytes(backup));
    }

    @Test
    public void interruptedLegacyClientMigrationResumesWithAnExistingBackup() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path legacy = root.resolve("buildscape.cfg");
        Files.writeString(legacy, fixture("old-buildscape.cfg"));
        Path backup = root.resolve("buildscape.cfg.backup");
        Files.copy(legacy, backup);
        new BuildscapeClientConfig(root);
        assertTrue(ConfigLayoutIO.readText(root.resolve("buildscape/buildscape.cfg")).contains("# --- Gameplay ---"));
        assertArrayEquals(Files.readAllBytes(legacy), Files.readAllBytes(backup));
        assertTrue(Files.exists(root.resolve("buildscape.cfg.backup.complete")));
    }

    @Test
    public void replacementDecodedLegacyClientConfigKeepsOriginalBytesAndLoadsReadOnly() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path legacy = root.resolve("buildscape.cfg");
        byte[] original = badBytes("# old note ", "\nMaxPipeNetworkSize = 27\n");
        Files.write(legacy, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertEquals(27, config.getMaxPipeNetworkSize());
        config.save();
        assertArrayEquals(original, Files.readAllBytes(root.resolve("buildscape/buildscape.cfg")));
        assertArrayEquals(original, Files.readAllBytes(legacy));
        assertArrayEquals(original, Files.readAllBytes(root.resolve("buildscape.cfg.backup")));
        assertTrue(Files.exists(root.resolve("buildscape.cfg.backup.complete")));
    }

    @Test
    public void ambiguousLegacyClientConfigStaysReadOnlyWithoutCompletingMigration() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path legacy = root.resolve("buildscape.cfg");
        String original = "MaxPipeNetworkSize = 12\nMaxPipeNetworkSize = 19\nlegacy line\n";
        Files.writeString(legacy, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertEquals(19, config.getMaxPipeNetworkSize());
        config.save();
        assertEquals(original, ConfigLayoutIO.readText(legacy));
        assertFalse(Files.exists(root.resolve("buildscape/buildscape.cfg")));
        assertFalse(Files.exists(root.resolve("buildscape.cfg.backup.complete")));
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
        assertTrue(config.items.containsAll(PillarParticleConfig.parseItems(original)));
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
        assertEquals("spiral",
                ConfigLayoutIO.readProperties(ConfigLayoutIO.readText(properties)).get("pattern").getAsString());
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
        assertEquals(64, Integer
                .parseInt(ConfigLayoutIO.readEntries(ConfigLayoutIO.readText(client)).get("MaxPipeNetworkSize")));

        Path directory = root.resolve("pillar");
        Files.createDirectories(directory);
        Path properties = directory.resolve("pillar-properties.json");
        Files.writeString(properties, fixture("old-pillar-properties.json"));
        PillarParticleConfig config = new PillarParticleConfig(directory);
        invoke(config, "loadPropertiesInternal");
        Files.delete(properties);
        config.pattern = "spiral";
        assertTrue(config.savePropertiesToDisk());
        assertEquals("spiral",
                ConfigLayoutIO.readProperties(ConfigLayoutIO.readText(properties)).get("pattern").getAsString());

        Path items = directory.resolve("pillar-items.json");
        Files.writeString(items, "{\"items\":[\"minecraft:stick\"],\"defaults_version\":410}");
        config.loadItemsInternal(false);
        Set<String> loadedItems = Set.copyOf(config.items);
        Files.delete(items);
        assertTrue(config.saveItemsToDisk(false));
        assertEquals(loadedItems, PillarParticleConfig.parseItems(ConfigLayoutIO.readText(items)));
    }

    private static void assertBeforeKey(String text, String comment, String key) {
        int commentPosition = text.indexOf(comment);
        assertTrue(comment, commentPosition >= 0);
        int keyPosition = text.indexOf("\"" + key + "\":", commentPosition);
        assertTrue(key, keyPosition >= 0);
        // No different setting may intervene between the old comment and its setting.
        String between = text.substring(commentPosition, keyPosition);
        for (String line : between.split("\\R")) {
            assertFalse(between, line.trim().startsWith("\""));
        }
    }

    @Test
    public void realReleased405FileIsDetectedAndLeftUntouchedByTheMerge() throws Exception {
        for (boolean vault : new boolean[]{false, true}) {
            Path directory = temporary.newFolder().toPath();
            Path file = directory.resolve("pillar-items.json");
            String original = fixture("old-pillar-items-" + (vault ? "vault" : "vanilla") + ".json");
            Files.writeString(file, original);
            PillarParticleConfig config = new PillarParticleConfig(directory);
            config.loadItemsInternal(vault);
            assertEquals(PillarParticleConfig.parseItems(original), config.items);
            com.google.gson.JsonArray before = ConfigLayoutIO.readProperties(ConfigLayoutIO.itemsJson(original))
                    .getAsJsonArray("items");
            com.google.gson.JsonArray after = ConfigLayoutIO.readProperties(ConfigLayoutIO.readText(file))
                    .getAsJsonArray("items");
            assertEquals(before, after);
            assertEquals(original, ConfigLayoutIO.readText(file));
            assertFalse(Files.exists(file.resolveSibling("pillar-items.json.pre-4.0.5.bak")));
            assertTrue(config.items.remove("minecraft:diamond"));
            assertTrue(config.saveItemsToDisk(vault));
            assertEquals(original, ConfigLayoutIO.readText(file.resolveSibling("pillar-items.json.pre-4.0.5.bak")));
        }
    }

    @Test
    public void realReleased405FileWithConfettiRemovedKeepsItRemoved() throws Exception {
        for (boolean vault : new boolean[]{false, true}) {
            Path directory = temporary.newFolder().toPath();
            Path file = directory.resolve("pillar-items.json");
            String original = fixture("old-pillar-items-" + (vault ? "vault" : "vanilla") + ".json")
                    .replaceAll("(?m)^\\s*\"buildscape:confetti\",\\R", "");
            assertFalse(original.contains("buildscape:confetti"));
            Files.writeString(file, original);
            PillarParticleConfig config = new PillarParticleConfig(directory);
            config.loadItemsInternal(vault);
            assertFalse(config.items.contains("buildscape:confetti"));
            String merged = ConfigLayoutIO.readText(file);
            assertFalse(PillarParticleConfig.parseItems(merged).contains("buildscape:confetti"));
            new PillarParticleConfig(directory).loadItemsInternal(vault);
            assertEquals(merged, ConfigLayoutIO.readText(file));
        }
    }
}
