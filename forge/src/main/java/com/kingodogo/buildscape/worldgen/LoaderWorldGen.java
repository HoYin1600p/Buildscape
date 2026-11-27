package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.registry.ForgeRegistryAdapter;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/** Queue all configured holders before Forge fires its vanilla registration event. */
public final class LoaderWorldGen {
    private static final Set<String> IDS = new HashSet<>();
    private LoaderWorldGen() {}

    public static boolean contains(CommonId id) {
        return "buildscape".equals(id.getNamespace()) && IDS.contains(id.getPath());
    }

    private static void queue(ForgeRegistryAdapter registry, String path, Supplier<?> factory) {
        if (!IDS.add(path)) throw new IllegalStateException("Duplicate configured feature: " + path);
        Supplier<ConfiguredFeature<?, ?>> feature = () -> com.kingodogo.buildscape.adapter.WorldGenFactory.configuredFeature(factory.get());
        registry.queue(BuiltinRegistries.CONFIGURED_FEATURE, path, feature);
    }

    public static void register(ForgeRegistryAdapter registry) {
        queue(registry, "mangrove", () -> com.kingodogo.buildscape.adapter.WorldGenFactory.createMangroveTreeFeature(false));
        queue(registry, "tall_mangrove", () -> com.kingodogo.buildscape.adapter.WorldGenFactory.createMangroveTreeFeature(true));
        ModConfiguredFeatures.forEachFeature((name, supplier) -> queue(registry, name, supplier));
    }
}
