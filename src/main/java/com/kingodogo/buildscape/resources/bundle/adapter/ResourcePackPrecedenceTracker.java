package com.kingodogo.buildscape.resources.bundle.adapter;

import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ResourcePackPrecedenceTracker {

    public static final ResourceLocation CLIENT_BUNDLE_LOCATION = new ResourceLocation("buildscape", "resources.bsrb");
    public static final ResourceLocation SERVER_DATA_BUNDLE_LOCATION = new ResourceLocation("buildscape", "data.bsrb");

    private static volatile ResourceManager lastResourceManager;
    private static volatile PackOrder lastPackOrder;

    private record PackOrder(ResourceManager manager, List<PackResources> packs, int bundleIndex) {
    }

    private ResourcePackPrecedenceTracker() {}

    /**
     * Ensures precedence overrides are up to date for the given ResourceManager.
     * Fast no-op if the ResourceManager has not changed since the last update.
     */
    public static void ensureInitialized(ResourceManager resourceManager) {
        if (resourceManager != null && lastResourceManager == resourceManager) {
            return;
        }
        synchronized (ResourcePackPrecedenceTracker.class) {
            if (resourceManager != null && lastResourceManager == resourceManager) {
                return;
            }
            updateOverrides(resourceManager);
        }
    }

    /**
     * Re-evaluates all active packs in the ResourceManager and updates BundledResourceRegistry overrides.
     */
    public static synchronized void updateOverrides(ResourceManager resourceManager) {
        lastResourceManager = null;
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        if (resourceManager == null) {
            lastPackOrder = null;
            registry.clearOverrides();
            return;
        }

        long start = System.nanoTime();
        List<PackResources> packs = resourceManager.listPacks().toList();
        int bundlePackIndex = findBundlePackIndex(packs);
        lastPackOrder = new PackOrder(resourceManager, packs, bundlePackIndex);

        if (bundlePackIndex < 0) {
            // If bundle owner pack is missing or disabled, mark all bundled resources inactive/overridden
            registry.setOverrides(registry.getModels().keySet(), registry.getBlockStates().keySet());
            registry.setOverrideDiscoveryDuration(System.nanoTime() - start);
            lastResourceManager = resourceManager;
            return;
        }

        if (bundlePackIndex == packs.size() - 1) {
            // Buildscape is the highest-priority pack; no higher packs can override
            registry.setOverrides(Collections.emptySet(), Collections.emptySet());
            registry.setOverrideDiscoveryDuration(System.nanoTime() - start);
            lastResourceManager = resourceManager;
            return;
        }

        // Higher-priority packs exist: scan resources that exist in external packs
        Set<String> overriddenModels = new HashSet<>();
        Set<String> overriddenBlockStates = new HashSet<>();

        Collection<ResourceLocation> modelRes = resourceManager.listResources("models", p -> p.endsWith(".json"));
        for (ResourceLocation loc : modelRes) {
            if ("buildscape".equals(loc.getNamespace())) {
                String path = loc.getPath();
                if (path.startsWith("models/") && path.endsWith(".json")) {
                    String modelId = "buildscape:"
                            + path.substring("models/".length(), path.length() - ".json".length());
                    if (registry.isModelBundled(modelId)
                            && isOverridden(resourceManager, loc, bundlePackIndex, packs)) {
                        overriddenModels.add(modelId);
                    }
                }
            }
        }

        Collection<ResourceLocation> bsRes = resourceManager.listResources("blockstates", p -> p.endsWith(".json"));
        for (ResourceLocation loc : bsRes) {
            if ("buildscape".equals(loc.getNamespace())) {
                String path = loc.getPath();
                if (path.startsWith("blockstates/") && path.endsWith(".json")) {
                    String stateId = "buildscape:"
                            + path.substring("blockstates/".length(), path.length() - ".json".length());
                    if (registry.isBlockStateBundled(stateId)
                            && isOverridden(resourceManager, loc, bundlePackIndex, packs)) {
                        overriddenBlockStates.add(stateId);
                    }
                }
            }
        }

        registry.setOverrides(overriddenModels, overriddenBlockStates);
        registry.setOverrideDiscoveryDuration(System.nanoTime() - start);
        lastResourceManager = resourceManager;
    }

    public static boolean isResourceOverridden(ResourceManager resourceManager, ResourceLocation fileLoc) {
        PackOrder order = lastPackOrder;
        if (order == null || order.manager() != resourceManager) {
            List<PackResources> current = resourceManager.listPacks().toList();
            order = new PackOrder(resourceManager, current, findBundlePackIndex(current));
            lastPackOrder = order;
        }
        List<PackResources> packs = order.packs();
        int bundlePackIndex = order.bundleIndex();
        if (bundlePackIndex < 0) {
            return true;
        }
        if (bundlePackIndex == packs.size() - 1) {
            return false;
        }
        return isOverridden(resourceManager, fileLoc, bundlePackIndex, packs);
    }

    public static int findClientBundlePackIndex(List<PackResources> packs) {
        return findBundlePackIndex(packs, PackType.CLIENT_RESOURCES, CLIENT_BUNDLE_LOCATION);
    }

    public static int findServerBundlePackIndex(List<PackResources> packs) {
        return findBundlePackIndex(packs, PackType.SERVER_DATA, SERVER_DATA_BUNDLE_LOCATION);
    }

    public static int findBundlePackIndex(List<PackResources> packs) {
        int clientIdx = findBundlePackIndex(packs, PackType.CLIENT_RESOURCES, CLIENT_BUNDLE_LOCATION);
        if (clientIdx >= 0) {
            return clientIdx;
        }
        return findBundlePackIndex(packs, PackType.SERVER_DATA, SERVER_DATA_BUNDLE_LOCATION);
    }

    public static int findBundlePackIndex(List<PackResources> packs, PackType packType,
            ResourceLocation bundleLocation) {
        // Pass 1: standard check - does a pack advertise the namespace and confirm hasResource for the bundle?
        for (int i = 0; i < packs.size(); i++) {
            PackResources pack = packs.get(i);
            try {
                // Vanilla's classpath lookup can see mod files outside its advertised namespaces.
                if (pack.getNamespaces(packType).contains(bundleLocation.getNamespace())
                        && pack.hasResource(packType, bundleLocation)) {
                    return i;
                }
            } catch (Exception ignored) {
            }
        }

        // Pass 2: In Forge, all mod resources are aggregated in "Mod Resources" (DelegatingResourcePack / DelegatingPackResources).
        // If an external cache wrapper or interceptor fails to report binary bundle paths,
        // identify the Mod Resources pack (or the base mod pack) that owns the buildscape namespace.
        for (int i = 0; i < packs.size(); i++) {
            PackResources pack = packs.get(i);
            try {
                if (pack.getNamespaces(packType).contains(bundleLocation.getNamespace())) {
                    String name = pack.getName();
                    String className = pack.getClass().getName();
                    if ("Mod Resources".equals(name) || "buildscape".equalsIgnoreCase(name)
                            || className.contains("Delegating")) {
                        return i;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return -1;
    }

    public static int findPackIndexByName(List<PackResources> packs, String name) {
        for (int i = 0; i < packs.size(); i++) {
            if (packs.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isOverridden(ResourceManager resourceManager, ResourceLocation fileLoc, int bundlePackIndex,
            List<PackResources> packs) {
        try {
            List<Resource> resources = resourceManager.getResources(fileLoc);
            if (resources.isEmpty()) {
                return false;
            }
            for (Resource resource : resources) {
                String sourceName = resource.getSourceName();
                int packIdx = findPackIndexByName(packs, sourceName);
                if (packIdx > bundlePackIndex) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
