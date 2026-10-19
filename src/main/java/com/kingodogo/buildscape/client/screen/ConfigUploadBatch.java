package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.ConfigSetting;

import java.util.EnumSet;
import java.util.Set;

/**
 * Config edits waiting to be uploaded. The upload goes out {@link #DELAY_MS} after the last edit (each edit
 * restarts the wait) or at once when the screen closes, and carries only the setting groups that changed.
 */
final class ConfigUploadBatch {
    static final long DELAY_MS = 3000L;

    private final EnumSet<ConfigSetting> pending = EnumSet.noneOf(ConfigSetting.class);
    private long lastEditAt;

    void edited(ConfigSetting setting, long now) {
        pending.add(setting);
        lastEditAt = now;
    }

    boolean hasPending() {
        return !pending.isEmpty();
    }

    boolean isDue(long now) {
        return !pending.isEmpty() && now - lastEditAt >= DELAY_MS;
    }

    /** The changed groups, and forgets them. */
    Set<ConfigSetting> take() {
        EnumSet<ConfigSetting> taken = EnumSet.copyOf(pending);
        pending.clear();
        return taken;
    }

}
