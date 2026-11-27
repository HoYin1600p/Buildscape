package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.network.SyncConfigPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigSyncPortTest {
    @TempDir Path directory;

    @Test
    void syncCarriesRevisionAndFileProtectionToTheClient() throws Exception {
        Files.writeString(directory.resolve("pillar-properties.json"), "{ broken");
        PillarParticleConfig config = new PillarParticleConfig(directory);
        var load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
        config.revision = 7;
        SyncConfigPacket outgoing = new SyncConfigPacket(config);
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            outgoing.write(buffer);
            SyncConfigPacket incoming = new SyncConfigPacket(buffer);
            assertEquals(7, incoming.baseRevision);
            assertTrue(incoming.propertiesReadOnly);
            assertFalse(incoming.itemsReadOnly);
            assertFalse(buffer.isReadable());
            incoming.handle(null);
            assertEquals(7, PillarParticleConfig.peek().revision);
            assertFalse(PillarParticleConfig.peek().canEditProperties());
        } finally {
            buffer.release();
            PillarParticleConfig.clearServerConfig();
        }
    }

    @Test
    void syncLimitsUnicodeByEncodedBytes() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.pattern = "\u20ac".repeat(32);
        String tooLong = "example:" + "\u00e9".repeat(128);
        config.items.add(tooLong);
        SyncConfigPacket outgoing = new SyncConfigPacket(config);
        assertEquals("ring", outgoing.pattern);
        assertFalse(outgoing.items.contains(tooLong));
    }

    @Test
    void anItemOmittedFromSyncCannotBeMistakenForAClientRemoval() throws Exception {
        String hidden = "example:" + "x".repeat(249);
        var document = new com.google.gson.JsonObject();
        document.addProperty("defaults_version", 405);
        var items = new com.google.gson.JsonArray();
        items.add("minecraft:stick");
        items.add(hidden);
        document.add("items", items);
        Files.writeString(directory.resolve("pillar-items.json"), document.toString());
        PillarParticleConfig server = new PillarParticleConfig(directory);
        server.loadItemsInternal(false);
        server.establishBaseline();
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new SyncConfigPacket(server).write(buffer);
            SyncConfigPacket client = new SyncConfigPacket(buffer);
            assertFalse(client.items.contains(hidden));
            client.items.add("minecraft:diamond");
            assertTrue(server.applyUpload(client, java.util.EnumSet.of(ConfigSetting.ITEMS)));
            assertTrue(server.items.contains(hidden));
            assertTrue(server.saveItemsToDisk(false));
            assertTrue(PillarParticleConfig.parseItems(
                    Files.readString(directory.resolve("pillar-items.json"))).contains(hidden));
        } finally {
            buffer.release();
        }
    }
}
