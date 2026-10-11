package com.kingodogo.buildscape.adapter.v118x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registry order from the 1.18.2 ModItems declarations, independent of tab display order. */
public final class RegistrationOrder118x {
    private static final List<String> ITEM_IDS = loadItemIds();

    private RegistrationOrder118x() {}

    public static List<String> itemIds() {
        return ITEM_IDS;
    }

    /** Return a registration view without changing the shared declaration map. */
    public static <T> Map<String, T> orderedItems(Map<String, T> items) {
        Map<String, T> ordered = new LinkedHashMap<>();
        for (String id : ITEM_IDS) {
            if (!items.containsKey(id)) {
                throw new IllegalStateException("Missing 1.18.2 item registration: " + id);
            }
            ordered.put(id, items.get(id));
        }
        if (ordered.size() != items.size()) {
            var unexpected = new HashSet<>(items.keySet());
            unexpected.removeAll(ordered.keySet());
            throw new IllegalStateException("Items absent from 1.18.2 registration order: " + unexpected);
        }
        return ordered;
    }

    private static List<String> loadItemIds() {
        String resource = "/buildscape/registration-order-118x.txt";
        var stream = RegistrationOrder118x.class.getResourceAsStream(resource);
        if (stream == null) throw new IllegalStateException("Missing registration order: " + resource);
        List<String> ids = new ArrayList<>();
        var seen = new HashSet<String>();
        try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String id = line.trim();
                if (id.isEmpty() || id.startsWith("#")) continue;
                if (!id.matches("[a-z0-9_]+") || !seen.add(id)) {
                    throw new IllegalStateException("Invalid or duplicate 1.18.2 registration id: " + id);
                }
                ids.add(id);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot read " + resource, exception);
        }
        if (ids.isEmpty()) throw new IllegalStateException("Empty registration order: " + resource);
        return List.copyOf(ids);
    }
}
