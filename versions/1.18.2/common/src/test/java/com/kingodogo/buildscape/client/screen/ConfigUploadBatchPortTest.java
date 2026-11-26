package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigUploadBatchPortTest {
    @Test
    void lostPermissionKeepsConcurrentServerItemChanges() {
        var config = new PillarParticleConfig();
        config.items.add("example:removed");
        var batch = new ConfigUploadBatch();
        batch.rebase(config);
        config.items.remove("example:removed");
        config.items.add("example:added");
        batch.edited(ConfigSetting.ITEMS, config, 100);
        var server = new PillarParticleConfig();
        server.items.remove("example:removed");
        server.items.add("example:added");
        server.setServerProtection(true, false);
        batch.reapply(server);
        batch.revert(ConfigSetting.ITEMS, server);
        assertFalse(server.items.contains("example:removed"));
        assertTrue(server.items.contains("example:added"));
        assertFalse(batch.hasPending());
    }

    @Test
    void detectsOnlyChangedGroupsAndDropsChangesRevertedBeforeSending() {
        var config = new PillarParticleConfig();
        double initialSpeed = config.particle_speed;
        var batch = new ConfigUploadBatch();
        batch.rebase(config);
        config.particle_speed = 0.7;
        batch.edited(config, 100);
        assertEquals(Set.of(ConfigSetting.PARTICLES), batch.pendingSettings());
        config.particle_speed = initialSpeed;
        config.items.add("example:local");
        batch.edited(config, 101);
        assertEquals(Set.of(ConfigSetting.ITEMS), batch.pendingSettings());
    }

    @Test
    void debounceRestartsAfterEachEditAndUploadsOnlyChangedGroups() {
        var config = new PillarParticleConfig();
        var batch = new ConfigUploadBatch();
        batch.rebase(config);
        config.particle_speed = 0.7;
        batch.edited(ConfigSetting.PARTICLES, config, 100);
        assertFalse(batch.isDue(3099));
        batch.edited(ConfigSetting.PARTICLES, config, 2000);
        assertFalse(batch.isDue(4999));
        assertTrue(batch.isDue(5000));
        assertEquals(Set.of(ConfigSetting.PARTICLES), batch.take());
        assertFalse(batch.hasPending());
    }

    @Test
    void serverSnapshotPreservesPendingFieldsAndOtherPlayersChanges() {
        var config = new PillarParticleConfig();
        var batch = new ConfigUploadBatch();
        batch.rebase(config);
        config.particle_speed = 0.7;
        config.items.add("example:local");
        batch.edited(ConfigSetting.PARTICLES, config, 100);
        batch.edited(ConfigSetting.ITEMS, config, 101);
        var server = new PillarParticleConfig();
        server.particle_spread = 0.9;
        server.items.add("example:remote");
        batch.reapply(server);
        assertEquals(0.7, server.particle_speed);
        assertEquals(0.9, server.particle_spread);
        assertTrue(server.items.containsAll(Set.of("example:local", "example:remote")));
    }

    @Test
    void unchangedPaletteDoesNotUndoConcurrentShrink() {
        var config = new PillarParticleConfig();
        config.particle_color = new ArrayList<>(List.of("red", "blue", "green"));
        var batch = new ConfigUploadBatch();
        batch.rebase(config);
        config.max_particle_color = 1;
        batch.edited(ConfigSetting.COLORS, config, 100);
        var server = new PillarParticleConfig();
        server.particle_color = new ArrayList<>(List.of("red"));
        batch.reapply(server);
        assertEquals(List.of("red"), server.particle_color);
        assertEquals(1, server.max_particle_color);
    }

    @Test
    void refusedUploadKeepsFieldsEditedAgainSinceSending() {
        var config = new PillarParticleConfig();
        double originalSpread = config.particle_spread;
        var batch = new ConfigUploadBatch();
        batch.rebase(config);
        config.particle_speed = 0.7;
        config.particle_spread = 0.8;
        batch.edited(ConfigSetting.PARTICLES, config, 100);
        batch.take();
        batch.measureFrom(config);
        config.particle_speed = 0.9;
        batch.edited(ConfigSetting.PARTICLES, config, 200);
        batch.refuse(ConfigSetting.PARTICLES, config);
        assertEquals(0.9, config.particle_speed);
        assertEquals(originalSpread, config.particle_spread);
        assertTrue(batch.hasPending());
    }

    @Test
    void lostPermissionRestoresOnlyTheAffectedPendingGroup() {
        var config = new PillarParticleConfig();
        var batch = new ConfigUploadBatch();
        batch.rebase(config);
        config.items.add("example:local");
        config.particle_speed = 0.7;
        batch.edited(ConfigSetting.ITEMS, config, 100);
        batch.edited(ConfigSetting.PARTICLES, config, 101);
        config.setServerProtection(true, false);
        batch.revert(ConfigSetting.ITEMS, config);
        assertFalse(config.items.contains("example:local"));
        assertEquals(Set.of(ConfigSetting.PARTICLES), batch.pendingSettings());
        assertEquals(0.7, config.particle_speed);
    }
}
