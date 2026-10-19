package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Config edits waiting to be uploaded. The upload goes out {@link #DELAY_MS} after the last edit (each edit
 * restarts the wait) or at once when the screen closes, and carries only the setting groups that changed.
 *
 * <p>Besides the changed groups, the batch keeps the individual edits: which fields were changed, which colour
 * slots, which items were added or removed. They are found by comparing the config with the server state last
 * seen ({@link #rebase}). A config snapshot from the server that arrives during the wait (another player saved)
 * is handled by {@link #reapply}, which lays only those edits back over it, so the other player's changes to
 * everything else, in the same group too, stay. If both players edited the same field, slot or item, the local
 * pending edit wins on this client and the server keeps whichever upload reaches it last.
 */
final class ConfigUploadBatch {
    static final long DELAY_MS = 3000L;

    /** The scalar settings that can be edited one by one. */
    private enum Field {
        PARTICLE_SPEED(ConfigSetting.PARTICLES, c -> c.particle_speed, (c, v) -> c.particle_speed = (Double) v),
        PARTICLE_SPREAD(ConfigSetting.PARTICLES, c -> c.particle_spread, (c, v) -> c.particle_spread = (Double) v),
        PARTICLE_LIFETIME(ConfigSetting.PARTICLES, c -> c.particle_lifetime, (c, v) -> c.particle_lifetime = (Integer) v),
        PARTICLE_DENSITY(ConfigSetting.PARTICLES, c -> c.particle_density, (c, v) -> c.particle_density = (Integer) v),
        USE_PATTERN(ConfigSetting.PARTICLES, c -> c.use_pattern, (c, v) -> c.use_pattern = (Boolean) v),
        PATTERN(ConfigSetting.PARTICLES, c -> c.pattern, (c, v) -> c.pattern = (String) v),
        PATTERN_SPEED(ConfigSetting.PARTICLES, c -> c.pattern_speed, (c, v) -> c.pattern_speed = (Double) v),
        PATTERN_SPREAD(ConfigSetting.PARTICLES, c -> c.pattern_spread, (c, v) -> c.pattern_spread = (Double) v),
        PATTERN_INTENSITY(ConfigSetting.PARTICLES, c -> c.pattern_intensity, (c, v) -> c.pattern_intensity = (Double) v),
        MAX_COLORS(ConfigSetting.COLORS, c -> c.max_particle_color, (c, v) -> c.max_particle_color = (Integer) v),
        EDIT_MODE(ConfigSetting.POLICY, c -> c.edit_permission_mode,
                (c, v) -> c.edit_permission_mode = (PillarParticleConfig.EditPermissionMode) v);

        final ConfigSetting group;
        final Function<PillarParticleConfig, Object> read;
        final BiConsumer<PillarParticleConfig, Object> write;

        Field(ConfigSetting group, Function<PillarParticleConfig, Object> read,
              BiConsumer<PillarParticleConfig, Object> write) {
            this.group = group;
            this.read = read;
            this.write = write;
        }
    }

    /** The server state the edits are measured against. */
    private static final class Baseline {
        final Map<Field, Object> fields = new EnumMap<>(Field.class);
        final List<String> colors;
        final Set<String> items;

        Baseline(PillarParticleConfig config) {
            for (Field field : Field.values()) fields.put(field, field.read.apply(config));
            colors = new ArrayList<>(config.particle_color);
            items = new HashSet<>(config.items);
        }
    }

    private final Set<ConfigSetting> groups = EnumSet.noneOf(ConfigSetting.class);
    private final Map<Field, Object> fieldEdits = new EnumMap<>(Field.class);
    private final Map<Integer, String> colorSlots = new TreeMap<>();
    /** The whole colour list, only when its length changed (a slot was added or removed). */
    private List<String> colorList;
    private final Set<String> itemsAdded = new HashSet<>();
    private final Set<String> itemsRemoved = new HashSet<>();
    private Baseline baseline;
    private long lastEditAt;

    /** Takes {@code config} as the server state the next edits are measured against. */
    void rebase(PillarParticleConfig config) {
        baseline = new Baseline(config);
    }

    /** {@link #rebase} unless edits are waiting, which are still measured against the older state. */
    void rebaseIfIdle(PillarParticleConfig config) {
        if (groups.isEmpty()) rebase(config);
    }

    /** Records that {@code setting} was just edited in {@code config}, and which of its values differ from the baseline. */
    void edited(ConfigSetting setting, PillarParticleConfig config, long now) {
        groups.add(setting);
        lastEditAt = now;
        if (baseline == null) return;
        switch (setting) {
            case ITEMS -> {
                itemsAdded.clear();
                itemsRemoved.clear();
                for (String item : config.items) {
                    if (!baseline.items.contains(item)) itemsAdded.add(item);
                }
                for (String item : baseline.items) {
                    if (!config.items.contains(item)) itemsRemoved.add(item);
                }
            }
            case COLORS -> {
                colorSlots.clear();
                colorList = null;
                List<String> current = config.particle_color;
                if (current.size() != baseline.colors.size()) {
                    colorList = new ArrayList<>(current);
                } else {
                    for (int i = 0; i < current.size(); i++) {
                        if (!Objects.equals(current.get(i), baseline.colors.get(i))) colorSlots.put(i, current.get(i));
                    }
                }
                recordFields(setting, config);
            }
            default -> recordFields(setting, config);
        }
    }

    private void recordFields(ConfigSetting setting, PillarParticleConfig config) {
        for (Field field : Field.values()) {
            if (field.group != setting) continue;
            Object value = field.read.apply(config);
            if (Objects.equals(value, baseline.fields.get(field))) {
                fieldEdits.remove(field);
            } else {
                fieldEdits.put(field, value);
            }
        }
    }

    boolean hasPending() {
        return !groups.isEmpty();
    }

    boolean isDue(long now) {
        return !groups.isEmpty() && now - lastEditAt >= DELAY_MS;
    }

    /**
     * {@code config} just came from the server: measure later edits against it, and write the pending edits over
     * it (only the edited fields, slots and items), so the screen shows the merge and the upload carries it.
     */
    void reapply(PillarParticleConfig config) {
        rebase(config);
        for (Map.Entry<Field, Object> edit : fieldEdits.entrySet()) {
            edit.getKey().write.accept(config, edit.getValue());
        }
        if (colorList != null) {
            config.particle_color = new ArrayList<>(colorList);
        } else if (!colorSlots.isEmpty()) {
            List<String> merged = new ArrayList<>(config.particle_color);
            for (Map.Entry<Integer, String> slot : colorSlots.entrySet()) {
                if (slot.getKey() < merged.size()) merged.set(slot.getKey(), slot.getValue());
            }
            config.particle_color = merged;
        }
        if (!itemsAdded.isEmpty() || !itemsRemoved.isEmpty()) {
            List<String> merged = new ArrayList<>(config.items);
            merged.removeAll(itemsRemoved);
            for (String item : itemsAdded) {
                if (!merged.contains(item)) merged.add(item);
            }
            config.replaceItems(merged);
        }
    }

    /** The groups with edits waiting. */
    Set<ConfigSetting> pendingSettings() {
        return groups.isEmpty() ? EnumSet.noneOf(ConfigSetting.class) : EnumSet.copyOf(groups);
    }

    /**
     * {@code setting}'s edits will not be uploaded (edit access was lost during the wait): put the server's values
     * back into {@code config} and forget the edits, so the screen does not keep showing a change the server never got.
     */
    void revert(ConfigSetting setting, PillarParticleConfig config) {
        if (setting == null) return;
        if (baseline != null) {
            for (Field field : fieldEdits.keySet()) {
                if (field.group == setting) field.write.accept(config, baseline.fields.get(field));
            }
            if (setting == ConfigSetting.COLORS && (colorList != null || !colorSlots.isEmpty())) {
                config.particle_color = new ArrayList<>(baseline.colors);
            }
            if (setting == ConfigSetting.ITEMS && (!itemsAdded.isEmpty() || !itemsRemoved.isEmpty())) {
                List<String> restored = new ArrayList<>(config.items);
                restored.removeAll(itemsAdded);
                for (String item : itemsRemoved) {
                    if (!restored.contains(item)) restored.add(item);
                }
                config.replaceItems(restored);
            }
        }
        dropRefused(setting);
    }

    /** The server refused an upload: drop the refused group (all of them when it names none) so its state shows. */
    void dropRefused(ConfigSetting setting) {
        if (setting == null) {
            groups.clear();
            fieldEdits.clear();
            colorSlots.clear();
            colorList = null;
            itemsAdded.clear();
            itemsRemoved.clear();
            return;
        }
        groups.remove(setting);
        fieldEdits.keySet().removeIf(field -> field.group == setting);
        if (setting == ConfigSetting.COLORS) {
            colorSlots.clear();
            colorList = null;
        } else if (setting == ConfigSetting.ITEMS) {
            itemsAdded.clear();
            itemsRemoved.clear();
        }
    }

    /** The changed groups, and forgets them and their edits. */
    Set<ConfigSetting> take() {
        EnumSet<ConfigSetting> taken = EnumSet.noneOf(ConfigSetting.class);
        taken.addAll(groups);
        dropRefused(null);
        return taken;
    }
}
