package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.google.gson.Gson;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConfigItemValidationAndEncodingTest extends BootstrappedGameTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void invalidLoadedItemsSurviveValidEditsAndPresetReplacement() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-items.json");
        String original = "{\"defaults_version\":405,\"items\":[\"Bad Id!\",\"#Bad Tag!\",\"minecraft:stick\"]}";
        Files.writeString(file, original, StandardCharsets.UTF_8);
        assertEquals(Set.of("Bad Id!", "#Bad Tag!", "minecraft:stick"), PillarParticleConfig.parseItems(original));
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        assertTrue(config.canEditItems());
        assertEquals(Set.of("Bad Id!", "#Bad Tag!"), config.getInvalidItems());
        assertFalse(config.addItem("New Bad Id!"));
        assertFalse(config.addItem("#"));
        assertFalse(config.removeItem("Bad Id!"));
        config.editItems(Set.of("minecraft:diamond", "#forge:gems"));
        assertTrue(config.saveItemsToDisk(false));
        assertEquals(Set.of("Bad Id!", "#Bad Tag!", "minecraft:diamond", "#forge:gems"),
                PillarParticleConfig.parseItems(Files.readString(file, StandardCharsets.UTF_8)));
        assertEquals(original,
                Files.readString(directory.resolve("pillar-items.json.pre-4.0.5.bak"), StandardCharsets.UTF_8));
        config.loadItemsInternal(false);
        assertEquals(Set.of("Bad Id!", "#Bad Tag!"), config.getInvalidItems());
    }

    @Test
    public void invalidItemSpellingEscapesAndDuplicatesSurviveLoadAndSaving() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-items.json");
        String escaped = "\"\\u0042ad Id!\"";
        String original = "{\"items\":[" + escaped + ",\"Bad Id!\",\"minecraft:stick\"]}";
        Files.writeString(file, original, StandardCharsets.UTF_8);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.loadItemsInternal(false);
        assertTrue(Files.readString(file, StandardCharsets.UTF_8).contains(escaped));
        config.editItems(Set.of("minecraft:diamond"));
        assertTrue(config.saveItemsToDisk(false));
        String saved = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(saved.contains(escaped));
        int invalidCount = 0;
        for (com.google.gson.JsonElement entry : ConfigLayoutIO.readProperties(saved).getAsJsonArray("items")) {
            if (entry.getAsString().equals("Bad Id!")) {
                invalidCount++;
            }
        }
        assertEquals(2, invalidCount);
    }

    @Test
    public void utf8TextRoundTripsThroughEverySharedWriter() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("buildscape.cfg");
        String original = "# Caf\u00e9 \u96ea\nCustomLabel = Cr\u00e8me\n";
        ConfigLayoutIO.writeNew(file, original);
        assertArrayEquals(original.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(file));
        assertEquals(original, ConfigLayoutIO.readText(file));
        assertFalse(ConfigLayoutIO.protectInvalidEncoding(file));
        ConfigLayoutIO.append(file, "Other = \u00e9\n", original);
        String appended = original + "Other = \u00e9\n";
        assertEquals(appended, ConfigLayoutIO.readText(file));
        ConfigLayoutIO.write(file, original, appended, true);
        assertArrayEquals(original.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(file));
    }

    @Test
    public void utf8ClientConfigPropertiesItemsAndPresetNamesSurviveSaves() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path clientFile = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(clientFile.getParent());
        Files.writeString(clientFile, "# Caf\u00e9\nCustomLabel = \u96ea\n", StandardCharsets.UTF_8);
        BuildscapeClientConfig client = new BuildscapeClientConfig(root);
        client.save();
        assertTrue(Files.readString(clientFile, StandardCharsets.UTF_8).contains("CustomLabel = \u96ea"));
        Path directory = temporary.newFolder().toPath();
        Path properties = directory.resolve("pillar-properties.json");
        Files.writeString(properties, "{\"particle_density\":7,\"custom_label\":\"Caf\u00e9 \u96ea\"}",
                StandardCharsets.UTF_8);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        loadProperties(config);
        assertTrue(config.canEditProperties());
        config.particle_density = 8;
        assertTrue(config.savePropertiesToDisk());
        assertEquals("Caf\u00e9 \u96ea",
                ConfigLayoutIO.readProperties(Files.readString(properties, StandardCharsets.UTF_8))
                        .get("custom_label").getAsString());
        Path items = directory.resolve("pillar-items.json");
        Files.writeString(items, "{\"defaults_version\":405,\"items\":[\"Caf\u00e9 \u96ea\",\"minecraft:stick\"]}",
                StandardCharsets.UTF_8);
        config.loadItemsInternal(false);
        config.editItems(Set.of("minecraft:diamond"));
        assertTrue(config.saveItemsToDisk(false));
        assertTrue(PillarParticleConfig.parseItems(Files.readString(items, StandardCharsets.UTF_8))
                .contains("Caf\u00e9 \u96ea"));
        Path presets = directory.resolve("pillar-presets.json");
        Files.writeString(presets, "{\"default\":{\"name\":\"Caf\u00e9 \u96ea\",\"items\":[]}}",
                StandardCharsets.UTF_8);
        PresetsConfig presetConfig = new PresetsConfig(directory);
        presetConfig.load();
        assertTrue(presetConfig.canEdit());
        presetConfig.save();
        presetConfig.load();
        assertEquals("Caf\u00e9 \u96ea", presetConfig.getPreset("default").name);
    }

    @Test
    public void protectedFilesRefusePacketsBeforeAnyValueChanges() throws Exception {
        for (String name : new String[]{"pillar-properties.json", "pillar-items.json"}) {
            Path directory = temporary.newFolder().toPath();
            PillarParticleConfig server = new PillarParticleConfig(directory);
            server.replaceItems(Set.of("minecraft:stick"));
            SyncConfigPacket incoming = changedValues();
            byte[] badBytes = "// Caf@".getBytes(StandardCharsets.US_ASCII);
            badBytes[badBytes.length - 1] = (byte) 0x81;
            Path file = directory.resolve(name);
            Files.write(file, badBytes);
            String before = new Gson().toJson(server);
            String refusal = UpdateConfigPacket.applyValues(server, incoming);
            assertNotNull(refusal);
            assertTrue(refusal.contains("read-only"));
            assertEquals(before, new Gson().toJson(server));
            assertArrayEquals(badBytes, Files.readAllBytes(file));
            assertArrayEquals(badBytes, Files.readAllBytes(file.resolveSibling(name + ".bak")));
        }
    }

    @Test
    public void uploadsRefuseNewInvalidIdsButPreserveExistingInvalidIds() throws Exception {
        PillarParticleConfig server = new PillarParticleConfig(temporary.newFolder().toPath());
        server.replaceItems(Set.of("Bad Id!", "minecraft:stick"));
        SyncConfigPacket incoming = changedValues();
        incoming.items = Set.of("New Bad Id!", "minecraft:diamond");
        String before = new Gson().toJson(server);
        String refusal = UpdateConfigPacket.applyValues(server, incoming);
        assertNotNull(refusal);
        assertTrue(refusal.contains("New Bad Id!"));
        assertEquals(before, new Gson().toJson(server));
        incoming.items = Set.of("Bad Id!", "minecraft:diamond");
        assertNull(UpdateConfigPacket.applyValues(server, incoming));
        assertEquals(Set.of("Bad Id!", "minecraft:diamond"), server.items);
        incoming.items = Set.of("minecraft:emerald");
        assertNull(UpdateConfigPacket.applyValues(server, incoming));
        assertEquals(Set.of("Bad Id!", "minecraft:emerald"), server.items);
    }

    @Test
    public void readOnlyFlagsRoundTripAndProtectRemoteScreens() throws Exception {
        Field remote = field("SERVER_CONFIG");
        Field snapshot = field("SNAPSHOT");
        Object oldRemote = remote.get(null);
        Object oldSnapshot = snapshot.get(null);
        try {
            for (boolean protectItems : new boolean[]{false, true}) {
                PillarParticleConfig config = new PillarParticleConfig(temporary.newFolder().toPath());
                field(protectItems ? "itemsReadOnly" : "propertiesReadOnly").setBoolean(config, true);
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                try {
                    new SyncConfigPacket(config).encode(buffer);
                    SyncConfigPacket decoded = SyncConfigPacket.decode(buffer);
                    assertEquals(protectItems, decoded.itemsReadOnly);
                    assertEquals(!protectItems, decoded.propertiesReadOnly);
                    assertFalse(buffer.isReadable());
                    PillarParticleConfig.setServerConfig(decoded);
                    PillarParticleConfig synced = (PillarParticleConfig) remote.get(null);
                    assertEquals(!protectItems, synced.canEditItems());
                    assertEquals(protectItems, synced.canEditProperties());
                } finally {
                    buffer.release();
                }
            }
        } finally {
            remote.set(null, oldRemote);
            snapshot.set(null, oldSnapshot);
        }
    }

    private static SyncConfigPacket changedValues() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.particle_density = 97;
        config.pattern = "spiral";
        config.replaceItems(Set.of("minecraft:diamond"));
        return new SyncConfigPacket(config);
    }

    private static void loadProperties(PillarParticleConfig config) throws Exception {
        Method load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
    }

    private static Field field(String name) throws Exception {
        Field field = PillarParticleConfig.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
