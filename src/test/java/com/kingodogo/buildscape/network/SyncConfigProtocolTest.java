package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.ConfigSetting;
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
        assertEquals("12", ModMessages.PROTOCOL_VERSION);
    }

    @Test
    public void syncGameRulesPacketCarriesSixRules() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        new SyncGameRulesPacket(true, false, true, false, true, false).encode(buf);
        assertEquals(6, buf.readableBytes());
        SyncGameRulesPacket decoded = SyncGameRulesPacket.decode(buf);
        assertTrue(decoded.fastLeafDecay);
        assertFalse(decoded.disableEndermanGriefing);
        assertTrue(decoded.disableCreeperGriefing);
        assertFalse(decoded.disableGhastGriefing);
        assertTrue(decoded.cakeStacking);
        assertFalse(decoded.waterBottleStacking);
        assertEquals(0, buf.readableBytes());
    }

    @Test
    public void editRefusedPacketRoundTrips() {
        for (EditRefusedPacket.Reason reason : EditRefusedPacket.Reason.values()) {
            for (ConfigSetting setting : new ConfigSetting[] {null, ConfigSetting.ITEMS, ConfigSetting.WORLD_RULES}) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                new EditRefusedPacket(reason, setting).encode(buf);
                EditRefusedPacket decoded = EditRefusedPacket.decode(buf);
                assertEquals(reason, decoded.reason);
                assertEquals(setting, decoded.setting());
                assertEquals(0, buf.readableBytes());
            }
        }
    }

    @Test
    public void uploadRefusalTextsMapToScreenReasons() {
        assertEquals(EditRefusedPacket.Reason.LOCKED,
                EditGuard.reasonFor("BuildScape: This setting is locked by the server edit policy."));
        assertEquals(EditRefusedPacket.Reason.READ_ONLY,
                EditGuard.reasonFor("BuildScape: Changes refused because the server config is read-only. Check the server log."));
        assertEquals(EditRefusedPacket.Reason.INVALID,
                EditGuard.reasonFor("BuildScape: Invalid item or tag refused: x"));
    }

    @Test
    public void unsyncedLevelFourClientUploadsItemsInStandardMode() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.edit_permission_mode = PillarParticleConfig.EditPermissionMode.STANDARD;
        config.replaceItems(Set.of("minecraft:stick"));
        ConfigAssembly assembly = new ConfigAssembly();
        SyncConfigPacket decoded = null;
        for (UpdateConfigPacket packet : UpdateConfigPacket.splitForClient(config, -1, true, true)) {
            decoded = assembly.accept(packet);
        }
        assertNotNull(decoded);
        assertNotEquals(0, ConfigSetting.mask(decoded.uploadSettings));
        assertTrue(decoded.uploadSettings.contains(ConfigSetting.ITEMS));
        assertEquals(config.items, decoded.items);
    }

    @Test
    public void strictLevelZeroChunksAreNeverBufferedEvenAfterPermissionRevocation() {
        PillarParticleConfig config = new PillarParticleConfig();
        UpdateConfigPacket packet = UpdateConfigPacket.split(config).get(0);
        java.util.Map<Object, ConfigAssembly> pending = new java.util.HashMap<>();
        Object sender = new Object();
        assertNull(UpdateConfigPacket.pendingAssembly(pending, sender, packet,
                PillarParticleConfig.EditPermissionMode.STRICT, 0));
        assertTrue(pending.isEmpty());
        assertNotNull(UpdateConfigPacket.pendingAssembly(pending, sender, packet,
                PillarParticleConfig.EditPermissionMode.STANDARD, 2));
        assertEquals(1, pending.size());
        assertNull(UpdateConfigPacket.pendingAssembly(pending, sender, packet,
                PillarParticleConfig.EditPermissionMode.STRICT, 0));
        assertTrue(pending.isEmpty());
    }

    @Test
    public void limitedUploadOmitsGeneralAndPolicyFieldsOnTheWire() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.edit_permission_mode = PillarParticleConfig.EditPermissionMode.STANDARD;
        config.replaceItems(Set.of("minecraft:stick"));
        config.particle_color = List.of("#112233", "#445566");
        config.max_particle_color = 2;
        ConfigAssembly assembly = new ConfigAssembly();
        SyncConfigPacket decoded = null;
        for (UpdateConfigPacket packet : UpdateConfigPacket.split(config, ConfigSetting.allowed(config.edit_permission_mode, 2))) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                packet.encode(buffer);
                decoded = assembly.accept(UpdateConfigPacket.decode(buffer));
                assertEquals(0, buffer.readableBytes());
            } finally {
                buffer.release();
            }
        }
        assertNotNull(decoded);
        assertEquals(java.util.EnumSet.of(ConfigSetting.ITEMS, ConfigSetting.COLORS), decoded.uploadSettings);
        assertEquals(config.items, decoded.items);
        assertEquals(config.particle_color, decoded.particle_color);
        assertEquals(2, decoded.max_particle_color);
        assertNull(decoded.pattern);
        assertEquals(0.0, decoded.particle_speed, 0.0);
        assertEquals(PillarParticleConfig.EditPermissionMode.STRICT, decoded.edit_permission_mode);
    }

    @Test
    public void eachSettingGroupRoundTripsIndependently() {
        PillarParticleConfig config = new PillarParticleConfig();
        config.pattern = "spiral";
        config.edit_permission_mode = PillarParticleConfig.EditPermissionMode.OPEN;
        config.replaceItems(Set.of("minecraft:stick"));
        for (ConfigSetting setting : ConfigSetting.uploadable()) {
            ConfigAssembly assembly = new ConfigAssembly();
            SyncConfigPacket decoded = null;
            for (UpdateConfigPacket packet : UpdateConfigPacket.split(config, java.util.EnumSet.of(setting))) {
                decoded = assembly.accept(packet);
            }
            assertNotNull(decoded);
            assertEquals(java.util.EnumSet.of(setting), decoded.uploadSettings);
            switch (setting) {
                case ITEMS -> {
                    assertEquals(config.items, decoded.items);
                    assertNull(decoded.particle_color);
                    assertNull(decoded.pattern);
                }
                case COLORS -> {
                    assertEquals(config.particle_color, decoded.particle_color);
                    assertEquals(config.max_particle_color, decoded.max_particle_color);
                    assertNull(decoded.items);
                }
                case PARTICLES -> {
                    assertEquals(config.pattern, decoded.pattern);
                    assertEquals(config.particle_speed, decoded.particle_speed, 0.0);
                    assertEquals(config.particle_spread, decoded.particle_spread, 0.0);
                    assertEquals(config.particle_lifetime, decoded.particle_lifetime);
                    assertEquals(config.particle_density, decoded.particle_density);
                    assertEquals(config.use_pattern, decoded.use_pattern);
                    assertEquals(config.pattern_speed, decoded.pattern_speed, 0.0);
                    assertEquals(config.pattern_spread, decoded.pattern_spread, 0.0);
                    assertEquals(config.pattern_intensity, decoded.pattern_intensity, 0.0);
                }
                case POLICY -> assertEquals(config.edit_permission_mode, decoded.edit_permission_mode);
            }
        }
    }

    @Test
    public void everyFieldAndBothReadOnlyFlagsRoundTrip() {
        for (boolean itemsReadOnly : new boolean[]{false, true}) {
            for (boolean propertiesReadOnly : new boolean[]{false, true}) {
                PillarParticleConfig config = new PillarParticleConfig();
                config.particle_speed = 0.37;
                config.particle_spread = 0.61;
                config.particle_lifetime = 83;
                config.particle_density = 7;
                config.use_pattern = true;
                config.pattern = "spiral";
                config.pattern_speed = 0.29;
                config.pattern_spread = 0.43;
                config.pattern_intensity = 0.71;
                config.particle_color = List.of("red", "blue");
                config.max_particle_color = 2;
                config.edit_permission_mode = PillarParticleConfig.EditPermissionMode.STANDARD;
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
                    assertEquals(original.edit_permission_mode, decoded.edit_permission_mode);
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
