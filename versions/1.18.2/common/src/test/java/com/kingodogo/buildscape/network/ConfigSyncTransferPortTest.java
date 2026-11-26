package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigSyncTransferPortTest {
    private SyncConfigPacket largeSync() {
        PillarParticleConfig config = new PillarParticleConfig();
        for (int i = 0; i < 20000; i++) config.items.add("example:item_" + i + "_" + "x".repeat(70));
        config.establishBaseline();
        return new SyncConfigPacket(config);
    }

    @Test
    void largeSyncFitsVanillaPayloadLimitAndAppliesOnlyWhenComplete() {
        SyncConfigPacket config = largeSync();
        FriendlyByteBuf full = new FriendlyByteBuf(Unpooled.buffer());
        try {
            config.write(full);
            assertTrue(full.readableBytes() > 1048576);
            assertThrows(IllegalArgumentException.class, () -> new ClientboundCustomPayloadPacket(
                    new ResourceLocation("buildscape", "sync_config_v2"), full));
        } finally {
            full.release();
        }
        var parts = SyncConfigChunkPacket.split(config);
        SyncConfigChunkPacket.Assembly assembly = new SyncConfigChunkPacket.Assembly();
        for (int i = 0; i < parts.size(); i++) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                parts.get(i).write(buffer);
                assertTrue(buffer.readableBytes() < 24500);
                new ClientboundCustomPayloadPacket(new ResourceLocation("buildscape", "sync_config_part_v2"), buffer);
                SyncConfigPacket complete = assembly.accept(new SyncConfigChunkPacket(buffer), 1000L + i);
                if (i < parts.size() - 1) assertNull(complete);
                else {
                    assertNotNull(complete);
                    assertEquals(config.items, complete.items);
                    assertEquals(config.baseRevision, complete.baseRevision);
                }
            } finally {
                buffer.release();
            }
        }
    }

    @Test
    void duplicateOutOfOrderAndExpiredSyncPartsAreRejected() {
        var parts = SyncConfigChunkPacket.split(largeSync());
        assertThrows(IllegalArgumentException.class,
                () -> new SyncConfigChunkPacket.Assembly().accept(parts.get(1), 1000));
        var duplicate = new SyncConfigChunkPacket.Assembly();
        duplicate.accept(parts.get(0), 1000);
        assertThrows(IllegalArgumentException.class, () -> duplicate.accept(parts.get(0), 1001));
        var expired = new SyncConfigChunkPacket.Assembly();
        expired.accept(parts.get(0), 1000);
        assertThrows(IllegalArgumentException.class, () -> expired.accept(parts.get(1), 31001));
    }
}
