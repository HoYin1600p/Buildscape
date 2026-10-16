package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.network.SyncConfigPacket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PillarConfigPersistenceTest {
    @TempDir Path directory;

    @Test
    void receivedConfigAndPresetSavesCannotOverwriteLocalFiles() throws Exception {
        PillarParticleConfig local = new PillarParticleConfig(directory);
        local.items.add("minecraft:diamond");
        local.particle_speed = 0.125;
        local.saveItemsToDisk();
        local.savePropertiesToDisk();
        String itemsBefore = Files.readString(directory.resolve("pillar-items.json"));
        String propertiesBefore = Files.readString(directory.resolve("pillar-properties.json"));

        Field instance = PillarParticleConfig.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        Object previous = instance.get(null);
        Field statTime = PillarParticleConfig.class.getDeclaredField("lastStatCheckTime");
        statTime.setAccessible(true);
        long previousTime = statTime.getLong(null);
        try {
            instance.set(null, local);
            statTime.setLong(null, System.currentTimeMillis());
            PillarParticleConfig remote = new PillarParticleConfig();
            remote.items.add("the_vault:echo_pog");
            remote.particle_speed = 0.75;
            SyncConfigPacket packet = new SyncConfigPacket(remote);
            packet.permission_level = 2;
            packet.handle(null);

            PillarParticleConfig received = PillarParticleConfig.get();
            assertNotSame(local, received);
            assertSame(local, PillarParticleConfig.getServerConfig());
            assertSame(received, PillarParticleConfig.peek());
            assertEquals(0.75, received.particle_speed);
            packet.items.clear();
            assertTrue(received.items.contains("the_vault:echo_pog"));

            // Applying a preset mutates the active set and requests a disk save.
            received.items = new HashSet<>(Set.of("minecraft:emerald"));
            received.saveItemsToDisk();
            received.savePropertiesToDisk();
            assertEquals(Set.of("minecraft:diamond"), local.items);
            assertEquals(0.125, local.particle_speed);
            assertEquals(itemsBefore, Files.readString(directory.resolve("pillar-items.json")));
            assertEquals(propertiesBefore, Files.readString(directory.resolve("pillar-properties.json")));

            PillarParticleConfig.clearServerConfig();
            assertSame(local, PillarParticleConfig.get());
            assertSame(local, PillarParticleConfig.peek());
            assertEquals(-1, PillarParticleConfig.getClientPermissionLevel());
            // A stale screen reference remains unable to save after disconnect.
            received.saveItemsToDisk();
            local.saveItemsToDisk();
            local.savePropertiesToDisk();
            assertEquals(itemsBefore, Files.readString(directory.resolve("pillar-items.json")));
            assertEquals(propertiesBefore, Files.readString(directory.resolve("pillar-properties.json")));
        } finally {
            PillarParticleConfig.clearServerConfig();
            instance.set(null, previous);
            statTime.setLong(null, previousTime);
        }
    }

    @Test
    void itemSavesHaveStableSortedOrder() throws Exception {
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.items.addAll(List.of("minecraft:zombie_head", "#the_vault:keys", "minecraft:diamond"));
        config.saveItemsToDisk();
        Path file = directory.resolve("pillar-items.json");
        String first = Files.readString(file);
        assertTrue(first.indexOf("#the_vault:keys") < first.indexOf("minecraft:diamond"));
        assertTrue(first.indexOf("minecraft:diamond") < first.indexOf("minecraft:zombie_head"));
        config.items = new HashSet<>(List.of("minecraft:diamond", "minecraft:zombie_head", "#the_vault:keys"));
        config.saveItemsToDisk();
        assertEquals(first, Files.readString(file));
    }

    @Test
    void invalidItemsAreBackedUpBeforeAnExplicitSave() throws Exception {
        assertCorruptedFilePreserved("pillar-items.json", "loadItemsInternal", false);
    }

    @Test
    void invalidPropertiesAreBackedUpBeforeAnExplicitSave() throws Exception {
        assertCorruptedFilePreserved("pillar-properties.json", "loadPropertiesInternal", true);
    }

    private void assertCorruptedFilePreserved(String name, String loader, boolean properties) throws Exception {
        Path file = directory.resolve(name);
        String broken = "{ invalid JSON";
        Files.writeString(file, broken);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        Method load = PillarParticleConfig.class.getDeclaredMethod(loader);
        load.setAccessible(true);
        load.invoke(config);
        assertEquals(broken, Files.readString(file));
        if (properties) config.savePropertiesToDisk();
        else config.saveItemsToDisk();
        try (var files = Files.list(directory)) {
            List<Path> backups = files.filter(path -> path.getFileName().toString().startsWith(name + ".corrupted-"))
                    .toList();
            assertEquals(1, backups.size());
            assertEquals(broken, Files.readString(backups.get(0)));
        }
        assertNotEquals(broken, Files.readString(file));
    }

    @Test
    void vaultMergeIsConditionalAdditiveAndIdempotent() {
        Set<String> items = new HashSet<>(Set.of("minecraft:diamond", "other:custom"));
        assertFalse(VaultPillarItems.merge(items, false));
        assertEquals(2, items.size());
        assertTrue(VaultPillarItems.merge(items, true));
        assertEquals(47, items.size());
        assertTrue(items.containsAll(Set.of("the_vault:echo_pog", "#the_vault:vault_gear", "the_vault:yeti_egg", "other:custom")));
        assertFalse(VaultPillarItems.merge(items, true));
    }

    @Test
    void missingSourceCannotBeSilentlyBackedUp() {
        assertThrows(java.io.IOException.class, () -> ConfigBackup.copyCorrupted(directory.resolve("missing.json")));
    }
}
