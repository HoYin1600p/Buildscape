package com.kingodogo.buildscape.resources.bundle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public final class BundledJsonResources {
    public static final String FILE = "assets/buildscape/resources.bsrc";
    private static final int MAGIC = 0x42534a31;
    private static final int MAX_BYTES = 64 * 1024 * 1024;
    private static final int MAX_FILE_BYTES = 4 * 1024 * 1024;
    private final Map<String, Entry> entries;

    private record Entry(int length, byte[] compressed) {
    }

    private BundledJsonResources(Map<String, Entry> entries) {
        this.entries = Collections.unmodifiableMap(entries);
    }

    public static void write(Map<String, byte[]> resources, OutputStream stream) throws IOException {
        if (resources.size() > ResourceBundleFormat.MAX_SECTION_RECORDS) {
            throw new IOException("Too many bundled JSON resources");
        }
        DataOutputStream out = new DataOutputStream(stream);
        out.writeInt(MAGIC);
        out.writeInt(resources.size());
        long total = 0;
        for (Map.Entry<String, byte[]> resource : new TreeMap<>(resources).entrySet()) {
            validatePath(resource.getKey());
            byte[] bytes = resource.getValue();
            total += bytes.length;
            if (bytes.length > MAX_FILE_BYTES || total > MAX_BYTES) {
                throw new IOException("Bundled JSON exceeds size budget");
            }
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            try (DeflaterOutputStream deflater = new DeflaterOutputStream(compressed)) {
                deflater.write(bytes);
            }
            byte[] payload = compressed.toByteArray();
            out.writeUTF(resource.getKey());
            out.writeInt(bytes.length);
            out.writeInt(payload.length);
            out.write(payload);
        }
        out.flush();
    }

    public static BundledJsonResources read(InputStream stream) throws IOException {
        DataInputStream in = new DataInputStream(stream);
        if (in.readInt() != MAGIC) {
            throw new IOException("Invalid bundled JSON header");
        }
        int count = in.readInt();
        if (count < 0 || count > ResourceBundleFormat.MAX_SECTION_RECORDS) {
            throw new IOException("Invalid bundled JSON resource count");
        }
        Map<String, Entry> entries = new LinkedHashMap<>();
        long total = 0;
        long compressedTotal = 0;
        for (int i = 0; i < count; i++) {
            String path = in.readUTF();
            validatePath(path);
            int length = in.readInt();
            int compressedLength = in.readInt();
            total += length;
            compressedTotal += compressedLength;
            if (length < 0 || length > MAX_FILE_BYTES || total > MAX_BYTES
                    || compressedLength < 0 || compressedLength > MAX_FILE_BYTES + 65536
                    || compressedTotal > MAX_BYTES) {
                throw new IOException("Invalid bundled JSON resource size");
            }
            byte[] payload = in.readNBytes(compressedLength);
            if (payload.length != compressedLength) {
                throw new IOException("Truncated bundled JSON resource");
            }
            if (entries.putIfAbsent(path, new Entry(length, payload)) != null) {
                throw new IOException("Duplicate bundled JSON resource: " + path);
            }
        }
        if (in.read() != -1) {
            throw new IOException("Trailing bundled JSON data");
        }
        return new BundledJsonResources(entries);
    }

    public Set<String> paths() {
        return entries.keySet();
    }

    public boolean contains(String path) {
        return entries.containsKey(path);
    }

    public InputStream open(String path) throws IOException {
        Entry entry = entries.get(path);
        if (entry == null) {
            return null;
        }
        try (InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(entry.compressed()))) {
            byte[] bytes = inflater.readNBytes(entry.length() + 1);
            if (bytes.length != entry.length() || inflater.read() != -1) {
                throw new IOException("Bundled JSON length mismatch: " + path);
            }
            return new ByteArrayInputStream(bytes);
        }
    }

    private static void validatePath(String path) throws IOException {
        if (!path.matches("assets/[a-z0-9_.-]+/(models|blockstates)/[a-z0-9_./-]+\\.json")
                || path.contains("/../") || path.contains("/./") || path.contains("//")) {
            throw new IOException("Invalid bundled JSON resource path: " + path);
        }
    }
}
