package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class SyncConfigProtocolTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void protocolVersionPinsTheCurrentPacketLayout() {
        assertEquals("8", ModMessages.PROTOCOL_VERSION);
    }

    @Test
    public void everyFieldAndBothReadOnlyFlagsRoundTrip() {
        for (boolean itemsReadOnly : new boolean[]{false, true}) {
            for (boolean propertiesReadOnly : new boolean[]{false, true}) {
                PillarParticleConfig config = new PillarParticleConfig();
                config.particle_color = List.of("red", "blue");
                config.max_particle_color = 2;
                config.replaceItems(Set.of("minecraft:stick", "#forge:gems"));
                SyncConfigPacket original = new SyncConfigPacket(config);
                original.itemsReadOnly = itemsReadOnly;
                original.propertiesReadOnly = propertiesReadOnly;
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                try {
                    original.encode(buffer);
                    SyncConfigPacket decoded = SyncConfigPacket.decode(buffer);
                    assertEquals(original.particle_speed, decoded.particle_speed, 0.0);
                    assertEquals(original.particle_spread, decoded.particle_spread, 0.0);
                    assertEquals(original.particle_lifetime, decoded.particle_lifetime);
                    assertEquals(original.particle_density, decoded.particle_density);
                    assertEquals(original.use_pattern, decoded.use_pattern);
                    assertEquals(original.pattern, decoded.pattern);
                    assertEquals(original.pattern_speed, decoded.pattern_speed, 0.0);
                    assertEquals(original.pattern_spread, decoded.pattern_spread, 0.0);
                    assertEquals(original.pattern_intensity, decoded.pattern_intensity, 0.0);
                    assertEquals(original.particle_color, decoded.particle_color);
                    assertEquals(original.max_particle_color, decoded.max_particle_color);
                    assertEquals(original.items, decoded.items);
                    assertEquals(itemsReadOnly, decoded.itemsReadOnly);
                    assertEquals(propertiesReadOnly, decoded.propertiesReadOnly);
                    assertEquals(0, buffer.readableBytes());
                } finally {
                    buffer.release();
                }
            }
        }
    }
}
