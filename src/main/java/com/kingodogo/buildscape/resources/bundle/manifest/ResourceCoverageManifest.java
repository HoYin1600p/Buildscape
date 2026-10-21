package com.kingodogo.buildscape.resources.bundle.manifest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ResourceCoverageManifest {

    public enum ResourceKind {
        MODEL,
        BLOCKSTATE,
        TAG
    }

    public record Entry(
            String relativePath, // e.g. "assets/buildscape/models/block/acacia_log_slab.json"
            String namespace, // e.g. "buildscape"
            ResourceKind kind,
            String sha256Hex) {
    }

    private final Map<String, Entry> entries = new LinkedHashMap<>();

    public ResourceCoverageManifest() {}

    public void addEntry(String relativePath, String namespace, ResourceKind kind, String sha256Hex) {
        String normalized = relativePath.replace('\\', '/');
        entries.put(normalized, new Entry(normalized, namespace, kind, sha256Hex));
    }

    public boolean covers(String relativePath) {
        String normalized = relativePath.replace('\\', '/');
        return entries.containsKey(normalized);
    }

    public Map<String, Entry> getEntries() {
        return Collections.unmodifiableMap(entries);
    }

    public void writeToFile(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# Resource Coverage Manifest\n");
            writer.write("count=" + entries.size() + "\n");
            for (Entry entry : entries.values()) {
                writer.write(entry.relativePath() + "\t"
                        + entry.namespace() + "\t"
                        + entry.kind().name() + "\t"
                        + entry.sha256Hex() + "\n");
            }
        }
    }

    public static ResourceCoverageManifest readFromFile(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            List<Entry> list = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("count=")) {
                    // skip
                } else if (line.startsWith("era=")) {
                    // ignore legacy era line if present
                } else {
                    String[] parts = line.split("\t");
                    if (parts.length >= 4) {
                        list.add(new Entry(parts[0], parts[1], ResourceKind.valueOf(parts[2]), parts[3]));
                    }
                }
            }
            ResourceCoverageManifest manifest = new ResourceCoverageManifest();
            for (Entry e : list) {
                manifest.entries.put(e.relativePath(), e);
            }
            return manifest;
        }
    }
}
