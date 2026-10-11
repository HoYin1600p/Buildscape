package com.kingodogo.buildscape.parity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

final class MixinDump {
    private MixinDump() {}

    // These are Mixin 0.8.5 fields, not Minecraft fields: no SRG translation.
    private static Object field(Object instance, Class<?> owner, String name) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(instance);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot inspect Mixin 0.8.5 " + name, failure);
        }
    }

    static JsonElement snapshot() {
        TreeMap<String, TreeSet<String>> targets = new TreeMap<>();
        TreeMap<String, TreeMap<String, TreeSet<String>>> applied = new TreeMap<>();
        // Prepared configs are removed from Mixins.getConfigs(), so also read the active processor.
        Mixins.getConfigs().forEach(config -> addConfig(config.getConfig(), targets));
        Object transformer = MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
        if (transformer == null) throw new IllegalStateException("No active mixin transformer");
        Object processor = field(transformer, transformer.getClass(), "processor");
        Collection<?> configs = (Collection<?>) field(processor, processor.getClass(), "configs");
        configs.forEach(config -> addConfig((IMixinConfig) config, targets));

        Map<?, ?> cache = (Map<?, ?>) field(null, ClassInfo.class, "cache");
        // Read only already-known classes; do not load targets merely to make them appear applied.
        for (Object value : new java.util.ArrayList<>(cache.values())) {
            if (!(value instanceof ClassInfo info)) continue;
            for (IMixinInfo mixin : info.getAppliedMixins()) {
                String config = mixin.getConfig().getName();
                if (!buildscape(config)) continue;
                targets.computeIfAbsent(config, ignored -> new TreeSet<>()).add(info.getClassName());
                applied.computeIfAbsent(config, ignored -> new TreeMap<>())
                        .computeIfAbsent(info.getClassName(), ignored -> new TreeSet<>()).add(mixin.getClassName());
            }
        }
        JsonObject result = new JsonObject();
        targets.forEach((config, configuredTargets) -> {
            JsonObject actual = new JsonObject();
            applied.getOrDefault(config, new TreeMap<>()).forEach((target, mixins) -> actual.add(target, DumpIO.json(mixins)));
            result.add(config, DumpIO.object("configuredTargets", configuredTargets, "applied", actual));
        });
        if (result.size() == 0) throw new IllegalStateException("No Buildscape mixin config found");
        return result;
    }

    private static boolean buildscape(String config) {
        return config.toLowerCase(java.util.Locale.ROOT).contains("buildscape");
    }

    private static void addConfig(IMixinConfig config, Map<String, TreeSet<String>> targets) {
        if (buildscape(config.getName())) targets.computeIfAbsent(config.getName(), ignored -> new TreeSet<>())
                .addAll(config.getTargets());
    }
}
