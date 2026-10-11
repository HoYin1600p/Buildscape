package com.kingodogo.buildscape.parity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/** JSON output shared by the snapshots and the headless self-check. */
final class DeterministicJson {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();

    private DeterministicJson() {}

    static JsonElement json(Object value) {
        if (value == null) return JsonNull.INSTANCE;
        if (value instanceof JsonElement element) return element;
        return GSON.toJsonTree(value);
    }

    static Map<String, JsonElement> sortedFiles(Map<String, JsonElement> files) {
        return new TreeMap<>(files);
    }

    static JsonElement canonical(JsonElement value) {
        if (value.isJsonObject()) {
            JsonObject result = new JsonObject();
            TreeMap<String, JsonElement> keys = new TreeMap<>();
            value.getAsJsonObject().entrySet().forEach(entry -> keys.put(entry.getKey(), entry.getValue()));
            keys.forEach((key, element) -> result.add(key, canonical(element)));
            return result;
        }
        if (value.isJsonArray()) {
            JsonArray result = new JsonArray();
            value.getAsJsonArray().forEach(element -> result.add(canonical(element)));
            return result;
        }
        return value;
    }

    static void write(Path path, JsonElement value) throws IOException {
        Files.writeString(path, GSON.toJson(canonical(value)) + "\n", StandardCharsets.UTF_8);
    }
}
