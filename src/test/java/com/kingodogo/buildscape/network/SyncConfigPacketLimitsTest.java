package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SyncConfigPacketLimitsTest extends BootstrappedGameTest {
    @Test
    public void overlongPatternAndItemEncodeWithRingAndOnlyNormalItems() {
        PillarParticleConfig config = normalConfig();
        String longPattern = "p".repeat(65);
        String longItem = "minecraft:" + "a".repeat(290);
        config.pattern = longPattern;
        config.replaceItems(Set.of("minecraft:stick", "#forge:gems", longItem));

        SyncConfigPacket packet = new SyncConfigPacket(config);
        assertEquals(ParticlePatterns.RING, packet.pattern);
        SyncConfigPacket decoded = roundTrip(packet);

        assertEquals(ParticlePatterns.RING, decoded.pattern);
        assertEquals(Set.of("minecraft:stick", "#forge:gems"), decoded.items);
        assertFalse(decoded.items.contains(longItem));
        assertUnchangedSettings(config, decoded);
        assertEquals(longPattern, config.pattern);
        assertEquals(Set.of("minecraft:stick", "#forge:gems", longItem), config.items);
    }

    @Test
    public void overlongItemIsDroppedWhileNormalPatternAndValuesRoundTrip() {
        PillarParticleConfig config = normalConfig();
        String longItem = "minecraft:" + "a".repeat(290);
        config.replaceItems(Set.of("minecraft:stick", "#forge:gems", longItem));

        SyncConfigPacket packet = new SyncConfigPacket(config);
        assertFalse(packet.items.contains(longItem));
        SyncConfigPacket decoded = roundTrip(packet);

        assertEquals(ParticlePatterns.SPIRAL, decoded.pattern);
        assertEquals(Set.of("minecraft:stick", "#forge:gems"), decoded.items);
        assertFalse(decoded.items.contains(longItem));
        assertUnchangedSettings(config, decoded);
        assertEquals(ParticlePatterns.SPIRAL, config.pattern);
        assertEquals(Set.of("minecraft:stick", "#forge:gems", longItem), config.items);
    }

    @Test
    public void multibytePatternOverByteLimitEncodesWithRing() {
        PillarParticleConfig config = normalConfig();
        String longPattern = "\u00e9".repeat(33);
        config.pattern = longPattern;
        config.replaceItems(Set.of("minecraft:stick", "#forge:gems"));

        SyncConfigPacket decoded = roundTrip(new SyncConfigPacket(config));

        assertEquals(ParticlePatterns.RING, decoded.pattern);
        assertEquals(config.items, decoded.items);
        assertUnchangedSettings(config, decoded);
        assertEquals(longPattern, config.pattern);
    }

    @Test
    public void multibyteItemOverByteLimitIsDroppedDuringRoundTrip() {
        PillarParticleConfig config = normalConfig();
        String longItem = "minecraft:" + "\u00e9".repeat(129);
        config.replaceItems(Set.of("minecraft:stick", "#forge:gems", longItem));

        SyncConfigPacket decoded = roundTrip(new SyncConfigPacket(config));

        assertEquals(ParticlePatterns.SPIRAL, decoded.pattern);
        assertEquals(Set.of("minecraft:stick", "#forge:gems"), decoded.items);
        assertFalse(decoded.items.contains(longItem));
        assertUnchangedSettings(config, decoded);
        assertEquals(Set.of("minecraft:stick", "#forge:gems", longItem), config.items);
    }

    @Test
    public void multibytePatternAtByteLimitIsPreservedDuringRoundTrip() {
        PillarParticleConfig config = normalConfig();
        String pattern = "\u00e9".repeat(32);
        config.pattern = pattern;
        config.replaceItems(Set.of("minecraft:stick", "#forge:gems"));

        SyncConfigPacket decoded = roundTrip(new SyncConfigPacket(config));

        assertEquals(pattern, decoded.pattern);
        assertEquals(config.items, decoded.items);
        assertUnchangedSettings(config, decoded);
        assertEquals(pattern, config.pattern);
    }

    private static PillarParticleConfig normalConfig() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.particle_speed = 0.37;
        config.particle_spread = 0.61;
        config.particle_lifetime = 83;
        config.particle_density = 7;
        config.use_pattern = true;
        config.pattern = ParticlePatterns.SPIRAL;
        config.pattern_speed = 0.29;
        config.pattern_spread = 0.43;
        config.pattern_intensity = 0.71;
        config.particle_color = List.of("red", "blue");
        config.max_particle_color = 2;
        config.edit_permission_mode = PillarParticleConfig.EditPermissionMode.STANDARD;
        config.revision = 17;
        return config;
    }

    private static SyncConfigPacket roundTrip(SyncConfigPacket packet) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            packet.encode(buffer);
            SyncConfigPacket decoded = SyncConfigPacket.decode(buffer);
            assertEquals(0, buffer.readableBytes());
            return decoded;
        } finally {
            buffer.release();
        }
    }

    private static void assertUnchangedSettings(PillarParticleConfig config, SyncConfigPacket decoded) {
        assertEquals(config.particle_speed, decoded.particle_speed, 0.0);
        assertEquals(config.particle_spread, decoded.particle_spread, 0.0);
        assertEquals(config.particle_lifetime, decoded.particle_lifetime);
        assertEquals(config.particle_density, decoded.particle_density);
        assertEquals(config.use_pattern, decoded.use_pattern);
        assertEquals(config.pattern_speed, decoded.pattern_speed, 0.0);
        assertEquals(config.pattern_spread, decoded.pattern_spread, 0.0);
        assertEquals(config.pattern_intensity, decoded.pattern_intensity, 0.0);
        assertEquals(config.particle_color, decoded.particle_color);
        assertEquals(config.max_particle_color, decoded.max_particle_color);
        assertEquals(config.edit_permission_mode, decoded.edit_permission_mode);
        assertEquals(config.revision, decoded.baseRevision);
        assertEquals(!config.canEditItems(), decoded.itemsReadOnly);
        assertEquals(!config.canEditProperties(), decoded.propertiesReadOnly);
    }
}
