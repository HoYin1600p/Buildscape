package com.kingodogo.buildscape.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultPillarItems405Test {
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
    public void userModificationsAreNeverOverwrittenOnLoad() throws Exception {
        File dir = temporaryFolder.newFolder("config", "buildscape", "pillar");
        File itemsFile = new File(dir, "pillar-items.json");
        String userContent = "{\n  \"items\": [\n    \"custom_mod:special_item\"\n  ]\n}";
        Files.writeString(itemsFile.toPath(), userContent);

        PillarParticleConfig config = new PillarParticleConfig(dir.toPath());
        config.loadItemsInternal(true);

        assertEquals(Set.of("custom_mod:special_item"), config.items);
        String currentContent = Files.readString(itemsFile.toPath());
        assertEquals("User file must not be modified or overwritten on launch", userContent, currentContent);
    }
}

