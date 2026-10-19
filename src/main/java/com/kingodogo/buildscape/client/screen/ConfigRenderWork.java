package com.kingodogo.buildscape.client.screen;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/** CPU-only helpers shared by the screen's layout and search caches. */
final class ConfigRenderWork {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private ConfigRenderWork() {}

    static String normalize(String text) {
        return text == null ? "" : WHITESPACE.matcher(text.trim().toLowerCase(Locale.ROOT)
                .replace('_', ' ').replace('-', ' ')).replaceAll(" ");
    }

    /** Inputs are cached normalised strings; display names are resolved only if the ID did not match. */
    static boolean matches(String id, String query, Supplier<String> displayName) {
        return query.isEmpty() || id.contains(query) || displayName.get().contains(query);
    }

    /** End is exclusive. Pixel bounds include partially visible rows. */
    static int[] visibleRows(double scroll, int viewportHeight, int step, int count) {
        int first = Math.max(0, Math.min(count, (int) Math.floor(scroll / step)));
        int end = Math.max(first, Math.min(count, (int) Math.ceil((scroll + viewportHeight) / step)));
        return new int[]{first, end};
    }

    static final class Cache<K, V> {
        private final Map<K, V> entries;

        Cache(int capacity) {
            entries = new LinkedHashMap<>(16, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > capacity;
                }
            };
        }

        V get(K key, Function<K, V> create) { return entries.computeIfAbsent(key, create); }
        void clear() { entries.clear(); }
    }
}
