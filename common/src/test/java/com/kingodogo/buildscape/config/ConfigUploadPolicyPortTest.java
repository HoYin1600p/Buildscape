package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigUploadPolicyPortTest {
    @TempDir Path directory;

    private PillarParticleConfig server() {
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.edit_permission_mode = PillarParticleConfig.EditPermissionMode.STANDARD;
        config.items.add("minecraft:stick");
        config.establishBaseline();
        return config;
    }

    private SyncConfigPacket changes(PillarParticleConfig config) {
        SyncConfigPacket data = new SyncConfigPacket(config);
        data.particle_speed = 0.5;
        data.particle_color = List.of("#FF0000");
        data.max_particle_color = 1;
        data.items = Set.of("minecraft:diamond");
        data.edit_permission_mode = PillarParticleConfig.EditPermissionMode.OPEN;
        return data;
    }

    @Test
    void limitedUploadCannotOverwriteGeneralSettingsOrEscalatePolicy() {
        PillarParticleConfig server = server();
        SyncConfigPacket data = changes(server);
        assertNotNull(UpdateConfigPacket.applyValues(server, data, 2));
        assertEquals(0.02, server.particle_speed);
        assertEquals(PillarParticleConfig.EditPermissionMode.STANDARD, server.edit_permission_mode);
        assertEquals(Set.of("minecraft:diamond"), server.items);
        assertEquals(List.of("#FF0000"), server.particle_color);
        assertEquals(EnumSet.of(ConfigSetting.ITEMS, ConfigSetting.COLORS), data.uploadSettings);
    }

    @Test
    void unreadableItemsDoNotBlockAnOtherwisePermittedColorEdit() throws Exception {
        PillarParticleConfig server = server();
        String original = "{\"items\":[\"minecraft:stick\",7]}";
        Files.writeString(directory.resolve("pillar-items.json"), original);
        server.loadItemsInternal(false);
        server.establishBaseline();
        SyncConfigPacket data = changes(server);
        assertNotNull(UpdateConfigPacket.applyValues(server, data, 2));
        assertEquals(EnumSet.of(ConfigSetting.COLORS), data.uploadSettings);
        assertEquals(List.of("#FF0000"), server.particle_color);
        assertEquals(original, Files.readString(directory.resolve("pillar-items.json")));
    }

    @Test
    void newlySuppliedInvalidIdsAreRefusedWithoutBlockingValidColorEdits() {
        PillarParticleConfig server = server();
        SyncConfigPacket data = changes(server);
        data.items = Set.of("Bad ID!");
        assertNotNull(UpdateConfigPacket.applyValues(server, data, 2));
        assertEquals(Set.of("minecraft:stick"), server.items);
        assertEquals(List.of("#FF0000"), server.particle_color);
        assertEquals(EnumSet.of(ConfigSetting.COLORS), data.uploadSettings);
    }
}
