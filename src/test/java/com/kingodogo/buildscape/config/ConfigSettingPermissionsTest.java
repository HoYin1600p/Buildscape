package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigSettingPermissionsTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void everyModeAndLevelUsesTheSameSettingRules() {
        for (PillarParticleConfig.EditPermissionMode mode : PillarParticleConfig.EditPermissionMode.values()) {
            for (int level : new int[]{0, 2, 4}) {
                for (ConfigSetting setting : ConfigSetting.values()) {
                    boolean expected = level == 4 || setting != ConfigSetting.POLICY
                            && (mode == PillarParticleConfig.EditPermissionMode.OPEN
                            || mode == PillarParticleConfig.EditPermissionMode.STANDARD && level == 2
                            && (setting == ConfigSetting.ITEMS || setting == ConfigSetting.COLORS));
                    assertEquals(mode + "/" + level + "/" + setting, expected, setting.canEdit(mode, level));
                    assertEquals(expected, ConfigSetting.allowed(mode, level).contains(setting));
                }
            }
        }
    }

    @Test
    public void limitedUploadCannotChangeGeneralSettingsOrEscalatePolicy() throws Exception {
        PillarParticleConfig server = new PillarParticleConfig(temporary.newFolder().toPath());
        server.edit_permission_mode = PillarParticleConfig.EditPermissionMode.STANDARD;
        double oldSpeed = server.particle_speed;
        PillarParticleConfig edited = new PillarParticleConfig();
        edited.replaceItems(Set.of("minecraft:stick"));
        edited.max_particle_color = 2;
        edited.particle_color = List.of("#112233", "#445566");
        edited.particle_speed = 0.9;
        edited.edit_permission_mode = PillarParticleConfig.EditPermissionMode.OPEN;
        SyncConfigPacket data = new SyncConfigPacket(edited);
        assertNotNull(UpdateConfigPacket.applyValues(server, data, 2));
        assertEquals(Set.of("minecraft:stick"), server.items);
        assertEquals(2, server.max_particle_color);
        assertEquals(edited.particle_color, server.particle_color);
        assertEquals(oldSpeed, server.particle_speed, 0.0);
        assertEquals(PillarParticleConfig.EditPermissionMode.STANDARD, server.edit_permission_mode);
        assertEquals(EnumSet.of(ConfigSetting.ITEMS, ConfigSetting.COLORS), data.uploadSettings);
    }

    @Test
    public void strictRefusalKeepsEveryValueAndOpenStillProtectsPolicy() throws Exception {
        PillarParticleConfig server = new PillarParticleConfig(temporary.newFolder().toPath());
        SyncConfigPacket data = new SyncConfigPacket(new PillarParticleConfig());
        data.items = Set.of("minecraft:stick");
        data.edit_permission_mode = PillarParticleConfig.EditPermissionMode.OPEN;
        assertNotNull(UpdateConfigPacket.applyValues(server, data, 2));
        assertTrue(server.items.isEmpty());
        assertTrue(data.uploadSettings.isEmpty());
        server.edit_permission_mode = PillarParticleConfig.EditPermissionMode.OPEN;
        data = new SyncConfigPacket(new PillarParticleConfig());
        data.items = Set.of("minecraft:diamond");
        assertNotNull(UpdateConfigPacket.applyValues(server, data, 0));
        assertEquals(Set.of("minecraft:diamond"), server.items);
        assertEquals(PillarParticleConfig.EditPermissionMode.OPEN, server.edit_permission_mode);
    }

    @Test
    public void protectedItemsDoNotBlockColorEditsAndProtectedPropertiesDoNotBlockItems() throws Exception {
        for (boolean protectItems : new boolean[]{false, true}) {
            Path directory = temporary.newFolder().toPath();
            PillarParticleConfig server = new PillarParticleConfig(directory);
            server.edit_permission_mode = PillarParticleConfig.EditPermissionMode.STANDARD;
            java.lang.reflect.Field field = PillarParticleConfig.class.getDeclaredField(protectItems ? "itemsReadOnly" : "propertiesReadOnly");
            field.setAccessible(true);
            field.setBoolean(server, true);
            SyncConfigPacket data = new SyncConfigPacket(new PillarParticleConfig());
            data.items = Set.of("minecraft:stick");
            data.max_particle_color = 2;
            data.uploadSettings = EnumSet.of(ConfigSetting.ITEMS, ConfigSetting.COLORS);
            assertNotNull(UpdateConfigPacket.applyValues(server, data, 2));
            assertEquals(protectItems ? Set.of() : Set.of("minecraft:stick"), server.items);
            assertEquals(protectItems ? 2 : 3, server.max_particle_color);
        }
    }
}
