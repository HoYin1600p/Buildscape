package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigUpdateTransferTest {
    private PillarParticleConfig largeConfig() {
        PillarParticleConfig config = new PillarParticleConfig();
        Set<String> items = new HashSet<>();
        for (int i = 0; i < 4_000; i++) {
            items.add("example:configured_item_" + i);
        }
        items.add("#forge:gems");
        config.replaceItems(items);
        return config;
    }

    @Test
    public void largeUpdateFitsEachServerboundPacketAndAppliesOnlyAtTheEnd() {
        PillarParticleConfig config = largeConfig();
        List<UpdateConfigPacket> packets = UpdateConfigPacket.split(config);
        assertTrue(packets.size() > 1);
        UpdateConfigPacket.Assembly assembly = new UpdateConfigPacket.Assembly();
        for (int i = 0; i < packets.size(); i++) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                packets.get(i).encode(buffer);
                assertTrue(buffer.readableBytes() < 32_767);
                SyncConfigPacket result = assembly.accept(UpdateConfigPacket.decode(buffer));
                if (i < packets.size() - 1) {
                    assertNull(result);
                } else {
                    assertNotNull(result);
                    assertEquals(config.items, result.items);
                    assertEquals(config.particle_speed, result.particle_speed, 0.0);
                }
            } finally {
                buffer.release();
            }
        }
    }

    @Test
    public void missingDuplicateAndMixedTransferChunksAreRejected() {
        List<UpdateConfigPacket> first = UpdateConfigPacket.split(largeConfig());
        List<UpdateConfigPacket> second = UpdateConfigPacket.split(largeConfig());
        assertThrows(IllegalArgumentException.class,
                () -> new UpdateConfigPacket.Assembly().accept(first.get(1)));
        UpdateConfigPacket.Assembly duplicate = new UpdateConfigPacket.Assembly();
        assertNull(duplicate.accept(first.get(0)));
        assertThrows(IllegalArgumentException.class, () -> duplicate.accept(first.get(0)));
        UpdateConfigPacket.Assembly mixed = new UpdateConfigPacket.Assembly();
        assertNull(mixed.accept(first.get(0)));
        assertThrows(IllegalArgumentException.class, () -> mixed.accept(second.get(1)));
    }

    @Test
    public void invalidResourceIdsCannotBeSent() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.replaceItems(Set.of("example:invalid\"id"));
        assertThrows(IllegalArgumentException.class, () -> UpdateConfigPacket.split(config));
    }

    @Test
    public void updatesAboveTwoMiBAreRejected() {
        PillarParticleConfig config = new PillarParticleConfig();
        Set<String> items = new HashSet<>();
        String prefix = "example:" + "a".repeat(240);
        for (int i = 0; i < 9_000; i++) {
            items.add(prefix + i);
        }
        config.replaceItems(items);
        assertThrows(IllegalArgumentException.class, () -> UpdateConfigPacket.split(config));
    }

    @Test
    public void uiFloatingPointBoundsMatchPacketBounds() {
        assertEquals(SyncConfigPacket.MAX_PARTICLE_SPEED,
                SyncConfigPacket.clampParticleValue(100.0, SyncConfigPacket.MAX_PARTICLE_SPEED), 0.0);
        assertEquals(SyncConfigPacket.MIN_PARTICLE_RATE,
                SyncConfigPacket.clampParticleValue(Double.NaN, SyncConfigPacket.MAX_PARTICLE_SPEED), 0.0);
    }
}
