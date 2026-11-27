package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.registry.FabricRegistryAdapter;
import com.kingodogo.buildscape.util.CommonId;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/** Register configured features after the Fabric block batch is ready. */
public final class LoaderWorldGen {
    private static final Set<String> IDS = new HashSet<>();
    private LoaderWorldGen() {}

    public static boolean contains(CommonId id) {
        return "buildscape".equals(id.getNamespace()) && IDS.contains(id.getPath());
    }

    private static void queue(FabricRegistryAdapter registry, String path, Supplier<?> factory) {
        if (!IDS.add(path)) throw new IllegalStateException("Duplicate configured feature: " + path);
        com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() -> com.kingodogo.buildscape.platform.Services.PLATFORM.registerConfiguredFeature(new CommonId("buildscape", path), factory.get()));
    }

    public static void register(FabricRegistryAdapter registry) {
        queue(registry, "mangrove", () -> com.kingodogo.buildscape.adapter.WorldGenFactory.createMangroveTreeFeature(false));
        queue(registry, "tall_mangrove", () -> com.kingodogo.buildscape.adapter.WorldGenFactory.createMangroveTreeFeature(true));
        ModConfiguredFeatures.forEachFeature((name, supplier) -> queue(registry, name, supplier));
    }
}
