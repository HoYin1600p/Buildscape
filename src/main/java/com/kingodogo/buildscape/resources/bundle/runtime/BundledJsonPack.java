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
import java.util.function.Predicate;
import java.util.function.Function;

public final class BundledJsonPack {
    private final Function<String, Path> resolve;
    private volatile BundledJsonResources resources;
    private volatile boolean loaded;

    public BundledJsonPack(Path source) {
        this(source::resolve);
    }

    public BundledJsonPack(Function<String, Path> resolve) {
        this.resolve = resolve;
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
                    resources = BundledJsonResources.read(in);
                } catch (IOException failure) {
                    throw new UncheckedIOException("Unable to read bundled JSON resources", failure);
                }
            }
            loaded = true;
        }
        return resources;
    }

    public boolean contains(String path) {
        if (!path.startsWith("assets/")) {
            return false;
        }
        // Every mod's pack is asked; most have no bundle, so check that before touching the file system.
        BundledJsonResources bundle = resources();
        return bundle != null && bundle.contains(path) && !Files.exists(resolve.apply(path));
    }

    public InputStream open(String path) throws IOException {
        return contains(path) ? resources().open(path) : null;
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
        String root = "assets/" + namespace + "/";
        String prefix = path.isEmpty() ? "" : path.endsWith("/") ? path : path + "/";
        Collection<ResourceLocation> result = new ArrayList<>();
        for (String entry : bundle.paths()) {
            if (!entry.startsWith(root)) {
                continue;
            }
            String relative = entry.substring(root.length());
            if (relative.startsWith(prefix) && relative.split("/").length <= maxDepth
                    && filter.test(relative.substring(relative.lastIndexOf('/') + 1))) {
                result.add(new ResourceLocation(namespace, relative));
            }
        }
        return result;
    }

    public Set<String> namespaces(PackType type) {
        Set<String> namespaces = new LinkedHashSet<>();
        if (type == PackType.CLIENT_RESOURCES) {
            BundledJsonResources bundle = resources();
            if (bundle != null) {
                for (String path : bundle.paths()) {
                    namespaces.add(path.substring(7, path.indexOf('/', 7)));
                }
            }
        }
        return namespaces;
    }

    public synchronized void close() {
        resources = null;
        loaded = false;
    }
}
