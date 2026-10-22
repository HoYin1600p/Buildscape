package com.kingodogo.buildscape.resources.bundle;

import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public final class BundledJsonResources {
    public static final String FILE = "assets/buildscape/resources.bsrc";
    private static final int MAGIC = 0x42534a31;
    private static final int MAX_BYTES = 64 * 1024 * 1024;
    private static final int MAX_FILE_BYTES = 4 * 1024 * 1024;
    private static final Pattern RESOURCE_PATH = Pattern.compile(
            "assets/[a-z0-9_.-]+/(models|blockstates)/[a-z0-9_./-]+\\.json");
    private final Map<String, Entry> entries;

    private static final class Entry {
        private final int length;
        private byte[] compressed;
        private volatile byte[] decoded;

        private Entry(int length, byte[] compressed) {
            this.length = length;
            this.compressed = compressed;
        }

        private byte[] bytes() throws IOException {
            byte[] result = decoded;
            if (result != null) {
                return result;
            }
            synchronized (this) {
                if (decoded == null) {
                    byte[] bytes = new byte[length];
                    if (length > 0) {
                        Inflater inflater = new Inflater();
                        try {
                            inflater.setInput(compressed);
                            int actual = inflater.inflate(bytes);
                            if (actual != length || !inflater.finished()) {
                                throw new IOException(
                                        "Bundled JSON length mismatch: expected " + length + ", got " + actual);
                            }
                        } catch (DataFormatException e) {
                            throw new IOException("Decompression error for bundled JSON", e);
                        } finally {
                            inflater.end();
                        }
                    }
                    compressed = null;
                    decoded = bytes;
                }
                return decoded;
            }
        }

        private void preload() {
            try {
                bytes();
            } catch (IOException ignored) {
                // Preserve lazy error handling: an unread corrupt resource must not hide valid resources.
            }
        }
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
        DataInputStream in = new DataInputStream(new BufferedInputStream(stream, 64 * 1024));
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

    public void preload() throws IOException {
        List<Entry> batch = List.copyOf(entries.values());
        int workers = Math.min(4, Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
        if (workers == 1 || batch.size() < 256) {
            batch.forEach(Entry::preload);
            return;
        }
        ExecutorService executor = Executors.newFixedThreadPool(workers, task -> {
            Thread thread = new Thread(task, "Buildscape resource decode");
            thread.setDaemon(true);
            return thread;
        });
        List<Future<?>> tasks = new ArrayList<>(workers);
        try {
            for (int worker = 0; worker < workers; worker++) {
                int start = batch.size() * worker / workers;
                int end = batch.size() * (worker + 1) / workers;
                tasks.add(executor.submit(() -> {
                    for (int i = start; i < end && !Thread.currentThread().isInterrupted(); i++) {
                        batch.get(i).preload();
                    }
                }));
            }
            for (Future<?> task : tasks) {
                task.get();
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while preparing bundled JSON", interrupted);
        } catch (ExecutionException failure) {
            if (failure.getCause() instanceof Error error) {
                throw error;
            }
            throw new IOException("Unable to prepare bundled JSON", failure.getCause());
        } finally {
            tasks.forEach(task -> task.cancel(true));
            executor.shutdownNow();
        }
    }

    public InputStream open(String path) throws IOException {
        Entry entry = entries.get(path);
        if (entry == null) {
            return null;
        }
        try {
            return new ByteArrayInputStream(entry.bytes());
        } catch (IOException failure) {
            throw new IOException("Unable to decode bundled JSON: " + path, failure);
        }
    }

    private static void validatePath(String path) throws IOException {
        if (!RESOURCE_PATH.matcher(path).matches()
                || path.contains("/../") || path.contains("/./") || path.contains("//")) {
            throw new IOException("Invalid bundled JSON resource path: " + path);
        }
    }
}
