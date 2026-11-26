package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTransferPortTest {
    private PillarParticleConfig largeConfig(int count, int suffixLength) {
        PillarParticleConfig config = new PillarParticleConfig();
        String suffix = "a".repeat(suffixLength);
        for (int i = 0; i < count; i++) config.items.add("example:item_" + i + "_" + suffix);
        config.establishBaseline();
        return config;
    }

    @Test
    void largeUploadUsesBoundedPacketsAndAppliesOnlyWhenComplete() {
        PillarParticleConfig config = largeConfig(3000, 20);
        var packets = UpdateConfigPacket.split(config);
        assertTrue(packets.size() > 1);
        ConfigAssembly assembly = new ConfigAssembly();
        for (int i = 0; i < packets.size(); i++) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                packets.get(i).write(buffer);
                assertTrue(buffer.readableBytes() < 24500);
                SyncConfigPacket complete = assembly.accept(new UpdateConfigPacket(buffer));
                if (i < packets.size() - 1) assertNull(complete);
                else {
                    assertNotNull(complete);
                    assertEquals(config.items, complete.items);
                    assertEquals(config.revision, complete.baseRevision);
                }
                assertFalse(buffer.isReadable());
            } finally {
                buffer.release();
            }
        }
    }

    @Test
    void missingDuplicateAndMixedTransferChunksAreRejected() {
        var packets = UpdateConfigPacket.split(largeConfig(3000, 20));
        var other = UpdateConfigPacket.split(largeConfig(3000, 20));
        assertThrows(IllegalArgumentException.class, () -> new ConfigAssembly().accept(packets.get(1)));
        ConfigAssembly duplicate = new ConfigAssembly();
        duplicate.accept(packets.get(0));
        assertThrows(IllegalArgumentException.class, () -> duplicate.accept(packets.get(0)));
        ConfigAssembly mixed = new ConfigAssembly();
        mixed.accept(packets.get(0));
        assertThrows(IllegalArgumentException.class, () -> mixed.accept(other.get(1)));
    }

    @Test
    void oversizedUploadIsRefusedBeforeSendingAnyPackets() {
        assertThrows(IllegalArgumentException.class,
                () -> UpdateConfigPacket.split(largeConfig(20000, 120)));
    }
}
