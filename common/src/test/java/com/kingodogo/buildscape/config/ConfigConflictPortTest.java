package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.network.SyncConfigPacket;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigConflictPortTest {
    @Test
    void simultaneousEditsToDifferentParticleSettingsDoNotOverwriteEachOther() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.establishBaseline();
        SyncConfigPacket first = new SyncConfigPacket(server);
        SyncConfigPacket second = new SyncConfigPacket(server);
        first.particle_speed = 0.5;
        second.particle_lifetime = 40;

        assertTrue(server.applyUpload(first, EnumSet.of(ConfigSetting.PARTICLES)));
        assertTrue(server.applyUpload(second, EnumSet.of(ConfigSetting.PARTICLES)));
        assertEquals(0.5, server.particle_speed);
        assertEquals(40, server.particle_lifetime);
    }

    @Test
    void concurrentItemAdditionsAndRemovalsMergeAgainstTheirSharedBaseline() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.items.add("minecraft:stick");
        server.establishBaseline();
        SyncConfigPacket first = new SyncConfigPacket(server);
        SyncConfigPacket second = new SyncConfigPacket(server);
        first.items.add("minecraft:diamond");
        second.items.remove("minecraft:stick");
        second.items.add("minecraft:apple");

        assertTrue(server.applyUpload(first, EnumSet.of(ConfigSetting.ITEMS)));
        assertTrue(server.applyUpload(second, EnumSet.of(ConfigSetting.ITEMS)));
        assertEquals(Set.of("minecraft:diamond", "minecraft:apple"), server.items);
    }

    @Test
    void expiredRevisionRefusesUploadWithoutChangingServerState() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.establishBaseline();
        SyncConfigPacket stale = new SyncConfigPacket(server);
        stale.particle_speed = 0.5;
        for (int i = 0; i < 51; i++) server.onReload();

        var groups = EnumSet.of(ConfigSetting.PARTICLES);
        assertFalse(server.applyUpload(stale, groups));
        assertTrue(groups.isEmpty());
        assertEquals(0.02, server.particle_speed);
    }

    @Test
    void unchangedStalePaletteDoesNotUndoAnotherEditorsShrink() {
        PillarParticleConfig server = new PillarParticleConfig();
        server.particle_color = new java.util.ArrayList<>(java.util.List.of("#FF0000", "#00FF00", "#0000FF"));
        server.max_particle_color = 3;
        server.establishBaseline();
        SyncConfigPacket first = new SyncConfigPacket(server);
        SyncConfigPacket unchanged = new SyncConfigPacket(server);
        first.particle_color = java.util.List.of("#FF0000");
        first.max_particle_color = 1;
        assertTrue(server.applyUpload(first, EnumSet.of(ConfigSetting.COLORS)));
        assertTrue(server.applyUpload(unchanged, EnumSet.of(ConfigSetting.COLORS)));
        assertEquals(java.util.List.of("#FF0000"), server.particle_color);
        assertEquals(1, server.max_particle_color);
    }
}
