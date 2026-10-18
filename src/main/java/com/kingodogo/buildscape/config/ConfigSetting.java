package com.kingodogo.buildscape.config;

import java.util.EnumSet;
import java.util.Set;

/** Settings uploaded independently, with the same access rules on either side. */
public enum ConfigSetting {
    ITEMS(PillarParticleConfig.EditScope.LIMITED, true),
    COLORS(PillarParticleConfig.EditScope.LIMITED, false),
    PARTICLES(PillarParticleConfig.EditScope.GENERAL, false),
    POLICY(PillarParticleConfig.EditScope.POLICY, false);

    public final PillarParticleConfig.EditScope scope;
    public final boolean itemsFile;

    ConfigSetting(PillarParticleConfig.EditScope scope, boolean itemsFile) {
        this.scope = scope;
        this.itemsFile = itemsFile;
    }

    public boolean canEdit(PillarParticleConfig.EditPermissionMode mode, int permissionLevel) {
        return PillarParticleConfig.canEdit(mode, permissionLevel, scope);
    }

    public static EnumSet<ConfigSetting> allowed(PillarParticleConfig.EditPermissionMode mode, int permissionLevel) {
        EnumSet<ConfigSetting> settings = EnumSet.noneOf(ConfigSetting.class);
        for (ConfigSetting setting : values()) {
            if (setting.canEdit(mode, permissionLevel)) settings.add(setting);
        }
        return settings;
    }

    public static int mask(Set<ConfigSetting> settings) {
        int mask = 0;
        for (ConfigSetting setting : settings) mask |= 1 << setting.ordinal();
        return mask;
    }

    public static EnumSet<ConfigSetting> fromMask(int mask) {
        if (mask < 0 || (mask >> values().length) != 0) {
            throw new IllegalArgumentException("Unknown config settings");
        }
        EnumSet<ConfigSetting> settings = EnumSet.noneOf(ConfigSetting.class);
        for (ConfigSetting setting : values()) {
            if ((mask & (1 << setting.ordinal())) != 0) settings.add(setting);
        }
        return settings;
    }
}
