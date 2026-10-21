package com.kingodogo.buildscape.resources.bundle.runtime;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.ResourceBundleFormat;
import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.JarEntry;

public final class BundledResourceRegistry {

    private static final boolean VERIFY_DIAGNOSTICS = Boolean.getBoolean("buildscape.resources.verify");
    private static final BundledResourceRegistry INSTANCE = new BundledResourceRegistry();

    public static BundledResourceRegistry getInstance() {
        return INSTANCE;
    }

    private static final class CountingInputStream extends FilterInputStream {
        private long count = 0;

        public CountingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            int b = super.read();
            if (b != -1) {
                count++;
            }
            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int n = super.read(b, off, len);
            if (n > 0) {
                count += n;
            }
            return n;
        }

        public long getCount() {
            return count;
        }
    }

    public record BundleStats(
            String path,
            int version,
            int blockStatesCount,
            int blockModelsCount,
            int itemModelsCount,
            int tagsCount,
            long uncompressedContainerBytes,
            long compressedContainerBytes,
            long ioAndDecodeDurationNanos) {
    }

    public record BundledSnapshot(
            Map<String, ModelIR> models,
            Map<String, BlockStateIR> blockStates,
            List<TagIR> tags,
            Set<String> overriddenModels,
            Set<String> overriddenBlockStates) {
        public static final BundledSnapshot EMPTY = new BundledSnapshot(
                Map.of(), Map.of(), List.of(), Set.of(), Set.of());
    }

    private final AtomicReference<BundledSnapshot> snapshot = new AtomicReference<>(BundledSnapshot.EMPTY);
    private volatile boolean initialized = false;

    private final AtomicLong reloadGeneration = new AtomicLong(0);
    private final List<BundleStats> bundleStats = new CopyOnWriteArrayList<>();

    private final LongAdder servedModels = new LongAdder();
    private final LongAdder servedBlockStates = new LongAdder();
    private final LongAdder servedTags = new LongAdder();
    private final LongAdder fallbackModels = new LongAdder();
    private final LongAdder fallbackBlockStates = new LongAdder();

    private final Set<String> uniqueServedModels = ConcurrentHashMap.newKeySet();
    private final Set<String> uniqueServedBlockStates = ConcurrentHashMap.newKeySet();
    private final Set<String> uniqueServedTags = ConcurrentHashMap.newKeySet();

    private final LongAdder accumulatedNativeConversionNanos = new LongAdder();
    private volatile long overrideDiscoveryDurationNanos = -1;
    private volatile long reloadStartNanos = -1;
    private volatile long reloadPhaseWallNanos = -1;

    private BundledResourceRegistry() {}

    /**
     * Atomically loads both assets and data bundles in a single transaction.
     * Uses bounded streaming without unbounded in-memory array allocation.
     */
    public synchronized void loadBundles(InputStream assetsIn, InputStream dataIn) throws IOException {
        loadBundles(assetsIn, dataIn, null);
    }

    public synchronized void loadBundles(InputStream assetsIn, InputStream dataIn, byte[] expectedDigest)
            throws IOException {
        Map<String, ModelIR> newModels = new LinkedHashMap<>();
        Map<String, BlockStateIR> newBlockStates = new LinkedHashMap<>();
        List<TagIR> newTags = new ArrayList<>();
        List<BundleStats> newStats = new ArrayList<>();

        if (assetsIn != null) {
            long start = System.nanoTime();
            CountingInputStream cis = new CountingInputStream(new BufferedInputStream(assetsIn));
            long compressedBytes = resolveCompressedSize("assets/buildscape/resources.bsrb");
            ResourceBundleContainer.Reader reader = new ResourceBundleContainer.Reader(cis, expectedDigest);
            newModels.putAll(reader.getModels());
            newBlockStates.putAll(reader.getBlockStates());
            newTags.addAll(reader.getTags());
            long duration = System.nanoTime() - start;

            int blockModels = 0;
            int itemModels = 0;
            for (String id : reader.getModels().keySet()) {
                if (id.contains("/item/") || id.contains(":item/")) {
                    itemModels++;
                } else {
                    blockModels++;
                }
            }

            newStats.add(new BundleStats(
                    "assets/buildscape/resources.bsrb",
                    reader.getVersion(),
                    reader.getBlockStates().size(),
                    blockModels,
                    itemModels,
                    reader.getTags().size(),
                    cis.getCount(),
                    compressedBytes,
                    duration));
        }

        if (dataIn != null) {
            long start = System.nanoTime();
            CountingInputStream cis = new CountingInputStream(new BufferedInputStream(dataIn));
            long compressedBytes = resolveCompressedSize("data/buildscape/data.bsrb");
            ResourceBundleContainer.Reader reader = new ResourceBundleContainer.Reader(cis, expectedDigest);
            newModels.putAll(reader.getModels());
            newBlockStates.putAll(reader.getBlockStates());
            newTags.addAll(reader.getTags());
            long duration = System.nanoTime() - start;

            int blockModels = 0;
            int itemModels = 0;
            for (String id : reader.getModels().keySet()) {
                if (id.contains("/item/") || id.contains(":item/")) {
                    itemModels++;
                } else {
                    blockModels++;
                }
            }

            newStats.add(new BundleStats(
                    "data/buildscape/data.bsrb",
                    reader.getVersion(),
                    reader.getBlockStates().size(),
                    blockModels,
                    itemModels,
                    reader.getTags().size(),
                    cis.getCount(),
                    compressedBytes,
                    duration));
        }

        snapshot.set(new BundledSnapshot(
                Collections.unmodifiableMap(newModels),
                Collections.unmodifiableMap(newBlockStates),
                Collections.unmodifiableList(newTags),
                Collections.emptySet(),
                Collections.emptySet()));
        bundleStats.clear();
        bundleStats.addAll(newStats);
        reloadGeneration.incrementAndGet();
        this.initialized = true;

        if (VERIFY_DIAGNOSTICS) {
            verifyIntegrity();
        }
    }

    public synchronized void initializeFromStream(InputStream in) throws IOException {
        initializeFromStream(in, null);
    }

    public synchronized void initializeFromStream(InputStream in, byte[] expectedDigest) throws IOException {
        long start = System.nanoTime();
        CountingInputStream cis = new CountingInputStream(new BufferedInputStream(in));
        ResourceBundleContainer.Reader reader = new ResourceBundleContainer.Reader(cis, expectedDigest);
        BundledSnapshot current = snapshot.get();

        Map<String, ModelIR> newModels = new LinkedHashMap<>(current.models());
        newModels.putAll(reader.getModels());

        Map<String, BlockStateIR> newBlockStates = new LinkedHashMap<>(current.blockStates());
        newBlockStates.putAll(reader.getBlockStates());

        List<TagIR> newTags = new ArrayList<>(current.tags());
        newTags.addAll(reader.getTags());

        int blockModels = 0;
        int itemModels = 0;
        for (String id : reader.getModels().keySet()) {
            if (id.contains("/item/") || id.contains(":item/")) {
                itemModels++;
            } else {
                blockModels++;
            }
        }

        bundleStats.add(new BundleStats(
                "stream",
                reader.getVersion(),
                reader.getBlockStates().size(),
                blockModels,
                itemModels,
                reader.getTags().size(),
                cis.getCount(),
                -1,
                System.nanoTime() - start));

        snapshot.set(new BundledSnapshot(
                Collections.unmodifiableMap(newModels),
                Collections.unmodifiableMap(newBlockStates),
                Collections.unmodifiableList(newTags),
                Collections.emptySet(),
                Collections.emptySet()));
        reloadGeneration.incrementAndGet();
        this.initialized = true;

        if (VERIFY_DIAGNOSTICS) {
            verifyIntegrity();
        }
    }

    public void ensureInitialized() {
        if (initialized) {
            return;
        }
        synchronized (this) {
            if (initialized) {
                return;
            }
            ClassLoader cl = getClass().getClassLoader();
            InputStream assetsIn = cl.getResourceAsStream("assets/buildscape/resources.bsrb");
            InputStream dataIn = cl.getResourceAsStream("data/buildscape/data.bsrb");
            if (assetsIn != null || dataIn != null) {
                try {
                    loadBundles(assetsIn, dataIn, null);
                } catch (Exception e) {
                    BuildScape.LOGGER.error("Failed to initialize resource bundles from classpath", e);
                } finally {
                    if (assetsIn != null) {
                        try {
                            assetsIn.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (dataIn != null) {
                        try {
                            dataIn.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
    }

    public synchronized void reset() {
        snapshot.set(BundledSnapshot.EMPTY);
        bundleStats.clear();
        this.initialized = false;
        prepareNextReload();
    }

    public void prepareNextReload() {
        reloadGeneration.incrementAndGet();
        servedModels.reset();
        servedBlockStates.reset();
        servedTags.reset();
        fallbackModels.reset();
        fallbackBlockStates.reset();
        uniqueServedModels.clear();
        uniqueServedBlockStates.clear();
        uniqueServedTags.clear();
        accumulatedNativeConversionNanos.reset();
        overrideDiscoveryDurationNanos = -1;
        reloadStartNanos = -1;
        reloadPhaseWallNanos = -1;
    }

    public void recordReloadStart() {
        reloadStartNanos = System.nanoTime();
    }

    public void setOverrideDiscoveryDuration(long nanos) {
        overrideDiscoveryDurationNanos = nanos;
    }

    public void recordNativeConversionNanos(long nanos) {
        accumulatedNativeConversionNanos.add(nanos);
    }

    public void recordModelBakingComplete() {
        if (reloadStartNanos > 0) {
            reloadPhaseWallNanos = System.nanoTime() - reloadStartNanos;
        }
    }

    public boolean verifyIntegrity() {
        BundledSnapshot snap = snapshot.get();
        if (snap == null || snap.models() == null || snap.blockStates() == null || snap.tags() == null) {
            throw new IllegalStateException("Corrupt snapshot in BundledResourceRegistry");
        }
        int total = snap.models().size() + snap.blockStates().size() + snap.tags().size();
        if (total > ResourceBundleFormat.MAX_TOTAL_RECORDS) {
            throw new IllegalStateException("Snapshot record count exceeds budget: " + total);
        }
        for (ModelIR model : snap.models().values()) {
            if (model.elements().size() > ResourceBundleFormat.MAX_ELEMENTS_PER_MODEL) {
                throw new IllegalStateException(
                        "Model " + model.id() + " element count exceeds budget: " + model.elements().size());
            }
        }
        return true;
    }

    public String buildReloadSummary() {
        BundledSnapshot snap = snapshot.get();
        long gen = reloadGeneration.get();

        long totalIoNanos = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Resource Cache Reload Summary (Generation %d):\n", gen));
        sb.append("  Bundles:\n");
        if (gen > 1) {
            sb.append("    (Containers preserved from initial load in immutable memory snapshot; 0 bytes re-read)\n");
        }
        for (BundleStats bs : bundleStats) {
            totalIoNanos += bs.ioAndDecodeDurationNanos();
            String compStr = bs.compressedContainerBytes() >= 0
                    ? formatBytes(bs.compressedContainerBytes())
                    : "unavailable";
            String decStr = formatBytes(bs.uncompressedContainerBytes());
            sb.append(String.format(
                    "    - %s [v%d]: %d blockstates, %d block models, %d item models, %d tags | Container: %s (uncompressed), %s (ZIP entry) | Initial Load & Decode: %.2f ms\n",
                    bs.path(), bs.version(),
                    bs.blockStatesCount(), bs.blockModelsCount(), bs.itemModelsCount(), bs.tagsCount(),
                    decStr, compStr, bs.ioAndDecodeDurationNanos() / 1_000_000.0));
        }
        if (bundleStats.isEmpty() && gen <= 1) {
            sb.append("    (No bundles loaded)\n");
        }

        long servedBs = servedBlockStates.sum();
        long uniqueBs = uniqueServedBlockStates.size();
        int overBs = snap.overriddenBlockStates().size();

        long servedM = servedModels.sum();
        long uniqueM = uniqueServedModels.size();
        int overM = snap.overriddenModels().size();

        long servedT = servedTags.sum();
        long uniqueT = uniqueServedTags.size();

        sb.append("  Served:\n");
        sb.append(String.format("    - BlockStates: %d unique served / %d accesses (%d overridden, %d fallback)\n",
                uniqueBs, servedBs, overBs, fallbackBlockStates.sum()));
        sb.append(String.format("    - Models: %d unique served / %d accesses (%d overridden, %d fallback)\n",
                uniqueM, servedM, overM, fallbackModels.sum()));
        sb.append(String.format("    - Tags: %d unique served / %d accesses (override/fallback: unavailable)\n",
                uniqueT, servedT));

        sb.append("  Timings:\n");
        sb.append(String.format("    - Bundle Container I/O, Decompression & Decode: %s\n",
                gen == 1
                        ? (totalIoNanos > 0 ? String.format("%.2f ms", totalIoNanos / 1_000_000.0) : "unavailable")
                        : "0.00 ms (reused memory snapshot)"));
        sb.append(String.format("    - Override Discovery: %s\n",
                overrideDiscoveryDurationNanos >= 0
                        ? String.format("%.2f ms", overrideDiscoveryDurationNanos / 1_000_000.0)
                        : "unavailable"));
        sb.append(String.format("    - Accumulated Native Conversion Duration: %s (summed across worker threads)\n",
                accumulatedNativeConversionNanos.sum() > 0
                        ? String.format("%.2f ms", accumulatedNativeConversionNanos.sum() / 1_000_000.0)
                        : "unavailable"));
        sb.append(String.format(
                "    - Model Preparation & Baking Phase (wall clock): %s (spans ModelManager prepare to apply completion)",
                reloadPhaseWallNanos >= 0
                        ? String.format("%.2f ms", reloadPhaseWallNanos / 1_000_000.0)
                        : "unavailable"));

        return sb.toString();
    }

    public void logReloadSummary() {
        BuildScape.LOGGER.info(buildReloadSummary());
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private long resolveCompressedSize(String resourcePath) {
        try {
            ClassLoader cl = getClass().getClassLoader();
            URL url = cl.getResource(resourcePath);
            if (url != null) {
                URLConnection conn = url.openConnection();
                if (conn instanceof JarURLConnection juc) {
                    JarEntry entry = juc.getJarEntry();
                    if (entry != null && entry.getCompressedSize() >= 0) {
                        return entry.getCompressedSize();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    public long getReloadGeneration() {
        return reloadGeneration.get();
    }

    public long getServedModelCount() {
        return servedModels.sum();
    }

    public long getServedBlockStateCount() {
        return servedBlockStates.sum();
    }

    public long getServedTagCount() {
        return servedTags.sum();
    }

    public long getServedTotalCount() {
        return servedModels.sum() + servedBlockStates.sum() + servedTags.sum();
    }

    public long getFallbackModelCount() {
        return fallbackModels.sum();
    }

    public long getFallbackBlockStateCount() {
        return fallbackBlockStates.sum();
    }

    public Set<String> getUniqueServedIds() {
        Set<String> all = new HashSet<>();
        for (String m : uniqueServedModels) {
            all.add("model:" + m);
        }
        for (String b : uniqueServedBlockStates) {
            all.add("blockstate:" + b);
        }
        for (String t : uniqueServedTags) {
            all.add("tag:" + t);
        }
        return Collections.unmodifiableSet(all);
    }

    public int getUniqueServedCount() {
        return uniqueServedModels.size() + uniqueServedBlockStates.size() + uniqueServedTags.size();
    }

    public Map<String, ModelIR> getModels() {
        ensureInitialized();
        return snapshot.get().models();
    }

    public Map<String, BlockStateIR> getBlockStates() {
        ensureInitialized();
        return snapshot.get().blockStates();
    }

    public ModelIR getModel(String id) {
        ensureInitialized();
        BundledSnapshot snap = snapshot.get();
        if (snap.overriddenModels().contains(id)) {
            fallbackModels.increment();
            return null;
        }
        ModelIR model = snap.models().get(id);
        if (model != null) {
            servedModels.increment();
            uniqueServedModels.add(id);
        } else if (id != null && id.startsWith("buildscape:")) {
            fallbackModels.increment();
        }
        return model;
    }

    public boolean hasModel(String id) {
        ensureInitialized();
        BundledSnapshot snap = snapshot.get();
        return snap.models().containsKey(id) && !snap.overriddenModels().contains(id);
    }

    public boolean isModelBundled(String id) {
        ensureInitialized();
        return snapshot.get().models().containsKey(id);
    }

    public boolean isModelOverridden(String id) {
        ensureInitialized();
        return snapshot.get().overriddenModels().contains(id);
    }

    public BlockStateIR getBlockState(String id) {
        ensureInitialized();
        BundledSnapshot snap = snapshot.get();
        if (snap.overriddenBlockStates().contains(id)) {
            fallbackBlockStates.increment();
            return null;
        }
        BlockStateIR bs = snap.blockStates().get(id);
        if (bs != null) {
            servedBlockStates.increment();
            uniqueServedBlockStates.add(id);
        } else if (id != null && id.startsWith("buildscape:")) {
            fallbackBlockStates.increment();
        }
        return bs;
    }

    public boolean hasBlockState(String id) {
        ensureInitialized();
        BundledSnapshot snap = snapshot.get();
        return snap.blockStates().containsKey(id) && !snap.overriddenBlockStates().contains(id);
    }

    public boolean isBlockStateBundled(String id) {
        ensureInitialized();
        return snapshot.get().blockStates().containsKey(id);
    }

    public boolean isBlockStateOverridden(String id) {
        ensureInitialized();
        return snapshot.get().overriddenBlockStates().contains(id);
    }

    public List<TagIR> getTags() {
        ensureInitialized();
        return snapshot.get().tags();
    }

    public void recordServedTag(String tagId) {
        recordServedTag("tag", tagId);
    }

    public void recordServedTag(String directory, String tagId) {
        servedTags.increment();
        if (tagId != null) {
            uniqueServedTags.add(directory + "/" + tagId);
        }
    }

    public void setOverrides(Set<String> newOverriddenModels, Set<String> newOverriddenBlockStates) {
        BundledSnapshot current = snapshot.get();
        snapshot.set(new BundledSnapshot(
                current.models(),
                current.blockStates(),
                current.tags(),
                Collections.unmodifiableSet(new HashSet<>(newOverriddenModels)),
                Collections.unmodifiableSet(new HashSet<>(newOverriddenBlockStates))));
    }

    public void markModelOverridden(String id) {
        setModelOverridden(id, true);
    }

    public synchronized void setModelOverridden(String id, boolean overridden) {
        BundledSnapshot current = snapshot.get();
        Set<String> newOverridden = new HashSet<>(current.overriddenModels());
        if (overridden) {
            newOverridden.add(id);
        } else {
            newOverridden.remove(id);
        }

        snapshot.set(new BundledSnapshot(
                current.models(),
                current.blockStates(),
                current.tags(),
                Collections.unmodifiableSet(newOverridden),
                current.overriddenBlockStates()));
    }

    public void markBlockStateOverridden(String id) {
        setBlockStateOverridden(id, true);
    }

    public synchronized void setBlockStateOverridden(String id, boolean overridden) {
        BundledSnapshot current = snapshot.get();
        Set<String> newOverridden = new HashSet<>(current.overriddenBlockStates());
        if (overridden) {
            newOverridden.add(id);
        } else {
            newOverridden.remove(id);
        }

        snapshot.set(new BundledSnapshot(
                current.models(),
                current.blockStates(),
                current.tags(),
                current.overriddenModels(),
                Collections.unmodifiableSet(newOverridden)));
    }

    public synchronized void clearOverrides() {
        BundledSnapshot current = snapshot.get();
        snapshot.set(new BundledSnapshot(
                current.models(),
                current.blockStates(),
                current.tags(),
                Collections.emptySet(),
                Collections.emptySet()));
    }

    public int getModelCount() {
        return snapshot.get().models().size();
    }

    public int getBlockStateCount() {
        return snapshot.get().blockStates().size();
    }

    public int getTagCount() {
        return snapshot.get().tags().size();
    }
}
