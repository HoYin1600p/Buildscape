package com.kingodogo.buildscape.config;

import com.google.gson.JsonObject;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.Assert.*;

public class Config410RegressionTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void densityZeroSurvivesSaveReloadAndMissingDensityKeepsPreviousValue() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-properties.json");
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.particle_density = 7;
        Files.writeString(file, "{}");
        invoke(config, "loadPropertiesInternal");
        assertEquals(7, config.particle_density);
        Files.writeString(file, "{\"particle_density\":0}");
        invoke(config, "loadPropertiesInternal");
        assertEquals(0, config.particle_density);
        assertTrue(config.savePropertiesToDisk());
        PillarParticleConfig reloaded = new PillarParticleConfig(directory);
        invoke(reloaded, "loadPropertiesInternal");
        assertEquals(0, reloaded.particle_density);
        Files.writeString(file, "{\"particle_density\":6}");
        invoke(reloaded, "loadPropertiesInternal");
        assertEquals(6, reloaded.particle_density);
        Files.writeString(file, "{\"particle_density\":-1}");
        invoke(reloaded, "loadPropertiesInternal");
        assertEquals(0, reloaded.particle_density);
    }

    @Test
    public void zeroDensitySurvivesBothPacketDirectionsAndSnapshotsSwitchImmediately() throws Exception {
        Field local = field("INSTANCE");
        Field remote = field("SERVER_CONFIG");
        Field snapshot = field("SNAPSHOT");
        Object oldLocal = local.get(null);
        Object oldRemote = remote.get(null);
        Object oldSnapshot = snapshot.get(null);
        try {
            PillarParticleConfig localConfig = new PillarParticleConfig();
            local.set(null, localConfig);
            snapshot.set(null, localConfig);
            PillarParticleConfig config = new PillarParticleConfig();
            config.particle_density = 0;
            config.replaceItems(Set.of("minecraft:stick"));
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            SyncConfigPacket decoded;
            try {
                new SyncConfigPacket(config).encode(buffer);
                decoded = SyncConfigPacket.decode(buffer);
            } finally {
                buffer.release();
            }
            assertEquals(0, decoded.particle_density);
            PillarParticleConfig.setServerConfig(decoded);
            assertSame(remote.get(null), PillarParticleConfig.peek());
            assertEquals(0, PillarParticleConfig.peek().particle_density);
            UpdateConfigPacket.Assembly assembly = new UpdateConfigPacket.Assembly();
            SyncConfigPacket assembled = null;
            for (UpdateConfigPacket packet : UpdateConfigPacket.split(config)) {
                FriendlyByteBuf update = new FriendlyByteBuf(Unpooled.buffer());
                try {
                    packet.encode(update);
                    assembled = assembly.accept(UpdateConfigPacket.decode(update));
                } finally {
                    update.release();
                }
            }
            assertNotNull(assembled);
            assertEquals(0, assembled.particle_density);
            PillarParticleConfig.clearServerConfig();
            assertSame(localConfig, PillarParticleConfig.peek());
        } finally {
            local.set(null, oldLocal);
            remote.set(null, oldRemote);
            snapshot.set(null, oldSnapshot);
        }
    }

    @Test
    public void incompleteMigrationRetriesEvenWithAnExistingBackup() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path directory = Files.createDirectory(root.resolve("pillar"));
        Path legacy = root.resolve("pillar-particles.json");
        Path backup = root.resolve("pillar-particles.json.backup");
        Files.writeString(legacy, "{\"particle_density\":0,\"items\":[\"minecraft:stick\"]}");
        Files.writeString(backup, "previous backup");
        String existing = "{\"particle_density\":8}";
        Files.writeString(directory.resolve("pillar-properties.json"), existing);
        invoke(new PillarParticleConfig(directory), "migrateOldConfig");
        assertEquals(Set.of("minecraft:stick"), PillarParticleConfig.parseItems(
                Files.readString(directory.resolve("pillar-items.json"))));
        assertEquals(existing, Files.readString(directory.resolve("pillar-properties.json")));
        assertEquals("previous backup", Files.readString(backup));
        Files.delete(directory.resolve("pillar-items.json"));
        invoke(new PillarParticleConfig(directory), "migrateOldConfig");
        assertFalse(Files.exists(directory.resolve("pillar-items.json")));
    }

    @Test
    public void failedMigrationDoesNotCreateDefaultsOrCompletionBackup() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path directory = Files.createDirectory(root.resolve("pillar"));
        Path legacy = root.resolve("pillar-particles.json");
        Path backup = root.resolve("pillar-particles.json.backup");
        Files.writeString(legacy, "{\"particle_density\":0,\"items\":{}}");
        PillarParticleConfig config = new PillarParticleConfig(directory);
        invoke(config, "loadInternal");
        assertFalse(Files.exists(directory.resolve("pillar-items.json")));
        assertFalse(Files.exists(backup));
        assertEquals(0, config.particle_density);
        String recovered = "{\"particle_density\":0,\"items\":[\"minecraft:stick\"]}";
        Files.writeString(legacy, recovered);
        invoke(config, "migrateOldConfig");
        assertTrue(Files.exists(directory.resolve("pillar-items.json")));
        assertEquals(recovered, Files.readString(backup));
    }

    @Test
    public void skippedPresetAndItemEntriesAreBackedUpWithoutBlockingOrdinarySaves() throws Exception {
        for (String malformed : new String[]{"\"broken\":{\"items\":null},",
                "\"partial\":{\"name\":\"Partial\",\"items\":[\"minecraft:stick\",7,null]},"}) {
            Path directory = temporary.newFolder().toPath();
            Path file = directory.resolve("pillar-presets.json");
            String source = "{" + malformed + "\"default\":{\"name\":\"Default\",\"items\":[]},\"_lastApplied\":\"default\"}";
            Files.writeString(file, source);
            PresetsConfig config = new PresetsConfig(directory);
            config.load();
            assertEquals(source, Files.readString(file.resolveSibling("pillar-presets.json.bak")));
            assertNotNull(config.getPreset("default"));
            assertTrue(config.savePreset("custom", "Custom", Set.of("minecraft:diamond")));
            JsonObject saved = ConfigLayoutIO.readProperties(Files.readString(file));
            assertTrue(saved.has("custom"));
            assertEquals("default", saved.get("_lastApplied").getAsString());
            config.load();
            config.save();
            assertEquals(source, Files.readString(file.resolveSibling("pillar-presets.json.bak")));
        }
    }

    private static Field field(String name) throws Exception {
        Field field = PillarParticleConfig.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static void invoke(PillarParticleConfig config, String name) throws Exception {
        Method method = PillarParticleConfig.class.getDeclaredMethod(name);
        method.setAccessible(true);
        method.invoke(config);
    }
}
