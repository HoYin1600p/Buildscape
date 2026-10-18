package com.kingodogo.buildscape.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultPillarItems405Test {
    @BeforeClass
    public static void bootstrap() {
        com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest.bootstrap();
    }
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void bundledTemplatesHaveNoDuplicatesAndGateVaultItems() {
        for (boolean vaultLoaded : new boolean[]{false, true}) {
            Set<String> items = ConfigPaths.loadDefaultItems(vaultLoaded);
            assertFalse(items.isEmpty());
            assertEquals(vaultLoaded, items.contains("#the_vault:companion_relic"));
            assertTrue(items.contains("buildscape:ashenking_gold_pillar"));
            assertTrue(items.contains("buildscape:bone_dice"));
            assertTrue(items.contains("minecraft:diamond"));
        }
    }

    @Test
    public void userEntriesKeepOrderDuplicatesAndInvalidIdsWhenDefaultsAreAppended() throws Exception {
        File dir = temporaryFolder.newFolder("config", "buildscape", "pillar");
        File itemsFile = new File(dir, "pillar-items.json");
        String userContent = "{\"items\":[\"custom_mod:special_item\",\"Bad Id!\",\"custom_mod:special_item\"],\"custom\":42}";
        Files.writeString(itemsFile.toPath(), userContent);

        PillarParticleConfig config = new PillarParticleConfig(dir.toPath());
        config.loadItemsInternal(true);

        assertTrue(config.items.contains("custom_mod:special_item"));
        assertTrue(config.items.contains("buildscape:bone_dice"));
        assertFalse(config.items.contains("minecraft:diamond"));
        assertTrue(config.items.contains("the_vault:echo_pog"));
        String currentContent = Files.readString(itemsFile.toPath());
        com.google.gson.JsonObject merged = ConfigLayoutIO.readProperties(currentContent);
        assertEquals("custom_mod:special_item", merged.getAsJsonArray("items").get(0).getAsString());
        assertEquals("Bad Id!", merged.getAsJsonArray("items").get(1).getAsString());
        assertEquals("custom_mod:special_item", merged.getAsJsonArray("items").get(2).getAsString());
        assertEquals(42, merged.get("custom").getAsInt());
        assertEquals(userContent, Files.readString(itemsFile.toPath().resolveSibling("pillar-items.json.bak")));
        assertFalse(Files.exists(itemsFile.toPath().resolveSibling("pillar-items.json.pre-4.0.5.bak")));
    }

    @Test
    public void removedNewDefaultStaysRemovedAcrossLoadsAndSaves() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        java.nio.file.Path file = dir.resolve("pillar-items.json");
        Files.writeString(file, "{\"items\":[\"minecraft:stick\"]}");
        PillarParticleConfig config = new PillarParticleConfig(dir);
        config.loadItemsInternal(false);
        assertTrue(config.items.remove("buildscape:bone_dice"));
        assertTrue(config.saveItemsToDisk(false));
        String saved = Files.readString(file);
        new PillarParticleConfig(dir).loadItemsInternal(false);
        assertFalse(PillarParticleConfig.parseItems(Files.readString(file)).contains("buildscape:bone_dice"));
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void protectedFileIsUntouchedWithoutOfferingDefaults() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        java.nio.file.Path file = dir.resolve("pillar-items.json");
        String original = "{\"items\":[\"minecraft:stick\",7]}";
        Files.writeString(file, original);
        PillarParticleConfig config = new PillarParticleConfig(dir);
        config.loadItemsInternal(false);
        assertFalse(config.canEditItems());
        assertEquals(original, Files.readString(file));
        assertFalse(Files.exists(dir.resolve("pillar-items.json.pre-4.0.5.bak")));
    }

    @Test
    public void operatingSystemReadOnlyFileIsNotReplaced() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        java.nio.file.Path file = dir.resolve("pillar-items.json");
        String original = "{\"items\":[\"minecraft:stick\"]}";
        Files.writeString(file, original);
        java.nio.file.attribute.DosFileAttributeView dos = Files.getFileAttributeView(file,
                java.nio.file.attribute.DosFileAttributeView.class);
        org.junit.Assume.assumeNotNull(dos);
        dos.setReadOnly(true);
        try {
            PillarParticleConfig config = new PillarParticleConfig(dir);
            config.loadItemsInternal(false);
            assertFalse(config.canEditItems());
            assertEquals(original, Files.readString(file));
            assertFalse(Files.exists(dir.resolve("pillar-items.json.pre-4.0.5.bak")));
        } finally {
            dos.setReadOnly(false);
        }
    }

    @Test
    public void failedBackupLeavesFileAndLoadedEntriesIntact() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        java.nio.file.Path file = dir.resolve("pillar-items.json");
        String original = "{\"items\":[\"minecraft:stick\"]}";
        Files.writeString(file, original);
        Files.createDirectory(dir.resolve("pillar-items.json.bak"));
        PillarParticleConfig config = new PillarParticleConfig(dir);
        config.loadItemsInternal(false);
        assertEquals(Set.of("minecraft:stick"), config.items);
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void versionStampsWithoutAnOfferedListStillMergeAndUseANormalBackup() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        java.nio.file.Path file = dir.resolve("pillar-items.json");
        String original = "{\"items\":[\"minecraft:stick\"],\"defaults_version\":410,\"vault_defaults_version\":410}";
        Files.writeString(file, original);
        PillarParticleConfig config = new PillarParticleConfig(dir);
        config.loadItemsInternal(true);
        assertTrue(config.items.contains("minecraft:stick"));
        assertTrue(config.items.contains("buildscape:bone_dice"));
        assertTrue(config.items.contains("the_vault:echo_pog"));
        assertFalse(config.items.contains("minecraft:diamond"));
        assertTrue(ConfigLayoutIO.readProperties(Files.readString(file)).has("offered_defaults"));
        assertEquals(original, Files.readString(dir.resolve("pillar-items.json.bak")));
        assertFalse(Files.exists(dir.resolve("pillar-items.json.pre-4.0.5.bak")));
        Set<String> loaded = Set.copyOf(config.items);
        config.loadItemsInternal(true);
        assertEquals(loaded, config.items);
    }

    @Test
    public void addingVaultOffersAllVaultDefaultsOnce() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        java.nio.file.Path file = dir.resolve("pillar-items.json");
        Files.writeString(file, "{\"items\":[\"minecraft:stick\"]}");
        PillarParticleConfig config = new PillarParticleConfig(dir);
        config.loadItemsInternal(false);
        assertFalse(config.items.contains("#the_vault:companion_relic"));
        config.loadItemsInternal(true);
        assertTrue(config.items.remove("#the_vault:companion_relic"));
        assertTrue(config.items.contains("the_vault:echo_pog"));
        assertTrue(config.saveItemsToDisk(true));
        config.loadItemsInternal(true);
        assertFalse(config.items.contains("#the_vault:companion_relic"));
    }

    @Test
    public void freshTemplateIsNotRewrittenAndMetadataWaitsForARealSave() throws Exception {
        for (boolean vault : new boolean[]{false, true}) {
            java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
            java.nio.file.Path file = dir.resolve("pillar-items.json");
            String name = vault ? "vault_pillar_items" : "vanilla_pillar_items";
            byte[] template;
            try (java.io.InputStream input = getClass().getResourceAsStream("/data/buildscape/config/" + name + ".json")) {
                assertNotNull(input);
                template = input.readAllBytes();
            }
            PillarParticleConfig config = new PillarParticleConfig(dir);
            config.loadItemsInternal(vault);
            assertArrayEquals(template, Files.readAllBytes(file));
            assertFalse(ConfigLayoutIO.readProperties(ConfigLayoutIO.itemsJson(Files.readString(file))).has("offered_defaults"));
            assertFalse(Files.exists(dir.resolve("pillar-items.json.bak")));
            assertFalse(Files.exists(dir.resolve("pillar-items.json.pre-4.0.5.bak")));
            java.nio.file.attribute.FileTime modified = Files.getLastModifiedTime(file);
            config.loadItemsInternal(vault);
            assertEquals(modified, Files.getLastModifiedTime(file));
            config.items.add("example:custom");
            assertTrue(config.saveItemsToDisk(vault));
            assertTrue(ConfigLayoutIO.readProperties(Files.readString(file)).has("offered_defaults"));
            assertFalse(Files.exists(dir.resolve("pillar-items.json.pre-4.0.5.bak")));
        }
    }

    @Test
    public void saveBeforeLoadMergesBeforeRecordingOfferedDefaults() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        PillarParticleConfig config = new PillarParticleConfig(dir);
        config.items.add("minecraft:stick");
        assertTrue(config.saveItemsToDisk(false));
        assertTrue(config.items.contains("buildscape:bone_dice"));
        assertTrue(ConfigLayoutIO.readProperties(Files.readString(dir.resolve("pillar-items.json"))).has("offered_defaults"));
    }

    @Test
    public void complete410FileWithoutMetadataRemainsUntouchedOnLoad() throws Exception {
        java.nio.file.Path dir = temporaryFolder.newFolder().toPath();
        java.nio.file.Path file = dir.resolve("pillar-items.json");
        com.google.gson.JsonObject original = ConfigPaths.loadItemsTemplate("vanilla_pillar_items");
        original.addProperty("custom", "retained");
        String source = original.toString();
        Files.writeString(file, source);
        java.nio.file.attribute.FileTime modified = Files.getLastModifiedTime(file);
        PillarParticleConfig config = new PillarParticleConfig(dir);
        config.loadItemsInternal(false);
        assertEquals(source, Files.readString(file));
        assertEquals(modified, Files.getLastModifiedTime(file));
        assertFalse(Files.exists(dir.resolve("pillar-items.json.bak")));
        assertFalse(Files.exists(dir.resolve("pillar-items.json.pre-4.0.5.bak")));
    }
}

