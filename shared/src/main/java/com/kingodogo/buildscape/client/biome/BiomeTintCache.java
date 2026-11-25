package com.kingodogo.buildscape.client.biome;

import com.kingodogo.buildscape.biome.BiomeTintStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/** Immutable client snapshots for tint lookup from render threads. */
public final class BiomeTintCache<T> {
    @FunctionalInterface
    public interface ColorSample<T> {
        int color(T biome, int x, int z);
    }

    private record State<T>(Object world, Map<Long, Map<Integer, T>> chunks) { }
    private volatile State<T> state = new State<>(null, Map.of());

    public synchronized boolean replace(Object world, long chunk, Map<Integer, T> columns) {
        Objects.requireNonNull(world, "world");
        Map<Integer, T> copy = Map.copyOf(columns);
        for (int index : copy.keySet()) {
            if (index < 0 || index >= 256) throw new IllegalArgumentException("Invalid tint column");
        }
        State<T> previous = state;
        Map<Long, Map<Integer, T>> chunks = new HashMap<>(previous.world == world ? previous.chunks : Map.of());
        Map<Integer, T> old = chunks.remove(chunk);
        if (!copy.isEmpty()) chunks.put(chunk, copy);
        state = new State<>(world, Map.copyOf(chunks));
        return !Objects.equals(old == null ? Map.of() : old, copy);
    }

    public synchronized boolean remove(Object world, long chunk) {
        State<T> current = state;
        if (current.world != world || !current.chunks.containsKey(chunk)) return false;
        Map<Long, Map<Integer, T>> chunks = new HashMap<>(current.chunks);
        chunks.remove(chunk);
        state = new State<>(world, Map.copyOf(chunks));
        return true;
    }

    public synchronized void clear() {
        state = new State<>(null, Map.of());
    }

    public T get(Object world, int x, int z) {
        State<T> current = state;
        return current.world == world ? lookup(current, x, z) : null;
    }

    /** Returns null when the sampled area has no overrides and vanilla should render it. */
    public Integer blendedColor(Object world, int x, int z, int radius,
            BiFunction<Integer, Integer, T> fallback, ColorSample<T> sample) {
        State<T> current = state;
        if (current.world != world || current.chunks.isEmpty()) return null;
        boolean nearby = false;
        for (int chunkX = (x - radius) >> 4; chunkX <= (x + radius) >> 4; chunkX++) {
            for (int chunkZ = (z - radius) >> 4; chunkZ <= (z + radius) >> 4; chunkZ++) {
                if (current.chunks.containsKey(BiomeTintStore.chunkKey(chunkX, chunkZ))) nearby = true;
            }
        }
        if (!nearby) return null;
        int red = 0, green = 0, blue = 0;
        boolean painted = false;
        for (int sampleX = x - radius; sampleX <= x + radius; sampleX++) {
            for (int sampleZ = z - radius; sampleZ <= z + radius; sampleZ++) {
                T biome = lookup(current, sampleX, sampleZ);
                if (biome == null) biome = fallback.apply(sampleX, sampleZ);
                else painted = true;
                int color = sample.color(biome, sampleX, sampleZ);
                red += (color >> 16) & 255;
                green += (color >> 8) & 255;
                blue += color & 255;
            }
        }
        if (!painted) return null;
        int samples = (radius * 2 + 1) * (radius * 2 + 1);
        return ((red / samples) << 16) | ((green / samples) << 8) | (blue / samples);
    }

    private T lookup(State<T> current, int x, int z) {
        Map<Integer, T> columns = current.chunks.get(BiomeTintStore.chunkKey(x >> 4, z >> 4));
        return columns == null ? null : columns.get(BiomeTintStore.columnIndex(x, z));
    }
}
