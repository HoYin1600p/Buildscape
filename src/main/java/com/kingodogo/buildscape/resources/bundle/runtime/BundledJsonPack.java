package com.kingodogo.buildscape.resources.bundle.runtime;

import com.kingodogo.buildscape.resources.bundle.BundledJsonResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.util.function.Predicate;
import java.util.function.Function;

public final class BundledJsonPack {
    private final Function<String, Path> resolve;
    private volatile BundledJsonResources resources;
    private volatile boolean loaded;
    private volatile Boolean hasLooseResources;
    private Map<String, NavigableMap<String, IndexedResource>> index = Map.of();

    private record IndexedResource(ResourceLocation location, int depth, String filename) {
    }

    public BundledJsonPack(Path source) {
        this(source::resolve);
    }

    public BundledJsonPack(Function<String, Path> resolve) {
        this.resolve = resolve;
    }

    private boolean hasLooseResources() {
        Boolean cached = hasLooseResources;
        if (cached != null) {
            return cached;
        }
        try {
            Path models = resolve.apply("assets/buildscape/models");
            Path blockstates = resolve.apply("assets/buildscape/blockstates");
            boolean exists = (models != null && Files.exists(models))
                    || (blockstates != null && Files.exists(blockstates));
            hasLooseResources = exists;
            return exists;
        } catch (Exception e) {
            hasLooseResources = false;
            return false;
        }
    }

    private BundledJsonResources resources() {
        // Resource reload workers ask every pack at once; after the first load no lock is needed.
        if (loaded) {
            return resources;
        }
        return loadResources();
    }

    private synchronized BundledJsonResources loadResources() {
        if (!loaded) {
            Path bundle = resolve.apply(BundledJsonResources.FILE);
            if (Files.isRegularFile(bundle)) {
                try (InputStream in = Files.newInputStream(bundle)) {
                    BundledJsonResources prepared = BundledJsonResources.read(in);
                    Map<String, NavigableMap<String, IndexedResource>> built = new HashMap<>();
                    for (String entry : prepared.paths()) {
                        int boundary = entry.indexOf('/', 7);
                        String namespace = entry.substring(7, boundary);
                        String relative = entry.substring(boundary + 1);
                        int depth = 1;
                        for (int i = 0; i < relative.length(); i++) {
                            if (relative.charAt(i) == '/') {
                                depth++;
                            }
                        }
                        built.computeIfAbsent(namespace, key -> new TreeMap<>()).put(relative,
                                new IndexedResource(new ResourceLocation(namespace, relative), depth,
                                        relative.substring(relative.lastIndexOf('/') + 1)));
                    }
                    index = Map.copyOf(built);
                    resources = prepared;
                } catch (IOException failure) {
                    throw new UncheckedIOException("Unable to read bundled JSON resources", failure);
                }
            }
            loaded = true;
        }
        return resources;
    }

    public boolean contains(String path) {
        if (isBinaryBundle(path)) {
            try {
                Path file = resolve.apply(path);
                if (file != null && (Files.isRegularFile(file) || Files.exists(file))) {
                    return true;
                }
            } catch (Exception ignored) {
            }
            return BundledResourceRegistry.getInstance().isInitialized();
        }
        if (!path.startsWith("assets/buildscape/") && !path.startsWith("data/buildscape/")) {
            return false;
        }
        // Every mod's pack is asked; most have no bundle, so check that before touching the file system.
        BundledJsonResources bundle = resources();
        if (bundle == null || !bundle.contains(path)) {
            return false;
        }
        return !hasLooseResources() || !Files.exists(resolve.apply(path));
    }

    public InputStream open(String path) throws IOException {
        if (isBinaryBundle(path)) {
            try {
                Path file = resolve.apply(path);
                if (file != null && (Files.isRegularFile(file) || Files.exists(file))) {
                    return Files.newInputStream(file);
                }
            } catch (Exception ignored) {
            }
            return BundledResourceRegistry.class.getClassLoader().getResourceAsStream(path);
        }
        if (!path.startsWith("assets/buildscape/") && !path.startsWith("data/buildscape/")) {
            return null;
        }
        BundledJsonResources bundle = resources();
        if (bundle == null) {
            return null;
        }
        if (hasLooseResources() && Files.exists(resolve.apply(path))) {
            return null;
        }
        return bundle.open(path);
    }

    private static boolean isBinaryBundle(String path) {
        return "assets/buildscape/resources.bsrb".equals(path) || "data/buildscape/data.bsrb".equals(path);
    }

    public Collection<ResourceLocation> list(PackType type, String namespace, String path, int maxDepth,
            Predicate<String> filter) {
        if (type != PackType.CLIENT_RESOURCES) {
            return java.util.List.of();
        }
        BundledJsonResources bundle = resources();
        if (bundle == null) {
            return java.util.List.of();
        }
        String prefix = path.isEmpty() ? "" : path.endsWith("/") ? path : path + "/";
        Collection<ResourceLocation> result = new ArrayList<>();
        NavigableMap<String, IndexedResource> entries = index.get(namespace);
        if (entries == null) {
            return result;
        }
        for (var entry : entries.tailMap(prefix, true).entrySet()) {
            if (!entry.getKey().startsWith(prefix)) {
                break;
            }
            IndexedResource resource = entry.getValue();
            if (resource.depth() <= maxDepth && filter.test(resource.filename())) {
                result.add(resource.location());
            }
        }
        return result;
    }

    public Set<String> namespaces(PackType type) {
        if (type == PackType.CLIENT_RESOURCES) {
            BundledJsonResources bundle = resources();
            if (bundle != null) {
                return new LinkedHashSet<>(index.keySet());
            }
        }
        return new LinkedHashSet<>();
    }

    public synchronized void close() {
        resources = null;
        index = Map.of();
        loaded = false;
        hasLooseResources = null;
    }
}
