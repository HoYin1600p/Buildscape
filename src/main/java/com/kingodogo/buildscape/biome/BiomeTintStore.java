package com.kingodogo.buildscape.biome;

import com.kingodogo.buildscape.util.CommonId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Per-dimension biome colour overrides, indexed by chunk and horizontal column. */
public final class BiomeTintStore {
    private final Map<Long, Map<Integer, CommonId>> chunks = new HashMap<>();

    public static long chunkKey(int chunkX, int chunkZ) {
        return (chunkX & 0xffffffffL) | ((chunkZ & 0xffffffffL) << 32);
    }

    public static int columnIndex(int x, int z) {
        return (x & 15) | ((z & 15) << 4);
    }

    public boolean putColumn(int x, int z, CommonId biome) {
        Objects.requireNonNull(biome, "biome");
        if (!biome.equals(CommonId.tryParse(biome.toString()))) {
            throw new IllegalArgumentException("Invalid biome identifier: " + biome);
        }
        Map<Integer, CommonId> columns = chunks.computeIfAbsent(chunkKey(x >> 4, z >> 4), ignored -> new HashMap<>());
        return !biome.equals(columns.put(columnIndex(x, z), biome));
    }

    public boolean hasChunk(long chunk) {
        return chunks.containsKey(chunk);
    }

    public Map<Integer, CommonId> getChunk(long chunk) {
        return Map.copyOf(chunks.getOrDefault(chunk, Map.of()));
    }

    public void restoreChunk(long chunk, Map<Integer, CommonId> columns) {
        Objects.requireNonNull(columns, "columns");
        for (var entry : columns.entrySet()) {
            if (entry.getKey() == null || entry.getKey() < 0 || entry.getKey() >= 256
                    || entry.getValue() == null
                    || !entry.getValue().equals(CommonId.tryParse(entry.getValue().toString()))) {
                throw new IllegalArgumentException("Invalid biome tint column");
            }
        }
        if (columns.isEmpty()) chunks.remove(chunk);
        else chunks.put(chunk, new HashMap<>(columns));
    }

    public Map<Long, Map<Integer, CommonId>> snapshot() {
        Map<Long, Map<Integer, CommonId>> copy = new HashMap<>();
        chunks.forEach((key, value) -> copy.put(key, Map.copyOf(value)));
        return Map.copyOf(copy);
    }
}
