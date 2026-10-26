package com.kingodogo.buildscape.config;

import java.util.EnumSet;
import java.util.Set;

/**
 * Operations guarded by the edit policy, with the same access rules on either side. The first four are
 * uploaded independently in the config packets; the rest are single-purpose operations on the server.
 */
public enum ConfigSetting {
    ITEMS(PillarParticleConfig.EditScope.LIMITED, true),
    COLORS(PillarParticleConfig.EditScope.LIMITED, false),
    PARTICLES(PillarParticleConfig.EditScope.GENERAL, false),
    POLICY(PillarParticleConfig.EditScope.POLICY, false),
    /** Editing, resetting or removing individual pillars. */
    PILLAR_EDIT(PillarParticleConfig.EditScope.LIMITED, false),
    /** Replacing the whole pillar registry in one upload. */
    PILLAR_BULK(PillarParticleConfig.EditScope.GENERAL, false),
    /** The World tab game rules. */
    WORLD_RULES(PillarParticleConfig.EditScope.GENERAL, false);

    /** Settings that travel in the config upload packets (the leading constants). */
    private static final int UPLOADED_COUNT = 4;

    public final PillarParticleConfig.EditScope scope;
    public final boolean itemsFile;

    ConfigSetting(PillarParticleConfig.EditScope scope, boolean itemsFile) {
        this.scope = scope;
        this.itemsFile = itemsFile;
    }

    public boolean canEdit(PillarParticleConfig.EditPermissionMode mode, int permissionLevel) {
        return PillarParticleConfig.canEdit(mode, permissionLevel, scope);
    }

    public boolean isUploaded() {
        return ordinal() < UPLOADED_COUNT;
    }

    public static EnumSet<ConfigSetting> uploadable() {
        EnumSet<ConfigSetting> settings = EnumSet.noneOf(ConfigSetting.class);
        for (ConfigSetting setting : values()) {
            if (setting.isUploaded()) {
                settings.add(setting);
            }
        }
        return settings;
    }

    public static int permissionLevel(boolean levelTwo, boolean levelFour) {
        return levelFour ? PillarParticleConfig.CONFIG_PERMISSION_LEVEL : levelTwo ? 2 : 0;
    }

    /** Settings the config upload packets may carry for this mode and level. */
    public static EnumSet<ConfigSetting> allowed(PillarParticleConfig.EditPermissionMode mode, int permissionLevel) {
        EnumSet<ConfigSetting> settings = EnumSet.noneOf(ConfigSetting.class);
        for (ConfigSetting setting : uploadable()) {
            if (setting.canEdit(mode, permissionLevel)) {
                settings.add(setting);
            }
        }
        return settings;
    }

    public static int mask(Set<ConfigSetting> settings) {
        int mask = 0;
        for (ConfigSetting setting : settings) {
            mask |= 1 << setting.ordinal();
        }
        return mask;
    }

    public static EnumSet<ConfigSetting> fromMask(int mask) {
        if (mask < 0 || (mask >> UPLOADED_COUNT) != 0) {
            throw new IllegalArgumentException("Unknown config settings");
        }
        EnumSet<ConfigSetting> settings = EnumSet.noneOf(ConfigSetting.class);
        for (ConfigSetting setting : uploadable()) {
            if ((mask & (1 << setting.ordinal())) != 0) {
                settings.add(setting);
            }
        }
        return settings;
    }
}
